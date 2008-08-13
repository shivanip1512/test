package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DrawingElement;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistCurrentAlarmsTable extends BasePersistElement {
	
	// Only create one of these	
	private static PersistElement instance = null;
	
	public static synchronized PersistElement getInstance() {
		if(instance ==  null) {
			instance = new PersistCurrentAlarmsTable();	
		}
		return instance;
	}
	
	 public void readFromJLX(DrawingElement drawingElem, InputStream in, int version) throws IOException  {
	 		
	 		CurrentAlarmsTable elem = (CurrentAlarmsTable) drawingElem;

	 		switch(version) {
	 			
	 			case 0: {
	 				int deviceId = LxSaveUtils.readInt(in);
	 				elem.setDeviceIds(new int[] { deviceId } );
	 			}
	 			break;
	 			
	 			case 1: {
	 				elem.setDeviceIds(LxSaveUtils.readIntArray(in,0));
	 			} 
	 			break;
	 			
	 			case 2: {
	 				elem.setDeviceIds(LxSaveUtils.readIntArray(in,0));
	 				elem.setPointIds(LxSaveUtils.readIntArray(in,0));
	 				elem.setAlarmCategoryIds(LxSaveUtils.readIntArray(in,0));
	 			}
	 			break;

	 			case 3: {
	 				elem.setDeviceIds(LxSaveUtils.readIntArray(in,0));
	 				elem.setPointIds(LxSaveUtils.readIntArray(in,0));
	 				elem.setAlarmCategoryIds(LxSaveUtils.readIntArray(in,0));
	 				elem.setHideInactive(LxSaveUtils.readBoolean(in));
	 				elem.setHideEvents(LxSaveUtils.readBoolean(in));
	 				elem.setHideAcknowledged(LxSaveUtils.readBoolean(in));
	 			}
	 			break;
	 			
	 			default: {
	 				throw new IOException("Unknown version: " + version + " in " + elem.getClass().getName());
	 			}
	 		}	 
	 }	
	 
	 public void saveAsJLX(DrawingElement drawingElem, OutputStream out, int version) throws IOException {
	 		CurrentAlarmsTable elem = (CurrentAlarmsTable) drawingElem;
	 		
	 		LxSaveUtils.writeIntArray(out, elem.getDeviceIds());
	 		LxSaveUtils.writeIntArray(out, elem.getPointIds());
	 		LxSaveUtils.writeIntArray(out, elem.getAlarmCategoryIds());
	 		LxSaveUtils.writeBoolean(out, elem.isHideInactive());
	 		LxSaveUtils.writeBoolean(out, elem.isHideEvents());
	 		LxSaveUtils.writeBoolean(out, elem.isHideAcknowledged());
	 }
}
