/*
 * Created on May 14, 2003
  */
package com.cannontech.esub.element.persist;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
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
								
				elem.setDefaultTextColor(   new Color(
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in) ));
											
				elem.setAlarmTextColor(		new Color(
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in) ));
				
				//TODO This could become very inefficient when a lot of points exist, be smarter
				int[] pointIDs = LxSaveUtils.readIntArray(in,0);
				ArrayList pointsList = new ArrayList(pointIDs.length);
				//
				for(int i = 0; i < pointIDs.length; i++) {
					LitePoint lp = PointFuncs.getLitePoint(pointIDs[i]);
					if(lp != null) {
						pointsList.add(lp);
					}
					else {
						CTILogger.info("PersistentAlarmText couldn't load point id: " + pointIDs[i]);
					}
				}
				LitePoint[] points = new LitePoint[pointsList.size()];
				pointsList.toArray(points);
				elem.setPoints(points);
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
			
			Color defaultTextColor = elem.getDefaultTextColor();
			LxSaveUtils.writeInt(out, defaultTextColor.getRed());
			LxSaveUtils.writeInt(out, defaultTextColor.getGreen());
			LxSaveUtils.writeInt(out, defaultTextColor.getBlue());
			
			Color alarmTextColor = elem.getAlarmTextColor();
			LxSaveUtils.writeInt(out, alarmTextColor.getRed());
			LxSaveUtils.writeInt(out, alarmTextColor.getGreen());
			LxSaveUtils.writeInt(out, alarmTextColor.getBlue());
			
			LitePoint[] points = elem.getPoints();
			int[] pointIDs = new int[points.length];
			for(int i = 0; i < points.length; i++) {
				pointIDs[i] = points[i].getPointID();
			}
			
			LxSaveUtils.writeIntArray(out, pointIDs, pointIDs.length);
			LxSaveUtils.writeString(out, elem.getLinkTo());
	}

}
