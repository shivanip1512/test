package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.AreaDao;
import com.cannontech.capcontrol.dao.providers.fields.AreaFields;
import com.cannontech.capcontrol.model.Area;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class AreaProvider implements PaoTypeProvider<AreaFields> {

	private AreaDao areaDao;
	
	private Area createArea(PaoIdentifier paoIdentifier, AreaFields fields) {
		Area area = new Area(paoIdentifier);
		
		area.setVoltReductionPointId(fields.getVoltReductionPointId());
		
		return area;
	}
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.CAPCONTROLAREA;
	};

	@Override
	public Class<AreaFields> getRequiredFields() {
		return AreaFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, AreaFields fields) {
		Area area = createArea(paoIdentifier, fields);
		
		areaDao.add(area);
	}

	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, AreaFields fields) {
		Area area = createArea(paoIdentifier, fields);
		
		areaDao.update(area);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		Area area = new Area(paoIdentifier);
		
		areaDao.remove(area);
	}
	
	@Autowired
	public void setAreaDao(AreaDao areaDao) {
		this.areaDao = areaDao;
	}
}
