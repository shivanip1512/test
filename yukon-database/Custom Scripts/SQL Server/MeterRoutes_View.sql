/**
 * View created for Cowlitz. Will be used by Cayenta/MeterSense integrations. Requested by Cliff Hammons
 * Customer will be parsing RouteName for Substation, Bank, Feeder, Repeater specific info is stored in the 
 *  routeName. MeterSense is responsible for parsing the Yukon RouteName into those specific details.
 * SLN 20120821 
 * 
 * From: Douglas Eagle (MeterSense)
 * Sent: Friday, August 10, 2012 6:52 AM
 * To: Paula Squarizzi; James Shields; Mark Langton
 * Cc: Michael Bennett
 * Subject: RE: Cowlitz call on Monday - Cooper's participation
 *   We need them to create a database view with the Sub Station, Bank, Feeder, Repeater, and Meter relationships.  
 *   I believe they store the Sub Station, Bank, Feeder, Repeater relationships as a concatenated string called ‘Route’, 
 *   so if they can provide a view with meter – route relationships it should work.  We’ll need to remind Brian 
 *   that he will need to update the routes already in Yukon with some sort of delineation so we can parse out the 
 *   sub station/bank/feeder etc.
 * Thanks, Doug
 */

CREATE VIEW MeterRoutes_View AS
SELECT pao.PaoName AS DeviceName, MeterNumber, pao.Type AS MeterType, rte.PAOName AS RouteName, rte.Type AS RouteType
FROM YukonPaobject pao JOIN DeviceMeterGroup dmg ON pao.paobjectid = dmg.deviceid
JOIN DeviceRoutes dr ON dr.DEVICEID = pao.PAObjectID
JOIN YukonPAObject rte ON rte.paobjectid = dr.routeid
