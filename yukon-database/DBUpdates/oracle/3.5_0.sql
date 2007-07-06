/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

/*3.4 changes that may have been missed*/
/* @error ignore-begin */
insert into YukonRoleProperty values(-100105, -1001, 'Target', 'true', 'display target settings');

update devicetypecommand set devicetype='MCT-430A' where devicetype = 'MCT-430EL';
update devicetypecommand set devicetype='MCT-430S4' where devicetype = 'MCT-430LG';
update devicetypecommand set devicetype='MCT-430SN' where devicetype = 'MCT-430IN';

insert into YukonRoleProperty values(-21003,-210,'Addtl Order Number Label','Addtl Order Number','Customizable label for the additional order number field.');
insert into YukonRoleProperty values(-20893,-201,'Inventory Checking Create','true','Allow creation of inventory if not found during Inventory Checking');

insert into yukonlistentry values (138, 100, 0, 'Float From 16bit', 0);

update point set pointname = 'MultiSpeak' where pointid = -110;
update YukonRole set RoleName = 'MultiSpeak', RoleDescription='MultiSpeak web services interface.' where RoleID = -7;
update YukonRoleProperty set KeyName = 'MultiSpeak Setup', Description='Controls access to configure the Multispeak Interfaces.' where RolePropertyID = -20011;

drop view ccinventory_view;
create or replace view CCINVENTORY_VIEW as
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

drop view ccoperations_view;
create or replace view CCOPERATIONS_VIEW as
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
/* @error ignore-end */

insert into FDRInterface values (25, 'TRISTATESUB', 'Receive', 'f' );
insert into FDRInterfaceOption values(25, 'Point', 1, 'Combo', 'Nucla 115/69 Xfmr.,Happy Canyon 661Idarado,Cascade 115/69 (T2),Ames Generation,Dallas Creek MW,Dallas Creek MV' );

insert into YukonRoleProperty values(-10814, -108,'Suppress Error Page Details', 'false', 'Disable stack traces for this user.');

insert into BillingFileFormats values(-25,'Itron Register Readings Export');

alter table ActivityLog modify Description varchar2(240);

insert into command values(-130, 'getvalue lp channel ?''Channel #'' ?''Enter Start Date: xx/xx/xxxx'' ?''Enter End Date: xx/xx/xxxx''', 'Read LP Channel Data', 'ALL MCT-4xx Series');
insert into command values(-131, 'getvalue lp status', 'Read LP Channel Data Status', 'ALL MCT-4xx Series');
insert into command values(-132, 'getvalue lp cancel', 'Read LP Channel Data Cancel', 'ALL MCT-4xx Series');

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

insert into yukonlistentry values (139, 100, 0, 'Get Point Limit', 0);
insert into yukonlistentry values (140, 100, 0, 'Get Interval Minutes', 0);
insert into yukonlistentry values (141, 100, 0, 'Intervals To Value', 0);
insert into yukonlistentry values (142, 100, 0, 'Linear Slope', 0);

alter table CICUSTOMERPOINTDATA drop constraint PK_CICUSTOMERPOINTDATA;
alter table CICUSTOMERPOINTDATA add constraint PK_CICUSTOMERPOINTDATA primary key (CustomerID, Type);

update YukonRoleProperty set DefaultValue = 'Curtailment' where RolePropertyID = -10922;

create table DEVICEGROUP  (
   DeviceGroupId      NUMBER(18,0)                    not null,
   GroupName          VARCHAR2(255),                   
   ParentDeviceGroupId NUMBER(18,0),
   SystemGroup        CHAR(1)                         not null,
   Type               VARCHAR2(255)                   not null
);

insert into DeviceGroup values (0,'','','Y','STATIC');
insert into DeviceGroup values (1,'Meters',0,'Y','STATIC');
insert into DeviceGroup values (2,'Billing',1,'Y','STATIC');
insert into DeviceGroup values (3,'Collection',1,'Y','STATIC');
insert into DeviceGroup values (4,'Alternate',1,'Y','STATIC');
insert into DeviceGroup values (5,'CustomGroup1',1,'Y','STATIC');
insert into DeviceGroup values (6,'CustomGroup2',1,'Y','STATIC');
insert into DeviceGroup values (7,'CustomGroup3',1,'Y','STATIC');

insert into DeviceGroup select distinct 8, CollectionGroup, 3, 'N', 'STATIC' from DeviceMeterGroup;
insert into DeviceGroup select distinct 8, TestCollectionGroup, 4, 'N', 'STATIC' from DeviceMeterGroup;
insert into DeviceGroup select distinct 8, BillingGroup, 2, 'N', 'STATIC' from DeviceMeterGroup;
update (select * from DeviceGroup where DeviceGroupID = 7) set DeviceGroupID = rownum + 7;

