package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyService;


@Configurable(value = "operationsSetupTagPrototype", autowire = Autowire.BY_NAME)
public class OperationsSetupTag extends YukonTagSupport {
    
    private EnergyCompanyService energyCompanyService;
    private RolePropertyDao rolePropertyDao;
    
    @Override
    public void doTag() throws JspException, IOException {
        LiteYukonUser user = ServletUtil.getYukonUser(getPageContext().getRequest());
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user);
        boolean isEcOperator = energyCompanyService.isOperator(user);
        boolean hasMultiSpeak = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP, user);
        boolean hasUserGroupEditor = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, user);
        
        boolean showSystemAdmin = false;
        if ((superUser || isEcOperator)
                || hasMultiSpeak
                || hasUserGroupEditor) {
            showSystemAdmin = true;
        }
        
        getJspContext().setAttribute("showSystemAdmin", showSystemAdmin);
    }
    
    @Autowired
    public void setEnegyCompanyService(EnergyCompanyService enegyCompanyService) {
        this.energyCompanyService = enegyCompanyService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
}