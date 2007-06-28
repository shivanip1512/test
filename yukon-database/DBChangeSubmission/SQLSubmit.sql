/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!

/*3.5 CHANGES IN BY FRI. MORNING PLEASE PLEASE PLEASE*/

/*CURRENT LIST*/
/*Tom's DeviceMeterGroup changes?*/


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