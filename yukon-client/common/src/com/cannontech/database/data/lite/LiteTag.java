/*
 * Created on Dec 19, 2003
 */
package com.cannontech.database.data.lite;

/**
 * @author aaron
 */
public class LiteTag extends LiteBase {
	
	private String _tagName;
	private int _tagLevel;
	private boolean _inhibit;
	private int _colorID;
	private int _imageID;

	{  
		setLiteType(LiteTypes.TAG);
	}
	
	public LiteTag(int tagID) {
		setTagID(tagID);
	}
	
	public LiteTag(int tagID, String tagName, int tagLevel, boolean inhibit, int colorID, int imageID) {
		setTagID(tagID);
		setTagName(tagName);
		setTagLevel(tagLevel);
		setInhibit(inhibit);
		setColorID(colorID);
		setImageID(imageID);
	}
	
	public String toString() {
		return "TagID: " + getTagID() + " TagName: " + getTagName() + " TagLevel: " + getTagLevel() + " Inhibit: " + isInhibit() + " ColorID: " + getColorID() + " TagID: " + getTagID();
	}
	
	/**
	 * @return
	 */
	public int getColorID() {
		return _colorID;
	}

	/**
	 * @return
	 */
	public int getImageID() {
		return _imageID;
	}

	/**
	 * @return
	 */
	public boolean isInhibit() {
		return _inhibit;
	}

	/**
	 * @return
	 */
	public int getTagID() {
		return getLiteID();
	}

	/**
	 * @return
	 */
	public int getTagLevel() {
		return _tagLevel;
	}

	/**
	 * @return
	 */
	public String getTagName() {
		return _tagName;
	}

	/**
	 * @param i
	 */
	public void setColorID(int i) {
		_colorID = i;
	}

	/**
	 * @param i
	 */
	public void setImageID(int i) {
		_imageID = i;
	}

	/**
	 * @param b
	 */
	public void setInhibit(boolean b) {
		_inhibit = b;
	}

	/**
	 * @param i
	 */
	public void setTagID(int i) {
		setLiteID(i);
	}

	/**
	 * @param i
	 */
	public void setTagLevel(int i) {
		_tagLevel = i;
	}

	/**
	 * @param string
	 */
	public void setTagName(String string) {
		_tagName = string;
	}
}
