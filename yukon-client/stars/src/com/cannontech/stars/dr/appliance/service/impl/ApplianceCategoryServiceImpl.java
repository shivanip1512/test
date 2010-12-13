package com.cannontech.stars.dr.appliance.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.web.YukonWebConfiguration;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.appliance.service.ApplianceCategoryService;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.YukonUserContext;

public class ApplianceCategoryServiceImpl implements ApplianceCategoryService {
    private StarsDatabaseCache starsDatabaseCache;
    private AssignedProgramDao assignedProgramDao;
    private DBPersistentDao dbPersistentDao;

    private static final String LINE_SEPARATOR =
        System.getProperty("line.separator");
    @Override
    @Transactional
    public void save(ApplianceCategory applianceCategory,
            YukonUserContext userContext) {
        int applianceCategoryId = applianceCategory.getApplianceCategoryId();
        boolean isNew = (applianceCategoryId <= 0);

        YukonWebConfiguration webConfiguration = new YukonWebConfiguration();
        webConfiguration.setLogoLocation(applianceCategory.getIcon());
        webConfiguration.setAlternateDisplayName(applianceCategory.getDisplayName());
        webConfiguration.setDescription(applianceCategory.getDescription().replaceAll(LINE_SEPARATOR, "<br>"));
        webConfiguration.setURL("");

        LiteStarsEnergyCompany energyCompany =
            starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        // look up the appliance type id based on the appliance type
        int applianceTypeId =
            YukonListEntryHelper.getListEntryId(energyCompany,
                                                YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
                                                applianceCategory.getApplianceType().getDefinitionId());

        com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
            new com.cannontech.database.data.stars.appliance.ApplianceCategory();
        com.cannontech.database.db.stars.appliance.ApplianceCategory appCatDB =
            appCat.getApplianceCategory();
        appCatDB.setCategoryID(applianceTypeId);
        appCatDB.setDescription(applianceCategory.getName());
        appCatDB.setConsumerSelectable(applianceCategory.isConsumerSelectable());
        appCat.setWebConfiguration(webConfiguration);

        LiteApplianceCategory liteApplianceCateogry = null;
        if (isNew) {
            appCat.setEnergyCompanyID(energyCompany.getEnergyCompanyID());

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
    @Transactional
    public void assignProgram(AssignedProgram assignedProgram,
            YukonUserContext userContext) {
        LiteStarsEnergyCompany energyCompany =
            starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        LiteApplianceCategory liteApplianceCategory =
            energyCompany.getApplianceCategory(assignedProgram.getApplianceCategoryId());

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
        LiteStarsEnergyCompany energyCompany =
            starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        try {
            StarsAdminUtil.deleteLMProgramWebPublishing(assignedProgramId,
                                                        energyCompany,
                                                        userContext.getYukonUser());
            // compress program orders
            LiteApplianceCategory liteApplianceCategory =
                energyCompany.getApplianceCategory(applianceCategoryId);
            Iterable<LiteLMProgramWebPublishing> applianceCategoryPrograms =
                liteApplianceCategory.getPublishedPrograms();
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
        LiteStarsEnergyCompany energyCompany =
            starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
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

    private void updateAssignedProgram(LiteStarsEnergyCompany energyCompany,
            LiteApplianceCategory liteApplianceCategory,
            AssignedProgram assignedProgram,
            LiteLMProgramWebPublishing liteProgram) {
        int programPaoId = assignedProgram.getProgramId();

        com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
            new com.cannontech.database.data.stars.LMProgramWebPublishing();
        com.cannontech.database.db.stars.LMProgramWebPublishing pubProgDB =
            pubProg.getLMProgramWebPublishing();
        pubProgDB.setApplianceCategoryID(liteApplianceCategory.getApplianceCategoryID());
        pubProgDB.setDeviceID(programPaoId);
        pubProgDB.setChanceOfControlID(assignedProgram.getChanceOfControlId());

        YukonWebConfiguration cfg = new YukonWebConfiguration();
        cfg.setLogoLocation(assignedProgram.getWebConfiguration().getLogoLocation());
        cfg.setAlternateDisplayName(assignedProgram.getWebConfiguration().getAlternateDisplayName());
        cfg.setDescription(assignedProgram.getDescription().replaceAll(LINE_SEPARATOR, "<br>"));
        cfg.setURL("");
        pubProg.setWebConfiguration(cfg);
        pubProgDB.setProgramOrder(assignedProgram.getProgramOrder());

        if (liteProgram != null) {
            pubProg.setProgramID(liteProgram.getProgramID());
            pubProg.getLMProgramWebPublishing().setWebSettingsID(liteProgram.getWebSettingsID());
            dbPersistentDao.performDBChange(pubProg, TransactionType.UPDATE);

            liteProgram.setChanceOfControlID(pubProg.getLMProgramWebPublishing().getChanceOfControlID().intValue());
            liteProgram.setProgramOrder(pubProg.getLMProgramWebPublishing().getProgramOrder().intValue());

            LiteWebConfiguration liteCfg = starsDatabaseCache.getWebConfiguration(liteProgram.getWebSettingsID());
            StarsLiteFactory.setLiteWebConfiguration(liteCfg,
                                                     pubProg.getWebConfiguration());
        } else {
            dbPersistentDao.performDBChange(pubProg, TransactionType.INSERT);
            liteProgram = (LiteLMProgramWebPublishing) StarsLiteFactory.createLite(pubProg.getLMProgramWebPublishing());
            energyCompany.addProgram(liteProgram, liteApplianceCategory);

            LiteWebConfiguration liteCfg = (LiteWebConfiguration) StarsLiteFactory.createLite(pubProg.getWebConfiguration());
            starsDatabaseCache.addWebConfiguration(liteCfg);
        }
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    @Autowired
    public void setAssignedProgramDao(AssignedProgramDao assignedProgramDao) {
        this.assignedProgramDao = assignedProgramDao;
    }

    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
}
