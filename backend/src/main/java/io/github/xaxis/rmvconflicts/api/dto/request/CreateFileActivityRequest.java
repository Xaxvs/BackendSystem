package io.github.xaxis.rmvconflicts.api.dto.request;

import io.github.xaxis.rmvconflicts.activity.ActivityType;

public record CreateFileActivityRequest(
    Long id, String filePath, ActivityType activityType) {}
