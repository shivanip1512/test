create or replace view Display2WayData_View (PointID, PointName, 
	PointType, PointState, DeviceName, DeviceType, 
	DeviceCurrentState, DeviceID,
	PointValue, PointQuality, PointTimeStamp, UofM ) 
	as
	select point.pointid, point.pointname, point.pointtype, 
	point.serviceflag, device.name, device.type, 
	device.currentstate, device.deviceid,
	'**DYNAMIC**', '**DYNAMIC**',	'**DYNAMIC**', 
	(select unit from pointunit where pointunit.pointid=point.pointid)
	from point, device
	where 
	point.deviceid=device.deviceid
/