package com.cannontech.cbc.oneline.view;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.model.OnelineControlPanel;
import com.cannontech.cbc.oneline.model.OnelineLogos;
import com.cannontech.cbc.oneline.model.cap.OnelineCap;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.Drawing;
import com.cannontech.yukon.cbc.SubBus;

public class OneLineDrawing {
    private final Drawing drawing;
    private final OneLineParams layoutParams;
    private final LiteYukonUser user;
    private String fileName;
    //3-tier
    private OnelineSub sub;
    private List<OnelineFeeder> feeders = new ArrayList<OnelineFeeder>();
    private List<OnelineCap> caps = new ArrayList<OnelineCap>();
    private OnelineLogos logos;
    private OnelineControlPanel controlPanel;

    public OneLineDrawing(OneLineParams layoutParams, LiteYukonUser user) {
        this.drawing = new Drawing();
        this.layoutParams = layoutParams;
        this.user = user;
    }
    
    public OneLineDrawing (OneLineParams layoutParams, LiteYukonUser user, String fileName) {
        this(layoutParams, user);
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
        sub.setUser(user);
        sub.addDrawing(this);
    }

    public void addLogos() {
        logos = new OnelineLogos();
        logos.addDrawing(this);
    }

    public void addCap(SubBus subBusMessage, int curIdx) {
        OnelineCap c = new OnelineCap(subBusMessage);
        c.setUser(user);
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
        onelineFeeder.setUser(user);
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
