package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.web.widget.support.WidgetControllerBase;

@Controller
@RequestMapping("/overloadedGatewaysWidget/*")
public class OverloadedGatewaysWidget extends WidgetControllerBase {

    @Autowired private DataStreamingService dataStreamingService;
    
    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {
        
        ModelAndView mav = new ModelAndView("overloadedGatewaysWidget/render.jsp");

        List<RfnGateway> overloadedGateways = new ArrayList<>();
        try {
            overloadedGateways = dataStreamingService.getOverloadedGateways();
        } catch (DataStreamingConfigException e) {}
        
        mav.addObject("overloadedGateways", overloadedGateways);
        
        return mav;
    }
}

