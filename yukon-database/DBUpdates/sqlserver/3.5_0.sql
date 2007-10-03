/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/*3.4 changes that may have been missed*/

/* Delete roles that are no longer in use. */
delete from YukonGroupRole 		where RoleID in (-204,-205,-301,-302)
delete from YukonRoleProperty 	where RoleID in (-204,-205,-301,-302)
delete from YukonUserRole 		where RoleID in (-204,-205,-301,-302)
delete from YukonRole 			where RoleID in (-204,-205,-301,-302)


/* @error ignore-begin */
insert into YukonRoleProperty values(-100105, -1001, 'Target', 'true', 'display target settings');
go

update devicetypecommand set devicetype='MCT-430A' where devicetype = 'MCT-430EL';
update devicetypecommand set devicetype='MCT-430S4' where devicetype = 'MCT-430LG';
update devicetypecommand set devicetype='MCT-430SN' where devicetype = 'MCT-430IN';
go

insert into YukonRoleProperty values(-21003,-210,'Addtl Order Number Label','Addtl Order Number','Customizable label for the additional order number field.');
insert into YukonRoleProperty values(-20893,-201,'Inventory Checking Create','true','Allow creation of inventory if not found during Inventory Checking');
go

insert into yukonlistentry values (138, 100, 0, 'Float From 16bit', 0);

update point set pointname = 'MultiSpeak' where pointid = -110;
update YukonRole set RoleName = 'MultiSpeak', RoleDescription='MultiSpeak web services interface.' where RoleID = -7;
update YukonRoleProperty set KeyName = 'MultiSpeak Setup', Description='Controls access to configure the Multispeak Interfaces.' where RolePropertyID = -20011;

drop view ccinventory_view;
go

create view CCINVENTORY_VIEW as
SELECT yp4.paoname AS Region, yp3.PAOName AS SubName, yp2.PAOName AS FeederName, yp3.PAObjectID AS subId, yp2.PAObjectID AS fdrId, 
                      yp.PAOName AS CBCName, yp.PAObjectID AS cbcId, yp1.PAOName AS Bankname, yp1.PAObjectID AS bankId, cb.BANKSIZE AS CapBankSize, 
                      fb.ControlOrder AS Sequence, dcb.ControlStatus, cb.SwitchManufacture AS SWMfgr, cb.TypeOfSwitch AS SWType, 
                      cb.OPERATIONALSTATE AS ControlType, cb.ControllerType AS Protocol, pts.IPADDRESS, da.SlaveAddress, 
					  capa.latitude AS LAT, capa.longitude AS LON, capa.drivedirections AS DriveDirection, 
					  cb.maplocationid AS OpCenter, capa.maintenanceareaid AS TA
FROM CAPBANK cb INNER JOIN
                      YukonPAObject yp ON yp.PAObjectID = cb.CONTROLDEVICEID INNER JOIN
                      YukonPAObject yp1 ON yp1.PAObjectID = cb.DEVICEID INNER JOIN
                      DynamicCCCapBank dcb ON dcb.CapBankID = yp1.PAObjectID INNER JOIN
                      STATE s ON s.STATEGROUPID = 3 AND dcb.ControlStatus = s.RAWSTATE INNER JOIN
                      CCFeederBankList fb ON fb.DeviceID = cb.DEVICEID INNER JOIN
                      YukonPAObject yp2 ON yp2.PAObjectID = fb.FeederID INNER JOIN
                      CCFeederSubAssignment sf ON fb.FeederID = sf.FeederID INNER JOIN
                      YukonPAObject yp3 ON yp3.PAObjectID = sf.SubStationBusID INNER JOIN
                      ccsubareaassignment sa on sa.substationbusid = yp3.paobjectid INNER JOIN
                      yukonpaobject yp4 on yp4.paobjectid = sa.areaid INNER JOIN
                      DeviceDirectCommSettings ddcs ON ddcs.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      DeviceAddress da ON da.DeviceID = cb.CONTROLDEVICEID INNER JOIN
                      PORTTERMINALSERVER pts ON pts.PORTID = ddcs.PORTID INNER JOIN
                      DeviceCBC cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      capbankadditional capa on capa.deviceid = cb.deviceid;
