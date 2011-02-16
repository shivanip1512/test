package com.cannontech.common.search;

import java.util.Collections;
import java.util.List;

public class SearchResult<T> {
    private int hitCount;           //total count (possibly estimate) of results available
    private int startIndex;         //inclusive, 0-based
    private int endIndex;           //exclusive, 0-based
    private List<T> resultList;
    private int previousStartIndex;
    
    private int count = 0;          // number of results per page
    private int lastStartIndex = 0; // index of last page of results
    private int numberOfPages = 0;  // total number of result pages 
    
    public static <T> SearchResult<T> emptyResult() {
        List<T> empty = Collections.emptyList();
        SearchResult<T> result = new SearchResult<T>();
        result.setEndIndex(0);
        result.setHitCount(0);
        result.setResultList(empty);
        result.setStartIndex(0);
        return result;
    }
    
    /**
     * Returns a SearchResult containing a specific "page" of items, based on the specified
     * parameters.
     */
    public SearchResult<T> pageBasedForWholeList(int currentPage, int itemsPerPage, List<T> itemList) {
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = itemList.size();
        
        if(numberOfResults < toIndex) toIndex = numberOfResults;
        itemList = itemList.subList(startIndex, toIndex);
        
        SearchResult<T> result = new SearchResult<T>();
        result.setResultList(itemList);
        result.setBounds(startIndex, itemsPerPage, numberOfResults);
        
        return result;
    }
    
    /**
     * @param start O-based index from which to start
     * @param count number of items the caller wanted back
     */
    public void setBounds(int startIndex, int count, int hitCount) {
        this.startIndex = startIndex;
        this.count = count;
        this.hitCount = hitCount;

        numberOfPages = ((hitCount - 1) / count) + 1;

        endIndex = Math.min(startIndex + count, hitCount);
        previousStartIndex = Math.max(0, startIndex - count);
        lastStartIndex = (numberOfPages - 1) * count;
    }

    public boolean isNextNeeded() {
        return endIndex < hitCount;
    }
    
    public boolean isPreviousNeeded() {
        return startIndex > 0;
    }
    
    public void setHitCount(int resultCount) {
        this.hitCount = resultCount;
    }

    /**
     * @return the total number of "hits" that the query would have generated if
     *         the count were ignored. This number will always be greater than
     *         or equal to resultCount (size of the resultList).
     */
    public int getHitCount() {
        return this.hitCount;
    }

    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }
    
    /**
     * @return a list of results
     */
    public List<T> getResultList() {
        return resultList;
    }

    /**
     * @return index of the last result (0-based, exclusive)
     */
    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    /**
     * @return index of the first result (0-based, inclusive)
     */
    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * The actual number of results that are contained in this instance.
     * @return
     */
    public int getResultCount() {
        return endIndex - startIndex;
    }
    
    /**
     * The "startIndex" for the previous "resultCount" hits.
     * @return
     */
    public int getPreviousStartIndex() {
        return previousStartIndex;
    }

    /**
     * @return the maximum number of results per page
     */
    public int getCount(){
        return count;
    }
    
    public int getLastStartIndex(){
        return lastStartIndex;
    }

    public int getNumberOfPages(){
        return numberOfPages;
    }
}
