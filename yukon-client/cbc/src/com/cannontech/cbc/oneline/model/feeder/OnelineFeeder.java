package com.cannontech.cbc.oneline.model.feeder;

import java.awt.Color;
import java.awt.Font;

import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.states.DynamicLineState;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxLine;

public class OnelineFeeder implements OnelineObject {
    public static final int LINE_LENGTH = 800;
    public static final Font LARGE_FONT = new java.awt.Font("arial",
                                                            java.awt.Font.BOLD,
                                                            14);
    public static String NAME_PREFIX = "OnelineFeeder_";
    private OneLineDrawing drawing = null;
    private SubBus subBusMsg = null;
    private FeederControlPanel controlPanel = null;
    private Integer paoId;
    private String name;
    private int currFdrIdx;
    private DynamicLineElement feederLn;
    private StaticText feederName;

    public void draw() {
        /*
         * Drawing actualDrawing = drawing.getDrawing(); OneLineParams
         * layoutParams = drawing.getLayoutParams(); int nextFdrIdx =
         * drawing.getFeeders().size(); Vector feederVector =
         * subBusMsg.getCcFeeders(); Feeder currentFeeder = (Feeder)
         * feederVector.get(nextFdrIdx); double feederSpacing =
         * layoutParams.getFeederHorzLineLength() / ((double)
         * feederVector.size() - 1); if (feederVector.size() == 1) {
         * feederSpacing = 0.0; } LxGraph graph = actualDrawing.getLxGraph();
         * double labelTextHorzLeft; double valueTextHorzLeft; double
         * textVertUpper; double feederPosition =
         * (layoutParams.getFeederHorzLineStart() + (feederSpacing *
         * nextFdrIdx)); LineElement currentFeederLine = new LineElement();
         * currentFeederLine.setPoint1(feederPosition,
         * layoutParams.getFeederVertLineStart());
         * currentFeederLine.setPoint2(feederPosition,
         * layoutParams.getFeederVertLineStop() + (layoutParams.getHeight() /
         * 20)); currentFeederLine.setLineArrow(LineElement.ARROW_END);
         * currentFeederLine.setLineColor(Color.YELLOW);
         * graph.add(currentFeederLine); // feeder point data start
         * labelTextHorzLeft = feederPosition +
         * layoutParams.getLabelTextHorzOffset(); valueTextHorzLeft =
         * feederPosition + layoutParams.getValueTextHorzOffset(); textVertUpper =
         * layoutParams.getSubLineLevel() +
         * layoutParams.getLabelTextVertOffset(); if
         * (currentFeeder.getPowerFactorPointID().intValue() > 0) { StaticText
         * feederPFLabel = new StaticText();
         * feederPFLabel.setX(labelTextHorzLeft);
         * feederPFLabel.setY(textVertUpper);
         * feederPFLabel.setFont(DEFAULT_FONT);
         * feederPFLabel.setPaint(Color.GREEN); feederPFLabel.setText("PF:");
         * graph.add(feederPFLabel); DynamicText feederPFValue = new
         * DynamicText(); feederPFValue.setX(valueTextHorzLeft);
         * feederPFValue.setY(textVertUpper);
         * feederPFValue.setPointID(currentFeeder.getPowerFactorPointID()
         * .intValue()); feederPFValue.setDisplayAttribs(PointAttributes.VALUE);
         * feederPFValue.setFont(DEFAULT_FONT);
         * feederPFValue.setPaint(Color.GREEN); graph.add(feederPFValue);
         * textVertUpper += layoutParams.getLabelTextVertOffset(); } if
         * (currentFeeder.getEstimatedPowerFactorPointID().intValue() > 0) {
         * StaticText feederEstPFLabel = new StaticText();
         * feederEstPFLabel.setX(labelTextHorzLeft);
         * feederEstPFLabel.setY(textVertUpper);
         * feederEstPFLabel.setFont(DEFAULT_FONT);
         * feederEstPFLabel.setPaint(Color.GREEN); feederEstPFLabel.setText("Est
         * PF:"); graph.add(feederEstPFLabel); DynamicText feederEstPFValue =
         * new DynamicText(); feederEstPFValue.setX(valueTextHorzLeft);
         * feederEstPFValue.setY(textVertUpper);
         * feederEstPFValue.setPointID(currentFeeder.getEstimatedPowerFactorPointID()
         * .intValue());
         * feederEstPFValue.setDisplayAttribs(PointAttributes.VALUE);
         * feederEstPFValue.setFont(DEFAULT_FONT);
         * feederEstPFValue.setPaint(Color.GREEN); graph.add(feederEstPFValue);
         * textVertUpper += layoutParams.getLabelTextVertOffset(); } if
         * (currentFeeder.getCurrentVarLoadPointID().intValue() > 0) {
         * StaticText feederKVARLabel = new StaticText();
         * feederKVARLabel.setX(labelTextHorzLeft);
         * feederKVARLabel.setY(textVertUpper);
         * feederKVARLabel.setFont(DEFAULT_FONT);
         * feederKVARLabel.setPaint(Color.GREEN);
         * feederKVARLabel.setText("KVAR:"); graph.add(feederKVARLabel);
         * DynamicText feederKVARValue = new DynamicText();
         * feederKVARValue.setX(valueTextHorzLeft);
         * feederKVARValue.setY(textVertUpper);
         * feederKVARValue.setPointID(currentFeeder.getCurrentVarLoadPointID()
         * .intValue());
         * feederKVARValue.setDisplayAttribs(PointAttributes.VALUE);
         * feederKVARValue.setFont(DEFAULT_FONT);
         * feederKVARValue.setPaint(Color.GREEN); graph.add(feederKVARValue);
         * textVertUpper += layoutParams.getLabelTextVertOffset(); } if
         * (currentFeeder.getEstimatedVarLoadPointID().intValue() > 0) {
         * StaticText feederEstKVARLabel = new StaticText();
         * feederEstKVARLabel.setX(labelTextHorzLeft);
         * feederEstKVARLabel.setY(textVertUpper);
         * feederEstKVARLabel.setFont(DEFAULT_FONT);
         * feederEstKVARLabel.setPaint(Color.GREEN);
         * feederEstKVARLabel.setText("Est KVAR:");
         * graph.add(feederEstKVARLabel); DynamicText feederEstKVARValue = new
         * DynamicText(); feederEstKVARValue.setX(valueTextHorzLeft);
         * feederEstKVARValue.setY(textVertUpper);
         * feederEstKVARValue.setPointID(currentFeeder.getEstimatedVarLoadPointID()
         * .intValue());
         * feederEstKVARValue.setDisplayAttribs(PointAttributes.VALUE);
         * feederEstKVARValue.setFont(DEFAULT_FONT);
         * feederEstKVARValue.setPaint(Color.GREEN);
         * graph.add(feederEstKVARValue); textVertUpper +=
         * layoutParams.getLabelTextVertOffset(); } if
         * (currentFeeder.getCurrentWattLoadPointID().intValue() > 0) {
         * StaticText feederKWLabel = new StaticText();
         * feederKWLabel.setX(labelTextHorzLeft);
         * feederKWLabel.setY(textVertUpper);
         * feederKWLabel.setFont(DEFAULT_FONT);
         * feederKWLabel.setPaint(Color.GREEN); feederKWLabel.setText("KW:");
         * graph.add(feederKWLabel); DynamicText feederKWValue = new
         * DynamicText(); feederKWValue.setX(valueTextHorzLeft);
         * feederKWValue.setY(textVertUpper);
         * feederKWValue.setPointID(currentFeeder.getCurrentWattLoadPointID()
         * .intValue()); feederKWValue.setDisplayAttribs(PointAttributes.VALUE);
         * feederKWValue.setFont(DEFAULT_FONT);
         * feederKWValue.setPaint(Color.GREEN); graph.add(feederKWValue);
         * textVertUpper += layoutParams.getLabelTextVertOffset(); } if
         * (currentFeeder.getDailyOperationsAnalogPointID().intValue() > 0) {
         * StaticText feederDailyOpsLabel = new StaticText();
         * feederDailyOpsLabel.setX(labelTextHorzLeft);
         * feederDailyOpsLabel.setY(textVertUpper);
         * feederDailyOpsLabel.setFont(DEFAULT_FONT);
         * feederDailyOpsLabel.setPaint(Color.GREEN);
         * feederDailyOpsLabel.setText("Ops:"); graph.add(feederDailyOpsLabel);
         * DynamicText feederDailyOpsValue = new DynamicText();
         * feederDailyOpsValue.setX(valueTextHorzLeft);
         * feederDailyOpsValue.setY(textVertUpper);
         * feederDailyOpsValue.setPointID(currentFeeder.getDailyOperationsAnalogPointID()
         * .intValue());
         * feederDailyOpsValue.setDisplayAttribs(PointAttributes.VALUE);
         * feederDailyOpsValue.setFont(DEFAULT_FONT);
         * feederDailyOpsValue.setPaint(Color.GREEN);
         * graph.add(feederDailyOpsValue); textVertUpper +=
         * layoutParams.getLabelTextVertOffset(); }
         */

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
        drawing.getDrawing().getLxGraph().add(feederLn);

    }

    private LineElement createFeederLn(double fdrYCoord, double currFdrX) {
        if (feederLn == null) {
            feederLn = new DynamicLineElement(this, new DynamicLineState());
            feederLn.setPoint1(currFdrX, fdrYCoord);
            feederLn.setPoint2(LINE_LENGTH, fdrYCoord);
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

}
