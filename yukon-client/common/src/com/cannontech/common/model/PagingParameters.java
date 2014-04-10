package com.cannontech.common.model;
import static com.google.common.base.Preconditions.*;

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
    public PagingParameters(int itemsPerPage, int page) {
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
}