go

drop view ccoperations_view;
go

create view CCOPERATIONS_VIEW as
SELECT 
	yp3.PAOName AS cbcName, yp.PAOName AS bankname, el.DateTime AS opTime, el.Text AS operation, 
	el2.DateTime AS confTime, el2.Text AS confStatus, yp1.PAOName AS feederName, yp1.PAObjectID AS feederId, 
        yp2.PAOName AS subName, yp2.PAObjectID AS subBusId, yp4.paoname AS region, cb.BANKSIZE, 
        cb.ControllerType AS protocol, p.Value AS ipAddress, cbc.SERIALNUMBER AS serialNum, da.SlaveAddress, 
        el2.kvarAfter, el2.kvarChange, el2.kvarBefore
FROM   
	(SELECT op.LogID AS oid, MIN(aaa.confid) AS cid FROM
	        (SELECT LogID, PointID FROM CCEventLog 
        WHERE Text LIKE '%Close sent%' OR Text LIKE '%Open sent%') op
        LEFT OUTER JOIN 
        (SELECT el.LogID AS opid, MIN(el2.LogID) AS confid 
        FROM CCEventLog el INNER JOIN CCEventLog el2 ON el2.PointID = el.PointID AND el.LogID < el2.LogID 
        LEFT OUTER JOIN
        (SELECT a.LogID AS aid, MIN(b.LogID) AS next_aid FROM 
        CCEventLog a INNER JOIN CCEventLog b ON a.PointID = b.PointID AND a.LogID < b.LogID 
        WHERE (a.Text LIKE '%Close sent,%' OR a.Text LIKE '%Open sent,%') 
        AND (b.Text LIKE '%Close sent,%' OR b.Text LIKE '%Open sent,%')
        GROUP BY a.LogID) el3 ON el3.aid = el.LogID 
	WHERE (el.Text LIKE '%Close sent,%' OR el.Text LIKE '%Open sent,%') 
        AND (el2.Text LIKE 'Var: %') AND (el2.LogID < el3.next_aid) 
	OR (el.Text LIKE '%Close sent,%' OR el.Text LIKE '%Open sent,%') 
        AND (el2.Text LIKE 'Var: %') AND (el3.next_aid IS NULL)
        GROUP BY el.LogID)  aaa ON op.LogID = aaa.opid
GROUP BY op.LogID) OpConf INNER JOIN
        CCEventLog el ON el.LogID = OpConf.oid LEFT OUTER JOIN
        CCEventLog el2 ON el2.LogID = OpConf.cid INNER JOIN
        POINT ON POINT.POINTID = el.PointID INNER JOIN
        DynamicCCCapBank ON DynamicCCCapBank.CapBankID = POINT.PAObjectID INNER JOIN
        YukonPAObject yp ON yp.PAObjectID = DynamicCCCapBank.CapBankID INNER JOIN
        YukonPAObject yp1 ON yp1.PAObjectID = el.FeederID INNER JOIN
        YukonPAObject yp2 ON yp2.PAObjectID = el.SubID INNER JOIN
        CAPBANK cb ON cb.DEVICEID = DynamicCCCapBank.CapBankID LEFT OUTER JOIN
        DeviceDirectCommSettings ddcs ON ddcs.DEVICEID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        DeviceAddress da ON da.DeviceID = cb.CONTROLDEVICEID INNER JOIN
        YukonPAObject yp3 ON yp3.PAObjectID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        DeviceCBC cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        (SELECT EntryID, PAObjectID, Owner, InfoKey, Value, UpdateTime
        FROM DynamicPAOInfo WHERE (InfoKey LIKE '%udp ip%')) 
        p ON p.PAObjectID = cb.CONTROLDEVICEID LEFT OUTER JOIN
        ccsubareaassignment as csa on csa.substationbusid = el.SubID left outer join 
        YukonPAObject AS yp4 ON yp4.paobjectid = csa.areaid; 
go
/* @error ignore-end */

