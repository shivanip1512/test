/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13973 */
ALTER TABLE EnergyCompany 
ADD ParentEnergyCompanyId NUMERIC NULL;
GO

ALTER TABLE EnergyCompany 
    ADD CONSTRAINT FK_EnergyComp_EnergyCompParent FOREIGN KEY (ParentEnergyCompanyId)
       REFERENCES EnergyCompany (EnergyCompanyId);
GO

UPDATE EnergyCompany
SET ParentEnergyCompanyId = ecm.EnergyCompanyId
FROM ECToGenericMapping ecm
WHERE EnergyCompany.EnergyCompanyId = ecm.ItemId
  AND ecm.MappingCategory = 'Member';

DELETE FROM ECToGenericMapping
WHERE MappingCategory = 'Member';
/* End YUK-13973 */

/* Start YUK-12968 */
GO
sp_rename 'SYS_C0013476', 'PK_CapControlSubstationBus', 'OBJECT'; 
GO
sp_rename 'SYS_C0013414', 'PK_ColumnType', 'OBJECT';
GO
sp_rename 'SYS_C0013112', 'PK_CommPort', 'OBJECT';
GO
sp_rename 'SYS_C0013412', 'PK_Display', 'OBJECT';
GO
sp_rename 'SYS_GrphDserID', 'PK_GraphDataSeries', 'OBJECT';
GO
sp_rename 'SYS_C0015109', 'PK_GraphDefinition', 'OBJECT';
GO
sp_rename 'SYS_C0013445', 'PK_Logic', 'OBJECT';
GO
sp_rename 'SYS_C0013322', 'PK_RawPointHistory', 'OBJECT';
GO
sp_rename 'SYS_RoutePK', 'PK_Route', 'OBJECT';
GO
sp_rename 'SYS_C0013407', 'PK_SystemLog', 'OBJECT';
GO
sp_rename 'SYS_C0013128', 'PK_StateGroup', 'OBJECT';
GO
sp_rename 'SYS_C0013425', 'PK_Template', 'OBJECT';
GO
sp_rename 'SYS_C0013344', 'PK_UnitMeasure', 'OBJECT';
GO
sp_rename 'SYS_C0013434', 'FK_CalcBase_Point', 'OBJECT';
GO
sp_rename 'SYS_C0013453', 'FK_CapBank_Device_DeviceId', 'OBJECT';
GO
sp_rename 'SYS_C0013454', 'FK_CapBank_Point', 'OBJECT';
GO
sp_rename 'SYS_C0013455', 'FK_CapBank_Device_ControlDev', 'OBJECT';
GO
sp_rename 'SYS_C0013478', 'FK_CapContrSubBus_Point_WattPt', 'OBJECT';
GO
sp_rename 'SYS_C0013479', 'FK_CapContrSubBus_Point_VarPt', 'OBJECT';
GO
sp_rename 'SYS_C0013264', 'FK_CarrierRoute_Route', 'OBJECT';
GO
sp_rename 'SYS_C0013216', 'FK_DeviceCarrierSetting_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013193', 'FK_DeviceDialupSettings_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013241', 'FK_DeviceIdlcRemote_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013245', 'FK_DeviceIed_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013234', 'FK_DeviceLoadProfile_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013253', 'FK_DeviceMctIedPort_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013213', 'FK_DeviceMeterGroup_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013198', 'FK_DeviceScanRate_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013237', 'FK_DeviceTapPagingSet_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013422', 'FK_Display2WayData_Display', 'OBJECT';
GO
sp_rename 'SYS_C0013418', 'FK_DisplayColumns_Display', 'OBJECT';
GO
sp_rename 'SYS_C0013419', 'FK_DisplayColumns_ColumnType', 'OBJECT';
GO
sp_rename 'SYS_C0013459', 'FK_DeviceCbc_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013460', 'FK_DeviceCbc_Route', 'OBJECT';
GO
sp_rename 'SYS_C0013186', 'FK_DeviceDirectCommSet_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013187', 'FK_DeviceDirectCommSet_CommPrt', 'OBJECT';
GO
sp_rename 'SYS_C0013219', 'FK_DeviceRoutes_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013220', 'FK_DeviceRoutes_Route', 'OBJECT';
GO
sp_rename 'SYS_C0015066', 'FK_FdrTranslation_Point', 'OBJECT';
GO
sp_rename 'SYS_C0013356', 'FK_LmGroupEmetcon_LMGroup', 'OBJECT';
GO
sp_rename 'SYS_C0013357', 'FK_LmGroupEmetcon_Route', 'OBJECT';
GO
sp_rename 'SYS_C0013367', 'FK_LmGroupVersacom_Route', 'OBJECT';
GO
sp_rename 'SYS_C0013274', 'FK_MacroRoute_Route_RouteId', 'OBJECT';
GO
sp_rename 'SYS_C0013275', 'FK_MacroRoute_Route_SingleRtId', 'OBJECT';
GO
sp_rename 'SYS_C0013317', 'FK_PointAccumulator_Point', 'OBJECT';
GO
sp_rename 'SYS_C0013300', 'FK_PointAnalog_Point', 'OBJECT';
GO
sp_rename 'SYS_C0013289', 'FK_PointLimits_Point', 'OBJECT';
GO
sp_rename 'SYS_C0013175', 'FK_PortDialupModem_CommPort', 'OBJECT';
GO
sp_rename 'SYS_C0013147', 'FK_PortLocalSerial_CommPort', 'OBJECT';
GO
sp_rename 'SYS_C0013169', 'FK_PortRadioSettings_CommPort', 'OBJECT';
GO
sp_rename 'SYS_C0013156', 'FK_PortSettings_CommPort', 'OBJECT';
GO
sp_rename 'SYS_C0013151', 'FK_PortTerminalServer_CommPort', 'OBJECT';
GO
sp_rename 'SYS_C0013163', 'FK_PortTiming_CommPort', 'OBJECT';
GO
sp_rename 'SYS_C0013269', 'FK_RepeaterRoute_Route', 'OBJECT';
GO
sp_rename 'SYS_C0013270', 'FK_RepeaterRoute_Device', 'OBJECT';
GO
sp_rename 'SYS_C0013342', 'FK_State_StateGroup', 'OBJECT';
GO
sp_rename 'SYS_C0013429', 'FK_TemplateColumns_Template', 'OBJECT';
GO
sp_rename 'SYS_C0013430', 'FK_TemplateColumns_ColumnType', 'OBJECT';
GO
sp_rename 'FK_CAPCONTR_CVOLTPTID', 'FK_CapContrSubBus_Point_VoltPt', 'OBJECT';
GO
sp_rename 'FK_CpSbBus_YPao', 'FK_CapContrSubBus_YukonPAO', 'OBJECT';
GO
sp_rename 'FK_CAPCONTR_SWPTID', 'FK_CapContrSubBus_Point_Switch', 'OBJECT';
/* End YUK-12968 */

