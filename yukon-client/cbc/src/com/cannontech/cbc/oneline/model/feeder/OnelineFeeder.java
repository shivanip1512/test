package com.cannontech.cbc.oneline.model.feeder;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.TagView;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.states.DynamicLineState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineFeeder implements OnelineObject {

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
    private Integer paoId;
    private String name;
    private int currFdrIdx;
    private DynamicLineElement feederLn;
    private StaticText feederName;
    private Map<Integer,List<LitePoint>> pointCache;

    UpdatableTextList tag = new UpdatableTextList();
    private StaticImage editorImage;
    private StaticImage infoImage;

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
        //TODO: the +1 is not right.  There is a black line being drawn above this line that i need to find to officially fix this.
        LineElement feederLn = createFeederLn(fdrYCoord+1, subInjLnX);

        initEditorImage();
        initInformationImage();
        
        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(feederLn);
        graph.add(editorImage);
        //graph.add(infoImage);
        UpdatableStats stats = new FeederUpdatableStats(graph, this);
        stats.draw();
        HiddenStates feederStates = new FeederHiddenStates(graph, this);
        TagView tagView = new FeederTagView(graph, this, feederStates);
        tagView.draw();

        feederStates.draw();
    }

    private void initInformationImage() {
        infoImage = new StaticImage();
        //String link = OnelineUtil.createEditLink(getCurrentFeederIdFromMessage().intValue());
        //infoImage.setLinkTo(link);
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
            feederName.setLinkTo("javascript:void(0)");

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

    public UpdatableTextList getTag() {
        return tag;
    }

    public Map<Integer, List<LitePoint>> getPointCache() {
        return pointCache;
    }

    public void setPointCache(Map<Integer, List<LitePoint>> pointCache) {
        this.pointCache = pointCache;
    }

}
