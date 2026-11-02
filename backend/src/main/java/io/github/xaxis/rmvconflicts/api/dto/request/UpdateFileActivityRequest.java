package io.github.xaxis.rmvconflicts.api.dto.request;

import io.github.xaxis.rmvconflicts.activity.ActivityType;

public record UpdateFileActivityRequest(
    Long projectId, Long userId, String filePath, ActivityType activityType) {}
