package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DynamicGraphElement;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistDynamicGraphElement extends BasePersistElement  {

	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
	 */
	public void readFromJLX(DrawingElement drawingElem, InputStream in, int version)
		throws IOException {
			
			DynamicGraphElement elem = (DynamicGraphElement) drawingElem;
				
			switch(version) {
				case 1: {
					elem.setGraphDefinitionID(LxSaveUtils.readInt(in));
                	elem.setTrendType(LxSaveUtils.readInt(in));
                	elem.setDisplayPeriod(LxSaveUtils.readString(in));
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
			
			DynamicGraphElement elem = (DynamicGraphElement) drawingElem;
			
			switch(version) {
				
				case 1: {
					LxSaveUtils.writeInt(out, elem.getGraphDefinitionID());
                    LxSaveUtils.writeInt(out, elem.getTrendType());
                	LxSaveUtils.writeString(out, elem.getDisplayPeriod());
				}
				break;
				
				default: {
					throw new IOException("Unknown version");
				}
			}
	}

}
