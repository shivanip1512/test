/*
 * Created on May 9, 2003
 * 
 * Example/Test program to generate a JLoox (.jlx) drawing 
 */
package com.cannontech.cbc.oneline.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.Drawing;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

/**
 * @author alauinger
 */
public class CapControlOnelineCanvas {

    private int drawingHeight = 800;
    private int drawingWidth = 1200;
    private OneLineParams layoutParams;
    private OneLineDrawing drawing;
    private LiteYukonUser user;

    public CapControlOnelineCanvas() {
    }

    public CapControlOnelineCanvas(Dimension d) {
        drawingHeight = (int) d.getHeight();
        drawingWidth = (int) d.getWidth();
    }

    public CapControlOnelineCanvas(int h, int w) {
        drawingHeight = h;
        drawingWidth = w;
    }

    public Drawing createDrawing(SubBus subBusMessage, String fileName) {
        return this.createDrawing(subBusMessage, fileName, null);
    }
    
    public Drawing createDrawing(SubBus subBusMessage, String fileName, Map<Integer,List<LitePoint>> pointCache) {
        boolean isSingleFeeder = subBusMessage.getCcFeeders().size() == 1;
        if (layoutParams == null) {
            layoutParams = new OneLineParams(drawingHeight,
                                             drawingWidth,
                                             isSingleFeeder);
            layoutParams.setUser (user);
        }
        if (fileName != null)
            drawing = new OneLineDrawing(layoutParams, fileName);
        else
            drawing = new OneLineDrawing(layoutParams);

        if (pointCache != null) {
            drawing.setPointCache(pointCache);
        }
        
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

    public void setDrawing(OneLineDrawing drawing) {
        this.drawing = drawing;
    }

    public OneLineParams getLayoutParams() {
        return layoutParams;
    }

    public void setLayoutParams(OneLineParams layoutParams) {
        this.layoutParams = layoutParams;
    }

    public void setUser(LiteYukonUser u) {
        user = u;        
    }



}
