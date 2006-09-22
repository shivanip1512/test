/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

/* @error ignore */
alter table DynamicCCFeeder rename column LastCurrentVarUpdate to LastCurrentVarUpdateTime;

insert into YukonListEntry values( 11, 1, 0, 'IVR Login', 3 );

alter table PointUnit add DecimalDigits NUMBER;
update PointUnit set DecimalDigits = 0;
alter table PointUnit modify DecimalDigits NUMBER not null;

/* @error ignore */
alter table DynamicPointAlarming drop constraint FKf_DynPtAl_SysL;

alter table DeviceMeterGroup modify CollectionGroup VARCHAR2(50);
alter table DeviceMeterGroup modify TestCollectionGroup VARCHAR2(50);
alter table DeviceMeterGroup modify BillingGroup VARCHAR2(50);

delete MSPInterface where vendorid = 1 and interface = 'CB_MR';
delete MSPInterface where vendorid = 1 and interface = 'EA_MR';
delete MSPInterface where vendorid = 1 and interface = 'OA_OD';
delete MSPInterface where vendorid = 1 and interface = 'CB_CD';

alter table dynamicccsubstationbus drop column multivoltcontrolstate;
alter table dynamicccfeeder drop column multivoltcontrolstate;
alter table dynamicccmonitorpointresponse modify delta float;

alter table dynamicccsubstationbus add currentwattpointquality number default 0 not null;
alter table dynamicccsubstationbus add currentvoltpointquality number default 0 not null;

alter table dynamicccfeeder add currentwattpointquality number default 0 not null;
alter table dynamicccfeeder add currentvoltpointquality number default 0 not null;

/*@error ignore-begin */
drop table DCCategoryItem;
drop table DCCategoryItemType;
drop table DCItemType;
drop table DCConfigurationCategoryType;
drop table DCConfigurationCategory;
drop table DCCategory;
drop table DCCategoryType;
drop table DCDeviceConfiguration;
drop table DCConfiguration;
drop table DCDeviceConfigurationType;
drop table DCConfigurationType;
/*@error ignore-end */

/*==============================================================*/
/* Table: DCItemType                                            */
/*==============================================================*/
create table DCItemType (
   ItemTypeID           number              not null,
   Name                 varchar2(40)          not null,
   ValidationType       varchar2(40)          null,
   Required             char(1)              not null,
   MinLength            number              not null,
   MaxLengh             number              not null,
   Description          varchar2(320)         not null
);

alter table DCItemType
   add constraint PK_DCITEMTYPE primary key  (ItemTypeID);

/*==============================================================*/
/* Table: DCCategoryType                                        */
/*==============================================================*/
create table DCCategoryType (
   CategoryTypeID       number              not null,
   Name                 varchar2(40)          not null,
   CategoryGroup        varchar2(40)          not null,
   CategoryTypeLevel    varchar2(40)          not null,
   Description          varchar2(320)         not null
);

alter table DCCategoryType
   add constraint PK_DCCATEGORYTYPE primary key  (CategoryTypeID);

/*==============================================================*/
/* Table: DCCategoryItemType                                    */
/*==============================================================*/
create table DCCategoryItemType (
   CategoryTypeID       number              not null,
   ItemTypeID           number              not null
);

alter table DCCategoryItemType
   add constraint PK_DCCATEGORYITEMTYPE primary key  (CategoryTypeID, ItemTypeID);

alter table DCCategoryItemType
   add constraint FK_DCCATITEMTYPE_DCCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID);

alter table DCCategoryItemType
   add constraint FK_DCITEMTY_DCCATITEMTY foreign key (ItemTypeID)
      references DCItemType (ItemTypeID);
/*==============================================================*/
/* Table: DCCategory                                            */
/*==============================================================*/
create table DCCategory (
   CategoryID           number              not null,
   CategoryTypeID       number              not null,
   Name                 varchar2(40)          not null
);

alter table DCCategory
   add constraint PK_DCCATEGORY primary key  (CategoryID);

