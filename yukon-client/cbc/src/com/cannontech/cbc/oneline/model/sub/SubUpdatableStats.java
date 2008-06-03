package com.cannontech.cbc.oneline.model.sub;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.cannontech.cbc.oneline.OnelineDisplayManager;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.util.PointQualCheckUpdatTextList;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.oneline.view.AdjustablePosition;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.esub.element.StaticText;
import com.cannontech.roles.capcontrol.CBCOnelineSettingsRole;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class SubUpdatableStats extends LxAbstractView implements
        UpdatableStats, AdjustablePosition {

    private static final String AREA_NAME = "Area: ";
    private static final String CTL_METHOD = "Ctl. Method: ";

    private static final String TARGET = "Target: ";
    private static final String TIMESTAMP = "Updated: ";

    private static final String VARLOAD = "kVAR: ";
    private static final String THREE_PHASE = "Phase A/B/C: ";
    private static final String EST_VARLOAD = "Est kVAR: ";

    private static final String POWERFACTOR = "PF: ";
    private static final String EST_POWERFACTOR = "Est. PF: ";

    private static final String WATTLOAD = "kW: ";
    private static final String VOLTLOAD = "Volts: ";
    
    private static final String DAYOP = "Daily Ops: ";
    private static final String MAX_DAYOP = "Max Daily Ops: ";
    private static final String DAYOP_MAXOP = "Daily / Max Ops: ";
    
    public UpdatableTextList target = new UpdatableTextList(CBCOnelineSettingsRole.SUB_TARGET, this);
    public UpdatableTextList timestamp = new UpdatableTextList(CBCOnelineSettingsRole.SUB_TIMESTAMP, this);
    public PointQualCheckUpdatTextList varLoad = new PointQualCheckUpdatTextList(CBCOnelineSettingsRole.SUB_VARLOAD,this);
    public UpdatableTextList threePhase = new UpdatableTextList(CBCOnelineSettingsRole.SUB_THREE_PHASE,this);
    public UpdatableTextList estVarLoad = new UpdatableTextList(CBCOnelineSettingsRole.SUB_EST_VARLOAD,this);
    public UpdatableTextList powerFactor = new UpdatableTextList(CBCOnelineSettingsRole.SUB_POWER_FACTOR,this);
    public UpdatableTextList estPowerFactor = new UpdatableTextList(CBCOnelineSettingsRole.SUB_EST_POWER_FACTOR,this);
    public PointQualCheckUpdatTextList wattLoad = new PointQualCheckUpdatTextList(CBCOnelineSettingsRole.SUB_WATTS,this);
    public PointQualCheckUpdatTextList voltLoad = new PointQualCheckUpdatTextList(CBCOnelineSettingsRole.SUB_VOLTS,this);
    public UpdatableTextList dayOp = new UpdatableTextList(CBCOnelineSettingsRole.SUB_DAILY_OPCNT, this);
    public UpdatableTextList maxDayOp = new UpdatableTextList(CBCOnelineSettingsRole.SUB_MAX_DAILY_OPCNT, this);
    public UpdatableTextList dayMaxOp = new UpdatableTextList( CBCOnelineSettingsRole.SUB_DAILY_MAX_OPCNT, this );
    public UpdatableTextList areaName = new UpdatableTextList(CBCOnelineSettingsRole.SUB_AREA,this);
    public UpdatableTextList ctlMethod = new UpdatableTextList(CBCOnelineSettingsRole.SUB_CTL_METHOD,this);

    private OnelineSub parent;
    private LxGraph graph;
    private SubBus subBus;
    private LiteYukonUser user;
    public static Hashtable<Integer, Integer> propColumnMap = new Hashtable<Integer, Integer>();
    public static Hashtable<Integer, String> propLabelMap = new Hashtable<Integer, String>();;
    public List<UpdatableTextList> allStats = new ArrayList<UpdatableTextList>();

    public SubUpdatableStats(LxGraph graph, OnelineSub parent, SubBus subBus) {
        super();
        this.parent = parent;
        this.graph = graph;
        this.user = parent.getUser();
        this.subBus = subBus;
        initPropColumnMap();
        initPropLabelMap();
        initAllStats();

    }

    private void initPropLabelMap() {

        propLabelMap.put(CBCOnelineSettingsRole.SUB_TARGET, TARGET);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_TIMESTAMP, TIMESTAMP);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_VARLOAD, VARLOAD);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_THREE_PHASE, THREE_PHASE);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_EST_VARLOAD, EST_VARLOAD);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_POWER_FACTOR, POWERFACTOR);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_EST_POWER_FACTOR, EST_POWERFACTOR);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_WATTS, WATTLOAD);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_VOLTS, VOLTLOAD);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_DAILY_OPCNT, DAYOP);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_MAX_DAILY_OPCNT, MAX_DAYOP);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_DAILY_MAX_OPCNT, DAYOP_MAXOP);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_AREA, AREA_NAME);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_CTL_METHOD, CTL_METHOD);

    }

    private void initPropColumnMap() {
        propColumnMap.put(CBCOnelineSettingsRole.SUB_TARGET, CBCDisplay.SUB_TARGET_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_TIMESTAMP, CBCDisplay.SUB_SHORT_TIME_STAMP_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_VARLOAD, CBCDisplay.SUB_ONELINE_KVAR_LOAD_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_THREE_PHASE, CBCDisplay.SUB_ONELINE_THREE_PHASE_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_EST_VARLOAD, CBCDisplay.SUB_ONELINE_KVAR_ESTMATED_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_POWER_FACTOR, CBCDisplay.SUB_ONELINE_PF_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_EST_POWER_FACTOR, CBCDisplay.SUB_ONELINE_EST_PF_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_WATTS, CBCDisplay.SUB_ONELINE_WATT_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_VOLTS, CBCDisplay.SUB_ONELINE_VOLT_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_DAILY_OPCNT, CBCDisplay.SUB_ONELINE_DAILY_OPCNT_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_MAX_DAILY_OPCNT, CBCDisplay.SUB_ONELINE_MAX_OPCNT_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_DAILY_MAX_OPCNT, CBCDisplay.SUB_ONELINE_DAILY_MAX_OPCNT_COLUMN);        
        propColumnMap.put(CBCOnelineSettingsRole.SUB_AREA, CBCDisplay.SUB_ONELINE_AREANAME_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_CTL_METHOD, CBCDisplay.SUB_ONELINE_CTL_METHOD_COLUMN);

    }

    public void draw() {
        filterInvisibleFromList();
        List<UpdatableTextList> copy = adjustPosition();
        addToGraph(graph, copy);
    }

    public void addToGraph(LxGraph graph, List<UpdatableTextList> copy) {
        for (UpdatableTextList list : copy) {
            graph.add(list.getFirstElement());
            graph.add(list.getLastElement());
            addExtraElements(graph, list);
        }
    }

    public void addExtraElements(LxGraph graph, UpdatableTextList list) {
        if (!list.getExtraElements().isEmpty()) {
            List<LxAbstractText> extraElements = list.getExtraElements();
            for (LxAbstractText text : extraElements) {
                graph.add(text);
            }
        }
    }

    public List<UpdatableTextList> adjustPosition() {
        List<UpdatableTextList> copy = new ArrayList<UpdatableTextList>();
        OnelineDisplayManager manager = OnelineDisplayManager.getInstance();
        if (allStats.size() > 0) {
            StaticText prevComp = parent.getName();
            UpdatableTextList pair = manager.adjustPosition(allStats, prevComp, 0, subBus, user);
            copy.add(pair);
        }
        
        if (allStats.size() > 1) {
            for (int i = 1; i < allStats.size(); i++) {
                UpdatableTextList prevEl = copy.get(i - 1);
                UpdatableTextList pair = manager.adjustPosition(allStats, prevEl.getFirstElement(), i, subBus, user);
                copy.add(pair);
            }
        }
        return copy;
    }

    public void initAllStats() {
        allStats.add(target);
        allStats.add(timestamp);
        initPointQualCheckable();
        allStats.add(estVarLoad);
        allStats.add(powerFactor);
        allStats.add(estPowerFactor);
        allStats.add(dayOp);   // day Ops
        allStats.add(maxDayOp);// max Ops
        allStats.add(dayMaxOp);// day / max Ops
        allStats.add(areaName);
        allStats.add(ctlMethod);

        for (UpdatableTextList list : allStats) {
            list.adjustVisibility();
        }

    }
    
    //TODO make this a part of interface
    private void initPointQualCheckable() {
        if (subBus != null) {
            varLoad.setPointCheckable(subBus);
            varLoad.setType(PointUnits.UOMID_KVAR);

            wattLoad.setPointCheckable(subBus);
            wattLoad.setType(PointUnits.UOMID_KW);

            voltLoad.setPointCheckable(subBus);
            voltLoad.setType(PointUnits.UOMID_KVOLTS);
        }
        allStats.add(varLoad);
        allStats.add(threePhase);
        allStats.add(wattLoad);
        allStats.add(voltLoad);
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

    public OnelineObject getParentOnelineObject() {
        return parent;
    }

    public void setParentOnelineObject(OnelineObject p) {
        parent = (OnelineSub) p;
    }

    @Override
    public LxAbstractGraph getGraph() {
        return graph;

    }

    @Override
    public void setGraph(LxAbstractGraph g) {
        graph = (LxGraph) g;

    }

    public Hashtable<Integer, Integer> getPropColumnMap() {
        return propColumnMap;
    }

    public Hashtable<Integer, String> getPropLabelMap() {
        return propLabelMap;
    }

}
