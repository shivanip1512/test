package com.cannontech.common.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PagingParameters {
    
    public final static PagingParameters EVERYTHING = new PagingParameters(Integer.MAX_VALUE, 1);
    
    private final int pageNumber;
    private final int itemsPerPage;
    private final int startIndex;
    private final int endIndex;

    /**
     * @param itemsPerPage must be greater than 0
     * @param page is 1-based (i.e. starts at 1, not 0)
     */
    private PagingParameters(int itemsPerPage, int page) {

        this.itemsPerPage = itemsPerPage;
        this.pageNumber = page;
        startIndex = (page - 1) * itemsPerPage;
        endIndex = startIndex + itemsPerPage - 1;
    }

    public int getPageNumber() {
        return pageNumber;
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
        return String.format("PagingParameters [pageNumber=%s, itemsPerPage=%s, startIndex=%s, endIndex=%s]",
                pageNumber, itemsPerPage, startIndex, endIndex);
    }
    

    @JsonCreator
    public static PagingParameters of(@JsonProperty("itemsPerPage") int itemsPerPage,@JsonProperty("pageNumber") int pageNumber) {
        return new PagingParameters(itemsPerPage, pageNumber);
    }
    
}