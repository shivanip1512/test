package com.cannontech.cbc.oneline.model;

import java.awt.Color;
import java.awt.Font;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.elements.SubDynamicImage;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.loox.jloox.LxGraph;

public class OnelineControlPanel {
    private static final Font LARGE_FONT = new java.awt.Font("arial",
                                                            java.awt.Font.BOLD,
                                                            20);
    private static final int ControlPanelSize = 200;
    private OneLineDrawing drawing;
    private StaticImage backButton;
    private StaticImage regenerateDraw;
    private StaticText legendText;
    private StaticText printView;
    private OneLineParams layoutParams;
    
    public OnelineControlPanel() {
        init ();
    }

    private void init() {
        backButton = new StaticImage();
        regenerateDraw = new StaticImage();
        backButton.setYukonImage(OnelineUtil.IMG_BACKBUTTON);
        regenerateDraw.setYukonImage(OnelineUtil.IMG_REGENERATE);
        legendText = new StaticText();
        legendText.setName("legend");
        legendText.setText("Legend");
        printView = new StaticText();
        printView.setName("print");
        printView.setText("Printable View");
    }

    public void addDrawing(OneLineDrawing old) {
        drawing = old;
        draw();        
    }

    private void draw() {
        drawBackButton();
        drawRegenerateLink();
        drawLegendLink();
        drawPrintView();
    }
    
    private void drawPrintView() {
        printView.setX(getLegendLink().getX());
        printView.setY(getLegendLink().getY() + 40);
        printView.setLinkTo("javascript:togglePrintableView();");
        printView.setFont(LARGE_FONT);
        printView.setPaint(Color.LIGHT_GRAY);
        
        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(printView);
    }

    private void drawLegendLink() {
        legendText.setX(getRegenerateLink().getX());
        legendText.setY(getRegenerateLink().getY() + 60);
        legendText.setFont(LARGE_FONT);
        legendText.setPaint(Color.LIGHT_GRAY);
        legendText.setLinkTo("javascript:void(0)");
        
        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(legendText);
    }
    
    private void drawRegenerateLink() {

        regenerateDraw.setX(getBackButton().getX());
        regenerateDraw.setY(getBackButton().getY() + 50);
        Integer id = drawing.getSub().getSubBus().getCcId();
        String regenerateLink = "/oneline/OnelineCBCServlet?id=" + id + "&redirectURL=" + layoutParams.getRedirectURL();
        regenerateDraw.setLinkTo(regenerateLink);
        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(regenerateDraw);
        
    }

    private void drawBackButton() {
        OnelineSub sub = drawing.getSub();
        SubDynamicImage transformerImg = sub.getTransformerImg();
        backButton.setX(transformerImg.getX());
        OneLineParams layoutParams = drawing.getLayoutParams();
       	backButton.setY(layoutParams.getHeight() - ControlPanelSize);
        backButton.setLinkTo(layoutParams.getRedirectURL());
        LxGraph graph = drawing.getDrawing().getLxGraph();
        graph.add(backButton);

    }

    public StaticImage getBackButton() {
        return backButton;
    }
    
    private StaticImage getRegenerateLink() {
        return this.regenerateDraw;
    }

    private StaticText getLegendLink() {
        return legendText;
    }
    
    public OneLineParams getLayoutParams() {
        return layoutParams;
    }
    
    public void setLayoutParams(OneLineParams params) {
        layoutParams = params;
    }

}
