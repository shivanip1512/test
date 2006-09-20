drop table DCCategoryItem
drop table DCCategoryItemType
drop table DCItemValue
drop table DCItemType
drop table DCConfigurationCategoryType
drop table DCConfigurationCategory
drop table DCCategory
drop table DCCategoryType
drop table DCDeviceConfiguration
drop table DCConfiguration
drop table DCDeviceConfigurationType
drop table DCConfigurationType


create table DCItemType(
	ItemTypeID numeric not null,
	Name varchar(40) not null,
	DisplayName varchar(40) not null,
	ValidationType varchar(40),
	Required char(1) not null default'N',
	MinValue numeric not null default 0,
	MaxValue numeric not null default 50,
	DefaultValue varchar(40),
	Description varchar(500),
	PRIMARY KEY (ItemTypeID)
)

create table DCItemValue(
	ItemTypeID numeric not null,
	Value varchar(40) not null,
	ValueOrder numeric not null
	FOREIGN KEY (ItemTypeID) REFERENCES DCItemType(ItemTypeID),
)

create table DCCategoryType(
	CategoryTypeID numeric not null,
	Name varchar(40) not null,
	DisplayName varchar(40) not null,
	CategoryGroup varchar(40) null,
	Level varchar(40) not null,
	Description varchar(500),
	PRIMARY KEY (CategoryTypeID)
)

create table DCCategoryItemType(
	CategoryTypeID numeric not null,
	ItemTypeID numeric not null,
	PRIMARY KEY (CategoryTypeID, ItemTypeID),
     	FOREIGN KEY (CategoryTypeID) REFERENCES DCCategoryType(CategoryTypeID),
	FOREIGN KEY (ItemTypeID) REFERENCES DCItemType(ItemTypeID)
	
)

create table DCCategory(
	CategoryID numeric not null,
	CategoryTypeID numeric not null,
	Name varchar(40) not null,
	PRIMARY KEY (CategoryID),
     	FOREIGN KEY (CategoryTypeID) REFERENCES DCCategoryType(CategoryTypeID)
)

create table DCCategoryItem(
	CategoryID numeric not null,
	ItemTypeID numeric not null,
	Value varchar(40) not null,
	PRIMARY KEY (CategoryID,ItemTypeID),
     	FOREIGN KEY (CategoryID) REFERENCES DCCategory(CategoryID),
	FOREIGN KEY (ItemTypeID) REFERENCES DCItemType(ItemTypeID)
)

create table DCConfigurationType(
	ConfigTypeID numeric not null,
	Name varchar(40) not null,
	DisplayName varchar(40) not null,
	Description varchar(500),
	PRIMARY KEY (ConfigTypeID)
)

create table DCConfigurationCategoryType(
	ConfigTypeID numeric not null,
	CategoryTypeID numeric not null,
	PRIMARY KEY (ConfigTypeID, CategoryTypeID),
     	FOREIGN KEY (ConfigTypeID) REFERENCES DCConfigurationType(ConfigTypeID),
	FOREIGN KEY (CategoryTypeID) REFERENCES DCCategoryType(CategoryTypeID)
)

create table DCConfiguration(
	ConfigID numeric not null,
	ConfigTypeID numeric not null,
	Name varchar(40) not null,
	PRIMARY KEY (ConfigID),
     	FOREIGN KEY (ConfigTypeID) REFERENCES DCConfigurationType(ConfigTypeID)
)

create table DCConfigurationCategory(
	ConfigID numeric not null,
	CategoryID numeric not null,
	PRIMARY KEY (ConfigID,CategoryID),
     	FOREIGN KEY (ConfigID) REFERENCES DCConfiguration(ConfigID),
     	FOREIGN KEY (CategoryID) REFERENCES DCCategory(CategoryID)
)

create table DCDeviceConfiguration(
	DeviceID numeric not null,
	ConfigID numeric not null,
	PRIMARY KEY (DeviceID),
     	FOREIGN KEY (DeviceID) REFERENCES YukonPAObject(PAObjectID),
	FOREIGN KEY (ConfigID) REFERENCES DCConfiguration(ConfigID)
)

create table DCDeviceConfigurationType(
	ConfigTypeID numeric not null,
	DeviceType varchar (30) not null,
	PRIMARY KEY (ConfigTypeID,DeviceType),
     	FOREIGN KEY (ConfigTypeID) REFERENCES DCConfigurationType(ConfigTypeID)
)