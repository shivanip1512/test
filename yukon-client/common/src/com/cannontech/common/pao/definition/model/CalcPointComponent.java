package com.cannontech.common.pao.definition.model;

import com.cannontech.database.db.point.calculation.CalcComponentTypes;

public class CalcPointComponent {

    private int pointId;
    private String pointName;
    private String type = CalcComponentTypes.OPERATION_COMP_TYPE;
    private String operation = CalcComponentTypes.ADDITION_OPERATION;

    public CalcPointComponent(String pointName, String type, String operation) {
        this.pointName = pointName;
        this.type = type;
        this.operation = operation;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

}
