package io.github.xaxis.rmvconflicts.api.dto.response;

import io.github.xaxis.rmvconflicts.action.Action;
import io.github.xaxis.rmvconflicts.project.membership.UserMembership;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record ProjectResponse(
    Long id,
    List<Action> actions,
    Set<UserMembership> members,
    String name,
    Instant lastActivityAt,
    Instant createdAt,
    Long ownerId,
    String ownerUsername,
    String githubRepositoryUrl
) {
}
