package com.cannontech.common.model;
import static com.google.common.base.Preconditions.*;

public final class PagingParameters {

    private final int page;
    private final int itemsPerPage;
    private final int startIndex;
    private final int endIndex;

    /**
     * @param numberPerPage must be greater than 0
     * @param pageNumber is 1-based (i.e. starts at 1, not 0)
     */
    public PagingParameters(int numberPerPage, int pageNumber) {
        checkArgument(numberPerPage > 0);
        checkArgument(pageNumber > 0);

        this.itemsPerPage = numberPerPage;
        this.page = pageNumber;
        startIndex = (pageNumber - 1) * numberPerPage;
        endIndex = startIndex + numberPerPage - 1;
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
