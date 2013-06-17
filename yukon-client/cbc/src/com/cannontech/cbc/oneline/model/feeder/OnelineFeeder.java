package com.cannontech.cbc.oneline.model.feeder;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import com.cannontech.cbc.oneline.CommandPopups;
import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.TagView;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.states.DynamicLineState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.loox.jloox.LxArrowElement;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineFeeder extends OnelineObject {

    public static final int LINE_LENGTH = 800;
    public static final Font LARGE_FONT = new java.awt.Font("arial",
                                                            java.awt.Font.BOLD,
                                                            14);
    public static final Font SMALL_FONT = new java.awt.Font("arial",
                                                            java.awt.Font.BOLD,
                                                            10);

    public static String NAME_PREFIX = "OnelineFeeder_";

    private String name;
    private int currFdrIdx;
    private DynamicLineElement feederLn;
    private StaticText feederName;

    UpdatableTextList tag = new UpdatableTextList();
    private StaticImage editorImage;
    private StaticImage infoImage;
	private StaticImage warningImageStatic;
	private Feeder feeder;
	private boolean editFlag = false;
    private boolean commandsFlag = false;
    
    public OnelineFeeder(SubBus subBus) {
        this.subBus = subBus;

    }
    
    @Override
    public void draw() {
        // add the feeder distribution line
        int numOfFdr = subBus.getCcFeeders().size();
        currFdrIdx = drawing.getFeeders().size();
        
        feeder = subBus.getCcFeeders().get(currFdrIdx);
        
        double refYBelow = getRefLnBelow().getPoint1().getY();
        double refYAbove = getRefLnAbove().getPoint1().getY();
        double fdrOffset = (refYBelow - refYAbove) / numOfFdr;
        double fdrYCoord = fdrOffset * currFdrIdx + refYAbove;

        double subInjLnX = drawing.getSub()
                                  .getInjectionLine()
                                  .getPoint2()
                                  .getX();

        LineElement feederLn = createFeederLn(fdrYCoord, subInjLnX);

        initEditorImage();
        initInformationImage();
        initWarningStaticImage();
        
        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(feederLn);
        
        if(isEditFlag()) {
        	graph.add(editorImage);
        }
        
        graph.add(warningImageStatic);
        
        UpdatableStats stats = new FeederUpdatableStats(graph, this);
        TagView tagView = new FeederTagView(graph, this, feeder);

        stats.draw();
        tagView.draw();
    }

    private void initInformationImage() {
        infoImage = new StaticImage();
        infoImage.setYukonImage(OnelineUtil.IMG_QUESTION);
        infoImage.setX(editorImage.getX() + 20);
        infoImage.setY(editorImage.getY());
            
    }

    private void initEditorImage() {
        editorImage = new StaticImage();
        String link = OnelineUtil.createBookmarkLink(getCurrentFeederIdFromMessage().intValue());
        editorImage.setLinkTo(link);
        editorImage.setYukonImage(OnelineUtil.IMG_EDITOR);
        double nameX = getFeederName().getX();
        int nameWidth = getFeederName().getText().length() * OnelineUtil.PXLS_PER_CHAR;
        editorImage.setX(nameX + + nameWidth + 10);
        editorImage.setY(getFeederName().getY());
    
    }

    private void initWarningStaticImage() {
        warningImageStatic = new StaticImage();
        
        StaticText nameTxt = getFeederName();
        Point2D startPoint = OnelineUtil.getStartPoint(nameTxt);
        double xImgYPos = startPoint.getY() - 15;
        double imgXPos = startPoint.getX() - 2;
        
        /* Choose which warning image to use */
        UpdaterHelper updaterHelper = YukonSpringHook.getBean("updaterHelper", UpdaterHelper.class);
        String color = (String) updaterHelper.getFeederValueAt(feeder, UpdaterHelper.UpdaterDataType.FDR_WARNING_IMAGE, userContext);

        String image;
        if( color.equalsIgnoreCase("true")) {
    		image = OnelineUtil.IMG_WARNING_YELLOW;
        } else {
    		image = OnelineUtil.IMG_WARNING_GREEN;
        }
        /* */
        
        warningImageStatic.setYukonImage(image);
        
        warningImageStatic.setX(imgXPos);
        warningImageStatic.setName(CommandPopups.WARNING_IMAGE + "_" + getCurrentFeederIdFromMessage());
        warningImageStatic.setCenterY(xImgYPos);
        warningImageStatic.setLinkTo("javascript:void(0)");
    }
    
    private LineElement createFeederLn(double fdrYCoord, double currFdrX) {
        if (feederLn == null) {
            feederLn = new DynamicLineElement(this, new DynamicLineState());
            feederLn.setPoint1(currFdrX, fdrYCoord);
            StaticImage yukonLogo = drawing.getLogos().getYukonLogo();
            int height = (int) (yukonLogo.getX() + yukonLogo.getWidth());
            feederLn.setPoint2(height, fdrYCoord);
            feederLn.setLineColor(Color.YELLOW);
            feederLn.setLineArrow(LxArrowElement.ARROW_END);
            feederLn.setName(getName());

            feederName = new StaticText();
            feederName.setFont(LARGE_FONT);
            feederName.setPaint(Color.PINK);
            feederName.setX(currFdrX + 5);
            feederName.setY(fdrYCoord + 5);
            Feeder feeder = subBus.getCcFeeders().get(currFdrIdx);
            feederName.setText(feeder.getCcName());
            drawing.getDrawing().getLxGraph().add(feederName);
            feederName.setName(getName());
            if(isCommandsFlag()) {
            	feederName.setLinkTo("javascript:void(0)");
            }
        }
        return feederLn;
    }

    @Override
    public void addDrawing(OneLineDrawing d) {
        drawing = d;
        setPaoId(getCurrentFeederIdFromMessage());
        setName(createFeederName());
        draw();
    }

    private Integer getCurrentFeederIdFromMessage() {
        Feeder feeder = subBus.getCcFeeders()
                                          .get(drawing.getFeeders().size());
        Integer ccId = feeder.getCcId();
        return ccId;
    }

    private String createFeederName() {
        return OnelineFeeder.NAME_PREFIX + getPaoId();
    }

    public final void setYukonUserContext(final YukonUserContext userContext) {
        this.userContext = userContext;
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        commandsFlag = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_FEEDER_CONTROLS, userContext.getYukonUser());
        editFlag = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
    }

    @Override
    public LxLine getRefLnBelow() {
        return drawing.getSub().getRefLnBelow();
    }

    @Override
    public LxLine getRefLnAbove() {
        return drawing.getSub().getRefLnAbove();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return subBus.getCcFeeders().get(currFdrIdx);
    }

    public UpdatableTextList getTag() {
        return tag;
    }
    
    public boolean isEditFlag() {
        return editFlag;
    }

    public boolean isCommandsFlag() {
        return commandsFlag;
    }

}