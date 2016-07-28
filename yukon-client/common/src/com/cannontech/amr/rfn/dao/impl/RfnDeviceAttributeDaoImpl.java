package com.cannontech.amr.rfn.dao.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
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

        List<MetricIdAttribute> metricMapping;

        public void setMetricMapping(List<MetricIdAttribute> map) {
            metricMapping = map;
        }
    }

    @PostConstruct
    public void initialize() throws IOException {

        attributeLookup = HashBiMap.create();
        metricAttributes = new HashSet<>();

        String jsonString = IOUtils.toString(inputFile.getInputStream(), Charsets.UTF_8);
        MetricIdAttributeMapping metricList = JsonUtils.fromJson(jsonString, MetricIdAttributeMapping.class);

        for (MetricIdAttributeMapping.MetricIdAttribute m : metricList.metricMapping) {
            try {
                // BuiltInAttribute attr = BuiltInAttribute.valueOf(m.attributeName);
                // Integer metric = Integer.valueOf(m.metricId);
                attributeLookup.put(m.metricId, m.attribute);
                metricAttributes.add(m.attribute);
            } catch (NumberFormatException ex) {
                log.warn("Invalid metric number " + m.metricId, ex);
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid attribute name " + m.attribute, ex);
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
    public Map<Integer, BuiltInAttribute> getMetricToAttributeMap(Collection<BuiltInAttribute> attributes) {
        return attributes.stream()
                         .collect(Collectors.toMap(
                              attribute -> attributeLookup.inverse().get(attribute), 
                              attribute -> attribute));
    }
    
    @Override
    public Integer getMetricIdForAttribute(BuiltInAttribute attribute) {
        Integer metricId = attributeLookup.inverse().get(attribute);
        if (metricId == null) {
            throw new IllegalStateException("No metricId found for attribute " + attribute);
        }
        return metricId;
    }
    
    public void setInputFile(Resource inputFile) {
        this.inputFile = inputFile;
    }
}
