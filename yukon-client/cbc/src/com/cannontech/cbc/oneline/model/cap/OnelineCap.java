package com.cannontech.cbc.oneline.model.cap;

import com.cannontech.cbc.oneline.CommandPopups;
import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.cbc.oneline.states.DynamicLineState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineCap extends OnelineObject {
    public static final String NONE = "X.gif";
    public static final String NAME_PREFIX = "CapBank_";
    private int currentCapIdx = -1;
    private String name;
    private int currFdrIndex;
    private StateImage stateImage;
    private UpdatableTextList tagInfo = new UpdatableTextList();
    private DynamicLineElement connectorLn;
    private StaticImage editorImage;
    private StaticImage infoImage;
    private StaticImage warningImageStatic;
    private StaticText capBankName;
    private StaticImage tmStmpImage;
    
    private boolean editFlag = false;
    private boolean commandsFlag = false;
    private boolean additionalInfoFlag = false;
    
    public OnelineCap(SubBus subBus) {
        this.subBus = subBus;
    }
    
    @Override
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
        initWarningStaticImage(xImgYPos,imgXPos);
        initEditorImage();
        initInformationImage();
        
        if (CapControlUtils.isTwoWay(PAOGroups.getDeviceType(getStreamable().getControlDeviceType()))) {
            initPointTimestampsImage();
        }

        initCapBankName ();
        graph.add(stateImage);
        graph.add(connectorLn);
        if(isEditFlag()) {
        	graph.add(editorImage);
        }
        graph.add(warningImageStatic);
        
        if(additionalInfoFlag)
        {    
        	graph.add(infoImage);
        }
        
        graph.add(capBankName);
        
        if (tmStmpImage != null)
            graph.add(tmStmpImage);
            
        CapBankDevice capBank = getCurrentCapFromMessage();
        
        UpdatableStats capStats = new CapBankUpdatableStats(graph, this);
        CapBankTagView tagView = new CapBankTagView(graph, this, capBank);

        capStats.draw();
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
        String displayableName = getStreamable().getCcName();
        if(getStreamable().getCcName().length() > 13) {
            displayableName = getStreamable().getCcName().substring(0, 11) + "...";
        }
        capBankName.setText(displayableName);

        
        if( !getStreamable().isBankMoved() ){
            capBankName.setName(getName() + "_" + CommandPopups.BANK_MOVE + "_" + getStreamable().getCcName());
        	capBankName.setPaint(OnelineUtil.PURPLISH);
        }
        else{//if moved
            capBankName.setName(getName() + "_" + CommandPopups.BANK_MOVE_BACK + "_" + getStreamable().getCcName());
            capBankName.setPaint(OnelineUtil.ORANGE);
        }
        
        if (isCommandsFlag()) {
        	capBankName.setLinkTo("javascript:void(0)");
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
        Feeder feeder = subBus.getCcFeeders().get(currFdrIndex);
        CapBankDevice cap = feeder.getCcCapBanks().get(currentCapIdx);
        stateImage.setPointID(cap.getStatusPointID().intValue());
        stateImage.setX(imgXPos);
        stateImage.setName(getName());
        stateImage.setCenterY(xImgYPos + 30);
        stateImage.setLinkTo("javascript:void(0)");
        if (isCommandsFlag()) {
            stateImage.setControlEnabled(true);
        } else { 
            stateImage.setControlEnabled(false);
        }
    }
    
    private void initWarningStaticImage(double xImgYPos, double imgXPos) {
        warningImageStatic = new StaticImage();
        
        CapBankDevice capBank = getCurrentCapFromMessage();
        
        /* Choose which warning image to use */
        UpdaterHelper updaterHelper = YukonSpringHook.getBean("updaterHelper", UpdaterHelper.class);
        String type = (String) updaterHelper.getCapBankValueAt(capBank, UpdaterHelper.UpdaterDataType.CB_WARNING_IMAGE_TEXT, userContext);
        String color = (String) updaterHelper.getCapBankValueAt(capBank, UpdaterHelper.UpdaterDataType.CB_WARNING_IMAGE_COLOR, userContext);
        String image;
        if( color.equalsIgnoreCase("Yellow")) {
        	if( type.equalsIgnoreCase("Local")) {
        		image = OnelineUtil.IMG_WARNING_YELLOW_L;
        	} else {
        		image = OnelineUtil.IMG_WARNING_YELLOW;
        	}
        } else {
        	if( type.equalsIgnoreCase("Local")) {
        		image = OnelineUtil.IMG_WARNING_GREEN_L;
        	} else {
        		image = OnelineUtil.IMG_WARNING_GREEN;
        	}
        }
        /* */
        
        warningImageStatic.setYukonImage(image);
        
        warningImageStatic.setX(imgXPos+20);
        warningImageStatic.setName(CommandPopups.WARNING_IMAGE + "_" + getCurrentCapIdFromMessage());
        warningImageStatic.setCenterY(xImgYPos + 40);
        warningImageStatic.setLinkTo("javascript:void(0)");
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
        String link = OnelineUtil.createBookmarkLink(getCurrentCapIdFromMessage());
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

    @Override
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
    
    public int getCurrentCapIdFromMessage() {
        CapBankDevice capBank = getCurrentCapFromMessage();
        int paoId = capBank.getCcId();
        return paoId;
    }

    private CapBankDevice getCurrentCapFromMessage() {
        int currFdrIndex = drawing.getFeeders().size() - 1;
        Feeder f = subBus.getCcFeeders().get(currFdrIndex);
        CapBankDevice c = f.getCcCapBanks().get(currentCapIdx);
        return c;
    }

    public int getCurrentCapIdx() {
        return currentCapIdx;
    }

    public void setCurrentCapIdx(int currentCapIdx) {
        this.currentCapIdx = currentCapIdx;
    }

    public String getName() {
        return name;
    }

    @Override
    public LxLine getRefLnAbove() {
        OnelineSub s = drawing.getSub();
        return s.getRefLnAbove();
    }

    @Override
    public LxLine getRefLnBelow() {
        OnelineSub s = drawing.getSub();
        return s.getRefLnBelow();
    }

    public CapBankDevice getStreamable() {
        Feeder feeder = subBus.getCcFeeders().get(currFdrIndex);
        CapBankDevice cap = feeder.getCcCapBanks().get(currentCapIdx);
        return cap;
    }
    
    public void setYukonUserContext(final YukonUserContext userContext) {
        this.userContext = userContext;
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        commandsFlag = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CAPBANK_CONTROLS, userContext.getYukonUser());
        editFlag = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
        additionalInfoFlag = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_CB_ADDINFO, userContext.getYukonUser());
    }
    
    public boolean isEditFlag() {
        return editFlag;
    }

    public boolean isCommandsFlag() {
        return commandsFlag;
    }

    public LxComponent getStateImage() {
        return stateImage;
    }
    
    public LxComponent getWarningStaticImage() {
        return warningImageStatic;
    }
    
    public UpdatableTextList getTagInfo() {
        return tagInfo;
    }
}