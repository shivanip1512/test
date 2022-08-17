package com.cannontech.web.tools.points.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.google.common.collect.ImmutableList;

public class PointValidationUtil extends ValidationUtils {

    @Autowired private PointDao pointDao;

    private static final String baseKey = "yukon.web.modules.tools.point.error";

    public void validatePointName(LitePointModel pointModel, String fieldName, Errors errors, boolean isCopyOperation) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "yukon.web.error.isBlank");
        YukonValidationUtils.checkExceedsMaxLength(errors, fieldName, pointModel.getPointName(), 60);
        if (!PaoUtils.isValidPaoName(pointModel.getPointName())) {
            errors.rejectValue(fieldName, "yukon.web.error.paoName.containsIllegalChars");
        }
        List<LitePoint> pointsOnPao = pointDao.getLitePointsByPaObjectId(pointModel.getPaoId());

        for (LitePoint pointOnPao : pointsOnPao) {
            if (pointOnPao.getPointName().trim().equalsIgnoreCase(pointModel.getPointName().trim())) {
                /**
                 *  1. If the current operation is point copy and there already exists a point with the same name
                 *  attached to the PAO, we should not proceed with the operation.
                 *  2. If the current operation is point edit and there exists any other point (other than the current 
                 *  one) with the same name attached to the PAO, we should not proceed with the operation.
                 */
                if (isCopyOperation || (pointOnPao.getPointID() != pointModel.getPointId())) {
                    errors.rejectValue(fieldName, "yukon.web.error.nameConflict");
                }
            }
        }
    }

    public void validatePointOffset(LitePointModel pointModel, String fieldName, Errors errors,
            boolean isCopyOperation) {

        if (pointModel.isPhysicalOffset()) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "yukon.web.error.isBlank");
        }

        int parentId = pointModel.getPaoId();
        List<LitePoint> pointsOnPao = pointDao.getLitePointsByPaObjectId(parentId);

        if (pointModel.getPointOffset() != null) {
            for (LitePoint pointOnPao : pointsOnPao) {

                if (pointModel.getPointOffset() != 0 && (pointOnPao.getPointOffset() == pointModel.getPointOffset()
                    && pointOnPao.getPointTypeEnum() == pointModel.getPointType())) {
                    /**
                     *  1. If the current operation is point copy and there already exists a point with the same name
                     *  attached to the PAO, we should not proceed with the operation.
                     *  2. If the current operation is point edit and there exists any other point (other than the current 
                     *  one) with the same name attached to the PAO, we should not proceed with the operation.
                     */
                    if (isCopyOperation || (pointOnPao.getPointID() != pointModel.getPointId())) {
                        List<Object> arguments = ImmutableList.of(pointOnPao.getPointName());
                        errors.rejectValue(fieldName, baseKey + ".pointOffset", arguments.toArray(),
                            "Invalid point offset");
                    }
                }
            }
        }
    }

}
