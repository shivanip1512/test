SET MODE MSSQLServer;

/*==============================================================*/
/* Table: YukonPAObject                                         */
/*==============================================================*/
create table YukonPAObject  (
   PAObjectID           NUMBER                          not null,
   Category             VARCHAR2(20)                    not null,
   PAOClass             VARCHAR2(20)                    not null,
   PAOName              VARCHAR2(60)                    not null,
   Type                 VARCHAR2(30)                    not null,
   Description          VARCHAR2(60)                    not null,
   DisableFlag          CHAR(1)                         not null,
   PAOStatistics        VARCHAR2(10)                    not null,
   constraint PK_YUKONPAOBJECT primary key (PAObjectID)
);

INSERT into YukonPAObject values (0, 'DEVICE', 'SYSTEM', 'System Device', 'SYSTEM', 'Reserved System Device', 'N', '-----'); 

/*==============================================================*/
/* Index: Indx_PAO                                              */
/*==============================================================*/
create unique index Indx_PAO on YukonPAObject (
   Category ASC,
   PAOName ASC,
   PAOClass ASC
);

/*==============================================================*/
/* Table: DEVICE                                                */
/*==============================================================*/
create table DEVICE  (
   DEVICEID             NUMBER                          not null,
   ALARMINHIBIT         VARCHAR2(1)                     not null,
   CONTROLINHIBIT       VARCHAR2(1)                     not null,
   constraint PK_DEV_DEVICEID2 primary key (DEVICEID)
);

INSERT into device values (0, 'N', 'N');

alter table DEVICE
   add constraint FK_Dev_YukPAO foreign key (DEVICEID)
      references YukonPAObject (PAObjectID);


/*==============================================================*/
/* Table: DeviceAddress                                         */
/*==============================================================*/
create table DeviceAddress  (
   DeviceID             NUMBER                          not null,
   MasterAddress        NUMBER                          not null,
   SlaveAddress         NUMBER                          not null,
   PostCommWait         NUMBER                          not null,
   constraint PK_DEVICEADDRESS primary key (DeviceID)
);

alter table DeviceAddress
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID);

/*==============================================================*/
/* Table: DeviceDirectCommSettings                              */
/*==============================================================*/
create table DeviceDirectCommSettings  (
   DEVICEID             NUMBER                          not null,
   PORTID               NUMBER                          not null,
   constraint PK_DEVICEDIRECTCOMMSETTINGS primary key (DEVICEID)
);

alter table DeviceDirectCommSettings
   add constraint SYS_C0013186 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

-- For our current unit test purposes, we don't have a CommPort table.
-- alter table DeviceDirectCommSettings
--    add constraint SYS_C0013187 foreign key (PORTID)
--       references CommPort (PORTID);


/*==============================================================*/
/* Table: DeviceWindow                                          */
/*==============================================================*/
create table DeviceWindow  (
   DeviceID             NUMBER                          not null,
   Type                 VARCHAR2(20)                    not null,
   WinOpen              NUMBER                          not null,
   WinClose             NUMBER                          not null,
   AlternateOpen        NUMBER                          not null,
   AlternateClose       NUMBER                          not null,
   constraint PK_DEVICEWINDOW primary key (DeviceID, Type)
);

alter table DeviceWindow
   add constraint FK_DevScWin_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);


/*==============================================================*/
/* Table: DEVICESCANRATE                                        */
/*==============================================================*/
create table DEVICESCANRATE  (
   DEVICEID             NUMBER                          not null,
   SCANTYPE             VARCHAR2(20)                    not null,
   INTERVALRATE         NUMBER                          not null,
   SCANGROUP            NUMBER                          not null,
   AlternateRate        NUMBER                          not null,
   constraint PK_DEVICESCANRATE primary key (DEVICEID, SCANTYPE)
);

alter table DEVICESCANRATE
   add constraint SYS_C0013198 foreign key (DEVICEID)
      references DEVICE (DEVICEID);


