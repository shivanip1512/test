package com.cannontech.cbc.oneline.model.cap;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.cbc.oneline.model.BaseControlPanel;
import com.cannontech.cbc.oneline.model.OnelineCommand;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.view.OnelineCommandTable;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.StaticText;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxGraph;

public class CapOnelineCommandTable extends OnelineCommandTable {
    private List<String> labelNames;
    public CapOnelineCommandTable(OnelineCommand[] c, BaseControlPanel p, Point2D pt) {
        super(c, p, pt);
    }

    public List<StaticText> createLabels() {
        double initX = getReferencePt().getX();
        double initY = getReferencePt().getY();
        List<StaticText> labels = new ArrayList<StaticText>(0);
        List<String> names = getLabelNames();
        for (String name : names) {
            StaticText t = new StaticText();
            if (name != null)
            {
                t.setX(initX);
                t.setY(initY);
                t.setPaint(Color.PINK);
                t.setText(name);
            }
            else
            {
                t.setPaint(Color.BLACK);
            }
            initY += 20;
            t.setFont(OnelineFeeder.LARGE_FONT);
            t.setLinkTo("javascript:void(0);");
            t.setName(getPanelName());
            labels.add(t);
        }
        
        return labels;
    }

    @Override
    public void drawTable(Drawing drawing) {
        LxGraph lxGraph = drawing.getLxGraph();
        List<StaticText> labelName = createLabels();
        for (StaticText text : labelName) {
            lxGraph.add(text);
        }
        
        for (int i = 0; i < getCommands().length; i++) {
            OnelineCommand command = getCommands()[i];
            LxAbstractText ln = createCommandLine(command, i);
            lxGraph.add(ln);
        }
    }

    public List<String> getLabelNames() {
        return labelNames;
    }

    public void setLabelNames(List<String> labelNames) {
        this.labelNames = labelNames;
    }

}
