package com.cannontech.common.pao.definition.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.attribute.lookup.BasicAttributeDefinition;
import com.cannontech.common.pao.definition.attribute.lookup.MappedAttributeDefinition;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinitionImpl;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTagDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.definition.model.castor.Archive;
import com.cannontech.common.pao.definition.model.castor.BasicLookup;
import com.cannontech.common.pao.definition.model.castor.Cmd;
import com.cannontech.common.pao.definition.model.castor.Command;
import com.cannontech.common.pao.definition.model.castor.MappedLookup;
import com.cannontech.common.pao.definition.model.castor.Pao;
import com.cannontech.common.pao.definition.model.castor.PaoDefinitions;
import com.cannontech.common.pao.definition.model.castor.Point;
import com.cannontech.common.pao.definition.model.castor.PointRef;
import com.cannontech.common.pao.definition.model.castor.Tag;
import com.cannontech.common.pao.definition.model.castor.TypeFilter;
import com.cannontech.common.search.FilterType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SetUtils;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * Implementation class for PaoDefinitionDao
 */
public class PaoDefinitionDaoImpl implements PaoDefinitionDao {

    private Resource inputFile = null;
    private Resource schemaFile = null;
    private Resource customInputFile = new FileSystemResource(CtiUtilities.getYukonBase() + "\\Server\\Config\\deviceDefinition.xml");
    private Resource pointLegendFile = null;
    private UnitMeasureDao unitMeasureDao = null;
    private StateDao stateDao = null;
    private Logger log = YukonLogManager.getLogger(PaoDefinitionDaoImpl.class);

    // Maps containing all of the data in the paoDefinition.xml file
    private Map<PaoType, Map<Attribute, AttributeDefinition>> paoAttributeAttrDefinitionMap = null;
    private Multimap<PaoType, Attribute> paoTypeAttributesMultiMap = null;
    private SetMultimap<PaoType, PointTemplate> paoAllPointTemplateMap = null;
    private SetMultimap<PaoType, PointTemplate> paoInitPointTemplateMap = null;
    private BiMap<PaoType, PaoDefinition> paoTypeMap = null;
    private ListMultimap<String, PaoDefinition> paoDisplayGroupMap = null;
    private SetMultimap<String, PaoDefinition> changeGroupPaosMap = null;
    private SetMultimap<PaoType, CommandDefinition> paoCommandMap = null;
    private Map<PaoType, ImmutableBiMap<PaoTag, PaoTagDefinition>> supportedTagsByType;
    private Map<PaoTag, BiMap<PaoType, PaoDefinition>> typesBySupportedTag;
    private Set<PaoDefinition> creatablePaoDefinitions = null;
    private List<String> fileIdOrder = null;
    private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    
    public void setInputFile(Resource inputFile) {
        this.inputFile = inputFile;
    }
    
    public void setSchemaFile(Resource schemaFile) {
        this.schemaFile = schemaFile;
    }
    
    public void setCustomInputFile(Resource customInputResource) {
        this.customInputFile = customInputResource;
    }

    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    @Autowired
    public void setUnitMeasureDao(UnitMeasureDao unitMeasureDao) {
        this.unitMeasureDao = unitMeasureDao;
    }
    
    public void setPointLegendFile(Resource pointLegendFile) {
        this.pointLegendFile = pointLegendFile;
    }
    
    // ATTRIBUTES
    //============================================
    @Override
    public AttributeDefinition getAttributeLookup(PaoType paoType,
            BuiltInAttribute attribute) throws IllegalUseOfAttribute {
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
        return this.getAllPointTemplates(paoDefinition.getType());
    }

    @Override
    public Set<PointTemplate> getInitPointTemplates(PaoDefinition paoDefinition) {
        return this.getInitPointTemplates(paoDefinition.getType());
    }
    
    @Override
    public PointTemplate getPointTemplateByTypeAndOffset(PaoType paoType, PointIdentifier pointIdentifier) {

    	PointType pointType = pointIdentifier.getPointType();
    	int offset = pointIdentifier.getOffset();

        Set<PointTemplate> allPointTemplates = this.getAllPointTemplates(paoType);

        for (PointTemplate template : allPointTemplates) {

            if (template.getPointType() == pointType && template.getOffset() == offset) {
                return template;
            }
        }

        throw new NotFoundException("Point template not found for pao type: " + paoType + ", point type: " + pointType + ", offset: " + offset);
    }
    
