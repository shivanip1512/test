package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DynamicText;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
*/
public class PersistDynamicText extends BasePersistElement {

	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
	 */
	public void readFromJLX(DrawingElement drawingElem, InputStream in, int version)
		throws IOException {
			
			DynamicText elem = (DynamicText) drawingElem;
			
			switch(version) {
				
				case 1: {
					elem.setPointID(LxSaveUtils.readInt(in));
        			elem.setDisplayAttribs(LxSaveUtils.readInt(in));     
        			elem.setLinkTo(LxSaveUtils.readString(in));   
				}
				break;
				
				default: {
					throw new IOException("Unknown version");
				}
			}
	}

	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#saveAsJLX(DrawingElement, OutputStream)
	 */
	public void saveAsJLX(DrawingElement drawingElem, OutputStream out, int version)
		throws IOException {
			
			DynamicText elem = (DynamicText) drawingElem;
			
			switch(version) {
				
				case 1: {
					LxSaveUtils.writeInt(out, elem.getPointID());
        			LxSaveUtils.writeInt(out, elem.getDisplayAttribs());
        			LxSaveUtils.writeString(out, elem.getLinkTo());
				}
				break;
				
				default: {
					throw new IOException("Unknown version");
				}
			}
	}

}
