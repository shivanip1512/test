/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7122 */
ALTER TABLE CCurtEEPricingWindow
ALTER COLUMN EnergyPrice NUMERIC(19,6) NOT NULL;
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


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
