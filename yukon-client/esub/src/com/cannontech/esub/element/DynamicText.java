package com.cannontech.esub.element;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;

import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.PointAttributes;
import com.cannontech.esub.element.persist.PersistDynamicText;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxContainer;

/**
 * DynamicText is a text element that is bound to a point attribute.
 * Creation date: (12/17/2001 1:44:37 PM)
 * @author: 
 */
public class DynamicText extends LxAbstractText implements DrawingElement, Serializable {
	
	private static final String ELEMENT_ID = "dynamicText";
	public static final int INVALID_POINT = -1;	
	
	private static final int CURRENT_VERSION = 1;
	
	static final Font DEFAULT_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
	static final Color DEFAULT_COLOR = java.awt.Color.white;
	
	private com.cannontech.database.data.lite.LitePoint point;	
	private int displayAttribs = 0x00;
	
	private transient Drawing drawing = null;
	private String linkTo = null;
	private Properties props = new Properties();
	private int version = CURRENT_VERSION;
	
/**
 * DynamicText constructor comment.
 */
public DynamicText() {
	super();
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 com.loox.jloox.LxContainer
 */
public DynamicText(LxContainer arg1) {
	super(arg1);
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 com.loox.jloox.LxContainer
 * @param arg2 java.awt.geom.Rectangle2D
 */
public DynamicText(LxContainer arg1, java.awt.geom.Rectangle2D arg2) {
	super(arg1, arg2);
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 com.loox.jloox.LxContainer
 * @param arg2 java.awt.geom.Rectangle2D
 * @param arg3 java.lang.String
 */
public DynamicText(LxContainer arg1, java.awt.geom.Rectangle2D arg2, String arg3) {
	super(arg1, arg2, arg3);
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 com.loox.jloox.LxContainer
 * @param arg2 java.lang.String
 */
public DynamicText(LxContainer arg1, String arg2) {
	super(arg1, arg2);
	initialize();
}
/**
 * DynamicText constructor comment.
 * @param arg1 java.lang.String
 */
public DynamicText(String arg1) {
	super(arg1);
	initialize();
}

/**
 * Creation date: (12/18/2001 4:51:49 PM)
 * @return com.cannontech.database.data.lite.LitePoint
 */
public com.cannontech.database.data.lite.LitePoint getPoint() {	
	return point;
}
/**
 * Creation date: (12/18/2001 12:47:22 PM)
 * @return int
 */
public int getPointID() {
	return point.getPointID();
}
/**
 * Creation date: (12/18/2001 4:58:59 PM)
 */
private void initialize() {
	
	setFont(DEFAULT_FONT);
	setPaint(DEFAULT_COLOR);
	point = new com.cannontech.database.data.lite.LitePoint(INVALID_POINT);
}

/**
 * Creation date: (1/14/2002 2:33:29 PM)
 * @param f java.awt.Font
 */
public void setFont(Font f) {
	super.setFont(f);
}

/**
 * Creation date: (12/18/2001 4:51:49 PM)
 * @param newPoint com.cannontech.database.data.lite.LitePoint
 */
public void setPoint(com.cannontech.database.data.lite.LitePoint newPoint) {
	point = newPoint;
}
/**
 * Creation date: (12/18/2001 12:47:22 PM)
 * @param newPointID int
 */
public void setPointID(int newPointID) 
{
	point = PointFuncs.getLitePoint( newPointID );
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
	 * @see java.lang.Object#clone()
	 */
	public Object clone()  {
		Object v = super.clone();		 
		return v;
	}

	
	/**
	 * Returns the displayAttribs.
	 * @return int
	 */
	public int getDisplayAttribs() {
		return displayAttribs;
	}

	/**
	 * Sets the displayAttribs.
	 * @param displayAttribs The displayAttribs to set
	 */
	public void setDisplayAttribs(int displayAttribs) {
		this.displayAttribs = displayAttribs;
	}

	/**
	 * Returns the editable.
	 * @return boolean
	 */
	public boolean isEditable() {
		return 
			(	displayAttribs == PointAttributes.LOW_LIMIT  ||
			 	displayAttribs == PointAttributes.HIGH_LIMIT ||
			 	displayAttribs == PointAttributes.LIMIT_DURATION );		
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
public void readFromJLX(InputStream in, String version) throws IOException
{  
        super.readFromJLX(in, version);
		PersistDynamicText.getInstance().readFromJLX(this,in);
}
/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public void saveAsJLX(OutputStream out) throws IOException 
{
        super.saveAsJLX(out);
 		PersistDynamicText.getInstance().saveAsJLX(this,out);
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
