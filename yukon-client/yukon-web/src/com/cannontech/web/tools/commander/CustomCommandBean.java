package com.cannontech.web.tools.commander;

import java.util.ArrayList;
import java.util.List;

public class CustomCommandBean {
    
    private List<DeviceCommandDetail> detail = new ArrayList<>();
    private String selectedCategory;

    public List<DeviceCommandDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<DeviceCommandDetail> detail) {
        this.detail = detail;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

}
