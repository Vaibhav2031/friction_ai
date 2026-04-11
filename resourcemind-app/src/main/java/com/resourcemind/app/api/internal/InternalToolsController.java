package com.resourcemind.app.api.internal;

import com.resourcemind.app.api.dto.AgentDecisionRequest;
import com.resourcemind.app.api.dto.BenchmarkToolResponse;
import com.resourcemind.app.api.dto.HistoryEntryResponse;
import com.resourcemind.app.api.dto.ProposalResponse;
import com.resourcemind.app.api.dto.ResourcesToolResponse;
import com.resourcemind.app.service.DecisionService;
import com.resourcemind.app.service.InternalToolsService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/tools")
public class InternalToolsController {

	private final InternalToolsService internalToolsService;
	private final DecisionService decisionService;

	public InternalToolsController(InternalToolsService internalToolsService, DecisionService decisionService) {
		this.internalToolsService = internalToolsService;
		this.decisionService = decisionService;
	}

	@GetMapping("/benchmark")
	public BenchmarkToolResponse benchmark(
			@RequestParam("type") String useCaseType,
			@RequestParam(value = "profile", required = false) String workloadProfile
	) {
		return internalToolsService.benchmark(useCaseType, workloadProfile);
	}

	@GetMapping("/history")
	public List<HistoryEntryResponse> history(
			@RequestParam("useCase") String useCaseKey,
			@RequestParam(value = "profile", required = false) String workloadProfile
	) {
		return internalToolsService.history(useCaseKey, workloadProfile);
	}

	@GetMapping("/resources")
	public ResourcesToolResponse resources() {
		return internalToolsService.resources();
	}

	@PostMapping("/decision")
	public ProposalResponse decision(@Valid @RequestBody AgentDecisionRequest request) {
		return decisionService.apply(request);
	}
}
