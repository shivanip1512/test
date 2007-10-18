package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DynamicGraphElement;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistDynamicGraphElement extends BasePersistElement  {

	// Only create one of these
	private static PersistElement instance = null;
	
	public static synchronized PersistElement getInstance() {
		if(instance == null) {
			instance = new PersistDynamicGraphElement();
		}
		return instance;
	}
	
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
				
				case 2: {
					elem.setGraphDefinitionID(LxSaveUtils.readInt(in));
                	elem.setTrendType(LxSaveUtils.readInt(in));
                	elem.setDisplayPeriod(LxSaveUtils.readString(in));
                	elem.setNumberOfEvents(0);
                	elem.setNumberOfEvents(LxSaveUtils.readInt(in));
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
			DynamicGraphElement elem = (DynamicGraphElement) drawingElem;
			LxSaveUtils.writeInt(out, elem.getGraphDefinitionID());
            LxSaveUtils.writeInt(out, elem.getTrendType());
           	LxSaveUtils.writeString(out, elem.getDisplayPeriod());
           	LxSaveUtils.writeInt(out, elem.getNumberOfEvents());
	}

}
