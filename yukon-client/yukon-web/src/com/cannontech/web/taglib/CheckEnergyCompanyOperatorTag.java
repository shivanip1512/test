package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class CheckEnergyCompanyOperatorTag extends TagSupport {

    private boolean showError;
    private Integer accountId;
    private Integer inventoryId;

    @Override
    public int doStartTag() throws JspException {

        int returnValue = EVAL_BODY_INCLUDE;
        boolean isValidUser = true;
        String errorMsg = null;
        EnergyCompanyDao ecDao = YukonSpringHook.getBean(EnergyCompanyDao.class);

        YukonUserContext userContext =
            YukonUserContextUtils.getYukonUserContext((HttpServletRequest) pageContext.getRequest());
        YukonUserContextMessageSourceResolver messageSourceResolver =
            YukonSpringHook.getBean("yukonUserContextMessageSourceResolver",
                YukonUserContextMessageSourceResolver.class);
        MessageSourceAccessor messageAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        LiteYukonUser user = ServletUtil.getYukonUser(pageContext.getRequest());
        if (!ecDao.isEnergyCompanyOperator(user)) {
            isValidUser = false;
            errorMsg =
                messageAccessor.getMessage("yukon.web.taglib.checkEnergyCompanyOperatorTag.userIsNotECOperator");
            returnValue = SKIP_BODY;
        } else if (accountId != null && accountId != 0) {
            // Account is linked to the page requested
            EnergyCompany operatorEC = ecDao.getEnergyCompany(user);
            EnergyCompany accountEC = ecDao.getEnergyCompanyByAccountId(accountId);
            RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
            Boolean manageMembers = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user);
            if (manageMembers) {
                isValidUser = operatorEC.isDescendant(true, accountEC.getId());
            } else {
                isValidUser = (accountEC.getId() == operatorEC.getId());
            }
            if (!isValidUser) {
                errorMsg =
                    messageAccessor.getMessage("yukon.web.taglib.checkEnergyCompanyOperatorTag.userIsNotAuthorized");
                returnValue = SKIP_BODY;
            }
        } else if (inventoryId != null && inventoryId != 0) {
            EnergyCompany operatorEC = ecDao.getEnergyCompany(user);
            EnergyCompany inventoryEC = ecDao.getEnergyCompanyByInventoryId(inventoryId);
            RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
            Boolean manageMembers = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user);
            if (manageMembers) {
                isValidUser = operatorEC.isDescendant(true, inventoryEC.getId());
            } else {
                isValidUser = (inventoryEC.getId() == operatorEC.getId());
            }
            if (!isValidUser) {
                errorMsg =
                    messageAccessor.getMessage("yukon.web.taglib.checkEnergyCompanyOperatorTag.userIsNotAuthorized");
                returnValue = SKIP_BODY;
            }
        }
        if (showError && !isValidUser) {
            JspWriter out = pageContext.getOut();
            try {
                out.print("<div class=\"error\">");
                out.print(errorMsg);
                out.print("</div>");
            } catch (IOException e) {
                throw new JspException(e);
            }
        }
        return returnValue;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }
}
