/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/


create table YukonServices (
ServiceID            numeric              not null,
ServiceName          varchar(60)          not null,
ServiceClass         varchar(100)         not null,
ParamNames           varchar(300)         not null,
ParamValues          varchar(300)         not null
);
go

insert into YukonServices values( 1, 'Notification_Server', 'com.cannontech.jmx.services.DynamicNotifcationServer', '(none)', '(none)' );
alter table YukonServices
   add constraint PK_YUKSER primary key  (ServiceID);
go




insert into billingFileformats values (9, 'CTI2');


alter table GraphDataSeries add moreData varchar(100);
update GraphDataSeries set moreData = '(none)';
alter table GraphDataSeries alter column moreData varchar(100) not null;
go


alter table systemlog add millis smallint;
update systemlog set millis = 0;
alter table systemlog alter column millis smallint not null;
go

alter table rawpointhistory add millis smallint;
update rawpointhistory set millis = 0;
alter table rawpointhistory alter column millis smallint not null;
go




/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '11-JAN-2004', 'Many changes. Much ');
go