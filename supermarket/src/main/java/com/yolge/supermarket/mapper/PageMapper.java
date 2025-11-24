package com.yolge.supermarket.mapper;

import com.yolge.supermarket.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageMapper {
    public <T, E> PageResponse<T> toDto(Iterable<T> content, Page<E> properties) {
        PageResponse<T> response = new PageResponse<T>();
        response.setContent(content);
        response.setPageNumber(properties.getNumber());
        response.setPageSize(properties.getSize());
        response.setTotalElements(properties.getTotalElements());
        response.setTotalPages(properties.getTotalPages());
        return response;
    }
}
