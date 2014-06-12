package com.cannontech.util;

import static org.junit.Assert.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionById;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointValue;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PaoSelectionUtil;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.temperature.CelsiusTemperature;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.token.Token;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.MonthYear;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.StringUtils;
import com.cannontech.dr.ThermostatRampRateValues;
import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.DeviceRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.InventoryRelayAppliances;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCount;
import com.cannontech.dr.ecobee.message.AuthenticationRequest;
import com.cannontech.dr.ecobee.message.CreateSetRequest;
import com.cannontech.dr.ecobee.message.DeviceDataResponse;
import com.cannontech.dr.ecobee.message.DrRestoreRequest;
import com.cannontech.dr.ecobee.message.MoveDeviceRequest;
import com.cannontech.dr.ecobee.message.MoveSetRequest;
import com.cannontech.dr.ecobee.message.RegisterDeviceRequest;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.message.partial.DemandResponseRef;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.model.EcobeeQueryStatistics;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMislocatedDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMislocatedSetDiscrepancy;
import com.cannontech.dr.model.PerformanceVerificationAverageReports;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationEventStats;
import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.loadcontrol.weather.GeographicCoordinate;
import com.cannontech.loadcontrol.weather.WeatherLocation;
import com.cannontech.loadcontrol.weather.WeatherStation;
import com.cannontech.stars.dr.optout.util.OptOutUtil;
import com.cannontech.stars.dr.program.service.HardwareEnrollmentInfo;
import com.cannontech.stars.util.SettlementConfigFuncs;
import com.google.common.base.Joiner;

/**
 * Tests objects that we claim are immutable to assert they remain immutable.
 * 
 * This only tests a few qualities of classes to determine if they are immutable. Although, we cannot
 * comprehensively determine if the objects are immutable (maybe its possible, but not trivial) which means if
 * an object passes this test it's possible the object is still mutable.
 */
public class SimpleImmutabilityTest {

    @Test
    public void test_miscObject() {
        assertMostLikelyImmutable(CalculationData.class);
        assertMostLikelyImmutable(SimpleDevice.class);
        assertMostLikelyImmutable(PagingParameters.class);
        assertMostLikelyImmutable(RfnIdentifier.class);
        assertMostLikelyImmutable(RfnDevice.class);
        assertMostLikelyImmutable(CelsiusTemperature.class);
        assertMostLikelyImmutable(FahrenheitTemperature.class);
        assertMostLikelyImmutable(Token.class);
        assertMostLikelyImmutable(UserPage.class);
        assertMostLikelyImmutable(UserSubscription.class);
        assertMostLikelyImmutable(DatedObject.class);
        assertMostLikelyImmutable(MonthYear.class);
        assertMostLikelyImmutable(Range.class);
        assertMostLikelyImmutable(ThermostatRampRateValues.class);
        assertMostLikelyImmutable(MessageCodeGenerator.class);
        assertMostLikelyImmutable(GeographicCoordinate.class);
        assertMostLikelyImmutable(WeatherLocation.class);
        assertMostLikelyImmutable(WeatherStation.class);
        assertMostLikelyImmutable(HardwareEnrollmentInfo.class);
        assertMostLikelyImmutable(SettlementConfigFuncs.class);
    }

    @Test
    public void test_utilClasses() {
        assertMostLikelyImmutable(TagUtils.class);
        assertMostLikelyImmutable(CommonUtils.class);
        assertMostLikelyImmutable(PaoSelectionUtil.class);
        assertMostLikelyImmutable(StringUtils.class);
        assertMostLikelyImmutable(OptOutUtil.class);
    }

    @Test
    public void test_DeviceCollectionClasses() {
        assertMostLikelyImmutable(DeviceConfiguration.class);
        assertMostLikelyImmutable(DeviceCollectionByField.class);
        assertMostLikelyImmutable(DeviceCollectionById.class);
        assertMostLikelyImmutable(DeviceConfigCategory.class);
        assertMostLikelyImmutable(DeviceConfigCategoryItem.class);
    }

    @Test
    public void test_PaoObjects() {
        assertMostLikelyImmutable(PaoMultiPointIdentifier.class);
        assertMostLikelyImmutable(PaoPointValue.class);
        assertMostLikelyImmutable(PaoTypePointIdentifier.class);
        assertMostLikelyImmutable(PointIdentifier.class);
        assertMostLikelyImmutable(PaoSelectionUtil.class);
        assertMostLikelyImmutable(PaoIdentifier.class);
    }

    @Test
    public void test_AssetAvailabilityObjects() {
        assertMostLikelyImmutable(AllRelayCommunicationTimes.class);
        assertMostLikelyImmutable(DeviceRelayCommunicationTimes.class);
        assertMostLikelyImmutable(InventoryRelayAppliances.class);
    }

    @Test
    public void test_ecobeeObjects() {
        assertMostLikelyImmutable(EcobeeQueryCount.class);
        assertMostLikelyImmutable(DemandResponseRef.class);
        assertMostLikelyImmutable(Status.class);
        assertMostLikelyImmutable(AuthenticationRequest.class);
        assertMostLikelyImmutable(CreateSetRequest.class);
        assertMostLikelyImmutable(DeviceDataResponse.class);
        assertMostLikelyImmutable(DrRestoreRequest.class);
        assertMostLikelyImmutable(MoveDeviceRequest.class);
        assertMostLikelyImmutable(MoveSetRequest.class);
        assertMostLikelyImmutable(RegisterDeviceRequest.class);
        assertMostLikelyImmutable(StandardResponse.class);
        assertMostLikelyImmutable(EcobeeMislocatedDeviceDiscrepancy.class);
        assertMostLikelyImmutable(EcobeeMislocatedSetDiscrepancy.class);
        assertMostLikelyImmutable(EcobeeDeviceReading.class);
        assertMostLikelyImmutable(EcobeeDeviceReadings.class);
        assertMostLikelyImmutable(EcobeeDutyCycleDrParameters.class);
        assertMostLikelyImmutable(EcobeeQueryStatistics.class);
    }

    @Test
    public void test_PerformanceVerificationObjects() {
        // performance verification
        assertMostLikelyImmutable(PerformanceVerificationAverageReports.class);
        assertMostLikelyImmutable(PerformanceVerificationEventMessage.class);
        assertMostLikelyImmutable(PerformanceVerificationEventMessageStats.class);
        assertMostLikelyImmutable(PerformanceVerificationEventStats.class);
    }

    private void assertMostLikelyImmutable(Class<?> aClass) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(aClass);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        List<String> failureMessages = new ArrayList<>();
        if (!Modifier.isFinal(aClass.getModifiers())) {
            failureMessages.add(aClass.getSimpleName() + " is not final.");
        }
        for (PropertyDescriptor property : propertyDescriptors) {
            if (property.getWriteMethod() != null) {
                failureMessages.add("Setter method: " + property.getWriteMethod().getName());
            }
        }
        for (Field field : aClass.getDeclaredFields()) {
            if (!Modifier.isFinal(field.getModifiers())) {
                failureMessages.add("Non final field: " + field.getType().getSimpleName() + " " + field.getName());
            }
        }
        if (!failureMessages.isEmpty()) {
            String reasons = Joiner.on(", ").join(failureMessages);
            fail(aClass.getSimpleName() + " is not immutable for the following reasons: " + reasons);
        }
    }
}
