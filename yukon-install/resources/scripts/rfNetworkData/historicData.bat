IF "%~1"=="" GOTO endparse
IF "%~2"=="" GOTO endparse
IF "%~2"=="" GOTO endparse
del .\query_log.txt 

mkdir Results

set hh=%time:~-11,2%
set /a hh=%hh%+100
set hh=%hh:~1%
set runtimestamp=%date:~10,4%%date:~4,2%%date:~7,2%%hh%%time:~3,2%%time:~6,2%
set fileName=historicData_%runtimestamp%
set resultDir=.\Results\%fileName%%

mkdir %resultDir%
		
SET currentDir=%CD%

echo =======================================  >> .\query_log.txt 
echo Query 1 GW subitem 1 >> .\query_log.txt 
bcp "select		g.gwName,		gr.gwRadioMACAddr,		g.gwSN,		g.gwRouteColor,		g.controlRate		from 		ekadb.ekadb.gateway g		left join ekadb.ekadb.GW_RADIO gr on (gr.gwID = g.gwID)		where		gr.gwRadioMACAddr <> 'FFFFFFFFFFFF'" queryout .\Query_GW_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_GW_1_DataWithoutHeaders.csv %resultDir%\GW_DataWithoutHeaders.csv 
del Query_GW_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 2 NodeSWVer subitem 1 >> .\query_log.txt 
bcp "select n.nodeSWRev, count(*) from ekadb.ekadb.node n group by n.nodeSWRev" queryout .\Query_NodeSWVer_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_NodeSWVer_1_DataWithoutHeaders.csv %resultDir%\NodeSWVer_DataWithoutHeaders.csv 
del Query_NodeSWVer_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 3 NetworkStatus subitem 1 >> .\query_log.txt 
bcp "select 		primaryRoutes.nodeAddress 		, nw.nodeSWRev 		, primaryRoutes.nextAddr 		, primaryRoutes.gwAddr 		, nd.etxBand 		, primaryRoutes.routeHops 		, primaryRoutes.routeCost 		, nd.currentRateID 		, nd.currentPowerID 		FROM 		ekadb.ekadb.NEIGHBOR_DATA_POINT nd 		,ekadb.ekadb.NODE nw 		, ( 		SELECT 		right(n.nodeAddress, 12) as nodeAddress 		, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr 		, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr 		, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops 		, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost 		, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID 		FROM 		ekadb.ekadb.ROUTE_DATA_POINT rd 		left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID 		WHERE 		rd.entityID != rd.gwID 		AND 		rd.routeDataTimeStamp  between 		(CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-1)) AS BIGINT))*1000 and 		(CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000 		AND 		(rd.routeFlagCodeID & 1) = 1 		GROUP BY n.nodeAddress 		) as primaryRoutes 		where 		nd.entityID = primaryRoutes.entityID 		and nd.neighborAddress = primaryRoutes.nextAddr 		and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12) 		and nd.neighborDataTimestamp  between 		(CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 and 		(CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_NetworkStatus_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_NetworkStatus_1_DataWithoutHeaders.csv %resultDir%\NetworkStatus_DataWithoutHeaders.csv 
del Query_NetworkStatus_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 3 NetworkStatus subitem 2 >> .\query_log.txt 
bcp "select           primaryRoutes.nodeAddress           , nw.nodeSWRev           , primaryRoutes.nextAddr           , primaryRoutes.gwAddr           , nd.etxBand           , primaryRoutes.routeHops           , primaryRoutes.routeCost           , nd.currentRateID           , nd.currentPowerID           FROM           ekadb.ekadb.NEIGHBOR_DATA_POINT nd           ,ekadb.ekadb.NODE nw           , (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				 GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID			and nd.neighborAddress = primaryRoutes.nextAddr			and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)			and nd.neighborDataTimestamp  between			(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_NetworkStatus_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y NetworkStatus_DataWithoutHeaders.csv+Query_NetworkStatus_2_DataWithoutHeaders.csv %resultDir%\NetworkStatus_DataWithoutHeaders.csv 
del Query_NetworkStatus_2_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 3 NetworkStatus subitem 3 >> .\query_log.txt 
bcp "select           primaryRoutes.nodeAddress           , nw.nodeSWRev           , primaryRoutes.nextAddr           , primaryRoutes.gwAddr           , nd.etxBand           , primaryRoutes.routeHops           , primaryRoutes.routeCost           , nd.currentRateID           , nd.currentPowerID           FROM           ekadb.ekadb.NEIGHBOR_DATA_POINT nd           ,ekadb.ekadb.NODE nw           , (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1			 GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID			and nd.neighborAddress = primaryRoutes.nextAddr			and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)			and nd.neighborDataTimestamp  between			(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 and			(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_NetworkStatus_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y NetworkStatus_DataWithoutHeaders.csv+Query_NetworkStatus_3_DataWithoutHeaders.csv %resultDir%\NetworkStatus_DataWithoutHeaders.csv 
del Query_NetworkStatus_3_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 5 NodeStatus subitem 1 >> .\query_log.txt 
bcp "select		nodeAddress, nodeID, gwID, gwName, nodeSWRev,		DATEADD(s, timestamp/1000, '1970-01-01') statusTS, currStatus		from (		select		row_number() over (partition by n.nodeID order by		na.isPrimary desc, r.routeDataTimeStamp desc, l.nodeCommStatusTimestamp desc) as rowNumber		, right(n.nodeAddress, 12) nodeAddress		, n.nodeID, na.gwID, g.gwName, n.nodeSWRev		, l.nodeCommStatusTimestamp as timeStamp		, l.nodeCommStatusCodeID as currStatusCode		, c.nodeCommStatusString as currStatus		from		ekadb.ekadb.NODE n		left join		ekadb.ekadb.NODE p		on  substring(n.nodeAddress, 3, 12) = p.nodeMacAddress		left join		ekadb.ekadb.NODE_ACCESS na		on  p.nodeID = na.nodeID		left join		ekadb.ekadb.GW_RADIO gr		on  na.gwID = gr.gwID		left join		ekadb.ekadb.ROUTE_DATA_POINT r		on  r.entityID = p.nodeID and r.destinationAddress = '00' + gr.gwRadioMACAddr		and r.routeFlagCodeID & 65 = 65		and r.routeDataTimeStamp >=		cast(datediff(second, '1970-01-01', CONVERT(date, GETDATE()-3)) as bigint) * 1000		left join		ekadb.ekadb.NODE_LAST_COMM_STATUS l		on  n.nodeID = l.nodeID and na.gwID = l.gwID		left join		ekadb.ekadb.NODE_COMM_STATUS_CODE c		on  l.nodeCommStatusCodeID = c.nodeCommStatusCodeID		left join		ekadb.ekadb.GATEWAY g		on  na.gwID = g.gwID		) t		where		rowNumber = 1" queryout .\Query_NodeStatus_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_NodeStatus_1_DataWithoutHeaders.csv %resultDir%\NodeStatus_DataWithoutHeaders.csv 
del Query_NodeStatus_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 6 nodereset_2 subitem 1 >> .\query_log.txt 
bcp "SELECT		DATEADD(s, ns.statusPointTimestamp/1000, '1970-01-01') statusTS		, ns.nodeID		, n.nodeSN		, right(n.nodeAddress, 12) as nodeAddress		, n.nodeSWRev		, ns.nodeStatusCodeID		, (ns.statusDataValue & 255) as resetCode		FROM		ekadb.ekadb.NODE_STATUS_DATA_POINT ns,		ekadb.ekadb.NODE n		WHERE		ns.nodeID = n.nodeID		and ns.nodeStatusCodeID = 48		and (ns.statusDataValue & 255) = 2		and ns.statusPointTimestamp between		(CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-21)) AS BIGINT))*1000 and		(CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000		ORDER BY statusTS" queryout .\Query_nodereset_2_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_nodereset_2_1_DataWithoutHeaders.csv %resultDir%\nodereset_2_DataWithoutHeaders.csv 
del Query_nodereset_2_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 7 metersn subitem 1 >> .\query_log.txt 
bcp "select		s.sensorSN		, n.nodeSN		, n.nodeSWRev		, right(n.nodeAddress, 12) as nodeAddress		from ekadb.ekadb.SENSOR_SN s left join ekadb.ekadb.NODE n on n.nodeID = s.nodeID" queryout .\Query_metersn_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_metersn_1_DataWithoutHeaders.csv %resultDir%\metersn_DataWithoutHeaders.csv 
del Query_metersn_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 8 gwswitch subitem 1 >> .\query_log.txt 
bcp "select		convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101) as TsDay		, count (*) as switchCount		from		(			select			rd.entityID as nodeID			, rd.routeDataTimeStamp as routeFullTS			, DATEADD(s, rd.routeDataTimeStamp/1000, '1970-01-01') as routeTS			, rd.gwID			, rd.destinationAddress as destAddr			, rd.nextHopAddress as hopAddr			, LAG(rd.destinationAddress) OVER (order by entityID, rd.routeDataTimeStamp) as prevDestAddr			, LAG(rd.entityID) OVER (order by entityID, rd.routeDataTimeStamp) as prevNodeID			, LAG(rd.nextHopAddress) OVER (order by entityID, rd.routeDataTimeStamp) as prevHopAddr			, rd.totalCost			, rd.hopCount			from			ekadb.ekadb.ROUTE_DATA_POINT rd			where			rd.routeFlagCodeID = 65			and rd.routeDataTimeStamp between			(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-30)) AS BIGINT))*1000 and			(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000		) as qStr		where		qStr.destAddr <> qStr.prevDestAddr		and qStr.nodeID = qStr.prevNodeID		group by convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101)		order by TsDay" queryout .\Query_gwswitch_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_gwswitch_1_DataWithoutHeaders.csv %resultDir%\gwswitch_DataWithoutHeaders.csv 
del Query_gwswitch_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 9 nodeswitch subitem 1 >> .\query_log.txt 
bcp "select		convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101) as TsDay		, count (*) as switchCount		from		(			select			rd.entityID as nodeID			, rd.routeDataTimeStamp as routeFullTS			, DATEADD(s, rd.routeDataTimeStamp/1000, '1970-01-01') as routeTS			, rd.gwID			, rd.destinationAddress as destAddr			, rd.nextHopAddress as hopAddr			, LAG(rd.destinationAddress) OVER (order by entityID, rd.routeDataTimeStamp) as prevDestAddr			, LAG(rd.entityID) OVER (order by entityID, rd.routeDataTimeStamp) as prevNodeID			, LAG(rd.nextHopAddress) OVER (order by entityID, rd.routeDataTimeStamp) as prevHopAddr			, rd.totalCost			, rd.hopCount			from			ekadb.ekadb.ROUTE_DATA_POINT rd			where			rd.routeFlagCodeID = 65			and rd.routeDataTimeStamp between			(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-30)) AS BIGINT))*1000 and			(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000		) as qStr		where		qStr.hopAddr <> qStr.prevHopAddr		and qStr.nodeID = qStr.prevNodeID		group by convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101)		order by TsDay" queryout .\Query_nodeswitch_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_nodeswitch_1_DataWithoutHeaders.csv %resultDir%\nodeswitch_DataWithoutHeaders.csv 
del Query_nodeswitch_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 10 onlypfnodeswitch subitem 1 >> .\query_log.txt 
bcp "select		convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101) as TsDay		, count (*) as switchCount		from		(			select			rd.entityID as nodeID			, rd.routeDataTimeStamp as routeFullTS			, DATEADD(s, rd.routeDataTimeStamp/1000, '1970-01-01') as routeTS			, rd.gwID			, rd.destinationAddress as destAddr			, rd.nextHopAddress as hopAddr			, LAG(rd.destinationAddress) OVER (order by entityID, rd.routeDataTimeStamp) as prevDestAddr			, LAG(rd.entityID) OVER (order by entityID, rd.routeDataTimeStamp) as prevNodeID			, LAG(rd.nextHopAddress) OVER (order by entityID, rd.routeDataTimeStamp) as prevHopAddr			, rd.totalCost			, rd.hopCount			from			ekadb.ekadb.ROUTE_DATA_POINT rd			where			rd.routeFlagCodeID = 65			and rd.routeDataTimeStamp between			(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-30)) AS BIGINT))*1000 and			(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000		) as qStr		where		qStr.hopAddr <> qStr.prevHopAddr		and qStr.destAddr = qStr.prevDestAddr		and qStr.nodeID = qStr.prevNodeID		group by convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101)		order by TsDay" queryout .\Query_onlypfnodeswitch_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_onlypfnodeswitch_1_DataWithoutHeaders.csv %resultDir%\onlypfnodeswitch_DataWithoutHeaders.csv 
del Query_onlypfnodeswitch_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 1 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-1+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_1_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_1_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 2 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-2+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_2_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_2_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_2_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 3 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-3+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_3_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_3_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_3_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 4 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-4+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_4_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_4_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_4_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_4_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 5 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-5+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_5_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_5_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_5_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_5_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 6 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-6+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_6_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_6_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_6_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_6_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 7 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-7+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_7_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_7_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_7_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_7_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 8 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-8+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_8_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_8_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_8_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_8_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 9 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-9+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_9_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_9_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_9_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_9_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 10 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-10+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_10_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_10_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_10_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_10_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 11 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-11+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_11_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_11_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_11_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_11_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 12 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-12+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_12_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_12_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_12_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_12_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 13 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-13+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_13_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_13_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_13_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_13_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 14 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-14+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_14_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_14_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_14_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_14_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 15 >> .\query_log.txt 
bcp "select			CONVERT(date, GETDATE()-15+1) as "dateOfQuery"			, primaryRoutes.nodeAddress			, nw.nodeSWRev			, primaryRoutes.nextAddr			, primaryRoutes.gwAddr			, nd.etxBand			, primaryRoutes.routeHops			, primaryRoutes.routeCost			, nd.currentRateID			, nd.currentPowerID			FROM			ekadb.ekadb.NEIGHBOR_DATA_POINT nd			,ekadb.ekadb.NODE nw			, (				SELECT				right(n.nodeAddress, 12) as nodeAddress				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr				, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost				, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID				FROM				ekadb.ekadb.ROUTE_DATA_POINT rd				left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID				WHERE				rd.entityID != rd.gwID				AND				rd.routeDataTimeStamp  between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15+1)) AS BIGINT))*1000				AND				(rd.routeFlagCodeID & 1) = 1				GROUP BY n.nodeAddress			) as primaryRoutes			where			nd.entityID = primaryRoutes.entityID            and nd.neighborAddress = primaryRoutes.nextAddr            and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12)            and nd.neighborDataTimestamp  between            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15-2)) AS BIGINT))*1000 and            (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_15_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_15_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_15_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_15_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 12 cntmasterconn subitem 1 >> .\query_log.txt 
bcp "with qStr as            (				select				ROW_NUMBER() over (order by bsd.entityID, bsd.statsTimestamp) as row				, convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) as TsDay				, bsd.entityID as entityID				, cast(gsd.generalStatisticValue as int) as statVal				from				ekadb.ekadb.node n				, ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd left join				ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd on (bsd.statsDataPointID = gsd.statsDataPointID)				where				n.nodeID = bsd.entityID				and gsd.generalStatisticCodeID = 15 and				bsd.statsTimestamp between				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 and				(CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000            )			select			qStr.TsDay as TsDay			, SUM(qStrNext.statVal - qStr.statVal) as MasterConnections			from			qStr join qStr as qStrNext on (qStrNext.row-1 = qStr.row)			where			qStrNext.entityID = qStr.entityID			and (qStrNext.statVal - qStr.statVal) >= 0			group by qStr.TsDay			order by MasterConnections desc" queryout .\Query_cntmasterconn_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_cntmasterconn_1_DataWithoutHeaders.csv %resultDir%\cntmasterconn_1_DataWithoutHeaders.csv 
del Query_cntmasterconn_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 13 cntslaveconn subitem 1 >> .\query_log.txt 
bcp "with qStr as           (           select           ROW_NUMBER() over (order by bsd.entityID, bsd.statsTimestamp) as row           , convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) as TsDay           , bsd.entityID as entityID           , cast(gsd.generalStatisticValue as int) as statVal           from           ekadb.ekadb.node n           , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd left join           ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd on (bsd.statsDataPointID = gsd.statsDataPointID)           where           n.nodeID = bsd.entityID           and gsd.generalStatisticCodeID = 16 and           bsd.statsTimestamp between           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 and           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000           )           select           qStr.TsDay as TsDay           , SUM(qStrNext.statVal - qStr.statVal) as SlaveConnections           from           qStr join qStr as qStrNext on (qStrNext.row-1 = qStr.row)           where           qStrNext.entityID = qStr.entityID           and (qStrNext.statVal - qStr.statVal) >= 0           group by qStr.TsDay           order by SlaveConnections desc" queryout .\Query_cntslaveconn_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_cntslaveconn_1_DataWithoutHeaders.csv %resultDir%\cntslaveconn_DataWithoutHeaders.csv 
del Query_cntslaveconn_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 14 cntpridrop subitem 1 >> .\query_log.txt 
bcp "with qStr as           (           select           ROW_NUMBER() over (order by bsd.entityID, bsd.statsTimestamp) as row           , convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) as TsDay           , bsd.entityID as entityID           , cast(gsd.generalStatisticValue as int) as statVal           from           ekadb.ekadb.node n           , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd left join           ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd on (bsd.statsDataPointID = gsd.statsDataPointID)           where           n.nodeID = bsd.entityID           and gsd.generalStatisticCodeID = 21 and           bsd.statsTimestamp between           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 and           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000           )           select           qStr.TsDay as TsDay           , SUM(qStrNext.statVal - qStr.statVal) as PrimaryDropCount           from           qStr join qStr as qStrNext on (qStrNext.row-1 = qStr.row)           where           qStrNext.entityID = qStr.entityID           and (qStrNext.statVal - qStr.statVal) >= 0           group by qStr.TsDay           order by PrimaryDropCount desc" queryout .\Query_cntpridrop_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_cntpridrop_1_DataWithoutHeaders.csv %resultDir%\cntpridrop_DataWithoutHeaders.csv 
del Query_cntpridrop_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 15 cntSecdrop subitem 1 >> .\query_log.txt 
bcp "with qStr as           (           select           ROW_NUMBER() over (order by bsd.entityID, bsd.statsTimestamp) as row           , convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) as TsDay           , bsd.entityID as entityID           , cast(gsd.generalStatisticValue as int) as statVal           from           ekadb.ekadb.node n           , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd left join           ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd on (bsd.statsDataPointID = gsd.statsDataPointID)           where           n.nodeID = bsd.entityID           and gsd.generalStatisticCodeID = 22 and           bsd.statsTimestamp between           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 and           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000           )           select           qStr.TsDay as TsDay           , SUM(qStrNext.statVal - qStr.statVal) as SecondaryDropCount           from           qStr join qStr as qStrNext on (qStrNext.row-1 = qStr.row)           where           qStrNext.entityID = qStr.entityID           and (qStrNext.statVal - qStr.statVal) >= 0           group by qStr.TsDay           order by SecondaryDropCount desc" queryout .\Query_cntSecdrop_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_cntSecdrop_1_DataWithoutHeaders.csv %resultDir%\cntSecdrop_DataWithoutHeaders.csv 
del Query_cntSecdrop_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 16 sensordatareportcnt subitem 1 >> .\query_log.txt 
bcp "select right(nt.nodeAddress, 12) as nodeAddress           , nt.nodeID           , nt.nodeType           , nt.nodeSWRev           ,snstatuscnt.count           from            (select            sn.nodeID as nodeID           , count(*) as count           from            ekadb.ekadb.sensor_data_point sd           , ekadb.ekadb.node ns left join ekadb.ekadb.sensor_sn sn on sn.nodeID = ns.nodeID           where           sd.nodeID = ns.nodeID           and sd.dataPointTimeStamp between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000           group by sn.nodeID)  as snstatuscnt           left join ekadb.ekadb.node nt on nt.nodeID  = snstatuscnt.nodeID" queryout .\Query_sensordatareportcnt_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_sensordatareportcnt_1_DataWithoutHeaders.csv %resultDir%\sensordatareportcnt_DataWithoutHeaders.csv 
del Query_sensordatareportcnt_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 17 nodeidmacmap subitem 1 >> .\query_log.txt 
bcp "select nodeSN, nodeID, nodeMACAddress from ekadb.ekadb.node" queryout .\Query_nodeidmacmap_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_nodeidmacmap_1_DataWithoutHeaders.csv %resultDir%\nodeidmacmap_DataWithoutHeaders.csv 
del Query_nodeidmacmap_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 1 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-1+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_1_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_1_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 2 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-2+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_2_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_2_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_2_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 3 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-3+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_3_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_3_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_3_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 4 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-4+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_4_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_4_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_4_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_4_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 5 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-5+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_5_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_5_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_5_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_5_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 6 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-6+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_6_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_6_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_6_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_6_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 7 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-7+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_7_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_7_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_7_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_7_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 8 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-8+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_8_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_8_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_8_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_8_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 9 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-9+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_9_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_9_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_9_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_9_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 10 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-10+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_10_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_10_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_10_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_10_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 11 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-11+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_11_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_11_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_11_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_11_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 12 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-12+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_12_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_12_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_12_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_12_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 13 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-13+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_13_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_13_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_13_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_13_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 14 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-14+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_14_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_14_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_14_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_14_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 15 >> .\query_log.txt 
bcp "select               CONVERT(date, GETDATE()-15+1) as "dateOfQuery"               , right(n.nodeAddress, 12) as nodeAddress               , count(*) as count               FROM               ekadb.ekadb.ROUTE_DATA_POINT rd               left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID               WHERE               rd.entityID != rd.gwID               AND               rd.routeDataTimeStamp  between               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15-2)) AS BIGINT))*1000 and               (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15+1)) AS BIGINT))*1000               AND               (rd.routeFlagCodeID & 1) = 1               GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_15_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_15_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_15_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_15_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 19 allmasterconncount subitem 1 >> .\query_log.txt 
bcp "select           convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) as TsDay           , right(n.nodeAddress, 12) as nodeAddress           , cast(gsd.generalStatisticValue as int) as statVal           from           ekadb.ekadb.node n           , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd left join           ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd on (bsd.statsDataPointID = gsd.statsDataPointID)           where           n.nodeID = bsd.entityID           and (gsd.generalStatisticCodeID = 15)           and bsd.statsTimestamp between           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 and           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_allmasterconncount_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_allmasterconncount_1_DataWithoutHeaders.csv %resultDir%\allmasterconncount_DataWithoutHeaders.csv 
del Query_allmasterconncount_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Query 20 allslaveconncount subitem 1 >> .\query_log.txt 
bcp "select           convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) as TsDay           , right(n.nodeAddress, 12) as nodeAddress           , cast(gsd.generalStatisticValue as int) as statVal           from           ekadb.ekadb.node n           , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd left join           ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd on (bsd.statsDataPointID = gsd.statsDataPointID)           where           n.nodeID = bsd.entityID           and (gsd.generalStatisticCodeID = 16)           and bsd.statsTimestamp between           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 and           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_allslaveconncount_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_allslaveconncount_1_DataWithoutHeaders.csv %resultDir%\allslaveconncount_DataWithoutHeaders.csv 
del Query_allslaveconncount_1_DataWithoutHeaders.csv  

