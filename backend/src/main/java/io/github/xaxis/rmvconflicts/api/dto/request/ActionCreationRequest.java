package io.github.xaxis.rmvconflicts.api.dto.request;

import io.github.xaxis.rmvconflicts.action.UserAction;
import jakarta.validation.constraints.NotNull;

public record ActionCreationRequest(

    @NotNull Long projectId,
    @NotNull Long userId,
    @NotNull UserAction userAction

) {
}
