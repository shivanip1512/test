package com.cannontech.web.common.pao.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.PaoPopupHelper;
import com.google.common.collect.Lists;

public class PaoPopupHelperImpl implements PaoPopupHelper {

    private final static int MAX_DEVICES_DISPLAYED = 1000;

    @Autowired  private PaoLoadingService paoLoadingService;
    @Autowired  private YukonUserContextMessageSourceResolver resolver;

    @Override
    public void buildPopupModel(DeviceCollection collection, ModelMap model,
            YukonUserContext context) {
        int totalDeviceCount = (int) collection.getDeviceCount();
        List<SimpleDevice> devicesToLoad = collection.getDevices(0,
                MAX_DEVICES_DISPLAYED);
        List<DeviceCollectionReportDevice> deviceCollectionReportDevices = paoLoadingService
                .getDeviceCollectionReportDevices(devicesToLoad);
        
        boolean allRfn = true;
        for (DeviceCollectionReportDevice device : deviceCollectionReportDevices) {
            if (!device.getPaoIdentifier().getPaoType().isRfn()) {
                allRfn = false;
                break;
            }
        }

        MessageSourceAccessor accessor = resolver
                .getMessageSourceAccessor(context);

        Collections.sort(deviceCollectionReportDevices);

        List<List<String>> rows = Lists.newArrayList();

        List<String> header = Lists.newArrayList();
        header.add(accessor.getMessage("yukon.common.deviceName"));
        header.add(accessor.getMessage("yukon.common.address"));
        if (allRfn) {
            header.add(accessor.getMessage("yukon.common.deviceType"));
        } else {
            header.add(accessor.getMessage("yukon.common.route"));
        }
        rows.add(header);

        for (DeviceCollectionReportDevice device : deviceCollectionReportDevices) {
            List<String> row = Lists.newArrayList();
            row.add(device.getName());
            row.add(device.getAddress());
            if (allRfn) {
                row.add(device.getType());
            } else {
                row.add(device.getRoute());
            }
            rows.add(row);
        }

        if (totalDeviceCount > MAX_DEVICES_DISPLAYED) {
            model.addAttribute("resultsLimitedTo", MAX_DEVICES_DISPLAYED);
        }
        model.addAttribute("deviceInfoList", rows);
    }

}
