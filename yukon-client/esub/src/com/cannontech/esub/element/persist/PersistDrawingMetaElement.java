package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DrawingMetaElement;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistDrawingMetaElement extends BasePersistElement {

	// Only create one of these
	private static PersistElement instance = null;
	 
	public static synchronized PersistElement getInstance() {
		if(instance == null) {
			instance = new PersistDrawingMetaElement();
		}
		return instance;
	}
	
	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
	 */
	public void readFromJLX(DrawingElement drawingElem, InputStream in, int version)
		throws IOException {
			DrawingMetaElement elem = (DrawingMetaElement) drawingElem;

			switch(version) {
				
				case 1: {
					elem.setDrawingWidth(LxSaveUtils.readInt(in));
					elem.setDrawingHeight(LxSaveUtils.readInt(in));
					elem.setRoleID(LxSaveUtils.readInt(in));
				}
				break;
				
				default: {
					throw new IOException("Unknown version: " + version + " in " + elem.getClass().getName());
				}
			}
	}

	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#saveAsJLX(DrawingElement, OutputStream)
	 */
	public void saveAsJLX(DrawingElement drawingElem, OutputStream out, int version)
		throws IOException {
			DrawingMetaElement elem = (DrawingMetaElement) drawingElem;
			LxSaveUtils.writeInt(out, elem.getDrawingWidth());
			LxSaveUtils.writeInt(out, elem.getDrawingHeight());
			LxSaveUtils.writeInt(out, elem.getRoleID());	
		}

}
