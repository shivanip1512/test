package com.cannontech.web.bulk.model.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;
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

        final int startAddress = ServletRequestUtils.getIntParameter(request, getParameterName("start"), -1);
        final int endAddress = ServletRequestUtils.getIntParameter(request, getParameterName("end"), -1);
        
        Validate.isTrue(startAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(endAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(startAddress <= endAddress, "end address must be greater than start address");

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
