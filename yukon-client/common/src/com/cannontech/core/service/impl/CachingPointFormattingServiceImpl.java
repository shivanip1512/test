package com.cannontech.core.service.impl;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class CachingPointFormattingServiceImpl implements CachingPointFormattingService {
    private static final String shortQualityKeyBase = "yukon.common.point.pointFormatting.shortQuality.";
    private final TemplateProcessorFactory templateProcessorFactory;
    private final PointDao pointDao;
    private final StateGroupDao stateGroupDao;
    private final YukonUserContextMessageSourceResolver messageSourceResolver;
    private final PointService pointService;
    
    private final Map<Integer, LitePoint> litePointCache = new HashMap<>();
    private final Map<Integer, LitePointUnit> pointUnitCache = new HashMap<>();

    public CachingPointFormattingServiceImpl(TemplateProcessorFactory templateProcessorFactory, PointDao pointDao,
            StateGroupDao stateGroupDao, PointService pointService,
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.templateProcessorFactory = templateProcessorFactory;
        this.pointDao = pointDao;
        this.stateGroupDao = stateGroupDao;
        this.messageSourceResolver = messageSourceResolver;
        this.pointService = pointService;
    }
    
    @Override
    public String getValueString(PointValueHolder data, String format, YukonUserContext userContext) {
        FormattingTemplateProcessor templateProcessor = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        Object value = "";
        Double rawValue = data.getValue();
        String valueStr = "";
        String unitString = "";
        String state = "";
        Color stateColor = null;
        Integer decimalDigits = 4;
        PointType pointType = PointType.getForId(data.getType());
        
        PointQuality quality = null;
        String shortQuality = "";
        if (data instanceof PointValueQualityHolder) {
            quality = ((PointValueQualityHolder)data).getPointQuality();
            shortQuality = getDisplayStringForQuality(((PointValueQualityHolder) data).getPointQuality(), userContext);
        }
        
        //LitePoint
        LitePoint litePoint = getPoint(data.getId());
        
        //State
        // Only load state information for points that have a state group other than SystemState
        // This may end up excluding something that is intentionally trying to use SystemState group
        // However, it is expected that this stategroup is truly not being utilized for actual state information
        // Excluding this group allows us to have a little more control over calls to stateDao (YUK-10270)
        if (litePoint.getStateGroupID() != StateGroupUtils.SYSTEM_STATEGROUPID) {
            LiteState liteState;

            if (litePoint.getPointTypeEnum().isStatus()) {
                liteState = stateGroupDao.findLiteState(litePoint.getStateGroupID(),(int)data.getValue());
            } else {
                //For non-status points, state is determined by alarm conditions (signals)
                liteState = pointService.getCurrentStateForNonStatusPoint(litePoint);
            }

            if (liteState != null) {
                value = liteState.getStateText();
                valueStr = liteState.getStateText();

                state = liteState.getStateText();
                YukonColorPalette yukonColor = YukonColorPalette.getColor(liteState.getFgColor());
                stateColor = yukonColor.getAwtColor();
            }
        }
        
        if (pointType.hasUnitMeasure()) {
            value = data.getValue();
            if (templateProcessor.contains(format, "unit") || templateProcessor.contains(format, "default")) {
                LitePointUnit pointUnit = getPointUnit(data.getId());
                unitString = getUnitOfMeasure(pointUnit.getUomID(), userContext);

                decimalDigits = pointUnit.getDecimalPlaces();
                NumberFormat numberInstance = NumberFormat.getNumberInstance(userContext.getLocale());
                numberInstance.setMinimumFractionDigits(decimalDigits);
                numberInstance.setMaximumFractionDigits(decimalDigits);
                valueStr = numberInstance.format(data.getValue());
            }
        }
        Map<String,Object> params = new HashMap<>();
        params.put("value", value);
        params.put("rawValue", rawValue);
        params.put("decimals", decimalDigits);
        params.put("default", valueStr);
        params.put("status", Boolean.valueOf(pointType.isStatus()));
        params.put("state", state);
        params.put("stateColor", stateColor);
        params.put("unit", unitString);
        Date pointDataTimeStamp = data.getPointDataTimeStamp();
        params.put("time", pointDataTimeStamp);
        params.put("quality", quality);
        params.put("shortQuality", shortQuality);
        String result = templateProcessor.process(format, params);
        return result.trim();
    }
    
    @Override
    public String getValueString(PointValueHolder value, Format format, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String message = messageSourceAccessor.getMessage(format.getFormatKey());
        return getValueString(value, message, userContext);
    }
    
    @Override
    public void addLitePointsToCache(Iterable<LitePoint> litePoints) {
        for (LitePoint litePoint : litePoints) {
            litePointCache.put(litePoint.getPointID(), litePoint);
        }
    }

    @Override
    public CachingPointFormattingService getCachedInstance() {
        return this;
    }
    
    private String getUnitOfMeasure(int uomId, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext); 
        return messageSourceAccessor.getMessage(UnitOfMeasure.getForId(uomId));
    }
    
    private String getDisplayStringForQuality(PointQuality quality, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String key = shortQualityKeyBase + quality.name();
        return messageSourceAccessor.getMessageWithDefault(key, quality.getAbbreviation());
    }
    
    private LitePoint getPoint(int pointId) {
        return litePointCache.computeIfAbsent(pointId, pointDao::getLitePoint);
    }
    
    private LitePointUnit getPointUnit(int pointId) {
        return pointUnitCache.computeIfAbsent(pointId, pointDao::getPointUnit);
    }

}
