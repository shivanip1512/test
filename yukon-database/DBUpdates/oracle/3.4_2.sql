/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/
update YukonRole set Category = 'Capacitor Control' where Category = 'CapBank Control';
update YukonRole set RoleName = 'Administrator' where RoleName = 'CBC Control';
update YukonRole set Category = 'Capacitor Control' where Category = 'CBC Oneline';

update State set Text = 'Inactive' where StateGroupID = -8 and RawState = 0;
update State set Text = 'Active' where StateGroupID = -8 and RawState = 1;

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

create or replace view CCOPERATIONS_VIEW as
SELECT 
	yp3.PAOName AS cbcName, yp.PAOName AS bankname, el.DateTime AS opTime, el.Text AS operation, el2.DateTime AS confTime, el2.Text AS confStatus, yp1.PAOName AS feederName, yp1.PAObjectID AS feederId, yp2.PAOName AS subName, yp2.PAObjectID AS subBusId, yp2.Description AS region, cb.BANKSIZE, cb.ControllerType AS protocol, p.Value AS ipAddress, cbc.SERIALNUMBER AS serialNum, da.SlaveAddress, el2.kvarAfter, el2.kvarChange, el2.kvarBefore
FROM   
      	(SELECT op.LogID AS oid, MIN(aaa.confid) AS cid FROM
		(SELECT LogID, PointID FROM dbo.CCEventLog WHERE (Text LIKE '%Close sent,%') OR (Text LIKE '%Open sent,%')) AS op LEFT OUTER JOIN 
		(SELECT el.LogID AS opid, MIN(el2.LogID) 
		    AS confid FROM dbo.CCEventLog AS el INNER JOIN dbo.CCEventLog AS el2 ON el2.PointID = el.PointID AND el.LogID < el2.LogID LEFT OUTER JOIN
                (SELECT a.LogID AS aid, MIN(b.LogID) AS next_aid FROM dbo.CCEventLog AS a INNER JOIN dbo.CCEventLog AS b ON a.PointID = b.PointID AND a.LogID < b.LogID WHERE (a.Text LIKE '%Close sent,%' OR a.Text LIKE '%Open sent,%') AND (b.Text LIKE '%Close sent,%' OR b.Text LIKE '%Open sent,%')
			GROUP BY a.LogID) 
		AS el3 ON el3.aid = el.LogID WHERE (el.Text LIKE '%Close sent,%' OR el.Text LIKE '%Open sent,%') AND (el2.Text LIKE 'Var: %') AND (el2.LogID < el3.next_aid) OR (el.Text LIKE '%Close sent,%' OR 
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
	(SELECT EntryID, PAObjectID, Owner, InfoKey, Value, UpdateTime
        	FROM dbo.DynamicPAOInfo WHERE (InfoKey LIKE '%udp ip%')) 
	AS p ON p.PAObjectID = cb.CONTROLDEVICEID;

create or replace view CCINVENTORY_VIEW as
SELECT yp3.Description AS Region, yp3.PAOName AS SubName, yp2.PAOName AS FeederName, yp3.PAObjectID AS subId, yp2.PAObjectID AS fdrId, 
                      yp.PAOName AS CBCName, yp.PAObjectID AS cbcId, yp1.PAOName AS Bankname, yp1.PAObjectID AS bankId, cb.BANKSIZE AS CapBankSize, 
                      fb.ControlOrder AS Sequence, dcb.ControlStatus, cb.SwitchManufacture AS SWMfgr, cb.TypeOfSwitch AS SWType, 
                      cb.OPERATIONALSTATE AS ControlType, cb.ControllerType AS Protocol, pts.IPADDRESS, da.SlaveAddress
FROM dbo.CAPBANK AS cb INNER JOIN
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
                      dbo.DeviceCBC AS cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID;

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
