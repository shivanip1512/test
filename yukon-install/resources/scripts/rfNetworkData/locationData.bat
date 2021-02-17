IF "%~1"=="" GOTO endparse
IF "%~2"=="" GOTO endparse
IF "%~2"=="" GOTO endparse
del .\query_log.txt 

mkdir Results

set hh=%time:~-11,2%
set /a hh=%hh%+100
set hh=%hh:~1%
set runtimestamp=%date:~10,4%%date:~4,2%%date:~7,2%%hh%%time:~3,2%%time:~6,2%
set fileName=locationData_%runtimestamp%
set resultDir=.\Results\%fileName%%

mkdir %resultDir%

SET currentDir=%CD%

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "SELECT g.gwName, g.gwSN, gr.gwRadioMACAddr, g.gwRouteColor, g.controlRate FROM ekadb.ekadb.gateway g LEFT JOIN ekadb.ekadb.GW_RADIO gr ON (gr.gwID = g.gwID) WHERE gr.gwRadioMACAddr <> 'FFFFFFFFFFFF'" queryout .\Query_GW.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_GW.csv %resultDir%\GW.csv 
del Query_GW.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "SELECT n.nodeSWRev, count(*) FROM ekadb.ekadb.node n GROUP BY n.nodeSWRev" queryout .\Query_NodeSWVer.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_NodeSWVer.csv %resultDir%\NodeSWVer.csv 
del Query_NodeSWVer.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "SELECT primaryRoutes.nodeAddress, nw.nodeSWRev, primaryRoutes.nextAddr, primaryRoutes.gwAddr, nd.etxBand, primaryRoutes.routeHops, primaryRoutes.routeCost, nd.currentRateID, nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd, ekadb.ekadb.NODE nw, (SELECT right(n.nodeAddress,12) AS nodeAddress, substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp AS nvarchar),13) + rd.nextHopAddress), 16,40) AS nextAddr, substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp AS nvarchar),13) + rd.destinationAddress),16,40) AS gwAddr, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.hopCount, 3)), 14, 3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ', 13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ', 3) + rd.totalCost, 3)), 14, 3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp AS nvarchar), 13) + right(replicate(' ',10) + rd.entityID,10)),14,10) AS int) AS entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID = rd.entityID WHERE rd.entityID != rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s,'1970-01-01',convert(date,GETDATE()-1)) AS BIGINT))*1000 and (cast(DATEDIFF(s,'1970-01-01', convert(date, GETDATE())) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) = 1 GROUP BY n.nodeAddress ) AS primaryRoutes WHERE nd.entityID = primaryRoutes.entityID and nd.neighborAddress = primaryRoutes.nextAddr and primaryRoutes.nodeAddress = right(nw.nodeAddress, 12) and nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s, '1970-01-01', convert(date, GETDATE()-3)) AS BIGINT))*1000 and (cast(DATEDIFF(s, '1970-01-01',convert(date,GETDATE())) AS BIGINT))*1000" queryout .\Query_NetworkStatus.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_NetworkStatus.csv %resultDir%\NetworkStatus.csv 
del Query_NetworkStatus.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "SELECT n.nodeAddress, rfn.SerialNumber, n.nodeSN, n.nodeHWRev, n.nodeSWRev, n.inNetwork, n.inNetworkTS, n.nodeProductNumber, loc.Latitude, loc.Longitude, y.Type FROM yukon.dbo.YukonPAObject y LEFT JOIN yukon.dbo.RfnAddress rfn ON rfn.DeviceId = y.PAObjectID LEFT JOIN yukon.dbo.PaoLocation loc ON loc.PAObjectId = y.PAObjectID LEFT JOIN ekadb.ekadb.SENSOR_SN sn ON rfn.SerialNumber = sn.sensorSN LEFT JOIN ekadb.ekadb.node n ON n.nodeID = sn.nodeID WHERE y.Type <> 'RFN Relay' and y.Type <> 'RF Gateway' and y.Type <> 'GWY-800' and y.Type <> 'GWY-801'" queryout .\Query_MeterLocationsInYukon.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_MeterLocationsInYukon.csv %resultDir%\MeterLocationsInYukon.csv 
del Query_MeterLocationsInYukon.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 

