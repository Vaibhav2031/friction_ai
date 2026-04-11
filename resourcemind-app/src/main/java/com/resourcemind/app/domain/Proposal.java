package com.resourcemind.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "proposal")
public class Proposal {

	@Id
	@Column(nullable = false, updatable = false)
	private UUID id;

	@Column(name = "public_id", nullable = false, unique = true, length = 64)
	private String publicId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private ProposalStatus status = ProposalStatus.PENDING;

	@Column(name = "use_case_type", nullable = false, length = 64)
	private String useCaseType;

	@Column(name = "workload_profile", length = 64)
	private String workloadProfile;

	@Column(name = "use_case_key", length = 128)
	private String useCaseKey;

	@Column(nullable = false, length = 512)
	private String title;

	@Column(nullable = false, columnDefinition = "text")
	private String description;

	@Column(name = "requested_gpus", nullable = false)
	private int requestedGpus;

	@Column(name = "approved_gpus")
	private Integer approvedGpus;

	@Column(name = "rationale_summary", columnDefinition = "text")
	private String rationaleSummary;

	@Column(name = "decision_contract_json", columnDefinition = "text")
	private String decisionContractJson;

	@Column(name = "agent_run_id", length = 128)
	private String agentRunId;

	@Column(name = "decision_idempotency_key", length = 128)
	private String decisionIdempotencyKey;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Proposal() {
	}

	public Proposal(
			UUID id,
			String publicId,
			String useCaseType,
			String workloadProfile,
			String useCaseKey,
			String title,
			String description,
			int requestedGpus,
			String agentRunId
	) {
		this.id = id;
		this.publicId = publicId;
		this.useCaseType = useCaseType;
		this.workloadProfile = workloadProfile;
		this.useCaseKey = useCaseKey;
		this.title = title;
		this.description = description;
		this.requestedGpus = requestedGpus;
		this.agentRunId = agentRunId;
	}

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		if (createdAt == null) {
			createdAt = now;
		}
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public String getPublicId() {
		return publicId;
	}

	public ProposalStatus getStatus() {
		return status;
	}

	public void setStatus(ProposalStatus status) {
		this.status = status;
	}

	public String getUseCaseType() {
		return useCaseType;
	}

	public String getWorkloadProfile() {
		return workloadProfile;
	}

	public void setWorkloadProfile(String workloadProfile) {
		this.workloadProfile = workloadProfile;
	}

	public String getUseCaseKey() {
		return useCaseKey;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public int getRequestedGpus() {
		return requestedGpus;
	}

	public Integer getApprovedGpus() {
		return approvedGpus;
	}

	public void setApprovedGpus(Integer approvedGpus) {
		this.approvedGpus = approvedGpus;
	}

	public String getRationaleSummary() {
		return rationaleSummary;
	}

	public void setRationaleSummary(String rationaleSummary) {
		this.rationaleSummary = rationaleSummary;
	}

	public String getDecisionContractJson() {
		return decisionContractJson;
	}

	public void setDecisionContractJson(String decisionContractJson) {
		this.decisionContractJson = decisionContractJson;
	}

	public String getAgentRunId() {
		return agentRunId;
	}

	public String getDecisionIdempotencyKey() {
		return decisionIdempotencyKey;
	}

	public void setDecisionIdempotencyKey(String decisionIdempotencyKey) {
		this.decisionIdempotencyKey = decisionIdempotencyKey;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
