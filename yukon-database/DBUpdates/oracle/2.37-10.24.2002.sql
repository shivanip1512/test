/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/

insert into CTIDatabase values('2.37', 'Ryan', '24-OCT-2002', 'Added ExpressCom views, columns to Dynamic Cap Control, column to GraphDataSeries.');



rename lmgroupexpresscomm to LMGroupExpresscom;
rename lmgroupexpresscommaddress to lmgroupexpresscomaddress;
/

/* Views for the ExpressComm tables */
create view ServiceAddress_View as 
select x.LMGroupID, a.address as ServiceAddress 
from lmgroupexpresscom x, lmgroupexpresscomaddress a 
where (x.serviceproviderid=a.addressid and (a.addresstype='SERVICE' or a.addressid=0) );

create view GeoAddress_View as 
select x.LMGroupID, a.address as GeoAddress 
from lmgroupexpresscom x, lmgroupexpresscomaddress a 
where (x.geoid=a.addressid and (a.addresstype='GEO' or a.addressid=0) );

create view SubstationAddress_View as 
select x.LMGroupID, a.address as SubstationAddress 
from lmgroupexpresscom x, lmgroupexpresscomaddress a 
where (x.substationid=a.addressid and (a.addresstype='SUBSTATION' or a.addressid=0) );

create view FeederAddress_View as 
select x.LMGroupID,  a.address as FeederAddress 
from lmgroupexpresscom x, lmgroupexpresscomaddress a 
where (x.feederid=a.addressid and (a.addresstype='FEEDER' or a.addressid=0) );

create view ProgramAddress_View as 
select x.LMGroupID, a.address as ProgramAddress 
from lmgroupexpresscom x, lmgroupexpresscomaddress a 
where (x.programid=a.addressid and (a.addresstype='PROGRAM' or a.addressid=0) );


create view expresscomAddress_View as 
select x.LMGroupID, x.routeid, x.serialnumber, s.serviceaddress, g.geoaddress, 
b.substationaddress, f.feederaddress, x.zipcodeaddress, x.udaddress, p.programaddress, x.splinteraddress,
x.addressusage, x.relayusage 
from lmgroupexpresscom x, ServiceAddress_View s, GeoAddress_View g, 
SubstationAddress_View b, FeederAddress_View f, ProgramAddress_View p 
where x.lmgroupid=s.lmgroupid 
and x.lmgroupid=g.lmgroupid 
and x.lmgroupid=b.lmgroupid 
and x.lmgroupid=f.lmgroupid 
and x.lmgroupid=p.lmgroupid;


INSERT INTO UnitMeasure VALUES( 46,'PPM',0,'Parts Per Million','(none)');
INSERT INTO UnitMeasure VALUES( 47,'MPH',0,'Miles Per Hour','(none)');
INSERT INTO UnitMeasure VALUES( 48,'Inches',0,'Inches','(none)');
INSERT INTO UnitMeasure VALUES( 49,'KPH',0,'Kilometers Per Hour','(none)');
INSERT INTO UnitMeasure VALUES( 50,'Milibars',0,'Milibars','(none)');



alter TABLE DynamicCCSubstationBus add CurrentVarPointQuality NUMBER;
update DynamicCCSubstationBus set CurrentVarPointQuality = 0;
alter TABLE DynamicCCSubstationBus MODIFY CurrentVarPointQuality NOT NULL;


alter TABLE DynamicCCFeeder add CurrentVarPointQuality NUMBER;
update DynamicCCFeeder set CurrentVarPointQuality = 0;
alter TABLE DynamicCCFeeder MODIFY CurrentVarPointQuality NOT NULL;


alter TABLE graphdataseries add Multiplier float;
update graphdataseries set Multiplier = 1.0;
alter TABLE graphdataseries MODIFY Multiplier NOT NULL;

