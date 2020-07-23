package com.cannontech.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaginatedResponse<T> {

    private Integer pageNumber;
    private Integer itemsPerPage;
    private List<T> items;

    public PaginatedResponse(List<T> items, Integer pageNumber, Integer itemsPerPage) {
        this.items = items;
        this.pageNumber = pageNumber;
        this.itemsPerPage = itemsPerPage;
    }

    @JsonProperty
    public Integer getTotalItems() {
        return items.size();
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    @JsonProperty
    public List<T> getItems() {
        Integer startPosition = (pageNumber * itemsPerPage >= items.size() ? items.size() : pageNumber * itemsPerPage);
        Integer endPosition = (startPosition + itemsPerPage > items.size() ? items.size() : startPosition + itemsPerPage);
        return items.subList(startPosition, endPosition);
    }

    @JsonIgnore
    public void setItems(List<T> items) {
        this.items = items;
    }

}
