package com.cannontech.common.rfn.dataStreaming;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.rfn.dao.impl.RfnDeviceAttributeDaoImpl;
import com.cannontech.amr.rfn.service.pointmapping.PointMapper;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMappingParser;
import com.cannontech.common.config.dao.RfnPointMappingDao;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.loader.DefinitionLoaderServiceImpl;
import com.cannontech.common.pao.definition.loader.PaoConfigurationException;
import com.cannontech.common.pao.service.impl.PointCreationServiceImpl;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper.DataStreamingPaoAttributes;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class DataStreamingAttributeHelperTest {
    @Autowired private UnitOfMeasureToPointMappingParser unitOfMeasureToPointMapper;
    private static Map<PaoType, Map<Attribute, AttributeDefinition>> paoAttributeAttrDefinitionMap;
    private static Multimap<PaoType, Attribute> paoTypeAttributesMultiMap;
    private static Multimap<PaoType, PointMapper> rfnPointMap;
    private static RfnDeviceAttributeDaoImpl rfnDeviceAttributeDao;
    private ApplicationContext ctx;

    @Before
    public void setUp() throws Exception {
        ctx = new ClassPathXmlApplicationContext();
        Resource paoXsd = ctx.getResource("classpath:pao/definition/pao.xsd");
        Resource pointsXsd = ctx.getResource("classpath:pao/definition/points.xsd");
        Resource overrideXsd = ctx.getResource("classpath:pao/definition/override.xsd");
        DefinitionLoaderServiceImpl definitionLoaderService = new DefinitionLoaderServiceImpl();
        StateGroupDao stateGroupDao = createNiceMock(StateGroupDao.class);
        stateGroupDao.getStateGroup(anyObject(String.class));
        expectLastCall().andAnswer(() -> {
            ArrayList<LiteState> states = new ArrayList<>();
            states.add(new LiteState(0, "Decommissioned", 0, 0, 0));
            return new LiteStateGroup(0, "state0", states);
        }).anyTimes();

        stateGroupDao.getStateGroup(anyInt());
        expectLastCall().andAnswer(() -> new LiteStateGroup(0, "state0")).anyTimes();

        Resource rfnPointMappingRes =
            ctx.getResource("classpath:com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml");
        unitOfMeasureToPointMapper = new UnitOfMeasureToPointMappingParser();
        PaoDefinitionDao paoDefinitionDao = createNiceMock(PaoDefinitionDao.class);
        RfnPointMappingDao rfnPointMappingDao = createNiceMock(RfnPointMappingDao.class);

        rfnPointMappingDao.getPointMappingFile();
        expectLastCall().andAnswer(rfnPointMappingRes::getInputStream).anyTimes();
        ReflectionTestUtils.setField(unitOfMeasureToPointMapper, "paoDefinitionDao", paoDefinitionDao);
        ReflectionTestUtils.setField(unitOfMeasureToPointMapper, "rfnPointMappingDao", rfnPointMappingDao);
        replay(rfnPointMappingDao);
        unitOfMeasureToPointMapper.initialize();
        rfnPointMap = unitOfMeasureToPointMapper.getPointMapper();

        PointDao pointDao = new MockPointDao();
        PointCreationServiceImpl pointCreationServiceImpl = new PointCreationServiceImpl();
        ReflectionTestUtils.setField(pointDao, "pointCreationService", pointCreationServiceImpl);
        ReflectionTestUtils.setField(definitionLoaderService, "paoXsd", paoXsd);
        ReflectionTestUtils.setField(definitionLoaderService, "pointsXsd", pointsXsd);
        ReflectionTestUtils.setField(definitionLoaderService, "overrideXsd", overrideXsd);
        ReflectionTestUtils.setField(definitionLoaderService, "stateGroupDao", stateGroupDao);
        replay(stateGroupDao);
        ReflectionTestUtils.setField(definitionLoaderService, "pointDao", pointDao);
        try {
            definitionLoaderService.load();
        } catch (PaoConfigurationException ex) {
            fail(ex.getMessage());
        }
        paoAttributeAttrDefinitionMap = definitionLoaderService.getPaoAttributeAttrDefinitionMap();
        Builder<PaoType, Attribute> builder = ImmutableListMultimap.builder();
        for (Map.Entry<PaoType, Map<Attribute, AttributeDefinition>> entry : paoAttributeAttrDefinitionMap.entrySet()) {
            builder.putAll(entry.getKey(), entry.getValue().keySet());
        }
        paoTypeAttributesMultiMap = builder.build();
        
        InputStream mapping = this.getClass().getClassLoader().getResourceAsStream("metricIdToAttributeMapping.json");
        rfnDeviceAttributeDao = new RfnDeviceAttributeDaoImpl();
        rfnDeviceAttributeDao.setInputFile(new InputStreamResource(mapping));
        rfnDeviceAttributeDao.initialize();
    }

    @After
    public void tearDown() {
        unitOfMeasureToPointMapper = null;
        paoAttributeAttrDefinitionMap = null;
        paoTypeAttributesMultiMap = null;
        rfnPointMap = null;
        rfnDeviceAttributeDao = null;
        ctx = null;
    }

    @Test
    public void testInOrder() throws Exception {
        testConsistentPaoTypes();
        testRFNAndDeviceTypeXMLs();
        testRfnPointMappingXML();
        testRfnDeviceAttributes();
    }

    public void testConsistentPaoTypes() {
        EnumMap<PaoType, Set<BuiltInAttribute>> typeToSupportedAttributes = Maps.newEnumMap(PaoType.class);
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            Collection<BuiltInAttribute> existingAttributes = typeToSupportedAttributes.get(dspa.getPaoType());
            if (existingAttributes != null) {
                Assert.assertEquals("Pao type " + dspa.getPaoType() + " not consistent when adding " + dspa,
                    existingAttributes, dspa.getSupportedAttributes());
            } else {
                typeToSupportedAttributes.put(dspa.getPaoType(), dspa.getSupportedAttributes());
            }
        }
    }

    /**
     * Validates attributes loaded from RFN and respective Paotype xmls against DataStreamingAttributeHelper.
     */
    private void testRFNAndDeviceTypeXMLs() {
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            Set<BuiltInAttribute> builtInAttributesSet = dspa.getSupportedAttributes();
            Collection<Attribute> attrsList = paoTypeAttributesMultiMap.get(dspa.getPaoType());
            builtInAttributesSet.forEach(entry -> {
                BuiltInAttribute builtInAttribute = (BuiltInAttribute) entry;
                Assert.assertTrue("Point Mismatch for " + dspa.getPaoType() + " for Point " + builtInAttribute.getDescription(), 
                    attrsList.contains(builtInAttribute));
            });
        }
    }
    
    /**
     * Validates DataStreamingAttributeHelper attributes to the points defined in
     * rfnPointMapping xml.
     */
    private void testRfnPointMappingXML() {
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            Set<BuiltInAttribute> builtInAttributesSet = dspa.getSupportedAttributes();
            Map<Attribute, AttributeDefinition> attrDefMap = paoAttributeAttrDefinitionMap.get(dspa.getPaoType());
            Collection<PointMapper> pointMapperCol = rfnPointMap.get(dspa.getPaoType());
            Set<String> rfnPoints = new HashSet<String>();
            pointMapperCol.forEach(pointMapper -> {
                rfnPoints.add(pointMapper.getName());
            });
            builtInAttributesSet.forEach(entry -> {
                BuiltInAttribute builtInAttribute = (BuiltInAttribute) entry;
                AttributeDefinition attributeDefinition = attrDefMap.get(builtInAttribute);

                Assert.assertTrue("Missing point in rfnPointMapping.xml " + builtInAttribute.getDescription() + " for PaoType" + dspa.getPaoType(),
                        rfnPoints.contains(attributeDefinition.getPointTemplate().getName()));
            });
        }
    }
    
    /**
     * Validates DataStreamingAttributeHelper attributes to the attributes defined in
     * metricIdToAttributeMapping.json (rfnDeviceAttributeDao).
     */
    private void testRfnDeviceAttributes() {
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            dspa.getSupportedAttributes().forEach(attribute -> 
                rfnDeviceAttributeDao.getMetricIdForAttribute(attribute, dspa.getPaoType()));
        }
    }
}
