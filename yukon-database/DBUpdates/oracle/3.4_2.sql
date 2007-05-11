/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/
update YukonRole set Category = 'Capacitor Control' where Category = 'CapBank Control';
update YukonRole set RoleName = 'Administrator' where RoleName = 'CBC Control';
update YukonRole set Category = 'Capacitor Control' where Category = 'CBC Oneline';

update State set Text = 'Inactive' where StateGroupID = -8 and RawState = 0;
update State set Text = 'Active' where StateGroupID = -8 and RawState = 1;

/* @start-block */
declare
v_paoid number(6);
v_areaname varchar2(60);
cursor c_areaname is select distinct description  as areaname from yukonpaobject where type = 'CCSUBBUS';
begin
select max(paobjectid) into v_paoid from yukonpaobject;
v_paoid := v_paoid + 1;
open c_areaname;
fetch c_areaname into v_areaname;
          
   while(c_areaname%found)
      loop
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
/* @end-block */

/* @start-block */
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
    fetch c_areaid into v_areaid, v_areaname1;
    while(c_areaid%found)
       loop
		insert into capcontrolarea values (v_areaid, 0);
		v_order := 1;
		open c_subarea;
		fetch c_subarea into v_subid, v_subdesc;
		while (c_subarea%found)
			loop	
				if (v_areaname1 = v_subdesc) then 
					insert into ccsubareaassignment values (v_areaid, v_subid, v_order);
					v_order := v_order + 1;
				end if;
				fetch c_subarea into v_subid, v_subdesc;
			end loop;
		close c_subarea;
		fetch c_areaid into v_areaid, v_areaname1;	
	end loop;
	close c_areaid;
end;
/* @end-block */

/* @start-block */
declare
v_tripOrder number;
v_deviceid number;
v_feedid number;
v_maxclose number;
cursor c_deviceid is select deviceid from ccfeederbanklist;
begin
	open c_deviceid;
      	loop
			fetch c_deviceid into v_deviceid;
			exit when c_deviceid%notfound;	 	
			select feederid into v_feedid from ccfeederbanklist where deviceid = v_deviceid;
        	select max(closeOrder) into v_maxclose from ccfeederbanklist where feederid = v_feedid group by feederid;
        	select (v_maxclose - controlorder + 1) into v_tripOrder from ccfeederbanklist where deviceid = v_deviceid;
        	update ccfeederbanklist set triporder = v_tripOrder where deviceid = v_deviceid;
	end loop;
 close c_deviceid;
end;
/* @end-block */
alter table ccfeederbanklist modify tripOrder number not null;

update command set label = 'Install Emetcon Gold 3' where commandid = -126;
update command set label = 'Install Emetcon Gold 2' where commandid = -125;

insert into command values(-127, 'putconfig raw 39 0', 'Install Emetcon Silver 1', 'VersacomSerial');
insert into command values(-128, 'putconfig raw 39 1', 'Install Emetcon Silver 2', 'VersacomSerial');
insert into command values(-129, 'putconfig raw 39 2', 'Install Emetcon Silver 3', 'VersacomSerial');

insert into devicetypecommand values(-576, -127, 'VersacomSerial', 22, 'N', -1);
insert into devicetypecommand values(-577, -128, 'VersacomSerial', 23, 'N', -1);
insert into devicetypecommand values(-578, -129, 'VersacomSerial', 24, 'N', -1);

insert into YukonListEntry values (1064,1005,-1,'ExpressStat Heat Pump',1313);

insert into YukonRoleProperty values(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
insert into YukonRoleProperty values(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');

insert into YukonRoleProperty values(-1114,-2,'Inherit Parent App Cats','true','If part of a member structure, should appliance categories be inherited from the parent.');

/* @error ignore-begin */
insert into YukonListEntry values (1064,1005,-1,'ExpressStat Heat Pump',1313);

insert into YukonRoleProperty values(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
insert into YukonRoleProperty values(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');
/* @error ignore-end */

create or replace view CCINVENTORY_VIEW as
SELECT yp3.Description AS Region, yp3.PAOName AS SubName, yp2.PAOName AS FeederName, yp3.PAObjectID AS subId, yp2.PAObjectID AS fdrId, 
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
                      DeviceDirectCommSettings ddcs ON ddcs.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      DeviceAddress da ON da.DeviceID = cb.CONTROLDEVICEID INNER JOIN
                      PORTTERMINALSERVER pts ON pts.PORTID = ddcs.PORTID INNER JOIN
                      DeviceCBC cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      capbankadditional capa on capa.deviceid = cb.deviceid;


create or replace view CCOPERATIONS_VIEW as
SELECT 
	yp3.PAOName AS cbcName, yp.PAOName AS bankname, el.DateTime AS opTime, el.Text AS operation, 
	el2.DateTime AS confTime, el2.Text AS confStatus, yp1.PAOName AS feederName, yp1.PAObjectID AS feederId, 
        yp2.PAOName AS subName, yp2.PAObjectID AS subBusId, yp2.Description AS region, cb.BANKSIZE, 
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
        p ON p.PAObjectID = cb.CONTROLDEVICEID;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.4', 'Jon', '07-May-2007', 'Latest Update', 2 );
