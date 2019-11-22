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
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class LoadGroupPointValidator extends LoadGroupSetupValidator<LoadGroupPoint> {
    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private PointDao pointDao;
    @Autowired private StateGroupDao stateGroupDao;

    public LoadGroupPointValidator() {
        super(LoadGroupPoint.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupPoint.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupPoint loadGroup, Errors errors) {

        lmValidatorHelper.checkIfFieldRequired("deviceIdUsage", errors, loadGroup.getDeviceIdUsage(), "Control Device ");
        if (!errors.hasFieldErrors("deviceIdUsage")) {
            java.util.Optional<LiteYukonPAObject> liteYukonPAObject = serverDatabaseCache.getAllYukonPAObjects().stream()
                    .filter(paobject -> paobject.getLiteID() == loadGroup.getDeviceIdUsage()).findFirst();
            // Validate Control device (deviceIdUsage)
            if (liteYukonPAObject.isPresent()) {
                if(liteYukonPAObject.get().getPaoType().isRtu() || liteYukonPAObject.get().getPaoType().isIon() ||
                        liteYukonPAObject.get().getPaoType().isCbc() || liteYukonPAObject.get().getPaoType().isMct() ) {
                    
                    // validate control point (pointIdUsage)
                    lmValidatorHelper.checkIfFieldRequired("pointIdUsage", errors, loadGroup.getPointIdUsage(), "Control Point ");
                    if (!errors.hasFieldErrors("pointIdUsage")) {
                        Optional<LitePoint> point = YukonSpringHook.getBean(PointDao.class)
                                .getLitePointsByPaObjectId(liteYukonPAObject.get().getYukonID()).stream()
                                .filter(litePoint -> litePoint.getLiteID() == loadGroup.getPointIdUsage()).findFirst();

                        if (point.isPresent()) {

                            StatusPoint dbPoint = (StatusPoint) pointDao.get(point.get().getLiteID());
                            if (point.get().getPointTypeEnum() == PointType.Status && dbPoint.getPointStatusControl().hasControl()) {
                                
                                // Validate control start state (startControlRawState)
                                lmValidatorHelper.checkIfFieldRequired("startControlRawStateId", errors,
                                        loadGroup.getStartControlRawStateId(), "Control Start State ");
                                if (!errors.hasFieldErrors("startControlRawStateId")) {
                                    Optional<LiteState> liteState = stateGroupDao.getStateGroup(point.get().getStateGroupID())
                                            .getStatesList()
                                            .stream()
                                            .filter(state -> state.getStateRawState() == loadGroup.getStartControlRawStateId() && (state.getLiteID() == 0 || state.getLiteID() == 1))
                                            .findFirst();
                                    if (liteState.isEmpty()) {
                                        errors.rejectValue("startControlRawStateId", key + "invalidValue");
                                    }
                                }
                            } else {
                                errors.rejectValue("pointIdUsage", key + "invalidPoint");
                            }
                        } else {
                            errors.rejectValue("pointIdUsage", key + "invalidValue");
                        }
                    }
                } else {
                    errors.rejectValue("deviceIdUsage", key + "invalidDeviceType");
                }
            } else {
                errors.rejectValue("deviceIdUsage", key + "invalidValue");
            }
        }
    }
}
