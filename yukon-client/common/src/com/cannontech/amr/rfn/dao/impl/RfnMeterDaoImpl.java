package com.cannontech.amr.rfn.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.model.RfnMeterIdentifier;
import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class RfnMeterDaoImpl implements RfnMeterDao {
    private YukonJdbcTemplate jdbcTemplate;
    private PaoDao paoDao;

    @Override
    public RfnMeter getMeter(RfnMeterIdentifier meterIdentifier) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PAObjectID, ypo.Type");
        sql.append("from YukonPaObject ypo");
        sql.append(  "join Device d on ypo.PAObjectID = d.DeviceId");
        sql.append(  "join RfnAddress rfn on d.DeviceId = rfn.DeviceId");
        sql.append("where rfn.SerialNumber").eq(meterIdentifier.getSensorSerialNumber());
        sql.append(  "and rfn.Manufacturer").eq(meterIdentifier.getSensorManufacturer());
        sql.append(  "and rfn.Model").eq(meterIdentifier.getSensorModel());
        
        try {
            PaoIdentifier pao = jdbcTemplate.queryForObject(sql, new YukonPaoRowMapper());
            RfnMeter rfnMeter = new RfnMeter(pao, meterIdentifier);
            return rfnMeter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("no meter matches " + meterIdentifier);
        }
    }
    
    @Override
    public RfnMeter getForId(int paoId) throws NotFoundException {
        return getMeter(paoDao.getYukonPao(paoId));
    }

    @Override
    public RfnMeter getMeter(YukonPao pao) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select rfn.SerialNumber, rfn.Manufacturer, rfn.Model");
        sql.append("from RfnAddress rfn");
        sql.append("where rfn.DeviceId").eq(pao.getPaoIdentifier().getPaoId());
        
        try {
            RfnMeterIdentifier rfnMeterIdentifier = jdbcTemplate.queryForObject(sql, new YukonRowMapper<RfnMeterIdentifier>() {
                public RfnMeterIdentifier mapRow(YukonResultSet rs) throws SQLException {
                    RfnMeterIdentifier result = new RfnMeterIdentifier(rs.getStringSafe("SerialNumber"), 
                                                                       rs.getStringSafe("Manufacturer"), 
                                                                       rs.getStringSafe("Model"));
                    return result;
                }
            });
            RfnMeter rfnMeter = new RfnMeter(pao, rfnMeterIdentifier);
            return rfnMeter;
        } catch (EmptyResultDataAccessException e) {
            RfnMeter rfnMeter = new RfnMeter(pao, RfnMeterIdentifier.createBlank());
            return rfnMeter;
        }
    }

    @Override
    public void updateMeter(RfnMeter meter) {
        if(meter.getMeterIdentifier().isBlank()) {
            /* When someone has blanked out the three fields of the rfn meter address, delete that row from RfnAddress. */
            deleteRfnAddress(meter);
            return;
        }
        /* If there is a row in RfnAddress for this meter, update it, otherwise insert it. */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO RfnAddress");
        sql.values(meter.getPaoIdentifier().getPaoId(), meter.getMeterIdentifier().getSensorSerialNumber(), meter.getMeterIdentifier().getSensorManufacturer(), meter.getMeterIdentifier().getSensorModel());

        try{
            jdbcTemplate.update(sql);
            return;
        } catch (DataIntegrityViolationException e) {
            /* Row is there, try to update it. */
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            updateSql.append("update RfnAddress");
            updateSql.append("set SerialNumber").eq(meter.getMeterIdentifier().getSensorSerialNumber());
            updateSql.append(  ", Manufacturer").eq(meter.getMeterIdentifier().getSensorManufacturer());
            updateSql.append(  ", Model").eq(meter.getMeterIdentifier().getSensorModel());
            updateSql.append("where DeviceId").eq(meter.getPaoIdentifier().getPaoId());
            int rowsAffected = jdbcTemplate.update(updateSql);
            
            if(rowsAffected == 0) {
                /* The initial insert failed because a different meter is using this SN, Manufacturer, Model combination. */
                throw new DataIntegrityViolationException("Serial Number, Manufacturer, and Model must be unique.");
            }
        }

    }
    
    private void deleteRfnAddress(RfnMeter meter) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM RfnAddress");
        sql.append("where DeviceId").eq(meter.getPaoIdentifier().getPaoId());
        
        jdbcTemplate.update(sql);
    }
    
    public String getFormattedDeviceName(RfnMeter device) throws IllegalArgumentException{
        return device.getName();
    }
    
    @Autowired
    public void setJdbcTemplate(YukonJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}