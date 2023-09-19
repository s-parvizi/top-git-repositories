package com.git.top.topgit.controller;

import com.git.top.topgit.model.GitHubResponseDto;
import com.git.top.topgit.service.GitHubApiClient;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repositories")
@Validated
public class RepositoryController {

    private final GitHubApiClient gitHubApiClient;

    @GetMapping("/top")
    public Mono<GitHubResponseDto> getTopRepositories(
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date should be in YYYY-MM-DD format")
            @RequestParam String date,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int pageSize) {
        return gitHubApiClient.getTopRepositories(date, Optional.ofNullable(language), page, pageSize);
    }
}
