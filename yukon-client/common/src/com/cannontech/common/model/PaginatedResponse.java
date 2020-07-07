package com.cannontech.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaginatedResponse<T> {

    private Integer totalItems;
    private Integer pageNumber;
    private Integer itemsPerPage;
    private List<T> items;

    public PaginatedResponse(List<T> items, Integer totalItems, Integer pageNumber, Integer itemsPerPage) {
        this.items = items;
        this.totalItems = totalItems;
        this.pageNumber = pageNumber;
        this.itemsPerPage = itemsPerPage;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
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
        return items;
    }

    @JsonIgnore
    public void setItems(List<T> items) {
        this.items = items;
    }

}
