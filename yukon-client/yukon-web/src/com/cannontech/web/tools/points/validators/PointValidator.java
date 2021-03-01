package com.cannontech.web.tools.points.validators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.model.PointModel;

@Service
public class PointValidator extends SimpleValidator<PointModel> {

    @Autowired private PointValidationUtil pointValidationUtil; 
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private YukonListDao listDao;
    
    private static final String baseKey = "yukon.web.modules.tools.point.error";
    private List<YukonListEntry> yukonListEntryList;
    private List<String> calcOperatorsList;

    public PointValidator() {
        super(PointModel.class);
    }

    
    @PostConstruct
    public void init() {
        YukonSelectionList calcCompList = listDao.getYukonSelectionList(CalcComponentTypes.CALC_FUNCTION_LIST_ID);
        yukonListEntryList = calcCompList.getYukonListEntries();
        calcOperatorsList = Arrays.asList(CalcComponentTypes.CALC_OPERATIONS);

    }
    private static class FdrUniquenessKey {

        private final FdrInterfaceType fdrInterfaceType;
        private final String direction;
        private final String translation;
        private final Integer pointId;
        
        public FdrUniquenessKey(FdrInterfaceType fdrInterfaceType, String direction, String translation, Integer pointId) {
            this.fdrInterfaceType = fdrInterfaceType;
            this.direction = direction;
            this.translation = translation;
            this.pointId = pointId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((direction == null) ? 0 : direction.hashCode());
            result = prime * result + ((fdrInterfaceType == null) ? 0 : fdrInterfaceType.hashCode());
            result = prime * result + ((translation == null) ? 0 : translation.hashCode());
            result = prime * result + ((pointId == null) ? 0 : pointId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FdrUniquenessKey other = (FdrUniquenessKey) obj;
            if (direction == null) {
                if (other.direction != null)
                    return false;
            } else if (!direction.equals(other.direction))
                return false;
            if (fdrInterfaceType != other.fdrInterfaceType)
                return false;
            if (!StringUtils.equals(translation, this.translation))
                return false;
            if (pointId == null) {
                if (other.pointId != null)
                    return false;
            } else if (!pointId.equals(other.pointId))
                return false;
            return true;
        }
    }

    @Override
    public void doValidation(PointModel pointModel, Errors errors) {
        
        PointBase base = pointModel.getPointBase();
        
        LitePointModel litePointModel = LitePointModel.of(base);

        pointValidationUtil.validatePointName(litePointModel, "pointBase.point.pointName", errors, false);
        pointValidationUtil.validatePointOffset(litePointModel, "pointBase.point.pointOffset", errors, false);

        doScalarValidation(pointModel, errors);
        doAnalogValidation(base, errors);
        doAccumulatorValidation(base, errors);
        doStatusValidation(pointModel, errors);
        doCalcValidation(base, errors);

        Set<FdrUniquenessKey> usedTypes = new HashSet<>();

        int index = 0;
        for (FDRTranslation translation : base.getPointFDRList()) {

            FdrUniquenessKey key = new FdrUniquenessKey(translation.getInterfaceEnum(), translation.getDirectionType(),
                translation.getTranslation(), pointModel.getPointBase().getPoint().getPointID());

            if (usedTypes.contains(key)) {
                errors.rejectValue("pointBase.pointFDRList[" +index + "].interfaceType", baseKey + ".fdr.unique");
                errors.rejectValue("pointBase.pointFDRList[" +index + "].directionType", "yukon.common.blank");
            }

            usedTypes.add(key);
            index++;
        }

    }
    
