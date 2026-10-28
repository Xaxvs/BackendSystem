package io.github.xaxis.rmvconflicts.api.dto.response;

import io.github.xaxis.rmvconflicts.membership.ProjectRole;

public record UserMembershipResponse(
    Long id,
    Long projectId,
    Long userId,
    ProjectRole projectRole
) {
}
