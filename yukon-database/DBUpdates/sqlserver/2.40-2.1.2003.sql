/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.40', 'Ryan', '2-FEB-2003', 'Merged STARS customer structure with Yukon');



/* ***** IMPORTANT *****  -  the billingFileFormats update is to disable MV_90 DATA Import */
/*  DO NOT RUN THE BILLINGFILEFORMATS UPDATE IF THE CUSTOMER HAS PAID FOR THE MV_90 FORMAT */
update billingFileFormats set formatid = -11 where formatid = 11
go

/******************* NEW BILLING FORMATS *******************/
insert into billingfileformats values(13, 'NISC-Turtle')
go
insert into billingfileformats values(14, 'NISC-NCDC')
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
)
go
insert into YukonSelectionList values( 0, 'N', '(none)', '(none)', '(none)', 'N' )
insert into YukonSelectionList values( 1, 'A', 'Contact', 'DBEditor contact type list', 'ContactType', 'N' )
go
create table YukonListEntry (
EntryID              numeric              not null,
ListID               numeric              not null,
EntryOrder           numeric              not null,
EntryText            varchar(50)          not null,
YukonDefinitionID    numeric              not null,
constraint PK_YUKONLISTENTRY primary key  (EntryID)
)
go
insert into YukonListEntry values( 0, 1, 0, '(none)', 0 )
go
insert into YukonListEntry values( 1, 1, 0, 'Email', 1 )
go
insert into YukonListEntry values( 2, 1, 0, 'Phone Number', 2 )
go
insert into YukonListEntry values( 3, 1, 0, 'Pager Number', 2 )
go
insert into YukonListEntry values( 4, 1, 0, 'Fax Number', 2 )
go
create index Indx_YkLstDefID on YukonListEntry (YukonDefinitionID)
go
alter table YukonListEntry
   add constraint FK_LstEnty_SelLst foreign key (ListID)
      references YukonSelectionList (ListID)
go



/******************* START CONTACTNOTIFICATION CHANGES *******************/
create table ContactNotification (
ContactNotifID       numeric              not null,
ContactID            numeric              not null,
NotificationCategoryID numeric              not null,
DisableFlag          char(1)              not null,
Notification         varchar(130)         not null,
constraint PK_CONTACTNOTIFICATION primary key  (ContactNotifID)
)
go
insert into ContactNotification values( 0, 0, 'N', '(none)', 0 )
go
/*****DO NOT ADD ANY REFERENCES TO THIS TABLE UNTIL THE END*****/
insert into ContactNotification
select r.recipientid, r.recipientid, 1, r.disableflag, r.emailaddress
from NotificationRecipient r where r.recipientid > 0
go
insert into ContactNotification
select r.contactid, r.contactid, 2, 'N', r.contphone1
from CustomerContact r where r.contactid > 0
go


/******************* START CUSTOMER CHANGES *******************/
create table Customer (
CustomerID           numeric              not null,
PrimaryContactID     numeric              not null,
CustomerTypeID       numeric              not null,
TimeZone             varchar(6)           not null,
constraint PK_CUSTOMER primary key  (CustomerID)
)
go
insert into Customer
select c.deviceid, c.primecontactid, 2, c.custtimezone
from CICustomerBase c
go
alter table CICustomerBase drop constraint Ref_YukPA_CICust
go
sp_rename 'CICustomerBase.DeviceID', 'CustomerID', 'COLUMN'
go
sp_rename 'CICustomerBase.AddressID', 'MainAddressID', 'COLUMN'
go
sp_rename 'CICustomerBase.CustFPL', 'CustomerDemandLevel', 'COLUMN'
go
alter table CICustomerBase drop column MainPhoneNumber
go
alter table CICustomerBase drop column MainFaxNumber
go
alter table CICustomerBase drop column PrimeContactID
go
alter table CICustomerBase drop column CustTimeZone
go
alter table CICustomerBase add CompanyName Varchar(80) not null DEFAULT '(none)'
go
update CICustomerBase
set CompanyName = y.PAOName
from CICustomerBase c, YukonPAObject y
where c.CustomerID = y.PAObjectID
go
alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID)
go
alter table Customer alter column TimeZone VARCHAR(40)
go
sp_rename 'LMProgramCurtailCustomerList.DeviceID', 'ProgramID', 'COLUMN'
go
sp_rename 'LMProgramCurtailCustomerList.LMCustomerDeviceID', 'CustomerID', 'COLUMN'
go
sp_rename 'LMEnergyExchangeCustomerList.DeviceID', 'ProgramID', 'COLUMN'
go
sp_rename 'LMEnergyExchangeCustomerList.LMCustomerDeviceID', 'CustomerID', 'COLUMN'
go

/******************* START GRAPHCUSTOMERLIST CHANGES *******************/
alter table GraphCustomerList drop constraint RefGrphCstLst_CICstBse
go
alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID)
go



/******************* START CUSTOMERADDRESS CHANGES *******************/
sp_rename 'CustomerAddress', 'Address'
go
alter table Address add County varchar(30) not null DEFAULT '(none)'
go



/******************* START CUSTOMERCONTACT CHANGES *******************/
sp_rename 'CustomerContact', 'Contact'
go
alter table Contact drop column ContPhone1
go
alter table Contact drop column ContPhone2
go
alter table Contact add AddressID numeric not null DEFAULT 0
go
alter table Contact drop constraint RefCustContract_GroupRecipient
go
alter table Contact drop column LocationID
go
alter table Contact
   add constraint FK_CON_REF__ADD foreign key (AddressID)
      references Address (AddressID)
go



/******************* START CICUSTCONTACT CHANGES *******************/
sp_rename 'CICustContact', 'CustomerAdditionalContact'
go
sp_rename 'CustomerAdditionalContact.DeviceID', 'CustomerID', 'COLUMN'
go
alter table CustomerAdditionalContact drop constraint RefCICustBase_CICustContract
go
alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID)
go



/******************* START NOTIFICATIONRECIPIENT CHANGES *******************/
alter table PointAlarming drop constraint POINTALARMIN_GROUPRECIPIENT
go
alter table NotificationDestination drop constraint DESTINATION_RECIPIENT
go
drop table NotificationRecipient
go
alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
go
alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID)
go


/******************* START FINAL MISC CHANGES *******************/
alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID)
go
alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID)
go
alter TABLE YukonRole add RoleDescription VARCHAR(200) not null DEFAULT '(none)'
go
insert into YukonRole values(-104,'WEB_OPERATOR','WebClient','(none)', '(none)')
go