/*
 * Created on May 9, 2003
 * 
 * Example/Test program to generate a JLoox (.jlx) drawing 
 */
package com.cannontech.cbc.oneline.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.esub.Drawing;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;

/**
 * @author alauinger
 */
public class CapControlOnelineCanvas {

    private int drawingHeight = 800;
    private int drawingWidth = 1200;
    private OneLineParams layoutParams;
    private OneLineDrawing drawing;
    private YukonUserContext userContext;

    public CapControlOnelineCanvas(Dimension d) {
        drawingHeight = (int) d.getHeight();
        drawingWidth = (int) d.getWidth();
    }

    public CapControlOnelineCanvas(int h, int w) {
        drawingHeight = h;
        drawingWidth = w;
    }

    public Drawing createDrawing(SubBus subBusMessage, String fileName) {
        boolean isSingleFeeder = subBusMessage.getCcFeeders().size() == 1;
        if (layoutParams == null) {
            layoutParams = new OneLineParams(drawingHeight, drawingWidth, isSingleFeeder);
            layoutParams.setYukonUserContext(userContext);
        }
        
        drawing = (fileName != null) ? new OneLineDrawing(layoutParams, userContext, fileName) : new OneLineDrawing(layoutParams, userContext);

        drawing.addLogos();
        drawing.addSub(subBusMessage);
        drawing.addNavigationPanel();

        List<Feeder> feederList = new ArrayList<Feeder>(subBusMessage.getCcFeeders());
        for (int i = 0; i < feederList.size(); i++) {
            Feeder currentFeeder = feederList.get(i);
            drawing.addFeeder(subBusMessage);

            for (int j = 0; j < currentFeeder.getCcCapBanks().size(); j++) {
                drawing.addCap(subBusMessage, j);
            }
        }
        return drawing.getDrawing();
    }

    public OneLineDrawing getDrawing() {
        return drawing;
    }

    public int getDrawingHeight() {
		return drawingHeight;
	}

	public int getDrawingWidth() {
		return drawingWidth;
	}

	public void setDrawing(OneLineDrawing drawing) {
        this.drawing = drawing;
    }

    public OneLineParams getLayoutParams() {
        return layoutParams;
    }

    public void setLayoutParams(OneLineParams layoutParams) {
        this.layoutParams = layoutParams;
    }

    public void setYukonUserContext(YukonUserContext userContext) {
        this.userContext = userContext;        
    }

}