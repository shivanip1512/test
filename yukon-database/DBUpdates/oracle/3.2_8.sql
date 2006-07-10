/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

/* @error ignore */
drop table dynamicccfeeder;

create table DynamicCCFeeder (
   FeederID             number              not null,
   CurrentVarPointValue float                not null,
   CurrentWattPointValue float                not null,
   NewPointDataReceivedFlag char(1)              not null,
   LastCurrentVarUpdate date             not null,
   EstimatedVarPointValue float                not null,
   CurrentDailyOperations number              not null,
   RecentlyControlledFlag char(1)              not null,
   LastOperationTime    date             not null,
   VarValueBeforeControl float                not null,
   LastCapBankDeviceID  number              not null,
   BusOptimizedVarCategory number              not null,
   BusOptimizedVarOffset float                not null,
   CTITimeStamp         date             not null,
   PowerFactorValue     float                not null,
   KvarSolution         float                not null,
   EstimatedPFValue     float                not null,
   CurrentVarPointQuality number              not null,
   WaiveControlFlag     char(1)              not null,
   AdditionalFlags      varchar2(32)          not null,
   CurrentVoltPointValue float                not null,
   EventSeq             number              not null,
   CurrVerifyCBId       number              not null,
   CurrVerifyCBOrigState number              not null
);

alter table DynamicCCFeeder
   add constraint PK_DYNAMICCCFEEDER primary key  (FeederID);

alter table DynamicCCFeeder
   add constraint FK_CCFeed_DyFeed foreign key (FeederID)
      references CapControlFeeder (FeederID);


alter table dynamicccsubstationbus add multiVoltControlState number;
update dynamicccsubstationbus  set multiVoltControlState = 0;
alter table dynamicccsubstationbus modify multiVoltControlState number not null;

alter table dynamicccfeeder add multiVoltControlState number;
update dynamicccfeeder  set multiVoltControlState = 0;
alter table dynamicccfeeder modify multiVoltControlState number not null;

drop table configurationvalue;
drop table configurationparts;
drop table configurationpartsname;
drop table deviceconfiguration;
drop table configurationname;

/*==============================================================*/
/* Table: DCItemType                                            */
/*==============================================================*/
create table DCItemType  (
   ItemTypeID           NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null
);

alter table DCItemType
   add constraint PK_DCITEMTYPE primary key (ItemTypeID);

/*==============================================================*/
/* Table: DCCategoryType                                        */
/*==============================================================*/
create table DCCategoryType  (
   CategoryTypeID       NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null
);

alter table DCCategoryType
   add constraint PK_DCCATEGORYTYPE primary key (CategoryTypeID);

/*==============================================================*/
/* Table: DCCategory                                            */
/*==============================================================*/
create table DCCategory  (
   CategoryID           NUMBER                          not null,
   CategoryTypeID       NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null
);

alter table DCCategory
   add constraint PK_DCCATEGORY primary key (CategoryID);

alter table DCCategory
   add constraint FK_DCCAT_DCCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID);


/*==============================================================*/
/* Table: DCCategoryItem                                        */
/*==============================================================*/
create table DCCategoryItem  (
   CategoryID           NUMBER                          not null,
   ItemTypeID           NUMBER                          not null,
   Value                VARCHAR2(40)                    not null
);

alter table DCCategoryItem
   add constraint PK_DCCATEGORYITEM primary key (CategoryID, ItemTypeID);

alter table DCCategoryItem
   add constraint FK_DCCATITEM_DCCAT foreign key (CategoryID)
      references DCCategory (CategoryID);

alter table DCCategoryItem
   add constraint FK_DCCATITEM_DCITEMTYPE foreign key (ItemTypeID)
      references DCItemType (ItemTypeID);

/*==============================================================*/
/* Table: DCCategoryItemType                                    */
/*==============================================================*/
create table DCCategoryItemType  (
   CategoryTypeID       NUMBER                          not null,
   ItemTypeID           NUMBER                          not null
);

alter table DCCategoryItemType
   add constraint PK_DCCATEGORYITEMTYPE primary key (CategoryTypeID, ItemTypeID);

/*==============================================================*/
/* Table: DCConfigurationType                                   */
/*==============================================================*/
create table DCConfigurationType  (
   ConfigTypeID         NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null
);

alter table DCConfigurationType
   add constraint PK_DCCONFIGURATIONTYPE primary key (ConfigTypeID);

/*==============================================================*/
/* Table: DCConfigurationCategoryType                           */
/*==============================================================*/
create table DCConfigurationCategoryType  (
   ConfigTypeID         NUMBER                          not null,
   CategoryTypeID       NUMBER                          not null
);

alter table DCConfigurationCategoryType
   add constraint PK_DCCONFIGURATIONCATEGORYTYPE primary key (ConfigTypeID, CategoryTypeID);


/*==============================================================*/
/* Table: DCConfiguration                                       */
/*==============================================================*/
create table DCConfiguration  (
   ConfigID             NUMBER                          not null,
   ConfigTypeID         NUMBER                          not null,
   Name                 VARCHAR2(40)                    not null
);

alter table DCConfiguration
   add constraint PK_DCCONFIGURATION primary key (ConfigID);

alter table DCConfiguration
   add constraint FK_DCCONFIG_DCCONFIGTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID);

/*==============================================================*/
/* Table: DCConfigurationCategory                               */
/*==============================================================*/
create table DCConfigurationCategory  (
   ConfigID             NUMBER                          not null,
   CategoryID           NUMBER                          not null
);

alter table DCConfigurationCategory
   add constraint PK_DCCONFIGURATIONCATEGORY primary key (ConfigID, CategoryID);

alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCONFIG foreign key (ConfigID)
      references DCConfiguration (ConfigID);

alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCAT foreign key (CategoryID)
      references DCCategory (CategoryID);

/*==============================================================*/
/* Table: DCDeviceConfiguration                                 */
/*==============================================================*/
create table DCDeviceConfiguration  (
   DeviceID             NUMBER                          not null,
   ConfigID             NUMBER                          not null
);

alter table DCDeviceConfiguration
   add constraint PK_DCDEVICECONFIGURATION primary key (DeviceID, ConfigID);

alter table DCDeviceConfiguration
   add constraint FK_DCDEVCONFIG_DCCONFIG foreign key (ConfigID)
      references DCConfiguration (ConfigID);

alter table DCDeviceConfiguration
   add constraint FK_DCDEVCONFIG_YKPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);


/* @error ignore */
insert into billingfileformats values(21, 'SIMPLE_TOU');

/* @error ignore */
insert into billingfileformats values(22, 'EXTENDED_TOU');

/* @error ignore */
update billingfileformats set formatid = -20 where formattype = 'IVUE_BI_T65';

update YUKONROLEPROPERTY set defaultvalue = 'Server/web/webapps/yukon/WebConfig/custom/notif_templates/', description = 'Either a URL base where the notification templates will be stored (file: or http:) or a directory relative to YUKON_BASE.' where rolepropertyid=-80100;

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */
