package io.github.xaxis.rmvconflicts.api.dto.request;

import io.github.xaxis.rmvconflicts.fileclaim.ClaimState;
import io.github.xaxis.rmvconflicts.fileclaim.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record FileClaimCreationRequest(

    @NotBlank String path,
    @NotNull Instant expiresAt,
    @NotNull Long projectId,
    @NotNull Long userId,
    @NotNull FileType fileType,
    ClaimState claimState

) {}
