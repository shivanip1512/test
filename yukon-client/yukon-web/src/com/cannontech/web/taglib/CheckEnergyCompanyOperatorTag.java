package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;


public class CheckEnergyCompanyOperatorTag extends TagSupport {

    private boolean showError;

    @Override
    public int doStartTag() throws JspException {
       
        int returnValue = EVAL_BODY_INCLUDE;
        EnergyCompanyDao ecDao =
                YukonSpringHook.getBean(EnergyCompanyDao.class);
        
        LiteYukonUser user = ServletUtil.getYukonUser(pageContext.getRequest());

        if (!ecDao.isEnergyCompanyOperator(user)) {
            if (showError) {
                YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext((HttpServletRequest)pageContext.getRequest());
                YukonUserContextMessageSourceResolver messageSourceResolver =
                        YukonSpringHook.getBean("yukonUserContextMessageSourceResolver", YukonUserContextMessageSourceResolver.class);
                MessageSourceAccessor messageAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String error = messageAccessor.getMessage("yukon.web.taglib.CheckEnergyCompanyOperatorTag.userIsNotECOperator");
                JspWriter out = pageContext.getOut();
                try {
                    out.print("<div class=\"error\">");
                    out.print(error);
                    out.print("</div>");
                } catch (IOException e) {
                    throw new JspException(e);
                }
            }
            returnValue = SKIP_BODY;
        }
        return returnValue;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
    }
}
