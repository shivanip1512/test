package com.cannontech.common.pao.definition.dao;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

/**
 * Data access object for pao definition information
 */
public interface PaoDefinitionDao {

	// ATTRIBUTES
	//============================================
    public abstract Set<AttributeDefinition> getDefinedAttributes(PaoType paoType);
    
    public Multimap<PaoType, Attribute> getPaoTypeAttributesMultiMap();
    
    public Map<PaoType, Map<Attribute, AttributeDefinition>> getPaoAttributeAttrDefinitionMap();
    
    public abstract AttributeDefinition getAttributeLookup(PaoType paoType, BuiltInAttribute attribute) throws IllegalUseOfAttribute;

    // POINTS
    //============================================

    /**
     * Method to get all of the point templates for a given pao
     * @param pao - Pao to get point templates for
     * @return A set of all point templates for the pao (returns a new copy
     *         each time the method is called)
     */
    public abstract Set<PointTemplate> getAllPointTemplates(PaoType paoType);

    /**
     * Method to get all of the point templates for a given pao definition
     * @param paoDefiniton - Pao definition to get point templates for
     * @return A set of all point templates for the pao (returns a new copy
     *         each time the method is called)
     */
    public abstract Set<PointTemplate> getAllPointTemplates(PaoDefinition paoDefiniton);

    /**
     * Method to get all of the point templates for a given pao that should
     * be initialized
     * @param pao - Pao to get point templates for
     * @return A set of all point templates for the pao that should be
     *         initialized (returns a new copy each time the method is called)
     */
    public abstract Set<PointTemplate> getInitPointTemplates(PaoType paoType);

    /**
     * Method to get all of the point templates for a given pao definition
     * that should be initialized
     * @param newDefinition - Pao definition to get point templates for
     * @return A set of all point templates for the pao that should be
     *         initialized (returns a new copy each time the method is called)
     */
    public abstract Set<PointTemplate> getInitPointTemplates(PaoDefinition newDefinition);
    
    /**
     * Method to get a point template for a pao based on point type and offset
     * @param pao - Pao to get point template for
     * @param offset - Offset of point template
     * @param pointType - Type of point template
     * @return Point template for pao
     */
    public abstract PointTemplate getPointTemplateByTypeAndOffset(PaoType paoType, PointIdentifier pointIdentifier);

    // COMMANDS
    //============================================
    /**
     * Method to get a list of command definitions for the given pao which
     * affect one or more of the points in the given set of points
     * @param pao - Pao to get commands for
     * @param pointSet - Set of points to get affecting commands for
     * @return The set of commands affecting one or more of the points
     */
    public Set<CommandDefinition> getCommandsThatAffectPoints(PaoType paoType, Set<? extends PointIdentifier> pointSet);
    
    public Set<CommandDefinition> getAvailableCommands(PaoDefinition newDefinition);
    
    // TAGS
    //============================================
    public abstract Set<PaoTag> getSupportedTags(PaoType paoType);
    public abstract Set<PaoTag> getSupportedTags(PaoDefinition paoDefiniton);
    
    public abstract Set<PaoDefinition> getPaosThatSupportTag(PaoTag feature);
    public abstract Set<PaoType> getPaoTypesThatSupportTag(PaoTag firstTag, PaoTag... otherTags);
    
    public abstract boolean isTagSupported(PaoType paoType, PaoTag feature);
    public abstract boolean isTagSupported(PaoDefinition paoDefiniton, PaoTag feature);
    
    // DEFINITIONS
    //============================================
    public abstract Set<PaoDefinition> getAllPaoDefinitions();
    
    /**
     * Method to get a map of pao display groups and their associated pao
     * types
     * @return An immutable map with key: display group name, value: list of
     *         pao display
     */
    public abstract ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap();

    /**
     * Method used to get a pao definition for a pao
     * @param pao - Pao to get definition for
     * @return The pao's pao definition
     */
    public abstract PaoDefinition getPaoDefinition(PaoType paoType);
    
    /**
     * Method to get a set of pao types that the given pao can change into
     * @param paoDefinition - Definition of pao to change
     * @return A set of pao definitions (returns a new copy each time the
     *         method is called)
     */
    //TODO rename me
    public abstract Set<PaoDefinition> getPaosThatPaoCanChangeTo(PaoDefinition paoDefinition);
    
    // MISC
    //============================================
    public String getPointLegendHtml(String displayGroup);

    /**
     * Takes an iterable of type T and returns a new Iterable of items that support
     * the provided PaoTag.
     * @param paos
     * @param feature
     * @return <T extends YukonPao> Iterable<T>
     */
    public <T extends YukonPao> Iterable<T> filterPaosForTag(Iterable<T> paos, PaoTag feature);

    /**
     * Method to return set of paoDefinitions that are creatable.
     * @return set of paoDefinition
     */
    public Set<PaoDefinition> getCreatablePaoDefinitions();
}