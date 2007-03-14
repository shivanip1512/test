package com.cannontech.cbc.oneline.model;

import com.loox.jloox.LxGraph;

public interface TagView {

    public void draw();

    public LxGraph getGraph();

    public void setGraph(LxGraph graph);

    public OnelineObject getParentOnelineObject();

    public void setParentOnelineObject(OnelineObject parent);

    public void addTagInfo();

    public String getTagString();

}
