package com.cannontech.cbc.oneline.model.sub;

import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class SubUpdatableStats extends LxAbstractView implements UpdatableStats {
    private static final String TARGET = "Target: ";
    private static final String VARLOAD = "VarLoad/Est: ";
    private static final String POWERFACTOR = "PF/Est: ";
    private static final String WATTVOLTLOAD = "Watts/Volts: ";
    private static final String DAYOP = "Daily/Max Ops: ";
    private static final String CLOSED_TRIP = "Closed/Tripped KVars: ";
    
    private UpdatableTextList target = new UpdatableTextList();
    private UpdatableTextList varLoad = new UpdatableTextList();
    private UpdatableTextList powerFactor = new UpdatableTextList();
    private UpdatableTextList wattVoltLoad = new UpdatableTextList();
    private UpdatableTextList dayOp = new UpdatableTextList();
    private UpdatableTextList closedTripedVar = new UpdatableTextList();

    private CBCDisplay oldWebDisplay = new CBCDisplay();
    private OnelineSub parent;
    private LxGraph graph;
    private SubBus subBusMsg;

    public SubUpdatableStats(LxGraph graph, OnelineSub parent) {
        super();
        this.parent = parent;
        this.graph = graph;
        subBusMsg = parent.getSubBusMsg();
    }

    public void draw() {
        addTarget();
        addVarLoad();
        addPowerFactor();
        addWattVoltLoad();
        addDayOp();
        addClosedTripedVar();

    }

    private void addClosedTripedVar() {

        String strLabel = CLOSED_TRIP;
        LxAbstractText firstElement = dayOp.getFirstElement();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(firstElement),
                                                         null,
                                                         new Integer((int) firstElement.getHeight() + 10));

        String text = (String) oldWebDisplay.getSubBusValueAt(subBusMsg,
                                                              CBCDisplay.SUB_TARGET_COLUMN);
        StaticText closedTripedVal = OnelineUtil.createTextElement(text,
                                                                   OnelineUtil.getStartPoint(label),
                                                                   new Integer((int) label.getWidth() + 10),
                                                                   null);

        closedTripedVal.setName("SubStat_" + subBusMsg.getCcId() + "_CLDTRIP");
        //graph.add(label);
        //graph.add(closedTripedVal);
        closedTripedVar.setFirstElement(label);
        closedTripedVar.setLastElement(closedTripedVal);

    }

    private void addDayOp() {

        String strLabel = DAYOP;
        LxAbstractText firstElement = wattVoltLoad.getFirstElement();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(firstElement),
                                                         null,
                                                         new Integer((int) firstElement.getHeight() + 10));

        String text = (String) oldWebDisplay.getSubBusValueAt(subBusMsg,
                                                              CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN);
        StaticText dayOpVal = OnelineUtil.createTextElement(text,
                                                            OnelineUtil.getStartPoint(label),
                                                            new Integer((int) label.getWidth() + 10),
                                                            null);

        //dayOpVal.setLinkTo("#");
        dayOpVal.setName("SubStat_" + subBusMsg.getCcId() + "_DAYOP");
        graph.add(label);
        graph.add(dayOpVal);
        dayOp.setFirstElement(label);
        dayOp.setLastElement(dayOpVal);

    }

    private void addWattVoltLoad() {

        String strLabel = WATTVOLTLOAD;

        LxAbstractText firstElement = powerFactor.getFirstElement();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(firstElement),
                                                         null,
                                                         new Integer((int) firstElement.getHeight() + 10));

        String text = (String) oldWebDisplay.getSubBusValueAt(subBusMsg,
                                                              CBCDisplay.SUB_WATTS_COLUMN);
        StaticText wattVolt = OnelineUtil.createTextElement(text,
                                                            OnelineUtil.getStartPoint(label),
                                                            new Integer((int) label.getWidth() + 10),
                                                            null);

        wattVolt.setName("SubStat_" + subBusMsg.getCcId() + "_WATTVOLT");

        graph.add(label);
        graph.add(wattVolt);
        wattVoltLoad.setFirstElement(label);
        wattVoltLoad.setLastElement(wattVolt);

    }

    private void addPowerFactor() {

        String strLabel = POWERFACTOR;

        LxAbstractText firstElement = varLoad.getFirstElement();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(firstElement),
                                                         null,
                                                         new Integer((int) firstElement.getHeight() + 10));

        String text = (String) oldWebDisplay.getSubBusValueAt(subBusMsg,
                                                              CBCDisplay.SUB_POWER_FACTOR_COLUMN);
        StaticText pf = OnelineUtil.createTextElement(text,
                                                      OnelineUtil.getStartPoint(label),
                                                      new Integer((int) label.getWidth() + 10),
                                                      null);

        pf.setName("SubStat_" + subBusMsg.getCcId() + "_PF");

        graph.add(label);
        graph.add(pf);
        powerFactor.setFirstElement(label);
        powerFactor.setLastElement(pf);

    }

    private void addVarLoad() {

        String strLabel = VARLOAD;

        LxAbstractText firstElement = target.getFirstElement();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(firstElement),
                                                         null,
                                                         new Integer((int) firstElement.getHeight() + 10));

        String text = (String) oldWebDisplay.getSubBusValueAt(subBusMsg,
                                                              CBCDisplay.SUB_VAR_LOAD_COLUMN);
        StaticText varLoadVal = OnelineUtil.createTextElement(text,
                                                              OnelineUtil.getStartPoint(label),
                                                              new Integer((int) label.getWidth() + 10),
                                                              null);

        varLoadVal.setName("SubStat_" + subBusMsg.getCcId() + "_VAR");

        graph.add(label);
        graph.add(varLoadVal);
        varLoad.setFirstElement(label);
        varLoad.setLastElement(varLoadVal);

    }

    private void addTarget() {

        String strLabel = TARGET;

        StaticText staticTextParentName = parent.getName();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(staticTextParentName),
                                                         null,
                                                         new Integer((int) staticTextParentName.getHeight() + 10));

        String text = (String) oldWebDisplay.getSubBusValueAt(subBusMsg,
                                                              CBCDisplay.SUB_TARGET_COLUMN);
        StaticText trgt = OnelineUtil.createTextElement(text,
                                                        OnelineUtil.getStartPoint(label),
                                                        new Integer((int) label.getWidth() + 10),
                                                        null);

        trgt.setName("SubStat_" + subBusMsg.getCcId() + "_TRGT");

        graph.add(label);
        graph.add(trgt);
        target.setFirstElement(label);
        target.setLastElement(trgt);

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

}
