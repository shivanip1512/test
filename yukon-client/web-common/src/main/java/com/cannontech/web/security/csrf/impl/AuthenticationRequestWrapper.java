/**
 * 
 */
package com.cannontech.web.security.csrf.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.google.common.io.CharStreams;

/**
 * AuthenticationRequestWrapper is used to Wrap our request object to another class before forwarding it to
 * the chain filterTo consume. That's how we can avoid the used stream problem
 * By this way, We won't face 'java.lang.IllegalStateException: getInputStream() has already been called for
 * this request' if getInputStream() or getReader() is called on the same request.
 *
 */
public class AuthenticationRequestWrapper extends HttpServletRequestWrapper {
    private final String payload;
    private final Logger log = YukonLogManager.getLogger(AuthenticationRequestWrapper.class);

    public AuthenticationRequestWrapper(HttpServletRequest request) {
        super(request);
        // read the original payload into the payload variable
        String body = null;
        
        try {
            //TODO: Revert this later. Added for YUK-18491.
            body = CharStreams.toString(request.getReader());
        } catch (IOException e) {
            log.error("Error occurred in fetching the payload content for the request", e);
        }
        payload = body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(payload.getBytes("ISO-8859-1"));
        ServletInputStream inputStream = new ServletInputStream() {
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
            
                return false;
            }

            @Override
            public boolean isReady() {
             
                return false;
            }

            @Override
            public void setReadListener(ReadListener arg0) {
               

            }
        };
        return inputStream;
    }
    
    //TODO: Revert this later. Added for YUK-18491.//TODO: Revert this later. Added for YUK-18491.
    public String getPayload() {
        return payload;
    }
}
