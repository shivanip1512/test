package com.cannontech.esub.element;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.persist.PersistStaticText;
import com.loox.jloox.LxAbstractText;

/**
 * Creation date: (1/23/2002 11:36:05 AM)
 * @author: alauinger
 */
public class StaticText extends LxAbstractText implements DrawingElement {
	private static final String ELEMENT_ID = "staticText";
	private static final int CURRENT_VERSION = 1;
	
	static final Font DEFAULT_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
	static final Color DEFAULT_COLOR = java.awt.Color.white;
	
	private transient Drawing drawing;
	private String linkTo;
	private Properties props = new Properties();
	private int version = CURRENT_VERSION;
	
/**
 * StaticText constructor comment.
 */
public StaticText() {
	super();
	initialize();
}

/**
 * Creation date: (1/23/2002 12:08:38 PM)
 */
private void initialize() {
	setText("");
	setFont(DEFAULT_FONT);	
	setPaint(DEFAULT_COLOR);
}

/**
 * Creation date: (1/23/2002 11:39:22 AM)
 * @param name java.lang.String
 * @param size int
 */
public void setFont(String name, int size) {
	setFont( new java.awt.Font(name, java.awt.Font.PLAIN, size));
}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#getDrawing()
	 */
	public Drawing getDrawing() {
		return drawing;
	}
	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#setDrawing(Drawing)
	 */
	public void setDrawing(Drawing d) {
		this.drawing = d;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#getElementProperties()
	 */
	public Properties getElementProperties() {
		return props;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#setElementProperties(Properties)
	 */
	public void setElementProperties(Properties props) {
		this.props = props;
	}

/**
 * Creation date: (12/17/2001 3:50:28 PM)
 * @param in java.io.InputStream
 * @param version java.lang.String
 */
public synchronized void readFromJLX(InputStream in, String version) throws IOException
{
        super.readFromJLX(in, version);
		PersistStaticText.getInstance().readFromJLX(this,in);
}

/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public synchronized void saveAsJLX(OutputStream out) throws IOException 
{
        super.saveAsJLX(out);
		PersistStaticText.getInstance().saveAsJLX(this,out);
}

	/**
	 * Returns the linkTo.
	 * @return String
	 */
	public String getLinkTo() {
		return linkTo;
	}

	/**
	 * Sets the linkTo.
	 * @param linkTo The linkTo to set
	 */
	public void setLinkTo(String linkTo) {
		this.linkTo = linkTo;
	}
	
	public boolean isCopyable() {
		return true;
	}

	/**
	 * @see com.cannontech.esub.element.DrawingElement#getVersion()
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @see com.cannontech.esub.element.DrawingElement#setVersion(int)
	 */
	public void setVersion(int newVer) {
		this.version = newVer;
	}

	public String getElementID() {
		return ELEMENT_ID;
	}
}
