/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

SELECT yp3.Description AS Region, yp3.PAOName AS SubName, yp2.PAOName AS FeederName, yp3.PAObjectID AS subId, yp2.PAObjectID AS fdrId, 
                      yp.PAOName AS CBCName, yp.PAObjectID AS cbcId, yp1.PAOName AS Bankname, yp1.PAObjectID AS bankId, cb.BANKSIZE AS CapBankSize, 
                      fb.ControlOrder AS Sequence, dcb.ControlStatus, cb.SwitchManufacture AS SWMfgr, cb.TypeOfSwitch AS SWType, 
                      cb.OPERATIONALSTATE AS ControlType, cb.ControllerType AS Protocol, pts.IPADDRESS, da.SlaveAddress, 
capa.latitude AS LAT, capa.longitude AS LON, capa.drivedirections AS DriveDirection, 
cb.maplocationid AS OpCenter, capa.maintenanceareaid AS TA
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
                      dbo.DeviceCBC AS cbc ON cbc.DEVICEID = cb.CONTROLDEVICEID INNER JOIN
                      dbo.capbankadditional as capa on capa.deviceid = cb.deviceid