    @Override
    public Set<PointTemplate> getAllPointTemplates(PaoType paoType) {
        Set<PointTemplate> templates = this.paoAllPointTemplateMap.get(paoType);
        return Collections.unmodifiableSet(templates);
    }

    @Override
    public Set<PointTemplate> getInitPointTemplates(PaoType paoType) {
        Set<PointTemplate> templates = this.paoInitPointTemplateMap.get(paoType);
        return Collections.unmodifiableSet(templates);
    }
    
    // COMMANDS
    //============================================
    @Override
    public Set<CommandDefinition> getCommandsThatAffectPoints(PaoType paoType, Set<? extends PointIdentifier> pointSet) {

        Set<CommandDefinition> commandSet = Sets.newHashSet();

        Set<CommandDefinition> allCommandSet = this.paoCommandMap.get(paoType);

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
    	
    	PaoType paoType = newDefinition.getType();
        Set<CommandDefinition> allCommandSet = this.paoCommandMap.get(paoType);
        return Collections.unmodifiableSet(allCommandSet);
    }
    
    // TAGS
    //============================================
    private ImmutableBiMap<PaoTag, PaoTagDefinition> getSupportedTagsForPaoType(PaoType paoType) {
        return supportedTagsByType.get(paoType);
    }
    
    private BiMap<PaoType, PaoDefinition> getPaoDefinitionsThatSupportTag(PaoTag tag) {
        return typesBySupportedTag.get(tag);
    }
    
    public Set<PaoTag> getSupportedTags(PaoType paoType) {
        // no need to wrap, already immutable
    	return getSupportedTagsForPaoType(paoType).keySet();
    }
    
    @Override
    public Set<PaoTag> getSupportedTags(PaoDefinition paoDefiniton) {
        // no need to wrap, already immutable
    	return getSupportedTagsForPaoType(paoDefiniton.getType()).keySet();
    }
    
    @Override
    public Set<PaoDefinition> getPaosThatSupportTag(PaoTag firstTag, PaoTag... otherTags) {

        // handle common single tag case to prevent new object instantiation
        if (otherTags.length == 0) {
            return Collections.unmodifiableSet(getPaoDefinitionsThatSupportTag(firstTag).values());
        }
        
        ImmutableSet.Builder<PaoDefinition> definitions = ImmutableSet.builder();
        for (PaoTag tag : Lists.asList(firstTag, otherTags)) {
            Set<PaoDefinition> definitionsForTag = getPaoDefinitionsThatSupportTag(tag).values();
            definitions.addAll(definitionsForTag);
        }
    	return definitions.build();
    }
    
    @Override
    public Set<PaoType> getPaoTypesThatSupportTag(PaoTag firstTag, PaoTag... otherTags) {
        
        // handle common single tag case to prevent new object instantiation
        if (otherTags.length == 0) {
            return Collections.unmodifiableSet(getPaoDefinitionsThatSupportTag(firstTag).keySet());
        }

        Set<PaoType> paoTypes = EnumSet.noneOf(PaoType.class);
        for (PaoTag tag : Lists.asList(firstTag, otherTags)) {
            Set<PaoType> types = getPaoDefinitionsThatSupportTag(tag).keySet();
            paoTypes.addAll(types);
        }
        return Collections.unmodifiableSet(paoTypes);
    }
    
    @Override
    public <T extends YukonPao> Iterable<T> filterPaosForTag(Iterable<T> paos, final PaoTag tag) {
        Predicate<YukonPao> supportsTagPredicate = new Predicate<YukonPao>() {
            @Override
            public boolean apply(YukonPao input) {
                return isTagSupported(input.getPaoIdentifier().getPaoType(), tag);
            }
        };
        
        return Iterables.filter(paos, supportsTagPredicate);    
    }
    
    @Override
    public long getValueForTagLong(PaoType paoType, PaoTag tag) {
        Number valueForTag = getConvertedValueForTag(paoType, tag, Number.class);
        return valueForTag.longValue();
    }
    
