package com.cannontech.esub.editor.element;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
public class DrawingMetaElement extends LxAbstractText {
	private static final int DEFAULT_WIDTH = 1024;
	private static final int DEFAULT_HEIGHT = 768;
	
	private int drawingWidth = DEFAULT_WIDTH;
	private int drawingHeight = DEFAULT_HEIGHT;
	/**
	 * @see com.loox.jloox.LxComponent#readFromJLX(InputStream, String)
	 */
	public void readFromJLX(InputStream in, String version) throws IOException {
		super.readFromJLX(in, version);
		
		setDrawingWidth(LxSaveUtils.readInt(in));
		setDrawingHeight(LxSaveUtils.readInt(in));
	
		LxSaveUtils.readEndOfPart(in);
	}

	/**
	 * @see com.loox.jloox.LxComponent#saveAsJLX(OutputStream)
	 */
	public void saveAsJLX(OutputStream out) throws IOException {
		super.saveAsJLX(out);
	
		LxSaveUtils.writeInt(out, getDrawingWidth());
		LxSaveUtils.writeInt(out, getDrawingHeight());
				
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

}
