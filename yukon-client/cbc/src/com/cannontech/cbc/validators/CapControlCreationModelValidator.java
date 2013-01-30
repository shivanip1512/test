package com.cannontech.cbc.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.capcontrol.creation.service.CapControlCreationService;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.cbc.service.CapControlCreationModel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoScheduleDao;
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

        //Must be a PAO

        if (model.isCreateNested()) {
            String cbcName = model.getNestedWizard().getName();
            if (name.equalsIgnoreCase(cbcName)) {
                errors.reject("Cannot create new Capacitor Bank and CBC with the same name.");
            }
        }

        final PaoType paoType = PaoType.getForId(type);

        if(paoDao.findUnique(name, paoType.getPaoCategory(), paoType.getPaoClass()) != null){
            errors.reject("There is already a Cap Control item with the name '" + name + "'");
        }
    }

}
