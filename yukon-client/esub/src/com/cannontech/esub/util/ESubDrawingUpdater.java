package com.cannontech.esub.util;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
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
import com.cannontech.esub.table.Table;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxView;

/**
 * A runnable which updates its drawing on each call to run. Designed to be used
 * from say a timer TODO: This update code should probably be factored out, this
 * is kinda ugly
 * @author alauinger
 */
public class ESubDrawingUpdater extends TimerTask implements DrawingUpdater {

    // The drawing to update
    private Drawing drawing;
    private YukonUserContext userContext = null;
    // Graphs are too expensive to always generate, set to false when only
    // generating svg
    private boolean updateGraphs = false;

    private PointService pointService = YukonSpringHook.getBean("pointService", PointService.class);;

    private DynamicDataSource dynamicDataSource;

    public ESubDrawingUpdater() {
        super();
    }

    /**
     * Method DrawingUpdater.
     * @param d - Drawing to update
     */
    public ESubDrawingUpdater(Drawing d) {
        super();
        setDrawing(d);

    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.esub.util.DrawingUpdater#updateDrawing()
     */
    public void updateDrawing() {
        synchronized (drawing) {
            try {
                // keep stuff up to date in the cache
                DefaultDatabaseCache.getInstance().getAllDevices();
                DefaultDatabaseCache.getInstance().getAllStateGroupMap();
                drawing.getLxGraph().startUndoEdit("update");
                // keep track if we changed anything
                boolean change = false;

                dynamicDataSource = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
                LxComponent[] comp = drawing.getLxGraph().getComponents();
                if (comp != null) {

                    for (int i = 0; i < comp.length; i++) {

                        LxComponent lxComponent = comp[i];
                        if (lxComponent instanceof DynamicText) {
                            change = updateDynamicText(change, lxComponent);
                        }

                        if (lxComponent instanceof StateImage) {
                            change = updateStateImage(change, lxComponent);
                        }

                        if (isUpdateGraphs() && lxComponent instanceof DynamicGraphElement) {
                            change = updateDynamicGraphElement(change, lxComponent);
                        }

                        if (lxComponent instanceof CurrentAlarmsTable) {
                            change = updateCurrentAlarmsTable(lxComponent);
                        }

                        if (lxComponent instanceof AlarmTextElement) {
                            change = updateAlarmTextElement(lxComponent);
                        }
                        if (lxComponent instanceof LineElement) {
                            change = updateLineElement(change, lxComponent);
                        }
                    }

                    // Only force an update if there is a view present
                    // and there has been a change
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

    public boolean updateLineElement(boolean change, LxComponent lxComponent) {
        LineElement le = (LineElement) lxComponent;
        int colorPoint = le.getColorPointID();
        int thicknessPoint = le.getThicknessPointID();
        int arrowPoint = le.getArrowPointID();
        int opacityPoint = le.getOpacityPointID();
        if (colorPoint > 0) {
            LitePoint liteColorPoint = null;
            try {
                liteColorPoint = DaoFactory.getPointDao().getLitePoint(colorPoint);
            }catch(NotFoundException nfe) {
                //this point may have been deleted
                CTILogger.error("LineElement Error: color pointid: " + colorPoint + " not found.");
            }

            if (liteColorPoint != null) {
                if (liteColorPoint.getPointType() == PointTypes.ANALOG_POINT 
                        || liteColorPoint.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                        || liteColorPoint.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT
                        || liteColorPoint.getPointType() == PointTypes.CALCULATED_POINT) {
                    // for analog type points
                    LiteState ls = pointService.getCurrentState(liteColorPoint.getPointID());
                    if (ls != null) {
                        le.setCurrentColorState(ls);
                        le.updateColor();
                        change = true;
                    }
                } else {
                    // must be a status like point
                    PointValueHolder pData = dynamicDataSource.getPointValue(liteColorPoint.getLiteID());

                    if (pData != null) {
                        LiteState ls = DaoFactory.getStateDao().findLiteState(liteColorPoint.getStateGroupID(), (int) pData.getValue());
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
            
            LitePoint liteThicknessPoint = null;
            try {
                liteThicknessPoint = DaoFactory.getPointDao().getLitePoint(thicknessPoint);
            }catch(NotFoundException nfe) {
                //this point may have been deleted
                CTILogger.error("LineElement Error: thickness pointid: " + thicknessPoint + " not found.");
            }

            if (liteThicknessPoint != null) {
                if (liteThicknessPoint.getPointType() == PointTypes.ANALOG_POINT 
                        || liteThicknessPoint.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                        || liteThicknessPoint.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT
                        || liteThicknessPoint.getPointType() == PointTypes.CALCULATED_POINT) {
                    LiteState ls = pointService.getCurrentState(liteThicknessPoint.getPointID());
                    if (ls != null) {
                        le.setCurrentThicknessState(ls);
                        le.updateThickness();
                        change = true;
                    }
                } else {
                    PointValueHolder pData = dynamicDataSource.getPointValue(liteThicknessPoint.getLiteID());

                    if (pData != null) {
                        LiteState ls = DaoFactory.getStateDao().findLiteState(liteThicknessPoint.getStateGroupID(), (int) pData.getValue());
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
            
            LitePoint liteArrowPoint = null;
            try {
                liteArrowPoint = DaoFactory.getPointDao().getLitePoint(arrowPoint);
            }catch(NotFoundException nfe) {
                //this point may have been deleted
                CTILogger.error("LineElement Error: arrow pointid: " + arrowPoint + " not found.");
            }

            if (liteArrowPoint != null) {
                if (liteArrowPoint.getPointType() == PointTypes.ANALOG_POINT 
                        || liteArrowPoint.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                        || liteArrowPoint.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT
                        || liteArrowPoint.getPointType() == PointTypes.CALCULATED_POINT) {
                    LiteState ls = pointService.getCurrentState(liteArrowPoint.getPointID());
                    if (ls != null) {
                        le.setCurrentArrowState(ls);
                        le.updateArrow();
                        change = true;
                    }
                } else {
                    PointValueHolder pData = dynamicDataSource.getPointValue(liteArrowPoint.getLiteID());

                    if (pData != null) {
                        LiteState ls = DaoFactory.getStateDao().findLiteState(liteArrowPoint.getStateGroupID(), (int) pData.getValue());
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
            LitePoint liteOpacityPoint = null;
            try {
                liteOpacityPoint = DaoFactory.getPointDao().getLitePoint(opacityPoint);
            }catch(NotFoundException nfe) {
                //this point may have been deleted
                CTILogger.error("LineElement Error: opacity pointid: " + opacityPoint + " not found.");
            }

            if (liteOpacityPoint != null) {
                if (liteOpacityPoint.getPointType() == PointTypes.ANALOG_POINT 
                        || liteOpacityPoint.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                        || liteOpacityPoint.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT
                        || liteOpacityPoint.getPointType() == PointTypes.CALCULATED_POINT) {
                    LiteState ls = pointService.getCurrentState(liteOpacityPoint.getPointID());
                    if (ls != null) {
                        le.setCurrentOpacityState(ls);
                        le.updateOpacity();
                        change = true;
                    }
                } else {
                    PointValueHolder pData = dynamicDataSource.getPointValue(liteOpacityPoint.getLiteID());

                    if (pData != null) {
                        LiteState ls = DaoFactory.getStateDao().findLiteState(liteOpacityPoint.getStateGroupID(), (int) pData.getValue());
                        if (ls != null) {
                            le.setCurrentOpacityState(ls);
                            le.updateOpacity();
                            change = true;
                        }
                    }
                }
            }
        }
        return change;
    }

    public boolean updateAlarmTextElement(LxComponent lxComponent) {
        boolean change;
        AlarmTextElement te = (AlarmTextElement) lxComponent;
        boolean inAlarm = false;

        int[] paoIds = te.getDeviceIds();
        List<Integer> paoIdsList = new ArrayList<Integer>();
        for(int paoId : paoIds) {
            paoIdsList.add(paoId);
        }
        try {
            List<Signal> deviceSignals = DaoFactory.getAlarmDao().getSignalsForPaos(paoIdsList);
            for (Iterator<Signal> iter = deviceSignals.iterator(); iter.hasNext();) {
                Signal signal = iter.next();
                // find out why there is a null in the list!
                if(signal != null) {
                    if (TagUtils.isAlarmUnacked(signal.getTags())) {
                        inAlarm = true;
                    }
                }
            }
        } catch (DynamicDataAccessException e){
            Throwable cause = e.getCause();
            if(cause.getMessage().contains("not found")){
                CTILogger.error("AlarmText Error: Some of these devices ( " + paoIdsList + " ) not found.");
                te.setText("BROKEN ALARM TEXT");
                return true;
            }
        }

        int[] pointIds = te.getPointIds();
        List<Integer> pointIdsList = new ArrayList<Integer>();
        for(int pointId : pointIds) {
            pointIdsList.add(pointId);
        }
        
        try {
            List<Signal> pointSignals = DaoFactory.getAlarmDao().getSignalsForPoints(pointIdsList);
            for (Iterator<Signal> iter = pointSignals.iterator(); iter.hasNext();) {
                Signal signal = iter.next();
                //find out why there is a null in the list!
                if(signal != null) {
                    if (TagUtils.isAlarmUnacked(signal.getTags())) {
                        inAlarm = true;
                    }
                }
            }
        } catch (DynamicDataAccessException e){
            Throwable cause = e.getCause();
            if(cause.getMessage().contains("not found")){
                String message = cause.getMessage();
                String badPointIds = message.substring(message.indexOf("(") +1 , message.indexOf(")"));
                CTILogger.error("AlarmText Error: points ( " + badPointIds + " ) not found.");
                te.setText("BROKEN ALARM TEXT");
                return true;
            }
        }
            
        int[] alarmCategoryIds = te.getAlarmCategoryIds();
        for (int j = 0; !inAlarm && j < alarmCategoryIds.length; j++) {
            List<Signal> alarmCategorySignals = DaoFactory.getAlarmDao().getSignalsForAlarmCategory(alarmCategoryIds[j]);
            for (Iterator<Signal> iter = alarmCategorySignals.iterator(); iter.hasNext();) {
                Signal signal = iter.next();
                if (TagUtils.isAlarmUnacked(signal.getTags())) {
                    inAlarm = true;
                }
            }
        }

        if (inAlarm) {
            te.setPaint(te.getAlarmTextColor());
        } else {
            te.setPaint(te.getDefaultTextColor());
        }
        change = true;
        return change;
    }

    public boolean updateCurrentAlarmsTable(LxComponent lxComponent) {
        boolean change;
        CurrentAlarmsTable cat = (CurrentAlarmsTable) lxComponent;
        Table table = cat.getTable();
        PointAlarmTableModel tableModel = (PointAlarmTableModel) table.getModel();
        tableModel.setHideInactive(cat.isHideInactive());
        tableModel.setHideEvents(cat.isHideEvents());
        tableModel.setHideAcknowledged(cat.isHideAcknowledged());
        boolean needsAttention = tableModel.refresh(null); 
        if(needsAttention){
            table.setTitle("BROKEN ALARM TABLE");
        } else {
            table.setTitle("Alarms");
        }
        change = true;
        return change;
    }

    public boolean updateDynamicGraphElement(boolean change, LxComponent lxComponent) {
        DynamicGraphElement dge = (DynamicGraphElement) lxComponent;
        if (dge.shouldUpdate()) {
            dge.updateGraph();
            change = true;
        }
        return change;
    }

    public boolean updateStateImage(boolean change, LxComponent lxComponent) {
        StateImage si = (StateImage) lxComponent;
        if(si.getPointID() == -1){
            setBrokenImage(si);
            return true;
        }
        
        LitePoint lp = null;
        PointDao pointDao = DaoFactory.getPointDao();
        try { 
            lp = pointDao.getLitePoint(si.getPointID());

            if (lp.getPointType() == PointTypes.ANALOG_POINT 
                    || lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                    || lp.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT
                    || lp.getPointType() == PointTypes.CALCULATED_POINT) {
                LiteState ls = pointService.getCurrentState(lp.getPointID());
                if (ls != null) {
                    si.setCurrentState(ls);
                    si.updateImage();
                    change = true;
                }
            } else {
                PointValueHolder pData = dynamicDataSource.getPointValue(lp.getLiteID());
                if (pData != null) {
                    LiteState ls = DaoFactory.getStateDao().findLiteState(lp.getStateGroupID(), (int) pData.getValue());
                    if (ls != null) {
                        si.setCurrentState(ls);
                        si.updateImage();
                        change = true;
                    }
                }
            }
            si.updateImage();
            return change;
        } catch (NotFoundException e) {
            CTILogger.error("StateImage Error: point ( " + si.getPointID() + " ) not found.");
            setBrokenImage(si);
            return true;
        }
    }
    
    private void setBrokenImage(StateImage si){
        byte[] imgBuf = Util.DEFAULT_IMAGE_BYTES;
        Image img = Util.prepareImage(imgBuf);      
        si.setImage(img);
    }

    public boolean updateDynamicText(boolean change, LxComponent lxComponent) {
        DynamicText dt = (DynamicText) lxComponent;
        int pointID = dt.getPointId();

        int colorPoint = dt.getColorPointID();
        int textPoint = -1;
        if ((dt.getDisplayAttribs() & PointAttributes.STATE_TEXT) != 0) {
            textPoint = pointID;
        }
        if (colorPoint > 0) {
            try {
                LitePoint liteColorPoint = DaoFactory.getPointDao().getLitePoint(colorPoint);
                if (liteColorPoint.getPointType() == PointTypes.ANALOG_POINT
                    || liteColorPoint.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                    || liteColorPoint.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT
                    || liteColorPoint.getPointType() == PointTypes.CALCULATED_POINT) {
                    LiteState ls = pointService.getCurrentState(liteColorPoint.getPointID());
                    if (ls != null) {
                        dt.setCurrentColorState(ls);
                        dt.updateColor();
                        change = true;
                    }
                } else {
                    PointValueHolder pData = dynamicDataSource.getPointValue(liteColorPoint.getLiteID());

                    if (pData != null) {
                        LiteState ls = DaoFactory.getStateDao().findLiteState(liteColorPoint.getStateGroupID(), (int) pData.getValue());
                        if (ls != null) {
                            dt.setCurrentColorState(ls);
                            dt.updateColor();
                            change = true;
                        }
                    }
                }
            } catch (NotFoundException nfe) {
                // this point may have been deleted
                CTILogger.error("DynamicText Error: Color point:" + colorPoint + " not found.");
            }
        }
        
        if (textPoint > 0) {
            try {
                LitePoint liteTextPoint = DaoFactory.getPointDao().getLitePoint(textPoint);

                if (liteTextPoint.getPointType() == PointTypes.ANALOG_POINT
                    || liteTextPoint.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                    || liteTextPoint.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT
                    || liteTextPoint.getPointType() == PointTypes.CALCULATED_POINT) {
                    LiteState ls = pointService.getCurrentState(liteTextPoint.getPointID());
                    if (ls != null) {
                        dt.setCurrentTextState(ls);
                        dt.updateText();
                        change = true;
                    }
                } else {
                    PointValueHolder pData = dynamicDataSource.getPointValue(liteTextPoint.getLiteID());

                    if (pData != null) {
                        LiteState ls = DaoFactory.getStateDao()
                            .findLiteState(liteTextPoint.getStateGroupID(),
                                          (int) pData.getValue());
                        if (ls != null) {
                            dt.setCurrentTextState(ls);
                            dt.updateText();
                            change = true;
                        }
                    }
                }
            } catch (NotFoundException nfe) {
                // this point may have been deleted
                dt.setText("BROKEN DYNAMIC TEXT");
                change = true;
                CTILogger.error("DynamicText Error: Text point:" + textPoint + " not found.");
            }
                
        } else {
            String text = UpdateUtil.getDynamicTextString(pointID, dt.getDisplayAttribs(), userContext, null);
            if (!text.equals(dt.getText())) {
                    dt.setText(text);
            }
        }
        return change;
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

    public YukonUserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }
    
}
