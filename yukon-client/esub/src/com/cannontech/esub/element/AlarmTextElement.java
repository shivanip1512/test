package com.cannontech.esub.element;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.persist.PersistAlarmText;
import com.loox.jloox.LxAbstractText;

/**
 * An alarm text element is a text string that changes colors depending on whether
 * an alarm is present or not.  Any combination of devices, points, and alarm categories
 * can be chosen as the basis for determining whether an alarm is present.
 * 
 * @author aaron
 */
public class AlarmTextElement
	extends LxAbstractText
	implements DrawingElement, Serializable {
	
	private static final String ELEMENT_ID = "alarmText";
	
	private static final int CURRENT_VERSION = 2;
	static final Font DEFAULT_FONT = new Font("arial", java.awt.Font.BOLD, 12);
    static final Color DEFAULT_COLOR = Color.white;
    static final Color DEFAULT_ALARM_COLOR = Color.red;

	private Color _defaultTextColor = DEFAULT_COLOR; 
	private Color _alarmTextColor = DEFAULT_ALARM_COLOR;
		
	// Consider alarms for these things
	private int[] _deviceIds = new int[0];
	private int[] _pointIds = new int[0];
	private int[] _alarmCategoryIds = new int[0];
			
	private String _linkTo = null;
	private int _version = CURRENT_VERSION;
	
	private Drawing drawing = null;
	
	public AlarmTextElement() {
		setFont(DEFAULT_FONT);
		setPaint(DEFAULT_COLOR);
	}
	/* 
	 * @see com.cannontech.esub.element.DrawingElement#getVersion()
	 */
	public int getVersion() {
		return _version;
	}

	/* 
	 * @see com.cannontech.esub.element.DrawingElement#setVersion(int)
	 */
	public void setVersion(int newVer) {
		_version = newVer;
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
		return _linkTo;
	}

	/* 
	 * @see com.cannontech.esub.element.DrawingElement#setLinkTo(java.lang.String)
	 */
	public void setLinkTo(String linkTo) {
		this._linkTo = linkTo;
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
	 * @return
	 */
	public Color getAlarmTextColor() {
		return _alarmTextColor;
	}	

	/**
	 * @return
	 */
	public Color getDefaultTextColor() {
		return _defaultTextColor;
	}

	/**
	 * @param color
	 */
	public void setAlarmTextColor(Color color) {
		_alarmTextColor = color;
	}

	/**
	 * @param color
	 */
	public void setDefaultTextColor(Color color) {
		_defaultTextColor = color;
	}

	public String getElementID() {
		return ELEMENT_ID;
	}
	public int[] getAlarmCategoryIds() {
		return _alarmCategoryIds;
	}
	public void setAlarmCategoryIds(int[] alarmCategoryIds) {
        if(alarmCategoryIds == null) {
            alarmCategoryIds = new int[0];
        }
		_alarmCategoryIds = alarmCategoryIds;
	}
    
	public int[] getDeviceIds() {
		return _deviceIds;
	}
	public void setDeviceIds(int[] deviceIds) {
        if(deviceIds == null) {
            deviceIds = new int[0];
        }
		_deviceIds = deviceIds;
	}
    
	public int[] getPointIds() {
		return _pointIds;
	}
	public void setPointIds(int[] pointIds) {
        if(pointIds == null) {
            pointIds = new int[0];
        }
		_pointIds = pointIds;
	}
}
