package com.cannontech.cbc.oneline.model;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.element.StaticImage;
import com.loox.jloox.LxGraph;

public class OnelineLogos {
    
    private Drawing drawing;
    private OneLineParams layoutParams;

    public OnelineLogos() {
        super();
    }

    public void addDrawing(OneLineDrawing old) {
        drawing = old.getDrawing();
        layoutParams = old.getLayoutParams();
        init();
    }

    private void init() {
        LxGraph graph = drawing.getLxGraph();
       
        DrawingMetaElement me = drawing.getMetaElement();
        me.setDrawingWidth(layoutParams.getWidth());
        me.setDrawingHeight(layoutParams.getHeight());

        StaticImage staticImage1 = new StaticImage();
        staticImage1.setYukonImage(OnelineUtil.CAP_CONTROL_LOGO);
        staticImage1.setX(layoutParams.getWidth() / 51.2);
        staticImage1.setY(layoutParams.getHeight() / 40.0);
        graph.add(staticImage1);

        StaticImage staticImage2 = new StaticImage();
        staticImage2.setYukonImage(OnelineUtil.YUKON_LOGO_WHITE);
        staticImage2.setX((staticImage1.getX() + staticImage1.getWidth()) + layoutParams.getWidth()/3);
        staticImage2.setY(staticImage1.getY());
        graph.add(staticImage2);
    }





}
