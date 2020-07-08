package com.cannontech.web.api.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.tools.points.model.CopyPoint;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.validators.PointValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

public class CopyPointValidator extends SimpleValidator<CopyPoint> {

    @Autowired private PointValidationUtil pointValidationUtil;
    @Autowired private IDatabaseCache serverDatabaseCache;
    protected static final String baseKey = "yukon.web.api.error";
    @Autowired private PointDao pointDao;
  
    public CopyPointValidator() {
        super(CopyPoint.class);
    }

    @Override
    protected void doValidation(CopyPoint copyPoint, Errors errors) {
        String idStr = ServletUtils.getPathVariable("id");
        PointBase pointBase = pointDao.get(Integer.valueOf(idStr));
        
        LitePointModel pointBaseModel = new LitePointModel();
        pointBaseModel.setPointName(copyPoint.getPointName());
        pointBaseModel.setPointOffset(copyPoint.getPointOffset());
        pointBaseModel.setPaoId(copyPoint.getPaoId());
        pointBaseModel.setPointType(pointBase.getPoint().getPointTypeEnum());
       
        // Check if point Name is NULL
        YukonValidationUtils.checkIfFieldRequired("pointName", errors, copyPoint.getPointName(), "pointName");
        
        if (copyPoint.getPointName() != null) {
            YukonValidationUtils.checkIsBlank(errors, "pointName", copyPoint.getPointName(), "Name", false);
        }
       
        // Check if pointOffset is NULL
        YukonValidationUtils.checkIfFieldRequired("pointOffset", errors, copyPoint.getPointOffset(), "pointOffset");
      
        // Check if paoId is NULL
        YukonValidationUtils.checkIfFieldRequired("paoId", errors, copyPoint.getPaoId(), "paoId");
        if (!errors.hasFieldErrors("paoId") && copyPoint.getPaoId() != null) {
            LiteYukonPAObject liteYukonPAObject = serverDatabaseCache.getAllPaosMap().get(copyPoint.getPaoId());
            if (liteYukonPAObject == null) {
                errors.rejectValue("paoId", baseKey + ".doesNotExist", new Object[] { copyPoint.getPaoId() }, "");
            }
        }
        
        if (!errors.hasFieldErrors("paoId")) {
            if (!errors.hasFieldErrors("pointName") && copyPoint.getPointName() != null) {
                pointValidationUtil.validatePointName(pointBaseModel, "pointName", errors, true);
            }

            if (!errors.hasFieldErrors("pointOffset") && copyPoint.getPointOffset() != null) {
                pointValidationUtil.validatePointOffset(pointBaseModel, "pointOffset", errors, true);
            }
        }
    }
}
