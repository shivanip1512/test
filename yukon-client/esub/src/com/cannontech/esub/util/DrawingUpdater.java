package com.cannontech.esub.util;

import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.service.PointService;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.PointAttributes;
import com.cannontech.esub.element.AlarmTextElement;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.model.PointAlarmTableModel;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.spring.YukonSpringHook;
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
								
                PointService pointService = (PointService) YukonSpringHook.getBean("pointService");
                DynamicDataSource dynamicDataSource = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource"); 

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
                            
                            int colorPoint = dt.getColorPointID();
                            int textPoint = -1;
                            if( (dt.getDisplayAttribs() & PointAttributes.STATE_TEXT) != 0 ) {
                                textPoint = dt.getPointID();
                            }
                            if (colorPoint > 0) {
                                LitePoint liteColorPoint = DaoFactory.getPointDao().getLitePoint(colorPoint);

                                if (liteColorPoint != null) {
                                    if (liteColorPoint.getPointType() == PointTypes.ANALOG_POINT) {
                                        LiteState ls = pointService.getCurrentState(liteColorPoint.getPointID());
                                        if (ls != null) {
                                            dt.setCurrentColorState(ls);
                                            dt.updateColor();
                                            change = true;
                                        }
                                    } else {
                                        PointData pData = dynamicDataSource.getPointData(liteColorPoint.getLiteID());

                                        if (pData != null) {
                                            LiteState ls = DaoFactory.getStateDao().getLiteState(liteColorPoint.getStateGroupID(), (int) pData.getValue());
                                            if (ls != null) {
                                                dt.setCurrentColorState(ls);
                                                dt.updateColor();
                                                change = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (textPoint > 0) {
                                LitePoint liteTextPoint = DaoFactory.getPointDao().getLitePoint(textPoint);

                                if (liteTextPoint != null) {
                                    if (liteTextPoint.getPointType() == PointTypes.ANALOG_POINT) {
                                        LiteState ls = pointService.getCurrentState(liteTextPoint.getPointID());
                                        if (ls != null) {
                                            dt.setCurrentTextState(ls);
                                            dt.updateText();
                                            change = true;
                                        }
                                    } else {
                                        PointData pData = dynamicDataSource.getPointData(liteTextPoint.getLiteID());

                                        if (pData != null) {
                                            LiteState ls = DaoFactory.getStateDao().getLiteState(liteTextPoint.getStateGroupID(), (int) pData.getValue());
                                            if (ls != null) {
                                                dt.setCurrentTextState(ls);
                                                dt.updateText();
                                                change = true;
                                            }
                                        }
                                    }
                                }
                            }
						}

						if (comp[i] instanceof StateImage) 
                        {							
							StateImage si = (StateImage) comp[i];							
							LitePoint lp = si.getPoint();
			
							if( lp != null ) 
                            {
                                if( lp.getPointType() == PointTypes.ANALOG_POINT)
                                {
								LiteState ls = pointService.getCurrentState(lp.getPointID());
								if( ls != null ) 
                                    {
    								    si.setCurrentState(ls);
    								    si.updateImage();
    								    change = true;
                                    }
                                }else
                                {
                                    PointData pData = dynamicDataSource.getPointData(lp.getLiteID());
                                    
                                    if (pData != null) 
                                    {
                                        LiteState ls = DaoFactory.getStateDao().getLiteState(lp.getStateGroupID(), (int) pData.getValue()); 
                                        if( ls != null ) 
                                        {
                                            si.setCurrentState(ls);
                                            si.updateImage();
                                            change = true;
                                        }
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
                        if(comp[i] instanceof LineElement) {
                            LineElement le = (LineElement) comp[i];                           
                            int colorPoint = le.getColorPointID();
                            int thicknessPoint = le.getThicknessPointID();
                            int arrowPoint = le.getArrowPointID();
                            int opacityPoint = le.getOpacityPointID();
                            if (colorPoint > 0) {
                                LitePoint liteColorPoint = DaoFactory.getPointDao().getLitePoint(colorPoint);

                                if (liteColorPoint != null) {
                                    if (liteColorPoint.getPointType() == PointTypes.ANALOG_POINT) {
                                        LiteState ls = pointService.getCurrentState(liteColorPoint.getPointID());
                                        if (ls != null) {
                                            le.setCurrentColorState(ls);
                                            le.updateColor();
                                            change = true;
                                        }
                                    } else {
                                        PointData pData = dynamicDataSource.getPointData(liteColorPoint.getLiteID());

                                        if (pData != null) {
                                            LiteState ls = DaoFactory.getStateDao().getLiteState(liteColorPoint.getStateGroupID(), (int) pData.getValue());
                                            if (ls != null) {
                                                le.setCurrentColorState(ls);
                                                le.updateColor();
                                                change = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (thicknessPoint > 0) {
                                LitePoint liteThicknessPoint = DaoFactory.getPointDao().getLitePoint(thicknessPoint);

                                if (liteThicknessPoint != null) {
                                    if (liteThicknessPoint.getPointType() == PointTypes.ANALOG_POINT) {
                                        LiteState ls = pointService.getCurrentState(liteThicknessPoint.getPointID());
                                        if (ls != null) {
                                            le.setCurrentThicknessState(ls);
                                            le.updateThickness();
                                            change = true;
                                        }
                                    } else {
                                        PointData pData = dynamicDataSource.getPointData(liteThicknessPoint.getLiteID());

                                        if (pData != null) {
                                            LiteState ls = DaoFactory.getStateDao().getLiteState(liteThicknessPoint.getStateGroupID(), (int) pData.getValue());
                                            if (ls != null) {
                                                le.setCurrentThicknessState(ls);
                                                le.updateThickness();
                                                change = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (arrowPoint > 0) {
                                LitePoint liteArrowPoint = DaoFactory.getPointDao().getLitePoint(arrowPoint);

                                if (liteArrowPoint != null) {
                                    if (liteArrowPoint.getPointType() == PointTypes.ANALOG_POINT) {
                                        LiteState ls = pointService.getCurrentState(liteArrowPoint.getPointID());
                                        if (ls != null) {
                                            le.setCurrentArrowState(ls);
                                            le.updateArrow();
                                            change = true;
                                        }
                                    } else {
                                        PointData pData = dynamicDataSource.getPointData(liteArrowPoint.getLiteID());

                                        if (pData != null) {
                                            LiteState ls = DaoFactory.getStateDao().getLiteState(liteArrowPoint.getStateGroupID(), (int) pData.getValue());
                                            if (ls != null) {
                                                le.setCurrentArrowState(ls);
                                                le.updateArrow();
                                                change = true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (opacityPoint > 0) {
                                LitePoint liteOpacityPoint = DaoFactory.getPointDao().getLitePoint(opacityPoint);

                                if (liteOpacityPoint != null) {
                                    if (liteOpacityPoint.getPointType() == PointTypes.ANALOG_POINT) {
                                        LiteState ls = pointService.getCurrentState(liteOpacityPoint.getPointID());
                                        if (ls != null) {
                                            le.setCurrentOpacityState(ls);
                                            le.updateOpacity();
                                            change = true;
                                        }
                                    } else {
                                        PointData pData = dynamicDataSource.getPointData(liteOpacityPoint.getLiteID());

                                        if (pData != null) {
                                            LiteState ls = DaoFactory.getStateDao().getLiteState(liteOpacityPoint.getStateGroupID(), (int) pData.getValue());
                                            if (ls != null) {
                                                le.setCurrentOpacityState(ls);
                                                le.updateOpacity();
                                                change = true;
                                            }
                                        }
                                    }
                                }
                            }
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
