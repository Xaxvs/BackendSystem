package io.github.xaxis.rmvconflicts.api.dto.response;

import io.github.xaxis.rmvconflicts.user.UserStatus;

import java.time.Instant;

public record UserResponse(
    Long id,
    String displayName,
    String email,
    Instant createdAt,
    Instant lastActivityAt,
    UserStatus userStatus
) {
}
