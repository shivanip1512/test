package com.cannontech.database.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreePath;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public abstract class AbstractDeviceTreeModel extends DBTreeModel {
    // the number of device we will put in the tree before painting the tree
    // protected static final int REFRESH_ITEM_COUNT = 1000;

    private boolean showPoints = true;

    // a Vector only needed to store temporary things
    private List<LitePoint> pointTempList = new java.util.Vector<>(20);

    // a mutable lite point used for comparisons
    private static final LitePoint DUMMY_LITE_POINT = new LitePoint(Integer.MIN_VALUE, "**DUMMY**", 0, 0, 0, 0);

    public AbstractDeviceTreeModel(DBTreeNode rootNode_) {
        this(true, rootNode_);
    }

    public AbstractDeviceTreeModel(boolean showPointNodes, DBTreeNode rootNode_) {
        super(rootNode_);
        showPoints = showPointNodes;
    }

    protected DBTreeNode getNewDeviceNode(LiteYukonPAObject pao) {
        return getNewNode(pao);
    }

    protected DBTreeNode getNewNode(Object obj) {
        return new DBTreeNode(obj);
    }

    protected DBTreeNode addDummyTreeNode(LitePoint lp, DBTreeNode node, String text, DBTreeNode deviceNode) {
        if (node == null) {
            DummyTreeNode retNode = new DummyTreeNode(text);

            int indx = -1;
            for (int i = 0; i < deviceNode.getChildCount(); i++) {
                if (deviceNode.getChildAt(i).equals(retNode)) {
                    indx = i;
                    break;
                }
            }

            if (indx >= 0) {
                node = (DummyTreeNode) deviceNode.getChildAt(indx);
            } else {
                node = retNode;
            }
        }

        node.add(getNewNode(lp));
        // updateTreeNodeStructure( node );

        return node;
    }

    private void addPoints(DBTreeNode deviceNode) {
        // type nodes of point types
        DBTreeNode anNode = null, stNode = null;
        DBTreeNode accDmndNode = null, accPulsNode = null;
        DBTreeNode calcNode = null;

        // the points in the pointList are added to the device node
        // pseudo points are added to the end of the list if sorting by point offset
        // if sorting by name, all points are added in alphabetical order, regardless if pseudo points
        for (LitePoint lp : pointTempList) {

            if (isPointValid(lp)) {
                switch (lp.getPointTypeEnum()) {
                case Analog:
                    anNode = addDummyTreeNode(lp, anNode, "Analog", deviceNode);
                    break;
                case Status:
                    stNode = addDummyTreeNode(lp, stNode, "Status", deviceNode);
                    break;
                case PulseAccumulator:
                    accPulsNode = addDummyTreeNode(lp, accPulsNode, "Pulse Accumulator", deviceNode);
                    break;
                case DemandAccumulator:
                    accDmndNode = addDummyTreeNode(lp, accDmndNode, "Demand Accumulator", deviceNode);
                    break;
                case CalcAnalog:
                case CalcStatus:
                    calcNode = addDummyTreeNode(lp, calcNode, "Calculated", deviceNode);
                    break;
                }
            }
        }

        // finally, add typeNodes to the device -- added here to ensure they are added in the same order every time
        // if a type node is null, it means there are no points of that type and the type node will not be added
        if (anNode != null)
            deviceNode.add(anNode);
        if (stNode != null)
            deviceNode.add(stNode);
        if (accPulsNode != null)
            deviceNode.add(accPulsNode);
        if (accDmndNode != null)
            deviceNode.add(accDmndNode);
        if (calcNode != null)
            deviceNode.add(calcNode);

        // pointList is cleared - only points associated with the current device are held in here
        pointTempList.clear();

    }

    /**
     * This method will return what List of LiteYukonPAObjects we want to use.
     * Example, a device tree model will use : cache.getAllDevices() and a
     * LoadManagement tree model will use: cache.getAllLoadManagement()
     * Override this method when using a different List
     * @return java.util.List
     */
    protected synchronized List getCacheList(IDatabaseCache cache) {
        return cache.getAllDevices();
    }

    protected boolean isPointValid(LitePoint lp) {
        return true; // all points are valid by default
    }

    @Override
    public boolean insertTreeObject(LiteBase lb) {
        if (lb == null || !isLiteTypeSupported(lb.getLiteType())) {
            return false;
        }

        DBTreeNode rootNode = (DBTreeNode) getRoot();

        if (lb instanceof LitePoint) {
            int devID = ((LitePoint) lb).getPaobjectID();

            LiteYukonPAObject pao = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(devID);

            // in order to find our lite object we need to first populate our TreeModel
            updateIfNothingHasBeenLoaded();

            rootNode = findLiteObject(null, pao);

            if (rootNode != null) {

                // this will force us to reload ALL the points for this PAOBject
                rootNode.setWillHaveChildren(true);
                TreePath rootPath = new TreePath(rootNode);
                treePathWillExpand(rootPath);

                updateTreeNodeStructure(rootNode);
                reload();
                return true;
            }

        } else if (lb instanceof LiteYukonPAObject) {
            LiteYukonPAObject liteYuk = (LiteYukonPAObject) lb;

            if (isDeviceValid(liteYuk.getPaoType().getPaoCategory(), liteYuk.getPaoType().getPaoClass(), liteYuk.getPaoType())) {
                DBTreeNode node = getNewDeviceNode(liteYuk);
                node.setWillHaveChildren(true);
                // add all new tree nodes to the top, for now
                int[] indexes = { 0 };

                rootNode.insert(node, indexes[0]);

                nodesWereInserted(rootNode, indexes);
                reload();

                return true;
            }

        }

        return false;
    }

    @Override
    public boolean isTreePrimaryForObject(LiteBase lb) {
        PaoType paoType = null;
        if (lb instanceof LiteYukonPAObject) {
            paoType = ((LiteYukonPAObject) lb).getPaoIdentifier().getPaoType();
        } else if (lb instanceof LitePoint) {
            int paobjectID = ((LitePoint) lb).getPaobjectID();
            YukonPao yukonPao = YukonSpringHook.getBean(PaoDao.class).getYukonPao(paobjectID);
            paoType = yukonPao.getPaoIdentifier().getPaoType();
        } else {
            return false;
        }

        return isDeviceValid(paoType.getPaoCategory(), paoType.getPaoClass(), paoType);
    }

    /**
     * Other models should override this method to return the condition they
     * want to use
     */
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return paoClass.isCore() && 
                paoCategory == PaoCategory.DEVICE && 
                paoType != PaoType.MCTBROADCAST;
    }

    @Override
    public boolean isLiteTypeSupported(int liteType) {
        return (liteType == LiteTypes.YUKON_PAOBJECT || liteType == LiteTypes.POINT);
    }

    /**
     * Override me if you want a sub class to do something different.
     */
    protected synchronized void runUpdate() {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            List devices = getCacheList(cache);
            
            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();

            for (int i = 0; i < devices.size(); i++) {
                if (isDeviceValid(((LiteYukonPAObject) devices.get(i)).getPaoType().getPaoCategory(),
                                  ((LiteYukonPAObject) devices.get(i)).getPaoType().getPaoClass(),
                                  ((LiteYukonPAObject) devices.get(i)).getPaoType())) {
                    DBTreeNode deviceNode = getNewDeviceNode((LiteYukonPAObject) devices.get(i));
                    rootNode.add(deviceNode);

                    if (showPoints) {
                        deviceNode.setWillHaveChildren(true);
                    }
                }
            }
        }
        reload();
    }

    @Override
    public synchronized void sortChildNodes(DBTreeNode parentNode, int sortType) {
        if (parentNode == null) {
            return;
        }

        // we only sort points, for now............

        // default the sorting to the POINT_NAME comparator
        Comparator comp = LiteComparators.liteStringComparator;
        if (sortType == SORT_POINT_OFFSET) {
            comp = LiteComparators.litePointPointOffsetComparator;
        }

        Vector liteObjects = new Vector(parentNode.getChildCount());
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            liteObjects.add(((DBTreeNode) parentNode.getChildAt(i)).getUserObject());
        }

        Collections.sort(liteObjects, comp);

        parentNode.removeAllChildren();
        for (int i = 0; i < liteObjects.size(); i++) {
            parentNode.add(getNewNode(liteObjects.get(i)));
        }

        updateTreeNodeStructure(parentNode);
    }

    @Override
    public synchronized void treePathWillExpand(javax.swing.tree.TreePath path) {
        if (!showPoints) {
            return;
        }

        // Watch out, this reloads the points every TIME!!!
        DBTreeNode node = (DBTreeNode) path.getLastPathComponent();

        if (node.willHaveChildren() && node.getUserObject() instanceof LiteYukonPAObject) {
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();

            synchronized (cache) {
                int deviceDevID = ((LiteYukonPAObject) node.getUserObject()).getYukonID();

                // change our dummy points device ID to the current DeviceID
                DUMMY_LITE_POINT.setPaobjectID(deviceDevID);

                // lock our point list down
                synchronized (pointTempList) {
                    node.removeAllChildren();
                    pointTempList.clear();

                    PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
                    pointTempList = pointDao.getLitePointsByPaObjectId(deviceDevID);

                    // sorts the pointList according to name or offset, (default is set to sort by name)
                    Collections.sort(pointTempList, LiteComparators.liteStringComparator);

                    // add all points and point types to the deviceNode
                    addPoints(node);
                }
            }
        }
        node.setWillHaveChildren(false);
    }

    @Override
    public synchronized void update() {
        runUpdate();
    }

    public void setShowPoints(boolean revealPts) {
        showPoints = revealPts;
    }

    @Override
    protected boolean removeAndAddNodeForUpdate(Object originalObject,
            LiteBase updatedObject) {

        if ((originalObject instanceof LitePoint && updatedObject instanceof LitePoint)) {
            if (((LitePoint) originalObject).getPointType() != ((LitePoint) updatedObject).getPointType()) {
                // type changed for a point and the point types are different
                return true;
            }
        }
        return super.removeAndAddNodeForUpdate(originalObject, updatedObject);
    }
}