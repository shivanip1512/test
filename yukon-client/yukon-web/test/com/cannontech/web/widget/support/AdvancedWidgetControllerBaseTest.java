package com.cannontech.web.widget.support;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;

import com.cannontech.web.widget.DeviceDataMonitorsWidget;
import com.cannontech.web.widget.MeterEventsWidget;
import com.cannontech.web.widget.PorterResponseMonitorsWidget;
import com.cannontech.web.widget.RfnDeviceMetadataWidget;
import com.cannontech.web.widget.RfnOutagesWidget;
import com.cannontech.web.widget.StatusPointMonitorsWidget;
import com.cannontech.web.widget.SubscribedMonitorsWidget;

public class AdvancedWidgetControllerBaseTest {

    @Test
    public void testNoControllerAnnotationOnAdvancedWidget() {
        assertNoControllerAnnotation(SubscribedMonitorsWidget.class);
        assertNoControllerAnnotation(DeviceDataMonitorsWidget.class);
        assertNoControllerAnnotation(MeterEventsWidget.class);
        assertNoControllerAnnotation(PorterResponseMonitorsWidget.class);
        assertNoControllerAnnotation(RfnDeviceMetadataWidget.class);
        assertNoControllerAnnotation(RfnOutagesWidget.class);
        assertNoControllerAnnotation(StatusPointMonitorsWidget.class);
    }
    
    private void assertNoControllerAnnotation(Class<? extends AdvancedWidgetControllerBase> clazz) {
        Controller annotation = AnnotationUtils.findAnnotation(clazz, Controller.class);
        Assert.assertNotNull("AdvancedWidgetControllers should have @Controller. Found annotation on " + clazz.getName(),
                          annotation);
    }
    
}
