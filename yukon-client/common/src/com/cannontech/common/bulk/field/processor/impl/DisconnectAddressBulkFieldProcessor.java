package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceMCT400Series;
import com.cannontech.message.dispatch.message.DBChangeMsg;


public class DisconnectAddressBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private DBPersistentDao dbPersistentDao;
    private AttributeService attributeService;
    private DeviceDefinitionDao deviceDefinitionDao;
    private PointDao pointDao;
    
    @Override
    public void updateField(YukonDevice device, YukonDeviceDto value) throws ProcessingException  {

        try {
            
            int deviceId = device.getDeviceId();

            if (DeviceTypesFuncs.isMCT410(device.getType())) {
            
                // get a dbPersistent device
                MCTBase findDevice = new MCTBase();
                findDevice.setDeviceID(deviceId);
                DBPersistent dbDevice = Transaction.createTransaction(Transaction.RETRIEVE, findDevice).execute();
            
                // set disconnect address
                DeviceMCT400Series device400 = (DeviceMCT400Series)dbDevice;
                device400.setDisconnectAddress(value.getDisconnectAddress());
                Transaction.createTransaction(Transaction.UPDATE, device400).execute();
                
                // db change msg for disconnect address update
                DBChangeMsg disconnectAddressMsg = new DBChangeMsg(deviceId,
                                                  DBChangeMsg.CHANGE_PAO_DB,
                                                  "",
                                                  "",
                                                  DBChangeMsg.CHANGE_TYPE_UPDATE );
                dbPersistentDao.processDBChange(disconnectAddressMsg);
                
                BuiltInAttribute disconnectAttr = BuiltInAttribute.DISCONNECT_STATUS;
                if (!attributeService.pointExistsForAttribute(device, disconnectAttr)) {
                    
                    // create disconnect point
                    attributeService.createPointForAttribute(device, disconnectAttr);
                  
                    // db change msg for new point
                    PointTemplate template = deviceDefinitionDao.getPointTemplateForAttribute(device, disconnectAttr);
                    LitePoint disconnectPoint = pointDao.getLitePointIdByDeviceId_Offset_PointType(deviceId, template.getOffset(), template.getType());
                
                    DBChangeMsg disconnectPointMsg = new DBChangeMsg(disconnectPoint.getPointID(),
                                                      DBChangeMsg.CHANGE_POINT_DB,
                                                      DBChangeMsg.CAT_POINT,
                                                      PointTypes.getType(disconnectPoint.getPointType()),
                                                      DBChangeMsg.CHANGE_TYPE_ADD);
                    dbPersistentDao.processDBChange(disconnectPointMsg);
                
                }
                
            }
            else {
                throw new ProcessingException("Device type does not accept disconnect addresses.");
            }
            
        }
        catch (TransactionException e) {
            throw new ProcessingException("Unable to update disconnect address.");
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Unable to update disconnect address.");
        }
    }
    
    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
