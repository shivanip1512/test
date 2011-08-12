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

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.ListBasedDeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Implementation of DeviceCollectionProducer for an address range
 */
public class DeviceAddressRangeCollectionProducer implements DeviceCollectionProducer {

    private PaoDao paoDao = null;

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.addressRange;
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException {

        final int startAddress = ServletRequestUtils.getIntParameter(request, getSupportedType().getParameterName("start"), -1);
        final int endAddress = ServletRequestUtils.getIntParameter(request, getSupportedType().getParameterName("end"), -1);
        
        Validate.isTrue(startAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(endAddress >= 0, "start address must be greater than or equal to 0");
        Validate.isTrue(startAddress <= endAddress, "end address must be greater than start address");

        return new ListBasedDeviceCollection() {

            public Map<String, String> getCollectionParameters() {

                Map<String, String> paramMap = new HashMap<String, String>();

                paramMap.put("collectionType", getSupportedType().name());
                paramMap.put(getSupportedType().getParameterName("start"),
                             String.valueOf(startAddress));
                paramMap.put(getSupportedType().getParameterName("end"), String.valueOf(endAddress));

                return paramMap;
            }

            public List<SimpleDevice> getDeviceList() {

                List<PaoIdentifier> paoIdentifierList = paoDao.getPaosByAddressRange(startAddress,
                                                                                                 endAddress);
                List<SimpleDevice> deviceList = PaoUtils.asSimpleDeviceList(paoIdentifierList);
                return deviceList;
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return new YukonMessageSourceResolvable("yukon.common.device.bulk.bulkAction.collection.addressRange", startAddress, endAddress);
            }

        };
    }
}
