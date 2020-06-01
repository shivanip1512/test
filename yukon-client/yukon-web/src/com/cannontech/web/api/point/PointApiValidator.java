package com.cannontech.web.api.point;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.ScalarPointModel;
import com.cannontech.web.tools.points.validators.PointValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

public class PointApiValidator<T extends PointBaseModel<?>> extends SimpleValidator<T> {
    @Autowired private PointValidationUtil pointValidationUtil;
    @Autowired private IDatabaseCache serverDatabaseCache;

    @SuppressWarnings("unchecked")
    public PointApiValidator() {
        super((Class<T>) PointBaseModel.class);
    }

    public PointApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T target, Errors errors) {

        if (target.getPaoId() != null) {
            LiteYukonPAObject liteYukonPAObject = serverDatabaseCache.getAllPaosMap().get(target.getPaoId());
            if (liteYukonPAObject == null) {
                errors.rejectValue("paoId", "yukon.web.api.error.doesNotExist", new Object[] { "Pao Id" }, "");
            }

            if (!errors.hasFieldErrors("paoId") && target.getPointName() != null && target.getPointOffset() != null) {
                boolean physicalOffset = target.getPointOffset() > 0 ? true : false;
                Integer pointId = null;
                if (ServletUtils.getPathVariable("id") != null) {
                    pointId = Integer.valueOf(ServletUtils.getPathVariable("id"));
                }

                LitePointModel litePointModel = new LitePointModel(target.getPointName(),
                                                                   pointId,
                                                                   physicalOffset,
                                                                   target.getPointOffset(),
                                                                   target.getPointType(),
                                                                   target.getPaoId());

                boolean isCreationOperation = pointId == null ? true : false;

                pointValidationUtil.validatePointName(litePointModel, "pointName", errors, isCreationOperation);
                pointValidationUtil.validatePointOffset(litePointModel, "pointOffset", errors, isCreationOperation);
            }
        }

        if (target instanceof ScalarPointModel) {
            ScalarPointModel<?> scalarPointModel = (ScalarPointModel<?>) target;

            if (scalarPointModel.getPointUnit() != null && scalarPointModel.getPointUnit().getUomId() != null) {
                List<UnitOfMeasure> unitMeasures = UnitOfMeasure.allValidValues();
                List<Integer> uomIds = unitMeasures.stream().map(unit -> unit.getId()).collect(Collectors.toList());
                if (!uomIds.contains(scalarPointModel.getPointUnit().getUomId())) {
                    errors.rejectValue("pointUnit.uomID", "yukon.web.api.error.doesNotExist",  new Object[] { "Uom Id" }, "");
                }
            }
        }

    }

}