    private void doScalarValidation(PointModel model, Errors errors) {
        
        PointBase base = model.getPointBase();
        
        if (!(base instanceof ScalarPoint)) return;
            
        ScalarPoint scalar = (ScalarPoint) base;
        
        PointLimit limitOne = scalar.getLimitOne();
        if (limitOne != null) {
            if (limitOne.getHighLimit() < limitOne.getLowLimit()) {
                errors.rejectValue("pointBase.pointLimitsMap[1].lowLimit", baseKey + ".limits");
                errors.rejectValue("pointBase.pointLimitsMap[1].highLimit", "yukon.common.blank");
            }
            
            YukonValidationUtils.checkRange(errors, "pointBase.pointLimitsMap[1].highLimit", 
                limitOne.getHighLimit(), -99999999.0, 99999999.0, true);
            
            YukonValidationUtils.checkRange(errors, "pointBase.pointLimitsMap[1].lowLimit", 
                limitOne.getLowLimit(), -99999999.0, 99999999.0, true);
            
            YukonValidationUtils.checkRange(errors, "pointBase.pointLimitsMap[1].limitDuration", 
                limitOne.getLimitDuration(), 0, 99999999, true);
        }
        
        PointLimit limitTwo = scalar.getLimitTwo();
        if (limitTwo != null) {
            if (limitTwo.getHighLimit() < limitTwo.getLowLimit()) {
                errors.rejectValue("pointBase.pointLimitsMap[2].lowLimit", baseKey + ".limits");
                errors.rejectValue("pointBase.pointLimitsMap[2].highLimit", "yukon.common.blank");
            }
            
            YukonValidationUtils.checkRange(errors, "pointBase.pointLimitsMap[2].highLimit", 
                limitTwo.getHighLimit(), -99999999.0, 99999999.0, true);
            
            YukonValidationUtils.checkRange(errors, "pointBase.pointLimitsMap[2].lowLimit", 
                limitTwo.getLowLimit(), -99999999.0, 99999999.0, true);
            
            YukonValidationUtils.checkRange(errors, "pointBase.pointLimitsMap[2].limitDuration", 
                limitTwo.getLimitDuration(), 0, 99999999, true);
        }
        if (scalar.getPointUnit() != null) {
            Double highReasonabilityLimit = scalar.getPointUnit().getHighReasonabilityLimit();
            Double lowReasonabilityLimit = scalar.getPointUnit().getLowReasonabilityLimit();
            if (highReasonabilityLimit < lowReasonabilityLimit) {
                YukonValidationUtils.rejectValues(errors, baseKey + ".reasonability",
                    "pointBase.pointUnit.highReasonabilityLimit", "pointBase.pointUnit.lowReasonabilityLimit");
            }
            if (highReasonabilityLimit != CtiUtilities.INVALID_MAX_DOUBLE)
                YukonValidationUtils.checkRange(errors, "pointBase.pointUnit.highReasonabilityLimit",
                    highReasonabilityLimit, -999999.999999, 999999.999999, true);
            if (lowReasonabilityLimit != CtiUtilities.INVALID_MIN_DOUBLE)
                YukonValidationUtils.checkRange(errors, "pointBase.pointUnit.lowReasonabilityLimit",
                    lowReasonabilityLimit, -999999.999999, 999999.999999, true);
        }
        if (!errors.hasFieldErrors("staleData.time")) {
            YukonValidationUtils.checkRange(errors, "staleData.time",
                    model.getStaleData().getTime(), 0, 99999999, model.getStaleData().isEnabled());
        }
    }
    
    private void doAnalogValidation(PointBase base, Errors errors) {
        
        if (!(base instanceof AnalogPoint)) return;
        AnalogPoint point = (AnalogPoint) base;
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
        YukonValidationUtils.checkRange(errors, "pointBase.pointAccumulator.multiplier", 
            point.getPointAccumulator().getMultiplier(), -99999999.0, 99999999.0, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointAccumulator.dataOffset", 
            point.getPointAccumulator().getDataOffset(), -99999999.0, 99999999.0, true);
    }
    
