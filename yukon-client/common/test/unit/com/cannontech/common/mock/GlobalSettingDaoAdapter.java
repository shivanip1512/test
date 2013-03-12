package com.cannontech.common.mock;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;

public class GlobalSettingDaoAdapter implements GlobalSettingDao {

    @Override
    public String getString(GlobalSettingType setting) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean getBoolean(GlobalSettingType setting) {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getInteger(GlobalSettingType setting) {
        throw new MethodNotImplementedException();
    }

    @Override
    public <E extends Enum<E>> E getEnum(GlobalSettingType setting, Class<E> enumClass) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void verifySetting(GlobalSettingType setting) throws NotAuthorizedException {
        throw new MethodNotImplementedException();
    }

    @Override
    public GlobalSetting getSetting(GlobalSettingType globalSetting) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void valueChanged() {
        throw new MethodNotImplementedException();
    }

    @Override
    public Integer getNullableInteger(GlobalSettingType type) {
        throw new MethodNotImplementedException();
    }
}