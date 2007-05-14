package com.cannontech.cbc.oneline.model.cap;

import java.awt.geom.Point2D;

import com.cannontech.cbc.oneline.CommandPopups;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.TagView;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.esub.element.StaticText;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class CapBankTagView extends LxAbstractView implements TagView {

    public OnelineCap parent;
    public LxGraph graph;
    public CapBankHiddenStates states;

    public CapBankTagView(LxGraph g, OnelineObject p, HiddenStates s) {
        graph = g;
        parent = (OnelineCap) p;
        states = (CapBankHiddenStates) s;
    }

    public HiddenStates getStates() {
        return states;
    }

    public void setStates(HiddenStates states) {
        this.states = (CapBankHiddenStates) states;
    }

    public void draw() {
        addTagInfo();

    }

    public LxGraph getGraph() {
        return graph;
    }

    public void setGraph(LxGraph graph) {
        this.graph = graph;
    }

    public OnelineObject getParentOnelineObject() {
        return parent;
    }

    public void setParentOnelineObject(OnelineObject parent) {
        this.parent = (OnelineCap) parent;
    }

    public void addTagInfo() {

        String strLabel = getTagString();
        LxComponent img = getStateImage();
        Point2D startPoint = OnelineUtil.getStartPoint(img);
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         startPoint,
                                                         new Integer (25),
                                                         null);

        label.setFont(OnelineUtil.LARGE_FONT);
        label.setPaint(OnelineUtil.TAG_COLOR);

        label.setLinkTo("javascript: void(0)");

        String text = "";
        StaticText value = OnelineUtil.createTextElement(text,
                                                         OnelineUtil.getStartPoint(label),
                                                         new Integer((int) label.getWidth() + 10),
                                                         null);

        label.setName(CommandPopups.CAP_TAG + "_" + getCurrentCapIdFromMessage());
        graph.add(label);
        UpdatableTextList tagInfo = new UpdatableTextList();
        tagInfo.setFirstElement(label);
        tagInfo.setLastElement(value);

    }

    public Integer getCurrentCapIdFromMessage() {
        return parent.getCurrentCapIdFromMessage();
    }

    public LxComponent getStateImage() {
        return parent.getStateImage();
    }

    public String getTagString() {
        String tagString = "T:";
        if (states.isDisabled().booleanValue())
            tagString += "D:";
        else
            tagString += ":";
        if (states.isOVUVDisabled())
            tagString += "V:";
        else
            tagString += ":";
        if (states.isStandalone())

            tagString += "S";
        else
            tagString += "";
        return tagString;
    }

}
