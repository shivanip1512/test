/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.40', 'Ryan', '20-FEB-2003', 'Merged STARS customer structure with Yukon');


/******************************************************************************
*  THIS SCRIPTS NEEDS THE rencol() PROCEDURE INSTALLED BEFORE IT WILL WORK
*  You must install the rencol() procedure AND run it from 
*  the sys/CHANGE_ON_INSTALL account
*
*  NOTE!!! for Oracle 9.x you should log in as 
*  sys/CHANGE_ON_INSTALL as sysdba (ACL 7/29)
*
******************************************************************************/


/* ***** IMPORTANT *****  -  the billingFileFormats update is to disable MV_90 DATA Import */
/*  DO NOT RUN THE BILLINGFILEFORMATS UPDATE IF THE CUSTOMER HAS PAID FOR THE MV_90 FORMAT */
update billingFileFormats set formatid = -11 where formatid = 11;

/******************* NEW BILLING FORMATS *******************/
insert into billingfileformats values(13, 'NISC-Turtle');
insert into billingfileformats values(14, 'NISC-NCDC');


/******************* START YUKONLISTENTRY CHANGES *******************/
create table YukonSelectionList  (
   ListID               NUMBER                           not null,
   Ordering             VARCHAR2(1)                      not null,
   SelectionLabel       VARCHAR2(30)                     not null,
   WhereIsList          VARCHAR2(100)                    not null,
   ListName             VARCHAR2(40)                     not null,
   UserUpdateAvailable  VARCHAR2(1)                      not null,
   constraint PK_YUKONSELECTIONLIST primary key (ListID)
);
insert into YukonSelectionList values( 0, 'N', '(none)', '(none)', '(none)', 'N' );
insert into YukonSelectionList values( 1, 'A', 'Contact', 'DBEditor contact type list', 'ContactType', 'N' );

create table YukonListEntry  (
   EntryID              NUMBER                           not null,
   ListID               NUMBER                           not null,
   EntryOrder           NUMBER                           not null,
   EntryText            VARCHAR2(50)                     not null,
   YukonDefinitionID    NUMBER                           not null,
   constraint PK_YUKONLISTENTRY primary key (EntryID),
   constraint FK_LstEnty_SelLst foreign key (ListID)
         references YukonSelectionList (ListID)
);
insert into YukonListEntry values( 0, 1, 0, '(none)', 0 );
insert into YukonListEntry values( 1, 1, 0, 'Email', 1 );
insert into YukonListEntry values( 2, 1, 0, 'Phone Number', 2 );
insert into YukonListEntry values( 3, 1, 0, 'Pager Number', 2 );
insert into YukonListEntry values( 4, 1, 0, 'Fax Number', 2 );

create index Indx_YkLstDefID on YukonListEntry (
   YukonDefinitionID ASC
);



/******************* START CONTACTNOTIFICATION CHANGES *******************/
create table ContactNotification  (
   ContactNotifID       NUMBER                           not null,
   ContactID            NUMBER                           not null,
   NotificationCategoryID NUMBER                           not null,
   DisableFlag          CHAR(1)                          not null,
   Notification         VARCHAR2(130)                    not null,
   constraint PK_CONTACTNOTIFICATION primary key (ContactNotifID)
);

/*****DO NOT ADD ANY REFERENCES TO THIS TABLE UNTIL THE END*****/
insert into ContactNotification values( 0, 0, 0, 'N', '(none)' );

insert into ContactNotification
select r.recipientid, r.recipientid, 1, r.disableflag, r.emailaddress
from NotificationRecipient r where r.recipientid > 0;

insert into ContactNotification
select r.contactid+1000, r.contactid, 2, 'N', r.contphone1
from CustomerContact r where r.contactid > 0;



/******************* START CUSTOMER CHANGES *******************/
create table Customer  (
   CustomerID           NUMBER                           not null,
   PrimaryContactID     NUMBER                           not null,
   CustomerTypeID       NUMBER                           not null,
   TimeZone             VARCHAR2(40)                     not null,
   constraint PK_CUSTOMER primary key (CustomerID)
);

/*****DO NOT ADD ANY REFERENCES TO THIS TABLE UNTIL THE END*****/
insert into Customer
select c.deviceid, c.primecontactid, 2, c.custtimezone
from CICustomerBase c;

