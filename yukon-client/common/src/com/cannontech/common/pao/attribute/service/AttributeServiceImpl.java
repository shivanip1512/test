package com.cannontech.common.pao.attribute.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

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
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;


public class AttributeServiceImpl implements AttributeService {
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoDefinitionService paoDefinitionService;
    @Autowired private PointService pointService;
    @Autowired private PointCreationService pointCreationService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private StateDao stateDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;

    private Logger log = YukonLogManager.getLogger(AttributeServiceImpl.class);
    
    private Set<Attribute> readableAttributes;
    {
        Iterable<BuiltInAttribute> readableAttributes = Iterables.filter(Arrays.asList(BuiltInAttribute.values()), new Predicate<BuiltInAttribute>() {
            @Override
            public boolean apply(BuiltInAttribute attribute) {
                // Exclude profile attributes and event attributes that are not readable
                return !attribute.isProfile() && !attribute.isRfnNonReadableEvent();
            }
        });
        
        // could consider other factors and handle user defined attributes in the future
        this.readableAttributes = ImmutableSet.<Attribute>copyOf(readableAttributes);
    }
    
    private Set<Attribute> advancedReadableAttributes;
    {
        Iterable<BuiltInAttribute> advancedReadableAttributes = Iterables.filter(Arrays.asList(BuiltInAttribute.values()), new Predicate<BuiltInAttribute>() {
            @Override
            public boolean apply(BuiltInAttribute attribute) {
                // Exclude event attributes that are not readable
                return !attribute.isRfnNonReadableEvent() ;
            }
        });
        
        // could consider other factors and handle user defined attributes in the future
        this.advancedReadableAttributes = ImmutableSet.<Attribute>copyOf(advancedReadableAttributes);
    }

