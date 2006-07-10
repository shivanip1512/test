/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* @error ignore */
drop table dynamicccfeeder;
go

create table DynamicCCFeeder (
   FeederID             numeric              not null,
   CurrentVarPointValue float                not null,
   CurrentWattPointValue float                not null,
   NewPointDataReceivedFlag char(1)              not null,
   LastCurrentVarUpdateTime datetime             not null,
   EstimatedVarPointValue float                not null,
   CurrentDailyOperations numeric              not null,
   RecentlyControlledFlag char(1)              not null,
   LastOperationTime    datetime             not null,
   VarValueBeforeControl float                not null,
   LastCapBankDeviceID  numeric              not null,
   BusOptimizedVarCategory numeric              not null,
   BusOptimizedVarOffset float                not null,
   CTITimeStamp         datetime             not null,
   PowerFactorValue     float                not null,
   KvarSolution         float                not null,
   EstimatedPFValue     float                not null,
   CurrentVarPointQuality numeric              not null,
   WaiveControlFlag     char(1)              not null,
   AdditionalFlags      varchar(32)          not null,
   CurrentVoltPointValue float                not null,
   EventSeq             numeric              not null,
   CurrVerifyCBId       numeric              not null,
   CurrVerifyCBOrigState numeric              not null
);
go


alter table DynamicCCFeeder
   add constraint PK_DYNAMICCCFEEDER primary key  (FeederID);
go


alter table DynamicCCFeeder
   add constraint FK_CCFeed_DyFeed foreign key (FeederID)
      references CapControlFeeder (FeederID);
go


alter table dynamicccsubstationbus add multiVoltControlState numeric;
go
update dynamicccsubstationbus  set multiVoltControlState = 0;
go
alter table dynamicccsubstationbus alter column multiVoltControlState numeric not null;
go

alter table dynamicccfeeder add multiVoltControlState numeric;
go
update dynamicccfeeder  set multiVoltControlState = 0;
go
alter table dynamicccfeeder alter column multiVoltControlState numeric not null;
go

drop table configurationvalue;
go
drop table configurationparts;
go
drop table configurationpartsname;
go
drop table deviceconfiguration;
go
drop table configurationname;
go

/*==============================================================*/
/* Table: DCItemType                                            */
/*==============================================================*/
create table DCItemType (
   ItemTypeID           numeric              not null,
   Name                 varchar(40)          not null
);
go


alter table DCItemType
   add constraint PK_DCITEMTYPE primary key  (ItemTypeID);
go

/*==============================================================*/
/* Table: DCCategoryType                                        */
/*==============================================================*/
create table DCCategoryType (
   CategoryTypeID       numeric              not null,
   Name                 varchar(40)          not null
);
go


alter table DCCategoryType
   add constraint PK_DCCATEGORYTYPE primary key  (CategoryTypeID);
go


/*==============================================================*/
/* Table: DCCategory                                            */
/*==============================================================*/
create table DCCategory (
   CategoryID           numeric              not null,
   CategoryTypeID       numeric              not null,
   Name                 varchar(40)          not null
);
go


alter table DCCategory
   add constraint PK_DCCATEGORY primary key  (CategoryID);
go


alter table DCCategory
   add constraint FK_DCCAT_DCCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID);
go


/*==============================================================*/
/* Table: DCCategoryItem                                        */
/*==============================================================*/
create table DCCategoryItem (
   CategoryID           numeric              not null,
   ItemTypeID           numeric              not null,
   Value                varchar(40)          not null
);
go


alter table DCCategoryItem
   add constraint PK_DCCATEGORYITEM primary key  (CategoryID, ItemTypeID);
go


alter table DCCategoryItem
   add constraint FK_DCCATITEM_DCCAT foreign key (CategoryID)
      references DCCategory (CategoryID);
go


alter table DCCategoryItem
   add constraint FK_DCCATITEM_DCITEMTYPE foreign key (ItemTypeID)
      references DCItemType (ItemTypeID);
go


/*==============================================================*/
/* Table: DCCategoryItemType                                    */
/*==============================================================*/
create table DCCategoryItemType (
   CategoryTypeID       numeric              not null,
   ItemTypeID           numeric              not null
);
go


alter table DCCategoryItemType
   add constraint PK_DCCATEGORYITEMTYPE primary key  (CategoryTypeID, ItemTypeID);
go

/*==============================================================*/
/* Table: DCConfigurationType                                   */
/*==============================================================*/
create table DCConfigurationType (
   ConfigTypeID         numeric              not null,
   Name                 varchar(40)          not null
);
go


alter table DCConfigurationType
   add constraint PK_DCCONFIGURATIONTYPE primary key  (ConfigTypeID);
go

/*==============================================================*/
/* Table: DCConfigurationCategoryType                           */
/*==============================================================*/
create table DCConfigurationCategoryType (
   ConfigTypeID         numeric              not null,
   CategoryTypeID       numeric              not null
);
go


alter table DCConfigurationCategoryType
   add constraint PK_DCCONFIGURATIONCATEGORYTYPE primary key  (ConfigTypeID, CategoryTypeID);
go

/*==============================================================*/
/* Table: DCConfiguration                                       */
/*==============================================================*/
create table DCConfiguration (
   ConfigID             numeric              not null,
   ConfigTypeID         numeric              not null,
   Name                 varchar(40)          not null
);
go


alter table DCConfiguration
   add constraint PK_DCCONFIGURATION primary key  (ConfigID);
go


alter table DCConfiguration
   add constraint FK_DCCONFIG_DCCONFIGTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID);
go

/*==============================================================*/
/* Table: DCConfigurationCategory                               */
/*==============================================================*/
create table DCConfigurationCategory (
   ConfigID             numeric              not null,
   CategoryID           numeric              not null
);
go


alter table DCConfigurationCategory
   add constraint PK_DCCONFIGURATIONCATEGORY primary key  (ConfigID, CategoryID);
go


alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCONFIG foreign key (ConfigID)
      references DCConfiguration (ConfigID);
go


alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCAT foreign key (CategoryID)
      references DCCategory (CategoryID);
go


/*==============================================================*/
/* Table: DCDeviceConfiguration                                 */
/*==============================================================*/
create table DCDeviceConfiguration (
   DeviceID             numeric              not null,
   ConfigID             numeric              not null
);
go


alter table DCDeviceConfiguration
   add constraint PK_DCDEVICECONFIGURATION primary key  (DeviceID, ConfigID);
go


alter table DCDeviceConfiguration
   add constraint FK_DCDEVCONFIG_DCCONFIG foreign key (ConfigID)
      references DCConfiguration (ConfigID);
go


alter table DCDeviceConfiguration
   add constraint FK_DCDEVCONFIG_YKPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);
go

/* @error ignore */
insert into billingfileformats values(21, 'SIMPLE_TOU');
go

/* @error ignore */
insert into billingfileformats values(22, 'EXTENDED_TOU');
go

/* @error ignore */
update billingfileformats set formatid = -20 where formattype = 'IVUE_BI_T65';
go

update YUKONROLEPROPERTY set defaultvalue = 'Server/web/webapps/yukon/WebConfig/custom/notif_templates/', description = 'Either a URL base where the notification templates will be stored (file: or http:) or a directory relative to YUKON_BASE.' where rolepropertyid=-80100;
go

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
