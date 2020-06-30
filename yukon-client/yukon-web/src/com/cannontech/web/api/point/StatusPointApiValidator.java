package com.cannontech.web.api.point;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.web.tools.points.model.PointStatusControlModel;
import com.cannontech.web.tools.points.model.StatusPointModel;

public class StatusPointApiValidator<T extends StatusPointModel<?>> extends PointApiValidator<T> {

    public StatusPointApiValidator() {
        super();
    }

    @Override
    public boolean supports(Class clazz) {
        return StatusPointModel.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(T statusPoint, Errors errors) {
        super.doValidation(statusPoint, errors);

        if (!errors.hasFieldErrors("stateGroupId") && statusPoint.getInitialState() != null) {
            List<LiteState> liteStates = stateGroupDao.getLiteStates(statusPoint.getStateGroupId());
            List<Integer> rawStates = liteStates.stream()
                                                .map(state -> state.getStateRawState())
                                                .collect(Collectors.toList());
            if (!rawStates.contains(statusPoint.getInitialState())) {

                errors.rejectValue("initialState",
                                   baseKey + ".invalid.initialState",
                                   new Object[] { statusPoint.getInitialState(), statusPoint.getStateGroupId() },
                                   "");
            }
        }

        validatePointStatusControl(statusPoint.getPointStatusControl(), errors);
    }

    /**
     *  Validate Point StatusControl
     */
    private void validatePointStatusControl(PointStatusControlModel pointStatusControl, Errors errors) {

        YukonValidationUtils.checkRange(errors, "pointStatusControl.controlOffset", pointStatusControl.getControlOffset(), -99999999, 99999999, false);

        YukonValidationUtils.checkRange(errors, "pointStatusControl.closeTime1", pointStatusControl.getCloseTime1(), 0, 9999, false);

        YukonValidationUtils.checkRange(errors, "pointStatusControl.closeTime2", pointStatusControl.getCloseTime2(), 0, 9999, false);

        YukonValidationUtils.checkRange(errors, "pointStatusControl.commandTimeOut", pointStatusControl.getCommandTimeOut(), 0, 9999999, false);

        if (pointStatusControl.getCloseCommand() != null) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "pointStatusControl.closeCommand", pointStatusControl.getCloseCommand(), 100);
        }

        if (pointStatusControl.getOpenCommand() != null) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "pointStatusControl.openCommand", pointStatusControl.getOpenCommand(), 100);
        }
        // if for accepting non-default values, need to specify control type in request otherwise it would accept only default values.
        if (pointStatusControl.getControlType() == null || pointStatusControl.getControlType() == StatusControlType.NONE) {
            if (pointStatusControl.getControlOffset() != null && pointStatusControl.getControlOffset() != 0) {
                errors.rejectValue("pointStatusControl.controlOffset", baseKey + ".invalid.controlOffset");
            }

            if (pointStatusControl.getControlInhibited() != null && pointStatusControl.getControlInhibited().equals(true)) {
                errors.rejectValue("pointStatusControl.controlInhibited", baseKey + ".invalid.controlInhibited");
            }

            if (pointStatusControl.getCloseTime1() != null && pointStatusControl.getCloseTime1() != 0) {
                errors.rejectValue("pointStatusControl.closeTime1", baseKey + ".invalid.closeTime1");
            }

            if (pointStatusControl.getCloseTime2() != null && pointStatusControl.getCloseTime2() != 0) {
                errors.rejectValue("pointStatusControl.closeTime2", baseKey + ".invalid.closeTime2");
            }

            if (pointStatusControl.getCommandTimeOut() != null && pointStatusControl.getCommandTimeOut() != 0) {
                errors.rejectValue("pointStatusControl.commandTimeOut", baseKey + ".invalid.commandTimeOut");
            }

            if (pointStatusControl.getCloseCommand() != null && !(pointStatusControl.getCloseCommand().equals(ControlStateType.CLOSE.getControlCommand()))) {
                errors.rejectValue("pointStatusControl.closeCommand",
                                   baseKey + ".invalid.closeCommand",
                                   new Object[] { ControlStateType.CLOSE.getControlCommand() },
                                   "");
            }

            if (pointStatusControl.getOpenCommand() != null && !(pointStatusControl.getOpenCommand().equals(ControlStateType.OPEN.getControlCommand()))) {
                errors.rejectValue("pointStatusControl.openCommand",
                                   baseKey + ".invalid.openCommand",
                                   new Object[] { ControlStateType.OPEN.getControlCommand() },
                                   "");
            }
        }

    }
}
