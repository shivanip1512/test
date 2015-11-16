package com.cannontech.web.spring;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.cannontech.web.security.annotation.IgnoreCsrfCheck;

@IgnoreCsrfCheck
public class IgnoreCsrfCheckHttpInvokerServiceExporter extends HttpInvokerServiceExporter {/*empty*/}
