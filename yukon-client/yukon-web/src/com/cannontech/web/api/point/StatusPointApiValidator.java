package com.cannontech.web.api.point;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.web.tools.points.model.PointStatusControl;
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

        if (!errors.hasFieldErrors("stateGroupId") && statusPoint.getInitialState() != null && statusPoint.getStateGroupId() != null) {
            List<LiteState> liteStates = stateGroupDao.getLiteStates(statusPoint.getStateGroupId());
            List<Integer> rawStates = liteStates.stream()
                                                .map(state -> state.getStateRawState())
                                                .collect(Collectors.toList());
            if (!rawStates.contains(statusPoint.getInitialState())) {
                errors.rejectValue("initialState", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { StringUtils.join(rawStates, ", ") }, "");
            }
        }

        validatePointStatusControl(statusPoint.getPointStatusControl(), errors);
    }

    /**
     *  Validate Point StatusControl
     */
    private void validatePointStatusControl(PointStatusControl pointStatusControl, Errors errors) {

        if(pointStatusControl.getControlType() != null && pointStatusControl.getControlType() != StatusControlType.NONE) {

            YukonApiValidationUtils.checkRange(errors, "pointStatusControl.controlOffset", pointStatusControl.getControlOffset(), -99999999, 99999999, false);

            YukonApiValidationUtils.checkRange(errors, "pointStatusControl.closeTime1", pointStatusControl.getCloseTime1(), 0, 9999, false);

            YukonApiValidationUtils.checkRange(errors, "pointStatusControl.closeTime2", pointStatusControl.getCloseTime2(), 0, 9999, false);

            YukonApiValidationUtils.checkRange(errors, "pointStatusControl.commandTimeOut", pointStatusControl.getCommandTimeOut(), 0, 9999999, false);

            if (pointStatusControl.getCloseCommand() != null) {
                YukonApiValidationUtils.checkExceedsMaxLength(errors, "pointStatusControl.closeCommand", pointStatusControl.getCloseCommand(), 100);
            }

            if (pointStatusControl.getOpenCommand() != null) {
                YukonApiValidationUtils.checkExceedsMaxLength(errors, "pointStatusControl.openCommand", pointStatusControl.getOpenCommand(), 100);
            }
        }

        // if for accepting non-default values, need to specify control type in request otherwise it would accept only default values.
        if (pointStatusControl.getControlType() == null || pointStatusControl.getControlType() == StatusControlType.NONE) {
            if (pointStatusControl.getControlOffset() != null && pointStatusControl.getControlOffset() != 0) {
                errors.rejectValue("pointStatusControl.controlOffset", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "0 when Control Type is None" }, "");
            }

            if (pointStatusControl.getControlInhibited() != null && pointStatusControl.getControlInhibited().equals(true)) {
                errors.rejectValue("pointStatusControl.controlInhibited", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "false when Control type is None" }, "");
            }

            if (pointStatusControl.getCloseTime1() != null && pointStatusControl.getCloseTime1() != 0) {
                errors.rejectValue("pointStatusControl.closeTime1", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "0 when Control Type is None" }, "");
            }

            if (pointStatusControl.getCloseTime2() != null && pointStatusControl.getCloseTime2() != 0) {
                errors.rejectValue("pointStatusControl.closeTime2", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "0 when Control Type is None" }, "");
            }

            if (pointStatusControl.getCommandTimeOut() != null && pointStatusControl.getCommandTimeOut() != 0) {
                errors.rejectValue("pointStatusControl.commandTimeOut", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "0 when Control Type is None" }, "");
            }

            if (pointStatusControl.getCloseCommand() != null
                    && !(pointStatusControl.getCloseCommand().equals(ControlStateType.CLOSE.getControlCommand()))) {
                errors.rejectValue("pointStatusControl.closeCommand", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { ControlStateType.CLOSE.getControlCommand() + " when Control Type is None" }, "");
            }

            if (pointStatusControl.getOpenCommand() != null
                    && !(pointStatusControl.getOpenCommand().equals(ControlStateType.OPEN.getControlCommand()))) {
                errors.rejectValue("pointStatusControl.openCommand", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { ControlStateType.OPEN.getControlCommand() + " when Control Type is None" }, "");
            }
        }

    }
    
    @Override
    protected void validateArchiveSettings(T target, PointType pointType, Errors errors) {
        super.validateArchiveSettings(target, pointType, errors);

        if (target.getArchiveType() != PointArchiveType.NONE && target.getArchiveType() != PointArchiveType.ON_CHANGE) {
            errors.rejectValue("archiveType", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { PointArchiveType.NONE + ", " + PointArchiveType.ON_CHANGE }, "");
        }
    }
}
