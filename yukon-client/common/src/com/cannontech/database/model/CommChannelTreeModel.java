package com.cannontech.database.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

// This model has the following:
// 1st Level = Ports
// 2nd Level = Devices and points on the port 
@SuppressWarnings("serial")
public class CommChannelTreeModel extends DBTreeModel {

    private static final String CALCULATED = "Calculated";
    
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

        if (paoType.isPort()) {
            return true;
        }

        boolean isDeviceValid = isDeviceValid(paoType);
        boolean isCoreDevice = paoType.getPaoClass().isCore();

        return (isDeviceValid && !isCoreDevice);
    }

    @Override
    public void update() {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();

            Consumer<DBTreeNode> setWillHaveChildren = dbtn -> dbtn.setWillHaveChildren(true);

            // Add the port nodes to the root node in order 
            cache.getAllPorts().stream()
                .map(DBTreeNode::new)
                .forEach(
                    setWillHaveChildren
                    .andThen(rootNode::add));
        }

        reload();
    }

    @Override
    public boolean insertTreeObject(LiteBase lb) {
        if (lb == null || !isLiteTypeSupported(lb.getLiteType()))
            return false;

        DBTreeNode rootNode = (DBTreeNode) getRoot();

        if (lb instanceof LiteYukonPAObject) {
            LiteYukonPAObject litePao = (LiteYukonPAObject) lb;
            if (litePao.getPortID() >= 0) {
                rootNode = findLiteObject(null, YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(litePao.getPortID()));

                if (rootNode != null) {

                    // this will force us to reload ALL the children for this PAObject
                    rootNode.setWillHaveChildren(true);
                    TreePath rootPath = new TreePath(rootNode);
                    treePathWillExpand(rootPath);

                    updateTreeNodeStructure(rootNode);
                    return true;
                }
            } else if (litePao.getPaoType().isPort()) {
                DBTreeNode node = new DBTreeNode(litePao);
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
        switch (paoType.getPaoClass()) {
        case CAPCONTROL:
        case LOADMANAGEMENT:
        case IED:
        case METER:
        case RTU:
        case TRANSMITTER:
        case VIRTUAL:
        case GRID:
        case SYSTEM:
            return true;
        default:
            return false;
        }
    }

    @Override
    public synchronized void treePathWillExpand(TreePath path) {

        // Watch out, this reloads the children every TIME!!!
        DBTreeNode node = (DBTreeNode) path.getLastPathComponent();

        if (node.willHaveChildren() && node.getUserObject() instanceof LiteYukonPAObject) {
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();

            synchronized (cache) {
                int portID = ((LiteYukonPAObject) node.getUserObject()).getYukonID();

                node.removeAllChildren();

                Function<LiteYukonPAObject, DBTreeNode> makeDeviceNode = 
                        device -> {
                            if (device.getPaoIdentifier().getPaoType().isCapControl()) {
                                // Cap Control devices are just a disabled string, and cannot be opened 
                                DBTreeNode nameNode = new DBTreeNode(device.getPaoName());
                                nameNode.setIsSystemReserved(true);
                                return nameNode;
                            } else {
                                return new DBTreeNode(device);
                            }
                        };

                // Add all the port's devices to the port node
                cache.getAllYukonPAObjects().stream()
                        .filter(d -> d.getPortID() == portID)
                        .filter(d -> isDeviceValid(d.getPaoType()))
                        .map(makeDeviceNode)
                        .forEach(node::add);

                PointDao pointDao = YukonSpringHook.getBean(PointDao.class);

                Function<Entry<String, List<LitePoint>>, DummyTreeNode> makePointCategoryNode = 
                        entry -> {
                            DummyTreeNode categoryNode = new DummyTreeNode(entry.getKey());
                            entry.getValue().stream()
                                .map(DBTreeNode::new)
                                .forEach(categoryNode::add);
                            return categoryNode; 
                        };

                Function<PointType, String> asCategoryName = 
                        type -> String.join(" ", StringUtils.splitByCharacterTypeCamelCase(type.name()));
                        
                Function<LitePoint, String> pointCategoryName = 
                        point -> {
                            PointType type = point.getPointTypeEnum();
                            switch (type) {
                                default:  
                                    return asCategoryName.apply(type);
                                case CalcAnalog:
                                case CalcStatus:
                                    return CALCULATED; 
                            }
                        };

                // Group all the port's points by point category name
                Map<String, DummyTreeNode> pointCategoryNodes = 
                        pointDao.getLitePointsByPaObjectId(portID).stream()
                            .sorted(LiteComparators.liteStringComparator)
                            .collect(Collectors.groupingBy(pointCategoryName))
                            .entrySet().stream()
                            .collect(Collectors.toMap(Entry::getKey, makePointCategoryNode));

                // Append the point categories to the port node
                Stream.concat(
                        Stream.of(PointType.Analog, PointType.Status, PointType.PulseAccumulator, PointType.DemandAccumulator)
                            .map(asCategoryName),
                        Stream.of(CALCULATED))
                    .map(pointCategoryNodes::get)
                    .filter(Objects::nonNull)
                    .forEach(node::add);
            }
        }

        node.setWillHaveChildren(false);
    }
}
