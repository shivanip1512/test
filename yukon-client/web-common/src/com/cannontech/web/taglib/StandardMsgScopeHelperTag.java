package com.cannontech.web.taglib;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;
import com.google.common.collect.Lists;

public class StandardMsgScopeHelperTag extends BodyTagSupport {

    private String module;
    private String pageName;
    private String fragmentName;
    
    @Override
    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        MessageScope messageScope = MessageScopeHelper.forRequest(request);
        
        String baseSearchPath = "modules." + module;
            
        String[] pageNameParts = StringUtils.split(pageName, ".");
        List<String> finalPaths = Lists.newArrayList();
        List<String> popupPaths = Lists.newArrayList();

        for (int i = 0; i < pageNameParts.length; i++) {
            baseSearchPath = baseSearchPath + "." + pageNameParts[i];
            finalPaths.add(baseSearchPath); // Add scope for the pageName or pageName part.
            popupPaths.add(baseSearchPath + "." + fragmentName); // Add scope for the pageName/part.popupName
        }
        
        messageScope.pushScope(finalPaths.toArray(new String[finalPaths.size()]));
        messageScope.pushScope(popupPaths.toArray(new String[popupPaths.size()]));
        
        return EVAL_BODY_INCLUDE;
    }
    
    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }
    
}