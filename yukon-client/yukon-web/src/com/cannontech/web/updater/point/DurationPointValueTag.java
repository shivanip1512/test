package com.cannontech.web.updater.point;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.PointDao;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.web.taglib.YukonTagSupport;
import com.cannontech.web.updater.UpdateValue;

public class DurationPointValueTag extends YukonTagSupport {
    
    @Autowired private PointDataRegistrationService registrationService;
    @Autowired private DurationFormattingService formattingService;
    @Autowired private PointDao pointDao;

    private String format = DurationFormat.DHMS_REDUCED.toString();
    private Object point;  // LitePoint or point id
    private boolean pointSet;
    private String unavailableValue = null;
    private String cssClass;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!pointSet) {
            throw new JspException("point attribute must be set (LitePoint or point id)");
        }
        
        LitePoint lp;
        if (point instanceof LitePoint) {
            lp = (LitePoint)point; 
        } else if (point instanceof Integer) {
            lp = pointDao.getLitePoint((Integer)point);
        } else {
            throw new JspException("point attribute must be of type LitePoint or Integer (point id)");
        }
        
        UnitOfMeasure uom = UnitOfMeasure.getForId(lp.getUofmID());
        if (!uom.isDuration()) {
            throw new JspException("This tag only works with points whose unit of measure is a duration.");
        }
        
        UpdateValue value = registrationService.getLatestValue(lp.getPointID(), Format.RAWVALUE.toString(), getUserContext());
        
        String outputText;
        if (value.isUnavailable()) {
            if (unavailableValue != null) {
                outputText = unavailableValue;
            } else {
                outputText = getMessageSource().getMessage("yukon.common.point.pointFormatting.unavailablePlaceholder");
            }
        } else {
            TimeUnit tu = TimeUnit.HOURS;
            if (uom == UnitOfMeasure.MINUTES) tu = TimeUnit.MINUTES; 
            if (uom == UnitOfMeasure.SECONDS) tu = TimeUnit.SECONDS; 
            if (uom == UnitOfMeasure.MS) tu = TimeUnit.MILLISECONDS; 
            outputText = formattingService.formatDuration(Long.parseLong(value.getValue()), tu, DurationFormat.valueOf(format), getUserContext());
        }
        
        JspWriter out = getJspContext().getOut();
        
        out.print("<span title=\"pointId:" + lp.getPointID() + "\" cannonUpdater=\"" + value.getFullIdentifier() + "\" class=\"pointValueTagSpan " + cssClass +"\" >");
        out.print(outputText);
        out.print("</span>");
    }

    public void setPoint(Object point) {
        pointSet = true;
        this.point = point;
    }

    /**
     * @param format either a DurationFormat enum value
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public void setUnavailableValue(String unavailableValue) {
        this.unavailableValue = unavailableValue;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
    
}