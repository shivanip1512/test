package com.cannontech.web.taglib.nav;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Writes out the given URLs in a bread crumb format.
 *
 * @author: ryan
 */
public class BreadCrumb extends TagSupport
{
	private String seperator = " > ";
	private boolean isFirstLink = true;

	/**
	 * BreadCrumb constructor comment.
	 */
	public BreadCrumb()
	{
		super();
	}

	/**
	 * Creation date: (11/13/2001 4:35:26 PM)
	 * @return int
	 * @exception javax.servlet.jsp.JspException The exception description.
	 */
	public int doStartTag() throws JspException
	{
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Creation date: (11/13/2001 4:36:50 PM)
	 * @return int
	 * @exception javax.servlet.jsp.JspException The exception description.
	 */
	public int doEndTag() throws javax.servlet.jsp.JspException
	{
		//reset our flag for the first tag
		isFirstLink = true;

		return EVAL_PAGE;
	}

	/**
	 * @return
	 */
	public String getSeperator()
	{
		return seperator;
	}

	/**
	 * @param string
	 */
	public void setSeperator(String string)
	{
		seperator = string;
	}

	/**
	 * @return
	 */
	public boolean isFirstLink()
	{
		return isFirstLink;
	}

	/**
	 * @param b
	 */
	protected void updateFirstLink()
	{
		isFirstLink = false;
	}

}
