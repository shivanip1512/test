package com.cannontech.analysis;

import java.io.Serializable;

/**
 * Created on Oct 10, 2003 
 * @author snebben
 *
 * Properties for a tableModel's JFreeReport display/location.
 */
public class ColumnProperties implements Serializable
{
	/** X absolute position */ 
	private float positionX = 0;
	/** Y absolute position */
	private float positionY = 1;
	/** Minimum width */
	private float width = 100;

	/** Value display format, null implies basic String/Text display */
	private String valueFormat = null;
		
	/**
	 * Constructor
	 * @param posX_ - x position
	 * @param posY_ - y position
	 * @param width_ - width
	 * @param height_ hight 
	 * @param valueFormat_ = string format of data, null for String/Text data
	 */
	public ColumnProperties(float posX_, float posY_, float width_, String valueFormat_)
	{
		super();
		positionX = posX_;
		positionY = posY_;
		width = width_;
		valueFormat = valueFormat_;
	}

	/**
	 * Returns the positionX
	 * @return float positionX
	 */
	public float getPositionX()
	{
		return positionX;
	}

	/**
	 * Returnns the positionY
	 * @return float positionY
	 */
	public float getPositionY()
	{
		return positionY;
	}

	/**
	 * Returns the width
	 * @return float width
	 */
	public float getWidth()
	{
		return width;
	}

	/**
	 * Set the positionX
	 * @param float posX_
	 */
	public void setPositionX(float posX_)
	{
		positionX = posX_;
	}

	/**
	 * Set the positionY
	 * @param float posY_
	 */
	public void setPositionY(float posY_)
	{
		positionY = posY_;
	}

	/**
	 * Set the width
	 * @param float width_
	 */
	public void setWidth(float width_)
	{
		width = width_;
	}

	/**
	 * Return the valueFormat string 
	 * @return String valueFormat
	 */
	public String getValueFormat()
	{
		return valueFormat;
	}

	/**
	 * Set the valueFormat string
	 * @param String format_
	 */
	public void setValueFormat(String format_)
	{
		valueFormat = format_;
	}

}