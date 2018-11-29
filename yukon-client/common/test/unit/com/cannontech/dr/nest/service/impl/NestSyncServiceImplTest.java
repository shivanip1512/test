package com.cannontech.dr.nest.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncI18nKey;
import com.cannontech.dr.nest.service.impl.NestSyncServiceImpl.Blacklist;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
public class NestSyncServiceImplTest {
    
    NestSyncServiceImpl impl;
    
    @Before
    public void init() {
        impl = new NestSyncServiceImpl();
    }
    
    @Test
    public void test_hasArea() {
        PaoIdentifier program1 = new PaoIdentifier(1, PaoType.LM_NEST_PROGRAM);
        PaoIdentifier program2 = new PaoIdentifier(2, PaoType.LM_ECOBEE_PROGRAM);
        PaoIdentifier area1 = new PaoIdentifier(3, PaoType.LM_CONTROL_AREA);
        HashSet<PaoIdentifier> set1 = new HashSet<>();
        HashSet<PaoIdentifier> set2 = new HashSet<>();
        set1.add(area1);
        Multimap<PaoIdentifier, PaoIdentifier> mm = ArrayListMultimap.create();
        mm.putAll(program1, set1);
        mm.putAll(program2, set2);
        assertTrue(impl.hasArea(Collections.singleton(program1), mm));
        assertFalse(impl.hasArea(Collections.singleton(program2), mm));
    }
    
    @Test
    public void test_validateProgramAndAreaSetup() {
        
        LiteYukonPAObject group = new LiteYukonPAObject(1);
        group.setPaoType(PaoType.LM_GROUP_NEST);
                
        LiteYukonPAObject groupEmpty = new LiteYukonPAObject(2);
        groupEmpty.setPaoType(PaoType.LM_GROUP_NEST);
        
        LiteYukonPAObject groupProgramNoArea = new LiteYukonPAObject(3);
        groupProgramNoArea.setPaoType(PaoType.LM_GROUP_NEST);
        
        PaoIdentifier program1 = new PaoIdentifier(4, PaoType.LM_NEST_PROGRAM);
        PaoIdentifier programEmpty = new PaoIdentifier(5, PaoType.LM_NEST_PROGRAM);
        PaoIdentifier area1 = new PaoIdentifier(6, PaoType.LM_CONTROL_AREA);
        
        
        
        Multimap<PaoIdentifier, PaoIdentifier> mmGtP = ArrayListMultimap.create();
        Multimap<PaoIdentifier, PaoIdentifier> mmPtA= ArrayListMultimap.create();

        Blacklist bl = new Blacklist();
        
        mmGtP.putAll(group.getPaoIdentifier(), Collections.singleton(program1));
        mmGtP.putAll(groupEmpty.getPaoIdentifier(), new ArrayList<PaoIdentifier>());
        mmGtP.putAll(groupProgramNoArea.getPaoIdentifier(), Collections.singleton(programEmpty));
        
        
        mmPtA.putAll(program1, Collections.singleton(area1));
        mmPtA.putAll(programEmpty, new ArrayList<PaoIdentifier>());
        
        List<LiteYukonPAObject> nestGroups = new ArrayList<>();
        nestGroups.add(group);
        nestGroups.add(groupEmpty);
        nestGroups.add(groupProgramNoArea);
        
        List<NestSyncDetail> actual = impl.validateProgramAndAreaSetup(0, nestGroups, mmGtP, mmPtA, bl);
        assertEquals(2, actual.size());
    }
    
