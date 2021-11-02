package com.cannontech.simulators.eatonCloud.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.core.Logger;
import org.springframework.http.HttpStatus;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandResponseV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudDeviceDetailV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudErrorV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteDeviceV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteDevicesV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDataRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceResultV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesResultV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTokenV1;
import com.cannontech.simulators.message.response.EatonCloudSimulatorResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EatonCloudDataV1 extends EatonCloudDataGenerator {
    private EatonCloudFakeTimeseriesDataV1 timeseriesData;
 
    public EatonCloudDataV1(EatonCloudFakeTimeseriesDataV1 eatonCloudTimeSeriesResultV1) {
        timeseriesData = eatonCloudTimeSeriesResultV1;
    }
    
    private static final Logger log = YukonLogManager.getLogger(EatonCloudDataV1.class);

    //Simulator has 2 sites
    private List<String> siteGuids = List.of("eccdcf03-2ca8-40a9-a5f3-9446a52f515d", "616ff40f-63b2-4d3c-87e2-16b3c40614ed");
   
    //devices in the process of being created
    //if device was not create in 30 seconds, it will not create at all. If debugging creation code, extend the 30 sec value. 
    private static Cache<String, String> creatingGuids =
            CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    public EatonCloudSimulatorResponse token() {
        if (status == HttpStatus.BAD_REQUEST.value()) {
            EatonCloudErrorV1 error = new EatonCloudErrorV1(List.of("ClientId"), "The field 'ClientId' is not a valid uuid.", "f0d48574-d5f5-47c1-b817-a1042a103b29", status, "2021-02-25T07:07:03.2423402+00:00", null);
            return new EatonCloudSimulatorResponse(error, status);

        }
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new EatonCloudSimulatorResponse(
                    new EatonCloudErrorV1(status,
                            "Authorization has been denied for this request. Invalid clientId and clientSecret combination."),
                    status);
        }

        int length = 120;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        return new EatonCloudSimulatorResponse(new EatonCloudTokenV1(generatedString), status);
    }

    public EatonCloudSimulatorResponse devicesV1(String id, Boolean recursive, Boolean includeDetail) {
        if (status == HttpStatus.BAD_REQUEST.value()) {
            EatonCloudErrorV1 error = new EatonCloudErrorV1(List.of("Id"), "Invalid UUID " + id + ".",
                    "616ff40f-63b2-4d3c-87e2-16b3c40614ed", status, "2021-02-26T10:52:16.0799958+00:00", 10022);
            return new EatonCloudSimulatorResponse(error, status);

        }
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new EatonCloudSimulatorResponse(
                    new EatonCloudErrorV1(status,"Authorization has been denied for this request. User token is invalid or expired. Please renew the token."),
                    status);
        } 
 
        if (id.equals(siteGuids.get(0))) {
            List<EatonCloudSiteDeviceV1> siteDeviceList = getSiteDeviceList();
            //first site returns devices
            return new EatonCloudSimulatorResponse(new EatonCloudSiteDevicesV1(id,
                    "test 1",
                    "test site 1",
                    "abc",
                    "abc@eaton.com",
                    "",
                    "",
                    "",
                    "",
                    "",
                    siteDeviceList), status);
        } else {
            //second site has no devices
            return new EatonCloudSimulatorResponse(new EatonCloudSiteDevicesV1(id,
                    "test 2",
                    "test site 2",
                    "abc",
                    "abc@eaton.com",
                    "",
                    "",
                    "",
                    "",
                    "",
                    new ArrayList<>()), status);
        }
    }
    
    public EatonCloudSimulatorResponse sitesV1(String userId) {
        if (status == HttpStatus.BAD_REQUEST.value()) {
            EatonCloudErrorV1 error = new EatonCloudErrorV1(List.of("Id"), "Invalid UUID "+userId, "616ff40f-63b2-4d3c-87e2-16b3c40614ed", status, "2021-02-26T10:52:16.0799958+00:00", 10022);
            return new EatonCloudSimulatorResponse(error, status);

        }
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new EatonCloudSimulatorResponse(
                    new EatonCloudErrorV1(status,"Authorization has been denied for this request. User token is invalid or expired. Please renew the token."),
                    status);
        } 
        if (status == HttpStatus.NOT_FOUND.value()) {
            return new EatonCloudSimulatorResponse(new EatonCloudErrorV1(status, "Resource not found"), status);
        } 
        List<EatonCloudSiteV1> sites = new ArrayList<>();
        sites.add(new EatonCloudSiteV1(siteGuids.get(0),
                "test 1",
                "test site 1",
                "abc",
                "abc@eaton.com",
                "",
                "",
                "",
                "",
                ""));
        sites.add(new EatonCloudSiteV1(siteGuids.get(1),
                "test 2",
                "test site 2",
                "abc",
                "abc@eaton.com",
                "",
                "",
                "",
                "",
                ""));
        
        return new EatonCloudSimulatorResponse(sites.toArray(), status);
    }


    public EatonCloudSimulatorResponse timeseriesV1(EatonCloudTimeSeriesDataRequestV1 eatonCloudTimeSeriesDataRequestV1) {        
        if (status == HttpStatus.BAD_REQUEST.value()) {
            EatonCloudErrorV1 error = new EatonCloudErrorV1(List.of("ClientId"),  "The field \u0027ClientId\u0027 is not a valid uuid.",
                    "4aeacd2f-9424-4e6d-a218-c0b621d0f4c9", status, "2021-02-25T13:45:10.4807211+00:00", 10022);
            return new EatonCloudSimulatorResponse(error, status);
        } else if (status == HttpStatus.UNAUTHORIZED.value()) {
            EatonCloudErrorV1 error = new EatonCloudErrorV1(List.of(),
                    "User is not authorized to access devices: 76f93adc-4e9e-4aae-9701-e4ca684e7af5.",
                    "763a1051-e142-4cdb-893c-46afa4f9af31", status, "2021-02-25T13:45:10.4807211+00:00", null);
            return new EatonCloudSimulatorResponse(error, status);
        }
        
        if(successPercentage != 100) {
            int randomPercentage = (int) (Math.random() * 100);
            if (randomPercentage > successPercentage) { 
                EatonCloudErrorV1 error = new EatonCloudErrorV1(List.of("ClientId"),  "The field \u0027ClientId\u0027 is not a valid uuid.",
                        "4aeacd2f-9424-4e6d-a218-c0b621d0f4c9", status, "2021-02-25T13:45:10.4807211+00:00", 10022);
                return new EatonCloudSimulatorResponse(error,  HttpStatus.BAD_REQUEST.value());
            }
        }
        
        //load bad data to test the parser
        boolean randomBadData = false;

        List<EatonCloudTimeSeriesDeviceResultV1> resultList = eatonCloudTimeSeriesDataRequestV1.getDevices().stream().map(d -> {
            List<String> tags = Arrays.asList(d.getTagTrait().split(","));
            PaoType type = createRequest == null ? PaoType.LCR6600C: createRequest.getPaoType();
            List<EatonCloudTimeSeriesResultV1> result = timeseriesData.getValues(d.getDeviceGuid(), tags, type, randomBadData, creatingGuids.getIfPresent(d.getDeviceGuid()) != null);
            return new EatonCloudTimeSeriesDeviceResultV1(d.getDeviceGuid(), result);
        }).collect(Collectors.toList());

        AtomicInteger count = new AtomicInteger();
        Map<String, List<String>> statistics = resultList.stream()
                .collect(Collectors.toMap(r -> "#"+count.getAndIncrement() + " " + r.getDeviceId(),
                        r -> r.getResults().stream().map(p -> p.getTag() + " " + p.getTrait() + " " + p.getValues().size())
                                .collect(Collectors.toList())));
        AtomicInteger total = new AtomicInteger();
        resultList.forEach(r -> r.getResults().forEach(a -> total.addAndGet(a.getValues().size())));
        
        log.info("timeseries:{} total values:{}", statistics, total);
        
        return new EatonCloudSimulatorResponse(resultList.toArray(), status);
    }
    
    private List<EatonCloudSiteDeviceV1> getSiteDeviceList() {

        int devices = createRequest == null ? 0 : createRequest.getDevices();

        List<EatonCloudSiteDeviceV1> siteDeviceList = new ArrayList<>();

        IntStream.range(0, devices).forEach(i -> {
            String guid = UUID.randomUUID().toString();
            int value = nextValueHelper.getNextValue("EatonCloudSimulatorNameIncrementor");
            String name = createRequest.getPaoType() + "_SIM_" + value;
            String serial = createRequest.getPaoType() + "SIM" + value;
            EatonCloudSiteDeviceV1 siteDevice = new EatonCloudSiteDeviceV1(guid,
                    "72358726-1ed0-485b-8beb-6a27a27b58e8", name, serial, "...", "...", "...",
                    paoTypeToHardware.get(createRequest.getPaoType()).toString(), "...", "...", "...", "...",
                    "...");
            siteDeviceList.add(siteDevice);
        });
        return siteDeviceList;
    }
    
    public EatonCloudSimulatorResponse sendCommandV1(String id, String command_instance_id, EatonCloudCommandRequestV1 eatonCloudCommandRequestV1) {
        if (status == HttpStatus.BAD_REQUEST.value()) {
            EatonCloudErrorV1 error = new EatonCloudErrorV1(List.of("id"), "Invalid device command payload, id=123.",
                    "f5f61b63-68aa-42be-b8a1-a84a171ca38e", status, "2021-02-24T08:23:35.7124876+00:00", null);
            return new EatonCloudSimulatorResponse(error, status);
        }
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new EatonCloudSimulatorResponse(
                    new EatonCloudErrorV1(status,
                            "Authorization has been denied for this request. User token is invalid or expired. Please renew the token."),
                    status);
        } 
        if (status == HttpStatus.NOT_FOUND.value()) {
            return new EatonCloudSimulatorResponse(new EatonCloudCommandResponseV1(status, "Resource not found"), HttpStatus.OK.value());
        }
        
        if(successPercentage != 100) {
            int randomPercentage = (int) (Math.random() * 100);
            if (randomPercentage > successPercentage) { 
                EatonCloudErrorV1 error = new EatonCloudErrorV1(List.of("id"), "Invalid device command payload, id=123.",
                        "f5f61b63-68aa-42be-b8a1-a84a171ca38e", status, "2021-02-24T08:23:35.7124876+00:00", null);
                return new EatonCloudSimulatorResponse(error,  HttpStatus.BAD_REQUEST.value());
            }
        }
        
        return new EatonCloudSimulatorResponse(
                    new EatonCloudCommandResponseV1(status, "Success sending command for device guid:" + id + " command guid:" + command_instance_id),
                    status);
        
    }
    
    public EatonCloudSimulatorResponse detailsV1(String deviceId, Boolean recursive) {
        if (status == HttpStatus.BAD_REQUEST.value()) {
            EatonCloudErrorV1 error = new EatonCloudErrorV1(List.of("Id"), "Invalid UUID "+ deviceId, "616ff40f-63b2-4d3c-87e2-16b3c40614ed", status, "2021-02-26T10:52:16.0799958+00:00", 10022);
            return new EatonCloudSimulatorResponse(error, status);

        }
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new EatonCloudSimulatorResponse(
                    new EatonCloudErrorV1(status,"Authorization has been denied for this request. User token is invalid or expired. Please renew the token."),
                    status);
        } 
        if (status == HttpStatus.NOT_FOUND.value()) {
            return new EatonCloudSimulatorResponse(new EatonCloudErrorV1(status, "Resource not found"), status);
        }
        creatingGuids.put(deviceId,deviceId);
        EatonCloudDeviceDetailV1 detail = new EatonCloudDeviceDetailV1(deviceId, "", "", "", "", "2143535", "", "", "", "", "", "", "", "", "", "YUKON_SIMULATOR", true, "");
        return new EatonCloudSimulatorResponse(detail, status);
    }
}