package com.resourcemind.app.service;

import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import com.resourcemind.app.api.dto.CreateProposalRequest;
import com.resourcemind.app.api.dto.ProposalResponse;
import com.resourcemind.app.api.error.NotFoundException;
import com.resourcemind.app.domain.Proposal;
import com.resourcemind.app.repository.ProposalRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProposalService {

	private final ProposalRepository proposalRepository;
	private final JsonMapper jsonMapper;
	private final LangGraphInvocationService langGraphInvocationService;

	public ProposalService(
			ProposalRepository proposalRepository,
			JsonMapper jsonMapper,
			LangGraphInvocationService langGraphInvocationService
	) {
		this.proposalRepository = proposalRepository;
		this.jsonMapper = jsonMapper;
		this.langGraphInvocationService = langGraphInvocationService;
	}

	@Transactional
	public ProposalResponse create(CreateProposalRequest request) {
		Proposal proposal = new Proposal(
				UUID.randomUUID(),
				newPublicId(),
				request.useCaseType().strip(),
				StringUtils.hasText(request.workloadProfile()) ? request.workloadProfile().strip() : null,
				StringUtils.hasText(request.useCaseKey()) ? request.useCaseKey().strip() : null,
				request.title().strip(),
				request.description(),
				request.requestedGpus(),
				StringUtils.hasText(request.agentCorrelationId()) ? request.agentCorrelationId().strip() : null
		);
		proposal = proposalRepository.save(proposal);
		langGraphInvocationService.trigger(proposal.getId());
		return toResponse(proposal);
	}

	@Transactional(readOnly = true)
	public ProposalResponse get(String idOrPublicId) {
		Proposal proposal = findByIdOrPublicId(idOrPublicId);
		return toResponse(proposal);
	}

	@Transactional(readOnly = true)
	public List<ProposalResponse> listAll() {
		return proposalRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(this::toResponse)
				.toList();
	}

	private Proposal findByIdOrPublicId(String idOrPublicId) {
		if (!StringUtils.hasText(idOrPublicId)) {
			throw new NotFoundException("Proposal not found.");
		}
		String key = idOrPublicId.strip();
		try {
			UUID uuid = UUID.fromString(key);
			return proposalRepository.findById(uuid).orElseThrow(() -> new NotFoundException("Proposal not found."));
		}
		catch (IllegalArgumentException ignored) {
			return proposalRepository.findByPublicId(key).orElseThrow(() -> new NotFoundException("Proposal not found."));
		}
	}

	private String newPublicId() {
		for (int i = 0; i < 5; i++) {
			String candidate = "REQ-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
			if (proposalRepository.findByPublicId(candidate).isEmpty()) {
				return candidate;
			}
		}
		return "REQ-" + UUID.randomUUID();
	}

	@SuppressWarnings("unchecked")
	private ProposalResponse toResponse(Proposal p) {
		Map<String, Object> contract = null;
		if (StringUtils.hasText(p.getDecisionContractJson())) {
			try {
				contract = jsonMapper.readValue(p.getDecisionContractJson(), Map.class);
			}
			catch (JacksonException ignored) {
				contract = null;
			}
		}
		return ProposalResponse.from(p, contract);
	}
}
