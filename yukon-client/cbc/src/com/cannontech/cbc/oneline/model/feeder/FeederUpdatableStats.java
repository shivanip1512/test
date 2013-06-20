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
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.user.YukonUserContext;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class FeederUpdatableStats extends LxAbstractView implements
        UpdatableStats, AdjustablePosition {
    public static final String LBL_TIMESTAMP = "Updated: ";
    public static final String LBL_KVAR_LOAD = "kVAR: ";
    public static final String LBL_PFACTOR = "PF: ";
    public static final String LBL_DAILYOPS = "Daily / Max Ops: ";
    private static final String LBL_WATT = "kW";
    private static final String LBL_VOLT = "Volt";
    private static final String LBL_TARGET = "Target: ";
    private static final String LBL_WATT_VOLT = "kW/Volt: ";
    private static final String LBL_THREE_PHASE = "Phase A/B/C: ";
    
    private UpdatableTextList 			timestamp = new UpdatableTextList(YukonRoleProperty.FDR_TIMESTAMP.getPropertyId(), this);
    private PointQualCheckUpdatTextList varLoad = new PointQualCheckUpdatTextList(YukonRoleProperty.FDR_KVAR.getPropertyId(),this);
    private UpdatableTextList 			pFactor = new UpdatableTextList(YukonRoleProperty.FDR_PF.getPropertyId(),this);
    private PointQualCheckUpdatTextList wattLoad = new PointQualCheckUpdatTextList(YukonRoleProperty.FDR_WATT.getPropertyId(),this);
    private UpdatableTextList 			dailyOps = new UpdatableTextList(YukonRoleProperty.FDR_OP_CNT.getPropertyId(),this);
    private PointQualCheckUpdatTextList voltLoad = new PointQualCheckUpdatTextList(YukonRoleProperty.FDR_VOLT.getPropertyId(),this);
    private UpdatableTextList 			target = new UpdatableTextList(YukonRoleProperty.FDR_TARGET.getPropertyId(),this);
    private UpdatableTextList			wattVolt = new UpdatableTextList(YukonRoleProperty.FDR_WATT_VOLT.getPropertyId(),this);
    private UpdatableTextList			threePhase = new UpdatableTextList(YukonRoleProperty.FDR_THREE_PHASE.getPropertyId(),this);
    
    private LxGraph graph;
    private OnelineFeeder parent;
    private YukonUserContext userContext;
    private Hashtable<Integer, String> propLabelMap = new Hashtable<Integer, String>();
    private Hashtable<Integer, UpdaterHelper.UpdaterDataType> propColumnMap = new Hashtable<Integer, UpdaterHelper.UpdaterDataType>();
    private List<UpdatableTextList> allStats = new ArrayList<UpdatableTextList>();

    public FeederUpdatableStats(LxGraph graph, OnelineFeeder parent) {
        this.graph = graph;
        this.parent = parent;
        this.userContext = parent.getYukonUserContext();
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

        propLabelMap.put(YukonRoleProperty.FDR_TIMESTAMP.getPropertyId(), LBL_TIMESTAMP);
        propLabelMap.put(YukonRoleProperty.FDR_KVAR.getPropertyId(), LBL_KVAR_LOAD);
        propLabelMap.put(YukonRoleProperty.FDR_PF.getPropertyId(), LBL_PFACTOR);
        propLabelMap.put(YukonRoleProperty.FDR_WATT.getPropertyId(), LBL_WATT);
        propLabelMap.put(YukonRoleProperty.FDR_OP_CNT.getPropertyId(), LBL_DAILYOPS);
        propLabelMap.put(YukonRoleProperty.FDR_VOLT.getPropertyId(), LBL_VOLT);
        propLabelMap.put(YukonRoleProperty.FDR_TARGET.getPropertyId(), LBL_TARGET);
        propLabelMap.put(YukonRoleProperty.FDR_WATT_VOLT.getPropertyId(),LBL_WATT_VOLT);
        propLabelMap.put(YukonRoleProperty.FDR_THREE_PHASE.getPropertyId(), LBL_THREE_PHASE);

    }

    private void initPropColumnMap() {
        propColumnMap.put(YukonRoleProperty.FDR_TIMESTAMP.getPropertyId(),
                          UpdaterHelper.UpdaterDataType.FDR_SHORT_TIME_STAMP_COLUMN);        
        propColumnMap.put(YukonRoleProperty.FDR_KVAR.getPropertyId(),
                          UpdaterHelper.UpdaterDataType.FDR_ONELINE_VAR_LOAD_COLUMN);
        propColumnMap.put(YukonRoleProperty.FDR_PF.getPropertyId(),
                          UpdaterHelper.UpdaterDataType.FDR_POWER_FACTOR_COLUMN);
        propColumnMap.put(YukonRoleProperty.FDR_WATT.getPropertyId(),
                          UpdaterHelper.UpdaterDataType.FDR_ONELINE_WATTS_COLUMN);
        propColumnMap.put(YukonRoleProperty.FDR_OP_CNT.getPropertyId(),
                          UpdaterHelper.UpdaterDataType.FDR_DAILY_OPERATIONS_COLUMN);
        propColumnMap.put(YukonRoleProperty.FDR_VOLT.getPropertyId(),
                          UpdaterHelper.UpdaterDataType.FDR_ONELINE_VOLTS_COLUMN);
        propColumnMap.put(YukonRoleProperty.FDR_TARGET.getPropertyId(),
                          UpdaterHelper.UpdaterDataType.FDR_TARGET_COLUMN);
        propColumnMap.put(YukonRoleProperty.FDR_WATT_VOLT.getPropertyId(), UpdaterHelper.UpdaterDataType.FDR_ONELINE_WATTS_VOLTS_COLUMN);
        propColumnMap.put(YukonRoleProperty.FDR_THREE_PHASE.getPropertyId(), UpdaterHelper.UpdaterDataType.FDR_ONELINE_THREE_PHASE_COLUMN);
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

    @Override
    public LxAbstractGraph getGraph() {
        return graph;

    }

    @Override
    public void setGraph(LxAbstractGraph g) {
        graph = (LxGraph) g;
    }

    public Hashtable<Integer, UpdaterHelper.UpdaterDataType> getPropColumnMap() {
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
                                                            getStreamable(),
                                                            userContext);
            copy.add(pair);

        }
        if (allStats.size() > 1) {
            for (int i = 1; i < allStats.size(); i++) {
                UpdatableTextList prevEl = copy.get(i - 1);
                UpdatableTextList pair = manager.adjustPosition(allStats,
                                                                prevEl.getFirstElement(),
                                                                i,
                                                                getStreamable(),
                                                                userContext);
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
        allStats.add(target);
        allStats.add(timestamp);
        initPointQualCheckable();
        allStats.add(pFactor);
        allStats.add(dailyOps);

        for (UpdatableTextList list : allStats) {
            list.adjustVisibility();
        }

    }

    // TODO make this a part of interface
    private void initPointQualCheckable() {
        Feeder streamable = parent.getStreamable();
        if (streamable != null) {
            varLoad.setPointCheckable(streamable);
            varLoad.setType(UnitOfMeasure.KVAR.getId());

            wattLoad.setPointCheckable(streamable);
            wattLoad.setType(UnitOfMeasure.KW.getId());

            voltLoad.setPointCheckable(streamable);
            voltLoad.setType(UnitOfMeasure.KVOLTS.getId());

        }
        allStats.add(varLoad); 
        allStats.add(threePhase);
        allStats.add(wattLoad);
        allStats.add(voltLoad);
        allStats.add(wattVolt);
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
