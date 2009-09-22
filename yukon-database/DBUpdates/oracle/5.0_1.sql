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

/* Start YUK-7850 */
CREATE TABLE DeviceGroupComposed  (
   DeviceGroupComposedId NUMBER                          NOT NULL,
   DeviceGroupId        NUMBER                          NOT NULL,
   CompositionType      VARCHAR2(100),
   CONSTRAINT PK_DevGroupComp PRIMARY KEY (DeviceGroupComposedId)
);

ALTER TABLE DeviceGroupComposed
   ADD CONSTRAINT FK_DevGroupComp_DevGroup FOREIGN KEY (DeviceGroupId)
      REFERENCES DeviceGroup (DeviceGroupId)
      ON DELETE CASCADE;

CREATE TABLE DeviceGroupComposedGroup  (
   DeviceGroupComposedGroupId NUMBER                          NOT NULL,
   DeviceGroupComposedId NUMBER                          NOT NULL,
   GroupName            VARCHAR2(255)                   NOT NULL,
   IsNot                CHAR(1)                         NOT NULL,
   CONSTRAINT PK_DevGroupCompGroup PRIMARY KEY (DeviceGroupComposedGroupId)
);

ALTER TABLE DeviceGroupComposedGroup
   ADD CONSTRAINT FK_DevGrpCompGrp_DevGrpComp FOREIGN KEY (DeviceGroupComposedId)
      REFERENCES DeviceGroupComposed (DeviceGroupComposedId)
      ON DELETE CASCADE;
/* End YUK-7850 */
   
/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
