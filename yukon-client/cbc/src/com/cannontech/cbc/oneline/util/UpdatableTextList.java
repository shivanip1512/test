package com.cannontech.cbc.oneline.util;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.cbc.oneline.OnelineDisplayManager;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.loox.jloox.LxAbstractText;

public class UpdatableTextList {

    private LxAbstractText firstElement;
    private LxAbstractText lastElement;
    private List<LxAbstractText> extraElements = new ArrayList<LxAbstractText>();
    
    private UpdatableStats stats;
    private int rolePropID;
    private boolean visible = true;

    public UpdatableTextList(int r, UpdatableStats s) {
        stats = s;
        rolePropID = r;
    }

    public UpdatableTextList() {
    }

    public LxAbstractText getFirstElement() {
        return firstElement;
    }

    public void setFirstElement(LxAbstractText firstElement) {
        this.firstElement = firstElement;
        adjustVisibility();
    }

    public LxAbstractText getLastElement() {
        return lastElement;
    }

    public void setLastElement(LxAbstractText lastElement) {
        this.lastElement = lastElement;
        adjustVisibility();
    }

    public void adjustVisibility() {
        OnelineDisplayManager instance = OnelineDisplayManager.getInstance();
        if (stats != null) {
            if (!instance.isVisible(rolePropID, stats)) {
                visible = false;
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public int getRolePropID() {
        return rolePropID;
    }

    public UpdatableStats getStats() {
        return stats;
    }

    public void setStats(UpdatableStats stats) {
        this.stats = stats;
    }

    public void setRolePropID(int rolePropID) {
        this.rolePropID = rolePropID;
    }

    public List<LxAbstractText> getExtraElements() {
        return extraElements;
    }
    
    public void addExtraElement (LxAbstractText t) {
        extraElements.add(t);
    }

}
