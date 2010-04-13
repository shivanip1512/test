package com.cannontech.stars.dr.appliance.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.appliance.ApplianceAirConditioner;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.database.db.stars.appliance.ApplianceChiller;
import com.cannontech.database.db.stars.appliance.ApplianceDualFuel;
import com.cannontech.database.db.stars.appliance.ApplianceDualStageAirCond;
import com.cannontech.database.db.stars.appliance.ApplianceGenerator;
import com.cannontech.database.db.stars.appliance.ApplianceGrainDryer;
import com.cannontech.database.db.stars.appliance.ApplianceHeatPump;
import com.cannontech.database.db.stars.appliance.ApplianceIrrigation;
import com.cannontech.database.db.stars.appliance.ApplianceStorageHeat;
import com.cannontech.database.db.stars.appliance.ApplianceWaterHeater;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsApplianceDao;
import com.cannontech.stars.dr.appliance.service.StarsApplianceService;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.xml.serialize.StarsAppliance;

public class StarsApplianceServiceImpl implements StarsApplianceService {

    private StarsApplianceDao starsApplianceDao;

    @Override
    public void createStarsAppliance(StarsAppliance starsAppliance,
                                     int energyCompanyId, int accountId) {

        try {
            com.cannontech.database.data.stars.appliance.ApplianceBase app = 
                new com.cannontech.database.data.stars.appliance.ApplianceBase();
            ApplianceBase appDB = app.getApplianceBase();

            appDB.setAccountID(accountId);
            appDB.setApplianceCategoryID(starsAppliance.getApplianceCategory()
                .getApplianceCategoryId());
            appDB.setProgramID(starsAppliance.getProgramID());
            appDB.setManufacturerID(starsAppliance.getManufacturer()
                .getEntryID());
            appDB.setLocationID(starsAppliance.getLocation().getEntryID());
            appDB.setNotes(starsAppliance.getNotes());
            appDB.setModelNumber(starsAppliance.getModelNumber());

            if (starsAppliance.hasYearManufactured())
                appDB.setYearManufactured(starsAppliance.getYearManufactured());
            if (starsAppliance.hasKwCapacity())
                appDB.setKWCapacity(starsAppliance.getKwCapacity());
            if (starsAppliance.hasEfficiencyRating())
                appDB.setEfficiencyRating(starsAppliance.getEfficiencyRating());

            if (starsAppliance.getInventoryID() > 0) {
                app.getLMHardwareConfig()
                    .setInventoryID(starsAppliance.getInventoryID());
                app.getLMHardwareConfig()
                    .setLoadNumber(starsAppliance.getLoadNumber());
                if (app.getLMHardwareConfig().getAddressingGroupID() == 0
                    && starsAppliance.getProgramID() > 0) {
                    Integer groupID = 
                        InventoryUtils.getYukonLoadGroupIDFromSTARSProgramID(
                           starsAppliance.getProgramID());
                    app.getLMHardwareConfig().setAddressingGroupID(groupID);
                }
            }

            app = (com.cannontech.database.data.stars.appliance.ApplianceBase) 
                      Transaction.createTransaction(Transaction.INSERT, app).execute();

            starsAppliance.setApplianceID(app.getApplianceBase().getApplianceID());
            LiteStarsAppliance liteApp = new LiteStarsAppliance();
            StarsLiteFactory.setLiteStarsAppliance(liteApp, app);

            switch (starsAppliance.getApplianceCategory().getApplianceType()) {
            case AIR_CONDITIONER:
                ApplianceAirConditioner appAC = new ApplianceAirConditioner();
                appAC.setApplianceID(app.getApplianceBase().getApplianceID());
                appAC.setTonnageID(starsAppliance.getAirConditioner().getTonnage().getEntryID());
                appAC.setTypeID(starsAppliance.getAirConditioner().getAcType().getEntryID());
                appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.INSERT,
                                                                                appAC).execute();

                liteApp.setAirConditioner(new LiteStarsAppliance.AirConditioner());
                StarsLiteFactory.setLiteAppAirConditioner(liteApp.getAirConditioner(),
                                                          appAC);

                break;

            case DUAL_STAGE:
                ApplianceDualStageAirCond appDS = new ApplianceDualStageAirCond();
                appDS.setApplianceID(app.getApplianceBase().getApplianceID());
                appDS.setStageOneTonnageID(starsAppliance.getDualStageAC().getTonnage().getEntryID());
                appDS.setStageTwoTonnageID(
                          starsAppliance.getDualStageAC().getStageTwoTonnage().getEntryID());
                appDS.setTypeID(starsAppliance.getDualStageAC().getAcType().getEntryID());
                appDS = (ApplianceDualStageAirCond) Transaction.createTransaction(Transaction.INSERT,
                                                                                  appDS).execute();

                liteApp.setDualStageAirCond(new LiteStarsAppliance.DualStageAirCond());
                StarsLiteFactory.setLiteAppDualStageAirCond(liteApp.getDualStageAirCond(),
                                                            appDS);

                break;

            case CHILLER:
                ApplianceChiller appChill = new ApplianceChiller();
                appChill.setApplianceID(app.getApplianceBase().getApplianceID());
                appChill.setTonnageID(starsAppliance.getChiller().getTonnage().getEntryID());
                appChill.setTypeID(starsAppliance.getChiller().getAcType().getEntryID());
                appChill = (ApplianceChiller) Transaction.createTransaction(Transaction.INSERT,
                                                                            appChill).execute();

                liteApp.setChiller(new LiteStarsAppliance.Chiller());
                StarsLiteFactory.setLiteAppChiller(liteApp.getChiller(),
                                                   appChill);

                break;

            case WATER_HEATER:
                ApplianceWaterHeater appWH = new ApplianceWaterHeater();
                appWH.setApplianceID(app.getApplianceBase().getApplianceID());
                appWH.setNumberOfGallonsID(
                          starsAppliance.getWaterHeater().getNumberOfGallons().getEntryID());
                appWH.setEnergySourceID(
                          starsAppliance.getWaterHeater().getEnergySource().getEntryID());
                if (starsAppliance.getWaterHeater().hasNumberOfElements()) {
                    appWH.setNumberOfElements(starsAppliance.getWaterHeater().getNumberOfElements());
                }
                appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.INSERT,
                                                                             appWH).execute();

                liteApp.setWaterHeater(new LiteStarsAppliance.WaterHeater());
                StarsLiteFactory.setLiteAppWaterHeater(liteApp.getWaterHeater(),
                                                       appWH);

                break;

            case DUAL_FUEL:
                ApplianceDualFuel appDF = new ApplianceDualFuel();
                appDF.setApplianceID(app.getApplianceBase().getApplianceID());
                appDF.setSwitchOverTypeID(
                          starsAppliance.getDualFuel().getSwitchOverType().getEntryID());
                appDF.setSecondaryEnergySourceID(
                          starsAppliance.getDualFuel().getSecondaryEnergySource().getEntryID());
                if (starsAppliance.getDualFuel().hasSecondaryKWCapacity()) {
                    appDF.setSecondaryKWCapacity(
                              starsAppliance.getDualFuel().getSecondaryKWCapacity());
                }
                appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.INSERT,
                                                                          appDF).execute();

