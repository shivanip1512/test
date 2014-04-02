package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.taglib.UniqueIdentifierTag;
import com.cannontech.web.taglib.YukonTagSupport;

/**
 * Tag used to output the right side logo for a page if one exists
 */
public class LogoTag extends YukonTagSupport {

	private String id = null;
    private String key;

    public void setId(String id) {
		this.id = id;
	}
    
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void doTag() throws JspException, IOException {

		MessageSourceAccessor messageSourceAccessor = getMessageSource();
		
		if (id == null) {
			id = UniqueIdentifierTag.generateIdentifier(getJspContext(), "logoTag");
		}
    	
        try {
            String url = messageSourceAccessor.getMessage(key);
            if(!StringUtils.isBlank(url)){
	            String safeUrl = ServletUtil.createSafeUrl(getRequest(), url);
	            JspWriter out = getJspContext().getOut();
	
	            // Create image html
	            StringBuilder sb = new StringBuilder();
	            sb.append("<img id=\"" + id + "\" class=\"logoImage\" src=\"");
	            sb.append(safeUrl);
	            sb.append("\"");
	
	            // Add alt text if it exists
	            try {
	                String altText = messageSourceAccessor.getMessage(key + ".alt");
	
	                sb.append(" alt=\"");
	                sb.append(altText);
	                sb.append("\"");

	                sb.append(" title=\"");
	                sb.append(altText);
	                sb.append("\"");
	            } catch (NoSuchMessageException e) {
	                // no alt text
	            }

	            // Add hover image if it exists
	            try {
	                String hoverUrl = messageSourceAccessor.getMessage(key + ".hover");
	                String safeHoverUrl = ServletUtil.createSafeUrl(getRequest(), hoverUrl);
	                
	                sb.append(" onmouseover=\"javascript:this.src='" + safeHoverUrl + "'\"");
	                sb.append(" onmouseout=\"javascript:this.src='" + safeUrl + "'\"");
	            } catch (NoSuchMessageException e) {
	                // no hover image
	            }
	
	            sb.append(">");
	
	            out.write(sb.toString());
            }
        } catch (NoSuchMessageException e) {
            // message not found - do nothing
        }

    }
}
