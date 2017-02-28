package com.cannontech.common.pao.definition.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.loader.DefinitionLoaderService;
import com.cannontech.common.pao.definition.loader.jaxb.CategoryType;
import com.cannontech.common.pao.definition.loader.jaxb.DeviceCategories.Category;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTagDefinition;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.util.SetUtils;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * Caches pao definitions. 
 */
public class PaoDefinitionDaoImpl implements PaoDefinitionDao {
    
    private final Logger log = YukonLogManager.getLogger(PaoDefinitionDaoImpl.class);
    
    private Map<PaoType, Map<Attribute, AttributeDefinition>> paoAttributeAttrDefinitionMap;
    private Multimap<PaoType, Attribute> paoTypeAttributesMultiMap;
    private SetMultimap<PaoType, PointTemplate> paoAllPointTemplateMap;
    private SetMultimap<PaoType, PointTemplate> paoInitPointTemplateMap;
    private SetMultimap<PaoType, Category> paoCategoryMap;
    private SetMultimap<Category, PaoType> categoryToPaoMap;
    private BiMap<PaoType, PaoDefinition> paoTypeMap;
    private ListMultimap<String, PaoDefinition> paoDisplayGroupMap = LinkedListMultimap.create();
    private SetMultimap<String, PaoDefinition> changeGroupPaosMap = HashMultimap.create();
    private SetMultimap<PaoType, CommandDefinition> paoCommandMap;
    private Map<PaoType, ImmutableBiMap<PaoTag, PaoTagDefinition>> supportedTagsByType;
    private Map<PaoTag, BiMap<PaoType, PaoDefinition>> typesBySupportedTag = new HashMap<>();
    private Set<PaoDefinition> creatablePaoDefinitions = new HashSet<>();
    private SetMultimap<PaoTypePointIdentifier, BuiltInAttribute> paoAndPointToAttribute =  HashMultimap.create();
    
    @Autowired private DefinitionLoaderService definitionLoaderService;
   
    @PostConstruct
    public void initialize() {
        paoAttributeAttrDefinitionMap = definitionLoaderService.getPaoAttributeAttrDefinitionMap();
        Builder<PaoType, Attribute> builder = ImmutableListMultimap.builder();
        for (Map.Entry<PaoType, Map<Attribute, AttributeDefinition>> entry : paoAttributeAttrDefinitionMap.entrySet()) {
            builder.putAll(entry.getKey(), entry.getValue().keySet());
        }
        paoTypeAttributesMultiMap = builder.build();
        paoAllPointTemplateMap = definitionLoaderService.getPointTemplateMap(false);
        paoInitPointTemplateMap =  definitionLoaderService.getPointTemplateMap(true);
        paoCategoryMap = definitionLoaderService.getPaoCategoryMap();
        categoryToPaoMap =  Multimaps.invertFrom(paoCategoryMap, HashMultimap.create());
        paoTypeMap = definitionLoaderService.getPaoTypeMap();
        for(PaoDefinition paoDefinition: paoTypeMap.values()){
            if(paoDefinition.isCreatable()){
                creatablePaoDefinitions.add(paoDefinition);
            }
            paoDisplayGroupMap.put(paoDefinition.getDisplayGroup(), paoDefinition);
            changeGroupPaosMap.put(paoDefinition.getChangeGroup(), paoDefinition);
        }
        paoCommandMap = definitionLoaderService.getPaoCommandMap();
        supportedTagsByType = definitionLoaderService.getSupportedTagsByType();

        for (PaoType paoType : paoTypeMap.keySet()) {
            ImmutableBiMap<PaoTag, PaoTagDefinition> tags = supportedTagsByType.get(paoType);
            for (PaoTag tag : tags.keySet()) {
                if (typesBySupportedTag.get(tag) == null) {
                    typesBySupportedTag.put(tag, HashBiMap.create());
                }
                typesBySupportedTag.get(tag).put(paoType, paoTypeMap.get(paoType));
            }
            Map<Attribute, AttributeDefinition> attributes = paoAttributeAttrDefinitionMap.get(paoType);
            if (attributes != null) {
                for (AttributeDefinition attribute : attributes.values()) {
                    PointIdentifier pointId = attribute.getPointTemplate().getPointIdentifier();
                    paoAndPointToAttribute.put(PaoTypePointIdentifier.of(paoType, pointId), attribute.getAttribute());
                }
            }

        }
        creatablePaoDefinitions = paoTypeMap.values()
                                            .stream()
                                            .filter(c -> c.isCreatable())
                                            .collect(Collectors.toSet());
    }
    
