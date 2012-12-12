package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;

@Configurable("paoNameTagPrototype")
public class PaoNameTag extends YukonTagSupport {
    private PaoDao paoDao;
    
    private int paoId;

    @Override
    public void doTag() throws JspException, IOException {
        String paoName;
        try {
            paoName = paoDao.getYukonPAOName(paoId);
        } catch(NotFoundException e) {
            paoName = "-";
        }
        
        JspWriter out = getJspContext().getOut();
        out.print(String.format("<span>%s</span>", paoName));
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
}
