package com.cannontech.database.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreePath;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

// This models has the following:
// 1st Level = Ports YukonPAOBjects
// 2nd Level = Devices YukonPAOBjects
public class CommChannelTreeModel extends DBTreeModel {
    // a mutable lite point used for comparisons
    private static final LiteYukonPAObject DUMMY_LITE_PAO = new LiteYukonPAObject(Integer.MIN_VALUE, "**DUMMY**",
        PaoType.SYSTEM, CtiUtilities.STRING_NONE, CtiUtilities.STRING_NONE);

    private boolean showPoints = true;
    // a Vector only needed to store temporary things
    private List<LitePoint> pointTempList = new java.util.Vector<LitePoint>(20);
    
    // a Vector only needed to store temporary things
    private List<LiteYukonPAObject> tempList = new Vector<>(32);

    public CommChannelTreeModel() {
        super(new DBTreeNode("Comm Channels"));
    }

    @Override
    public boolean isLiteTypeSupported(int liteType) {
        return (liteType == LiteTypes.YUKON_PAOBJECT);
    }

    @Override
    public boolean isTreePrimaryForObject(LiteBase lb) {
        PaoType paoType = null;
        if (lb instanceof LiteYukonPAObject) {
            paoType = ((LiteYukonPAObject) lb).getPaoIdentifier().getPaoType();
        } else {
            return false;
        }

        if (paoType == null) {
            return false;
        }

        boolean isDeviceValid = isDeviceValid(paoType);
        boolean isCoreDevice = paoType.getPaoClass().isCore();
        boolean isCommChannPort = paoType.isPort();
        if (isCommChannPort) {
            return true;
        }
        return (isDeviceValid && !isCoreDevice);
    }

    @Override
    public void update() {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllYukonPAObjects();
            List<LiteYukonPAObject> ports = cache.getAllPorts();

            Collections.sort(devices, LiteComparators.liteYukonPAObjectPortComparator);
            
            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();

            int portID;
            int devicePortID;
            for (int i = 0; i < ports.size(); i++) {
                DBTreeNode portNode = new DBTreeNode(ports.get(i));
                rootNode.add(portNode);
                if (showPoints) {
                    portNode.setWillHaveChildren(true);
                }
               portID = ports.get(i).getYukonID();

                boolean devicesFound = false;

                for (int j = 0; j < devices.size(); j++) {
                    LiteYukonPAObject liteYuk = devices.get(j);

                    if (isDeviceValid(liteYuk.getPaoType())) {
                        devicePortID = devices.get(j).getPortID();
                        if (devicePortID == portID) {
                            devicesFound = true;
                            portNode.add(new DBTreeNode(devices.get(j)));
                        } else if (devicesFound) // used to optimize the iterations
                        {
                            devicesFound = false;
                            break;
                        }
                    }
                }
            }
        }

