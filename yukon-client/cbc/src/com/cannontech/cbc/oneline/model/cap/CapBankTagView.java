package com.cannontech.cbc.oneline.model.cap;

import java.awt.geom.Point2D;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.cbc.oneline.CommandPopups;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.TagView;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.esub.element.StaticText;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class CapBankTagView extends LxAbstractView implements TagView {

    public final OnelineCap parent;
    public final LxGraph graph;
    public final CapBankDevice capBank;

    public CapBankTagView(LxGraph g, OnelineObject p, CapBankDevice capBank) {
        this.graph = g;
        this.parent = (OnelineCap) p;
        this.capBank = capBank;
    }

    public void draw() {
        addTagInfo();

    }

    @Override
    public LxGraph getGraph() {
        return graph;
    }

    public OnelineObject getParentOnelineObject() {
        return parent;
    }

    public void addTagInfo() {

        String strLabel = getTagString();
        LxComponent img = getWarningImageStatic();
        Point2D startPoint = OnelineUtil.getStartPoint(img);
        StaticText label = OnelineUtil.createTextElement(strLabel,
                                                         startPoint,
                                                         new Integer (15),
                                                         null);

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

    public LxComponent getWarningImageStatic() {
    	return parent.getWarningStaticImage();
    }
    public String getTagString() {
        String tagString = "T:";
        if (capBank.getCcDisableFlag()) {
            tagString += "D:";
        } else {
            tagString += ":";
        }
        
        if (capBank.getOvUVDisabled()) {
            tagString += "V:";
        } else {
            tagString += ":";
        }
        
        BankOpState state = BankOpState.getStateByName(capBank.getOperationalState());
        switch (state) {
            case FIXED : {
                tagString += "F:";
                break;
            }
            case STANDALONE : {
                tagString += "S:";
                break;
            }
            default : tagString += ":";
        }
        
        if (capBank.isIgnoreFlag()) {
        	tagString += "R";
        } else {
        	tagString += "";
        }
        
        return tagString;
    }

}
