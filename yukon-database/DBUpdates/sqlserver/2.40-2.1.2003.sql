/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

sp_rename 'CICustomerBase.DeviceID', 'CustomerID', 'COLUMN';
go
sp_rename 'CICustomerBase.AddressID', 'MainAddressID', 'COLUMN';
go
sp_rename 'CICustomerBase.CustFPL', 'CustomerDemandLevel', 'COLUMN';
go
sp_rename 'LMProgramCurtailCustomerList.DeviceID', 'ProgramID', 'COLUMN';
go
sp_rename 'LMProgramCurtailCustomerList.LMCustomerDeviceID', 'CustomerID', 'COLUMN';
go
sp_rename 'LMEnergyExchangeCustomerList.DeviceID', 'ProgramID', 'COLUMN';
go
sp_rename 'LMEnergyExchangeCustomerList.LMCustomerDeviceID', 'CustomerID', 'COLUMN';
go
sp_rename 'CICustContact.DeviceID', 'CustomerID', 'COLUMN';
go

/******************* START YUKONLISTENTRY CHANGES *******************/
create table YukonSelectionList (
ListID               numeric              not null,
Ordering             varchar(1)           not null,
SelectionLabel       varchar(30)          not null,
WhereIsList          varchar(100)         not null,
ListName             varchar(40)          not null,
UserUpdateAvailable  varchar(1)           not null,
constraint PK_YUKONSELECTIONLIST primary key  (ListID)
);
go

create table YukonListEntry (
EntryID              numeric              not null,
ListID               numeric              not null,
EntryOrder           numeric              not null,
EntryText            varchar(50)          not null,
YukonDefinitionID    numeric              not null,
constraint PK_YUKONLISTENTRY primary key  (EntryID)
);
go
create index Indx_YkLstDefID on YukonListEntry (YukonDefinitionID);
go
alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID);
go


create table ContactNotification (
ContactNotifID       numeric              not null,
ContactID            numeric              not null,
NotificationCategoryID numeric              not null,
DisableFlag          char(1)              not null,
Notification         varchar(130)         not null,
constraint PK_CONTACTNOTIFICATION primary key  (ContactNotifID)
);
go

create table Customer (
CustomerID           numeric              not null,
PrimaryContactID     numeric              not null,
CustomerTypeID       numeric              not null,
TimeZone             varchar(40)           not null,
constraint PK_CUSTOMER primary key  (CustomerID)
);
go


/* @error ignore */
alter table CICustomerBase drop constraint FK_YukPA_CICust;
go
alter table CICustomerBase add CompanyName Varchar(80);
go
update CICustomerBase set CompanyName = '(none)';
go
alter table CICustomerBase alter column CompanyName VARCHAR(80) not null;
go


insert into Customer
select c.customerid, c.primecontactid, 2, c.custtimezone
from CICustomerBase c;
go

update CICustomerBase set CompanyName =
(select PAOName
from YukonPAObject
where CustomerID = PAObjectID)
where CustomerID in
(select PAObjectID from YukonPAObject
where CustomerID = PAObjectID);
go

alter table CICustomerBase drop column MainPhoneNumber;
go
alter table CICustomerBase drop column MainFaxNumber;
go
alter table CICustomerBase drop column PrimeContactID;
go
alter table CICustomerBase drop column CustTimeZone;
go

alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID);
go


/* @error ignore */
alter table GraphCustomerList drop constraint FK_GRA_REFG_CIC;
go
alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID);
go

sp_rename 'CustomerAddress', 'Address';
go
alter table Address add County varchar(30);
go
update Address set County = '(none)';
go
alter table Address alter column County VARCHAR(30) not null;
go


sp_rename 'CICustContact', 'CustomerAdditionalContact';
go
/* @error ignore */
alter table CustomerAdditionalContact drop constraint FK_CICSTBASE_CICSTCONT;
go
alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID);
go


/* @error ignore */
alter table PointAlarming drop constraint FK_POI_POIN_NOT;
go
/* @error ignore */
alter table NotificationDestination drop constraint FK_DESTID_RECID;
go

alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID);
go

alter TABLE YukonRole add RoleDescription VARCHAR(200);
go
update YukonRole set RoleDescription = '(none)';
go
alter TABLE YukonRole alter column RoleDescription VARCHAR(200) not null;
go

alter TABLE EnergyCompany add WebConfigID numeric;
go
update EnergyCompany set WebConfigID = 0;
go
alter TABLE EnergyCompany alter column WebConfigID numeric not null;
go


