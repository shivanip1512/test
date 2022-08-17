package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.util.TagUtils;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.yukon.IDatabaseCache;

@Configurable(value="paoDetailUrlTagPrototype", autowire=Autowire.BY_NAME)
public class PaoDetailUrlTag extends YukonTagSupport {
    
    private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private IDatabaseCache cache;

    private int paoId;
    private YukonPao yukonPao;
    private String var;
    private boolean newTab;
    private String scope = TagUtils.SCOPE_PAGE;

    @Override
    public void doTag() throws JspException, IOException {
        
        if (yukonPao == null) {
            yukonPao = cache.getAllPaosMap().get(paoId);
        }
        String urlForPaoDetailPage = paoDetailUrlHelper.getUrlForPaoDetailPage(yukonPao);
        
        JspWriter out = getJspContext().getOut();
        if (var == null) {
            if (urlForPaoDetailPage == null) {
                out.print("<span title=\"" + yukonPao.getPaoIdentifier() + "\">");
                getJspBody().invoke(out);
                out.print("</span>");
            } else {
                out.print("<a title=\"" + yukonPao.getPaoIdentifier() + "\" href=\"");
                out.print(getRequest().getContextPath() + urlForPaoDetailPage);
                out.print("\"");
                if (newTab) {
                    out.print(" target=\"_blank\" ");
                }
                out.print(">");
                getJspBody().invoke(out);
                out.print("</a>");
            }
        } else {
            getJspContext().setAttribute(var, getRequest().getContextPath() + urlForPaoDetailPage,
                TagUtils.getScope(scope));
        }
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setNewTab(boolean newTab) {
        this.newTab = newTab;
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

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }
    
}