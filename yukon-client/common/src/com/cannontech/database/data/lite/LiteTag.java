/*
 * Created on Dec 19, 2003
 */
package com.cannontech.database.data.lite;

import com.cannontech.database.db.tags.Tag;

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
		return getTagName();
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
	
	public void retrieve(String databaseAlias) 
	{
 
	   com.cannontech.database.SqlStatement s = 
		  new com.cannontech.database.SqlStatement(
			 "SELECT TagID, TagName, TagLevel, Inhibit, ColorID, ImageID "  + 
				"FROM TAGS where TagID = " + getTagID(),
			 com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	   try 
	   {
		  s.execute();

		  if( s.getRowCount() <= 0 )
			 throw new IllegalStateException("Unable to find a tag with ID = " + getLiteID() );


		  setTagID( new Integer(s.getRow(0)[0].toString()).intValue() );
		  setTagName( s.getRow(0)[1].toString() );
		  setTagLevel( new Integer(s.getRow(0)[2].toString()).intValue());
		  setInhibit( s.getRow(0)[3].toString().compareTo("Y") == 0);
	   }
	   catch( Exception e )
	   {
		  com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	   }
      
	}
}
