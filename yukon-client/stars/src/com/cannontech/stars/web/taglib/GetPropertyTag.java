package com.cannontech.stars.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.stars.util.ServletUtils;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GetPropertyTag extends BodyTagSupport {
	
	private String file = null;
	private String name = null;
	private String format = null;

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		String text = (format == null) ?
				ServletUtils.getECProperty( file, name ) :
				ServletUtils.getECProperty( file, name, format );
		try {
			pageContext.getOut().write( (text == null) ? "" : text );
		}
		catch(java.io.IOException e )
		{
			throw new JspException(e.getMessage());
		}
		
		return SKIP_BODY;
	}

	/**
	 * Returns the file.
	 * @return String
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the file.
	 * @param file The file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the format.
	 * @return String
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the format.
	 * @param format The format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

}
