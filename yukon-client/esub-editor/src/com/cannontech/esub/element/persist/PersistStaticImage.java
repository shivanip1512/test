package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.StaticImage;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistStaticImage extends BasePersistElement {

	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
	 */
	public void readFromJLX(DrawingElement drawingElem, InputStream in, int version)
		throws IOException {
			
			StaticImage elem = (StaticImage) drawingElem;
			
			switch(version) {
				
				case 1: {
					int imgID = LxSaveUtils.readInt(in);
        			LiteYukonImage img = YukonImageFuncs.getLiteYukonImage(imgID);
        			elem.setYukonImage(img);
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
			
			StaticImage elem = (StaticImage) drawingElem;
			
			switch(version) {
				
				case 1: {
					LxSaveUtils.writeInt(out, elem.getYukonImage().getImageID());
        			LxSaveUtils.writeString(out, elem.getLinkTo());
				}
				break;
				
				default: {
					throw new IOException("Unknown version");
				}
			}
		}	
}
