package com.cannontech.common.pao.definition.dao;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTagDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

public class PaoDefinitionDaoAdapter implements PaoDefinitionDao {

	@Override
	public Set<PaoDefinition> getAllPaoDefinitions() {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public Set<AttributeDefinition> getDefinedAttributes(PaoType paoType) {
	    throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public AttributeDefinition getAttributeLookup(PaoType paoType, BuiltInAttribute attribute) {
	    throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PointTemplate> getAllPointTemplates(PaoType paoType) {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public Set<PointTemplate> getAllPointTemplates(
			PaoDefinition paoDefiniton) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<CommandDefinition> getAvailableCommands(
			PaoDefinition newDefinition) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<CommandDefinition> getCommandsThatAffectPoints(PaoType paoType, Set<? extends PointIdentifier> pointSet) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PaoDefinition getPaoDefinition(PaoType paoType) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PaoDefinition> getPaosThatPaoCanChangeTo(
			PaoDefinition paoDefinition) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PaoDefinition> getPaosThatSupportTag(PaoTag tag, PaoTag... otherTags) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PaoType> getPaoTypesThatSupportTag(PaoTag tag, PaoTag... otherTags) {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public Set<PointTemplate> getInitPointTemplates(PaoType paoType) {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public Set<PointTemplate> getInitPointTemplates(
			PaoDefinition newDefinition) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getPointLegendHtml(String displayGroup) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PointTemplate getPointTemplateByTypeAndOffset(PaoType paoType, PointIdentifier pointIdentifier) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PaoTag> getSupportedTags(PaoType paoType) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PaoTag> getSupportedTags(
			PaoDefinition paoDefiniton) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isTagSupported(PaoType paoType, PaoTag tag) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isTagSupported(PaoDefinition paoDefiniton,
			PaoTag tag) {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public long getValueForTagLong(PaoType paoType, PaoTag tag) {
	    return 0;
	}
	
	@Override
	public String getValueForTagString(PaoType paoType, PaoTag tag) {
	    return null;
	}

    @Override
    public <T extends YukonPao> Iterable<T> filterPaosForTag(Iterable<T> paos, PaoTag feature) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Set<PaoDefinition> getCreatablePaoDefinitions() {
        throw new UnsupportedOperationException("not implemented");
    }
    
    @Override
    public Multimap<PaoType, Attribute> getPaoTypeAttributesMultiMap() {
        throw new UnsupportedOperationException("not implemented");
    }
    
    @Override
    public Map<PaoType, Map<Attribute, AttributeDefinition>> getPaoAttributeAttrDefinitionMap() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ImmutableBiMap<PaoTag, PaoTagDefinition> getSupportedTagsForPaoType(PaoType paoType) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public PointIdentifier getPointIdentifierByDefaultName(PaoType key, String defaultPointName) {
        throw new UnsupportedOperationException("not implemented");
    }
}
