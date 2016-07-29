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

/**
 * If set, the logged in user's energy company will be checked against the accountId's energy company
 */
public class CheckEnergyCompanyOperatorTag extends TagSupport {

    private boolean showError;
    private Integer accountId;
    private Integer inventoryId;

    @Override
    public int doStartTag() throws JspException {
        int returnValue = EVAL_BODY_INCLUDE;
        String errorCode = null; // used to also determine valid user
        EnergyCompanyDao ecDao = YukonSpringHook.getBean(EnergyCompanyDao.class);

        LiteYukonUser user = ServletUtil.getYukonUser(pageContext.getRequest());
        if (!ecDao.isEnergyCompanyOperator(user)) {
            errorCode = "yukon.web.taglib.checkEnergyCompanyOperatorTag.userIsNotECOperator";
        } else if (accountId != null && accountId != 0) {
            // Account is linked to the page requested
            EnergyCompany accountEC = ecDao.getEnergyCompanyByAccountId(accountId);
            if (!isEcOperatorOf(ecDao, user, accountEC)) {
                errorCode = "yukon.web.taglib.checkEnergyCompanyOperatorTag.userIsNotAuthorized";
            }
        } else if (inventoryId != null && inventoryId != 0) {
            EnergyCompany inventoryEC = ecDao.getEnergyCompanyByInventoryId(inventoryId);
            if (!isEcOperatorOf(ecDao, user, inventoryEC)) {
                errorCode = "yukon.web.taglib.checkEnergyCompanyOperatorTag.userIsNotAuthorized";
            }
        }

        if (errorCode != null) {
            if (showError) {
                YukonUserContext userContext =
                    YukonUserContextUtils.getYukonUserContext((HttpServletRequest) pageContext.getRequest());
                YukonUserContextMessageSourceResolver messageSourceResolver =
                    YukonSpringHook.getBean("yukonUserContextMessageSourceResolver",
                        YukonUserContextMessageSourceResolver.class);
                MessageSourceAccessor messageAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

                String errorMsg = messageAccessor.getMessage(errorCode);

                JspWriter out = pageContext.getOut();
                try {
                    out.print("<div class=\"error\">");
                    out.print(errorMsg);
                    out.print("</div>");
                } catch (IOException e) {
                    throw new JspException(e);
                }
            }
            returnValue = SKIP_BODY;
        }
        return returnValue;
    }

    /**
     * Returns true if user's energy company is a valid EC Operator for of energyCompany.
     * Where of may be an account's EC or inventory's EC
     */
    private boolean isEcOperatorOf(EnergyCompanyDao ecDao, LiteYukonUser user, EnergyCompany of) {
        boolean isValidUser;
        EnergyCompany operatorEC = ecDao.getEnergyCompany(user);
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
        Boolean manageMembers = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user);
        if (manageMembers) {
            isValidUser = operatorEC.isDescendant(true, of.getId());
        } else {
            isValidUser = (of.getId() == operatorEC.getId());
        }
        return isValidUser;
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
