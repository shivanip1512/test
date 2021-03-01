package com.cannontech.web.tools.points.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.google.common.collect.ImmutableList;

public class PointValidationUtilCommon extends ValidationUtils {

    @Autowired private PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public boolean validatePointName(LitePointModel pointModel, boolean isCopyOrCreate) {
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
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validatePaoName(String pointName) {
        return (PaoUtils.isValidPaoName(pointName)) ? true : false;
    }

    public String isPointOrPhyicalOffset(LitePointModel pointModel, String fieldName, Errors errors) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
        if (pointModel.isPhysicalOffset() && !errors.hasFieldErrors(fieldName)) {
            String physicalPort = messageSourceAccessor.getMessage("yukon.web.modules.tools.point.physicalOffset");
            if (pointModel.getPointType().isCalcPoint()) {
                physicalPort = messageSourceAccessor.getMessage("yukon.web.modules.tools.point.offset");
                return physicalPort;
            }
            return physicalPort;
        }
        return null;
    }

    public List<Object> isValidPointOffset(LitePointModel pointModel, boolean isCopyOrCreate) {

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
                        return arguments;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check if pointType is matched with the pointObject retrieved.
     */
    public boolean checkIfPointTypeChanged(LitePointModel litePointModel, boolean isCreationOperation) {
        if (!isCreationOperation && litePointModel.getPointId() != null) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            if (litePointModel.getPointType() != null
                    && litePointModel.getPointType() != PointType.getForString(pointBase.getPoint().getPointType())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if paoId is matched with the pointObject retrieved.
     */
    public boolean checkIfPaoIdMatch(LitePointModel litePointModel, boolean isCreationOperation) {
        if (!isCreationOperation && litePointModel.getPointId() != null) {
            PointBase pointBase = pointDao.get(litePointModel.getPointId());
            if (litePointModel.getPaoId() != null && !litePointModel.getPaoId().equals(pointBase.getPoint().getPaoID())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if provided pointId is valid or not.
     */
    public boolean validatePointId(Integer pointId) {
        try {
            pointDao.getLitePoint(pointId);
        } catch (NotFoundException ex) {
            return false;
        }
        return true;
    }
}
