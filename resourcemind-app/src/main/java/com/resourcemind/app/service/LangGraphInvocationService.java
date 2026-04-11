package com.resourcemind.app.service;

import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class LangGraphInvocationService {

	private static final Logger log = LoggerFactory.getLogger(LangGraphInvocationService.class);

	private final RestClient restClient;

	private final String baseUrl;
	private final String invokePath;

	public LangGraphInvocationService(
			@Value("${resourcemind.langgraph.base-url:}") String baseUrl,
			@Value("${resourcemind.langgraph.invoke-path:/invoke}") String invokePath
	) {
		this.restClient = RestClient.create();
		this.baseUrl = baseUrl != null ? baseUrl.strip() : "";
		this.invokePath = invokePath != null ? invokePath : "/invoke";
	}

	@Async
	public void trigger(UUID proposalId) {
		if (!StringUtils.hasText(baseUrl)) {
			log.debug("LangGraph base URL not set; skipping agent trigger for proposal {}", proposalId);
			return;
		}
		String uri = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) + invokePath
				: baseUrl + invokePath;
		Map<String, Object> body = Map.of("proposalId", proposalId.toString());
		try {
			restClient.post()
					.uri(uri)
					.contentType(MediaType.APPLICATION_JSON)
					.body(body)
					.retrieve()
					.toBodilessEntity();
			log.info("LangGraph invoke accepted for proposal {}", proposalId);
		}
		catch (RestClientException e) {
			log.warn("LangGraph invoke failed for proposal {}: {}", proposalId, e.getMessage());
		}
		catch (Exception e) {
			log.warn("LangGraph invoke failed for proposal {}", proposalId, e);
		}
	}
}
