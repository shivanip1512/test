/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.39', 'Ryan', '20-DEC-2002', 'Added two columns to DynamicCCCapBank, YukonUser, PortDialback and others');


insert into columntype values ( 14, 'PointImage' );
update graphdataseries set type = 'peakinterval' where type = 'peakvalue';


create or replace view Peakpointhistory_View as
select rph1.POINTID pointid, rph1.VALUE value, min(rph1.timestamp) timestamp
from RAWPOINTHISTORY rph1
where value in ( select max ( value ) from rawpointhistory rph2 where rph1.pointid = rph2.pointid )
group by pointid, value;





/* Change the DynamicCCCapBank table */
alter table DynamicCCCapBank add OriginalFeederID NUMBER;
update DynamicCCCapBank set OriginalFeederID = 0;
alter TABLE DynamicCCCapBank MODIFY OriginalFeederID NOT NULL;


alter table DynamicCCCapBank add OriginalSwitchingOrder NUMBER;
update DynamicCCCapBank set OriginalSwitchingOrder = 0;
alter TABLE DynamicCCCapBank MODIFY OriginalSwitchingOrder NOT NULL;



/* Change all the PAOName illegal chars to - */
/* The return result will say all rows updated, but that is not necessarily true */
update yukonpaobject set paoname = replace(paoname, ',', '-');
update yukonpaobject set paoname = replace(paoname, '\', '-');
update yukonpaobject set paoname = replace(paoname, '|', '-');
update yukonpaobject set paoname = replace(paoname, '"', '-');

update display set description = 'com.cannontech.tdc.windows.WinServicePanel' where displaynum=-4;



/* Try to add/modify any needed items that allows us to migrate to the YukonUser */
alter table energycompanyoperatorloginlist drop constraint FK_OpLgEnCmpOpLs;
alter table lmdirectoperatorlist drop constraint FK_OpLg_LMDOpLs;
alter table customercontact drop constraint FK_RefCstLg_CustCont;
alter table customerloginserialgroup drop constraint FK_CsLgSG_CsL;
alter table operatorserialgroup drop constraint FK_OpSGrp_OpLg;
alter table LMMACSScheduleOperatorList drop constraint FK_OpLgLMMcSchOpLs;
alter table OperatorLoginGraphList drop constraint FK_OpLgOpLgGrLs2;




/*==============================================================*/
/* Table : YukonUser                                            */
/*==============================================================*/
create table YukonUser  (
   UserID               NUMBER                           not null,
   UserName             VARCHAR2(64)                     not null,
   Password             VARCHAR2(64)                     not null,
   LoginCount           NUMBER                           not null,
   LastLogin            DATE                             not null,
   Status               VARCHAR2(20)                     not null,
   constraint PK_YUKONUSER primary key (UserID)
);
insert into YukonUser values(-1,'yukon','yukon',0,'01-JAN-00','Enabled');
insert into YukonUser values(-2,'webuser','webuser',0,'01-JAN-00','Enabled');
insert into YukonUser values(-3,'weboper','weboper',0,'01-JAN-00','Enabled');
/*==============================================================*/
/* Index: Indx_YkUsIDNm                                         */
/*==============================================================*/
create unique index Indx_YkUsIDNm on YukonUser (
   UserName ASC
);



/* We must change the reference ID from CustomerLogin to match  */
/*  the new entry into YukonUser (There better not be more than 100 operator logins) */
update CustomerContact set LoginID=LoginID+100 where LoginID >= 0;
update CustomerLoginSerialGroup set LoginID=LoginID+100 where LoginID >= 0;


insert into YukonUser (UserID,UserName,Password,LoginCount,LastLogin,Status)
select LoginID+100,UserName,Password,LoginCount,LastLogin,Status from CustomerLogin
where loginid >= 0;


insert into YukonUser (UserID,UserName,Password,LoginCount,LastLogin,Status)
select LoginID,UserName,Password,LoginCount,LastLogin,Status from OperatorLogin;



/*==============================================================*/
/* Table : YukonRole                                            */
/*==============================================================*/
create table YukonRole  (
   RoleID               NUMBER                           not null,
   RoleName             VARCHAR2(120)                    not null,
   Category             VARCHAR2(60)                     not null,
   DefaultValue         VARCHAR2(1000)                   not null,
   constraint PK_YUKONROLE primary key (RoleID)
);
insert into YukonRole values(-1,'point_id_edit','Client','true');	
insert into YukonRole values(-2,'dbeditor_core','Client','true');
insert into YukonRole values(-3,'dbeditor_lm','Client','true');
insert into YukonRole values(-4,'dbeditor_cap_control','Client','false');
insert into YukonRole values(-5,'dbeditor_system','Client','true');
insert into YukonRole values(-6,'dispatch_machine','Client','127.0.0.1');
insert into YukonRole values(-7,'dispatch_port','Client','1510');
insert into YukonRole values(-8,'porter_machine','Client','127.0.0.1');
insert into YukonRole values(-9,'porter_port','Client','1540');
insert into YukonRole values(-10,'macs_machine','Client','127.0.0.1');
insert into YukonRole values(-11,'macs_port','Client','1900');
insert into YukonRole values(-12,'cap_control_machine','Client','127.0.0.1');
insert into YukonRole values(-13,'cap_control_port','Client','1910');
insert into YukonRole values(-14,'loadcontrol_machine','Client','127.0.0.1');
insert into YukonRole values(-15,'loadcontrol_port','Client','1920');
insert into YukonRole values(-16,'loadcontrol_edit','Client','00000000');
insert into YukonRole values(-17,'macs_edit','Client','00000CCC');
insert into YukonRole values(-18,'tdc_express','Client','ludicrous_speed');
insert into YukonRole values(-19,'tdc_max_rows','Client','500');
insert into YukonRole values(-20,'calc_historical_interval','Client','900');
insert into YukonRole values(-21,'calc_historical_baseline_calctime','Client','4');
insert into YukonRole values(-22,'calc_historical_daysprevioustocollect','Client','30');
insert into YukonRole values(-23,'webgraph_home_directory','Client','c:\yukon\client\webgraphs');
insert into YukonRole values(-24,'webgraph_run_interval','Client','900');
insert into YukonRole values(-25,'graph_edit_graphdefinition','Client','true');
insert into YukonRole values(-26,'decimal_places','Client','2');
insert into YukonRole values(-27,'CAP_CONTROL_INTERFACE','Client','amfm');
insert into YukonRole values(-28,'utility_id_range','Client','1-254');
insert into YukonRole values(-29,'tdc_rights','Client','00000000');
insert into YukonRole values(-30,'cbc_creation_name','Client','CBC %PAOName%');
insert into YukonRole values(-31,'billing_wiz_activate','Client','false'	);
insert into YukonRole values(-32,'billing_input_file','Client','c:\yukon\client\bin\BillingIn.txt');
insert into YukonRole values(-33,'client_log_level','Client','INFO');
insert into YukonRole values(-34,'client_log_file','Client','false');

insert into YukonRole values(-100,'HOME_URL','WebClient','default.jsp');
insert into YukonRole values(-101,'WEB_USER','WebClient','(none)');
insert into YukonRole values(-102,'WEB_OPERATOR','WebClient','(none)');

/*==============================================================*/
/* Index: Indx_YukRol_Nm                                        */
/*==============================================================*/
create index Indx_YukRol_Nm on YukonRole (
   RoleName ASC
);


/*==============================================================*/
/* Table : YukonGroup                                           */
/*==============================================================*/
create table YukonGroup  (
   GroupID              NUMBER                           not null,
   GroupName            VARCHAR2(120)                    not null,
   constraint PK_YUKONGROUP primary key (GroupID)
);
insert into YukonGroup values(-1,'default users');
insert into YukonGroup values(-2,'web users');
insert into YukonGroup values(-3,'web operators');


/*==============================================================*/
/* Table : YukonUserRole                                        */
/*==============================================================*/
create table YukonUserRole  (
   UserID               NUMBER                           not null,
   RoleID               NUMBER                           not null,
   Value                VARCHAR2(1000)                   not null,
   constraint PK_YUKONUSERROLE primary key (UserID, RoleID),
   constraint FK_YkUsRlr_YkUs foreign key (UserID)
         references YukonUser (UserID),
   constraint FK_YkUsRl_YkRl foreign key (RoleID)
         references YukonRole (RoleID)
);


/*==============================================================*/
/* Table : YukonUserGroup                                       */
/*==============================================================*/
create table YukonUserGroup  (
   UserID               NUMBER                           not null,
   GroupID              NUMBER                           not null,
   constraint PK_YUKONUSERGROUP primary key (UserID, GroupID),
   constraint FK_YUK_REF__YUK foreign key (UserID)
         references YukonUser (UserID),
   constraint FK_YkUsGr_YkGr foreign key (GroupID)
         references YukonGroup (GroupID)
);
insert into YukonUserGroup values(-1,-1);
insert into YukonUserGroup values(-2,-2);
insert into YukonUserGroup values(-3,-3);


/* Assign all users to the default user group, this may not be what is wanted */
insert into YukonUserGroup (UserID, GroupID)
select UserID,-1 from YukonUser
where UserID >= 0;


/*==============================================================*/
/* Table : YukonGroupRole                                       */
/*==============================================================*/
create table YukonGroupRole  (
   GroupID              NUMBER                           not null,
   RoleID               NUMBER                           not null,
   Value                VARCHAR2(1000)                   not null,
   constraint PK_YUKONGROUPROLE primary key (GroupID, RoleID),
   constraint FK_YkGrRl_YkGr foreign key (GroupID)
         references YukonGroup (GroupID),
   constraint FK_YkGrRl_YkRl foreign key (RoleID)
         references YukonRole (RoleID)
);
insert into YukonGroupRole values(-1,-1,'true');
insert into YukonGroupRole values(-1,-2,'true');
insert into YukonGroupRole values(-1,-3,'true');
insert into YukonGroupRole values(-1,-4,'false');
insert into YukonGroupRole values(-1,-5,'true');
insert into YukonGroupRole values(-1,-6,'127.0.0.1');
insert into YukonGroupRole values(-1,-7,'1510');
insert into YukonGroupRole values(-1,-8,'128.0.0.1');
insert into YukonGroupRole values(-1,-9,'1540');
insert into YukonGroupRole values(-1,-10,'127.0.0.1');
insert into YukonGroupRole values(-1,-11,'1900');
insert into YukonGroupRole values(-1,-12,'127.0.0.1');
insert into YukonGroupRole values(-1,-13,'1910');
insert into YukonGroupRole values(-1,-14,'127.0.0.1');
insert into YukonGroupRole values(-1,-15,'1920');
insert into YukonGroupRole values(-1,-16,'00000000');
insert into YukonGroupRole values(-1,-17,'00000CCC');
insert into YukonGroupRole values(-1,-18,'ludicrous_speed');
insert into YukonGroupRole values(-1,-19,'500');
insert into YukonGroupRole values(-1,-20,'900');
insert into YukonGroupRole values(-1,-21,'4');
insert into YukonGroupRole values(-1,-22,'30');
insert into YukonGroupRole values(-1,-23,'c:\yukon\client\webgraphs');
insert into YukonGroupRole values(-1,-24,'900');
insert into YukonGroupRole values(-1,-25,'4');
insert into YukonGroupRole values(-1,-26,'2');
insert into YukonGroupRole values(-1,-27,'amfm');
insert into YukonGroupRole values(-1,-28,'1-254');
insert into YukonGroupRole values(-1,-29,'00000000');
insert into YukonGroupRole values(-1,-30,'CBC %PAOName%');
insert into YukonGroupRole values(-1,-31,'false');
insert into YukonGroupRole values(-1,-32,'c:\yukon\client\bin\BillingIn.txt');
insert into YukonGroupRole values(-1,-33,'INFO');
insert into YukonGroupRole values(-1,-34,'false');
insert into YukonGroupRole values(-2,-101,'(none)');
insert into YukonGroupRole values(-3,-102,'(none)');
insert into YukonGroupRole values(-2,-100,'/user/user_trending.jsp?tab=graph');
insert into YukonGroupRole values(-3,-100,'/operator/oper_trending.jsp?tab=graph');


INSERT INTO UnitMeasure VALUES( 51,'km/h',0,'Kilometers Per Hour','(none)');
INSERT INTO UnitMeasure VALUES( 52,'m/s',0,'Meters Per Second','(none)');
INSERT INTO UnitMeasure VALUES( 53,'KV', 0,'KVolts','(none)' );
INSERT INTO UnitMeasure VALUES( 54,'UNDEF', 0,'Undefined','(none)' );


/* NEW DATABASE UPDATES FROM BRANCH BUILD - sn */
/* Update all gds from their string representation to a bit representation.
   The numeric values are defined in GraphDataSeries.java */
update graphdataseries set type = replace(type, 'graph', '9');
update graphdataseries set type = replace(type, 'peakinterval', '17');
update graphdataseries set type = replace(type, 'peakvalue', '17');
update graphdataseries set type = replace(type, 'peak', '2');
update graphdataseries set type = replace(type, 'usage', '4');
update graphdataseries set type = replace(type, 'yesterday', '33');

/* Alter the graphdataseries column type from a varchar to a numeric */
create table gdstemp  (
   gdsID              NUMBER                           not null,
   type               NUMBER                           not null
);
insert into gdstemp (gdsid, type) (select distinct graphdataseriesid, type from graphdataseries);
alter table graphdataseries modify type null;
update graphdataseries set type = null;
alter table graphdataseries modify type number;
update graphdataseries set type = (select type from gdstemp where graphdataseriesid = gdsid);
alter table graphdataseries modify type not null;
drop table gdstemp;

/* Update graphdataseries type.
   Here we are combining all previous peak point gds into their corresponding 
   based off of pointids.  Since we are converting to a bit mask type field,
   we no longer need separate entries for all gds*/
update graphdataseries set type = (type + 2) where graphdataseriesid in (
select gds1.graphdataseriesid  from graphdataseries gds1, graphdataseries gds2
where gds1.graphdefinitionid = gds2.graphdefinitionid 
and gds1.pointid = gds2.pointid
and gds1.color = gds2.color
and gds1.axis = gds2.axis
and gds1.label = gds2.label
and gds2.type = 2
and gds1.type != 2);

/* Remove the duplicate gds entries (all old peak types) */
delete graphdataseries where type = 2;
