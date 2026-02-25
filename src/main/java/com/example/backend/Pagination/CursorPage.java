package com.example.backend.pagination;

import java.util.List;

public class CursorPage<T> {

    private final List<T> data;
    private final Long nextCursor;
    private final boolean hasMore;

    public CursorPage(List<T> data, Long nextCursor, boolean hasMore) {
        this.data = data;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
    }

    public List<T> getData() {
        return data;
    }

    public Long getNextCursor() {
        return nextCursor;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}