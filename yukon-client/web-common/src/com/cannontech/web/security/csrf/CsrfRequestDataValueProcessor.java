package com.cannontech.web.security.csrf;

import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.beans.factory.annotation.Autowired;


public class CsrfRequestDataValueProcessor implements RequestDataValueProcessor  
{
    @Autowired private CsrfTokenService csrfTokenService;

    @Override
    public Map<String,String> getExtraHiddenFields(HttpServletRequest request) {
      Map<String,String> hiddenFields = Collections.singletonMap(CsrfTokenService.REQUEST_CSRF_TOKEN,
                                         csrfTokenService.getTokenForSession(request.getSession()));

      return hiddenFields;
     }
    
   
    @Override
    public String processAction(HttpServletRequest request, String action) {
        return action;
    }

    @Override
    public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
        return value;
    }

    @Override
    public String processUrl(HttpServletRequest request, String url) {
        return url;
    }

}
