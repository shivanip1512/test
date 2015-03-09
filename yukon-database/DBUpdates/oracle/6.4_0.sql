/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13973 */
ALTER TABLE EnergyCompany 
ADD ParentEnergyCompanyId NUMBER NULL;

ALTER TABLE EnergyCompany 
    ADD CONSTRAINT FK_EnergyComp_EnergyCompParent FOREIGN KEY (ParentEnergyCompanyId)
       REFERENCES EnergyCompany (EnergyCompanyId);

UPDATE EnergyCompany ec
SET ec.ParentEnergyCompanyId = 
    (SELECT ecm.EnergyCompanyId
     FROM ECToGenericMapping ecm
     WHERE ec.EnergyCompanyId = ecm.ItemId
       AND ecm.MappingCategory = 'Member');

DELETE FROM ECToGenericMapping
WHERE MappingCategory = 'Member';
/* End YUK-13973 */

/* Start YUK-12968 */
ALTER TABLE CapcontrolSubstationBus RENAME CONSTRAINT SYS_C0013476 TO PK_CapControlSubstationBus;
ALTER INDEX SYS_C0013476 RENAME TO PK_CapControlSubstationBus;

ALTER TABLE ColumnType RENAME CONSTRAINT SYS_C0013414 TO PK_ColumnType;
ALTER INDEX SYS_C0013414 RENAME TO PK_ColumnType;

ALTER TABLE CommPort RENAME CONSTRAINT SYS_C0013112 TO PK_CommPort;
ALTER INDEX SYS_C0013112 RENAME TO PK_CommPort;

ALTER TABLE Display RENAME CONSTRAINT SYS_C0013412 TO PK_Display;
ALTER INDEX SYS_C0013412 RENAME TO PK_Display;

ALTER TABLE GraphDataSeries RENAME CONSTRAINT SYS_GrphDserID TO PK_GraphDataSeries;
ALTER INDEX SYS_GrphDserID RENAME TO PK_GraphDataSeries;

ALTER TABLE GraphDefinition RENAME CONSTRAINT SYS_C0015109 TO PK_GraphDefinition;
ALTER INDEX SYS_C0015109 RENAME TO PK_GraphDefinition;

ALTER TABLE Logic RENAME CONSTRAINT SYS_C0013445 TO PK_Logic;
ALTER INDEX SYS_C0013445 RENAME TO PK_Logic;

ALTER TABLE RawPointHistory RENAME CONSTRAINT SYS_C0013322 TO PK_RawPointHistory;
ALTER INDEX SYS_C0013322 RENAME TO PK_RawPointHistory;

ALTER TABLE Route RENAME CONSTRAINT SYS_RoutePK TO PK_Route;
ALTER INDEX SYS_RoutePK RENAME TO PK_Route;

ALTER TABLE SystemLog RENAME CONSTRAINT SYS_C0013407 TO PK_SystemLog;
ALTER INDEX SYS_C0013407 RENAME TO PK_SystemLog;

ALTER TABLE StateGroup RENAME CONSTRAINT SYS_C0013128 TO PK_StateGroup;
ALTER INDEX SYS_C0013128 RENAME TO PK_StateGroup;

ALTER TABLE Template RENAME CONSTRAINT SYS_C0013425 TO PK_Template;
ALTER INDEX SYS_C0013425 RENAME TO PK_Template;

ALTER TABLE UnitMeasure RENAME CONSTRAINT SYS_C0013344 TO PK_UnitMeasure;
ALTER INDEX SYS_C0013344 RENAME TO PK_UnitMeasure;

