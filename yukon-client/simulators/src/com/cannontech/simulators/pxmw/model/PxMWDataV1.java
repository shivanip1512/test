package com.cannontech.simulators.pxmw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;

import com.cannontech.dr.pxmw.model.v1.PxMWChannelDataV1;
import com.cannontech.dr.pxmw.model.v1.PxMWChannelV1;
import com.cannontech.dr.pxmw.model.v1.PxMWChannelValueV1;
import com.cannontech.dr.pxmw.model.v1.PxMWChannelValuesV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceProfileV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceTimeseriesLatestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorV1;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorsV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;
import com.cannontech.simulators.message.response.PxMWSimulatorResponse;

public class PxMWDataV1 extends PxMWDataGenerator {

    public PxMWSimulatorResponse token() {
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            PxMWErrorsV1 errors = new PxMWErrorsV1(List.of(new PxMWErrorV1(String.valueOf(status), "Unauthorized")));
            return new PxMWSimulatorResponse(errors, status);

        }
        if (status == HttpStatus.FORBIDDEN.value()) {
            PxMWErrorsV1 errors = new PxMWErrorsV1(List.of(new PxMWErrorV1(String.valueOf(status),
                    "User account has been locked out. Please try again after some time.")));
            return new PxMWSimulatorResponse(errors, status);
        }

