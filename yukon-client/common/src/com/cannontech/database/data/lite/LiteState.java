package com.cannontech.database.data.lite;

/*
 */
public class LiteState extends LiteBase
{
	private String stateText = null;
	private int fgColor = 0;
	private int bgColor = 0;
   private int imageID = 0;

/**
 * LiteState
 */
public LiteState( int rawSt ) 
{
	this( rawSt, null, 0, 0, 0 );
}
/**
 * LiteState
 */
public LiteState( int rawSt, String stText, int fgColor_, int bgColor_, int imgID ) 
{
	super();
	setLiteID(rawSt);
	stateText = stText;
	setLiteType(LiteTypes.STATE);	
	setFgColor( fgColor_ );
	setBgColor( bgColor_ );
   setImageID( imgID );
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getStateRawState() 
{
	return getLiteID();
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String getStateText() {
	return stateText;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setStateRawState(int newValue) 
{
	setLiteID(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setStateText(String newValue) {
	this.stateText = new String(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() {
	return stateText;
}
	/**
	 * Returns the imageID.
	 * @return int
	 */
	public int getImageID()
	{
		return imageID;
	}

	/**
	 * Sets the imageID.
	 * @param imageID The imageID to set
	 */
	public void setImageID(int imageID)
	{
		this.imageID = imageID;
	}

	/**
	 * @return
	 */
	public int getBgColor()
	{
		return bgColor;
	}

	/**
	 * @return
	 */
	public int getFgColor()
	{
		return fgColor;
	}

	/**
	 * @param i
	 */
	public void setBgColor(int i)
	{
		bgColor = i;
	}

	/**
	 * @param i
	 */
	public void setFgColor(int i)
	{
		fgColor = i;
	}

}
