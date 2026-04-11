package com.resourcemind.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "workload_usage_history")
public class WorkloadUsageHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "use_case_key", nullable = false, length = 128)
	private String useCaseKey;

	@Column(name = "workload_profile", length = 64)
	private String workloadProfile;

	@Column(name = "period_start", nullable = false)
	private Instant periodStart;

	@Column(name = "period_end", nullable = false)
	private Instant periodEnd;

	@Column(name = "gpus_allocated", nullable = false)
	private int gpusAllocated;

	@Column(name = "avg_utilization_pct", nullable = false, precision = 7, scale = 2)
	private BigDecimal avgUtilizationPct;

	@Column(nullable = false, length = 64)
	private String source;

	protected WorkloadUsageHistory() {
	}

	public WorkloadUsageHistory(
			String useCaseKey,
			String workloadProfile,
			Instant periodStart,
			Instant periodEnd,
			int gpusAllocated,
			BigDecimal avgUtilizationPct,
			String source
	) {
		this.useCaseKey = useCaseKey;
		this.workloadProfile = workloadProfile;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
		this.gpusAllocated = gpusAllocated;
		this.avgUtilizationPct = avgUtilizationPct;
		this.source = source;
	}

	public Long getId() {
		return id;
	}

	public String getUseCaseKey() {
		return useCaseKey;
	}

	public String getWorkloadProfile() {
		return workloadProfile;
	}

	public Instant getPeriodStart() {
		return periodStart;
	}

	public Instant getPeriodEnd() {
		return periodEnd;
	}

	public int getGpusAllocated() {
		return gpusAllocated;
	}

	public BigDecimal getAvgUtilizationPct() {
		return avgUtilizationPct;
	}

	public String getSource() {
		return source;
	}
}
