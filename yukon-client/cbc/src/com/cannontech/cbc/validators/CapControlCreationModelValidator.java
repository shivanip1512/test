package com.cannontech.cbc.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.capcontrol.creation.service.CapControlCreationService;
import com.cannontech.cbc.service.CapControlCreationModel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;

public class CapControlCreationModelValidator extends SimpleValidator<CapControlCreationModel>{

    private final CapControlCreationService creationService;

    public CapControlCreationModelValidator(CapControlCreationService creationService) {
        super(CapControlCreationModel.class);
        this.creationService = creationService;
    }

    @Override
    protected void doValidation(CapControlCreationModel model, Errors errors) {
        final PaoType type = PaoType.getForId( model.getSelectedType());
        final String name = model.getName();

        if(StringUtils.isBlank(name)){
            errors.reject("A name must be specified for this object.");
        }

        if (model.isCreateNested()) {
            String cbcName = model.getNestedWizard().getName();
            if (name.equalsIgnoreCase(cbcName)) {
                errors.reject("Cannot create new Capacitor Bank and CBC with the same name.");
            }
        }

        if(creationService.createsNameConflict(type, name)){
            errors.reject("There is already a Cap Control item with the name '" + name + "'");
        }
    }

}
