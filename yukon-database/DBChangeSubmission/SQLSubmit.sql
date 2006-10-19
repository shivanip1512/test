/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/


/***********************************************************************************/

alter table capcontrolstrategy add peakVARlag float;

go

update capcontrolstrategy set peakVARlag = 0;

go

alter table capcontrolstrategy alter column peakVARlag float not null;

go

alter table capcontrolstrategy add peakVARlead float;

go

update capcontrolstrategy set peakVARlead = 0;

go

alter table capcontrolstrategy alter column peakVARlead float not null;

go

alter table capcontrolstrategy add offPkVARlag float;

go

update capcontrolstrategy set offPkVARlag = 0;

go

alter table capcontrolstrategy alter column offPkVARlag float not null;

go

alter table capcontrolstrategy add offPkVARlead float;

go

update capcontrolstrategy set offPkVARlead = 0;

go

alter table capcontrolstrategy alter column offPkVARlead float not null;

go

/***********************************************************************************/

alter table capcontrolstrategy add peakPFSetPoint float;

go

update capcontrolstrategy set peakPFSetPoint = 100;

go

alter table capcontrolstrategy alter column peakPFSetPoint float not null;

go

alter table capcontrolstrategy add offPkPFSetPoint float;

go

update capcontrolstrategy set offPkPFSetPoint = 100;

go

alter table capcontrolstrategy alter column offPkPFSetPoint float not null;

go

/***********************************************************************************/

delete from YukonRoleProperty where RolePropertyID = -20010;

insert into YukonRoleProperty values(-1019,-1,'batched_switch_command_timer','auto','Specifies whether the STARS application should automatically process batched switch commands');

/***********************************************************************************/