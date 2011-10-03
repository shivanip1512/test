package com.cannontech.common.pao.service;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PaoProviderTableEnum implements DatabaseRepresentationSource {
	YUKONPAOBJECT,
	DEVICE,
	DEVICECARRIERSETTINGS,
	DEVICESCANRATE,
	DEVICEWINDOW,
	DEVICEDIALUPSETTINGS,
	DEVICEDIRECTCOMMSETTINGS,
	DEVICECBC,
	DEVICEADDRESS,
	REGULATOR,
	CAPCONTROLAREA,
	CAPCONTROLSPECIALAREA,
	CAPCONTROLSUBSTATION,
	CAPCONTROLSUBSTATIONBUS,
	CAPCONTROLFEEDER,
	CAPBANK,
	CAPBANKADDITIONAL,
	DIGIGATEWAY,
	ZBGATEWAY,
	ZBENDPOINT,
	;
	
	public Object getDatabaseRepresentation() {
	    return name();
	};
}
