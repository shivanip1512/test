/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.40', 'Ryan', '2-FEB-2003', 'Meged STARS customer structure with Yukon');


/******************* START CUSTOMER CHANGES *******************/
/* COPY ALL CICUSTOMERBASE AND PAO CUSTOMER DATA TO CUSTOMER TABLE */
/* TODO */
insert into Customer values
( select c.deviceid, c.primecontactid, 2, c.custtimezone
from CICustomerBase )
go

alter table CICustomerBase drop constraint Ref_YukPA_CICust
go

sp_rename('CICustomerBase', 'DeviceID', 'CustomerID')
go
sp_rename('CICustomerBase', 'AddressID', 'MainAddressID')
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

update set c.CompanyName = y.PAOName
from CICustomerBase c, YukonPAObject y
where c.CustomerID = y.PAObjectID
go



alter table CICustomerBase
   add constraint FK_CstCI_Cst foreign key (CustomerID)
      references Customer (CustomerID)
go



/******************* START CUSTOMERADDRESS CHANGES *******************/
sp_rename( 'CustomerAddress', 'Address' )
go

alter table Address add County varchar(30) not null DEFAULT '(none)'
go
alter table Address add AddressID numeric not null DEFAULT 0
go

alter table Address drop column ContPhone1
go
alter table Address drop column ContPhone2
go



/******************* START CUSTOMERCONTACT CHANGES *******************/
sp_rename( 'CustomerContact', 'Contact' )
go

alter table Contact drop constraint RefCustContract_GroupRecipient
go
alter table Contact drop column LocationID
go

alter table Contact
   add constraint FK_CON_REF__ADD foreign key (AddressID)
      references Address (AddressID)
go
