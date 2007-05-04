/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/****************** ORACLE **************************/
declare
v_paoid number(6);
v_areaname varchar2(60);
cursor c_areaname is select distinct description  as areaname from yukonpaobject where type = 'CCSUBBUS';
begin
select max(paobjectid) into v_paoid from yukonpaobject;
v_paoid := v_paoid + 1;
open c_areaname;
fetch c_areaname into v_areaname;
          
   while(v_areaname%found)
      loop
          --fetch c_areaname into v_areaname;
          insert into yukonpaobject(paobjectid, category, paoclass, paoname, type, description, disableflag, paostatistics)
                  select v_paoid,
                   'CAPCONTROL',
                   'CAPCONTROL',
                   v_areaname,
                   'CCAREA',
                   '(none)',
                   'N',
                   '-----' from yukonpaobject;
            v_paoid := v_paoid + 1;
fetch c_areaname into v_areaname;
         
     end loop;
close c_areaname;
end;


declare
v_areaid number;
v_areaname1 varchar2(60);
v_subid number;
v_subdesc varchar2(60);
v_order number := 1;

cursor c_areaid is (select paobjectid, paoname from yukonpaobject where type = 'CCAREA');
cursor c_subarea is (select paobjectid as subid, description from yukonpaobject where type = 'CCSUBBUS');

begin
    open c_areaid;
    while(c_areaid%notfound)
       loop
			fetch c_areaid into v_areaid, v_areaname1;
			insert into capcontrolarea values (v_areaid, 0);
		v_order := 1;
             
			open c_subarea;
			while (c_subarea%notfound)
				loop	
					fetch c_subarea into v_subid, v_subdesc;
					if (v_areaname1 = v_subdesc) then 
					
						insert into ccsubareaassignment values (v_areaid, v_subid, v_order);
						v_order := v_order + 1;
					end if;
				end loop;
			close c_subarea;
		end loop;
	close c_areaid;
end;
	
		

/**** JON!!!! THIS DON't WORKY WORKY!!! ********/
alter table ccfeederbanklist add closeOrder number;
update ccfeederbanklist set closeOrder = ControlOrder;
alter table ccfeederbanklist  modify  closeOrder number not null;
alter table ccfeederbanklist add tripOrder number;

declare 
v_tripOrder number;
v_devid number;
v_feedid number;
v_maxclose number;
cursor c_deviceid is (select deviceid from ccfeederbanklist);

begin
    open c_deviceid;
    while(c_deviceid%notfound)
      loop
		fetch c_deviceid into v_devid;
		select feederid into v_feedid from ccfeederbanklist where deviceid = v_devid;
        select max(closeOrder) into v_maxclose from ccfeederbanklist where feederid = v_feedid group by feederid;
        select (v_maxclose - fb.controlorder + 1) into v_tripOrder from ccfeederbanklist fb where fb.deviceid = v_devid;
        update ccfeederbanklist set triporder = v_tripOrder where deviceid = v_devid;		
	  end loop;
	close c_deviceid;
end;

alter table ccfeederbanklist modify tripOrder number not null;
/**** END JON!!!! THIS DON't WORKY WORKY!!! ********/



/***** Thain's changes ********/

/** Old code **/
insert into StateGroup values (-8, 'TwoStateActive', 'Status');
insert into State values(-8, 0, 'Active', 0, 6, 0);
insert into State values(-8, 1, 'Inactive', 2, 6, 0);  

/**  Change to: **/
insert into StateGroup values (-8, 'TwoStateActive', 'Status');
insert into State values(-8, 0, 'Inactive', 2, 6, 0);
insert into State values(-8, 1, 'Active', 0, 6, 0);  


/********* Jason's changes: Please also add to 3.4 -- Update existing command labels and add 3 new commands *************/
update command set label = 'Install Emetcon Gold 3' where commandid = -126
update command set label = 'Install Emetcon Gold 2' where commandid = -125

