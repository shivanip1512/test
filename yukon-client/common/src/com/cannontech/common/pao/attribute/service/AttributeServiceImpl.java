package com.cannontech.common.pao.attribute.service;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;


public class AttributeServiceImpl implements AttributeService {
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointService pointService;
    @Autowired private PointCreationService pointCreationService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private ObjectFormattingService objectFormattingService;

    private Logger log = YukonLogManager.getLogger(AttributeServiceImpl.class);
    
    private Set<Attribute> readableAttributes;
    {
        Set<BuiltInAttribute> nonReadableEvents =
                Sets.difference(BuiltInAttribute.getRfnEventTypes(), EnumSet.of(BuiltInAttribute.POWER_FAIL_FLAG,
                                                                                BuiltInAttribute.REVERSE_POWER_FLAG,
                                                                                BuiltInAttribute.TAMPER_FLAG,
                                                                                BuiltInAttribute.OUTAGE_STATUS));
    	EnumSet<BuiltInAttribute> readableAttributes = EnumSet.noneOf(BuiltInAttribute.class);
    	for (BuiltInAttribute attribute : BuiltInAttribute.values()) {
    	    // Exclude profile attributes and event attributes that are not readable
    		if (!attribute.isProfile() && !nonReadableEvents.contains(attribute)) {
    		    readableAttributes.add(attribute);
    		}
    	}
    	// could consider other factors and handle user defined attributes in the future
    	this.readableAttributes = ImmutableSet.<Attribute>copyOf(readableAttributes);
    }

