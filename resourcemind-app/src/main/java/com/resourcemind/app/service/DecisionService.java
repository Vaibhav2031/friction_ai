package com.resourcemind.app.service;

import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import com.resourcemind.app.api.dto.AgentDecisionRequest;
import com.resourcemind.app.api.dto.ProposalResponse;
import com.resourcemind.app.api.error.BadRequestException;
import com.resourcemind.app.api.error.ConflictException;
import com.resourcemind.app.api.error.NotFoundException;
import com.resourcemind.app.domain.DecisionOutcome;
import com.resourcemind.app.domain.Proposal;
import com.resourcemind.app.domain.ProposalStatus;
import com.resourcemind.app.domain.ResourcePool;
import com.resourcemind.app.repository.ProposalRepository;
import com.resourcemind.app.repository.ResourcePoolRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class DecisionService {

	private final ProposalRepository proposalRepository;
	private final ResourcePoolRepository resourcePoolRepository;
	private final JsonMapper jsonMapper;
	private final String defaultPoolName;

	public DecisionService(
			ProposalRepository proposalRepository,
			ResourcePoolRepository resourcePoolRepository,
			JsonMapper jsonMapper,
			@Value("${resourcemind.resource-pool.default-name:default}") String defaultPoolName
	) {
		this.proposalRepository = proposalRepository;
		this.resourcePoolRepository = resourcePoolRepository;
		this.jsonMapper = jsonMapper;
		this.defaultPoolName = defaultPoolName;
	}

	@Transactional
	public ProposalResponse apply(AgentDecisionRequest request) {
		Proposal proposal = proposalRepository
				.findByIdForUpdate(request.proposalId())
				.orElseThrow(() -> new NotFoundException("Proposal not found: " + request.proposalId()));

		if (proposal.getStatus() == ProposalStatus.APPROVED || proposal.getStatus() == ProposalStatus.REJECTED) {
			if (StringUtils.hasText(request.idempotencyKey())
					&& request.idempotencyKey().equals(proposal.getDecisionIdempotencyKey())) {
				return toResponse(proposal);
			}
			throw new ConflictException("Proposal is already in a terminal state.");
		}

		if (proposal.getStatus() != ProposalStatus.PENDING && proposal.getStatus() != ProposalStatus.IN_REVIEW) {
			throw new ConflictException("Proposal cannot accept a decision in status " + proposal.getStatus());
		}

		if (request.outcome() == DecisionOutcome.APPROVE) {
			applyApprove(proposal, request);
		}
		else {
			applyReject(proposal, request);
		}

		if (StringUtils.hasText(request.idempotencyKey())) {
			proposal.setDecisionIdempotencyKey(request.idempotencyKey());
		}

		return toResponse(proposalRepository.save(proposal));
	}

	private void applyApprove(Proposal proposal, AgentDecisionRequest request) {
		if (request.approvedGpus() == null) {
			throw new BadRequestException("approvedGpus is required when outcome is APPROVE.");
		}
		int approved = request.approvedGpus();
		if (approved < 0) {
			throw new BadRequestException("approvedGpus must be non-negative.");
		}
		if (approved > proposal.getRequestedGpus()) {
			throw new BadRequestException(
					"approvedGpus (%d) cannot exceed requestedGpus (%d).".formatted(approved, proposal.getRequestedGpus())
			);
		}

		ResourcePool pool = resourcePoolRepository
				.findByPoolNameForUpdate(defaultPoolName)
				.orElseThrow(() -> new NotFoundException("Resource pool not found."));
		if (pool.getAvailableGpus() < approved) {
			throw new BadRequestException(
					"Insufficient GPUs: available=%d, approved=%d.".formatted(pool.getAvailableGpus(), approved)
			);
		}

		pool.setAvailableGpus(pool.getAvailableGpus() - approved);
		resourcePoolRepository.save(pool);

		proposal.setApprovedGpus(approved);
		proposal.setRationaleSummary(request.rationaleSummary());
		proposal.setDecisionContractJson(writeJson(request.decisionContract()));
		proposal.setStatus(ProposalStatus.APPROVED);
	}

	private void applyReject(Proposal proposal, AgentDecisionRequest request) {
		proposal.setApprovedGpus(null);
		proposal.setRationaleSummary(request.rationaleSummary());
		proposal.setDecisionContractJson(writeJson(request.decisionContract()));
		proposal.setStatus(ProposalStatus.REJECTED);
	}

	private String writeJson(Map<String, Object> contract) {
		if (contract == null) {
			return null;
		}
		try {
			return jsonMapper.writeValueAsString(contract);
		}
		catch (JacksonException e) {
			throw new BadRequestException("decisionContract is not serializable JSON.");
		}
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
