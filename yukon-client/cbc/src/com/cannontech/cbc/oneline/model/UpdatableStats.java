package com.cannontech.cbc.oneline.model;

import java.util.Hashtable;

import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.util.UpdaterHelper.UpdaterDataType;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxGraph;

public interface UpdatableStats {

    public void draw();

    public OnelineObject getParentOnelineObject();

    public void setParentOnelineObject(OnelineObject p);

    public LxAbstractGraph getGraph();

    public void setGraph(LxAbstractGraph graph);

    public Hashtable<Integer, UpdaterDataType> getPropColumnMap();

    public Hashtable<Integer, String> getPropLabelMap();

    public void addExtraElements(LxGraph graph, UpdatableTextList list);

    

}