    @Test
    public void test_buildSyncDetail() {
        NestSyncServiceImpl impl = new NestSyncServiceImpl();
        
        LiteYukonPAObject group = new LiteYukonPAObject(1);
        group.setPaoType(PaoType.LM_GROUP_NEST);
                
        LiteYukonPAObject groupEmpty = new LiteYukonPAObject(2);
        groupEmpty.setPaoType(PaoType.LM_GROUP_NEST);
        
        LiteYukonPAObject groupProgramNoArea = new LiteYukonPAObject(3);
        groupProgramNoArea.setPaoType(PaoType.LM_GROUP_NEST);
        
        PaoIdentifier program1 = new PaoIdentifier(4, PaoType.LM_NEST_PROGRAM);
        PaoIdentifier programEmpty = new PaoIdentifier(5, PaoType.LM_NEST_PROGRAM);
        PaoIdentifier area1 = new PaoIdentifier(6, PaoType.LM_CONTROL_AREA);
        
        
        
        Multimap<PaoIdentifier, PaoIdentifier> mmGtP = ArrayListMultimap.create();
        Multimap<PaoIdentifier, PaoIdentifier> mmPtA= ArrayListMultimap.create();

        Blacklist bl = new Blacklist();
        
        mmGtP.putAll(group.getPaoIdentifier(), Collections.singleton(program1));
        mmGtP.putAll(groupEmpty.getPaoIdentifier(), new ArrayList<PaoIdentifier>());
        mmGtP.putAll(groupProgramNoArea.getPaoIdentifier(), Collections.singleton(programEmpty));
        
        
        mmPtA.putAll(program1, Collections.singleton(area1));
        mmPtA.putAll(programEmpty, new ArrayList<PaoIdentifier>());

        Optional<NestSyncDetail> emptyOptional = impl.buildSyncDetail(1, group, mmGtP, mmPtA, bl);
        assertFalse(emptyOptional.isPresent());
        
        Optional<NestSyncDetail> programNotFoundActual = impl.buildSyncDetail(1, groupEmpty, mmGtP, mmPtA, bl);
        assertTrue(programNotFoundActual.isPresent());
        Assert.assertEquals(NestSyncI18nKey.NOT_FOUND_PROGRAM_FOR_NEST_GROUP, programNotFoundActual.get().getReasonKey());
                
        Optional<NestSyncDetail> areaNotFound = impl.buildSyncDetail(1, groupProgramNoArea, mmGtP, mmPtA, bl);
        assertTrue(areaNotFound.isPresent());
        Assert.assertEquals(NestSyncI18nKey.NOT_FOUND_AREA_FOR_NEST_GROUP, areaNotFound.get().getReasonKey());
    }

    @Test
    public void test_validateThermostats() {
        Set<String> thermostatsInNest = Collections.singleton("nonNest");
        
        HashMap<String, Thermostat> thermostatMap = new HashMap<>();
        
        Thermostat nonNest = new Thermostat();
        nonNest.setType(HardwareType.ECOBEE_3);
        
        Thermostat nestThermostat = new Thermostat();
        nestThermostat.setType(HardwareType.NEST_THERMOSTAT);
        
        thermostatMap.put("nonNest", nonNest);
        thermostatMap.put("nestThermostat", nestThermostat);
        
        Blacklist bl = new Blacklist();
        
        List<NestSyncDetail> actual = impl.validateThermostats(thermostatsInNest, thermostatMap, 1, bl);
        assertEquals(1, actual.size());
        assertEquals(NestSyncI18nKey.NOT_NEST_THERMOSTAT, actual.get(0).getReasonKey());
        thermostatMap.remove("nonNest");
        
        actual = impl.validateThermostats(thermostatsInNest, thermostatMap, 1, bl);
        assertEquals(0, actual.size());
    }
    
    @Test
    public void test_filterNestGroups() {
        
        
        LiteYukonPAObject lyp = new LiteYukonPAObject(1, "nestName", PaoType.LM_GROUP_ECOBEE, "", "");
        
        List<String> groupsInNest = new ArrayList<>();
        groupsInNest.add("nestName");
        
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllLMGroups()).andReturn(Collections.singletonList((lyp)));
        EasyMock.replay(cache);
        
        Blacklist bl = new Blacklist();
        ReflectionTestUtils.setField(impl, "dbCache", cache);

        List<NestSyncDetail> actual = impl.filterNestGroups(groupsInNest, 1, bl);
        
        assertEquals(1, actual.size());
        assertEquals(NestSyncI18nKey.FOUND_NON_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME, actual.get(0).getReasonKey());
        
    }

}
