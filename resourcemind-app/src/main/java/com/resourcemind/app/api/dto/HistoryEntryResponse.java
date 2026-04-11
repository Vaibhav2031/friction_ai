package com.resourcemind.app.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record HistoryEntryResponse(
		String useCaseKey,
		String workloadProfile,
		Instant periodStart,
		Instant periodEnd,
		int gpusAllocated,
		BigDecimal avgUtilizationPct,
		String source
) {
}
