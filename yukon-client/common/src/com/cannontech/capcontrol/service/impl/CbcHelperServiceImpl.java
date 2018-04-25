package com.cannontech.capcontrol.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class CbcHelperServiceImpl implements CbcHelperService {
    private static final Logger logger = YukonLogManager.getLogger(CbcHelperServiceImpl.class);
    private static final String DEFAULT_FIXED_TEXT = "Fixed";

    private RolePropertyDao rolePropertyDao;
    private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private AttributeService attributeService;

    private static final Map<BuiltInAttribute,String> cbcFormatMappings = ImmutableMap.<BuiltInAttribute,String>builder()
        .put(BuiltInAttribute.FIRMWARE_VERSION, "{rawValue|firmwareVersion}")
        .put(BuiltInAttribute.IP_ADDRESS, "{rawValue|ipAddress}")
        .put(BuiltInAttribute.NEUTRAL_CURRENT_SENSOR, "{rawValue|neutralCurrent}")
        .put(BuiltInAttribute.SERIAL_NUMBER, "{rawValue|long}")
        .put(BuiltInAttribute.UDP_PORT, "{rawValue|long}")
        .put(BuiltInAttribute.LAST_CONTROL_REASON, "{rawValue|lastControlReason}")
        .put(BuiltInAttribute.IGNORED_CONTROL_REASON, "{rawValue|ignoredControlReason}")
        .build();
    
    @Autowired
    public CbcHelperServiceImpl(VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory) {
        this.vendorSpecificSqlBuilderFactory = vendorSpecificSqlBuilderFactory;
    }
    
    @Override
    public String getFixedText(LiteYukonUser yukonUser) {
        String fixedText = DEFAULT_FIXED_TEXT;
        
        //check to see if user is in Cap Bank Display role, a.k.a. CBC_ONELINE_CAP_SETTINGS
        boolean hasRole = rolePropertyDao.checkRole(YukonRole.CBC_ONELINE_CAP_SETTINGS, yukonUser);
        
        if(hasRole) {
            fixedText = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.CAP_BANK_FIXED_TEXT, yukonUser);
        } else {
            CTILogger.warn("User cannot access CAP_BANK_FIXED_TEXT property, using default text: " + DEFAULT_FIXED_TEXT);
        }
        
        return fixedText;
    }
    
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Override
    public int getControlPointIdForCbc(int controlDeviceID) {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();

        CbcQueryHelper.appendDeviceControlPointQuery(controlDeviceID, builder);
        
        try {
            return jdbcTemplate.queryForInt(builder);
        } catch(IncorrectResultSizeDataAccessException ex) {
            throw new NotFoundException("Control point not found for control device ID " + controlDeviceID, ex);
        }
    }
    
    @Override
    public Integer findControlPointIdForCbc(int controlDeviceID) {
        try {
            return getControlPointIdForCbc(controlDeviceID);
        } catch(NotFoundException ex) {
            return null;
        }
    }
    
    @Override
    public SqlFragmentSource getOrphanSql() {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();

        CbcQueryHelper.appendOrphanQuery(builder);
        
        return builder;
    }

    @Override
    public <T> Map<T, String> getPaoTypePointFormats(PaoType paoType, List<T> points, 
            Function<T, Integer> pointIdMapper, Function<T, PointIdentifier> pointIdentifierMapper) {
        
        Multimap<T, BuiltInAttribute> pointAttributes = 
                getPaoTypePointAttributes(paoType, points, pointIdMapper, pointIdentifierMapper, cbcFormatMappings.keySet());
        
        return mapAttributesToFormats(pointAttributes, cbcFormatMappings);
    }

    private <T> Multimap<T, BuiltInAttribute> getPaoTypePointAttributes(PaoType paoType, List<T> points, 
            Function<T, Integer> pointIdMapper, Function<T, PointIdentifier> pointIdentifierMapper, Set<BuiltInAttribute> possibleMatches) {

        // First, get the paoDefinition attribute mappings
    	Multimap<T, BuiltInAttribute> attributeMappings = 
                points.stream()
                        .collect(StreamUtils.mappedValueToMultimap(
                                Function.identity(), 
                                e -> attributeService.findAttributesForPoint(
                                            PaoTypePointIdentifier.of(paoType, pointIdentifierMapper.apply(e)), 
                                            possibleMatches)));

        //  if the paotype supports attribute mapping, look up its overrides, if any
        if (paoDefinitionDao.isAttributeMappingConfigurationType(paoType)) {

            ChunkingMappedSqlTemplate mappedSqlTemplate = new ChunkingMappedSqlTemplate(jdbcTemplate);
            
            Multimap<T, String> overrides = 
                    mappedSqlTemplate.multimappedQuery(
                        sublist -> {
                            VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
                            CbcQueryHelper.appendAttributeMappingQuery(possibleMatches, sublist, builder);
                            return builder;
                        }, 
                        points, 
                        rs -> Maps.immutableEntry(rs.getInt("PointId"), rs.getString("Attribute")), 
                        p -> pointIdMapper.apply(p));  //  create a Google Function out of a Java Function

            //  Add the entries to the end of any existing entries
            attributeMappings.putAll(Multimaps.transformValues(overrides, BuiltInAttribute::valueOf));
        }
        
        return attributeMappings;
    }

    private <T> Map<T, String> mapAttributesToFormats(Multimap<T, BuiltInAttribute> pointAttributes, 
            Map<BuiltInAttribute, String> formatMappings) {
        //  Transform the attribute mappings into format mappings  
        Multimap<T, String> formatEntries = 
                Multimaps.transformValues(pointAttributes, formatMappings::get);
        
        return formatEntries.entries().stream()
                .filter(e -> e.getValue() != null)  //  filter out anything without a format mapping (shouldn't happen, but safety first)
                .collect(Collectors.toMap(
                        Entry::getKey, 
                        Entry::getValue,
                        //  collisions may happen if a remapped point overlaps a paoDefinition offset, so take the latter/override format.                        
                        (first, second) -> {   
                            if (!first.equals(second)) {
                                logger.debug("Point format override: " + first + ", " + second);       
                            } 
                            return second;
                        })); 
    }
}
