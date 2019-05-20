package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

public class SetupControllerHelper {
    
    public void populateBindingError(BindingResult result, BindException error, ResponseEntity<Object> errorResponse) {
        LinkedHashMap<?, ?> errorObject = (LinkedHashMap<?, ?>) errorResponse.getBody();
        ArrayList<?> fieldError = (ArrayList<?>) errorObject.get("fieldErrors");
        ArrayList<?> globalError = (ArrayList<?>) errorObject.get("globalErrors");

        for (int i = 0; i < fieldError.size(); i++) {
            LinkedHashMap<?, ?> map1 = (LinkedHashMap<?, ?>) fieldError.get(i);
            error.rejectValue(map1.get("field").toString(), StringUtils.EMPTY, map1.get("code").toString());
        }
        result.addAllErrors(error);
    }

}
