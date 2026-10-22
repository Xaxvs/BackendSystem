package io.github.xaxis.rmvconflicts.api.ticket;

import jakarta.validation.constraints.NotNull;

public record AssignAgentRequest(
        @NotNull
        Long agentId
) {
}
