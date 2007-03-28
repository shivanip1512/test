/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/*****************************************************************************/
/* JULIE: This is to create CCAREA type inserts into the yukonpaobject table */
/*****************************************************************************/
declare @areaname varchar(60);
declare @paoid numeric;
set @paoid = (select max(paobjectid) + 1 from yukonpaobject);
declare areaname_curs cursor for (select distinct(description) as areaname from yukonpaobject where type = 'ccsubbus');

open areaname_curs;

fetch areaname_curs into @areaname;

while (@@fetch_status = 0)
  begin 
     insert into yukonpaobject(paobjectid, category, paoclass, paoname, type, description, disableflag, paostatistics)
                  select @paoid,
                   'CAPCONTROL', 
                   'CAPCONTROL',
                   @areaname, 
                   'CCAREA', 
                   '(none)',
                   'N',
                   '-----'
     fetch areaname_curs into @areaname;
     set @paoid = @paoid + 1;

  end

close areaname_curs;
deallocate areaname_curs;

/*****************************************************************************/
/* END JULIE                                                                 */
/*****************************************************************************/

/*****************************************************************************/
/* JULIE: This is to update the capcontrolarea table and to assign the       */
/* correct subs to the ccsubareaassignment table                             */
/*****************************************************************************/
declare @areaid numeric;
declare @areaname1 varchar(60);
declare @subid numeric;
declare @subdesc varchar(60);
declare @order numeric;
set @order = 1;

declare areaid_curs cursor for (select paobjectid, paoname from yukonpaobject where type = 'CCAREA');
declare subarea_curs cursor for (select paobjectid as subid, description from yukonpaobject where type = 'CCSUBBUS');

open areaid_curs;
fetch areaid_curs into @areaid, @areaname1;

while (@@fetch_status = 0)
    begin
       insert into capcontrolarea values (@areaid, 0);
       set @order = 1;
       open subarea_curs;
       fetch subarea_curs into @subid, @subdesc;      
       while (@@fetch_status = 0)
            begin
              if (@areaname1 = @subdesc)
                begin
                insert into ccsubareaassignment values (@areaid, @subid, @order);
                set @order = @order + 1;
                end     
              fetch subarea_curs into @subid, @subdesc;      
            end
       close subarea_curs;
    fetch areaid_curs into @areaid, @areaname1;
    end

close areaid_curs;
deallocate areaid_curs;
deallocate subarea_curs;
/*****************************************************************************/
/* END JULIE                                                                 */
/*****************************************************************************/

/*****************************************************************************/
/* JULIE: This is to update the ccfeederbanklist table and to assign the     */
/* additional close and trip sequences to the capbanks                       */
/*****************************************************************************/
alter table ccfeederbanklist add closeOrder numeric;
go
update ccfeederbanklist set closeOrder = ControlOrder;
go
alter table ccfeederbanklist  alter column closeOrder numeric not null;
go 
alter table ccfeederbanklist add tripOrder numeric;
go
declare @tripOrder numeric;
declare @devid numeric;
declare @feedid numeric;
declare @maxclose numeric;
declare deviceid_curs cursor for (select deviceid from ccfeederbanklist);
open deviceid_curs;
fetch deviceid_curs into @devid;

while (@@fetch_status = 0)
begin
    set @feedid = (select feederid from ccfeederbanklist where deviceid = @devid);
    set @maxclose = (select max(closeOrder)as maxclose from ccfeederbanklist where feederid = @feedid group by feederid);
    set @tripOrder = (select (@maxclose - fb.controlorder + 1) from ccfeederbanklist fb where fb.deviceid = @devid);
    update ccfeederbanklist set triporder = @tripOrder where deviceid = @devid; 
            
    fetch deviceid_curs into @devid;
end
close deviceid_curs;
deallocate deviceid_curs;
go
alter table ccfeederbanklist  alter column tripOrder numeric not null;
/*****************************************************************************/
/* END JULIE                                                                 */
/*****************************************************************************/

/************* Adding data integration option to strategy ********************/
/**TEMPORARILY COMMENTING THIS OUT..... for weekly build client/server compatibility*/
--alter table capcontrolstrategy add integrateflag char(1);
--go
--update capcontrolstrategy set integrateflag = 'N';
--go
--alter table capcontrolstrategy  alter column integrateflag char(1) not null;
--go 
--alter table capcontrolstrategy add integrateperiod numeric;
--go
--update capcontrolstrategy set integrateperiod = 0;
--go
--alter table capcontrolstrategy  alter column integrateperiod numeric not null;
--go 
/*****************************************************************************/

