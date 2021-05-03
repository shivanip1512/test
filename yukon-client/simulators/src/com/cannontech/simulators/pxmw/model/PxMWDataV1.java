package com.cannontech.simulators.pxmw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.core.Logger;
import org.springframework.http.HttpStatus;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceDetail;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;
import com.cannontech.simulators.message.response.PxMWSimulatorResponse;

public class PxMWDataV1 extends PxMWDataGenerator {
    private PxMWFakeTimeseriesDataV1 timeseriesData = new PxMWFakeTimeseriesDataV1();
 
    private static final Logger log = YukonLogManager.getLogger(PxMWDataV1.class);

    public PxMWSimulatorResponse token() {
        if (status == HttpStatus.BAD_REQUEST.value()) {
            PxMWErrorV1 error = new PxMWErrorV1(List.of("ClientId"), "The field 'ClientId' is not a valid uuid.", "f0d48574-d5f5-47c1-b817-a1042a103b29", status, "2021-02-25T07:07:03.2423402+00:00", null);
            return new PxMWSimulatorResponse(error, status);

        }
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new PxMWSimulatorResponse(
                    new PxMWErrorV1(status,
                            "Authorization has been denied for this request. Invalid clientId and clientSecret combination."),
                    status);
        }

        int length = 120;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        return new PxMWSimulatorResponse(new PxMWTokenV1(generatedString), status);
    }

    public PxMWSimulatorResponse devicesV1(String id, Boolean recursive, Boolean includeDetail) {
        if (status == HttpStatus.BAD_REQUEST.value()) {
            PxMWErrorV1 error = new PxMWErrorV1(List.of("Id"), "Invalid UUID-f28b0", "616ff40f-63b2-4d3c-87e2-16b3c40614ed", status, "2021-02-26T10:52:16.0799958+00:00", 10022);
            return new PxMWSimulatorResponse(error, status);

        }
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new PxMWSimulatorResponse(
                    new PxMWErrorV1(status,"Authorization has been denied for this request. User token is invalid or expired. Please renew the token."),
                    status);
        } 
        
        List<PxMWSiteDeviceV1> siteDeviceList = getSiteDeviceList();
        PxMWSiteV1 site = new PxMWSiteV1(id,
                "test",
                "test site 1",
                "abc",
                "abc@eaton.com",
                "7345345737",
                "2021-02-04T14:54:30Z",
                "88ea4353-b6ed-41df-a3b9-0a2894e9d0f3",
                "",
                "",
                siteDeviceList);
        return new PxMWSimulatorResponse(site, status);
    }


    public PxMWSimulatorResponse timeseriesV1(PxMWTimeSeriesDataRequestV1 pxMWTimeSeriesDataRequestV1) {

        if (status == HttpStatus.BAD_REQUEST.value()) {
            PxMWErrorV1 error = new PxMWErrorV1(List.of("DeviceID"), "Invalid UUID-76f93adc-4e9e-4aae-9700-e4ca684e7af51",
                    "b7074310-050c-4c11-ada0-a4ae542fa645", status, "2021-02-25T13:45:10.4807211+00:00", 10022);
            return new PxMWSimulatorResponse(error, status);
        } else if (status == HttpStatus.UNAUTHORIZED.value()) {
            PxMWErrorV1 error = new PxMWErrorV1(List.of(),
                    "User is not authorized to access devices: 76f93adc-4e9e-4aae-9701-e4ca684e7af5.",
                    "763a1051-e142-4cdb-893c-46afa4f9af31", status, "2021-02-25T13:45:10.4807211+00:00", null);
            return new PxMWSimulatorResponse(error, status);
        }
        
        //load bad data to test the parser
        boolean randomBadData = false;

        List<PxMWTimeSeriesDeviceResultV1> resultList = pxMWTimeSeriesDataRequestV1.getDevices().stream().map(d -> {
            List<String> tags = Arrays.asList(d.getTagTrait().split(","));
            PaoType type = createRequest == null ? PaoType.LCR6600C: createRequest.getPaoType();
            List<PxMWTimeSeriesResultV1> result = timeseriesData.getValues(tags, type, randomBadData);
            return new PxMWTimeSeriesDeviceResultV1(d.getDeviceGuid(), result);
        }).collect(Collectors.toList());

        AtomicInteger count = new AtomicInteger();
        Map<String, List<String>> statistics = resultList.stream()
                .collect(Collectors.toMap(r -> "#"+count.getAndIncrement() + " " + r.getDeviceId(),
                        r -> r.getResults().stream().map(p -> p.getTag() + " " + p.getTrait() + " " + p.getValues().size())
                                .collect(Collectors.toList())));
        AtomicInteger total = new AtomicInteger();
        resultList.forEach(r -> r.getResults().forEach(a -> total.addAndGet(a.getValues().size())));
        
        log.info("timeseries:{} total values:{}", statistics, total);
        

        return new PxMWSimulatorResponse(resultList.toArray(), status);
    }
    
    private List<PxMWSiteDeviceV1> getSiteDeviceList() {

        int devices = createRequest == null ? 0 : createRequest.getDevices();

        List<PxMWSiteDeviceV1> siteDeviceList = new ArrayList<PxMWSiteDeviceV1>();

        IntStream.range(0, devices).forEach(i -> {
            String guid = UUID.randomUUID().toString();
            int value = nextValueHelper.getNextValue("PxMWSimulatorNameIncrementor");
            String name = createRequest.getPaoType() + "_SIM_" + value;
            PxMWSiteDeviceV1 siteDevice = new PxMWSiteDeviceV1(guid,
                    "72358726-1ed0-485b-8beb-6a27a27b58e8", name, "...", "...", "...",
                    paoTypeToHardware.get(createRequest.getPaoType()).toString(), "...", "...", "...", "...",
                    "...");
            siteDeviceList.add(siteDevice);
        });
        return siteDeviceList;
    }
    
    public PxMWSimulatorResponse sendCommandV1(String id, String command_instance_id, PxMWCommandRequestV1 pxMWCommandRequestV1) {
        if (status == HttpStatus.BAD_REQUEST.value()) {
            PxMWErrorV1 error = new PxMWErrorV1(List.of("id"), "Invalid device command payload, id=123.",
                    "f5f61b63-68aa-42be-b8a1-a84a171ca38e", status, "2021-02-24T08:23:35.7124876+00:00", null);
            return new PxMWSimulatorResponse(error, status);
        }
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new PxMWSimulatorResponse(
                    new PxMWErrorV1(status,
                            "Authorization has been denied for this request. User token is invalid or expired. Please renew the token."),
                    status);
        } 
        if (status == HttpStatus.NOT_FOUND.value()) {
            return new PxMWSimulatorResponse(new PxMWErrorV1(status, "Resource not found"), status);
        }
        return new PxMWSimulatorResponse(
                    new PxMWCommandResponseV1(0, "Success sending command for device guid:" + id + " command guid:" + command_instance_id),
                    status);
        
    }
    
    public PxMWSimulatorResponse detailsV1(String deviceId, Boolean recursive) {
        if (status == HttpStatus.BAD_REQUEST.value()) {
            PxMWErrorV1 error = new PxMWErrorV1(List.of("Id"), "Invalid UUID-f28b0", "616ff40f-63b2-4d3c-87e2-16b3c40614ed", status, "2021-02-26T10:52:16.0799958+00:00", 10022);
            return new PxMWSimulatorResponse(error, status);

        }
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return new PxMWSimulatorResponse(
                    new PxMWErrorV1(status,"Authorization has been denied for this request. User token is invalid or expired. Please renew the token."),
                    status);
        } 
        if (status == HttpStatus.NOT_FOUND.value()) {
            return new PxMWSimulatorResponse(new PxMWErrorV1(status, "Resource not found"), status);
        }
        PxMWDeviceDetail detail = new  PxMWDeviceDetail(deviceId, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", true, "");
        return new PxMWSimulatorResponse(detail, status);
    }
}
