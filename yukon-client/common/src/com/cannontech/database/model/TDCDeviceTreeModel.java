package com.cannontech.database.model;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class TDCDeviceTreeModel extends AbstractDeviceTreeModel {

    public TDCDeviceTreeModel() {
        super(true, new DBTreeNode("Object Types"));
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return true;
    }

    @Override
    protected synchronized List<LiteYukonPAObject> getCacheList(IDatabaseCache cache) {
        return cache.getAllYukonPAObjects();
    }

    // Override me if you want a sub class to do something different.
    @Override
    protected synchronized void runUpdate() {
        DBTreeNode rootNode = (DBTreeNode) getRoot();
        Vector<DummyTreeNode> typeList = new Vector<DummyTreeNode>(32);

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> paos = getCacheList(cache);
            Collections.sort(paos, LiteComparators.litePaoTypeComparator);
            
            rootNode.removeAllChildren();

            PaoType currType = null;
            DummyTreeNode devTypeNode = null;
            for (int i = 0; i < paos.size(); i++) {
                LiteYukonPAObject litPao = paos.get(i);

                if (currType != litPao.getPaoType()) {
                    devTypeNode = new DummyTreeNode(litPao.getPaoType().getDbString());
                    typeList.add(devTypeNode);
                }

                DBTreeNode deviceNode = getNewNode(litPao);
                devTypeNode.add(deviceNode);
                deviceNode.setWillHaveChildren(true);

                currType = litPao.getPaoType();

            } // for loop
        } // synch

        // this list will be a fixed size with a controlled max value
        Collections.sort(typeList, DummyTreeNode.comparator);

        for (int i = 0; i < typeList.size(); i++) {
            rootNode.add(typeList.get(i));
        }

        reload();
    }
}
