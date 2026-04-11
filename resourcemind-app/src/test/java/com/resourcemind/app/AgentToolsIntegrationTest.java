package com.resourcemind.app;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import com.resourcemind.app.testsupport.PostgresContainerTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(PostgresContainerTestConfiguration.class)
@EnabledIf("com.resourcemind.app.testsupport.DockerConditions#isDockerAvailable")
class AgentToolsIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void benchmarkRequiresProfileForAiWebApp() throws Exception {
		mockMvc.perform(get("/internal/tools/benchmark").param("type", "AI_WEB_APP"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("BAD_REQUEST"));
	}

	@Test
	void proposalToolingDecisionAndInventoryFlow() throws Exception {
		mockMvc.perform(get("/internal/tools/benchmark").param("type", "AI_WEB_APP").param("profile", "TEXT_GENERATION"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.standardAllocationGpus").value(1));

		mockMvc.perform(get("/internal/tools/history").param("useCase", "guessing_game"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].gpusAllocated").value(1));

		mockMvc.perform(get("/internal/tools/resources"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.availableGpus").value(20));

		var createJson = """
				{
				  "title": "AI Guessing Game",
				  "description": "Inference web app",
				  "useCaseType": "AI_WEB_APP",
				  "workloadProfile": "TEXT_GENERATION",
				  "useCaseKey": "guessing_game",
				  "requestedGpus": 4,
				  "agentCorrelationId": "thread-1"
				}
				""";
		MvcResult created = mockMvc.perform(post("/api/v1/proposals").contentType(MediaType.APPLICATION_JSON).content(createJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("PENDING"))
			.andExpect(jsonPath("$.requestedGpus").value(4))
			.andReturn();

		JsonNode body = objectMapper.readTree(created.getResponse().getContentAsString());
		String proposalId = body.get("id").asText();

		var decisionJson = """
				{
				  "proposalId": "%s",
				  "outcome": "APPROVE",
				  "approvedGpus": 2,
				  "rationaleSummary": "Compromise after audit",
				  "decisionContract": { "approvedGpus": 2, "notes": "binding" },
				  "idempotencyKey": "decision-run-1"
				}
				""".formatted(proposalId);

		mockMvc.perform(post("/internal/tools/decision").contentType(MediaType.APPLICATION_JSON).content(decisionJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("APPROVED"))
			.andExpect(jsonPath("$.approvedGpus").value(2));

		mockMvc.perform(get("/internal/tools/resources"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.availableGpus").value(18));

		mockMvc.perform(get("/api/v1/proposals/" + proposalId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.publicId").exists())
			.andExpect(jsonPath("$.status").value("APPROVED"));

		mockMvc.perform(post("/internal/tools/decision").contentType(MediaType.APPLICATION_JSON).content(decisionJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.approvedGpus").value(2));

		var duplicateDecision = decisionJson.replace("decision-run-1", "decision-run-2");
		mockMvc.perform(post("/internal/tools/decision").contentType(MediaType.APPLICATION_JSON).content(duplicateDecision))
			.andExpect(status().isConflict());
	}
}
