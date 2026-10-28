package io.github.xaxis.rmvconflicts.api.dto.request;

import io.github.xaxis.rmvconflicts.action.UserAction;

public record ActionCreationRequest(

    Long projectId,
    Long userId,
    UserAction userAction

) {
}
