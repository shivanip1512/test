package com.cannontech.web.api.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.model.PointCopy;
import com.cannontech.web.tools.points.validators.PointApiValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

public class PointCopyApiValidator extends SimpleValidator<PointCopy> {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    @Autowired private PointApiValidationUtil pointApiValidationUtil;
    @Autowired private IDatabaseCache serverDatabaseCache;
    protected static final String baseKey = "yukon.web.api.error";
    @Autowired private PointDao pointDao;

    public PointCopyApiValidator() {
        super(PointCopy.class);
    }

    protected void doValidation(PointCopy copyPoint, Errors errors) {

        String idStr = ServletUtils.getPathVariable("id");
        // Handled NumberFormatException
        try {
            Integer pointId = Integer.valueOf(idStr);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Invalid pointId value.");
        }

        // Check that pointId which is used for copying any Point exist.
        PaoPointIdentifier paoPointIdentifier = pointDao.findPaoPointIdentifier(Integer.valueOf(idStr));
        if (paoPointIdentifier == null) {
            throw new NotFoundException("A point with id " + idStr + " cannot be found.");
        }
       
        // Check if point Name is NULL
        yukonApiValidationUtils.checkIfFieldRequired("pointName", errors, copyPoint.getPointName(), "Point Name");

        if (copyPoint.getPointName() != null) {
            yukonApiValidationUtils.checkIsBlank(errors, "pointName", copyPoint.getPointName(), "Point Name", false);
        }

        // Check if pointOffset is NULL
        yukonApiValidationUtils.checkIfFieldRequired("pointOffset", errors, copyPoint.getPointOffset(), "Point Offset");

        // Check if paoId is NULL
        yukonApiValidationUtils.checkIfFieldRequired("paoId", errors, copyPoint.getPaoId(), "PaoId");
        if (!errors.hasFieldErrors("paoId")) {
            LiteYukonPAObject liteYukonPAObject = serverDatabaseCache.getAllPaosMap().get(copyPoint.getPaoId());
            if (liteYukonPAObject == null) {
                errors.rejectValue("paoId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                        new Object[] { copyPoint.getPaoId() }, "");
            }
            if (!errors.hasFieldErrors("paoId")) {
                
                LitePointModel pointModel = new LitePointModel();
                pointModel.setPointName(copyPoint.getPointName());
                pointModel.setPaoId(copyPoint.getPaoId());
                pointModel.setPointType(paoPointIdentifier.getPointIdentifier().getPointType());
              
                if (!errors.hasFieldErrors("pointName")) {
                    pointApiValidationUtil.validatePointName(pointModel, "pointName", errors, true);
                }
                
                // Check pointOffset range
                yukonApiValidationUtils.checkRange(errors, "pointOffset", copyPoint.getPointOffset(), 0, 99999999, true);
                if (!errors.hasFieldErrors("pointOffset")) {
                    pointModel.setPointOffset(copyPoint.getPointOffset());
                    boolean physicalPointOffset = pointModel.getPointOffset() > 0 ? true : false;
                    pointModel.setPhysicalOffset(physicalPointOffset);
                    pointApiValidationUtil.validatePointOffset(pointModel, "pointOffset", errors, true);
                }
            }
        }
    }
}
