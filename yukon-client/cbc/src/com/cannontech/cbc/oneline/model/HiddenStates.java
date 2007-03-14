package com.cannontech.cbc.oneline.model;

import com.loox.jloox.LxGraph;

public interface HiddenStates {


    public OnelineObject getParentOnelineObject();

    public void setParentOnelineObject(OnelineObject parent);

    public void draw();

    public void addStateInfo();

    public LxGraph getGraph();

    public void setGraph(LxGraph graph);

}
