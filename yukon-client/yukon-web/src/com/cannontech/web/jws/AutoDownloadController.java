package com.cannontech.web.jws;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class AutoDownloadController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String path = ServletRequestUtils.getRequiredStringParameter(request, "app");
        path = "/jws/" + path;
        StringBuffer fullUrl = request.getRequestURL();
        URL thisFull = new URL(fullUrl.toString());
        URL appFull = new URL(thisFull.getProtocol(), thisFull.getHost(), thisFull.getPort(), path);
        ModelAndView mav = new ModelAndView("auto");
        mav.addObject("app", appFull.toString());
        return mav;
    }

}
