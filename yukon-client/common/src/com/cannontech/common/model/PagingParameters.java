package com.cannontech.common.model;
import static com.google.common.base.Preconditions.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PagingParameters {
    
    public final static PagingParameters EVERYTHING = new PagingParameters(Integer.MAX_VALUE, 1);
    
    private final int page;
    private final int itemsPerPage;
    private final int startIndex;
    private final int endIndex;

    /**
     * @param itemsPerPage must be greater than 0
     * @param page is 1-based (i.e. starts at 1, not 0)
     */
    private PagingParameters(int itemsPerPage, int page) {
        checkArgument(itemsPerPage > 0);
        checkArgument(page > 0);

        this.itemsPerPage = itemsPerPage;
        this.page = page;
        startIndex = (page - 1) * itemsPerPage;
        endIndex = startIndex + itemsPerPage - 1;
    }

    public int getPage() {
        return page;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getOneBasedStartIndex() {
        return startIndex + 1;
    }

    public int getOneBasedEndIndex() {
        return endIndex + 1;
    }

    @Override
    public String toString() {
        return String.format("PagingParameters [page=%s, itemsPerPage=%s, startIndex=%s, endIndex=%s]",
                        page, itemsPerPage, startIndex, endIndex);
    }
    

    @JsonCreator
    public static PagingParameters of(@JsonProperty("itemsPerPage") int itemsPerPage,@JsonProperty("page") int page) {
        return new PagingParameters(itemsPerPage, page);
    }
    
}