    public LitePoint getPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        try {
            PaoPointIdentifier paoPointIdentifier = getPaoPointIdentifierForAttribute(pao, attribute);

            LitePoint litePoint = pointService.getPointForPao(paoPointIdentifier);
        
            return litePoint;
        } catch (NotFoundException nfe) {
            throw new IllegalUseOfAttribute("Illegal use of attribute (no point): " + attribute.getDescription());
        }
    }

    @Override
    public PaoPointIdentifier getPaoPointIdentifierForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute);
        return attributeDefinition.getPointIdentifier(pao);
    }

    @Override
    public List<PaoMultiPointIdentifier> findPaoMultiPointIdentifiersForAttributes(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes) {
        return getPaoMultiPointIdentifiersForAttributes(devices,attributes,false);
    }
    
    @Override
    public List<PaoMultiPointIdentifier> getPaoMultiPointIdentifiersForAttributes(Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes) {
        return  getPaoMultiPointIdentifiersForAttributes(devices,attributes, true);
    }
    
    private  List<PaoMultiPointIdentifier> getPaoMultiPointIdentifiersForAttributes(Iterable<? extends YukonPao> devices,
                                                               Set<? extends Attribute> attributes, boolean throwException){
        List<PaoMultiPointIdentifier> devicesAndPoints = 
                Lists.newArrayListWithCapacity(IterableUtils.guessSize(devices));
            for (YukonPao pao : devices) {
                List<PaoPointIdentifier> points = Lists.newArrayListWithCapacity(attributes.size());
                for (Attribute attribute : attributes) {
                    try {
                        PaoPointIdentifier paoPointIdentifier = getPaoPointIdentifierForAttribute(pao, attribute);
                        points.add(paoPointIdentifier);
                    } catch (IllegalUseOfAttribute e) {
                        LogHelper.warn(log, "unable to look up values for %s on %s: %s", attribute, pao, e.toString());
                        if(throwException){
                            throw e;
                        }else{
                            continue; // This device does not support the selected attribute.
                        }
                    }
                }
                if (!points.isEmpty()) {
                    devicesAndPoints.add(new PaoMultiPointIdentifier(points));
                }
            }
            return devicesAndPoints;
        
    }
    
    @Override
    public Set<Attribute> getAvailableAttributes(YukonPao pao) {
        return getAvailableAttributes(pao.getPaoIdentifier().getPaoType());
    }
    
    @Override
    public Set<Attribute> getAvailableAttributes(PaoType paoType) {
        Set<Attribute> result = Sets.newHashSet();
        
        // first add type-based attributes
        Set<AttributeDefinition> definedAttributes = paoDefinitionDao.getDefinedAttributes(paoType);
        for (AttributeDefinition attributeDefinition : definedAttributes) {
            result.add(attributeDefinition.getAttribute());
        }
        
        return result;
    }

    public Set<Attribute> getAllExistingAttributes(YukonPao pao) {
        Set<Attribute> result = Sets.newHashSet();
        Set<Attribute> availableAttribute = this.getAvailableAttributes(pao);
        
        for (final Attribute attribute : availableAttribute) {
            try {
                getPointForAttribute(pao, attribute);
                result.add(attribute);
            } catch (IllegalUseOfAttribute ignore) { }
        }
        return result;
    }
    
    public Attribute resolveAttributeName(String name) {
        // some day this should also "lookup" user defined attributes
        return BuiltInAttribute.valueOf(name);
    }

    public boolean isAttributeSupported(YukonPao pao, Attribute attribute) {
        boolean result = getAvailableAttributes(pao).contains(attribute);
        return result;
    }

    public boolean pointExistsForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {

        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), (BuiltInAttribute) attribute);
        PaoPointIdentifier paoPointIdentifier = attributeDefinition.findActualPointIdentifier(pao);
        if (paoPointIdentifier == null) {
            return false;
        }

        return pointService.pointExistsForPao(paoPointIdentifier);
    }

    public PaoPointTemplate getPaoPointTemplateForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute);
        return attributeDefinition.getPointTemplate(pao);
    }

    @Override
    public List<SimpleDevice> getDevicesInGroupThatSupportAttribute(DeviceGroup group, Attribute attribute) {
        Multimap<PaoType, Attribute> allDefinedAttributes = paoDefinitionDao.getPaoTypeAttributesMultiMap();
        Multimap<Attribute, PaoType> dest = HashMultimap.create();
        Multimaps.invertFrom(allDefinedAttributes, dest);
        Collection<PaoType> collection = dest.get(attribute);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YPO.paobjectid, YPO.type");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject YPO ON (d.deviceid = YPO.paobjectid)");
        sql.append("WHERE YPO.type").in(collection);
        SqlFragmentSource groupSqlWhereClause = deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "YPO.paObjectId");
        sql.append("AND").appendFragment(groupSqlWhereClause);

        YukonDeviceRowMapper mapper = new YukonDeviceRowMapper();
        List<SimpleDevice> devices = yukonJdbcTemplate.query(sql, mapper);

        return devices;
    }

    public void createPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        boolean pointExists = this.pointExistsForAttribute(pao, attribute);
        if (!pointExists) {
            PaoPointTemplate paoPointTemplate = getPaoPointTemplateForAttribute(pao, attribute);
            PointBase point = pointCreationService.createPoint(paoPointTemplate.getPaoIdentifier(), paoPointTemplate.getPointTemplate());
            try {
                dbPersistentDao.performDBChange(point, TransactionType.INSERT);
            } catch (PersistenceException e) {
                // TODO this should throw a different exception
                throw new DataAccessException("Could not create point for pao: " + pao, e) {};
            }
        }
    }
    
    @Override
    public boolean isPointAttribute(PaoPointIdentifier paoPointIdentifier, Attribute attribute) {
        // the following could probably be optimized, but it is technically correct
        
        PaoIdentifier paoIdentifier = paoPointIdentifier.getPaoIdentifier();
        try {
            PaoPointIdentifier pointForAttribute = getPaoPointIdentifierForAttribute(paoIdentifier, attribute);
            boolean result = pointForAttribute.equals(paoPointIdentifier);
            return result;
        } catch (IllegalUseOfAttribute e) {
            return false;
        }
    }

    @Override
    public SqlFragmentSource getAttributeLookupSql(Attribute attribute) {
        Map<PaoType, Map<Attribute, AttributeDefinition>> definitionMap = paoDefinitionDao.getPaoAttributeAttrDefinitionMap();
        
        SetMultimap<PointIdentifier, PaoType> typesByPointIdentifier = HashMultimap.create();
        for (Entry<PaoType, Map<Attribute, AttributeDefinition>> entry : definitionMap.entrySet()) {
            AttributeDefinition attributeDefinition = entry.getValue().get(attribute);
            
            if (attributeDefinition == null) continue;
            
            PointIdentifier pointIdentifier = attributeDefinition.getPointTemplate().getPointIdentifier();
            typesByPointIdentifier.put(pointIdentifier, entry.getKey());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT pao_als.paObjectid, p_als.pointId");
        sql.append("FROM YukonPAObject pao_als");
        sql.append(  "JOIN Point p_als ON pao_als.paObjectId = p_als.paObjectId");
        
        SqlFragmentCollection orCollection = SqlFragmentCollection.newOrCollection();
        for (Entry<PointIdentifier, Collection<PaoType>> entry : typesByPointIdentifier.asMap().entrySet()) {
            SqlStatementBuilder clause1 = new SqlStatementBuilder();
            clause1.append("(");
            clause1.append("pao_als.Type").in(entry.getValue());
            clause1.append(  "AND p_als.pointType").eq_k(entry.getKey().getPointType());
            clause1.append(  "AND p_als.pointOffset").eq_k(entry.getKey().getOffset());
            clause1.append(")");
            orCollection.add(clause1);
        }
        
        if (!orCollection.isEmpty()) {
            sql.append("WHERE").appendFragment(orCollection);
        }
        
        return sql;
    }
    
    @Override
    public BuiltInAttribute findAttributeForPoint(PaoTypePointIdentifier paoTypePointIdentifier, Set<BuiltInAttribute> possibleMatches) {
        
        BuiltInAttribute attribute = paoDefinitionDao.findAttributeForPaoTypeAndPoint(paoTypePointIdentifier); 
        
        if (attribute != null && possibleMatches.contains(attribute)) return attribute;
        
        return null;
    }
    
    @Override
    public Set<Attribute> getReadableAttributes() {
    	return readableAttributes;
    }
    
    @Override
    public Map<AttributeGroup, List<BuiltInAttribute>> getGroupedAttributeMapFromCollection(
            Collection<? extends Attribute> attributes, YukonUserContext userContext) {
        
        ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> allGroupedAttributes = 
                BuiltInAttribute.getAllGroupedAttributes();

        Map<AttributeGroup, Set<BuiltInAttribute>> groupedAttributesMap = Maps.newHashMap();
        
        for (AttributeGroup attributeGroup : allGroupedAttributes.keySet()) {
            Set<BuiltInAttribute> attributesInGroup = Sets.newHashSet();
            for (BuiltInAttribute builtInAttribute : allGroupedAttributes.get(attributeGroup)) {
                if (attributes.contains(builtInAttribute)) {
                    attributesInGroup.add(builtInAttribute);
                }
            }
            if (!attributesInGroup.isEmpty()) {
                groupedAttributesMap.put(attributeGroup, attributesInGroup);
            }
        }
        return objectFormattingService.sortDisplayableValues(
                groupedAttributesMap, userContext);
    }
}
