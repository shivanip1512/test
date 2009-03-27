/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7122 */
ALTER TABLE CCurtEEPricingWindow
MODIFY EnergyPrice NUMBER(19,6);
/* End YUK-7122 */

/* Start YUK-7167 */
INSERT INTO YukonRoleProperty VALUES(-80004,-800,'IVR URL Dialer Template','http://127.0.0.1:9998/VoiceXML.start?tokenid=yukon-{MESSAGE_TYPE}&numbertodial={PHONE_NUMBER}','The URL used to initiate a call, see documentation for allowed variables'); 
INSERT INTO YukonRoleProperty VALUES(-80005,-800,'IVR URL Dialer Success Matcher','success','A Java Regular Expression that will be matched against the output of the URL to determine if the call was successfull'); 

DELETE FROM YukonUserRole 
WHERE RolePropertyId IN (-1400, -80002, -1401);
DELETE FROM YukonGroupRole 
WHERE RolePropertyId IN (-1400, -80002, -1401);
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId IN (-1400, -80002, -1401);
/* End YUK-7167 */

/* Start YUK-7164 */
INSERT INTO YukonRoleProperty VALUES(-10318,-103,'Enable Web Commander', 'true', 'Controls access to web commander applications');
INSERT INTO YukonRoleProperty VALUES(-10319,-103,'Enable Client Commander', 'true', 'Controls access to client commander application'); 
/* End YUK-7164 */

/* Start YUK-7184 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId IN (-100007, -100008, -100200, -100204);
DELETE FROM YukonGroupRole 
WHERE RolePropertyId IN (-100007, -100008, -100200, -100204);
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId IN (-100007, -100008, -100200, -100204);

UPDATE YukonRoleProperty 
SET KeyName = 'Daily/Max Operation Count', Description = 'is Daily/Max Operation stat displayed'
WHERE RolePropertyId = -100103; 
/* End YUK-7184 */

/* Start YUK-7174 */
/* This delete can take some time. */
DELETE RawPointHistory 
WHERE PointId NOT IN (SELECT DISTINCT PointId 
                      FROM Point); 
/* End YUK-7174 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
