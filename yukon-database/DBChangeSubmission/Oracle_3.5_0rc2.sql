/* 3.5_0rc2 changes.  These are changes to 3.5 that have been made since 3.5_0rc1*/
/* Start YUK-4745 */
insert into YukonRole values(-211,'CI Curtailment','Operator','Operator access to C&I Curtailment');
insert into YukonRoleProperty values(-21100,-211,'CI Curtailment Label','CI Curtailment','The operator specific name for C&I Curtailment');
/* End YUK-4745 */

/* Start YUK-4906 */
alter table MSPVendor add MaxReturnRecords int;
update MSPVendor set MaxReturnRecords = 10000;
alter table MSPVendor modify MaxReturnRecords int not null;

alter table MSPVendor add RequestMessageTimeout int;
update MSPVendor set RequestMessageTimeout = 120000;
alter table MSPVendor modify RequestMessageTimeout int not null;

alter table MSPVendor add MaxInitiateRequestObjects int;
update MSPVendor set MaxInitiateRequestObjects = 15;
alter table MSPVendor modify MaxInitiateRequestObjects int not null;

alter table MSPVendor add TemplateNameDefault varchar2(50);
update MSPVendor set TemplateNameDefault = '*Default Template';
alter table MSPVendor modify TemplateNameDefault varchar2(50) not null;
/* End YUK-4906 */

/* Start YUK-4984 */
/* @start-block */
declare
v_capid number;
cursor c_capid is select deviceid as capid from capbank;
begin
	open c_capid;
	fetch c_capid into v_capid;
	while (c_capid%found)
	  loop
	    insert into capbankadditional
		select 
			v_capid,
			0,
			0,
			'(none)',
			0.0,
			0.0,
			'(none)',
			'(none)',
			0,
			'N',
			'(none)',
			'01-JAN-1900',
			'01-JAN-1900',
			'01-JAN-1900',
			'(none)',
			'N',
			'(none)',
			'(none)',
			'01-JAN-1900'
		from 
			dual;
	  fetch c_capid into v_capid;
	  end loop;
	close c_capid;
end;
/ 
/* @end-block */
/* End YUK-4984 */
