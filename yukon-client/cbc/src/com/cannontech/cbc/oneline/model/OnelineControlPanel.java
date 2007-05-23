package com.cannontech.cbc.oneline.model;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.elements.SubDynamicImage;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.esub.element.StaticImage;
import com.loox.jloox.LxGraph;

public class OnelineControlPanel {

    private OneLineDrawing drawing;
    private StaticImage backButton;
    private StaticImage regenerateDraw;
    private OneLineParams layoutParams;
    
    public OnelineControlPanel() {
        init ();
    }

    private void init() {
        backButton = new StaticImage();
        regenerateDraw = new StaticImage();
        backButton.setYukonImage(OnelineUtil.IMG_BACKBUTTON);
        regenerateDraw.setYukonImage(OnelineUtil.IMG_REGENERATE);
    }

    public void addDrawing(OneLineDrawing old) {
        drawing = old;
        draw();        
    }

    private void draw() {
        drawBackButton();
        drawRegenerateLink();
    }

    private void drawRegenerateLink() {

        regenerateDraw.setX(getBackButton().getX());
        regenerateDraw.setY(getBackButton().getY() + 50);
        Integer id = drawing.getSub().getSubBusMsg().getCcId();
        String regenerateLink = "/capcontrol/oneline/OnelineCBCServlet?id=" + id + "&redirectURL=" + getBackButton().getLinkTo();
        regenerateDraw.setLinkTo(regenerateLink);
        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(regenerateDraw);
        
    }

    private void drawBackButton() {
        OnelineSub sub = drawing.getSub();
        DynamicLineElement distributionLn = sub.getDistributionLn();
        SubDynamicImage transformerImg = sub.getTransformerImg();
        backButton.setX(transformerImg.getX());
        backButton.setY(distributionLn.getPoint2().getY());
        backButton.setLinkTo(layoutParams.getRedirectURL());
        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(backButton);

    }

    public StaticImage getBackButton() {
        return backButton;
    }

    public OneLineParams getLayoutParams() {
        return layoutParams;
    }

    public void setLayoutParams(OneLineParams params) {
        layoutParams = params;
    }

}