insert into FDRInterface values (25, 'TRISTATESUB', 'Receive', 'f' );
go
insert into FDRInterfaceOption values(25, 'Point', 1, 'Combo', 'Nucla 115/69 Xfmr.,Happy Canyon 661Idarado,Cascade 115/69 (T2),Ames Generation,Dallas Creek MW,Dallas Creek MV' );
go

insert into YukonRoleProperty values(-10814, -108,'Suppress Error Page Details', 'false', 'Disable stack traces for this user.');
go

insert into BillingFileFormats values(-25,'Itron Register Readings Export');
go

/* @error ignore-begin */
insert into billingfileformats values(-26, 'SIMPLE_TOU_DeviceName');
/* @error ignore-end */
go

alter table ActivityLog alter column Description varchar(240);
go

insert into command values(-130, 'getvalue lp channel ?''Channel #'' ?''Enter Start Date: xx/xx/xxxx'' ?''Enter End Date: xx/xx/xxxx''', 'Read LP Channel Data', 'ALL MCT-4xx Series');
insert into command values(-131, 'getvalue lp status', 'Read LP Channel Data Status', 'ALL MCT-4xx Series');
insert into command values(-132, 'getvalue lp cancel', 'Read LP Channel Data Cancel', 'ALL MCT-4xx Series');
go

insert into devicetypecommand values(-640, -130, 'MCT-410 kWh Only', 21, 'N', -1);
insert into devicetypecommand values(-641, -130, 'MCT-410CL', 27, 'N', -1);
insert into devicetypecommand values(-642, -130, 'MCT-410FL', 27, 'N', -1);
insert into devicetypecommand values(-643, -130, 'MCT-410GL', 27, 'N', -1);
insert into devicetypecommand values(-644, -130, 'MCT-410IL', 27, 'N', -1);
insert into devicetypecommand values(-645, -130, 'MCT-410iLE', 21, 'N', -1);
insert into devicetypecommand values(-646, -130, 'MCT-430A', 23, 'N', -1);
insert into devicetypecommand values(-647, -130, 'MCT-430S4', 23, 'N', -1);
insert into devicetypecommand values(-648, -130, 'MCT-430SN', 23, 'N', -1);
insert into devicetypecommand values(-649, -130, 'MCT-470', 29, 'N', -1);

insert into devicetypecommand values(-650, -131, 'MCT-410 kWh Only', 22, 'N', -1);
insert into devicetypecommand values(-651, -131, 'MCT-410CL', 28, 'N', -1);
insert into devicetypecommand values(-652, -131, 'MCT-410FL', 28, 'N', -1);
insert into devicetypecommand values(-653, -131, 'MCT-410GL', 28, 'N', -1);
insert into devicetypecommand values(-654, -131, 'MCT-410IL', 28, 'N', -1);
insert into devicetypecommand values(-655, -131, 'MCT-410iLE', 22, 'N', -1);
insert into devicetypecommand values(-656, -131, 'MCT-430A', 24, 'N', -1);
insert into devicetypecommand values(-657, -131, 'MCT-430S4', 24, 'N', -1);
insert into devicetypecommand values(-658, -131, 'MCT-430SN', 24, 'N', -1);
insert into devicetypecommand values(-659, -131, 'MCT-470', 30, 'N', -1);

insert into devicetypecommand values(-660, -132, 'MCT-410 kWh Only', 23, 'N', -1);
insert into devicetypecommand values(-661, -132, 'MCT-410CL', 29, 'N', -1);
insert into devicetypecommand values(-662, -132, 'MCT-410FL', 29, 'N', -1);
insert into devicetypecommand values(-663, -132, 'MCT-410GL', 29, 'N', -1);
insert into devicetypecommand values(-664, -132, 'MCT-410IL', 29, 'N', -1);
insert into devicetypecommand values(-665, -132, 'MCT-410iLE', 23, 'N', -1);
insert into devicetypecommand values(-666, -132, 'MCT-430A', 25, 'N', -1);
insert into devicetypecommand values(-667, -132, 'MCT-430S4', 25, 'N', -1);
insert into devicetypecommand values(-668, -132, 'MCT-430SN', 25, 'N', -1);
insert into devicetypecommand values(-669, -132, 'MCT-470', 31, 'N', -1);
go

