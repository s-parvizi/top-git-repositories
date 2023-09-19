package com.git.top.topgit.service;

import com.git.top.topgit.exception.GitHubApiClientException;
import com.git.top.topgit.exception.GitHubApiRequestException;
import com.git.top.topgit.exception.GitHubApiServerException;
import com.git.top.topgit.model.GitHubResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Service
public class GitHubApiClient {
    private final WebClient webClient;

    public GitHubApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<GitHubResponseDto> getTopRepositories(String date, Optional<String> language, int page, int pageSize) {
        String query = "created:>=" + date + language.map(lang -> "+language:" + lang).orElse("");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", query)
                        .queryParam("sort", "stars")
                        .queryParam("order", "desc")
                        .queryParam("page", page)
                        .queryParam("per_page", pageSize)
                        .build())
                .retrieve()
                .bodyToMono(GitHubResponseDto.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode().is4xxClientError()) {
                        throw new GitHubApiClientException("Client error occurred while fetching data from GitHub", e);
                    } else if (e.getStatusCode().is5xxServerError()) {
                        throw new GitHubApiServerException("Server error occurred while fetching data from GitHub", e);
                    } else {
                        return Mono.error(e);
                    }
                })
                .onErrorResume(WebClientRequestException.class, e -> {
                    throw new GitHubApiRequestException("I/O error occurred while fetching data from GitHub", e);
                });
    }
}
