package io.github.xaxis.rmvconflicts.api.dto.request;

import io.github.xaxis.rmvconflicts.fileclaim.ClaimState;
import io.github.xaxis.rmvconflicts.fileclaim.FileType;

import java.time.Instant;

public record FileClaimCreationRequest(

    String path,
    Instant expiresAt,
    Long projectId,
    Long userId,
    FileType fileType,
    ClaimState claimState

) {}
