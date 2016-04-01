package com.cannontech.web.capcontrol.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
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
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.web.capcontrol.service.CapBankService;
import com.cannontech.web.capcontrol.service.CbcService;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class CapBankServiceImpl implements CapBankService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CapControlCache ccCache;
    @Autowired private PointDao pointDao;
    @Autowired private CapbankDao capbankDao;
    @Autowired private CbcService cbcService;
    @Autowired private PaoPersistenceService paoPersistenceService;

    
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
            List<CCMonitorBankList> unassignedPoints = new ArrayList<CCMonitorBankList>();
            int controlDeviceId = capBank.getCapBank().getControlDeviceID().intValue();
            if (controlDeviceId > 0) {
                List<LitePoint> allPoints = pointDao.getLitePointsByPaObjectId(controlDeviceId);
                for (int i = 0; i < allPoints.size(); i++) {
                    LitePoint point = allPoints.get(i);
                    
                    if (UnitOfMeasure.getForId(point.getUofmID()) == UnitOfMeasure.VOLTS) {
                        CapBankMonitorPointParams monitorPoint = new CapBankMonitorPointParams();
                        monitorPoint.setPointId(point.getLiteID());
                        monitorPoint.setDeviceId(controlDeviceId);
                        monitorPoint.setPointName(point.getPointName());
                        // set the feeder limits by default
                        setDefaultFeederLimits(capBank, monitorPoint);
                        
                        CCMonitorBankList monitorListPoint = new CCMonitorBankList(monitorPoint);

                        //check if already assigned
                        boolean alreadyAssigned = false;
                        for(CCMonitorBankList bankPoint : capBank.getCcMonitorBankList()) {
                            if(bankPoint.getPointId().equals(monitorPoint.getPointId())){
                                alreadyAssigned = true;
                                break;
                            }
                        }
                        if (!alreadyAssigned) {
                            unassignedPoints.add(monitorListPoint);
                        }
                    }
                }
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
    
    private void setDefaultFeederLimits(CapBank capBank, CapBankMonitorPointParams monitorPoint) {
        int fdrId = 0;
        try {
            fdrId = capbankDao.getParentFeederIdentifier(capBank.getPAObjectID().intValue()).getPaoId();
        }
        catch( EmptyResultDataAccessException e) {
            CTILogger.debug("Feeder " + capBank.getPAObjectID().intValue() + " not found. Capbank may be orphaned.");
        }
        
        if (fdrId != 0) {
            Feeder feeder = ccCache.getFeeder(fdrId);
            monitorPoint.setLowerBandwidth((float)feeder.getPeakLag());
            monitorPoint.setUpperBandwidth((float)feeder.getPeakLead());
        }
    }

    @Override
    @Transactional
    public int save(CapBank capbank) {
        if(capbank.getCapBank().getControlDeviceID() != 0) {
            LitePoint point = pointDao.getLitePointIdByDeviceId_Offset_PointType(capbank.getCapBank().getControlDeviceID(), 1, PointTypes.STATUS_POINT);
            capbank.getCapBank().setControlPointID(point.getPointID());
        }
        
        if (capbank.getId() == null) {
            if(!capbank.getCbcControllerName().isEmpty()){
                CapControlCBC cbc = new CapControlCBC();
                cbc.setName(capbank.getCbcControllerName());
                cbc.setPaoType(capbank.getCbcType());
                if(capbank.getCbcCommChannel() != null && capbank.getCbcCommChannel() != 0) {
                    DeviceDirectCommSettings commSettings = new DeviceDirectCommSettings();
                    commSettings.setPortID(capbank.getCbcCommChannel());
                    cbc.setDeviceDirectCommSettings(commSettings);
                }
                int cbcId = cbcService.create(cbc);
                capbank.getCapBank().setControlDeviceID(cbcId);
                LitePoint point = pointDao.getLitePointIdByDeviceId_Offset_PointType(capbank.getCapBank().getControlDeviceID(), 1, PointTypes.STATUS_POINT);
                capbank.getCapBank().setControlPointID(point.getPointID());
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
                    setDefaultFeederLimits(capbank, monitorPoint);
                    monitorListPoint.setUpperBandwidth((float)monitorPoint.getUpperBandwidth());
                    monitorListPoint.setLowerBandwidth((float)monitorPoint.getLowerBandwidth());
                    monitorListPoint.add();
                }
            } catch (SQLException e) {
                log.warn("caught exception in savePoints", e);
            }
            displayOrder++;
        }
        
        //delete any that were removed
        for (CCMonitorBankList existingMonitor : existingList) {
            boolean delete = true;
            for (int pointId : pointIds) {
                if(existingMonitor.getPointId().equals(pointId)) {
                    delete = false;
                }
            }
            if (delete) {
                try {
                    existingMonitor.setDbConnection(connection);
                    existingMonitor.delete();
                } catch (SQLException e) {
                    log.warn("caught exception in savePoints", e);
                }
            }
        }   
    }


}
