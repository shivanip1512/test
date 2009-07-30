package com.cannontech.common.device.definition.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceTag;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class DeviceDefinitionDaoAdapter implements DeviceDefinitionDao {

	@Override
	public Set<DeviceDefinition> getAllDeviceDefinitions() {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public Set<AttributeDefinition> getDefinedAttributes(PaoType deviceType) {
	    throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public AttributeDefinition getAttributeLookup(PaoType deviceType, BuiltInAttribute attribute) {
	    throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PointTemplate> getAllPointTemplates(PaoType deviceType) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PointTemplate> getAllPointTemplates(
			DeviceDefinition deviceDefiniton) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<CommandDefinition> getAvailableCommands(
			DeviceDefinition newDefinition) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<CommandDefinition> getCommandsThatAffectPoints(PaoType deviceType, Set<? extends PointIdentifier> pointSet) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public DeviceDefinition getDeviceDefinition(PaoType deviceType) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<DeviceDefinition> getDevicesThatDeviceCanChangeTo(
			DeviceDefinition deviceDefinition) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<DeviceDefinition> getDevicesThatSupportTag(
			DeviceTag tag) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PointTemplate> getInitPointTemplates(PaoType deviceType) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PointTemplate> getInitPointTemplates(
			DeviceDefinition newDefinition) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getPointLegendHtml(String displayGroup) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PointTemplate getPointTemplateByTypeAndOffset(PaoType deviceType, PointIdentifier pointIdentifier) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<DeviceTag> getSupportedTags(PaoType deviceType) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<DeviceTag> getSupportedTags(
			DeviceDefinition deviceDefiniton) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isTagSupported(PaoType deviceType, DeviceTag tag) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isTagSupported(DeviceDefinition deviceDefiniton,
			DeviceTag tag) {
		throw new UnsupportedOperationException("not implemented");
	}

}
