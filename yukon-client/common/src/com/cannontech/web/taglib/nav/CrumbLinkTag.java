package com.cannontech.web.taglib.nav;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import com.cannontech.util.ServletUtil;

/**
 * Creates a "crumb" in a crumb trail. Can be either a link or static text depending on whether a url is specified in tag.
 * The crumb's text is defined by the title attribute, and/or any content in the tag body.
 * @author m_peterson
 *
 */
public class CrumbLinkTag extends SimpleTagSupport
{
	private String url = null;
	private String title = null;
	
	public void doTag() throws JspException, IOException
	{
		// access to page writer and request
		JspWriter out = getJspContext().getOut();
		PageContext context = (PageContext)getJspContext();
	    HttpServletRequest httpRequest = (HttpServletRequest)context.getRequest();
		
		// the containing BreadCrumbsTag
		BreadCrumbsTag bc = (BreadCrumbsTag)
			findAncestorWithClass( this, BreadCrumbsTag.class );
		
		if( bc == null ) {
            throw new JspTagException("Unnested tag requires parent tag of type: "  + BreadCrumbsTag.class.toString() );
        }
		
		// this crumb is static text
		if (getUrl() == null) {
			
			out.write((bc.isFirstLink() ? "" : bc.getSeperator()));
			
			// the link display name will be the title attribute and/or any body content
			writeTitleIfAvailable();
			writeBodyContentIfAvailable();
			
			bc.updateFirstLink();
		}
		
		// this crumb is a link
		else {
			
			// build the link
			String url = getUrl();
			url= ServletUtil.createSafeUrl(httpRequest, url);
			url = StringEscapeUtils.escapeHtml(url);
			
			String link = (bc.isFirstLink() ? "" : bc.getSeperator()) 
						+ "<a href=\"" 
						+ url 
						+ "\">";
			
			out.write(link);
			
			// the link display name will be the title attribute and/or any body content
			writeTitleIfAvailable();
			writeBodyContentIfAvailable();
			
			out.write("</a>");
			
			// make sure the containing bread crumb set knows at least one crumb has been written now
			bc.updateFirstLink();
			
		}
	}

	private void writeTitleIfAvailable() throws JspException, IOException {
		
		String title = getTitle() == null ? "" : getTitle();
		getJspContext().getOut().write(title);
	}

	private void writeBodyContentIfAvailable() throws JspException, IOException {
		
		JspFragment tagBody = getJspBody();
		if (tagBody != null) {
			tagBody.invoke(getJspContext().getOut());
		}
	}
	
	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public void setTitle(String string) {
		title = string;
	}

	public void setUrl(String string) {
		url = string;
	}

}
