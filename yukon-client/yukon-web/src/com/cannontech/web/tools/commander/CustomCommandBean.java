package com.cannontech.web.tools.commander;

import java.util.ArrayList;
import java.util.List;

public class CustomCommandBean {
    
    private List<DeviceCommandDetail> detail = new ArrayList<>();

    public List<DeviceCommandDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<DeviceCommandDetail> detail) {
        this.detail = detail;
    }

}
