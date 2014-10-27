package com.cannontech.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.springframework.util.Assert;

public class UnitTestUtil {

    private static AtomicLong accumulatedTimeAdjustments = new AtomicLong();

    /**
     * Causes calls to new Instant() to return a value this number of seconds adjusted
     */
    public static void adjustSystemTimeBySeconds(int numberOfSeconds) {
        DateTimeUtils.setCurrentMillisOffset(accumulatedTimeAdjustments.addAndGet((long) numberOfSeconds * 1000));
    }

    /**
     * There are cases in this test where we need handle "now" changing while the test is running.
     * This method will return true if the two durations are within a single second of each other.
     */
    public static boolean areClose(Duration duration1, Duration duration2, Duration allowedDeviation) {
        return duration1.minus(allowedDeviation).isShorterThan(duration2)
            && duration1.plus(allowedDeviation).isLongerThan(duration2);
    }

    public static boolean withinOneSecond(Duration duration1, Duration duration2) {
        return areClose(duration1, duration2, Duration.standardSeconds(1));
    }

    public static boolean withinOneMinute(Duration duration1, Duration duration2) {
        return areClose(duration1, duration2, Duration.standardMinutes(1));
    }

    /**
     * Asserts object is not null and all getter methods return non-null objects.
     */
    public static void assertPropertiesNotNull(Object object) {
        Assert.notNull(object, "Object itself is null.");

        BeanInfo beanInfo;
        try {
            Class<?> clazz = object.getClass();
            beanInfo = Introspector.getBeanInfo(clazz);
            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                Method readMethod = property.getReadMethod();
                if (readMethod != null) {
                    Assert.notNull(readMethod.invoke(object), "Method " + clazz.getSimpleName() + "." + readMethod.getName()
                        + "() returned null.");
                }
            }
        } catch (ReflectiveOperationException | IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
