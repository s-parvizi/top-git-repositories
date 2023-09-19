package com.git.top.topgit.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record GitHubResponseDto(

	@JsonProperty("total_count")
	int totalCount,

	@JsonProperty("items")
	List<Repository> items
) {
}