insert into command values(-127, 'putconfig raw 39 0', 'Install Emetcon Silver 1', 'VersacomSerial')
insert into command values(-128, 'putconfig raw 39 1', 'Install Emetcon Silver 2', 'VersacomSerial')
insert into command values(-129, 'putconfig raw 39 2', 'Install Emetcon Silver 3', 'VersacomSerial')

insert into devicetypecommand values(-576, -127, 'VersacomSerial', 22, 'N', -1)
insert into devicetypecommand values(-577, -128, 'VersacomSerial', 23, 'N', -1)
insert into devicetypecommand values(-578, -129, 'VersacomSerial', 24, 'N', -1)
/**********************/

/* 3.4 and HEAD*/
insert into YukonListEntry values (1064,1005,-1,'ExpressStat Heat Pump',1313);
insert into LMThermostatSchedule (ScheduleID, ScheduleName, ThermostatTypeID, AccountID, InventoryID) select max(scheduleID) + 1, '(none)', 1064, 0, 0 from LMThermostatSchedule;
insert into ECToGenericMapping (EnergyCompanyID, ItemID, MappingCategory) select 0, max(scheduleID), 'LMThermostatSchedule' from LMThermostatSchedule

insert into YukonRoleProperty values(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
insert into YukonRoleProperty values(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');
/* 3.4 and HEAD Changes for 2 new capcontrol view      START*/

CREATE VIEW [dbo].[CCOperations_View]
AS
SELECT     yp3.PAOName AS cbcName, yp.PAOName AS bankname, el.DateTime AS opTime, el.Text AS operation, el2.DateTime AS confTime, 
                      el2.Text AS confStatus, yp1.PAOName AS feederName, yp1.PAObjectID AS feederId, yp2.PAOName AS subName, yp2.PAObjectID AS subBusId, 
                      yp2.Description AS region, cb.BANKSIZE, cb.ControllerType AS protocol, p.Value AS ipAddress, cbc.SERIALNUMBER AS serialNum, da.SlaveAddress, 
                      el2.kvarAfter, el2.kvarChange, el2.kvarBefore
FROM         (SELECT     op.LogID AS oid, MIN(aaa.confid) AS cid
                       FROM          (SELECT     LogID, PointID
                                               FROM          dbo.CCEventLog
                                               WHERE      (Text LIKE '%Close sent,%') OR
                                                                      (Text LIKE '%Open sent,%')) AS op LEFT OUTER JOIN
                                                  (SELECT     el.LogID AS opid, MIN(el2.LogID) AS confid
                                                    FROM          dbo.CCEventLog AS el INNER JOIN
                                                                           dbo.CCEventLog AS el2 ON el2.PointID = el.PointID AND el.LogID < el2.LogID LEFT OUTER JOIN
                                                                               (SELECT     a.LogID AS aid, MIN(b.LogID) AS next_aid
                                                                                 FROM          dbo.CCEventLog AS a INNER JOIN
                                                                                                        dbo.CCEventLog AS b ON a.PointID = b.PointID AND a.LogID < b.LogID
                                                                                 WHERE      (a.Text LIKE '%Close sent,%' OR
                                                                                                        a.Text LIKE '%Open sent,%') AND (b.Text LIKE '%Close sent,%' OR
                                                                                                        b.Text LIKE '%Open sent,%')
                                                                                 GROUP BY a.LogID) AS el3 ON el3.aid = el.LogID
                                                    WHERE      (el.Text LIKE '%Close sent,%' OR
                                                                           el.Text LIKE '%Open sent,%') AND (el2.Text LIKE 'Var: %') AND (el2.LogID < el3.next_aid) OR
                                                                           (el.Text LIKE '%Close sent,%' OR
                                                                           el.Text LIKE '%Open sent,%') AND (el2.Text LIKE 'Var: %') AND (el3.next_aid IS NULL)
                                                    GROUP BY el.LogID) AS aaa ON op.LogID = aaa.opid
                       GROUP BY op.LogID) AS OpConf INNER JOIN
                      dbo.CCEventLog AS el ON el.LogID = OpConf.oid LEFT OUTER JOIN
                      dbo.CCEventLog AS el2 ON el2.LogID = OpConf.cid INNER JOIN
                      dbo.POINT ON dbo.POINT.POINTID = el.PointID INNER JOIN
                      dbo.DynamicCCCapBank ON dbo.DynamicCCCapBank.CapBankID = dbo.POINT.PAObjectID INNER JOIN
                      dbo.YukonPAObject AS yp ON yp.PAObjectID = dbo.DynamicCCCapBank.CapBankID INNER JOIN
                      dbo.YukonPAObject AS yp1 ON yp1.PAObjectID = el.FeederID INNER JOIN
                      dbo.YukonPAObject AS yp2 ON yp2.PAObjectID = el.SubID INNER JOIN
                      dbo.CAPBANK AS cb ON cb.DEVICEID = dbo.DynamicCCCapBank.CapBankID LEFT OUTER JOIN
                      dbo.DeviceDirectCommSettings AS ddcs ON ddcs.DEVICEID = cb.CONTROLDEVICEID LEFT OUTER JOIN
                      dbo.DeviceAddress AS da ON da.DeviceID = cb.CONTROLDEVICEID INNER JOIN
                      dbo.YukonPAObject AS yp3 ON yp3.PAObjectID = cb.CONTROLDEVICEID LEFT OUTER JOIN
                      dbo.DeviceCBC AS cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID LEFT OUTER JOIN
                          (SELECT     EntryID, PAObjectID, Owner, InfoKey, Value, UpdateTime
                            FROM          dbo.DynamicPAOInfo
                            WHERE      (InfoKey LIKE '%udp ip%')) AS p ON p.PAObjectID = cb.CONTROLDEVICEID
GO

CREATE VIEW [dbo].[CCInventory_View]
AS

SELECT     yp3.Description AS Region, yp3.PAOName AS SubName, yp2.PAOName AS FeederName, yp3.PAObjectID AS subId, yp2.PAObjectID AS fdrId, 
                      yp.PAOName AS CBCName, yp.PAObjectID AS cbcId, yp1.PAOName AS Bankname, yp1.PAObjectID AS bankId, cb.BANKSIZE AS CapBankSize, 
                      fb.ControlOrder AS Sequence, dcb.ControlStatus, cb.SwitchManufacture AS SWMfgr, cb.TypeOfSwitch AS SWType, 
                      cb.OPERATIONALSTATE AS ControlType, cb.ControllerType AS Protocol, pts.IPADDRESS, da.SlaveAddress, '' AS LAT, '' AS LON, '' AS DriveDirection, 
                      '' AS OpCenter, '' AS TA
FROM         dbo.CAPBANK AS cb INNER JOIN
                      dbo.YukonPAObject AS yp ON yp.PAObjectID = cb.CONTROLDEVICEID INNER JOIN
                      dbo.YukonPAObject AS yp1 ON yp1.PAObjectID = cb.DEVICEID INNER JOIN
                      dbo.DynamicCCCapBank AS dcb ON dcb.CapBankID = yp1.PAObjectID INNER JOIN
                      dbo.STATE AS s ON s.STATEGROUPID = 3 AND dcb.ControlStatus = s.RAWSTATE INNER JOIN
                      dbo.CCFeederBankList AS fb ON fb.DeviceID = cb.DEVICEID INNER JOIN
                      dbo.YukonPAObject AS yp2 ON yp2.PAObjectID = fb.FeederID INNER JOIN
                      dbo.CCFeederSubAssignment AS sf ON fb.FeederID = sf.FeederID INNER JOIN
                      dbo.YukonPAObject AS yp3 ON yp3.PAObjectID = sf.SubStationBusID INNER JOIN
                      dbo.DeviceDirectCommSettings AS ddcs ON ddcs.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      dbo.DeviceAddress AS da ON da.DeviceID = cb.CONTROLDEVICEID INNER JOIN
                      dbo.PORTTERMINALSERVER AS pts ON pts.PORTID = ddcs.PORTID INNER JOIN
                      dbo.DeviceCBC AS cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID

GO

/* 3.4 and HEAD Changes for 2 new capcontrol view      END*/
