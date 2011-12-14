package com.cannontech.common.pao.definition.model;

import com.cannontech.database.db.point.calculation.CalcComponentTypes;

public class CalcPointComponent {

    private PointIdentifier pointIdentifier;
    private String calcComponentType = CalcComponentTypes.OPERATION_COMP_TYPE;
    private String operation = CalcComponentTypes.ADDITION_OPERATION;

    public CalcPointComponent(PointIdentifier pointIdentifier, String calcComponentType, String operation) {
        this.pointIdentifier = pointIdentifier;
        this.calcComponentType = calcComponentType;
        this.operation = operation;
    }

    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }

    public void setPointIdentifier(PointIdentifier pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }

    public String getCalcComponentType() {
        return calcComponentType;
    }

    public void setCalcComponentType(String calcComponentType) {
        this.calcComponentType = calcComponentType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

}