insert into yukonlistentry values (139, 100, 0, 'Get Point Limit', 0);
insert into yukonlistentry values (140, 100, 0, 'Get Interval Minutes', 0);
insert into yukonlistentry values (141, 100, 0, 'Intervals To Value', 0);
insert into yukonlistentry values (142, 100, 0, 'Linear Slope', 0);
go

alter table CICUSTOMERPOINTDATA drop constraint PK_CICUSTOMERPOINTDATA;
go
alter table CICUSTOMERPOINTDATA add constraint PK_CICUSTOMERPOINTDATA primary key (CustomerID, Type);
go

update YukonRoleProperty set DefaultValue = 'Curtailment' where RolePropertyID = -10922;
go

create table DEVICEGROUP (
   DeviceGroupId        numeric(18,0)        not null,
   GroupName            varchar(255)         not null,
   ParentDeviceGroupId  numeric(18,0)        null,
   SystemGroup          char(1)              not null,
   Type                 varchar(255)         not null
);
go

insert into DeviceGroup values (0,'',null,'Y','STATIC');
insert into DeviceGroup values (1,'Meters',0,'Y','STATIC');
insert into DeviceGroup values (2,'Billing',1,'Y','STATIC');
insert into DeviceGroup values (3,'Collection',1,'Y','STATIC');
insert into DeviceGroup values (4,'Alternate',1,'Y','STATIC');
insert into DeviceGroup values (5,'CustomGroup1',1,'Y','STATIC');
insert into DeviceGroup values (6,'CustomGroup2',1,'Y','STATIC');
insert into DeviceGroup values (7,'CustomGroup3',1,'Y','STATIC');
insert into DeviceGroup values (8,'Flags',1,'Y','STATIC');
insert into DeviceGroup values (9,'Inventory',8,'Y','STATIC');
insert into DeviceGroup values (10,'DisconnectedStatus',8,'Y','STATIC');
insert into DeviceGroup values (11,'UsageMonitoring',8,'Y','STATIC');
go

update DeviceMeterGroup set CollectionGroup = replace(replace(replace(CollectionGroup,'@_INV_',''),'@_UM_',''),'@_DISC_','');
update DeviceMeterGroup set BillingGroup = replace(replace(replace(BillingGroup,'@_INV_',''),'@_UM_',''),'@_DISC_','');
update DeviceMeterGroup set TestCollectionGroup = replace(replace(replace(TestCollectionGroup,'@_INV_',''),'@_UM_',''),'@_DISC_','');
go

insert into DeviceGroup select distinct 100, CollectionGroup, 3, 'N', 'STATIC' from DeviceMeterGroup;
insert into DeviceGroup select distinct 100, TestCollectionGroup, 4, 'N', 'STATIC' from DeviceMeterGroup;
insert into DeviceGroup select distinct 100, BillingGroup, 2, 'N', 'STATIC' from DeviceMeterGroup;
declare @cnt numeric set @cnt = 100 update DeviceGroup set @cnt = DeviceGroupID = @cnt + 1 where DeviceGroupID >= 100;
go

alter table DEVICEGROUP
   add constraint PK_DEVICEGROUP primary key (DeviceGroupId);
go

alter table DEVICEGROUP
   add constraint FK_DEVICEGROUP_DEVICEGROUP foreign key (ParentDeviceGroupId)
      references DEVICEGROUP (DeviceGroupId);
go


create table DEVICEGROUPMEMBER (
   DeviceGroupID        numeric(18,0)        not null,
   YukonPaoId           numeric(18,0)        not null
);
go

alter table DEVICEGROUPMEMBER
   add constraint PK_DEVICEGROUPMEMBER primary key (DeviceGroupID, YukonPaoId);
go

alter table DEVICEGROUPMEMBER
   add constraint FK_DeviceGroupMember_DEVICE foreign key (YukonPaoId)
      references DEVICE (DEVICEID)
         on delete cascade;
go

alter table DEVICEGROUPMEMBER
   add constraint FK_DevGrpMember_DeviceGroup foreign key (DeviceGroupID)
      references DEVICEGROUP (DeviceGroupId);
go