ALTER TABLE CalcBase RENAME CONSTRAINT SYS_C0013434 TO FK_CalcBase_Point;
ALTER TABLE CapBank  RENAME CONSTRAINT SYS_C0013453 TO FK_CapBank_Device_DeviceId;
ALTER TABLE CapBank  RENAME CONSTRAINT SYS_C0013454 TO FK_CapBank_Point;
ALTER TABLE CapBank  RENAME CONSTRAINT SYS_C0013455 TO FK_CapBank_Device_ControlDev;
ALTER TABLE CapControlSubstationBus  RENAME CONSTRAINT SYS_C0013478 TO FK_CapContrSubBus_Point_WattPt;
ALTER TABLE CapControlSubstationBus  RENAME CONSTRAINT SYS_C0013479 TO FK_CapContrSubBus_Point_VarPt;
ALTER TABLE CapControlSubstationBus  RENAME CONSTRAINT FK_CAPCONTR_CVOLTPTID TO FK_CapContrSubBus_Point_VoltPt;
ALTER TABLE CapControlSubstationBus  RENAME CONSTRAINT FK_CpSbBus_YPao TO FK_CapContrSubBus_YukonPAO;
ALTER TABLE CapControlSubstationBus  RENAME CONSTRAINT FK_CAPCONTR_SWPTID TO FK_CapContrSubBus_Point_Switch;
ALTER TABLE CarrierRoute             RENAME CONSTRAINT SYS_C0013264 TO FK_CarrierRoute_Route;
ALTER TABLE DeviceCarrierSettings    RENAME CONSTRAINT SYS_C0013216 TO FK_DeviceCarrierSetting_Device;
ALTER TABLE DeviceDialupSettings     RENAME CONSTRAINT SYS_C0013193 TO FK_DeviceDialupSettings_Device;
ALTER TABLE DeviceIdlcRemote         RENAME CONSTRAINT SYS_C0013241 TO FK_DeviceIdlcRemote_Device;
ALTER TABLE DeviceIed                RENAME CONSTRAINT SYS_C0013245 TO FK_DeviceIed_Device;
ALTER TABLE DeviceLoadProfile        RENAME CONSTRAINT SYS_C0013234 TO FK_DeviceLoadProfile_Device;
ALTER TABLE DeviceMctIedPort         RENAME CONSTRAINT SYS_C0013253 TO FK_DeviceMctIedPort_Device;
ALTER TABLE DeviceMeterGroup         RENAME CONSTRAINT SYS_C0013213 TO FK_DeviceMeterGroup_Device;
ALTER TABLE DeviceScanRate           RENAME CONSTRAINT SYS_C0013198 TO FK_DeviceScanRate_Device;
ALTER TABLE DeviceTapPagingSettings  RENAME CONSTRAINT SYS_C0013237 TO FK_DeviceTapPagingSet_Device;
ALTER TABLE Display2WayData          RENAME CONSTRAINT SYS_C0013422 TO FK_Display2WayData_Display;
ALTER TABLE DisplayColumns           RENAME CONSTRAINT SYS_C0013418 TO FK_DisplayColumns_Display;
ALTER TABLE DisplayColumns           RENAME CONSTRAINT SYS_C0013419 TO FK_DisplayColumns_ColumnType;
ALTER TABLE DeviceCbc                RENAME CONSTRAINT SYS_C0013459 TO FK_DeviceCbc_Device;
ALTER TABLE DeviceCbc                RENAME CONSTRAINT SYS_C0013460 TO FK_DeviceCbc_Route;
ALTER TABLE DeviceDirectCommSettings RENAME CONSTRAINT SYS_C0013186 TO FK_DeviceDirectCommSet_Device;
ALTER TABLE DeviceDirectCommSettings RENAME CONSTRAINT SYS_C0013187 TO FK_DeviceDirectCommSet_CommPrt;
ALTER TABLE DeviceRoutes       RENAME CONSTRAINT SYS_C0013219 TO FK_DeviceRoutes_Device;
ALTER TABLE DeviceRoutes       RENAME CONSTRAINT SYS_C0013220 TO FK_DeviceRoutes_Route;
ALTER TABLE FdrTranslation     RENAME CONSTRAINT SYS_C0015066 TO FK_FdrTranslation_Point;
ALTER TABLE LmGroupEmetcon     RENAME CONSTRAINT SYS_C0013356 TO FK_LmGroupEmetcon_LMGroup;
ALTER TABLE LmGroupEmetcon     RENAME CONSTRAINT SYS_C0013357 TO FK_LmGroupEmetcon_Route;
ALTER TABLE LmGroupVersacom    RENAME CONSTRAINT SYS_C0013367 TO FK_LmGroupVersacom_Route;
ALTER TABLE MacroRoute         RENAME CONSTRAINT SYS_C0013274 TO FK_MacroRoute_Route_RouteId;
ALTER TABLE MacroRoute         RENAME CONSTRAINT SYS_C0013275 TO FK_MacroRoute_Route_SingleRtId;
ALTER TABLE PointAccumulator   RENAME CONSTRAINT SYS_C0013317 TO FK_PointAccumulator_Point;
ALTER TABLE PointAnalog        RENAME CONSTRAINT SYS_C0013300 TO FK_PointAnalog_Point;
ALTER TABLE PointLimits        RENAME CONSTRAINT SYS_C0013289 TO FK_PointLimits_Point;
ALTER TABLE PortDialupModem    RENAME CONSTRAINT SYS_C0013175 TO FK_PortDialupModem_CommPort;
ALTER TABLE PortLocalSerial    RENAME CONSTRAINT SYS_C0013147 TO FK_PortLocalSerial_CommPort;
ALTER TABLE PortRadioSettings  RENAME CONSTRAINT SYS_C0013169 TO FK_PortRadioSettings_CommPort;
ALTER TABLE PortSettings       RENAME CONSTRAINT SYS_C0013156 TO FK_PortSettings_CommPort;
ALTER TABLE PortTerminalServer RENAME CONSTRAINT SYS_C0013151 TO FK_PortTerminalServer_CommPort;
ALTER TABLE PortTiming         RENAME CONSTRAINT SYS_C0013163 TO FK_PortTiming_CommPort;
ALTER TABLE RepeaterRoute      RENAME CONSTRAINT SYS_C0013269 TO FK_RepeaterRoute_Route;
ALTER TABLE RepeaterRoute      RENAME CONSTRAINT SYS_C0013270 TO FK_RepeaterRoute_Device;
ALTER TABLE State              RENAME CONSTRAINT SYS_C0013342 TO FK_State_StateGroup;
ALTER TABLE TemplateColumns    RENAME CONSTRAINT SYS_C0013429 TO FK_TemplateColumns_Template;
ALTER TABLE TemplateColumns    RENAME CONSTRAINT SYS_C0013430 TO FK_TemplateColumns_ColumnType;
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

