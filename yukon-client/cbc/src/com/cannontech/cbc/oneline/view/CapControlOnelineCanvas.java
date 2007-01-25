/*
 * Created on May 9, 2003
 * 
 * Example/Test program to generate a JLoox (.jlx) drawing 
 */
package com.cannontech.cbc.oneline.view;

import java.util.Vector;

import com.cannontech.cbc.oneline.OneLineParams;
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

    
    public CapControlOnelineCanvas () {
    }
    public CapControlOnelineCanvas(int h, int w) {
        drawingHeight = h;
        drawingWidth = w;
    }

    public  Drawing createDrawing(SubBus subBusMessage, String fileName) {
        boolean isSingleFeeder = subBusMessage.getCcFeeders().size() == 1;
        layoutParams = new OneLineParams(drawingHeight,
                                         drawingWidth,
                                         isSingleFeeder);
        if (fileName != null)
            drawing = new OneLineDrawing(layoutParams, fileName);
        else
            drawing = new OneLineDrawing (layoutParams);

        drawing.addLogos();
        drawing.addSub(subBusMessage);

        Vector feederVector = subBusMessage.getCcFeeders();
        for (int i = 0; i < feederVector.size(); i++) {
            Feeder currentFeeder = (Feeder) feederVector.get(i);
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

}
