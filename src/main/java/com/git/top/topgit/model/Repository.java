package com.git.top.topgit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record Repository(

        @JsonProperty("id")
        int id,

        @JsonProperty("name")
        String name,

        @JsonProperty("description")
        String description,

        @JsonProperty("created_at")
        String createdAt,

        @JsonProperty("stargazers_count")
        int stargazersCount,

        @JsonProperty("language")
        String language
) {
}