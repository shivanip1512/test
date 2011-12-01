package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

@Configurable("yukonListEntryForDefinitionTagPrototype")
public class YukonListEntryForDefinitionTag extends YukonTagSupport {

    private String var;
    private int definitionId;
    private int energyCompanyId;
    
    private StarsDatabaseCache starsDatabaseCache;
    private YukonListDao yukonListDao;
    
    @Override
    public void doTag() throws JspException, IOException {
        YukonEnergyCompany ec = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        YukonListEntry entry = yukonListDao.getYukonListEntry(definitionId, ec);
        String entryText = entry.getEntryText();
        
        if (var == null) {
            getJspContext().getOut().print(entryText);
        } else {
            this.getJspContext().setAttribute(var, entryText);
        }
    }

    public void setVar(String var) {
        this.var = var;
    }
    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    public void setDefinitionId(int definitionId) {
        this.definitionId = definitionId;
    }
    
    @Required
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
    
    @Required
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
}
