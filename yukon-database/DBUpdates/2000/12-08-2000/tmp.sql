insert into deviceloadprofile(deviceid, LASTINTERVALDEMANDRATE, LOADPROFILEDEMANDRATE, LOADPROFILECOLLECTION)
	select deviceid,
	0,
	0,
	'NNNN'
	from device where type='Alpha Meter';