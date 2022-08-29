package com.cannontech.common.pao.meter.model;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;

public class MeterTypeHelper {
    
    private static Map<MeterGroup, Set<PaoType>> createGroupedMeters;
    @Autowired private PaoDefinitionDao paoDefDao;
    @Autowired private ConfigurationSource configurationSource;

    
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
        
        Map<MeterGroup, Set<PaoType>> allGroupedBuilder = new LinkedHashMap<>();
        
        Set<PaoDefinition> creatableDefs = paoDefDao.getCreatablePaoDefinitions();
        Set<PaoType> creatable = new LinkedHashSet<>(); 
        creatableDefs.stream().forEach(def -> creatable.add(def.getType()));
        
        Comparator<PaoType> byDbString = (PaoType o1, PaoType o2) -> o1.getDbString().compareTo(o2.getDbString());

        Set<PaoType> rfMetersBuilder = new LinkedHashSet<>();
        rfMetersBuilder.addAll(PaoType.getRfMeterTypes().stream()
                  .filter(type -> creatable.contains(type))
                  .sorted(byDbString).collect(Collectors.toList()));
        allGroupedBuilder.put(MeterGroup.RF_MESH, rfMetersBuilder);
        
        Set<PaoType> mctMetersBuilder = new LinkedHashSet<>();
        mctMetersBuilder.addAll(PaoType.getMctTypes().stream()
                 .filter(type -> creatable.contains(type))
                 .sorted(byDbString).collect(Collectors.toList()));
        allGroupedBuilder.put(MeterGroup.MCT, mctMetersBuilder);
        
        Set<PaoType> virtualMeterBuilder = new LinkedHashSet<>();
        virtualMeterBuilder.add(PaoType.VIRTUAL_METER);
        allGroupedBuilder.put(MeterGroup.VIRTUAL, virtualMeterBuilder);

        // YUK-17216 asked for these to not be included, at least for now.
        /*ImmutableSet.Builder<PaoType> electronicMetersBuilder = ImmutableSet.builder();
        List<PaoType> iedTypes = PaoType.getIedTypes().stream()
                .filter(type -> !type.isIon() && !type.isIpc() && creatable.contains(type)) 
                .sorted(byDbString).collect(Collectors.toList());
        electronicMetersBuilder.addAll(iedTypes);
        allGroupedBuilder.put(MeterGroup.ELECTRONIC, electronicMetersBuilder.build());*/

        // The attribute group map that is created can be used in conjunction with
        // the selectNameValue tag and groupItems="true".
        createGroupedMeters = allGroupedBuilder;
        removeMetersForRoleProperty();
    }

    public Map<MeterGroup, Set<PaoType>> getCreateGroupedMeters() {
        return createGroupedMeters;
    }
    
    private void removeMetersForRoleProperty() {
        // Remove DER Edge Coordinator device from Create Meter when Edge cparm is False
        // YUK-26189
        if (!configurationSource.getBoolean(MasterConfigBoolean.DER_EDGE_COORDINATOR)) {
            createGroupedMeters.get(MeterGroup.RF_MESH).remove(PaoType.RFN530S4X_DER);
        }
    }
}
