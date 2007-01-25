package com.cannontech.cbc.oneline.model;

import java.awt.Font;

import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.cbc.oneline.view.OnelineCommandTable;
import com.cannontech.esub.Drawing;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxLine;

public class BaseControlPanel {
    private Drawing drawing = null;
    private SubBus subBusMsg = null;

    private LxLine referenceLn = null;
    private String fileName = null;

    private String labelName;
    private String panelName;
    private String jSFunction;
    private OneLineDrawing onelineDrawing;
    
    public static final Font DEFAULT_FONT = new java.awt.Font("arial",
                                                              java.awt.Font.BOLD,
                                                              12);
    OnelineCommand[] commands;
    private Integer paoId;
    private OnelineCommandTable commandTable;
    

    public BaseControlPanel() {
        super();
    }

    public BaseControlPanel(OnelineObject o) {
        super();
        onelineDrawing = o.getDrawing();
        drawing = o.getDrawing().getDrawing();
        subBusMsg = o.getSubBusMsg();
        referenceLn = o.getRefLnBelow();
        paoId = o.getPaoId();
    }

    public LxLine getReferenceLn() {
        return referenceLn;
    }

    public void setReferenceLn(LxLine ln) {
        referenceLn = ln;
    }

    public Drawing getDrawing() {
        return drawing;
    }

    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SubBus getSubBusMsg() {
        return subBusMsg;
    }

    public void setSubBusMsg(SubBus subBusMsg) {
        this.subBusMsg = subBusMsg;
    }

    public void create() {
        commandTable = new OnelineCommandTable(commands, this, getReferenceLn().getPoint1());
        commandTable.setPanelName(getPanelName());
        String function = getJSFunction();
        commandTable.setJSFunction(function);
        commandTable.setLabelName(getLabelName());
        commandTable.drawTable(getDrawing());
        
    }

    public  OnelineCommand[] getCommands() {
        return commands;
    }

    public void setCommands(OnelineCommand[] c) {
        commands = c;
    }



    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String n) {
        panelName = n;
    }

    public String getJSFunction() {
        return jSFunction;
    }

    public void setJSFunction(String function) {
        jSFunction = function;
    }

    public Integer getPaoId() {
        return paoId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public OnelineCommandTable getCommandTable() {
        return commandTable;
    }

    public OneLineDrawing getOnelineDrawing() {
        return onelineDrawing;
    }

}