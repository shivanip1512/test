package com.cannontech.web.updater.development.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.objects.DevCapControl;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;
import com.cannontech.web.updater.development.DevExecutionUpdaterHandler;
import com.cannontech.web.updater.development.DevExecutionUpdaterTypeEnum;

public class TotalCountDevCapControlUpdaterHandler implements DevExecutionUpdaterHandler {
    private DevDatabasePopulationService devDatabasePopulationService;

    @Override
    public String handle() {
        DevDbSetupTask devDbBackingBean = devDatabasePopulationService.getExecuting();
        if (devDbBackingBean == null) {
            return StringUtils.EMPTY;
        }
        DevCapControl devCapControl = devDbBackingBean.getDevCapControl();
        if (devCapControl == null) {
            return StringUtils.EMPTY;
        }
        int count = devCapControl.getTotal();
        String result = String.valueOf(count);
        return result;
    }

    @Override
    public DevExecutionUpdaterTypeEnum getUpdaterType() {
        return DevExecutionUpdaterTypeEnum.CC_TOTAL_COUNT;
    }

    @Autowired
    public void setDevDatabasePopulationService(DevDatabasePopulationService devDatabasePopulationService) {
        this.devDatabasePopulationService = devDatabasePopulationService;
    }
}
