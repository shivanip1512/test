/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10752 */
/* @error ignore-begin */
ALTER TABLE DynamicCCSubstation
    DROP CONSTRAINT FK_DynCCSubst_CCSubst;
ALTER TABLE DynamicCCSubstation
   ADD CONSTRAINT FK_DynCCSub_CCSub FOREIGN KEY (SubStationId)
      REFERENCES CapControlSubstation (SubstationId)
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
@wrongOrder NUMERIC = 0;
BEGIN
    SET @wrongOrder = (SELECT ORDINAL_POSITION
                       FROM INFORMATION_SCHEMA.COLUMNS 
                       WHERE TABLE_NAME = 'ThermostatEventHistory' 
                         AND COLUMN_NAME = 'ManualCoolTemp');
    IF @wrongOrder = 12
        BEGIN
            EXECUTE ('ALTER TABLE ThermostatEventHistory 
                      ADD Temp_ManualHeatTemp NUMERIC');
            EXECUTE ('UPDATE ThermostatEventHistory 
                      SET Temp_ManualHeatTemp = ManualHeatTemp');
            EXECUTE ('ALTER TABLE ThermostatEventHistory 
                      DROP COLUMN ManualHeatTemp');
            EXECUTE ('ALTER TABLE ThermostatEventHistory 
                      ADD ManualHeatTemp NUMERIC');
            EXECUTE ('UPDATE ThermostatEventHistory 
                      SET ManualHeatTemp = Temp_ManualHeatTemp');
            EXECUTE ('ALTER TABLE ThermostatEventHistory 
                      DROP COLUMN Temp_ManualHeatTemp');
        END
END;
/* @end-block */
/* End YUK-10753 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
