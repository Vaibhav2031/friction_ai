package com.resourcemind.app.api.dto;

import com.resourcemind.app.domain.Proposal;
import com.resourcemind.app.domain.ProposalStatus;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ProposalResponse(
		UUID id,
		String publicId,
		ProposalStatus status,
		String useCaseType,
		String workloadProfile,
		String useCaseKey,
		String title,
		String description,
		int requestedGpus,
		Integer approvedGpus,
		String rationaleSummary,
		Map<String, Object> decisionContract,
		String agentRunId,
		Instant createdAt,
		Instant updatedAt
) {
	public static ProposalResponse from(Proposal p, Map<String, Object> contractOrNull) {
		return new ProposalResponse(
				p.getId(),
				p.getPublicId(),
				p.getStatus(),
				p.getUseCaseType(),
				p.getWorkloadProfile(),
				p.getUseCaseKey(),
				p.getTitle(),
				p.getDescription(),
				p.getRequestedGpus(),
				p.getApprovedGpus(),
				p.getRationaleSummary(),
				contractOrNull,
				p.getAgentRunId(),
				p.getCreatedAt(),
				p.getUpdatedAt()
		);
	}
}
