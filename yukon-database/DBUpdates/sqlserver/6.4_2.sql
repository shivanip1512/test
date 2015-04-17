/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14220 */
DELETE FROM DynamicPointDispatch 
WHERE PointId IN (
    SELECT PointId 
    FROM Point p 
    JOIN YukonPaobject pao ON p.PAObjectID = pao.PAObjectID
    WHERE Type LIKE 'RFN-%')
AND (Timestamp > DATEADD(YEAR, 1, GETDATE())
OR Timestamp < '01-JAN-2000');
/* End YUK-14220 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '30-APR-2015', 'Latest Update', 2, GETDATE());*/