/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-10000 */ 
UPDATE State 
SET Text = 'Connected'
WHERE StateGroupId = -13
  AND RawState = 0; 

INSERT INTO State VALUES(-13, 2, 'Disconnected', 2, 6, 0); 

DELETE FROM PointStatus 
WHERE PointId IN (SELECT P.PointId 
                  FROM Point P 
                    JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                  WHERE P.StateGroupId = -11 
                    AND (PAO.Type = 'ZigBee Utility Pro' OR 
                         PAO.Type = 'Digi Gateway')); 

DELETE FROM PointAlarming 
WHERE PointId IN (SELECT P.PointId 
                  FROM Point P 
                    JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                  WHERE P.StateGroupId = -11 
                    AND (PAO.Type = 'ZigBee Utility Pro' OR 
                         PAO.Type = 'Digi Gateway')); 

DELETE FROM DynamicPointDispatch 
WHERE PointId IN (SELECT P.PointId 
                  FROM Point P 
                    JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                  WHERE P.StateGroupId = -11 
                    AND (PAO.Type = 'ZigBee Utility Pro' OR 
                         PAO.Type = 'Digi Gateway')); 

DELETE FROM Point 
WHERE PointId IN (SELECT P.PointId 
                  FROM Point P 
                    JOIN YukonPAObject PAO ON PAO.PAObjectId = P.PAObjectId 
                  WHERE P.StateGroupId = -11 
                    AND (PAO.Type = 'ZigBee Utility Pro' OR 
                         PAO.Type = 'Digi Gateway')); 
/* End YUK-10000 */

/* Start YUK-10062 */ 
CREATE INDEX Indx_DevCarSet_Address ON DeviceCarrierSettings (
   ADDRESS ASC
);
/* End YUK-10062 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
