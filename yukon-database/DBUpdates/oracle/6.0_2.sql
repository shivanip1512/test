/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12536*/
INSERT INTO YukonRoleProperty VALUES (-90044,-900,'Asset Availability','false','Controls access to view Asset Availability for Scenarios, Control Areas, Programs, and Load Groups.');
/* End YUK-12536*/

/* Start YUK-12484*/
INSERT INTO YukonRoleProperty VALUES(-20020,-200,'Network Manager Access','false','Controls access to Network Manager.');
/* End YUK-12484*/

/* Start YUK-12305 */
ALTER TABLE JobProperty
MODIFY Value VARCHAR2(4000);
/* End YUK-12305 */

/* Start YUK-12525 */
DECLARE 
    v_PAObjectId NUMBER;
    v_PaoName VARCHAR2(60);
    v_Type VARCHAR2(32);
    v_UserId NUMBER;
    v_PagePath VARCHAR2(2048);
    v_PageName VARCHAR2(32);
    v_UserPageId NUMBER;
    v_UserPageParamId NUMBER;
    CURSOR favorites_curs IS (
        SELECT YPO.PAObjectID, YPO.PaoName, YPO.Type, PF.UserId
        FROM PAOFavorites PF
        LEFT OUTER JOIN YukonPAObject YPO ON
        YPO.PAObjectID = PF.PAObjectId);
BEGIN 
    OPEN favorites_curs;
    LOOP
        FETCH favorites_curs INTO v_PAObjectId, v_PaoName, v_Type, v_UserId;
        EXIT WHEN favorites_curs%NOTFOUND;

        IF v_Type LIKE '%GROUP%'
        THEN
            v_PagePath := '/dr/loadGroup/detail?loadGroupId=' || CAST(v_PAObjectID AS VARCHAR2);
            v_PageName := 'loadGroupDetail';
        END IF;

        IF v_Type LIKE '%PROGRAM%'
        THEN
            v_PagePath := '/dr/program/detail?programId=' || CAST(v_PAObjectID AS VARCHAR2);
            v_PageName := 'programDetail';
        END IF;

        IF v_Type LIKE '%SCENARIO%'
        THEN
            v_PagePath := '/dr/scenario/detail?scenarioId=' || CAST(v_PAObjectID AS VARCHAR2);
            v_PageName := 'scenarioDetail';
        END IF;

        IF v_Type LIKE '%AREA%'
        THEN
            v_PagePath := '/dr/controlArea/detail?controlAreaId=' || CAST(v_PAObjectID AS VARCHAR2);
            v_PageName := 'controlAreaDetail';
        END IF;
 
        SELECT NVL(MAX(UserPageId) + 1, 1) INTO v_UserPageId FROM UserPage;
        SELECT NVL(MAX(UserPageParamId) + 1, 1) INTO v_UserPageParamId FROM UserPageParam;
 
        INSERT INTO UserPage
        VALUES (v_UserPageId, v_UserId, v_PagePath, 'dr', v_PageName, 1, SYSDATE);
 
        INSERT INTO UserPageParam
        VALUES (v_UserPageParamId, v_UserPageId, 0, v_PAOName);
    END LOOP;
    CLOSE favorites_curs;
END;

DROP TABLE PAOFavorites;
DROP TABLE PAORecentViews;
/* End YUK-12525 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '16-OCT-2013', 'Latest Update', 2, SYSDATE);*/