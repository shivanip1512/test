package com.cannontech.sensus;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class ConfigurablePaoLookup implements YukonDeviceLookup {
	private Map<Integer, Integer> paoMapping = new HashMap<Integer, Integer>();
	private PaoDao paoDao;
	private Logger log = YukonLogManager.getLogger(ConfigurablePaoLookup.class);
    private YukonJdbcTemplate jdbcTemplate;
	
	public void setPaoMapping(Map<Integer, Integer> paoMapping) {
		this.paoMapping.putAll(paoMapping);
	}

	@Override
    @SuppressWarnings("unchecked")
	public synchronized LiteYukonPAObject getDeviceForRepId(int repId) {
		Integer paoId = paoMapping.get(repId);

		if (paoId == null) {
			SqlStatementBuilder sql = new SqlStatementBuilder();
			sql.append("select da.deviceid from deviceaddress da, yukonpaobject ypa");
			sql.append("where ypa.paobjectid = da.deviceid");
			sql.append("and ypa.paoclass").eq_k(PaoClass.GRID);
            sql.append("and da.masteraddress").eq(repId);
			try {
                int devId = jdbcTemplate.queryForInt(sql);
				paoMapping.put(repId, devId); 	// Add it to our map to avoid the select next time.
				paoId = devId;
			} catch (IncorrectResultSizeDataAccessException e) {
				log.warn("Zero or non-unique DB entries for gridadvisor serial address: " + repId, e);
				return null;
			}
		}

		LiteYukonPAObject yukonPAObject = paoDao.getLiteYukonPAO(paoId);

		if (yukonPAObject == null) {
			log.warn("Yukon PAO Object not configured, and could not be found for repId: " + repId + ".");
		}

		return yukonPAObject;
	}

	@Override
    public boolean isDeviceConfigured(int repId) {
		return paoMapping.containsKey(repId);
	}

	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
	
    public void setYukonJdbcTemplate(YukonJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
