package com.cannontech.web.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.springframework.web.servlet.View;

public class JsonView implements View {

    public String getContentType() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public void render(Map model, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        JSONObject object = new JSONObject(model);
        response.addHeader("X-JSON", object.toString());
        
        // Setting the content type to plain text makes Firefox okay with the
        // content being completely empty.
        response.setContentType("text/plain");
    }
}
