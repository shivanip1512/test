package com.cannontech.cbc.oneline.view;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.model.OnelineControlPanel;
import com.cannontech.cbc.oneline.model.OnelineLogos;
import com.cannontech.cbc.oneline.model.cap.OnelineCap;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.esub.Drawing;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;

public class OneLineDrawing {
    private final Drawing drawing;
    private final OneLineParams layoutParams;
    private final YukonUserContext userContext;
    private String fileName;
    //3-tier
    private OnelineSub sub;
    private List<OnelineFeeder> feeders = new ArrayList<OnelineFeeder>();
    private List<OnelineCap> caps = new ArrayList<OnelineCap>();
    private OnelineLogos logos;
    private OnelineControlPanel controlPanel;

    public OneLineDrawing(OneLineParams layoutParams, YukonUserContext userContext) {
        this.drawing = new Drawing();
        this.layoutParams = layoutParams;
        this.userContext = userContext;
    }
    
    public OneLineDrawing (OneLineParams layoutParams, YukonUserContext userContext, String fileName) {
        this(layoutParams, userContext);
        setFileName(fileName);
    }

    public Drawing getDrawing() {
        return drawing;
    }

    public OneLineParams getLayoutParams() {
        return layoutParams;
    }

    public void addSub(SubBus subBusMessage) {
        sub = new OnelineSub(subBusMessage);
        sub.setYukonUserContext(userContext);
        sub.addDrawing(this);
    }

    public void addLogos() {
        logos = new OnelineLogos();
        logos.addDrawing(this,userContext);
    }

    public void addCap(SubBus subBusMessage, int curIdx) {
        OnelineCap c = new OnelineCap(subBusMessage);
        c.setYukonUserContext(userContext);
        c.setCurrentCapIdx(curIdx);
        c.addDrawing(this);
        caps.add(c);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public OnelineSub getSub() {
        return sub;
    }

    public List<OnelineCap> getCaps() {
        return caps;
    }

    public List<OnelineFeeder> getFeeders() {
        return feeders;
    }

    public void addFeeder(SubBus subBus) {
        OnelineFeeder onelineFeeder = new OnelineFeeder(subBus);
        onelineFeeder.setYukonUserContext(userContext);
        onelineFeeder.addDrawing(this);
        feeders.add(onelineFeeder);
    }

    public OnelineLogos getLogos() {
        return logos;
    }

    public void addNavigationPanel() {
        controlPanel = new OnelineControlPanel();
        controlPanel.setLayoutParams(layoutParams);
        controlPanel.addDrawing (this);
    }

    public OnelineControlPanel getControlPanel() {
        return controlPanel;
    }

}
