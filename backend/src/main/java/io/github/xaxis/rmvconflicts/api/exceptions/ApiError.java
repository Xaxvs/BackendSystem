package io.github.xaxis.rmvconflicts.api.exceptions;

public record ApiError(
    String code,
    String message
) {
}
