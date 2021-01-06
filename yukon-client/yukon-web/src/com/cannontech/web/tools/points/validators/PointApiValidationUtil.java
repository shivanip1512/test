package com.cannontech.web.tools.points.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.google.common.collect.ImmutableList;

public class PointApiValidationUtil extends ValidationUtils {

    @Autowired private PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public void validatePointName(LitePointModel pointModel, String fieldName, Errors errors, boolean isCopyOrCreate) {
        validateName(fieldName, errors, pointModel.getPointName());
        List<LitePoint> pointsOnPao = pointDao.getLitePointsByPaObjectId(pointModel.getPaoId());

        for (LitePoint pointOnPao : pointsOnPao) {
            if (pointOnPao.getPointName().trim().equalsIgnoreCase(pointModel.getPointName().trim())) {
                /**
                 * 1. If the current operation is point copy and there already exists a point with the same name
                 * attached to the PAO, we should not proceed with the operation.
                 * 2. If the current operation is point edit and there exists any other point (other than the current
                 * one) with the same name attached to the PAO, we should not proceed with the operation.
                 */
                if (isCopyOrCreate || (pointModel.getPointId() != null && pointOnPao.getPointID() != pointModel.getPointId())) {
                    errors.rejectValue(fieldName, ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { fieldName }, "");
                }
            }
        }
    }

    public void validateName(String fieldName, Errors errors, String pointName) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "yukon.web.error.isBlank");
        YukonValidationUtils.checkExceedsMaxLength(errors, fieldName, pointName, 60);
        if (!PaoUtils.isValidPaoName(pointName)) {
            errors.rejectValue(fieldName, ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    public void validatePointOffset(LitePointModel pointModel, String fieldName, Errors errors,
            boolean isCopyOrCreate) {

        if (pointModel.isPhysicalOffset() && !errors.hasFieldErrors(fieldName)) {
            MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
            String physicalPort = messageSourceAccessor.getMessage("yukon.web.modules.tools.point.physicalOffset");
            if (pointModel.getPointType().isCalcPoint()) {
                physicalPort = messageSourceAccessor.getMessage("yukon.web.modules.tools.point.offset");
            }
            Range<Integer> range = Range.inclusive(0, 99999999);
            YukonValidationUtils.checkRange(errors, fieldName, physicalPort,
                    pointModel.getPointOffset(), range, true);
        }

        int parentId = pointModel.getPaoId();
        List<LitePoint> pointsOnPao = pointDao.getLitePointsByPaObjectId(parentId);

        if (pointModel.getPointOffset() != null) {
            for (LitePoint pointOnPao : pointsOnPao) {

                if (pointModel.getPointOffset() != 0 && (pointOnPao.getPointOffset() == pointModel.getPointOffset()
                        && pointOnPao.getPointTypeEnum() == pointModel.getPointType())) {
                    /**
                     * 1. If the current operation is point copy and there already exists a point with the same name
                     * attached to the PAO, we should not proceed with the operation.
                     * 2. If the current operation is point edit and there exists any other point (other than the current
                     * one) with the same name attached to the PAO, we should not proceed with the operation.
                     */
                    if (isCopyOrCreate
                            || (pointModel.getPointId() != null && pointOnPao.getPointID() != pointModel.getPointId())) {
                        List<Object> arguments = ImmutableList.of(pointOnPao.getPointName());
                        errors.rejectValue(fieldName, ApiErrorDetails.POINT_OFFSET_NOT_AVAILABLE.getCodeString(),
                                arguments.toArray(), "Invalid point offset");
                    }
                }
            }
        }
    }

    /**
     * Check if pointType is matched with the pointObject retrieved.
     */
    public void checkIfPointTypeChanged(Errors errors, LitePointModel litePointModel, boolean isCreationOperation) {
        if (!isCreationOperation && litePointModel.getPointId() != null) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            if (litePointModel.getPointType() != null
                    && litePointModel.getPointType() != PointType.getForString(pointBase.getPoint().getPointType())) {
                errors.rejectValue("pointType", ApiErrorDetails.POINT_TYPE_MISMATCH.getCodeString(),
                        new Object[] { litePointModel.getPointType(), pointBase.getPoint().getPointType(),
                                litePointModel.getPointId() },
                        "");
            }
        }
    }

    /**
     * Check if paoId is matched with the pointObject retrieved.
     */
    public void checkIfPaoIdChanged(Errors errors, LitePointModel litePointModel, boolean isCreationOperation) {
        if (!isCreationOperation && litePointModel.getPointId() != null) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            if (litePointModel.getPaoId() != null && !litePointModel.getPaoId().equals(pointBase.getPoint().getPaoID())) {
                errors.rejectValue("paoId", ApiErrorDetails.PAO_ID_MISMATCH.getCodeString(),
                        new Object[] { litePointModel.getPaoId(), pointBase.getPoint().getPaoID() }, "");
            }
        }
    }

    /**
     * Check if provided pointId is valid or not.
     */
    public void validatePointId(Errors errors, String field, Integer pointId, String fieldName) {
        try {
            pointDao.getLitePoint(pointId);
        } catch (NotFoundException ex) {
            errors.rejectValue(field, ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { fieldName }, "");
        }
    }
}