/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.33', 'Ryan', '22-AUG-2002', 'Added StateImage, ThermoStat gear and YukonImage');


/* Add a new BillingFile format */
insert into billingfileformats values( 11, 'MV_90 DATA Import');


/* Change the StateImage table to the YukonImage table */
alter table state drop constraint FK_StIm_St;
drop table stateimage;
go
create table YukonImage (
ImageID              numeric              not null,
ImageCategory        varchar(20)          null,
ImageName            varchar(80)          null,
ImageValue           image                null,
constraint PK_YUKONIMAGE primary key  (ImageID)
)
go
insert into YukonImage values( 0, '(none)', '(none)', null );
go
ALTER TABLE state ADD constraint FK_YkIm_St foreign key (ImageID) references YukonImage (ImageID);
go


/* Remove the RawPointHistory display from all TDC's */
DELETE FROM DisplayColumns WHERE displaynum = 3;
go
DELETE FROM Display WHERE displaynum = 3;
go


/* Add the LMThermoStatGear table, this table may already exist in some newer DB's */
create table LMThermoStatGear (
GearID               numeric              not null,
Settings             varchar(10)          not null,
MinValue             numeric              not null,
MaxValue             numeric              not null,
ValueB               numeric              not null,
ValueD               numeric              not null,
ValueF               numeric              not null,
Random               numeric              not null,
ValueTa              numeric              not null,
ValueTb              numeric              not null,
ValueTc              numeric              not null,
ValueTd              numeric              not null,
ValueTe              numeric              not null,
ValueTf              numeric              not null,
constraint PK_LMTHERMOSTATGEAR primary key  (GearID)
)
go
alter table LMThermoStatGear
   add constraint FK_ThrmStG_PrDiGe foreign key (GearID)
      references LMProgramDirectGear (GearID)
go


/* Add a couple columns to the DynamicCCSubstationBus table */
alter TABLE DynamicCCSubstationBus add PowerFactorValue FLOAT not null DEFAULT 0.0;
alter TABLE DynamicCCSubstationBus add KvarSolution FLOAT not null DEFAULT 0.0;
