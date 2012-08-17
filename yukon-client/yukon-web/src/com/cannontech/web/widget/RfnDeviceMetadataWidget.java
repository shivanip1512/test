package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

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

    @RequestMapping
    public String render(final ModelMap model, int deviceId, final YukonUserContext context) {
        
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        model.addAttribute("device", device);
        
        WaitableDataCallback<Map<RfnMetadata, Object>> waitableCallback = new WaitableDataCallback<Map<RfnMetadata, Object>>() {
            
            @Override
            public void processingExceptionOccured(MessageSourceResolvable message) {
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
        
        List<RfnMetadata> metadataTypes = Lists.newArrayList(metadata.keySet());
        Collections.sort(metadataTypes, new Comparator<RfnMetadata>() {
            @Override
            public int compare(RfnMetadata o1, RfnMetadata o2) {
                String name1 = accessor.getMessage(o1);
                String name2 = accessor.getMessage(o2);
                return name1.compareToIgnoreCase(name2);
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