create table YukonWebConfiguration (
ConfigurationID      numeric              not null,
LogoLocation         varchar(100)         null,
Description          varchar(500)         null,
AlternateDisplayName varchar(50)          null,
URL                  varchar(100)         null,
constraint PK_YUKONWEBCONFIGURATION primary key  (ConfigurationID)
);
go

/****** IMPORTANT *****  -  the billingFileFormats update is to disable MV_90 DATA Import */
/*  DO NOT RUN THE BILLINGFILEFORMATS UPDATE IF THE CUSTOMER HAS PAID FOR THE MV_90 FORMAT */
update billingFileFormats set formatid = -11 where formatid = 11;
go

/******************* NEW BILLING FORMATS *******************/
insert into billingfileformats values(13, 'NISC-Turtle');
go
insert into billingfileformats values(14, 'NISC-NCDC');
go


/******************* START YUKONLISTENTRY CHANGES *******************/
insert into YukonSelectionList values( 0, 'N', '(none)', '(none)', '(none)', 'N' );
insert into YukonSelectionList values( 1, 'A', 'Contact', 'DBEditor contact type list', 'ContactType', 'N' );
go

insert into YukonListEntry values( 0, 1, 0, '(none)', 0 );
go
insert into YukonListEntry values( 1, 1, 0, 'Email', 1 );
go
insert into YukonListEntry values( 2, 1, 0, 'Phone Number', 2 );
go
insert into YukonListEntry values( 3, 1, 0, 'Pager Number', 2 );
go
insert into YukonListEntry values( 4, 1, 0, 'Fax Number', 2 );
go
insert into YukonListEntry values( 5, 1, 0, 'Home Phone', 2 );
go
insert into YukonListEntry values( 6, 1, 0, 'Work Phone', 2 );
go



/******************* START CONTACTNOTIFICATION CHANGES *******************/
insert into ContactNotification
select r.recipientid, r.recipientid, 1, r.disableflag, r.emailaddress
from NotificationRecipient r where r.recipientid > 0;
go
insert into ContactNotification
select r.contactid+1000, r.contactid, 2, 'N', r.contphone1
from CustomerContact r where r.contactid > 0;
go

alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID);
go


/******************* START CUSTOMERCONTACT CHANGES *******************/
sp_rename 'CustomerContact', 'Contact';
go
alter table Contact drop column ContPhone1;
go
alter table Contact drop column ContPhone2;
go
alter table Contact add AddressID numeric;
go
update Contact set AddressID = 0;
go
alter table Contact alter column AddressID numeric not null;
go


/* @error ignore */
alter table Contact drop constraint FK_CSTCONT_GRPRECIP;
go
alter table Contact drop column LocationID;
go

/* @error ignore */
insert into address values ( 0, '(none)', '(none)', '(none)', 'MN', '(none)', '(none)' );

/* @error ignore */
insert into contact values ( 0, '(none)', '(none)', -1, 0 );

insert into ContactNotification values( 0, 0, 0, 'N', '(none)' );

alter table Contact
   add constraint FK_CON_REF__ADD foreign key (AddressID)
      references Address (AddressID);
go

alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID);
go

alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID);
go


/* Create a dummy contact for notifications that do not have a contact */
insert into address 
select max(addressid)+1, '(none)', '(none)', '(none)', 'MN', '(none)', '(none)' from address;
go
insert into contact 
select max(contactid)+1, 'DUMMY', 'DUMMY', -1, max(a.addressid) from contact,address a;
go

/* Assign the dangling notifications to the dummy contact */
update cn 
set cn.contactid = (select max(c.contactid) from contact c)
from contactnotification cn
where cn.contactid not in (select c.contactid from contact c);
go


drop table NotificationRecipient;
go


/******************* START FINAL MISC CHANGES *******************/
insert into DeviceMeterGroup 
select paobjectid, 'Default', 'Default', 'Default', 'Default' from YukonPAObject
where type like '%ION%';
go

insert into YukonWebConfiguration values(0,'(none)','(none)','(none)','(none)');

update yukonlistentry set listid=0 where entryid=0;


delete from yukongrouprole where roleid=-101 or roleid=-102 or roleid=-104;
delete from yukonuserrole where roleid=-101 or roleid=-102 or roleid=-104;
delete from yukonrole where roleid=-101 or roleid=-102 or roleid=-104;
go


