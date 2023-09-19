package com.git.top.topgit.exception;


public class GitHubApiClientException extends RuntimeException {
    public GitHubApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}