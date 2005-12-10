package com.cannontech.web.taglib.nav;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.util.ServletUtil;

/**
 * Creates a link and uses the given title as its text.
 *
 * @author: ryan
 */
public class CrumbLinkTag extends TagSupport
{
	private String url = null;
	private String title = null;
	private String cssClass = "";

	/**
	 * @return int
	 * @exception JspException The exception description.
	 */
	public int doEndTag() throws javax.servlet.jsp.JspException
	{
		return EVAL_PAGE;
	}
	/**
	 * @return int
	 * @exception JspException The exception description.
	 */
	public int doStartTag() throws JspException
	{
		BreadCrumbsTag bc = (BreadCrumbsTag)
			findAncestorWithClass( this, BreadCrumbsTag.class );
		
		if( bc == null ) {
            throw new JspTagException("Unnested tag requires parent tag of type: " 
                                      + BreadCrumbsTag.class.toString() );
        }
		
		try
		{
            String url = ServletUtil.createSafeUrl(pageContext.getRequest(), getUrl());
            
			if( getTitle() == null ) {
                setTitle( url );
            }
			
			pageContext.getOut().write(
				(bc.isFirstLink() ? "" : bc.getSeperator()) +
				"<a href=\"" + url + "\">" +
				getTitle() + "</a>");

			bc.updateFirstLink();
		}
		catch (java.io.IOException e)
		{
			throw new JspException("Could not build breadCrumb",e);
		}

		return SKIP_BODY;
	}

	public String getTitle()
	{
		return title;
	}

	public String getUrl()
	{
		return url;
	}

	public void setTitle(String string)
	{
		title = string;
	}

	public void setUrl(String string)
	{
		url = string;
	}

	public String getCssClass()
	{
		return cssClass;
	}

	public void setCssClass(String string)
	{
		cssClass = string;
	}

}
