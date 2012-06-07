package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;

@Configurable("yukonListEntryListTagPrototype")
public class YukonListEntryListTag extends YukonTagSupport {

	private String var;
	private int energyCompanyId;
	private String listName;
	private StarsDatabaseCache starsDatabaseCache;
    
    @Override
    public void doTag() throws JspException, IOException {
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
    	YukonSelectionListEnum yukonSelectionListEnum = YukonSelectionListEnum.valueOf(listName);
    	List<YukonListEntry> yukonListEntries = energyCompany.getYukonSelectionList(yukonSelectionListEnum.getListName()).getYukonListEntries();
    	
    	this.getJspContext().setAttribute(var, yukonListEntries);
    }
    
    public void setVar(String var) {
		this.var = var;
	}
    
    public void setEnergyCompanyId(int energyCompanyId) {
		this.energyCompanyId = energyCompanyId;
	}
    
    public void setListName(String listName) {
		this.listName = listName;
	}
    
    @Required
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
}