/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.41', 'Ryan', '20-APR-2003', '');


insert into yukonrole values (-300,'READMETER_MAIN_LOGO','Readmeter','DemoHeader.gif','(none)')
go


/**** CHANGE THE POINT LIMITS PK ****/
alter table pointlimits drop constraint PK_POINTLIMITS
go
alter table pointlimits 
	add constraint PK__POINTID_LIMITNUM primary key (pointid, limitnumber)
go
