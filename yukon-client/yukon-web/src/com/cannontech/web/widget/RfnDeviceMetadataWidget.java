package com.cannontech.web.widget;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.metadata.RfnMetadata;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceMetadataService;
import com.cannontech.common.rfn.service.WaitableDataCallback;
import com.cannontech.common.util.Pair;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/rfnDeviceMetadataWidget/*")
public class RfnDeviceMetadataWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnDeviceMetadataService metadataService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    private String keyPrefix = "yukon.web.widgets.RfnDeviceMetadataWidget.";
    private static final ImmutableSet<RfnMetadata> csrSubset = ImmutableSet.of(
                                                                               RfnMetadata.COMM_STATUS,
                                                                               RfnMetadata.COMM_STATUS_TIMESTAMP,
                                                                               RfnMetadata.GROUPS,
                                                                               RfnMetadata.NEIGHBOR_COUNT,
                                                                               RfnMetadata.NODE_SERIAL_NUMBER,
                                                                               RfnMetadata.PRIMARY_GATEWAY,
                                                                               RfnMetadata.PRIMARY_GATEWAY_HOP_COUNT);
    
    @Autowired
    public RfnDeviceMetadataWidget(@Qualifier("widgetInput.deviceId")
                SimpleWidgetInput simpleWidgetInput) {
        
        addInput(simpleWidgetInput);
        setLazyLoad(true);
    }

    @RequestMapping("render")
    public String render(final ModelMap model, int deviceId, final YukonUserContext context) {
        
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        model.addAttribute("device", device);
        
        WaitableDataCallback<Map<RfnMetadata, Object>> waitableCallback = new WaitableDataCallback<Map<RfnMetadata, Object>>() {
            
            @Override
            public void processingExceptionOccurred(MessageSourceResolvable message) {
                model.addAttribute("error", message);
            }
            
            @Override
            public void receivedData(Map<RfnMetadata, Object> metadata) {
                addMetadata(metadata, model, context);
            }
            
            @Override
            public void receivedDataError(MessageSourceResolvable message) {
                model.addAttribute("error", message);
            }
        };
        
        metadataService.send(device, waitableCallback, keyPrefix);
        
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */ }
        
        if (model.get("error") == null) {
            model.addAttribute("showAll", true);
        }
        
        return "rfnDeviceMetadataWidget/render.jsp";
    }
    
    private void addMetadata(Map<RfnMetadata, Object> metadata, ModelMap model, final YukonUserContext context) {
        final MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        List<Pair<RfnMetadata, Object>> metadataPairs = Lists.newArrayList();
        List<Pair<RfnMetadata, Object>> csrMetadataPairs = Lists.newArrayList();
        
        Object objWifiMetaData = metadata.get(RfnMetadata.WIFI_SUPER_METER_DATA);
        if(objWifiMetaData != null) {
            metadata.remove(RfnMetadata.WIFI_SUPER_METER_DATA);
            model.addAttribute("wifiSuperMeterData", objWifiMetaData);
        }
        
        List<RfnMetadata> metadataTypes = Lists.newArrayList(metadata.keySet());
        final Collator collator = Collator.getInstance(context.getLocale());
        Collections.sort(metadataTypes, new Comparator<RfnMetadata>() {
            @Override
            public int compare(RfnMetadata o1, RfnMetadata o2) {
                String name1 = accessor.getMessage(o1.getFormatKey());
                String name2 = accessor.getMessage(o2.getFormatKey());
                return collator.compare(name1, name2);
            }
        });
        
        for (RfnMetadata data : metadataTypes) {
            Pair<RfnMetadata, Object> pair = new Pair<RfnMetadata, Object>(data, metadata.get(data));
            metadataPairs.add(pair);
            if (csrSubset.contains(data)) {
                csrMetadataPairs.add(pair);
            }
        }
        
        model.addAttribute("metadata", metadataPairs);
        model.addAttribute("csrMetadata", csrMetadataPairs);
    }
    
}