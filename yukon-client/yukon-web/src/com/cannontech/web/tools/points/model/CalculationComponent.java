package com.cannontech.web.tools.points.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.point.calculation.CalcComponent;

public class CalculationComponent implements DBPersistentConverter<com.cannontech.database.db.point.calculation.CalcComponent> {

    private CalcCompType componentType;
    private String operation;
    private Double operand;

    public CalcCompType getComponentType() {
        return componentType;
    }

    public void setComponentType(CalcCompType componentType) {
        this.componentType = componentType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Double getOperand() {
        return operand;
    }

    public void setOperand(Double operand) {
        this.operand = operand;
    }

    @Override
    public void buildDBPersistent(CalcComponent calcComponent) {

        if (getComponentType() != null) {
            calcComponent.setComponentType(getComponentType().getCalcCompType());
            if (getComponentType() == CalcCompType.OPERATION) {
                if (getOperand() != null) {
                    calcComponent.setComponentPointID(getOperand().intValue());
                }
                if (getOperation() != null) {
                    calcComponent.setOperation(getOperation());
                }

                calcComponent.setConstant(0.0);
                calcComponent.setFunctionName(CtiUtilities.STRING_NONE);

            } else if (getComponentType() == CalcCompType.CONSTANT) {

                if (getOperation() != null) {
                    calcComponent.setOperation(getOperation());
                }

                if (getOperand() != null) {
                    calcComponent.setConstant(getOperand());
                }

                calcComponent.setComponentPointID(0);
                calcComponent.setFunctionName(CtiUtilities.STRING_NONE);

            } else if (getComponentType() == CalcCompType.FUNCTION) {
                if (getOperation() != null) {
                    calcComponent.setFunctionName(getOperation());
                }

                if (getOperand() != null) {
                    calcComponent.setComponentPointID(getOperand().intValue());
                }
                calcComponent.setConstant(0.0);
                calcComponent.setOperation(CtiUtilities.STRING_NONE);
            }
        }
    }

    @Override
    public void buildModel(CalcComponent calcComponent) {
        setComponentType(CalcCompType.getCalcCompType(calcComponent.getComponentType()));

        if (getComponentType() == CalcCompType.CONSTANT) {
            setOperation(calcComponent.getOperation());
            setOperand(calcComponent.getConstant());
        }

        if (getComponentType() == CalcCompType.FUNCTION) {
            setOperand(calcComponent.getComponentPointID().doubleValue());
            setOperation(calcComponent.getFunctionName());
        }

        if (getComponentType() == CalcCompType.OPERATION) {
            setOperand(calcComponent.getComponentPointID().doubleValue());
            setOperation(calcComponent.getOperation());
        }

    }

}
