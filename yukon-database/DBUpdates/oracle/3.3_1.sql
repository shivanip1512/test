/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/
/* @error ignore-begin */
alter table DeviceMeterGroup modify CollectionGroup varchar(50) not null;
alter table DeviceMeterGroup modify TestCollectionGroup varchar(50) not null;
alter table DeviceMeterGroup modify BillingGroup varchar(50) not null;
/* @error ignore-end */

delete MSPInterface where vendorid = 1 and interface = 'CB_MR';
delete MSPInterface where vendorid = 1 and interface = 'EA_MR';
delete MSPInterface where vendorid = 1 and interface = 'OA_OD';
delete MSPInterface where vendorid = 1 and interface = 'CB_CD';

insert into AlarmCategory values(33,'Category 33',1);
insert into AlarmCategory values(34,'Category 34',1);
insert into AlarmCategory values(35,'Category 35',1);
insert into AlarmCategory values(36,'Category 36',1);
insert into AlarmCategory values(37,'Category 37',1);
insert into AlarmCategory values(38,'Category 38',1);
insert into AlarmCategory values(39,'Category 39',1);
insert into AlarmCategory values(40,'Category 40',1);
insert into AlarmCategory values(41,'Category 41',1);
insert into AlarmCategory values(42,'Category 42',1);
insert into AlarmCategory values(43,'Category 43',1);
insert into AlarmCategory values(44,'Category 44',1);
insert into AlarmCategory values(45,'Category 45',1);
insert into AlarmCategory values(46,'Category 46',1);
insert into AlarmCategory values(47,'Category 47',1);
insert into AlarmCategory values(48,'Category 48',1);
insert into AlarmCategory values(49,'Category 49',1);
insert into AlarmCategory values(50,'Category 50',1);
insert into AlarmCategory values(51,'Category 51',1);
insert into AlarmCategory values(52,'Category 52',1);
insert into AlarmCategory values(53,'Category 53',1);
insert into AlarmCategory values(54,'Category 54',1);
insert into AlarmCategory values(55,'Category 55',1);
insert into AlarmCategory values(56,'Category 56',1);
insert into AlarmCategory values(57,'Category 57',1);
insert into AlarmCategory values(58,'Category 58',1);
insert into AlarmCategory values(59,'Category 59',1);
insert into AlarmCategory values(60,'Category 60',1);
insert into AlarmCategory values(61,'Category 61',1);
insert into AlarmCategory values(62,'Category 62',1);
insert into AlarmCategory values(63,'Category 63',1);
insert into AlarmCategory values(64,'Category 64',1);
insert into AlarmCategory values(65,'Category 65',1);
insert into AlarmCategory values(66,'Category 66',1);
insert into AlarmCategory values(67,'Category 67',1);
insert into AlarmCategory values(68,'Category 68',1);
insert into AlarmCategory values(69,'Category 69',1);
insert into AlarmCategory values(70,'Category 70',1);
insert into AlarmCategory values(71,'Category 71',1);
insert into AlarmCategory values(72,'Category 72',1);
insert into AlarmCategory values(73,'Category 73',1);
insert into AlarmCategory values(74,'Category 74',1);
insert into AlarmCategory values(75,'Category 75',1);
insert into AlarmCategory values(76,'Category 76',1);
insert into AlarmCategory values(77,'Category 77',1);
insert into AlarmCategory values(78,'Category 78',1);
insert into AlarmCategory values(79,'Category 79',1);
insert into AlarmCategory values(80,'Category 80',1);
insert into AlarmCategory values(81,'Category 81',1);
insert into AlarmCategory values(82,'Category 82',1);
insert into AlarmCategory values(83,'Category 83',1);
insert into AlarmCategory values(84,'Category 84',1);
insert into AlarmCategory values(85,'Category 85',1);
insert into AlarmCategory values(86,'Category 86',1);
insert into AlarmCategory values(87,'Category 87',1);
insert into AlarmCategory values(88,'Category 88',1);
insert into AlarmCategory values(89,'Category 89',1);
insert into AlarmCategory values(90,'Category 90',1);
insert into AlarmCategory values(91,'Category 91',1);
insert into AlarmCategory values(92,'Category 92',1);
insert into AlarmCategory values(93,'Category 93',1);
insert into AlarmCategory values(94,'Category 94',1);
insert into AlarmCategory values(95,'Category 95',1);
insert into AlarmCategory values(96,'Category 96',1);
insert into AlarmCategory values(97,'Category 97',1);
insert into AlarmCategory values(98,'Category 98',1);
insert into AlarmCategory values(99,'Category 99',1);
insert into AlarmCategory values(100,'Category 100',1);

update yukonuserrole set value='true' where userroleid=-1011;
update yukonuserrole set value='(none)' where userroleid=-1012;

create table CCurtProgramNotifGroup (
   CCurtProgramID      NUMBER                          not null,
   NotificationGroupID  NUMBER                          not null
);

alter table CCurtProgramNotifGroup
   add constraint PK_CCurtProgramNotifGroup primary key (CCurtProgramID,NotificationGroupID);