insert into DeviceGroupMember
select DeviceGroupId, DeviceId 
from DeviceMeterGroup
join DeviceGroup on ParentDeviceGroupId=3 and GroupName = CollectionGroup;

insert into DeviceGroupMember
select DeviceGroupId, DeviceId 
from DeviceMeterGroup
join DeviceGroup on ParentDeviceGroupId=4 and GroupName = TestCollectionGroup;

insert into DeviceGroupMember
select DeviceGroupId, DeviceId 
from DeviceMeterGroup
join DeviceGroup on ParentDeviceGroupId=2 and GroupName = BillingGroup;

alter table DeviceMeterGroup drop column CollectionGroup;
alter table DeviceMeterGroup drop column TestCollectionGroup;
alter table DeviceMeterGroup drop column BillingGroup;
go

/* @error ignore-begin */
insert into YukonRoleProperty values(-1019,-1,'batched_switch_command_timer','auto','Specifies whether the STARS application should automatically process batched switch commands');
/* @error ignore-end */
insert into YukonGroupRole values(-20,-1,-1,-1019,'(none)');
insert into YukonGroupRole values(-21,-1,-1,-1020,'(none)');
go

update YukonGroupRole set RoleID=-102, RolePropertyID=-10206 where RoleID= -304 and RolePropertyID = -30403;
update YukonGroupRole set RoleID=-102, RolePropertyID=-10205 where RoleID= -304 and RolePropertyID = -30402;
update YukonGroupRole set RoleID=-102, RolePropertyID=-10203 where RoleID= -304 and RolePropertyID = -30401;
update YukonGroupRole set RoleID=-102, RolePropertyID=-10202 where RoleID= -304 and RolePropertyID = -30400;
go

delete YukonGroupRole where RoleID = -304;
delete YukonUserRole where RoleID = -304;
delete YukonRoleProperty where RoleID= -304;
delete YukonRole where RoleID= -304;
go

delete YukonGroupRole where RolePropertyID in(-20202, -20201, -20200);
delete YukonUserRole where RolePropertyID in(-20202, -20201, -20200);
delete YukonRoleProperty where RolePropertyID in(-20202, -20201, -20200);
update YukonRole set ROleName = 'Metering', RoleDescription='Operator access to Metering' where RoleID = -202;
go

create table DYNAMICPAOSTATISTICSHISTORY (
   PAObjectID           numeric(18,0)        not null,
   DateOffset           numeric(18,0)        not null,
   Requests             numeric(18,0)        not null,
   Completions          numeric(18,0)        not null,
   Attempts             numeric(18,0)        not null,
   CommErrors           numeric(18,0)        not null,
   ProtocolErrors       numeric(18,0)        not null,
   SystemErrors         numeric(18,0)        not null
);
go

alter table DYNAMICPAOSTATISTICSHISTORY
   add constraint PK_DYNAMICPAOSTATISTICSHISTORY primary key (PAObjectID, DateOffset);
go

alter table DYNAMICPAOSTATISTICSHISTORY
   add constraint FK_DYNPAOSTHIST_YKNPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
go

insert into DynamicPAOStatistics select distinct paobjectid, 'Lifetime', 0, 0, 0, 0, 0, 0, getutcdate(), getutcdate() from DynamicPAOStatistics;
go

create table PROFILEPEAKRESULT (
   ResultId             numeric              not null,
   DeviceId             numeric              not null,
   ResultFrom           varchar(30)          not null,
   ResultTo             varchar(30)          not null,
   RunDate              varchar(30)          not null,
   PeakDay              varchar(30)          not null,
   Usage                varchar(25)          not null,
   Demand               varchar(25)          not null,
   AverageDailyUsage    varchar(25)          not null,
   TotalUsage           varchar(25)          not null,
   ResultType           varchar(5)           not null,
   Days                 numeric              not null
);
go

alter table PROFILEPEAKRESULT
   add constraint PK_PROFILEPEAKRESULT primary key nonclustered (ResultId);
go

alter table PROFILEPEAKRESULT
   add constraint FK_PROFILEPKRSLT_DEVICE foreign key (DeviceId)
      references DEVICE (DEVICEID);
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('YukonPAObject')
            and   name  = 'Indx_PAO'
            and   indid > 0
            and   indid < 255)
   drop index YukonPAObject.Indx_PAO;
