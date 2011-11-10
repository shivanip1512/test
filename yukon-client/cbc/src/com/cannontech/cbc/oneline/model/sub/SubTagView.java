package com.cannontech.cbc.oneline.model.sub;

import java.awt.geom.Point2D;

import com.cannontech.cbc.oneline.CommandPopups;
import com.cannontech.cbc.oneline.elements.SubDynamicImage;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.TagView;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.esub.element.StaticText;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class SubTagView extends LxAbstractView implements TagView {

    private final LxGraph graph;
    private final OnelineSub parent;
    private final SubBus subBus;

    public SubTagView(LxGraph graph, OnelineObject parent, SubBus subBus) {
        super();
        this.graph = graph;
        this.parent = (OnelineSub) parent;
        this.subBus = subBus;
    }

    public void addTagInfo() {

        String strLabel = getTagString();
        SubDynamicImage img = getTransformerImg();
        Point2D startPoint = OnelineUtil.getStartPoint(img);
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

        label.setName(CommandPopups.SUB_TAG + "_" + subBus.getCcId());
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
        if (subBus.getCcDisableFlag())
            tagString += "D:";
        else
            tagString += ":";
        if (subBus.getOvUvDisabledFlag())
            tagString += "V";
        return tagString;
    }

    @Override
    public LxGraph getGraph() {
        return graph;

    }

}
