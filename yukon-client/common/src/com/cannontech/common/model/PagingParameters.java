package com.cannontech.common.model;
import static com.google.common.base.Preconditions.*;

public final class PagingParameters {

    private final int pageNumber;
    private final int numberPerPage;
    private final int startIndex;
    private final int endIndex;

    /**
     * @param numberPerPage must be greater than 0
     * @param pageNumber is 1-based (i.e. starts at 1, not 0)
     */
    public PagingParameters(int numberPerPage, int pageNumber) {
        checkArgument(numberPerPage > 0);
        checkArgument(pageNumber > 0);

        this.numberPerPage = numberPerPage;
        this.pageNumber = pageNumber;
        startIndex = (pageNumber - 1) * numberPerPage;
        endIndex = startIndex + numberPerPage - 1;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getNumberPerPage() {
        return numberPerPage;
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
