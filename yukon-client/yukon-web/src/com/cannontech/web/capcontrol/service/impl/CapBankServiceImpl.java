package com.cannontech.web.capcontrol.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.creation.service.CapControlCreationService;
import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.model.CompleteCapBank;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.service.CapBankService;
import com.cannontech.yukon.IDatabaseCache;

public class CapBankServiceImpl implements CapBankService {

    @Autowired private CapControlCreationService ccCreationService;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PointDao pointDao;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private AttributeService attributeService;
    @Autowired private CbcHelperService cbcHelperService;
    
    private Logger log = YukonLogManager.getLogger(getClass());


    @Override
    public CapBank getCapBank(int id) {
        
        assertCapBankExists(id);

        CapBank capbank = new CapBank();

        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        DBPersistent dbPersistent = dbPersistentDao.retrieveDBPersistent(pao);
        DeviceBase deviceBase = (DeviceBase) dbPersistent;
        if(deviceBase  instanceof CapBank) {
            capbank = (CapBank) deviceBase;
        }
        
        return capbank;
    }
    
    
    @Override
    public List<CCMonitorBankList> getUnassignedPoints(CapBank capBank) {
        List<CCMonitorBankList> unassignedPoints = new ArrayList<>();
        List<Integer> alreadyAssignedPoints = new ArrayList<>();
        capBank.getCcMonitorBankList().forEach(monitor -> alreadyAssignedPoints.add(monitor.getId()));
        int controlDeviceId = capBank.getCapBank().getControlDeviceID().intValue();
        if (controlDeviceId > 0) {
            List<LitePoint> allPoints = pointDao.getLitePointsByPaObjectId(controlDeviceId);
            allPoints.stream().filter(point -> UnitOfMeasure.getForId(point.getUofmID()) == UnitOfMeasure.VOLTS)
                .forEach(voltPoint -> {
                    if (!alreadyAssignedPoints.contains(voltPoint.getLiteID())){
                        CapBankMonitorPointParams monitorPoint = new CapBankMonitorPointParams();
                        monitorPoint.setPointId(voltPoint.getLiteID());
                        monitorPoint.setDeviceId(controlDeviceId);
                        monitorPoint.setPointName(voltPoint.getPointName());
                        CCMonitorBankList monitorListPoint = new CCMonitorBankList(monitorPoint);
                        unassignedPoints.add(monitorListPoint);
                    }
                });
        }
            
        return unassignedPoints;
    }
    

    @Override
    public void setAssignedPoints(CapBank capbank) {
        List<CCMonitorBankList> bankList = capbank.getCcMonitorBankList();
        for (CCMonitorBankList point : bankList) {
            LitePoint lPoint = pointDao.getLitePoint(point.getPointId());
            point.setName(lPoint.getPointName());
        }
        
        Collections.sort(bankList, DISPLAY_ORDER_COMPARATOR);
        capbank.setCcMonitorBankList(bankList);

    }

    @Override
    @Transactional
    public int save(CapBank capbank) {
        if(capbank.getCapBank().getControlDeviceID() != 0) {
            com.cannontech.database.db.capcontrol.CapBank dbCapBank = capbank.getCapBank();
            int controlPointId = cbcHelperService.getControlPointIdForCbc(dbCapBank.getControlDeviceID());
            dbCapBank.setControlPointID(controlPointId);
        }
        
        if (capbank.getId() == null) {
            if(!capbank.getCbcControllerName().isEmpty()){
                DeviceConfiguration configuration = deviceConfigurationDao.getDefaultDNPConfiguration();

                int portId = capbank.getCbcCommChannel() != null ? capbank.getCbcCommChannel() : 0;
                PaoIdentifier cbcId = ccCreationService.createCbc(capbank.getCbcType(), capbank.getCbcControllerName(), false, portId, configuration);
                capbank.getCapBank().setControlDeviceID(cbcId.getPaoId());
                LitePoint point = attributeService.findPointForAttribute(cbcId, BuiltInAttribute.CONTROL_POINT);
                if (point != null) {
                    capbank.getCapBank().setControlPointID(point.getPointID());
                }
            }
            create(capbank);
        } else {
            assertCapBankExists(capbank.getId());
            dbPersistentDao.performDBChange(capbank,  TransactionType.UPDATE);
        }

        return capbank.getId();
    }
    
    private int create(CapBank capbank) {
        CompleteCapBank completeCapbank = new CompleteCapBank();
        completeCapbank.setPaoName(capbank.getName());
        completeCapbank.setDisabled(capbank.isDisabled());
        completeCapbank.setControlDeviceId(capbank.getCapBank().getControlDeviceID());
        completeCapbank.setControlPointId(capbank.getCapBank().getControlPointID());
        paoPersistenceService.createPaoWithDefaultPoints(completeCapbank, capbank.getPaoType());
        capbank.setId(completeCapbank.getPaObjectId());
        return capbank.getId();
    }

    @Override
    public boolean delete(int id) {
        CapBank capbank = getCapBank(id);
        dbPersistentDao.performDBChange(capbank, TransactionType.DELETE);
        return true;
    }


    /**
     * @throws NotFoundException if the given id is not a capbank
     */
    private void assertCapBankExists(int id) throws NotFoundException {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null || pao.getPaoType() != PaoType.CAPBANK) {
            throw new NotFoundException("No capbank with id " + id + " found.");
        }
    }
    
    
    private static final Comparator<CCMonitorBankList> DISPLAY_ORDER_COMPARATOR = new Comparator<CCMonitorBankList>() {
        @Override
        public int compare(CCMonitorBankList o1, CCMonitorBankList o2) {
            Integer order1 = o1.getDisplayOrder();
            Integer order2 = o2.getDisplayOrder();
            int result = order1.compareTo(order2);
            if (result == 0) {
                result = new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
            }

            return result;
        }
    };


    @Override
    public void savePoints(int capbankId, Integer[] pointIds) {
        CapBank capbank = getCapBank(capbankId);
        List<CCMonitorBankList> existingList = capbank.getCcMonitorBankList();
        int displayOrder = 1;
        Connection connection = PoolManager.getInstance()
                .getConnection(CtiUtilities.getDatabaseAlias());
        for (int pointId : pointIds) {
            boolean update = false;
            CapBankMonitorPointParams monitorPoint = new CapBankMonitorPointParams();
            monitorPoint.setDeviceId(capbank.getCapBank().getDeviceID());
            monitorPoint.setPointId(pointId);
            CCMonitorBankList monitorListPoint = new CCMonitorBankList(monitorPoint);            
            for (CCMonitorBankList existingMonitor : existingList) {
                if (existingMonitor.getPointId().equals(pointId)){
                    monitorListPoint = existingMonitor;
                    update = true;
                }
            }

            monitorListPoint.setDisplayOrder(displayOrder);
            monitorListPoint.setDbConnection(connection);
            
            try {
                if (update) {
                    monitorListPoint.update();
                } else {
                    monitorListPoint.add();
                }
            } catch (SQLException e) {
                log.warn("Exception while updating/adding points for CapBank", e);
            }
            displayOrder++;
        }
        
        //delete any that were removed
        existingList.stream().filter(point -> !Arrays.asList(pointIds).contains(point.getId()))
            .forEach(deletePoint -> {
                try {
                    deletePoint.setDbConnection(connection);
                    deletePoint.delete();
                } catch (SQLException e) {
                    log.warn("Exception while deleting points from CapBank", e);
                }
        });
        
        dbChangeManager.processPaoDbChange(PaoIdentifier.of(capbankId, PaoType.CAPBANK), DbChangeType.UPDATE);
    }


}
