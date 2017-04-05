package com.cannontech.multispeak.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.meter.dao.impl.MeterRowMapper;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class MspMeterDaoBase {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate = null;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MeterRowMapper meterRowMapper;

    protected static String selectSql;

    static {
        selectSql =
            "SELECT PaobjectId, Type, PaoName, MeterNumber, Address, DisconnectAddress, "
                + "SerialNumber, Manufacturer, Model " + "FROM YukonPaobject pao "
                + "JOIN DeviceMeterGroup dmg ON pao.paobjectId = dmg.deviceId "
                + "LEFT JOIN DeviceCarrierSettings dcs ON pao.PAObjectID = dcs.DEVICEID "
                + "LEFT JOIN DeviceMCT400Series mct ON pao.paobjectId = mct.deviceId "
                + "LEFT JOIN RFNAddress rfn ON pao.PAObjectID = rfn.DeviceId";
    };

    /**
     * Gets the SQL query to search for the meters of AMR/CD support type
     * 
     * @param meterSupportType to indicate if meter is AMR or CD supported meter
     * @param lastReceived search criteria , last meter number received
     * @returns SqlStatementBuilder with the SQL query formed 
     */
    public SqlStatementBuilder buildSqlStatementByMeterSupportType(MeterSupportType meterSupportType,
            String lastReceived) {
        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);

        if (lastReceived != null) {
            sql.append("WHERE MeterNumber").gt(lastReceived);
        }
        if (excludeDisabled) {
            if (StringUtils.containsIgnoreCase(sql.getSql(), "WHERE ")) {
                sql.append("AND");
            } else {
                sql.append("WHERE");
            }
            sql.append("DisableFlag").eq_k(YNBoolean.NO);
        }

        if (meterSupportType == MeterSupportType.CD_SUPPORTED) {
            if (StringUtils.containsIgnoreCase(sql.getSql(), "WHERE ")) {
                sql.append("AND");
            } else {
                sql.append("WHERE");
            }
            Collection<PaoType> collection = getIntegratedDisconnectPaoDefinitions();
            sql.append("(pao.type IN (").appendArgumentList(collection).append(")");
            sql.append("OR (DisconnectAddress IS NOT NULL) )");
        }
        sql.append("ORDER BY METERNUMBER");
        return sql;
    }

    /**
     * Returns true if meterNumber is a disconnect meter.
     */
    public boolean isCDSupportedMeter(String meterNumber) {
        Collection<PaoType> collection = getIntegratedDisconnectPaoDefinitions();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM (");
        sql.append(selectSql);
        sql.append("WHERE (pao.type IN (").appendArgumentList(collection).append(")");
        sql.append("OR (DisconnectAddress IS NOT NULL) )");
        sql.append("AND METERNUMBER").eq(meterNumber);
        sql.append(") Temp");

        int meterCount = yukonJdbcTemplate.queryForInt(sql);
        if (meterCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public YukonMeter getMeterForMeterNumber(String meterNumber) {

        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(meterRowMapper.getSql());
        sql.append("WHERE UPPER(dmg.MeterNumber)").eq(meterNumber.toUpperCase());
        if (excludeDisabled) {
            sql.append("AND ypo.DisableFlag").eq(YNBoolean.NO);
        }

        try {
            YukonMeter yukonMeter = yukonJdbcTemplate.queryForObject(sql, meterRowMapper);
            return yukonMeter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("no meter matches " + meterNumber);
        }
    }

    public YukonMeter getForSerialNumberOrAddress(String serialNumberOrAddress) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            sql.append("WHERE dcs.Address").eq(serialNumberOrAddress);
            sql.append("OR SerialNumber").eq(serialNumberOrAddress);
            YukonMeter meter = yukonJdbcTemplate.queryForObject(sql, meterRowMapper);
            return meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown physical address or rfn sensorSerialNumber " + serialNumberOrAddress);
        }
    }

    /**
     * Helper method to return a collection of PaoTypes that support "integrated" disconnect capabilities.
     */
    private Collection<PaoType> getIntegratedDisconnectPaoDefinitions() {
        Set<PaoDefinition> discCollar = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_COLLAR_COMPATIBLE);
        Set<PaoDefinition> disc410 = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_410);
        Set<PaoDefinition> disc310 = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_310);
        Set<PaoDefinition> disc213 = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_213);
        Set<PaoDefinition> discRfn = paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_RFN);

        List<PaoDefinition> discIntegrated = Lists.newArrayList();
        discIntegrated.addAll(disc410);
        discIntegrated.addAll(disc310);
        discIntegrated.addAll(disc213);
        discIntegrated.addAll(discRfn);
        discIntegrated.removeAll(discCollar); // Remove disconnect collar compatible

        Collection<PaoType> collection = Collections2.transform(discIntegrated, new Function<PaoDefinition, PaoType>() {
            @Override
            public PaoType apply(PaoDefinition paoDefinition) {
                return paoDefinition.getType();
            }
        });

        return collection;
    }
}