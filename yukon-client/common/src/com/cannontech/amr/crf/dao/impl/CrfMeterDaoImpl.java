package com.cannontech.amr.crf.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
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
            throw new NotFoundException("no meter matches " + pao);
        }
    }

    @Override
    public void updateMeter(CrfMeter meter) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update CrfAddress");
        sql.append("set SerialNumber").eq(meter.getMeterIdentifier().getSensorSerialNumber());
        sql.append(  ", Manufacturer").eq(meter.getMeterIdentifier().getSensorManufacturer());
        sql.append(  ", Model").eq(meter.getMeterIdentifier().getSensorModel());
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