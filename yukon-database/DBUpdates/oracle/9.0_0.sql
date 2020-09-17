/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-22834 */
/* @error warn-once */
/* @start-block */
DECLARE
    v_count NUMBER := 0;
    v_newLine VARCHAR2(2);
    v_errorText VARCHAR2(512);
BEGIN
    SELECT COUNT(*) INTO v_count FROM (SELECT ZoneName, COUNT(*) AS temp_count FROM Zone GROUP BY ZoneName HAVING COUNT(*) > 1);
    IF v_count > 0 THEN
        v_newLine := CHR(13) || CHR(10);
        v_errorText := 'IVVC Zone Names are now required to be unique.' || v_newLine
            || 'Setup has detected that IVVC Zones with duplicate names are present in the system.' || v_newLine
            || 'In order to proceed with the update this must be manually resolved.' || v_newLine
            || 'More information can be found in YUK-22834.' || v_newLine
            || 'To locate Zones that have duplicated names you can use the query below:' || v_newLine
            || 'SELECT ZoneName, COUNT(*) AS NumberOfOccurences FROM Zone GROUP BY ZoneName HAVING COUNT(*) > 1';
        RAISE_APPLICATION_ERROR(-20001, v_errorText);
    END IF;
END;
/
/* @end-block */
ALTER TABLE Zone ADD CONSTRAINT Ak_ZoneName UNIQUE (ZoneName);

INSERT INTO DBUpdates VALUES ('YUK-22834', '9.0.0', SYSDATE);
/* @end YUK-22834 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.0', '09-SEP-2020', 'Latest Update', 0, SYSDATE); */