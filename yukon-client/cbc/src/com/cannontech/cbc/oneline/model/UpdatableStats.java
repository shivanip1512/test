package com.cannontech.cbc.oneline.model;

import com.loox.jloox.LxAbstractGraph;

public interface UpdatableStats {

    public void draw();

    public OnelineObject getParentOnelineObject();

    public void setParentOnelineObject(OnelineObject p);

    public LxAbstractGraph getGraph();

    public void setGraph(LxAbstractGraph graph);

}
