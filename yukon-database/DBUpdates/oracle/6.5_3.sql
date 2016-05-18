/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-15173 */
/* @error warn-once */
/* @start-block */
DECLARE
    v_count NUMBER := 0;
    v_newLine VARCHAR2(2);
    v_errorText VARCHAR2(1024);
BEGIN
    SELECT count(*) INTO v_count FROM USER_INDEXES WHERE INDEX_NAME = 'PKC_RAWPOINTHISTORY';
    IF v_count = 0 THEN 
        v_newLine := CHR(13) || CHR(10);
        v_errorText := 'Indexes on RawPointHistory are being modified to improve system performance.' || v_newLine
            || 'Setup has detected that these indexes have not yet been updated on this system.' || v_newLine
            || 'This can potentially be a long-running task so it is not included in the normal DBToolsFrame update process,' || v_newLine
            || 'some downtime should be scheduled in order to complete this update with minimal system impact.' || v_newLine
            || 'More information can be found in YUK-15173.' || v_newLine
            || 'The SQL for the index update can be found in the file:' || v_newLine
            || '~\YukonMisc\YukonDatabase\DatabaseUpdates\Oracle\RPH_Index_Modification.sql';
        RAISE_APPLICATION_ERROR(-20001, v_errorText);
    END IF;
END;
/
/* @end-block */
/* End YUK-15173 */



/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.5', '11-MAY-2015', 'Latest Update', 3, SYSDATE);