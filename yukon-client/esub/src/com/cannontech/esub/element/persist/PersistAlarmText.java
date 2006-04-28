/*
 * Created on May 14, 2003
  */
package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.element.AlarmTextElement;
import com.cannontech.esub.element.DrawingElement;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistAlarmText extends BasePersistElement {

	// Only create one of these
	private static PersistElement instance = null;
	
	public static synchronized PersistElement getInstance() {
		if(instance == null) {
			instance = new PersistAlarmText();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.esub.element.persist.BasePersistElement#readFromJLX(com.cannontech.esub.element.DrawingElement, java.io.InputStream, int)
	 */
	protected void readFromJLX(
		DrawingElement drawingElem,
		InputStream in,
		int version)
		throws IOException {
			AlarmTextElement elem = (AlarmTextElement) drawingElem;
			
			switch(version) {
		
			case 1: {
				elem.setDefaultTextColor(PersistUtils.readColor(in));
				elem.setAlarmTextColor(PersistUtils.readColor(in));
				
				elem.setPointIds(LxSaveUtils.readIntArray(in,0));
				
				elem.setLinkTo(LxSaveUtils.readString(in));				
			}
			break;
			
			case 2: {
				elem.setDefaultTextColor(PersistUtils.readColor(in));
				elem.setAlarmTextColor(PersistUtils.readColor(in));

				elem.setDeviceIds(LxSaveUtils.readIntArray(in,0));
				elem.setPointIds(LxSaveUtils.readIntArray(in,0));
				elem.setAlarmCategoryIds(LxSaveUtils.readIntArray(in,0));
				
				elem.setLinkTo(LxSaveUtils.readString(in));
			}
			break;
				
			default: {
				throw new IOException("Unknown version: " + version + " in " + elem.getClass().getName());
			}
		}			
	}

	/* (non-Javadoc)
	 * @see com.cannontech.esub.element.persist.BasePersistElement#saveAsJLX(com.cannontech.esub.element.DrawingElement, java.io.OutputStream, int)
	 */
	protected void saveAsJLX(
		DrawingElement drawingElem,
		OutputStream out,
		int version)
		throws IOException {
			AlarmTextElement elem = (AlarmTextElement) drawingElem;
			
			PersistUtils.writeColor(out, elem.getDefaultTextColor());
			PersistUtils.writeColor(out, elem.getAlarmTextColor());
			
			LxSaveUtils.writeIntArray(out, elem.getDeviceIds());
			LxSaveUtils.writeIntArray(out, elem.getPointIds());
			LxSaveUtils.writeIntArray(out, elem.getAlarmCategoryIds());

			LxSaveUtils.writeString(out, elem.getLinkTo());
	}

}
