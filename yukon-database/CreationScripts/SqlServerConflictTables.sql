/* These files are being used until we can find a 
 * better way to use incompatable queries in powerdesigner. */
/********************  Sql Server 2005 **************************/
/*==============================================================*/
/* View: AreaSubBusFeeder_View                                  */
/*==============================================================*/
CREATE VIEW AreaSubBusFeeder_View
AS 
SELECT YPA.PAOName AS Region, YPS.PAOName AS Substation, YP.PAOName AS Subbus, YPF.PAOName AS Feeder, 
       STRAT.StrategyName, STRAT.ControlMethod, SA.SeasonName, 
       cast(DOS.SeasonStartMonth AS VARCHAR(2)) + '/' + cast(DOS.SeasonStartDay AS VARCHAR(2)) + '/' + 
       cast(datepart(year,getdate()) AS VARCHAR(4)) AS SeasonStartDate, 
       cast(DOS.SeasonEndMonth AS VARCHAR(2)) + '/'  + cast(DOS.SeasonEndDay AS VARCHAR(2)) + '/' + 
       cast(datepart(year,getdate()) AS VARCHAR(4)) AS SeasonEndDate
FROM CCSeasonStrategyAssignment SA
JOIN YukonPAObject YPX ON (YPX.PAObjectId = SA.PAObjectId OR YPX.PAObjectId = SA.PAObjectId OR YPX.PAObjectId = SA.PAObjectId)
JOIN YukonPAObject YP ON YP.PAObjectId = SA.PAObjectId
JOIN CCFeederSubAssignment FS ON FS.SubstationBusId = YP.PAObjectId
JOIN YukonPAObject YPF ON YPF.PAObjectId = FS.FeederId
JOIN CCSubstationSubbusList SS ON SS.SubstationBusId = YP.PAObjectId
JOIN CCSubAreaAssignment SAA ON SAA.SubstationBusId = SS.SubstationId
JOIN YukonPAObject YPS ON SS.SubstationId = YPS.PAObjectId
JOIN YukonPAObject YPA ON YPA.PAObjectId = SAA.AreaId
JOIN CapControlStrategy STRAT ON STRAT.StrategyId = SA.StrategyId
JOIN DateOfSeason DOS ON SA.SeasonName = DOS.SeasonName 
AND SA.SeasonScheduleId = DOS.SeasonScheduleId
AND (cast(SeasonStartMonth AS VARCHAR(2)) + '/' + 
          cast(SeasonStartDay AS VARCHAR(2)) + '/' + 
          cast(datepart(year,getdate()) AS VARCHAR(4))) <= getdate()
AND (cast(SeasonEndMonth AS VARCHAR(2)) + '/' + 
          cast(SeasonEndDay AS VARCHAR(2)) + '/' + 
          cast(datepart(year,getdate()) AS VARCHAR(4))) > getdate();

