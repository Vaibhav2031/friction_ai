package com.resourcemind.app.service;

import com.resourcemind.app.api.dto.BenchmarkToolResponse;
import com.resourcemind.app.api.dto.HistoryEntryResponse;
import com.resourcemind.app.api.dto.ResourcesToolResponse;
import com.resourcemind.app.api.error.BadRequestException;
import com.resourcemind.app.api.error.NotFoundException;
import com.resourcemind.app.domain.ResourcePool;
import com.resourcemind.app.domain.UseCaseBenchmark;
import com.resourcemind.app.domain.WorkloadUsageHistory;
import com.resourcemind.app.repository.ResourcePoolRepository;
import com.resourcemind.app.repository.UseCaseBenchmarkRepository;
import com.resourcemind.app.repository.WorkloadUsageHistoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class InternalToolsService {

	private final UseCaseBenchmarkRepository benchmarkRepository;
	private final WorkloadUsageHistoryRepository historyRepository;
	private final ResourcePoolRepository resourcePoolRepository;

	private final String defaultPoolName;

	public InternalToolsService(
			UseCaseBenchmarkRepository benchmarkRepository,
			WorkloadUsageHistoryRepository historyRepository,
			ResourcePoolRepository resourcePoolRepository,
			@Value("${resourcemind.resource-pool.default-name:default}") String defaultPoolName
	) {
		this.benchmarkRepository = benchmarkRepository;
		this.historyRepository = historyRepository;
		this.resourcePoolRepository = resourcePoolRepository;
		this.defaultPoolName = defaultPoolName;
	}

	@Transactional(readOnly = true)
	public BenchmarkToolResponse benchmark(String useCaseType, String workloadProfile) {
		BenchmarkPolicy.validateBenchmarkQuery(useCaseType, workloadProfile);
		String type = useCaseType.strip();
		String profile = resolveProfileForLookup(type, workloadProfile);
		UseCaseBenchmark row = benchmarkRepository
				.findByUseCaseTypeAndWorkloadProfile(type, profile)
				.orElseThrow(() -> new NotFoundException("No benchmark for type=%s profile=%s".formatted(type, profile)));
		return new BenchmarkToolResponse(
				row.getUseCaseType(),
				row.getWorkloadProfile(),
				row.getStandardAllocationGpus(),
				row.getNotes()
		);
	}

	@Transactional(readOnly = true)
	public List<HistoryEntryResponse> history(String useCaseKey, String workloadProfile) {
		if (!StringUtils.hasText(useCaseKey)) {
			throw new BadRequestException("Query parameter 'useCase' is required.");
		}
		String profile = StringUtils.hasText(workloadProfile) ? workloadProfile.strip() : null;
		return historyRepository.findRecent(useCaseKey.strip(), profile).stream()
				.map(InternalToolsService::toHistoryDto)
				.toList();
	}

	private static String resolveProfileForLookup(String useCaseType, String workloadProfile) {
		if (BenchmarkPolicy.TYPE_AI_WEB_APP.equalsIgnoreCase(useCaseType)) {
			return workloadProfile.strip();
		}
		return StringUtils.hasText(workloadProfile) ? workloadProfile.strip() : "DEFAULT";
	}

	private static HistoryEntryResponse toHistoryDto(WorkloadUsageHistory h) {
		return new HistoryEntryResponse(
				h.getUseCaseKey(),
				h.getWorkloadProfile(),
				h.getPeriodStart(),
				h.getPeriodEnd(),
				h.getGpusAllocated(),
				h.getAvgUtilizationPct(),
				h.getSource()
		);
	}

	@Transactional(readOnly = true)
	public ResourcesToolResponse resources() {
		ResourcePool pool = resourcePoolRepository
				.findByPoolName(defaultPoolName)
				.orElseThrow(() -> new NotFoundException("Resource pool '%s' not found.".formatted(defaultPoolName)));
		return new ResourcesToolResponse(pool.getPoolName(), pool.getAvailableGpus(), pool.getTotalGpus());
	}
}
