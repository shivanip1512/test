package com.cannontech.cbc.oneline.model.cap;

import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableTextList;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineCap implements OnelineObject {
    private static final String GROUND = "Ground.gif";

    private static final String CAPACITOR = "Capacitor.gif";

    public static final String NONE = "X.gif";

    public static final String NAME_PREFIX = "CapBank_";

    // private static final Font DEFAULT_FONT = new java.awt.Font("arial",
    // java.awt.Font.BOLD,
    // 12);

    public OneLineDrawing drawing = null;
    private SubBus subBusMsg = null;
    private int currentCapIdx = -1;
    private Integer paoId;
    private String name;

    private CapControlPanel controlPanel;
    private int currFdrIndex;
    private StateImage stateImage;
    private UpdatableTextList bankSize = new UpdatableTextList();
    private UpdatableTextList opcount = new UpdatableTextList();

    public void draw() {

        currFdrIndex = drawing.getFeeders().size() - 1;
        OnelineFeeder f = drawing.getFeeders().get(currFdrIndex);
        double nameX = f.getFeederName().getX();
        int nameWidth = OnelineUtil.getFeederOffset(f.getFeederName().getText());
        double nameOffset = nameX + nameWidth;
        int twenty = 20;

        stateImage = new StateImage();
        Feeder feeder = (Feeder) subBusMsg.getCcFeeders().get(currFdrIndex);
        CapBankDevice cap = (CapBankDevice) feeder.getCcCapBanks()
                                                  .get(currentCapIdx);
        stateImage.setPointID(cap.getStatusPointID().intValue());
        double imgWidth = OnelineUtil.CAP_IMG_WIDTH;
        double initialCapXPos = nameOffset + twenty;
        double imgXPos = initialCapXPos + (OnelineUtil.PXLS_PER_CAPBANK * currentCapIdx);
        stateImage.setX(imgXPos);
        stateImage.setName(getName());
        double xImgYPos = f.getFeederLn().getY() - imgWidth / 2;
        stateImage.setCenterY(xImgYPos);

        StaticImage capacImg = new StaticImage();
        capacImg.setYukonImage(CAPACITOR);

        StaticImage grdImg = new StaticImage();
        grdImg.setYukonImage(GROUND);

        capacImg.setX(imgXPos);
        grdImg.setX(imgXPos);

        double capImgYPos = xImgYPos + imgWidth;
        capacImg.setY(capImgYPos);
        double grdImgYPos = capImgYPos + imgWidth;
        grdImg.setY(grdImgYPos);

        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(stateImage);
        graph.add(capacImg);
        graph.add(grdImg);

        addBankSize(graph);
        addOpcount(graph);
    }

    private void addBankSize(LxGraph graph) {

        String strLabel = "Bank Size: ";
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                   OnelineUtil.getStartPoint(getStateImage()),
                                                   new Integer ((int)getStateImage().getWidth() + 25),
                                                   new Integer ((int)getStateImage().getHeight() + 20));

        StaticText size = OnelineUtil.createTextElement(getStreamable().getBankSize() + "",
                                                       OnelineUtil.getStartPoint(label),
                                                       new Integer((int) label.getWidth() + 10),
                                                       null);
        size.setName("CapStat_" + getStreamable().getCcId() + "_SIZE");
        graph.add(label);
        graph.add(size);
        bankSize.setFirstElement(label);
        bankSize.setLastElement(size);
    }
    private void addOpcount(LxGraph graph) {
        String strLabel = "Opcount: ";
        LxAbstractText firstElement = getBankSize().getFirstElement();
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                   OnelineUtil.getStartPoint(firstElement),
                                                   null,
                                                   new Integer ((int)getBankSize().getFirstElement().getHeight() + 10));

        StaticText cnt = OnelineUtil.createTextElement(getStreamable().getTotalOperations() + "",
                                                       OnelineUtil.getStartPoint(label),
                                                       new Integer((int) label.getWidth() + 10),
                                                       null);
        cnt.setName("CapStat_" + getStreamable().getCcId() + "_CNT");
        graph.add(label);
        graph.add(cnt);
        opcount.setFirstElement(label);
        opcount.setLastElement(cnt);
    }



    public UpdatableTextList getOpcount() {

        return opcount;
    }

    public UpdatableTextList getBankSize() {
        return bankSize;
    }

    public OnelineCap(SubBus subBusMessage) {
        subBusMsg = subBusMessage;
    }

    public void addDrawing(OneLineDrawing d) {
        drawing = d;
        setPaoId(getCurrentCapIdFromMessage());
        setName(createCapName());
        draw();
        controlPanel = new CapControlPanel(this);

    }

    public void setName(String n) {
        name = n;
    }

    public String createCapName() {
        return NAME_PREFIX + getPaoId();
    }

    public void setPaoId(Integer currentCapIdFromMessage) {
        paoId = currentCapIdFromMessage;
    }

    public Integer getCurrentCapIdFromMessage() {
        int currFdrIndex = drawing.getFeeders().size() - 1;
        Feeder f = (Feeder) subBusMsg.getCcFeeders().get(currFdrIndex);
        CapBankDevice c = (CapBankDevice) f.getCcCapBanks().get(currentCapIdx);
        return c.getCcId();
    }

    public int getCurrentCapIdx() {
        return currentCapIdx;
    }

    public void setCurrentCapIdx(int currentCapIdx) {
        this.currentCapIdx = currentCapIdx;
    }

    public Integer getPaoId() {
        return paoId;
    }

    public String getName() {
        return name;
    }

    public OneLineDrawing getDrawing() {
        return drawing;
    }

    public LxLine getRefLnAbove() {
        OnelineSub s = drawing.getSub();
        return s.getRefLnAbove();
    }

    public LxLine getRefLnBelow() {
        OnelineSub s = drawing.getSub();
        return s.getRefLnBelow();
    }

    public SubBus getSubBusMsg() {
        return subBusMsg;
    }

    public CapControlPanel getControlPanel() {
        return controlPanel;
    }

    public CapBankDevice getStreamable() {
        Feeder feeder = (Feeder) subBusMsg.getCcFeeders().get(currFdrIndex);
        CapBankDevice cap = (CapBankDevice) feeder.getCcCapBanks()
                                                  .get(currentCapIdx);
        return cap;
    }

    public LxComponent getStateImage() {
        return stateImage;
    }

}
