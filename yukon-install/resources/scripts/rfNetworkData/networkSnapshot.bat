IF "%~1"=="" GOTO endparse
IF "%~2"=="" GOTO endparse
IF "%~2"=="" GOTO endparse

del .\query_log.txt 

mkdir Results

set hh=%time:~-11,2%
set /a hh=%hh%+100
set hh=%hh:~1%
set runtimestamp=%date:~10,4%%date:~4,2%%date:~7,2%%hh%%time:~3,2%%time:~6,2%
set fileName=networkSnapshot_%runtimestamp%
set resultDir=.\Results\%fileName%%

mkdir %resultDir%
		
SET currentDir=%CD%

echo =======================================  >> .\query_log.txt 
echo Query 1 Electric Node Data >> .\query_log.txt 
bcp "select * from ((select n.nodeID, n.nodeSN, ntc.nodeTypeString, v.gwID, v.gwName, dateadd(s,v.timeStamp/1000,'19700101') as commStatusTime, v.currStatus, ssn.sensorSN, ssn.sensorMfg, ssn.sensorModelNumber, row_number() over (partition by n.nodeID order by ssn.recvdTimeStamp desc) as rn FROM ekadb.ekadb.NODE_CURRENT_COMM_STATUS_VIEW v left join ekadb.ekadb.NODE n on v.nodeID = n.nodeID left join ekadb.ekadb.NODE_TYPE_CODE ntc on n.nodeType = ntc.nodeType left join ekadb.ekadb.SENSOR_SN ssn on n.nodeID = ssn.nodeID) node_meter_assoc LEFT JOIN (SELECT RA.DeviceId AS Meter_Number, RA.Model, RA.SerialNumber, PL.Latitude, PL.Longitude FROM yukon.dbo.RfnAddress RA JOIN yukon.dbo.PaoLocation PL ON RA.DeviceId = PL.PAObjectId) yukonDat ON node_meter_assoc.sensorSN = yukonDat.SerialNumber) WHERE rn = 1" queryout .\yukonData.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt
copy /b /y yukonData.csv %resultDir%\yukonData.csv  
del yukonData.csv 

echo =======================================  >> .\query_log.txt 
echo Query 2 DescendentCount >> .\query_log.txt 
bcp "select DISTINCT concat(right(primaryRoutes.nodeAddress, 2),':', substring(primaryRoutes.nodeAddress, 9,2),':', substring(primaryRoutes.nodeAddress, 7,2),':', substring(primaryRoutes.nodeAddress, 5,2),':', substring(primaryRoutes.nodeAddress, 3,2),':', left(primaryRoutes.nodeAddress, 2)  ) as nodeAddress, nw.nodeID, nw.nodeSWRev, nw.nodeProductNumber, concat(right(primaryRoutes.nextAddr, 2),':', substring(primaryRoutes.nextAddr, 9,2),':', substring(primaryRoutes.nextAddr, 7,2),':', substring(primaryRoutes.nextAddr, 5,2),':', substring(primaryRoutes.nextAddr, 3,2),':', left(primaryRoutes.nextAddr, 2)  ) as nextAddr, concat(right(primaryRoutes.gwAddr, 2),':', substring(primaryRoutes.gwAddr, 9,2),':', substring(primaryRoutes.gwAddr, 7,2),':', substring(primaryRoutes.gwAddr, 5,2),':', substring(primaryRoutes.gwAddr, 3,2),':', left(primaryRoutes.gwAddr, 2)  ) as gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost, nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd, ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress, 12) as nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) as smallint) as routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) as smallint) as routeCost, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID WHERE rd.entityID != rd.gwID AND rd.routeDataTimeStamp between (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-10)) AS BIGINT))*1000 and     (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) = 1 GROUP BY n.nodeAddress) as primaryRoutes where nd.entityID = primaryRoutes.entityID and nd.neighborAddress = primaryRoutes.nextAddr and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12) and nd.neighborDataTimestamp between (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-10)) AS BIGINT))*1000 and (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000 ORDER BY nodeID" queryout .\nmData.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y nmData.csv %resultDir%\nmData.csv
del nmData.csv 

echo =======================================  >> .\query_log.txt 
echo Query 3 Location and Current Node Status >> .\query_log.txt 
bcp "with PRIMARY_ROUTES as (SELECT right(n.nodeAddress, 12) as nodeAddress, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.nextHopAddress), 16, 40) as nextAddr, substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + rd.destinationAddress), 16, 40) as gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp as nvarchar), 13) + right(replicate(' ', 10) + rd.entityID, 10)), 14, 10) as int) as entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd inner join ekadb.ekadb.NODE n on n.nodeID = rd.entityID WHERE rd.routeDataTimeStamp between (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-1)) AS BIGINT))*1000 and (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) = 1 GROUP BY n.nodeAddress) SELECT concat(right(results.node_MAC, 2),':', substring(results.node_MAC, 9,2),':', substring(results.node_MAC, 7,2),':', substring(results.node_MAC, 5,2),':', substring(results.node_MAC, 3,2),':', left(results.node_MAC, 2)  ) as node_MAC,concat(right(results.gwAddr, 2),':', substring(results.gwAddr, 9,2),':', substring(results.gwAddr, 7,2),':', substring(results.gwAddr, 5,2),':', substring(results.gwAddr, 3,2),':', left(results.gwAddr, 2)  ) as gwAddr, Count(*) as used_by FROM (SELECT rfNode.*, nodes.nextAddr FROM (select primaryRoutes.nodeAddress as node_MAC, primaryRoutes.gwAddr FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd, PRIMARY_ROUTES primaryRoutes WHERE nd.entityID = primaryRoutes.entityID and nd.neighborAddress = primaryRoutes.nextAddr and nd.neighborDataTimestamp between (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE()-3)) AS BIGINT))*1000 and (CAST(DATEDIFF(s, '1970-01-01',CONVERT(date, GETDATE())) AS BIGINT))*1000) rfNode LEFT JOIN PRIMARY_ROUTES nodes ON rfNode.node_MAC = nodes.nextAddr) results GROUP BY node_MAC, gwAddr" queryout .\descData.csv -S%1 -U %2 -P %3 -c -t, >> .\query_log.txt 
copy /b /y descData.csv %resultDir%\descData.csv
del descData.csv 

SET PowerShellScriptPath=%~dp0%Zip.ps1
SET srcPath=%currentDir%%resultDir%
SET destPath=%currentDir%%resultDir%.zip
PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' '%srcPath%' '%destPath%'"

rmdir /s /q %srcPath%
cd ..\

GOTO end
:endprase
echo "Usage: networkSnapshot.bat <serverip> <username> <password>"
:end