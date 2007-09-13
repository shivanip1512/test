package com.cannontech.cbc.oneline.model.sub;

import java.awt.geom.Point2D;

import com.cannontech.cbc.oneline.CommandPopups;
import com.cannontech.cbc.oneline.elements.SubDynamicImage;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.TagView;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class SubTagView extends LxAbstractView implements TagView {

    private SubBus subBusMsg;
    private LxGraph graph;
    private OnelineSub parent;
    private SubHiddenStates states;

    public SubTagView(LxGraph graph, OnelineObject parent, HiddenStates states) {
        super();
        this.graph = graph;
        this.parent = (OnelineSub) parent;
        this.states = (SubHiddenStates) states;
        subBusMsg = parent.getSubBusMsg();
    }

    public void addTagInfo() {

        String strLabel = getTagString();
        SubDynamicImage img = getTransformerImg();
        Point2D startPoint = OnelineUtil.getStartPoint(img);
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         startPoint,
                                                         null,
                                                         new Integer(-20));

        label.setFont(OnelineUtil.LARGE_FONT);
        label.setPaint(OnelineUtil.TAG_COLOR);
        label.setLinkTo("javascript: void(0)");

        String text = "";
        StaticText value = OnelineUtil.createTextElement(text,
                                                         OnelineUtil.getStartPoint(label),
                                                         new Integer((int) label.getWidth() + 10),
                                                         null);

        label.setName(CommandPopups.SUB_TAG + "_" + subBusMsg.getCcId());
        graph.add(label);
        UpdatableTextList tag = new UpdatableTextList();
        //        graph.add(value);
        tag.setFirstElement(label);
        tag.setLastElement(value);

    }

    private SubDynamicImage getTransformerImg() {
        return parent.getTransformerImg();
    }

    public void draw() {
        addTagInfo();
    }

    public OnelineObject getParentOnelineObject() {
        return parent;
    }

    public String getTagString() {
        String tagString = "T:";
        if (states.isDisabled().booleanValue())
            tagString += "D:";
        else
            tagString += ":";
        if (states.isOVUVDisabled())
            tagString += "V";
        return tagString;
    }

    public void setGraph(LxGraph g) {
        graph = g;
    }

    public void setParentOnelineObject(OnelineObject p) {
        parent = (OnelineSub) p;
    }

    public LxGraph getGraph() {
        return graph;

    }

}
