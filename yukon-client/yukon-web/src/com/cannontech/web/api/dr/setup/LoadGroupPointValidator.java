package com.cannontech.web.api.dr.setup;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.LoadGroupPoint;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.dr.loadgroup.service.impl.LoadGroupSetupServiceImpl;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class LoadGroupPointValidator extends LoadGroupSetupValidator<LoadGroupPoint> {
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private PointDao pointDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public LoadGroupPointValidator() {
        super(LoadGroupPoint.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupPoint.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupPoint loadGroup, Errors errors) {
        // Validate Control Device (deviceUsage)
        yukonApiValidationUtils.checkIfFieldRequired("deviceUsage", errors, loadGroup.getDeviceUsage(), "Control Device Point");

        if (!errors.hasFieldErrors("deviceUsage")) {
            yukonApiValidationUtils.checkIfFieldRequired("deviceUsage.id", errors, loadGroup.getDeviceUsage().getId(),
                    "Control Device Point");

            if (!errors.hasFieldErrors("deviceUsage.id")) {
                Optional<LiteYukonPAObject> liteYukonPAObject = serverDatabaseCache.getAllYukonPAObjects().stream()
                        .filter(paobject -> paobject.getLiteID() == loadGroup.getDeviceUsage().getId()).findFirst();

                // Validate Control device (deviceUsage type)
                if (liteYukonPAObject.isPresent()) {
                    if (liteYukonPAObject.get().getPaoType().isRtu() || liteYukonPAObject.get().getPaoType().isIon() ||
                            liteYukonPAObject.get().getPaoType().isCbc() || liteYukonPAObject.get().getPaoType().isMct()) {

                        validatePointUsage(loadGroup, errors, liteYukonPAObject);

                    } else {
                        errors.rejectValue("deviceUsage.id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { "MCT, RTU, CBC, or ION" }, "");
                    }
                } else {
                    errors.rejectValue("deviceUsage.id", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] {""}, "");
                }
            }
        }
    }

    private void validatePointUsage(LoadGroupPoint loadGroup, Errors errors, Optional<LiteYukonPAObject> liteYukonPAObject) {
        // Validate Control Point (pointUsage)
        yukonApiValidationUtils.checkIfFieldRequired("pointUsage", errors, loadGroup.getPointUsage(), "Control Device Point");
        if (!errors.hasFieldErrors("pointUsage")) {
            yukonApiValidationUtils.checkIfFieldRequired("pointUsage.id", errors, loadGroup.getPointUsage().getId(),
                    "Control Device Point");

            if (!errors.hasFieldErrors("pointUsage.id")) {
                Optional<LitePoint> point = YukonSpringHook.getBean(PointDao.class)
                        .getLitePointsByPaObjectId(liteYukonPAObject.get().getYukonID()).stream()
                        .filter(litePoint -> litePoint.getLiteID() == loadGroup.getPointUsage().getId()).findFirst();

                if (point.isPresent()) {
                    // Validates if the Point is Status Type
                    if (point.get().getPointTypeEnum() == PointType.Status) {
                        StatusPoint dbPoint = (StatusPoint) pointDao.get(point.get().getLiteID());
                        // Validates if the Status Point has control
                        if (dbPoint.getPointStatusControl().hasControl()) {
                            validateStartControlRawState(loadGroup, errors, point);
                        } else {
                            errors.rejectValue("pointUsage.id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { "Status point with control enabled" }, "");
                        }
                    } else {
                        errors.rejectValue("pointUsage.id", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                new Object[] { "Status point with control enabled" }, "");
                    }
                } else {
                    errors.rejectValue("pointUsage.id",  ApiErrorDetails.INVALID_VALUE.getCodeString());
                }

            }
        }
    }

    private void validateStartControlRawState(LoadGroupPoint loadGroup, Errors errors, Optional<LitePoint> point) {
        // Validate Control Start State (startControlRawState)
        yukonApiValidationUtils.checkIfFieldRequired("startControlRawState", errors, loadGroup.getStartControlRawState(),
                "Control Start State");

        if (!errors.hasFieldErrors("startControlRawState")) {
            yukonApiValidationUtils.checkIfFieldRequired("startControlRawState.id", errors,
                    loadGroup.getStartControlRawState().getId(), "Control Start State");

            if (!errors.hasFieldErrors("startControlRawState.id")) {
                Optional<LiteState> liteState = stateGroupDao.getStateGroup(point.get().getStateGroupID())
                        .getStatesList()
                        .stream()
                        .filter(state -> state.getStateRawState() == loadGroup.getStartControlRawState().getId()
                                && LoadGroupSetupServiceImpl.isValidPointGroupRawState(state))
                        .findFirst();

                if (liteState.isEmpty()) {
                    errors.rejectValue("startControlRawState.id",  ApiErrorDetails.INVALID_VALUE.getCodeString());
                }
            }
        }
    }

}
