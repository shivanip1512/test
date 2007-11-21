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
    private StaticImage ccLogo;
    private StaticImage yukonLogo;

    public OnelineLogos() {
        super();
    }

    public void addDrawing(OneLineDrawing old) {
        drawing = old.getDrawing();
        layoutParams = old.getLayoutParams();
        draw();
    }

    private void draw() {
        LxGraph graph = drawing.getLxGraph();

        DrawingMetaElement me = drawing.getMetaElement();
        me.setDrawingWidth(layoutParams.getWidth());
        me.setDrawingHeight(layoutParams.getHeight());
        me.setDrawingRgbColor(layoutParams.getRgbColor());

        graph.add(getCcLogo());
        graph.add(getYukonLogo());
    }

    public StaticImage getYukonLogo() {
        if (yukonLogo == null) {
            yukonLogo = new StaticImage();
            yukonLogo.setYukonImage(OnelineUtil.IMG_YUKON_LOGO_WHITE);
            yukonLogo.setX(layoutParams.getWidth() - yukonLogo.getWidth());
            yukonLogo.setY(ccLogo.getY());
        }
        return yukonLogo;
    }

    public StaticImage getCcLogo() {
        if (ccLogo == null) {
            ccLogo = new StaticImage();
            ccLogo.setYukonImage(OnelineUtil.IMG_CAP_CONTROL_LOGO);
            ccLogo.setX(layoutParams.getWidth() / 51.2);
            ccLogo.setY(layoutParams.getHeight() / 40.0);
        }
        return ccLogo;
    }

}
