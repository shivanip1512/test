#include "precompiled.h"

#include "PointResponseDatabaseDao.h"
#include "database_util.h"
#include "database_reader.h"

#include "ccid.h"

extern unsigned long _CC_DEBUG;

using std::vector;
using std::string;
using namespace Cti::Database;

namespace Cti {
namespace CapControl {
namespace Database {

/*  - in the spirit of monitor points...
 
SELECT
    DMPR.DeviceID,
    DMPR.PointID,
    DMPR.PreOpValue,
    DMPR.Delta,
    DMPR.StaticDelta,
    X.SubstationBusID
FROM (
    SELECT
        FB.DeviceID AS PAObjectID, FS.SubstationBusId
    FROM
        CCFeederBankList FB
        JOIN CCFeederSubAssignment FS ON FB.FeederID = FS.FeederID
    UNION
    SELECT
        RTZ.RegulatorId AS PAObjectID, Z.SubstationBusId
    FROM
        RegulatorToZoneMapping RTZ
        JOIN Zone Z ON RTZ.ZoneId = Z.ZoneId
    UNION
    SELECT
        P.PAObjectID AS PAObjectID, Z.SubstationBusId
    FROM
        POINT P
        JOIN PointToZoneMapping PTZ ON P.POINTID = PTZ.PointId
        JOIN Zone Z ON PTZ.ZoneId = Z.ZoneId
    ) X
JOIN DynamicCCMonitorPointResponse DMPR ON X.PAObjectID = DMPR.DeviceId
 
--WHERE X.SubstationBusId = ?
--WHERE DMPR.DeviceId = ? 
--WHERE DMPR.PointID = ? 
 
*/

const string PointResponseDatabaseDao::_selectSqlForBanksBySubBus = "select DMPR.DeviceID,DMPR.PointID,DMPR.PreOpValue,DMPR.Delta,DMPR.StaticDelta, FS.SubstationBusId "
                                                            "FROM dynamicccmonitorpointresponse DMPR, ccfeederbanklist FB, ccfeedersubassignment FS "
                                                            "WHERE DMPR.DeviceID = FB.DeviceID "
                                                            "AND FB.feederID = FS.feederID ";
const string PointResponseDatabaseDao::_selectSqlForRegulatorsBySubBus = "select DMPR.DeviceID,DMPR.PointID,DMPR.PreOpValue,DMPR.Delta,DMPR.StaticDelta, Z.SubstationBusId "
                                                            "FROM dynamicccmonitorpointresponse DMPR, regulatortozonemapping RTZ, zone Z "
                                                            "WHERE DMPR.DeviceID = RTZ.RegulatorID "
                                                            "AND RTZ.zoneID = Z.zoneID ";
const string PointResponseDatabaseDao::_selectSqlForAdditionalBySubBus = "select DMPR.DeviceID,DMPR.PointID,DMPR.PreOpValue,DMPR.Delta,DMPR.StaticDelta, Z.SubstationBusId "
                                                            "FROM dynamicccmonitorpointresponse DMPR, pointtozonemapping PTZ, zone Z, point P "
                                                            "WHERE DMPR.DeviceID = P.paobjectid "
                                                            "AND P.pointid = PTZ.pointid "
                                                            "and PTZ.zoneid = Z.zoneid ";

const string PointResponseDatabaseDao::_selectSql = _selectSqlForBanksBySubBus + " UNION " + _selectSqlForRegulatorsBySubBus + " UNION " + _selectSqlForAdditionalBySubBus;

PointResponseDatabaseDao::PointResponseDatabaseDao()
{

}

vector<PointResponse> PointResponseDatabaseDao::getPointResponsesBySubBusId(int subBusId)
{
    DatabaseConnection databaseConnection;
    static const string sql = _selectSqlForBanksBySubBus + " AND FS.SubstationBusID = ? " + " UNION " + 
                              _selectSqlForRegulatorsBySubBus + " AND Z.SubstationBusID = ? " + " UNION " + 
                              _selectSqlForAdditionalBySubBus + " AND Z.SubstationBusID = ? ";

    vector<PointResponse> pointResponses;

    DatabaseReader dbReader(databaseConnection,sql);

    //intentional insertion of same parameter to dbReader.
    dbReader << subBusId << subBusId << subBusId;

    performDatabaseOperation(dbReader,pointResponses);

    return pointResponses;
}

vector<PointResponse> PointResponseDatabaseDao::getAllPointResponses()
{
    DatabaseConnection databaseConnection;

    vector<PointResponse> pointResponses;

    DatabaseReader dbReader(databaseConnection,_selectSql);

    performDatabaseOperation(dbReader,pointResponses);

    return pointResponses;
}

bool PointResponseDatabaseDao::update(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse)
{
    static const string sql = "UPDATE dynamicccmonitorpointresponse "
                                    " SET PreOpValue = ?,Delta = ?,StaticDelta = ? "
                                    " WHERE DeviceId = ? AND PointId = ? ";

    DatabaseWriter dbUpdater(databaseConnection, sql);

    string tempString = "N";

    if (pointResponse.getStaticDelta())
    {
        tempString = "Y";
    }

    dbUpdater << pointResponse.getPreOpValue()
              << pointResponse.getDelta()
              << tempString
              << pointResponse.getDeviceId()
              << pointResponse.getPointId();

    // require both of these flags to show the debug on an update
    const LogDebug logDebug = (_CC_DEBUG & (CC_DEBUG_DATABASE | CC_DEBUG_DYNPOINTRESPONSE))
                              == (CC_DEBUG_DATABASE | CC_DEBUG_DYNPOINTRESPONSE);

    // if Nothing to update. Quietly return false;
    return executeUpdater( dbUpdater, __FILE__, __LINE__, logDebug, LogNoRowsAffected::Disable );
}

bool PointResponseDatabaseDao::insert(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse)
{
    static const string sql = "INSERT INTO dynamicccmonitorpointresponse values(?, ?, ?, ?, ?)";

    DatabaseWriter dbInserter(databaseConnection, sql);

    string tempString = "N";

    if (pointResponse.getStaticDelta())
    {
        tempString = "Y";
    }

    dbInserter << pointResponse.getDeviceId()
               << pointResponse.getPointId()
               << pointResponse.getPreOpValue()
               << pointResponse.getDelta()
               << tempString;

    return executeCommand( dbInserter, __FILE__, __LINE__, LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) );
}

bool PointResponseDatabaseDao::save(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse)
{
    //Attempt to update, if false insert it.
    if( ! update( databaseConnection,pointResponse ))
    {
        return insert(databaseConnection,pointResponse);
    }

    return true; // Update was successful!
}

bool PointResponseDatabaseDao::performDatabaseOperation(DatabaseReader& reader, vector<PointResponse>& pointResponses)
{
    if( ! executeCommand( reader, __FILE__, __LINE__, LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
    {
        return false;
    }

    buildPointResponseFromReader(reader,pointResponses);
    
    return true; // No error occured!
}

void PointResponseDatabaseDao::buildPointResponseFromReader(DatabaseReader& reader, vector<PointResponse>& pointResponses)
{
    while(reader())
    {
        long deviceId,pointId, subBusId;
        double preOpValue,delta;
        bool staticDelta;
        string tempString;

        reader["DeviceID"] >> deviceId;
        reader["PointID"] >> pointId;
        reader["PreOpValue"] >> preOpValue;
        reader["Delta"] >> delta;
        reader["StaticDelta"] >> tempString;
        reader["SubstationBusId"] >> subBusId;

        staticDelta = (tempString=="y");

        PointResponse pointResponse(pointId,deviceId,preOpValue,delta,staticDelta, subBusId);

        pointResponses.push_back(pointResponse);
    }
}

}
}
}
