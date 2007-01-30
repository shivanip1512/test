package com.cannontech.cbc.oneline.model.feeder;

import java.awt.Color;
import java.awt.Font;

import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableTextList;
import com.cannontech.cbc.oneline.states.DynamicLineState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineFeeder implements OnelineObject {
    public static final String LBL_KVAR_LOAD = "KVAR: ";
    public static final String LBL_PFACTOR = "PF: ";
    public static final String LBL_WATT_VOLT = "Watt/Volt";
    private static final String LBL_DAILYOPS = "Daily Ops: ";

    public static final int LINE_LENGTH = 800;
    public static final Font LARGE_FONT = new java.awt.Font("arial",
                                                            java.awt.Font.BOLD,
                                                            14);
    public static final Font SMALL_FONT = new java.awt.Font("arial",
                                                            java.awt.Font.BOLD,
                                                            10);

    public static String NAME_PREFIX = "OnelineFeeder_";

    private OneLineDrawing drawing = null;
    private SubBus subBusMsg = null;
    private FeederControlPanel controlPanel = null;
    private Integer paoId;
    private String name;
    private int currFdrIdx;
    private DynamicLineElement feederLn;
    private StaticText feederName;

    // dynamic text elements
    UpdatableTextList varLoad = new UpdatableTextList();
    UpdatableTextList pFactor = new UpdatableTextList();
    UpdatableTextList wattVoltLoad = new UpdatableTextList();
    UpdatableTextList dailyOps = new UpdatableTextList();

    public void draw() {

        // add the feeder distribution line
        int numOfFdr = subBusMsg.getCcFeeders().size();
        currFdrIdx = drawing.getFeeders().size();
        double refYBelow = getRefLnBelow().getPoint1().getY();
        double refYAbove = getRefLnAbove().getPoint1().getY();
        double fdrOffset = (refYBelow - refYAbove) / numOfFdr;
        double fdrYCoord = fdrOffset * currFdrIdx + refYAbove;

        double subInjLnX = drawing.getSub()
                                  .getInjectionLine()
                                  .getPoint2()
                                  .getX();

        LineElement feederLn = createFeederLn(fdrYCoord, subInjLnX);

        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(feederLn);

        addVarLoad(graph);
        addPFactor(graph);
        addWattVoltLoad(graph);
        addDailyOps(graph);

    }

    private LineElement createFeederLn(double fdrYCoord, double currFdrX) {
        if (feederLn == null) {
            feederLn = new DynamicLineElement(this, new DynamicLineState());
            feederLn.setPoint1(currFdrX, fdrYCoord);
            StaticImage yukonLogo = drawing.getLogos().getYukonLogo();
            int height = (int) (yukonLogo.getX() + yukonLogo.getWidth());
            feederLn.setPoint2(height, fdrYCoord);
            feederLn.setLineColor(Color.YELLOW);
            feederLn.setLineArrow(LineElement.ARROW_END);
            feederLn.setName(getName());

            feederName = new StaticText();
            feederName.setFont(LARGE_FONT);
            feederName.setPaint(Color.PINK);
            feederName.setX(currFdrX + 5);
            feederName.setY(fdrYCoord + 5);
            Feeder feeder = (Feeder) subBusMsg.getCcFeeders().get(currFdrIdx);
            feederName.setText(feeder.getCcName());
            drawing.getDrawing().getLxGraph().add(feederName);
            feederName.setName(getName());

        }
        return feederLn;
    }

    public OnelineFeeder(SubBus subBusMessage) {
        subBusMsg = subBusMessage;

    }

    public void addDrawing(OneLineDrawing d) {
        drawing = d;
        setPaoId(getCurrentFeederIdFromMessage());
        setName(createFeederName());
        draw();
        controlPanel = new FeederControlPanel(this);
    }

    private Integer getCurrentFeederIdFromMessage() {
        Feeder feeder = (Feeder) subBusMsg.getCcFeeders()
                                          .get(drawing.getFeeders().size());
        Integer ccId = feeder.getCcId();
        return ccId;
    }

    private String createFeederName() {
        return OnelineFeeder.NAME_PREFIX + getPaoId();
    }

    public OneLineDrawing getDrawing() {
        return drawing;
    }

    public Integer getPaoId() {
        return paoId;
    }

    public LxLine getRefLnBelow() {
        return drawing.getSub().getRefLnBelow();
    }

    public SubBus getSubBusMsg() {
        return subBusMsg;
    }

    public LxLine getRefLnAbove() {
        return drawing.getSub().getRefLnAbove();
    }

    public void setPaoId(Integer paoId) {
        this.paoId = paoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FeederControlPanel getControlPanel() {
        return controlPanel;
    }

    public int getCurrFdrIdx() {
        return currFdrIdx;
    }

    public LineElement getFeederLn() {
        return feederLn;
    }

    public StaticText getFeederName() {
        return feederName;
    }

    public Feeder getStreamable() {
        return (Feeder) subBusMsg.getCcFeeders().get(currFdrIdx);
    }

    public void addPFactor(LxGraph graph) {
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

    public void addVarLoad(LxGraph graph) {
        String strLabel = LBL_KVAR_LOAD;
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(getFeederName()),
                                                         null,
                                                         new Integer((int) getFeederName().getHeight() + 10));
        
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

    public void addDailyOps(LxGraph graph) {

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

    public void addWattVoltLoad(LxGraph graph) {

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

    public UpdatableTextList getWattVoltLoad() {
        return wattVoltLoad;
    }

    public UpdatableTextList getPFactor() {
        return pFactor;
    }

    public UpdatableTextList getVarLoad() {
        return varLoad;
    }

    public UpdatableTextList getDailyOps() {
        return dailyOps;
    }

}
