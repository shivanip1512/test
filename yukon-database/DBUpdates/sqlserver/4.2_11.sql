/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7257 */
/* @error ignore-begin */
DROP INDEX LMThermostatSeasonEntry.INDX_LMThermSeaEnt_SeaId;
DROP INDEX LMThermostatSeasonEntry.INDX_LMThermSeaEntry_SeaId;
/* @error ignore-end */
CREATE INDEX INDX_LMThermSeaEntry_SeaId ON LMThermostatSeasonEntry (
   SeasonId ASC
);
/* End YUK-7257 */

/* Start YUK-7503 */
UPDATE FDRInterface 
SET PossibleDirections = 'Receive,Send' 
WHERE InterfaceId = 26;
/* End YUK-7503 */

/* Start YUK-7518 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRoleProperty VALUES(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');
/* @error ignore-end */
/* End YUK-7518 */

/* Start YUK-7537 */
DELETE FROM EnergyCompanyOperatorLoginList
WHERE OperatorLoginId IN (SELECT DISTINCT ECOLL.OperatorLoginId 
                          FROM EnergyCompanyOperatorLoginList ECOLL, YukonUserGroup YUG, YukonGroupRole YGR, YukonRole YR
                          WHERE ECOLL.OperatorLoginId = YUG.UserId
                          AND YUG.GroupId = YGR.GroupId
                          AND YGR.RoleId = YR.RoleId
                          AND YR.Category = 'Consumer'
                          AND YR.RoleName = 'Residential Customer');
/* End YUK-7537 */

/* Start YUK-7561 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
/* @error ignore-end */
/* End YUK-7561 */

/* Start YUK-7590 */
INSERT INTO YukonUserRole VALUES(-1100, -100, -2, -1100, '(none)');
INSERT INTO YukonUserRole VALUES(-1101, -100, -2, -1101, '(none)');
INSERT INTO YukonUserRole VALUES(-1102, -100, -2, -1102, '(none)');
INSERT INTO YukonUserRole VALUES(-1105, -100, -2, -1105, '(none)');
INSERT INTO YukonUserRole VALUES(-1106, -100, -2, -1106, '(none)');
INSERT INTO YukonUserRole VALUES(-1107, -100, -2, -1107, '(none)');
INSERT INTO YukonUserRole VALUES(-1108, -100, -2, -1108, '(none)');
INSERT INTO YukonUserRole VALUES(-1109, -100, -2, -1109, '(none)');
INSERT INTO YukonUserRole VALUES(-1110, -100, -2, -1110, '(none)');
INSERT INTO YukonUserRole VALUES(-1111, -100, -2, -1111, '(none)');
INSERT INTO YukonUserRole VALUES(-1112, -100, -2, -1112, '(none)');
INSERT INTO YukonUserRole VALUES(-1114, -100, -2, -1114, '(none)'); 
/* End YUK-7590 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES('4.2', 'Matt K', '16-JUN-2009', 'Latest Update', 11);
