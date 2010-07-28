package com.cannontech.amr.crf.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.crf.dao.CrfMeterDao;
import com.cannontech.amr.crf.model.CrfMeter;
import com.cannontech.amr.crf.model.CrfMeterIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class CrfMeterDaoImpl implements CrfMeterDao {
    private YukonJdbcTemplate jdbcTemplate;
    private PaoDao paoDao;

    @Override
    public CrfMeter getMeter(CrfMeterIdentifier meterIdentifier) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.PAObjectID, ypo.Type");
        sql.append("from YukonPaObject ypo");
        sql.append(  "join Device d on ypo.PAObjectID = d.DeviceId");
        sql.append(  "join CrfAddress crf on d.DeviceId = crf.DeviceId");
        sql.append("where crf.SerialNumber").eq(meterIdentifier.getSensorSerialNumber());
        sql.append(  "and crf.Manufacturer").eq(meterIdentifier.getSensorManufacturer());
        sql.append(  "and crf.Model").eq(meterIdentifier.getSensorModel());
        
        try {
            PaoIdentifier pao = jdbcTemplate.queryForObject(sql, new YukonPaoRowMapper());
            CrfMeter crfMeter = new CrfMeter(pao, meterIdentifier);
            return crfMeter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("no meter matches " + meterIdentifier);
        }
    }
    
    @Override
    public CrfMeter getForId(int paoId) throws NotFoundException {
        return getMeter(paoDao.getYukonPao(paoId));
    }

    @Override
    public CrfMeter getMeter(YukonPao pao) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select crf.SerialNumber, crf.Manufacturer, crf.Model");
        sql.append("from CrfAddress crf");
        sql.append("where crf.DeviceId").eq(pao.getPaoIdentifier().getPaoId());
        
        try {
            CrfMeterIdentifier crfMeterIdentifier = jdbcTemplate.queryForObject(sql, new YukonRowMapper<CrfMeterIdentifier>() {
                public CrfMeterIdentifier mapRow(YukonResultSet rs) throws SQLException {
                    CrfMeterIdentifier result = new CrfMeterIdentifier(rs.getStringSafe("SerialNumber"), 
                                                                       rs.getStringSafe("Manufacturer"), 
                                                                       rs.getStringSafe("Model"));
                    return result;
                }
            });
            CrfMeter crfMeter = new CrfMeter(pao, crfMeterIdentifier);
            return crfMeter;
        } catch (EmptyResultDataAccessException e) {
            CrfMeter crfMeter = new CrfMeter(pao, CrfMeterIdentifier.createBlank());
            return crfMeter;
        }
    }

    @Override
    public void updateMeter(CrfMeter meter) {
        if(meter.getMeterIdentifier().isBlank()) {
            /* When someone has blanked out the three fields of the crf meter address, delete that row from CrfAddress. */
            deleteCrfAddress(meter);
            return;
        }
        /* If there is a row in CrfAddress for this meter, update it, otherwise insert it. */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO CrfAddress");
        sql.values(meter.getPaoIdentifier().getPaoId(), meter.getMeterIdentifier().getSensorSerialNumber(), meter.getMeterIdentifier().getSensorManufacturer(), meter.getMeterIdentifier().getSensorModel());

        try{
            jdbcTemplate.update(sql);
            return;
        } catch (DataIntegrityViolationException e) {
            /* Row is there, try update it. */
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            updateSql.append("update CrfAddress");
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
    
    private void deleteCrfAddress(CrfMeter meter) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CrfAddress");
        sql.append("where DeviceId").eq(meter.getPaoIdentifier().getPaoId());
        
        jdbcTemplate.update(sql);
    }
    
    public String getFormattedDeviceName(CrfMeter device) throws IllegalArgumentException{
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