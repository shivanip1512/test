package com.cannontech.cbc.oneline.model.sub;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.elements.SubDynamicImage;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.TagView;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.states.DynamicLineState;
import com.cannontech.cbc.oneline.states.SubImgState;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

public class OnelineSub extends OnelineObject {
    public static final String SUB_TAG = "SubTag_";
    public static final String SUB_TRANSFORMER_IMG = "sub_TransformerImg";
    private StaticText name = null;
    private SubDynamicImage transformerImg = null;
    private DynamicLineElement injectionLine;
    private DynamicLineElement distributionLn;
    private StaticImage editorImage;

    public OnelineSub(SubBus subBusMessage) {
        subBusMsg = subBusMessage;
        paoId = subBusMsg.getCcId();
    }

    @Override
    public void addDrawing(OneLineDrawing old) {
        drawing = old;
        draw();
    }

    @Override
    public void draw() {
        Drawing d = drawing.getDrawing();
        LxGraph graph = d.getLxGraph();

        getTransformerImg().setPointID(-100000000);
        
        graph.add(getTransformerImg());
        graph.add(getName());

        graph.add(getInjectionLine());
        graph.add(getDistributionLn());
        initEditorImage();
        graph.add(editorImage);

        UpdatableStats stats = new SubUpdatableStats(graph, this);
        stats.draw();
        //tag info
        HiddenStates states = new SubHiddenStates(graph, this);
        TagView tagView = new SubTagView(graph, this, states);
        tagView.draw();
        states.draw();
    }

    private void initEditorImage() {
        editorImage = new StaticImage();
        String link = OnelineUtil.createBookmarkLink(subBusMsg.getCcId().intValue());
        editorImage.setLinkTo(link);
        editorImage.setYukonImage(OnelineUtil.IMG_EDITOR);
        editorImage.setX(getInjectionLine().getPoint1().getX() + 5);
        editorImage.setY(getInjectionLine().getPoint1().getY() - 25);
            
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
            injectionLine.setPoint2(injLineX + lnLength, injLineY);
            injectionLine.setName("Sub_Injection_" + getPaoId());
        }
        return injectionLine;
    }

    public SubDynamicImage getTransformerImg() {
        if (transformerImg == null) {
            transformerImg = new SubDynamicImage(subBusMsg, new SubImgState());
            OneLineParams layoutParams = drawing.getLayoutParams();
            transformerImg.setCenter(20, layoutParams.getHeight() / 3 + 50);
            transformerImg.setName(SUB_TRANSFORMER_IMG);
            transformerImg.setLinkTo("javascript:void(0)");

        }
        return transformerImg;
    }

    public StaticText getName() {
        if (name == null) {
            name = new StaticText();
            name.setFont(new java.awt.Font("arial", Font.BOLD, 16));
            name.setPaint(Color.WHITE);
            name.setText(subBusMsg.getCcName());
            StaticImage ccLogo = drawing.getLogos().getCcLogo();
            double ccLogoX = ccLogo.getX();
            name.setX(ccLogoX);
            name.setY(ccLogo.getY() + ccLogo.getHeight() + 10);
        }
        return name;
    }

    @Override
    public LxLine getRefLnBelow() {
        LxLine ln = new LxLine();
        Point2D injLnPt1 = getInjectionLine().getPoint1();
        double lnY = drawing.getLayoutParams().getHeight();
        ln.setPoint1(injLnPt1.getX(), lnY);
        ln.setPoint2(injLnPt1.getX() + 800, lnY);
        return ln;
    }

    @Override
    public LxLine getRefLnAbove() {
        LxLine ln = new LxLine();
        StaticImage ccLogo = drawing.getLogos().getCcLogo();
        double lnY = ccLogo.getY() + ccLogo.getHeight() + 30;
        ln.setPoint1(0, lnY);
        ln.setPoint2(drawing.getLayoutParams().getWidth(), lnY);
        return ln;
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

}