    @Override
    public String getValueForTagString(PaoType paoType, PaoTag tag) {
        String valueForTag = getConvertedValueForTag(paoType, tag, String.class);
        return valueForTag;
    }

    public <T> T getConvertedValueForTag(PaoType paoType, PaoTag tag, Class<T> returnType) {
        Validate.isTrue(tag.isTagHasValue(), "Tag does not support an attached value");
        LogHelper.debug(log, "getting converted value of %s for %s as %s", tag, paoType, returnType.getSimpleName());
        Validate.isTrue(returnType.isAssignableFrom(tag.getValueType().getTypeClass()), "can't convert " + tag + " to " + returnType);
        PaoTagDefinition tagDefinition = getSupportedTag(paoType, tag);
        if (tagDefinition == null) {
            throw new RuntimeException("no value of " + tag + " for " + paoType + " that is supported");
        }
        Object convertedValue = tagDefinition.getValue();

        LogHelper.debug(log, "returning: %s", convertedValue);
        T result = returnType.cast(convertedValue);
        return result;
    }
    
    @Override
    public boolean isTagSupported(PaoDefinition paoDefiniton, PaoTag tag) {
    	return isTagSupported(paoDefiniton.getType(), tag);
    }

    @Override
    public boolean isTagSupported(PaoType paoType, PaoTag tag) {
        PaoTagDefinition supportedTag = getSupportedTag(paoType, tag);
        return supportedTag != null;
    }
    
    private PaoTagDefinition getSupportedTag(PaoType paoType, PaoTag tag) {
        Map<PaoTag, PaoTagDefinition> supportedTags = getSupportedTagsForPaoType(paoType);
        // ok if this returns null when tag isn't in map
        return supportedTags.get(tag);
    }
    
    // DEFINITIONS
    //============================================
    @Override
    public Set<PaoDefinition> getCreatablePaoDefinitions() {
        return Collections.unmodifiableSet(creatablePaoDefinitions);
    }
    
    @Override
    public Set<PaoDefinition> getAllPaoDefinitions() {
    	
    	Set<PaoDefinition> allDefinitions = Collections.unmodifiableSet(this.paoTypeMap.values());
    	return allDefinitions;
    }
    
    @Override
    public ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap() {
        return this.paoDisplayGroupMap;
    }
    
    @Override
    public PaoDefinition getPaoDefinition(PaoType paoType) {
        
        if (this.paoTypeMap.containsKey(paoType)) {
            return this.paoTypeMap.get(paoType);
        } else {
            throw new IllegalArgumentException("Pao type " + paoType
                    + " is not supported.");
        }
    }
    
    @Override
    public Set<PaoDefinition> getPaosThatPaoCanChangeTo(PaoDefinition paoDefinition) {

        String changeGroup = paoDefinition.getChangeGroup();
        if (changeGroup != null && this.changeGroupPaosMap.containsKey(changeGroup)) {

            Set<PaoDefinition> definitions = this.changeGroupPaosMap.get(changeGroup);
            return SetUtils.minusOne(definitions, paoDefinition).immutableCopy();
        } else {
            throw new IllegalArgumentException("No pao types found for change group: "
                    + changeGroup);
        }
    }
    
    // MISC
    //============================================
    @Override
    public String getPointLegendHtml(String displayGroup) {
        try {
            log.debug("Transforming notification");
            log.debug("javax.xml: " + System.getProperty("javax.xml.transform.TransformerFactory"));
            Writer resultWriter = new StringWriter();
            Result result = new StreamResult(resultWriter);
            Source inputSource = new StreamSource(inputFile.getInputStream());
            Source xslSource = new StreamSource(pointLegendFile.getInputStream());
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer(xslSource);
            transformer.setParameter("group", displayGroup);
            transformer.transform(inputSource, result);
            String resultString = resultWriter.toString();
            return resultString;
        } catch (Exception e) {
            log.warn("Unable to translate paoDefinition.xml file for displayGroup=" + displayGroup, e);
            return "Error rendering point legends: " + e.toString();
        }
    }
    
