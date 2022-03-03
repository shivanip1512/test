package com.cannontech.web.common.pao.service;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImpl;
import com.cannontech.common.pao.definition.loader.DefinitionLoaderServiceImpl;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class YukonPointHelperImplTest {

    private YukonPointHelperImpl yukonPointHelperImpl;
    private final CustomAttribute ca1 = new CustomAttribute(1, "foo");
    private final CustomAttribute ca2 = new CustomAttribute(2, "alpha");
    private final CustomAttribute ca3 = new CustomAttribute(3, "beta");
    
    private final StaticMessageSource messageSource = new StaticMessageSource();
    {
        messageSource.addMessage("yukon.common.attribute.customAttribute.1", Locale.US, "Z Foo");
        messageSource.addMessage("yukon.common.attribute.customAttribute.2", Locale.US, "Y Alpha");
        messageSource.addMessage("yukon.common.attribute.customAttribute.3", Locale.US, "X Beta");
    }
    
    // sorted by i18n: ca3, ca2, ca1
    // sorted by default (name): ca2, ca3, ca1

    private class YukonUserContextMessageSourceResolverMock implements YukonUserContextMessageSourceResolver, MessageSourceAware {
        
        private MessageSource messageSource;

        @Override
        public MessageSource getMessageSource(YukonUserContext userContext) {
            return messageSource;
        }

        @Override
        public MessageSourceAccessor getMessageSourceAccessor(YukonUserContext userContext) {
            return new MessageSourceAccessor(messageSource, Locale.US);
        }

        @Override
        public void setMessageSource(MessageSource messageSource) {
            this.messageSource = messageSource;
        }
    };
    
    @BeforeEach
    public void setUp() {
        yukonPointHelperImpl = new YukonPointHelperImpl();
        
        PaoDefinitionDao paoDefinitionDao = getTestPaoDefinitionDao();
        ReflectionTestUtils.setField(yukonPointHelperImpl, "paoDefinitionDao", paoDefinitionDao);
    }
    
    @Test
    public void test_firstAttribute_null() throws Exception {
        Set<BuiltInAttribute> builtInAttributes = Collections.emptySet();
        List<CustomAttribute> customAttributes = Collections.emptyList();

        YukonUserContextMessageSourceResolverMock resolver = new YukonUserContextMessageSourceResolverMock();
        resolver.setMessageSource(new StaticMessageSource());
        Attribute attribute = executeTest(builtInAttributes, customAttributes, resolver);
        assertEquals(null, attribute, "Objects not equal.");
    }

    @Test
    public void test_firstAttribute_onlyOneCustomAttribute_one() throws Exception {
        Set<BuiltInAttribute> builtInAttributes = Collections.emptySet();
        List<CustomAttribute> customAttributes = Lists.newArrayList(ca3);
        
        YukonUserContextMessageSourceResolverMock resolver = new YukonUserContextMessageSourceResolverMock();
        resolver.setMessageSource(new StaticMessageSource());
        Attribute attribute = executeTest(builtInAttributes, customAttributes, resolver);
        assertEquals(ca3, attribute, "Objects not equal.");
    }

    @Test
    public void test_firstAttribute_onlyOneCustomAttribute_many() throws Exception {
        Set<BuiltInAttribute> builtInAttributes = Collections.emptySet();
        List<CustomAttribute> customAttributes = Lists.newArrayList(ca3, ca2);
        
        YukonUserContextMessageSourceResolverMock resolver = new YukonUserContextMessageSourceResolverMock();
        resolver.setMessageSource(new StaticMessageSource());
        Attribute attribute = executeTest(builtInAttributes, customAttributes, resolver);
        assertEquals(ca2, attribute, "Objects not equal.");
    }

    @Test
    public void test_firstAttribute_onlyBuiltInAttribute_one() throws Exception {
        Set<BuiltInAttribute> builtInAttributes = Sets.newHashSet(BuiltInAttribute.DELIVERED_KWH);
        List<CustomAttribute> customAttributes = Collections.emptyList();
        
        YukonUserContextMessageSourceResolverMock resolver = new YukonUserContextMessageSourceResolverMock();
        resolver.setMessageSource(new StaticMessageSource());
        Attribute attribute = executeTest(builtInAttributes, customAttributes, resolver);
        assertEquals(BuiltInAttribute.DELIVERED_KWH, attribute, "Objects not equal.");
    }

    @Test
    public void test_firstAttribute_onlyBuiltInAttribute_many() throws Exception {
        Set<BuiltInAttribute> builtInAttributes = Sets.newHashSet(BuiltInAttribute.DELIVERED_KWH, BuiltInAttribute.USAGE);
        List<CustomAttribute> customAttributes = Collections.emptyList();
        
        YukonUserContextMessageSourceResolverMock resolver = new YukonUserContextMessageSourceResolverMock();
        resolver.setMessageSource(new StaticMessageSource());
        Attribute attribute = executeTest(builtInAttributes, customAttributes, resolver);
        // PaoType.RFN420CD = <pointInfo name="Delivered kWh" init="true" attributes="USAGE,DELIVERED_KWH"/>
        assertEquals(BuiltInAttribute.USAGE, attribute, "Objects not equal.");
    }

    @Test
    public void test_firstAttribute_bothTypesOfAttributes() throws Exception {
        Set<BuiltInAttribute> builtInAttributes = Sets.newHashSet(BuiltInAttribute.USAGE, BuiltInAttribute.DELIVERED_KWH);
        List<CustomAttribute> customAttributes = Lists.newArrayList(ca1, ca2, ca3);
        
        YukonUserContextMessageSourceResolverMock resolver = new YukonUserContextMessageSourceResolverMock();
        resolver.setMessageSource(new StaticMessageSource());
        Attribute attribute = executeTest(builtInAttributes, customAttributes, resolver);
        assertEquals(ca2, attribute, "Objects not equal.");
    }
    
    @Test
    public void test_firstAttribute_bothTypesOfAttributes_i18n() throws Exception {
        Set<BuiltInAttribute> builtInAttributes = Sets.newHashSet(BuiltInAttribute.USAGE, BuiltInAttribute.DELIVERED_KWH);
        List<CustomAttribute> customAttributes = Lists.newArrayList(ca1, ca2, ca3);
        
        YukonUserContextMessageSourceResolverMock resolver = new YukonUserContextMessageSourceResolverMock();
        resolver.setMessageSource(messageSource);
        Attribute attribute = executeTest(builtInAttributes, customAttributes, resolver);
        assertEquals(ca3, attribute, "Objects not equal.");
    }
    
    private Attribute executeTest(Set<BuiltInAttribute> builtInAttributes, List<CustomAttribute> customAttributes, 
            YukonUserContextMessageSourceResolverMock resolver) {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(YukonUserContext.system);
        List<Attribute> attributes = new ArrayList<>();
        attributes.addAll(builtInAttributes);
        attributes.addAll(customAttributes);
        Attribute attribute = ReflectionTestUtils.invokeMethod(yukonPointHelperImpl,
                                                                               "getFirstAttribute",
                                                                               PaoType.RFN420CD,
                                                                               accessor,
                                                                               builtInAttributes,
                                                                               customAttributes,
                                                                               attributes);
        return attribute;
    }
    
    public static PaoDefinitionDao getTestPaoDefinitionDao() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext();
        PaoDefinitionDaoImpl paoDefinitionDaoImpl = new PaoDefinitionDaoImpl();
        DefinitionLoaderServiceImpl definitionLoaderService = new DefinitionLoaderServiceImpl();
        Resource paoXsd = ctx.getResource("classpath:pao/definition/pao.xsd");
        Resource pointsXsd = ctx.getResource("classpath:pao/definition/points.xsd");
        Resource overrideXsd = ctx.getResource("classpath:pao/definition/override.xsd");
        
        StateGroupDao stateGroupDao = createNiceMock(StateGroupDao.class);
        stateGroupDao.getStateGroup(anyObject(String.class));
        expectLastCall().andAnswer(() -> {
            ArrayList<LiteState> states = new ArrayList<>();
            states.add(new LiteState(0, "Decommissioned", 0, 0, 0));
            return new LiteStateGroup(0, "state0", states);
        }).anyTimes();

        stateGroupDao.getStateGroup(anyInt());
        expectLastCall().andAnswer(() -> new LiteStateGroup(0, "state0")).anyTimes();

        PointDao pointDao = createNiceMock(PointDao.class);
        ReflectionTestUtils.setField(definitionLoaderService, "paoXsd", paoXsd);
        ReflectionTestUtils.setField(definitionLoaderService, "pointsXsd", pointsXsd);
        ReflectionTestUtils.setField(definitionLoaderService, "overrideXsd", overrideXsd);
        ReflectionTestUtils.setField(definitionLoaderService, "stateGroupDao", stateGroupDao);
        replay(stateGroupDao);
        ReflectionTestUtils.setField(definitionLoaderService, "pointDao", pointDao);
        ReflectionTestUtils.setField(paoDefinitionDaoImpl, "definitionLoaderService", definitionLoaderService);
        definitionLoaderService.load();
        paoDefinitionDaoImpl.initialize();
        ctx.close();
        return paoDefinitionDaoImpl;
    }
}