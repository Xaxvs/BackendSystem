package io.github.xaxis.rmvconflicts.api.dto.request;

import io.github.xaxis.rmvconflicts.membership.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UserMembershipCreationRequest(
    @NotNull Long projectId,
    @NotNull Long userId,
    @NotNull ProjectRole projectRole
) {
}
