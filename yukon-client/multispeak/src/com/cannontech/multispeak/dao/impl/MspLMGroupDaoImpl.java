package com.cannontech.multispeak.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMGroupEmetcon;
import com.cannontech.loadcontrol.data.LMGroupExpresscom;
import com.cannontech.loadcontrol.data.LMGroupVersacom;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.multispeak.dao.MspLMGroupDao;
import com.cannontech.multispeak.db.MspLMGroupCommunications;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMGroupStatus;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMProgramMode;

public class MspLMGroupDaoImpl implements MspLMGroupDao {

	private SimpleJdbcTemplate template;
	private PointDao pointDao;
	public DynamicDataSource dynamicDataSource;
	
	static String selectClause;
	static String joinClause;
	static String sqlExpresscom;
	static String sqlEmetcon;
	static String sqlVersacom;

	
    static {
        selectClause = " grp.RouteId, transPao.paobjectId, transPao.DisableFlag "; 
        
		joinClause = " JOIN YukonPaobject routePao on grp.RouteId = PaobjectId " +
						" LEFT OUTER JOIN MacroRoute mr on mr.RouteId = grp.RouteId " +
						" LEFT OUTER JOIN Route r2 on r2.RouteId = mr.SingleRouteId " +
						" LEFT OUTER JOIN Route r on r.RouteId = grp.RouteId " +
						" JOIN DeviceDirectCommSettings trans on (r2.DeviceId = trans.DeviceId OR r.deviceId = trans.DeviceId) " +
						" JOIN YukonPaobject transPao on trans.DeviceId = transPao.PaobjectId";
		
		sqlExpresscom = "SELECT DISTINCT grp.LMGroupId, " + selectClause + " FROM LMGroupExpressCom grp " + joinClause;
		sqlEmetcon = "SELECT DISTINCT grp.DeviceId, " + selectClause + " FROM LMGroupEmetcon grp " + joinClause;
		sqlVersacom = "SELECT DISTINCT grp.DeviceId, " + selectClause + " FROM LMGroupVersacom grp " + joinClause;
    }

	private final ParameterizedRowMapper<MspLMGroupCommunications> mspLMGroupCommunicationsMapper = new ParameterizedRowMapper<MspLMGroupCommunications>() {
        public MspLMGroupCommunications mapRow(ResultSet rs, int rowNum) throws SQLException {
            return createMspLMGroupCommunicationsMapping(rs);
        };
    };
    
	@Override
	public List<MspLMGroupCommunications> getLMGroupCommunications(
			LMGroupBase lmGroupBase) {
		if (lmGroupBase instanceof LMGroupEmetcon) {
			return getLMGroupEmetconCommunications((LMGroupEmetcon)lmGroupBase);
		} else if (lmGroupBase instanceof LMGroupExpresscom) {
			return getLMGroupExpresscomCommunications((LMGroupExpresscom)lmGroupBase);
		} else if (lmGroupBase instanceof LMGroupVersacom) {
			return getLMGroupVerscomCommunications((LMGroupVersacom)lmGroupBase);
		} else {
			throw new IllegalArgumentException("LMGroup object type not identified: " + lmGroupBase.toString());
		}
	}

	@Override
	public List<MspLMGroupCommunications> getLMGroupEmetconCommunications(
			LMGroupEmetcon lmGroupEmetcon) {
		String sql = sqlEmetcon + " WHERE grp.DeviceId = ? ";
		return template.query(sql, mspLMGroupCommunicationsMapper, lmGroupEmetcon.getYukonID());
	}

	@Override
	public List<MspLMGroupCommunications> getLMGroupExpresscomCommunications(
			LMGroupExpresscom lmGroupExpresscom) {
		String sql = sqlExpresscom + " WHERE grp.LMGroupId = ? ";
		return template.query(sql, mspLMGroupCommunicationsMapper, lmGroupExpresscom.getYukonID());
	}

	@Override
	public List<MspLMGroupCommunications> getLMGroupVerscomCommunications(
			LMGroupVersacom lmGroupVersacom) {
		String sql = sqlVersacom + " WHERE grp.DeviceId = ? ";
		return template.query(sql, mspLMGroupCommunicationsMapper, lmGroupVersacom.getYukonID());
	}

	@Override
	public MspLMGroupStatus getStatus(
			List<MspLMGroupCommunications> mspLMGroupCommunications) {
		
		for (MspLMGroupCommunications mspLMGroupCommunication : mspLMGroupCommunications) {
			// If any are disabled we are offline
			if (mspLMGroupCommunication.isDisabled()){
				return MspLMGroupStatus.OFFLINE; 
			}
		}

		// Can sum together all status, if status sum is greater then 0, then we know we have some bad statuses.
		int totalStatusValue = 0;
		// Total number of transmitter status points that we checked
		int totalChecked = 0;
		// Check status flag of port
		for (MspLMGroupCommunications mspLMGroupCommunication : mspLMGroupCommunications) {
			try {
				LitePoint commStatusPoint = pointDao.getLitePointIdByDeviceId_Offset_PointType(mspLMGroupCommunication.getTransmitterId(),
																2000, PointTypes.STATUS_POINT);
				PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(commStatusPoint.getPointID());
				Integer value = (int)pointValue.getValue();
				
				if (value == 0 || value == 1) {	//two state status values
					totalChecked++;
					totalStatusValue += value;
				} else {
					//ignore, no idea what this values represents.  Should never get here with a two state status.
				}				
			} catch (NotFoundException e) {
				//can't check if point doesn't exist
			}
		}
		
		if (totalStatusValue > 0 & totalChecked > 0) {
			//at least one of the transmitters had bad status
			return MspLMGroupStatus.FAILED;
		}
		return MspLMGroupStatus.ONLINE; 
	}
	
    private MspLMGroupCommunications createMspLMGroupCommunicationsMapping( ResultSet rset) throws SQLException {

        int lmGroupId = rset.getInt(1);
        int routeId = rset.getInt(2);
        int transId = rset.getInt(3);
        char disabledChar = rset.getString(4).charAt(0);
        boolean disabled = CtiUtilities.isTrue(disabledChar);

        MspLMGroupCommunications mspLMGroupCommunications = new MspLMGroupCommunications(lmGroupId,
        																					routeId,
        																					transId,
        																					disabled);
        return mspLMGroupCommunications;
    }

    @Override
    public MspLMGroupStatus getMasterStatus(
    		List<MspLMGroupStatus> mspLMGroupStatus) {
    	
    	if (mspLMGroupStatus.contains(MspLMGroupStatus.FAILED)) {
    		return MspLMGroupStatus.FAILED;
    	} else if (mspLMGroupStatus.contains(MspLMGroupStatus.OFFLINE)) {
    		return MspLMGroupStatus.OFFLINE;
    	} else {
    		return MspLMGroupStatus.ONLINE;
    	}
    }
 
    @Override
    public MspLMProgramMode getMode(LMProgramBase lmProgramBase) {
    	if (lmProgramBase.isActive()) {
    		return MspLMProgramMode.COIN;
    	} else {
    		return MspLMProgramMode.NONCOIN;
    	}
    }
    
    @Override
    public MspLMProgramMode getMasterMode(
    		List<MspLMProgramMode> mspLMProgramMode) {
    	if (mspLMProgramMode.contains(MspLMProgramMode.COIN)) {
    		return MspLMProgramMode.COIN;
    	} else {
    		return MspLMProgramMode.NONCOIN;
    	}
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate template) {
        this.template = template;
    }
    @Autowired
    public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
		this.dynamicDataSource = dynamicDataSource;
	}
}