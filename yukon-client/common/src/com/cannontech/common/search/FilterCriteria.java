package com.cannontech.common.search;

import org.apache.commons.lang3.Validate;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FilterCriteria<T> {
    
    private PagingParameters pagingParameters;
    private SortingParameters sortingParameters;
    private T filteringParameters;
    
    public FilterCriteria (@JsonProperty("filteringParameters") T filteringParameters, @JsonProperty("sortingParameters") SortingParameters sortingParameters, @JsonProperty("pagingParameters") PagingParameters pagingParameters) {
        Validate.notNull(filteringParameters, "filteringParameters must not be null");
        Validate.notNull(sortingParameters, "SortingParameters must not be null");
        this.filteringParameters = filteringParameters;
        this.sortingParameters = sortingParameters;
        if (pagingParameters != null) {
            this.pagingParameters = pagingParameters;
        } else {
            this.pagingParameters = PagingParameters.EVERYTHING;
        }
        
    }

    public PagingParameters getPagingParameters() {
        return pagingParameters;
    }

    public void setPagingParameters(PagingParameters pagingParameters) {
        this.pagingParameters = pagingParameters;
    }

    public SortingParameters getSortingParameters() {
        return sortingParameters;
    }

    public void setSortingParameters(SortingParameters sortingParameters) {
        this.sortingParameters = sortingParameters;
    }

    public T getFilteringParameters() {
        return filteringParameters;
    }

    public void setFilteringParameter(T filteringParameters) {
        this.filteringParameters = filteringParameters;
    }

}
