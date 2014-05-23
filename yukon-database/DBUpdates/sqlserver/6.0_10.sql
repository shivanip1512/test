/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13225 */
ALTER TABLE EstimatedLoadFormula
    ADD CONSTRAINT AK_EstimatedLoadFormula_Name UNIQUE (Name);
/* End YUK-13225 */

/* Start YUK-13277 */
ALTER TABLE InventoryToAcctThermostatSch
   DROP CONSTRAINT FK_InvToAcctThermSch_InvBase;

ALTER TABLE InventoryToAcctThermostatSch
   ADD CONSTRAINT FK_InvToAcctThermSch_InvBase FOREIGN KEY (InventoryId)
      REFERENCES InventoryBase (InventoryID)
      ON DELETE CASCADE;
/* End YUK-13277 */

/* Start YUK-13323 */
INSERT INTO DeviceConfigCategoryItem
SELECT 
    ROW_NUMBER() OVER(ORDER BY C.DeviceConfigCategoryID) + (SELECT ISNULL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
    C.DeviceConfigCategoryId,
    'disconnectMode',
    CASE
        WHEN (cast(DisconnectDemandThreshold.ItemValue AS float) <> 0) THEN 'DEMAND_THRESHOLD'
        WHEN (ConnectMinutes.ItemValue <> '0') THEN 'CYCLING'
        WHEN (DisconnectMinutes.ItemValue <> '0') THEN 'CYCLING'
        ELSE 'ON_DEMAND'
    END
FROM DeviceConfigCategory C 
JOIN DeviceConfigCategoryItem ConnectMinutes ON C.DeviceConfigCategoryId = ConnectMinutes.DeviceConfigCategoryId
JOIN DeviceConfigCategoryItem DisconnectMinutes ON C.DeviceConfigCategoryId = DisconnectMinutes.DeviceConfigCategoryId
JOIN DeviceConfigCategoryItem DisconnectDemandThreshold ON C.DeviceConfigCategoryId = DisconnectDemandThreshold.DeviceConfigCategoryId
WHERE 
    C.CategoryType = 'mctDisconnectConfiguration'
    AND ConnectMinutes.ItemName = 'connectMinutes'
    AND DisconnectMinutes.ItemName = 'disconnectMinutes'
    AND DisconnectDemandThreshold.ItemName = 'disconnectDemandThreshold'
    AND NOT EXISTS (SELECT 1 FROM DeviceConfigCategoryItem DCCI 
                    WHERE C.DeviceConfigCategoryId = DCCI.DeviceConfigCategoryId 
                    AND DCCI.ItemName = 'disconnectMode');
/* End YUK-13323 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '19-MAY-2014', 'Latest Update', 10, GETDATE());*/