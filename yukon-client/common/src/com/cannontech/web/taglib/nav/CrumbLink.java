package com.cannontech.web.taglib.nav;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Creates a link and uses the given title as its text.
 *
 * @author: ryan
 */
public class CrumbLink extends BodyTagSupport
{
	private String url = null;
	private String title = null;
	private String cssClass = "";

	/**
	 * CrumbLink constructor comment.
	 */
	public CrumbLink()
	{
		super();
	}
	/**
	 * Creation date: (11/13/2001 4:36:50 PM)
	 * @return int
	 * @exception javax.servlet.jsp.JspException The exception description.
	 */
	public int doEndTag() throws javax.servlet.jsp.JspException
	{
		return EVAL_PAGE;
	}
	/**
	 * Creation date: (11/13/2001 4:35:26 PM)
	 * @return int
	 * @exception javax.servlet.jsp.JspException The exception description.
	 */
	public int doStartTag() throws JspException
	{
		BreadCrumb bc = (BreadCrumb)
			findAncestorWithClass( this, BreadCrumb.class );
		
		if( bc == null )
			throw new JspTagException("Unnested tag requires parent tag of type: " + BreadCrumb.class.toString() );
		
		try
		{
			if( getTitle() == null )
				setTitle( getUrl() );
			
			pageContext.getOut().write(
				(bc.isFirstLink() ? "" : bc.getSeperator()) +
				"<a href=\"" + getUrl() + "\" class=\"" + getCssClass() + "\">" +
				getTitle() + "</a>");

			bc.updateFirstLink();
		}
		catch (java.io.IOException e)
		{
			throw new JspException(e.getMessage());
		}

		return EVAL_BODY_BUFFERED;
	}

	/**
	 * @return
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @return
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param string
	 */
	public void setTitle(String string)
	{
		title = string;
	}

	/**
	 * @param string
	 */
	public void setUrl(String string)
	{
		url = string;
	}

	/**
	 * @return
	 */
	public String getCssClass()
	{
		return cssClass;
	}

	/**
	 * @param string
	 */
	public void setCssClass(String string)
	{
		cssClass = string;
	}

}
