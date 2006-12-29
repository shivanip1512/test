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

	// Only create one of these
	private static PersistElement instance = null;
	
	public static synchronized PersistElement getInstance() {
		if(instance == null) {
			instance = new PersistDynamicText();
		}
		return instance;
	}
	
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
                
                case 2: {
                    elem.setPointID(LxSaveUtils.readInt(in));
                    elem.setDisplayAttribs(LxSaveUtils.readInt(in));     
                    elem.setLinkTo(LxSaveUtils.readString(in));   
                    elem.setControlEnabled(LxSaveUtils.readBoolean(in));
                }
                
                case 3: {
                    elem.setPointID(LxSaveUtils.readInt(in));
                    elem.setDisplayAttribs(LxSaveUtils.readInt(in));     
                    elem.setLinkTo(LxSaveUtils.readString(in));   
                    elem.setControlEnabled(LxSaveUtils.readBoolean(in));
                    elem.setColorPointID(LxSaveUtils.readInt(in));
                    elem.setCustomColorMap(PersistUtils.readIntColorMap(in));
                    elem.setCustomTextMap(PersistUtils.readIntStringMap(in));
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
			DynamicText elem = (DynamicText) drawingElem;
			LxSaveUtils.writeInt(out, elem.getPointID());
  			LxSaveUtils.writeInt(out, elem.getDisplayAttribs());
   			LxSaveUtils.writeString(out, elem.getLinkTo());
            LxSaveUtils.writeBoolean(out, elem.getControlEnabled());
            LxSaveUtils.writeInt(out, elem.getColorPointID());
            PersistUtils.writeIntColorMap(out, elem.getCustomColorMap());
            PersistUtils.writeIntStringMap(out, elem.getCustomTextMap());
	}

}
