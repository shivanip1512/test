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
declare @areaname varchar(60);
declare @subid numeric;
declare @subdesc varchar(60);
declare @order numeric;
set @order = 1;

declare areaid_curs cursor for (select paobjectid, paoname from yukonpaobject where type = 'CCAREA');
declare subarea_curs cursor for (select paobjectid as subid, description from yukonpaobject where type = 'CCSUBBUS');

open areaid_curs;
fetch areaid_curs into @areaid, @areaname;

while (@@fetch_status = 0)
    begin
       insert into capcontrolarea values (@areaid, 0);
       set @order = 1;
       open subarea_curs;
       fetch subarea_curs into @subid, @subdesc;      
       while (@@fetch_status = 0)
            begin
              if (@areaname = @subdesc)
                begin
                insert into ccsubareaassignment values (@areaid, @subid, @order);
                set @order = @order + 1;
                end     
              fetch subarea_curs into @subid, @subdesc;      
            end
       close subarea_curs;
    fetch areaid_curs into @areaid, @areaname;
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


/********** Adding metering role properties ************/
insert into YukonRoleProperty values(-20201,-202,'Enable Billing','true','Allows access to billing');
insert into YukonRoleProperty values(-20202,-202,'Enable Trending','true','Allows access to Trending');
insert into YukonRoleProperty values(-20203,-202,'Enable Bulk Importer','true','Allows access to the Bulk Importer');
/********** end metering role properties ************/