/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/*3.5 CHANGES IN BY FRI. MORNING PLEASE PLEASE PLEASE*/

/*CURRENT LIST*/
/*Tom's DeviceMeterGroup changes?*/


/*Removal/cleanup of Commercial Metering roles*/


/*PAObject constraint change*/


/*new CommStatistics from Jess*/


/*Thain changes?*/

/************** Jason - High bill complaint table - 3.5/head only ****************/
create table ProfilePeakResult (
	resultid numeric,
	deviceId numeric,
	resultFrom varchar(30),
	resultTo varchar(30),
	runDate varchar(30),
	peakDay varchar(30),
	usage  varchar(25),
	demand varchar(25),
	averageDailyUsage varchar(25),
	totalUsage varchar(25),
	resultType varchar(5),
	days numeric
)
/*********************************************************************************/