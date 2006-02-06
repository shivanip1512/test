/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

create   index Indx_CntNotif_CntId on ContactNotification (
ContactID
)
go

create   index Indx_Cstmr_PcId on Customer (
PrimaryContactID
)
go

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.3', 'Ryan', '06-Jan-2006', 'Manual version insert done', 0 );
