package com.cannontech.esub.editor.element;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.util.Util;
import com.loox.jloox.*;
/**
 * Creation date: (1/22/2002 10:15:09 AM)
 * @author: 
 */
public class StaticImage extends LxAbstractImage implements DrawingElement {

	public static final String INVALID_IMAGE_NAME = "X.gif";
	
	// Absolute path the the image, not persisted
	private String absoluteImagePath;
	
	// Relateive path (to drawing) of the image, is persisted
	private String relativeImagePath;
	
	private Drawing drawing;
	private String linkTo;
/**
 * StaticImage constructor comment.
 */
public StaticImage() {
	super();
	initialize();
}
/**
 * Creation date: (1/22/2002 10:17:19 AM)
 * @return java.lang.String
 */
public java.lang.String getAbsoluteImagePath() {
	
	return absoluteImagePath;
}
/**
 * Creation date: (1/22/2002 10:18:53 AM)
 * @return java.lang.String
 */
public java.lang.String getLinkTo() {
	return linkTo;
}
/**
 * Creation date: (1/22/2002 10:20:30 AM)
 */
private void initialize() {
	setImage( Util.findImage(INVALID_IMAGE_NAME));	
}
/**
 * Creation date: (12/17/2001 3:50:28 PM)
 * @param in java.io.InputStream
 * @param version java.lang.String
 */
public synchronized void readFromJLX(InputStream in, String version) throws IOException
{
	super.readFromJLX(in, version);

//	setAbsoluteImagePath(LxSaveUtils.readString(in));
	setRelativeImagePath(LxSaveUtils.readString(in));
	setLinkTo(LxSaveUtils.readString(in));
	
	LxSaveUtils.readEndOfPart(in);
}
/**
 * Creation date: (12/17/2001 3:49:44 PM)
 * @param out java.io.OutputStream
 */
public synchronized void saveAsJLX(OutputStream out) throws IOException 
{
	super.saveAsJLX(out);

	//LxSaveUtils.writeString(out, getAbsoluteImagePath());
	LxSaveUtils.writeString(out, calcRelativeImagePath());
	LxSaveUtils.writeString(out, getLinkTo());
	
	LxSaveUtils.writeEndOfPart(out);
}
/**
 * Creation date: (1/22/2002 10:17:19 AM)
 * @param newImageName java.lang.String
 */
public void setAbsoluteImagePath(java.lang.String newImageName) {
	absoluteImagePath = newImageName;
	setImage( Util.loadImage(absoluteImagePath));
}
/**
 * Creation date: (1/22/2002 10:18:53 AM)
 * @param newLinkTo java.lang.String
 */
public void setLinkTo(java.lang.String newLinkTo) {
	linkTo = newLinkTo;
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
	 * Calculates the relative image path based on
	 * the path of the drawing this element is a part of.
	 * @return String
	 */
	public String calcRelativeImagePath() {
		if( getDrawing() != null ) {
			return Util.getRelativePath(getDrawing(), getAbsoluteImagePath());
		}
		else {
			return null;
		}
	}
	/**
	 * Exposed only for persisting this element.
	 * Normally set the relativePath indirectly through
	 * the absolu
	 * @param relativeImagePath The relativeImagePath to set
	 */
	public void setRelativeImagePath(String relativeImagePath) {
		this.relativeImagePath = relativeImagePath;
	}

	/**
	 * Exposed only for persisting this element,
	 * Usually call calcRelativeImagePath()
	 * @return String
	 */
	public String getRelativeImagePath() {
		return relativeImagePath;
	}

}
