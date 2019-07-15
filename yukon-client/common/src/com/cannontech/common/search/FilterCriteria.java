package com.cannontech.common.search;

import org.apache.commons.lang3.Validate;

import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;

public class FilterCriteria<T> {
    
    private PagingParameters pagingParameters;
    private SortingParameters sortingParameters;
    private T filteringParameter;
    
    public FilterCriteria (T filteringParameters, SortingParameters sortingParameters) {
        Validate.notNull(filteringParameters, "filteringParameters must not be null");
        Validate.notNull(sortingParameters, "SortingParameters must not be null");
        this.filteringParameter = filteringParameters;
        this.sortingParameters = sortingParameters;
        this.pagingParameters = PagingParameters.EVERYTHING;
    }
    
    public FilterCriteria (T filteringParameters, SortingParameters sortingParameters, PagingParameters pagingParameters) {
        Validate.notNull(filteringParameters, "filteringParameters must not be null");
        Validate.notNull(sortingParameters, "SortingParameters must not be null");
        this.filteringParameter = filteringParameters;
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

    public T getFilteringParameter() {
        return filteringParameter;
    }

    public void setFilteringParameter(T filteringParameter) {
        this.filteringParameter = filteringParameter;
    }

}
