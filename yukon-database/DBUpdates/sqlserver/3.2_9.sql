/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

insert into YukonListEntry values( 11, 1, 0, 'IVR Login', 3 );
go

alter table pointunit add DecimalDigits numeric;
go
update pointunit set DecimalDigits = 0;
go
alter table pointunit alter column DecimalDigits numeric not null;
go

/* @error ignore */
alter table DynamicPointAlarming drop constraint FKf_DynPtAl_SysL;
go

alter table devicemetergroup alter column CollectionGroup varchar(50) not null;
go
alter table devicemetergroup alter column TestCollectionGroup varchar(50) not null;
go
alter table devicemetergroup alter column BillingGroup varchar(50) not null;
go

delete MSPInterface where vendorid = 1 and interface = 'CB_MR';
delete MSPInterface where vendorid = 1 and interface = 'EA_MR';
delete MSPInterface where vendorid = 1 and interface = 'OA_OD';
delete MSPInterface where vendorid = 1 and interface = 'CB_CD';
go

/*@error ignore-begin */
alter table dynamicccsubstationbus drop column multivoltcontrolstate;
go
alter table dynamicccfeeder drop column multivoltcontrolstate;
go
alter table dynamicccmonitorpointresponse alter column delta float not null;
go

alter table dynamicccsubstationbus add currentwattpointquality numeric not null default 0;
go

alter table dynamicccsubstationbus add currentvoltpointquality numeric not null default 0;
go

alter table dynamicccfeeder add currentwattpointquality numeric not null default 0;
go

alter table dynamicccfeeder add currentvoltpointquality numeric not null default 0;
go

drop table DCCategoryItem;
go

drop table DCCategoryItemType;
go

drop table DCItemType;
go

drop table DCConfigurationCategoryType;
go

drop table DCConfigurationCategory;
go

drop table DCCategory;
go

drop table DCCategoryType;
go

drop table DCDeviceConfiguration;
go

drop table DCConfiguration;
go

drop table DCDeviceConfigurationType;
go

drop table DCConfigurationType;
go
/*@error ignore-end */

/*==============================================================*/
/* Table: DCItemType                                            */
/*==============================================================*/
create table DCItemType (
   ItemTypeID           numeric              not null,
   Name                 varchar(40)          not null,
   ValidationType       varchar(40)          null,
   Required             char(1)              not null,
   MinLength            numeric              not null,
   MaxLengh             numeric              not null,
   Description          varchar(320)         not null
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
   Name                 varchar(40)          not null,
   CategoryGroup        varchar(40)          not null,
   CategoryTypeLevel    varchar(40)          not null,
   Description          varchar(320)         not null
);
go


alter table DCCategoryType
   add constraint PK_DCCATEGORYTYPE primary key  (CategoryTypeID);
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


alter table DCCategoryItemType
   add constraint FK_DCCATITEMTYPE_DCCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID);
go


alter table DCCategoryItemType
   add constraint FK_DCITEMTY_DCCATITEMTY foreign key (ItemTypeID)
      references DCItemType (ItemTypeID);
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
/* Table: DCConfigurationType                                   */
/*==============================================================*/
create table DCConfigurationType (
   ConfigTypeID         numeric              not null,
   Name                 varchar(40)          not null,
   DESCRIPTION          varchar(320)         not null
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


alter table DCConfigurationCategoryType
   add constraint FK_DCCFGTYPE_DCCFGCATTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID);
go


alter table DCConfigurationCategoryType
   add constraint FK_DCCATTYPE_DCCFGCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID);
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

/*==============================================================*/
/* Table: DCDeviceConfigurationType                             */
/*==============================================================*/
create table DCDeviceConfigurationType (
   ConfigTypeID         numeric              not null,
   DeviceType           varchar(30)          not null
);
go


alter table DCDeviceConfigurationType
   add constraint PK_DCDEVICECONFIGURATIONTYPE primary key  (ConfigTypeID, DeviceType);
go


alter table DCDeviceConfigurationType
   add constraint FK_DCCFGTYPE_DCCFGDVCFGTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID);
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
insert into CTIDatabase values('3.2', 'DBUPdater', '20-SEP-2006', 'DB Update Script', 9 );
go
