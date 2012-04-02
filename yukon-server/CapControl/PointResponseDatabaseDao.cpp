#include "precompiled.h"

#include "PointResponseDatabaseDao.h"
#include "PointResponse.h"
#include "database_util.h"

#include "ccid.h"

extern unsigned long _CC_DEBUG;
extern double _IVVC_DEFAULT_DELTA;

using std::vector;
using std::string;
using namespace Cti::Database;

namespace Cti {
namespace CapControl {
namespace Database {

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

vector<PointResponse> PointResponseDatabaseDao::getPointResponsesByDeviceId(int deviceId)
{
    DatabaseConnection databaseConnection;
    static const string sql = _selectSqlForBanksBySubBus + " AND DMPR.DeviceID = ? " + " UNION " + 
                              _selectSqlForRegulatorsBySubBus + " AND DMPR.DeviceID = ? " + " UNION " + 
                              _selectSqlForAdditionalBySubBus + " AND DMPR.DeviceID = ? ";

    vector<PointResponse> pointResponses;

    DatabaseReader dbReader(databaseConnection,sql);

    dbReader << deviceId << deviceId << deviceId;

    performDatabaseOperation(dbReader,pointResponses);

    return pointResponses;
}

vector<PointResponse> PointResponseDatabaseDao::getPointResponsesByPointId(int pointId)
{
    DatabaseConnection databaseConnection;
    static const string sql = _selectSqlForBanksBySubBus + " AND DMPR.PointID = ? " + " UNION " + 
                              _selectSqlForRegulatorsBySubBus + " AND DMPR.PointID = ? " + " UNION " + 
                              _selectSqlForAdditionalBySubBus + " AND DMPR.PointID = ? ";

    vector<PointResponse> pointResponses;

    DatabaseReader dbReader(databaseConnection,sql);

    dbReader << pointId << pointId << pointId;

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

bool PointResponseDatabaseDao::update(PointResponse pointResponse)
{
    DatabaseConnection databaseConnection;
    return update(databaseConnection,pointResponse);
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

    bool success = executeDbCommand(dbUpdater,(_CC_DEBUG & CC_DEBUG_DATABASE));

    if (success)
    {
        int rowsAffected = dbUpdater.rowsAffected();
        if (rowsAffected == 0)
        {
            //Nothing to update. Quietly return false;
            return false;
        }
    }

    return success;
}

bool PointResponseDatabaseDao::insert(PointResponse pointResponse)
{
    DatabaseConnection databaseConnection;
    return insert(databaseConnection,pointResponse);
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

    bool success = executeDbCommand(dbInserter,(_CC_DEBUG & CC_DEBUG_DATABASE));

    return success;
}

bool PointResponseDatabaseDao::save(PointResponse pointResponse)
{
    DatabaseConnection databaseConnection;
    return save(databaseConnection,pointResponse);
}

bool PointResponseDatabaseDao::save(Cti::Database::DatabaseConnection& databaseConnection, PointResponse pointResponse)
{
    //Attempt to update, if false insert it.
    bool ret = update(databaseConnection,pointResponse);

    if (ret == false)
    {
        ret = insert(databaseConnection,pointResponse);
    }

    return ret;
}

bool PointResponseDatabaseDao::performDatabaseOperation(DatabaseReader& reader, vector<PointResponse>& pointResponses)
{
    bool success = executeDbCommand(reader,(_CC_DEBUG & CC_DEBUG_DATABASE));

    if (success)
    {
        buildPointResponseFromReader(reader,pointResponses);
    }

    return success;
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
