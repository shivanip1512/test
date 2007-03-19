package com.cannontech.cbc.oneline.model;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.elements.SubDynamicImage;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.esub.element.StaticImage;
import com.loox.jloox.LxGraph;

public class OnelineNavPanel {

    private OneLineDrawing drawing;
    private StaticImage backButton;
    private OneLineParams layoutParams;
    
    public OnelineNavPanel() {
        init ();
    }

    private void init() {
        backButton = new StaticImage();
        backButton.setYukonImage(OnelineUtil.IMG_BACKBUTTON);
    }

    public void addDrawing(OneLineDrawing old) {
        drawing = old;
        draw();        
    }

    private void draw() {
        drawBackButton();
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
