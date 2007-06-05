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
    
    public SearchResult() {
    }
    
    /**
     * @param start O-based index from which to start
     * @param count number of items the caller wanted back
     */
    public void setBounds(int start, int count, int hitCount) {
        this.hitCount = hitCount;
        startIndex = start;
        endIndex = Math.min(startIndex + count,hitCount);
        previousStartIndex = Math.max(0, startIndex - count);
        this.count = count;
        this.lastStartIndex = (hitCount / count) * count;
        this.numberOfPages = hitCount / count;
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
     * This is the total number of "hits" that the query would have generated
     * if the count were ignored. This number will always be greater than or
     * equal to the resultCount.
     * @return
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
