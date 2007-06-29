/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!

/*3.5 CHANGES IN BY FRI. MORNING PLEASE PLEASE PLEASE*/

/*CURRENT LIST*/
/*Tom's DeviceMeterGroup changes*/
CREATE TABLE DeviceGroup(
	DeviceGroupId numeric(18, 0) NOT NULL,
	GroupName varchar(255) NOT NULL,
	ParentDeviceGroupId numeric(18, 0) NULL,
	SystemGroup char(1) NOT NULL,
	Type varchar(255) NOT NULL,
 CONSTRAINT PK_DeviceGroup PRIMARY KEY CLUSTERED (DeviceGroupId ASC)
)

ALTER TABLE DeviceGroup  WITH CHECK ADD  CONSTRAINT FK_DeviceGroup_DeviceGroup FOREIGN KEY(ParentDeviceGroupId)
REFERENCES DeviceGroup (DeviceGroupId)

CREATE TABLE DeviceGroupMember(
	DeviceGroupId numeric(18, 0) NOT NULL,
	YukonPaoId numeric(18, 0) NOT NULL,
 CONSTRAINT PK_DeviceGroupMember PRIMARY KEY CLUSTERED (DeviceGroupId ASC,YukonPaoId ASC)
)

ALTER TABLE DeviceGroupMember  WITH CHECK ADD  CONSTRAINT FK_DeviceGroupMember_DEVICE FOREIGN KEY(YukonPaoId)
REFERENCES DEVICE (DEVICEID)
GO
ALTER TABLE DeviceGroupMember  WITH CHECK ADD  CONSTRAINT FK_DeviceGroupMember_DeviceGroup FOREIGN KEY(DeviceGroupId)
REFERENCES DeviceGroup (DeviceGroupId)

insert into DeviceGroup values (0,'',null,'Y','STATIC');
insert into DeviceGroup values (1,'Meters',0,'Y','STATIC');
insert into DeviceGroup values (2,'Billing',1,'Y','STATIC');
insert into DeviceGroup values (3,'Collection',1,'Y','STATIC');
insert into DeviceGroup values (4,'Alternate',1,'Y','STATIC');

/* we also need to drop CollectionGroup, TestCollectionGroup, BillingGroup from DeviceMeterGroup */

/*Removal/cleanup of Commercial Metering roles*/
/******SN - Deprecating of CICustomer.CommercialMetering, changed to Application.Trending properites /*****/
/** Jon, as we discussed, the CiCustomer.CommercialMeteringRole will go away, the below SQL protects the change
somewhat, we're still probalby missing something, but I think this is fairly acceptible **/
update yukongrouprole set roleid=-102, rolepropertyid=-10206 where roleid= -304 and rolepropertyid = -30403;
update yukongrouprole set roleid=-102, rolepropertyid=-10205 where roleid= -304 and rolepropertyid = -30402;
update yukongrouprole set roleid=-102, rolepropertyid=-10203 where roleid= -304 and rolepropertyid = -30401;
update yukongrouprole set roleid=-102, rolepropertyid=-10202 where roleid= -304 and rolepropertyid = -30400;

delete yukongrouprole where roleid = -304;
delete yukonuserrole where roleid = -304;
delete roleproperty where roleid= -304;
delete yukonrole where roleid= -304

delete yukongrouprole where rolepropertyid in(-20202, -20201, -20200)
delete yukonuserrole where rolepropertyid in(-20202, -20201, -20200)
delete yukonroleproperty where rolepropertyid in(-20202, -20201, -20200)
update yukonrole set ROleName = 'Metering', RoleDescription='Operator access to Metering' where roleid = -202

*/

/*PAObject constraint change*/


/*new CommStatistics from Jess*/

/*** THIS NEEDS A ORACLE COUNTERPART FOR GETUTCDATE ****/
insert into dynamicpaostatistics select distinct paobjectid, 'Lifetime', 0, 0, 0, 0, 0, 0, getutcdate(), getutcdate() from dynamicpaostatistics;

CREATE TABLE DynamicPAOStatisticsHistory (
	PAOBjectID numeric(18, 0) NOT NULL ,
	DateOffset numeric(18, 0) NOT NULL ,
	Requests numeric(18, 0) NOT NULL ,
	Completions numeric(18, 0) NOT NULL ,
	Attempts numeric(18, 0) NOT NULL ,
	CommErrors numeric(18, 0) NOT NULL ,
	ProtocolErrors numeric(18, 0) NOT NULL ,
	SystemErrors numeric(18, 0) NOT NULL ,
	CONSTRAINT PK_DYNAMICPAOSTATISTICSHISTORY PRIMARY KEY
	(
		PAOBjectID,
		DateOffset
	)  ON PRIMARY ,
	CONSTRAINT FK_DynPAOStHist_YkPA FOREIGN KEY 
	(
		PAOBjectID
	) REFERENCES YukonPAObject(PAObjectID)
) ON PRIMARY
GO

/*Thain changes?*/

/************** Jason - High bill complaint table - 3.5/head only ****************/
create table ProfilePeakResult (
	resultid numeric,
	deviceId numeric,
	resultFrom varchar(30),
	resultTo varchar(30),
	runDate varchar(30),
	peakDay varchar(30),
	usage  varchar(25),
	demand varchar(25),
	averageDailyUsage varchar(25),
	totalUsage varchar(25),
	resultType varchar(5),
	days numeric
)
/*********************************************************************************/