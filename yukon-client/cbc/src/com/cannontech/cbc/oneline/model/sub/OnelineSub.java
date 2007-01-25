package com.cannontech.cbc.oneline.model.sub;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.elements.SubDynamicImage;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.states.DynamicLineState;
import com.cannontech.cbc.oneline.states.SubImgState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.PointAttributes;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineSub implements OnelineObject {
    public static final String SUB_TRANSFORMER_IMG = "sub_TransformerImg";
    private static final Font DEFAULT_FONT = new java.awt.Font("arial",
                                                               java.awt.Font.BOLD,
                                                               12);
    // elements
    public OneLineDrawing drawing = null;
    private SubBus subBusMsg = null;

    private StaticText pfLabel = null;
    private DynamicText pfValue = null;

    private StaticText estPFLabel = null;
    private DynamicText estPFValue = null;

    private StaticText kVARLabel = null;
    private DynamicText kVARValue = null;

    private StaticText estKVARLabel = null;
    private DynamicText estKVARValue = null;

    private StaticText kWLabel = null;
    private DynamicText kWValue = null;

    private StaticText dailyOpsLabel = null;
    private DynamicText dailyOpsValue = null;

    private StaticText ctlMethod = null;
    private StaticText name = null;

    private SubDynamicImage transformerImg = null;
    private DynamicLineElement injectionLine;

    // other layout params
    private double labelTextHorzLeft = 0;
    private double valueTextHorzLeft = 0;
    private double textVertUpper = 0;

    private double infoHorzPosition = 0;
    private double infoVertOffset = 0;

    private SubControlPanel controlPanel = null;
    public Integer paoId;
    private DynamicLineElement distributionLn;

    public OnelineSub(SubBus subBusMessage) {
        subBusMsg = subBusMessage;
        paoId = subBusMsg.getCcId();
    }

    public void addDrawing(OneLineDrawing old) {
        drawing = old;
        draw();
        controlPanel = new SubControlPanel(this, drawing.getFileName());
    }

    public void draw() {
        Drawing d = drawing.getDrawing();
        OneLineParams layoutParams = drawing.getLayoutParams();
        LxGraph graph = d.getLxGraph();

        labelTextHorzLeft = layoutParams.getHalfAcross() + layoutParams.getLabelTextHorzOffset();
        valueTextHorzLeft = layoutParams.getHalfAcross() + layoutParams.getValueTextHorzOffset();
        textVertUpper = layoutParams.getSubLineStart() * 1.1;

        infoHorzPosition = layoutParams.getFeederHorzLineStart() + ((layoutParams.getHalfAcross() - layoutParams.getFeederHorzLineStart()) / 2);
        infoVertOffset = (layoutParams.getHeight() / 26.666);

        getTransformerImg().setPointID(-100000000);
        graph.add(getTransformerImg());

        graph.add(getInjectionLine());
        graph.add(getDistributionLn());

        if (subBusMsg.getPowerFactorPointId().intValue() > 0) {
            graph.add(getPfLabel());
            graph.add(getPfValue());
            textVertUpper += layoutParams.getLabelTextVertOffset();
        }

        if (subBusMsg.getEstimatedPowerFactorPointId().intValue() > 0) {
            graph.add(getEstPFLabel());
            graph.add(getEstPFValue());
            textVertUpper += layoutParams.getLabelTextVertOffset();
        }

        if (subBusMsg.getCurrentVarLoadPointID().intValue() > 0) {
            graph.add(getKVARLabel());
            graph.add(getKVARValue());
            // textVertUpper += layoutParams.getLabelTextVertOffset();
        }

        /*
         * if (subBusMsg.getEstimatedVarLoadPointID().intValue() > 0) {
         * graph.add(getEstKVARLabel()); graph.add(getEstKVARValue());
         * textVertUpper += layoutParams.getLabelTextVertOffset(); }
         */
        if (subBusMsg.getCurrentWattLoadPointID().intValue() > 0) {
            // graph.add(getKWLabel());
            // graph.add(getKWValue());
            textVertUpper += layoutParams.getLabelTextVertOffset();
        }

        if (subBusMsg.getDailyOperationsAnalogPointId().intValue() > 0) {
            graph.add(getDailyOpsLabel());
            graph.add(getDailyOpsValue());
            textVertUpper += layoutParams.getLabelTextVertOffset();
        }

        // graph.add(getCtlMethod());
        graph.add(getName());

    }

    public LineElement getInjectionLine() {
        if (injectionLine == null) {
            injectionLine = new DynamicLineElement(this, new DynamicLineState());
            Color c = new Color(1, 188, 255);
            injectionLine.setLineColor(c);
            OneLineParams layoutParams = drawing.getLayoutParams();
            double transImgWidth = OnelineUtil.SUB_IMG_WIDTH;
            double injLineX = 20 + transImgWidth;
            int transImgHeight = OnelineUtil.SUB_IMG_HEIGHT;
            double injLineY = transImgHeight / 2 + getTransformerImg().getCenterY();
            injectionLine.setPoint1(injLineX, injLineY);
            injectionLine.setPoint2(injLineX + layoutParams.getSubLineLength(),
                                    injLineY);
            injectionLine.setName("Sub_Injection_" + getPaoId());
        }
        return injectionLine;
    }

    private SubDynamicImage getTransformerImg() {
        if (transformerImg == null) {
            transformerImg = new SubDynamicImage(subBusMsg, new SubImgState());
            OneLineParams layoutParams = drawing.getLayoutParams();
            transformerImg.setCenter(20, layoutParams.getHeight() / 3);
            transformerImg.setName(SUB_TRANSFORMER_IMG);

        }
        return transformerImg;
    }

    public StaticText getName() {
        if (name == null) {
            name = new StaticText();
            name.setFont(new java.awt.Font("arial", Font.BOLD, 16));
            name.setPaint(Color.WHITE);
            name.setText("Sub Bus: " + subBusMsg.getCcName());
            name.setX(infoHorzPosition);
            name.setY(drawing.getLayoutParams().getSubLineLevel() - infoVertOffset);
        }
        return name;
    }

    private StaticText getCtlMethod() {
        if (ctlMethod == null) {
            ctlMethod = new StaticText();
            ctlMethod.setFont(new Font("arial", Font.BOLD, 14));
            ctlMethod.setPaint(Color.WHITE);
            OneLineParams layoutParams = drawing.getLayoutParams();
            ctlMethod.setCenter(infoHorzPosition,
                                layoutParams.getSubLineLevel() - infoVertOffset);
        }
        return ctlMethod;
    }

    private DynamicText getDailyOpsValue() {
        if (dailyOpsValue == null) {
            dailyOpsValue = new DynamicText();
            dailyOpsValue.setX(valueTextHorzLeft);
            dailyOpsValue.setY(textVertUpper);
            dailyOpsValue.setPointID(subBusMsg.getDailyOperationsAnalogPointId()
                                              .intValue());
            dailyOpsValue.setDisplayAttribs(PointAttributes.VALUE);
            dailyOpsValue.setFont(DEFAULT_FONT);
            dailyOpsValue.setPaint(Color.GREEN);
        }
        return dailyOpsValue;
    }

    private StaticText getDailyOpsLabel() {
        if (dailyOpsLabel == null) {
            dailyOpsLabel = new StaticText();
            dailyOpsLabel.setX(labelTextHorzLeft);
            dailyOpsLabel.setY(textVertUpper);
            dailyOpsLabel.setFont(DEFAULT_FONT);
            dailyOpsLabel.setPaint(Color.GREEN);
            dailyOpsLabel.setText("Ops:");
        }
        return dailyOpsLabel;
    }

    private DynamicText getKWValue() {
        if (kWValue == null) {
            kWValue = new DynamicText();
            kWValue.setX(valueTextHorzLeft);
            kWValue.setY(textVertUpper);
            kWValue.setPointID(subBusMsg.getCurrentWattLoadPointID().intValue());
            kWValue.setDisplayAttribs(PointAttributes.VALUE);
            kWValue.setFont(DEFAULT_FONT);
            kWValue.setPaint(Color.GREEN);
        }
        return kWValue;
    }

    private StaticText getKWLabel() {
        if (kWLabel == null) {
            kWLabel = new StaticText();
            kWLabel.setX(labelTextHorzLeft);
            kWLabel.setY(textVertUpper);
            kWLabel.setFont(DEFAULT_FONT);
            kWLabel.setPaint(Color.GREEN);
            kWLabel.setText("KW:");
        }
        return kWLabel;
    }

    private DynamicText getEstKVARValue() {
        if (estKVARValue == null) {
            estKVARValue = new DynamicText();
            estKVARValue.setX(valueTextHorzLeft);
            estKVARValue.setY(textVertUpper);
            estKVARValue.setPointID(subBusMsg.getEstimatedVarLoadPointID()
                                             .intValue());
            estKVARValue.setDisplayAttribs(PointAttributes.VALUE);
            estKVARValue.setFont(DEFAULT_FONT);
            estKVARValue.setPaint(Color.GREEN);
        }
        return estKVARValue;
    }

    private StaticText getEstKVARLabel() {
        if (estKVARLabel == null) {
            estKVARLabel = new StaticText();
            estKVARLabel.setX(labelTextHorzLeft);
            estKVARLabel.setY(textVertUpper);
            estKVARLabel.setFont(DEFAULT_FONT);
            estKVARLabel.setPaint(Color.GREEN);
            estKVARLabel.setText("Est KVAR:");
        }
        return estKVARLabel;
    }

    private DynamicText getKVARValue() {
        if (kVARValue == null) {
            kVARValue = new DynamicText();
            kVARValue.setX(valueTextHorzLeft);
            kVARValue.setY(textVertUpper);
            kVARValue.setPointID(subBusMsg.getCurrentVarLoadPointID()
                                          .intValue());
            kVARValue.setDisplayAttribs(PointAttributes.VALUE);
            kVARValue.setFont(DEFAULT_FONT);
            kVARValue.setPaint(Color.GREEN);
        }
        return kVARValue;
    }

    private StaticText getKVARLabel() {
        if (kVARLabel == null) {
            kVARLabel = new StaticText();
            kVARLabel.setX(labelTextHorzLeft);
            kVARLabel.setY(textVertUpper);
            kVARLabel.setFont(DEFAULT_FONT);
            kVARLabel.setPaint(Color.GREEN);
            kVARLabel.setText("KVAR:");
        }
        return kVARLabel;
    }

    public StaticText getPfLabel() {
        if (pfLabel == null) {
            pfLabel = new StaticText();
            pfLabel.setX(labelTextHorzLeft);
            pfLabel.setY(textVertUpper);
            pfLabel.setFont(DEFAULT_FONT);
            pfLabel.setPaint(Color.GREEN);
            pfLabel.setText("PF:");
        }
        return pfLabel;
    }

    public DynamicText getPfValue() {
        if (pfValue == null) {
            pfValue = new DynamicText();
            pfValue.setX(valueTextHorzLeft);
            pfValue.setY(textVertUpper);
            pfValue.setPointID(subBusMsg.getPowerFactorPointId().intValue());
            pfValue.setDisplayAttribs(PointAttributes.VALUE);
            pfValue.setFont(DEFAULT_FONT);
            pfValue.setPaint(Color.GREEN);
        }
        return pfValue;
    }

    public StaticText getEstPFLabel() {
        if (estPFLabel == null) {
            estPFLabel = new StaticText();
            estPFLabel.setX(labelTextHorzLeft);
            estPFLabel.setY(textVertUpper);
            estPFLabel.setFont(DEFAULT_FONT);
            estPFLabel.setPaint(Color.GREEN);
            estPFLabel.setText("Est PF:");
        }
        return estPFLabel;
    }

    public DynamicText getEstPFValue() {
        if (estPFValue == null) {
            estPFValue = new DynamicText();
            estPFValue.setX(valueTextHorzLeft);
            estPFValue.setY(textVertUpper);
            estPFValue.setPointID(subBusMsg.getEstimatedPowerFactorPointId()
                                           .intValue());
            estPFValue.setDisplayAttribs(PointAttributes.VALUE);
            estPFValue.setFont(DEFAULT_FONT);
            estPFValue.setPaint(Color.GREEN);
        }
        return estPFValue;
    }

    public OneLineDrawing getDrawing() {
        return drawing;
    }

    public SubBus getSubBusMsg() {
        return subBusMsg;
    }

    public LxLine getRefLnBelow() {
        LxLine ln = new LxLine();
        Point2D injLnPt1 = getInjectionLine().getPoint1();
        ln.setPoint1(injLnPt1.getX(), injLnPt1.getY() + 200);
        ln.setPoint2(injLnPt1.getX() + 800, injLnPt1.getY() + 200);
        ln.setLineColor(Color.YELLOW);
        drawing.getDrawing().getLxGraph().add(ln);
        return ln;
    }

    public LxLine getRefLnAbove() {
        LxLine ln = new LxLine();
        ln.setPoint1(0, getName().getY() + 50);
        ln.setPoint2(drawing.getLayoutParams().getWidth(),
                     getName().getY() + 50);
        drawing.getDrawing().getLxGraph().add(ln);
        return ln;
    }

    public Integer getPaoId() {
        return paoId;
    }

    public SubControlPanel getControlPanel() {
        return controlPanel;
    }

    public DynamicLineElement getDistributionLn() {
        if (distributionLn == null) {
            int numOfFdr = subBusMsg.getCcFeeders().size();
            double refYBelow = getRefLnBelow().getPoint1().getY();
            double refYAbove = getRefLnAbove().getPoint1().getY();
            double fdrOffset = (refYBelow - refYAbove) / numOfFdr;
            double fdrYCoord = fdrOffset * (numOfFdr - 1) + refYAbove;
            distributionLn = new DynamicLineElement(this,
                                                    new DynamicLineState());
            double subInjLnY = getInjectionLine().getPoint2().getY();
            LineElement subInjLine = drawing.getSub().getInjectionLine();
            double distLnX = subInjLine.getPoint2().getX();
            distributionLn.setPoint1(distLnX, refYAbove);
            if (numOfFdr == 1)
                distributionLn.setPoint2(distLnX, subInjLnY);
            else
                distributionLn.setPoint2(distLnX, fdrYCoord);
            distributionLn.setLineColor(Color.YELLOW);
            distributionLn.setName("Sub_Distribution_" + getPaoId());
        }
        return distributionLn;
    }

}