alter table DCCategory
   add constraint FK_DCCAT_DCCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID);

/*==============================================================*/
/* Table: DCCategoryItem                                        */
/*==============================================================*/
create table DCCategoryItem (
   CategoryID           number              not null,
   ItemTypeID           number              not null,
   Value                varchar2(40)          not null
);

alter table DCCategoryItem
   add constraint PK_DCCATEGORYITEM primary key  (CategoryID, ItemTypeID);

alter table DCCategoryItem
   add constraint FK_DCCATITEM_DCCAT foreign key (CategoryID)
      references DCCategory (CategoryID);

alter table DCCategoryItem
   add constraint FK_DCCATITEM_DCITEMTYPE foreign key (ItemTypeID)
      references DCItemType (ItemTypeID);

/*==============================================================*/
/* Table: DCConfigurationType                                   */
/*==============================================================*/
create table DCConfigurationType (
   ConfigTypeID         number              not null,
   Name                 varchar2(40)          not null,
   DESCRIPTION          varchar2(320)         not null
);

alter table DCConfigurationType
   add constraint PK_DCCONFIGURATIONTYPE primary key  (ConfigTypeID);

/*==============================================================*/
/* Table: DCConfigurationCategoryType                           */
/*==============================================================*/
create table DCConfigurationCategoryType (
   ConfigTypeID         number              not null,
   CategoryTypeID       number              not null
);

alter table DCConfigurationCategoryType
   add constraint PK_DCCONFIGURATIONCATEGORYTYPE primary key  (ConfigTypeID, CategoryTypeID);

alter table DCConfigurationCategoryType
   add constraint FK_DCCFGTYPE_DCCFGCATTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID);

alter table DCConfigurationCategoryType
   add constraint FK_DCCATTYPE_DCCFGCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID);

/*==============================================================*/
/* Table: DCConfiguration                                       */
/*==============================================================*/
create table DCConfiguration (
   ConfigID             number              not null,
   ConfigTypeID         number              not null,
   Name                 varchar2(40)          not null
);

alter table DCConfiguration
   add constraint PK_DCCONFIGURATION primary key  (ConfigID);

alter table DCConfiguration
   add constraint FK_DCCONFIG_DCCONFIGTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID);

/*==============================================================*/
/* Table: DCConfigurationCategory                               */
/*==============================================================*/
create table DCConfigurationCategory (
   ConfigID             number              not null,
   CategoryID           number              not null
);

alter table DCConfigurationCategory
   add constraint PK_DCCONFIGURATIONCATEGORY primary key  (ConfigID, CategoryID);

alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCONFIG foreign key (ConfigID)
      references DCConfiguration (ConfigID);

alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCAT foreign key (CategoryID)
      references DCCategory (CategoryID);

/*==============================================================*/
/* Table: DCDeviceConfiguration                                 */
/*==============================================================*/
create table DCDeviceConfiguration (
   DeviceID             number              not null,
   ConfigID             number              not null
);

alter table DCDeviceConfiguration
   add constraint PK_DCDEVICECONFIGURATION primary key  (DeviceID, ConfigID);

alter table DCDeviceConfiguration
   add constraint FK_DCDEVCONFIG_DCCONFIG foreign key (ConfigID)
      references DCConfiguration (ConfigID);

alter table DCDeviceConfiguration
   add constraint FK_DCDEVCONFIG_YKPAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);

/*==============================================================*/
/* Table: DCDeviceConfigurationType                             */
/*==============================================================*/
create table DCDeviceConfigurationType (
   ConfigTypeID         number              not null,
   DeviceType           varchar2(30)          not null
);

alter table DCDeviceConfigurationType
   add constraint PK_DCDEVICECONFIGURATIONTYPE primary key  (ConfigTypeID, DeviceType);

alter table DCDeviceConfigurationType
   add constraint FK_DCCFGTYPE_DCCFGDVCFGTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID);

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.2', 'Ryan', '12-Sep-2006', 'Latest Update', 9 );
