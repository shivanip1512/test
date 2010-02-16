package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;

@Configurable("yukonListEntryListTagPrototype")
public class YukonListEntryListTag extends YukonTagSupport {

	private String var;
	private int accountId;
	private String listName;
	
	private ECMappingDao ecMappingDao;
    
    @Override
    public void doTag() throws JspException, IOException {
    	
    	LiteStarsEnergyCompany liteStarsEnergyCompany = ecMappingDao.getCustomerAccountEC(accountId);
    	YukonSelectionListEnum yukonSelectionListEnum = YukonSelectionListEnum.valueOf(listName);
    	List<YukonListEntry> yukonListEntries = liteStarsEnergyCompany.getYukonSelectionList(yukonSelectionListEnum.getListName()).getYukonListEntries();
    	
    	this.getJspContext().setAttribute(var, yukonListEntries);
    }

    public void setVar(String var) {
		this.var = var;
	}
    public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
    public void setListName(String listName) {
		this.listName = listName;
	}
    
    @Required
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
}
