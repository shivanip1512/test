package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.List;

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

public class CheckAccountEnergyCompanyOperatorTag extends TagSupport {

    private boolean showError;
    

    @Override
    public int doStartTag() throws JspException {

        int returnValue = EVAL_BODY_INCLUDE;
        boolean isValidUser = true;
        String errorMsg = null;
        EnergyCompanyDao ecDao =
                YukonSpringHook.getBean(EnergyCompanyDao.class);

        YukonUserContext userContext =
            YukonUserContextUtils.getYukonUserContext((HttpServletRequest) pageContext.getRequest());
        YukonUserContextMessageSourceResolver messageSourceResolver =
            YukonSpringHook.getBean("yukonUserContextMessageSourceResolver",
                YukonUserContextMessageSourceResolver.class);
        MessageSourceAccessor messageAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        LiteYukonUser user = ServletUtil.getYukonUser(pageContext.getRequest());
        String accountId = ServletUtil.getParameter((HttpServletRequest) pageContext.getRequest(), "accountId");
        if (!ecDao.isEnergyCompanyOperator(user)) {
            isValidUser = false;
            errorMsg = messageAccessor.getMessage("yukon.web.taglib.CheckAccountEnergyCompanyOperatorTag.userIsNotECOperator");
            returnValue = SKIP_BODY;
        } else if (accountId != null && !accountId.equals("0")) {
            // Account is linked to the page requested
            EnergyCompany operatorEC = ecDao.getEnergyCompany(user);
            EnergyCompany accountEC = ecDao.getEnergyCompanyByAccountId(Integer.parseInt(accountId));
            if (accountEC.getId() != operatorEC.getId()) {
                List<EnergyCompany> childECs = operatorEC.getChildren();
                boolean isMemberEC = false;
                for (EnergyCompany child : childECs) {
                    if (child.getId() == accountEC.getId()) {
                        isMemberEC = true; // Account belongs to the Member EC of Operator
                    }
                }
                RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
                Boolean hasAdminManageMembersPrivilege =
                    rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user);
                if (!(isMemberEC && hasAdminManageMembersPrivilege)) {
                    // Check IF user has admin manage member energy companies role property
                    errorMsg =
                        messageAccessor.getMessage("yukon.web.taglib.CheckAccountEnergyCompanyOperatorTag.userIsNotAuthorized");
                    returnValue = SKIP_BODY;
                    isValidUser = false;
                }
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
}
