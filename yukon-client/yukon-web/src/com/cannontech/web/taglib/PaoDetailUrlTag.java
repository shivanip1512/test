package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.web.common.pao.PaoDetailUrlHelper;

@Configurable("paoDetailUrlTagPrototype")
public class PaoDetailUrlTag extends YukonTagSupport {
    private PaoDetailUrlHelper paoDetailUrlHelper;

    private YukonPao yukonPao = null;

    @Override
    public void doTag() throws JspException, IOException {
        String urlForPaoDetailPage = paoDetailUrlHelper.getUrlForPaoDetailPage(yukonPao);

        JspWriter out = getJspContext().getOut();
        if (urlForPaoDetailPage == null) {
            out.print("<span title=\"" + yukonPao.getPaoIdentifier() + "\">");
            getJspBody().invoke(out);
            out.print("</span>");
        } else {
            out.print("<a title=\"" + yukonPao.getPaoIdentifier() + "\" class=\"simpleLink\" href=\"");
            out.print(urlForPaoDetailPage);
            out.print("\">");
            getJspBody().invoke(out);
            out.print("</a>");
        }
    }

    public void setYukonPao(YukonPao yukonPao) {
        this.yukonPao = yukonPao;
    }

    public void setPaoDetailUrlHelper(PaoDetailUrlHelper paoDetailUrlHelper) {
        this.paoDetailUrlHelper = paoDetailUrlHelper;
    }
}
