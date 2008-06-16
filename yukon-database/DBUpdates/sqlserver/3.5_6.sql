/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-5961 */
ALTER TABLE DeviceGroupMember DROP CONSTRAINT FK_DevGrpMember_DeviceGroup;
go 

ALTER TABLE DeviceGroup DROP CONSTRAINT FK_DeviceGroup_DeviceGroup;
go

CREATE TABLE TempDeviceGroupIdMap ( 
   deviceGroupId NUMERIC(18,0), 
   newDeviceGroupId NUMERIC(18,0) 
); 
go 

INSERT INTO TempDeviceGroupIdMap (deviceGroupId, newDeviceGroupId) 
(SELECT deviceGroupid, deviceGroupid 
 FROM DeviceGroup 
 WHERE deviceGroupId > 11 and deviceGroupId < 101); 
go 

UPDATE tempDeviceGroupIdMap 
SET newDeviceGroupId = deviceGroupId + (SELECT CASE 
                                        WHEN (MAX(deviceGroupId) is NULL) 
                                            THEN 101 
                                        ELSE MAX(deviceGroupId) + 1 
                                        END 
                                        FROM DeviceGroup 
                                        WHERE deviceGroupId > 100);
go 

UPDATE DeviceGroup 
SET parentDeviceGroupId = (SELECT newDeviceGroupId 
                           FROM TempDeviceGroupIdMap DGMAP
                           WHERE deviceGroup.parentDeviceGroupId = DGMAP.deviceGroupId) 
WHERE deviceGroupId IN (SELECT DISTINCT deviceGroupId 
                        FROM TempDeviceGroupIdMap
                        WHERE parentDeviceGroupId > 11);
go

UPDATE DeviceGroup 
SET deviceGroupId = (SELECT newDeviceGroupId 
                     FROM TempDeviceGroupIdMap DGMAP 
                     WHERE deviceGroup.deviceGroupId = DGMAP.deviceGroupId) 
WHERE deviceGroupId IN (SELECT DISTINCT deviceGroupId 
                        FROM TempDeviceGroupIdMap);
go

UPDATE DeviceGroupMember 
SET deviceGroupId = (SELECT newDeviceGroupid 
                     FROM TempDeviceGroupIdMap DGMAP 
                     WHERE DeviceGroupMember.deviceGroupId = DGMAP.deviceGroupId) 
WHERE deviceGroupId IN (SELECT DISTINCT deviceGroupId 
                        FROM TempDeviceGroupIdMap);
go 

DROP TABLE TempDeviceGroupIdMap; 
go 

ALTER TABLE DEVICEGROUP
    ADD CONSTRAINT FK_DeviceGroup_DeviceGroup FOREIGN KEY (parentDeviceGroupId)
        REFERENCES DeviceGroup (deviceGroupId);
go

ALTER TABLE DEVICEGROUPMEMBER 
   ADD CONSTRAINT FK_DevGrpMember_DeviceGroup foreign key (deviceGroupId) 
      REFERENCES DEVICEGROUP (deviceGroupId); 
go

DELETE FROM SequenceNumber 
WHERE SequenceName = 'DeviceGroup'; 

/* @start-block */
DECLARE @dGSequenceNumber INT; 
SET @dGSequenceNumber = CAST((SELECT CASE  
                                  WHEN (MAX(DeviceGroupId) IS NULL)
                                      THEN 100 
                                  ELSE 
                                      MAX(deviceGroupId) 
                                  END 
                              FROM DeviceGroup 
                              WHERE deviceGroupId > 100) as INT);


INSERT INTO SequenceNumber 
VALUES (@dGSequenceNumber, 'DeviceGroup');
/* @end-block */
go
/* End YUK-5961 */

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

insert into CTIDatabase values('3.5', 'Matt K', '30-May-2008', 'Latest Update', 6 );