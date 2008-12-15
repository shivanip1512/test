package com.cannontech.web.widget.support;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.web.servlet.view.JstlView;

public class ErrorPreservingJstlView extends JstlView {
    @Override
    public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncludeSafeHttpServletResponse safeHttpServletResponse = new IncludeSafeHttpServletResponse(response);
        super.render(model, request, safeHttpServletResponse);
        safeHttpServletResponse.verify();
    }
    
    private static class IncludeSafeHttpServletResponse extends HttpServletResponseWrapper {
        private ServletException heldException = null;
        public IncludeSafeHttpServletResponse(HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public void sendError(int sc) throws IOException {
            heldException = new ServletException("Got error while rendering view: " + sc);
            super.sendError(sc);
        }
        
        @Override
        public void sendError(int sc, String msg) throws IOException {
            heldException = new ServletException("Got error while rendering view: " + msg + "(" + sc + ")");
            super.sendError(sc, msg);
        }
        
        public void verify() throws ServletException {
            if (heldException != null) {
                throw heldException;
            }
        }

    }

}
