package com.cannontech.web.common.pao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class PaoDetailUrlHelper {
    private static Map<PaoTag, Function<YukonPao, String>> supportDeviceUrlPatterns;
    private static Map<PaoType, Function<YukonPao, String>> supportPaoUrlPatterns;
    static {
        // note that ImmutableMap preserves the order of its entries

        // Device urls
        Builder<PaoTag, Function<YukonPao, String>> tagBuilder = ImmutableMap.builder();
        tagBuilder.put(PaoTag.METER_DETAIL_DISPLAYABLE, new Function<YukonPao, String>() {
            public String apply(YukonPao pao) {
                return "/meter/home?deviceId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        tagBuilder.put(PaoTag.WATER_METER_DETAIL_DISPLAYABLE, new Function<YukonPao, String>() {
            public String apply(YukonPao pao) {
                return "/meter/water/home?deviceId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        tagBuilder.put(PaoTag.LM_SCENARIO, new Function<YukonPao, String>() {
            public String apply(YukonPao pao) {
            	return "/dr/scenario/detail?scenarioId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        tagBuilder.put(PaoTag.LM_CONTROL_AREA, new Function<YukonPao, String>() {
            public String apply(YukonPao pao) {
            	return "/dr/controlArea/detail?controlAreaId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        tagBuilder.put(PaoTag.LM_PROGRAM, new Function<YukonPao, String>() {
            public String apply(YukonPao pao) {
            	return "/dr/program/detail?programId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        tagBuilder.put(PaoTag.LM_GROUP, new Function<YukonPao, String>() {
            public String apply(YukonPao pao) {
                return "/dr/loadGroup/detail?loadGroupId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        
        supportDeviceUrlPatterns = tagBuilder.build();

        // All 'to-date' known uses of this builder have been replaced with tagBuilder options.
        // This builder is being left here for completeness and potential future use.
        // Pao type urls
        Builder<PaoType, Function<YukonPao, String>> paoBuilder = ImmutableMap.builder();
        supportPaoUrlPatterns = paoBuilder.build();
        
        
    }
    
    private PaoDefinitionDao paoDefinitionDao;
    
    /**
     * Returns a URL for the appropriate detail page (e.g. the Meter Detail page)
     * or null of no such page exists (in this case it is expected that no link
     * would be rendered).
     * 
     * @param pao
     * @return an absolute URL path
     */
    public String getUrlForPaoDetailPage(YukonPao pao) {
        PaoType paoType = pao.getPaoIdentifier().getPaoType();
        
        // Check device url pattern map
        for (Map.Entry<PaoTag, Function<YukonPao, String>> entry : supportDeviceUrlPatterns.entrySet()) {
            if (paoDefinitionDao.isTagSupported(paoType, entry.getKey())) {
                String url = entry.getValue().apply(pao);
                return url;
            }
        }

        // Check pao url pattern map first
        Function<YukonPao, String> urlFunction = supportPaoUrlPatterns.get(paoType);
        if(urlFunction != null) {
            String url = urlFunction.apply(pao);
            return url;
        }
        
        // No url builder found, return null
        return null;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}
