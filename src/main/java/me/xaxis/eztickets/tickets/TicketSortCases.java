package me.xaxis.eztickets.tickets;

public enum TicketSortCases {

    CREATED_AT("createdAt"),
    TITLE("title"),
    STATUS("status");

    private final String string;

    TicketSortCases(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
