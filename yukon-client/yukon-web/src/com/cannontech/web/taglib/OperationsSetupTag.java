package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.util.ServletUtil;


@Configurable(value = "operationsSetupTagPrototype", autowire = Autowire.BY_NAME)
public class OperationsSetupTag extends YukonTagSupport {
    
    private EnergyCompanyService energyCompanyService;
    private RolePropertyDao rolePropertyDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    @Override
    public void doTag() throws JspException, IOException {
        LiteYukonUser user = ServletUtil.getYukonUser(getPageContext().getRequest());
        boolean superUser = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_SUPER_USER, user);
        boolean isEcOperator = energyCompanyService.isOperator(user);
        boolean hasMultiSpeak = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP, user);
        boolean hasUserGroupEditor = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, user);
        String energyCompanyName = null;
        try
        {
            YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
            energyCompanyName = energyCompany.getName();
        }catch (EmptyResultDataAccessException e){
            energyCompanyName = "";
        }

        boolean showSystemAdmin = false;
        if ((superUser || isEcOperator)
                || hasMultiSpeak
                || hasUserGroupEditor) {
            showSystemAdmin = true;
        }
        
        getJspContext().setAttribute("showSystemAdmin", showSystemAdmin);
        getJspContext().setAttribute("energyCompanyName", energyCompanyName);
    }
    
    @Autowired
    public void setEnegyCompanyService(EnergyCompanyService enegyCompanyService) {
        this.energyCompanyService = enegyCompanyService;
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