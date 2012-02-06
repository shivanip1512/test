package com.cannontech.capcontrol.creation.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.model.CompleteCapBank;
import com.cannontech.common.pao.model.CompleteCapControlArea;
import com.cannontech.common.pao.model.CompleteCapControlFeeder;
import com.cannontech.common.pao.model.CompleteCapControlSubstation;
import com.cannontech.common.pao.model.CompleteCapControlSubstationBus;
import com.cannontech.common.pao.model.CompleteCbcBase;
import com.cannontech.common.pao.model.CompleteOneWayCbc;
import com.cannontech.common.pao.model.CompleteTwoWayCbc;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class CapControlImportServiceImpl implements CapControlImportService {

    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private CapbankControllerDao capbankControllerDao;
    @Autowired private SubstationDao substationDao;
    @Autowired private CapbankDao capbankDao;
    @Autowired private FeederDao feederDao;
    @Autowired private SubstationBusDao substationBusDao;

    private abstract class PaoRetriever {
        abstract CompleteYukonPao retrievePao(PaoIdentifier pao, HierarchyImportData hierarchyImportData);
    }

    final Map<PaoType, PaoRetriever> paoRetrievers;

    // I should go into the constructor
    public CapControlImportServiceImpl() {
        Builder<PaoType, PaoRetriever> builder = ImmutableMap.builder();
        builder.put(PaoType.CAP_CONTROL_AREA, new PaoRetriever() {
            @Override
            CompleteYukonPao retrievePao(PaoIdentifier pao,
                                              HierarchyImportData hierarchyImportData) {
                return paoPersistenceService.retreivePao(pao, CompleteCapControlArea.class);
            }
        });
        builder.put(PaoType.CAP_CONTROL_FEEDER, new PaoRetriever() {
            @Override
            CompleteYukonPao retrievePao(PaoIdentifier pao,
                                              HierarchyImportData hierarchyImportData) {
                CompleteCapControlFeeder feeder =
                        paoPersistenceService.retreivePao(pao, CompleteCapControlFeeder.class);
                if (hierarchyImportData.getMapLocationId() != null) {
                    feeder.setMapLocationId(hierarchyImportData.getMapLocationId());
                }
                return feeder;
            }
        });
        builder.put(PaoType.CAP_CONTROL_SUBSTATION, new PaoRetriever() {
            @Override
            CompleteYukonPao retrievePao(PaoIdentifier pao,
                                              HierarchyImportData hierarchyImportData) {
                CompleteCapControlSubstation substation =
                        paoPersistenceService.retreivePao(pao, CompleteCapControlSubstation.class);
                if (hierarchyImportData.getMapLocationId() != null) {
                    substation.setMapLocationId(hierarchyImportData.getMapLocationId());
                }
                return substation;
            }
        });
        builder.put(PaoType.CAP_CONTROL_SUBBUS, new PaoRetriever() {
            @Override
            CompleteYukonPao retrievePao(PaoIdentifier pao,
                                              HierarchyImportData hierarchyImportData) {
                CompleteCapControlSubstationBus substationBus =
                        paoPersistenceService.retreivePao(pao, CompleteCapControlSubstationBus.class);
                if (hierarchyImportData.getMapLocationId() != null) {
                    substationBus.setMapLocationId(hierarchyImportData.getMapLocationId());
                }
                return substationBus;
            }
        });
        builder.put(PaoType.CAPBANK, new PaoRetriever() {
            @Override
            CompleteYukonPao retrievePao(PaoIdentifier pao,
                                              HierarchyImportData hierarchyImportData) {
                CompleteCapBank capBank =
                        paoPersistenceService.retreivePao(pao, CompleteCapBank.class);
                if (hierarchyImportData.getMapLocationId() != null) {
                    capBank.setMapLocationId(hierarchyImportData.getMapLocationId());
                }
                if (hierarchyImportData.getBankOpState() != null) {
                    capBank.setOperationalState(hierarchyImportData.getBankOpState());
                }
                if (hierarchyImportData.getCapBankSize() != null) {
                    capBank.setBankSize(hierarchyImportData.getCapBankSize());
                }
                return capBank;
            }
        });

        paoRetrievers = builder.build();
    }
    
    private CompleteYukonPao createHierarchyCompletePao(HierarchyImportData hierarchyImportData,
                                                   List<HierarchyImportResult> results) {
        PaoType paoType = hierarchyImportData.getPaoType();
        HierarchyPaoCreator creator = HierarchyPaoCreator.valueOf(paoType.name());
        
        if (creator == null) {
            results.add(new HierarchyImportResult(hierarchyImportData,
                                                  HierarchyImportResultType.INVALID_TYPE));
            return null;
        }
        
        return creator.getCompleteYukonPao(hierarchyImportData);
    }

    @Override
    @Transactional
    public void createCbc(CbcImportData cbcImportData, List<CbcImportResult> results) throws NotFoundException {
        YukonPao yukonPao = retrieveCbcPao(cbcImportData.getCbcName());
        if (yukonPao != null) {
            // We were told to add a device that already exists. This is an error!
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.OBJECT_EXISTS));
            return;
        }
        
        Integer parentId = getParentId(cbcImportData);
        
        CompleteCbcBase pao;
        if (paoDefinitionDao.isTagSupported(cbcImportData.getCbcType(), PaoTag.ONE_WAY_DEVICE)) {
            pao = new CompleteOneWayCbc();
        } else if (paoDefinitionDao.isTagSupported(cbcImportData.getCbcType(), PaoTag.TWO_WAY_DEVICE)) {
            pao = new CompleteTwoWayCbc();
            CompleteTwoWayCbc cbc = (CompleteTwoWayCbc)pao;
            cbc.setMasterAddress(cbcImportData.getMasterAddress());
            cbc.setSlaveAddress(cbcImportData.getSlaveAddress());
            if (cbcImportData.getAltInterval() != null) {
                cbc.setAlternateRate(cbcImportData.getAltInterval());
            }
            if (cbcImportData.getScanInterval() != null ){
                cbc.setIntervalRate(cbcImportData.getScanInterval());
            }
            
            YukonPao commChannel = paoDao.findYukonPao(cbcImportData.getCommChannel(),
                                                       PaoCategory.PORT, PaoClass.PORT);
            if (commChannel != null) {
                cbc.setPortId(commChannel.getPaoIdentifier().getPaoId());
            } else {
                results.add(new CbcImportResult(cbcImportData, CbcImportResultType.INVALID_COMM_CHANNEL));
                return;
            }
        } else {
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.INVALID_COMM_CHANNEL));
            return;
        }

        pao.setSerialNumber(cbcImportData.getCbcSerialNumber());
        pao.setPaoName(cbcImportData.getCbcName());
        
        paoPersistenceService.createPao(pao, cbcImportData.getCbcType());
        
        int paoId = pao.getPaObjectId();
        
        // parentId would still be null if no parent was specified.
        if (parentId != null) {
            capbankControllerDao.assignController(parentId, paoId);
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
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.OBJECT_EXISTS));
            return;
        }
        
        Integer parentId = getParentId(cbcImportData);
        
        PaoIdentifier templateIdentifier = templatePao.getPaoIdentifier();
        // Get a copy of the points from the template to copy to the new CBC.
        int templatePaoId = templateIdentifier.getPaoId();
        List<PointBase> copyPoints = pointDao.getPointsForPao(templatePaoId);
        
        CompleteCbcBase template;
        if (paoDefinitionDao.isTagSupported(templateIdentifier.getPaoType(), PaoTag.ONE_WAY_DEVICE)) {
            template = paoPersistenceService.retreivePao(templateIdentifier, CompleteOneWayCbc.class);
        } else if (paoDefinitionDao.isTagSupported(templateIdentifier.getPaoType(), PaoTag.TWO_WAY_DEVICE)) {
            template = paoPersistenceService.retreivePao(templateIdentifier, CompleteTwoWayCbc.class);
            CompleteTwoWayCbc cbc = (CompleteTwoWayCbc)template;
            cbc.setMasterAddress(cbcImportData.getMasterAddress());
            cbc.setSlaveAddress(cbcImportData.getSlaveAddress());
            
            YukonPao commChannel = paoDao.findYukonPao(cbcImportData.getCommChannel(),
                                                       PaoCategory.PORT, PaoClass.PORT);
            if (commChannel != null) {
                cbc.setPortId(commChannel.getPaoIdentifier().getPaoId());
            } else {
                results.add(new CbcImportResult(cbcImportData, CbcImportResultType.INVALID_COMM_CHANNEL));
                return;
            }
            
            if (cbcImportData.getAltInterval() != null) {
                cbc.setAlternateRate(cbcImportData.getAltInterval());
            }
            if (cbcImportData.getScanInterval() != null) {
                cbc.setIntervalRate(cbcImportData.getScanInterval());
            }
        } else {
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.INVALID_PARENT));
            return;
        }

        template.setPaoName(cbcImportData.getCbcName());
        template.setSerialNumber(cbcImportData.getCbcSerialNumber());
        
        paoPersistenceService.createPaoWithCustomPoints(template, template.getPaoType(), copyPoints);
        
        int paoId = template.getPaObjectId();
        
        if (parentId != null) {
            capbankControllerDao.assignController(parentId, paoId);
        }

        results.add(new CbcImportResult(cbcImportData, CbcImportResultType.SUCCESS));
    }

    @Override
    @Transactional
    public void updateCbc(CbcImportData cbcImportData, List<CbcImportResult> results) throws NotFoundException {
        YukonPao yukonPao = retrieveCbcPao(cbcImportData.getCbcName());
        if (yukonPao == null) {
            // We were told to update a device that doesn't exist. This is an error!
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.NO_SUCH_OBJECT));
            return;
        }

        Integer parentId = getParentId(cbcImportData);

        CompleteCbcBase pao;
        if (paoDefinitionDao.isTagSupported(cbcImportData.getCbcType(), PaoTag.ONE_WAY_DEVICE)) {
            pao = paoPersistenceService.retreivePao(yukonPao.getPaoIdentifier(), CompleteOneWayCbc.class);
        } else if (paoDefinitionDao.isTagSupported(cbcImportData.getCbcType(), PaoTag.TWO_WAY_DEVICE)) {
            pao = paoPersistenceService.retreivePao(yukonPao.getPaoIdentifier(), CompleteTwoWayCbc.class);
            CompleteTwoWayCbc cbc = (CompleteTwoWayCbc)pao;
            cbc.setMasterAddress(cbcImportData.getMasterAddress());
            cbc.setSlaveAddress(cbcImportData.getSlaveAddress());
            
            if (cbcImportData.getAltInterval() != null) {
                cbc.setAlternateRate(cbcImportData.getAltInterval());
            }
            if (cbcImportData.getScanInterval() != null) {
                cbc.setIntervalRate(cbcImportData.getScanInterval());
            }
            
            YukonPao commChannel = paoDao.findYukonPao(cbcImportData.getCommChannel(),
                                                       PaoCategory.PORT, PaoClass.PORT);
            if (commChannel != null) {
                cbc.setPortId(commChannel.getPaoIdentifier().getPaoId());
            } else {
                results.add(new CbcImportResult(cbcImportData, CbcImportResultType.INVALID_COMM_CHANNEL));
                return;
            }
        } else {
            results.add(new CbcImportResult(cbcImportData, CbcImportResultType.INVALID_TYPE));
            return;
        }
        
        pao.setSerialNumber(cbcImportData.getCbcSerialNumber());
        
        paoPersistenceService.updatePao(pao);

        if (parentId != null) {
            capbankControllerDao.assignController(parentId, yukonPao.getPaoIdentifier().getPaoId());
        }

        results.add(new CbcImportResult(cbcImportData, CbcImportResultType.SUCCESS));
    }

    private Integer getParentId(CbcImportData cbcImportData) {
        String capBankName = cbcImportData.getCapBankName();
        Integer parentId = null;
        if (capBankName != null) {
            parentId = getParentId(capBankName, PaoType.CAPBANK);
        }
        return parentId;
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

        paoPersistenceService.deletePao(paoIdentifier);

        results.add(new CbcImportResult(cbcImportData, CbcImportResultType.SUCCESS));
    }

    @Override
    @Transactional
    public void createHierarchyObject(HierarchyImportData hierarchyImportData,
                                      List<HierarchyImportResult> results) throws NotFoundException {
        YukonPao yukonPao = findHierarchyPao(hierarchyImportData);
        if (yukonPao != null) {
            // We were told to add an object that already exists. This is an error!
            results.add(new HierarchyImportResult(hierarchyImportData,
                                                  HierarchyImportResultType.OBJECT_EXISTS));
            return;
        }

        CompleteYukonPao pao = createHierarchyCompletePao(hierarchyImportData, results);
        paoPersistenceService.createPao(pao, hierarchyImportData.getPaoType());
        int childId = pao.getPaObjectId();
        
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
        
        PaoRetriever paoRetriever = paoRetrievers.get(pao.getPaoIdentifier().getPaoType());
        if (paoRetriever == null) {
            results.add(new HierarchyImportResult(hierarchyImportData,
                                                  HierarchyImportResultType.INVALID_TYPE));
            return;
        }
        CompleteYukonPao completePao = paoRetriever.retrievePao(pao.getPaoIdentifier(), hierarchyImportData);
        
        paoPersistenceService.updatePao(completePao);
        
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
        
        paoPersistenceService.deletePao(paoIdentifier);
        
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
}