package com.cannontech.core.service.impl;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class PointFormattingServiceImpl implements PointFormattingService {
    private PointDao pointDao;
    private StateDao stateDao;
    private UnitMeasureDao unitMeasureDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private TemplateProcessorFactory templateProcessorFactory;
    private Logger log = YukonLogManager.getLogger(PointFormattingServiceImpl.class);

    @Required
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Required
    public void setUnitMeasureDao(UnitMeasureDao unitMeasureDao) {
        this.unitMeasureDao = unitMeasureDao;
    }
    
    @Required
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setTemplateProcessorFactory(
            TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }
    
    @Override
    public CachingPointFormattingService getCachedInstance() {
    	CachingPointFormattingService impl = new CachingPointFormattingService() {
            
            private Map<Integer, LitePoint> litePointCache = new HashMap<Integer, LitePoint>();
            private Map<Integer, LitePointUnit> pointUnitCache = new HashMap<Integer, LitePointUnit>();
            
            public String getValueString(PointValueHolder data, String format, YukonUserContext userContext) {
                FormattingTemplateProcessor templateProcessor = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
                Object value = "";
                Double rawValue = data.getValue();
                String valueStr = "";
                String unitString = "";
                String state = "";
                Color stateColor = null;
                Integer decimalDigits = 4;
                Boolean statusPoint = (data.getType() == PointTypes.STATUS_POINT || data.getType() == PointTypes.CALCULATED_STATUS_POINT);
                
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
	                LiteState liteState = stateDao.getLiteState(litePoint.getStateGroupID(),(int)data.getValue());
	                
	                if (liteState != null) {
	                    value = liteState.getStateText();
	                    valueStr = liteState.getStateText();
	
	                	state = liteState.getStateText();
	                    stateColor = Colors.getColor(liteState.getFgColor());
	                
	                    /* Use custom colors for red and green.
	                     * Should be the same as the errorRed and okGreen styles in YukonGeneralStyles.css 
	                     * YUK-9652 will solve this problem, at which point this can be reverted. */
	                    if (stateColor == Color.green) {
	                        stateColor = new Color(0,102, 51); //#006633
	                    } else if (stateColor == Color.red) {
	                        stateColor = new Color(204, 0, 0); //#CC0000
	                    }
	                }
                }
                    
                if (!statusPoint) {
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
                params.put("status", statusPoint);
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

            public String getValueString(PointValueHolder value, Format format, YukonUserContext userContext) {
                MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String message = messageSourceAccessor.getMessage(format.getFormatKey());
                String result = getValueString(value, message, userContext);
                return result;
            }
            
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

    public String getValueString(PointValueHolder value, Format format, YukonUserContext userContext) {
        return getCachedInstance().getValueString(value, format, userContext);
    }
    
    public String getValueString(PointValueHolder value, String format, YukonUserContext userContext) {
        return getCachedInstance().getValueString(value, format, userContext);
    }

}
