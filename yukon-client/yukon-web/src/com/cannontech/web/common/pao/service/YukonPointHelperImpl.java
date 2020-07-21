package com.cannontech.web.common.pao.service;

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
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class YukonPointHelperImpl implements YukonPointHelper {

    @Autowired private PointDao pointDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private AttributeDao attributeDao;

    @Override
    public List<LiteYukonPoint> getYukonPoints(final YukonPao pao, SortingParameters sorting,
            final MessageSourceAccessor accessor) {
        
        List<LiteYukonPoint> liteYukonPoints = getYukonPointsForSorting(pao);
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
    private List<LiteYukonPoint> getYukonPointsForSorting(final YukonPao pao) {
        List<LitePoint> points = pointDao.getLitePointsByPaObjectId(pao.getPaoIdentifier().getPaoId());
        return points.stream().map(point -> {
            PaoPointIdentifier paoPointIdent = new PaoPointIdentifier(pao.getPaoIdentifier(), new PointIdentifier(point.getPointTypeEnum(), point.getPointOffset()));
            Set<BuiltInAttribute> attributes = paoDefinitionDao.findAttributeForPaoTypeAndPoint(paoPointIdent.getPaoTypePointIdentifier());
            Attribute attribute = !attributes.isEmpty() ? attributes.iterator().next() : attributeDao
                    .findCustomAttributeForPaoTypeAndPoint(paoPointIdent.getPaoTypePointIdentifier());
            return LiteYukonPoint.of(paoPointIdent, attribute, point.getPointName(), point.getLiteID());
        }).collect(Collectors.toList());
    }

    @Override
    public List<LiteYukonPoint> getYukonPoints(final YukonPao pao) {
        List<LiteYukonPoint> liteYukonPoints = getYukonPointsForSorting(pao);
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