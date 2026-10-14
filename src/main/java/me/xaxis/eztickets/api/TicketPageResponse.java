package me.xaxis.eztickets.api;

import jakarta.validation.constraints.*;

import java.util.List;

public record TicketPageResponse(

        @NotNull
        List<TicketResponse> content,

        @PositiveOrZero
        int page,

        @Positive
        @Min(20)
        @Max(100)
        int size,

        @PositiveOrZero
        int totalPages,

        @PositiveOrZero
        long totalElements,

        boolean first,

        boolean last

) {
}
