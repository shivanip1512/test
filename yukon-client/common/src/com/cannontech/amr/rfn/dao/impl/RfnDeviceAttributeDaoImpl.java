package com.cannontech.amr.rfn.dao.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.cannontech.amr.rfn.dao.RfnDeviceAttributeDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.util.JsonUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class RfnDeviceAttributeDaoImpl implements RfnDeviceAttributeDao {

    private final Logger log = YukonLogManager.getLogger(RfnDeviceAttributeDaoImpl.class);

    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @Value("classpath:metricIdToAttributeMapping.json") private Resource inputFile;

    private BiMap<Integer, BuiltInAttribute> attributeLookup = null;
    private Map<PaoType, BiMap<Integer, BuiltInAttribute>> paoAttributes = null;
    private Set<BuiltInAttribute> metricAttributes = null;

    public static class MetricIdAttributeMapping {
        public static class MetricIdAttribute {
            Integer metricId;
            BuiltInAttribute attribute;
            
            public void setMetricId(int value) {
                metricId = value;
            }

            public void setAttribute(BuiltInAttribute attr) {
                attribute = attr;
            }
        }

        public static class AttributeOverride {
            Integer metricId;
            BuiltInAttribute attribute;
            List<String> paoTypes;
            
            public void setMetricId(int value) {
                metricId = value;
            }

            public void setAttribute(BuiltInAttribute attr) {
                attribute = attr;
            }
            
            public void setPaoTypes(List<String> types) {
                paoTypes = types;
            }
        }

        List<MetricIdAttribute> metricMapping;
        List<AttributeOverride> attributeOverrides;

        public void setMetricMapping(List<MetricIdAttribute> map) {
            metricMapping = map;
        }
        public void setAttributeOverrides(List<AttributeOverride> overrides) {
            attributeOverrides = overrides;
        }
    }

    @PostConstruct
    public void initialize() throws IOException {

        attributeLookup = HashBiMap.create();
        paoAttributes = new HashMap<>();
        metricAttributes = new HashSet<>();

        String jsonString = IOUtils.toString(inputFile.getInputStream(), Charsets.UTF_8);
        MetricIdAttributeMapping metricList = JsonUtils.fromJson(jsonString, MetricIdAttributeMapping.class);

        for (MetricIdAttributeMapping.MetricIdAttribute m : metricList.metricMapping) {
            try {
                attributeLookup.put(m.metricId, m.attribute);
                metricAttributes.add(m.attribute);
            } catch (NumberFormatException ex) {
                log.warn("Invalid metric number " + m.metricId, ex);
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid attribute name " + m.attribute, ex);
            }
        }
        for (MetricIdAttributeMapping.AttributeOverride a : metricList.attributeOverrides) {
            for (String paoType : a.paoTypes) {
                paoAttributes.computeIfAbsent(PaoType.getForDbString(paoType), key -> HashBiMap.create())
                             .put(a.metricId, a.attribute);
            }
        }
    }

    @Override
    public Collection<BuiltInAttribute> getAttributesForAllTypes() {
        return metricAttributes;
    }

    @Override
    public Collection<BuiltInAttribute> getAttributesForPaoTypes(Set<PaoType> paoTypes) {

        Set<BuiltInAttribute> paoAttributes = new HashSet<>();

        paoAttributes.addAll(metricAttributes);

        Multimap<PaoType, Attribute> definedAttributes = paoDefinitionDao.getPaoTypeAttributesMultiMap();

        for (PaoType type : paoTypes) {
            paoAttributes = Sets.intersection(paoAttributes, new HashSet<>(definedAttributes.get(type)));
        }

        return paoAttributes;
    }
    
    @Override
    public Integer getMetricIdForAttribute(BuiltInAttribute attribute, PaoType type) {
        Integer metricId = 
                Optional.ofNullable(paoAttributes.get(type))
                        .map(m -> m.inverse().get(attribute))
                        .orElse(attributeLookup.inverse().get(attribute));
        
        if (metricId == null) {
            throw new IllegalStateException("No metricId found for attribute " + attribute);
        }
        return metricId;
    }
    
    //  Unit test access
    BiMap<Integer, BuiltInAttribute> getAttributeLookup() {
        return attributeLookup;
    }
    
    public void setInputFile(Resource inputFile) {
        this.inputFile = inputFile;
    }
}
