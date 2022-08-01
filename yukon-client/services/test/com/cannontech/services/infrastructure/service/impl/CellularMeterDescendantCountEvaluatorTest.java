package com.cannontech.services.infrastructure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;

@ExtendWith(MockitoExtension.class)
public class CellularMeterDescendantCountEvaluatorTest {

    @Mock GlobalSettingDao globalSettingDao;
    @Mock IDatabaseCache dbCache;
    @Mock RfnDeviceDao rfnDeviceDao;
    
    @InjectMocks
    CellularMeterDescendantCountEvaluator evaluator = new CellularMeterDescendantCountEvaluator();
    
    @Test
    public void testInfratructureWarningEvaluation() {
        
        int warningThresholdForTest = 100;
        
        List<LiteYukonPAObject> cellMeterPaos = generateCellularMeterYukonPAObjects();
        List<DynamicRfnDeviceData> deviceDatas = generateDynamicRfnDeviceDataForPaos(cellMeterPaos);
        
        // Set every other count to be in violation, starting with the second
        Boolean shouldBeWarned = false;
        for (DynamicRfnDeviceData data : deviceDatas) {
            if (shouldBeWarned) {
                data.setDescendantCount(warningThresholdForTest + 15);
            }
            shouldBeWarned = !shouldBeWarned;
        }
        Integer expectedWarnings = deviceDatas.size() / 2;
        
        Mockito.when(globalSettingDao.getInteger(GlobalSettingType.CELLULAR_METER_DESCENDANT_COUNT_WARNING_THRESHOLD)).thenReturn(warningThresholdForTest);
        Mockito.when(dbCache.getAllYukonPAObjects()).thenReturn(cellMeterPaos);
        Mockito.when(rfnDeviceDao.getDynamicRfnDeviceData(Mockito.anyCollection())).thenReturn(deviceDatas);
        List<InfrastructureWarning> warnings = evaluator.evaluate();
        
        assertEquals(expectedWarnings, warnings.size());
        
    }
    
    /**
     * Generates a list of LiteYukonPAObjects, creating 3 of each PaoType that is a
     * RFMESH class and DEVICE category
     * 
     */
    private List<LiteYukonPAObject> generateYukonPAObjectsForMock() {
        List<LiteYukonPAObject> paoList = new ArrayList<LiteYukonPAObject>();
        int paoId = 1;
        for (PaoType paoType : PaoType.values()) {
            if (paoType.getPaoClass().equals(PaoClass.RFMESH) && paoType.getPaoCategory().equals(PaoCategory.DEVICE)) {
                for (int i = 0; i < 3; i++) {
                    String paoName = paoType.name() + String.valueOf(i);
                    LiteYukonPAObject newPao = new LiteYukonPAObject(paoId, 
                            paoName, 
                            paoType.getPaoCategory(), 
                            paoType.getPaoClass(),
                            paoType,
                            paoName,
                            "false");
                    paoList.add(newPao);
                }
            }
        }
        return paoList;
    }
    
    /**
     * Generates a list of LiteYukonPAObjects creating 2 of each cellular meter type
     */
    private List<LiteYukonPAObject> generateCellularMeterYukonPAObjects() {
        List<LiteYukonPAObject> paoList = new ArrayList<LiteYukonPAObject>();
        int paoId = 1;
        for (PaoType paoType : PaoType.getCellularMeterTypes()) {
            for (int i = 0; i < 2; i++) {
                String paoName = paoType.name() + String.valueOf(i);
                LiteYukonPAObject newPao = new LiteYukonPAObject(paoId, 
                        paoName, 
                        paoType.getPaoCategory(), 
                        paoType.getPaoClass(),
                        paoType,
                        paoName,
                        "false");
                paoList.add(newPao);
            }
        }
        return paoList;
    }
    
    /**
     * Generates a list of DynamicRfnDeviceData for each YukonPao provided
     */
    private List<DynamicRfnDeviceData> generateDynamicRfnDeviceDataForPaos(Collection<LiteYukonPAObject> liteYukonPaos) {
        
        String gatewaySerialNumber = "12122121";
        String gatewayManufacturer = "EATON";
        String gatewayModel = "Virtual Gateway";
        Integer gatewayPaoId = 12323213;
        String gatewayName = "myGateway";
        RfnIdentifier gatewayId = new RfnIdentifier(gatewaySerialNumber, gatewayManufacturer, gatewayModel);
        LiteYukonPAObject gatewayLitePao = generateQuickVirtualGateway(gatewayPaoId, gatewaySerialNumber); 
        RfnDevice gatewayDevice = new RfnDevice(gatewayName, gatewayLitePao, gatewayId);
        
        List<DynamicRfnDeviceData> generatedDeviceDatas = new ArrayList<>();
        int serialNumber = 100;
        int descendantCount = 95; // Arbitrary, expected that caller will modify this to their needs
        for (LiteYukonPAObject pao : liteYukonPaos) {
            RfnManufacturerModel manModel = RfnManufacturerModel.getForType(pao.getPaoType()).get(0); // In the case of multiple entries for PAO, we only want the first
            RfnIdentifier identifier = new RfnIdentifier(String.valueOf(serialNumber), manModel.getManufacturer(), manModel.getModel());
            RfnDevice device = new RfnDevice(pao.getPaoName(), pao, identifier);
            DynamicRfnDeviceData deviceData = new DynamicRfnDeviceData(device, gatewayDevice, descendantCount, new Instant());
            generatedDeviceDatas.add(deviceData);
        }
        return generatedDeviceDatas;
    }
    
    /**
     * Quickly generates a virtual gateway for a paoId and serialNumber you provide. Serial number is only used in the name
     */
    private LiteYukonPAObject generateQuickVirtualGateway(Integer paoId, String serialNumber) {
        String gatewayName = "gateway" + serialNumber;
        var gateway = new LiteYukonPAObject(paoId,
                gatewayName,
                PaoCategory.DEVICE,
                PaoClass.RFMESH,
                PaoType.VIRTUAL_GATEWAY,
                gatewayName,
                "false");
        return gateway;
    }
    
}
