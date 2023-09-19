package com.git.top.topgit.exception;

public class GitHubApiRequestException extends RuntimeException {
    public GitHubApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}