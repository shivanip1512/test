package com.cannontech.common.mock;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.GlobalSettingsDao;

public class GlobalSettingsDaoAdapter implements GlobalSettingsDao {

    @Override
    public String getString(GlobalSetting setting) {
        return null;
    }

    @Override
    public Boolean getBoolean(GlobalSetting setting) {
        return null;
    }

    @Override
    public Integer getInteger(GlobalSetting setting) {
        return null;
    }

    @Override
    public <E extends Enum<E>> E getEnum(GlobalSetting setting, Class<E> enumClass) {
        return null;
    }

    @Override
    public boolean checkSetting(GlobalSetting setting) {
        return false;
    }

    @Override
    public void verifySetting(GlobalSetting setting) throws NotAuthorizedException {
    }
}