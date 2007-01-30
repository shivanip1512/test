package com.cannontech.cbc.oneline.model.sub;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.elements.SubDynamicImage;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableTextList;
import com.cannontech.cbc.oneline.states.DynamicLineState;
import com.cannontech.cbc.oneline.states.SubImgState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineSub implements OnelineObject {
    public static final String SUB_TRANSFORMER_IMG = "sub_TransformerImg";
    private static final String TARGET = "Target: ";
    private static final String VARLOAD = "VarLoad/Est: ";
    private static final String POWERFACTOR = "PF/Est: ";
    private static final String WATTVOLTLOAD = "Watts/Volts: ";
    private static final String DAYOP = "Daily/Max Ops: ";
    private static final String CLOSED_TRIP = "Closed/Tripped KVars: ";
    // elements
    public OneLineDrawing drawing = null;
    private SubBus subBusMsg = null;
    private StaticText name = null;
    private SubDynamicImage transformerImg = null;
    private DynamicLineElement injectionLine;
    private SubControlPanel controlPanel = null;
    private DynamicLineElement distributionLn;
    public Integer paoId;

    private UpdatableTextList target = new UpdatableTextList();
    private UpdatableTextList varLoad = new UpdatableTextList();
    private UpdatableTextList powerFactor = new UpdatableTextList();
    private UpdatableTextList wattVoltLoad = new UpdatableTextList();
    private UpdatableTextList dayOp = new UpdatableTextList();
    private UpdatableTextList closedTripedVar = new UpdatableTextList();
    //use this because it contains stat functions we need
    //later they should be migrated into a shared package
    private CBCDisplay oldWebDisplay = new CBCDisplay();

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
        LxGraph graph = d.getLxGraph();

        getTransformerImg().setPointID(-100000000);
        graph.add(getTransformerImg());
        graph.add(getName());

        graph.add(getInjectionLine());
        graph.add(getDistributionLn());


        addTarget(graph);
        addVarLoad(graph);
        addPowerFactor(graph);
        addWattVoltLoad(graph);
        addDayOp(graph);
        addClosedTripedVar(graph);
    }

    private void addClosedTripedVar(LxGraph graph) {

        String strLabel = CLOSED_TRIP;
        LxAbstractText firstElement = getDayOp().getFirstElement();
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

    private void addDayOp(LxGraph graph) {
        String strLabel = DAYOP;
        LxAbstractText firstElement = getWattVoltLoad().getFirstElement();
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

        dayOpVal.setName("SubStat_" + subBusMsg.getCcId() + "_DAYOP");
        graph.add(label);
        graph.add(dayOpVal);
        dayOp.setFirstElement(label);
        dayOp.setLastElement(dayOpVal);
    
    }

    private void addWattVoltLoad(LxGraph graph) {
        String strLabel = WATTVOLTLOAD;
       
        LxAbstractText firstElement = getPowerFactor().getFirstElement();
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

    private void addPowerFactor(LxGraph graph) {



        String strLabel = POWERFACTOR;
       
        LxAbstractText firstElement = getVarLoad().getFirstElement();
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

    private void addVarLoad(LxGraph graph) {


        String strLabel = VARLOAD;
       
        LxAbstractText firstElement = getTarget().getFirstElement();
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

    private void addTarget(LxGraph graph) {

        String strLabel = TARGET;
        
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         OnelineUtil.getStartPoint(getName()),
                                                         null,
                                                         new Integer((int) getName().getHeight() + 10));

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

    public LineElement getInjectionLine() {
        if (injectionLine == null) {
            injectionLine = new DynamicLineElement(this, new DynamicLineState());
            Color c = new Color(1, 188, 255);
            injectionLine.setLineColor(c);
            double transImgWidth = OnelineUtil.SUB_IMG_WIDTH;
            double injLineX = 20 + transImgWidth;
            int transImgHeight = OnelineUtil.SUB_IMG_HEIGHT;
            double injLineY = transImgHeight / 2 + getTransformerImg().getCenterY();
            injectionLine.setPoint1(injLineX, injLineY);
            String subName = getName().getText();
            double lnLength = OnelineUtil.getInjLineLength(subName);
            injectionLine.setPoint2(injLineX + lnLength,
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
            StaticImage ccLogo = drawing.getLogos().getCcLogo();
            double ccLogoX = ccLogo.getX();
            name.setX(ccLogoX);
            name.setY(ccLogo.getY() + ccLogo.getHeight() + 10);
        }
        return name;
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
        double lnY = drawing.getLayoutParams().getHeight() - 300;
        ln.setPoint1(injLnPt1.getX(), lnY);
        ln.setPoint2(injLnPt1.getX() + 800, lnY);
        ln.setLineColor(Color.YELLOW);
        drawing.getDrawing().getLxGraph().add(ln);
        return ln;
    }

    public LxLine getRefLnAbove() {
        LxLine ln = new LxLine();
        StaticImage ccLogo = drawing.getLogos().getCcLogo();
        double lnY = ccLogo.getY() + ccLogo.getHeight() + 10;
        ln.setPoint1(0, lnY);
        ln.setPoint2(drawing.getLayoutParams().getWidth(), lnY);
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
            double btwLines = fdrOffset * (numOfFdr - 1) + refYAbove;
            double injY = getInjectionLine().getPoint2().getY();
            double fdrYCoord = Math.max(btwLines, injY);
            distributionLn = new DynamicLineElement(this,
                                                    new DynamicLineState());
            double subInjLnY = injY;
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

    public UpdatableTextList getClosedTripedVar() {
        return closedTripedVar;
    }

    public UpdatableTextList getDayOp() {
        return dayOp;
    }

    public UpdatableTextList getPowerFactor() {
        return powerFactor;
    }

    public UpdatableTextList getTarget() {
        return target;
    }

    public UpdatableTextList getVarLoad() {
        return varLoad;
    }

    public UpdatableTextList getWattVoltLoad() {
        return wattVoltLoad;
    }

}