insert into YukonRole values(-101,'WEB_OPERATOR','WebClient','(none)','(none)');
insert into YukonRole values(-102,'WEB_RESIDENTIAL_CUSTOMER','WebClient','(none)','(none)');
insert into YukonRole values(-103,'WEB_CICUSTOMER','WebClient','(none)','(none)');

insert into YukonRole values(-120,'OPERATOR_CONSUMER_INFO','WebClient','(none)','(none)');
insert into YukonRole values(-121,'OPERATOR_COMMERCIAL_METERING','WebClient','(none)','(none)');
insert into YukonRole values(-122,'OPERATOR_LOADCONTROL','WebClient','(none)','(none)');
insert into YukonRole values(-123,'OPERATOR_HARDWARE_INVENTORY','WebClient','(none)','(none)');
insert into YukonRole values(-124,'OPERATOR_WORK_ORDERS','WebClient','(none)','(none)');
insert into YukonRole values(-125,'OPERATOR_ADMINISTRATION','WebClient','(none)','(none)');

insert into YukonRole values(-130,'OPERATOR_DIRECT_CONTROL','WebClient','(none)','(none)');
insert into YukonRole values(-131,'OPERATOR_CURTAILMENT','WebClient','(none)','(none)');
insert into YukonRole values(-132,'OPERATOR_ENERGY_EXCHANGE','WebClient','(none)','(none)');

insert into YukonRole values(-140,'CICUSTOMER_DIRECT_CONTROL','WebClient','(none)','(none)');
insert into YukonRole values(-141,'CICUSTOMER_CURTAILMENT','WebClient','(none)','(none)');
insert into YukonRole values(-142,'CICUSTOMER_ENERGY_EXCHANGE','WebClient','(none)','(none)');

