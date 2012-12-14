package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;

@Configurable("lmGroupCapacityTagPrototype")
public class LmGroupCapacityTag extends YukonTagSupport {
    private LoadGroupDao loadGroupDao;
    private ObjectFormattingService objectFormattingService;
    
    private int paoId;

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        
        Double capacity = loadGroupDao.findCapacity(paoId);
        
        if(capacity != null) {
            out.print(objectFormattingService.formatObjectAsString(capacity, getUserContext()));
        } else {
            out.print("-");
        }
    }

    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }

    public void setObjectFormattingService(ObjectFormattingService objectFormattingService) {
        this.objectFormattingService = objectFormattingService;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
}
