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
go
update GraphDataSeries set moreData = '(none)';
go
alter table GraphDataSeries alter column moreData varchar(100) not null;
go


alter table systemlog add millis smallint;
go
update systemlog set millis = 0;
go
alter table systemlog alter column millis smallint not null;
go

alter table rawpointhistory add millis smallint;
go
update rawpointhistory set millis = 0;
go
alter table rawpointhistory alter column millis smallint not null;
go




update yukonrole set category = 'Yukon' where roleid = -104;
update yukonrole set roledescription = 'Calculation HIstorical. Edit this role from the Yukon SetUp page.' where roleid = -104;
update yukonrole set category = 'Yukon' where roleid = -105;
update yukonrole set roledescription = 'Web Graph. Edit this role from the Yukon SetUp page.' where roleid = -105;
update yukonrole set category = 'Yukon' where roleid = -106;
update yukonrole set roledescription = 'Billing. Edit this role from the Yukon SetUp page.' where roleid = -106;
update yukonrole set category = 'Operator' where roleid = -2;
update yukonrole set roledescription = 'Default Yukon role. Edit this role from the Yukon SetUp page.' where roleid = -1;



delete from YukonGroupRole where roleid=-104;
delete from YukonGroupRole where roleid=-105;
delete from YukonGroupRole where roleid=-106;


delete from yukonroleproperty where roleid=-303;
delete from yukonrole where roleid=-303;


delete from YukonUserRole where userid=-1;

/* Database Editor */
insert into YukonUserRole values(100,-1,-100,-10000,'(none)');
insert into YukonUserRole values(101,-1,-100,-10001,'(none)');
insert into YukonUserRole values(102,-1,-100,-10002,'(none)');
insert into YukonUserRole values(103,-1,-100,-10003,'(none)');
insert into YukonUserRole values(104,-1,-100,-10004,'(none)');
insert into YukonUserRole values(105,-1,-100,-10005,'(none)');
insert into YukonUserRole values(106,-1,-100,-10006,'(none)');
insert into YukonUserRole values(107,-1,-100,-10007,'(none)');

/* TDC */
insert into YukonUserRole values(120,-1,-101,-10100,'(none)');
insert into YukonUserRole values(121,-1,-101,-10101,'(none)');
insert into YukonUserRole values(122,-1,-101,-10102,'(none)');
insert into YukonUserRole values(123,-1,-101,-10103,'(none)');
insert into YukonUserRole values(124,-1,-101,-10104,'(none)');
insert into YukonUserRole values(125,-1,-101,-10105,'(none)');
insert into YukonUserRole values(126,-1,-101,-10106,'(none)');
insert into YukonUserRole values(127,-1,-101,-10107,'(none)');
insert into YukonUserRole values(128,-1,-101,-10108,'(none)');
insert into YukonUserRole values(129,-1,-101,-10109,'(none)');
insert into YukonUserRole values(130,-1,-101,-10110,'(none)');

/* Trending */
insert into YukonUserRole values(150,-1,-102,-10200,'(none)');
insert into YukonUserRole values(151,-1,-102,-10201,'(none)');

/* Commander */
insert into YukonUserRole values(170,-1,-103,-10300,'(none)');

/* Esubstation Editor */
insert into YukonUserRole values(250,-1,-107,-10700,'(none)');

insert into YukonUserRole values(300,-1,-206,-20600,'(none)');
insert into YukonUserRole values(301,-1,-206,-20601,'(none)');
insert into YukonUserRole values(302,-1,-206,-20602,'(none)');

insert into YukonUserRole values(350,-1,-206,-20600,'(none)');
insert into YukonUserRole values(351,-1,-206,-20601,'true');
insert into YukonUserRole values(352,-1,-206,-20602,'false');

/* Web Client Customers Web Client role */
insert into YukonUserRole values (400, -1, -108, -10800, '/user/CILC/user_trending.jsp');
insert into YukonUserRole values (401, -1, -108, -10801, '(none)');
insert into YukonUserRole values (402, -1, -108, -10802, '(none)');
insert into YukonUserRole values (403, -1, -108, -10803, '(none)');
insert into YukonUserRole values (404, -1, -108, -10804, '(none)');
insert into YukonUserRole values (405, -1, -108, -10805, '(none)');
insert into YukonUserRole values (406, -1, -108, -10806, '(none)');

