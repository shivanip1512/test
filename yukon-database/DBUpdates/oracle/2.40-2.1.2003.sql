/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

/*********************************************************************************/
/*  Oracle 9.2 and above have the rename column command built in.                */
/*  If the DB version is less than 9.2, you must install                         */
/*  the rencol() procedure AND run it from                                       */
/*  the sys/CHANGE_ON_INSTALL account manually                                   */
/*                                                                               */
/*  NOTE!!! for Oracle 9.0 - 9.1 you should log in as                            */
/*  sys/CHANGE_ON_INSTALL as sysdba (ACL 7/29)                                   */
/*                                                                               */
/*********************************************************************************/
alter table CICustomerBase rename column DeviceID to CustomerID;
alter table CICustomerBase rename column AddressID to MainAddressID;
alter table CICustomerBase rename column CustFPL to CustomerDemandLevel;
alter table LMProgramCurtailCustomerList rename column DeviceID to ProgramID;
alter table LMProgramCurtailCustomerList rename column LMCustomerDeviceID to CustomerID;
alter table LMEnergyExchangeCustomerList rename column DeviceID to ProgramID;
alter table LMEnergyExchangeCustomerList rename column LMCustomerDeviceID to CustomerID;
alter table CICustContact rename column DeviceID to CustomerID;

create table YukonSelectionList  (
   ListID               NUMBER                           not null,
   Ordering             VARCHAR2(1)                      not null,
   SelectionLabel       VARCHAR2(30)                     not null,
   WhereIsList          VARCHAR2(100)                    not null,
   ListName             VARCHAR2(40)                     not null,
   UserUpdateAvailable  VARCHAR2(1)                      not null,
   constraint PK_YUKONSELECTIONLIST primary key (ListID)
);

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

create index Indx_YkLstDefID on YukonListEntry (
   YukonDefinitionID ASC
);

create table ContactNotification  (
   ContactNotifID       NUMBER                           not null,
   ContactID            NUMBER                           not null,
   NotificationCategoryID NUMBER                           not null,
   DisableFlag          CHAR(1)                          not null,
   Notification         VARCHAR2(130)                    not null,
   constraint PK_CONTACTNOTIFICATION primary key (ContactNotifID)
);

create table Customer  (
   CustomerID           NUMBER                           not null,
   PrimaryContactID     NUMBER                           not null,
   CustomerTypeID       NUMBER                           not null,
   TimeZone             VARCHAR2(40)                     not null,
   constraint PK_CUSTOMER primary key (CustomerID)
);

/* @error ignore */
alter table CICustomerBase drop constraint FK_YukPA_CICust;
alter table CICustomerBase ADD CompanyName VARCHAR2(80);
update CICustomerBase SET CompanyName='(none)';
alter TABLE CICustomerBase MODIFY CompanyName NOT NULL;


insert into Customer
select c.customerid, c.primecontactid, 2, c.custtimezone
from CICustomerBase c;

update CICustomerBase set CompanyName =
(select PAOName
from YukonPAObject
where CustomerID = PAObjectID)
where CustomerID in
(select PAObjectID from YukonPAObject
where CustomerID = PAObjectID);


alter table CICustomerBase drop column MainPhoneNumber;
alter table CICustomerBase drop column MainFaxNumber;
alter table CICustomerBase drop column PrimeContactID;
alter table CICustomerBase drop column CustTimeZone;

alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID);

/* @error ignore */
alter table GraphCustomerList drop constraint FK_GRA_REFG_CIC;

alter table GraphCustomerList
   add constraint FK_GrphCstLst_Cst foreign key (CustomerID)
      references Customer (CustomerID);

rename CustomerAddress TO Address;
alter table Address ADD County VARCHAR2(30);
UPDATE Address SET County='(none)';
alter TABLE Address MODIFY County NOT NULL;

rename CICustContact TO CustomerAdditionalContact;
/* @error ignore */
alter table CustomerAdditionalContact drop constraint FK_CICSTBASE_CICSTCONT;
alter table CustomerAdditionalContact
   add constraint FK_Cust_CustAddCnt foreign key (CustomerID)
      references Customer (CustomerID);

/* @error ignore */
alter table PointAlarming drop constraint FK_POI_POIN_NOT;
/* @error ignore */
alter table NotificationDestination drop constraint FK_DESTID_RECID;

alter table ContactNotification
   add constraint FK_CntNot_YkLs foreign key (NotificationCategoryID)
      references YukonListEntry (EntryID);
alter table YukonRole ADD RoleDescription VARCHAR2(200);
update YukonRole SET RoleDescription='(none)';
alter TABLE YukonRole MODIFY RoleDescription NOT NULL;

alter table EnergyCompany ADD WebConfigID NUMBER;
UPDATE EnergyCompany SET WebConfigID = 0;
alter TABLE EnergyCompany MODIFY WebConfigID NOT NULL;

create table YukonWebConfiguration  (
   ConfigurationID      NUMBER                           not null,
   LogoLocation         VARCHAR2(100),
   Description          VARCHAR2(500),
   AlternateDisplayName VARCHAR2(50),
   URL                  VARCHAR2(100),
   constraint PK_YUKONWEBCONFIGURATION primary key (ConfigurationID)
);





/* ***** IMPORTANT *****  -  the billingFileFormats update is to disable MV_90 DATA Import */
/*  DO NOT RUN THE BILLINGFILEFORMATS UPDATE IF THE CUSTOMER HAS PAID FOR THE MV_90 FORMAT */
update billingFileFormats set formatid = -11 where formatid = 11;