/* Start YUK-14066 */
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
    v_configNumber                   NUMBER := 1;
    v_newConfigId                    NUMBER;
    v_newConfigCategoryId            NUMBER;
    v_newConfigCategoryItemId        NUMBER;
    v_newConfigDeviceTypeId          NUMBER;
    v_keepAliveTimer                 NUMBER;
    v_keepAliveConfig                NUMBER;
    v_voltChangePerTap               NUMBER;
    
    CURSOR regulator_curs IS SELECT DISTINCT KeepAliveTimer, KeepAliveConfig, VoltChangePerTap 
                             FROM Regulator R
                             ORDER BY KeepAliveTimer ASC, KeepAliveConfig ASC, VoltChangePerTap ASC;
BEGIN
    OPEN regulator_curs;
    LOOP
        FETCH regulator_curs into v_keepAliveTimer, v_keepAliveConfig, v_voltChangePerTap;
        EXIT WHEN regulator_curs%NOTFOUND;
        
        SELECT MAX(DeviceConfigurationId) + 1 INTO v_newConfigId FROM DeviceConfiguration;
        INSERT INTO DeviceConfiguration (DeviceConfigurationId, Name, Description)
        VALUES (v_NewConfigId, 'Generated Regulator Config ' || v_configNumber, NULL);
        
        SELECT MAX(DeviceConfigCategoryId) + 1 INTO v_newConfigCategoryId FROM DeviceConfigCategory;
        INSERT INTO DeviceConfigCategory  (DeviceConfigCategoryId, CategoryType, Name, Description)
        VALUES (v_newConfigCategoryId, 'regulatorCategory', 'Generated Regulator Config Category ' || v_configNumber, NULL);
        
        INSERT INTO DeviceConfigCategoryMap (DeviceConfigurationId, DeviceConfigCategoryId)
        VALUES (v_newConfigId, v_newConfigCategoryId);
        
        SELECT MAX(DeviceConfigCategoryItemId) INTO v_newConfigCategoryItemId FROM DeviceConfigCategoryItem;
        INSERT INTO DeviceConfigCategoryItem VALUES (v_newConfigCategoryItemId + 1, v_newConfigCategoryId, 'voltageChangePerTap', v_voltChangePerTap);
        INSERT INTO DeviceConfigCategoryItem VALUES (v_newConfigCategoryItemId + 2, v_newConfigCategoryId, 'heartbeatPeriod', v_keepAliveTimer);
        INSERT INTO DeviceConfigCategoryItem VALUES (v_newConfigCategoryItemId + 3, v_newConfigCategoryId, 'heartbeatValue', v_keepAliveConfig);
        INSERT INTO DeviceConfigCategoryItem VALUES (v_newConfigCategoryItemId + 4, v_newConfigCategoryId, 'voltageControlMode', 'DIRECT_TAP');

        SELECT MAX(DeviceConfigDeviceTypeId) INTO v_newConfigDeviceTypeId FROM DeviceConfigDeviceTypes;
        INSERT INTO DeviceConfigDeviceTypes VALUES (v_newConfigDeviceTypeId + 1, v_newConfigId, 'LTC');
        INSERT INTO DeviceConfigDeviceTypes VALUES (v_newConfigDeviceTypeId + 2, v_newConfigId, 'GO_REGULATOR');
        INSERT INTO DeviceConfigDeviceTypes VALUES (v_newConfigDeviceTypeId + 3, v_newConfigId, 'PO_REGULATOR');

        INSERT INTO DeviceConfigurationDeviceMap (DeviceId, DeviceConfigurationId)
           (SELECT RegulatorId, v_newConfigId FROM Regulator
            WHERE KeepAliveTimer = v_keepAliveTimer
              AND KeepAliveConfig = v_keepAliveConfig
              AND VoltChangePerTap = v_voltChangePerTap);

        v_configNumber := v_configNumber + 1;
    END LOOP;
    CLOSE regulator_curs;
END;
/
/* @end-block */
/* End YUK-14066 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-JAN-2015', 'Latest Update', 0, SYSDATE);*/