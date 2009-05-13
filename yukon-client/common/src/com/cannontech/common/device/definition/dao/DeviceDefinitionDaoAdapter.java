package com.cannontech.common.device.definition.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceFeature;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class DeviceDefinitionDaoAdapter implements DeviceDefinitionDao {

	@Override
	public Set<DeviceDefinition> getAllDeviceDefinitions() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PointTemplate> getAllPointTemplates(YukonDevice device) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PointTemplate> getAllPointTemplates(
			DeviceDefinition deviceDefiniton) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<Attribute> getAvailableAttributes(YukonDevice meter) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<Attribute> getAvailableAttributes(
			DeviceDefinition deviceDefinition) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<CommandDefinition> getAvailableCommands(
			DeviceDefinition newDefinition) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<CommandDefinition> getCommandsThatAffectPoints(
			YukonDevice device, Set<? extends DevicePointIdentifier> pointSet) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public DeviceDefinition getDeviceDefinition(YukonDevice device) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public DeviceDefinition getDeviceDefinition(int deviceType) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<DevicePointIdentifier> getDevicePointIdentifierForAttributes(
			YukonDevice device, Set<? extends Attribute> attributes) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<DeviceDefinition> getDevicesThatDeviceCanChangeTo(
			DeviceDefinition deviceDefinition) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<DeviceDefinition> getDevicesThatSupportFeature(
			DeviceFeature feature) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<PointTemplate> getInitPointTemplates(YukonDevice device) {
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
	public PointTemplate getPointTemplateByTypeAndOffset(YukonDevice device,
			Integer offset, Integer pointType) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PointTemplate getPointTemplateForAttribute(YukonDevice device,
			Attribute attribute) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<DeviceFeature> getSupportedFeatures(YukonDevice device) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<DeviceFeature> getSupportedFeatures(
			DeviceDefinition deviceDefiniton) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isFeatureSupported(YukonDevice device, DeviceFeature feature) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isFeatureSupported(DeviceDefinition deviceDefiniton,
			DeviceFeature feature) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isFeatureSupported(LiteYukonPAObject litePao,
			DeviceFeature feature) {
		throw new UnsupportedOperationException("not implemented");
	}

}