    // INITALIZATION
    //============================================
    public void initialize() throws Exception {

        this.paoTypeMap = EnumHashBiMap.create(PaoType.class);
        this.paoAllPointTemplateMap = HashMultimap.create();
        this.paoInitPointTemplateMap = HashMultimap.create();
        this.paoDisplayGroupMap = LinkedListMultimap.create();
        this.changeGroupPaosMap = HashMultimap.create();
        this.paoAttributeAttrDefinitionMap = Maps.newEnumMap(PaoType.class);
        this.paoCommandMap = HashMultimap.create();
        this.creatablePaoDefinitions = Sets.newHashSet();
        this.fileIdOrder = Lists.newArrayList();
        this.typesBySupportedTag = Maps.newEnumMap(PaoTag.class);
        for (PaoTag paoTag : PaoTag.values()) {
            typesBySupportedTag.put(paoTag, EnumHashBiMap.<PaoType, PaoDefinition>create(PaoType.class));
        }
        this.supportedTagsByType = Maps.newEnumMap(PaoType.class);
        
        // definition resources (in order from lowest level overrides to highest)
        List<Resource> definitionResources = new ArrayList<Resource>();
        if (customInputFile != null && customInputFile.exists()) {
        	definitionResources.add(customInputFile);
        }
        definitionResources.add(inputFile);
        
        // merge resources, sort
        Map<String, PaoStore> paoStores;
        try {
            paoStores = mergeDefinitionResourcesIntoPaoStoreMap(definitionResources);
        } catch (Exception e) {
            log.warn("Unable to read PAO definitions. Check that there is not a <deviceDefinitions> element in the custom deviceDefinition.xml file (see YUK-8738).", e);
            throw e;
        }
        Map<String, PaoStore> sortedPaoStores = new LinkedHashMap<String, PaoStore>();
        for (String id : this.fileIdOrder) {
    		sortedPaoStores.put(id, paoStores.remove(id));
        }
        sortedPaoStores.putAll(paoStores);
        
        
        // inherit definitions
        List<PaoStore> finalPaoStores = new ArrayList<PaoStore>();
        for (PaoStore paoStore : sortedPaoStores.values()) {
        	
        	mergeInheritedPaoStoresOntoPaoStore(paoStore, sortedPaoStores);
        	
        	if (!paoStore.isAbstract()) {
        		finalPaoStores.add(paoStore);
        	}
    	}
        
        // final paos are ready to have data added to the dao data maps 
        for (PaoStore paoStore : finalPaoStores) {
            try {
                this.addPao(paoStore);
            } catch (Exception e) {
                log.error("type will be unavailable and PAO definitions may be inconsistent: " + paoStore.getId() , e);
            }
        }
        
        Builder<PaoType, Attribute> builder = ImmutableListMultimap.builder();
        for (Map.Entry<PaoType, Map<Attribute, AttributeDefinition>> entry : paoAttributeAttrDefinitionMap.entrySet()) {
            builder.putAll(entry.getKey(), entry.getValue().keySet());
        }
        paoTypeAttributesMultiMap = builder.build();
    }
    
    private void mergeInheritedPaoStoresOntoPaoStore(PaoStore paoStore, Map<String, PaoStore> paoStores) {
    	
    	List<String> inheritedIds = paoStore.getInheritedIds();
    	for (String inheritedId : inheritedIds) {
    		
    		PaoStore inheritedPaoStore = paoStores.get(inheritedId);
    		mergeInheritedPaoStoresOntoPaoStore(inheritedPaoStore, paoStores);
    		paoStore.mergePaoStore(inheritedPaoStore);
    	}
    }
    
