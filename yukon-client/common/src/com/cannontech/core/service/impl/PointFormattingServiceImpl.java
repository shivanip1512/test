package com.cannontech.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class PointFormattingServiceImpl implements PointFormattingService {
    @Autowired private PointDao pointDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private TemplateProcessorFactory templateProcessorFactory;
    @Autowired private PointService pointService;

    @Override
    public CachingPointFormattingService getCachedInstance() {
        return new CachingPointFormattingServiceImpl(templateProcessorFactory, pointDao, stateGroupDao, 
                                                     pointService, messageSourceResolver); 
    }
    
    @Override
    public String getValueString(PointValueHolder value, Format format, YukonUserContext userContext) {
        return getCachedInstance().getValueString(value, format, userContext);
    }

    @Override
    public String getValueString(PointValueHolder value, String format, YukonUserContext userContext) {
        return getCachedInstance().getValueString(value, format, userContext);
    }
}