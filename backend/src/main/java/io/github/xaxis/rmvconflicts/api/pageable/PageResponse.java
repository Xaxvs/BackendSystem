package io.github.xaxis.rmvconflicts.api.pageable;

import jakarta.validation.constraints.*;
import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponse<T>(List<T> content, int page, int size, int totalPages, long totalElements, boolean first, boolean last) {

  public PageResponse(
      @NotNull List<T> content,
      @PositiveOrZero int page,
      @Positive @Min(1) @Max(100) int size,
      @PositiveOrZero int totalPages,
      @PositiveOrZero long totalElements,
      boolean first,
      boolean last) {
    this.content = content;
    this.page = page;
    this.size = size;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.first = first;
    this.last = last;
  }

  public PageResponse(@NotNull List<T> content, Page<?> page) {
    this(
        content,
        page.getNumber(),
        page.getSize(),
        page.getTotalPages(),
        page.getTotalElements(),
        page.isFirst(),
        page.isLast());
  }

}
