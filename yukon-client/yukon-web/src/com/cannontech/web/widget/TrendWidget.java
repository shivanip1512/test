package com.cannontech.web.widget;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class TrendWidget extends WidgetControllerBase {

    private PaoDao paoDao = null;
    private AttributeService attributeService = null;
    private UnitMeasureDao unitMeasureDao = null;

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public void setUnitMeasureDao(UnitMeasureDao unitMeasureDao) {
        this.unitMeasureDao = unitMeasureDao;
    }

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView mav = new ModelAndView();

        // Get lite pao
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);

        // Get a list of all of the pao's attributes for which a point exists on
        // the pao
        Set<Attribute> allAtributes = attributeService.getAllExistingAtributes(pao);

        // Get point based on device and attribute
        String attributeType = WidgetParameterHelper.getStringParameter(request,
                                                                        "attributeType",
                                                                        "USAGE");
        BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributeType);
        LitePoint point = attributeService.getPointForAttribute(pao, attribute);
        LiteUnitMeasure unitOfMeasure = unitMeasureDao.getLiteUnitMeasure(point.getUofmID());

        // Get start date and period
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(new Date());
        String period = WidgetParameterHelper.getStringParameter(request, "period", "DAY");

        mav.addObject("allAtributes", allAtributes);
        mav.addObject("attributeType", attributeType);
        mav.addObject("title", "Previous " + period + "'s " + attribute.getKey());
        mav.addObject("pointIds", point.getPointID());
        mav.addObject("unitOfMeasure", unitOfMeasure.getUnitMeasureName());
        mav.addObject("startDate", startDate.getTimeInMillis());
        mav.addObject("period", period);

        return mav;
    }

}
