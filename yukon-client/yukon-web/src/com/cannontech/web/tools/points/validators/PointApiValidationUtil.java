package com.cannontech.web.tools.points.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.web.tools.points.model.LitePointModel;

public class PointApiValidationUtil extends ValidationUtils {

    @Autowired private PointDao pointDao;
    @Autowired private PointValidationUtilCommon pointValidationUtilHelper;

    public void validatePointName(LitePointModel pointModel, String fieldName, Errors errors, boolean isCopyOrCreate) {
        validateName(fieldName, errors, pointModel.getPointName());
        if (!pointValidationUtilHelper.validatePointName(pointModel, isCopyOrCreate)) {
            errors.rejectValue(fieldName, ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    public void validateName(String fieldName, Errors errors, String pointName) {

        YukonApiValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "yukon.web.error.isBlank");
        YukonApiValidationUtils.checkExceedsMaxLength(errors, fieldName, pointName, 60);
        if (!pointValidationUtilHelper.validatePaoName(pointName)) {
            errors.rejectValue(fieldName, ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    public void validatePointOffset(LitePointModel pointModel, String fieldName, Errors errors,
            boolean isCopyOrCreate) {
        String physicalPort = pointValidationUtilHelper.isPointOrPhyicalOffset(pointModel, fieldName, errors);
        if (!physicalPort.isEmpty()) {
            Range<Integer> range = Range.inclusive(0, 99999999);
            YukonApiValidationUtils.checkRange(errors, fieldName, physicalPort,
                    pointModel.getPointOffset(), range, true);
        }
        List<Object> arguments = pointValidationUtilHelper.isValidPointOffset(pointModel, isCopyOrCreate);
        if (!arguments.isEmpty()) {
            errors.rejectValue(fieldName, ApiErrorDetails.POINT_OFFSET_NOT_AVAILABLE.getCodeString(),
                    arguments.toArray(), "Invalid point offset");
        }
    }

    /**
     * Check if pointType is matched with the pointObject retrieved.
     */
    public void checkIfPointTypeChanged(Errors errors, LitePointModel litePointModel, boolean isCreationOperation) {
        if (!pointValidationUtilHelper.checkIfPointTypeChanged(litePointModel, isCreationOperation)) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            errors.rejectValue("pointType", ApiErrorDetails.POINT_TYPE_MISMATCH.getCodeString(),
                    new Object[] { litePointModel.getPointType(), pointBase.getPoint().getPointType(),
                            litePointModel.getPointId() },
                    "");
        }
    }

    /**
     * Check if paoId is matched with the pointObject retrieved.
     */
    public void checkIfPaoIdChanged(Errors errors, LitePointModel litePointModel, boolean isCreationOperation) {
        if (!pointValidationUtilHelper.checkIfPaoIdMatch(litePointModel, isCreationOperation)) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            errors.rejectValue("paoId", ApiErrorDetails.PAO_ID_MISMATCH.getCodeString(),
                    new Object[] { litePointModel.getPaoId(), pointBase.getPoint().getPaoID() }, "");
        }
    }

    /**
     * Check if provided pointId is valid or not.
     */
    public void validatePointId(Errors errors, String field, Integer pointId, String fieldName) {
        if (!pointValidationUtilHelper.validatePointId(pointId)) {
            errors.rejectValue(field, ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { fieldName }, "");
        }
    }
}