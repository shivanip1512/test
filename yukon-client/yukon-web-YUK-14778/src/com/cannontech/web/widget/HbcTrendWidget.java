package com.cannontech.web.widget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.impl.CachingWidgetParameterGrabber;


@Controller
@RequestMapping("/hbcTrendWidget/*")
public class HbcTrendWidget extends CsrTrendWidget {
    
    @Autowired @Qualifier("hbcTrendWidgetCachingWidgetParameterGrabber")
    private CachingWidgetParameterGrabber cachingWidgetParameterGrabber;

    public HbcTrendWidget() {
    }
    
    @Autowired
    public HbcTrendWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput) {
        super(simpleWidgetInput);
    }
}