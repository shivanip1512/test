package com.cannontech.esub.editor.element;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.editor.Drawing;
import com.loox.jloox.*;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.awt.Font;
import java.awt.Color;

/**
 * DynamicText is a text element that is bound to a point attribute.
 * Creation date: (12/17/2001 1:44:37 PM)
 * @author: 
 */
public class DynamicText extends LxAbstractText implements DrawingElement {
	static final Font DEFAULT_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
	static final Color DEFAULT_COLOR = java.awt.Color.white;
	static final int INVALID_POINT = -1;	

	private com.cannontech.database.data.lite.LitePoint point;	
	
	private Drawing drawing = null;
	private String linkTo = "";
	
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
 * Creation date: (1/14/2002 1:47:07 PM)
 * @return java.lang.String
 */
public java.lang.String getLinkTo() {
	return linkTo;
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
	setText("N/A");
	setFont(DEFAULT_FONT);
	CTILogger.info(DEFAULT_FONT.getFontName());
	setPaint(DEFAULT_COLOR);

	point = new com.cannontech.database.data.lite.LitePoint(INVALID_POINT);
}
/**
 * Creation date: (12/17/2001 3:50:28 PM)
 * @param in java.io.InputStream
 * @param version java.lang.String
 */
public void readFromJLX(InputStream in, String version) throws IOException
{  
	super.readFromJLX(in, version);

	setPointID(LxSaveUtils.readInt(in));

	// read in font
/*	String fontName = LxSaveUtils.readString(in);
	int fontSize = LxSaveUtils.readInt(in);
	
	Font f = new Font(fontName, Font.PLAIN, fontSize );
	setFont(f);

	//read color
	int r = LxSaveUtils.readInt(in);
	int g = LxSaveUtils.readInt(in);
	int b = LxSaveUtils.readInt(in);

	Color c = new Color(r, g, b);
	setPaint(c);*/
	
	//read link
	setLinkTo( LxSaveUtils.readString(in));
	
	LxSaveUtils.readEndOfPart(in);
}
/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public void saveAsJLX(OutputStream out) throws IOException 
{
	super.saveAsJLX(out);
	
	LxSaveUtils.writeInt(out, getPointID());

	//write out font into
/*	LxSaveUtils.writeString(out, getFont().getName());
	LxSaveUtils.writeInt(out, getFont().getSize());
	
	//save color
	Color textColor = (Color) getPaint();
	LxSaveUtils.writeInt(out, textColor.getRed());
	LxSaveUtils.writeInt(out, textColor.getGreen());
	LxSaveUtils.writeInt(out, textColor.getBlue());
*/	
	//save link
	LxSaveUtils.writeString(out, getLinkTo() );
	
	LxSaveUtils.writeEndOfPart(out);
}
/**
 * Creation date: (1/14/2002 2:33:29 PM)
 * @param f java.awt.Font
 */
public void setFont(Font f) {
	super.setFont(f);
}
/**
 * Creation date: (1/14/2002 1:47:07 PM)
 * @param newLinkTo java.lang.String
 */
public void setLinkTo(java.lang.String newLinkTo) {
	linkTo = newLinkTo;
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
public void setPointID(int newPointID) {
	List pList = DefaultDatabaseCache.getInstance().getAllPoints();
	Iterator iter = pList.iterator();
	while( iter.hasNext() ) {
		LitePoint lp = (LitePoint) iter.next();
		if( lp.getPointID() == newPointID ) {
			point = lp;
		}			
	}
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

}
