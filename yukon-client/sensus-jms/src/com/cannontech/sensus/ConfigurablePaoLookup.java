package com.cannontech.sensus;

import java.util.Map;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class ConfigurablePaoLookup implements YukonDeviceLookup {
    private Map<Integer, Integer> paoMapping;
    private PaoDao paoDao;
    private Logger log = YukonLogManager.getLogger(ConfigurableDeviceLookup.class);

    public void setPaoMapping(Map<Integer, Integer> paoMapping) {
        this.paoMapping = paoMapping;
    }

    public LiteYukonPAObject getDeviceForRepId(int repId) {
        Integer paoId = paoMapping.get(repId);
        if (paoId == null) {
            return null;
        }
        LiteYukonPAObject yukonPAObject = paoDao.getLiteYukonPAO(paoId);
        if (yukonPAObject == null) {
            log.warn("PAO Id '" + paoId + "' could not be found. ");
        }
        return yukonPAObject;
    }

    public boolean isDeviceConfigured(int repId) {
        return paoMapping.containsKey(repId);
    }

    public void setPaoDao(PaoDao paoDao) {
    	this.paoDao = paoDao;
    }
}