        reload();
    }

    @Override
    public boolean insertTreeObject(LiteBase lb) {
        if (lb == null || !isLiteTypeSupported(lb.getLiteType()))
            return false;

        DBTreeNode rootNode = (DBTreeNode) getRoot();

        if (lb instanceof LiteYukonPAObject && ((LiteYukonPAObject) lb).getPortID() > PAOGroups.INVALID) {
            int devID = ((LiteYukonPAObject) lb).getPortID();

            rootNode = findLiteObject(null, YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(devID));

            if (rootNode != null) {

                // this will force us to reload ALL the children for this PAObject
                rootNode.setWillHaveChildren(true);
                TreePath rootPath = new TreePath(rootNode);
                treePathWillExpand(rootPath);

                updateTreeNodeStructure(rootNode);
                return true;
            }
        } else if (lb instanceof LiteYukonPAObject) {
            LiteYukonPAObject liteYuk = (LiteYukonPAObject) lb;

            if (liteYuk.getPaoType().isPort()) {
                DBTreeNode node = new DBTreeNode(lb);
                node.setWillHaveChildren(true);
                // add all new tree nodes to the top, for now
                int[] indexes = { 0 };

                rootNode.insert(node, indexes[0]);

                nodesWereInserted(rootNode, indexes);
                return true;
            }

        }
        update();
        return false;
    }

    public boolean isDeviceValid(PaoType paoType) {
        return (paoType.getPaoClass() == PaoClass.LOADMANAGEMENT || paoType.getPaoClass() == PaoClass.IED
            || paoType.getPaoClass() == PaoClass.METER || paoType.getPaoClass() == PaoClass.RTU
            || paoType.getPaoClass() == PaoClass.TRANSMITTER || paoType.getPaoClass() == PaoClass.VIRTUAL
            || paoType.getPaoClass() == PaoClass.GRID || paoType.getPaoClass() == PaoClass.SYSTEM);
    }

    /**
     * This method first sorts list of objects using the passed in comparator.
     * Then, it uses a binary search to find the first instance of the key.
     * If there is more elements that equal key, then the algorithm walks
     * backwards through the sorted list until it reaches the beginning of
     * occurrences of key. Then each occurrence of key is returned
     * in a List.
     */
    private final static <T> List<T> binarySearchRepetition(List<T> listToBeSorted, T key, Comparator<T> comp,
            List<T> destList) {
        destList.clear();

        // do the sort and search here
        Collections.sort(listToBeSorted, comp);
        int loc = Collections.binarySearch(listToBeSorted, key, // must have the needed ID set in
                                                                // this key that comp uses!!
            comp);

        int listSize = listToBeSorted.size();

        // only loop if there is a found item
        if (loc >= 0) {
            // walk back thru the list and make sure we
            // have the first occurrence of the ID
            for (int j = (loc - 1); j >= 0; j--) {
                if (comp.compare(listToBeSorted.get(j), key) == 0) { // is equal
                    loc--;
                } else {
                    break;
                }
            }

            // the element in the location loc SHOULD/MUST be an instance of
            // what we are looking for!
            for (; loc < listSize; loc++) {
                if (comp.compare(listToBeSorted.get(loc), key) == 0) { // is equal
                    destList.add(listToBeSorted.get(loc));
                } else {
                    break; // we've gone past all elements since they are sorted, get out of the loop
                }
            }
        }

        return destList;
    }

    private boolean createPaoChildList(List<LiteYukonPAObject> paos, List<LiteYukonPAObject> destList) {
        // searches and sorts the list!
        // must have the needed PortID set!!
        binarySearchRepetition(paos, DUMMY_LITE_PAO, LiteComparators.litePaoPortIDComparator, destList);

        return destList.size() > 0;
    }

    @Override
    public synchronized void treePathWillExpand(TreePath path) {
        
        if (!showPoints) {
            return;
        }

        // Watch out, this reloads the children every TIME!!!
        DBTreeNode node = (DBTreeNode) path.getLastPathComponent();

        if (node.willHaveChildren() && node.getUserObject() instanceof LiteYukonPAObject) {
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();

            synchronized (cache) {
                int portID = ((LiteYukonPAObject) node.getUserObject()).getYukonID();
                List<LiteYukonPAObject> paos = cache.getAllYukonPAObjects();

                // change our dummy points device ID to the current DeviceID
                DUMMY_LITE_PAO.setPortID(portID);

                // lock our point list down
                synchronized (pointTempList) {
                    node.removeAllChildren();
                    pointTempList.clear();

                    PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
                    pointTempList = pointDao.getLitePointsByPaObjectId(portID);

                    // sorts the pointList according to name or offset, (default is set to sort by name)
                    Collections.sort(pointTempList, LiteComparators.liteStringComparator);

                    // makes a list of points associated with the current deviceNode
                    synchronized (tempList) {
                        tempList.clear();
                        createPaoChildList(paos, tempList);
                    }

                    // add all points and point types to the deviceNode
                    addPaos(node);
                    addPoints(node);
                }
            }
        }

        node.setWillHaveChildren(false);
    }

    private void addPaos(DBTreeNode deviceNode) {
        for (int j = 0; j < tempList.size(); j++) {
            deviceNode.add(new DBTreeNode(tempList.get(j)));
        }

        // tempList is cleared
        tempList.clear();
    }
    
    private void addPoints(DBTreeNode portNode) {
        // type nodes of point types
        DBTreeNode anNode = null;
        DBTreeNode stNode = null;
        DBTreeNode accDmndNode = null;
        DBTreeNode accPulsNode = null;
        DBTreeNode calcNode = null;

        // the points in the pointList are added to the port node
        // pseudo points are added to the end of the list if sorting by point offset
        // if sorting by name, all points are added in alphabetical order, regardless if pseudo points
        for (LitePoint lp : pointTempList) {

            if (isPointValid(lp)) {
                switch (lp.getPointTypeEnum()) {
                case Analog:
                    anNode = addDummyTreeNode(lp, anNode, "Analog", portNode);
                    break;
                case Status:
                    stNode = addDummyTreeNode(lp, stNode, "Status", portNode);
                    break;
                case PulseAccumulator:
                    accPulsNode = addDummyTreeNode(lp, accPulsNode, "Pulse Accumulator", portNode);
                    break;
                case DemandAccumulator:
                    accDmndNode = addDummyTreeNode(lp, accDmndNode, "Demand Accumulator", portNode);
                    break;
                case CalcAnalog:
                case CalcStatus:
                    calcNode = addDummyTreeNode(lp, calcNode, "Calculated", portNode);
                    break;
                }
            }
        }

        // finally, add typeNodes to the port -- added here to ensure they are added in the same order every
        // time
        // if a type node is null, it means there are no points of that type and the type node will not be
        // added
        if (anNode != null)
            portNode.add(anNode);
        if (stNode != null)
            portNode.add(stNode);
        if (accPulsNode != null)
            portNode.add(accPulsNode);
        if (accDmndNode != null)
            portNode.add(accDmndNode);
        if (calcNode != null)
            portNode.add(calcNode);

        // pointList is cleared - only points associated with the current device are held in here
        pointTempList.clear();
    }

    protected DBTreeNode getNewNode(Object obj) {
        return new DBTreeNode(obj);
    }

    protected DBTreeNode addDummyTreeNode(LitePoint lp, DBTreeNode node, String text, DBTreeNode portNode) {
        if (node == null) {
            DummyTreeNode retNode = new DummyTreeNode(text);

            int indx = -1;
            for (int i = 0; i < portNode.getChildCount(); i++) {
                if (portNode.getChildAt(i).equals(retNode)) {
                    indx = i;
                    break;
                }
            }

            if (indx >= 0) {
                node = (DummyTreeNode) portNode.getChildAt(indx);
            } else {
                node = retNode;
            }
        }

        node.add(getNewNode(lp));
        return node;
    }

    protected boolean isPointValid(LitePoint lp) {
        return true; // all points are valid by default
    }

}
