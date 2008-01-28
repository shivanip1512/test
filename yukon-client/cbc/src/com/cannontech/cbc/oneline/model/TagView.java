package com.cannontech.cbc.oneline.model;

import com.loox.jloox.LxGraph;

public interface TagView {

    public void draw();

    public LxGraph getGraph();

    public OnelineObject getParentOnelineObject();

    public void addTagInfo();

    public String getTagString();

}
