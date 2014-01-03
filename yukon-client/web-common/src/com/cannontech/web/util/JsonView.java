package com.cannontech.web.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonView implements View {

    private ObjectMapper jsonObjectMapper = new ObjectMapper();
    
    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        response.addHeader("X-JSON", jsonObjectMapper.writeValueAsString(model));
        
        // Setting the content type to plain text makes Firefox okay with the
        // content being completely empty.
        response.setContentType("text/plain");
    }
}