alter table DEVICEGROUP 
   add constraint PK_DEVICEGROUP primary key (DeviceGroupId);

alter table DEVICEGROUP
   add constraint FK_DEVICEGROUP_DEVICEGROUP foreign key (ParentDeviceGroupId)
      references DEVICEGROUP (DeviceGroupId);

create table DEVICEGROUPMEMBER  (
   DeviceGroupID      NUMBER(18,0)                    not null,
   YukonPaoId         NUMBER(18,0)                    not null
);

alter table DEVICEGROUPMEMBER
   add constraint PK_DEVICEGROUPMEMBER primary key (DeviceGroupID, YukonPaoId);

alter table DEVICEGROUPMEMBER
   add constraint FK_DeviceGroupMember_DEVICE foreign key (YukonPaoId)
      references DEVICE (DEVICEID);

alter table DEVICEGROUPMEMBER
   add constraint FK_DevGrpMember_DeviceGroup foreign key (DeviceGroupID)
      references DEVICEGROUP (DeviceGroupId);
      
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

update YukonGroupRole set RoleID=-102, RolePropertyID=-10206 where RoleID= -304 and RolePropertyID = -30403;
update YukonGroupRole set RoleID=-102, RolePropertyID=-10205 where RoleID= -304 and RolePropertyID = -30402;
update YukonGroupRole set RoleID=-102, RolePropertyID=-10203 where RoleID= -304 and RolePropertyID = -30401;
update YukonGroupRole set RoleID=-102, RolePropertyID=-10202 where RoleID= -304 and RolePropertyID = -30400;

delete YukonGroupRole where RoleID = -304;
delete YukonUserRole where RoleID = -304;
delete YukonRoleProperty where RoleID= -304;
delete YukonRole where RoleID= -304;

delete YukonGroupRole where RolePropertyID in(-20202, -20201, -20200);
delete YukonUserRole where RolePropertyID in(-20202, -20201, -20200);
delete YukonRoleProperty where RolePropertyID in(-20202, -20201, -20200);
update YukonRole set ROleName = 'Metering', RoleDescription='Operator access to Metering' where RoleID = -202;

create table DYNAMICPAOSTATISTICSHISTORY  (
   PAObjectID         NUMBER(18,0)                    not null,
   DateOffset         NUMBER(18,0)                    not null,
   Requests           NUMBER(18,0)                    not null,
   Completions        NUMBER(18,0)                    not null,
   Attempts           NUMBER(18,0)                    not null,
   CommErrors         NUMBER(18,0)                    not null,
   ProtocolErrors     NUMBER(18,0)                    not null,
   SystemErrors       NUMBER(18,0)                    not null
);

alter table DYNAMICPAOSTATISTICSHISTORY
   add constraint PK_DYNAMICPAOSTATISTICSHISTORY primary key (PAObjectID, DateOffset);

alter table DYNAMICPAOSTATISTICSHISTORY
   add constraint FK_DYNPAOSTHIST_YKNPAO foreign key (PAObjectID)
      references YukonPAObject (PAObjectID);
      
insert into DynamicPAOStatistics select distinct paobjectid, 'Lifetime', 0, 0, 0, 0, 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP from DynamicPAOStatistics;
      
create table PROFILEPEAKRESULT  (
   ResultId           number                          not null,
   DeviceId           number                          not null,
   ResultFrom         varchar(30)                     not null,
   ResultTo           varchar(30)                     not null,
   RunDate            varchar(30)                     not null,
   PeakDay            varchar(30)                     not null,
   Usage              varchar(25)                     not null,
   Demand             varchar(25)                     not null,
   AverageDailyUsage  varchar(25)                     not null,
   TotalUsage         varchar(25)                     not null,
   ResultType         varchar(5)                      not null,
   Days               number                          not null
);

alter table PROFILEPEAKRESULT
   add constraint PK_PROFILEPEAKRESULT primary key (ResultId);

alter table PROFILEPEAKRESULT
   add constraint FK_PROFILEPKRSLT_DEVICE foreign key (DeviceId)
      references DEVICE (DEVICEID);

drop index Indx_PAO;
      
update YukonPAObject set PAOName = PAOName + SUBSTR(Type, 4, LENGTH(Type))
where PAObjectID in
(select PAObjectID from YukonPAObject 
where PAOName in (select PAOName from YukonPAObject group by PAOName, PAOClass having count(PAOName) > 1 AND PAOClass = 'Carrier'));

create unique index Indx_PAO on YukonPAObject (
   Category ASC,
   PAOName ASC,
   PAOClass ASC
);
      
/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */
