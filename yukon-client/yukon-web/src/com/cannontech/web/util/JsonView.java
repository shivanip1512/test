package com.cannontech.web.util;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.web.servlet.View;

public class JsonView implements View {

    public String getContentType() {
        return null;
    }

    public void render(Map model, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        ServletOutputStream out = response.getOutputStream();
        JSONObject object = new JSONObject(model);
        response.addHeader("X-JSON", object.toString());
        out.flush();
    }

}
