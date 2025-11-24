package com.yolge.supermarket.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
    "pageNumber",
    "pageSize",
    "totalElements",
    "totalPages",
    "content"
})
public class PageResponse<T> {
    private Iterable<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
