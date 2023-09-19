package com.git.top.topgit.exception;

import com.git.top.topgit.model.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(assignableTypes = com.git.top.topgit.controller.RepositoryController.class)
public class RepositoryExceptionHandler {

    @ExceptionHandler(GitHubApiClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponseDto> handleCustomClientException(GitHubApiClientException ex) {
        log.error("Client error: ", ex);
        return Mono.just(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(GitHubApiServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponseDto> handleCustomServerException(GitHubApiServerException ex) {
        log.error("Server error: ", ex);
        return Mono.just(new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    @ExceptionHandler(GitHubApiRequestException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<ErrorResponseDto> handleCustomRequestException(GitHubApiRequestException ex) {
        log.error("I/O error: ", ex);
        return Mono.just(new ErrorResponseDto(HttpStatus.SERVICE_UNAVAILABLE.value(), ex.getMessage()));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ErrorResponseDto> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("Error occurred while making an external API call", ex);
        if (ex.getStatusCode().is4xxClientError()) {
            return Mono.just(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), "Error occurred while making an external API call: " + ex.getMessage()));
        } else if (ex.getStatusCode().is5xxServerError()) {
            return Mono.just(new ErrorResponseDto(HttpStatus.BAD_GATEWAY.value(), "Error occurred while making an external API call: " + ex.getMessage()));
        } else {
            return Mono.just(new ErrorResponseDto(ex.getRawStatusCode(), ex.getMessage()));
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Validation error occurred", ex);

        String errorMessage = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));

        return Mono.just(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), "Validation error: " + errorMessage));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponseDto> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return Mono.just(new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + ex.getMessage()));
    }
}
