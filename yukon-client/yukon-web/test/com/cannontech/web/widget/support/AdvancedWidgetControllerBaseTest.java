package com.cannontech.web.widget.support;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;

import com.cannontech.web.widget.AllMonitorsWidget;
import com.cannontech.web.widget.MeterEventsWidget;
import com.cannontech.web.widget.RfnDeviceMetadataWidget;
import com.cannontech.web.widget.RfnOutagesWidget;
import com.cannontech.web.widget.SubscribedMonitorsWidget;

public class AdvancedWidgetControllerBaseTest {

    @Test
    public void testNoControllerAnnotationOnAdvancedWidget() {
        assertNoControllerAnnotation(SubscribedMonitorsWidget.class);
        assertNoControllerAnnotation(MeterEventsWidget.class);
        assertNoControllerAnnotation(RfnDeviceMetadataWidget.class);
        assertNoControllerAnnotation(RfnOutagesWidget.class);
        assertNoControllerAnnotation(AllMonitorsWidget.class);
        
    }
    
    private void assertNoControllerAnnotation(Class<? extends AdvancedWidgetControllerBase> clazz) {
        Controller annotation = AnnotationUtils.findAnnotation(clazz, Controller.class);
        assertNotNull(annotation, "AdvancedWidgetControllers should have @Controller. Found annotation on " + clazz.getName());
    }
    
}
