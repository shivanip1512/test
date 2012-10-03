package com.cannontech.common.mock;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;

public class YukonSettingsDaoAdapter implements YukonSettingsDao {

    @Override
    public String getSettingStringValue(YukonSetting setting) {
        return null;
    }

    @Override
    public Boolean getSettingBooleanValue(YukonSetting setting) {
        return null;
    }

    @Override
    public Integer getSettingIntegerValue(YukonSetting setting) {
        return null;
    }

    @Override
    public <E extends Enum<E>> E getSettingEnumValue(YukonSetting setting, Class<E> enumClass) {
        return null;
    }

    @Override
    public boolean checkSetting(YukonSetting setting) {
        return false;
    }

    @Override
    public void verifySetting(YukonSetting setting) throws NotAuthorizedException {
    }
}