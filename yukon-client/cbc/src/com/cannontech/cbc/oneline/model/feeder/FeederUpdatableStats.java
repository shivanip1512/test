package com.cannontech.cbc.oneline.model.feeder;

import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.Feeder;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class FeederUpdatableStats extends LxAbstractView implements
        UpdatableStats {
    public static final String LBL_KVAR_LOAD = "KVAR: ";
    public static final String LBL_PFACTOR = "PF: ";
    public static final String LBL_WATT_VOLT = "Watt/Volt";
    public static final String LBL_DAILYOPS = "Daily Ops: ";
    private UpdatableTextList varLoad = new UpdatableTextList();
    private UpdatableTextList pFactor = new UpdatableTextList();
    private UpdatableTextList wattVoltLoad = new UpdatableTextList();
    private UpdatableTextList dailyOps = new UpdatableTextList();
    private LxGraph graph;
    private OnelineFeeder parent;

    public FeederUpdatableStats(LxGraph graph, OnelineFeeder parent) {
        this.graph = graph;
        this.parent = parent;
    }

    public void draw() {
        addVarLoad();
        addPFactor();
        addWattVoltLoad();
        addDailyOps();

    }

    private void addDailyOps() {

        String strLabel = LBL_DAILYOPS;
        LxAbstractText firstElement = getWattVoltLoad().getFirstElement();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(firstElement),
                                                         null,
                                                         new Integer((int) firstElement.getHeight() + 10));

        Integer dayOp = getStreamable().getCurrentDailyOperations();
        StaticText dayOpVal = OnelineUtil.createTextElement(dayOp + "",
                                                            OnelineUtil.getStartPoint(label),
                                                            new Integer((int) (label.getWidth() + 10)),
                                                            null);
        dayOpVal.setName("FeederStat_" + getStreamable().getCcId() + "_DAYOP");
        StaticText separator = OnelineUtil.createTextElement(" / ",
                                                             OnelineUtil.getStartPoint(dayOpVal),
                                                             new Integer((int) (dayOpVal.getWidth() + 10)),
                                                             null);
        int maxDayOp = getStreamable().getMaxDailyOperation().intValue();
        StaticText maxDayVal = OnelineUtil.createTextElement((maxDayOp <= 0) ? CBCDisplay.STR_NA
                                                                     : " " + maxDayOp,
                                                             OnelineUtil.getStartPoint(separator),
                                                             new Integer((int) (separator.getWidth() + 10)),
                                                             null);
        graph.add(label);
        graph.add(dayOpVal);
        graph.add(separator);
        graph.add(maxDayVal);
        dailyOps.setFirstElement(label);
        dailyOps.setLastElement(maxDayVal);

    }

    private UpdatableTextList getWattVoltLoad() {
        return wattVoltLoad;
    }

    private void addWattVoltLoad() {

        String strLabel = LBL_WATT_VOLT;
        LxAbstractText firstElement = getPFactor().getFirstElement();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(firstElement),
                                                         null,
                                                         new Integer((int) firstElement.getHeight() + 10));

        double wats = getStreamable().getCurrentWattLoadPointValue()
                                     .doubleValue();
        double volts = getStreamable().getCurrentVoltLoadPointValue()
                                      .doubleValue();
        StaticText wattVal = OnelineUtil.createTextElement(CommonUtils.formatDecimalPlaces(wats,
                                                                                           2),
                                                           OnelineUtil.getStartPoint(label),
                                                           new Integer((int) label.getWidth() + 10),
                                                           null);
        wattVal.setName("FeederStat_" + getStreamable().getCcId() + "_WATT");

        StaticText separator = OnelineUtil.createTextElement(" / ",
                                                             OnelineUtil.getStartPoint(wattVal),
                                                             new Integer((int) wattVal.getWidth() + 10),
                                                             null);
        StaticText voltVal = OnelineUtil.createTextElement(CommonUtils.formatDecimalPlaces(volts,
                                                                                           2),

                                                           OnelineUtil.getStartPoint(separator),
                                                           new Integer((int) separator.getWidth() + 10),
                                                           null);

        graph.add(label);
        graph.add(wattVal);
        graph.add(separator);
        graph.add(voltVal);
        wattVoltLoad.setFirstElement(label);
        wattVoltLoad.setLastElement(voltVal);

    }

    private UpdatableTextList getPFactor() {
        return pFactor;
    }

    private void addPFactor() {

        String strLabel = LBL_PFACTOR;
        StaticText staticLabel = (StaticText) getVarLoad().getFirstElement();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(staticLabel),
                                                         null,
                                                         new Integer((int) staticLabel.getHeight() + 10));

        double powerFactorValue = getStreamable().getPowerFactorValue()
                                                 .doubleValue();
        StaticText pfVal = OnelineUtil.createTextElement(CBCDisplay.getPowerFactorText(powerFactorValue,
                                                                                       true),
                                                         OnelineUtil.getStartPoint(label),
                                                         new Integer((int) label.getWidth() + 10),
                                                         null);
        pfVal.setName("FeederStat_" + getStreamable().getCcId() + "_PF");

        StaticText separator = OnelineUtil.createTextElement(" / ",
                                                             OnelineUtil.getStartPoint(pfVal),
                                                             new Integer((int) pfVal.getWidth() + 10),
                                                             null);

        double estPF = getStreamable().getEstimatedPFValue().doubleValue();
        StaticText estPFVal = OnelineUtil.createTextElement(CBCDisplay.getPowerFactorText(estPF,
                                                                                          true),
                                                            OnelineUtil.getStartPoint(separator),
                                                            new Integer((int) separator.getWidth() + 10),
                                                            null);
        graph.add(label);
        graph.add(pfVal);
        graph.add(separator);
        graph.add(estPFVal);

        pFactor.setFirstElement(label);
        pFactor.setLastElement(estPFVal);

    }

    private UpdatableTextList getVarLoad() {
        return varLoad;
    }

    private void addVarLoad() {

        String strLabel = LBL_KVAR_LOAD;
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(getFeederName()),
                                                         null,
                                                         new Integer((int) getFeederName().getHeight() + 20));

        double vars = getStreamable().getCurrentVarLoadPointValue()
                                     .doubleValue();
        double estVars = getStreamable().getEstimatedVarLoadPointValue();

        StaticText varVal = OnelineUtil.createTextElement(CommonUtils.formatDecimalPlaces(vars,
                                                                                          2),
                                                          OnelineUtil.getStartPoint(label),
                                                          new Integer((int) label.getWidth() + 10),
                                                          null);

        varVal.setName("FeederStat_" + getStreamable().getCcId() + "_VAR");

        StaticText separator = OnelineUtil.createTextElement(" / ",
                                                             OnelineUtil.getStartPoint(varVal),
                                                             new Integer((int) varVal.getWidth() + 10),
                                                             null);

        StaticText estVarVal = OnelineUtil.createTextElement(CommonUtils.formatDecimalPlaces(estVars,
                                                                                             2),
                                                             OnelineUtil.getStartPoint(separator),
                                                             new Integer((int) separator.getWidth() + 10),
                                                             null);
        graph.add(label);
        graph.add(varVal);
        graph.add(separator);
        graph.add(estVarVal);
        varLoad.setFirstElement(label);
        varLoad.setLastElement(estVarVal);

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

}
