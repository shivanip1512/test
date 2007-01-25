package com.cannontech.cbc.oneline.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

import com.cannontech.cbc.oneline.elements.PanelDynamicTextElement;
import com.cannontech.cbc.oneline.model.BaseControlPanel;
import com.cannontech.cbc.oneline.model.OnelineCommand;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.states.PanelTextState;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.StaticText;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxGraph;

public class OnelineCommandTable {

    private OnelineCommand[] commands;
    private Integer paoId;

    private String panelName;
    private String jSFunction;
    private String labelName;
    private Point2D referencePt;
    private List<String> addParams;
    private BaseControlPanel parentControlPanel;

    public OnelineCommandTable(OnelineCommand[] c, BaseControlPanel panel,
            Point2D point) {
        commands = c;
        referencePt = point;
        paoId = panel.getPaoId();
        parentControlPanel = panel;
    }

    public void drawTable(Drawing drawing) {
        LxGraph lxGraph = drawing.getLxGraph();
        StaticText labelName = createLabelName();
        lxGraph.add(labelName);
        for (int i = 0; i < commands.length; i++) {
            OnelineCommand command = commands[i];
            LxAbstractText ln = createCommandLine(command, i);
            lxGraph.add(ln);
        }
    }

    private StaticText createLabelName() {
        StaticText t = new StaticText();
        t.setX(getReferencePt().getX());
        t.setY(getReferencePt().getY());
        if (getLabelName() != null) {
            t.setPaint(Color.PINK);
            t.setText(getLabelName());
        } else
            t.setPaint(Color.BLACK);
        t.setFont(OnelineFeeder.LARGE_FONT);
        t.setLinkTo("javascript:void(0);");
        t.setName(getPanelName());
        return t;
    }

    public LxAbstractText createCommandLine(OnelineCommand command, int row) {
        LxAbstractText cmdLn = null;
        int cmdID = command.getId();
        String cmdName = command.getName();
        if (command.isDynamic()) {
            String stateGrpName = command.getStateGrpName();
            cmdLn = new PanelDynamicTextElement(this,
                                                new PanelTextState(),
                                                stateGrpName);

            PanelDynamicTextElement temp = ((PanelDynamicTextElement) cmdLn);
            temp.setUpdateColor(command.isUpdateColor());
            temp.setUpdateText(command.isUpdateText());
            temp.setLinkTo(createJSFunction(cmdID, cmdName, getAddParams()));
            cmdLn.setName(getPanelName());

        } else {
            cmdLn = new StaticText();
            ((StaticText) cmdLn).setLinkTo(createJSFunction(cmdID,
                                                            cmdName,
                                                            getAddParams()));
            cmdLn.setName(getPanelName());
        }
        cmdLn.setX(getReferencePt().getX());
        cmdLn.setY((getReferencePt().getY() + 20) + ((row + 1) * 20));
        cmdLn.setText(cmdName);
        cmdLn.setFont(BaseControlPanel.DEFAULT_FONT);
        cmdLn.setPaint(Color.WHITE);

        return cmdLn;

    }

    public String createJSFunction(int cmdID, String cmdName,
            List<String> params) {
        String end = ");";
        String buffer = "javascript:" + getJSFunction() + "(" + paoId + "," + cmdID + ",'" + cmdName + "'";

        if (params != null) {
            String is_manual = params.get(0);
            String temp = "," + is_manual;
            buffer += temp;
        }
        return (buffer += end);
    }

    public OnelineCommand[] getCommands() {
        return commands;
    }

    public Point2D getReferencePt() {
        return referencePt;
    }

    public void setPanelName(String n) {
        panelName = n;
    }

    public String getPanelName() {
        return panelName;
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

    public void setPaoId(Integer paoId) {
        this.paoId = paoId;
    }

    public void setLabelName(String n) {
        labelName = n;

    }

    public String getLabelName() {
        return labelName;
    }

    public List<String> getAddParams() {
        return addParams;
    }

    public void setAddParams(List<String> addParams) {
        this.addParams = addParams;
    }

    public BaseControlPanel getParentControlPanel() {
        return parentControlPanel;
    }

}
