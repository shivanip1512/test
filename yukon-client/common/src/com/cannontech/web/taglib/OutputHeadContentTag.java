package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.ModuleBase;

/**
 * Outputs a <link> for CSS for any CSS file specified with the IncludeCssTag
 * within the StandardPageTag.
 */
public class OutputHeadContentTag extends BodyTagSupport {
    List layoutScriptFiles = null;
    List layoutCssFiles = null;

    public int doStartTag() throws JspException {
        layoutScriptFiles = new ArrayList();
        layoutCssFiles = new ArrayList();
        return EVAL_BODY_BUFFERED;
    }
    
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
    
    public int doEndTag() throws JspException {
        try {
            
            handleCssFiles();
            handleJavaScriptFiles();

            return EVAL_PAGE;
        } catch (Exception e) {
            throw new JspException("Can't output other CSS files.", e);
        }
    }
    
    private void handleJavaScriptFiles() throws IOException {
        Set finalScriptList = new LinkedHashSet();
        
        // get script files declared in the layout file
        for (Iterator iter = layoutScriptFiles.iterator(); iter.hasNext();) {
            String file = (String) iter.next();
            finalScriptList.add(file);
        }
        
        // get script files declared in the module
        ModuleBase moduleBase = (ModuleBase) pageContext.getAttribute(StandardPageTag.CTI_MODULE_BASE, 
                                                                      PageContext.REQUEST_SCOPE);
        for (Iterator iter = moduleBase.getScriptFiles().iterator(); iter.hasNext();) {
            String file = (String) iter.next();
            finalScriptList.add(file);
        }
        List scriptList = (List) pageContext.getAttribute(StandardPageTag.CTI_SCRIPT_FILES,
                                                       PageContext.REQUEST_SCOPE);
        for (Iterator iter = scriptList.iterator(); iter.hasNext();) {
            String file = (String) iter.next();
            finalScriptList.add(file);
        }
        pageContext.getOut().write("\n<!-- Consolidated Script Files -->\n");
        outputScriptFiles(finalScriptList);
    }
    
    private void outputScriptFiles(Collection scriptList) throws IOException {
        if (scriptList.isEmpty()) {
            pageContext.getOut().write("<!--  (none)  -->\n");
        }
        for (Iterator iter = scriptList.iterator(); iter.hasNext();) {
            String scriptFile = (String) iter.next();
            scriptFile = ServletUtil.createSafeUrl(pageContext.getRequest(), scriptFile);
            pageContext.getOut()
                       .write("<script type=\"text/javascript\" src=\"");
            pageContext.getOut().write(scriptFile.trim());
            pageContext.getOut().write("\" ></script>\n");
        }
    }
    
    private void handleCssFiles() throws IOException {
        pageContext.getOut().write("\n<!-- Layout CSS files -->\n");
        outputCssFiles(layoutCssFiles);
        
        pageContext.getOut().write("\n<!-- Module CSS files from module_config.xml -->\n");
        ModuleBase moduleBase = (ModuleBase) pageContext.getAttribute(StandardPageTag.CTI_MODULE_BASE, 
                                                                      PageContext.REQUEST_SCOPE);
        outputCssFiles(moduleBase.getCssFiles());
        
        pageContext.getOut().write("\n<!-- Individual files from includeCss tag on the request page -->\n");
        List cssList = (List) pageContext.getAttribute(StandardPageTag.CTI_CSS_FILES,
                                                       PageContext.REQUEST_SCOPE);
        outputCssFiles(cssList);
        
        pageContext.getOut().write("\n<!-- Energy Company specific style sheets (EnergyCompanyRole)-->\n");
        LiteYukonUser user = 
            (LiteYukonUser) pageContext.getSession().getAttribute(ServletUtil.ATT_YUKON_USER);
        String cssLocations = EnergyCompanyFuncs.getEnergyCompanyProperty(user, EnergyCompanyRole.STD_PAGE_STYLE_SHEET);
        if (cssLocations != null) {
            String[] cssLocationArray = cssLocations.split("\\s*,\\s*");
            outputCssFiles(Arrays.asList(cssLocationArray));
        }
    }
    
    private void outputCssFiles(List cssList) throws IOException {
        if (cssList.isEmpty()) {
            pageContext.getOut().write("<!--  (none)  -->\n");
        }
        for (Iterator iter = cssList.iterator(); iter.hasNext();) {
            String cssFile = (String) iter.next();
            cssFile = ServletUtil.createSafeUrl(pageContext.getRequest(), cssFile);
            pageContext.getOut()
                       .write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            pageContext.getOut().write(cssFile.trim());
            pageContext.getOut().write("\" >\n");
        }
    }
    
    public void addScriptFile(String link) {
        layoutScriptFiles.add(link);
    }

    public void addCSSFile(String link) {
        layoutCssFiles.add(link);
    }


}
