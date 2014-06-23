package com.cannontech.database.model;

import java.util.List;
import java.util.Vector;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.MCT_Broadcast;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class MCTBroadcastGroupTreeModel extends DBTreeModel {

    public MCTBroadcastGroupTreeModel() {
        super(new DBTreeNode("MCT Broadcast"));
    }

    @Override
    public boolean insertTreeObject(LiteBase lb) {
        if (lb == null || !isLiteTypeSupported(lb.getLiteType())) {
            return false;
        }
        
        update();
        return false;
    }

    @Override
    public boolean isLiteTypeSupported(int liteType) {
        return (liteType == LiteTypes.YUKON_PAOBJECT);
    }

    @Override
    public boolean isTreePrimaryForObject(LiteBase lb) {
        if (lb instanceof LiteYukonPAObject) {
            PaoType paoType = ((LiteYukonPAObject) lb).getPaoIdentifier().getPaoType();
            return paoType == PaoType.MCTBROADCAST;
        }
        return false;
    }

    @Override
    public void update() {

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            // This is a crappy and inefficient way to do this if there are many devices...
            List<LiteYukonPAObject> devicesList = cache.getAllDevices();
            Vector<LiteYukonPAObject> broadcastersList = new Vector<LiteYukonPAObject>();
            Vector<LiteYukonPAObject> mctList = new Vector<LiteYukonPAObject>();
            // This hopefully helps with the speed issue somewhat
            NativeIntVector mctIDIntList = new NativeIntVector(30);

            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();

            int mctBroadcastID;
            MCT_Broadcast necessaryEvil = new MCT_Broadcast();

            // wow, this just keeps getting fouler...Just wait, it gets even worse...keep going
            for (LiteYukonPAObject liteYukonPAObject : devicesList) {
                if (liteYukonPAObject.getPaoType() == PaoType.MCTBROADCAST) {
                    broadcastersList.add(liteYukonPAObject);
                } else if (liteYukonPAObject.getPaoType().isMct()) {
                    mctList.add(liteYukonPAObject);
                }
            }

            /*
             * This is a bit slow because the database must be hit to find out
             * what MCTs are owned by each broadcast group. There is no way to
             * get this info using Lite objects, so a new chubby must be created
             * in order to call its internal MCT ownership methods
             */
            for (LiteYukonPAObject broadcaster : broadcastersList) {
                DBTreeNode broadcastGroupNode = new DBTreeNode(broadcaster);
                mctBroadcastID = broadcaster.getYukonID();

                mctIDIntList = necessaryEvil.getAllMCTsIDList(new Integer(mctBroadcastID));

                for (int j = 0; j < mctList.size(); j++) {
                    if (mctIDIntList.contains(mctList.elementAt(j).getYukonID())) {
                        broadcastGroupNode.add(new DBTreeNode((mctList.elementAt(j))));
                    }
                }
                rootNode.add(broadcastGroupNode);
            }
        }
        reload();
    }

    @Override
    public boolean updateTreeObject(LiteBase lb) {
        if (lb == null || !isLiteTypeSupported(lb.getLiteType()))
            return false;

        update();
        return false;
    }
}