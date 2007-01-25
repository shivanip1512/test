package com.cannontech.cbc.oneline.model.cap;

import java.util.Vector;

import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.PaoDaoImpl;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.util.ESubDrawingUpdater;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineCap implements OnelineObject {
    private static final String GROUND = "Ground.gif";

    private static final String CAPACITOR = "Capacitor.gif";

    public static final String NONE = "X.gif";

    public static final String NAME_PREFIX = "CapBank_";
    
    //private static final Font DEFAULT_FONT = new java.awt.Font("arial",
    //                                                           java.awt.Font.BOLD,
    //                                                           12);
    //private static String DEF_CONTROLS_HOME = "../capcontrols.jsp";

    public OneLineDrawing drawing = null;
    private SubBus subBusMsg = null;
    private int currentCapIdx = -1;
    private Integer paoId;
    private String name;

    private CapControlPanel controlPanel;

    private int currFdrIndex;

    public void draw() {

        currFdrIndex = drawing.getFeeders().size() - 1;
        OnelineFeeder f = drawing.getFeeders().get(currFdrIndex);
        double lineLength = OnelineFeeder.LINE_LENGTH;
        double nameOffset = f.getFeederName().getX() + f.getFeederName()
                                                        .getWidth();
        int twenty = 20;
        double remainLength = lineLength - (nameOffset + (twenty * 2));

        Vector feederVector = subBusMsg.getCcFeeders();
        Feeder currentFeeder = (Feeder) feederVector.get(currFdrIndex);
        Vector caps = currentFeeder.getCcCapBanks();

        StateImage xImage = new StateImage();
        //xImage.setYukonImage(NONE);
        StaticImage capacImg = new StaticImage();
        capacImg.setYukonImage(CAPACITOR);

        StaticImage grdImg = new StaticImage();
        grdImg.setYukonImage(GROUND);

        double imgWidth = OnelineUtil.CAP_IMG_WIDTH;
        int capNum = caps.size();
        int numOfSpacesBtwCaps = capNum - 1;
        double btwCapLength = (remainLength - (capNum * imgWidth)) / (numOfSpacesBtwCaps);
        double initialCapXPos = nameOffset + twenty;
        double imgXPos = initialCapXPos + ((btwCapLength + imgWidth) * currentCapIdx);
        Feeder feeder = (Feeder )subBusMsg.getCcFeeders().get(currFdrIndex);
        CapBankDevice cap = (CapBankDevice) feeder.getCcCapBanks().get(currentCapIdx);
        
        xImage.setPointID(cap.getStatusPointID().intValue());

        xImage.setX(imgXPos);
        capacImg.setX(imgXPos);
        grdImg.setX(imgXPos);

        double xImgYPos = f.getFeederLn().getY() - imgWidth / 2;
        xImage.setCenterY(xImgYPos);
        double capImgYPos = xImgYPos + imgWidth;
        capacImg.setY(capImgYPos);
        double grdImgYPos = capImgYPos + imgWidth;
        grdImg.setY(grdImgYPos);

        xImage.setName(getName());

        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(xImage);
        graph.add(capacImg);
        graph.add(grdImg);

        /*
         if (feederVector.size() == 1) {
         feederSpacing = 0.0;
         }

         double feederPosition = (layoutParams.getFeederHorzLineStart() + (feederSpacing * currFdrIndex));
         double capBankNameHorzOffset = layoutParams.getWidth() / 68.2666;
         double capBankNameVertOffset = layoutParams.getHeight() / 160;
         Vector capBankVector = currentFeeder.getCcCapBanks();
         if (capBankVector.size() > 1) {// make sure we don't try to divide by
         // zero
         double capBanksStart = layoutParams.getFeederVertLineStart() + (layoutParams.getHeight() / 5.7143);
         double capBankSpacing = (layoutParams.getFeederVertLineStop() - capBanksStart) / (capBankVector.size() - 1);

         double capBankPosition = capBanksStart + (capBankSpacing * j);
         CapBankDevice currentCapBank = (CapBankDevice) capBankVector.get(j);

         LineElement capacitorAndGroundLine = new LineElement();
         capacitorAndGroundLine.setPoint1(feederPosition, capBankPosition);
         capacitorAndGroundLine.setPoint2(feederPosition - (layoutParams.getWidth() / 34.1333),
         capBankPosition);
         capacitorAndGroundLine.setLineColor(Color.YELLOW);
         graph.add(capacitorAndGroundLine);

         StaticImage capacitorImage = new StaticImage();
         capacitorImage.setYukonImage("Capacitor.gif");
         capacitorImage.setCenter(feederPosition - (layoutParams.getWidth() / 34.1333),
         capBankPosition + (layoutParams.getHeight() / 80));
         graph.add(capacitorImage);

         StaticImage groundImage = new StaticImage();
         groundImage.setYukonImage("Ground.gif");
         groundImage.setCenter(feederPosition - (layoutParams.getWidth() / 34.1333),
         capBankPosition + (layoutParams.getHeight() / 26.6666));
         graph.add(groundImage);

         StateImage stateImage = new StateImage();

         stateImage.setPointID(currentCapBank.getStatusPointID().intValue());
         graph.add(stateImage);
         updater.updateDrawing();
         stateImage.setCenter(feederPosition, capBankPosition);
         stateImage.setLinkTo(DEF_CONTROLS_HOME + "?paoID=" + currentCapBank.getCcId() + "&lastSubID=" + subBusMsg.getCcId() + "&controlType=" + CapControlWebAnnex.CMD_CAPBANK + "&redirectURL=" + fileName);
         StaticText capBankNameString = new StaticText();
         capBankNameString.setX(feederPosition + capBankNameHorzOffset);
         capBankNameString.setY(capBankPosition - capBankNameVertOffset - (layoutParams.getHeight() / 80));
         capBankNameString.setFont(DEFAULT_FONT);
         capBankNameString.setPaint(Color.LIGHT_GRAY);
         capBankNameString.setText(currentCapBank.getCcName());
         graph.add(capBankNameString);

         StaticText capBankSizeString = new StaticText();
         capBankSizeString.setX(feederPosition + capBankNameHorzOffset);
         capBankSizeString.setY(capBankPosition + capBankNameVertOffset);
         capBankSizeString.setFont(DEFAULT_FONT);
         capBankSizeString.setPaint(Color.LIGHT_GRAY);
         capBankSizeString.setText("Size: " + Integer.toString(currentCapBank.getBankSize()
         .intValue()) + " KVAR");
         graph.add(capBankSizeString);
         } else if (capBankVector.size() == 1) {
         double capBankPosition = layoutParams.getFeederVertLineStop();
         CapBankDevice currentCapBank = (CapBankDevice) capBankVector.get(0);

         LineElement capacitorAndGroundLine = new LineElement();
         capacitorAndGroundLine.setPoint1(feederPosition, capBankPosition);
         capacitorAndGroundLine.setPoint2(feederPosition - (layoutParams.getWidth() / 34.1333),
         capBankPosition);
         capacitorAndGroundLine.setLineColor(Color.YELLOW);
         graph.add(capacitorAndGroundLine);

         StaticImage capacitorImage = new StaticImage();
         capacitorImage.setYukonImage("Capacitor.gif");
         capacitorImage.setCenter(feederPosition - (layoutParams.getWidth() / 34.1333),
         capBankPosition + (layoutParams.getHeight() / 80));
         graph.add(capacitorImage);

         StaticImage groundImage = new StaticImage();
         groundImage.setYukonImage("Ground.gif");
         groundImage.setCenter(feederPosition - (layoutParams.getWidth() / 34.1333),
         capBankPosition + (layoutParams.getHeight() / 26.666));
         graph.add(groundImage);

         StateImage stateImage = new StateImage();
         stateImage.setPointID(currentCapBank.getStatusPointID().intValue());
         graph.add(stateImage);
         updater.updateDrawing();
         stateImage.setCenter(feederPosition, capBankPosition);
         stateImage.setLinkTo(DEF_CONTROLS_HOME + "?paoID=" + currentCapBank.getCcId() + "&lastSubID=" + subBusMsg.getCcId() + "&controlType=" + CapControlWebAnnex.CMD_CAPBANK + "&redirectURL=" + fileName);

         StaticText capBankNameString = new StaticText();
         capBankNameString.setX(feederPosition + capBankNameHorzOffset);
         capBankNameString.setY(capBankPosition - capBankNameVertOffset - (layoutParams.getHeight() / 80));
         capBankNameString.setFont(DEFAULT_FONT);
         capBankNameString.setPaint(Color.LIGHT_GRAY);
         capBankNameString.setText(currentCapBank.getCcName());
         graph.add(capBankNameString);

         StaticText capBankSizeString = new StaticText();
         capBankSizeString.setX(feederPosition + capBankNameHorzOffset);
         capBankSizeString.setY(capBankPosition + capBankNameVertOffset);
         capBankSizeString.setFont(DEFAULT_FONT);
         capBankSizeString.setPaint(Color.LIGHT_GRAY);
         capBankSizeString.setText("Size: " + Integer.toString(currentCapBank.getBankSize()
         .intValue()) + " KVAR");
         graph.add(capBankSizeString);
         }
         */

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
        CapBankDevice cap = (CapBankDevice) feeder.getCcCapBanks().get(currentCapIdx);
        return cap; 
    }
}
