/*
 * Created on May 14, 2003
  */
package com.cannontech.esub.element.persist;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
				// Break up font and color into components to save
				elem.setDefaultTextFont(	new Font( 
											LxSaveUtils.readString(in),
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in) ));
				
				elem.setDefaultTextColor(   new Color(
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in) ));

				elem.setAlarmTextFont(		new Font(
											LxSaveUtils.readString(in),
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in) ));
											
				elem.setAlarmTextColor(		new Color(
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in),
											LxSaveUtils.readInt(in) ));
				
				
				//TODO This could become very inefficient when a lot of points exist, be smarter
				int[] pointIDs = LxSaveUtils.readIntArray(in,0);
				LitePoint[] points = new LitePoint[pointIDs.length];
				for(int i = 0; i < pointIDs.length; i++) {
					points[i] = PointFuncs.getLitePoint(pointIDs[i]);
				}
				
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
			
			Font defaultTextFont = elem.getDefaultTextFont();
			LxSaveUtils.writeString(out, defaultTextFont.getFontName());
			LxSaveUtils.writeInt(out, defaultTextFont.getStyle());
			LxSaveUtils.writeInt(out, defaultTextFont.getSize());
			
			Color defaultTextColor = elem.getDefaultTextColor();
			LxSaveUtils.writeInt(out, defaultTextColor.getRed());
			LxSaveUtils.writeInt(out, defaultTextColor.getGreen());
			LxSaveUtils.writeInt(out, defaultTextColor.getBlue());
			
			Font alarmTextFont = elem.getAlarmTextFont();
			LxSaveUtils.writeString(out, alarmTextFont.getFontName());
			LxSaveUtils.writeInt(out, alarmTextFont.getStyle());
			LxSaveUtils.writeInt(out, alarmTextFont.getSize());
			
			Color alarmTextColor = elem.getAlarmTextColor();
			LxSaveUtils.writeInt(out, alarmTextColor.getRed());
			LxSaveUtils.writeInt(out, alarmTextColor.getGreen());
			LxSaveUtils.writeInt(out, alarmTextColor.getBlue());
			
			LitePoint[] points = elem.getPoints();
			int[] pointIDs = new int[points.length];
			for(int i = 0; i < points.length; i++) {
				pointIDs[i] = points[i].getPointID();
			}

			LxSaveUtils.writeString(out, elem.getLinkTo());
	}

}
