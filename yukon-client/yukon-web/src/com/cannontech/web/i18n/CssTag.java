package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.taglib.YukonTagSupport;

/**
 * Tag used to include a css file by I18N key
 */
public class CssTag extends YukonTagSupport {

    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void doTag() throws JspException, IOException {

    	MessageSourceAccessor messageSourceAccessor = getMessageSource();
    	
    	try {
            String url = messageSourceAccessor.getMessage(key);
            if(!StringUtils.isBlank(url)){
	            String safeUrl = ServletUtil.createSafeUrl(getRequest(), url);
	            JspWriter out = getJspContext().getOut();
	
	            // Create include css html
	            StringBuilder sb = new StringBuilder();
	            sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
	            sb.append(safeUrl);
	            sb.append("\">");
	
	            out.write(sb.toString());
            }
        } catch (NoSuchMessageException e) {
            // message not found - do nothing
        }

    }
}
