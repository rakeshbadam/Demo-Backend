package com.example.backend.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCursorService<E, D> {

    /**
     * Fetch entities after the given cursor using pageable (limit only).
     */
    protected abstract List<E> fetchAfterCursor(Long cursor, Pageable pageable);

    /**
     * Extract ID from entity (used to compute nextCursor).
     */
    protected abstract Long extractId(E entity);

    /**
     * Convert entity to DTO.
     */
    protected abstract D mapToDTO(E entity);

    /**
     * Generic cursor-based pagination logic.
     */
    public CursorPage<D> getPage(Long cursor, int size) {

        // ✅ Defensive size validation
        if (size <= 0) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        Pageable pageable = PageRequest.of(0, size + 1);

        List<E> entities = fetchAfterCursor(cursor, pageable);

        if (entities == null) {
            entities = Collections.emptyList();
        }

        boolean hasMore = entities.size() > size;

        // Trim extra record safely
        List<E> pageContent = hasMore
                ? entities.subList(0, size)
                : entities;

        Long nextCursor = pageContent.isEmpty()
                ? null
                : extractId(pageContent.get(pageContent.size() - 1));

        List<D> dtoList = pageContent.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new CursorPage<>(dtoList, nextCursor, hasMore);
    }
}