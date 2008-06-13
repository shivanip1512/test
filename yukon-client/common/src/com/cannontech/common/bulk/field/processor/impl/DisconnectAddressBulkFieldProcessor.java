package com.cannontech.common.bulk.field.processor.impl;

import org.apache.commons.lang.StringUtils;
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
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.DBPersistent;
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
                
                if (value.getDisconnectAddress() != null && StringUtils.isNotBlank(value.getDisconnectAddress().toString()) && StringUtils.isNumeric(value.getDisconnectAddress().toString())) {            
                    
                 // get dbPersistent MCT400SeriesBase
                    MCT400SeriesBase mct400 = new MCT400SeriesBase();
                    mct400.setDeviceID(deviceId);
                    mct400 = (MCT400SeriesBase)Transaction.createTransaction(Transaction.RETRIEVE, mct400).execute();
            
                    // set disconnect address and update
                    mct400.setHasNewDisconnect(true);
                    mct400.getDeviceMCT400Series().setDisconnectAddress(value.getDisconnectAddress());
                    Transaction.createTransaction(Transaction.UPDATE, mct400).execute();
                    
                    DBChangeMsg disconnectAddressMsg = new DBChangeMsg(deviceId,
                                                      DBChangeMsg.CHANGE_PAO_DB,
                                                      PAOGroups.STRING_CAT_DEVICE,
                                                      PAOGroups.getPAOTypeString(device.getType()),
                                                      DBChangeMsg.CHANGE_TYPE_UPDATE );
                    dbPersistentDao.processDBChange(disconnectAddressMsg);
                    
                    // add disconnect point if not already exists
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
                
                // blank disconnect address, remove disconnect
                else if (value.getDisconnectAddress() == null || StringUtils.isBlank(value.getDisconnectAddress().toString())) {
                    
                    // get dbPersistent MCT400SeriesBase
                    MCT400SeriesBase mct400 = new MCT400SeriesBase();
                    mct400.setDeviceID(deviceId);
                    mct400 = (MCT400SeriesBase)Transaction.createTransaction(Transaction.RETRIEVE, mct400).execute();
                
                    // set to remove disconnect and update
                    mct400.setHasNewDisconnect(false);
                    mct400.getDeviceMCT400Series().deleteAnAddress(mct400.getPAObjectID());
                    Transaction.createTransaction(Transaction.UPDATE, mct400).execute();
                    
                    DBChangeMsg disconnectAddressMsg = new DBChangeMsg(deviceId,
                                                      DBChangeMsg.CHANGE_PAO_DB,
                                                      PAOGroups.STRING_CAT_DEVICE,
                                                      PAOGroups.getPAOTypeString(device.getType()),
                                                      DBChangeMsg.CHANGE_TYPE_UPDATE);
                    dbPersistentDao.processDBChange(disconnectAddressMsg);
                
                    // remove disconnect point if exists
                    if (attributeService.pointExistsForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS)) {
                        
                        PointTemplate template = deviceDefinitionDao.getPointTemplateForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS);
                        LitePoint litePoint = pointDao.getLitePointIdByDeviceId_Offset_PointType(device.getDeviceId(), template.getOffset(), template.getType());
                        DBPersistent point = dbPersistentDao.retrieveDBPersistent(litePoint);
                        
                        dbPersistentDao.performDBChange(point, Transaction.DELETE);
                        
                    }
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
    
    @Required
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }
    
    @Required
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
}
