package com.cannontech.esub.editor.element;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.editor.Drawing;
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
	private static final int DEFAULT_WIDTH = 1024;
	private static final int DEFAULT_HEIGHT = 768;	
	private static final int DEFAULT_ROLE_ID = 200;
	
	private int version = 1;
	private int drawingWidth = DEFAULT_WIDTH;
	private int drawingHeight = DEFAULT_HEIGHT;
	private int roleID = DEFAULT_ROLE_ID;	
	
	private transient Drawing drawing = null;

	
	/**
	 * @see com.loox.jloox.LxComponent#readFromJLX(InputStream, String)
	 */
	public void readFromJLX(InputStream in, String version) throws IOException {
		super.readFromJLX(in, version);
		
		setVersion(LxSaveUtils.readInt(in));
		setDrawingWidth(LxSaveUtils.readInt(in));
		setDrawingHeight(LxSaveUtils.readInt(in));
		setRoleID(LxSaveUtils.readInt(in));
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
		LxSaveUtils.writeInt(out, getRoleID());
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
	 * Returns the roleID.
	 * @return int
	 */
	public int getRoleID() {
		return roleID;
	}

	/**
	 * Sets the roleID.
	 * @param roleID The roleID to set
	 */
	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#getDrawing()
	 */
	public Drawing getDrawing() {
		return drawing;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#getLinkTo()
	 */
	public String getLinkTo() {
		return null;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#setDrawing(Drawing)
	 */
	public void setDrawing(Drawing d) {
		drawing = d;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#setLinkTo(String)
	 */
	public void setLinkTo(String linkTo) {
	}

}
