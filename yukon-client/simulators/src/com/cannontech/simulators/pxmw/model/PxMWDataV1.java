package com.cannontech.simulators.pxmw.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.cannontech.dr.pxmw.model.v1.PxMWChannelV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceProfileV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorV1;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorsV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;

public class PxMWDataV1 extends PxMWDataGenerator {

    public Object deviceprofileV1(String id) {
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
        return profile;
    }

    public Object devicesV1(String id, Boolean recursive, Boolean includeDetail) {
        if (status == HttpStatus.NOT_FOUND.value()) {
            return new PxMWErrorsV1(List.of(
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
        return site;
    }
}
