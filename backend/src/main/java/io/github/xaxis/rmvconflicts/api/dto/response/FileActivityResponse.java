package io.github.xaxis.rmvconflicts.api.dto.response;

public record FileActivityResponse(
    Long id, String filePath, String activityType, String timestamp
) {}
