package io.github.xaxis.rmvconflicts.api.dto.request;

public record ProjectCreationRequest(
    String name,
    Long ownerId,
    String githubRepositoryUrl
) {
}
