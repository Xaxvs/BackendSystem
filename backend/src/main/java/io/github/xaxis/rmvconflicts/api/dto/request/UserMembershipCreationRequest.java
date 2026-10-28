package io.github.xaxis.rmvconflicts.api.dto.request;

import io.github.xaxis.rmvconflicts.membership.ProjectRole;

public record UserMembershipCreationRequest(
    Long projectId,
    Long userId,
    ProjectRole projectRole
) {
}
