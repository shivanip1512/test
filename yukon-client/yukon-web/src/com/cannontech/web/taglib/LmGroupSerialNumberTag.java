package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;

@Configurable("lmGroupSerialNumberTagPrototype")
public class LmGroupSerialNumberTag extends YukonTagSupport {
    private LoadGroupDao loadGroupDao;

    private int paoId;

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        
        Integer serialNumber = loadGroupDao.findSerialNumber(paoId);
        if(serialNumber != null) {
            out.print(String.format("%d", serialNumber));
        } else {
            out.print("-");
        }
    }

    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
}
