package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.xml.serialize.StarsAppliance;

public class ApplianceValidator extends SimpleValidator<StarsAppliance> {

    private ApplianceCategoryDao applianceCategoryDao;
    
    public ApplianceValidator() {
        super(StarsAppliance.class);
    }

    @Override
    public void doValidation(StarsAppliance starsAppliance, Errors errors) {

        // General Appliance Validation
        YukonValidationUtils.checkExceedsMaxLength(errors, "modelNumber", starsAppliance.getModelNumber(), 40);
        YukonValidationUtils.checkExceedsMaxLength(errors, "yearManufactured", String.valueOf(starsAppliance.getYearManufactured()), 4);
        YukonValidationUtils.checkExceedsMaxLength(errors, "kwCapacity", String.valueOf(starsAppliance.getKwCapacity()), 14);
        YukonValidationUtils.checkExceedsMaxLength(errors, "efficiencyRating", String.valueOf(starsAppliance.getEfficiencyRating()), 14);
        YukonValidationUtils.checkExceedsMaxLength(errors, "notes", starsAppliance.getNotes(), 500);
        ApplianceCategory applianceCategory = applianceCategoryDao.getById(starsAppliance.getApplianceCategory().getApplianceCategoryId());
        starsAppliance.setApplianceCategory(applianceCategory);
        
        // Appliance Subtype Validation
        validateDualFuel(starsAppliance, errors);
        validateGenerator(starsAppliance, errors);
        validateHeatPump(starsAppliance, errors);
        validateStorageHeat(starsAppliance, errors);
        validateWaterHeater(starsAppliance, errors);

    }
    
    private void validateDualFuel(StarsAppliance starsAppliance, Errors errors) {
        if (starsAppliance.getApplianceCategory().getApplianceType().equals(ApplianceTypeEnum.DUAL_FUEL)) {
            YukonValidationUtils.checkExceedsMaxLength(errors,
                                                       "dualFuel.secondaryKWCapacity",
                                                       String.valueOf(starsAppliance.getDualFuel().getSecondaryKWCapacity()),
                                                       14);
        }
    }

    private void validateGenerator(StarsAppliance starsAppliance, Errors errors) {
        if (starsAppliance.getApplianceCategory().getApplianceType().equals(ApplianceTypeEnum.GENERATOR)) {
            YukonValidationUtils.checkExceedsMaxLength(errors,
                                                       "generator.peakKWCapacity",
                                                       String.valueOf(starsAppliance.getGenerator().getPeakKWCapacity()),
                                                       14);
            YukonValidationUtils.checkExceedsMaxLength(errors, 
                                                       "generator.fuelCapGallons", 
                                                       String.valueOf(starsAppliance.getGenerator().getFuelCapGallons()),
                                                       14);
            YukonValidationUtils.checkExceedsMaxLength(errors, 
                                                       "generator.startDelaySeconds", 
                                                       String.valueOf(starsAppliance.getGenerator().getStartDelaySeconds()),
                                                       14);
            
        }
    }

    private void validateHeatPump(StarsAppliance starsAppliance, Errors errors) {
        if (starsAppliance.getApplianceCategory().getApplianceType().equals(ApplianceTypeEnum.HEAT_PUMP)) {
            YukonValidationUtils.checkExceedsMaxLength(errors, 
                                                       "heatPump.restartDelaySeconds", 
                                                       String.valueOf(starsAppliance.getHeatPump().getRestartDelaySeconds()),
                                                       14);
        }
    }

    private void validateStorageHeat(StarsAppliance starsAppliance, Errors errors) {
        if (starsAppliance.getApplianceCategory().getApplianceType().equals(ApplianceTypeEnum.STORAGE_HEAT)) {
            YukonValidationUtils.checkExceedsMaxLength(errors, 
                                                       "storageHeat.hoursToRecharge", 
                                                       String.valueOf(starsAppliance.getStorageHeat().getHoursToRecharge()),
                                                       14);
            YukonValidationUtils.checkExceedsMaxLength(errors,
                                                       "storageHeat.peakKWCapacity",
                                                       String.valueOf(starsAppliance.getStorageHeat().getPeakKWCapacity()),
                                                       14);
        }
    }

    private void validateWaterHeater(StarsAppliance starsAppliance, Errors errors) {
        if (starsAppliance.getApplianceCategory().getApplianceType().equals(ApplianceTypeEnum.WATER_HEATER)) {
            YukonValidationUtils.checkExceedsMaxLength(errors,
                                                       "waterHeater.numberOfElements",
                                                       String.valueOf(starsAppliance.getWaterHeater().getNumberOfElements()),
                                                       14);
            
        }
    }
    
    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }

}