alter table CICustomerBase drop constraint FK_YukPA_CICust;


exec rencol('<UserName>', 'CICustomerBase', 'DeviceID', 'CustomerID');

exec rencol('<UserName>', 'CICustomerBase', 'AddressID', 'MainAddressID');

exec rencol('<UserName>', 'CICustomerBase', 'CustFPL', 'CustomerDemandLevel');


alter table CICustomerBase drop column MainPhoneNumber;

alter table CICustomerBase drop column MainFaxNumber;

alter table CICustomerBase drop column PrimeContactID;

alter table CICustomerBase drop column CustTimeZone;

alter table CICustomerBase ADD CompanyName VARCHAR2(80);

update CICustomerBase SET CompanyName='(none)';

alter TABLE CICustomerBase MODIFY CompanyName NOT NULL;

update CICustomerBase set CompanyName =
(select PAOName
from YukonPAObject
where CustomerID = PAObjectID)
where CustomerID in
(select PAObjectID from YukonPAObject
where CustomerID = PAObjectID);


alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID);

exec rencol('<UserName>', 'LMProgramCurtailCustomerList', 'DeviceID', 'ProgramID');

exec rencol('<UserName>', 'LMProgramCurtailCustomerList', 'LMCustomerDeviceID', 'CustomerID');

exec rencol('<UserName>', 'LMEnergyExchangeCustomerList', 'DeviceID', 'ProgramID');

exec rencol('<UserName>', 'LMEnergyExchangeCustomerList', 'LMCustomerDeviceID', 'CustomerID');




/******************* START GRAPHCUSTOMERLIST CHANGES *******************/
alter table GraphCustomerList drop constraint FK_GRA_REFG_CIC;

alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID);



/******************* START CUSTOMERADDRESS CHANGES *******************/
rename CustomerAddress TO Address;

alter table Address ADD County VARCHAR2(30);

UPDATE Address SET County='(none)';

alter TABLE Address MODIFY County NOT NULL;




/******************* START CUSTOMERCONTACT CHANGES *******************/
rename CustomerContact TO Contact;

alter table Contact drop column ContPhone1;

alter table Contact drop column ContPhone2;

alter table Contact ADD AddressID NUMBER;

update Contact SET AddressID=0;

alter TABLE Contact MODIFY AddressID NOT NULL;



alter table Contact drop constraint FK_CSTCONT_GRPRECIP;

alter table Contact drop column LocationID;

alter table Contact
   add constraint FK_CON_REF__ADD foreign key (AddressID)
      references Address (AddressID);

/* No big deal if this fails, just insure a row is there */
insert into contact values ( 0, '(none)', '(none)', -1, 0 );



/******************* START CICUSTCONTACT CHANGES *******************/
rename CICustContact TO CustomerAdditionalContact;

exec rencol('<UserName>', 'CustomerAdditionalContact', 'DeviceID', 'CustomerID');


alter table CustomerAdditionalContact drop constraint FK_CICSTBASE_CICSTCONT;

alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID);




/******************* START NOTIFICATIONRECIPIENT CHANGES *******************/
alter table PointAlarming drop constraint FK_POI_POIN_NOT;

alter table NotificationDestination drop constraint FK_DESTID_RECID;

drop table NotificationRecipient;

alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID);

alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID);



/******************* START FINAL MISC CHANGES *******************/
alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID);

alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID);

alter table YukonRole ADD RoleDescription VARCHAR2(200);

update YukonRole SET RoleDescription='(none)';

alter TABLE YukonRole MODIFY RoleDescription NOT NULL;

insert into YukonRole values(-104,'WEB_OPERATOR','WebClient','(none)', '(none)');


insert into DeviceMeterGroup 
select paobjectid, 'Default', 'Default', paoname, 'Default' from YukonPAObject
where type like '%ION%';


alter table EnergyCompany ADD WebConfigID NUMBER;
UPDATE EnergyCompany SET WebConfigID = 0;
alter TABLE EnergyCompany MODIFY WebConfigID NOT NULL;


alter table EnergyCompany ADD PrimaryContactID NUMBER;
UPDATE EnergyCompany SET PrimaryContactID = 0;
alter TABLE EnergyCompany MODIFY PrimaryContactID NOT NULL;

alter table EnergyCompany
   add constraint FK_EngCmp_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);
