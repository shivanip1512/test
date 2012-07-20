package com.cannontech.common.point;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.core.dao.PaoDao;


public class PointBuilderFactory {
    @Autowired private PointPropertyValueDao pointPropertyValueDao;
    @Autowired private PaoDao paoDao;
    
    /**
     * Get a builder for AnalogPoints. Call build() to create an AnalogPoint object, or insert() to 
     * create and insert it into the database.
     */
    public AnalogPointBuilder getAnalogPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled) {
        return new AnalogPointBuilder(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
    }
    
    /**
     * Get a builder for StatusPoints. Call build() to create a StatusPoint object, or insert() to 
     * create and insert it into the database.
     */
    public StatusPointBuilder getStatusPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled) {
        return new StatusPointBuilder(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
    }
    
    /**
     * Get a builder for AccumulatorPoints. Call build() to create an AccumulatorPoint object, or 
     * insert() to create and insert it into the database.
     */
    public AccumulatorPointBuilder getAccumulatorPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled, AccumulatorType accumulatorType) {
        return new AccumulatorPointBuilder(paoId, pointId, pointName, isDisabled, accumulatorType, pointPropertyValueDao);
    }
    
    /**
     * Get a builder for CalcStatusPoints. Call build() to create a CalcStatusPoint object, or 
     * insert() to create and insert it into the database.
     */
    public CalcStatusPointBuilder getCalcStatusPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled) {
        return new CalcStatusPointBuilder(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
    }
    
    /**
     * Get a builder for CalcAnalogPoints. Call build() to create a CalcStatusPoint object, or 
     * insert() to create and insert it into the database.
     */
    public CalcAnalogPointBuilder getCalcAnalogPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled) {
        return new CalcAnalogPointBuilder(paoId, pointId, pointName, isDisabled, pointPropertyValueDao, paoDao);
    }
    
}
