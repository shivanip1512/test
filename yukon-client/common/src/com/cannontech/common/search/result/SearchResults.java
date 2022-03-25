package com.cannontech.common.search.result;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.fasterxml.jackson.annotation.JsonCreator;

public class SearchResults<T> {
    private int hitCount;           //total count (possibly estimate) of results available
    private int startIndex;         //inclusive, 0-based
    private int endIndex;           //exclusive, 0-based
    private List<T> resultList;
    private int previousStartIndex;
    
    private int count = 0;          // number of results per page
    private int lastStartIndex = 0; // index of last page of results
    private int numberOfPages = 0;  // total number of result pages 
    

    private static final Logger log = YukonLogManager.getLogger(MethodHandles.lookup().lookupClass());
    
    @SuppressWarnings("unchecked")
    @JsonCreator
    public static <T> SearchResults<T> emptyResult() {
        SearchResults<Object> emptyResult = new SearchResults<>();
        List<Object> empty = Collections.emptyList();
        emptyResult.setEndIndex(0);
        emptyResult.setHitCount(0);
        emptyResult.setResultList(empty);
        emptyResult.setStartIndex(0);
        return (SearchResults<T>) emptyResult;
    }

    /**
     * Build a SearchResult containing a specific "page" of items, based on the specified parameters.
     * This method takes a list of _all_ results and will set the result list to the appropriate
     * sublist for the specified page.
     * 
     * @param currentPage The current page. The first page is numbered 1.
     */
    public static <T> SearchResults<T> pageBasedForWholeList(int currentPage, int itemsPerPage, List<T> itemList) {
        return indexBasedForWholeList((currentPage - 1) * itemsPerPage, itemsPerPage, itemList);
    }

    /**
     * Build a SearchResult containing a specific "page" of items, based on the specified parameters.
     * This method takes a list of _all_ results and will set the result list to the appropriate
     * sublist for the specified page.
     */
    public static <T> SearchResults<T> pageBasedForWholeList(PagingParameters pagingParameters, List<T> itemList) {
        return indexBasedForWholeList(pagingParameters.getStartIndex(), pagingParameters.getItemsPerPage(), itemList);
    }

    /**
     * Build a SearchResult based on a start index for a result list which contains all results.
     */
    public static <T> SearchResults<T> indexBasedForWholeList(int startIndex, int itemsPerPage, List<T> itemList) {
        int numberOfResults = itemList.size();
        int endIndex = Math.min(startIndex + itemsPerPage, numberOfResults);

        itemList = new ArrayList<>(itemList.subList(startIndex, endIndex));

        SearchResults<T> result = new SearchResults<>();
        result.setResultList(itemList);
        result.setBounds(startIndex, itemsPerPage, numberOfResults);

        return result;
    }

    /**
     * Create an instance with a list of items that has already been reduced to the items on the given page.
     */
    public static <T> SearchResults<T> pageBasedForSublist(List<T> sublist, PagingParameters pagingParameters,
                                                           int numberOfResults) {
        SearchResults<T> result = new SearchResults<>();
        result.setResultList(sublist);
        result.setBounds(pagingParameters.getStartIndex(), pagingParameters.getItemsPerPage(), numberOfResults);
        return result;
    }
    
    /**
     * Utilizes fetch/offset to find search result
     * 
     * @param template - used to do the query
     * @param paging - determines how many rows to fetch
     * @param selectSql - used to do select data
     * @param countSql - find row count
     * @param mapper - maps returned data
     * @return SearchResults
     */
    public static <T> SearchResults<T> pageBasedForOffset(YukonJdbcTemplate template, PagingParameters paging,
            SqlStatementBuilder selectSql, SqlStatementBuilder countSql, YukonRowMapper<T> mapper) {
        selectSql.append("OFFSET").append(paging.getStartIndex()).append("ROWS FETCH NEXT").append(paging.getItemsPerPage()).append("ROWS ONLY");   
        SearchResults<T> result = new SearchResults<>();
        result.setResultList(template.query(selectSql, mapper));
        log.debug("paging:{} result count:{} total count:{} sql:{}", paging, result.getResultCount(), result.hitCount, selectSql.getDebugSql());
        result.setBounds(paging.getStartIndex(), paging.getItemsPerPage(), template.queryForInt(countSql));
        return result;
    }

    /**
     * Create an instance with a list of items that has already been reduced to the items on the given page.
     */
    public static <T> SearchResults<T> pageBasedForSublist(List<T> sublist, int currentPage, int itemsPerPage,
            int numberOfResults) {
        int startIndex = (currentPage - 1) * itemsPerPage;

        SearchResults<T> result = new SearchResults<>();
        result.setResultList(sublist);
        result.setBounds(startIndex, itemsPerPage, numberOfResults);

        return result;
    }

    /**
     * @param startIndex O-based index from which to start
     * @param count number of items the caller wanted back
     * @param hitCount The number of hits, including those on previous pages
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
    
    public int getCurrentPage() {
        int currentPage = (startIndex + count) / count;
        return currentPage;
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