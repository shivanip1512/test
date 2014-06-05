package com.cannontech.web.updater.dr;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.ecobee.model.EcobeeReadResult;
import com.cannontech.web.updater.ResultAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;

public enum EcobeeReadUpdateType {
    STATUS(
        new ResultAccessor<EcobeeReadResult>() {
            @Override
            public Object getValue(EcobeeReadResult result) {
                
                Map<String, Object> status = new HashMap<>();
                status.put("key", result.getKey());
                status.put("complete", result.isComplete());
                status.put("successful", result.isSuccessful());
                status.put("percentDone", result.getPercentDone());
                
                try {
                    return JsonUtils.toJson(status);
                } catch (JsonProcessingException e) {
                    return "";
                }
            }
        }
    ),
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
