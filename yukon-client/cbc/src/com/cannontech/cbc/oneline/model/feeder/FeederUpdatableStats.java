package com.cannontech.cbc.oneline.model.feeder;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.cannontech.cbc.oneline.OnelineDisplayManager;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.util.PointQualCheckUpdatTextList;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.oneline.view.AdjustablePosition;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.roles.capcontrol.CBCOnelineSettingsRole;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.Feeder;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class FeederUpdatableStats extends LxAbstractView implements
        UpdatableStats, AdjustablePosition {
    public static final String LBL_TIMESTAMP = "Updated: ";
    public static final String LBL_KVAR_LOAD = "KVAR: ";
    public static final String LBL_PFACTOR = "PF: ";
    public static final String LBL_WATT_VOLT = "Watt/Volt";
    public static final String LBL_DAILYOPS = "Daily / Max Ops: ";
    private static final String LBL_WATT = "Watt";
    private static final String LBL_VOLT = "Volt";
    private static final String LBL_TARGET = "Target: ";
    
    public UpdatableTextList timestamp = new UpdatableTextList(CBCOnelineSettingsRole.FDR_TIMESTAMP, this);
    
    private PointQualCheckUpdatTextList varLoad = new PointQualCheckUpdatTextList(CBCOnelineSettingsRole.FDR_KVAR,this);
    
    private UpdatableTextList pFactor = new UpdatableTextList(CBCOnelineSettingsRole.FDR_PF,this);
    
    private PointQualCheckUpdatTextList wattLoad = new PointQualCheckUpdatTextList(CBCOnelineSettingsRole.FDR_WATT,this);
    
    private UpdatableTextList dailyOps = new UpdatableTextList(CBCOnelineSettingsRole.FDR_OP_CNT,this);
    
    private PointQualCheckUpdatTextList voltLoad = new PointQualCheckUpdatTextList(CBCOnelineSettingsRole.FDR_VOLT,this);

    private UpdatableTextList target = new UpdatableTextList(CBCOnelineSettingsRole.FDR_TARGET,this);

    private LxGraph graph;
    private OnelineFeeder parent;
    private Hashtable<Integer, String> propLabelMap = new Hashtable<Integer, String>();
    private Hashtable<Integer, Integer> propColumnMap = new Hashtable<Integer, Integer>();
    private List<UpdatableTextList> allStats = new ArrayList<UpdatableTextList>();

    public FeederUpdatableStats(LxGraph graph, OnelineFeeder parent) {
        this.graph = graph;
        this.parent = parent;
        initPropColumnMap();
        initPropLabelMap();
        initAllStats();
    }

    public void draw() {

        filterInvisibleFromList();
        List<UpdatableTextList> copy = adjustPosition();
        addToGraph(graph, copy);

    }

    private void initPropLabelMap() {

        propLabelMap.put(CBCOnelineSettingsRole.FDR_TIMESTAMP, LBL_TIMESTAMP);
        propLabelMap.put(CBCOnelineSettingsRole.FDR_KVAR, LBL_KVAR_LOAD);
        propLabelMap.put(CBCOnelineSettingsRole.FDR_PF, LBL_PFACTOR);
        propLabelMap.put(CBCOnelineSettingsRole.FDR_WATT, LBL_WATT);
        propLabelMap.put(CBCOnelineSettingsRole.FDR_OP_CNT, LBL_DAILYOPS);
        propLabelMap.put(CBCOnelineSettingsRole.FDR_VOLT, LBL_VOLT);
        propLabelMap.put(CBCOnelineSettingsRole.FDR_TARGET, LBL_TARGET);

    }

    private void initPropColumnMap() {
        propColumnMap.put(CBCOnelineSettingsRole.FDR_TIMESTAMP,
                          CBCDisplay.FDR_SHORT_TIME_STAMP_COLUMN);        
        propColumnMap.put(CBCOnelineSettingsRole.FDR_KVAR,
                          CBCDisplay.FDR_ONELINE_VAR_LOAD_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.FDR_PF,
                          CBCDisplay.FDR_POWER_FACTOR_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.FDR_WATT,
                          CBCDisplay.FDR_ONELINE_WATTS_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.FDR_OP_CNT,
                          CBCDisplay.FDR_DAILY_OPERATIONS_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.FDR_VOLT,
                          CBCDisplay.FDR_ONELINE_VOLTS_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.FDR_TARGET,
                          CBCDisplay.FDR_TARGET_COLUMN);
    }

    private Feeder getStreamable() {
        return parent.getStreamable();
    }

    private LxComponent getFeederName() {
        return parent.getFeederName();
    }

    public OnelineObject getParentOnelineObject() {
        return parent;
    }

    public void setParentOnelineObject(OnelineObject p) {
        parent = (OnelineFeeder) p;
    }

    public LxAbstractGraph getGraph() {
        return graph;

    }

    public void setGraph(LxAbstractGraph g) {
        graph = (LxGraph) g;
    }

    public Hashtable<Integer, Integer> getPropColumnMap() {
        return propColumnMap;
    }

    public Hashtable<Integer, String> getPropLabelMap() {
        return propLabelMap;
    }

    public void addToGraph(LxGraph graph, List<UpdatableTextList> copy) {
        for (UpdatableTextList list : copy) {
            graph.add(list.getFirstElement());
            graph.add(list.getLastElement());
            addExtraElements(graph, list);
        }
    }

    public List<UpdatableTextList> adjustPosition() {
        List<UpdatableTextList> copy = new ArrayList<UpdatableTextList>();
        OnelineDisplayManager manager = OnelineDisplayManager.getInstance();
        if (allStats.size() > 0) {
            LxComponent prevComp = getFeederName();
            UpdatableTextList pair = manager.adjustPosition(allStats,
                                                            prevComp,
                                                            0,
                                                            getStreamable());
            copy.add(pair);

        }
        if (allStats.size() > 1) {
            for (int i = 1; i < allStats.size(); i++) {
                UpdatableTextList prevEl = copy.get(i - 1);
                UpdatableTextList pair = manager.adjustPosition(allStats,
                                                                prevEl.getFirstElement(),
                                                                i,
                                                                getStreamable());
                copy.add(pair);
            }
        }
        return copy;
    }

    public void filterInvisibleFromList() {
        List<UpdatableTextList> tempList = new ArrayList<UpdatableTextList>();
        for (UpdatableTextList list : allStats) {
            if (list.isVisible()) {
                tempList.add(list);
            }
        }
        allStats = tempList;
    }

    public void initAllStats() {
        allStats.add(timestamp);
        initPointQualCheckable();
        allStats.add(pFactor);
        allStats.add(dailyOps);
        allStats.add(target);

        for (UpdatableTextList list : allStats) {
            list.adjustVisibility();
        }

    }

    // TODO make this a part of interface
    private void initPointQualCheckable() {
        Feeder streamable = parent.getStreamable();
        if (streamable != null) {
            varLoad.setPointCheckable(streamable);
            varLoad.setType(PointUnits.UOMID_KVAR);

            wattLoad.setPointCheckable(streamable);
            wattLoad.setType(PointUnits.UOMID_KW);

            voltLoad.setPointCheckable(streamable);
            voltLoad.setType(PointUnits.UOMID_KVOLTS);

        }
        allStats.add(varLoad);
        allStats.add(wattLoad);
        allStats.add(voltLoad);

    }

    public void addExtraElements(LxGraph graph, UpdatableTextList list) {
        if (!list.getExtraElements().isEmpty()) {
            List<LxAbstractText> extraElements = list.getExtraElements();
            for (LxAbstractText text : extraElements) {
                graph.add(text);
            }
        }
    }

}
