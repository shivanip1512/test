package com.cannontech.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.util.ServletUtil;

public class PermissionCheckingHttpInvokerServiceExporter extends
HttpInvokerServiceExporter {

    UserChecker permissionChecker = new NullUserChecker();

    public void setPermissionChecker(UserChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        permissionChecker.verify(yukonUser);
        super.handleRequest(request, response);
    }
}
