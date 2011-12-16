package com.cannontech.capcontrol.creation.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.creation.service.CapControlImportService;
import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.dao.providers.fields.AreaFields;
import com.cannontech.capcontrol.dao.providers.fields.CapBankFields;
import com.cannontech.capcontrol.dao.providers.fields.CapbankAdditionalFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceCbcFields;
import com.cannontech.capcontrol.dao.providers.fields.FeederFields;
import com.cannontech.capcontrol.dao.providers.fields.SpecialAreaFields;
import com.cannontech.capcontrol.dao.providers.fields.SubstationBusFields;
import com.cannontech.capcontrol.dao.providers.fields.SubstationFields;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.service.PaoCreationService;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.pao.service.providers.fields.DeviceAddressFields;
import com.cannontech.common.pao.service.providers.fields.DeviceDirectCommSettingsFields;
import com.cannontech.common.pao.service.providers.fields.DeviceFields;
import com.cannontech.common.pao.service.providers.fields.DeviceScanRateFields;
import com.cannontech.common.pao.service.providers.fields.DeviceWindowFields;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.data.point.PointBase;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class CapControlImportServiceImpl implements CapControlImportService {

    private PaoCreationService paoCreationService;
    private PaoDao paoDao;
    private PointDao pointDao;
    private CapbankControllerDao capbankControllerDao;
    private SubstationDao substationDao;
    private CapbankDao capbankDao;
    private FeederDao feederDao;
    private SubstationBusDao substationBusDao;

    private PaoTemplate createCbcPaoTemplate(CbcImportData cbcImportData,
                                             List<CbcImportResult> results) {
        PaoType paoType = cbcImportData.getCbcType();

        ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
        YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(cbcImportData.getCbcName());
        paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);

        DeviceFields deviceFields = new DeviceFields();
        paoFields.put(DeviceFields.class, deviceFields);

        // Set up the fields based on the CBC type.
        switch (paoType) {
        case CBC_7020:
        case CBC_7022:
        case CBC_7023:
        case CBC_7024:
        case CBC_8020:
        case CBC_8024:
        case CBC_DNP:
        case DNP_CBC_6510:
            DeviceScanRateFields deviceScanRateFields = new DeviceScanRateFields();
            Integer altInterval = cbcImportData.getAltInterval();
            if (altInterval != null) {
                deviceScanRateFields.setAlternateRate(altInterval);
            }

            Integer scanInterval = cbcImportData.getScanInterval();
            if (scanInterval != null) {
                deviceScanRateFields.setIntervalRate(scanInterval);
            }
            paoFields.put(DeviceScanRateFields.class, deviceScanRateFields);

            DeviceWindowFields deviceWindowFields = new DeviceWindowFields();
            paoFields.put(DeviceWindowFields.class, deviceWindowFields);

            YukonPao commChannel = paoDao.findYukonPao(cbcImportData.getCommChannel(),
                                                       PaoCategory.PORT, PaoClass.PORT);
            if (commChannel == null) {
                // Can't create this guy without a valid comm channel.
                results.add(new CbcImportResult(cbcImportData,
                                                CbcImportResultType.INVALID_COMM_CHANNEL));
                return null;
            } else {
                DeviceDirectCommSettingsFields commSettingsFields =
                        new DeviceDirectCommSettingsFields(commChannel.getPaoIdentifier().getPaoId());
                paoFields.put(DeviceDirectCommSettingsFields.class, commSettingsFields);
            }

            if (paoType != PaoType.DNP_CBC_6510) {
                DeviceCbcFields cbcFields = new DeviceCbcFields();
                cbcFields.setSerialNumber(cbcImportData.getCbcSerialNumber());
                paoFields.put(DeviceCbcFields.class, cbcFields);
            }

            DeviceAddressFields addressFields = new DeviceAddressFields();
            addressFields.setMasterAddress(cbcImportData.getMasterAddress());
            addressFields.setSlaveAddress(cbcImportData.getSlaveAddress());
            paoFields.put(DeviceAddressFields.class, addressFields);

            break;

        case CBC_7010:
        case CBC_7011:
        case CBC_7012:
        case CBC_EXPRESSCOM:
        case CBC_FP_2800:
        case CAPBANKCONTROLLER:
            DeviceCbcFields deviceCbcFields = new DeviceCbcFields();
            deviceCbcFields.setSerialNumber(cbcImportData.getCbcSerialNumber());
            paoFields.put(DeviceCbcFields.class, deviceCbcFields);

            break;

        default:
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.INVALID_TYPE));
            return null;
        }

        PaoTemplate paoTemplate = new PaoTemplate(paoType, paoFields);

        return paoTemplate;
    }

    private PaoTemplate createHierarchyPaoTemplate(HierarchyImportData hierarchyImportData,
                                                   List<HierarchyImportResult> results) {
        ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();

        /* The default value for the MapLocationId in all Fields objects is "0". We 
         * want to use the default if our data has a null value. Perhaps in the year 
         * 2000 there will be a fancier way to do this, like a public static variable
         * and/or method that will give us the default value from the Fields object.
         */
        String dataLocationId = hierarchyImportData.getMapLocationId();
        String mapLocationId = (dataLocationId != null) ? dataLocationId : "0";

        switch (hierarchyImportData.getPaoType()) {
        case CAP_CONTROL_AREA:
            paoFields.put(AreaFields.class, new AreaFields());

            break;
        case CAP_CONTROL_SPECIAL_AREA:
            paoFields.put(SpecialAreaFields.class, new SpecialAreaFields());

            break;
        case CAP_CONTROL_SUBSTATION:
            SubstationFields substationFields = new SubstationFields();
            substationFields.setMapLocationId(mapLocationId);
            paoFields.put(SubstationFields.class, substationFields);

            break;
        case CAP_CONTROL_SUBBUS:
            SubstationBusFields substationBusFields = new SubstationBusFields();
            substationBusFields.setMapLocationId(mapLocationId);
            paoFields.put(SubstationBusFields.class, substationBusFields);

            break;
        case CAP_CONTROL_FEEDER:
            FeederFields feederFields = new FeederFields();
            feederFields.setMapLocationId(mapLocationId);
            paoFields.put(FeederFields.class, feederFields);

            break;
        case CAPBANK:
            CapBankFields capBankFields = new CapBankFields();
            capBankFields.setMapLocationId(mapLocationId);
            paoFields.put(CapBankFields.class, capBankFields);

            paoFields.put(DeviceFields.class, new DeviceFields());
            paoFields.put(CapbankAdditionalFields.class, new CapbankAdditionalFields());

            break;
        default:
            results.add(new HierarchyImportResult(hierarchyImportData,
                                                  HierarchyImportResultType.INVALID_TYPE));
            return null;
        }

        // Create and set-up the YukonPaObjectFields
        YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(hierarchyImportData.getName());
        
        // We don't want to attempt to write null values to the database!
        if (hierarchyImportData.getDescription() != null) {
            yukonPaObjectFields.setDescription(hierarchyImportData.getDescription());
        }
        
        if (hierarchyImportData.isDisabled() != null) {
            yukonPaObjectFields.setDisabled(YNBoolean.valueOf(hierarchyImportData.isDisabled()));
        }
        
        paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);

        PaoTemplate paoTemplate = new PaoTemplate(hierarchyImportData.getPaoType(), paoFields);

        return paoTemplate;
    }

    private PaoTemplate createTemplateDeviceData(PaoIdentifier paoIdentifier,
                                                 CbcImportData cbcImportData,
                                                 List<CbcImportResult> results) {
        ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();

        paoFields.put(YukonPaObjectFields.class,
                      paoDao.getYukonPaObjectData(paoIdentifier, cbcImportData.getCbcName()));

        YukonPao commChannel = paoDao.findYukonPao(cbcImportData.getCommChannel(),
                                                   PaoCategory.PORT, PaoClass.PORT);
        if (commChannel == null) {
            // Can't create this guy without a valid comm channel.
            results.add(new CbcImportResult(cbcImportData,
                                            CbcImportResultType.INVALID_COMM_CHANNEL));
            return null;
        } else {
            DeviceDirectCommSettingsFields commSettingsFields =
                new DeviceDirectCommSettingsFields(commChannel.getPaoIdentifier().getPaoId());
            paoFields.put(DeviceDirectCommSettingsFields.class, commSettingsFields);
        }

        DeviceAddressFields addressFields =
            capbankControllerDao.getDeviceAddressData(paoIdentifier);
        addressFields.setMasterAddress(cbcImportData.getMasterAddress());
        addressFields.setSlaveAddress(cbcImportData.getSlaveAddress());
        paoFields.put(DeviceAddressFields.class, addressFields);

        // Nothing in DeviceCBC needs to come from the Template Device.
        DeviceCbcFields deviceCbcFields = new DeviceCbcFields();
        deviceCbcFields.setSerialNumber(cbcImportData.getCbcSerialNumber());
        paoFields.put(DeviceCbcFields.class, deviceCbcFields);

        paoFields.put(DeviceFields.class, capbankControllerDao.getDeviceData(paoIdentifier));

        DeviceScanRateFields scanRateFields =
            capbankControllerDao.getDeviceScanRateData(paoIdentifier);
        scanRateFields.setAlternateRate(cbcImportData.getAltInterval());
        scanRateFields.setIntervalRate(cbcImportData.getScanInterval());
        paoFields.put(DeviceScanRateFields.class, scanRateFields);

        try {
            // This stuff might not be here, so we could get a DataAccessException. If we do, just
            // create and use the default one.
            paoFields.put(DeviceWindowFields.class,
                          capbankControllerDao.getDeviceWindowData(paoIdentifier));
        } catch (DataAccessException e) {
            paoFields.put(DeviceWindowFields.class, new DeviceWindowFields());
        }

        PaoTemplate paoTemplate = new PaoTemplate(paoIdentifier.getPaoType(), paoFields);

        return paoTemplate;
    }

    @Override
    @Transactional
    public void createCbc(CbcImportData cbcImportData, List<CbcImportResult> results) throws NotFoundException {
        YukonPao pao = retrieveCbcPao(cbcImportData.getCbcName());
        if (pao != null) {
            // We were told to add a device that already exists. This is an error!
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.DEVICE_EXISTS));
            return;
        }

        PaoTemplate paoTemplate = createCbcPaoTemplate(cbcImportData, results);

        if (paoTemplate == null) {
            return;
        }

        String capBankName = cbcImportData.getCapBankName();
        Integer parentId = null;
        if (capBankName != null) {
            parentId = getParentId(capBankName, PaoType.CAPBANK);
        }

        PaoIdentifier newPao = paoCreationService.createPao(paoTemplate);

        // parentId would still be null if no parent was specified.
        if (parentId != null) {
            capbankControllerDao.assignController(parentId, newPao.getPaoId());
        }

        results.add(new CbcImportResult(cbcImportData, CbcImportResultType.SUCCESS));
    }

    @Override
    @Transactional
    public void createCbcFromTemplate(CbcImportData cbcImportData, List<CbcImportResult> results) throws NotFoundException {
        YukonPao templatePao = retrieveCbcPao(cbcImportData.getTemplateName());
        if (templatePao == null) {
            // The template didn't exist.
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.NO_SUCH_OBJECT));
            return;
        }

        YukonPao pao = retrieveCbcPao(cbcImportData.getCbcName());
        if (pao != null) {
            // The object we're trying to make already exists.
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.DEVICE_EXISTS));
            return;
        }

        PaoIdentifier templateIdentifier = templatePao.getPaoIdentifier();

        // Load up the template device data, then inject our import data.
        PaoTemplate newCbcTemplate =
            createTemplateDeviceData(templateIdentifier, cbcImportData, results);

        // Get a copy of the points from the template to copy to the new CBC.
        int templatePaoId = templatePao.getPaoIdentifier().getPaoId();
        List<PointBase> copyPoints = pointDao.getPointsForPao(templatePaoId);

        String capBankName = cbcImportData.getCapBankName();
        Integer parentId = null;
        if (capBankName != null) {
            parentId = getParentId(capBankName, PaoType.CAPBANK);
        }

        PaoIdentifier paoIdentifier =
            paoCreationService.createPaoWithCustomPoints(newCbcTemplate, copyPoints);

        if (parentId != null) {
            capbankControllerDao.assignController(parentId, paoIdentifier.getPaoId());
        }

        results.add(new CbcImportResult(cbcImportData, CbcImportResultType.SUCCESS));
    }

    @Override
    @Transactional
    public void updateCbc(CbcImportData cbcImportData, List<CbcImportResult> results) throws NotFoundException {
        YukonPao pao = retrieveCbcPao(cbcImportData.getCbcName());
        if (pao == null) {
            // We were told to update a device that doesn't exist. This is an error!
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.NO_SUCH_OBJECT));
            return;
        }

        PaoTemplate paoTemplate = createCbcPaoTemplate(cbcImportData, results);

        if (paoTemplate == null) {
            // There was a problem creating the template. The results list already has the reason why.
            return;
        }

        String capBankName = cbcImportData.getCapBankName();
        Integer parentId = null;
        if (capBankName != null) {
            parentId = getParentId(capBankName, PaoType.CAPBANK);
        }

        paoCreationService.updatePao(pao.getPaoIdentifier().getPaoId(), paoTemplate);

        if (parentId != null) {
            capbankControllerDao.assignController(parentId, pao.getPaoIdentifier().getPaoId());
        }

        results.add(new CbcImportResult(cbcImportData, CbcImportResultType.SUCCESS));
    }

    @Override
    @Transactional
    public void removeCbc(CbcImportData cbcImportData, List<CbcImportResult> results) {
        YukonPao pao = retrieveCbcPao(cbcImportData.getCbcName());
        if (pao == null) {
            // We were told to remove a device that doesn't exist. This is an error!
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.NO_SUCH_OBJECT));
            return;
        }

        PaoIdentifier paoIdentifier = pao.getPaoIdentifier();

        // Unassign the device from any capbank it may be attached to.
        capbankControllerDao.unassignController(paoIdentifier.getPaoId());

        paoCreationService.deletePao(paoIdentifier);

        results.add(new CbcImportResult(cbcImportData, CbcImportResultType.SUCCESS));
    }

    @Override
    @Transactional
    public void createHierarchyObject(HierarchyImportData hierarchyImportData,
                                      List<HierarchyImportResult> results) throws NotFoundException {
        YukonPao pao = findHierarchyPao(hierarchyImportData);
        if (pao != null) {
            // We were told to add an object that already exists. This is an error!
            results.add(new HierarchyImportResult(hierarchyImportData,
                                                  HierarchyImportResultType.OBJECT_EXISTS));
            return;
        }

        PaoTemplate paoTemplate = createHierarchyPaoTemplate(hierarchyImportData, results);

        int childId = paoCreationService.createPao(paoTemplate).getPaoId();

        // This will throw if the parent doesn't exist, preventing creation of the child, as
        // desired.
        String parentName = hierarchyImportData.getParent();
        if (parentName != null) {
            int parentId = getParentId(parentName, PaoCategory.CAPCONTROL, PaoClass.CAPCONTROL);
            if (!createHierarchyParentLink(hierarchyImportData, parentId, childId, results)) {
                // Invalid child type or parent type. We don't have a success here.
                return;
            }
        }

        results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultType.SUCCESS));
    }

    @Override
    @Transactional
    public void updateHierarchyObject(HierarchyImportData hierarchyImportData,
                                      List<HierarchyImportResult> results) throws NotFoundException {
        YukonPao pao = findHierarchyPao(hierarchyImportData);
        if (pao == null) {
            // We were told to remove an object that doesn't exist. This is an error!
            results.add(new HierarchyImportResult(hierarchyImportData,
                                                  HierarchyImportResultType.NO_SUCH_OBJECT));
            return;
        }

        int childId = pao.getPaoIdentifier().getPaoId();
        PaoTemplate paoTemplate = createHierarchyPaoTemplate(hierarchyImportData, results);

        paoCreationService.updatePao(childId, paoTemplate);
        
        // This will throw if the parent doesn't exist, preventing the update of the child, as
        // desired.
        String parentName = hierarchyImportData.getParent();
        if (parentName != null) {
            int parentId = getParentId(parentName, PaoCategory.CAPCONTROL, PaoClass.CAPCONTROL);
            if (!createHierarchyParentLink(hierarchyImportData, parentId, childId, results)) {
                // Invalid child type or parent type. We don't have a success here.
                return;
            }
        }

        results.add(new HierarchyImportResult(hierarchyImportData,
                                              HierarchyImportResultType.SUCCESS));
    };

    @Override
    @Transactional
    public void removeHierarchyObject(HierarchyImportData hierarchyImportData,
                                      List<HierarchyImportResult> results) {
        YukonPao pao = findHierarchyPao(hierarchyImportData);
        if (pao == null) {
            // We were told to remove a device that doesn't exist. This is an error!
            results.add(new HierarchyImportResult(hierarchyImportData,
                                                  HierarchyImportResultType.NO_SUCH_OBJECT));
            return;
        }

        PaoIdentifier paoIdentifier = pao.getPaoIdentifier();

        paoCreationService.deletePao(paoIdentifier);

        results.add(new HierarchyImportResult(hierarchyImportData,
                                              HierarchyImportResultType.SUCCESS));
    };
    
    private boolean parentTypeIsValid(int childId, int parentId) {
        PaoType parentType = paoDao.getYukonPao(parentId).getPaoIdentifier().getPaoType();
        PaoType childType  = paoDao.getYukonPao(childId).getPaoIdentifier().getPaoType();
        
        switch (childType) {
        case CAP_CONTROL_AREA:
            return true;
        case CAP_CONTROL_SUBSTATION:
            return (parentType == PaoType.CAP_CONTROL_AREA);
        case CAP_CONTROL_SUBBUS:
            return (parentType == PaoType.CAP_CONTROL_SUBSTATION);
        case CAP_CONTROL_FEEDER:
            return (parentType == PaoType.CAP_CONTROL_SUBBUS);
        case CAPBANK:
            return (parentType == PaoType.CAP_CONTROL_FEEDER);
        default:
            return false;
        }
    }

    private int getParentId(String parentName, PaoType paoType) throws NotFoundException {
        return getParentId(parentName, paoType.getPaoCategory(), paoType.getPaoClass());
    }
    
    private int getParentId(String parentName, PaoCategory paoCategory, PaoClass paoClass) throws NotFoundException {
        YukonPao parentPao = paoDao.getYukonPao(parentName, paoCategory, paoClass);
        return parentPao.getPaoIdentifier().getPaoId();
    }

    private YukonPao findHierarchyPao(HierarchyImportData data) {
        String hierarchyName = data.getName();
        PaoType paoType = data.getPaoType();

        YukonPao pao = paoDao.findYukonPao(hierarchyName, paoType);

        return pao;
    }

    private YukonPao retrieveCbcPao(String name) {
        // We know all CBCs have a Category of Device and a PAOClass of Capcontrol.
        return paoDao.findYukonPao(name, PaoCategory.DEVICE, PaoClass.CAPCONTROL);
    }

    private boolean createHierarchyParentLink(HierarchyImportData hierarchyImportData, int parentId,
                                           int childId,
                                           List<HierarchyImportResult> results) {
        if(!parentTypeIsValid(childId, parentId)) {
            results.add(new HierarchyImportResult(hierarchyImportData,
                                                  HierarchyImportResultType.INVALID_PARENT));
            return false;
        }
        
        switch (hierarchyImportData.getPaoType()) {
        case CAP_CONTROL_AREA:
        case CAP_CONTROL_SPECIAL_AREA:
            // We don't want to do anything here, areas don't have parents
            return true;
        case CAP_CONTROL_SUBSTATION:
            return substationDao.assignSubstation(parentId, childId);
        case CAP_CONTROL_SUBBUS:
            return substationBusDao.assignSubstationBus(parentId, childId);
        case CAP_CONTROL_FEEDER:
            return feederDao.assignFeeder(parentId, childId);
        case CAPBANK:
            return capbankDao.assignCapbank(parentId, childId);
        default:
            results.add(new HierarchyImportResult(hierarchyImportData,
                                                  HierarchyImportResultType.INVALID_TYPE));
            return false;
        }
    }
    
    @Autowired
    public void setFeederDao(FeederDao feederDao) {
        this.feederDao = feederDao;
    }
    
    @Autowired
    public void setSubstationBusDao(SubstationBusDao substationBusDao) {
        this.substationBusDao = substationBusDao;
    }

    @Autowired
    public void setPaoCreationService(PaoCreationService paoCreationService) {
        this.paoCreationService = paoCreationService;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
        this.capbankControllerDao = capbankControllerDao;
    }
    
    @Autowired
    public void setCapbankDao(CapbankDao capbankDao) {
        this.capbankDao = capbankDao;
    }
    
    @Autowired
    public void setSubstationDao(SubstationDao substationDao) {
        this.substationDao = substationDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
}
