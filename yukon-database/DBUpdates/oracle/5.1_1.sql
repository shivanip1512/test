/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8218 */
INSERT INTO YukonRoleProperty VALUES(-20910,-209,'Device Reconfiguration','false','Access to Device Reconfiguration Tool');
/* End YUK-8218 */

/* Start YUK-8219 */
ALTER TABLE OptOutTemporaryOverride
    ADD CONSTRAINT FK_OptOutTempOver_LMProgWebPub FOREIGN KEY (ProgramId)
        REFERENCES LMProgramWebPublishing (ProgramId);
/* End YUK-8219 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
