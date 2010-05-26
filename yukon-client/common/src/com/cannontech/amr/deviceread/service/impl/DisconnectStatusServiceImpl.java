package com.cannontech.amr.deviceread.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.model.DisconnectState;
import com.cannontech.amr.deviceread.service.DisconnectStatusService;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;

public class DisconnectStatusServiceImpl implements DisconnectStatusService {
	
	private AttributeService attributeService;
	private DynamicDataSource dynamicDataSource;
	private StateDao stateDao;

	@Override
	public DisconnectState getDisconnectState(Meter meter, CommandResultHolder result) {

		Double stateValue = null;
		LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);

		// result is empty, do lookup on dynamicDataSource
		if (result.getValues().isEmpty()) {

			PointValueHolder pointValue = dynamicDataSource .getPointValue(litePoint.getPointID());
			int stateGroupId = litePoint.getStateGroupID();
			LiteState liteState = stateDao.getLiteState(stateGroupId, (int) pointValue.getValue());

			stateValue = (double) liteState.getStateRawState();
		}

		// else grab from result if correct point is found, otherwise unknown
		else {

			for (PointValueHolder pvh : result.getValues()) {

				int pointId = pvh.getId();
				if (pointId == litePoint.getLiteID()) {
					stateValue = pvh.getValue();
					break;
				}
			}

			if (stateValue == null) {
				return DisconnectState.UNKNOWN;
			}
		}

		return DisconnectState.getDisconneectStateForRawState(stateValue);
	}
	
	@Autowired
	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}
	
	@Autowired
	public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
		this.dynamicDataSource = dynamicDataSource;
	}
	
	@Autowired
	public void setStateDao(StateDao stateDao) {
		this.stateDao = stateDao;
	}
}
