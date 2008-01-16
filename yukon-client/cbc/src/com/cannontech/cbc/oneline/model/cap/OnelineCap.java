package com.cannontech.cbc.oneline.model.cap;

import java.util.List;
import java.util.Map;

import com.cannontech.cbc.oneline.CommandPopups;
import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.cbc.oneline.states.DynamicLineState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineCap implements OnelineObject {

    public static final String NONE = "X.gif";

    public static final String NAME_PREFIX = "CapBank_";

    private Map<Integer, List<LitePoint>> pointCache;
    
    public OneLineDrawing drawing = null;
    private SubBus subBusMsg = null;
    private int currentCapIdx = -1;
    private Integer paoId;
    private String name;
    private int currFdrIndex;
    private StateImage stateImage;

    private UpdatableTextList tagInfo = new UpdatableTextList();

    private DynamicLineElement connectorLn;

    private StaticImage editorImage;

    private StaticImage infoImage;

    private StaticText capBankName;

    private StaticImage tmStmpImage;

    public void draw() {

        currFdrIndex = drawing.getFeeders().size() - 1;
        OnelineFeeder f = getParentFeeder();
        
        double nameX = f.getFeederName().getX();
        int nameWidth = OnelineUtil.getFeederOffset(f.getFeederName().getText());
        double nameOffset = nameX + nameWidth;
        int twenty = 20;
        
        double initialCapXPos = nameOffset + (twenty * 4);
        double xImgYPos = f.getFeederLn().getY();
        double imgXPos = initialCapXPos + (OnelineUtil.PXLS_PER_CAPBANK * currentCapIdx);
        LxGraph graph = drawing.getDrawing().getLxGraph();

        initConnector(xImgYPos, imgXPos);
        initStateImage(xImgYPos, imgXPos);
        initEditorImage();
        initInformationImage();
        
        if (CBCUtils.isTwoWay(PAOGroups.getDeviceType(getStreamable().getControlDeviceType()))) {
            initPointTimestampsImage();
        }

        initCapBankName ();
        graph.add(stateImage);
        graph.add(connectorLn);
        graph.add(editorImage);
        graph.add(infoImage);
        graph.add(capBankName);
        
        if (tmStmpImage != null)
            graph.add(tmStmpImage);
            
        UpdatableStats capStats = new CapBankUpdatableStats(graph, this);
        capStats.draw();
        HiddenStates hiddenStates = new CapBankHiddenStates(graph, this);
        CapBankTagView tagView = new CapBankTagView(graph, this, hiddenStates);
        HiddenStates capBankInfo = new CapBankAdditionalInfo (graph, this);
        hiddenStates.draw();
        capBankInfo.draw();
        tagView.draw();

    }



    private void initPointTimestampsImage() {
        tmStmpImage = new StaticImage();
        tmStmpImage.setYukonImage(OnelineUtil.IMG_PTTIMESTAMP);
        tmStmpImage.setX(infoImage.getX());
        tmStmpImage.setY(infoImage.getY() + 30);
        tmStmpImage.setLinkTo("javascript:void(0)");
        tmStmpImage.setName(CommandPopups.CAP_TMSTMP + "_" + getStreamable().getControlDeviceID());
    }



    private void initCapBankName() {
        capBankName = new StaticText();
        capBankName.setFont(OnelineUtil.MEDIUM_FONT);
        
        capBankName.setX(getStateImage().getX() + 20);
        capBankName.setY(getStateImage().getY() - 20);
        capBankName.setText(getStreamable().getCcName());
        capBankName.setName(getName());
        
        if( !getStreamable().isBankMoved() ){
            capBankName.setPaint(OnelineUtil.PURPLISH);
            capBankName.setLinkTo("/capcontrol/tempmoveWrapper.jsp?bankid=" + getStreamable().getCcId() + "&oneline=" + "true");
        }
        else{//if moved
            capBankName.setPaint(OnelineUtil.ORANGE);
            capBankName.setLinkTo("/capcontrol/oneline/OnelineCBCServlet?paoID=" + getStreamable().getCcId() + "&cmdID=11&controlType=CAPBANK_TYPE");
        }
       
    }



    private void initConnector(double xImgYPos, double imgXPos) {
        OnelineFeeder f = getParentFeeder();
        connectorLn = new DynamicLineElement(this, new DynamicLineState());
        connectorLn.setPoint1(imgXPos + 10, xImgYPos);
        connectorLn.setPoint2(imgXPos + 10, xImgYPos + 30);
        connectorLn.setName(f.getName());
    }



    private void initStateImage(double xImgYPos, double imgXPos) {
        stateImage = new StateImage();
        Feeder feeder = (Feeder) subBusMsg.getCcFeeders().get(currFdrIndex);
        CapBankDevice cap = (CapBankDevice) feeder.getCcCapBanks()
                                                  .get(currentCapIdx);
        stateImage.setPointID(cap.getStatusPointID().intValue());
        stateImage.setX(imgXPos);
        stateImage.setName(getName());
        stateImage.setCenterY(xImgYPos + 30);
        stateImage.setLinkTo("javascript:void(0)");
    }

    private void initInformationImage() {
        infoImage = new StaticImage();
        infoImage.setYukonImage(OnelineUtil.IMG_QUESTION);
        infoImage.setX(editorImage.getX()  - 20);
        infoImage.setY(editorImage.getY());
        infoImage.setLinkTo("javascript:void(0)");
        infoImage.setName(CommandPopups.CAP_INFO + "_" + getCurrentCapIdFromMessage());
    }


    private void initEditorImage() {
        editorImage = new StaticImage();
        String link = OnelineUtil.createBookmarkLink(getCurrentCapIdFromMessage().intValue());
        editorImage.setLinkTo(link);
        editorImage.setYukonImage(OnelineUtil.IMG_EDITOR);
        editorImage.setX(stateImage.getX() - 20);
        editorImage.setY(stateImage.getY());
    }



    public OnelineFeeder getParentFeeder() {
        return drawing.getFeeders().get(currFdrIndex);
    }

    public Boolean isDisabled() {
        return getStreamable().getCcDisableFlag();
    }

    public boolean isOVUVDisabled() {
        return getStreamable().getOvUVDisabled().booleanValue();
    }

    public boolean isStandalone() {
        if (getCurrentCapBankState().equalsIgnoreCase(CapBank.STANDALONE_OPSTATE)) {
            return true;
        }
        return false;
    }
    public boolean isFixed() {
        if (getCurrentCapBankState().equalsIgnoreCase(CapBank.FIXED_OPSTATE)) {
            return true;
        }
        return false;
    }
    public boolean isSwitched() {
        if (getCurrentCapBankState().equalsIgnoreCase(CapBank.SWITCHED_OPSTATE)) {
            return true;
        }
        return false;
    }
    public boolean isUninstalled() {
        if (getCurrentCapBankState().equalsIgnoreCase(CapBank.UNINSTALLED_OPSTATE)) {
            return true;
        }
        return false;
    }
    private String getCurrentCapBankState() {
        String state = getStreamable().getOperationalState();
        return state;

    }

    public OnelineCap(SubBus subBusMessage) {
        subBusMsg = subBusMessage;
    }

    public void addDrawing(OneLineDrawing d) {
        drawing = d;
        setPaoId(getCurrentCapIdFromMessage());
        setName(createCapName());

        draw();

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

    public CapBankDevice getStreamable() {
        Feeder feeder = (Feeder) subBusMsg.getCcFeeders().get(currFdrIndex);
        CapBankDevice cap = (CapBankDevice) feeder.getCcCapBanks()
                                                  .get(currentCapIdx);
        return cap;
    }

    public LxComponent getStateImage() {
        return stateImage;
    }

    public UpdatableTextList getTagInfo() {
        return tagInfo;
    }



    public Map<Integer, List<LitePoint>> getPointCache() {
        return pointCache;
    }

    public void setPointCache(Map<Integer, List<LitePoint>> pointCache) {
        this.pointCache = pointCache;      
    }

}
