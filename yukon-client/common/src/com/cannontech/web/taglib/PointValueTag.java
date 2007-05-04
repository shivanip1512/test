package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.spring.YukonSpringHook;

public class PointValueTag extends SimpleTagSupport {
    private DynamicDataSource dynamicDataSource = YukonSpringHook.getBean("dynamicDataSource", DynamicDataSource.class);
    private UnitMeasureDao unitMeasureDao = YukonSpringHook.getBean("unitMeasureDao", UnitMeasureDao.class);
    private StateDao stateDao;
    private PointDao pointDao;
    private int pointId = 0;
    private boolean pointIdSet = false;
    private PointValueHolder pointValue = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (pointIdSet && pointValue != null) {
            throw new JspException("pointId and pointValue should not both be set");
        }
        
        if (!pointIdSet && pointValue == null) {
            throw new JspException("either pointId or pointValue must be set");
        }
        
        if (pointIdSet) {
            pointValue = dynamicDataSource.getPointData(pointId);
        } else {
            pointId = pointValue.getId();
        }
        
        String valueString = "";
        if (pointValue.getType() != PointTypes.STATUS_POINT) {
            LiteUnitMeasure unitOfMeasure = unitMeasureDao.getLiteUnitMeasureByPointID(pointId);
            String unitMeasureName = unitOfMeasure.getUnitMeasureName();
            valueString = pointValue.getValue() + " " + unitMeasureName;
        } else {
            LitePoint litePoint = pointDao.getLitePoint(pointId);
            int stateGroupId = litePoint.getStateGroupID();
            LiteState liteState = stateDao.getLiteState(stateGroupId, (int) pointValue.getValue());
            valueString = liteState.getStateText();
        }
        
        JspWriter out = getJspContext().getOut();
        out.print("<span class=\"pointValueTagSpan" + pointId + "\" title=\"" + pointValue.getPointDataTimeStamp() + "\">");
        out.print(valueString);
        out.print("</span>");
    }
    
    public int getPointId() {
        return pointId;
    }
    public void setPointId(int pointId) {
        pointIdSet = true;
        this.pointId = pointId;
    }
    public PointValueHolder getPointValue() {
        return pointValue;
    }
    public void setPointValue(PointValueHolder pointValue) {
        this.pointValue = pointValue;
    }

}
