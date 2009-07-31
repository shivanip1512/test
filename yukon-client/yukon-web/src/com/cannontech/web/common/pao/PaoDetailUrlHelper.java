package com.cannontech.web.common.pao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceTag;
import com.cannontech.common.pao.YukonPao;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class PaoDetailUrlHelper {
    private static Map<DeviceTag, Function<YukonPao, String>> supportUrlPatterns;
    static {
        // note that ImmutableMap preserves the order of its entries
        Builder<DeviceTag, Function<YukonPao, String>> builder = ImmutableMap.builder();
        builder.put(DeviceTag.METER_DETAIL_DISPLAYABLE, new Function<YukonPao, String>() {
            public String apply(YukonPao pao) {
                return "/spring/meter/home?deviceId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        supportUrlPatterns = builder.build();
    }
    
    private DeviceDefinitionDao deviceDefinitionDao;
    
    /**
     * Returns a URL for the appropriate detail page (e.g. the Meter Detail page)
     * or null of no such page exists (in this case it is expected that no link
     * would be rendered).
     * 
     * @param pao
     * @return an absolute URL path
     */
    public String getUrlForPaoDetailPage(YukonPao pao) {
        for (Map.Entry<DeviceTag, Function<YukonPao, String>> entry : supportUrlPatterns.entrySet()) {
            if (deviceDefinitionDao.isTagSupported(pao.getPaoIdentifier().getPaoType(), entry.getKey())) {
                String result = entry.getValue().apply(pao);
                return result;
            }
        }
        return null;
    }
    
    @Autowired
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }
}