    @Override
    public void reload(){
        definitionLoaderService.reload();
        initialize();
    }
    
    @Override
    public <T extends Attribute> AttributeDefinition getAttributeLookup(PaoType paoType, T attribute)
            throws IllegalUseOfAttribute {
        Map<Attribute, AttributeDefinition> attributeLookupsForPao = paoAttributeAttrDefinitionMap.get(paoType);
        if (attributeLookupsForPao == null) {
            throw new IllegalUseOfAttribute("No AttributeLookups exist for " + paoType);
        }
        AttributeDefinition attributeDefinition = attributeLookupsForPao.get(attribute);
        if (attributeDefinition == null) {
            throw new IllegalUseOfAttribute("No AttributeLookup exists for " + attribute + " on " + paoType);
        }
        return attributeDefinition;
    }
    
    @Override
    public Map<PaoType, Map<Attribute, AttributeDefinition>> getPaoAttributeAttrDefinitionMap() {
        return paoAttributeAttrDefinitionMap;
    }
    
    @Override
    public Multimap<PaoType, Attribute> getPaoTypeAttributesMultiMap() {
        return paoTypeAttributesMultiMap;
    }
    
    @Override
    public Set<AttributeDefinition> getDefinedAttributes(PaoType paoType) {
        Map<Attribute, AttributeDefinition> attributeLookupsForPao = paoAttributeAttrDefinitionMap.get(paoType);
        if (attributeLookupsForPao == null) {
            return ImmutableSet.of();
        } else {
            return ImmutableSet.copyOf(attributeLookupsForPao.values());
        }
    }
    
    @Override
    public Set<PointTemplate> getAllPointTemplates(PaoDefinition paoDefinition) {
        return getAllPointTemplates(paoDefinition.getType());
    }
    
    @Override
    public Set<PointTemplate> getInitPointTemplates(PaoDefinition paoDefinition) {
        return getInitPointTemplates(paoDefinition.getType());
    }
    
    @Override
    public Set<PointTemplate> getAllPointTemplates(PaoType paoType) {
        Set<PointTemplate> templates = paoAllPointTemplateMap.get(paoType);
        return Collections.unmodifiableSet(templates);
    }
    
    @Override
    public Set<PointTemplate> getInitPointTemplates(PaoType paoType) {
        Set<PointTemplate> templates = paoInitPointTemplateMap.get(paoType);
        return Collections.unmodifiableSet(templates);
    }
    
    @Override
    public PointTemplate getPointTemplateByTypeAndOffset(PaoType paoType, PointIdentifier pointIdentifier) { 
        Optional<PointTemplate> optional = paoAllPointTemplateMap.get(paoType)
                                                                 .stream()
                                                                 .filter(x -> x.getPointIdentifier().equals(x.getPointIdentifier()))
                                                                 .findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }

