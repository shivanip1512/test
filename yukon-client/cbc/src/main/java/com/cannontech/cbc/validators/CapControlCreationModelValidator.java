package com.cannontech.cbc.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.cbc.service.CapControlCreationModel;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.database.data.pao.CapControlTypes;

public class CapControlCreationModelValidator extends SimpleValidator<CapControlCreationModel>{

    private final PaoScheduleDao paoScheduleDao;
    private final StrategyDao strategyDao;
    private final PaoDao paoDao;

    public CapControlCreationModelValidator(PaoScheduleDao paoScheduleDao, StrategyDao strategyDao, PaoDao paoDao) {
        super(CapControlCreationModel.class);
        this.paoScheduleDao = paoScheduleDao;
        this.strategyDao = strategyDao;
        this.paoDao = paoDao;
    }

    @Override
    protected void doValidation(CapControlCreationModel model, Errors errors) {
        final int type = model.getSelectedType();
        final String name = model.getName();

        if(StringUtils.isBlank(name)){
            errors.reject("A name must be specified for this object.");
        }
        
        if (type != CapControlTypes.CAP_CONTROL_SCHEDULE && type != CapControlTypes.CAP_CONTROL_STRATEGY) {
            if (!PaoUtils.isValidPaoName(name)) {
                errors.reject(name + " cannot include any of the following characters: / \\ , ' |");
            }
        }
        
        if (type == CapControlTypes.CAP_CONTROL_SCHEDULE) {
            if(paoScheduleDao.isUniqueName(name)){
                errors.reject("There is already a Schedule with the name '" + name + "'");
            }
            return;
        }

        if (type == CapControlTypes.CAP_CONTROL_STRATEGY) {
            if(strategyDao.isUniqueName(name)){
                errors.reject("There is already a Strategy with the name '" + name + "'");
            }
            return;
        }

        //Check for Cap Bank and CBC having the same name
        if (model.isCreateNested()) {
            String cbcName = model.getNestedWizard().getName();
            if (name.equalsIgnoreCase(cbcName)) {
                errors.reject("Cannot create new Capacitor Bank and CBC with the same name.");
            }
        }

        /*
         * The tuple (name, PaoCategory, PaoClass) must be unique in the database.
         * At this point, the device is one of: Area, Special Area, Substation, Substation Bus, Feeder, CapBank, CBC, and Regulator.
         * These all have PaoClass CAPCONTROL. 
         * CBC and CapBank have PaoCategory DEVICE, the rest have PaoCategory CAPCONTROL
         */

        final PaoType paoType = PaoType.getForId(type);
        final PaoCategory paoCategory = paoType.getPaoCategory();


        if(paoDao.findUnique(name, paoType) != null) {
            if(paoCategory.equals(PaoCategory.CAPCONTROL)){
                errors.reject("There is already an Area, Substation, Substation Bus, Feeder, or Regulator with the name '" + name + "'");
            }
            else {
                errors.reject("There is already a Cap Bank or CBC with the name '" + name + "'");
            }
        }
    }

}
