/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.41', 'Ryan', '20-APR-2003', '-');


insert into yukonrole values (-300,'READMETER_MAIN_LOGO','Readmeter','DemoHeader.gif','(none)');


/**** CHANGE THE POINT LIMITS PK ****/
alter table pointlimits drop constraint PK_POINTLIMITS;

alter table pointlimits
   add constraint PK__POINTID_LIMITNUM primary key (pointid, limitnumber);

   

/**** ADD COLUMNS TO THE DynamicLMGroup TABLE ****/
alter table DynamicLMGroup ADD ControlStartTime DATE;
UPDATE DynamicLMGroup SET ControlStartTime='01-JAN-1990';
alter TABLE DynamicLMGroup MODIFY ControlStartTime NOT NULL;

alter table DynamicLMGroup ADD ControlCompleteTime DATE;
UPDATE DynamicLMGroup SET ControlCompleteTime='01-JAN-1990';
alter TABLE DynamicLMGroup MODIFY ControlCompleteTime NOT NULL;
