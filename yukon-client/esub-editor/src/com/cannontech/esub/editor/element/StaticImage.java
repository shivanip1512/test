package com.cannontech.esub.editor.element;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import com.loox.jloox.*;
/**
 * Creation date: (1/22/2002 10:15:09 AM)
 * @author: 
 */
public class StaticImage extends LxAbstractImage implements LinkedElement {

	public static final String INVALID_IMAGE_NAME = "X.gif";
	
	private String imageName;
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
public java.lang.String getImageName() {
	return imageName;
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
	setImageName(INVALID_IMAGE_NAME);	
}
/**
 * Creation date: (12/17/2001 3:50:28 PM)
 * @param in java.io.InputStream
 * @param version java.lang.String
 */
public synchronized void readFromJLX(InputStream in, String version) throws IOException
{
	super.readFromJLX(in, version);

	setImageName(LxSaveUtils.readString(in));
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

	LxSaveUtils.writeString(out, getImageName());
	LxSaveUtils.writeString(out, getLinkTo());
	
	LxSaveUtils.writeEndOfPart(out);
}
/**
 * Creation date: (1/22/2002 10:17:19 AM)
 * @param newImageName java.lang.String
 */
public void setImageName(java.lang.String newImageName) {
	imageName = newImageName;
	setImage( com.cannontech.esub.util.ImageCache.getInstance().getImage(imageName));
}
/**
 * Creation date: (1/22/2002 10:18:53 AM)
 * @param newLinkTo java.lang.String
 */
public void setLinkTo(java.lang.String newLinkTo) {
	linkTo = newLinkTo;
}
}