                liteApp.setDualFuel(new LiteStarsAppliance.DualFuel());
                StarsLiteFactory.setLiteAppDualFuel(liteApp.getDualFuel(), appDF);

                break;

            case GENERATOR:
                ApplianceGenerator appGen = new ApplianceGenerator();
                appGen.setApplianceID(app.getApplianceBase().getApplianceID());
                appGen.setTransferSwitchTypeID(
                           starsAppliance.getGenerator().getTransferSwitchType().getEntryID());
                appGen.setTransferSwitchMfgID(
                           starsAppliance.getGenerator().getTransferSwitchManufacturer().getEntryID());
                if (starsAppliance.getGenerator().hasPeakKWCapacity()) {
                    appGen.setPeakKWCapacity(starsAppliance.getGenerator().getPeakKWCapacity());
                }
                if (starsAppliance.getGenerator().hasFuelCapGallons()) {
                    appGen.setFuelCapGallons(starsAppliance.getGenerator().getFuelCapGallons());
                }
                if (starsAppliance.getGenerator().hasStartDelaySeconds()) {
                    appGen.setStartDelaySeconds(starsAppliance.getGenerator().getStartDelaySeconds());
                }
                appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.INSERT,
                                                                            appGen).execute();

                liteApp.setGenerator(new LiteStarsAppliance.Generator());
                StarsLiteFactory.setLiteAppGenerator(liteApp.getGenerator(),
                                                     appGen);

                break;

            case GRAIN_DRYER:
                ApplianceGrainDryer appGD = new ApplianceGrainDryer();
                appGD.setApplianceID(app.getApplianceBase().getApplianceID());
                appGD.setDryerTypeID(
                          starsAppliance.getGrainDryer().getDryerType().getEntryID());
                appGD.setBinSizeID(
                          starsAppliance.getGrainDryer().getBinSize().getEntryID());
                appGD.setBlowerEnergySourceID(
                          starsAppliance.getGrainDryer().getBlowerEnergySource().getEntryID());
                appGD.setBlowerHorsePowerID(
                          starsAppliance.getGrainDryer().getBlowerHorsePower().getEntryID());
                appGD.setBlowerHeatSourceID(
                          starsAppliance.getGrainDryer().getBlowerHeatSource().getEntryID());
                appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.INSERT,
                                                                            appGD).execute();

                liteApp.setGrainDryer(new LiteStarsAppliance.GrainDryer());
                StarsLiteFactory.setLiteAppGrainDryer(liteApp.getGrainDryer(),
                                                      appGD);

                break;

            case STORAGE_HEAT:
                ApplianceStorageHeat appSH = new ApplianceStorageHeat();
                appSH.setApplianceID(app.getApplianceBase().getApplianceID());
                appSH.setStorageTypeID(
                          starsAppliance.getStorageHeat().getStorageType().getEntryID());
                if (starsAppliance.getStorageHeat().hasPeakKWCapacity()) {
                    appSH.setPeakKWCapacity(starsAppliance.getStorageHeat().getPeakKWCapacity());
                }
                if (starsAppliance.getStorageHeat().hasHoursToRecharge()) {
                    appSH.setHoursToRecharge(starsAppliance.getStorageHeat().getHoursToRecharge());
                }
                appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.INSERT,
                                                                             appSH).execute();

                liteApp.setStorageHeat(new LiteStarsAppliance.StorageHeat());
                StarsLiteFactory.setLiteAppStorageHeat(liteApp.getStorageHeat(),
                                                       appSH);

                break;

            case HEAT_PUMP:
                ApplianceHeatPump appHP = new ApplianceHeatPump();
                appHP.setApplianceID(app.getApplianceBase().getApplianceID());
                appHP.setPumpTypeID(starsAppliance.getHeatPump().getPumpType().getEntryID());
                appHP.setPumpSizeID(starsAppliance.getHeatPump().getPumpSize().getEntryID());
                appHP.setStandbySourceID(starsAppliance.getHeatPump().getStandbySource().getEntryID());
                if (starsAppliance.getHeatPump().hasRestartDelaySeconds()) {
                    appHP.setSecondsDelayToRestart(
                              starsAppliance.getHeatPump().getRestartDelaySeconds());
                }
                appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.INSERT,
                                                                          appHP).execute();

                liteApp.setHeatPump(new LiteStarsAppliance.HeatPump());
                StarsLiteFactory.setLiteAppHeatPump(liteApp.getHeatPump(),
                                                    appHP);

                break;

            case IRRIGATION:
                ApplianceIrrigation appIrr = new ApplianceIrrigation();
                appIrr.setApplianceID(app.getApplianceBase().getApplianceID());
                appIrr.setIrrigationTypeID(
                           starsAppliance.getIrrigation().getIrrigationType().getEntryID());
                appIrr.setHorsePowerID(
                           starsAppliance.getIrrigation().getHorsePower().getEntryID());
                appIrr.setEnergySourceID(
                           starsAppliance.getIrrigation().getEnergySource().getEntryID());
                appIrr.setSoilTypeID(
                           starsAppliance.getIrrigation().getSoilType().getEntryID());
                appIrr.setMeterLocationID(
                           starsAppliance.getIrrigation().getMeterLocation().getEntryID());
                appIrr.setMeterVoltageID(
                           starsAppliance.getIrrigation().getMeterVoltage().getEntryID());
                appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.INSERT,
                                                                             appIrr).execute();

                liteApp.setIrrigation(new LiteStarsAppliance.Irrigation());
                StarsLiteFactory.setLiteAppIrrigation(liteApp.getIrrigation(),
                                                      appIrr);

                break;
            }
        } catch (TransactionException e) {
            throw new RuntimeException("Error creating STARS Appliance.", e);
        }
    }

    @Override
    public void removeStarsAppliance(int applianceId, int energyCompanyId,
                                       String accountNumber, LiteYukonUser user) {
        try {
            LiteStarsAppliance liteApp = 
                starsApplianceDao.getByApplianceIdAndEnergyCompanyId(applianceId,
                                                                     energyCompanyId);
            if (liteApp == null) {
                throw new IllegalArgumentException("Cannot find the appliance to be updated");
            }

            boolean unenrollProgram = false;
            if (liteApp.getProgramID() > 0 && liteApp.getInventoryID() > 0) {
                unenrollProgram = true;
            }

            switch (liteApp.getApplianceCategory().getApplianceType()) {
            case AIR_CONDITIONER:
                ApplianceAirConditioner airConditionerApp = new ApplianceAirConditioner();
                airConditionerApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE,
                                              airConditionerApp).execute();

                break;

            case CHILLER:
                ApplianceChiller chillerApp = new ApplianceChiller();
                chillerApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE, 
                                              chillerApp).execute();

                break;

            case WATER_HEATER:
                ApplianceWaterHeater waterHeaterApp = new ApplianceWaterHeater();
                waterHeaterApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE,
                                              waterHeaterApp).execute();

                break;

            case DUAL_FUEL:
                ApplianceDualFuel dualFuelApp = new ApplianceDualFuel();
                dualFuelApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE, 
                                              dualFuelApp).execute();

                break;

            case GENERATOR:
                ApplianceGenerator generatorApp = new ApplianceGenerator();
                generatorApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE, 
                                              generatorApp).execute();

                break;

            case GRAIN_DRYER:
                ApplianceGrainDryer grainDryerApp = new ApplianceGrainDryer();
                grainDryerApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE, 
                                              grainDryerApp).execute();

                break;

            case STORAGE_HEAT:
                ApplianceStorageHeat storageHeatApp = new ApplianceStorageHeat();
                storageHeatApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE,
                                              storageHeatApp).execute();

                break;

            case HEAT_PUMP:
                ApplianceHeatPump heatPumpApp = new ApplianceHeatPump();
                heatPumpApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE, 
                                              heatPumpApp).execute();

                break;

            case IRRIGATION:
                ApplianceIrrigation irrigationApp = new ApplianceIrrigation();
                irrigationApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE, 
                                              irrigationApp).execute();

                break;

            case DUAL_STAGE:
                ApplianceDualStageAirCond dualStageApp = new ApplianceDualStageAirCond();
                dualStageApp.setApplianceID(liteApp.getApplianceID());
                Transaction.createTransaction(Transaction.DELETE, 
                                              dualStageApp).execute();

                break;
            }

            if (unenrollProgram) {
                LoadGroupDao loadGroupDao = 
                    YukonSpringHook.getBean("loadGroupDao", LoadGroupDao.class);
                LoadGroup loadGroup = loadGroupDao.getById(liteApp.getAddressingGroupID());
                String loadGroupName = loadGroup.getLoadGroupName();

                ProgramDao programDao = 
                    YukonSpringHook.getBean("starsProgramDao", ProgramDao.class);
                Program program = programDao.getByProgramId(liteApp.getProgramID());
                String programName = program.getProgramName();

                LMHardwareBaseDao lmHardwareBaseDao = 
                    YukonSpringHook.getBean("hardwareBaseDao", LMHardwareBaseDao.class);
                LMHardwareBase hardwareBase = lmHardwareBaseDao.getById(liteApp.getInventoryID());
                String serialNumber = hardwareBase.getManufacturerSerialNumber();

                EnrollmentHelper enrollmentHelper = 
                    new EnrollmentHelper(accountNumber, loadGroupName, programName, serialNumber);

                EnrollmentHelperService enrollmentHelperService = 
                    YukonSpringHook.getBean("enrollmentService", EnrollmentHelperService.class);
                enrollmentHelperService.doEnrollment(enrollmentHelper,
                                                     EnrollmentEnum.UNENROLL,
                                                     user);
            }

            com.cannontech.database.data.stars.appliance.ApplianceBase app = 
                new com.cannontech.database.data.stars.appliance.ApplianceBase();
            app.setApplianceID(liteApp.getApplianceID());
            Transaction.createTransaction(Transaction.DELETE, app).execute();
        } catch (TransactionException e) {
            throw new RuntimeException("Error removing STARS Appliance.", e);
        }
    }

    @Override
    public void updateStarsAppliance(StarsAppliance liteStarsAppliance,
                                     int energyCompanyId) {
        try {
            LiteStarsAppliance liteApp = 
                starsApplianceDao.getByApplianceIdAndEnergyCompanyId(liteStarsAppliance.getApplianceID(),
                                                                     energyCompanyId);

            if (liteApp == null) {
                throw new IllegalArgumentException("Cannot find the appliance to be updated");
            }

            com.cannontech.database.data.stars.appliance.ApplianceBase app = 
                (com.cannontech.database.data.stars.appliance.ApplianceBase) 
                    StarsLiteFactory.createDBPersistent(liteApp);
            com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
            com.cannontech.database.db.stars.hardware.LMHardwareConfiguration appConfig = 
                app.getLMHardwareConfig();

            if (liteStarsAppliance.hasApplianceCategoryID()) {
                appDB.setApplianceCategoryID(liteStarsAppliance.getApplianceCategoryID());
            }
            appDB.setManufacturerID(liteStarsAppliance.getManufacturer().getEntryID());
            appDB.setLocationID(liteStarsAppliance.getLocation().getEntryID());
            appDB.setNotes(liteStarsAppliance.getNotes());
            appDB.setModelNumber(liteStarsAppliance.getModelNumber());

            if (liteStarsAppliance.hasYearManufactured()) {
                appDB.setYearManufactured(liteStarsAppliance.getYearManufactured());
            }
            if (liteStarsAppliance.hasKwCapacity()) {
                appDB.setKWCapacity(liteStarsAppliance.getKwCapacity());
            }
            if (liteStarsAppliance.hasEfficiencyRating()) {
                appDB.setEfficiencyRating(liteStarsAppliance.getEfficiencyRating());
            }

            if (appConfig.getInventoryID() != null) {
                appConfig.setLoadNumber(liteStarsAppliance.getLoadNumber());
                if (appConfig.getAddressingGroupID().intValue() == 0
                    && liteStarsAppliance.getProgramID() > 0) {
                    Integer groupID = 
                        InventoryUtils.getYukonLoadGroupIDFromSTARSProgramID(
                                           liteStarsAppliance.getProgramID());
                    appConfig.setAddressingGroupID(groupID);
                }
            }

            Transaction.createTransaction(Transaction.UPDATE, appDB).execute();
            if (appConfig.getInventoryID() != null) {
                Transaction.createTransaction(Transaction.UPDATE, appConfig).execute();
            }
            StarsLiteFactory.setLiteStarsAppliance(liteApp, app);

            switch (liteStarsAppliance.getApplianceCategory().getApplianceType()) {
            case AIR_CONDITIONER:
                ApplianceAirConditioner appAC = new ApplianceAirConditioner();
                appAC.setApplianceID(liteApp.getApplianceID());
                appAC.setTonnageID(liteStarsAppliance.getAirConditioner().getTonnage().getEntryID());
                appAC.setTypeID(liteStarsAppliance.getAirConditioner().getAcType().getEntryID());

                if (liteApp.getAirConditioner() != null) {
                    appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.UPDATE,
                                                                                    appAC).execute();
                } else {
                    appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.INSERT,
                                                                                    appAC).execute();
                    liteApp.setAirConditioner(new LiteStarsAppliance.AirConditioner());
                }

                StarsLiteFactory.setLiteAppAirConditioner(liteApp.getAirConditioner(),
                                                          appAC);

                break;

            case DUAL_STAGE:
                ApplianceDualStageAirCond dualStageAC = new ApplianceDualStageAirCond();
                dualStageAC.setApplianceID(liteApp.getApplianceID());
                dualStageAC.setStageOneTonnageID(
                                liteStarsAppliance.getDualStageAC().getTonnage().getEntryID());
                dualStageAC.setStageTwoTonnageID(
                                liteStarsAppliance.getDualStageAC().getStageTwoTonnage().getEntryID());
                dualStageAC.setTypeID(liteStarsAppliance.getDualStageAC().getAcType().getEntryID());

                if (liteApp.getDualStageAirCond() != null) {
                    dualStageAC = 
                        (ApplianceDualStageAirCond) Transaction.createTransaction(Transaction.UPDATE,
                                                                                  dualStageAC).execute();
                } else {
                    dualStageAC =
                        (ApplianceDualStageAirCond) Transaction.createTransaction(Transaction.INSERT,
                                                                                  dualStageAC).execute();
                    liteApp.setDualStageAirCond(new LiteStarsAppliance.DualStageAirCond());
                }

                StarsLiteFactory.setLiteAppDualStageAirCond(liteApp.getDualStageAirCond(),
                                                            dualStageAC);

                break;

            case CHILLER:
                ApplianceChiller appChill = new ApplianceChiller();
                appChill.setApplianceID(liteApp.getApplianceID());
                appChill.setTonnageID(liteStarsAppliance.getChiller().getTonnage().getEntryID());
                appChill.setTypeID(liteStarsAppliance.getChiller().getAcType().getEntryID());

                if (liteApp.getChiller() != null) {
                    appChill = (ApplianceChiller) Transaction.createTransaction(Transaction.UPDATE,
                                                                                appChill).execute();
                } else {
                    appChill = (ApplianceChiller) Transaction.createTransaction(Transaction.INSERT,
                                                                                appChill).execute();
                    liteApp.setAirConditioner(new LiteStarsAppliance.AirConditioner());
                }

                StarsLiteFactory.setLiteAppChiller(liteApp.getChiller(),
                                                   appChill);

                break;

            case WATER_HEATER:
                ApplianceWaterHeater appWH = new ApplianceWaterHeater();
                appWH.setApplianceID(liteApp.getApplianceID());
                appWH.setNumberOfGallonsID(
                          liteStarsAppliance.getWaterHeater().getNumberOfGallons().getEntryID());
                appWH.setEnergySourceID(
                          liteStarsAppliance.getWaterHeater().getEnergySource().getEntryID());
                if (liteStarsAppliance.getWaterHeater().hasNumberOfElements()) {
                    appWH.setNumberOfElements(
                              liteStarsAppliance.getWaterHeater().getNumberOfElements());
                }
                if (liteApp.getWaterHeater() != null) {
                    appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.UPDATE,
                                                                                 appWH).execute();
                } else {
                    appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.INSERT,
                                                                                 appWH).execute();
                    liteApp.setWaterHeater(new LiteStarsAppliance.WaterHeater());
                }

                StarsLiteFactory.setLiteAppWaterHeater(liteApp.getWaterHeater(),
                                                       appWH);

                break;

            case DUAL_FUEL:
                ApplianceDualFuel appDF = new ApplianceDualFuel();
                appDF.setApplianceID(liteApp.getApplianceID());
                appDF.setSwitchOverTypeID(
                          liteStarsAppliance.getDualFuel().getSwitchOverType().getEntryID());
                appDF.setSecondaryEnergySourceID(
                          liteStarsAppliance.getDualFuel().getSecondaryEnergySource().getEntryID());
                if (liteStarsAppliance.getDualFuel().hasSecondaryKWCapacity()) {
                    appDF.setSecondaryKWCapacity(
                              liteStarsAppliance.getDualFuel().getSecondaryKWCapacity());
                }

                if (liteApp.getDualFuel() != null) {
                    appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.UPDATE,
                                                                              appDF).execute();
                } else {
                    appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.INSERT,
                                                                              appDF).execute();
                    liteApp.setDualFuel(new LiteStarsAppliance.DualFuel());
                }

                StarsLiteFactory.setLiteAppDualFuel(liteApp.getDualFuel(),
                                                    appDF);

                break;

            case GENERATOR:
                ApplianceGenerator appGen = new ApplianceGenerator();
                appGen.setApplianceID(liteApp.getApplianceID());
                appGen.setTransferSwitchTypeID(
                           liteStarsAppliance.getGenerator().getTransferSwitchType().getEntryID());
                appGen.setTransferSwitchMfgID(
                           liteStarsAppliance.getGenerator().getTransferSwitchManufacturer().getEntryID());
                if (liteStarsAppliance.getGenerator().hasPeakKWCapacity()) {
                    appGen.setPeakKWCapacity(liteStarsAppliance.getGenerator().getPeakKWCapacity());
                }
                if (liteStarsAppliance.getGenerator().hasFuelCapGallons()) {
                    appGen.setFuelCapGallons(liteStarsAppliance.getGenerator().getFuelCapGallons());
                }
                if (liteStarsAppliance.getGenerator().hasStartDelaySeconds()) {
                    appGen.setStartDelaySeconds(liteStarsAppliance.getGenerator().getStartDelaySeconds());
                }

                if (liteApp.getGenerator() != null) {
                    appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.UPDATE,
                                                                                appGen).execute();
                } else {
                    appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.INSERT,
                                                                                appGen).execute();
                    liteApp.setGenerator(new LiteStarsAppliance.Generator());
                }

                StarsLiteFactory.setLiteAppGenerator(liteApp.getGenerator(),
                                                     appGen);

                break;

            case GRAIN_DRYER:
                ApplianceGrainDryer appGD = new ApplianceGrainDryer();
                appGD.setApplianceID(liteApp.getApplianceID());
                appGD.setDryerTypeID(liteStarsAppliance.getGrainDryer().getDryerType().getEntryID());
                appGD.setBinSizeID(liteStarsAppliance.getGrainDryer().getBinSize().getEntryID());
                appGD.setBlowerEnergySourceID(
                          liteStarsAppliance.getGrainDryer().getBlowerEnergySource().getEntryID());
                appGD.setBlowerHorsePowerID(
                          liteStarsAppliance.getGrainDryer().getBlowerHorsePower().getEntryID());
                appGD.setBlowerHeatSourceID(
                          liteStarsAppliance.getGrainDryer().getBlowerHeatSource().getEntryID());

                if (liteApp.getGrainDryer() != null) {
                    appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.UPDATE,
                                                                                appGD).execute();
                } else {
                    appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.INSERT,
                                                                                appGD).execute();
                    liteApp.setGrainDryer(new LiteStarsAppliance.GrainDryer());
                }

                StarsLiteFactory.setLiteAppGrainDryer(liteApp.getGrainDryer(),
                                                      appGD);

                break;

            case STORAGE_HEAT:
                ApplianceStorageHeat appSH = new ApplianceStorageHeat();
                appSH.setApplianceID(liteApp.getApplianceID());
                appSH.setStorageTypeID(
                          liteStarsAppliance.getStorageHeat().getStorageType().getEntryID());
                if (liteStarsAppliance.getStorageHeat().hasPeakKWCapacity()) {
                    appSH.setPeakKWCapacity(liteStarsAppliance.getStorageHeat().getPeakKWCapacity());
                }
                if (liteStarsAppliance.getStorageHeat().hasHoursToRecharge()) {
                    appSH.setHoursToRecharge(liteStarsAppliance.getStorageHeat().getHoursToRecharge());
                }

                if (liteApp.getStorageHeat() != null) {
                    appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.UPDATE,
                                                                                 appSH).execute();
                } else {
                    appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.INSERT,
                                                                                 appSH).execute();
                    liteApp.setStorageHeat(new LiteStarsAppliance.StorageHeat());
                }

                StarsLiteFactory.setLiteAppStorageHeat(liteApp.getStorageHeat(),
                                                       appSH);

                break;

            case HEAT_PUMP:
                ApplianceHeatPump appHP = new ApplianceHeatPump();
                appHP.setApplianceID(liteApp.getApplianceID());
                appHP.setPumpTypeID(liteStarsAppliance.getHeatPump().getPumpType().getEntryID());
                appHP.setPumpSizeID(liteStarsAppliance.getHeatPump().getPumpSize().getEntryID());
                appHP.setStandbySourceID(liteStarsAppliance.getHeatPump().getStandbySource().getEntryID());
                if (liteStarsAppliance.getHeatPump().hasRestartDelaySeconds()) {
                    appHP.setSecondsDelayToRestart(
                              liteStarsAppliance.getHeatPump().getRestartDelaySeconds());
                }

                if (liteApp.getHeatPump() != null) {
                    appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.UPDATE,
                                                                              appHP).execute();
                } else {
                    appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.INSERT,
                                                                              appHP).execute();
                    liteApp.setHeatPump(new LiteStarsAppliance.HeatPump());
                }

                StarsLiteFactory.setLiteAppHeatPump(liteApp.getHeatPump(),
                                                    appHP);

                break;

            case IRRIGATION:
                ApplianceIrrigation appIrr = new ApplianceIrrigation();
                appIrr.setApplianceID(liteApp.getApplianceID());
                appIrr.setIrrigationTypeID(
                           liteStarsAppliance.getIrrigation().getIrrigationType().getEntryID());
                appIrr.setHorsePowerID(
                           liteStarsAppliance.getIrrigation().getHorsePower().getEntryID());
                appIrr.setEnergySourceID(
                           liteStarsAppliance.getIrrigation().getEnergySource().getEntryID());
                appIrr.setSoilTypeID(
                           liteStarsAppliance.getIrrigation().getSoilType().getEntryID());
                appIrr.setMeterLocationID(
                           liteStarsAppliance.getIrrigation().getMeterLocation().getEntryID());
                appIrr.setMeterVoltageID(
                           liteStarsAppliance.getIrrigation().getMeterVoltage().getEntryID());

                if (liteApp.getIrrigation() != null) {
                    appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.UPDATE,
                                                                                 appIrr).execute();
                } else {
                    appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.INSERT,
                                                                                 appIrr).execute();
                    liteApp.setIrrigation(new LiteStarsAppliance.Irrigation());
                }

                StarsLiteFactory.setLiteAppIrrigation(liteApp.getIrrigation(),
                                                      appIrr);

                break;
            }

        } catch (TransactionException e) {
            throw new RuntimeException("Error updating STARS Appliance.", e);
        }
    }

    @Autowired
    public void setStarsApplianceDao(StarsApplianceDao starsApplianceDao) {
        this.starsApplianceDao = starsApplianceDao;
    }
}