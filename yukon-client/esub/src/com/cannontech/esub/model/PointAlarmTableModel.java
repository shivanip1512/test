package com.cannontech.esub.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.dispatch.message.Signal;

/**
 * @author alauinger
 */
public class PointAlarmTableModel extends AbstractTableModel {
	
	private static final String TIMESTAMP_COL = "Timestamp";
	private static final String DEVICENAME_COL = "Device Name";
	private static final String POINTNAME_COL = "Point Name";
	private static final String TEXTMESSAGE_COL = "Text Message";
		
	private static final String[] columnNames = {
		TIMESTAMP_COL, DEVICENAME_COL, POINTNAME_COL, TEXTMESSAGE_COL
	};
	
	private static final int NUM_COLUMNS = columnNames.length;
		
	//show alarms for these devices
	private int[] deviceIDs = new int[] { -1 };
		
	//internal representation - rows contains ArrayLists
	private ArrayList rows = new ArrayList();
	
	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		//return 3;
		return rows.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return NUM_COLUMNS;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}
	
	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return ((ArrayList) rows.get(rowIndex)).get(columnIndex);
	}

	/**
	 * Fill up with fresh data!	 * 
	 */
	public void refresh() {
		rows = new ArrayList();
		
		PointChangeCache cache = PointChangeCache.getPointChangeCache();		
		
		for(int i = 0; i < deviceIDs.length; i++) {
			int deviceID = deviceIDs[i];
			LitePoint[] points = PAOFuncs.getLitePointsForPAObject(deviceID);
	
			for(int j = 0; j < points.length; j++) {
				Signal s = cache.getSignal(points[j].getPointID());
				if(s != null) {
					addSignal(s);
				}			
			}
		}
	}	
	
	/**
	 * Add a row to the table based on this signal
	 * @param s
	 */
	private void addSignal(Signal s) {
		int pointID = (int) s.getPointID();
		LitePoint point = PointFuncs.getLitePoint(pointID);
		
		int devID = point.getPaobjectID();
		LiteYukonPAObject device = PAOFuncs.getLiteYukonPAO(devID);
		
		ArrayList row = new ArrayList(NUM_COLUMNS);
		row.add(s.getTimeStamp());
		row.add(device.getPaoName());
		row.add(point.getPointName());
		row.add(s.getDescription());	
		//row.add(s.getUserName());
		rows.add(row);			
	}
	

	/**
	 * Returns the deviceID.
	 * @return int
	 */
	public int getDeviceID() {
		return getDeviceIDs()[0];
	}

	/**
	 * Sets the deviceID.
	 * @param deviceID The deviceID to set
	 */
	public void setDeviceID(int deviceID) {
		setDeviceIDs(new int[] { deviceID } );
	}

	/**
	 * Returns the array of device IDs
	 * @return int[]
	 */
	public int[] getDeviceIDs() {
		return deviceIDs;
	}
	
	/** Sets the array of deviceIDs
	 * @param deviceIDs the array of deviceIDs
	 */
	public void setDeviceIDs(int[] deviceIDs) {
		this.deviceIDs = deviceIDs;
	}
}