/************* Adding dynamic two-way cbc info table for Progress FL *********/
create table dynamiccctwowaycbc
(     
      deviceid				numeric		not null,
      recloseBlocked		char(1)		not null,
      controlMode			char(1)		not null,  
      autoVoltControl		char(1)     not null,
      lastControl			numeric     not null,     
      condition				numeric     not null,  
      opFailedNeutralCurrent char(1)	not null,
      neutralCurrentFault    char(1)	not null,
      badRelay				char(1)     not null,
      dailyMaxOps			char(1)     not null,
      voltageDeltaAbnormal  char(1)		not null,
      tempAlarm				char(1)		not null,
      dstActive				char(1)     not null,
      neutralLockout		char(1)     not null,
      ignoredIndicator		char(1)		not null, 
      voltage               float		not null,
      highvoltage			float		not null,
      lowvoltage			float		not null,
      deltavoltage			float		not null,
      analogInputOne		numeric     not null,  
      temp					float		not null,
      rssi					numeric		not null,  
      ignoredReason			numeric		not null,  
      totalOpCount			numeric     not null,
      uvOpCount				numeric     not null,
      ovOpCount				numeric     not null,
      ovUvCountResetDate    datetime    not null, 
      uvSetPoint			numeric		not null,  
      ovSetPoint			numeric		not null,  
      ovuvTrackTime			numeric		not null,  
      lastOvUvDateTime      datetime	not null,   
      neutralCurrentSensor  numeric		not null, 
      neutralCurrentAlarmSetPoint  numeric      not null,  
      ipAddress				numeric     not null, 
      udpPort               numeric     not null  
)
/*****************************************************************************/
/************* Adding capbank additional info table for Progress FL *********/
insert into YukonRoleProperty values(-10814, -108,'Show Cap Bank Add Info', 'false', 'Show Cap Bank Addititional Info tab');
go

create table capbankadditional
(
	  deviceid            numeric       not null,
      maintenanceAreaId numeric			not null,  
      poleNumber        numeric         not null, 
      driveDirections   varchar(120)	not null, 
      latitude          float           not null,
      longitude         float           not null,
      capBankConfig     varchar(10)		not null, 
      commMedium        varchar(20)		not null, 
      commStrength      numeric         not null, 
      extAntenna        char(1)         not null, 
      antennaType       varchar(10)		not null,
      lastMaintVisit    datetime		not null,
      lastInspVisit     datetime		not null,
      opCountResetDate  datetime		not null,
      potentialTransformer    varchar(10) not null, 
      maintenanceReqPend      char(1)   not null,   
      otherComments     varchar(150)    not null,
      opTeamComments    varchar(150)    not null,
      cbcBattInstallDate      datetime  not null
)

declare @capid numeric
declare capid_curs cursor for (select deviceid as capid from capbank);
open capid_curs;

fetch capid_curs into @capid;
while (@@fetch_status = 0)
  begin
     insert into capbankadditional
                  select @capid,
                   0,
                   0,
                   '(none)',
                   0.0,
                   0.0,
                   '(none)',
                   '(none)',
				   0,
				  'N',
				  '(none)',
				  '1/1/1900',
				  '1/1/1900',
				  '1/1/1900',
				  '(none)',
				  'N',
				  '(none)',
				  '(none)',
				  '1/1/1900';  		
 	fetch capid_curs into @capid;
  end

close capid_curs;
deallocate capid_curs;
/*****************************************************************************/

/********** Adding metering role properties ************/
insert into YukonRoleProperty values(-20201,-202,'Enable Billing','true','Allows access to billing');
insert into YukonRoleProperty values(-20202,-202,'Enable Trending','true','Allows access to Trending');
insert into YukonRoleProperty values(-20203,-202,'Enable Bulk Importer','true','Allows access to the Bulk Importer');
/********** end metering role properties ************/


/****************** ORACLE **************************/
declare
v_paoid number;
v_areaname varchar2(60);
select max(paobjectid) + 1 into v_paoid from yukonpaobject;
cursor c_areaname  is select distinct(description) as areaname from yukonpaobject where type = 'ccsubbus';
begin
    open c_areaname;
   while(c_areaname%notfound)
      loop
          fetch c_areaname into v_areaname;
          insert into yukonpaobject(paobjectid, category, paoclass, paoname, type, description, disableflag, paostatistics)
                  select v_paoid,
                   'CAPCONTROL',
                   'CAPCONTROL',
                   v_areaname,
                   'CCAREA',
                   '(none)',
                   'N',
                   '-----' from yukonpaobject;
            v_paoid := v_paoid + 1;
     end loop;
close c_areaname;
end;
/****************** END ORACLE **************************/

