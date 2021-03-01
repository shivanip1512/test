package com.cannontech.web.common.pao.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class YukonPointHelperImpl implements YukonPointHelper {

    @Autowired private PointDao pointDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private AttributeService attributeService;

    @Override
    public List<LiteYukonPoint> getYukonPoints(final YukonPao pao, SortingParameters sorting,
            final MessageSourceAccessor accessor) {
        
        List<LiteYukonPoint> liteYukonPoints = getYukonPointsForSorting(pao, accessor);
        PointSortField sortField = PointSortField.valueOf(sorting.getSort());
        if (sortField != PointSortField.ATTRIBUTE) {
            Collections.sort(liteYukonPoints, LiteYukonPoint.getComparatorForSortField(sortField));
        } else {
            Collections.sort(liteYukonPoints, new Comparator<LiteYukonPoint>() {
                @Override
                public int compare(LiteYukonPoint o1, LiteYukonPoint o2) {
                    if (o1.getAttribute() == o2.getAttribute()) {
                        return 0;
                    } else if (o1.getAttribute() == null) {
                        return 1;
                    } else if (o2.getAttribute() == null) {
                        return -1;
                    } else {
                        return accessor.getMessage(o1.getAttribute()).compareToIgnoreCase(
                            accessor.getMessage(o2.getAttribute()));
                    }
                }
            });
        }
        if (sorting.getDirection() == Direction.desc) {
            liteYukonPoints = Lists.reverse(liteYukonPoints);
        }
        return liteYukonPoints;
    }
    
    /**
     * Method to get list of YukonPoints for sorting
     */
    private List<LiteYukonPoint> getYukonPointsForSorting(YukonPao pao, MessageSourceAccessor accessor) {
        List<LitePoint> points = pointDao.getLitePointsByPaObjectId(pao.getPaoIdentifier().getPaoId());
        return points.stream().map(point -> {
            PaoPointIdentifier paoPointIdent = new PaoPointIdentifier(pao.getPaoIdentifier(),
                    new PointIdentifier(point.getPointTypeEnum(), point.getPointOffset()));
            
            Set<BuiltInAttribute> builtInAttributes = paoDefinitionDao
                    .findAttributeForPaoTypeAndPoint(paoPointIdent.getPaoTypePointIdentifier());
            List<CustomAttribute> customAttributes = attributeService
                    .findCustomAttributesForPaoTypeAndPoint(paoPointIdent.getPaoTypePointIdentifier());
            
            List<Attribute> attributes = getSortedAttributes(builtInAttributes, customAttributes, accessor); 

            Attribute attribute = getFirstAttribute(pao.getPaoIdentifier().getPaoType(), accessor,
                                                    builtInAttributes, customAttributes, attributes);
            return LiteYukonPoint.of(paoPointIdent, attribute, attributes, point.getPointName(), point.getLiteID());
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<Attribute> getSortedAttributes(Set<BuiltInAttribute> builtInAttributes, List<CustomAttribute> customAttributes,
                                               MessageSourceAccessor accessor) {
        List<Attribute> attributes = new ArrayList<>();
        attributes.addAll(builtInAttributes);
        attributes.addAll(customAttributes);
        //sort in alphabetical order
        attributes.sort((Attribute a1, Attribute a2) -> accessor.getMessage(a1).compareToIgnoreCase(
                accessor.getMessage(a2)));
        return attributes;
    }

    @Override
    public Attribute getFirstAttribute(PaoType paoType, MessageSourceAccessor accessor,
            Set<BuiltInAttribute> buildInAttributes, List<CustomAttribute> customAttributes, List<Attribute> attributes) {
        Attribute attribute = null;
        if (attributes.size() == 1) {   // exactly one attribute
            attribute = attributes.get(0);
        } else if (!customAttributes.isEmpty()) {    // at least one custom attribute
            // sort the custom attributes, then pick the first one
            customAttributes.sort((Attribute a1, Attribute a2) -> accessor.getMessage(a1).compareToIgnoreCase(accessor.getMessage(a2)));
            attribute = customAttributes.get(0);
         } else if (!buildInAttributes.isEmpty()) {   // at least one built in attribute
             // pick the first one from the xml file
             // <pointInfo name="Delivered kWh" init="true" attributes="USAGE,DELIVERED_KWH"/> - will return USAGE
             Multimap<PaoType, Attribute> definedAttributes = paoDefinitionDao.getPaoTypeAttributesMultiMap();
             attribute = definedAttributes.get(paoType)
                    .stream().filter(attr -> buildInAttributes.contains((BuiltInAttribute)attr))
                    .findFirst()
                    .orElse(null);
        }
        return attribute;
    }
    
    @Override
    public List<LiteYukonPoint> getYukonPoints(final YukonPao pao, MessageSourceAccessor accessor) {
        List<LiteYukonPoint> liteYukonPoints = getYukonPointsForSorting(pao, accessor);
        Collections.sort(liteYukonPoints, LiteYukonPoint.getComparatorForSortField(PointSortField.POINTNAME));
        return liteYukonPoints;
    }

    @Override
    public void verifyRoles(LiteYukonUser user, HierarchyPermissionLevel hierarchyPermissionLevel) throws NotAuthorizedException {
        boolean capControlEditor = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        boolean isPointEditor = rolePropertyDao.checkLevel(YukonRoleProperty.MANAGE_POINTS, hierarchyPermissionLevel, user);

        if (!capControlEditor && !isPointEditor) {
            throw new NotAuthorizedException("User not allowed to edit points");
        }
    }
}