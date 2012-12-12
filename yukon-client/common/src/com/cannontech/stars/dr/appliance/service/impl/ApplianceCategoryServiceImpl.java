package com.cannontech.stars.dr.appliance.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.db.web.YukonWebConfiguration;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.LMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteApplianceCategory;
import com.cannontech.stars.database.data.lite.LiteLMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteWebConfiguration;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.appliance.service.ApplianceCategoryService;
import com.cannontech.stars.dr.hardware.dao.ProgramToAlternateProgramDao;
import com.cannontech.stars.dr.hardware.model.ProgramToAlternateProgram;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ApplianceCategoryServiceImpl implements ApplianceCategoryService {
    
	@Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private ProgramToAlternateProgramDao programToSeasonalProgramDao;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    @Transactional
    public void save(ApplianceCategory applianceCategory, YukonUserContext userContext) {
        int applianceCategoryId = applianceCategory.getApplianceCategoryId();
        boolean isNew = (applianceCategoryId <= 0);

        YukonWebConfiguration webConfiguration = new YukonWebConfiguration();
        webConfiguration.setLogoLocation(applianceCategory.getIcon());
        webConfiguration.setAlternateDisplayName(applianceCategory.getDisplayName());
        webConfiguration.setDescription(applianceCategory.getDescription().replaceAll(LINE_SEPARATOR, "<br>"));
        webConfiguration.setURL("");

        LiteStarsEnergyCompany energyCompany =
            starsDatabaseCache.getEnergyCompany(applianceCategory.getEnergyCompanyId());
        // look up the appliance type id based on the appliance type
        int applianceTypeId =
            YukonListEntryHelper.getListEntryId(energyCompany,
                                                YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
                                                applianceCategory.getApplianceType().getDefinitionId());

        com.cannontech.stars.database.data.appliance.ApplianceCategory appCat =
            new com.cannontech.stars.database.data.appliance.ApplianceCategory();
        com.cannontech.stars.database.db.appliance.ApplianceCategory appCatDB =
            appCat.getApplianceCategory();
        appCatDB.setCategoryID(applianceTypeId);
        appCatDB.setDescription(applianceCategory.getName());
        appCatDB.setConsumerSelectable(applianceCategory.isConsumerSelectable());
        appCat.setWebConfiguration(webConfiguration);

        LiteApplianceCategory liteApplianceCateogry = null;
        if (isNew) {
            appCat.setEnergyCompanyID(applianceCategory.getEnergyCompanyId());

            dbPersistentDao.performDBChange(appCat, TransactionType.INSERT);

            liteApplianceCateogry = (LiteApplianceCategory) StarsLiteFactory.createLite(appCat.getApplianceCategory());
            energyCompany.addApplianceCategory(liteApplianceCateogry);
            LiteWebConfiguration liteConfig =
                (LiteWebConfiguration) StarsLiteFactory.createLite(appCat.getWebConfiguration());
            starsDatabaseCache.addWebConfiguration(liteConfig);
            applianceCategory.setApplianceCategoryId(liteApplianceCateogry.getApplianceCategoryID());
        } else {
            liteApplianceCateogry = energyCompany.getApplianceCategory(applianceCategoryId);
            if (energyCompany.isApplianceCategoryInherited(applianceCategoryId)) {
                throw new RuntimeException("Cannot update an inherited appliance category"); 
            }

            appCat.setApplianceCategoryID(applianceCategoryId);
            appCatDB.setWebConfigurationID(liteApplianceCateogry.getWebConfigurationID());

            dbPersistentDao.performDBChange(appCat, TransactionType.UPDATE);

            StarsLiteFactory.setLiteApplianceCategory(liteApplianceCateogry,
                                                      appCat.getApplianceCategory());
            LiteWebConfiguration liteConfig =
                starsDatabaseCache.getWebConfiguration(appCat.getWebConfiguration().getConfigurationID().intValue());
            StarsLiteFactory.setLiteWebConfiguration(liteConfig, appCat.getWebConfiguration());
        }
    }

    @Override
    public void delete(int applianceCategoryId, YukonUserContext userContext) {
        int ecId = applianceCategoryDao.getEnergyCompanyForApplianceCategory(applianceCategoryId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        if (energyCompany.isApplianceCategoryInherited(applianceCategoryId)) {
            throw new RuntimeException("Cannot delete inherited appliance category.");
        }                
        try {
            StarsAdminUtil.deleteApplianceCategory(applianceCategoryId,
                                                   energyCompany,
                                                   userContext.getYukonUser());
        } catch (TransactionException transactionException) {
            throw new RuntimeException("transaction exception deleting appliance category",
                                       transactionException);
        }
    }

    @Override
    @Transactional
    public void assignProgram(AssignedProgram assignedProgram,
            YukonUserContext userContext) {
        int applianceCategoryId = assignedProgram.getApplianceCategoryId();
        int ecId = applianceCategoryDao.getEnergyCompanyForApplianceCategory(applianceCategoryId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        LiteApplianceCategory liteApplianceCategory =
            energyCompany.getApplianceCategory(applianceCategoryId);

        int assignedProgramId = assignedProgram.getAssignedProgramId();
        LiteLMProgramWebPublishing liteProgram = null;
        int highestProgramOrder = 0;
        for (LiteLMProgramWebPublishing program :
            liteApplianceCategory.getPublishedPrograms()) {
            if (assignedProgramId == 0) {
                highestProgramOrder =
                    Math.max(highestProgramOrder, program.getProgramOrder());
            } else if (program.getProgramID() == assignedProgramId) {
                liteProgram = program;
                break;
            }
        }

        if (assignedProgramId != 0 && liteProgram == null) {
            throw new RuntimeException("could not find assigned program " +
                                       assignedProgramId + " to update");
        }

        if (liteProgram == null) {
            assignedProgram.setProgramOrder(highestProgramOrder + 1);
        }

        updateAssignedProgram(energyCompany, liteApplianceCategory,
                              assignedProgram, liteProgram);
    }

    @Override
    @Transactional
    public void unassignProgram(int applianceCategoryId, int assignedProgramId,
            YukonUserContext userContext) {
        int ecId = applianceCategoryDao.getEnergyCompanyForApplianceCategory(applianceCategoryId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        try {
            StarsAdminUtil.deleteLMProgramWebPublishing(assignedProgramId,
                                                        energyCompany,
                                                        userContext.getYukonUser());
            // compress program orders
            LiteApplianceCategory liteApplianceCategory =
                energyCompany.getApplianceCategory(applianceCategoryId);
            List<LiteLMProgramWebPublishing> applianceCategoryPrograms =
                Lists.newArrayList(liteApplianceCategory.getPublishedPrograms());
            Collections.sort(applianceCategoryPrograms, new Comparator<LiteLMProgramWebPublishing>() {
                @Override
                public int compare(LiteLMProgramWebPublishing o1, LiteLMProgramWebPublishing o2) {
                    return o1.getProgramOrder() - o2.getProgramOrder();
                }
            });
            int programOrder = 1;
            for (LiteLMProgramWebPublishing program : applianceCategoryPrograms) {
                AssignedProgram assignedProgram =
                    assignedProgramDao.getById(program.getProgramID());
                assignedProgram.setProgramOrder(programOrder++);
                updateAssignedProgram(energyCompany, liteApplianceCategory,
                                      assignedProgram, program);
            }
        } catch (TransactionException te) {
            throw new RuntimeException("error removing assigned program " +
                                       assignedProgramId + " from db", te);
        }
    }

    @Override
    @Transactional
    public void moveAssignedProgramUp(int applianceCategoryId,
            int assignedProgramId, YukonUserContext userContext) {
        moveAssignedProgram(applianceCategoryId, assignedProgramId,
                            userContext, -1);
    }

    @Override
    @Transactional
    public void moveAssignedProgramDown(int applianceCategoryId,
            int assignedProgramId, YukonUserContext userContext) {
        moveAssignedProgram(applianceCategoryId, assignedProgramId,
                            userContext, 1);
    }

    private void moveAssignedProgram(int applianceCategoryId,
            int assignedProgramId, YukonUserContext userContext, int offset) {
        int ecId = applianceCategoryDao.getEnergyCompanyForApplianceCategory(applianceCategoryId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        LiteApplianceCategory liteApplianceCategory =
            energyCompany.getApplianceCategory(applianceCategoryId);
        Iterable<LiteLMProgramWebPublishing> applianceCategoryPrograms =
            liteApplianceCategory.getPublishedPrograms();

        LiteLMProgramWebPublishing liteProgram = null;
        for (LiteLMProgramWebPublishing program : applianceCategoryPrograms) {
            if (program.getProgramID() == assignedProgramId) {
                liteProgram = program;
            }
        }
        if (liteProgram == null) {
            throw new RuntimeException("could not find assigned program " +
                                       assignedProgramId);
        }

        int oldProgramOrder = liteProgram.getProgramOrder();
        int newProgramOrder = oldProgramOrder + offset;

        LiteLMProgramWebPublishing liteProgramToSwapWith = null;
        for (LiteLMProgramWebPublishing program : applianceCategoryPrograms) {
            if (program.getProgramOrder() == newProgramOrder) {
                liteProgramToSwapWith = program;
            }
        }
        if (liteProgramToSwapWith == null) {
            throw new RuntimeException("could not find assigned program " +
                                       "with order " + newProgramOrder);
        }

        AssignedProgram assignedProgram =
            assignedProgramDao.getById(liteProgram.getProgramID());
        assignedProgram.setProgramOrder(newProgramOrder);
        updateAssignedProgram(energyCompany, liteApplianceCategory,
                              assignedProgram, liteProgram);
        assignedProgram =
            assignedProgramDao.getById(liteProgramToSwapWith.getProgramID());
        assignedProgram.setProgramOrder(oldProgramOrder);
        updateAssignedProgram(energyCompany, liteApplianceCategory,
                              assignedProgram, liteProgramToSwapWith);
    }

    private void updateAssignedProgram(LiteStarsEnergyCompany lec,
            LiteApplianceCategory lac,
            AssignedProgram ap,
            LiteLMProgramWebPublishing liteLmPWP) {
    	
        int programPaoId = ap.getProgramId();

        LMProgramWebPublishing pubProg = new LMProgramWebPublishing();
        com.cannontech.stars.database.db.LMProgramWebPublishing pubProgDB =
            pubProg.getLMProgramWebPublishing();
        pubProgDB.setApplianceCategoryID(lac.getApplianceCategoryID());
        pubProgDB.setDeviceID(programPaoId);
        pubProgDB.setChanceOfControlID(ap.getChanceOfControlId());

        YukonWebConfiguration cfg = new YukonWebConfiguration();
        cfg.setLogoLocation(ap.getWebConfiguration().getLogoLocation());
        cfg.setAlternateDisplayName(ap.getWebConfiguration().getAlternateDisplayName());
        cfg.setDescription(ap.getDescription().replaceAll(LINE_SEPARATOR, "<br>"));
        cfg.setURL("");
        pubProg.setWebConfiguration(cfg);
        pubProgDB.setProgramOrder(ap.getProgramOrder());

        if (liteLmPWP != null) {
            pubProg.setProgramID(liteLmPWP.getProgramID());
            pubProg.getLMProgramWebPublishing().setWebSettingsID(liteLmPWP.getWebSettingsID());
            dbPersistentDao.performDBChange(pubProg, TransactionType.UPDATE);

            liteLmPWP.setChanceOfControlID(pubProg.getLMProgramWebPublishing().getChanceOfControlID().intValue());
            liteLmPWP.setProgramOrder(pubProg.getLMProgramWebPublishing().getProgramOrder().intValue());

            LiteWebConfiguration liteCfg = starsDatabaseCache.getWebConfiguration(liteLmPWP.getWebSettingsID());
            StarsLiteFactory.setLiteWebConfiguration(liteCfg,
                                                     pubProg.getWebConfiguration());
        } else {
            dbPersistentDao.performDBChange(pubProg, TransactionType.INSERT);
            liteLmPWP = (LiteLMProgramWebPublishing) StarsLiteFactory.createLite(pubProg.getLMProgramWebPublishing());
            lec.addProgram(liteLmPWP, lac);

            LiteWebConfiguration liteCfg = (LiteWebConfiguration) StarsLiteFactory.createLite(pubProg.getWebConfiguration());
            starsDatabaseCache.addWebConfiguration(liteCfg);
        }
        
        Integer altProgramId = ap.getAlternateProgramId();
	 	if (altProgramId != null) {
	 	    ProgramToAlternateProgram mapping = ProgramToAlternateProgram.of(liteLmPWP.getProgramID(), altProgramId);
	 	    programToSeasonalProgramDao.save(mapping);
	 	} else {
	 		programToSeasonalProgramDao.delete(liteLmPWP.getProgramID());
	 	}
    }

}
