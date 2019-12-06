package com.cannontech.web.api.dr.setup;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupPoint;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class LoadGroupPointValidator extends LoadGroupSetupValidator<LoadGroupPoint> {
    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private PointDao pointDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private LoadGroupSetupService loadGroupService;

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
        lmValidatorHelper.checkIfFieldRequired("deviceUsage", errors, loadGroup.getDeviceUsage(), "Control Device Point");

        if (!errors.hasFieldErrors("deviceUsage")) {
            lmValidatorHelper.checkIfFieldRequired("deviceUsage.id", errors, loadGroup.getDeviceUsage().getId(),
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
                        errors.rejectValue("deviceUsage.id", key + "invalidDeviceType");
                    }
                } else {
                    errors.rejectValue("deviceUsage.id", key + "invalidValue");
                }
            }
        }
    }

    private void validatePointUsage(LoadGroupPoint loadGroup, Errors errors, Optional<LiteYukonPAObject> liteYukonPAObject) {
        // Validate Control Point (pointUsage)
        lmValidatorHelper.checkIfFieldRequired("pointUsage", errors, loadGroup.getPointUsage(), "Control Device Point");
        if (!errors.hasFieldErrors("pointUsage")) {
            lmValidatorHelper.checkIfFieldRequired("pointUsage.id", errors, loadGroup.getPointUsage().getId(),
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
                            errors.rejectValue("pointUsage.id", key + "invalidPoint");
                        }
                    } else {
                        errors.rejectValue("pointUsage.id", key + "invalidPoint");
                    }
                } else {
                    errors.rejectValue("pointUsage.id", key + "invalidValue");
                }

            }
        }
    }

    private void validateStartControlRawState(LoadGroupPoint loadGroup, Errors errors, Optional<LitePoint> point) {
        // Validate Control Start State (startControlRawState)
        lmValidatorHelper.checkIfFieldRequired("startControlRawState", errors, loadGroup.getStartControlRawState(),
                "Control Start State");

        if (!errors.hasFieldErrors("startControlRawState")) {
            lmValidatorHelper.checkIfFieldRequired("startControlRawState.rawState", errors,
                    loadGroup.getStartControlRawState().getRawState(), "Control Start State");

            if (!errors.hasFieldErrors("startControlRawState.rawState")) {
                Optional<LiteState> liteState = stateGroupDao.getStateGroup(point.get().getStateGroupID())
                        .getStatesList()
                        .stream()
                        .filter(state -> state.getStateRawState() == loadGroup.getStartControlRawState().getRawState()
                                && loadGroupService.isValidPointGroupRawState(state))
                        .findFirst();

                if (liteState.isEmpty()) {
                    errors.rejectValue("startControlRawState.rawState", key + "invalidValue");
                }
            }
        }
    }

}
