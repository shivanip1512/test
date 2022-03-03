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
bcp "SELECT g.gwName, gr.gwRadioMACAddr, g.gwSN, g.gwRouteColor, g.controlRate FROM ekadb.ekadb.gateway g LEFT JOIN ekadb.ekadb.GW_RADIO gr ON (gr.gwID =  g.gwID) WHERE gr.gwRadioMACAddr <> 'FFFFFFFFFFFF'" queryout .\Query_GW_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_GW_1_DataWithoutHeaders.csv %resultDir%\GW_DataWithoutHeaders.csv 
del Query_GW_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 2 NodeSWVer subitem 1 >> .\query_log.txt 
bcp "SELECT n.nodeSWRev, count(*) FROM ekadb.ekadb.node n group by n.nodeSWRev" queryout .\Query_NodeSWVer_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_NodeSWVer_1_DataWithoutHeaders.csv %resultDir%\NodeSWVer_DataWithoutHeaders.csv 
del Query_NodeSWVer_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 3 NetworkStatus subitem 1 >> .\query_log.txt 
bcp "SELECT primaryRoutes.nodeAddress, nw.nodeSWRev, primaryRoutes.nextAddr, primaryRoutes.gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost, nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd, ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress, 12) AS nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-1)) AS BIGINT))*1000 AND (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 AND (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_NetworkStatus_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_NetworkStatus_1_DataWithoutHeaders.csv %resultDir%\NetworkStatus_DataWithoutHeaders.csv 
del Query_NetworkStatus_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 3 NetworkStatus subitem 2 >> .\query_log.txt 
bcp "SELECT primaryRoutes.nodeAddress, nw.nodeSWRev, primaryRoutes.nextAddr, primaryRoutes.gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost , nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd,ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress, 12) AS nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (CAST(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 AND(cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_NetworkStatus_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y NetworkStatus_DataWithoutHeaders.csv+Query_NetworkStatus_2_DataWithoutHeaders.csv %resultDir%\NetworkStatus_DataWithoutHeaders.csv 
del Query_NetworkStatus_2_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 3 NetworkStatus subitem 3 >> .\query_log.txt 
bcp "SELECT primaryRoutes.nodeAddress, nw.nodeSWRev, primaryRoutes.nextAddr, primaryRoutes.gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost, nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd,ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress, 12) AS nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_NetworkStatus_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y NetworkStatus_DataWithoutHeaders.csv+Query_NetworkStatus_3_DataWithoutHeaders.csv %resultDir%\NetworkStatus_DataWithoutHeaders.csv 
del Query_NetworkStatus_3_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 5 NodeStatus subitem 1 >> .\query_log.txt 
bcp "SELECT nodeAddress, nodeID, gwID, gwName, nodeSWRev, DATEADD(s, timestamp/1000, '1970-01-01') statusTS, currStatus FROM ( SELECT row_number() over (partition by n.nodeID ORDER BY na.isPrimary desc, r.routeDataTimeStamp desc, l.nodeCommStatusTimestamp desc) AS rowNumber, right(n.nodeAddress, 12) nodeAddress, n.nodeID, na.gwID, g.gwName, n.nodeSWRev, l.nodeCommStatusTimestamp AS timeStamp, l.nodeCommStatusCodeID AS currStatusCode, c.nodeCommStatusString AS currStatus FROM ekadb.ekadb.NODE n LEFT JOIN ekadb.ekadb.NODE p ON substring(n.nodeAddress, 3, 12) =  p.nodeMacAddress LEFT JOIN ekadb.ekadb.NODE_ACCESS na ON p.nodeID =  na.nodeID LEFT JOIN ekadb.ekadb.GW_RADIO gr ON na.gwID =  gr.gwID LEFT JOIN ekadb.ekadb.ROUTE_DATA_POINT r ON r.entityID =  p.nodeID AND r.destinationAddress =  '00' + gr.gwRadioMACAddr AND r.routeFlagCodeID & 65 =  65 AND r.routeDataTimeStamp >=  cast(datediff(second, '1970-01-01', CONVERT(date, GETDATE()-3)) AS bigint) * 1000 LEFT JOIN ekadb.ekadb.NODE_LAST_COMM_STATUS l ON n.nodeID =  l.nodeID AND na.gwID =  l.gwID LEFT JOIN ekadb.ekadb.NODE_COMM_STATUS_CODE c ON l.nodeCommStatusCodeID =  c.nodeCommStatusCodeID LEFT JOIN ekadb.ekadb.GATEWAY g ON na.gwID =  g.gwID ) t WHERE rowNumber =  1" queryout .\Query_NodeStatus_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_NodeStatus_1_DataWithoutHeaders.csv %resultDir%\NodeStatus_DataWithoutHeaders.csv 
del Query_NodeStatus_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 6 nodereset_2 subitem 1 >> .\query_log.txt 
bcp "SELECT DATEADD(s, ns.statusPointTimestamp/1000, '1970-01-01') statusTS, ns.nodeID, n.nodeSN, right(n.nodeAddress, 12) AS nodeAddress, n.nodeSWRev, ns.nodeStatusCodeID , (ns.statusDataValue & 255) AS resetCode FROM ekadb.ekadb.NODE_STATUS_DATA_POINT ns, ekadb.ekadb.NODE n WHERE ns.nodeID =  n.nodeID AND ns.nodeStatusCodeID =  48 AND (ns.statusDataValue & 255) =  2 AND ns.statusPointTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-21)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000 ORDER BY statusTS" queryout .\Query_nodereset_2_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_nodereset_2_1_DataWithoutHeaders.csv %resultDir%\nodereset_2_DataWithoutHeaders.csv 
del Query_nodereset_2_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 7 metersn subitem 1 >> .\query_log.txt 
bcp "SELECT s.sensorSN, n.nodeSN, n.nodeSWRev, right(n.nodeAddress, 12) AS nodeAddress FROM ekadb.ekadb.SENSOR_SN s LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  s.nodeID" queryout .\Query_metersn_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_metersn_1_DataWithoutHeaders.csv %resultDir%\metersn_DataWithoutHeaders.csv 
del Query_metersn_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 8 gwswitch subitem 1 >> .\query_log.txt 
bcp "SELECT convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101) AS TsDay , count (*) AS switchCount FROM (SELECT rd.entityID AS nodeID, rd.routeDataTimeStamp AS routeFullTS, DATEADD(s, rd.routeDataTimeStamp/1000, '1970-01-01') AS routeTS, rd.gwID, rd.destinationAddress AS destAddr, rd.nextHopAddress AS hopAddr, LAG(rd.destinationAddress) OVER (ORDER BY entityID, rd.routeDataTimeStamp) AS prevDestAddr, LAG(rd.entityID) OVER (ORDER BY entityID, rd.routeDataTimeStamp) AS prevNodeID, LAG(rd.nextHopAddress) OVER (ORDER BY entityID, rd.routeDataTimeStamp) AS prevHopAddr, rd.totalCost, rd.hopCount FROM ekadb.ekadb.ROUTE_DATA_POINT rd WHERE rd.routeFlagCodeID =  65 AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-30)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 ) AS qStr WHERE qStr.destAddr <> qStr.prevDestAddr AND qStr.nodeID =  qStr.prevNodeID group by convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101) ORDER BY TsDay" queryout .\Query_gwswitch_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_gwswitch_1_DataWithoutHeaders.csv %resultDir%\gwswitch_DataWithoutHeaders.csv 
del Query_gwswitch_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 9 nodeswitch subitem 1 >> .\query_log.txt 
bcp "SELECT convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101) AS TsDay , count (*) AS switchCount FROM (SELECT rd.entityID AS nodeID , rd.routeDataTimeStamp AS routeFullTS, DATEADD(s, rd.routeDataTimeStamp/1000, '1970-01-01') AS routeTS, rd.gwID, rd.destinationAddress AS destAddr, rd.nextHopAddress AS hopAddr, LAG(rd.destinationAddress) OVER (ORDER BY entityID, rd.routeDataTimeStamp) AS prevDestAddr, LAG(rd.entityID) OVER (ORDER BY entityID, rd.routeDataTimeStamp) AS prevNodeID, LAG(rd.nextHopAddress) OVER (ORDER BY entityID, rd.routeDataTimeStamp) AS prevHopAddr, rd.totalCost, rd.hopCount FROM ekadb.ekadb.ROUTE_DATA_POINT rd WHERE rd.routeFlagCodeID =  65 AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-30)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 ) AS qStr WHERE qStr.hopAddr <> qStr.prevHopAddr AND qStr.nodeID =  qStr.prevNodeID group by convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101) ORDER BY TsDay" queryout .\Query_nodeswitch_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_nodeswitch_1_DataWithoutHeaders.csv %resultDir%\nodeswitch_DataWithoutHeaders.csv 
del Query_nodeswitch_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 10 onlypfnodeswitch subitem 1 >> .\query_log.txt 
bcp "SELECT convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101) AS TsDay, count (*) AS switchCount FROM (SELECT rd.entityID AS nodeID, rd.routeDataTimeStamp AS routeFullTS, DATEADD(s, rd.routeDataTimeStamp/1000, '1970-01-01') AS routeTS, rd.gwID, rd.destinationAddress AS destAddr, rd.nextHopAddress AS hopAddr, LAG(rd.destinationAddress) OVER (ORDER BY entityID, rd.routeDataTimeStamp) AS prevDestAddr, LAG(rd.entityID) OVER (ORDER BY entityID, rd.routeDataTimeStamp) AS prevNodeID, LAG(rd.nextHopAddress) OVER (ORDER BY entityID, rd.routeDataTimeStamp) AS prevHopAddr , rd.totalCost, rd.hopCount FROM ekadb.ekadb.ROUTE_DATA_POINT rd WHERE rd.routeFlagCodeID =  65 AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-30)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 ) AS qStr WHERE qStr.hopAddr <> qStr.prevHopAddr AND qStr.destAddr =  qStr.prevDestAddr AND qStr.nodeID =  qStr.prevNodeID group by convert(nvarchar, DATEADD(s, qStr.routeFullTS/1000, '1970-01-01'), 101) ORDER BY TsDay" queryout .\Query_onlypfnodeswitch_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_onlypfnodeswitch_1_DataWithoutHeaders.csv %resultDir%\onlypfnodeswitch_DataWithoutHeaders.csv 
del Query_onlypfnodeswitch_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 1 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-1+1) AS "dateOfQuery", primaryRoutes.nodeAddress, nw.nodeSWRev, primaryRoutes.nextAddr, primaryRoutes.gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost, nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd,ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress, 12) AS nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_1_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_1_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 2 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-2+1) AS "dateOfQuery", primaryRoutes.nodeAddress, nw.nodeSWRev, primaryRoutes.nextAddr, primaryRoutes.gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost, nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd,ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress, 12) AS nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_2_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_2_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_2_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 3 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-3+1) AS "dateOfQuery", primaryRoutes.nodeAddress, nw.nodeSWRev, primaryRoutes.nextAddr, primaryRoutes.gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost, nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd,ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress, 12) AS nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_3_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_3_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_3_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 4 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-4+1) AS "dateOfQuery", primaryRoutes.nodeAddress, nw.nodeSWRev, primaryRoutes.nextAddr, primaryRoutes.gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost, nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd,ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress, 12) AS nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_4_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_4_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_4_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_4_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 5 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-5+1) AS "dateOfQuery", primaryRoutes.nodeAddress, nw.nodeSWRev, primaryRoutes.nextAddr, primaryRoutes.gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost, nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress, 12) AS nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_5_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_5_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_5_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_5_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 6 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-6+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_6_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_6_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_6_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_6_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 7 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-7+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_7_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_7_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_7_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_7_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 8 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-8+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_8_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_8_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_8_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_8_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 9 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-9+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_9_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_9_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_9_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_9_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 10 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-10+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_10_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_10_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_10_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_10_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 11 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-11+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_11_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_11_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_11_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_11_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 12 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-12+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_12_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_12_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_12_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_12_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 13 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-13+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_13_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_13_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_13_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_13_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 14 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-14+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_14_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_14_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_14_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_14_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 11 dailyNetworkStatus subitem 15 >> .\query_log.txt 
bcp "SELECT CONVERT(date, GETDATE()-15+1) AS "dateOfQuery" , primaryRoutes.nodeAddress , nw.nodeSWRev , primaryRoutes.nextAddr , primaryRoutes.gwAddr , nd.etxBand , primaryRoutes.routeHops , primaryRoutes.routeCost , nd.currentRateID , nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw , ( SELECT right(n.nodeAddress, 12) AS nodeAddress , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.nextHopAddress), 16, 40) AS nextAddr , substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + rd.destinationAddress), 16, 40) AS gwAddr , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost , cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID WHERE rd.entityID !=  rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15+1)) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) =  1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID =  primaryRoutes.entityID AND nd.neighborAddress =  primaryRoutes.nextAddr AND primaryRoutes.nodeAddress =  right(nw.nodeAddress, 12) AND nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15-2)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15+1)) AS BIGINT))*1000" queryout .\Query_dailyNetworkStatus_15_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailyNetworkStatus_15_DataWithoutHeaders.csv %resultDir%\dailyNetworkStatus_15_DataWithoutHeaders.csv 
del Query_dailyNetworkStatus_15_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 12 cntmasterconn subitem 1 >> .\query_log.txt 
bcp "with qStr AS ( SELECT ROW_NUMBER() over (ORDER BY bsd.entityID, bsd.statsTimestamp) AS row , convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) AS TsDay , bsd.entityID AS entityID , cast(gsd.generalStatisticValue AS int) AS statVal FROM ekadb.ekadb.node n , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd LEFT JOIN ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd ON (bsd.statsDataPointID =  gsd.statsDataPointID) WHERE n.nodeID =  bsd.entityID AND gsd.generalStatisticCodeID =  15 AND bsd.statsTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 ) SELECT qStr.TsDay AS TsDay , SUM(qStrNext.statVal - qStr.statVal) AS MasterConnections FROM qStr join qStr AS qStrNext ON (qStrNext.row-1 =  qStr.row) WHERE qStrNext.entityID =  qStr.entityID AND (qStrNext.statVal - qStr.statVal) >=  0 group by qStr.TsDay ORDER BY MasterConnections desc" queryout .\Query_cntmasterconn_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_cntmasterconn_1_DataWithoutHeaders.csv %resultDir%\cntmasterconn_1_DataWithoutHeaders.csv 
del Query_cntmasterconn_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 13 cntslaveconn subitem 1 >> .\query_log.txt 
bcp "with qStr AS ( SELECT ROW_NUMBER() over (ORDER BY bsd.entityID, bsd.statsTimestamp) AS row , convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) AS TsDay , bsd.entityID AS entityID , cast(gsd.generalStatisticValue AS int) AS statVal FROM ekadb.ekadb.node n , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd LEFT JOIN ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd ON (bsd.statsDataPointID =  gsd.statsDataPointID) WHERE n.nodeID =  bsd.entityID AND gsd.generalStatisticCodeID =  16 AND bsd.statsTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 ) SELECT qStr.TsDay AS TsDay , SUM(qStrNext.statVal - qStr.statVal) AS SlaveConnections FROM qStr join qStr AS qStrNext ON (qStrNext.row-1 =  qStr.row) WHERE qStrNext.entityID =  qStr.entityID AND (qStrNext.statVal - qStr.statVal) >=  0 group by qStr.TsDay ORDER BY SlaveConnections desc" queryout .\Query_cntslaveconn_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_cntslaveconn_1_DataWithoutHeaders.csv %resultDir%\cntslaveconn_DataWithoutHeaders.csv 
del Query_cntslaveconn_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 14 cntpridrop subitem 1 >> .\query_log.txt 
bcp "with qStr AS ( SELECT ROW_NUMBER() over (ORDER BY bsd.entityID, bsd.statsTimestamp) AS row , convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) AS TsDay , bsd.entityID AS entityID , cast(gsd.generalStatisticValue AS int) AS statVal FROM ekadb.ekadb.node n , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd LEFT JOIN ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd ON (bsd.statsDataPointID =  gsd.statsDataPointID) WHERE n.nodeID =  bsd.entityID AND gsd.generalStatisticCodeID =  21 AND bsd.statsTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 ) SELECT qStr.TsDay AS TsDay , SUM(qStrNext.statVal - qStr.statVal) AS PrimaryDropCount FROM qStr join qStr AS qStrNext ON (qStrNext.row-1 =  qStr.row) WHERE qStrNext.entityID =  qStr.entityID AND (qStrNext.statVal - qStr.statVal) >=  0 group by qStr.TsDay ORDER BY PrimaryDropCount desc" queryout .\Query_cntpridrop_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_cntpridrop_1_DataWithoutHeaders.csv %resultDir%\cntpridrop_DataWithoutHeaders.csv 
del Query_cntpridrop_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 15 cntSecdrop subitem 1 >> .\query_log.txt 
bcp "with qStr AS ( SELECT ROW_NUMBER() over (ORDER BY bsd.entityID, bsd.statsTimestamp) AS row , convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) AS TsDay , bsd.entityID AS entityID , cast(gsd.generalStatisticValue AS int) AS statVal FROM ekadb.ekadb.node n , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd LEFT JOIN ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd ON (bsd.statsDataPointID =  gsd.statsDataPointID) WHERE n.nodeID =  bsd.entityID AND gsd.generalStatisticCodeID =  22 AND bsd.statsTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 ) SELECT qStr.TsDay AS TsDay , SUM(qStrNext.statVal - qStr.statVal) AS SecondaryDropCount FROM qStr join qStr AS qStrNext ON (qStrNext.row-1 =  qStr.row) WHERE qStrNext.entityID =  qStr.entityID AND (qStrNext.statVal - qStr.statVal) >=  0 group by qStr.TsDay ORDER BY SecondaryDropCount desc" queryout .\Query_cntSecdrop_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_cntSecdrop_1_DataWithoutHeaders.csv %resultDir%\cntSecdrop_DataWithoutHeaders.csv 
del Query_cntSecdrop_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 16 sensordatareportcnt subitem 1 >> .\query_log.txt 
bcp "SELECT right(nt.nodeAddress, 12) AS nodeAddress , nt.nodeID , nt.nodeType , nt.nodeSWRev ,snstatuscnt.count FROM (SELECT sn.nodeID AS nodeID , count(*) AS count FROM ekadb.ekadb.sensor_data_point sd , ekadb.ekadb.node ns LEFT JOIN ekadb.ekadb.sensor_sn sn ON sn.nodeID =  ns.nodeID WHERE sd.nodeID =  ns.nodeID AND sd.dataPointTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 group by sn.nodeID) AS snstatuscnt LEFT JOIN ekadb.ekadb.node nt ON nt.nodeID =  snstatuscnt.nodeID" queryout .\Query_sensordatareportcnt_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_sensordatareportcnt_1_DataWithoutHeaders.csv %resultDir%\sensordatareportcnt_DataWithoutHeaders.csv 
del Query_sensordatareportcnt_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 17 nodeidmacmap subitem 1 >> .\query_log.txt 
bcp "SELECT nodeSN, nodeID, nodeMACAddress FROM ekadb.ekadb.node" queryout .\Query_nodeidmacmap_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_nodeidmacmap_1_DataWithoutHeaders.csv %resultDir%\nodeidmacmap_DataWithoutHeaders.csv 
del Query_nodeidmacmap_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 1 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-1+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-1+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_1_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_1_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 2 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-2+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-2+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_2_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_2_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_2_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 3 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-3+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-3+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_3_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_3_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_3_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 4 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-4+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-4+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_4_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_4_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_4_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_4_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 5 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-5+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-5+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_5_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_5_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_5_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_5_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 6 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-6+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-6+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_6_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_6_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_6_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_6_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 7 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-7+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-7+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_7_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_7_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_7_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_7_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 8 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-8+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-8+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_8_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_8_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_8_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_8_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 9 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-9+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-9+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_9_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_9_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_9_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_9_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 10 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-10+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-10+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_10_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_10_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_10_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_10_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 11 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-11+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-11+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_11_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_11_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_11_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_11_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 12 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-12+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-12+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_12_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_12_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_12_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_12_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 13 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-13+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-13+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_13_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_13_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_13_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_13_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 14 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-14+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-14+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_14_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_14_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_14_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_14_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 18 dailycntrouterprt subitem 15 >> .\query_log.txt 
bcp "SELECT   CONVERT(date, GETDATE()-15+1) AS "dateOfQuery"   , right(n.nodeAddress, 12) AS nodeAddress   , count(*) AS count   FROM   ekadb.ekadb.ROUTE_DATA_POINT rd   LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID =  rd.entityID   WHERE   rd.entityID !=  rd.gwID   AND   rd.routeDataTimeStamp BETWEEN   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15-2)) AS BIGINT))*1000 AND   (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15+1)) AS BIGINT))*1000   AND   (rd.routeFlagCodeID & 1) =  1   GROUP BY n.nodeAddress" queryout .\Query_dailycntrouterprt_15_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_dailycntrouterprt_15_DataWithoutHeaders.csv %resultDir%\dailycntrouterprt_15_DataWithoutHeaders.csv 
del Query_dailycntrouterprt_15_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 19 allmasterconncount subitem 1 >> .\query_log.txt 
bcp "SELECT convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) AS TsDay , right(n.nodeAddress, 12) AS nodeAddress , cast(gsd.generalStatisticValue AS int) AS statVal FROM ekadb.ekadb.node n , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd LEFT JOIN ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd ON (bsd.statsDataPointID =  gsd.statsDataPointID) WHERE n.nodeID =  bsd.entityID AND (gsd.generalStatisticCodeID =  15) AND bsd.statsTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_allmasterconncount_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_allmasterconncount_1_DataWithoutHeaders.csv %resultDir%\allmasterconncount_DataWithoutHeaders.csv 
del Query_allmasterconncount_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Query 20 allslaveconncount subitem 1 >> .\query_log.txt 
bcp "SELECT convert(nvarchar, DATEADD(s, bsd.statsTimestamp/1000, '1970-01-01'), 101) AS TsDay , right(n.nodeAddress, 12) AS nodeAddress , cast(gsd.generalStatisticValue AS int) AS statVal FROM ekadb.ekadb.node n , ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd LEFT JOIN ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd ON (bsd.statsDataPointID =  gsd.statsDataPointID) WHERE n.nodeID =  bsd.entityID AND (gsd.generalStatisticCodeID =  16) AND bsd.statsTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()-15)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Query_allslaveconncount_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Query_allslaveconncount_1_DataWithoutHeaders.csv %resultDir%\allslaveconncount_DataWithoutHeaders.csv 
del Query_allslaveconncount_1_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Time_adjust_error subitem 1 >> .\query_log.txt
bcp "SELECT dateadd(s,statusPointTimestamp/1000,'19700101') AS statusPointTS,* FROM NODE_STATUS_DATA_POINT WHERE nodeStatusCodeID =  60 AND statusPointTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()- 30)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000" queryout .\Time_adjust_error.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Time_adjust_error.csv %resultDir%\Time_adjust_error.csv 
del Time_adjust_error.csv

