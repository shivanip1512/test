package com.cannontech.web.common.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardSettings {
    
    private List<DashboardSetting> settings;

    public List<DashboardSetting> getSettings() {
        return settings;
    }

    public void setSettings(List<DashboardSetting> settings) {
        this.settings = settings;
    }
    
    public void addSetting(DashboardSetting setting) {
        if (settings == null) {
            settings = new ArrayList<>();
        }
        settings.add(setting);
    }

}
