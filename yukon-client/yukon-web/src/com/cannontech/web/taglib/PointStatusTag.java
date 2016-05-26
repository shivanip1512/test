package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.web.updater.UpdateValue;
import com.cannontech.web.updater.point.PointDataRegistrationService;

@Configurable(value="pointStatusTagPrototype", autowire=Autowire.BY_NAME)
public class PointStatusTag extends YukonTagSupport {
    
    private Integer pointId;
    private Integer rawState;
    private String format = "{stateColor|#%02X%02X%02X}";
    private String classes = ""; // Prevent 'null' from being written as a class name.
    private boolean statusPointOnly = false;
    
    @Autowired private PointDataRegistrationService registrationService;
    @Autowired private PointDao pointDao;
    @Autowired private StateGroupDao stateGroupDao;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        if (pointId == null) throw new JspException("pointId must be set");
        
        if (statusPointOnly) {
            PointType type = pointDao.getPaoPointIdentifier(pointId).getPointIdentifier().getPointType();
            if (!type.isStatus()) return;
        }

        StringBuilder boxBuilder = new StringBuilder();
        
        boxBuilder.append("<span class=\"box state-box " + classes + "\" ");
        
        String color;
        
        if (rawState == null) {
            final UpdateValue value = registrationService.getLatestValue(pointId, format, getUserContext());
            
            if (value.isUnavailable()) {
                color = "rgb(255,255,255)";
            } else {
                color = value.getValue();
            }
            
            boxBuilder.append("data-format=\"background\" ");
            boxBuilder.append("data-color-updater=\"" + value.getIdentifier().getFullIdentifier() + "\" ");
            
        } else {
            LitePoint p = pointDao.getLitePoint(pointId);
            if (p.getStateGroupID() == StateGroupUtils.SYSTEM_STATEGROUPID) {
                color = "rgb(255,255,255)";
            } else {
                LiteState s = stateGroupDao.findLiteState(p.getStateGroupID(), rawState);
                color = Colors.getColorString(s.getFgColor()).toLowerCase();
            }
        }
        
        boxBuilder.append("style=\"background-color: " + color + "\"");
        boxBuilder.append("></span>");
        
        String boxContent = boxBuilder.toString();
        final JspWriter writer = getJspContext().getOut();
        writer.print(boxContent);
    }
    
    public void setPointId(final int pointId) {
        this.pointId = pointId;
    }
    
    public void setRawState(int rawState) {
        this.rawState = rawState;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }
    
    public void setStatusPointOnly(boolean statusPointOnly) {
        this.statusPointOnly = statusPointOnly;
    }

}
