package com.cannontech.common.pao.definition.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.loader.jaxb.CategoryType;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTagDefinition;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;



/**
 * Data access object for pao definition information
 */
  public interface PaoDefinitionDao {

    // ATTRIBUTES
    // ============================================
     Set<AttributeDefinition> getDefinedAttributes(PaoType paoType);

    Multimap<PaoType, Attribute> getPaoTypeAttributesMultiMap();

    Map<PaoType, Map<Attribute, AttributeDefinition>> getPaoAttributeAttrDefinitionMap();

     <T extends Attribute> AttributeDefinition getAttributeLookup(PaoType paoType, T attribute)
            throws IllegalUseOfAttribute;

     Optional<AttributeDefinition> findAttributeLookup(PaoType paoType, BuiltInAttribute attribute);

    /**
     * Returns the BuiltInAttribute for the pao type and point template or null if no attribute is
     * defined for that pao type, pointIdentifier combination
     * 
     * @param paoTypePointIdentifier
     * @return BuiltInAttribute
     */
    Set<BuiltInAttribute> findAttributeForPaoTypeAndPoint(PaoTypePointIdentifier paoTypePointIdentifier);

    /**
     * Helper method to support legacy implementation of one attribute per paoTypeAndPoint. Do Not Use!
     * 
     * @param paoTypePointIdentifier
     * @return (randomly) returns one builtInAttribute from the multiSet.
     * @deprecated use findAttributeForPaoTypeAndPoint(PaoTypePointIdentifier paoTypePointIdentifier)
     */
    @Deprecated
    BuiltInAttribute findOneAttributeForPaoTypeAndPoint(PaoTypePointIdentifier paoTypePointIdentifier);

    // DEVICE CONFIGURATION
    // ============================================

    SetMultimap<ConfigurationCategory, PaoType> getCategoryToPaoTypeMap();

    Set<ConfigurationCategory> getCategoriesForPaoType(PaoType paoType);

    Collection<ConfigurationCategory> getCategoriesForPaoTypes(Set<PaoType> paoTypes);

    boolean isDnpConfigurationType(PaoType paoType);
    boolean isAttributeMappingConfigurationType(PaoType paoType);

    // POINTS
    // ============================================

    /**
     * Method to get all of the point templates for a given pao
     * 
     * @param pao - Pao to get point templates for
     * @return An unmodifiable set of all point templates for the pao
     */
     Set<PointTemplate> getAllPointTemplates(PaoType paoType);

    /**
     * Method to get all of the point templates for a given pao definition
     * 
     * @param paoDefiniton - Pao definition to get point templates for
     * @return A unmodifiable set of all point templates for the pao
     */
     Set<PointTemplate> getAllPointTemplates(PaoDefinition paoDefiniton);

    /**
     * Method to get all of the point templates for a given pao that should
     * be initialized
     * 
     * @param pao - Pao to get point templates for
     * @return A unmodifiable set of all point templates for the pao that should be
     *         initialized
     */
     Set<PointTemplate> getInitPointTemplates(PaoType paoType);

    /**
     * Method to get all of the point templates for a given pao definition
     * that should be initialized
     * 
     * @param newDefinition - Pao definition to get point templates for
     * @return A unmodifiable set of all point templates for the pao that should be
     *         initialized
     */
     Set<PointTemplate> getInitPointTemplates(PaoDefinition newDefinition);

    /**
     * Method to get a point template for a pao based on point type and offset
     * 
     * @param pao - Pao to get point template for
     * @param offset - Offset of point template
     * @param pointType - Type of point template
     * @return Point template for pao
     */
     PointTemplate getPointTemplateByTypeAndOffset(PaoType paoType, PointIdentifier pointIdentifier);

    // COMMANDS
    // ============================================
    /**
     * Method to get a list of command definitions for the given pao which
     * affect one or more of the points in the given set of points
     * 
     * @param pao - Pao to get commands for
     * @param pointSet - Set of points to get affecting commands for
     * @return The set of commands affecting one or more of the points
     */
    Set<CommandDefinition> getCommandsThatAffectPoints(PaoType paoType, Set<? extends PointIdentifier> pointSet);

    Set<CommandDefinition> getAvailableCommands(PaoDefinition newDefinition);

    // TAGS
    // ============================================
    Set<PaoTag> getSupportedTags(PaoType paoType);

    Set<PaoTag> getSupportedTags(PaoDefinition paoDefiniton);

    Set<PaoDefinition> getPaosThatSupportTag(PaoTag firstTag, PaoTag... otherTags);

    Set<PaoType> getPaoTypesThatSupportTag(PaoTag firstTag, PaoTag... otherTags);

    Set<PaoDefinition> getCreatablePaosThatSupportTag(PaoTag firstTag, PaoTag... otherTags);

    boolean isTagSupported(PaoType paoType, PaoTag feature);

    String getValueForTagString(PaoType paoType, PaoTag tag);

    // DEFINITIONS
    // ============================================
     Set<PaoDefinition> getAllPaoDefinitions();

    /**
     * Method to get a map of pao display groups and their associated pao
     * types
     * 
     * @return An immutable map with key: display group name, value: list of
     *         pao display
     */
     ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap();

    /**
     * Method used to get a pao definition for a pao
     * 
     * @param pao - Pao to get definition for
     * @return The pao's pao definition
     */
     PaoDefinition getPaoDefinition(PaoType paoType);

    /**
     * Method to get a set of PaoDefinitions into which the given PAO can change.
     * Result is guaranteed not to contain the paoDefinition that was passed in.
     *
     * @param paoDefinition - Definition of PAO to change
     * @return An immutable set of PaoDefinition
     */
     Set<PaoDefinition> getPaosThatPaoCanChangeTo(PaoDefinition paoDefinition);

    /**
     * Takes an iterable of type T and returns a new List of items that support
     * the provided PaoTag.
     */
    <T extends YukonPao> List<T> filterPaosForTag(Iterable<T> paos, PaoTag feature);

    /**
     * Takes an Set of type T and returns a new Set of items that support
     * the provided PaoTag.
     */
    <T extends YukonPao> Set<T> filterPaosForTag(Set<T> paos, PaoTag feature);

    /**
     * Method to return set of paoDefinitions that are creatable.
     * 
     * @return An unmodifiable set of paoDefinition
     */
    Set<PaoDefinition> getCreatablePaoDefinitions();

    ImmutableBiMap<PaoTag, PaoTagDefinition> getSupportedTagsForPaoType(PaoType paoType);

    /**
     * Uses the cached pao definition of the given type to return the point identifier matching
     * the given default point name Throws {@link NotFoundException} when no point identifier
     * matches that default point name.
     */
    PointIdentifier getPointIdentifierByDefaultName(PaoType key, String defaultPointName) throws NotFoundException;

    void override();

    /**
     * Reloads all pao definitions files and re-initializes the cache. This method should be used for testing
     * only since
     * all pao definitions are loaded on
     * the start-up.
     */
    void load();

    /**
     * Checks whether the given category type is supported by the pao type
     * @param paoType
     * @param catType
     * @return true if supported
     */
    boolean isCategoryTypeSupportedByPaoType(PaoType paoType, CategoryType catType);

    List<CategoryType> getRequiredCategoriesByPaoType(PaoType paoType);
}
