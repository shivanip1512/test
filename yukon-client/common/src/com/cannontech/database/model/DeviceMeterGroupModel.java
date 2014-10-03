package com.cannontech.database.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class DeviceMeterGroupModel extends DBTreeModel {
    
    private static final PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);

    public DeviceMeterGroupModel() {
        super(new DBTreeNode("Meter Numbers"));
    }
    
    @Override
    protected LiteBase convertLiteBase(LiteBase lb) {
        if (lb instanceof LiteYukonPAObject) {
            return findLiteDMG((LiteYukonPAObject) lb);
        }
        return lb;
    }
    
    private LiteBase findLiteDMG(LiteYukonPAObject liteYuk) {
        
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            SimpleMeter meter = cache.getAllMeters().get(liteYuk.getLiteID());
            if (meter != null) {
                return new LiteDeviceMeterNumber(meter.getPaoIdentifier().getPaoId(), 
                        meter.getMeterNumber(), meter.getPaoIdentifier().getPaoType());
            }
        }
        
        return null;
    }
    
    @Override
    public boolean isLiteTypeSupported(int liteType) {
        return (liteType == LiteTypes.YUKON_PAOBJECT || liteType == LiteTypes.DEVICE_METERNUMBER);
    }

    @Override
    public boolean isTreePrimaryForObject(LiteBase lb) {
        return false;
    }

    private boolean isDeviceValid(PaoType paoType) {
        if (isCommanderDevicesOnly()) {
            if (!paoDefinitionDao.isTagSupported(paoType, PaoTag.COMMANDER_REQUESTS)) {
                // only devices read through porter
                return false;
            }
        }
        return paoType.hasMeterNumber();
    }

    @Override
    public void update() {
        
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        
        synchronized (cache) {
            Map<Integer, SimpleMeter> allMeters = cache.getAllMeters();
            List<LiteDeviceMeterNumber> dmns = new ArrayList<>();
            for (SimpleMeter meter : allMeters.values()) {
                dmns.add(new LiteDeviceMeterNumber(meter.getPaoIdentifier().getPaoId(), 
                        meter.getMeterNumber(), meter.getPaoIdentifier().getPaoType()));
            }
            Collections.sort(dmns, LiteComparators.liteStringComparator);
            
            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();
            
            for (LiteDeviceMeterNumber dmn : dmns) {
                DBTreeNode deviceNode = new DBTreeNode(dmn);
                if (isDeviceValid(dmn.getPaoType())) {
                    rootNode.add(deviceNode);
                }
            }
        }
        
        reload();
    }
    
}