package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;

@Configurable("paoNameTagPrototype")
public class PaoNameTag extends YukonTagSupport {
    
	private String var = null;
    private PaoDao paoDao; 
    private int paoId = 0;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        String name = null;
        try{
        
        	name = paoDao.getYukonPAOName(paoId);
            
        } catch (NotFoundException e) {
            throw new JspException("paoId: " + paoId + " is not a valid paoId", e);
        }

        JspContext jspContext = getJspContext();
        if (var == null) {
        	JspWriter out = jspContext.getOut();
            out.print("<span class=\"deviceNameTagSpan\" title=\"paoId: " + paoId + "\">");
            out.print(name);
            out.print("</span>");
        } else {
        	jspContext.setAttribute(var, name);
        }
        
    }
    
    public int getpaoId() {
        return paoId;
    }
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
    
    public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	@Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
