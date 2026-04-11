package com.resourcemind.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "use_case_benchmark",
		uniqueConstraints = @UniqueConstraint(columnNames = { "use_case_type", "workload_profile" })
)
public class UseCaseBenchmark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "use_case_type", nullable = false, length = 64)
	private String useCaseType;

	@Column(name = "workload_profile", nullable = false, length = 64)
	private String workloadProfile;

	@Column(name = "standard_allocation_gpus", nullable = false)
	private int standardAllocationGpus;

	@Column(nullable = false, columnDefinition = "text")
	private String notes;

	protected UseCaseBenchmark() {
	}

	public UseCaseBenchmark(String useCaseType, String workloadProfile, int standardAllocationGpus, String notes) {
		this.useCaseType = useCaseType;
		this.workloadProfile = workloadProfile;
		this.standardAllocationGpus = standardAllocationGpus;
		this.notes = notes;
	}

	public Long getId() {
		return id;
	}

	public String getUseCaseType() {
		return useCaseType;
	}

	public String getWorkloadProfile() {
		return workloadProfile;
	}

	public int getStandardAllocationGpus() {
		return standardAllocationGpus;
	}

	public String getNotes() {
		return notes;
	}
}
