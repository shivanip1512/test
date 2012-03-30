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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 