    @Override
    public LitePoint getPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        try {
            PaoPointIdentifier paoPointIdentifier = getPaoPointIdentifierForAttribute(pao, attribute);

            LitePoint litePoint = pointService.getPointForPao(paoPointIdentifier);
        
            return litePoint;
        } catch (NotFoundException nfe) {
            throw new IllegalUseOfAttribute("Illegal use of attribute (no point): " + attribute);
        }
    }

    @Override
    public PaoPointIdentifier getPaoPointIdentifierForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute);
        return attributeDefinition.getPointIdentifier(pao);
    }
    
    @Override
    public PaoTypePointIdentifier getPaoTypePointIdentifierForAttribute(PaoType type, Attribute attribute) 
    throws IllegalUseOfAttribute {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(type, builtInAttribute);
        return PaoTypePointIdentifier.of(type, attributeDefinition.getPointTemplate().getPointIdentifier());
    }
    
    @Override
    public PaoPointTemplate getPaoPointTemplateForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute);
        return attributeDefinition.getPointTemplate(pao);
    }

    @Override
    public PaoMultiPointIdentifierWithUnsupported findPaoMultiPointIdentifiersForAttributes(
            Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes) {

        return getPaoMultiPointIdentifiersForAttributesHelper(devices, attributes, false);
    }

    @Override
    public PaoMultiPointIdentifierWithUnsupported getPaoMultiPointIdentifiersForAttributes(
            Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes) {

        return getPaoMultiPointIdentifiersForAttributesHelper(devices, attributes, true);
    }
    
    private  PaoMultiPointIdentifierWithUnsupported getPaoMultiPointIdentifiersForAttributesHelper(
            Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes, boolean throwException) {

        PaoMultiPointIdentifierWithUnsupported devicesAndPoints = new PaoMultiPointIdentifierWithUnsupported();
        
        for (YukonPao pao : devices) {
            List<PaoPointIdentifier> points = new ArrayList<>(attributes.size());
            for (Attribute attribute : attributes) {
                try {
                    points.add(getPaoPointIdentifierForAttribute(pao, attribute));
                } catch (IllegalUseOfAttribute e) {
                    devicesAndPoints.addUnsupportedDevice(pao, attribute);
                    if (throwException) {
                        log.error("Unable to look up values for " + attribute + " on " + pao);
                        throw e;
                    }
                    LogHelper.debug(log, "unable to look up values for %s on %s: %s", attribute, pao, e.toString());
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

    @Override
    public Set<Attribute> getExistingAttributes(YukonPao pao, Set<? extends Attribute> desiredAttributes) {
        Set<Attribute> result = Sets.newHashSet();
        
        for (final Attribute attribute : desiredAttributes) {
            try {
                getPointForAttribute(pao, attribute);
                result.add(attribute);
            } catch (IllegalUseOfAttribute ignore) { }
        }
        return result;
    }
    
    @Override
    public Attribute resolveAttributeName(String name) {
        // some day this should also "lookup" user defined attributes
        return BuiltInAttribute.valueOf(name);
    }

    @Override
    public boolean isAttributeSupported(YukonPao pao, Attribute attribute) {
        boolean result = getAvailableAttributes(pao).contains(attribute);
        return result;
    }

    @Override
    public boolean pointExistsForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {

        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), (BuiltInAttribute) attribute);
        PaoPointIdentifier paoPointIdentifier = attributeDefinition.findActualPointIdentifier(pao);
        if (paoPointIdentifier == null) {
            return false;
        }

        return pointService.pointExistsForPao(paoPointIdentifier);
    }

    @Override
    public Set<SimpleDevice> getDevicesInGroupThatSupportAnyAttributes(DeviceGroup group, Set<? extends Attribute> attributes) {
        Set<SimpleDevice> allSupportedDevices = Sets.newHashSet();
        for (Attribute attribute: attributes) {
            List<SimpleDevice> supportedDevices = getDevicesInGroupThatSupportAttribute(group, attribute);
            allSupportedDevices.addAll(supportedDevices);
        }
        return allSupportedDevices;
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

    @Override
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
    public Set<BuiltInAttribute> findAttributesForPoint(PaoTypePointIdentifier paoTypePointIdentifier, Set<? extends Attribute> possibleMatches) {
        
        Set<BuiltInAttribute> attributes = paoDefinitionDao.findAttributeForPaoTypeAndPoint(paoTypePointIdentifier); 
        Set<BuiltInAttribute> intersection = Sets.newHashSet(attributes);
        intersection.retainAll(possibleMatches);
        return intersection;
    }
    
    @Override
    public Set<Attribute> getReadableAttributes() {
    	return readableAttributes;
    }
    
    @Override
    public Set<Attribute> getAdvancedReadableAttributes() {
        return advancedReadableAttributes;
    }
    
    @Override
    public SortedMap<BuiltInAttribute, String> resolveAllToString(Set<BuiltInAttribute> bins, final YukonUserContext context) {
        
        SortedMap<BuiltInAttribute, String> sortedAttributes = new TreeMap<BuiltInAttribute, String>(getNameComparator(context));
        for (BuiltInAttribute bin : bins) {
            sortedAttributes.put(bin, objectFormattingService.formatObjectAsString(bin, context));
        }
        
        return sortedAttributes;
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

    @Override
    public Comparator<Attribute> getNameComparator(final YukonUserContext context) {

        Ordering<Attribute> descriptionOrdering = Ordering.natural()
                .onResultOf(new Function<Attribute, String>() {
                    @Override
                    public String apply(Attribute from) {
                        return objectFormattingService.formatObjectAsString(from.getMessage(), context);
                    }
                });
        return descriptionOrdering;

    }

    /**
     * First time we need to get the State Groups for a particular Device Group
     * + Attribute so that we can display only the appropriate options on the
     * UI.
     * 
     * @category    YUK-11992
     * @since       5.6.4
     * 
     * @param groupName         String      Must be findable in the database, eg. "/Group1 Meters"
     * @param attributeKey      String      Should be exactly from the java constant, eg. BuiltInAttribute.USAGE.getKey()
     * @return                  List<LiteStateGroup>
     */
    @Override
    public List<LiteStateGroup> findListOfStateGroupsForDeviceGroupAndAttributeKey(String groupName,
                                                                                   String attributeKey) {
        DeviceGroup group = deviceGroupService.findGroupName(groupName);
        if (group == null) {
            return new ArrayList<>(0);
        }
        Multimap<PaoType, Attribute> allDefinedAttributes = paoDefinitionDao.getPaoTypeAttributesMultiMap();
        Multimap<Attribute, PaoType> dest = HashMultimap.create();
        Collection<PaoType> possiblePaoTypes = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();
        Map<Attribute, Collection<PaoType>> actualAttrPaos = new HashMap<>();

        Multimaps.invertFrom(allDefinedAttributes, dest);
        for (Attribute attr : dest.keySet()) {
            if (attr.getKey().equals(attributeKey) && !attributes.contains(attr)) {
                attributes.add(attr);
                Collection<PaoType> coll = dest.get(attr);
                possiblePaoTypes.addAll(coll);
                actualAttrPaos.put(attr, coll);
            }
        }

        // Get list of possible PAO types in the group we are using.
        // This will reduce the number of queries later.
        Collection<PaoType> actualPaoTypes = paoDefinitionService.findListOfPaoTypesInGroup(group, possiblePaoTypes);

        // Now: remove all PaoTypes not in that group, and all Attributes ending with no Pao types.
        for (Attribute attr : actualAttrPaos.keySet()) {
            actualAttrPaos.get(attr).retainAll(actualPaoTypes);
            if (actualAttrPaos.get(attr).isEmpty()) {
                attributes.remove(attr);
            }
        }

        if (attributes.isEmpty()) {
            return Collections.emptyList();
        }

        SqlFragmentSource groupSqlWhereClause =
                deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "YPO.paObjectId");

        // get the state group ID's
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT P.stategroupid AS stategroupid");
        sql.append("FROM Point P");
        sql.append("JOIN YukonPAObject YPO on P.PAObjectID = YPO.PAObjectID");

        // Find the point per pao type/attribute combination
        sql.append("and (");
        boolean addOr = false;
        for (PaoType paoType : actualPaoTypes) {
            if (!addOr) {
                addOr = true;
            } else {
                sql.append(" OR ");
            }
            sql.append("((");

            int countAttributes = 0;
            for (Attribute attr : attributes) {
                final PaoTypePointIdentifier ptId = this.getPaoTypePointIdentifierForAttribute(paoType, attr);

                if (countAttributes > 0) {
                    sql.append(" OR ");
                }
                if (attributes.size() > 1) {
                    sql.append("(");
                }
                sql.append("P.POINTOFFSET").eq_k(ptId.getPointIdentifier().getOffset());
                sql.append("and P.POINTTYPE").eq_k(ptId.getPointIdentifier().getPointType());
                if (attributes.size() > 1) {
                    sql.append(")");
                }
            }

            // Filter all devices of a particular type from a particular group.
            sql.append(") and YPO.PAObjectID in (");
            sql.append("SELECT YPO.paobjectid");
            sql.append("FROM Device D");
            sql.append("JOIN YukonPaObject YPO ON (D.deviceid = YPO.paobjectid)");
            sql.append("WHERE YPO.type").eq(paoType);
            sql.append("AND").appendFragment(groupSqlWhereClause);
            sql.append("))");
        }
        sql.append(")");

        LiteStateGroup[] allStateGroups = stateDao.getAllStateGroups();
        final Map<Integer, LiteStateGroup> stateGroupsById =
            Maps.uniqueIndex(Arrays.asList(allStateGroups), new Function<LiteStateGroup, Integer>() {
            @Override
            public Integer apply(LiteStateGroup stateGroup) {
                return stateGroup.getStateGroupID();
            }
        });
        List<LiteStateGroup> results = yukonJdbcTemplate.query(sql, new YukonRowMapper<LiteStateGroup>() {
            @Override
            public LiteStateGroup mapRow(YukonResultSet rs) throws SQLException {
                return stateGroupsById.get(rs.getInt("stateGroupId"));
            }
        });
        return results;
    } // ENDS findListOfStateGroupsForDeviceGroupAndAttributeKey

    @Override
    public List<PointIdentifier> findPointsForDevicesAndAttribute(
            Iterable<? extends YukonPao> devices, Attribute attribute) {

        Map<PaoType, YukonPao> typeToDevice = new HashMap<PaoType, YukonPao>();
        for (YukonPao device : devices) {
            final PaoType theType = device.getPaoIdentifier().getPaoType();
            if (!typeToDevice.keySet().contains(theType)) {
                typeToDevice.put(theType, device);
            }
        }
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        List<PointIdentifier> pis = new ArrayList<>();
        for (PaoType type : typeToDevice.keySet()) {
            AttributeDefinition attrDef = paoDefinitionDao.getAttributeLookup(type, builtInAttribute);
            YukonPao device = typeToDevice.get(type);
            final PaoPointIdentifier ppi    = attrDef.getPointIdentifier(device); // throws ???
            if (!pis.contains(ppi.getPointIdentifier())) {
                pis.add(ppi.getPointIdentifier());
            }
        }

        return pis;
    }
}
