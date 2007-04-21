package com.cannontech.cbc.oneline.view;

import java.util.List;

import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.loox.jloox.LxGraph;

public interface AdjustablePosition {
    public void initAllStats();

    public List<UpdatableTextList> adjustPosition();

    public void addToGraph(LxGraph graph, List<UpdatableTextList> copy);

    public void filterInvisibleFromList();
}
