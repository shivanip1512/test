package com.cannontech.web.util;

public class ListBackingBean {
    private String name = null;

    private String sort = null;
    private boolean descending = false;

    private int page = 1;
    private int itemsPerPage = 25;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public boolean getDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page != null ? page : 1;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage != null ? itemsPerPage : 25;
    }

    public int getStartIndex() {
        return (page - 1) * itemsPerPage;
    }
}
