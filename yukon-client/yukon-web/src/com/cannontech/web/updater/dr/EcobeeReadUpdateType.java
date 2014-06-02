package com.cannontech.web.updater.dr;

import org.joda.time.Instant;

import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.web.updater.ResultAccessor;

public enum EcobeeReadUpdateType {
    IS_COMPLETE(
        new ResultAccessor<EcobeeReadResult>() {
            @Override
            public Object getValue(EcobeeReadResult result) {
                return result.isComplete();
            }
        }
    ),
    COMPLETED_COUNT(
        new ResultAccessor<EcobeeReadResult>() {
            @Override
            public Object getValue(EcobeeReadResult result) {
                return result.getCompleted();
            }
        }
    ),
    TOTAL_COUNT(
        new ResultAccessor<EcobeeReadResult>() {
            @Override
            public Object getValue(EcobeeReadResult result) {
                return result.getTotal();
            }
        }
    ),
    START_DATE(
        new ResultAccessor<EcobeeReadResult>() {
            @Override
            public Object getValue(EcobeeReadResult result) {
                Instant startDate = result.getStartDate();
                //TODO format and return date
                return "";
            }
        }
    ),
    END_DATE(
        new ResultAccessor<EcobeeReadResult>() {
            @Override
            public Object getValue(EcobeeReadResult result) {
                Instant endDate = result.getEndDate();
                //TODO format and return date
                return "";
            }
        }
    )
    
    ;
    
    private ResultAccessor<EcobeeReadResult> resultAccessor;
    
    EcobeeReadUpdateType(ResultAccessor<EcobeeReadResult> resultAccessor) {
        this.resultAccessor = resultAccessor;
    }
    
    public Object getValue(EcobeeReadResult result) {
        return this.resultAccessor.getValue(result);
    }
}
