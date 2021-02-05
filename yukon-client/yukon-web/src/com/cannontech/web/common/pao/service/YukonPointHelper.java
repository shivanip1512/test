package com.cannontech.web.common.pao.service;

import java.util.List;
import java.util.Set;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonPointHelper {

    /**
     * Builds a list of {@link LiteYukonPoint} objects for a device and sorts them.
     * @param pao the yukon PAO whose points are to be sorted.
     * @param sorting {sort : field on which sorting is to be done,direction:direction for sorting}.
     * @param accessor the accessor is required to getMessage for internalization.
     * @return sorted list of yukon points based on field.
     */
    public List<LiteYukonPoint> getYukonPoints(YukonPao pao, SortingParameters sorting, MessageSourceAccessor accessor);

    /**
     * Builds a list of {@link LiteYukonPoint} objects for a device
     * @param pao the yukon PAO whose points are to be sorted.
     * @return the List of yukon points
     */
    public List<LiteYukonPoint> getYukonPoints(YukonPao pao, MessageSourceAccessor accessor);

    /**
     * Checks if the user has either the DB Editor Role, or the Cap Control Editor role
     * @throws NotAuthorizedException if user doesn't have required permissions
     */
    public void verifyRoles(LiteYukonUser user, HierarchyPermissionLevel hierarchyPermissionLevel) throws NotAuthorizedException;

    /**
     * Gets the first attribute to be displayed from a list of BuiltInAttributes and CustomAttributes
     */
    Attribute getFirstAttribute(PaoType paoType, MessageSourceAccessor accessor,
                                Set<BuiltInAttribute> buildInAttributes, List<CustomAttribute> customAttributes,
                                List<Attribute> attributes);

    /**
     * Gets a List of all BuiltInAttributes and CustomAttributes sorted by name
     */
    List<Attribute> getSortedAttributes(Set<BuiltInAttribute> builtInAttributes, List<CustomAttribute> customAttributes,
                                        MessageSourceAccessor accessor);
}