echo Time_sync_greater_than_spec_value subitem 1 >> .\query_log.txt
echo =======================================  >> .\query_log.txt 
bcp "SELECT * , DATEADD(s,statusPointTimestamp/1000,'19700101') AS statusPointTS FROM NODE_STATUS_DATA_POINT WHERE nodeStatusCodeID =  49 AND statusPointTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE()- 30)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE())) AS BIGINT))*1000 AND (cast(statusDataValue AS int)) > 60" queryout .\Time_sync_greater_than_spec_value.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Time_sync_greater_than_spec_value.csv %resultDir%\Time_sync_greater_than_spec_value.csv 
del Time_sync_greater_than_spec_value.csv

echo Reset_cause subitem 1 >> .\query_log.txt
echo =======================================  >> .\query_log.txt 
bcp "SELECT DATEADD(s, ns.statusPointTimestamp/1000, '1970-01-01') statusTS , ns.nodeID, n.nodeSN, n.nodeAddress, n.nodeSWRev, ns.nodeStatusCodeID, (ns.statusDataValue & 255) AS resetCode FROM ekadb.ekadb.NODE_STATUS_DATA_POINT ns, ekadb.ekadb.NODE n WHERE ns.nodeID =  n.nodeID AND ns.nodeStatusCodeID =  48 AND ns.statusPointTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', CONVERT(date, GETDATE() - 15)) AS BIGINT))*1000 AND (cast(DATEDIFF(s, '1970-01-01', GETDATE()) AS BIGINT))*1000 ORDER BY statusTS " queryout .\Reset_cause.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y Reset_cause.csv %resultDir%\Reset_cause.csv 
del Reset_cause.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 1 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-1+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-1+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_1_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_1_DataWithoutHeaders.csv 
del gateway_Reset_Count_1_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 2 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-2+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-2+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_2_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_2_DataWithoutHeaders.csv 
del gateway_Reset_Count_2_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 3 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-3+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-3+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_3_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_3_DataWithoutHeaders.csv 
del gateway_Reset_Count_3_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 4 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-4+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-4+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_4_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_4_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_4_DataWithoutHeaders.csv 
del gateway_Reset_Count_4_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 5 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-5+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-5+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_5_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_5_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_5_DataWithoutHeaders.csv 
del gateway_Reset_Count_5_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 6 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-6+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-6+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_6_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_6_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_6_DataWithoutHeaders.csv 
del gateway_Reset_Count_6_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 7 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-7+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-7+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_7_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gateway_Reset_Count_7_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_7_DataWithoutHeaders.csv 
del gateway_Reset_Count_7_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 8 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-8+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-8+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_8_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_8_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_8_DataWithoutHeaders.csv 
del gateway_Reset_Count_8_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 9 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-9+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-9+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_9_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_9_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_9_DataWithoutHeaders.csv 
del gateway_Reset_Count_9_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 10 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-10+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-10+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_10_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_10_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_10_DataWithoutHeaders.csv 
del gateway_Reset_Count_10_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 11 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-11+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-11+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_11_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gateway_Reset_Count_11_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_11_DataWithoutHeaders.csv 
del gateway_Reset_Count_11_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 12 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-12+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-12+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_12_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gateway_Reset_Count_12_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_12_DataWithoutHeaders.csv 
del gateway_Reset_Count_12_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 13 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-13+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-13+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_13_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_13_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_13_DataWithoutHeaders.csv 
del gateway_Reset_Count_13_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 14 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-14+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-14+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_14_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gateway_Reset_Count_14_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_14_DataWithoutHeaders.csv 
del gateway_Reset_Count_14_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo gateway_Reset_Count subitem 15 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-15+1) AS "dateOfQuery", count(*) AS ""Gateway Reset Count (Last 24 Hours)"" FROM ekadb.ekadb.GW_LOG_ENTRY gwle WHERE gwle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-15+1) - 86400 AND gwle.gwLogCodeID=1601;" queryout .\gateway_Reset_Count_15_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gateway_Reset_Count_15_DataWithoutHeaders.csv %resultDir%\gateway_Reset_Count_15_DataWithoutHeaders.csv 
del gateway_Reset_Count_15_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 1 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-1+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-1+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_1_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_1_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_1_DataWithoutHeaders.csv 
del gap_Fill_Requests_1_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 2 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-2+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-2+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_2_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_2_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_2_DataWithoutHeaders.csv 
del gap_Fill_Requests_2_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 3 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-3+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-3+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_3_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_3_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_3_DataWithoutHeaders.csv 
del gap_Fill_Requests_3_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 4 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-4+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-4+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_4_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_4_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_4_DataWithoutHeaders.csv 
del gap_Fill_Requests_4_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 5 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-5+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-5+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_5_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_5_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_5_DataWithoutHeaders.csv 
del gap_Fill_Requests_5_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 6 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-6+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-6+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_6_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_6_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_6_DataWithoutHeaders.csv 
del gap_Fill_Requests_6_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 7 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-7+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-7+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_7_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gap_Fill_Requests_7_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_7_DataWithoutHeaders.csv 
del gap_Fill_Requests_7_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 8 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-8+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-8+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_8_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_8_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_8_DataWithoutHeaders.csv 
del gap_Fill_Requests_8_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 9 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-9+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-9+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_9_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_9_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_9_DataWithoutHeaders.csv 
del gap_Fill_Requests_9_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 10 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-10+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-10+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_10_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gap_Fill_Requests_10_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_10_DataWithoutHeaders.csv 
del gap_Fill_Requests_10_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 11 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-11+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-11+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_11_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_11_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_11_DataWithoutHeaders.csv 
del gap_Fill_Requests_11_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 12 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-12+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-12+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_12_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_12_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_12_DataWithoutHeaders.csv 
del gap_Fill_Requests_12_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 13 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-13+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-13+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_13_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y gap_Fill_Requests_13_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_13_DataWithoutHeaders.csv 
del gap_Fill_Requests_13_DataWithoutHeaders.csv 

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 14 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-14+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-14+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_14_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_14_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_14_DataWithoutHeaders.csv 
del gap_Fill_Requests_14_DataWithoutHeaders.csv

echo =======================================  >> .\query_log.txt 
echo Gap_Fill_Requests subitem 15 >> .\query_log.txt
bcp "SELECT CONVERT(date, GETDATE()-15+1) AS "dateOfQuery", count(*) AS ""Gap fill requests (Last 24 Hours)"" FROM ekadb.ekadb.GW_NODE_LOG_ENTRY nle WHERE nle.gatewayTimeStamp / 1000 > datediff(s, '1970-01-01', GETDATE()-15+1) - 86400 AND nle.gwNodeLogCodeID=408;" queryout .\gap_Fill_Requests_15_DataWithoutHeaders.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y gap_Fill_Requests_15_DataWithoutHeaders.csv %resultDir%\gap_Fill_Requests_15_DataWithoutHeaders.csv 
del gap_Fill_Requests_15_DataWithoutHeaders.csv

SET PowerShellScriptPath=%~dp0%Zip.ps1
SET srcPath=%currentDir%%resultDir%
SET destPath=%currentDir%%resultDir%.zip
PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' '%srcPath%' '%destPath%'"

rmdir /s /q %srcPath%

GOTO end
:endprase
echo "Usage: historicData.bat <serverip> <username> <password>"
:end