package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;

import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DrawingElement;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistCurrentAlarmsTable extends BasePersistElement {
		
	 public void readFromJLX(DrawingElement drawingElem, InputStream in, int version) throws IOException  {
	 		
	 		CurrentAlarmsTable elem = (CurrentAlarmsTable) drawingElem;
	 		switch(version) {
	 			
	 			case 1: {
	 					// We now store an array of device ids instead of a single id
	 					elem.setDeviceIDs(LxSaveUtils.readIntArray(in,0));
	 			} 
	 			break;
	 			
	 			default: {
	 				throw new IOException("Unknown version");
	 			}
	 		}	 		
	 }	
	 
	 public void saveAsJLX(DrawingElement drawingElem, OutputStream out, int version) throws IOException {
	 	
	 		CurrentAlarmsTable elem = (CurrentAlarmsTable) drawingElem;
	 		switch(version) {
	 			
	 		    case 1: {
	 				LxSaveUtils.writeIntArray(out, elem.getDeviceIDs());
	 			}
	 			break;
	 			
	 			default: {
	 				throw new IOException("Unknown version");
	 			}
	 		}
	 }
}