echo =======================================  >> .\query_log.txt 
echo Time_adjust_error subitem 1 >> .\query_log.txt
bcp "select dateadd(s,statusPointTimestamp/1000,'19700101') as statusPointTS,* from NODE_STATUS_DATA_POINT where nodeStatusCodeID = 60 and statusPointTimestamp between (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()- 30)) AS BIGINT))*1000 and           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Time_adjust_error.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Time_adjust_error.csv %resultDir%\Time_adjust_error.csv 
del Time_adjust_error.csv

echo Time_sync_greater_than_spec_value subitem 1 >> .\query_log.txt
echo =======================================  >> .\query_log.txt 
bcp "select * , DATEADD(s,statusPointTimestamp/1000,'19700101') as statusPointTS from NODE_STATUS_DATA_POINT where nodeStatusCodeID = 49 and statusPointTimestamp between (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()- 30)) AS BIGINT))*1000 and           (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 and (CAST(statusDataValue as int)) > 60" queryout .\Time_sync_greater_than_spec_value.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Time_sync_greater_than_spec_value.csv %resultDir%\Time_sync_greater_than_spec_value.csv 
del Time_sync_greater_than_spec_value.csv

echo Reset_cause subitem 1 >> .\query_log.txt
echo =======================================  >> .\query_log.txt 
bcp "SELECT DATEADD(s, ns.statusPointTimestamp/1000, '1970-01-01') statusTS , ns.nodeID, n.nodeSN, n.nodeAddress, n.nodeSWRev, ns.nodeStatusCodeID, (ns.statusDataValue & 255) as resetCode FROM ekadb.ekadb.NODE_STATUS_DATA_POINT ns, ekadb.ekadb.NODE n WHERE ns.nodeID = n.nodeID and ns.nodeStatusCodeID = 48 and ns.statusPointTimestamp between (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE() - 15)) AS BIGINT))*1000 and  (CAST(DATEDIFF(s, '1970-01-01', GETDATE()) AS BIGINT))*1000 ORDER BY statusTS " queryout .\Reset_cause.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Reset_cause.csv %resultDir%\Reset_cause.csv 
del Reset_cause.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 1 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-1+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-1+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_1_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_1_DataWithoutHeaders.csv 
del gateway_Reset_Count_1_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 2 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-2+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-2+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_2_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_2_DataWithoutHeaders.csv 
del gateway_Reset_Count_2_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 3 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-3+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-3+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_3_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_3_DataWithoutHeaders.csv 
del gateway_Reset_Count_3_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 4 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-4+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-4+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_4_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_4_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_4_DataWithoutHeaders.csv 
del gateway_Reset_Count_4_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 5 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-5+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-5+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_5_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_5_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_5_DataWithoutHeaders.csv 
del gateway_Reset_Count_5_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 6 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-6+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-6+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_6_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_6_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_6_DataWithoutHeaders.csv 
del gateway_Reset_Count_6_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 7 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-7+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-7+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_7_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gateway_Reset_Count_7_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_7_DataWithoutHeaders.csv 
del gateway_Reset_Count_7_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 8 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-8+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-8+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_8_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_8_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_8_DataWithoutHeaders.csv 
del gateway_Reset_Count_8_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 9 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-9+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-9+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_9_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_9_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_9_DataWithoutHeaders.csv 
del gateway_Reset_Count_9_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 10 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-10+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-10+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_10_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_10_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_10_DataWithoutHeaders.csv 
del gateway_Reset_Count_10_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 11 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-11+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-11+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_11_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gateway_Reset_Count_11_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_11_DataWithoutHeaders.csv 
del gateway_Reset_Count_11_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 12 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-12+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-12+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_12_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gateway_Reset_Count_12_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_12_DataWithoutHeaders.csv 
del gateway_Reset_Count_12_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 13 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-13+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-13+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_13_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_13_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_13_DataWithoutHeaders.csv 
del gateway_Reset_Count_13_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 14 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-14+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-14+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_14_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_14_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_14_DataWithoutHeaders.csv 
del gateway_Reset_Count_14_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 15 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-15+1) as "dateOfQuery", count(*) as ""Gateway Reset Count (Last 24 Hours)"" from ekadb.ekadb.GW_LOG_ENTRY gwle where gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-15+1) - 86400 and gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_15_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gateway_Reset_Count_15_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_15_DataWithoutHeaders.csv 
del gateway_Reset_Count_15_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 1 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-1+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-1+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_1_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_1_DataWithoutHeaders.csv 
del gap_Fill_Requests_1_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 2 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-2+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-2+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_2_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_2_DataWithoutHeaders.csv 
del gap_Fill_Requests_2_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 3 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-3+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-3+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_3_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_3_DataWithoutHeaders.csv 
del gap_Fill_Requests_3_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 4 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-4+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-4+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_4_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_4_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_4_DataWithoutHeaders.csv 
del gap_Fill_Requests_4_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 5 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-5+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-5+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_5_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_5_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_5_DataWithoutHeaders.csv 
del gap_Fill_Requests_5_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 6 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-6+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-6+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_6_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_6_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_6_DataWithoutHeaders.csv 
del gap_Fill_Requests_6_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 7 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-7+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-7+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_7_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gap_Fill_Requests_7_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_7_DataWithoutHeaders.csv 
del gap_Fill_Requests_7_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 8 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-8+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-8+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_8_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_8_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_8_DataWithoutHeaders.csv 
del gap_Fill_Requests_8_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 9 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-9+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-9+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_9_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_9_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_9_DataWithoutHeaders.csv 
del gap_Fill_Requests_9_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 10 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-10+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-10+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_10_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gap_Fill_Requests_10_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_10_DataWithoutHeaders.csv 
del gap_Fill_Requests_10_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 11 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-11+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-11+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_11_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_11_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_11_DataWithoutHeaders.csv 
del gap_Fill_Requests_11_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 12 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-12+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-12+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_12_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_12_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_12_DataWithoutHeaders.csv 
del gap_Fill_Requests_12_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 13 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-13+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-13+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_13_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gap_Fill_Requests_13_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_13_DataWithoutHeaders.csv 
del gap_Fill_Requests_13_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 14 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-14+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-14+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_14_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_14_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_14_DataWithoutHeaders.csv 
del gap_Fill_Requests_14_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 15 >> .\query_log.txt
bcp "select CONVERT(date, GETDATE()-15+1) as "dateOfQuery", count(*) as ""Gap fill requests (Last 24 Hours)"" from ekadb.ekadb.GW_NODE_LOG_ENTRY nle where nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-15+1) - 86400 and nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_15_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_15_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_15_DataWithoutHeaders.csv 
del gap_Fill_Requests_15_DataWithoutHeaders.csv

SET PowerShellScriptPath=%~dp0%Zip.ps1
SET srcPath=%currentDir%%resultDir%
SET destPath=%currentDir%%resultDir%.zip
PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' '%srcPath%' '%destPath%'"
cd ..\

GOTO end
:endprase
echo "Usage: historicData.bat <serverip> <username> <password>"
:end