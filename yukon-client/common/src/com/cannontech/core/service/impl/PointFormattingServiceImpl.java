package com.cannontech.core.service.impl;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class PointFormattingServiceImpl implements PointFormattingService {
    @Autowired private PointDao pointDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private TemplateProcessorFactory templateProcessorFactory;
    @Autowired private PointService pointService;
    private final Logger log = YukonLogManager.getLogger(PointFormattingServiceImpl.class);

    @Override
    public CachingPointFormattingService getCachedInstance() {
    	CachingPointFormattingService impl = new CachingPointFormattingService() {
            
            private final Map<Integer, LitePoint> litePointCache = new HashMap<Integer, LitePoint>();
            private final Map<Integer, LitePointUnit> pointUnitCache = new HashMap<Integer, LitePointUnit>();

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
                LitePoint litePoint = litePointCache.get(data.getId());
                if (litePoint == null) {
                    litePoint = pointDao.getLitePoint(data.getId());
                    litePointCache.put(data.getId(), litePoint);
                }
                
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
	                    stateColor = Colors.getColor(liteState.getFgColor());
	                
	                    /* Use custom colors for red and green.
	                     * Should be the same as the error and success styles in yukon.css 
	                     * YUK-9652 will solve this problem, at which point this can be reverted. */
	                    if (stateColor == Color.green) {
	                        stateColor = new Color(0,153, 51); //#009933
	                    } else if (stateColor == Color.red) {
	                        stateColor = new Color(209, 72, 54); //#D14836
	                    } else if (stateColor == Color.orange) {
	                        stateColor = new Color(255, 153, 0); //#FF9900
	                    }
	                }
                }
                    
                if (pointType.hasUnitMeasure()) {
                    value = data.getValue();
                    if (templateProcessor.contains(format, "unit") || templateProcessor.contains(format, "default")) {
                        // point unit
                        LitePointUnit pointUnit = pointUnitCache.get(data.getId());
                        if (pointUnit == null) {
                            pointUnit = pointDao.getPointUnit(data.getId());
                            pointUnitCache.put(data.getId(), pointUnit);
                        }

                        // unit of measure
                        int uOfMId = pointUnit.getUomID();
                        // unitMeasureDao caches internally
                        LiteUnitMeasure unitOfMeasure = unitMeasureDao.getLiteUnitMeasure(uOfMId);

                        if (unitOfMeasure != null) {
                            unitString = unitOfMeasure.getUnitMeasureName();
                        } else {
                            log.debug("Couldn't load LiteUnitMeasure for point " + data.getId());
                        }

                        try {
                            decimalDigits = pointUnit.getDecimalPlaces();
                        } catch (NotFoundException e) {
                            log.debug("Couldn't load LitePointUnit for point " + data.getId() + ", using default");
                        }
                        NumberFormat numberInstance = NumberFormat.getNumberInstance(userContext.getLocale());
                        numberInstance.setMinimumFractionDigits(decimalDigits);
                        numberInstance.setMaximumFractionDigits(decimalDigits);
                        valueStr = numberInstance.format(data.getValue());
                    }
                }
                Map<String,Object> params = new HashMap<String, Object>();
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
                String result = getValueString(value, message, userContext);
                return result;
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
        };
        return impl;
    }
    
    private String getDisplayStringForQuality(PointQuality quality, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String key = "yukon.common.point.pointFormatting.shortQuality." + quality.name();
        String message = messageSourceAccessor.getMessageWithDefault(key, quality.getAbbreviation());
        
        return message;
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