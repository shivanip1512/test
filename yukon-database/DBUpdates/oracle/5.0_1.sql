/******************************************/
/**** Oracle DBupdates                 ****/
/******************************************/

/* Start YUK-7845 */
/* @error ignore-begin */
ALTER TABLE CommandRequestExecResult
   DROP CONSTRAINT FK_ComReqExecResult_Device;

ALTER TABLE CommandRequestExecResult
   DROP CONSTRAINT FK_ComReqExecResult_Route;
/* @error ignore-end */
/* End YUK-7845 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
