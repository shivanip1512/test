package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.util.TagUtils;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;

@Configurable("paoDetailUrlTagPrototype")
public class PaoDetailPageNameTag extends YukonTagSupport {
    private PaoDetailUrlHelper paoDetailUrlHelper;

    private YukonPao yukonPao = null;
    private String var = "";
    private String scope = TagUtils.SCOPE_PAGE;

    @Override
    public void doTag() throws JspException, IOException {
        String pageName = paoDetailUrlHelper.getPageNameForPaoDetailPage(yukonPao);
        getJspContext().setAttribute(var, pageName, TagUtils.getScope(scope));
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setYukonPao(YukonPao yukonPao) {
        this.yukonPao = yukonPao;
    }

    public void setPaoDetailUrlHelper(PaoDetailUrlHelper paoDetailUrlHelper) {
        this.paoDetailUrlHelper = paoDetailUrlHelper;
    }
}
