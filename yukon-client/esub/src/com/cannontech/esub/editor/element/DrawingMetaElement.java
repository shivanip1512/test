package com.cannontech.esub.editor.element;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.editor.EditorPrefs;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxSaveUtils;

/**
 * The purpose of this element is to store meta information about a drawing.
 * It extends LxAbstractText only because its parents LxElement and LxComponent
 * have methods that are not visible outside of their package meaning I don't
 * think I can directory subclass them.
 * 
 * This class has no visible representation.
 * 
 * @author alauinger
 */
public class DrawingMetaElement extends LxAbstractText implements DrawingElement {

	private static final int DEFAULT_ROLE_ID = 200;
	
	private int version = 1;
	private int drawingWidth;
	private int drawingHeight;
	private int viewRoleID = DEFAULT_ROLE_ID;
	private int editRoleID = DEFAULT_ROLE_ID;
	private int controlRoleID = Integer.MAX_VALUE;	
		
	private transient Drawing drawing = null;
	private String linkTo = null;
	private Properties props = new Properties();
	
	public DrawingMetaElement() {
		EditorPrefs prefs = EditorPrefs.getPreferences();		
		setDrawingWidth(prefs.getDefaultDrawingWidth());
		setDrawingHeight(prefs.getDefaultDrawingHeight());
	}	
	
	/**
	 * @see com.loox.jloox.LxComponent#readFromJLX(InputStream, String)
	 */
	public void readFromJLX(InputStream in, String version) throws IOException {
		super.readFromJLX(in, version);
		
		setVersion(LxSaveUtils.readInt(in));
		setDrawingWidth(LxSaveUtils.readInt(in));
		setDrawingHeight(LxSaveUtils.readInt(in));
		setViewRoleID(LxSaveUtils.readInt(in));
		setEditRoleID(LxSaveUtils.readInt(in));
		setControlRoleID(LxSaveUtils.readInt(in));
		LxSaveUtils.readEndOfPart(in);
	}

	/**
	 * @see com.loox.jloox.LxComponent#saveAsJLX(OutputStream)
	 */
	public void saveAsJLX(OutputStream out) throws IOException {
		super.saveAsJLX(out);
	
		LxSaveUtils.writeInt(out, getVersion());
		LxSaveUtils.writeInt(out, getDrawingWidth());
		LxSaveUtils.writeInt(out, getDrawingHeight());
		LxSaveUtils.writeInt(out, getViewRoleID());
		LxSaveUtils.writeInt(out, getEditRoleID());
		LxSaveUtils.writeInt(out, getControlRoleID());
		LxSaveUtils.writeEndOfPart(out);
	}

	/**
	 * Returns the drawingHeight.
	 * @return int
	 */
	public int getDrawingHeight() {
		return drawingHeight;
	}

	/**
	 * Returns the drawingWidth.
	 * @return int
	 */
	public int getDrawingWidth() {
		return drawingWidth;
	}

	/**
	 * Sets the drawingHeight.
	 * @param drawingHeight The drawingHeight to set
	 */
	public void setDrawingHeight(int drawingHeight) {
		this.drawingHeight = drawingHeight;
	}

	/**
	 * Sets the drawingWidth.
	 * @param drawingWidth The drawingWidth to set
	 */
	public void setDrawingWidth(int drawingWidth) {
		this.drawingWidth = drawingWidth;
	}

	/**
	 * Returns the version.
	 * @return int
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 * @param version The version to set
	 */
	public void setVersion(int version) {
		this.version = version;
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
		drawing = d;
	}



	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#getElementProperties()
	 */
	public Properties getElementProperties() {
		return props;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#setElementProperties(Properties)
	 */
	public void setElementProperties(Properties props) {
		this.props = props;
	}

	/**
	 * Returns the linkTo.
	 * @return String
	 */
	public String getLinkTo() {
		return linkTo;
	}

	/**
	 * Sets the linkTo.
	 * @param linkTo The linkTo to set
	 */
	public void setLinkTo(String linkTo) {
		this.linkTo = linkTo;
	}

	/**
	 * Returns the controlRoleID.
	 * @return int
	 */
	public int getControlRoleID() {
		return controlRoleID;
	}

	/**
	 * Returns the editRoleID.
	 * @return int
	 */
	public int getEditRoleID() {
		return editRoleID;
	}

	/**
	 * Returns the viewRoleID.
	 * @return int
	 */
	public int getViewRoleID() {
		return viewRoleID;
	}

	/**
	 * Sets the controlRoleID.
	 * @param controlRoleID The controlRoleID to set
	 */
	public void setControlRoleID(int controlRoleID) {
		this.controlRoleID = controlRoleID;
	}

	/**
	 * Sets the editRoleID.
	 * @param editRoleID The editRoleID to set
	 */
	public void setEditRoleID(int editRoleID) {
		this.editRoleID = editRoleID;
	}

	/**
	 * Sets the viewRoleID.
	 * @param viewRoleID The viewRoleID to set
	 */
	public void setViewRoleID(int viewRoleID) {
		this.viewRoleID = viewRoleID;
	}

}
