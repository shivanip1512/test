package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.web.menu.ModuleBase;

/**
 * Outputs a <link> for CSS for any CSS file specified with the IncludeCssTag
 * within the StandardPageTag.
 */
public class OutputOtherCss extends TagSupport {

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }
    public int doEndTag() throws JspException {
        try {
            ModuleBase moduleBase = (ModuleBase) pageContext.getAttribute(StandardPageTag.CTI_MODULE_BASE, 
                                                                          PageContext.REQUEST_SCOPE);
            pageContext.getOut().write("\n<!-- Module files from XML config -->\n");
            outputCssFiles(moduleBase.getCssFiles());
            List cssList = (List) pageContext.getAttribute(StandardPageTag.CTI_CSS_FILES,
                                                           PageContext.REQUEST_SCOPE);
            pageContext.getOut().write("\n<!-- Individual files from includeCss tag -->\n");
            outputCssFiles(cssList);

            return EVAL_PAGE;
        } catch (Exception e) {
            throw new JspException("Can't output other CSS files.", e);
        }
    }
    private void outputCssFiles(List cssList) throws IOException {
        for (Iterator iter = cssList.iterator(); iter.hasNext();) {
            String cssFile = (String) iter.next();
            pageContext.getOut()
                       .write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            pageContext.getOut().write(cssFile);
            pageContext.getOut().write("\" >\n");
        }
    }

}
