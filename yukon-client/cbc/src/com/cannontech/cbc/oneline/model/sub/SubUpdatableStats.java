package com.cannontech.cbc.oneline.model.sub;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.cannontech.cbc.oneline.OnelineDisplayManager;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.oneline.view.AdjustablePosition;
import com.cannontech.esub.element.StaticText;
import com.cannontech.roles.capcontrol.CBCOnelineSettingsRole;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class SubUpdatableStats extends LxAbstractView implements
        UpdatableStats, AdjustablePosition {
    private static final String TARGET = "Target: ";
    private static final String VARLOAD = "VarLoad/Est: ";
    private static final String POWERFACTOR = "PF/Est: ";
    private static final String WATTVOLTLOAD = "Watts/Volts: ";
    private static final String DAYOP = "Daily/Max Ops: ";
    //private static final String CLOSED_TRIP = "Closed/Tripped KVars: ";

    public UpdatableTextList target = new UpdatableTextList(CBCOnelineSettingsRole.SUB_TARGET,
                                                            this);
    public UpdatableTextList varLoad = new UpdatableTextList(CBCOnelineSettingsRole.SUB_VARLOAD,
                                                             this);
    public UpdatableTextList powerFactor = new UpdatableTextList(CBCOnelineSettingsRole.SUB_POWER_FACTOR,
                                                                 this);
    public UpdatableTextList wattVoltLoad = new UpdatableTextList(CBCOnelineSettingsRole.SUB_WATTSVOLTS,
                                                                  this);
    public UpdatableTextList dayOp = new UpdatableTextList(CBCOnelineSettingsRole.SUB_OP_CNT,
                                                           this);
    public UpdatableTextList closedTripedVar = new UpdatableTextList();

    public OnelineSub parent;
    private LxGraph graph;
    public SubBus subBusMsg;
    public static Hashtable<Integer, Integer> propColumnMap = new Hashtable<Integer, Integer>();
    public static Hashtable<Integer, String> propLabelMap = new Hashtable<Integer, String>();;
    public List<UpdatableTextList> allStats = new ArrayList<UpdatableTextList>();

    public SubUpdatableStats(LxGraph graph, OnelineSub parent) {
        super();
        this.parent = parent;
        this.graph = graph;
        subBusMsg = parent.getSubBusMsg();
        initPropColumnMap();
        initPropLabelMap();
        initAllStats();

    }

    private void initPropLabelMap() {

        propLabelMap.put(CBCOnelineSettingsRole.SUB_TARGET, TARGET);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_VARLOAD, VARLOAD);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_POWER_FACTOR, POWERFACTOR);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_WATTSVOLTS, WATTVOLTLOAD);
        propLabelMap.put(CBCOnelineSettingsRole.SUB_OP_CNT, DAYOP);

    }

    private void initPropColumnMap() {
        propColumnMap.put(CBCOnelineSettingsRole.SUB_TARGET,
                          CBCDisplay.SUB_TARGET_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_VARLOAD,
                          CBCDisplay.SUB_VAR_LOAD_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_POWER_FACTOR,
                          CBCDisplay.SUB_POWER_FACTOR_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_WATTSVOLTS,
                          CBCDisplay.SUB_WATTS_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.SUB_OP_CNT,
                          CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN);
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
        }
    }

    public List<UpdatableTextList> adjustPosition() {
        List<UpdatableTextList> copy = new ArrayList<UpdatableTextList>();
        OnelineDisplayManager manager = OnelineDisplayManager.getInstance();
        if (allStats.size() > 0) {
            StaticText prevComp = parent.getName();
            UpdatableTextList pair = manager.adjustPosition(allStats,
                                                            prevComp,
                                                            0,
                                                            subBusMsg);
            copy.add(pair);

        }
        if (allStats.size() > 1) {
            for (int i = 1; i < allStats.size(); i++) {
                UpdatableTextList prevEl = copy.get(i - 1);
                UpdatableTextList pair = manager.adjustPosition(allStats,
                                                                prevEl.getFirstElement(),
                                                                i,
                                                                subBusMsg);
                copy.add(pair);
            }
        }
        return copy;
    }

    public void initAllStats() {
        allStats.add(target);
        allStats.add(varLoad);
        allStats.add(powerFactor);
        allStats.add(wattVoltLoad);
        allStats.add(dayOp);
        for (UpdatableTextList list : allStats) {
            list.adjustVisibility();
        }
        
    }

    public void filterInvisibleFromList() {
        List<UpdatableTextList> tempList = new ArrayList<UpdatableTextList>();
        for (UpdatableTextList list : allStats) {
            if (list.isVisible())
            {
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

}
