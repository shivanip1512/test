package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.core.image.dao.YukonImageDao;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.spring.YukonSpringHook;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistStaticImage extends BasePersistElement {

	// Only create one of these
	private static PersistElement instance = null;
	
	public static synchronized PersistElement getInstance() {
		if(instance == null) {
			instance = new PersistStaticImage();
		}
		return instance;
	}
	
	/**
	 * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
	 */
	public void readFromJLX(DrawingElement drawingElem, InputStream in, int version)
		throws IOException {
			
			StaticImage elem = (StaticImage) drawingElem;
			
			switch(version) {
				
				case 1: {
					int imgID = LxSaveUtils.readInt(in);
					LiteYukonImage img = YukonSpringHook.getBean(YukonImageDao.class).getLiteYukonImage(imgID);
					elem.setYukonImage(img);
					elem.setLinkTo(LxSaveUtils.readString(in));
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
			StaticImage elem = (StaticImage) drawingElem;
			
			LxSaveUtils.writeInt(out, elem.getYukonImage().getImageID());
        	LxSaveUtils.writeString(out, elem.getLinkTo());
		}	
}