/* Web Client Customers Direct Load Control role */
insert into YukonUserRole values (407, -1, -300, -30000, '(none)');
insert into YukonUserRole values (408, -1, -300, -30001, 'true');

/* Web Client Customers Curtailment role */
insert into YukonUserRole values (409, -1, -301, -30100, '(none)');
insert into YukonUserRole values (410, -1, -301, -30101, '(none)');

/* Web Client Customers Energy Buyback role */
insert into YukonUserRole values (411, -1, -302, -30200, '(none)');
insert into YukonUserRole values (412, -1, -302, -30200, '(none)');

/* Web Client Customers Commercial Metering role */
insert into YukonUserRole values (413, -1, -304, -30400, '(none)');
insert into YukonUserRole values (414, -1, -304, -30401, 'true');

/* Web Client Customers Administrator role */
insert into YukonUserRole values (415, -1, -305, -30500, 'true');

insert into YukonUserRole values (500,-1,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into YukonUserRole values (501,-1,-108,-10801,'(none)');
insert into YukonUserRole values (502,-1,-108,-10802,'(none)');
insert into YukonUserRole values (503,-1,-108,-10803,'(none)');
insert into YukonUserRole values (504,-1,-108,-10804,'(none)');
insert into YukonUserRole values (505,-1,-108,-10805,'DemoHeaderCES.gif');
insert into YukonUserRole values (506,-1,-108,-10806,'(none)');
insert into YukonUserRole values (520,-1,-400,-40000,'true');
insert into YukonUserRole values (521,-1,-400,-40001,'true');
insert into YukonUserRole values (522,-1,-400,-40002,'false');
insert into YukonUserRole values (523,-1,-400,-40003,'true');
insert into YukonUserRole values (524,-1,-400,-40004,'true');
insert into YukonUserRole values (525,-1,-400,-40005,'true');
insert into YukonUserRole values (526,-1,-400,-40006,'true');
insert into YukonUserRole values (527,-1,-400,-40007,'true');
insert into YukonUserRole values (528,-1,-400,-40008,'true');
insert into YukonUserRole values (529,-1,-400,-40009,'true');
insert into YukonUserRole values (530,-1,-400,-40010,'true');
insert into YukonUserRole values (550,-1,-400,-40050,'false');
insert into YukonUserRole values (551,-1,-400,-40051,'false');
insert into YukonUserRole values (554,-1,-400,-40054,'false');
insert into YukonUserRole values (600,-1,-400,-40100,'(none)');
insert into YukonUserRole values (601,-1,-400,-40101,'(none)');
insert into YukonUserRole values (610,-1,-400,-40110,'(none)');
insert into YukonUserRole values (611,-1,-400,-40111,'(none)');
insert into YukonUserRole values (612,-1,-400,-40112,'(none)');
insert into YukonUserRole values (613,-1,-400,-40113,'(none)');
insert into YukonUserRole values (614,-1,-400,-40114,'(none)');
insert into YukonUserRole values (615,-1,-400,-40115,'(none)');
insert into YukonUserRole values (616,-1,-400,-40116,'(none)');
insert into YukonUserRole values (617,-1,-400,-40117,'(none)');
insert into YukonUserRole values (630,-1,-400,-40130,'(none)');
insert into YukonUserRole values (631,-1,-400,-40131,'(none)');
insert into YukonUserRole values (632,-1,-400,-40132,'(none)');
insert into YukonUserRole values (633,-1,-400,-40133,'(none)');
insert into YukonUserRole values (634,-1,-400,-40134,'(none)');
insert into YukonUserRole values (650,-1,-400,-40150,'(none)');
insert into YukonUserRole values (651,-1,-400,-40151,'(none)');
insert into YukonUserRole values (652,-1,-400,-40152,'(none)');
insert into YukonUserRole values (653,-1,-400,-40153,'(none)');
insert into YukonUserRole values (654,-1,-400,-40154,'(none)');
insert into YukonUserRole values (655,-1,-400,-40155,'(none)');
insert into YukonUserRole values (656,-1,-400,-40156,'(none)');
insert into YukonUserRole values (657,-1,-400,-40157,'(none)');
insert into YukonUserRole values (658,-1,-400,-40158,'(none)');
insert into YukonUserRole values (670,-1,-400,-40170,'(none)');
insert into YukonUserRole values (671,-1,-400,-40171,'(none)');
insert into YukonUserRole values (672,-1,-400,-40172,'(none)');
insert into YukonUserRole values (673,-1,-400,-40173,'(none)');
insert into YukonUserRole values (680,-1,-400,-40180,'(none)');
insert into YukonUserRole values (681,-1,-400,-40181,'(none)');

insert into YukonUserRole values (700,-1,-108,-10800,'/operator/Operations.jsp');
insert into YukonUserRole values (701,-1,-108,-10801,'(none)');
insert into YukonUserRole values (702,-1,-108,-10802,'(none)');
insert into YukonUserRole values (703,-1,-108,-10803,'(none)');
insert into YukonUserRole values (704,-1,-108,-10804,'(none)');
insert into YukonUserRole values (705,-1,-108,-10805,'(none)');
insert into YukonUserRole values (706,-1,-108,-10806,'(none)');
insert into YukonUserRole values (720,-1,-201,-20100,'true');
insert into YukonUserRole values (721,-1,-201,-20101,'true');
insert into YukonUserRole values (722,-1,-201,-20102,'true');
insert into YukonUserRole values (723,-1,-201,-20103,'true');
insert into YukonUserRole values (724,-1,-201,-20104,'false');
insert into YukonUserRole values (725,-1,-201,-20105,'false');
insert into YukonUserRole values (726,-1,-201,-20106,'true');
insert into YukonUserRole values (727,-1,-201,-20107,'true');
insert into YukonUserRole values (728,-1,-201,-20108,'true');
insert into YukonUserRole values (729,-1,-201,-20109,'true');
insert into YukonUserRole values (730,-1,-201,-20110,'true');
insert into YukonUserRole values (731,-1,-201,-20111,'true');
insert into YukonUserRole values (732,-1,-201,-20112,'true');
insert into YukonUserRole values (733,-1,-201,-20113,'true');
insert into YukonUserRole values (734,-1,-201,-20114,'true');
insert into YukonUserRole values (735,-1,-201,-20115,'true');
insert into YukonUserRole values (736,-1,-201,-20116,'true');
insert into YukonUserRole values (737,-1,-201,-20117,'true');
insert into YukonUserRole values (750,-1,-201,-20150,'true');
insert into YukonUserRole values (751,-1,-201,-20151,'true');
insert into YukonUserRole values (752,-1,-201,-20152,'false');
insert into YukonUserRole values (770,-1,-202,-20200,'(none)');
insert into YukonUserRole values (775,-1,-203,-20300,'(none)');
insert into YukonUserRole values (776,-1,-203,-20301,'(none)');
insert into YukonUserRole values (780,-1,-204,-20400,'(none)');
insert into YukonUserRole values (785,-1,-205,-20500,'(none)');
insert into YukonUserRole values (790,-1,-207,-20700,'(none)');
insert into YukonUserRole values (800,-1,-201,-20800,'(none)');
insert into YukonUserRole values (810,-1,-201,-20810,'(none)');
insert into YukonUserRole values (813,-1,-201,-20813,'(none)');
insert into YukonUserRole values (814,-1,-201,-20814,'(none)');
insert into YukonUserRole values (815,-1,-201,-20815,'(none)');
insert into YukonUserRole values (816,-1,-201,-20816,'(none)');
insert into YukonUserRole values (819,-1,-201,-20819,'(none)');
insert into YukonUserRole values (820,-1,-201,-20820,'(none)');
insert into YukonUserRole values (830,-1,-201,-20830,'(none)');
insert into YukonUserRole values (831,-1,-201,-20831,'(none)');
insert into YukonUserRole values (832,-1,-201,-20832,'(none)');
insert into YukonUserRole values (833,-1,-201,-20833,'(none)');
insert into YukonUserRole values (834,-1,-201,-20834,'(none)');
insert into YukonUserRole values (850,-1,-201,-20850,'(none)');
insert into YukonUserRole values (851,-1,-201,-20851,'(none)');
insert into YukonUserRole values (852,-1,-201,-20852,'(none)');
insert into YukonUserRole values (853,-1,-201,-20853,'(none)');
insert into YukonUserRole values (854,-1,-201,-20854,'(none)');
insert into YukonUserRole values (855,-1,-201,-20855,'(none)');
insert into YukonUserRole values (856,-1,-201,-20856,'(none)');
insert into YukonUserRole values (870,-1,-201,-20870,'(none)');





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