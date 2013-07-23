package com.cannontech.web.updater.point;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.util.TagUtils;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable(value="pointColorTagPrototype", autowire=Autowire.BY_NAME)
public class PointColorTag extends YukonTagSupport {
    
    @Autowired private PointDataRegistrationService registrationService;
    @Autowired private PointDao pointDao;
    @Autowired private StateDao stateDao;
    
    private int pointId;
    private int rawState;
    private String var;
    private String scope = TagUtils.SCOPE_PAGE;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        LitePoint p = pointDao.getLitePoint(pointId);
        LiteState s = stateDao.findLiteState(p.getStateGroupID(), rawState);
        String c = Colors.getColorString(s.getFgColor()).toLowerCase();
        
        if (var == null) {
            getJspContext().getOut().print(c);
        } else {
            getJspContext().setAttribute(var, c, TagUtils.getScope(scope));
        }
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }
    
    public void setRawState(int rawState) {
        this.rawState = rawState;
    }
    
    /**
     * Set PageContext attribute name under which to expose
     * a variable that contains the color.
     * @see #setScope
     * @see javax.servlet.jsp.PageContext#setAttribute
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /**
     * Set the scope to export the variable to.
     * Default is SCOPE_PAGE ("page").
     * @see #setVar
     * @see org.springframework.web.util.TagUtils#SCOPE_PAGE
     * @see javax.servlet.jsp.PageContext#setAttribute
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

}