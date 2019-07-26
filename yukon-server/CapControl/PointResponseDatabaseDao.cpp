#include "precompiled.h"

#include "PointResponseDatabaseDao.h"
#include "database_util.h"
#include "database_reader.h"
#include "ccutil.h"

#include "ccid.h"

extern unsigned long _CC_DEBUG;

using std::vector;
using std::string;
using namespace Cti::Database;

namespace Cti {
namespace CapControl {
namespace Database {

namespace
{
    const static std::string selectAllSql =
        "SELECT "
            "D.DeviceID, "
            "D.PointID, "
            "D.PreOpValue, "
            "D.Delta, "
            "D.StaticDelta, "
            "X.SubstationBusID, "
            "P.POINTNAME, "
            "Y.PAOName "
        "FROM ( "
            "SELECT "
                "FB.DeviceID AS PAObjectID, FS.SubstationBusId "
            "FROM "
                "CCFeederBankList FB "
                "JOIN CCFeederSubAssignment FS ON FB.FeederID = FS.FeederID "
            "UNION "
            "SELECT "
                "RTZ.RegulatorId AS PAObjectID, Z.SubstationBusId "
            "FROM "
                "RegulatorToZoneMapping RTZ "
                "JOIN Zone Z ON RTZ.ZoneId = Z.ZoneId "
            "UNION "
            "SELECT "
                "P.PAObjectID AS PAObjectID, Z.SubstationBusId "
            "FROM "
                "POINT P "
                "JOIN PointToZoneMapping PTZ ON P.POINTID = PTZ.PointId "
                "JOIN Zone Z ON PTZ.ZoneId = Z.ZoneId "
            ") X "
        "JOIN DynamicCCMonitorPointResponse D ON X.PAObjectID = D.DeviceId "
        "JOIN POINT P ON D.PointID = P.POINTID "
        "JOIN YukonPAObject Y ON Y.PAObjectID = P.PAObjectID";

    const static std::string busFilterSql =
        "WHERE "
            "X.SubStationBusID = ?";

    const static std::string insertSql =
        "INSERT INTO "
            "DynamicCCMonitorPointResponse "
        "VALUES "
            "(?, ?, ?, ?, ?)";

    const static std::string updateSql =
        "UPDATE "
            "DynamicCCMonitorPointResponse "
        "SET "
            "PreOpValue = ?, "
            "Delta = ?, "
            "StaticDelta = ? "
        "WHERE "
            "DeviceId = ? AND "
            "PointId = ? ";
}


PointResponseDatabaseDao::PointResponseDatabaseDao()
{

}

vector<PointResponse> PointResponseDatabaseDao::getPointResponsesBySubBusId(int subBusId)
{
    vector<PointResponse> pointResponses;

    DatabaseConnection databaseConnection;

    DatabaseReader dbReader(databaseConnection, selectAllSql + " " + busFilterSql);

    dbReader << subBusId;

    performDatabaseOperation(dbReader,pointResponses);

    return pointResponses;
}

vector<PointResponse> PointResponseDatabaseDao::getAllPointResponses()
{
    vector<PointResponse> pointResponses;

    DatabaseConnection databaseConnection;

    DatabaseReader dbReader(databaseConnection, selectAllSql);

    performDatabaseOperation(dbReader,pointResponses);

    return pointResponses;
}

bool PointResponseDatabaseDao::update(Cti::Database::DatabaseConnection& databaseConnection, PointResponse & pointResponse)
{
    DatabaseWriter dbUpdater(databaseConnection, updateSql);

    dbUpdater << pointResponse.getPreOpValue()
              << pointResponse.getDelta()
              << serializeFlag( pointResponse.getStaticDelta() )
              << pointResponse.getDeviceId()
              << pointResponse.getPointId();

    // require both of these flags to show the debug on an update
    const LogDebug logDebug = (_CC_DEBUG & (CC_DEBUG_DATABASE | CC_DEBUG_DYNPOINTRESPONSE))
                              == (CC_DEBUG_DATABASE | CC_DEBUG_DYNPOINTRESPONSE);

    // if Nothing to update. Quietly return false;
    return executeUpdater( dbUpdater, CALLSITE, logDebug, LogNoRowsAffected::Disable );
}

bool PointResponseDatabaseDao::insert(Cti::Database::DatabaseConnection& databaseConnection, PointResponse & pointResponse)
{
    DatabaseWriter dbInserter(databaseConnection, insertSql);

    dbInserter << pointResponse.getDeviceId()
               << pointResponse.getPointId()
               << pointResponse.getPreOpValue()
               << pointResponse.getDelta()
               << serializeFlag( pointResponse.getStaticDelta() );

    return executeCommand( dbInserter, CALLSITE, LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) );
}

bool PointResponseDatabaseDao::save(Cti::Database::DatabaseConnection& databaseConnection, PointResponse & pointResponse)
{
    if ( pointResponse.isDirty() ) 
    {
        if ( update( databaseConnection, pointResponse )
             || insert( databaseConnection, pointResponse ) )
        {
            pointResponse.setDirty( false );
        }
        else
        {
            return false;
        }
    }

    return true;
}

bool PointResponseDatabaseDao::performDatabaseOperation(DatabaseReader& reader, vector<PointResponse>& pointResponses)
{
    if( ! executeCommand( reader, CALLSITE, LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
    {
        return false;
    }

    buildPointResponseFromReader(reader,pointResponses);
    
    return true; // No error occurred!
}

void PointResponseDatabaseDao::buildPointResponseFromReader(DatabaseReader& reader, vector<PointResponse>& pointResponses)
{
    while(reader())
    {
        long deviceId,pointId, subBusId;
        double preOpValue,delta;
        bool staticDelta;
        string tempString, pointName, deviceName;

        reader["DeviceID"] >> deviceId;
        reader["PointID"] >> pointId;
        reader["PreOpValue"] >> preOpValue;
        reader["Delta"] >> delta;
        reader["StaticDelta"] >> tempString;
        reader["SubstationBusId"] >> subBusId;

        reader["POINTNAME"] >> pointName;
        reader["PAOName"] >> deviceName;

        staticDelta = deserializeFlag( tempString );

        PointResponse pointResponse(pointId,deviceId,preOpValue,delta,staticDelta, subBusId, pointName, deviceName);

        pointResponses.push_back(pointResponse);
    }
}

}
}
}
