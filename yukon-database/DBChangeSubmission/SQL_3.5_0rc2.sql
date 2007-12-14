/* 3.5_0rc2 changes.  These are changes to 3.5 that have been made since 3.5_0rc1*/
/* Start YUK-4745 */
insert into YukonRole values(-211,'CI Curtailment','Operator','Operator access to C&I Curtailment');
insert into YukonRoleProperty values(-21100,-211,'CI Curtailment Label','CI Curtailment','The operator specific name for C&I Curtailment');
/* End YUK-4745 */

/* Start YUK-4906 */
alter table MSPVendor add MaxReturnRecords int;
go
update MSPVendor set MaxReturnRecords = 10000;
go
alter table MSPVendor alter column MaxReturnRecords int not null;
go

alter table MSPVendor add RequestMessageTimeout int;
go
update MSPVendor set RequestMessageTimeout = 120000;
go
alter table MSPVendor alter column RequestMessageTimeout int not null;
go

alter table MSPVendor add MaxInitiateRequestObjects int;
go
update MSPVendor set MaxInitiateRequestObjects = 15;
go
alter table MSPVendor alter column MaxInitiateRequestObjects int not null;
go

alter table MSPVendor add TemplateNameDefault varchar(50);
go
update MSPVendor set TemplateNameDefault = '*Default Template';
go
alter table MSPVendor alter column TemplateNameDefault varchar(50) not null;
go
/* End YUK-4906 */

