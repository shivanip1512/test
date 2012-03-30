/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10752 */
/* @error ignore-begin */
ALTER TABLE DynamicCCSubstation
    DROP CONSTRAINT FK_DynCCSub_CCSub;
ALTER TABLE DynamicCCSubstation
   ADD CONSTRAINT FK_DynCCSubst_CCSubst FOREIGN KEY (SubStationId)
      REFERENCES CAPCONTROLSUBSTATION (SubstationId)
      ON DELETE CASCADE;
ALTER TABLE DynamicDeviceScanData
    DROP CONSTRAINT FK_DynDeviceScanData;
ALTER TABLE DynamicDeviceScanData 
    ADD CONSTRAINT FK_DynDeviceScanData_Device FOREIGN KEY (DeviceId) 
        REFERENCES Device (DeviceId) 
        ON DELETE CASCADE;
/* @error ignore-end */
/* End YUK-10752 */
        
/* Start YUK-10753 */
/* Testing to see if ManualCoolTemp is the 11th or 12th column in this table.
 * If it is the 12th it is in the wrong spot, so move it to position 11
 * by deleting ManualHeatTemp and re-creating it so that it follows ManualCoolTemp.
 */
/* @start-block */
DECLARE
    v_wrongOrder NUMBER := 0;
BEGIN
    SELECT COLUMN_ID INTO v_wrongOrder
    FROM USER_TAB_COLUMNS
    WHERE TABLE_NAME = 'THERMOSTATEVENTHISTORY'
        AND COLUMN_NAME = 'MANUALCOOLTEMP';
    IF v_wrongOrder = 12 
    THEN
        BEGIN
            EXECUTE IMMEDIATE 'ALTER TABLE ThermostatEventHistory 
                               ADD Temp_ManualHeatTemp NUMBER';
            EXECUTE IMMEDIATE 'UPDATE ThermostatEventHistory
                               SET Temp_ManualHeatTemp = ManualHeatTemp';
            EXECUTE IMMEDIATE 'ALTER TABLE ThermostatEventHistory 
                               DROP COLUMN ManualHeatTemp';
            EXECUTE IMMEDIATE 'ALTER TABLE ThermostatEventHistory 
                               ADD ManualHeatTemp NUMBER';
            EXECUTE IMMEDIATE 'UPDATE ThermostatEventHistory
                               SET ManualHeatTemp = Temp_ManualHeatTemp';
            EXECUTE IMMEDIATE 'ALTER TABLE ThermostatEventHistory
                               DROP COLUMN Temp_ManualHeatTemp';
        END;
    END IF;
END;
/* @end-block */
/* End YUK-10753 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 

