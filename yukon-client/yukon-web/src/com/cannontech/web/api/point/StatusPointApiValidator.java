package com.cannontech.web.api.point;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.points.model.PointStatusControl;
import com.cannontech.web.tools.points.model.StatusPointModel;

public class StatusPointApiValidator<T extends StatusPointModel<?>> extends PointApiValidator<T> {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private final static String pointBaseKey = "yukon.web.modules.tools.point.";
    private MessageSourceAccessor accessor;

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

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
                String initialStateI18nText = accessor.getMessage(pointBaseKey + "initialState");
                errors.rejectValue("initialState", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { initialStateI18nText }, "");
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
                String controlOffsetI18nText = accessor.getMessage(pointBaseKey + "control.offset");
                errors.rejectValue("pointStatusControl.controlOffset", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { controlOffsetI18nText }, "");
            }

            if (pointStatusControl.getControlInhibited() != null && pointStatusControl.getControlInhibited().equals(true)) {
                String controlinibitI18nText = accessor.getMessage(pointBaseKey + "control.inhibit");
                errors.rejectValue("pointStatusControl.controlInhibited", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { controlinibitI18nText }, "");
            }

            if (pointStatusControl.getCloseTime1() != null && pointStatusControl.getCloseTime1() != 0) {
                String controlTime1I18nText = accessor.getMessage(pointBaseKey + "close.time1");
                errors.rejectValue("pointStatusControl.closeTime1", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { controlTime1I18nText }, "");
            }

            if (pointStatusControl.getCloseTime2() != null && pointStatusControl.getCloseTime2() != 0) {
                String controlTime2I18nText = accessor.getMessage(pointBaseKey + "close.time2");
                errors.rejectValue("pointStatusControl.closeTime2", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { controlTime2I18nText }, "");
            }

            if (pointStatusControl.getCommandTimeOut() != null && pointStatusControl.getCommandTimeOut() != 0) {
                String commandTimeoutI18nText = accessor.getMessage(pointBaseKey + "command.timeout");
                errors.rejectValue("pointStatusControl.commandTimeOut", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { commandTimeoutI18nText }, "");
            }

            if (pointStatusControl.getCloseCommand() != null
                    && !(pointStatusControl.getCloseCommand().equals(ControlStateType.CLOSE.getControlCommand()))) {
                String closeCommandOffsetI18nText = accessor.getMessage(pointBaseKey + "command.close");
                errors.rejectValue("pointStatusControl.closeCommand", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { closeCommandOffsetI18nText }, "");
            }

            if (pointStatusControl.getOpenCommand() != null
                    && !(pointStatusControl.getOpenCommand().equals(ControlStateType.OPEN.getControlCommand()))) {
                String openCommandOffsetI18nText = accessor.getMessage(pointBaseKey + "command.open");
                errors.rejectValue("pointStatusControl.openCommand", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { openCommandOffsetI18nText }, "");
            }
        }

    }
    
    @Override
    protected void validateArchiveSettings(T target, PointType pointType, Errors errors) {
        super.validateArchiveSettings(target, pointType, errors);

        if (target.getArchiveType() != PointArchiveType.NONE && target.getArchiveType() != PointArchiveType.ON_CHANGE) {
            errors.rejectValue("archiveType", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Archive type" }, "");
        }
    }
}
