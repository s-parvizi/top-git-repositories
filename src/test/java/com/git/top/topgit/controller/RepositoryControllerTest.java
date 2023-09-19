package com.git.top.topgit.controller;

import com.git.top.topgit.exception.GitHubApiClientException;
import com.git.top.topgit.exception.GitHubApiRequestException;
import com.git.top.topgit.exception.GitHubApiServerException;
import com.git.top.topgit.model.GitHubResponseDto;
import com.git.top.topgit.model.Repository;
import com.git.top.topgit.service.GitHubApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;


@WebFluxTest(RepositoryController.class)
class RepositoryControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private GitHubApiClient gitHubApiClient;

    @Test
    void testGetTopRepositories_withoutProvidingRequiredParameters_returnServerError() {
        GitHubResponseDto mockResponse = getGitHubResponseDto();

        given(gitHubApiClient.getTopRepositories("2020-01-01", Optional.empty(), 1, 30))
                .willReturn(Mono.just(mockResponse));

        webTestClient.get().uri("/repositories/top")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().valueEquals("Content-type", "application/json");
    }

    @Test
    void testGetTopRepositories_validRequest_successfulResponse() {
        GitHubResponseDto mockResponse = getGitHubResponseDto();

        given(gitHubApiClient.getTopRepositories("2020-01-01", Optional.empty(), 1, 30))
                .willReturn(Mono.just(mockResponse));

        webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.items.length()").isEqualTo(5);
    }

    @Test
    void testGetTopRepositories_gitHubApiClientException_badRequest() {
        given(gitHubApiClient.getTopRepositories("2020-01-01", Optional.empty(), 1, 30))
                .willThrow(GitHubApiClientException.class);

        webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetTopRepositories_gitHubApiServerException_serverError() {
        given(gitHubApiClient.getTopRepositories("2020-01-01", Optional.empty(), 1, 30))
                .willThrow(GitHubApiServerException.class);

        webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testGetTopRepositories_gitHubApiRequestException_serverError() {
        given(gitHubApiClient.getTopRepositories("2020-01-01", Optional.empty(), 1, 30))
                .willThrow(GitHubApiRequestException.class);

        webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    private GitHubResponseDto getGitHubResponseDto() {
        Repository repository1 = new Repository(1, "reponame1", "description", "2020-01-01", 11, "java");
        Repository repository2 = new Repository(2, "reponame2", "description", "2020-01-01", 12, "java");
        Repository repository3 = new Repository(3, "reponame3", "description", "2020-01-01", 13, "java");
        Repository repository4 = new Repository(4, "reponame4", "description", "2020-01-01", 14, "java");
        Repository repository5 = new Repository(5, "reponame5", "description", "2020-01-01", 15, "java");

        return new GitHubResponseDto(10, List.of(repository1, repository2, repository3, repository4, repository5));
    }
}
