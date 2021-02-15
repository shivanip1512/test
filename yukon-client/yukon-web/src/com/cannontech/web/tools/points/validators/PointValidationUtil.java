package com.cannontech.web.tools.points.validators;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.util.Range;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.web.tools.points.model.LitePointModel;

public class PointValidationUtil extends ValidationUtils {

    @Autowired private PointDao pointDao;
    @Autowired private PointValidationUtilCommon pointValidationUtilCommon;

    private static final String baseKey = "yukon.web.modules.tools.point.error";

    public void validatePointName(LitePointModel pointModel, String fieldName, Errors errors, boolean isCopyOrCreate) {
        validateName(fieldName, errors, pointModel.getPointName());
        if (!pointValidationUtilCommon.validatePointName(pointModel, isCopyOrCreate)) {
            errors.rejectValue(fieldName, "yukon.web.error.nameConflict");
        }
    }

    public void validateName(String fieldName, Errors errors, String pointName) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "yukon.web.error.isBlank");
        YukonValidationUtils.checkExceedsMaxLength(errors, fieldName, pointName, 60);
        if (!pointValidationUtilCommon.validatePaoName(pointName)) {
            errors.rejectValue(fieldName, "yukon.web.error.paoName.containsIllegalChars");
        }
    }

    public void validatePointOffset(LitePointModel pointModel, String fieldName, Errors errors,
            boolean isCopyOrCreate) {
        String physicalPort = pointValidationUtilCommon.isPointOrPhyicalOffset(pointModel, fieldName, errors);
        if (StringUtils.isNotEmpty(physicalPort)) {
            Range<Integer> range = Range.inclusive(0, 99999999);
            YukonValidationUtils.checkRange(errors, fieldName, physicalPort,
                    pointModel.getPointOffset(), range, true);
        }
        List<Object> arguments = pointValidationUtilCommon.isValidPointOffset(pointModel, isCopyOrCreate);
        if (CollectionUtils.isNotEmpty(arguments)) {
            errors.rejectValue(fieldName, baseKey + ".pointOffset", arguments.toArray(),
                    "Invalid point offset");
        }
    }

    /**
     * Check if pointType is matched with the pointObject retrieved.
     */
    public void checkIfPointTypeChanged(Errors errors, LitePointModel litePointModel, boolean isCreationOperation) {
        if (!pointValidationUtilCommon.checkIfPointTypeChanged(litePointModel, isCreationOperation)) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            errors.rejectValue("pointType", "yukon.web.api.error.pointTypeMismatch",
                    new Object[] { litePointModel.getPointType(), pointBase.getPoint().getPointType(),
                            litePointModel.getPointId() },
                    "");
        }
    }

    /**
     * Check if paoId is matched with the pointObject retrieved.
     */
    public void checkIfPaoIdChanged(Errors errors, LitePointModel litePointModel, boolean isCreationOperation) {
        if (!pointValidationUtilCommon.checkIfPaoIdMatch(litePointModel, isCreationOperation)) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            errors.rejectValue("paoId", "yukon.web.api.error.paoIdMismatch",
                    new Object[] { litePointModel.getPaoId(), pointBase.getPoint().getPaoID() }, "");
        }
    }

    /**
     * Check if provided pointId is valid or not.
     */
    public void validatePointId(Errors errors, String field, Integer pointId, String fieldName) {
        if (!pointValidationUtilCommon.validatePointId(pointId)) {
            errors.rejectValue(field, "yukon.web.modules.dr.setup.error.pointId.doesNotExist", new Object[] { fieldName }, "");
        }
    }
}