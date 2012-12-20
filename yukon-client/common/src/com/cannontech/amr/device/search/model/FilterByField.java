package com.cannontech.amr.device.search.model;


abstract public class FilterByField<T> implements EditableFilterBy<T> {
    private SearchField searchField;
    private T filterValue;

    public FilterByField(SearchField searchField, T filterValue) {
        this.searchField = searchField;
        this.filterValue = filterValue;
    }

    public SearchField getSearchField() {
        return searchField;
    }

    @Override
    public boolean hasValue() {
        return (filterValue != null);
    }

    @Override
    public String getName() {
        return searchField.getFieldName();
    }

    @Override
    public String getFormatKey() {
        return searchField.getFormatKey();
    }

    @Override
    public T getFilterValue() {
        return filterValue;
    }

    @Override
    public void setFilterValue(T filterValue) {
        this.filterValue = filterValue;
    }
}
