IF "%~1"=="" GOTO endparse
IF "%~2"=="" GOTO endparse
IF "%~2"=="" GOTO endparse
del .\query_log.txt 
echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "select g.gwName,g.gwSN,gr.gwRadioMACAddr,g.gwRouteColor,g.controlRate from ekadb.ekadb.gateway g left join ekadb.ekadb.GW_RADIO gr on (gr.gwID = g.gwID) where gr.gwRadioMACAddr <> 'FFFFFFFFFFFF'" queryout .\Query_GW.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_GW.csv GW.csv 
del Query_GW.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "select n.nodeSWRev,count(*) from ekadb.ekadb.node n group by n.nodeSWRev" queryout .\Query_NodeSWVer.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_NodeSWVer.csv NodeSWVer.csv 
del Query_NodeSWVer.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "select primaryRoutes.nodeAddress ,nw.nodeSWRev ,primaryRoutes.nextAddr ,primaryRoutes.gwAddr ,nd.etxBand ,primaryRoutes.routeHops ,primaryRoutes.routeCost ,nd.currentRateID ,nd.currentPowerID FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.NODE nw ,( SELECT right(n.nodeAddress,12) as nodeAddress ,substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + rd.nextHopAddress),16,40) as nextAddr ,substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + rd.destinationAddress),16,40) as gwAddr ,cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + right(replicate(' ',3) + rd.hopCount,3)),14,3) as smallint) as routeHops ,cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + right(replicate(' ',3) + rd.totalCost,3)),14,3) as smallint) as routeCost ,cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + right(replicate(' ',10) + rd.entityID,10)),14,10) as int) as entityID FROM ekadb.ekadb.ROUTE_DATA_POINT rd left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID WHERE rd.entityID != rd.gwID AND rd.routeDataTimeStamp between (CAST(DATEDIFF(s,'1970-01-01',CONVERT(date,GETDATE()-1)) AS BIGINT))*1000 and (CAST(DATEDIFF(s,'1970-01-01',CONVERT(date,GETDATE())) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) = 1 GROUP BY n.nodeAddress ) as primaryRoutes where nd.entityID = primaryRoutes.entityID and nd.neighborAddress = primaryRoutes.nextAddr and primaryRoutes.nodeAddress = right(nw.nodeAddress,12) and nd.neighborDataTimestamp between (CAST(DATEDIFF(s,'1970-01-01',CONVERT(date,GETDATE()-3)) AS BIGINT))*1000 and (CAST(DATEDIFF(s,'1970-01-01',CONVERT(date,GETDATE())) AS BIGINT))*1000" queryout .\Query_NetworkStatus.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_NetworkStatus.csv NetworkStatus.csv 
del Query_NetworkStatus.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "select n.nodeAddress,rfn.SerialNumber ,n.nodeSN ,n.nodeHWRev ,n.nodeSWRev ,n.inNetwork ,n.inNetworkTS ,n.nodeProductNumber ,loc.Latitude ,loc.Longitude ,y.Type from yukon.dbo.YukonPAObject y left join yukon.dbo.RfnAddress rfn on rfn.DeviceId = y.PAObjectID left join yukon.dbo.PaoLocation loc on loc.PAObjectId = y.PAObjectID left join ekadb.ekadb.SENSOR_SN sn on rfn.SerialNumber = sn.sensorSN left join ekadb.ekadb.node n on n.nodeID = sn.nodeID where y.Type <> 'RFN Relay' and y.Type <> 'RF Gateway' and y.Type <> 'GWY-800'" queryout .\Query_MeterLocationsInYukon.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_MeterLocationsInYukon.csv MeterLocationsInYukon.csv 
del Query_MeterLocationsInYukon.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "select n.nodeAddress ,n.nodeSN ,n.nodeHWRev ,n.nodeSWRev ,n.inNetwork ,n.inNetworkTS ,n.nodeProductNumber ,loc.Latitude ,loc.Longitude from yukon.dbo.YukonPAObject y left join yukon.dbo.RfnAddress rfn on rfn.DeviceId = y.PAObjectID left join yukon.dbo.PaoLocation loc on loc.PAObjectId = y.PAObjectID left join ekadb.ekadb.node n on rfn.SerialNumber = n.nodeSN where y.Type = 'RFN Relay'" queryout .\Query_RelayLocationsInYukon.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_RelayLocationsInYukon.csv RelayLocationsInYukon.csv 
del Query_RelayLocationsInYukon.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "select y.PAOName as gwName ,gw.gwSN ,gr.gwRadioMACAddr ,gw.gwSWRev ,gw.gwRouteColor ,gw.controlRate ,gw.gwTotalNodes ,gw.gwTotalReadyNodes ,gw.gwTotalNotReadyNodes ,loc.Latitude ,loc.Longitude from yukon.dbo.YukonPAObject y left join yukon.dbo.RfnAddress rfn on rfn.DeviceId = y.PAObjectID left join yukon.dbo.PaoLocation loc on loc.PAObjectId = y.PAObjectID left join ekadb.ekadb.GATEWAY gw on gw.gwName = y.PAOName left join ekadb.ekadb.GW_RADIO gr on gw.gwID = gr.gwID where gr.gwRadioMACAddr <> 'FFFFFFFFFFFF'" queryout .\Query_GatewayLocationsInYukon.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_GatewayLocationsInYukon.csv GatewayLocationsInYukon.csv 
del Query_GatewayLocationsInYukon.csv 

