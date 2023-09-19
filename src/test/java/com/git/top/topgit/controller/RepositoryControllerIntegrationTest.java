package com.git.top.topgit.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@WireMockTest(httpsEnabled = true, httpPort = 8888)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureWebTestClient
class RepositoryControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void testGetTopRepositories_gitHubReturnOk_shouldReturnOk() {
        stubFor(get("/search/repositories?q=created:%3E%3D2020-01-01&sort=stars&order=desc&page=1&per_page=30")
                .willReturn(ok()));

        webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json");
    }

    @Test
    void testGetTopRepositories_invalidRequest_shouldReturnServerError() {
        stubFor(get("/search/repositories?q=created:%3E%3D2020-01-01&sort=stars&order=desc&page=1&per_page=30")
                .willReturn(ok()));

        webTestClient.get().uri("/repositories/top")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().valueEquals("Content-type", "application/json");
    }

    @Test
    void testGetTopRepositories_gitHubReturn400Error_shouldReturn400() {
        stubFor(get("/search/repositories?q=created:%3E%3D2020-01-01&sort=stars&order=desc&page=1&per_page=30")
                .willReturn(badRequest()));

        webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectHeader().valueEquals("Content-type", "application/json");
    }

    @Test
    void testGetTopRepositories_gitHubReturn500Error_shouldReturn400() {
        stubFor(get("/search/repositories?q=created:%3E%3D2020-01-01&sort=stars&order=desc&page=1&per_page=30")
                .willReturn(serverError()));

        webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectHeader().valueEquals("Content-type", "application/json");
    }

    @Test
    void testGetTopRepositories_gitHubReturn503Error_shouldReturn503() {
        stubFor(get("/search/repositories?q=created:%3E%3D2020-01-01&sort=stars&order=desc&page=1&per_page=30")
                .willReturn(serviceUnavailable()));

        webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectHeader().valueEquals("Content-type", "application/json");
    }

    @Test
    void testGetTopRepositories_gitHubReturnResponse_shouldReturnResponse() {
        stubFor(get("/search/repositories?q=created:%3E%3D2020-01-01&sort=stars&order=desc&page=1&per_page=30")
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github_api_response_1_items.json")));

        EntityExchangeResult<byte[]> entityExchangeResult = webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.items.length()").isEqualTo(1)
                .returnResult();

        assertThat(entityExchangeResult.getResponseBody()).isNotNull();
    }

    @Test
    void testGetTopRepositories_gitHubReturn10Items_shouldReturn10Items() {
        stubFor(get("/search/repositories?q=created:%3E%3D2020-01-01&sort=stars&order=desc&page=1&per_page=30")
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("github_api_response_10_items.json")));

        webTestClient.get().uri("/repositories/top?date=2020-01-01")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.items.length()").isEqualTo(10);
    }
}
