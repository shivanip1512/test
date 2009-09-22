/******************************************/
/**** SQLServer 2000 DBupdates         ****/
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
CREATE TABLE DeviceGroupComposed (
   DeviceGroupComposedId NUMERIC              NOT NULL,
   DeviceGroupId        NUMERIC              NOT NULL,
   CompositionType      VARCHAR(100)         NULL,
   CONSTRAINT PK_DevGroupComp PRIMARY KEY (DeviceGroupComposedId)
);
GO

ALTER TABLE DeviceGroupComposed
   ADD CONSTRAINT FK_DevGroupComp_DevGroup FOREIGN KEY (DeviceGroupId)
      REFERENCES DeviceGroup (DeviceGroupId)
         ON DELETE CASCADE;
GO

CREATE TABLE DeviceGroupComposedGroup (
   DeviceGroupComposedGroupId NUMERIC              NOT NULL,
   DeviceGroupComposedId NUMERIC              NOT NULL,
   GroupName            VARCHAR(255)         NOT NULL,
   IsNot                CHAR(1)              NOT NULL,
   CONSTRAINT PK_DevGroupCompGroup PRIMARY KEY (DeviceGroupComposedGroupId)
);
GO

ALTER TABLE DeviceGroupComposedGroup
   ADD CONSTRAINT FK_DevGrpCompGrp_DevGrpComp FOREIGN KEY (DeviceGroupComposedId)
      REFERENCES DeviceGroupComposed (DeviceGroupComposedId)
         ON DELETE CASCADE;
GO
/* End YUK-7850 */
   
/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
