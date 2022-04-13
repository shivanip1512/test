package com.cannontech.web.api.dr.setup;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.AddressLevel;
import com.cannontech.common.dr.setup.LoadGroupMCT;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class LoadGroupMCTValidator extends LoadGroupSetupValidator<LoadGroupMCT> {
    
    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public LoadGroupMCTValidator() {
        super(LoadGroupMCT.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupMCT.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupMCT loadGroup, Errors errors) {

        lmApiValidatorHelper.validateRoute(errors, loadGroup.getRouteId());
        yukonApiValidationUtils.checkIfFieldRequired("level", errors, loadGroup.getLevel(), "Address level");
        // Validate address.
        if (loadGroup.getLevel() == AddressLevel.MCT_ADDRESS) {
            yukonApiValidationUtils.checkIfFieldRequired("mctDeviceId", errors, loadGroup.getMctDeviceId(), "MCT Address");
            if (!errors.hasFieldErrors("mctDeviceId") && !isValidMCTDeviceId(loadGroup.getMctDeviceId())) {
                errors.rejectValue("mctDeviceId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] {loadGroup.getMctDeviceId()}, "");
            }
        } else {
            // Check for valid range of addresses.
            yukonApiValidationUtils.checkIfFieldRequired("address", errors, loadGroup.getAddress(), "Address");
            if (!errors.hasFieldErrors("address")) {
                yukonApiValidationUtils.checkRange(errors, "address", loadGroup.getAddress(), 1, Integer.MAX_VALUE, true);
            }
        }
    }

    /**
     * Returns true if passed deviceId is for MCT.
     */
    private boolean isValidMCTDeviceId(Integer deviceId) {
        List<Integer> mctIds = serverDatabaseCache.getAllMCTs()
                                                  .stream()
                                                  .map(e -> e.getPaoIdentifier().getPaoId())
                                                  .collect(Collectors.toList());
        return mctIds.contains(deviceId);
    }
}
