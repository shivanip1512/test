/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/****************** ORACLE **************************/
declare
v_paoid number(6);
v_areaname varchar2(60);
cursor c_areaname is select distinct description  as areaname from yukonpaobject where type = 'CCSUBBUS';
begin
select max(paobjectid) into v_paoid from yukonpaobject;
v_paoid := v_paoid + 1;
open c_areaname;
fetch c_areaname into v_areaname;
          
   while(v_areaname%found)
      loop
          --fetch c_areaname into v_areaname;
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
fetch c_areaname into v_areaname;
         
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

/*************eliot*****************/
/*********role properties for oneline config****/
/*****start*************************************/
INSERT INTO YukonRole
VALUES(-1000, 'Substation','CBC Oneline', 'Oneline View Substation Config')

INSERT INTO YukonRole
VALUES(-1001, 'Feeder','CBC Oneline', 'Oneline View Feeder Config')

INSERT INTO YukonRole
VALUES(-1002, 'Cap Bank','CBC Oneline', 'Oneline View Cap Bank Config')

go



INSERT INTO YukonRoleProperty
VALUES(-100000, -1000, 'Target', 'true', 'is target stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100001, -1000, 'Var Load', 'true', 'is target stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100002, -1000, 'Estimated Var Load', 'true', 'is Var Load stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100003, -1000, 'Power Factor', 'true', 'is Power Factor stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100004, -1000, 'Estimated Power Factor', 'true', 'is Estimated Power Factor stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100005, -1000, 'Watts', 'true', 'are Watts stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100006, -1000, 'Volts', 'true', 'are Volts stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100007, -1000, 'Daily Operation Count', 'true', 'is Daily Operation Count stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100008, -1000, 'Max Operation Count', 'true', 'is Max Operation Count stat displayed')

go


INSERT INTO YukonRoleProperty
VALUES(-100100, -1001, 'KVAR', 'true', 'is KVAR stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100101, -1001, 'Power Factor', 'true', 'is Power Factor stat displayed')


INSERT INTO YukonRoleProperty
VALUES(-100102, -1001, 'WattsVolts', 'true', 'are Watts and Volts stat displayed')


INSERT INTO YukonRoleProperty
VALUES(-100103, -1001, 'Operation Count', 'true', 'is Daily Operation Count stat displayed')

go

INSERT INTO YukonRoleProperty
VALUES(-100200, -1002, 'Op Count', 'true', 'is KVAR stat displayed')

INSERT INTO YukonRoleProperty
VALUES(-100201, -1002, 'Bank Size', 'true', 'is Bank Size stat displayed')

go
/************end********************************/
