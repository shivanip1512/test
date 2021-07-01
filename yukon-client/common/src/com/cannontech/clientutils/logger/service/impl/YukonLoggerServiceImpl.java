package com.cannontech.clientutils.logger.service.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.logger.dao.YukonLoggerDao;
import com.cannontech.clientutils.logger.service.YukonLoggerService;
import com.cannontech.common.log.model.YukonLogger;

public class YukonLoggerServiceImpl implements YukonLoggerService {
    @Autowired private YukonLoggerDao yukonLoggerDao;

    @Override
    public YukonLogger getLogger(int loggerId) throws ExecutionException {
        return yukonLoggerDao.getLogger(loggerId);
    }

    @Override
    public YukonLogger addLogger(YukonLogger logger) throws ExecutionException {
        yukonLoggerDao.addLogger(logger);
        return logger;
    }

    @Override
    public YukonLogger updateLogger(YukonLogger logger) throws ExecutionException {
        yukonLoggerDao.updateLogger(logger);
        return logger;
    }

    @Override
    public int deleteLogger(int loggerId) {
        yukonLoggerDao.deleteLogger(loggerId);
        return loggerId;
    }

    @Override
    public List<YukonLogger> getLoggers() {
        return yukonLoggerDao.getLoggers();
    }
}