        int length = 120;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        return new PxMWSimulatorResponse(new PxMWTokenV1(generatedString), status);
    }

    public PxMWSimulatorResponse deviceProfileV1(String id) {
        PxMWChannelV1 channel = new PxMWChannelV1("12343",
                "sRelayStatus");
        List<PxMWChannelV1> channelList = new ArrayList<PxMWChannelV1>();
        channelList.add(channel);

        PxMWDeviceV1 device = new PxMWDeviceV1("Eaton",
                "LCR",
                "device",
                "LCR6200C",
                "LCR-6200C Long",
                "LCR-6200C Short",
                "LCR-6200C Long",
                "Firmware V9.4",
                "master");
        PxMWDeviceProfileV1 profile = new PxMWDeviceProfileV1(id,
                "2020-11-11T10:10:10.000Z",
                "ffffffff-ffff-ffff-ffff-ffffffffffff",
                "2020-11-11T15:15:15.000Z",
                "11111111-4e7e-48b7-a2ff-6f1f7916c92d",
                device,
                channelList);
        return new PxMWSimulatorResponse(profile, status);
    }

    public PxMWSimulatorResponse devicesV1(String id, Boolean recursive, Boolean includeDetail) {
        if (status == HttpStatus.NOT_FOUND.value()) {
            PxMWErrorsV1 errors =  new PxMWErrorsV1(List.of(
                    new PxMWErrorV1("Site not found, please provide the correct Site Id", "Site is not registered on the system")));
        }
        
        PxMWSiteDeviceV1 siteDevice = new PxMWSiteDeviceV1("e0824ba4-d832-49d6-ab60-6212a63bcd10",
                "72358726-1ed0-485b-8beb-6a27a27b58e8", "...", "...", "...", "...", "...", "...", "...", "...", "...", "...",
                "...");
        List<PxMWSiteDeviceV1> siteDeviceList = new ArrayList<PxMWSiteDeviceV1>();
        siteDeviceList.add(siteDevice);
        PxMWSiteV1 site = new PxMWSiteV1(id,
                "87654321-4321-4321-4321-210987654321",
                "Yukon Software Simulator",
                "Px Simulator site",
                "Simulator",
                "simulator@eaton.com",
                "1-111-867-5309",
                siteDeviceList,
                "2020-11-11T10:10:10.000Z",
                "ffffffff-4e7e-48b7-a2ff-6f1f7916c92d",
                "2020-11-11T15:15:15.000Z",
                "11111111-4e7e-48b7-a2ff-6f1f7916c92d");
        return new PxMWSimulatorResponse(site, status);
    }
    
    public PxMWSimulatorResponse getTimeseriesLatestV1(String id, String tags) {

        if (status == HttpStatus.NOT_FOUND.value()) {
            PxMWErrorsV1 errors = new PxMWErrorsV1(List.of(new PxMWErrorV1(
                    "Device is not registered with System, please check UUID or register your device",
                    "Latest known values for a list of channels cannot be retrieved for a device which is not registered with system or device registered under a deleted site.")));
            return new PxMWSimulatorResponse(errors, status);
        }

        List<String> tagList = Stream.of(tags.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        List<PxMWChannelDataV1> dataList = tagList.stream().map(value -> {
            PxMWChannelDataV1 data = new PxMWChannelDataV1(value, value, value, value);
            return data;
        }).collect(Collectors.toList());

        return new PxMWSimulatorResponse(new PxMWDeviceTimeseriesLatestV1("id", dataList), status);
    }
    
    public PxMWSimulatorResponse cloudEnableV1(String id, Boolean state) {
        if (status == HttpStatus.NOT_FOUND.value()) {
            PxMWErrorsV1 errors = new PxMWErrorsV1(List.of(new PxMWErrorV1(
                    "Device is not registered with System, please check UUID or register your device",
                    "Device which is not registered with system or device registered under a deleted site, cannot be enabled or disabled on the IoTHub")));
            return new PxMWSimulatorResponse(errors, status);
        }
        
        return state 
                ? new PxMWSimulatorResponse("Device " + id + " is enabled successfully.", status) 
                : new PxMWSimulatorResponse("Device " + id + " is disabled successfully.", status);
    }
    
    public PxMWSimulatorResponse getChannelValuesV1(String id, List<String> tags) {
        // 500
        if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new PxMWSimulatorResponse(new PxMWChannelValuesV1(String.valueOf(status), "Terminated", new ArrayList<>()),
                    200);
        }
        // 400
        if (status == HttpStatus.BAD_REQUEST.value()) {
            // change to throw error
            return new PxMWSimulatorResponse(new PxMWChannelValuesV1(String.valueOf(status), "Resend", new ArrayList<>()), 200);
        }

        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new PxMWSimulatorResponse(new PxMWErrorsV1(List.of(new PxMWErrorV1(
                    "Authorization has been denied for this request",
                    "Verify the user has permission to execute the GetChannelValues operation for the site where the device is registered"))),
                    status);
        }
        if (status == HttpStatus.NOT_FOUND.value()) {
            return new PxMWSimulatorResponse(new PxMWErrorsV1(List.of(new PxMWErrorV1(
                    "Device is not registered with System, please check UUID or register your device",
                    "Verify device id is valid and device is active on the system"),
                    new PxMWErrorV1(
                            "Device Profile not defined",
                            "Push a device tree message to set the device profile and use the Web API to upload a profile for the device"))),
                    status);
        }
        if (status == HttpStatus.NOT_IMPLEMENTED.value()) {
            return new PxMWSimulatorResponse(new PxMWErrorsV1(List.of(new PxMWErrorV1(
                    "Device command is not supported",
                    "Device ingress must be set to iothub and device profile must have the direct_method capability set to true"))),
                    status);
        }
        if (status == HttpStatus.SERVICE_UNAVAILABLE.value()) {
            return new PxMWSimulatorResponse(new PxMWErrorsV1(List.of(new PxMWErrorV1(
                    "Unable to send device command",
                    "Verify the device is communicating with the IoT Hub"),
                    new PxMWErrorV1(
                            "Error while reading device data from Mongo store",
                            "Retry the command again at a later time"))),
                    status);
        }

        List<PxMWChannelValueV1> dataList = tags.stream().map(value -> {
            PxMWChannelValueV1 data = new PxMWChannelValueV1(value, "1.0");
            return data;
        }).collect(Collectors.toList());

        return new PxMWSimulatorResponse(new PxMWChannelValuesV1("200", null, dataList), status);
    }

    public PxMWSimulatorResponse getTrendDataV1(PxMWTimeSeriesDataRequestV1 pxMWTimeSeriesDataRequestV1) {
        List<PxMWTimeSeriesDeviceResultV1> resultList = null;
        // 200
        if (status == HttpStatus.OK.value()) {
            return new PxMWSimulatorResponse(
                    new PxMWTimeSeriesDataResponseV1(resultList),
                    status);
        }
        // 400
        else if (status == HttpStatus.BAD_REQUEST.value()) {
            // change to throw error
            return new PxMWSimulatorResponse(new PxMWTimeSeriesDataResponseV1(resultList), 200);
        }
        // 401
        else if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new PxMWSimulatorResponse(new PxMWErrorsV1(List.of(new PxMWErrorV1(
                    "Authorization has been denied for this request",
                    "Verify the user has permission to execute the GetTimeSeriesData operation for the site where the device is registered"))),
                    status);
        }
        return null;
    }

    public PxMWSimulatorResponse sendCommandV1(String id, String command_instance_id, PxMWCommandRequestV1 pxMWCommandRequestV1) {
        if (status == HttpStatus.OK.value()) {
            return new PxMWSimulatorResponse(
                    new PxMWCommandResponseV1(0, "Success sending command for device guid:" + id + " command guid:" + command_instance_id),
                    status);
        } else {
            return new PxMWSimulatorResponse(null, status);
        }
    }
}
