package com.cannontech.esub.editor.element;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import com.loox.jloox.*;

import com.cannontech.esub.util.ImageCache;
import com.cannontech.database.data.lite.LitePoint;

/**
 * 
 * Creation date: (1/8/2002 1:47:20 PM)
 * @author: Aaron Lauinger
 */
public class StateImage extends LxAbstractImage implements LinkedElement {
	public static final String INVALID_STATE_IMAGE_NAME = "X.gif";
	private static final int INVALID_POINT = -1;

	
	private LitePoint point;
	private String[] states;
	private String[] images;
	private String state;
	private String linkTo;
/**
 * StateImage constructor comment.
 */
public StateImage() {
	super();
	initialize();
}
/**
 * Creation date: (1/21/2002 12:21:07 PM)
 * @return java.lang.String
 * @param state java.lang.String
 */
public String getImage(String state) {

	if( states == null )
		return null;
		
	for( int i = 0; i < states.length; i++ ) {
		if( states[i].equalsIgnoreCase(state) ) {
			return images[i];			
		}
	}

	return null;
}
/**
 * Creation date: (1/14/2002 1:47:27 PM)
 * @return java.lang.String
 */
public java.lang.String getLinkTo() {
	return linkTo;
}
/**
 * Creation date: (1/8/2002 1:56:26 PM)
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
 * Creation date: (1/8/2002 1:56:52 PM)
 * @return java.lang.String
 */
public java.lang.String getState() {
	return state;
}
/**
 * Creation date: (1/21/2002 12:17:04 PM)
 * @return java.lang.String[]
 */
public String[] getStates() {
	return states;
}
/**
 * Creation date: (1/8/2002 2:07:06 PM)
 */
private void initialize() {
	setImage(ImageCache.getInstance().getImage(INVALID_STATE_IMAGE_NAME));
	point = new com.cannontech.database.data.lite.LitePoint(INVALID_POINT);
}
/**
 * Creation date: (12/17/2001 3:50:28 PM)
 * @param in java.io.InputStream
 * @param version java.lang.String
 */
public synchronized void readFromJLX(InputStream in, String version) throws IOException
{System.out.println("StateImage::readFromJLX");
	super.readFromJLX(in, version);

	//restore point id
	setPointID(LxSaveUtils.readInt(in));
		
	int numStates = LxSaveUtils.readInt(in);

	states = new String[numStates];
	images = new String[numStates];
	for( int i = 0; i < numStates; i++ ) {
		states[i] = LxSaveUtils.readString(in);
		images[i] = LxSaveUtils.readString(in);
		System.out.println(states[i] + "," + images[i]);
	}	

	//restore state
	setState(LxSaveUtils.readString(in));
		
	//restore link
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

	// save point id
	LxSaveUtils.writeInt(out, getPointID());
	
	//write number of states
	LxSaveUtils.writeInt(out, states.length);
	
	for( int i = 0; i < states.length; i++ ) {
		LxSaveUtils.writeString(out, states[i]);
		LxSaveUtils.writeString(out, images[i]);
	}

	//save current state	
	LxSaveUtils.writeString(out, getState());

	//save link
	LxSaveUtils.writeString(out, getLinkTo());
	
	LxSaveUtils.writeEndOfPart(out);
}
/**
 * Creation date: (1/21/2002 1:04:51 PM)
 * @param state java.lang.String
 */
public void setImage(String state, String image) {
	for( int i = 0; i < states.length; i++ ) {
		if( states[i].equalsIgnoreCase(state) ) {
			images[i] = image;
			break;
		}
	}
			
}
/**
 * Creation date: (1/14/2002 1:47:27 PM)
 * @param newLinkTo java.lang.String
 */
public void setLinkTo(java.lang.String newLinkTo) {
	linkTo = newLinkTo;
}
/**
 * Creation date: (1/8/2002 1:56:26 PM)
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
	point.setPointID(newPointID);
}
/**
 * Creation date: (1/8/2002 1:56:52 PM)
 * @param newState java.lang.String
 */
public void setState(java.lang.String newState) {
	state = newState;

	// find the correct image for the new state
	for( int i = 0; i < states.length; i++ ) {
		if( states[i].equalsIgnoreCase(newState) ) {	
			System.out.println("Looking for image: " + images[i]);	
			setImage(ImageCache.getInstance().getImage(images[i]));			
			break;
		}
	}
		
}
/**
 * Creation date: (1/21/2002 1:04:43 PM)
 * @param states java.lang.String[]
 */
public void setStates(String[] states) {
	this.states = states;
	this.images = new String[states.length];
	this.state = states[0];
}
}