/******************* NEW BILLING FORMATS *******************/
insert into billingfileformats values(13, 'NISC-Turtle');
insert into billingfileformats values(14, 'NISC-NCDC');



/******************* START YUKONLISTENTRY CHANGES *******************/
insert into YukonSelectionList values( 0, 'N', '(none)', '(none)', '(none)', 'N' );
insert into YukonSelectionList values( 1, 'A', 'Contact', 'DBEditor contact type list', 'ContactType', 'N' );

insert into YukonListEntry values( 0, 1, 0, '(none)', 0 );
insert into YukonListEntry values( 1, 1, 0, 'Email', 1 );
insert into YukonListEntry values( 2, 1, 0, 'Phone Number', 2 );
insert into YukonListEntry values( 3, 1, 0, 'Pager Number', 2 );
insert into YukonListEntry values( 4, 1, 0, 'Fax Number', 2 );
insert into YukonListEntry values( 5, 1, 0, 'Home Phone', 2 );
insert into YukonListEntry values( 6, 1, 0, 'Work Phone', 2 );



/******************* START ContactNotification CHANGES *******************/
insert into ContactNotification
select r.recipientid, r.recipientid, 1, r.disableflag, r.emailaddress
from NotificationRecipient r where r.recipientid > 0;

insert into ContactNotification
select r.contactid+1000, r.contactid, 2, 'N', r.contphone1
from CustomerContact r where r.contactid > 0;

alter table NotificationDestination
   add constraint FK_CntNt_NtDst foreign key (RecipientID)
      references ContactNotification (ContactNotifID);

rename CustomerContact TO Contact;
alter table Contact drop column ContPhone1;
alter table Contact drop column ContPhone2;
alter table Contact ADD AddressID NUMBER;
update Contact SET AddressID=0;
alter TABLE Contact MODIFY AddressID NOT NULL;

/* @error ignore */
alter table Contact drop constraint FK_CSTCONT_GRPRECIP;

alter table Contact drop column LocationID;

/* @error ignore */
insert into address values ( 0, '(none)', '(none)', '(none)', 'MN', '(none)', '(none)' );

/* @error ignore */
insert into contact values ( 0, '(none)', '(none)', -1, 0 );

insert into ContactNotification values( 0, 0, 0, 'N', '(none)' );

alter table Contact
   add constraint FK_CON_REF__ADD foreign key (AddressID)
      references Address (AddressID);
alter table ContactNotification
   add constraint FK_Cnt_CntNot foreign key (ContactID)
      references Contact (ContactID);

alter table PointAlarming
   add constraint FK_CntNt_PtAl foreign key (RecipientID)
      references ContactNotification (ContactNotifID);


/* Create a dummy contact for notifications that do not have a contact */
insert into address 
select max(addressid)+1, '(none)', '(none)', '(none)', 'MN', '(none)', '(none)' from address;
insert into contact 
select max(contactid)+1, 'DUMMY', 'DUMMY', -1, max(a.addressid) from contact,address a;

/* Assign the dangling notifications to the dummy contact */
update contactnotification cn 
set cn.contactid = (select max(c.contactid) from contact c)
where cn.contactid not in (select c.contactid from contact c);

drop table NotificationRecipient;


/******************* START FINAL MISC CHANGES *******************/
insert into DeviceMeterGroup 
select paobjectid, 'Default', 'Default', 'Default', 'Default' from YukonPAObject
where type like '%ION%';

insert into YukonWebConfiguration values(0,'(none)','(none)','(none)','(none)');

update yukonlistentry set listid=0 where entryid=0;


delete from yukongrouprole where roleid=-101 or roleid=-102 or roleid=-104;
delete from yukonuserrole where roleid=-101 or roleid=-102 or roleid=-104;
delete from yukonrole where roleid=-101 or roleid=-102 or roleid=-104;


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


insert into YukonRole values(-1000,'WEB_OPERATOR_SUPER','WebClient','(none)','(none)');
insert into YukonRole values(-1001,'NEW_ACCOUNT_WIZARD','WebClient','(none)','(none)');
insert into YukonRole values(-1002,'SWITCH_COMMAND_BATCH','WebClient','(none)','(none)');
insert into YukonRole values(-1003,'NOTIFICATION_ON_GENERAL_PAGE','WebClient','(none)','(none)');
insert into YukonRole values(-1004,'HIDE_OPT_OUT_BOX','WebClient','(none)','(none)');
insert into YukonRole values(-1005,'CUSTOMIZED_FAQ_LINK','WebClient','(none)','(none)');
insert into YukonRole values(-1006,'CUSTOMIZED_EMAIL_LINK','WebClient','(none)','(none)');


insert into yukonrole values(-9000,'TRENDING_DISCLAIMER_TEXT','WebClient',' ','(none)');
insert into yukonrole values(-9001,'ENERGYEXCHANGE_TEXT','WebClient','Energy Exchange','(none)');
insert into yukonrole values(-9002,'ENERGYEXCHANGE_HEADING_TEXT','WebClient','Energy Exchange','(none)');
insert into yukonrole values(-9003,'ENERGYEXCHANGE_PHONE_TEXT','WebClient',' ','(none)');
insert into yukonrole values(-9010,'CURTAILMENT_TEXT','WebClient','Notification','(none)');
insert into yukonrole values(-9011,'CURTAILMENT_PROVIDER_TEXT','WebClient','Curtailment Provider','(none)');


insert into yukongroup values(-210,'Web Demo Operators');
insert into yukongroup values(-211,'Web Demo Residential Customers');
insert into yukongroup values(-212,'Web Demo CICustomers');

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


/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.40', 'Ryan', '20-FEB-2003', 'Merged STARS customer structure with Yukon');