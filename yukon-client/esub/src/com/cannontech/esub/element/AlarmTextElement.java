/*
 * Created on May 14, 2003
*/
package com.cannontech.esub.element;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.element.persist.PersistAlarmText;
import com.loox.jloox.LxAbstractText;

/**
 * @author aaron
 */
public class AlarmTextElement
	extends LxAbstractText
	implements DrawingElement, Serializable {

	private static final int CURRENT_VERSION = 1;
	static final Font DEFAULT_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
    static final Color DEFAULT_COLOR = java.awt.Color.white;
    
    static final Font DEFAULT_ALARM_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
    static final Color DEFAULT_ALARM_COLOR = java.awt.Color.red;
	
	private String defaultText = "-";
	private Font defaultTextFont = DEFAULT_FONT;
	private Color defaultTextColor = DEFAULT_COLOR;
	
	private String alarmText = "-";
	private Font alarmTextFont = DEFAULT_ALARM_FONT;
	private Color alarmTextColor = DEFAULT_ALARM_COLOR;
		
   	private LitePoint[] points = new LitePoint[0];
			
	private String linkTo = null;
	private int version = CURRENT_VERSION;
	
	private Drawing drawing = null;
	
	/* 
	 * @see com.cannontech.esub.element.DrawingElement#getVersion()
	 */
	public int getVersion() {
		return version;
	}

	/* 
	 * @see com.cannontech.esub.element.DrawingElement#setVersion(int)
	 */
	public void setVersion(int newVer) {
		version = newVer;
	}

	/* 
	 * @see com.cannontech.esub.element.DrawingElement#isCopyable()
	 */
	public boolean isCopyable() {
		return true;
	}

	/* 
	 * @see com.cannontech.esub.element.DrawingElement#getDrawing()
	 */
	public Drawing getDrawing() {
		return drawing;
	}

	/* 
	 * @see com.cannontech.esub.element.DrawingElement#setDrawing(com.cannontech.esub.editor.Drawing)
	 */
	public void setDrawing(Drawing d) {
		drawing = d;
	}

	/* 
	 * @see com.cannontech.esub.element.DrawingElement#getLinkTo()
	 */
	public String getLinkTo() {
		return linkTo;
	}

	/* 
	 * @see com.cannontech.esub.element.DrawingElement#setLinkTo(java.lang.String)
	 */
	public void setLinkTo(String linkTo) {
		this.linkTo = linkTo;
	}
	
	/* 
	 * @see com.loox.jloox.LxComponent#readFromJLX(java.io.InputStream, java.lang.String)
	 */
	public void readFromJLX(InputStream in, String version) throws IOException {
		super.readFromJLX(in, version);
		PersistAlarmText.getInstance().readFromJLX(this, in);
	}

	/* 
	 * @see com.loox.jloox.LxComponent#saveAsJLX(java.io.OutputStream)
	 */
	public void saveAsJLX(OutputStream out) throws IOException {
		super.saveAsJLX(out);
		PersistAlarmText.getInstance().saveAsJLX(this, out);
	}

	/**
	 * @return LitePoint[] 
	 */
	public LitePoint[] getPoints() {
		return points;
	}

	/**
	 * @param points
	 */
	public void setPoints(LitePoint[] points) {
		this.points = points;
	}

	/**
	 * @return
	 */
	public String getDefaultText() {
		return defaultText;
	}

	/**
	 * @param string
	 */
	public void setDefaultText(String string) {
		defaultText = string;
	}

	/**
	 * @return
	 */
	public String getAlarmText() {
		return alarmText;
	}

	/**
	 * @param string
	 */
	public void setAlarmText(String string) {
		alarmText = string;
	}

	
	/**
	 * @return
	 */
	public Font getAlarmTextFont() {
		return alarmTextFont;
	}

	/**
	 * @return
	 */
	public Font getDefaultTextFont() {
		return defaultTextFont;
	}

	/**
	 * @param font
	 */
	public void setAlarmTextFont(Font font) {
		alarmTextFont = font;
	}

	/**
	 * @param font
	 */
	public void setDefaultTextFont(Font font) {
		defaultTextFont = font;
	}

	/**
	 * @return
	 */
	public Color getAlarmTextColor() {
		return alarmTextColor;
	}

	/**
	 * @return
	 */
	public Color getDefaultTextColor() {
		return defaultTextColor;
	}

	/**
	 * @param color
	 */
	public void setAlarmTextColor(Color color) {
		alarmTextColor = color;
	}

	/**
	 * @param color
	 */
	public void setDefaultTextColor(Color color) {
		defaultTextColor = color;
	}

}
