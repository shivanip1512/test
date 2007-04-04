package com.cannontech.cbc.oneline.model.cap;

import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxComponent;

@SuppressWarnings("serial")
public class CapBankUpdatableStats extends LxAbstractView implements UpdatableStats{

    private UpdatableTextList bankSize = new UpdatableTextList();
    private UpdatableTextList opcount = new UpdatableTextList();
    public LxAbstractGraph graph;
    public OnelineCap parentCap;

    public CapBankUpdatableStats(LxAbstractGraph g, OnelineObject p) {
        graph = g;
        parentCap = (OnelineCap) p;
    }

    public void draw() {
        addBankSize(graph);
        addOpcount(graph);

    }

    public void addOpcount(LxAbstractGraph graph2) {

        //String strLabel = "Opcount: ";
        LxAbstractText firstElement = getBankSize().getFirstElement();
        StaticText cnt = OnelineUtil.createTextElement(getStreamable().getTotalOperations() + " Ops",
                                                         OnelineUtil.getStartPoint(firstElement),
                                                         null,
                                                         new Integer((int) getBankSize().getFirstElement()
                                                                                        .getHeight() + 10));

        /*StaticText cnt = OnelineUtil.createTextElement(getStreamable().getTotalOperations() + "",
                                                       OnelineUtil.getStartPoint(label),
                                                       new Integer((int) label.getWidth() + 10),
                                                       null);*/
        cnt.setName("CapStat_" + getStreamable().getCcId() + "_CNT");
        //graph.add(label);
        graph.add(cnt);
        opcount.setFirstElement(cnt);
        //opcount.setLastElement(cnt);

    }

    private CapBankDevice getStreamable() {
        return parentCap.getStreamable();
    }

    private LxComponent getStateImage() {
        return parentCap.getStateImage();
    }

    public void addBankSize(LxAbstractGraph graph2) {

        //String strLabel = "Bank Size: ";
        StaticText size = OnelineUtil.createTextElement(getStreamable().getBankSize() + " KVar",
                                                         OnelineUtil.getStartPoint(getStateImage()),
                                                         null,
                                                         new Integer (25)); //image size is 20 px + 5 px below the image

       /* StaticText size = OnelineUtil.createTextElement(getStreamable().getBankSize() + "",
                                                        OnelineUtil.getStartPoint(label),
                                                        new Integer((int) label.getWidth() + 10),
                                                        null);*/
        size.setName("CapStat_" + getStreamable().getCcId() + "_SIZE");
        //graph.add(label);
        graph.add(size);
        bankSize.setFirstElement(size);
        //bankSize.setLastElement(size);

    }

    private UpdatableTextList getBankSize() {
        return bankSize;
    }

    public OnelineObject getParentOnelineObject() {
        return parentCap;
    }

    public void setParentOnelineObject(OnelineObject p) {
        this.parentCap = (OnelineCap) p;
    }

    public LxAbstractGraph getGraph() {
        return graph;
    }

    public void setGraph(LxAbstractGraph graph) {
        this.graph = graph;
    }
}
