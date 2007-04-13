/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/****************** ORACLE **************************/
declare
v_paoid number;
v_areaname varchar2(60);
select max(paobjectid) + 1 into v_paoid from yukonpaobject;
cursor c_areaname is select distinct(description) as areaname from yukonpaobject where type = 'ccsubbus';
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


declare
v_areaid number;
v_areaname1 varchar2(60);
v_subid number;
v_subdesc varchar2(60);
v_order number := 1;

cursor c_areaid is (select paobjectid, paoname from yukonpaobject where type = 'CCAREA');
cursor c_subarea is (select paobjectid as subid, description from yukonpaobject where type = 'CCSUBBUS');

begin
    open c_areaid;
    while(c_areaid%notfound)
       loop
			fetch c_areaid into v_areaid, v_areaname1;
			insert into capcontrolarea values (v_areaid, 0);
		v_order := 1;
             
			open c_subarea;
			while (c_subarea%notfound)
				loop	
					fetch c_subarea into v_subid, v_subdesc;
					if (v_areaname1 = v_subdesc) then 
					
						insert into ccsubareaassignment values (v_areaid, v_subid, v_order);
						v_order := v_order + 1;
					end if;
				end loop;
			close c_subarea;
		end loop;
	close c_areaid;
end;
	
		

/**** JON!!!! THIS DON't WORKY WORKY!!! ********/
alter table ccfeederbanklist add closeOrder number;
update ccfeederbanklist set closeOrder = ControlOrder;
alter table ccfeederbanklist  modify  closeOrder number not null;
alter table ccfeederbanklist add tripOrder number;

declare 
v_tripOrder number;
v_devid number;
v_feedid number;
v_maxclose number;
cursor c_deviceid is (select deviceid from ccfeederbanklist);

begin
    open c_deviceid;
    while(c_deviceid%notfound)
      loop
		fetch c_deviceid into v_devid;
		select feederid into v_feedid from ccfeederbanklist where deviceid = v_devid;
        select max(closeOrder) into v_maxclose from ccfeederbanklist where feederid = v_feedid group by feederid;
        select (v_maxclose - fb.controlorder + 1) into v_tripOrder from ccfeederbanklist fb where fb.deviceid = v_devid;
        update ccfeederbanklist set triporder = v_tripOrder where deviceid = v_devid;		
	  end loop;
	close c_deviceid;
end;

alter table ccfeederbanklist modify tripOrder number not null;
/**** END JON!!!! THIS DON't WORKY WORKY!!! ********/
/****************** END ORACLE **************************/


/** commercial curtailment **/

alter table ccurtprogram add  LastIdentifier numeric;
update ccurtprogram set LastIdentifier = 0;
alter table ccurtprogram alter column LastIdentifier numeric not null;

alter table ccurtprogram add  IdentifierPrefix varchar(32);
update ccurtprogram set IdentifierPrefix = 'PROG-';
alter table ccurtprogram alter column IdentifierPrefix varchar(32) not null;


/** end **/


/***********************************************
  These alters need to be changed in the creation script also 
  **************************************************/
alter table dcitemtype rename column maxlengh to MaxValue;
alter table dcitemtype rename column minlength to MinValue;

insert into YukonRoleProperty values(-1020,-1,'stars_activation','false','Specifies whether STARS functionality should be allowed in this web deployment.');
/*start block*/
if 1 < (select count(*) from ApplianceCategory)
begin
   insert into YukonGroupRole values (-20, -1, -1, -1020, 'true');
end;
/*end block*/

insert into YukonRoleProperty values (-20012,-200,'LM User Assignment','false','Controls visibility of LM objects for 3-tier and direct control, based off assignment of users.');


/** new ISOC tables **/
/*==============================================================*/
/* Table: CCurtAcctEvent                                        */
/*==============================================================*/
create table CCurtAcctEvent (
   CCurtAcctEventId     numeric              not null,
   CCurtProgramID       numeric              null,
   Duration             numeric              not null,
   Reason               varchar(255)         not null,
   StartTime            datetime             not null,
   Identifier           numeric              not null
)
go


alter table CCurtAcctEvent
   add constraint PK_foo2 primary key  (CCurtAcctEventId)
go


/*==============================================================*/
/* Table: CCurtAcctEventParticipant                             */
/*==============================================================*/
create table CCurtAcctEventParticipant (
   CCurtAcctEventParticipantId  numeric        not null,
   CustomerID                   numeric        not null,
   CCurtAcctEventId             numeric        not null
)
go


alter table CCurtAcctEventParticipant
   add constraint PK_foo1 primary key  (CCurtAcctEventParticipantId)
go


alter table CCurtAcctEventParticipant
   add constraint FK_foo3 foreign key (CCurtAcctEventId)
      references CCurtAcctEvent (CCurtAcctEventId)
go


alter table CCurtAcctEventParticipant
   add constraint FK_foo4 foreign key (CustomerID)
      references CICustomerBase (CustomerID)
go


alter table CCurtAcctEvent
   add constraint FK_foo5 foreign key (CCurtProgramID)
      references CCurtProgram (CCurtProgramID)
go

/** end new ISOC tables **/