insert into YukonRole values(-150,'LOADCONTROL_CONTROL_ODDS','LoadControl','(none)','(none)');
insert into YukonRole values(-151,'CONSUMERINFO_NOT_IMPLEMENTED','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-160,'CONSUMERINFO_ACCOUNT','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-161,'CONSUMERINFO_ACCOUNT_GENERAL','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-162,'CONSUMERINFO_ACCOUNT_CALL_TRACKING','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-163,'CONSUMERINFO_METERING','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-164,'CONSUMERINFO_METERING_INTERVAL_DATA','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-165,'CONSUMERINFO_METERING_USAGE','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-166,'CONSUMERINFO_PROGRAMS','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-167,'CONSUMERINFO_PROGRAMS_CONTROL_HISTORY','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-168,'CONSUMERINFO_PROGRAMS_ENROLLMENT','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-169,'CONSUMERINFO_PROGRAMS_OPTOUT','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-170,'CONSUMERINFO_APPLIANCES','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-171,'CONSUMERINFO_APPLIANCES_CREATE','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-172,'CONSUMERINFO_HARDWARES','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-173,'CONSUMERINFO_HARDWARES_CREATE','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-174,'CONSUMERINFO_WORKORDERS','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-175,'CONSUMERINFO_ADMIN','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-176,'CONSUMERINFO_ADMIN_CHANGE_LOGIN','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-177,'CONSUMERINFO_THERMOSTAT','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-178,'CONSUMERINFO_QUESTIONS','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-179,'CONSUMERINFO_ACCOUNT_RESIDENCE','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-180,'CONSUMERINFO_QUESTIONS_UTIL','ConsumerInfo','(none)','(none)');
insert into YukonRole values(-181,'CONSUMERINFO_QUESTIONS_FAQ','ConsumerInfo','(none)','(none)');
go


insert into YukonRole values(-1000,'WEB_OPERATOR_SUPER','WebClient','(none)','(none)');
insert into YukonRole values(-1001,'NEW_ACCOUNT_WIZARD','WebClient','(none)','(none)');
insert into YukonRole values(-1002,'SWITCH_COMMAND_BATCH','WebClient','(none)','(none)');
insert into YukonRole values(-1003,'NOTIFICATION_ON_GENERAL_PAGE','WebClient','(none)','(none)');
insert into YukonRole values(-1004,'HIDE_OPT_OUT_BOX','WebClient','(none)','(none)');
insert into YukonRole values(-1005,'CUSTOMIZED_FAQ_LINK','WebClient','(none)','(none)');
insert into YukonRole values(-1006,'CUSTOMIZED_EMAIL_LINK','WebClient','(none)','(none)');
go

insert into yukonrole values(-9000,'TRENDING_DISCLAIMER_TEXT','WebClient',' ','(none)');
insert into yukonrole values(-9001,'ENERGYEXCHANGE_TEXT','WebClient','Energy Exchange','(none)');
insert into yukonrole values(-9002,'ENERGYEXCHANGE_HEADING_TEXT','WebClient','Energy Exchange','(none)');
insert into yukonrole values(-9003,'ENERGYEXCHANGE_PHONE_TEXT','WebClient',' ','(none)');
insert into yukonrole values(-9010,'CURTAILMENT_TEXT','WebClient','Notification','(none)');
insert into yukonrole values(-9011,'CURTAILMENT_PROVIDER_TEXT','WebClient','Curtailment Provider','(none)');
go

insert into yukongroup values(-210,'Web Demo Operators');
insert into yukongroup values(-211,'Web Demo Residential Customers');
insert into yukongroup values(-212,'Web Demo CICustomers');
go
insert into yukongrouprole values(-210,-100,'/operator/Operations.jsp');
insert into yukongrouprole values(-210,-101,'(none)');
insert into yukongrouprole values(-210,-120,'(none)');
insert into yukongrouprole values(-210,-121,'(none)');
insert into yukongrouprole values(-210,-122,'(none)');
insert into yukongrouprole values(-210,-123,'(none)');
insert into yukongrouprole values(-210,-124,'(none)');
insert into yukongrouprole values(-210,-125,'(none)');
insert into yukongrouprole values(-210,-130,'(none)');
insert into yukongrouprole values(-210,-131,'(none)');
insert into yukongrouprole values(-210,-132,'(none)');
insert into yukongrouprole values(-210,-150,'(none)');
insert into yukongrouprole values(-210,-151,'(none)');
insert into yukongrouprole values(-210,-160,'(none)');
insert into yukongrouprole values(-210,-161,'(none)');
insert into yukongrouprole values(-210,-162,'(none)');
insert into yukongrouprole values(-210,-163,'(none)');
insert into yukongrouprole values(-210,-164,'(none)');
insert into yukongrouprole values(-210,-165,'(none)');
insert into yukongrouprole values(-210,-166,'(none)');
insert into yukongrouprole values(-210,-167,'(none)');
insert into yukongrouprole values(-210,-168,'(none)');
insert into yukongrouprole values(-210,-169,'(none)');
insert into yukongrouprole values(-210,-170,'(none)');
insert into yukongrouprole values(-210,-171,'(none)');
insert into yukongrouprole values(-210,-172,'(none)');
insert into yukongrouprole values(-210,-173,'(none)');
insert into yukongrouprole values(-210,-174,'(none)');
insert into yukongrouprole values(-210,-175,'(none)');
insert into yukongrouprole values(-210,-176,'(none)');
insert into yukongrouprole values(-210,-177,'(none)');
insert into yukongrouprole values(-210,-179,'(none)');
insert into yukongrouprole values(-210,-181,'(none)');
insert into yukongrouprole values(-210,-9000,' ');
insert into yukongrouprole values(-210,-9001,'Energy Exchange');
insert into yukongrouprole values(-210,-9002,'Energy Exchange');
insert into yukongrouprole values(-210,-9003,' ');
insert into yukongrouprole values(-210,-9010,'Notification');
insert into yukongrouprole values(-210,-9011,'Curtailment Provider');


insert into yukongrouprole values(-211,-100,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values(-211,-102,'(none)');
insert into yukongrouprole values(-211,-160,'(none)');
insert into yukongrouprole values(-211,-161,'(none)');
insert into yukongrouprole values(-211,-163,'(none)');
insert into yukongrouprole values(-211,-165,'(none)');
insert into yukongrouprole values(-211,-166,'(none)');
insert into yukongrouprole values(-211,-167,'(none)');
insert into yukongrouprole values(-211,-168,'(none)');
insert into yukongrouprole values(-211,-169,'(none)');
insert into yukongrouprole values(-211,-175,'(none)');
insert into yukongrouprole values(-211,-176,'(none)');
insert into yukongrouprole values(-211,-177,'(none)');
insert into yukongrouprole values(-211,-178,'(none)');
insert into yukongrouprole values(-211,-180,'(none)');
insert into yukongrouprole values(-211,-181,'(none)');
insert into yukongrouprole values(-212,-100,'/user/CILC/user_trending.jsp');
insert into yukongrouprole values(-212,-103,'(none)');
insert into yukongrouprole values(-212,-140,'(none)');
insert into yukongrouprole values(-212,-141,'(none)');
insert into yukongrouprole values(-212,-142,'(none)');
go

/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.40', 'Ryan', '20-FEB-2003', 'Merged STARS customer structure with Yukon');
go