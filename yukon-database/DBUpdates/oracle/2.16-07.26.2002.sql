/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.16', 'Ryan', '26-JUL-2002', 'Added the ImageID to the STATE table and GroupType to the StateGroup table. Created the StateImage table.');



/* New column type for TDC */
insert into columntype values (13, 'Tags');
insert into templatecolumns values( 1, 'Tags', 13, 6, 80 );
insert into templatecolumns values( 2, 'Tags', 13, 5, 80 );
insert into templatecolumns values( 3, 'Tags', 13, 5, 80 );


create or replace view DISPLAY2WAYDATA_VIEW (POINTID, POINTNAME , POINTTYPE , POINTSTATE , DEVICENAME, DEVICETYPE, DEVICECURRENTSTATE, DEVICEID, POINTVALUE, POINTQUALITY, POINTTIMESTAMP, UofM, TAGS) as
select POINTID, POINTNAME, POINTTYPE, SERVICEFLAG, YukonPAObject.PAOName, YukonPAObject.Type, YukonPAObject.Description, YukonPAObject.PAObjectID, '**DYNAMIC**', '**DYNAMIC**', '**DYNAMIC**', (select uomname from pointunit,unitmeasure where pointunit.pointid=point.pointid and pointunit.uomid=unitmeasure.uomid), '**DYNAMIC**'
from YukonPAObject, POINT
where YukonPAObject.PAObjectID = POINT.PAObjectID;

/* Add the StateImage table */
create table StateImage  (
   ImageID              NUMBER                           not null,
   StateImage           LONG RAW,
   constraint PK_STATEIMAGE primary key (ImageID)
);
insert into StateImage values( 0, null );


/* Alter the State table */
alter table State ADD ImageID NUMBER;
UPDATE State SET ImageID=0;
alter TABLE State MODIFY ImageID NOT NULL;

ALTER TABLE State ADD constraint FK_StIm_St foreign key (ImageID)
         references StateImage (ImageID);


/* Add a column to the StateGroup table and update the data */
alter table StateGroup ADD GroupType VARCHAR(20);
UPDATE StateGroup SET GroupType='Status';
alter TABLE StateGroup MODIFY GroupType NOT NULL;


UPDATE StateGroup SET GroupType = 'Analog' WHERE stategroupid = -1;
UPDATE StateGroup SET GroupType = 'Accumulator' WHERE stategroupid = -2;
UPDATE StateGroup SET GroupType = 'Calculated' WHERE stategroupid = -3;
UPDATE StateGroup SET GroupType = 'System' WHERE stategroupid = -5;
UPDATE StateGroup SET GroupType = 'System' WHERE stategroupid = 0;

