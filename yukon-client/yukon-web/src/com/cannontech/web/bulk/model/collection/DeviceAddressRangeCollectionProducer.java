package com.cannontech.web.bulk.model.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.bulk.model.DeviceCollectionProducer;

/**
 * Implementation of DeviceCollectionProducer for an address range
 */
public class DeviceAddressRangeCollectionProducer implements
        DeviceCollectionProducer {

    private String supportedType = "addressRange";
    private PaoDao paoDao = null;

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public String getSupportedType() {
        return supportedType;
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        final int startAddress = ServletRequestUtils.getIntParameter(request,
                                                                     supportedType + ".start");
        final int endAddress = ServletRequestUtils.getIntParameter(request,
                                                                   supportedType + ".end");

        return new DeviceCollectionBase() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", supportedType);
                paramMap.put(supportedType + ".start",
                             String.valueOf(startAddress));
                paramMap.put(supportedType + ".end", String.valueOf(endAddress));

                return paramMap;
            }

            protected List<YukonDevice> getDevices() {
                List<YukonDevice> deviceList = new ArrayList<YukonDevice>();

                List<LiteYukonPAObject> litePaoList = paoDao.getLiteYukonPaobjectsByAddressRange(startAddress,
                                                                                                 endAddress);
                for (LiteYukonPAObject pao : litePaoList) {
                    YukonDevice device = new YukonDevice(pao.getLiteID(),
                                                         pao.getType());
                    deviceList.add(device);
                }
                return deviceList;
            }

            public String getDescriptionKey() {
                return collectionKeyBase + "addressRange";
            }

            public List<String> getParameterList() {

                List<String> paramList = new ArrayList<String>();
                paramList.add(String.valueOf(startAddress));
                paramList.add(String.valueOf(endAddress));

                return paramList;
            }

        };
    }
}
