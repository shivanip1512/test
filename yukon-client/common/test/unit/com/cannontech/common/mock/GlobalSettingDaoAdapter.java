package com.cannontech.common.mock;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;

public class GlobalSettingDaoAdapter implements GlobalSettingDao {

    @Override
    public String getString(GlobalSettingType setting) {
        return null;
    }

    @Override
    public Boolean getBoolean(GlobalSettingType setting) {
        return null;
    }

    @Override
    public Integer getInteger(GlobalSettingType setting) {
        return null;
    }

    @Override
    public <E extends Enum<E>> E getEnum(GlobalSettingType setting, Class<E> enumClass) {
        return null;
    }

    @Override
    public boolean checkSetting(GlobalSettingType setting) {
        return false;
    }

    @Override
    public void verifySetting(GlobalSettingType setting) throws NotAuthorizedException {
    }

    @Override
    public GlobalSetting getSetting(GlobalSettingType globalSetting) {
        return null;
    }

    @Override
    public Object convertSettingValue(GlobalSettingType type, String value) {
        return null;
    }

    @Override
    public void clearCache() {
    }
}