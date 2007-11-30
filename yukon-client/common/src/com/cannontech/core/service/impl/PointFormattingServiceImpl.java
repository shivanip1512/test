package com.cannontech.core.service.impl;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.TemplateProcessor;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;

public class PointFormattingServiceImpl implements PointFormattingService {
    private PointDao pointDao;
    private StateDao stateDao;
    private UnitMeasureDao unitMeasureDao;
    private EnergyCompanyDao energyCompanyDao;
    private Logger log = YukonLogManager.getLogger(PointFormattingServiceImpl.class);

    public String getValueString(PointValueHolder value, Format format) {
        return getCachedInstance().getValueString(value, format);
    }
    
    public String getValueString(PointValueHolder value, String format) {
        return getCachedInstance().getValueString(value, format);
    }
    
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
    
    @Required
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
    
    @Override
    public PointFormattingService getCachedInstance() {
        PointFormattingService impl = new PointFormattingService() {
            
            private Map<Integer, LitePoint> litePointCache = new HashMap<Integer, LitePoint>();
            private Map<Integer, LiteState> stateCache = new HashMap<Integer, LiteState>();
            private Map<Integer, LiteUnitMeasure> unitCache = new HashMap<Integer, LiteUnitMeasure>();
            private Map<Integer, LitePointUnit> pointUnitCache = new HashMap<Integer, LitePointUnit>();
            private Map<LiteYukonUser, TimeZone> timeZoneCache = new HashMap<LiteYukonUser, TimeZone>();
            
            public String getValueString(PointValueHolder data, String format, TimeZone timeZone) {
                TemplateProcessor templateProcessor = new SimpleTemplateProcessor();
                Object value = "";
                String valueStr = "";
                String unitString = "";
                String state = "";
                Boolean statusPoint = data.getType() == PointTypes.STATUS_POINT;
                if (statusPoint) {
                    
                    // lite point
                    LitePoint litePoint = litePointCache.get(data.getId());
                    if (litePoint == null) {
                        litePoint = pointDao.getLitePoint(data.getId());
                        litePointCache.put(data.getId(), litePoint);
                    }
                    
                    // state group
                    LiteState liteState = stateCache.get((int) data.getValue());
                    if (liteState == null) {
                        int stateGroupId = litePoint.getStateGroupID();
                        liteState = stateDao.getLiteState(stateGroupId, (int) data.getValue());
                        stateCache.put((int) data.getValue(), liteState);
                    }
                    
                    state = liteState.getStateText();
                    value = liteState.getStateText();
                    valueStr = liteState.getStateText();
                    
                } else {
                    value = data.getValue();
                    if (templateProcessor.contains(format, "unit")) {
                        
                        // unit of measure
                        LiteUnitMeasure unitOfMeasure = unitCache.get(data.getId());
                        if (unitOfMeasure == null) {
                            unitOfMeasure = unitMeasureDao.getLiteUnitMeasureByPointID(data.getId());
                            unitCache.put(data.getId(), unitOfMeasure);
                        }
                        
                        if (unitOfMeasure != null) {
                            unitString = unitOfMeasure.getUnitMeasureName();
                        } else {
                            log.debug("Couldn't load LiteUnitMeasure for point " + data.getId());
                        }
                        
                    }

                    if (templateProcessor.contains(format, "default")) {
                        int decimalPlaces = 4;
                        try {
                            
                            // point unit
                            LitePointUnit pointUnit = pointUnitCache.get(data.getId());
                            if (pointUnit == null) {
                                pointUnit = pointDao.getPointUnit(data.getId());
                                pointUnitCache.put(data.getId(), pointUnit);
                            }
                            
                            decimalPlaces = pointUnit.getDecimalPlaces();
                        } catch (NotFoundException e) {
                            log.debug("Couldn't load LitePointUnit for point " + data.getId() + ", using default");
                        }
                        NumberFormat numberInstance = NumberFormat.getNumberInstance();
                        numberInstance.setMinimumFractionDigits(decimalPlaces);
                        numberInstance.setMaximumFractionDigits(decimalPlaces);
                        valueStr = numberInstance.format(data.getValue());
                    }
                }
                Map<String,Object> params = new HashMap<String, Object>();
                params.put("value", value);
                params.put("default", valueStr);
                params.put("status", statusPoint);
                params.put("state", state);
                params.put("unit", unitString);
                Date pointDataTimeStamp = data.getPointDataTimeStamp();
                Calendar pointDataCal = Calendar.getInstance(timeZone);
                pointDataCal.setTime(pointDataTimeStamp);
                params.put("time", pointDataCal);
                String result = templateProcessor.process(format, params);
                return result;
            }

            public String getValueString(PointValueHolder value, String format, LiteYukonUser user) {
                TimeZone timeZone = timeZoneCache.get(user);
                if (timeZone == null) {
                    LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
                    timeZone = energyCompanyDao.getEnergyCompanyTimeZone(energyCompany);
                    timeZoneCache.put(user, timeZone);
                }

                return getValueString(value, format, timeZone);
            }

            public String getValueString(PointValueHolder value, Format format) {
                return getValueString(value, format.getFormat(), TimeZone.getDefault());
            }
            
            public String getValueString(PointValueHolder value, String format) {
                return getValueString(value, format, TimeZone.getDefault());
            }
            
            public String getValueString(PointValueHolder value, Format format, LiteYukonUser user) {
                TimeZone timeZone = timeZoneCache.get(user);
                if (timeZone == null) {
                    LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
                    timeZone = energyCompanyDao.getEnergyCompanyTimeZone(energyCompany);
                    timeZoneCache.put(user, timeZone);
                }

                return getValueString(value, format.getFormat(), timeZone);
            }

            public String getValueString(PointValueHolder value, Format format, TimeZone timeZone) {
                return getValueString(value, format.getFormat(), timeZone);
            }
            
            @Override
            public PointFormattingService getCachedInstance() {
                return this;
            }
        };
        return impl;
    }

    public String getValueString(PointValueHolder value, String format, LiteYukonUser user) {
        return getCachedInstance().getValueString(value, format, user);
    }

    public String getValueString(PointValueHolder data, String format, TimeZone timeZone) {
        return getCachedInstance().getValueString(data, format, timeZone);
    }

    public String getValueString(PointValueHolder value, Format format, LiteYukonUser user) {
        return getCachedInstance().getValueString(value, format, user);
    }

    public String getValueString(PointValueHolder value, Format format, TimeZone timeZone) {
        return getCachedInstance().getValueString(value, format, timeZone);
    }

}