bcp "SELECT n.nodeAddress, n.nodeSN, n.nodeHWRev, n.nodeSWRev, n.inNetwork, n.inNetworkTS, n.nodeProductNumber, loc.Latitude, loc.Longitude FROM yukon.dbo.YukonPAObject y LEFT JOIN yukon.dbo.RfnAddress rfn ON rfn.DeviceId = y.PAObjectID LEFT JOIN yukon.dbo.PaoLocation loc ON loc.PAObjectId = y.PAObjectID LEFT JOIN ekadb.ekadb.node n ON rfn.SerialNumber = n.nodeSN WHERE y.Type = 'RFN Relay'" queryout .\Query_RelayLocationsInYukon.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_RelayLocationsInYukon.csv %resultDir%\RelayLocationsInYukon.csv 
del Query_RelayLocationsInYukon.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "SELECT y.PAOName AS gwName, gw.gwSN, gr.gwRadioMACAddr, gw.gwSWRev, gw.gwRouteColor, gw.controlRate, gw.gwTotalNodes, gw.gwTotalReadyNodes, gw.gwTotalNotReadyNodes, loc.Latitude, loc.Longitude FROM yukon.dbo.YukonPAObject y LEFT JOIN yukon.dbo.RfnAddress rfn ON rfn.DeviceId = y.PAObjectID LEFT JOIN yukon.dbo.PaoLocation loc ON loc.PAObjectId = y.PAObjectID LEFT JOIN ekadb.ekadb.GATEWAY gw ON gw.gwName = y.PAOName LEFT JOIN ekadb.ekadb.GW_RADIO gr ON gw.gwID = gr.gwID WHERE gr.gwRadioMACAddr <> 'FFFFFFFFFFFF'" queryout .\Query_GatewayLocationsInYukon.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_GatewayLocationsInYukon.csv %resultDir%\GatewayLocationsInYukon.csv 
del Query_GatewayLocationsInYukon.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "SELECT primaryRoutes.nodeAddress, substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + primaryRoutes.nextAddr),14,40) AS nextAddr, substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + primaryRoutes.gwAddr),14,40) AS gwAddr, substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + right(replicate(' ',16) + nw.nodeSWRev,16)),16,40) AS nodeSWRev, cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + right(replicate(' ',3) + nd.etxBand,3)),14,3) AS smallint) AS etxBand, cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + right(replicate(' ',3) + primaryRoutes.routeHops,3)),14,3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + right(replicate(' ',3) + primaryRoutes.routeCost,3)),14,3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + right(replicate(' ',3) + nd.currentRateID,3)),14,3) AS smallint) AS currentRateID, cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + right(replicate(' ',3) + nd.currentPowerID,3)),14,3) AS smallint) AS currentPowerID ,cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + right(replicate(' ',4) + primaryRoutes.neighborCount,4)),14,4) AS smallint) AS neighborCount, substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + g.gwName),14,16) AS gwName ,substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + convert(varchar,nd.neighborLinkCost)),14,4) AS linkCost, cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp AS nvarchar),13) + right(replicate(' ',3) + nd.numSamples,3)),14,3) AS smallint) AS numSamples FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd, ekadb.ekadb.GATEWAY g LEFT JOIN ekadb.ekadb.GW_RADIO gr ON gr.gwID = g.gwID, ekadb.ekadb.NODE nw ,(SELECT right(n.nodeAddress,12) AS nodeAddress, substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp AS nvarchar),13) + rd.nextHopAddress),16,40) AS nextAddr, substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp AS nvarchar),13) + rd.destinationAddress),16,40) AS gwAddr, cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp AS nvarchar),13) + right(replicate(' ',3) + rd.hopCount,3)),14,3) AS smallint) AS routeHops, cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp AS nvarchar),13) + right(replicate(' ',3) + rd.totalCost,3)),14,3) AS smallint) AS routeCost, cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp AS nvarchar),13) + right(replicate(' ',10) + rd.entityID,10)),14,10) AS int) AS entityID, cast(substring(max(right(replicate(' ',13) + cast(bsd.gatewayTimestamp AS nvarchar),13) + right(replicate(' ',3) + gsd.generalStatisticValue,3)),14,3) AS smallint) AS neighborCount FROM ekadb.ekadb.ROUTE_DATA_POINT rd LEFT JOIN ekadb.ekadb.NODE n ON n.nodeID = rd.entityID LEFT JOIN ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd ON n.nodeID = bsd.entityID LEFT JOIN ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd ON (bsd.statsDataPointID = gsd.statsDataPointID) WHERE rd.entityID != rd.gwID AND rd.routeDataTimeStamp BETWEEN (cast(DATEDIFF(s,'1970-01-01',convert(date,GETDATE() - 3)) AS BIGINT))*1000 and (cast(DATEDIFF(s,'1970-01-01',convert(date,GETDATE())) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) = 1 AND gsd.generalStatisticCodeID = 48   GROUP BY n.nodeAddress) AS primaryRoutes WHERE nd.entityID = primaryRoutes.entityID and nd.neighborAddress = primaryRoutes.nextAddr and primaryRoutes.nodeAddress = right(nw.nodeAddress,12) and gr.gwRadioMACAddr = primaryRoutes.gwAddr and nd.neighborDataTimestamp BETWEEN (cast(DATEDIFF(s,'1970-01-01',convert(date,GETDATE() - 8)) AS BIGINT))*1000 and (cast(DATEDIFF(s,'1970-01-01',convert(date,GETDATE())) AS BIGINT))*1000 GROUP BY primaryRoutes.nodeAddress" queryout .\Query_NetworkData.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_NetworkData.csv %resultDir%\NetworkData.csv 
del Query_NetworkData.csv 

SET PowerShellScriptPath=%~dp0%Zip.ps1
SET srcPath=%currentDir%%resultDir%
SET destPath=%currentDir%%resultDir%.zip
PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& '%PowerShellScriptPath%' '%srcPath%' '%destPath%'"

rmdir /s /q %srcPath%

GOTO end
:endprase
echo "Usage: locationData.bat <serverip> <username> <password>"
:end