go

update YukonPAObject set PAOName = PAOName + SUBSTRING(Type, 4, LEN(Type))
where PAObjectID in
(select PAObjectID from YukonPAObject 
where PAOName in (select PAOName from YukonPAObject group by PAOName, PAOClass having count(PAOName) > 1 AND PAOClass = 'Carrier'));
go

create unique  index Indx_PAO on YukonPAObject (
Category,
PAOName,
PAOClass
);
go

insert into command values(-133, 'putconfig xcom utility usage ?''Channel'':?''Value''', 'Thermostat Register Download', 'ExpresscomSerial');
insert into command values(-134, 'putconfig xcom data ''?''Text Message'''' port ?''Port (0 is default)'' deletable priority 7 timeout 30 hour clear', 'Thermostat Text Message', 'ExpresscomSerial');
insert into command values(-135, 'control xcom backlight cycles 20 duty 30 bperiod 10', 'Thermostat Display Blink', 'ExpresscomSerial');
go

insert into DeviceTypeCommand values (-670, -133, 'ExpresscomSerial', 21, 'Y', -1);
insert into DeviceTypeCommand values (-671, -134, 'ExpresscomSerial', 22, 'Y', -1);
insert into DeviceTypeCommand values (-672, -135, 'ExpresscomSerial', 23, 'Y', -1);
go

insert into DeviceTypeCommand values (-673, -133, 'Expresscom Group', 3, 'Y', -1);
insert into DeviceTypeCommand values (-674, -134, 'Expresscom Group', 4, 'Y', -1);
insert into DeviceTypeCommand values (-675, -135, 'Expresscom Group', 5, 'Y', -1);
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('POINT')
            and   name  = 'INDX_PAOBJECTID'
            and   indid > 0
            and   indid < 255)
   drop index POINT.INDX_PAOBJECTID
go

/*==============================================================*/
/* Index: INDX_PAOBJECTID                                       */
/*==============================================================*/
create index INDX_PAOBJECTID on POINT (
PAObjectID ASC
)
go

insert into YukonRole values(-212,'Scheduler','Operator','Operator access to Scheduler'); 
insert into yukonroleproperty values (-100011,-1000, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
insert into yukonroleproperty values (-100012,-1000, 'Substation Last Update Timestamp', 'true', 'is last update timstamp shown for substations');
insert into yukonroleproperty values (-100106,-1001, 'Feeder Last Update Timestamp', 'true', 'is last update timstamp shown for feeders');
insert into yukonroleproperty values (-100203,-1002, 'CapBank Last Update Timestamp', 'true', 'is last update timstamp shown for capbanks');
insert into yukonroleproperty values (-100105,-1001, 'Target', 'true', 'is target stat displayed');
insert into YukonRoleProperty values(-21200,-212,'Enable/Disable Schedule','true','Right to enable or disable a schedule'); 
insert into YukonRoleProperty values(-10815, -108,'Data Updater Delay (milliseconds)', '4000', 'The number of milliseconds between requests for the latest point values on pages that support the data updater.');
ALTER TABLE DeviceReadJobLog DROP CONSTRAINT FK_DEVICERE_FK_DRJOBL_MACSCHED
go

update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100008;
update yukonroleproperty set DefaultValue = 'false' where rolepropertyid = -100007;
go

delete from LMThermostatSeasonEntry where SeasonID in (select SeasonID from LMThermostatSeason where ScheduleID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100)));
go
delete from LMThermostatSeason where ScheduleID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
go
delete from ECToGenericMapping where MappingCategory = 'LMThermostatSchedule' and ItemID in (select ScheduleID from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100));
go
delete from LMThermostatSchedule where ThermostatTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);
go
update LMHardwareBase set LMHardwareTypeID = 0 where LMHardwareTypeID in (select EntryID from YukonListEntry where YukonDefinitionID = 3100);
go
delete from YukonlistEntry where YukonDefinitionID = 3100;
go

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.5', 'David', '03-Oct-2007', 'Latest Update', 0 );