/* Start YUK-14083 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-21403, -214, 'Infrastructure View', 'false', 'Controls the ability to view infrastructure devices. i.e. RF Gateways.');
/* @error ignore-end */
/* End YUK-14083 */

/* Start YUK-14006 */
UPDATE UserPage
SET PagePath = '/capcontrol/schedules'
WHERE PagePath = '/capcontrol/schedule/schedules';

UPDATE UserPage
SET PagePath = '/capcontrol/schedules/assignments'
WHERE PagePath = '/capcontrol/schedule/scheduleAssignments';
/* End YUK-14006 */

/* Start YUK-14006 */
INSERT INTO DeviceConfiguration VALUES (-2, 'Default Regulator Configuration', null);

INSERT INTO DeviceConfigDeviceTypes VALUES (-10, -2, 'LTC');
INSERT INTO DeviceConfigDeviceTypes VALUES (-11, -2, 'GO_REGULATOR');
INSERT INTO DeviceConfigDeviceTypes VALUES (-12, -2, 'PO_REGULATOR');

INSERT INTO DeviceConfigCategory VALUES (-2, 'regulatorCategory', 'Default Regulator Category', null);

INSERT INTO DeviceConfigCategoryMap VALUES(-2, -2);

INSERT INTO DeviceConfigCategoryItem VALUES (-1, -2, 'voltageChangePerTap', '0.75');
INSERT INTO DeviceConfigCategoryItem VALUES (-2, -2, 'heartbeatPeriod', '0');
INSERT INTO DeviceConfigCategoryItem VALUES (-3, -2, 'heartbeatValue', '0');
INSERT INTO DeviceConfigCategoryItem VALUES (-4, -2, 'voltageControlMode', 'DIRECT_TAP');

