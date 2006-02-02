package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.ModuleBase;

/**
 * Outputs a <link> for CSS for any CSS file specified with the IncludeCssTag
 * within the StandardPageTag.
 */
public class OutputHeadContentTag extends BodyTagSupport {
    List layoutScriptFiles = null;

    public int doStartTag() throws JspException {
        layoutScriptFiles = new ArrayList();
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
        
        // get script file declared in the layout file
        for (Iterator iter = layoutScriptFiles.iterator(); iter.hasNext();) {
            String file = (String) iter.next();
            finalScriptList.add(file);
        }
        
        // get script files declared in the 
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
    private void checkPath(String file) {
        if (!file.startsWith("/")) {
            CTILogger.warn("Please use absolute paths for CSS and JS files (" + file + ")");
        }
    }
    
    private void outputScriptFiles(Collection cssList) throws IOException {
        for (Iterator iter = cssList.iterator(); iter.hasNext();) {
            String scriptFile = (String) iter.next();
            checkPath(scriptFile);
            scriptFile = ServletUtil.createSafeUrl(pageContext.getRequest(), scriptFile);
            pageContext.getOut()
                       .write("<script type=\"text/javascript\" src=\"");
            pageContext.getOut().write(scriptFile);
            pageContext.getOut().write("\" ></script>\n");
        }
    }
    
    private void handleCssFiles() throws IOException {
        ModuleBase moduleBase = (ModuleBase) pageContext.getAttribute(StandardPageTag.CTI_MODULE_BASE, 
                                                                      PageContext.REQUEST_SCOPE);
        pageContext.getOut().write("\n<!-- Module files from XML config -->\n");
        outputCssFiles(moduleBase.getCssFiles());
        List cssList = (List) pageContext.getAttribute(StandardPageTag.CTI_CSS_FILES,
                                                       PageContext.REQUEST_SCOPE);
        pageContext.getOut().write("\n<!-- Individual files from includeCss tag -->\n");
        outputCssFiles(cssList);
    }
    
    private void outputCssFiles(List cssList) throws IOException {
        if (cssList.isEmpty()) {
            pageContext.getOut().write("<!--  (none)  -->\n");
        }
        for (Iterator iter = cssList.iterator(); iter.hasNext();) {
            String cssFile = (String) iter.next();
            checkPath(cssFile);
            cssFile = ServletUtil.createSafeUrl(pageContext.getRequest(), cssFile);
            pageContext.getOut()
                       .write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            pageContext.getOut().write(cssFile);
            pageContext.getOut().write("\" >\n");
        }
    }
    public void addScriptFile(String link) {
        layoutScriptFiles.add(link);
    }


}
