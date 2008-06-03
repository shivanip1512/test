package com.cannontech.web.bulk.model.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.ListBasedDeviceCollection;
import com.cannontech.common.bulk.mapper.LiteYukonPAObjectToYukonDeviceMapper;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.MappingList;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Implementation of DeviceCollectionProducer for an address range
 */
public class DeviceAddressRangeCollectionProducer extends
        DeviceCollectionProducerBase {

    private PaoDao paoDao = null;
    private LiteYukonPAObjectToYukonDeviceMapper deviceMapper;

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setDeviceMapper(LiteYukonPAObjectToYukonDeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
    }

    public String getSupportedType() {
        return "addressRange";
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        String startAddressStr = ServletRequestUtils.getStringParameter(request, getParameterName("start"));
        String endAddressStr = ServletRequestUtils.getStringParameter(request, getParameterName("end"));
        
        final int startAddress = Integer.valueOf(StringUtils.strip(startAddressStr));
        final int endAddress = Integer.valueOf(StringUtils.strip(endAddressStr));

        return new ListBasedDeviceCollection() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType());
                paramMap.put(getParameterName("start"),
                             String.valueOf(startAddress));
                paramMap.put(getParameterName("end"), String.valueOf(endAddress));

                return paramMap;
            }

            public List<YukonDevice> getDeviceList() {

                List<LiteYukonPAObject> litePaoList = paoDao.getLiteYukonPaobjectsByAddressRange(startAddress,
                                                                                                 endAddress);
                List<YukonDevice> deviceList = new MappingList<LiteYukonPAObject, YukonDevice>(litePaoList, deviceMapper);
                return deviceList;
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.addressRange", startAddress, endAddress);
            }

        };
    }
}
