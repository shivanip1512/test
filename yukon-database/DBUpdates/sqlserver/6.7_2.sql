/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-17248 */
CREATE INDEX Indx_YukonUser_UserGroupId ON YukonUser (
UserGroupId ASC
);
GO
/* End YUK-17248 */

/* Start YUK-17167 */
UPDATE Point SET PointName = 'Daily Max Volts'
WHERE PointName = 'Max Volts' AND PointType = 'Analog' AND PointOffset = '14'
AND PaobjectId IN (SELECT Distinct PaobjectId FROM YukonPaobject WHERE Type LIKE 'RFN%');

UPDATE Point SET PointName = 'Daily Min Volts'
WHERE PointName = 'Min Volts' AND PointType = 'Analog' AND PointOffset = '15'
AND PaobjectId IN (SELECT Distinct PaobjectId FROM YukonPaobject WHERE Type LIKE 'RFN%');
/* End YUK-17167 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '01-AUG-2017', 'Latest Update', 2, GETDATE());*/