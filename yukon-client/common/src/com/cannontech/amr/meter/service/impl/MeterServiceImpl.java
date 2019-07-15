package com.cannontech.amr.meter.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cannontech.amr.meter.service.MeterService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class MeterServiceImpl implements MeterService {
    
    @Autowired private AttributeService attributeService;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public void addDisconnectAddress(SimpleDevice device, int disconnectAddress) throws IllegalArgumentException, IllegalUseOfAttribute {

        boolean supportsDiscCollar = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.DISCONNECT_COLLAR_COMPATIBLE);
        if (!supportsDiscCollar) {
            throw new IllegalArgumentException("Device type does not accept disconnect addresses.");
        }

        /* If there is a row in DeviceMCT400Series for this device, update it, otherwise insert it. */
        /* Use default TOU Schedule Id (0) if adding new entry. */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO DeviceMCT400Series");
        sql.values(device.getPaoIdentifier().getPaoId(), disconnectAddress, 0);
    
        try {
            jdbcTemplate.update(sql);
        } catch (DataIntegrityViolationException e) {
            /* Row is there, try to update it. */
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            updateSql.append("UPDATE DeviceMCT400Series");
            updateSql.append("SET DisconnectAddress").eq(disconnectAddress);
            updateSql.append("WHERE DeviceId").eq(device.getPaoIdentifier().getPaoId());
            jdbcTemplate.update(updateSql);
        }
        
        // add disconnect point if needed
        BuiltInAttribute disconnectAttr = BuiltInAttribute.DISCONNECT_STATUS;
        if (!attributeService.pointExistsForAttribute(device, disconnectAttr)) {
            attributeService.createPointForAttribute(device, disconnectAttr);
        }
        
        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }
    
    @Override
    public void removeDisconnectAddress(SimpleDevice device) throws IllegalArgumentException, IllegalUseOfAttribute {
        
        boolean supportsDiscCollar = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.DISCONNECT_COLLAR_COMPATIBLE);
        if (!supportsDiscCollar) {
            throw new IllegalArgumentException("Device type does not accept disconnect addresses.");
        }
        
        /* Row is there, try to update it. */
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        updateSql.append("DELETE FROM DeviceMCT400Series");
        updateSql.append("WHERE DeviceId").eq(device.getPaoIdentifier().getPaoId());
        jdbcTemplate.update(updateSql);
        
        // remove disconnect point if exists
        if (attributeService.pointExistsForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS)) {
            
            LitePoint liteDisconnectPoint = attributeService.getPointForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS);
            DBPersistent pointBase = LiteFactory.convertLiteToDBPers(liteDisconnectPoint);
            dbPersistentDao.performDBChange(pointBase,TransactionType.DELETE);
        }
        
        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }
}
