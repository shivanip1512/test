package com.cannontech.common.pao.meter.model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class MeterTypeHelper {
    
    private static Map<MeterGroup, Set<PaoType>> createGroupedMeters;
    @Autowired private PaoDefinitionDao paoDefDao;

    
    public enum MeterGroup implements DisplayableEnum  {
    
        // RFN/MCT/Electronic Meter groups.
        MCT,
        RF_MESH,
        VIRTUAL,
        ELECTRONIC;        
        private static final String groupPrefix = "yukon.common.meterGroups.";

    
        @Override
        public String getFormatKey() {
            return groupPrefix + name();
            
        }
    }
    
    
    /**
     * On startup, put the enum entries into collections for easy access.
     */
    @PostConstruct
    private void init() {
        buildCreateMeterGroups();
    }
    
    
    /**
     * Group meters in categories so they can be more easily displayed and
     * found in the UI.
     */
    
    private void buildCreateMeterGroups() { 
        
        ImmutableMap.Builder<MeterGroup, Set<PaoType>> allGroupedBuilder = ImmutableMap.builder();
        
        Set<PaoDefinition> creatableDefs = paoDefDao.getCreatablePaoDefinitions();
        Set<PaoType> creatable = new HashSet<>(); 
        creatableDefs.stream().forEach(def -> creatable.add(def.getType()));
        
        Comparator<PaoType> byDbString = (PaoType o1, PaoType o2) -> o1.getDbString().compareTo(o2.getDbString());

        ImmutableSet.Builder<PaoType> mctMetersBuilder = ImmutableSet.builder();
        mctMetersBuilder.addAll(PaoType.getMctTypes().stream()
                 .filter(type -> creatable.contains(type))
                 .sorted(byDbString).collect(Collectors.toList()));
        allGroupedBuilder.put(MeterGroup.MCT, mctMetersBuilder.build());
        
        ImmutableSet.Builder<PaoType> rfMetersBuilder = ImmutableSet.builder();
        rfMetersBuilder.addAll(PaoType.getRfMeterTypes().stream()
                  .filter(type -> creatable.contains(type))
                  .sorted(byDbString).collect(Collectors.toList()));
        allGroupedBuilder.put(MeterGroup.RF_MESH, rfMetersBuilder.build());
        
        ImmutableSet.Builder<PaoType> virtualMeterBuilder = ImmutableSet.builder();
        virtualMeterBuilder.add(PaoType.VIRTUAL_METER);
        allGroupedBuilder.put(MeterGroup.VIRTUAL, virtualMeterBuilder.build());

        // YUK-17216 asked for these to not be included, at least for now.
        /*ImmutableSet.Builder<PaoType> electronicMetersBuilder = ImmutableSet.builder();
        List<PaoType> iedTypes = PaoType.getIedTypes().stream()
                .filter(type -> !type.isIon() && !type.isIpc() && creatable.contains(type)) 
                .sorted(byDbString).collect(Collectors.toList());
        electronicMetersBuilder.addAll(iedTypes);
        allGroupedBuilder.put(MeterGroup.ELECTRONIC, electronicMetersBuilder.build());*/

        // The attribute group map that is created can be used in conjunction with
        // the selectNameValue tag and groupItems="true".
        createGroupedMeters = allGroupedBuilder.build();
    }

    public Map<MeterGroup, Set<PaoType>> getCreateGroupedMeters() {
        return createGroupedMeters;
    }
}
