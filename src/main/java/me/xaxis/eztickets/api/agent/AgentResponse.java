package me.xaxis.eztickets.api.agent;

import java.time.Instant;

public record AgentResponse(
        Long id,
        String displayName,
        String email,
        Instant createdAt
) {
}
