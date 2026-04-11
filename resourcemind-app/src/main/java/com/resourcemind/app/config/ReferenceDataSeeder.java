package com.resourcemind.app.config;

import com.resourcemind.app.domain.ResourcePool;
import com.resourcemind.app.domain.UseCaseBenchmark;
import com.resourcemind.app.domain.WorkloadUsageHistory;
import com.resourcemind.app.repository.ResourcePoolRepository;
import com.resourcemind.app.repository.UseCaseBenchmarkRepository;
import com.resourcemind.app.repository.WorkloadUsageHistoryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDataSeeder implements ApplicationRunner {

	private final ResourcePoolRepository resourcePoolRepository;
	private final UseCaseBenchmarkRepository benchmarkRepository;
	private final WorkloadUsageHistoryRepository historyRepository;

	private final String defaultPoolName;
	private final int seedTotalGpus;

	public ReferenceDataSeeder(
			ResourcePoolRepository resourcePoolRepository,
			UseCaseBenchmarkRepository benchmarkRepository,
			WorkloadUsageHistoryRepository historyRepository,
			@Value("${resourcemind.resource-pool.default-name:default}") String defaultPoolName,
			@Value("${resourcemind.seed.total-gpus:20}") int seedTotalGpus
	) {
		this.resourcePoolRepository = resourcePoolRepository;
		this.benchmarkRepository = benchmarkRepository;
		this.historyRepository = historyRepository;
		this.defaultPoolName = defaultPoolName;
		this.seedTotalGpus = seedTotalGpus;
	}

	@Override
	public void run(ApplicationArguments args) {
		seedPoolIfEmpty();
		seedBenchmarksIfEmpty();
		seedHistoryIfEmpty();
	}

	private void seedPoolIfEmpty() {
		if (resourcePoolRepository.findByPoolName(defaultPoolName).isEmpty()) {
			resourcePoolRepository.save(new ResourcePool(defaultPoolName, seedTotalGpus, seedTotalGpus));
		}
	}

	private void seedBenchmarksIfEmpty() {
		if (benchmarkRepository.count() > 0) {
			return;
		}
		benchmarkRepository.save(
				new UseCaseBenchmark(
						"AI_WEB_APP",
						"TEXT_GENERATION",
						1,
						"Single-model text inference; standard allocation is 1 GPU."
				)
		);
		benchmarkRepository.save(
				new UseCaseBenchmark(
						"AI_WEB_APP",
						"MULTIMODAL_MULTIAGENT",
						3,
						"Vision, summarization, and orchestrated agents require additional capacity vs plain text."
				)
		);
	}

	private void seedHistoryIfEmpty() {
		if (historyRepository.count() > 0) {
			return;
		}
		Instant end = Instant.parse("2025-06-01T12:00:00Z");
		Instant start = Instant.parse("2025-05-01T12:00:00Z");
		historyRepository.save(
				new WorkloadUsageHistory(
						"guessing_game",
						"TEXT_GENERATION",
						start,
						end,
						1,
						new BigDecimal("40.00"),
						"demo_seed"
				)
		);
	}
}
