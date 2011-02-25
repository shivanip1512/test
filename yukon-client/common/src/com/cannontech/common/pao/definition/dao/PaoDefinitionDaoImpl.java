package com.cannontech.common.pao.definition.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
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
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

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
    private Map<PaoType, Set<PointTemplate>> paoAllPointTemplateMap = null;
    private Map<PaoType, Set<PointTemplate>> paoInitPointTemplateMap = null;
    private Map<PaoType, PaoDefinition> paoTypeMap = null;
    private ListMultimap<String, PaoDefinition> paoDisplayGroupMap = null;
    private Map<String, Set<PaoDefinition>> changeGroupPaosMap = null;
    private Map<PaoType, Set<CommandDefinition>> paoCommandMap = null;
    private Map<PaoType, Set<PaoTagDefinition>> paoFeatureMap = null;
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

    	int pointType = pointIdentifier.getPointType().getPointTypeId();
    	int offset = pointIdentifier.getOffset();

        Set<PointTemplate> allPointTemplates = this.getAllPointTemplates(paoType);

        for (PointTemplate template : allPointTemplates) {

            if (template.getPointType().getPointTypeId() == pointType && template.getOffset() == offset) {
                return template;
            }
        }

        String pointTypeString = PointTypes.getType(pointType);

        throw new NotFoundException("Point template not found for pao type: " + paoType + ", point type: " + pointTypeString + ", offset: " + offset);
    }
    
    @Override
    public Set<PointTemplate> getAllPointTemplates(PaoType paoType) {
        if (this.paoAllPointTemplateMap.containsKey(paoType)) {
            Set<PointTemplate> templates = this.paoAllPointTemplateMap.get(paoType);
            Set<PointTemplate> returnSet = new HashSet<PointTemplate>();
            for (PointTemplate template : templates) {
                returnSet.add(template);
            }
            return returnSet;
        } else {
            throw new IllegalArgumentException("Pao type "
                    + paoType + " is not supported.");
        }
    }

    @Override
    public Set<PointTemplate> getInitPointTemplates(PaoType paoType) {

        if (this.paoInitPointTemplateMap.containsKey(paoType)) {
        	Set<PointTemplate> templates = this.paoInitPointTemplateMap.get(paoType);
        	Set<PointTemplate> returnSet = new HashSet<PointTemplate>();
        	for (PointTemplate template : templates) {
                returnSet.add(template);
            }
        	return returnSet;
        } else {
            throw new IllegalArgumentException("Pao type " + paoType + " is not supported.");
        }
    }
    
    // COMMANDS
    //============================================
    @Override
    public Set<CommandDefinition> getCommandsThatAffectPoints(PaoType paoType, Set<? extends PointIdentifier> pointSet) {

        Set<CommandDefinition> commandSet = new HashSet<CommandDefinition>();

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
        return allCommandSet;
    }
    
    // TAGS
    //============================================
    public Set<PaoTag> getSupportedTags(PaoType paoType) {
    	
    	return getSupportedTagsForPaoType(paoType);
    }
    
    @Override
    public Set<PaoTag> getSupportedTags(PaoDefinition paoDefiniton) {
    	
    	return getSupportedTagsForPaoType(paoDefiniton.getType());
    }
    
    @Override
    public Set<PaoDefinition> getPaosThatSupportTag(PaoTag feature) {
    	
    	Set<PaoDefinition> definitions = new HashSet<PaoDefinition>();
    	for (PaoType paoType : this.paoFeatureMap.keySet()) {
    		
    		Set<PaoTagDefinition> allPaoFeatureDefinitionsSet = this.paoFeatureMap.get(paoType);
    		for (PaoTagDefinition featureDefinition : allPaoFeatureDefinitionsSet) {
    			
    			if (featureDefinition.getTag().equals(feature) && featureDefinition.isTagTrue()) {
    				definitions.add(getPaoDefinition(paoType));
    				break;
    			}
    		}
    	}
    	return definitions;
    }
    
    @Override
    public List<PaoType> getPaoTypesThatSupportTag(PaoTag feature) {
    	
        Set<PaoDefinition> definitions = getPaosThatSupportTag(feature);
        ObjectMapper<PaoDefinition, PaoType> objectMapper = new ObjectMapper<PaoDefinition, PaoType>() {
            public PaoType map(PaoDefinition from) throws ObjectMappingException {
                PaoType paoType = from.getType();
                return paoType;
            }
        };
        List<PaoType> paoTypes = new MappingList<PaoDefinition, PaoType>(Lists.newArrayList(definitions), objectMapper);
        return paoTypes;
    }
    
    @Override
    public <T extends YukonPao> Iterable<T> filterPaosForTag(Iterable<T> paos, final PaoTag feature) {
        Predicate<YukonPao> supportsTagPredicate = new Predicate<YukonPao>() {
            @Override
            public boolean apply(YukonPao input) {
                return isTagSupported(input.getPaoIdentifier().getPaoType(), feature);
            }
        };
        
        return Iterables.filter(paos, supportsTagPredicate);    
    }

    @Override
    public Set<PaoDefinition> getCreatablePaoDefinitions() {
        return creatablePaoDefinitions;
    }
    
    @Override
    public boolean isTagSupported(PaoDefinition paoDefiniton, PaoTag feature) {
    	return isTagSupported(paoDefiniton.getType(), feature);
    }
    
    @Override
    public boolean isTagSupported(PaoType paoType, PaoTag feature) {
    	
    	Set<PaoTagDefinition> allPaoFeatureDefinitionsSet = this.paoFeatureMap.get(paoType);
    	if (allPaoFeatureDefinitionsSet != null) {
    		for (PaoTagDefinition featureDefinition : allPaoFeatureDefinitionsSet) {
    			if (featureDefinition.getTag().equals(feature) && featureDefinition.isTagTrue()) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    private Set<PaoTag> getSupportedTagsForPaoType(PaoType paoType) {
    	
    	Set<PaoTag> supportedPaoFeaturesSet = new HashSet<PaoTag>();
    	Set<PaoTagDefinition> allPaoFeatureDefinitionsSet = this.paoFeatureMap.get(paoType);
    	
    	if (allPaoFeatureDefinitionsSet != null) {
	    	for (PaoTagDefinition featureDefinition : allPaoFeatureDefinitionsSet) {
	    		if (featureDefinition.isTagTrue()) {
	    			supportedPaoFeaturesSet.add(featureDefinition.getTag());
	    		}
	    	}
    	}
    	return supportedPaoFeaturesSet;
    }
    
    // DEFINITIONS
    //============================================
    @Override
    public Set<PaoDefinition> getAllPaoDefinitions() {
    	
    	Set<PaoDefinition> allDefinitions = Collections.unmodifiableSet(new LinkedHashSet<PaoDefinition>(this.paoTypeMap.values()));
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
            Set<PaoDefinition> returnDefinitions = new HashSet<PaoDefinition>();
            for (PaoDefinition definition : definitions) {
                if (!definition.equals(paoDefinition)) {
                    returnDefinitions.add(definition);
                }
            }
            return returnDefinitions;
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

        this.paoTypeMap = new LinkedHashMap<PaoType, PaoDefinition>();
        this.paoAllPointTemplateMap = new HashMap<PaoType, Set<PointTemplate>>();
        this.paoInitPointTemplateMap = new HashMap<PaoType, Set<PointTemplate>>();
        this.paoDisplayGroupMap = LinkedListMultimap.create();
        this.changeGroupPaosMap = new HashMap<String, Set<PaoDefinition>>();
        this.paoAttributeAttrDefinitionMap = new HashMap<PaoType, Map<Attribute, AttributeDefinition>>();
        this.paoCommandMap = new HashMap<PaoType, Set<CommandDefinition>>();
        this.paoFeatureMap = new HashMap<PaoType, Set<PaoTagDefinition>>();
        this.creatablePaoDefinitions = new HashSet<PaoDefinition>();
        this.fileIdOrder = new ArrayList<String>();
        
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
            this.addPao(paoStore);
        }
        
// this junk should probably stick around for a while during 4.3 development, makes it easy to compare dao output to earlier versions
//      //TEST FILE
//        BufferedWriter out = new BufferedWriter(new FileWriter("c:\\paoDefinition_NEW-6.txt"));
//        
//        List<PaoDefinition> definitionList = new ArrayList<PaoDefinition>(this.paoTypeMap.values());
//        Collections.sort(definitionList);
//        for (PaoDefinition definition : definitionList) {
//        	
//        	PaoType paoType = definition.getType();
//        	Set<PointTemplate> pointTemplates = this.paoAllPointTemplateMap.get(paoType);
//        	Set<PointTemplate> initPointTemplates = this.paoInitPointTemplateMap.get(paoType);
//        	Set<CommandDefinition> commandDefinitions = this.paoCommandMap.get(paoType);
//        	Map<Attribute, AttributeDefinition> attributesMap = this.paoAttributeAttrDefinitionMap.get(paoType);
//        	
//        	// definition
//            out.write("definition:type" + "\t\t" + definition.getType().getPaoTypeId() + "\n");
//            out.write("definition:changeGroup" + "\t\t" + definition.getChangeGroup() + "\n");
//            out.write("definition:displayGroup" + "\t\t" + definition.getDisplayGroup() + "\n");
//            out.write("definition:displayName" + "\t\t" + definition.getDisplayName() + "\n");
//            out.write("definition:javaConstant" + "\t\t" + definition.getJavaConstant() + "\n");
//            
//            // points
//            List<PointTemplate> pointTemplatesList = new ArrayList<PointTemplate>(pointTemplates);
//            Collections.sort(pointTemplatesList);
//            for (PointTemplate pointTemplate : pointTemplatesList) {
//            	
//            	out.write("pointTemplate:name" + "\t\t" + pointTemplate.getName() + "\n");
//                out.write("pointTemplate:type" + "\t\t" + pointTemplate.getType() + "\n");
//                out.write("pointTemplate:offset" + "\t\t" + pointTemplate.getOffset() + "\n");
//                out.write("pointTemplate:multiplier" + "\t\t" + pointTemplate.getMultiplier() + "\n");
//                out.write("pointTemplate:stateGroup" + "\t\t" + pointTemplate.getStateGroupId() + "\n");
//                out.write("pointTemplate:uom" + "\t\t" + pointTemplate.getUnitOfMeasure() + "\n");
//                
//                if (initPointTemplates.contains(pointTemplate)) {
//                	out.write("pointTemplate:init" + "\t\t" + true + "\n");
//                } else {
//                	out.write("pointTemplate:init" + "\t\t" + false + "\n");
//                }
//            }
//            
//            // commands
//            List<CommandDefinition> commandDefinitionsList = new ArrayList<CommandDefinition>(commandDefinitions);
//            Collections.sort(commandDefinitionsList);
//            for (CommandDefinition commandDefinition : commandDefinitionsList) {
//            	
//            	out.write("commandDefinition:name" + "\t\t" + commandDefinition.getName() + "\n");
//            	
//            	Set<PointIdentifier> affectedPointSet = commandDefinition.getAffectedPointList();
//            	List<PointIdentifier> affectedPointList = new ArrayList<PointIdentifier>(affectedPointSet);
//            	Collections.sort(affectedPointList);
//            	for (PointIdentifier affectedPoint : affectedPointList) {
//            		
//            		out.write("commandDefinition:affectedPoint:type" + "\t\t" + affectedPoint.getType() + "\n");
//            		out.write("commandDefinition:affectedPoint:offset" + "\t\t" + affectedPoint.getOffset() + "\n");
//            	}
//            	
//            	List<String> commandStringList = commandDefinition.getCommandStringList();
//            	Collections.sort(commandStringList);
//            	for (String commandstring : commandStringList) {
//            		
//            		out.write("commandDefinition:commandstring" + "\t\t" + commandstring + "\n");
//            	}
//            }
//            
//            // attributes
//            List<AttributeDefinition> attributeLookupList = new ArrayList<AttributeDefinition>(attributesMap.values());
//            //Collections.sort(attributeLookupList);
//            for (AttributeDefinition attributeLookup : attributeLookupList) {
//
//        		out.write("attribute:key" + "\t\t" + attributeLookup.getAttribute().getKey() + "\n");
//        		out.write("attribute:description" + "\t\t" + attributeLookup.getAttribute().getDescription() + "\n");
//        		out.write("attribute:attributeLookup" + "\t\t" + "---" + "\n");
//        	}
//            
//            out.write("\n\n");
//        }
//        out.flush();
//        out.close();
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
        String group = null;
        if (pao.getDisplayGroup() != null) {
            group = pao.getDisplayGroup();
        }

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

            // Add the paoClass to the hashmap if not there
            if (!this.changeGroupPaosMap.containsKey(changeGroup)) {
                this.changeGroupPaosMap.put(changeGroup, new HashSet<PaoDefinition>());
            }

            this.changeGroupPaosMap.get(changeGroup).add(paoDefinition);

        }

        // Add paoDefinition to group map
        this.paoDisplayGroupMap.put(group, paoDefinition);

        // Add pao points
        Set<PointTemplate> pointSet = new HashSet<PointTemplate>();
        Set<PointTemplate> initPointSet = new HashSet<PointTemplate>();

        Map<String, PointTemplate> pointNameTemplateMap = new HashMap<String, PointTemplate>();

        Point[] points = pao.getEnabledPoints();
        for (Point point : points) {
            PointTemplate template = this.createPointTemplate(point);
            pointSet.add(template);

            if (point.getInit()) {
            	initPointSet.add(template);
            }
            
            if (pointNameTemplateMap.containsKey(template.getName())) {
                throw new RuntimeException("Point name: " + template.getName() + " is used twice for pao type: " + javaConstant + " in the paoDefinition.xml file - point names must be unique within a pao type");
            }
            pointNameTemplateMap.put(template.getName(), template);
        }
        this.paoAllPointTemplateMap.put(paoType, pointSet);
        this.paoInitPointTemplateMap.put(paoType, initPointSet);
        
        // Add pao attributes
        Map<Attribute, AttributeDefinition> attributeMap = new HashMap<Attribute, AttributeDefinition>();
        com.cannontech.common.pao.definition.model.castor.Attribute[] attributes = pao.getEnabledAttributes();
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
        Set<CommandDefinition> commandSet = new HashSet<CommandDefinition>();
        Command[] commands = pao.getEnabledCommands();
        for (Command command : commands) {
            CommandDefinition definition = this.createCommandDefinition(pao.getDisplayName(), 
                                                                        command,
                                                                        pointNameTemplateMap);
            commandSet.add(definition);
        }
        this.paoCommandMap.put(paoType, commandSet);
        
        // Add pao features
        Set<PaoTagDefinition> featureSet = new HashSet<PaoTagDefinition>();
        Tag[] tags = pao.getEnabledTags();
        for (Tag tag : tags) {
            PaoTagDefinition definition = this.createPaoTagDefinition(tag.getName(), tag.getValue());
            
            featureSet.add(definition);
        }
        this.paoFeatureMap.put(paoType, featureSet);
        
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
     * Helper method to convert a castor feature to feature definition.
     * @param featureName
     * @param value
     * @return
     */
    private PaoTagDefinition createPaoTagDefinition(String tagName, boolean value) {
    	
    	PaoTag feature = PaoTag.valueOf(tagName);
    	PaoTagDefinition definition = new PaoTagDefinition(feature, value);
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