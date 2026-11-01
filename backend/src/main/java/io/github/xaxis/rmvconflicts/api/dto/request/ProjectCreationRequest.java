package io.github.xaxis.rmvconflicts.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectCreationRequest(
    @NotBlank String name,
    @NotNull Long ownerId,
    @NotBlank String githubRepositoryUrl
) {
}
