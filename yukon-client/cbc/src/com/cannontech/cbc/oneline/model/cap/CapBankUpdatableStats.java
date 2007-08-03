package com.cannontech.cbc.oneline.model.cap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.cannontech.cbc.oneline.OnelineDisplayManager;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.oneline.util.UpdatableTextList;
import com.cannontech.cbc.oneline.view.AdjustablePosition;
import com.cannontech.esub.element.StaticText;
import com.cannontech.roles.capcontrol.CBCOnelineSettingsRole;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.loox.jloox.LxAbstractGraph;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class CapBankUpdatableStats extends LxAbstractView implements
        UpdatableStats, AdjustablePosition {

    private UpdatableTextList timestamp = new UpdatableTextList(CBCOnelineSettingsRole.CAP_TIMESTAMP, this);
    private UpdatableTextList bankSize = new UpdatableTextList(CBCOnelineSettingsRole.CAP_BANK_SIZE, this);
    private UpdatableTextList opcount = new UpdatableTextList(CBCOnelineSettingsRole.CAP_OPCNT, this);
    private UpdatableTextList cbcName = new UpdatableTextList(CBCOnelineSettingsRole.CAP_CBC_NAME, this);

    public LxAbstractGraph graph;
    public OnelineCap parentCap;
    private Hashtable<Integer, Integer> propColumnMap = new Hashtable<Integer, Integer>();
    private Hashtable<Integer, String> propLabelMap = new Hashtable<Integer, String>();
    private List<UpdatableTextList> allStats = new ArrayList<UpdatableTextList>();

    public CapBankUpdatableStats(LxAbstractGraph g, OnelineObject p) {
        graph = g;
        parentCap = (OnelineCap) p;
        initPropColumnMap();
        initPropLabelMap();
        initAllStats();

    }

    public void draw() {
        filterInvisibleFromList();
        List<UpdatableTextList> copy = adjustPosition();
        addToGraph((LxGraph) graph, copy);
    }

    private void initPropLabelMap() {
        propLabelMap.put(CBCOnelineSettingsRole.CAP_TIMESTAMP, "Updated:");
        propLabelMap.put(CBCOnelineSettingsRole.CAP_OPCNT, "Op Count:");
        propLabelMap.put(CBCOnelineSettingsRole.CAP_BANK_SIZE, "Bank Size");
        propLabelMap.put(CBCOnelineSettingsRole.CAP_CBC_NAME, "CBC:");

    }

    private void initPropColumnMap() {
        propColumnMap.put(CBCOnelineSettingsRole.CAP_TIMESTAMP,
                CBCDisplay.CB_SHORT_TIME_STAMP_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.CAP_BANK_SIZE,
                          CBCDisplay.CB_BANK_SIZE_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.CAP_OPCNT,
                          CBCDisplay.CB_OP_COUNT_COLUMN);
        propColumnMap.put(CBCOnelineSettingsRole.CAP_CBC_NAME, CBCDisplay.CB_CONTROLLER);
    }

    private CapBankDevice getStreamable() {
        return parentCap.getStreamable();
    }

    private LxComponent getStateImage() {
        return parentCap.getStateImage();
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

    public Hashtable<Integer, Integer> getPropColumnMap() {
        return propColumnMap;
    }

    public Hashtable<Integer, String> getPropLabelMap() {
        return propLabelMap;
    }

    public void addToGraph(LxGraph graph, List<UpdatableTextList> copy) {
        for (UpdatableTextList list : copy) {
            graph.add(list.getFirstElement());
            graph.add(list.getLastElement());
            addExtraElements(graph, list);
        }
    }

    public List<UpdatableTextList> adjustPosition() {
        List<UpdatableTextList> copy = new ArrayList<UpdatableTextList>();
        OnelineDisplayManager manager = OnelineDisplayManager.getInstance();
        if (allStats.size() > 0) {
            LxComponent prevComp = getStateImage();
            StaticText dummy = new StaticText();
            dummy.setX(prevComp.getX());
            dummy.setY(prevComp.getY() + 30);
            UpdatableTextList pair = manager.adjustPosition(allStats,
                                                            dummy,
                                                            0,
                                                            getStreamable());
            copy.add(pair);

        }
        if (allStats.size() > 1) {
            for (int i = 1; i < allStats.size(); i++) {
                UpdatableTextList prevEl = copy.get(i - 1);
                UpdatableTextList pair = manager.adjustPosition(allStats,
                                                                prevEl.getFirstElement(),
                                                                i,
                                                                getStreamable());
                copy.add(pair);
            }
        }
        return copy;
    }

    public void filterInvisibleFromList() {
        List<UpdatableTextList> tempList = new ArrayList<UpdatableTextList>();
        for (UpdatableTextList list : allStats) {
            if (list.isVisible()) {
                tempList.add(list);
            }
        }
        allStats = tempList;
    }

    public void initAllStats() {
        allStats.add(timestamp);
        allStats.add(opcount);
        allStats.add(bankSize);
        allStats.add(cbcName);
        for (UpdatableTextList list : allStats) {
            list.adjustVisibility();
        }
    }

    public void addExtraElements(LxGraph graph, UpdatableTextList list) {
        if (!list.getExtraElements().isEmpty()) {
            List<LxAbstractText> extraElements = list.getExtraElements();
            for (LxAbstractText text : extraElements) {
                graph.add(text);
            }
        }
    }
}
