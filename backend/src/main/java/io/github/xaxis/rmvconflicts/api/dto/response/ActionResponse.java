package io.github.xaxis.rmvconflicts.api.dto.response;

import io.github.xaxis.rmvconflicts.action.UserAction;

import java.time.Instant;

public record ActionResponse(
    Long id,
    Long projectId,
    Long userId,
    UserAction userAction,
    Instant createdAt
) {
}
