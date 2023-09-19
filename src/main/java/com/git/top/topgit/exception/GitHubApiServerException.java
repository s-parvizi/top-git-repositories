package com.git.top.topgit.exception;

public class GitHubApiServerException extends RuntimeException {
    public GitHubApiServerException(String message, Throwable cause) {
        super(message, cause);
    }
}