    private void doStatusValidation(PointModel model, Errors errors) {
        
        PointBase base = model.getPointBase();
        
        if (!(base instanceof StatusPoint)) return;
        StatusPoint point = (StatusPoint) base;
        YukonValidationUtils.checkRange(errors, "staleData.time", 
            model.getStaleData().getTime(), 0, 99999999,  model.getStaleData().isEnabled());
        
        if (point.getPointStatusControl().getControlType() == StatusControlType.NONE.getControlName()) {
            return;
        }
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointStatusControl.controlOffset", 
            point.getPointStatusControl().getControlOffset(), -99999999, 99999999, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointStatusControl.closeTime1", 
            point.getPointStatusControl().getCloseTime1(), 0, 9999, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointStatusControl.closeTime2", 
            point.getPointStatusControl().getCloseTime2(), 0, 9999, true);
        
        YukonValidationUtils.checkRange(errors, "pointBase.pointStatusControl.commandTimeOut", 
            point.getPointStatusControl().getCommandTimeOut(), 0, 9999999, true);

        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
        String openCommandI18nText = messageSourceAccessor.getMessage("yukon.web.modules.tools.point.command.open");
        String closeCommandI18nText = messageSourceAccessor.getMessage("yukon.web.modules.tools.point.command.close");
        YukonValidationUtils.checkIsBlank(errors, "pointBase.pointStatusControl.stateOneControl",
                point.getPointStatusControl().getStateOneControl(), closeCommandI18nText, false);
        YukonValidationUtils.checkIsBlank(errors, "pointBase.pointStatusControl.stateZeroControl",
                point.getPointStatusControl().getStateZeroControl(), openCommandI18nText, false);
        if (!errors.hasFieldErrors("pointBase.pointStatusControl.stateOneControl")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "pointBase.pointStatusControl.stateOneControl",
                    point.getPointStatusControl().getStateOneControl(), 100);
        }
        if (!errors.hasFieldErrors("pointBase.pointStatusControl.stateZeroControl")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "pointBase.pointStatusControl.stateZeroControl",
                    point.getPointStatusControl().getStateZeroControl(), 100);
        }
    }

    private void doCalcValidation(PointBase base, Errors errors) {
        if (base instanceof CalcStatusPoint || base instanceof CalculatedPoint) {
            List<CalcComponent> calcComponents;
            if (base instanceof CalcStatusPoint) {
                CalcStatusPoint calcPoint = (CalcStatusPoint) base;
                calcComponents = calcPoint.getCalcComponents();
            } else {
                CalculatedPoint calcPoint = (CalculatedPoint) base;
                calcComponents = calcPoint.getCalcComponents();
            }
            int index = 0;
            for (CalcComponent calcComponent : calcComponents) {
                if (calcComponent.getConstant() == null) {
                    if (!errors.hasFieldErrors("pointBase.calcComponents[" + index + "].constant")
                            && calcComponent.getConstant() == null) {
                        errors.rejectValue("pointBase.calcComponents[" + index + "].constant", "yukon.web.error.isBlank");
                    }
                }
                validateSupportedOperation(calcComponent, errors, index);
                index++;
            }
        }
    }

    private void validateSupportedOperation(CalcComponent calcComponent, Errors errors, int index) {
        if (!calcComponent.getFunctionName().isEmpty() && !calcComponent.getFunctionName().equals("(none)")) {
            if (!yukonListEntryList.toString().contains(calcComponent.getFunctionName().toString()))
                errors.rejectValue("pointBase.calcComponents[" + index + "].functionName", "yukon.web.error.notSupported",
                        new Object[] { "Operation" }, "");
        }
        if (!calcComponent.getOperation().isEmpty() && !calcComponent.getOperation().equals("(none)")) {
            if (!calcOperatorsList.toString().contains(calcComponent.getOperation()))
                errors.rejectValue("pointBase.calcComponents[" + index + "].operation", "yukon.web.error.notSupported",
                        new Object[] { "Operation" }, "");
        }
    }
}