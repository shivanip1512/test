package com.cannontech.cbc.oneline.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.model.OnelineLogos;
import com.cannontech.cbc.oneline.model.OnelineControlPanel;
import com.cannontech.cbc.oneline.model.cap.OnelineCap;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.OnelineSub;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.Drawing;
import com.cannontech.yukon.cbc.SubBus;

public class OneLineDrawing {
    private Drawing drawing;
    private OneLineParams layoutParams;
    private String fileName = null;
    private Map<Integer,List<LitePoint>> pointCache;

    //3-tier
    private OnelineSub sub = null;
    private List<OnelineFeeder> feeders = new ArrayList<OnelineFeeder>(0);
    private List<OnelineCap> caps = new ArrayList<OnelineCap>(0);
    private OnelineLogos logos = null;
    private OnelineControlPanel controlPanel;

    public OneLineDrawing(OneLineParams p) {
        drawing = new Drawing();
        layoutParams = p;

    }
    
    public OneLineDrawing (OneLineParams p, String n) {
        this(p);
        setFileName(n);
    }

    public Drawing getDrawing() {
        return drawing;
    }

    public OneLineParams getLayoutParams() {
        return layoutParams;
    }

    public void addSub(SubBus subBusMessage) {
        sub = new OnelineSub(subBusMessage);
        sub.setPointCache(this.pointCache);
        sub.addDrawing(this);

    }

    public void addLogos() {
        logos = new OnelineLogos();
        logos.addDrawing(this);

    }

    public void addCap(SubBus subBusMessage, int curIdx) {
        OnelineCap c = new OnelineCap(subBusMessage);
        c.setPointCache(this.pointCache);
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
        onelineFeeder.setPointCache(this.pointCache);
        onelineFeeder.addDrawing(this);
        feeders.add(onelineFeeder);
    }

    public OnelineLogos getLogos() {
        return logos;
    }

    public void addNavigationPanel() {
        controlPanel = new OnelineControlPanel ();
        controlPanel.setLayoutParams(layoutParams);
        controlPanel.addDrawing (this);
    }

    public OnelineControlPanel getControlPanel() {
        return controlPanel;
    }

    public void setPointCache(Map<Integer, List<LitePoint>> pointCache) {
        this.pointCache = pointCache;
    }

}
