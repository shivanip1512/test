package com.cannontech.web.updater.development.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.objects.DevStars;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;
import com.cannontech.web.updater.development.DevExecutionUpdaterHandler;
import com.cannontech.web.updater.development.DevExecutionUpdaterTypeEnum;

public class SuccessCountDevStarsUpdaterHandler implements DevExecutionUpdaterHandler {
    private DevDatabasePopulationService devDatabasePopulationService;

    @Override
    public String handle() {
        DevDbSetupTask devDbBackingBean = devDatabasePopulationService.getExecuting();
        if (devDbBackingBean == null) {
            return StringUtils.EMPTY;
        }
        DevStars devStars = devDbBackingBean.getDevStars();
        if (devStars == null) {
            return StringUtils.EMPTY;
        }
        int count = devStars.getSuccessCount();
        String result = String.valueOf(count);
        return result;
    }

    @Override
    public DevExecutionUpdaterTypeEnum getUpdaterType() {
        return DevExecutionUpdaterTypeEnum.STARS_SUCCESS_COUNT;
    }

    @Autowired
    public void setDevDatabasePopulationService(DevDatabasePopulationService devDatabasePopulationService) {
        this.devDatabasePopulationService = devDatabasePopulationService;
    }
}
