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
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.esub.element.StaticText;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class SubUpdatableStats extends LxAbstractView implements
        UpdatableStats, AdjustablePosition {

    private static final String TARGET = "Target: ";
    private static final String TIMESTAMP = "Updated: ";

    private static final String VARLOAD = "kVAR: ";
    private static final String THREE_PHASE = "Phase A/B/C: ";
    private static final String EST_VARLOAD = "Est kVAR: ";

    private static final String POWERFACTOR = "PF: ";
    private static final String EST_POWERFACTOR = "Est. PF: ";

    private static final String WATTLOAD = "kW: ";
    private static final String VOLTLOAD = "Volts: ";
    
    private static final String DAYOP_MAXOP = "Daily / Max Ops: ";
    
    public UpdatableTextList target = new UpdatableTextList(YukonRoleProperty.SUB_TARGET.getPropertyId(), this);
    public UpdatableTextList timestamp = new UpdatableTextList(YukonRoleProperty.SUB_TIMESTAMP.getPropertyId(), this);
    public PointQualCheckUpdatTextList varLoad = new PointQualCheckUpdatTextList(YukonRoleProperty.SUB_VARLOAD.getPropertyId(),this);
    public UpdatableTextList threePhase = new UpdatableTextList(YukonRoleProperty.SUB_THREE_PHASE.getPropertyId(),this);
    public UpdatableTextList estVarLoad = new UpdatableTextList(YukonRoleProperty.SUB_EST_VARLOAD.getPropertyId(),this);
    public UpdatableTextList powerFactor = new UpdatableTextList(YukonRoleProperty.SUB_POWER_FACTOR.getPropertyId(),this);
    public UpdatableTextList estPowerFactor = new UpdatableTextList(YukonRoleProperty.SUB_EST_POWER_FACTOR.getPropertyId(),this);
    public PointQualCheckUpdatTextList wattLoad = new PointQualCheckUpdatTextList(YukonRoleProperty.SUB_WATTS.getPropertyId(),this);
    public PointQualCheckUpdatTextList voltLoad = new PointQualCheckUpdatTextList(YukonRoleProperty.SUB_VOLTS.getPropertyId(),this);
    public UpdatableTextList dayMaxOp = new UpdatableTextList( YukonRoleProperty.SUB_DAILY_MAX_OPCNT.getPropertyId(), this );

    private OnelineSub parent;
    private LxGraph graph;
    private SubBus subBus;
    private YukonUserContext userContext;
    public static Hashtable<Integer, UpdaterHelper.UpdaterDataType> propColumnMap = new Hashtable<Integer, UpdaterHelper.UpdaterDataType>();
    public static Hashtable<Integer, String> propLabelMap = new Hashtable<Integer, String>();;
    public List<UpdatableTextList> allStats = new ArrayList<UpdatableTextList>();

    public SubUpdatableStats(LxGraph graph, OnelineSub parent, SubBus subBus) {
        super();
        this.parent = parent;
        this.graph = graph;
        this.userContext = parent.getYukonUserContext();
        this.subBus = subBus;
        initPropColumnMap();
        initPropLabelMap();
        initAllStats();

    }

    private void initPropLabelMap() {

        propLabelMap.put(YukonRoleProperty.SUB_TARGET.getPropertyId(), TARGET);
        propLabelMap.put(YukonRoleProperty.SUB_TIMESTAMP.getPropertyId(), TIMESTAMP);
        propLabelMap.put(YukonRoleProperty.SUB_VARLOAD.getPropertyId(), VARLOAD);
        propLabelMap.put(YukonRoleProperty.SUB_THREE_PHASE.getPropertyId(), THREE_PHASE);
        propLabelMap.put(YukonRoleProperty.SUB_EST_VARLOAD.getPropertyId(), EST_VARLOAD);
        propLabelMap.put(YukonRoleProperty.SUB_POWER_FACTOR.getPropertyId(), POWERFACTOR);
        propLabelMap.put(YukonRoleProperty.SUB_EST_POWER_FACTOR.getPropertyId(), EST_POWERFACTOR);
        propLabelMap.put(YukonRoleProperty.SUB_WATTS.getPropertyId(), WATTLOAD);
        propLabelMap.put(YukonRoleProperty.SUB_VOLTS.getPropertyId(), VOLTLOAD);
        propLabelMap.put(YukonRoleProperty.SUB_DAILY_MAX_OPCNT.getPropertyId(), DAYOP_MAXOP);

    }

    private void initPropColumnMap() {
        propColumnMap.put(YukonRoleProperty.SUB_TARGET.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_TARGET_COLUMN);
        propColumnMap.put(YukonRoleProperty.SUB_TIMESTAMP.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_SHORT_TIME_STAMP_COLUMN);
        propColumnMap.put(YukonRoleProperty.SUB_VARLOAD.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_ONELINE_KVAR_LOAD_COLUMN);
        propColumnMap.put(YukonRoleProperty.SUB_THREE_PHASE.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_ONELINE_THREE_PHASE_COLUMN);
        propColumnMap.put(YukonRoleProperty.SUB_EST_VARLOAD.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_ONELINE_KVAR_ESTMATED_COLUMN);
        propColumnMap.put(YukonRoleProperty.SUB_POWER_FACTOR.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_ONELINE_PF_COLUMN);
        propColumnMap.put(YukonRoleProperty.SUB_EST_POWER_FACTOR.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_ONELINE_EST_PF_COLUMN);
        propColumnMap.put(YukonRoleProperty.SUB_WATTS.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_ONELINE_WATT_COLUMN);
        propColumnMap.put(YukonRoleProperty.SUB_VOLTS.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_ONELINE_VOLT_COLUMN);
        propColumnMap.put(YukonRoleProperty.SUB_DAILY_MAX_OPCNT.getPropertyId(), UpdaterHelper.UpdaterDataType.SUB_ONELINE_DAILY_MAX_OPCNT_COLUMN);        

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
            UpdatableTextList pair = manager.adjustPosition(allStats, prevComp, 0, subBus, userContext);
            copy.add(pair);
        }
        
        if (allStats.size() > 1) {
            for (int i = 1; i < allStats.size(); i++) {
                UpdatableTextList prevEl = copy.get(i - 1);
                UpdatableTextList pair = manager.adjustPosition(allStats, prevEl.getFirstElement(), i, subBus, userContext);
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
        allStats.add(dayMaxOp);// day / max Ops

        for (UpdatableTextList list : allStats) {
            list.adjustVisibility();
        }

    }
    
    //TODO make this a part of interface
    private void initPointQualCheckable() {
        if (subBus != null) {
            varLoad.setPointCheckable(subBus);
            varLoad.setType(UnitOfMeasure.KVAR.getId());

            wattLoad.setPointCheckable(subBus);
            wattLoad.setType(UnitOfMeasure.KW.getId());

            voltLoad.setPointCheckable(subBus);
            voltLoad.setType(UnitOfMeasure.KVOLTS.getId());
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

    public Hashtable<Integer, UpdaterHelper.UpdaterDataType> getPropColumnMap() {
        return propColumnMap;
    }

    public Hashtable<Integer, String> getPropLabelMap() {
        return propLabelMap;
    }

}
