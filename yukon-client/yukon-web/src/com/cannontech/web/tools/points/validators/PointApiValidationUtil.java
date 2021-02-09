package com.cannontech.web.tools.points.validators;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired private PointValidationUtilCommon pointValidationUtilCommon;

    public void validatePointName(LitePointModel pointModel, String fieldName, Errors errors, boolean isCopyOrCreate) {
        validateName(fieldName, errors, pointModel.getPointName());
        if (!pointValidationUtilCommon.validatePointName(pointModel, isCopyOrCreate)) {
            errors.rejectValue(fieldName, ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                    new Object[] { pointModel.getPointName() }, "");
        }
    }

    public void validateName(String fieldName, Errors errors, String pointName) {

        YukonApiValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "yukon.web.error.isBlank");
        YukonApiValidationUtils.checkExceedsMaxLength(errors, fieldName, pointName, 60);
        if (!pointValidationUtilCommon.validatePaoName(pointName)) {
            errors.rejectValue(fieldName, ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    public void validatePointOffset(LitePointModel pointModel, String fieldName, Errors errors,
            boolean isCopyOrCreate) {
        String physicalPort = pointValidationUtilCommon.isPointOrPhyicalOffset(pointModel, fieldName, errors);
        if (StringUtils.isNotEmpty(physicalPort)) {
            Range<Integer> range = Range.inclusive(0, 99999999);
            YukonApiValidationUtils.checkRange(errors, fieldName, physicalPort,
                    pointModel.getPointOffset(), range, true);
        }
        List<Object> arguments = pointValidationUtilCommon.isValidPointOffset(pointModel, isCopyOrCreate);
        if (CollectionUtils.isNotEmpty(arguments)) {
            errors.rejectValue(fieldName, ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                    arguments.toArray(), "Invalid point offset");
        }
    }

    /**
     * Check if pointType is matched with the pointObject retrieved.
     */
    public void checkIfPointTypeChanged(Errors errors, LitePointModel litePointModel, boolean isCreationOperation) {
        if (!pointValidationUtilCommon.checkIfPointTypeChanged(litePointModel, isCreationOperation)) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            errors.rejectValue("pointType", ApiErrorDetails.TYPE_MISMATCH.getCodeString(),
                    new Object[] { litePointModel.getPointType(), pointBase.getPoint().getPointType() },
                    "");
        }
    }

    /**
     * Check if paoId is matched with the pointObject retrieved.
     */
    public void checkIfPaoIdChanged(Errors errors, LitePointModel litePointModel, boolean isCreationOperation) {
        if (!pointValidationUtilCommon.checkIfPaoIdMatch(litePointModel, isCreationOperation)) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            errors.rejectValue("paoId", ApiErrorDetails.TYPE_MISMATCH.getCodeString(),
                    new Object[] { litePointModel.getPaoId(), pointBase.getPoint().getPaoID() }, "");
        }
    }

    /**
     * Check if provided pointId is valid or not.
     */
    public void validatePointId(Errors errors, String field, Integer pointId, String fieldName) {
        if (!pointValidationUtilCommon.validatePointId(pointId)) {
            errors.rejectValue(field, ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { pointId }, "");
        }
    }
}