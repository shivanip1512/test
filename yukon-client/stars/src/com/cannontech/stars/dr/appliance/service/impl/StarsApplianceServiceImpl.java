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
    public void createStarsAppliance(StarsAppliance starsAppliance, int energyCompanyId, int accountId) {

        try {
            com.cannontech.database.data.stars.appliance.ApplianceBase app = 
                new com.cannontech.database.data.stars.appliance.ApplianceBase();
            ApplianceBase appDB = app.getApplianceBase();
            
            appDB.setAccountID(accountId);
            appDB.setApplianceCategoryID( new Integer(starsAppliance.getApplianceCategory().getApplianceCategoryId()) );
            appDB.setProgramID( new Integer(starsAppliance.getProgramID()) );
            appDB.setManufacturerID( new Integer(starsAppliance.getManufacturer().getEntryID()) );
            appDB.setLocationID( new Integer(starsAppliance.getLocation().getEntryID()) );
            appDB.setNotes( starsAppliance.getNotes() );
            appDB.setModelNumber( starsAppliance.getModelNumber() );
            
            if (starsAppliance.hasYearManufactured())
                appDB.setYearManufactured( new Integer(starsAppliance.getYearManufactured()) );
            if (starsAppliance.hasKwCapacity())
                appDB.setKWCapacity( new Double(starsAppliance.getKwCapacity()) );
            if (starsAppliance.hasEfficiencyRating())
                appDB.setEfficiencyRating( new Double(starsAppliance.getEfficiencyRating()) );
            
            if (starsAppliance.getInventoryID() > 0) {
                app.getLMHardwareConfig().setInventoryID( new Integer(starsAppliance.getInventoryID()) );
                app.getLMHardwareConfig().setLoadNumber( new Integer(starsAppliance.getLoadNumber()) );
                if(app.getLMHardwareConfig().getAddressingGroupID() == 0 && starsAppliance.getProgramID() > 0) {
                    Integer groupID = InventoryUtils.getYukonLoadGroupIDFromSTARSProgramID(starsAppliance.getProgramID());
                    app.getLMHardwareConfig().setAddressingGroupID(groupID);
                }
            }
                    
            app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
                    Transaction.createTransaction(Transaction.INSERT, app).execute();
            
            starsAppliance.setApplianceID(app.getApplianceBase().getApplianceID());
            
            LiteStarsAppliance liteApp = new LiteStarsAppliance();
            StarsLiteFactory.setLiteStarsAppliance( liteApp, app );
            
            switch (starsAppliance.getApplianceCategory().getApplianceType()) {
                case AIR_CONDITIONER:
                    ApplianceAirConditioner appAC = new ApplianceAirConditioner();
                    appAC.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appAC.setTonnageID( new Integer(starsAppliance.getAirConditioner().getTonnage().getEntryID()) );
                    appAC.setTypeID( new Integer(starsAppliance.getAirConditioner().getAcType().getEntryID()) );
                    appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.INSERT, appAC).execute();
                    
                    liteApp.setAirConditioner( new LiteStarsAppliance.AirConditioner() );
                    StarsLiteFactory.setLiteAppAirConditioner( liteApp.getAirConditioner(), appAC );
        
                    break;
            
                case DUAL_STAGE:
                    ApplianceDualStageAirCond appDS = new ApplianceDualStageAirCond();
                    appDS.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appDS.setStageOneTonnageID( new Integer(starsAppliance.getDualStageAC().getTonnage().getEntryID()) );
                    appDS.setStageTwoTonnageID( new Integer(starsAppliance.getDualStageAC().getStageTwoTonnage().getEntryID()));
                    appDS.setTypeID( new Integer(starsAppliance.getDualStageAC().getAcType().getEntryID()) );
                    appDS = (ApplianceDualStageAirCond) Transaction.createTransaction(Transaction.INSERT, appDS).execute();
                    
                    liteApp.setDualStageAirCond( new LiteStarsAppliance.DualStageAirCond() );
                    StarsLiteFactory.setLiteAppDualStageAirCond( liteApp.getDualStageAirCond(), appDS );
                    
                    break;
                    
                case CHILLER:
                    ApplianceChiller appChill = new ApplianceChiller();
                    appChill.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appChill.setTonnageID( new Integer(starsAppliance.getChiller().getTonnage().getEntryID()) );
                    appChill.setTypeID( new Integer(starsAppliance.getChiller().getAcType().getEntryID()) );
                    appChill = (ApplianceChiller) Transaction.createTransaction(Transaction.INSERT, appChill).execute();
                    
                    liteApp.setChiller( new LiteStarsAppliance.Chiller() );
                    StarsLiteFactory.setLiteAppChiller( liteApp.getChiller(), appChill );
                    
                    break;
                    
                case WATER_HEATER:
                    ApplianceWaterHeater appWH = new ApplianceWaterHeater();
                    appWH.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appWH.setNumberOfGallonsID( new Integer(starsAppliance.getWaterHeater().getNumberOfGallons().getEntryID()) );
                    appWH.setEnergySourceID( new Integer(starsAppliance.getWaterHeater().getEnergySource().getEntryID()) );
                    if (starsAppliance.getWaterHeater().hasNumberOfElements())
                        appWH.setNumberOfElements( new Integer(starsAppliance.getWaterHeater().getNumberOfElements()) );
                    appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.INSERT, appWH).execute();
                    
                    liteApp.setWaterHeater( new LiteStarsAppliance.WaterHeater() );
                    StarsLiteFactory.setLiteAppWaterHeater( liteApp.getWaterHeater(), appWH );
                    
                    break;
                    
                case DUAL_FUEL:
                    ApplianceDualFuel appDF = new ApplianceDualFuel();
                    appDF.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appDF.setSwitchOverTypeID( new Integer(starsAppliance.getDualFuel().getSwitchOverType().getEntryID()) );
                    appDF.setSecondaryEnergySourceID( new Integer(starsAppliance.getDualFuel().getSecondaryEnergySource().getEntryID()) );
                    if (starsAppliance.getDualFuel().hasSecondaryKWCapacity())
                        appDF.setSecondaryKWCapacity( new Integer(starsAppliance.getDualFuel().getSecondaryKWCapacity()) );
                    appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.INSERT, appDF).execute();
                    
                    liteApp.setDualFuel( new LiteStarsAppliance.DualFuel() );
                    StarsLiteFactory.setLiteAppDualFuel( liteApp.getDualFuel(), appDF );
                    
                    break;
                    
                case GENERATOR:
                    ApplianceGenerator appGen = new ApplianceGenerator();
                    appGen.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appGen.setTransferSwitchTypeID( new Integer(starsAppliance.getGenerator().getTransferSwitchType().getEntryID()) );
                    appGen.setTransferSwitchMfgID( new Integer(starsAppliance.getGenerator().getTransferSwitchManufacturer().getEntryID()) );
                    if (starsAppliance.getGenerator().hasPeakKWCapacity())
                        appGen.setPeakKWCapacity( new Integer(starsAppliance.getGenerator().getPeakKWCapacity()) );
                    if (starsAppliance.getGenerator().hasFuelCapGallons())
                        appGen.setFuelCapGallons( new Integer(starsAppliance.getGenerator().getFuelCapGallons()) );
                    if (starsAppliance.getGenerator().hasStartDelaySeconds())
                        appGen.setStartDelaySeconds( new Integer(starsAppliance.getGenerator().getStartDelaySeconds()) );
                    appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.INSERT, appGen).execute();
                    
                    liteApp.setGenerator( new LiteStarsAppliance.Generator() );
                    StarsLiteFactory.setLiteAppGenerator( liteApp.getGenerator(), appGen );
                    
                    break;
                    
                case GRAIN_DRYER:
                    ApplianceGrainDryer appGD = new ApplianceGrainDryer();
                    appGD.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appGD.setDryerTypeID( new Integer(starsAppliance.getGrainDryer().getDryerType().getEntryID()) );
                    appGD.setBinSizeID( new Integer(starsAppliance.getGrainDryer().getBinSize().getEntryID()) );
                    appGD.setBlowerEnergySourceID( new Integer(starsAppliance.getGrainDryer().getBlowerEnergySource().getEntryID()) );
                    appGD.setBlowerHorsePowerID( new Integer(starsAppliance.getGrainDryer().getBlowerHorsePower().getEntryID()) );
                    appGD.setBlowerHeatSourceID( new Integer(starsAppliance.getGrainDryer().getBlowerHeatSource().getEntryID()) );
                    appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.INSERT, appGD).execute();
                    
                    liteApp.setGrainDryer( new LiteStarsAppliance.GrainDryer() );
                    StarsLiteFactory.setLiteAppGrainDryer( liteApp.getGrainDryer(), appGD );
                    
                    break;
                    
                case STORAGE_HEAT:
                    ApplianceStorageHeat appSH = new ApplianceStorageHeat();
                    appSH.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appSH.setStorageTypeID( new Integer(starsAppliance.getStorageHeat().getStorageType().getEntryID()) );
                    if (starsAppliance.getStorageHeat().hasPeakKWCapacity())
                        appSH.setPeakKWCapacity( new Integer(starsAppliance.getStorageHeat().getPeakKWCapacity()) );
                    if (starsAppliance.getStorageHeat().hasHoursToRecharge())
                        appSH.setHoursToRecharge( new Integer(starsAppliance.getStorageHeat().getHoursToRecharge()) );
                    appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.INSERT, appSH).execute();
                    
                    liteApp.setStorageHeat( new LiteStarsAppliance.StorageHeat() );
                    StarsLiteFactory.setLiteAppStorageHeat( liteApp.getStorageHeat(), appSH );
                    
                    break;
                    
                case HEAT_PUMP:
                    ApplianceHeatPump appHP = new ApplianceHeatPump();
                    appHP.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appHP.setPumpTypeID( new Integer(starsAppliance.getHeatPump().getPumpType().getEntryID()) );
                    appHP.setPumpSizeID( new Integer(starsAppliance.getHeatPump().getPumpSize().getEntryID()) );
                    appHP.setStandbySourceID( new Integer(starsAppliance.getHeatPump().getStandbySource().getEntryID()) );
                    if (starsAppliance.getHeatPump().hasRestartDelaySeconds())
                        appHP.setSecondsDelayToRestart( new Integer(starsAppliance.getHeatPump().getRestartDelaySeconds()) );
                    appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.INSERT, appHP).execute();
                    
                    liteApp.setHeatPump( new LiteStarsAppliance.HeatPump() );
                    StarsLiteFactory.setLiteAppHeatPump( liteApp.getHeatPump(), appHP );
                    
                    break;
                    
                case IRRIGATION:
                    ApplianceIrrigation appIrr = new ApplianceIrrigation();
                    appIrr.setApplianceID( app.getApplianceBase().getApplianceID() );
                    appIrr.setIrrigationTypeID( new Integer(starsAppliance.getIrrigation().getIrrigationType().getEntryID()) );
                    appIrr.setHorsePowerID( new Integer(starsAppliance.getIrrigation().getHorsePower().getEntryID()) );
                    appIrr.setEnergySourceID( new Integer(starsAppliance.getIrrigation().getEnergySource().getEntryID()) );
                    appIrr.setSoilTypeID( new Integer(starsAppliance.getIrrigation().getSoilType().getEntryID()) );
                    appIrr.setMeterLocationID( new Integer(starsAppliance.getIrrigation().getMeterLocation().getEntryID()) );
                    appIrr.setMeterVoltageID( new Integer(starsAppliance.getIrrigation().getMeterVoltage().getEntryID()) );
                    appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.INSERT, appIrr).execute();
                    
                    liteApp.setIrrigation( new LiteStarsAppliance.Irrigation() );
                    StarsLiteFactory.setLiteAppIrrigation( liteApp.getIrrigation(), appIrr );
        
                    break;
            }
        } catch (TransactionException e) {
            throw new RuntimeException("Error creating STARS Appliance.",e);
        }
    }
    
    @Override
    public void removeStarsAppliance(int applianceId, 
                                     int energyCompanyId, 
                                     String accountNumber, 
                                     LiteYukonUser user){
        try {
            LiteStarsAppliance liteApp = 
                starsApplianceDao.getByApplianceIdAndEnergyCompanyId(applianceId, energyCompanyId);
            if (liteApp == null) {
                throw new IllegalArgumentException("Cannot find the appliance to be updated" );
            }
        
            boolean unenrollProgram = false;
            if (liteApp.getProgramID() > 0 &&
                    liteApp.getInventoryID() > 0) {
                unenrollProgram = true;
            }
        
            switch (liteApp.getApplianceCategory().getApplianceType()) {
            case AIR_CONDITIONER:
                ApplianceAirConditioner airConditionerApp = new ApplianceAirConditioner();
                airConditionerApp.setApplianceID( new Integer(liteApp.getApplianceID()) );
                Transaction.createTransaction(Transaction.DELETE, airConditionerApp).execute();
        
                break;
                
            case CHILLER:
                ApplianceChiller chillerApp = new ApplianceChiller();
                chillerApp.setApplianceID( new Integer(liteApp.getApplianceID()));
                Transaction.createTransaction(Transaction.DELETE, chillerApp).execute();
        
                break;
                
            case WATER_HEATER:
                ApplianceWaterHeater waterHeaterApp = new ApplianceWaterHeater();
                waterHeaterApp.setApplianceID( new Integer(liteApp.getApplianceID()) );
                Transaction.createTransaction(Transaction.DELETE, waterHeaterApp).execute();
        
                break;
        
            case DUAL_FUEL:
                ApplianceDualFuel dualFuelApp = new ApplianceDualFuel();
                dualFuelApp.setApplianceID( new Integer(liteApp.getApplianceID()) );
                Transaction.createTransaction(Transaction.DELETE, dualFuelApp).execute();
        
                break;
        
            case GENERATOR:
                ApplianceGenerator generatorApp = new ApplianceGenerator();
                generatorApp.setApplianceID( new Integer(liteApp.getApplianceID()) );
                Transaction.createTransaction(Transaction.DELETE, generatorApp).execute();
        
                break;
        
            case GRAIN_DRYER:
                ApplianceGrainDryer grainDryerApp = new ApplianceGrainDryer();
                grainDryerApp.setApplianceID( new Integer(liteApp.getApplianceID()) );
                Transaction.createTransaction(Transaction.DELETE, grainDryerApp).execute();
        
                break;
        
            case STORAGE_HEAT:
                ApplianceStorageHeat storageHeatApp = new ApplianceStorageHeat();
                storageHeatApp.setApplianceID( new Integer(liteApp.getApplianceID()) );
                Transaction.createTransaction(Transaction.DELETE, storageHeatApp).execute();
        
                break;
        
            case HEAT_PUMP:
                ApplianceHeatPump heatPumpApp = new ApplianceHeatPump();
                heatPumpApp.setApplianceID( new Integer(liteApp.getApplianceID()) );
                Transaction.createTransaction(Transaction.DELETE, heatPumpApp).execute();
        
                break;
        
            case IRRIGATION:
                ApplianceIrrigation irrigationApp = new ApplianceIrrigation();
                irrigationApp.setApplianceID( new Integer(liteApp.getApplianceID()) );
                Transaction.createTransaction(Transaction.DELETE, irrigationApp).execute();
        
                break;
                
            case DUAL_STAGE:
                ApplianceDualStageAirCond dualStageApp = new ApplianceDualStageAirCond();
                dualStageApp.setApplianceID( new Integer(liteApp.getApplianceID()));
                Transaction.createTransaction(Transaction.DELETE, dualStageApp).execute();
                
                break;
            }
        
        
            if (unenrollProgram) {
                LoadGroupDao loadGroupDao = YukonSpringHook.getBean("loadGroupDao", LoadGroupDao.class);
                LoadGroup loadGroup = loadGroupDao.getById(liteApp.getAddressingGroupID());
                String loadGroupName = loadGroup.getLoadGroupName();
        
                ProgramDao programDao = YukonSpringHook.getBean("starsProgramDao", ProgramDao.class);
                Program program = programDao.getByProgramId(liteApp.getProgramID());
                String programName = program.getProgramName();
        
                LMHardwareBaseDao lmHardwareBaseDao = YukonSpringHook.getBean("hardwareBaseDao", LMHardwareBaseDao.class);
                LMHardwareBase hardwareBase = lmHardwareBaseDao.getById(liteApp.getInventoryID());
                String serialNumber = hardwareBase.getManufacturerSerialNumber(); 
        
                EnrollmentHelper enrollmentHelper = new EnrollmentHelper(accountNumber,
                                                                         loadGroupName, 
                                                                         programName, 
                                                                         serialNumber);
        
        
                EnrollmentHelperService enrollmentHelperService = YukonSpringHook.getBean("enrollmentService", EnrollmentHelperService.class);
                enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.UNENROLL, user);
            }    
        
            com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
            app.setApplianceID( new Integer(liteApp.getApplianceID()) );
            Transaction.createTransaction(Transaction.DELETE, app).execute();
        } catch (TransactionException e) {
            throw new RuntimeException("Error removing STARS Appliance.",e);
        }
    }
    

    @Override
    public void updateStarsAppliance(StarsAppliance liteStarsAppliance, int energyCompanyId) {
        try {
            LiteStarsAppliance liteApp = starsApplianceDao.getByApplianceIdAndEnergyCompanyId(liteStarsAppliance.getApplianceID(), energyCompanyId);
            
            if (liteApp == null) {
                throw new IllegalArgumentException("Cannot find the appliance to be updated" );
            }
            
            com.cannontech.database.data.stars.appliance.ApplianceBase app =
                    (com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
            com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
            com.cannontech.database.db.stars.hardware.LMHardwareConfiguration appConfig = app.getLMHardwareConfig();
            
            if (liteStarsAppliance.hasApplianceCategoryID())
                appDB.setApplianceCategoryID( new Integer(liteStarsAppliance.getApplianceCategoryID()) );
            appDB.setManufacturerID( new Integer(liteStarsAppliance.getManufacturer().getEntryID()) );
            appDB.setLocationID( new Integer(liteStarsAppliance.getLocation().getEntryID()) );
            appDB.setNotes( liteStarsAppliance.getNotes() );
            appDB.setModelNumber( liteStarsAppliance.getModelNumber() );
            
            if (liteStarsAppliance.hasYearManufactured())
                appDB.setYearManufactured( new Integer(liteStarsAppliance.getYearManufactured()) );
            if (liteStarsAppliance.hasKwCapacity())
                appDB.setKWCapacity( new Double(liteStarsAppliance.getKwCapacity()) );
            if (liteStarsAppliance.hasEfficiencyRating())
                appDB.setEfficiencyRating( new Double(liteStarsAppliance.getEfficiencyRating()) );
            
            if (appConfig.getInventoryID() != null) {
                appConfig.setLoadNumber(new Integer(liteStarsAppliance.getLoadNumber()));
                if(appConfig.getAddressingGroupID().intValue() == 0 && liteStarsAppliance.getProgramID() > 0) {
                    Integer groupID = InventoryUtils.getYukonLoadGroupIDFromSTARSProgramID(liteStarsAppliance.getProgramID());
                    appConfig.setAddressingGroupID(groupID);
                }
            }
            
                Transaction.createTransaction(Transaction.UPDATE, appDB).execute();
            if (appConfig.getInventoryID() != null) {
                Transaction.createTransaction(Transaction.UPDATE, appConfig).execute();
            }
            StarsLiteFactory.setLiteStarsAppliance( liteApp, app );
            
            switch (liteStarsAppliance.getApplianceCategory().getApplianceType()){
                case AIR_CONDITIONER:
                    ApplianceAirConditioner appAC = new ApplianceAirConditioner();
                    appAC.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    appAC.setTonnageID( new Integer(liteStarsAppliance.getAirConditioner().getTonnage().getEntryID()) );
                    appAC.setTypeID( new Integer(liteStarsAppliance.getAirConditioner().getAcType().getEntryID()) );
                    
                    if (liteApp.getAirConditioner() != null) {
                        appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.UPDATE, appAC).execute();
                    }
                    else {
                        appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.INSERT, appAC).execute();
                        liteApp.setAirConditioner( new LiteStarsAppliance.AirConditioner() );
                    }
                    
                    StarsLiteFactory.setLiteAppAirConditioner( liteApp.getAirConditioner(), appAC );
                    
                    break;
                    
                case DUAL_STAGE:
                    ApplianceDualStageAirCond dualStageAC = new ApplianceDualStageAirCond();
                    dualStageAC.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    dualStageAC.setStageOneTonnageID( new Integer(liteStarsAppliance.getDualStageAC().getTonnage().getEntryID()) );
                    dualStageAC.setStageTwoTonnageID( new Integer(liteStarsAppliance.getDualStageAC().getStageTwoTonnage().getEntryID()) );
                    dualStageAC.setTypeID( new Integer(liteStarsAppliance.getDualStageAC().getAcType().getEntryID()) );
                    
                    if (liteApp.getDualStageAirCond() != null) {
                        dualStageAC = (ApplianceDualStageAirCond) Transaction.createTransaction(Transaction.UPDATE, dualStageAC).execute();
                    }
                    else {
                        dualStageAC = (ApplianceDualStageAirCond) Transaction.createTransaction(Transaction.INSERT, dualStageAC).execute();
                        liteApp.setDualStageAirCond( new LiteStarsAppliance.DualStageAirCond() );
                    }
                    
                    StarsLiteFactory.setLiteAppDualStageAirCond( liteApp.getDualStageAirCond(), dualStageAC );
                    
                    break;
                    
                case CHILLER:
                    ApplianceChiller appChill = new ApplianceChiller();
                    appChill.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    appChill.setTonnageID( new Integer(liteStarsAppliance.getChiller().getTonnage().getEntryID()) );
                    appChill.setTypeID( new Integer(liteStarsAppliance.getChiller().getAcType().getEntryID()) );
                    
                    if (liteApp.getChiller() != null) {
                        appChill = (ApplianceChiller) Transaction.createTransaction(Transaction.UPDATE, appChill).execute();
                    }
                    else {
                        appChill = (ApplianceChiller) Transaction.createTransaction(Transaction.INSERT, appChill).execute();
                        liteApp.setAirConditioner( new LiteStarsAppliance.AirConditioner() );
                    }
                    
                    StarsLiteFactory.setLiteAppChiller( liteApp.getChiller(), appChill );
                    
                    break;
                    
                case WATER_HEATER:
                    ApplianceWaterHeater appWH = new ApplianceWaterHeater();
                    appWH.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    appWH.setNumberOfGallonsID( new Integer(liteStarsAppliance.getWaterHeater().getNumberOfGallons().getEntryID()) );
                    appWH.setEnergySourceID( new Integer(liteStarsAppliance.getWaterHeater().getEnergySource().getEntryID()) );
                    if (liteStarsAppliance.getWaterHeater().hasNumberOfElements())
                        appWH.setNumberOfElements( new Integer(liteStarsAppliance.getWaterHeater().getNumberOfElements()) );
                    
                    if (liteApp.getWaterHeater() != null) {
                        appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.UPDATE, appWH).execute();
                    }
                    else {
                        appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.INSERT, appWH).execute();
                        liteApp.setWaterHeater( new LiteStarsAppliance.WaterHeater() );
                    }
                    
                    StarsLiteFactory.setLiteAppWaterHeater( liteApp.getWaterHeater(), appWH );
                    
                    
                    break;
                    
                case DUAL_FUEL:
                    ApplianceDualFuel appDF = new ApplianceDualFuel();
                    appDF.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    appDF.setSwitchOverTypeID( new Integer(liteStarsAppliance.getDualFuel().getSwitchOverType().getEntryID()) );
                    appDF.setSecondaryEnergySourceID( new Integer(liteStarsAppliance.getDualFuel().getSecondaryEnergySource().getEntryID()) );
                    if (liteStarsAppliance.getDualFuel().hasSecondaryKWCapacity())
                        appDF.setSecondaryKWCapacity( new Integer(liteStarsAppliance.getDualFuel().getSecondaryKWCapacity()) );
                    
                    if (liteApp.getDualFuel() != null) {
                        appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.UPDATE, appDF).execute();
                    }
                    else {
                        appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.INSERT, appDF).execute();
                        liteApp.setDualFuel( new LiteStarsAppliance.DualFuel() );
                    }
                    
                    StarsLiteFactory.setLiteAppDualFuel( liteApp.getDualFuel(), appDF );
                    
                    break;
                    
                case GENERATOR:
                    ApplianceGenerator appGen = new ApplianceGenerator();
                    appGen.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    appGen.setTransferSwitchTypeID( new Integer(liteStarsAppliance.getGenerator().getTransferSwitchType().getEntryID()) );
                    appGen.setTransferSwitchMfgID( new Integer(liteStarsAppliance.getGenerator().getTransferSwitchManufacturer().getEntryID()) );
                    if (liteStarsAppliance.getGenerator().hasPeakKWCapacity())
                        appGen.setPeakKWCapacity( new Integer(liteStarsAppliance.getGenerator().getPeakKWCapacity()) );
                    if (liteStarsAppliance.getGenerator().hasFuelCapGallons())
                        appGen.setFuelCapGallons( new Integer(liteStarsAppliance.getGenerator().getFuelCapGallons()) );
                    if (liteStarsAppliance.getGenerator().hasStartDelaySeconds())
                        appGen.setStartDelaySeconds( new Integer(liteStarsAppliance.getGenerator().getStartDelaySeconds()) );
                    
                    if (liteApp.getGenerator() != null) {
                        appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.UPDATE, appGen).execute();
                    }
                    else {
                        appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.INSERT, appGen).execute();
                        liteApp.setGenerator( new LiteStarsAppliance.Generator() );
                    }
                    
                    StarsLiteFactory.setLiteAppGenerator( liteApp.getGenerator(), appGen );
                    
                    break;
                    
                case GRAIN_DRYER:
                    ApplianceGrainDryer appGD = new ApplianceGrainDryer();
                    appGD.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    appGD.setDryerTypeID( new Integer(liteStarsAppliance.getGrainDryer().getDryerType().getEntryID()) );
                    appGD.setBinSizeID( new Integer(liteStarsAppliance.getGrainDryer().getBinSize().getEntryID()) );
                    appGD.setBlowerEnergySourceID( new Integer(liteStarsAppliance.getGrainDryer().getBlowerEnergySource().getEntryID()) );
                    appGD.setBlowerHorsePowerID( new Integer(liteStarsAppliance.getGrainDryer().getBlowerHorsePower().getEntryID()) );
                    appGD.setBlowerHeatSourceID( new Integer(liteStarsAppliance.getGrainDryer().getBlowerHeatSource().getEntryID()) );
                    
                    if (liteApp.getGrainDryer() != null) {
                        appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.UPDATE, appGD).execute();
                    }
                    else {
                        appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.INSERT, appGD).execute();
                        liteApp.setGrainDryer( new LiteStarsAppliance.GrainDryer() );
                    }
                    
                    StarsLiteFactory.setLiteAppGrainDryer( liteApp.getGrainDryer(), appGD );
                    
                    break;
                    
                case STORAGE_HEAT:
                    ApplianceStorageHeat appSH = new ApplianceStorageHeat();
                    appSH.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    appSH.setStorageTypeID( new Integer(liteStarsAppliance.getStorageHeat().getStorageType().getEntryID()) );
                    if (liteStarsAppliance.getStorageHeat().hasPeakKWCapacity())
                        appSH.setPeakKWCapacity( new Integer(liteStarsAppliance.getStorageHeat().getPeakKWCapacity()) );
                    if (liteStarsAppliance.getStorageHeat().hasHoursToRecharge())
                        appSH.setHoursToRecharge( new Integer(liteStarsAppliance.getStorageHeat().getHoursToRecharge()) );
                    
                    if (liteApp.getStorageHeat() != null) {
                        appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.UPDATE, appSH).execute();
                    }
                    else {
                        appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.INSERT, appSH).execute();
                        liteApp.setStorageHeat( new LiteStarsAppliance.StorageHeat() );
                    }
                    
                    StarsLiteFactory.setLiteAppStorageHeat( liteApp.getStorageHeat(), appSH );
                    
                    break;
                    
                case HEAT_PUMP:
                    ApplianceHeatPump appHP = new ApplianceHeatPump();
                    appHP.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    appHP.setPumpTypeID( new Integer(liteStarsAppliance.getHeatPump().getPumpType().getEntryID()) );
                    appHP.setPumpSizeID( new Integer(liteStarsAppliance.getHeatPump().getPumpSize().getEntryID()) );
                    appHP.setStandbySourceID( new Integer(liteStarsAppliance.getHeatPump().getStandbySource().getEntryID()) );
                    if (liteStarsAppliance.getHeatPump().hasRestartDelaySeconds())
                        appHP.setSecondsDelayToRestart( new Integer(liteStarsAppliance.getHeatPump().getRestartDelaySeconds()) );
                    
                    if (liteApp.getHeatPump() != null) {
                        appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.UPDATE, appHP).execute();
                    }
                    else {
                        appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.INSERT, appHP).execute();
                        liteApp.setHeatPump( new LiteStarsAppliance.HeatPump() );
                    }
                    
                    StarsLiteFactory.setLiteAppHeatPump( liteApp.getHeatPump(), appHP );
                    
                    break;
                    
                case IRRIGATION:
                    ApplianceIrrigation appIrr = new ApplianceIrrigation();
                    appIrr.setApplianceID( new Integer(liteApp.getApplianceID()) );
                    appIrr.setIrrigationTypeID( new Integer(liteStarsAppliance.getIrrigation().getIrrigationType().getEntryID()) );
                    appIrr.setHorsePowerID( new Integer(liteStarsAppliance.getIrrigation().getHorsePower().getEntryID()) );
                    appIrr.setEnergySourceID( new Integer(liteStarsAppliance.getIrrigation().getEnergySource().getEntryID()) );
                    appIrr.setSoilTypeID( new Integer(liteStarsAppliance.getIrrigation().getSoilType().getEntryID()) );
                    appIrr.setMeterLocationID( new Integer(liteStarsAppliance.getIrrigation().getMeterLocation().getEntryID()) );
                    appIrr.setMeterVoltageID( new Integer(liteStarsAppliance.getIrrigation().getMeterVoltage().getEntryID()) );
                    
                    if (liteApp.getIrrigation() != null) {
                        appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.UPDATE, appIrr).execute();
                    }
                    else {
                        appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.INSERT, appIrr).execute();
                        liteApp.setIrrigation( new LiteStarsAppliance.Irrigation() );
                    }
                    
                    StarsLiteFactory.setLiteAppIrrigation( liteApp.getIrrigation(), appIrr );
                    
                    break;
            }
            
        } catch (TransactionException e) {
            throw new RuntimeException("Error updating STARS Appliance.",e);
        }
    }

    
    @Autowired
    public void setStarsApplianceDao(StarsApplianceDao starsApplianceDao) {
        this.starsApplianceDao = starsApplianceDao;
    }
}