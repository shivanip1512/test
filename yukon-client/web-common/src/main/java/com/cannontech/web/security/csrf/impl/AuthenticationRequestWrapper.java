/**
 * 
 */
package com.cannontech.web.security.csrf.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

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

    /**
     * @param request
     */
    public AuthenticationRequestWrapper(HttpServletRequest request) {
        super(request);
        // read the original payload into the payload variable
        String body = null;
        try {
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            body = scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            log.error("Error occured in fetching the payload content for the request", e);
        }
        payload = body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(payload.getBytes());
        ServletInputStream inputStream = new ServletInputStream() {
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return inputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