    private Map<String, PaoStore> mergeDefinitionResourcesIntoPaoStoreMap(List<Resource> definitionResources) throws Exception {
		
    	Map<String, PaoStore> paoStores = new LinkedHashMap<String, PaoStore>();
    	
    	int resourceCount = 0;
    	boolean isLastResource = false;
    	for (Resource definitionResource : definitionResources) {
    	
    		resourceCount++;
    		if (resourceCount == definitionResources.size()) {
    			isLastResource = true;
    		}
    		
			validateXmlSchema(definitionResource);
			InputStreamReader reader = new InputStreamReader(definitionResource.getInputStream());
			
			try {
	            
	            PaoDefinitions definition = (PaoDefinitions) Unmarshaller.unmarshal(PaoDefinitions.class, reader);
	            
	            for (Pao pao : definition.getPao()) {
	        		if (pao.getEnabled()) {
	        			
	        			PaoStore paoStore = new PaoStore(pao);
	        			String id = paoStore.getId();
	        			
	        			if (paoStores.get(id) == null) {
	        				paoStores.put(id, paoStore);
	        			} else {
	        				PaoStore existingPaoStore = paoStores.get(id);
	        				existingPaoStore.mergePaoStore(paoStore);
	        			}
	        			
	        			if (isLastResource) {
		        			this.fileIdOrder.add(id);
		        		}
	        		}
	            }
			} finally {
				try {
	                if (reader != null) {
	                    reader.close();
	                }
	            } catch (IOException e) {}
			}
    	}
    	
    	return paoStores;
    }
    
    public void validateXmlSchema(Resource currentDefinitionResource) throws IOException, SAXException, ParserConfigurationException {
        InputStream is = currentDefinitionResource.getInputStream();
        URL schemaUrl = schemaFile.getURL();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false); // this is for DTD, so we want it off
        factory.setNamespaceAware(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaUrl);
        factory.setSchema(schema);

