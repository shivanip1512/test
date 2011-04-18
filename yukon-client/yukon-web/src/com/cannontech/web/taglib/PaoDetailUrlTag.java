package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.util.TagUtils;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.web.common.pao.PaoDetailUrlHelper;

@Configurable("paoDetailUrlTagPrototype")
public class PaoDetailUrlTag extends YukonTagSupport {
    private PaoDetailUrlHelper paoDetailUrlHelper;

    private YukonPao yukonPao = null;
    private String var = null;
    private String scope = TagUtils.SCOPE_PAGE;

    @Override
    public void doTag() throws JspException, IOException {
        String urlForPaoDetailPage = paoDetailUrlHelper.getUrlForPaoDetailPage(yukonPao);

        JspWriter out = getJspContext().getOut();
        if (var == null) {
            if (urlForPaoDetailPage == null) {
                out.print("<span title=\"" + yukonPao.getPaoIdentifier() + "\">");
                getJspBody().invoke(out);
                out.print("</span>");
            } else {
                out.print("<a title=\"" + yukonPao.getPaoIdentifier() + "\" href=\"");
                out.print(urlForPaoDetailPage);
                out.print("\">");
                getJspBody().invoke(out);
                out.print("</a>");
            }
        } else {
            getJspContext().setAttribute(var, urlForPaoDetailPage, TagUtils.getScope(scope));
        }
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
