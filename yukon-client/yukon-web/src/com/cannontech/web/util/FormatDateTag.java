package com.cannontech.web.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("formatDateTagPrototype")
public class FormatDateTag extends YukonTagSupport {
    private YukonUserDao userDao;
    private Date value;
    private String type;
    private String var;
    
    @Override
    public void doTag() throws JspException, IOException {
        final LiteYukonUser user = this.getYukonUser();
        final TimeZone zone = userDao.getUserTimeZone(user);
        
        DateFormat df = createDateFormat();
        df.setTimeZone(zone);
        if( value != null) {
            String formattedDate = df.format(value);
            this.getJspContext().setAttribute(var, formattedDate);
        }
    }
    
    private DateFormat createDateFormat() {
        if (type.equalsIgnoreCase("time")) {
            return new SimpleDateFormat("HH:mm");
        }

        if (type.equalsIgnoreCase("date")) {
            return new SimpleDateFormat("MM/dd/yyyy");
        }

        if (type.equalsIgnoreCase("both")) {
            return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
        }
        return new SimpleDateFormat();
    }
    
    public void setYukonUserDao(final YukonUserDao userDao) {
        this.userDao = userDao;
    }
    
    public void setValue(final Date value) {
        this.value = value;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
}
