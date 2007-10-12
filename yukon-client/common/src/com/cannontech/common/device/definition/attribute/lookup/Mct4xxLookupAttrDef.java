package com.cannontech.common.device.definition.attribute.lookup;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.core.dao.NotFoundException;

public class Mct4xxLookupAttrDef extends AttributeLookup{

	private List<Mapping> mapping;
	
	public class Mapping {

		private String type = null;
		private String pointName = null;
		private boolean defaultMapping = false;
		
		public Mapping(String type, String pointName, boolean defaultMapping) {
			super();
			this.type = type;
			this.pointName = pointName;
			this.defaultMapping = defaultMapping;
		}
		public boolean isDefaultMapping() {
			return defaultMapping;
		}
		public String getPointName() {
			return pointName;
		}
		public String getType() {
			return type;
		}
	}
	/** 
	 * Constructor for Mct4xxAttributeDef
	 * Sets the AttributeLookup to MCT4xx.
	 * @param attribute
	 */
	public Mct4xxLookupAttrDef(Attribute attribute) {
		super(attribute);
	}
	
	public List<Mapping> getMapping() {
		return mapping;
	}
	
	public void setMapping(List<Mapping> mapping) {
		this.mapping = mapping;
	}

	public Mapping getDefaultMapping() {
		for (Mapping map : getMapping()) {
			
			if( map.isDefaultMapping())
				return map;
		}
		throw new NotFoundException("No default map found for attributeDefinition " + getAttribute());
	}
	
	@Override
	public String getPointRefName(YukonDevice device) {
		//TODO - add actual implementation of this feature instead of just the default point.
		return getDefaultMapping().getPointName();
	}
}
