package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.element.DrawingElement;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public abstract class BasePersistElement implements PersistElement {
	// implement these!	
	protected abstract void readFromJLX(DrawingElement drawingElem, InputStream in, int version) 
		throws IOException;
	protected abstract void saveAsJLX(DrawingElement drawingElem, OutputStream out, int version)
		throws IOException;
	
	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
	 */
	public void readFromJLX(DrawingElement drawingElem, InputStream in)
		throws IOException {
			
			int version = LxSaveUtils.readInt(in);
			readFromJLX(drawingElem,in,version);
			LxSaveUtils.readEndOfPart(in);
	}
 
	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#saveAsJLX(DrawingElement, OutputStream)
	 */
	public void saveAsJLX(DrawingElement drawingElem, OutputStream out)
		throws IOException {
			
			int version = drawingElem.getVersion();
			LxSaveUtils.writeInt(out,version);
			saveAsJLX(drawingElem,out, version);
			LxSaveUtils.writeEndOfPart(out);
	}
}	

