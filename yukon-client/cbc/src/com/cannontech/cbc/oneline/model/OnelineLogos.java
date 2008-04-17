package com.cannontech.cbc.oneline.model;

import java.awt.Color;
import java.awt.Font;

import com.cannontech.cbc.oneline.CommandPopups;
import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.spring.YukonSpringHook;
import com.loox.jloox.LxGraph;

public class OnelineLogos {
    
	public static final Font LARGE_FONT = new java.awt.Font("arial",Font.BOLD,14);
	public static final int ALERTS_POINT_ID = 8;
	
    private Drawing drawing;
    private OneLineParams layoutParams;
    private StaticImage ccLogo;
    private StaticImage yukonLogo;
    private StaticText alerts;
    private LiteYukonUser user;
    
    public OnelineLogos() {
        super();
    }

    public void addDrawing(OneLineDrawing old, LiteYukonUser user) {
        drawing = old.getDrawing();
        layoutParams = old.getLayoutParams();
        this.user = user;
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
       	graph.add(getAlerts());
    }

    public StaticText getAlerts() {
        AlertService alertService = YukonSpringHook.getBean("alertService",AlertService.class);
        int num = alertService.getCountForUser(user);

		alerts = new StaticText();
		alerts.setName(CommandPopups.ALERTS_POPUP);
		
		alerts.setText("Alerts(" + num + ")");
		StaticImage logo = getCcLogo();
		alerts.setX(logo.getX()+logo.getWidth()+10);
		alerts.setY(logo.getY()+logo.getHeight()/2);
		alerts.setFont(LARGE_FONT);		
		
		//if 0 it will hide the Alert by color black.
		if (num > 0) {
			alerts.setPaint(Color.red);
		} else {
			alerts.setPaint(Color.black);
		}
    	
		alerts.setLinkTo("javascript:void(0)");
		
		return alerts;
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
