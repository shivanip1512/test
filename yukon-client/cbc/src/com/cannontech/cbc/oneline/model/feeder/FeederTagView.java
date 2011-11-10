package com.cannontech.cbc.oneline.model.feeder;

import java.awt.geom.Point2D;

import com.cannontech.cbc.oneline.CommandPopups;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.TagView;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.esub.element.StaticText;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class FeederTagView extends LxAbstractView implements TagView {

    private final LxGraph graph;
    private final OnelineFeeder parent;
    private final Feeder feeder;

    public FeederTagView(LxGraph graph, OnelineObject parent, Feeder feeder) {
        this.graph = graph;
        this.parent = (OnelineFeeder) parent;
        this.feeder = feeder;
    }

    public void addTagInfo() {

        String strLabel = getTagString();
        StaticText nameTxt = getFeederName();
        Point2D startPoint = OnelineUtil.getStartPoint(nameTxt);
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         startPoint,
                                                         new Integer(15),
                                                         new Integer(-20));

        label.setFont(OnelineUtil.LARGE_FONT);
        label.setPaint(OnelineUtil.TAG_COLOR);
        if(parent.isCommandsFlag()) {
        	label.setLinkTo("javascript: void(0)");
        }
        String text = "";
        StaticText value = OnelineUtil.createTextElement(text,
                                                         OnelineUtil.getStartPoint(label),
                                                         new Integer((int) label.getWidth() + 10),
                                                         null);

        label.setName(CommandPopups.FEEDER_TAG + "_" + getCurrentFeederIdFromMessage());
        graph.add(label);
        UpdatableTextList tag = new UpdatableTextList();
        // graph.add(value);
        tag.setFirstElement(label);
        tag.setLastElement(value);

    }

    private StaticText getFeederName() {
        return parent.getFeederName();
    }

    private Integer getCurrentFeederIdFromMessage() {
        return parent.getStreamable().getCcId();
    }

    public void draw() {
        addTagInfo();
    }

    public OnelineObject getParentOnelineObject() {
        return parent;
    }

    public String getTagString() {
        String tagString = "T:";
        if (feeder.getCcDisableFlag())
            tagString += "D:";
        else
            tagString += ":";
        if (feeder.getOvUvDisabledFlag())
            tagString += "V";
            
        return tagString;

    }

    @Override
    public LxGraph getGraph() {
        return graph;
    }

}
