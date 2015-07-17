package com.cannontech.web.capcontrol.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.web.capcontrol.models.PointModel;
import com.google.common.collect.ImmutableList;

@Service
public class PointValidator extends SimpleValidator<PointModel> {

    @Autowired PointDao pointDao;
    
    private static final String baseKey = "yukon.web.modules.capcontrol.point.error";

    public PointValidator() {
        super(PointModel.class);
    }

    @Override
    public void doValidation(PointModel pointModel, Errors errors) {
        
        PointBase base = pointModel.getPointBase();
        
        doScalarValidation(pointModel, errors);
        doAnalogValidation(base, errors);
        doAccumulatorValidation(base, errors);
        doStatusValidation(pointModel, errors);

        int parentId = base.getPoint().getPaoID();
        List<LitePoint> pointsOnPao = pointDao.getLitePointsByPaObjectId(parentId);
        for (LitePoint pointOnPao : pointsOnPao) {
            if (pointOnPao.getPointOffset() == base.getPoint().getPointOffset()) {
                if (pointOnPao.getPointID() != base.getPoint().getPointID()) {
                    List<Object> arguments = ImmutableList.of(pointOnPao.getPointName());
                    errors.rejectValue("pointBase.point.pointOffset", baseKey + ".pointOffset", arguments.toArray(), 
                        "Invalid point offset");
                }
            }
        }
    }
    
    private void doScalarValidation(PointModel model, Errors errors) {
        
        PointBase base = model.getPointBase();
        
        if (!(base instanceof ScalarPoint)) return;
            
        ScalarPoint scalar = (ScalarPoint) base;
        
        PointLimit limitOne = scalar.getLimitOne();
        if (limitOne != null) {
            if (limitOne.getHighLimit() < limitOne.getLowLimit()) {
                errors.rejectValue("pointBase.limitOne.lowLimit", baseKey + ".limits");
                errors.reject("pointBase.limitOne.highLimit");
            }
            
            YukonValidationUtils.checkRange(errors, "pointBase.limitOne.highLimit", 
                limitOne.getHighLimit(), -99999999.0, 99999999.0, true);
            
            YukonValidationUtils.checkRange(errors, "pointBase.limitOne.lowLimit", 
                limitOne.getLowLimit(), -99999999.0, 99999999.0, true);
            
            YukonValidationUtils.checkRange(errors, "pointBase.limitOne.limitDuration", 
                limitOne.getLimitDuration(), 0, 99999999, true);
        }
        
        PointLimit limitTwo = scalar.getLimitTwo();
        if (limitTwo != null) {
            if (limitTwo.getHighLimit() < limitTwo.getLowLimit()) {
                errors.rejectValue("pointBase.limitTwo.lowLimit", baseKey + ".limits");
                errors.reject("pointBase.limitTwo.highLimit");
            }
            
            YukonValidationUtils.checkRange(errors, "pointBase.limitTwo.highLimit", 
                limitTwo.getHighLimit(), -99999999.0, 99999999.0, true);
            
            YukonValidationUtils.checkRange(errors, "pointBase.limitTwo.lowLimit", 
                limitTwo.getLowLimit(), -99999999.0, 99999999.0, true);
            
            YukonValidationUtils.checkRange(errors, "pointBase.limitTwo.limitDuration", 
                limitTwo.getLimitDuration(), 0, 99999999, true);
        }
        
        YukonValidationUtils.checkRange(errors, "staleData.time", 
            model.getStaleData().getTime(), 0, 99999999, true);
    }
    
    private void doAnalogValidation(PointBase base, Errors errors) {
        
        if (!(base instanceof AnalogPoint)) return;
        AnalogPoint point = (AnalogPoint) base;
        
        YukonValidationUtils.checkRange(errors, "pointBase.point.pointOffset", 
            point.getPoint().getPointOffset(), 0, 99999999, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointAnalog.deadband", 
            point.getPointAnalog().getDeadband(), -1.0, 99999999.0, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointAnalog.multiplier", 
            point.getPointAnalog().getMultiplier(), -99999999.0, 99999999.0, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointAnalog.dataOffset", 
            point.getPointAnalog().getDataOffset(), -99999999.0, 99999999.0, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointAnalogControl.controlOffset", 
            point.getPointAnalogControl().getControlOffset(), -99999999, 99999999, true);
    }
    
    private void doAccumulatorValidation(PointBase base, Errors errors) {
        
        if (!(base instanceof AccumulatorPoint)) return;
        AccumulatorPoint point = (AccumulatorPoint) base;
        
        YukonValidationUtils.checkRange(errors, "pointBase.point.pointOffset", 
            point.getPoint().getPointOffset(), 0, 99999999, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointAccumulator.multiplier", 
            point.getPointAccumulator().getMultiplier(), -99999999.0, 99999999.0, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointAccumulator.dataOffset", 
            point.getPointAccumulator().getDataOffset(), -99999999.0, 99999999.0, true);
    }
    
    private void doStatusValidation(PointModel model, Errors errors) {
        
        PointBase base = model.getPointBase();
        
        if (!(base instanceof StatusPoint)) return;
        if (base instanceof CalcStatusPoint) return;
        
        StatusPoint point = (StatusPoint) base;
        
        YukonValidationUtils.checkRange(errors, "pointBase.point.pointOffset", 
            point.getPoint().getPointOffset(), 0, 99999999, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointStatusControl.controlOffset", 
            point.getPointStatusControl().getControlOffset(), -99999999, 99999999, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointStatusControl.closeTime1", 
            point.getPointStatusControl().getCloseTime1(), 0, 9999, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointStatusControl.closeTime2", 
            point.getPointStatusControl().getCloseTime2(), 0, 9999, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointStatusControl.commandTimeOut", 
            point.getPointStatusControl().getCommandTimeOut(), 0, 9999999, true);
        
        YukonValidationUtils.checkRange(errors, "staleData.time", 
            model.getStaleData().getTime(), 0, 99999999, true);
        
    }
    
}
