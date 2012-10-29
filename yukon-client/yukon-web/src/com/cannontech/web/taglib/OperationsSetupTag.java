package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.util.ServletUtil;


@Configurable(value = "operationsSetupTagPrototype", autowire = Autowire.BY_NAME)
public class OperationsSetupTag extends YukonTagSupport {
    
    private RolePropertyDao rolePropertyDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    @Override
    public void doTag() throws JspException, IOException {
        LiteYukonUser user = ServletUtil.getYukonUser(getPageContext().getRequest());
        boolean superUser = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_SUPER_USER, user);
        boolean isEnergyCompanyOperator = yukonEnergyCompanyService.isEnergyCompanyOperator(user);
        boolean hasMultiSpeak = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP, user);
        boolean hasUserGroupEditor = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, user);

        boolean showSystemAdmin = false;
        if ((superUser || isEnergyCompanyOperator)
                || hasMultiSpeak
                || hasUserGroupEditor) {
            showSystemAdmin = true;
        }
        
        getJspContext().setAttribute("showSystemAdmin", showSystemAdmin);     
		getJspContext().setAttribute("isEnergyCompanyOperator", isEnergyCompanyOperator);        
       
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
}