/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/
/**  Thain: to go into head(3.5)  ready */

insert into FDRInterface values (25, 'TRISTATESUB', 'Receive', 'f' );
insert into FDRInterfaceOption values(25, 'Point', 1, 'Combo', 
'Nucla 115/69 Xfmr.,Happy Canyon 661Idarado,Cascade 115/69 (T2),Ames Generation,Dallas Creek MW,Dallas Creek MV' );

/** End Thain   */

insert into YukonRoleProperty values(-10814, -108,'Suppress Error Page Details', 'false', 'Disable stack traces for this user.');
select * from yukonroleproperty where rolepropertyid = -10814;

/** Elliot - added target settings for oneline feeder**/
insert into YukonRoleProperty values(-100105, -1001, 'Target', 'true', 'display target settings');
go
/***************************************/


/** Jason - update devicetypecommand with new 430 names (into head AND 3.4) **/
update devicetypecommand set devicetype='MCT-430A' where devicetype = 'MCT-430EL';
update devicetypecommand set devicetype='MCT-430S4' where devicetype = 'MCT-430LG';
update devicetypecommand set devicetype='MCT-430SN' where devicetype = 'MCT-430IN';
/***************************************/

/*Missing role property, originally added for Xcel.  Should have been in 3.3 scripts*/
insert into YukonRoleProperty values(-21003,-210,'Addtl Order Number Label','Addtl Order Number','Customizable label for the additional order number field.');

/*Missing role property, originally added for Xcel.  Should have been in 3.3_0 update scripts*/
insert into YukonRoleProperty values(-20893,-201,'Inventory Checking Create','true','Allow creation of inventory if not found during Inventory Checking');

/* Added entry for drop down box*/
/* NOTE THIS BELONGS IN HEAD, 3.4 and 3.2!!! */
insert into yukonlistentry values (138, 100, 0, 'Float From 16bit', 0);

/*************************************/
/*  MultiSpeak spelling changes for head and 3.4 branch --- SN 2007/06/02 */
update point set pointname = 'MultiSpeak' where pointid = -110;
update YukonRole set RoleName = 'MultiSpeak', RoleDescription='MultiSpeak web services interface.' where RoleID = -7;
update YukonRoleProperty set KeyName = 'MultiSpeak Setup', Description='Controls access to configure the Multispeak Interfaces.' where RolePropertyID = -20011;
/*************************************/


/*** JULIE : report changes, views need to be modified to reflect accurate area.****/
/**** NOTE : change needs to be in HEAD nad 3.4!!!  ********************************/
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
/*** END JULIE : report changes, views need to be modified to reflect accurate area.****/
