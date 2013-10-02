/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12536*/
INSERT INTO YukonRoleProperty VALUES (-90044,-900,'Asset Availability','false','Controls access to view Asset Availability for Scenarios, Control Areas, Programs, and Load Groups.');
/* End YUK-12536*/

/* Start YUK-12484*/
INSERT INTO YukonRoleProperty VALUES(-20020,-200,'Network Manager Access','false','Controls access to Network Manager.');
/* End YUK-12484*/

/* Start YUK-12305 */
ALTER TABLE JobProperty
ALTER COLUMN Value VARCHAR(4000) NOT NULL;
/* End YUK-12305 */

/* Start YUK-12525 */
DECLARE @PAObjectId AS NUMERIC;
DECLARE @PaoName AS VARCHAR(60);
DECLARE @Type AS VARCHAR(32);
DECLARE @UserId AS NUMERIC;
DECLARE @PagePath AS VARCHAR(2048);
DECLARE @PageName AS VARCHAR(32);
DECLARE @UserPageId AS NUMERIC;
DECLARE @UserPageParamId AS NUMERIC;
 
DECLARE favorites_curs CURSOR FOR (
    SELECT YPO.PAObjectID, YPO.PaoName, YPO.Type, PF.UserId 
    FROM PAOFavorites PF
    LEFT OUTER JOIN YukonPAObject YPO on 
    YPO.PAObjectID = PF.PAObjectId);
 
OPEN favorites_curs;
FETCH NEXT FROM favorites_curs INTO @PAObjectId, @PaoName, @Type, @UserId;
WHILE @@FETCH_STATUS = 0
BEGIN
    IF @Type LIKE '%GROUP%' 
    BEGIN
        SET @PagePath = '/dr/loadGroup/detail?loadGroupId=' + CAST(@PAObjectID AS VARCHAR);
        SET @PageName = 'loadGroupDetail';
    END
    IF @Type LIKE '%PROGRAM%'
    BEGIN
        SET @PagePath = '/dr/program/detail?programId=' + CAST(@PAObjectID AS VARCHAR);
        SET @PageName = 'programDetail';
    END
    IF @Type LIKE '%SCENARIO%'
    BEGIN
        SET @PagePath = '/dr/scenario/detail?scenarioId=' + CAST(@PAObjectID AS VARCHAR);
        SET @PageName = 'scenarioDetail';
    END
    IF @Type LIKE '%AREA%'
    BEGIN
        SET @PagePath = '/dr/controlArea/detail?controlAreaId=' + CAST(@PAObjectID AS VARCHAR);
        SET @PageName = 'controlAreaDetail';
    END
 
    SET @UserPageId = (SELECT ISNULL(MAX(UserPageId) + 1, 1) FROM UserPage);
    SET @UserPageParamId = (SELECT ISNULL(MAX(UserPageParamId) + 1, 1) FROM UserPageParam);
 
    INSERT INTO UserPage
    VALUES (@UserPageId, @UserId, @PagePath, 'dr', @PageName, 1, GETDATE());
 
    INSERT INTO UserPageParam
    VALUES (@UserPageParamId, @UserPageId, 0, @PAOName);
 
    FETCH NEXT FROM favorites_curs INTO @PAObjectId, @PaoName, @Type, @UserId;
END
CLOSE favorites_curs;
DEALLOCATE favorites_curs;
 
DROP TABLE PAOFavorites;
DROP TABLE PAORecentViews;
/* End YUK-12525 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '16-OCT-2013', 'Latest Update', 2, GETDATE());*/