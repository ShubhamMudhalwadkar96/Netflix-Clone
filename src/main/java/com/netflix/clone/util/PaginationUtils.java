package com.netflix.clone.util;

import com.netflix.clone.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

public class PaginationUtils {

    private PaginationUtils() {

    }

    /**
     * Creates a pageable request with descending sorting.
     *
     * @param page zero-based page index
     * @param size number of records per page
     * @param sortBy entity field to sort by in descending order
     * @return configured {@link Pageable} instance
     */
    public static Pageable createPageRequest(int page, int size, String sortBy) {
        return PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, sortBy));
    }

    /**
     * Creates a pageable request without any sorting.
     *
     * @param page zero-based page index
     * @param size number of records per page
     * @return configured {@link Pageable} instance
     */
    public static Pageable createPageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }

    /**
     * Converts a Spring Data {@link Page} into a custom {@link PageResponse}
     * by mapping each entity to a response DTO.
     *
     * @param page the paginated entity result
     * @param mapper function used to convert each entity into its corresponding DTO
     * @param <T> source entity type
     * @param <R> destination DTO type
     * @return custom paginated response containing mapped content and pagination metadata
     */
    public static <T,R>PageResponse<R> toPageResponse(Page<T> page, Function<T,R> mapper) {
        List<R> content = page.getContent()
                .stream()
                .map(mapper)
                .toList();

        return new PageResponse<>(content, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }

    /**
     * Converts a Spring Data {@link Page} into a custom {@link PageResponse}
     * using an already mapped list of response objects.
     *
     * <p>This method is useful when DTO mapping requires additional business logic,
     * data enrichment, or information from multiple sources.</p>
     *
     * @param page original Spring Data page
     * @param mappedContent pre-mapped response content
     * @param <R> response DTO type
     * @return custom paginated response containing the provided content and pagination metadata
     */
    public static <R> PageResponse<R> toPageResponse(Page<?> page, List<R> mappedContent) {
        return new PageResponse<>(mappedContent, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }
}
