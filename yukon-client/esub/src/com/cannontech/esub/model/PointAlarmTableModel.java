package com.cannontech.esub.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
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

	//	 Consider alarms for these things
	private int[] _deviceIds = new int[0];
	private int[] _pointIds = new int[0];
	private int[] _alarmCategoryIds = new int[0];
	
	//internal representation - _rows contains ArrayLists
	private List _rows = new ArrayList();
	
	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return _rows.size();
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
		return ((ArrayList) _rows.get(rowIndex)).get(columnIndex);
	}
	
	/**
	 * Fill up with fresh data!	 * 
	 */
	public void refresh() {
        // Since a given signal can be for a device, point, and alarm category
        // we need to only accept each signal once.
        // Use a hashset that only accepts unique objects
        Set allSignals = new HashSet();
		_rows = new ArrayList();
		
		for (int i = 0; i < _deviceIds.length; i++) {
			int deviceId = _deviceIds[i];
			List deviceSignals = DaoFactory.getAlarmDao().getSignalsForPao(deviceId);
            allSignals.addAll(deviceSignals);
		}
		
		for (int i = 0; i < _pointIds.length; i++) {
			int pointId = _pointIds[i];
			List pointSignals = DaoFactory.getAlarmDao().getSignalsForPoint(pointId);
			allSignals.addAll(pointSignals);
		}
		
		for (int i = 0; i < _alarmCategoryIds.length; i++) {
			int alarmCategoryId = _alarmCategoryIds[i];
			List alarmCategorySignals = DaoFactory.getAlarmDao().getSignalsForAlarmCategory(alarmCategoryId);
			allSignals.addAll(alarmCategorySignals);
		}
		
        for (Iterator iter = allSignals.iterator(); iter.hasNext();) {
            Signal signal = (Signal) iter.next();
            addSignal(signal);
        }
        
		sortRows();
	}	

	/**
	 * Add a row to the table based on this signal
	 * @param s
	 */
	private void addSignal(Signal s) {
	    // find out why there is a null in the list!
	    if(s != null) {
    	    if(!TagUtils.isAlarmUnacked(s.getTags())) {
    	        return;
            }
    		int pointID = s.getPointID();
    		LitePoint point = null;
    		try {
    		    point = DaoFactory.getPointDao().getLitePoint(pointID);
    		}catch(NotFoundException nfe) {
    		    // this point may have been deleted.
    		    CTILogger.error("The point (pointId:"+ pointID + ") for this AlarmTable might have been deleted!", nfe);
    		}
    		if(point != null) {
        		int devID = point.getPaobjectID();
        		LiteYukonPAObject device = DaoFactory.getPaoDao().getLiteYukonPAO(devID);
        		
        		ArrayList row = new ArrayList(NUM_COLUMNS);
        		row.add(s.getTimeStamp());
        		row.add(device.getPaoName());
        		row.add(point.getPointName());
        		row.add(s.getDescription());	
        		_rows.add(row);
    		}
	    }
	}
	
	private void sortRows() {
		Collections.sort(_rows, new Comparator() {
			public int compare(Object a, Object b) {
				ArrayList rowA = (ArrayList) a;
				ArrayList rowB = (ArrayList) b;
				int timeDiff = ((Date) rowA.get(0)).compareTo((Date) rowB.get(0)) * -1;
				return (timeDiff != 0 ? timeDiff : ((String) rowA.get(2)).compareTo((String) rowB.get(2))*-1);
			}
		}
		);
	}
	
	public int[] getAlarmCategoryIds() {
		return _alarmCategoryIds;
	}
	public void setAlarmCategoryIds(int[] alarmCategoryIds) {
        if(alarmCategoryIds == null) {
            alarmCategoryIds = new int[0];
        }
		_alarmCategoryIds = alarmCategoryIds;
	}
	public int[] getDeviceIds() {
		return _deviceIds;
	}
	public void setDeviceIds(int[] deviceIds) {
        if(deviceIds == null) {
            deviceIds = new int[0];
        }
		_deviceIds = deviceIds;
	}
	public int[] getPointIds() {
		return _pointIds;
	}
	public void setPointIds(int[] pointIds) {
        if(pointIds == null) {
            pointIds = new int[0];
        }
		_pointIds = pointIds;
	}
	
}
