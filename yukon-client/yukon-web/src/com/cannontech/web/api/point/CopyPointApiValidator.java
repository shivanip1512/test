package com.cannontech.web.api.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.tools.points.model.CopyPoint;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.validators.PointValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

public class CopyPointApiValidator extends SimpleValidator<CopyPoint> {

    @Autowired private PointValidationUtil pointValidationUtil;
    @Autowired private IDatabaseCache serverDatabaseCache;
    protected static final String baseKey = "yukon.web.api.error";
    @Autowired private PointDao pointDao;

    public CopyPointApiValidator() {
        super(CopyPoint.class);
    }

    protected void doValidation(CopyPoint copyPoint, Errors errors) {

        // Check that valid pointId is passed for copy. If PointId doesn't exist it will give error
        String idStr = ServletUtils.getPathVariable("id");
        PointBase pointBase = pointDao.get(Integer.valueOf(idStr));

        // Check if point Name is NULL
        YukonValidationUtils.checkIfFieldRequired("pointName", errors, copyPoint.getPointName(), "pointName");

        if (copyPoint.getPointName() != null) {
            YukonValidationUtils.checkIsBlank(errors, "pointName", copyPoint.getPointName(), "Point Name", false);
        }

        // Check if pointOffset is NULL
        YukonValidationUtils.checkIfFieldRequired("pointOffset", errors, copyPoint.getPointOffset(), "pointOffset");

        // Check if paoId is NULL
        YukonValidationUtils.checkIfFieldRequired("paoId", errors, copyPoint.getPaoId(), "paoId");
        if (!errors.hasFieldErrors("paoId")) {
            LiteYukonPAObject liteYukonPAObject = serverDatabaseCache.getAllPaosMap().get(copyPoint.getPaoId());
            if (liteYukonPAObject == null) {
                errors.rejectValue("paoId", baseKey + ".doesNotExist", new Object[] { copyPoint.getPaoId() }, "");
            }
            if (!errors.hasFieldErrors("paoId")) {

                // here we require findPaoPointIdentifier (return null) instead of getPaoPointIdentifier
                PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(Integer.valueOf(idStr));
                LitePointModel pointModel = new LitePointModel();
                pointModel.setPointName(copyPoint.getPointName());
                pointModel.setPointOffset(copyPoint.getPointOffset());
                pointModel.setPaoId(copyPoint.getPaoId());
                pointModel.setPointType(paoPointIdentifier.getPointIdentifier().getPointType());

                if (!errors.hasFieldErrors("pointName")) {
                    pointValidationUtil.validatePointName(pointModel, "pointName", errors, true);
                }

                if (!errors.hasFieldErrors("pointOffset")) {
                    pointValidationUtil.validatePointOffset(pointModel, "pointOffset", errors, true);
                }
            }
        }
    }
}
