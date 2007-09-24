/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!*/

/*New issue, check YUK-4334 for reference */
NEED TO CHECK TO SEE WHETHER THE USERPAOWNER TO USERPAOPERMISSION TRANSITION IN THE 3.4_0.sql file
is working correctly to move the default route group mapping for STARS energy companies.

If a default route is not loading correctly in STARS, then run the following query:
insert into UserPaoPermission (UserPaoPermissionID, UserID, PaoID, Permission) select 0, ec.userID, macro.OwnerID, 'allow LM visibility'
from EnergyCompany ec, LMGroupExpressCom exc, GenericMacro macro 
where macro.MacroType = 'GROUP' and macro.ChildID = exc.LMGroupID and exc.SerialNumber = '0' and ec.EnergyCompanyID = 0;

If an ID is returned, then the mapping failed and the default route is not correctly saved.

There are several questions we need to answer on this issue, so this can stay open for awhile.  
/*I'm marking this in here for reference until we figure out the best way to deal with this.  Jon*/

/********************** (HEAD only) - nmeverden 9/05/07 **********************/
update YukonRoleProperty SET Description='Set the default authentication type to use {PLAIN,HASH_SHA,RADIUS,AD,LDAP,NONE}' where RolePropertyID = -1307

insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1308,-4,'LDAP DN','dc=example,dc=com','LDAP Distinguished Name')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1309,-4,'LDAP User Suffix','ou=users','LDAP User Suffix')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1310,-4,'LDAP User Prefix','uid=','LDAP User Prefix')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1311,-4,'LDAP Server Address','127.0.0.1','LDAP Server Address')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1312,-4,'LDAP Server Port','389','LDAP Server Port')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1313,-4,'LDAP Server Timeout','30','LDAP Server Timeout (in seconds)')

insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1314,-4,'Active Directory Server Address','127.0.0.1','Active Directory Server Address')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1315,-4,'Active Directory Server Port','389','Active Directory Server Port')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1316,-4,'Active Directory Server Timeout','30','Active Directory Server Timeout (in seconds)')
insert into YukonRoleProperty (RolePropertyID,RoleID,KeyName,DefaultValue,Description) values(-1317,-4,'Active Directory NT Domain Name','(none)','Active Directory NT DOMAIN NAME')

insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-50,-1,-4,-1308,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-51,-1,-4,-1309,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-52,-1,-4,-1310,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-53,-1,-4,-1311,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-54,-1,-4,-1312,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-55,-1,-4,-1313,'(none)')

insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-56,-1,-4,-1314,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-57,-1,-4,-1315,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-58,-1,-4,-1316,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-59,-1,-4,-1317,'(none)')
insert into YukonGroupRole (GroupRoleID,GroupID,RoleID,RolePropertyID,Value) values(-92,-1,-4,-1307,'(none)')
/*****************************************************************************/


/** CHANGE CREATION SCRIPTS ONLY FOR THIS IN HEAD AND 3.5  - SN 20070918 
These insert statements already exist, but the formatType values are being changed, removing the "turtle" text */
insert into billingfileformats values( 13, 'NISC',1);
insert into billingfileformats values( -19, ' NISC No Limit kWh ',1);
/*** */


/** Head only capcontrol changes  -Thain 20070921 */
alter table ccfeederbanklist alter column controlorder float;
alter table ccfeederbanklist alter column closeorder float;
alter table ccfeederbanklist alter column triporder float;

/** Head only TOU Widget db updates */
USE [sw_cart40]
GO
/****** Object:  Table [dbo].[TouAttributeMapping]    Script Date: 09/24/2007 10:54:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[TouAttributeMapping](
	[touId] [int] IDENTITY(1,1) NOT NULL,
	[displayName] [varchar](50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[peakAttribute] [varchar](50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[usageAttribute] [varchar](50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [PK_TouAttributeMapping] PRIMARY KEY CLUSTERED 
(
	[touId] ASC
)WITH (IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF

INSERT INTO [sw_cart40].[dbo].[TouAttributeMapping]
           ([displayName], [peakAttribute], [usageAttribute])
     VALUES ('Normal', 'PEAK_DEMAND', 'USAGE');

INSERT INTO [sw_cart40].[dbo].[TouAttributeMapping]
           ([displayName], [peakAttribute], [usageAttribute])
     VALUES ('B', 'TOU_RATE_B_PEAK', 'TOU_RATE_B_USAGE');

INSERT INTO [sw_cart40].[dbo].[TouAttributeMapping]
           ([displayName], [peakAttribute], [usageAttribute])
     VALUES ('C', 'TOU_RATE_C_PEAK', 'TOU_RATE_C_USAGE');

INSERT INTO [sw_cart40].[dbo].[TouAttributeMapping]
           ([displayName], [peakAttribute], [usageAttribute])
     VALUES ('D', 'TOU_RATE_D_PEAK', 'TOU_RATE_D_USAGE');

insert into YukonRoleProperty values(-20204,-202,'Enable Tou','true','Allows access to Tou(Time of use) data');  
insert into YukonRoleProperty values(-20205,-202,'Enable Device Group','true','Allows access to change device groups for a device');  