alter table CCurtProgramNotifGroup
   add constraint FK_CCURTPNG_NG foreign key (NotificationGroupID)
      references NotificationGroup (NotificationGroupID);

alter table CCurtProgramNotifGroup
   add constraint FK_CCURTPNG_CCURTP foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID);

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
drop table DCItemValue;

/*==============================================================*/
/* Table: DCItemType                                            */
/*==============================================================*/
create table DCItemType (
   ItemTypeID           numeric              not null,
   Name                 varchar(40)          not null,
   DisplayName          varchar(40)          not null,
   ValidationType       varchar(40)          null,
   Required             char(1)              not null,
   MinLength            numeric              not null,
   MaxLengh             numeric              not null,
   DefaultValue         varchar(40)          null,
   Description          varchar(320)         null
);

alter table DCItemType
   add constraint PK_DCITEMTYPE primary key  (ItemTypeID);

/*==============================================================*/
/* Table: DCItemValue                                           */
/*==============================================================*/
create table DCItemValue (
   ItemTypeID           numeric              not null,
   Value                varchar(40)          not null,
   ValueOrder           numeric              not null
);

alter table DCItemValue
   add constraint PK_DCITEMVALUE primary key  (ItemTypeID, ValueOrder);

alter table DCItemValue
   add constraint FK_DCIITEMVALUE_DCITEMTYPE foreign key (ItemTypeID)
      references DCItemType (ItemTypeID);

/*==============================================================*/
/* Table: DCCategoryType                                        */
/*==============================================================*/
create table DCCategoryType (
   CategoryTypeID       numeric              not null,
   Name                 varchar(40)          not null,
   DisplayName          varchar(40)          not null,
   CategoryGroup        varchar(40)          null,
   CategoryTypeLevel    varchar(40)          not null,
   Description          varchar(320)         null
);

alter table DCCategoryType
   add constraint PK_DCCATEGORYTYPE primary key  (CategoryTypeID);

/*==============================================================*/
/* Table: DCCategoryItemType                                    */
/*==============================================================*/
create table DCCategoryItemType (
   CategoryTypeID       numeric              not null,
   ItemTypeID           numeric              not null
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
   CategoryID           numeric              not null,
   CategoryTypeID       numeric              not null,
   Name                 varchar(40)          not null
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
   CategoryID           numeric              not null,
   ItemTypeID           numeric              not null,
   Value                varchar(40)          not null
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
   ConfigTypeID         numeric              not null,
   Name                 varchar(40)          not null,
   DisplayName          varchar(40)          not null,
   Description          varchar(320)         null
);

alter table DCConfigurationType
   add constraint PK_DCCONFIGURATIONTYPE primary key  (ConfigTypeID);

/*==============================================================*/
/* Table: DCConfigurationCategoryType                           */
/*==============================================================*/
create table DCConfigurationCategoryType (
   ConfigTypeID         numeric              not null,
   CategoryTypeID       numeric              not null
);

alter table DCConfigurationCategoryType
   add constraint PK_DCCONFIGURATIONCATEGORYTYPE primary key  (ConfigTypeID, CategoryTypeID);

alter table DCConfigurationCategoryType
   add constraint FK_DCCATTYPE_DCCFGCATTYPE foreign key (CategoryTypeID)
      references DCCategoryType (CategoryTypeID);

alter table DCConfigurationCategoryType
   add constraint FK_DCCFGTYPE_DCCFGCATTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID);

/*==============================================================*/
/* Table: DCConfiguration                                       */
/*==============================================================*/
create table DCConfiguration (
   ConfigID             numeric              not null,
   ConfigTypeID         numeric              not null,
   Name                 varchar(40)          not null
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
   ConfigID             numeric              not null,
   CategoryID           numeric              not null
);

alter table DCConfigurationCategory
   add constraint PK_DCCONFIGURATIONCATEGORY primary key  (ConfigID, CategoryID);

alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCAT foreign key (CategoryID)
      references DCCategory (CategoryID);

alter table DCConfigurationCategory
   add constraint FK_DCCONFIGCAT_DCCONFIG foreign key (ConfigID)
      references DCConfiguration (ConfigID);

/*==============================================================*/
/* Table: DCDeviceConfiguration                                 */
/*==============================================================*/
create table DCDeviceConfiguration (
   DeviceID             numeric              not null,
   ConfigID             numeric              not null
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
   ConfigTypeID         numeric              not null,
   DeviceType           varchar(30)          not null
);

alter table DCDeviceConfigurationType
   add constraint PK_DCDEVICECONFIGURATIONTYPE primary key  (ConfigTypeID, DeviceType);

alter table DCDeviceConfigurationType
   add constraint FK_DCCFGTYPE_DCCFGDVCFGTYPE foreign key (ConfigTypeID)
      references DCConfigurationType (ConfigTypeID);
/*@error ignore-end */

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.3', 'Jon', '25-SEP-2006', 'DB Update Script', 1 );