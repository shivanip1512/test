package com.cannontech.common.pao.attribute.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
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
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public class AttributeServiceImpl implements AttributeService {
    private static final Logger log = YukonLogManager.getLogger(AttributeServiceImpl.class);

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoDefinitionService paoDefinitionService;
    @Autowired private PointCreationService pointCreationService;
    @Autowired private PointService pointService;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

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
    public LitePoint findPointForAttribute(YukonPao pao, Attribute attribute) {
        try {
            PaoPointIdentifier paoPointIdentifier = getPaoPointIdentifierForAttribute(pao, attribute);
            LitePoint litePoint = pointService.getPointForPao(paoPointIdentifier);

            return litePoint;
        } catch (NotFoundException | IllegalUseOfAttribute nfe) {
            return null;
        }
    }

    @Override
    public PaoPointIdentifier getPaoPointIdentifierForAttribute(YukonPao pao, Attribute attribute)
            throws IllegalUseOfAttribute {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition =
            paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute);
        return attributeDefinition.getPaoPointIdentifier(pao);
    }

    @Override
    public Optional<PaoPointIdentifier> findPaoPointIdentifierForAttribute(PaoIdentifier pao, BuiltInAttribute builtInAttribute) {
        return paoDefinitionDao.findAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute)
                .map(ad -> ad.getPaoPointIdentifier(pao));
    }

    @Override
    public PaoTypePointIdentifier getPaoTypePointIdentifierForAttribute(PaoType type, Attribute attribute)
            throws IllegalUseOfAttribute {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(type, builtInAttribute);
        return PaoTypePointIdentifier.of(type, attributeDefinition.getPointTemplate().getPointIdentifier());
    }

    @Override
    public PaoPointTemplate getPaoPointTemplateForAttribute(YukonPao pao, Attribute attribute)
            throws IllegalUseOfAttribute {
        BuiltInAttribute builtInAttribute = (BuiltInAttribute) attribute;
        AttributeDefinition attributeDefinition =
            paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), builtInAttribute);
        return attributeDefinition.getPointTemplate(pao);
    }

    @Override
    public PaoMultiPointIdentifierWithUnsupported findPaoMultiPointIdentifiersForAttributesWithUnsupported(
            Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes) {

        PaoMultiPointIdentifierWithUnsupported multiPointIdentifier =
            findPaoMultiPointIdentifiersForAttributes(devices, attributes, true);

        return multiPointIdentifier;
    }

    @Override
    public List<PaoMultiPointIdentifier> findPaoMultiPointIdentifiersForAttributes(
            Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes) {

        PaoMultiPointIdentifierWithUnsupported multiPointIdentifier =
            findPaoMultiPointIdentifiersForAttributes(devices, attributes, false);
        
        return multiPointIdentifier.getSupportedDevicesAndPoints();
    }

    @Override
    public Set<Attribute> getAvailableAttributes(YukonPao pao) {
        return getAvailableAttributes(pao.getPaoIdentifier().getPaoType());
    }

    @Override
    public Set<Attribute> getAvailableAttributes(PaoType paoType) {
        
        Set<Attribute> result = new HashSet<>();
        for (AttributeDefinition attributeDefinition : paoDefinitionDao.getDefinedAttributes(paoType)) {
            result.add(attributeDefinition.getAttribute());
        }
        
        return result;
    }

    @Override
    public Set<Attribute> getExistingAttributes(YukonPao pao, Set<? extends Attribute> desiredAttributes) {
        Set<Attribute> result = new HashSet<>();

        for (final Attribute attribute : desiredAttributes) {
            try {
                getPointForAttribute(pao, attribute);
                result.add(attribute);
            } catch (IllegalUseOfAttribute ignore) {
                // Skip attributes that don't match.
            }
        }
        return result;
    }

    @Override
    public Attribute resolveAttributeName(String name) {
        // some day this should also "lookup" user defined attributes
        return BuiltInAttribute.valueOf(StringEscapeUtils.escapeXml11(name));
    }

    @Override
    public boolean isAttributeSupported(YukonPao pao, Attribute attribute) {
        boolean result = getAvailableAttributes(pao).contains(attribute);
        return result;
    }

    @Override
    public boolean pointExistsForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {

        AttributeDefinition attributeDefinition =
            paoDefinitionDao.getAttributeLookup(pao.getPaoIdentifier().getPaoType(), (BuiltInAttribute) attribute);
        PaoPointIdentifier paoPointIdentifier = attributeDefinition.findActualPointIdentifier(pao);
        if (paoPointIdentifier == null) {
            return false;
        }

        return pointService.pointExistsForPao(paoPointIdentifier);
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
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "YPO.paObjectId");
        sql.append("AND").appendFragment(groupSqlWhereClause);

        YukonDeviceRowMapper mapper = new YukonDeviceRowMapper();
        List<SimpleDevice> devices = jdbcTemplate.query(sql, mapper);

        return devices;
    }

    @Override
    public boolean createPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        boolean pointExists = this.pointExistsForAttribute(pao, attribute);
        if (!pointExists) {
            log.debug("Creating point for attribute (" + attribute + ") on pao: " + pao);
            PaoPointTemplate paoPointTemplate = getPaoPointTemplateForAttribute(pao, attribute);
            PointBase point =
                pointCreationService.createPoint(paoPointTemplate.getPaoIdentifier(),
                    paoPointTemplate.getPointTemplate());
            try {
                dbPersistentDao.performDBChange(point, TransactionType.INSERT);
                return true;
            } catch (PersistenceException e) {
                // TODO this should throw a different exception
                throw new DataAccessException("Could not create point for pao: " + pao, e) {
                };
            }
        }
        return false;
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
    public Set<BuiltInAttribute> findAttributesForPoint(PaoTypePointIdentifier paoTypePointIdentifier,
            Set<? extends Attribute> possibleMatches) {
        Set<BuiltInAttribute> attributes = paoDefinitionDao.findAttributeForPaoTypeAndPoint(paoTypePointIdentifier);
        Set<BuiltInAttribute> intersection = new HashSet<>(attributes);
        intersection.retainAll(possibleMatches);
        return intersection;
    }

    @Override
    public Set<Attribute> getReadableAttributes() {
        return ImmutableSet.<Attribute> copyOf(BuiltInAttribute.getReadableAttributes());
    }

    @Override
    public Set<Attribute> getAdvancedReadableAttributes() {
        return ImmutableSet.<Attribute> copyOf(BuiltInAttribute.getAdvancedReadableAttributes());
    }

    @Override
    public SortedMap<BuiltInAttribute, String> resolveAllToString(Set<BuiltInAttribute> bins,
            final YukonUserContext context) {
        SortedMap<BuiltInAttribute, String> sortedAttributes =
            new TreeMap<>(getNameComparator(context));
        for (BuiltInAttribute bin : bins) {
            sortedAttributes.put(bin, objectFormattingService.formatObjectAsString(bin, context));
        }

        return sortedAttributes;
    }

    @Override
    public Map<AttributeGroup, List<BuiltInAttribute>> getGroupedAttributeMapFromCollection(
            Collection<? extends Attribute> attributes, YukonUserContext userContext) {
        Map<AttributeGroup, Set<BuiltInAttribute>> allGroupedAttributes = BuiltInAttribute.getAllGroupedAttributes();
        Map<AttributeGroup, Set<BuiltInAttribute>> groupedAttributesMap = new HashMap<>();

        for (AttributeGroup attributeGroup : allGroupedAttributes.keySet()) {
            Set<BuiltInAttribute> attributesInGroup = new HashSet<>();
            for (BuiltInAttribute builtInAttribute : allGroupedAttributes.get(attributeGroup)) {
                if (attributes.contains(builtInAttribute)) {
                    attributesInGroup.add(builtInAttribute);
                }
            }
            if (!attributesInGroup.isEmpty()) {
                groupedAttributesMap.put(attributeGroup, attributesInGroup);
            }
        }
        return objectFormattingService.sortDisplayableValues(groupedAttributesMap, userContext);
    }

    @Override
    public Comparator<Attribute> getNameComparator(final YukonUserContext context) {

        Ordering<Attribute> descriptionOrdering = Ordering.natural().onResultOf(new Function<Attribute, String>() {
            @Override
            public String apply(Attribute from) {
                return objectFormattingService.formatObjectAsString(from.getMessage(), context);
            }
        });
        return descriptionOrdering;

    }

    @Override
    public List<Integer> getPointIds(Iterable<SimpleDevice> devices, BuiltInAttribute attribute) {
        ChunkingSqlTemplate chunkyTemplate = new ChunkingSqlTemplate(jdbcTemplate);

        // get the points that match the attribute for these devices
        List<Integer> pointIds = new ArrayList<>();

        // Look up point ids by pao type, sink into pointIds list
        // About 6x faster than using getPointForAttribute(device, attribute) for each device
        ListMultimap<PaoType, SimpleDevice> typeToDevices = ArrayListMultimap.create();
        for (SimpleDevice device : devices) {
            typeToDevices.put(device.getDeviceType(), device);
        }
        for (PaoType type : typeToDevices.keySet()) {

            try {
                final PointIdentifier pi =
                    paoDefinitionDao.getAttributeLookup(type, attribute).getPointTemplate().getPointIdentifier();

                SqlFragmentGenerator<Integer> generator = new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append("select PointId");
                        sql.append("from Point");
                        sql.append("where PointType").eq(pi.getPointType());
                        sql.append("and PointOffset").eq(pi.getOffset());
                        sql.append("and PAObjectId").in(subList);
                        return sql;
                    }
                };
                chunkyTemplate.queryInto(generator, PaoUtils.asPaoIdList(typeToDevices.get(type)), TypeRowMapper.INTEGER,
                    pointIds);
            } catch (IllegalUseOfAttribute e) {
                // Ignore pao types that don't support this attribute
            }
        }

        return pointIds;
    }

    @Override
    public BiMap<PaoIdentifier, LitePoint> getPoints(Iterable<? extends YukonPao> devices, BuiltInAttribute attribute) {
        ChunkingSqlTemplate chunkyTemplate = new ChunkingSqlTemplate(jdbcTemplate);

        // get the points that match the attribute for these devices
        final BiMap<PaoIdentifier, LitePoint> points = HashBiMap.create();

        // Look up point ids by pao type, sink into pointIds list
        // About 6x faster than using getPointForAttribute(device, attribute) for each device
        ListMultimap<PaoType, YukonPao> typeToDevices = ArrayListMultimap.create();
        for (YukonPao pao: devices) {
            typeToDevices.put(pao.getPaoIdentifier().getPaoType(), pao);
        }
        for (PaoType type : typeToDevices.keySet()) {

            try {
                final PointIdentifier pi =
                    paoDefinitionDao.getAttributeLookup(type, attribute).getPointTemplate().getPointIdentifier();

                SqlFragmentGenerator<Integer> generator = new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        SqlStatementBuilder sql = new SqlStatementBuilder();
                        sql.append(PointDao.LITE_POINT_WITH_YUKONPAOBJECT_ROW_MAPPER.getBaseQuery());
                        sql.append("WHERE PointType").eq(pi.getPointType());
                        sql.append("AND PointOffset").eq(pi.getOffset());
                        sql.append("AND pao.PaobjectId").in(subList);

                        return sql;
                    }
                };
                chunkyTemplate.query(generator, PaoUtils.asPaoIdList(typeToDevices.get(type)),
                    new YukonRowCallbackHandler() {
                        @Override
                        public void processRow(YukonResultSet rs) throws SQLException {
                            PaoIdentifier pao = rs.getPaoIdentifier("PaobjectId", "Type");
                            LitePoint litePoint = PointDao.LITE_POINT_WITH_YUKONPAOBJECT_ROW_MAPPER.mapRow(rs);
                            points.put(pao, litePoint);
                        }
                    });
            } catch (IllegalUseOfAttribute e) {
                // Ignore pao types that don't support this attribute
            }
        }

        return points;
    }

    @Override
    public List<LiteStateGroup> findStateGroups(List<SimpleDevice> devices, BuiltInAttribute attribute) {
        ChunkingSqlTemplate chunkyTemplate = new ChunkingSqlTemplate(jdbcTemplate);

        // get the points that match the attribute for these devices
        List<Integer> pointIds = getPointIds(devices, attribute);

        // get the distinct list of stategroups for these points
        Set<Integer> groupIds = new HashSet<>();
        SqlFragmentGenerator<Integer> generator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select distinct StateGroupId");
                sql.append("from Point");
                sql.append("where PointId").in(subList);
                return sql;
            }
        };
        chunkyTemplate.queryInto(generator, pointIds, TypeRowMapper.INTEGER, groupIds);

        List<LiteStateGroup> groups = new ArrayList<>();
        for (Integer groupId : groupIds) {
            groups.add(stateGroupDao.getStateGroup(groupId));
        }

        return groups;
    }

    @Override
    public List<LiteStateGroup> findStateGroups(String groupName, BuiltInAttribute attribute) {
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
            if (attr == attribute && !attributes.contains(attr)) {
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

        // create map of unique pointIdentifier to set of paoTypes; creates the smallest set of OR clauses for filtering
        SetMultimap<PointIdentifier, PaoType> pointIdToPaoTypes = HashMultimap.create();
        for (PaoType paoType : actualPaoTypes) {
            for (Attribute attr : attributes) {
                AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(paoType, attr);
                PointIdentifier ptId = attributeDefinition.getPointTemplate().getPointIdentifier();
                pointIdToPaoTypes.put(ptId, paoType);
            }
        }

        // get the state group ID's
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT P.stategroupid AS stategroupid");
        sql.append("FROM Point P");
        sql.append("JOIN YukonPAObject YPO on P.PAObjectID = YPO.PAObjectID");

        // add the monitored group filter
        sql.append("WHERE").appendFragment(groupSqlWhereClause);

        // add unique pointIdentifier to paoTypes        
        SqlFragmentCollection orCollection = SqlFragmentCollection.newOrCollection();
        for (Entry<PointIdentifier, Collection<PaoType>> entry : pointIdToPaoTypes.asMap().entrySet()) {
            SqlStatementBuilder clause = new SqlStatementBuilder();
            clause.append("(");
            clause.append("P.POINTOFFSET").eq_k(entry.getKey().getOffset());
            clause.append("AND P.POINTTYPE").eq_k(entry.getKey().getPointType());
            clause.append("AND YPO.type").in(entry.getValue());
            clause.append(")");
            orCollection.add(clause);
        }
        
        if (!orCollection.isEmpty()) {
            sql.append("AND").appendFragment(orCollection);
        }

        List<LiteStateGroup> allStateGroups = stateGroupDao.getAllStateGroups();
        final Map<Integer, LiteStateGroup> stateGroupsById =
            Maps.uniqueIndex(allStateGroups, new Function<LiteStateGroup, Integer>() {
                @Override
                public Integer apply(LiteStateGroup stateGroup) {
                    return stateGroup.getStateGroupID();
                }
            });
        List<LiteStateGroup> results = jdbcTemplate.query(sql, new YukonRowMapper<LiteStateGroup>() {
            @Override
            public LiteStateGroup mapRow(YukonResultSet rs) throws SQLException {
                return stateGroupsById.get(rs.getInt("stateGroupId"));
            }
        });
        return results;
    }

    @Override
    public List<PointIdentifier> findPointsForDevicesAndAttribute(Iterable<? extends YukonPao> devices,
            Attribute attribute) {
        Map<PaoType, YukonPao> typeToDevice = new HashMap<>();
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
            final PaoPointIdentifier ppi = attrDef.getPaoPointIdentifier(device); // throws ???
            if (!pis.contains(ppi.getPointIdentifier())) {
                pis.add(ppi.getPointIdentifier());
            }
        }

        return pis;
    }
    
    /**
     * Returns PaoMultiPointIdentifierWithUnsupported which contains supported devices and points and
     * unsupported devices.
     */
    private PaoMultiPointIdentifierWithUnsupported findPaoMultiPointIdentifiersForAttributes(
            Iterable<? extends YukonPao> devices, Set<? extends Attribute> attributes, boolean addUnsupported) {

        if (log.isDebugEnabled()) {
            log.debug("START:findPaoMultiPointIdentifiersForAttributesWithUnsupported Attributes:" + attributes
                + " Devices:" + devices);
        }

        Set<YukonPao> devicesWithoutPoint = new HashSet<>();
        List<PaoMultiPointIdentifier> devicesAndPoints = new ArrayList<>();
        Multimap<PaoType, YukonPao> typeToDevice = ArrayListMultimap.create();
        for (YukonPao pao : devices) {
            typeToDevice.put(pao.getPaoIdentifier().getPaoType(), pao);
        }

        Multimap<YukonPao, PaoPointIdentifier> paoToPoint = ArrayListMultimap.create();

        for (PaoType type : typeToDevice.keySet()) {
            for (Attribute attribute : attributes) {
                try {
                    AttributeDefinition attributeDefinition = paoDefinitionDao.getAttributeLookup(type, attribute);
                    for (YukonPao pao : typeToDevice.get(type)) {
                        PaoPointIdentifier paoPointIdentifier = attributeDefinition.getPaoPointIdentifier(pao);
                        if (paoPointIdentifier != null) {
                            paoToPoint.put(pao, paoPointIdentifier);
                        }
                    }
                } catch (IllegalUseOfAttribute e) {
                    if (addUnsupported) {
                        for (YukonPao pao : typeToDevice.get(type)) {
                            // meters that do not support the attribute 
                            devicesWithoutPoint.add(pao);
                        }
                    }
                }
            }
        }

        for (YukonPao pao : paoToPoint.keySet()) {
            Collection<PaoPointIdentifier> points = paoToPoint.get(pao);
            if (!points.isEmpty()) {
                devicesAndPoints.add(new PaoMultiPointIdentifier(paoToPoint.get(pao)));
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("END:findPaoMultiPointIdentifiersForAttributesWithUnsupported");
        }

        return new PaoMultiPointIdentifierWithUnsupported(devicesAndPoints, devicesWithoutPoint);
    }
    
    @Override
    public Multimap<BuiltInAttribute, SimpleDevice> getDevicesInGroupThatSupportAttribute(DeviceGroup group,
            List<BuiltInAttribute> attributes, List<Integer> deviceIds) {

        Multimap<BuiltInAttribute, PaoType> attributeToPaoType = HashMultimap.create();
        for (Entry<PaoType, Attribute> entry : paoDefinitionDao.getPaoTypeAttributesMultiMap().entries()) {
            if (attributes.contains(entry.getValue())) {
                attributeToPaoType.put((BuiltInAttribute) entry.getValue(), entry.getKey());
            }
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YPO.paobjectid, YPO.type");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject YPO ON (d.deviceid = YPO.paobjectid)");
        sql.append("WHERE YPO.type").in(Sets.newHashSet(attributeToPaoType.values()));
        sql.append("AND YPO.Category").eq_k(PaoCategory.DEVICE);
        if (deviceIds != null) {
            sql.append("AND YPO.paobjectid").in(deviceIds);
        }
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "YPO.paObjectId");
        sql.append("AND").appendFragment(groupSqlWhereClause);

        YukonDeviceRowMapper mapper = new YukonDeviceRowMapper();

        List<SimpleDevice> devices = jdbcTemplate.query(sql, mapper);

        Multimap<BuiltInAttribute, SimpleDevice> attributeToDevice = HashMultimap.create();
        attributes.forEach(attribute -> {
            Collection<PaoType> types = attributeToPaoType.get(attribute);
            List<SimpleDevice> devicesForAttribute =
                devices.stream().filter(d -> types.contains(d.getDeviceType())).collect(Collectors.toList());
            attributeToDevice.putAll(attribute, devicesForAttribute);
        });

        return attributeToDevice;
    }

    @Override
    public LitePoint createAndFindPointForAttribute(YukonPao pao, BuiltInAttribute attribute) {
        createPointForAttribute(pao, attribute);
        return findPointForAttribute(pao, attribute);
    }
}
