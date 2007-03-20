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

/********** Adding metering role properties ************/
insert into YukonRoleProperty values(-20201,-202,'Enable Billing','true','Allows access to billing');
insert into YukonRoleProperty values(-20202,-202,'Enable Trending','true','Allows access to Trending');
insert into YukonRoleProperty values(-20203,-202,'Enable Bulk Importer','true','Allows access to the Bulk Importer');
/********** end metering role properties ************/