        throw new NotFoundException("Point template not found for pao type: " + paoType + ", " + pointIdentifier);
    }
        
    @Override
    public Set<BuiltInAttribute> findAttributeForPaoTypeAndPoint(PaoTypePointIdentifier paoTypePointIdentifier) {
        Set<BuiltInAttribute> builtInAttributes = paoAndPointToAttribute.get(paoTypePointIdentifier);
        return Collections.unmodifiableSet(builtInAttributes);
    }
    
    @Override
    @Deprecated
    public BuiltInAttribute findOneAttributeForPaoTypeAndPoint(PaoTypePointIdentifier paoTypePointIdentifier) {
        Set<BuiltInAttribute> builtInAttributes = findAttributeForPaoTypeAndPoint(paoTypePointIdentifier);
        if (CollectionUtils.isNotEmpty(builtInAttributes)) {
            return builtInAttributes.iterator().next();
        }
        return null;
    }
    
    @Override
    public Set<CommandDefinition> getCommandsThatAffectPoints(PaoType paoType, Set<? extends PointIdentifier> pointSet) {
        
        Set<CommandDefinition> commandSet = Sets.newHashSet();
        Set<CommandDefinition> allCommandSet = paoCommandMap.get(paoType);
        
        for (CommandDefinition command : allCommandSet) {
            for (PointIdentifier point : pointSet) {
                if (command.affectsPoint(point)) {
                    commandSet.add(command);
                    break;
                }
            }
        }
        
        return commandSet;
    }
    
    @Override
    public Set<CommandDefinition> getAvailableCommands(PaoDefinition newDefinition) {

        Set<CommandDefinition> allCommandSet = paoCommandMap.get(newDefinition.getType());
        return Collections.unmodifiableSet(allCommandSet);
    }
    
    @Override
    public ImmutableBiMap<PaoTag, PaoTagDefinition> getSupportedTagsForPaoType(PaoType paoType) {
        return supportedTagsByType.get(paoType);
    }
    
    @Override
    public Set<PaoTag> getSupportedTags(PaoType paoType) {
        return supportedTagsByType.get(paoType).keySet();
    }
    
    @Override
    public Set<PaoTag> getSupportedTags(PaoDefinition paoDefiniton) {
        return getSupportedTags(paoDefiniton.getType());
    }
                
    @Override
    public Set<PaoDefinition> getPaosThatSupportTag(PaoTag firstTag, PaoTag... otherTags) {
        List<PaoTag> tags = getCombinedTags(firstTag, otherTags);
        ImmutableSet.Builder<PaoDefinition> definitions = ImmutableSet.builder();
        for (PaoTag tag : tags) {
            definitions.addAll(typesBySupportedTag.get(tag).values());
        }
        
        return definitions.build();
    }
    
    @Override
    public Set<PaoDefinition> getCreatablePaosThatSupportTag(PaoTag firstTag, PaoTag... otherTags) {
        return getPaosThatSupportTag(firstTag, otherTags).stream()
                                                         .filter(x -> x.isCreatable())
                                                         .collect(Collectors.toSet());
    }
    
    @Override
    public Set<PaoType> getPaoTypesThatSupportTag(PaoTag firstTag, PaoTag... otherTags) {
        return getPaosThatSupportTag(firstTag, otherTags).stream()
                                                         .map(x -> x.getType())
                                                         .collect(Collectors.toSet());
    }
    
    @Override
    public boolean isTagSupported(PaoType paoType, PaoTag tag) {
        PaoTagDefinition supportedTag = getSupportedTag(paoType, tag);
        return supportedTag != null;
    }
    
    private PaoTagDefinition getSupportedTag(PaoType paoType, PaoTag tag) {
        Map<PaoTag, PaoTagDefinition> supportedTags = getSupportedTagsForPaoType(paoType);
        return supportedTags.get(tag);
    }
    
    @Override
    public Set<PaoDefinition> getCreatablePaoDefinitions() {
        return Collections.unmodifiableSet(creatablePaoDefinitions);
    }
    
    @Override
    public <T extends YukonPao> List<T> filterPaosForTag(Iterable<T> paos, final PaoTag tag) {
        
        Predicate<YukonPao> supportsTagPredicate = new Predicate<YukonPao>() {
            @Override
            public boolean apply(YukonPao input) {
                return isTagSupported(input.getPaoIdentifier().getPaoType(), tag);
            }
        };
        
        return Lists.newArrayList(Iterables.filter(paos, supportsTagPredicate));
    }
    
    @Override
    public <T extends YukonPao> Set<T> filterPaosForTag(Set<T> paos, final PaoTag tag) {
        Predicate<YukonPao> supportsTagPredicate = new Predicate<YukonPao>() {
            @Override
            public boolean apply(YukonPao input) {
                return isTagSupported(input.getPaoIdentifier().getPaoType(), tag);
            }
        };
        
        return Sets.filter(paos, supportsTagPredicate);
    }
    
    @Override
    public String getValueForTagString(PaoType paoType, PaoTag tag) {
        String valueForTag = getConvertedValueForTag(paoType, tag, String.class);
        return valueForTag;
    }
    
    public <T> T getConvertedValueForTag(PaoType paoType, PaoTag tag, Class<T> returnType) {
        Validate.isTrue(tag.isTagHasValue(), "Tag does not support an attached value");
        log.debug(String.format("getting converted value of %s for %s as %s", tag, paoType, returnType.getSimpleName()));
        Validate.isTrue(returnType.isAssignableFrom(tag.getValueType().getTypeClass()), "can't convert " + tag + " to "
            + returnType);
        PaoTagDefinition tagDefinition = getSupportedTag(paoType, tag);
        if (tagDefinition == null) {
            throw new RuntimeException("no value of " + tag + " for " + paoType + " that is supported");
        }
        Object convertedValue = tagDefinition.getValue();
        
        log.debug(String.format("returning: %s", convertedValue));
        T result = returnType.cast(convertedValue);
        return result;
    }
    
    @Override
    public Set<PaoDefinition> getAllPaoDefinitions() {
        Set<PaoDefinition> allDefinitions = Collections.unmodifiableSet(paoTypeMap.values());
        return allDefinitions;
    }
    
    @Override
    public ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap() {
        return paoDisplayGroupMap;
    }
    
    @Override
    public PaoDefinition getPaoDefinition(PaoType paoType) {
  
        if (paoTypeMap.containsKey(paoType)) {
            return paoTypeMap.get(paoType);
        } else {
            throw new IllegalArgumentException("Pao type " + paoType + " is not supported.");
        }
    }
    
    @Override
    public Set<PaoDefinition> getPaosThatPaoCanChangeTo(PaoDefinition paoDefinition) {
        
        String changeGroup = paoDefinition.getChangeGroup();
        if (changeGroup != null && changeGroupPaosMap.containsKey(changeGroup)) {
            Set<PaoDefinition> definitions = changeGroupPaosMap.get(changeGroup);
            return SetUtils.minusOne(definitions, paoDefinition).immutableCopy();
        } else {
            throw new IllegalArgumentException("No pao types found for change group: " + changeGroup);
        }
    }
    
    @Override
    public PointIdentifier getPointIdentifierByDefaultName(PaoType type, String defaultPointName)
            throws NotFoundException {

        Optional<PointTemplate> optional = paoAllPointTemplateMap.get(type)
                                                                 .stream()
                                                                 .filter(x -> x.getName().equals(defaultPointName))
                                                                 .findFirst();
        if (optional.isPresent()) {
            return optional.get().getPointIdentifier();
        }
        throw new NotFoundException("Could not find point identifier for" + type + "/" + defaultPointName);
    }

    @Override
    public SetMultimap<String, PaoType> getCategoryTypeToPaoTypesMap() {
        
        SetMultimap<String, PaoType> map = HashMultimap.create();
        for (Category category : categoryToPaoMap.keySet()) {
            map.putAll(category.getType().value(), categoryToPaoMap.get(category));
        }
        
        return map;
    }
    
    @Override
    public Set<Category> getCategoriesForPaoType(PaoType paoType) {
        Set<Category> categories = paoCategoryMap.get(paoType);
        return  Collections.unmodifiableSet(categories);
    }
    
    @Override
    public Set<Category> getCategoriesForPaoTypes(Set<PaoType> paoTypes) {
        Set<Category> categories = new HashSet<>();
        for (PaoType paoType : paoTypes) {
            categories.addAll(paoCategoryMap.get(paoType));
        }
        
        return Collections.unmodifiableSet(categories);
    }

    @Override
    public boolean isDnpConfigurationType(PaoType paoType) {
        Optional<Category> optional = paoCategoryMap.get(paoType)
                                                    .stream()
                                                    .filter(x -> CategoryType.DNP == x.getType())
                                                    .findFirst();
        if (optional.isPresent()) {
            return true;
        }
        return false;
    }
    
    
    /**
     * Combines firstTag and otherTags.
     */
    private List<PaoTag> getCombinedTags(PaoTag firstTag, PaoTag... otherTags){
        List<PaoTag> tags = new ArrayList<>();
        tags.add(firstTag);
        if(otherTags.length > 0){
            tags.addAll(Arrays.asList(otherTags));
        }
        return tags;
    }
}

