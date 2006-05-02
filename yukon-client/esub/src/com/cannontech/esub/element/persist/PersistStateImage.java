package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.StateImage;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistStateImage extends BasePersistElement {

	// Only create one of these
	private static PersistElement instance = null;
	
	public static synchronized PersistElement getInstance() {
		if(instance == null) {
			instance = new PersistStateImage();
		}
		return instance;
	}
	
	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
	 */
	public void readFromJLX(DrawingElement drawingElem, InputStream in, int version)
		throws IOException {
			
			StateImage elem = (StateImage) drawingElem;
			
			switch(version) {
				case 1: {
					int pointID = LxSaveUtils.readInt(in);
        			elem.setPointID(pointID);
                    elem.setLinkTo(LxSaveUtils.readString(in));                                          
				}
				
				break;
				case 2: {
					int pointID = LxSaveUtils.readInt(in);
					Map customImageMap = PersistUtils.readIntIntMap(in);
        			elem.setPointID(pointID);
        			elem.setCustomImageMap(customImageMap);
                    elem.setLinkTo(LxSaveUtils.readString(in));
                }
				break;
                
                case 3: {
                    int pointID = LxSaveUtils.readInt(in);
                    Map customImageMap = PersistUtils.readIntIntMap(in);
                    elem.setPointID(pointID);
                    elem.setCustomImageMap(customImageMap);
                    elem.setLinkTo(LxSaveUtils.readString(in));
                    elem.setControlEnabled(LxSaveUtils.readBoolean(in));
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
			StateImage elem = (StateImage) drawingElem;
			LitePoint lp = elem.getPoint();
		     int pointID = -1;
        
   			if( lp != null ) {
              		pointID = lp.getPointID();
   			}        
   			
   			LxSaveUtils.writeInt(out, pointID);
   			PersistUtils.writeIntIntMap(out, elem.getCustomImageMap());
           	LxSaveUtils.writeString(out, elem.getLinkTo());
            LxSaveUtils.writeBoolean(out, elem.getControlEnabled());
	}

}