        SAXParser saxParser = factory.newSAXParser();

        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setErrorHandler(new ErrorHandler() {

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        
        
        xmlReader.parse(new InputSource(is));
        is.close();
    }
    
    // ADD PAO
    //============================================
    /**
     * Helper method to add the pao and its point templates to the
     * appropriate maps
     * @param pao - Pao to add
     */
    private void addPao(PaoStore pao) {

        PaoType paoType = pao.getPaoType();
        String javaConstant = pao.getId();

        String displayName = null;
        if (pao.getDisplayName() != null) {
            displayName = pao.getDisplayName();
        }
        String group = pao.getDisplayGroup();

        PaoDefinition paoDefinition = new PaoDefinitionImpl(paoType,
                                                            displayName,
                                                            group,
                                                            javaConstant,
                                                            pao.getChangeGroup(),
                                                            pao.isCreatable());

        // Add paoDefinition to type map
        this.paoTypeMap.put(paoType, paoDefinition);

        // Add paoDefinition to change group map
        if (pao.getChangeGroup() != null) {
            String changeGroup = pao.getChangeGroup();

            this.changeGroupPaosMap.put(changeGroup, paoDefinition);
        }

        // Add paoDefinition to group map
        if (group != null) {
            this.paoDisplayGroupMap.put(group, paoDefinition);
        }

        // Add pao points
        Map<String, PointTemplate> pointNameTemplateMap = new HashMap<String, PointTemplate>();

        List<Point> points = pao.getEnabledPoints();
        for (Point point : points) {
            PointTemplate template = this.createPointTemplate(point);
            this.paoAllPointTemplateMap.put(paoType, template);

            if (point.getInit()) {
            	this.paoInitPointTemplateMap.put(paoType, template);
            }
            
            if (pointNameTemplateMap.containsKey(template.getName())) {
                throw new RuntimeException("Point name: " + template.getName() + " is used twice for pao type: " + javaConstant + " in the paoDefinition.xml file - point names must be unique within a pao type");
            }
            pointNameTemplateMap.put(template.getName(), template);
        }
        
        // Add pao attributes
        Map<Attribute, AttributeDefinition> attributeMap = new HashMap<Attribute, AttributeDefinition>();
        List<com.cannontech.common.pao.definition.model.castor.Attribute> attributes = pao.getEnabledAttributes();
        for (com.cannontech.common.pao.definition.model.castor.Attribute attribute : attributes) {
        	
        	AttributeDefinition attributeDefinition = 
        		createAttributeDefinition(attribute.getName(), attribute.getChoiceValue(), pointNameTemplateMap);
        	Attribute pointAttribute = BuiltInAttribute.valueOf(attribute.getName());
        	
            if (attributeMap.containsKey(pointAttribute)) {
                throw new RuntimeException("Attribute: " + attribute.getName() + " is used twice for pao type: " + javaConstant + " in the paoDefinition.xml file - attribute names must be unique within a pao type");
            }
        	attributeMap.put(pointAttribute, attributeDefinition);
        }   
        this.paoAttributeAttrDefinitionMap.put(paoType, attributeMap);

        // Add pao commands
        List<Command> commands = pao.getEnabledCommands();
        for (Command command : commands) {
            CommandDefinition definition = this.createCommandDefinition(pao.getDisplayName(), 
                                                                        command,
                                                                        pointNameTemplateMap);
            this.paoCommandMap.put(paoType, definition);
        }
        
        // Add pao tags
        List<Tag> tags = pao.getTags();
        ImmutableBiMap.Builder<PaoTag, PaoTagDefinition> supportedTagsBuilder = ImmutableBiMap.builder();
        for (Tag tag : tags) {
            if (tag.getSupports()) {
                PaoTagDefinition definition = this.createPaoTagDefinition(tag.getName(), tag.getValue(), paoType.toString());
                supportedTagsBuilder.put(definition.getTag(), definition);
                typesBySupportedTag.get(definition.getTag()).put(paoType, paoDefinition);
            }
        }
        supportedTagsByType.put(paoType, supportedTagsBuilder.build());
        
        // Add pao to creatable list
        if( paoDefinition.isCreatable()) {
            this.creatablePaoDefinitions.add(paoDefinition);
        }
    }

    /**
     * Helper method to convert a castor AttributeLookup CHOICE object to a AttributeLookup definition
     * @param choiceLookup - The choice object from the attribute lookup
     * @param pointNameTemplateMap 
     * @return The AttributeLookup definition representing the castor CHOICE lookup
     */
	private AttributeDefinition createAttributeDefinition(String attributeName,
            Object choiceLookup, Map<String, PointTemplate> pointNameTemplateMap) {

        AttributeDefinition attributeDefinition = null;
        Attribute attribute = BuiltInAttribute.valueOf(attributeName);

        if (choiceLookup instanceof BasicLookup) {
            BasicLookup lookup = (BasicLookup) choiceLookup;
            PointTemplate pointTemplate = pointNameTemplateMap.get(lookup.getPoint());
            if (pointTemplate == null) {
                throw new IllegalArgumentException("Can't resolve point name '" + lookup.getPoint() + ":");
            }
            
            attributeDefinition = new BasicAttributeDefinition(attribute, pointTemplate);
        } else if(choiceLookup instanceof MappedLookup) {
            MappedLookup lookup = (MappedLookup) choiceLookup;
            TypeFilter typeFilter = lookup.getTypeFilter();
            String typeString = typeFilter.getType();
            FilterType filterType = FilterType.valueOf(typeString);
            attributeDefinition = new MappedAttributeDefinition(attribute, filterType, extraPaoPointAssignmentDao);
        }

        if (attributeDefinition != null) {
            return attributeDefinition;
        }

        throw new IllegalArgumentException("Attribute Choice '" + choiceLookup.toString() + "' is not supported.");
    }
	
    /**
     * Helper method to convert a castor command to a command definition
     * @param paoName - Name of pao for commands
     * @param command - Command to convert
     * @param pointNameTemplateMap
     * @return The command definition representing the castor command
     */
    private CommandDefinition createCommandDefinition(String paoName, Command command,
            Map<String, PointTemplate> pointNameTemplateMap) {

        CommandDefinition definition = new CommandDefinition(command.getName());

        // Add command text
        Cmd[] cmds = command.getCmd();
        for (Cmd cmd : cmds) {
            definition.addCommandString(cmd.getText());
        }

        // Add point reference
        PointRef[] pointRefs = command.getPointRef();
        for (PointRef pointRef : pointRefs) {
            if (!pointNameTemplateMap.containsKey(pointRef.getName())) {
                throw new RuntimeException("Point name: " + pointRef.getName() + " not found for pao: " + paoName + ".  command pointRefs must reference a point name from the same pao in the paoDefinition.xml file. Point is not on pao, or point has been named incorrectly in a custom file.");
            }
            PointTemplate template = pointNameTemplateMap.get(pointRef.getName());
            PointIdentifier pi = new PointIdentifier(template.getPointType(), template.getOffset());
            definition.addAffectedPoint(pi);
        }

        return definition;
    }

    /**
     * Helper method to convert a castor tag to tag definition.
     * @param tagName
     * @param value
     * @param context 
     * @return
     */
    private PaoTagDefinition createPaoTagDefinition(String tagName, String value, String context) {
    	
    	PaoTag tag = PaoTag.valueOf(tagName);
    	Object convertedValue = null;
    	if (tag.isTagHasValue()) {
    	    convertedValue = InputTypeFactory.convertPropertyValue(tag.getValueType(), value);
            if (convertedValue == null) {
                throw new RuntimeException("converted value of " + tag + " for " + context + " was null");
            }
    	} else {
    	    if (StringUtils.isNotBlank(value)) {
    	        throw new RuntimeException("tag " + tag + " for " + context + " has value, but it is not allowed to");
    	    }
    	}
    	PaoTagDefinition definition = new PaoTagDefinition(tag, convertedValue);
    	return definition;
    }

    /**
     * Helper method to convert a castor point to a point template
     * @param point - Point to convert
     * @return The point template representing the castor point
     */
    private PointTemplate createPointTemplate(Point point) {

        PointTemplate template = new PointTemplate(PointType.getForString(point.getType()), point.getOffset());

        template.setName(point.getName());

        double multiplier = 1.0;
        int unitOfMeasure = PointUnits.UOMID_INVALID;
        int decimalPlaces = PointUnit.DEFAULT_DECIMAL_PLACES;
        if (point.getPointChoice().getPointChoiceSequence() != null) {
            if (point.getPointChoice().getPointChoiceSequence().getMultiplier() != null) {
                multiplier = point.getPointChoice()
                                  .getPointChoiceSequence()
                                  .getMultiplier()
                                  .getValue()
                                  .doubleValue();
            }
            if (point.getPointChoice().getPointChoiceSequence().getUnitofmeasure() != null) {
                String unitOfMeasureName = point.getPointChoice()
                                                .getPointChoiceSequence()
                                                .getUnitofmeasure()
                                                .getValue();
                LiteUnitMeasure unitMeasure = null;
                try {
                    unitMeasure = unitMeasureDao.getLiteUnitMeasure(unitOfMeasureName);
                } catch (EmptyResultDataAccessException e) {
                    throw new NotFoundException("Unit of measure does not exist: "
                            + unitOfMeasureName + ". Check the paoDefinition.xml file ", e);
                }
                unitOfMeasure = unitMeasure.getUomID();
            }
            if (point.getPointChoice().getPointChoiceSequence().getDecimalplaces() != null) {
                decimalPlaces = point.getPointChoice().getPointChoiceSequence().getDecimalplaces().getValue();
            }
        }
        template.setMultiplier(multiplier);
        template.setUnitOfMeasure(unitOfMeasure);
        template.setDecimalPlaces(decimalPlaces);

        ControlType controlType = ControlType.valueOf(point.getControltype());
        template.setControlType(controlType);
        
        Archive archive = point.getArchive();
        if (archive != null) {
        	template.setPointArchiveType(PointArchiveType.valueOf(archive.getType()));
        	template.setPointArchiveInterval(PointArchiveInterval.valueOf(archive.getInterval()));
        }
        
        int stateGroupId = StateGroupUtils.SYSTEM_STATEGROUPID;
        if (point.getPointChoice().getStategroup() != null) {

            String stateGroupName = point.getPointChoice().getStategroup().getValue();
            LiteStateGroup stateGroup = null;
            try {
                stateGroup = stateDao.getLiteStateGroup(stateGroupName);
            } catch (NotFoundException e) {
                throw new NotFoundException("State group does not exist: " + stateGroupName
                        + ". Check the paoDefinition.xml file ", e);
            }
            stateGroupId = stateGroup.getStateGroupID();
        }
        template.setStateGroupId(stateGroupId);

        return template;
    }
    
    @Autowired
    public void setExtraPaoPointAssignmentDao(ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao) {
        this.extraPaoPointAssignmentDao = extraPaoPointAssignmentDao;
    }
}