/* @start-block */
DECLARE
    @configNumber                   NUMERIC = 1,
    @newConfigId                    NUMERIC,
    @newConfigCategoryId            NUMERIC,
    @newConfigCategoryItemId        NUMERIC,
    @newConfigDeviceTypeId          NUMERIC,
    @keepAliveTimer                 NUMERIC,
    @keepAliveConfig                NUMERIC,
    @voltChangePerTap               NUMERIC(18,5);
    
    DECLARE regulator_curs CURSOR FOR (SELECT DISTINCT KeepAliveTimer, KeepAliveConfig, VoltChangePerTap FROM Regulator);
BEGIN
    OPEN regulator_curs;
    FETCH FROM regulator_curs INTO @keepAliveTimer, @keepAliveConfig, @voltChangePerTap;
    WHILE @@FETCH_STATUS = 0
    BEGIN
        SET @newConfigId = (SELECT MAX(DeviceConfigurationId) + 1 FROM DeviceConfiguration);
        INSERT INTO DeviceConfiguration (DeviceConfigurationId, Name, Description)
        VALUES (@NewConfigId, 'Generated Regulator Config ' + CAST(@configNumber AS VARCHAR(32)), NULL);
        
        SET @newConfigCategoryId = (SELECT MAX(DeviceConfigCategoryId) + 1 FROM DeviceConfigCategory);
        INSERT INTO DeviceConfigCategory  (DeviceConfigCategoryId, CategoryType, Name, Description)
        VALUES (@newConfigCategoryId, 'regulatorCategory', 'Generated Regulator Config Category ' + CAST(@configNumber AS VARCHAR(32)), NULL);
        
        INSERT INTO DeviceConfigCategoryMap (DeviceConfigurationId, DeviceConfigCategoryId)
        VALUES (@newConfigId, @newConfigCategoryId);
        
        SET @newConfigCategoryItemId = (SELECT MAX(DeviceConfigCategoryItemId) FROM DeviceConfigCategoryItem);
        INSERT INTO DeviceConfigCategoryItem VALUES (@newConfigCategoryItemId + 1, @newConfigCategoryId, 'voltageChangePerTap', @voltChangePerTap);
        INSERT INTO DeviceConfigCategoryItem VALUES (@newConfigCategoryItemId + 2, @newConfigCategoryId, 'heartbeatPeriod', @keepAliveTimer);
        INSERT INTO DeviceConfigCategoryItem VALUES (@newConfigCategoryItemId + 3, @newConfigCategoryId, 'heartbeatValue', @keepAliveConfig);
        INSERT INTO DeviceConfigCategoryItem VALUES (@newConfigCategoryItemId + 4, @newConfigCategoryId, 'voltageControlMode', 'DIRECT_TAP');

        SET @newConfigDeviceTypeId = (SELECT MAX(DeviceConfigDeviceTypeId) FROM DeviceConfigDeviceTypes);
        INSERT INTO DeviceConfigDeviceTypes VALUES (@newConfigDeviceTypeId + 1, @newConfigId, 'LTC');
        INSERT INTO DeviceConfigDeviceTypes VALUES (@newConfigDeviceTypeId + 2, @newConfigId, 'GO_REGULATOR');
        INSERT INTO DeviceConfigDeviceTypes VALUES (@newConfigDeviceTypeId + 3, @newConfigId, 'PO_REGULATOR');

        INSERT INTO DeviceConfigurationDeviceMap (DeviceId, DeviceConfigurationId)
           (SELECT RegulatorId, @newConfigId FROM Regulator
            WHERE KeepAliveTimer = @keepAliveTimer
              AND KeepAliveConfig = @keepAliveConfig
              AND VoltChangePerTap = @voltChangePerTap);

        SET @configNumber = @configNumber + 1;

       FETCH FROM regulator_curs INTO @keepAliveTimer, @keepAliveConfig, @voltChangePerTap;
    END
    CLOSE regulator_curs;
    DEALLOCATE regulator_curs;
END;
/* @end-block */
/* End YUK-14006 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-JAN-2015', 'Latest Update', 0, GETDATE());*/