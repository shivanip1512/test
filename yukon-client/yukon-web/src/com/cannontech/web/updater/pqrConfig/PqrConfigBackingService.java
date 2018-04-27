package com.cannontech.web.updater.pqrConfig;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.rfn.model.PqrConfigResult;
import com.cannontech.dr.rfn.service.PqrConfigService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class PqrConfigBackingService implements UpdateBackingService {
    @Autowired PqrConfigService pqrConfigService;
    
    private enum RequestType {
        ITEMS_PROCESSED(result -> result.getCounts().getProcessed()),
        SUCCESS_COUNT(result -> result.getCounts().getSuccess()),
        UNSUPPORTED_COUNT(result -> result.getCounts().getUnsupported()),
        FAILED_COUNT(result -> result.getCounts().getFailed()),
        NEW_OPERATION_FOR_SUCCESS(result -> result.hasSuccessAndIsComplete() ? "" : "dn"),
        NEW_OPERATION_FOR_UNSUPPORTED(result -> result.hasSuccessAndIsComplete() ? "" : "dn"),
        NEW_OPERATION_FOR_FAILED(result -> result.hasFailedAndIsComplete() ? "" : "dn"),
        ;
        
        private Function<PqrConfigResult, Object> valueFunction;
        
        private RequestType(Function<PqrConfigResult, Object> valueFunction) {
            this.valueFunction = valueFunction;
        }
        
        private String getValue(PqrConfigResult result) {
            return valueFunction.apply(result).toString();
        }
    }
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        String[] idParts = StringUtils.split(identifier, "/");
        String resultId = idParts[0];
        RequestType type = RequestType.valueOf(idParts[1]);
        
        PqrConfigResult result = pqrConfigService.getResult(resultId)
                                                 .orElseThrow(() -> new IllegalArgumentException("Invalid result id: " 
                                                              + resultId));
        return type.getValue(result);
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}