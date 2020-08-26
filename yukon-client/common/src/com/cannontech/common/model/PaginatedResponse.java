package com.cannontech.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaginatedResponse<T> {

    private Integer pageNumber;
    private Integer itemsPerPage;
    private List<T> items;
    private Integer totalItems;
    
    public PaginatedResponse() {
        
    }

    public PaginatedResponse(List<T> items, Integer pageNumber, Integer itemsPerPage) {
        this.pageNumber = pageNumber;
        this.itemsPerPage = itemsPerPage;
        setItems(items);
    }

    public Integer getTotalItems() {
        return totalItems;
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

    /**
     * Returns a single page from an entire list of objects, determined by the page number and items per page.
     */
    @JsonProperty(value = "items")
    public List<T> getItems() {
        return items;
    }

    /**
     * Accepts a list of items that needs to be divided into pages
     */
    @JsonIgnore
    public void setItems(List<T> allItems) {
        this.totalItems = allItems.size();
        int indexBasedPageNbr = pageNumber - 1;
        Integer startPosition = (indexBasedPageNbr * itemsPerPage >= allItems.size() ? allItems.size() : indexBasedPageNbr * itemsPerPage);
        Integer endPosition = (startPosition + itemsPerPage > allItems.size() ? allItems.size() : startPosition + itemsPerPage);
        items = allItems.subList(startPosition, endPosition);
    }

}
