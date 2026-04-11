package com.resourcemind.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "resource_pool")
public class ResourcePool {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "pool_name", nullable = false, unique = true, length = 64)
	private String poolName;

	@Column(name = "available_gpus", nullable = false)
	private int availableGpus;

	@Column(name = "total_gpus", nullable = false)
	private int totalGpus;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected ResourcePool() {
	}

	public ResourcePool(String poolName, int availableGpus, int totalGpus) {
		this.poolName = poolName;
		this.availableGpus = availableGpus;
		this.totalGpus = totalGpus;
	}

	@PrePersist
	@PreUpdate
	void touch() {
		updatedAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getPoolName() {
		return poolName;
	}

	public int getAvailableGpus() {
		return availableGpus;
	}

	public void setAvailableGpus(int availableGpus) {
		this.availableGpus = availableGpus;
	}

	public int getTotalGpus() {
		return totalGpus;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
