package com.cannontech.database.model;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreePath;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.yukon.IDatabaseCache;

// This models has the following:
// 1st Level = Ports YukonPAOBjects
// 2nd Level = Devices YukonPAOBjects
public class CommChannelTreeModel extends DBTreeModel {
    // a mutable lite point used for comparisons
    private static final LiteYukonPAObject DUMMY_LITE_PAO = new LiteYukonPAObject(Integer.MIN_VALUE, "**DUMMY**",
        PaoType.SYSTEM, CtiUtilities.STRING_NONE, CtiUtilities.STRING_NONE);

    // a Vector only needed to store temporary things
    private List<LiteYukonPAObject> tempList = new Vector<>(32);

    public CommChannelTreeModel() {
        super(new DBTreeNode("Comm Channels"));
    }

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

        boolean isDeviceValid =
            isDeviceValid(paoType.getPaoCategory().getCategoryId(), paoType.getPaoClass().getPaoClassId(),
                paoType.getDeviceTypeId());
        boolean isCoreDevice = DeviceClasses.isCoreDeviceClass(paoType.getPaoClass().getPaoClassId());
        boolean isCommChannPort = PAOGroups.isValidPortType(paoType.getDeviceTypeId());
        if (isCommChannPort) {
            return true;
        }
        return (isDeviceValid && !isCoreDevice);
    }

    public void update() {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllYukonPAObjects();
            List<LiteYukonPAObject> ports = cache.getAllPorts();

            Collections.sort(devices, LiteComparators.liteYukonPAObjectPortComparator);
            Collections.sort(ports, LiteComparators.liteStringComparator);

            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();

            int portID;
            int devicePortID;
            for (int i = 0; i < ports.size(); i++) {
                DBTreeNode portNode = new DBTreeNode(ports.get(i));
                rootNode.add(portNode);
                portID = ((LiteYukonPAObject) ports.get(i)).getYukonID();

                boolean devicesFound = false;

                for (int j = 0; j < devices.size(); j++) {
                    LiteYukonPAObject liteYuk = (LiteYukonPAObject) devices.get(j);

                    if (isDeviceValid(liteYuk.getPaoType().getPaoCategory().getCategoryId(), liteYuk.getPaoType()
                        .getPaoClass().getPaoClassId(), liteYuk.getPaoType().getDeviceTypeId())) {
                        devicePortID = ((LiteYukonPAObject) devices.get(j)).getPortID();
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

    public boolean insertTreeObject(LiteBase lb) {
        if (lb == null || !isLiteTypeSupported(lb.getLiteType()))
            return false;

        DBTreeNode rootNode = (DBTreeNode) getRoot();

        if (lb instanceof LiteYukonPAObject && ((LiteYukonPAObject) lb).getPortID() > PAOGroups.INVALID) {
            int devID = ((LiteYukonPAObject) lb).getPortID();

            rootNode = findLiteObject(null, DaoFactory.getPaoDao().getLiteYukonPAO(devID));

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

            if (PAOGroups.isValidPortType(liteYuk.getPaoType().getDeviceTypeId())) {
                DBTreeNode node = new DBTreeNode(lb);

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

    public boolean isDeviceValid(int category_, int deviceClass, int type_) {
        return (deviceClass == DeviceClasses.LOADMANAGEMENT || deviceClass == DeviceClasses.IED
            || deviceClass == DeviceClasses.METER || deviceClass == DeviceClasses.RTU
            || deviceClass == DeviceClasses.TRANSMITTER || deviceClass == DeviceClasses.VIRTUAL
            || deviceClass == DeviceClasses.GRID || deviceClass == DeviceClasses.SYSTEM);
    }

    private boolean createPaoChildList(List<LiteYukonPAObject> paos, List<LiteYukonPAObject> destList) {
        // searches and sorts the list!
        CtiUtilities.binarySearchRepetition(paos, DUMMY_LITE_PAO, // must have the needed PortID set!!
        LiteComparators.litePaoPortIDComparator, destList);

        return destList.size() > 0;
    }

    public synchronized void treePathWillExpand(TreePath path) {
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
                synchronized (tempList) {
                    node.removeAllChildren();
                    tempList.clear();

                    // makes a list of points associated with the current deviceNode
                    createPaoChildList(paos, tempList);

                    // add all points and point types to the deviceNode
                    addPaos(node);
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
}
