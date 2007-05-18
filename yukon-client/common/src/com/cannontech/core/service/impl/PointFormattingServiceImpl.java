package com.cannontech.core.service.impl;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Required;

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

    public String getValueString(PointValueHolder value, Format format) {
        return getValueString(value, format.getFormat(), TimeZone.getDefault());
    }
    
    public String getValueString(PointValueHolder value, String format) {
        return getValueString(value, format, TimeZone.getDefault());
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

    public String getValueString(PointValueHolder value, String format, LiteYukonUser user) {
        LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
        TimeZone timeZone = energyCompanyDao.getEnergyCompanyTimeZone(energyCompany);

        return getValueString(value, format, timeZone);
    }

    public String getValueString(PointValueHolder data, String format, TimeZone timeZone) {
        TemplateProcessor templateProcessor = new SimpleTemplateProcessor();
        Object value = "";
        String valueStr = "";
        String unitString = "";
        String state = "";
        Boolean statusPoint = data.getType() == PointTypes.STATUS_POINT;
        if (statusPoint) {
            LitePoint litePoint = pointDao.getLitePoint(data.getId());
            int stateGroupId = litePoint.getStateGroupID();
            LiteState liteState = stateDao.getLiteState(stateGroupId, (int) data.getValue());
            state = liteState.getStateText();
            value = liteState.getStateText();
            valueStr = liteState.getStateText();
        } else {
            value = data.getValue();
            try {
                if (templateProcessor.contains(format, "unit")) {
                    LiteUnitMeasure unitOfMeasure = unitMeasureDao.getLiteUnitMeasureByPointID(data.getId());
                    unitString = unitOfMeasure.getUnitMeasureName();
                }
                
                if (templateProcessor.contains(format, "default")) {
                    LitePointUnit pointUnit = pointDao.getPointUnit(data.getId());
                    int decimalPlaces = pointUnit.getDecimalPlaces();
                    NumberFormat numberInstance = NumberFormat.getNumberInstance();
                    numberInstance.setMinimumFractionDigits(decimalPlaces);
                    numberInstance.setMaximumFractionDigits(decimalPlaces);
                    valueStr = numberInstance.format(data.getValue());
                }
            } catch (NotFoundException e) {
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

    public String getValueString(PointValueHolder value, Format format, LiteYukonUser user) {
        LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
        TimeZone timeZone = energyCompanyDao.getEnergyCompanyTimeZone(energyCompany);
        
        return getValueString(value, format.getFormat(), timeZone);
    }

    public String getValueString(PointValueHolder value, Format format, TimeZone timeZone) {
        return getValueString(value, format.getFormat(), timeZone);
    }

}
