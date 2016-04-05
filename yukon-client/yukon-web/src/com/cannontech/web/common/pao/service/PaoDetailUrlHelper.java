package com.cannontech.web.common.pao.service;

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
    
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private static Map<PaoTag, Function<YukonPao, String>> supportDeviceUrlPatterns;
    private static Map<PaoType, Function<YukonPao, String>> capControlUrlPatterns;
    private static Map<PaoTag, String> supportDevicePageNames;
    
    static {
        // note that ImmutableMap preserves the order of its entries
        
        // Device urls
        Builder<PaoTag, Function<YukonPao, String>> urlBuilder = ImmutableMap.builder();
        Builder<PaoTag, String> pageNameBuilder = ImmutableMap.builder();
        urlBuilder.put(PaoTag.METER_DETAIL_DISPLAYABLE, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/meter/home?deviceId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        pageNameBuilder.put(PaoTag.METER_DETAIL_DISPLAYABLE, "meterDetail.electric");

        urlBuilder.put(PaoTag.WATER_METER_DETAIL_DISPLAYABLE, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/meter/water/home?deviceId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        pageNameBuilder.put(PaoTag.WATER_METER_DETAIL_DISPLAYABLE, "meterDetail.water");

        urlBuilder.put(PaoTag.LM_SCENARIO, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/dr/scenario/detail?scenarioId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        pageNameBuilder.put(PaoTag.LM_SCENARIO, "scenarioDetail");

        urlBuilder.put(PaoTag.LM_CONTROL_AREA, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/dr/controlArea/detail?controlAreaId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        pageNameBuilder.put(PaoTag.LM_CONTROL_AREA, "controlAreaDetail");

        urlBuilder.put(PaoTag.LM_PROGRAM, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/dr/program/detail?programId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        pageNameBuilder.put(PaoTag.LM_PROGRAM, "programDetail");

        urlBuilder.put(PaoTag.LM_GROUP, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/dr/loadGroup/detail?loadGroupId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        pageNameBuilder.put(PaoTag.LM_GROUP, "loadGroupDetail");
        
        urlBuilder.put(PaoTag.ASSET_DETAIL_DISPLAYABLE, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/stars/operator/inventory/view?deviceId=" + pao.getPaoIdentifier().getPaoId();
            }
        });
        pageNameBuilder.put(PaoTag.ASSET_DETAIL_DISPLAYABLE, "hardware.VIEW");

        supportDeviceUrlPatterns = urlBuilder.build();
        supportDevicePageNames = pageNameBuilder.build();

        Builder<PaoType, Function<YukonPao, String>> capControlUrlBuilder = ImmutableMap.builder();

        Function<YukonPao, String> cbcLink = new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/capcontrol/cbc/" + pao.getPaoIdentifier().getPaoId();
            }
        };
        Function<YukonPao, String> regulatorLink = new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/capcontrol/regulators/" + pao.getPaoIdentifier().getPaoId();
            }
        };
        for (PaoType type : PaoType.getCbcTypes()) {
            capControlUrlBuilder.put(type, cbcLink);
        }
        for (PaoType type : PaoType.getRegulatorTypes()) {
            capControlUrlBuilder.put(type, regulatorLink);
        }
        
        capControlUrlBuilder.put(PaoType.CAPBANK, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/capcontrol/capbanks/" + pao.getPaoIdentifier().getPaoId();
            }
        });
        
        capControlUrlBuilder.put(PaoType.CAP_CONTROL_FEEDER, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/capcontrol/feeders/" + pao.getPaoIdentifier().getPaoId();
            }
        });
        
        capControlUrlBuilder.put(PaoType.CAP_CONTROL_SUBBUS, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/capcontrol/buses/" + pao.getPaoIdentifier().getPaoId();
            }
        });
        
        capControlUrlBuilder.put(PaoType.CAP_CONTROL_SUBSTATION, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/capcontrol/substations/" + pao.getPaoIdentifier().getPaoId();
            }
        });
        
        capControlUrlBuilder.put(PaoType.CAP_CONTROL_AREA, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/capcontrol/areas/" + pao.getPaoIdentifier().getPaoId();
            }
        });
        
        capControlUrlBuilder.put(PaoType.CAP_CONTROL_SPECIAL_AREA, new Function<YukonPao, String>() {
            @Override
            public String apply(YukonPao pao) {
                return "/capcontrol/areas/" + pao.getPaoIdentifier().getPaoId();
            }
        });
        
        capControlUrlPatterns = capControlUrlBuilder.build();
    }
    
    /**
     * Returns a URL for the appropriate detail page (e.g. the Meter Detail page)
     * or null of no such page exists (in this case it is expected that no link
     * would be rendered).
     * 
     * @param pao
     * @return an absolute URL path
     */
    public String getUrlForPaoDetailPage(YukonPao pao) {
        
        PaoType type = pao.getPaoIdentifier().getPaoType();
        
        if (PaoType.getRfGatewayTypes().contains(type)) {
            return "/stars/gateways/" + pao.getPaoIdentifier().getPaoId();
        }
        if (type.isCapControl()) {
            return capControlUrlPatterns.get(type).apply(pao);
        }
        
        // Check device url pattern map
        for (Map.Entry<PaoTag, Function<YukonPao, String>> entry : supportDeviceUrlPatterns.entrySet()) {
            if (paoDefinitionDao.isTagSupported(type, entry.getKey())) {
                String url = entry.getValue().apply(pao);
                return url;
            }
        }

        // No url builder found, return null
        return null;
    }

    public String getPageNameForPaoDetailPage(YukonPao pao) {
        
        PaoType type = pao.getPaoIdentifier().getPaoType();
        
        // Check device page name pattern map
        for (Map.Entry<PaoTag, String> entry : supportDevicePageNames.entrySet()) {
            if (paoDefinitionDao.isTagSupported(type, entry.getKey())) {
                String pageName = entry.getValue();
                return pageName;
            }
        }

        // No page name found, return null
        return null;
    }
    
}