/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

create table YukonServices  (
   ServiceID            NUMBER                           not null,
   ServiceName          VARCHAR2(60)                     not null,
   ServiceClass         VARCHAR2(100)                    not null,
   ParamNames           VARCHAR2(300)                    not null,
   ParamValues          VARCHAR2(300)                    not null
);

insert into YukonServices values( 1, 'Notification_Server', 'com.cannontech.jmx.services.DynamicNotifcationServer', '(none)', '(none)' );
alter table YukonServices
   add constraint PK_YUKSER primary key (ServiceID);


insert into billingFileformats values (9, 'CTI2');


alter table GraphDataSeries add moreData varchar2(100);
update GraphDataSeries set moreData = '(none)';
alter table GraphDataSeries modify moreData not null;
go


alter table systemlog add millis smallint;
update systemlog set millis = 0;
alter table systemlog modify millis not null;
go

alter table rawpointhistory add millis smallint;
update rawpointhistory set millis = 0;
alter table rawpointhistory modify millis not null;
go





/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '11-JAN-2004', 'Many changes to a major version jump');
go