/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.33', 'Ryan', '22-AUG-2002', 'Added StateImage, ThermoStat gear and YukonImage');


/* Add a new BillingFile format */
insert into billingfileformats values( 11, 'MV_90 DATA Import');


/* Change the StateImage table to the YukonImage table */
alter table state drop constraint FK_StIm_St;
drop table stateimage;
/
create table YukonImage  (
   ImageID              NUMBER                           not null,
   ImageCategory        VARCHAR2(20),
   ImageName            VARCHAR2(80),
   ImageValue           LONG RAW,
   constraint PK_YUKONIMAGE primary key (ImageID)
)
/
insert into YukonImage values( 0, '(none)', '(none)', null );
/
ALTER TABLE state ADD constraint FK_YkIm_St foreign key (ImageID) references YukonImage (ImageID);


/* Remove the RawPointHistory display from all TDC's */
DELETE FROM DisplayColumns WHERE displaynum = 3;
/
DELETE FROM Display WHERE displaynum = 3;
/


/* Add the LMThermoStatGear table, this table may already exist in some newer DB's */
create table LMThermoStatGear  (
   GearID               NUMBER                           not null,
   Settings             VARCHAR2(10)                     not null,
   MinValue             NUMBER                           not null,
   MaxValue             NUMBER                           not null,
   ValueB               NUMBER                           not null,
   ValueD               NUMBER                           not null,
   ValueF               NUMBER                           not null,
   Random               NUMBER                           not null,
   ValueTa              NUMBER                           not null,
   ValueTb              NUMBER                           not null,
   ValueTc              NUMBER                           not null,
   ValueTd              NUMBER                           not null,
   ValueTe              NUMBER                           not null,
   ValueTf              NUMBER                           not null,
   constraint PK_LMTHERMOSTATGEAR primary key (GearID),
   constraint FK_ThrmStG_PrDiGe foreign key (GearID)
         references LMProgramDirectGear (GearID)
)
/


/* Add a couple columns to the DynamicCCSubstationBus table */
alter table DynamicCCSubstationBus add PowerFactorValue FLOAT;
update DynamicCCSubstationBus set PowerFactorValue = 0.0;
alter TABLE DynamicCCSubstationBus MODIFY PowerFactorValue NOT NULL;
/
alter table DynamicCCSubstationBus add KvarSolution FLOAT;
update DynamicCCSubstationBus set KvarSolution = 0.0;
alter TABLE DynamicCCSubstationBus MODIFY KvarSolution NOT NULL;
/