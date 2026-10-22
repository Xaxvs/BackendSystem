package io.github.xaxis.rmvconflicts.agent;

public enum AgentSortCases {
    DISPLAY_NAME("displayName"),
    CREATED_AT("createdAt")
    ;

    private final String string;
    AgentSortCases(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
