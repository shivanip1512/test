package com.cannontech.web.security.csrf;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import com.cannontech.web.security.csrf.impl.CsrfTokenServiceImpl;


public class CsrfRequestDataValueProcessor implements RequestDataValueProcessor  
{

    @Override
    public Map<String,String> getExtraHiddenFields(HttpServletRequest request) {
      Map<String,String> hiddenFields = new HashMap<String,String>();
      hiddenFields.put(CsrfTokenService.REQUEST_CSRF_TOKEN, 
                       new CsrfTokenServiceImpl().getTokenForSession(request.getSession()));
      
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
