package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.database.cache.functions.PointFuncs;
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
					 LitePoint lp = PointFuncs.getLitePoint( LxSaveUtils.readInt(in));
        			elem.setPoint(lp);
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
			
			StateImage elem = (StateImage) drawingElem;
			
			switch(version) {
				
				case 1: {
					LitePoint lp = elem.getPoint();
				     int pointID = -1;
        
        			if( lp != null ) {
                		pointID = lp.getPointID();
        			}        

        			LxSaveUtils.writeInt(out, pointID);
                	LxSaveUtils.writeString(out, elem.getLinkTo());
				}
				break;
				
				default: {
					throw new IOException("Unknown version");
				}
			}
	}

}
