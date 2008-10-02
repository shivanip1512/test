package com.cannontech.esub.model;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.*;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;

/**
 * @author alauinger
 */
public class PointAlarmTableModel extends AbstractTableModel {
	
	private static final String TIMESTAMP_COL = "Timestamp";
	private static final String DEVICENAME_COL = "Device Name";
	private static final String POINTNAME_COL = "Point Name";
	private static final String TEXTMESSAGE_COL = "Condition + Message";
	private static final String ACTIVE_COL = "Active / Ack";
	
	private static final int TIMESTAMP = 0;
	private static final int DEVICENAME = 1;
	private static final int POINTNAME = 2;
	private static final int TEXTMESSAGE = 3;
	private static final int ACTIVE_ACK = 4;
		
	private static final String[] columnNames = {
		TIMESTAMP_COL, DEVICENAME_COL, POINTNAME_COL, TEXTMESSAGE_COL, ACTIVE_COL
	};
	
	private static final int NUM_COLUMNS = columnNames.length;

	private DateFormattingService dateFormattingService = YukonSpringHook.getBean("dateFormattingService",DateFormattingService.class);
	
	//	 Consider alarms for these things
	private int[] _deviceIds = new int[0];
	private int[] _pointIds = new int[0];
	private int[] _alarmCategoryIds = new int[0];
	private boolean hideInactive;
	private boolean hideEvents;
	private boolean hideAcknowledged;
	private YukonUserContext userContext = new SystemUserContext();

	//internal representation - _rows contains ArrayLists
	private List<AlarmRow> rows = new ArrayList<AlarmRow>();
	
	public class AlarmRow {
        private Date timeStamp;
        public String paoName;
        public String pointName;
        public String description;
        public String activeAcknowledgedFlags;
        
        public boolean equals(Object o) {
            return true;
        }
        
        public void setTimeStamp(Date date)
        {
            timeStamp = date;
        }
        public String getTimeStamp()
        {
            String str = dateFormattingService.formatDate(timeStamp, DateFormattingService.DateFormatEnum.BOTH, getUserContext());
            return str;
        }
    }
	
	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
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
	    
	    AlarmRow row = rows.get(rowIndex);
	    
	    switch (columnIndex) {
	    
            case TIMESTAMP:
                return row.getTimeStamp();
                
            case DEVICENAME:
                return row.paoName;
                
            case POINTNAME:
                return row.pointName;
                
            case TEXTMESSAGE:
                return row.description;
                
            case ACTIVE_ACK:
                return row.activeAcknowledgedFlags;
    
            default:
                return null;
        }
	}
	
	/**
	 * Fill up with fresh data!	 * 
	 */
	public void refresh() {
        // Since a given signal can be for a device, point, and alarm category
        // we need to only accept each signal once.
        // Use a hashset that only accepts unique objects
        HashSet<Signal> allSignals = new HashSet<Signal>();
		rows = new ArrayList<AlarmRow>();
		
		for (int i = 0; i < _deviceIds.length; i++) {
			int deviceId = _deviceIds[i];
			List<Signal> deviceSignals = DaoFactory.getAlarmDao().getSignalsForPao(deviceId);
            allSignals.addAll(deviceSignals);
		}
		
		for (int i = 0; i < _pointIds.length; i++) {
			int pointId = _pointIds[i];
			List<Signal> pointSignals = DaoFactory.getAlarmDao().getSignalsForPoint(pointId);
			allSignals.addAll(pointSignals);
		}
		
		for (int i = 0; i < _alarmCategoryIds.length; i++) {
			int alarmCategoryId = _alarmCategoryIds[i];
			List<Signal> alarmCategorySignals = DaoFactory.getAlarmDao().getSignalsForAlarmCategory(alarmCategoryId);
			allSignals.addAll(alarmCategorySignals);
		}
		Iterator<Signal> iter = allSignals.iterator();
        while(iter.hasNext()) {
            Signal signal = iter.next();
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
	        int pointID = s.getPointID();
            LitePoint point = null;
            AlarmRow row = new AlarmRow();
            try {
                point = DaoFactory.getPointDao().getLitePoint(pointID);
            }catch(NotFoundException nfe) {
                // this point may have been deleted.
                CTILogger.error("The point (pointId:"+ pointID + ") for this AlarmTable might have been deleted!", nfe);
            }
            if(point != null) {
                int devID = point.getPaobjectID();
                LiteYukonPAObject device = DaoFactory.getPaoDao().getLiteYukonPAO(devID);
                String activeAcknowledgedFlags = (TagUtils.isConditionActive(s.getTags()) ? "True / " : "False / ") + (!TagUtils.isAlarmUnacked(s.getTags()) ? "True" : "False");
                row = new AlarmRow();
                row.setTimeStamp(s.getTimeStamp());
                row.paoName = device.getPaoName();
                row.pointName = point.getPointName();
                row.description = s.getDescription();
                row.activeAcknowledgedFlags = activeAcknowledgedFlags;
            }
	        
	    	if(isHideEvents() && s.getCategoryID() <= Signal.EVENT_SIGNAL) {
	    	    if(rows.contains(s))
	    		return;
	    	}

    		if(isHideAcknowledged() && (s.getCategoryID() > Signal.EVENT_SIGNAL) ) {
		    	if( !TagUtils.isAlarmUnacked(s.getTags())) {	
		    	    if(rows.contains(row)) {
                        rows.remove(row);
                    }
	    	        return;
	            }	    			
    		}
    		
    		if(isHideInactive()) {
    			if( !TagUtils.isConditionActive(s.getTags()) ) {
    			    if(rows.contains(row)) {
    			        rows.remove(row);
    			    }
    				return;
    			}	    			
    		}  		
	    	
    		if(point != null) {
        		rows.add(row);
    		}
	    }
	}
	
	private void sortRows() {
	    Collections.sort(rows, new Comparator<AlarmRow>() {
	        public int compare(AlarmRow a, AlarmRow b) {
	            int timeDiff = a.getTimeStamp().compareTo(b.getTimeStamp()) * -1;
	            return (timeDiff != 0 ? timeDiff : a.pointName.compareTo(b.pointName)*-1);
	        }
	    });
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

	public boolean isHideInactive() {
		return hideInactive;
	}

	public void setHideInactive(boolean _use_active) {
		this.hideInactive = _use_active;
	}

	public boolean isHideAcknowledged() {
		return hideAcknowledged;
	}

	public void setHideAcknowledged(boolean _use_unacknowledged) {
		this.hideAcknowledged = _use_unacknowledged;
	}

	public boolean isHideEvents() {
		return hideEvents;
	}

	public void setHideEvents(boolean alarmsOnly) {
		hideEvents = alarmsOnly;
	}
	
    public YukonUserContext getUserContext() {
        return userContext;
    }
    
    public void setUserContext(YukonUserContext user) {
        this.userContext = user;
    }
}
