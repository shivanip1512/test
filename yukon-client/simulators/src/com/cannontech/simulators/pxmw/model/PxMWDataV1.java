package com.cannontech.simulators.pxmw.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;

import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataResponseV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesValueV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;
import com.cannontech.simulators.message.response.PxMWSimulatorResponse;

public class PxMWDataV1 extends PxMWDataGenerator {

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
        
        PxMWSiteDeviceV1 siteDevice = new PxMWSiteDeviceV1("e0824ba4-d832-49d6-ab60-6212a63bcd10", "Test Device");
        List<PxMWSiteDeviceV1> siteDeviceList = new ArrayList<PxMWSiteDeviceV1>();
        siteDeviceList.add(siteDevice);
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
  
    public PxMWSimulatorResponse timeseries(PxMWTimeSeriesDataRequestV1 pxMWTimeSeriesDataRequestV1) {

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
        
        PxMWTimeSeriesValueV1 timeSeriesValue1= new PxMWTimeSeriesValueV1(1613402541, "True");
        List<PxMWTimeSeriesValueV1> values1 = new ArrayList<PxMWTimeSeriesValueV1>();
        values1.add(0, timeSeriesValue1);
        PxMWTimeSeriesValueV1 timeSeriesValue2= new PxMWTimeSeriesValueV1(1613402541, "False");
        List<PxMWTimeSeriesValueV1> values2 = new ArrayList<PxMWTimeSeriesValueV1>();
        values2.add(0, timeSeriesValue2);
        PxMWTimeSeriesResultV1 timeSeriesResult1 = new PxMWTimeSeriesResultV1("110739", "v", values1);
        PxMWTimeSeriesResultV1 timeSeriesResult2 = new PxMWTimeSeriesResultV1("110595", "v", values2);
        List<PxMWTimeSeriesResultV1> results = new ArrayList<PxMWTimeSeriesResultV1>();
        results.add(0, timeSeriesResult1);
        results.add(1, timeSeriesResult2);
        PxMWTimeSeriesDeviceResultV1 deviceResults = new PxMWTimeSeriesDeviceResultV1("12343adc-567e-4321-9700-e4ca684e1234", results);
        List<PxMWTimeSeriesDeviceResultV1> resultList = new ArrayList<PxMWTimeSeriesDeviceResultV1>();
        resultList.add(0, deviceResults);
        PxMWTimeSeriesDataResponseV1 response = new PxMWTimeSeriesDataResponseV1(resultList);
        return new PxMWSimulatorResponse(response, status);
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
}