echo ======================================= >> .\query_log.txt 
echo Query >> .\query_log.txt 
bcp "select	primaryRoutes.nodeAddress ,substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + primaryRoutes.nextAddr),14,40) as nextAddr ,substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + primaryRoutes.gwAddr),14,40) as gwAddr ,substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + right(replicate(' ',16) + nw.nodeSWRev,16)),16,40) as nodeSWRev ,cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + right(replicate(' ',3) + nd.etxBand,3)),14,3) as smallint) as etxBand ,cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + right(replicate(' ',3) + primaryRoutes.routeHops,3)),14,3) as smallint) as routeHops ,cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + right(replicate(' ',3) + primaryRoutes.routeCost,3)),14,3) as smallint) as routeCost ,cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + right(replicate(' ',3) + nd.currentRateID,3)),14,3) as smallint) as currentRateID ,cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + right(replicate(' ',3) + nd.currentPowerID,3)),14,3) as smallint) as currentPowerID ,cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + right(replicate(' ',4) + primaryRoutes.neighborCount,4)),14,4) as smallint) as neighborCount	,substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + g.gwName),14,16) as gwName ,substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + convert(varchar,nd.neighborLinkCost)),14,4) as linkCost ,cast(substring(max(right(replicate(' ',13) + cast(nd.neighborDataTimestamp as nvarchar),13) + right(replicate(' ',3) + nd.numSamples,3)),14,3) as smallint) as numSamples FROM ekadb.ekadb.NEIGHBOR_DATA_POINT nd ,ekadb.ekadb.GATEWAY g left join ekadb.ekadb.GW_RADIO gr on gr.gwID = g.gwID ,ekadb.ekadb.NODE nw ,(SELECT right(n.nodeAddress,12) as nodeAddress ,substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + rd.nextHopAddress),16,40) as nextAddr ,substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + rd.destinationAddress),16,40) as gwAddr ,cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + right(replicate(' ',3) + rd.hopCount,3)),14,3) as smallint) as routeHops ,cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + right(replicate(' ',3) + rd.totalCost,3)),14,3) as smallint) as routeCost ,cast(substring(max(right(replicate(' ',13) + cast(rd.routeDataTimeStamp as nvarchar),13) + right(replicate(' ',10) + rd.entityID,10)),14,10) as int) as entityID ,cast(substring(max(right(replicate(' ',13) + cast(bsd.gatewayTimestamp as nvarchar),13) + right(replicate(' ',3) + gsd.generalStatisticValue,3)),14,3) as smallint) as neighborCount FROM ekadb.ekadb.ROUTE_DATA_POINT rd left join ekadb.ekadb.NODE n on n.nodeID = rd.entityID left join ekadb.ekadb.BROADCAST_STATS_DATA_POINT bsd on n.nodeID = bsd.entityID left join ekadb.ekadb.BROADCAST_GENERAL_STATS_DATA gsd on (bsd.statsDataPointID = gsd.statsDataPointID) WHERE rd.entityID != rd.gwID AND rd.routeDataTimeStamp between (CAST(DATEDIFF(s,'1970-01-01',CONVERT(date,GETDATE() - 3)) AS BIGINT))*1000 and (CAST(DATEDIFF(s,'1970-01-01',CONVERT(date,GETDATE())) AS BIGINT))*1000 AND (rd.routeFlagCodeID & 1) = 1 AND gsd.generalStatisticCodeID = 48	GROUP BY n.nodeAddress) as primaryRoutes where nd.entityID = primaryRoutes.entityID	and nd.neighborAddress = primaryRoutes.nextAddr and primaryRoutes.nodeAddress = right(nw.nodeAddress,12) and gr.gwRadioMACAddr = primaryRoutes.gwAddr and nd.neighborDataTimestamp between (CAST(DATEDIFF(s,'1970-01-01',CONVERT(date,GETDATE() - 8)) AS BIGINT))*1000 and (CAST(DATEDIFF(s,'1970-01-01',CONVERT(date,GETDATE())) AS BIGINT))*1000 GROUP BY primaryRoutes.nodeAddress" queryout .\Query_NetworkData.csv -S%1 -U %2 -P %3 -c -t,>> .\query_log.txt 
copy /b /y Query_NetworkData.csv NetworkData.csv 
del Query_NetworkData.csv 

GOTO end
:endprase
echo "Usage: locationData.bat <serverip> <username> <password>"
:end
