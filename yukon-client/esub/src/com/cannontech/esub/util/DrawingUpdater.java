package com.cannontech.esub.util;

import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.AlarmTextElement;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.model.PointAlarmTableModel;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxView;

/**
 * A runnable which updates its drawing on each call
 * to run.
 * 
 * Designed to be used from say a timer
 * TODO:  This update code should probably be factored out, this is kinda ugly
 * @author alauinger
 */
public class DrawingUpdater extends TimerTask {

	// The drawing to update
	private Drawing drawing;

	// Graphs are too expensive to always generate, set to false when only generating svg
	private boolean updateGraphs = false;
	
    public DrawingUpdater() {
        super();
    }
    
	/**
	 * Method DrawingUpdater.
	 * @param d - Drawing to update
	 */
	public DrawingUpdater(Drawing d) {
		super();
		setDrawing(d);
	}

	public void updateDrawing() {
		synchronized (drawing) {	
			try {
				// keep stuff up to date in the cache
				DefaultDatabaseCache.getInstance().getAllDevices();
				DefaultDatabaseCache.getInstance().getAllStateGroupMap();
				drawing.getLxGraph().startUndoEdit("update");				
				// keep track if we changed anything
				boolean change = false; 
								
				PointChangeCache pcc = PointChangeCache.getPointChangeCache();

				LxComponent[] comp = drawing.getLxGraph().getComponents();
				if (comp != null) {

					for (int i = 0; i < comp.length; i++) {

						if (comp[i] instanceof DynamicText) {
							DynamicText dt = (DynamicText) comp[i];
							String text = UpdateUtil.getDynamicTextString(dt.getPointID(), dt.getDisplayAttribs());
							
							// only update if there is something to update
							if( !text.equals(dt.getText()) ) {
								if( !text.equals(dt.getText()) ) {
									dt.setText(text);
								}
							}
						}

						if (comp[i] instanceof StateImage) {							
							StateImage si = (StateImage) comp[i];							
							LitePoint lp = si.getPoint();
			
							if( lp != null ) {
								PointData pData = pcc.getValue(lp.getPointID());
								if( pData != null ) {
									LiteState ls = DaoFactory.getStateDao().getLiteState(lp.getStateGroupID(), (int) pData.getValue());
									if( ls != null ) {
										si.setCurrentState(ls);
										si.updateImage();
										change = true;
									}	
	
								}								
							}
							
							si.updateImage();
						}
						
						if( isUpdateGraphs() && comp[i] instanceof DynamicGraphElement ) {
							DynamicGraphElement dge = (DynamicGraphElement) comp[i];
							if( dge.shouldUpdate() ) {
								dge.updateGraph();
								change = true;
							}							
						}
						
						if(comp[i] instanceof CurrentAlarmsTable) {
							CurrentAlarmsTable cat = (CurrentAlarmsTable) comp[i];
							((PointAlarmTableModel)cat.getTable().getModel()).refresh();
							change = true;
						}
						
						if(comp[i] instanceof AlarmTextElement) {
							AlarmTextElement te =(AlarmTextElement) comp[i];
							boolean inAlarm = false;

							int[] deviceIds = te.getDeviceIds();
							for(int j = 0; j < deviceIds.length; j++) {
								List deviceSignals = DaoFactory.getAlarmDao().getSignalsForPao(deviceIds[j]);
								for (Iterator iter = deviceSignals.iterator(); iter.hasNext();) {
									Signal signal  = (Signal) iter.next();
									if(TagUtils.isAlarmUnacked(signal.getTags())) {
										inAlarm = true;
									}
								}
							}
							
							int[] pointIds = te.getPointIds();
							for(int j = 0; !inAlarm && j < pointIds.length; j++) {
								List pointSignals = DaoFactory.getAlarmDao().getSignalsForPoint(pointIds[j]);
								for (Iterator iter = pointSignals.iterator(); iter.hasNext();) {
									Signal signal = (Signal) iter.next();
									if(TagUtils.isAlarmUnacked(signal.getTags())) {
										inAlarm = true;
									}
								}
							}
							int[] alarmCategoryIds = te.getAlarmCategoryIds();
							for(int j = 0; !inAlarm && j < alarmCategoryIds.length; j++) {
								List alarmCategorySignals = DaoFactory.getAlarmDao().getSignalsForAlarmCategory(alarmCategoryIds[j]);
								for (Iterator iter = alarmCategorySignals.iterator(); iter.hasNext();) {
									Signal signal = (Signal) iter.next();
									if(TagUtils.isAlarmUnacked(signal.getTags())) {
										inAlarm = true;
									}									
								}
							}
							
							if(inAlarm) {
								te.setPaint(te.getAlarmTextColor());
							}
							else {
								te.setPaint(te.getDefaultTextColor());
							}
							change = true;
						}
					}

					//Only force an update if there is a view present
					//and there has been a change
					LxView view = drawing.getLxView();
					if (change && view != null) {
						view.repaint();
					}
				}
drawing.getLxGraph().cancelUndoEdit();
			} catch (Throwable t) {
				t.printStackTrace();
			}			
		}

	}
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (drawing == null) {
			return;
		} 	

		updateDrawing();		
	}

	/**
	 * Returns the drawing.
	 * @return Drawing
	 */
	public Drawing getDrawing() {
		return drawing;
	}

	/**
	 * Sets the drawing.
	 * @param drawing The drawing to set
	 */
	public synchronized void setDrawing(Drawing drawing) {
		this.drawing = drawing;
	}

	/**
	 * @return
	 */
	public boolean isUpdateGraphs() {
		return updateGraphs;
	}

	/**
	 * @param b
	 */
	public void setUpdateGraphs(boolean b) {
		updateGraphs = b;
	}

}