/*==============================================================*/
/* Table: DeviceCBC                                             */
/*==============================================================*/
create table DeviceCBC  (
   DEVICEID             NUMBER                          not null,
   SERIALNUMBER         NUMBER                          not null,
   ROUTEID              NUMBER                          not null,
   constraint PK_DEVICECBC primary key (DEVICEID)
);

alter table DeviceCBC
   add constraint SYS_C0013459 foreign key (DEVICEID)
      references DEVICE (DEVICEID);

-- For our current unit test purposes, we don't have a Route table.
-- alter table DeviceCBC
--    add constraint SYS_C0013460 foreign key (ROUTEID)
--       references Route (RouteID);

/*==============================================================*/
/* Table: EnergyCompanySetting                                  */
/*==============================================================*/
create table EnergyCompanySetting (
   EnergyCompanySettingId numeric            not null,
   EnergyCompanyId      numeric              not null,
   Name                 varchar(100)         not null,
   Value                varchar(1000)        null,
   Enabled              char(1)              null,
   Comments             varchar(1000)        null,
   LastChangedDate      datetime             null,
   constraint PK_EnergyCompanySetting primary key (EnergyCompanySettingId)
);

alter table EnergyCompanySetting
   add constraint AK_ECSetting_ECId_Name unique (EnergyCompanyId, Name);

create table RfnBroadcastEventDeviceStatus (
   DeviceId             numeric              not null,
   RfnBroadcastEventId  numeric              not null,
   Result               varchar(30)          not null,
   DeviceReceivedTime   datetime             null,
   constraint PK_RfnBroadcastEventDevStatus primary key (DeviceId, RfnBroadcastEventId)
);

create table InventoryBase (
   InventoryID          numeric              not null,
   AccountID            numeric              null,
   InstallationCompanyID numeric              null,
   CategoryID           numeric              not null,
   ReceiveDate          datetime             null,
   InstallDate          datetime             null,
   RemoveDate           datetime             null,
   AlternateTrackingNumber varchar(40)          null,
   VoltageID            numeric              null,
   Notes                varchar(500)         null,
   DeviceID             numeric              null,
   DeviceLabel          varchar(60)          null,
   CurrentStateID       numeric              not null,
   constraint PK_INVENTORYBASE primary key (InventoryID)
);

create table LMHardwareBase (
   InventoryID          numeric              not null,
   ManufacturerSerialNumber varchar(30)          null,
   LMHardwareTypeID     numeric              not null,
   RouteID              numeric              not null,
   ConfigurationID      numeric              not null,
   constraint PK_LMHARDWAREBASE primary key (InventoryID)
);

create table ECToInventoryMapping (
   EnergyCompanyID      numeric              not null,
   InventoryID          numeric              not null,
   constraint PK_ECTOINVENTORYMAPPING primary key (EnergyCompanyID, InventoryID)
);

create table CustomerAccount (
   AccountID            numeric              not null,
   AccountSiteID        numeric              null,
   AccountNumber        varchar(40)          null,
   CustomerID           numeric              not null,
   BillingAddressID     numeric              null,
   AccountNotes         varchar(200)         null,
   constraint PK_CUSTOMERACCOUNT primary key (AccountID)
);

create table DynamicLcrCommunications (
   DeviceId             numeric              not null,
   LastCommunication    datetime             null,
   LastNonZeroRuntime   datetime             null,
   Relay1Runtime        datetime             null,
   Relay2Runtime        datetime             null,
   Relay3Runtime        datetime             null,
   Relay4Runtime        datetime             null,
   constraint PK_DynamicLcrCommunications primary key (DeviceId)
);

create table LMHardwareControlGroup (
   ControlEntryID       int                  not null,
   InventoryID          int                  not null,
   LMGroupID            int                  not null,
   AccountID            int                  not null,
   GroupEnrollStart     datetime             null,
   GroupEnrollStop      datetime             null,
   OptOutStart          datetime             null,
   OptOutStop           datetime             null,
   Type                 int                  not null,
   Relay                int                  not null,
   UserIDFirstAction    int                  not null,
   UserIDSecondAction   int                  not null,
   ProgramId            int                  not null,
   constraint PK_LMHARDWARECONTROLGROUP primary key (ControlEntryID)
);