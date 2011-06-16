#include "yukon.h"

#include "PointResponseDatabaseDao.h"
#include "PointResponse.h"
#include "database_util.h"

#include "ccid.h"

extern ULONG _CC_DEBUG;
extern double _IVVC_DEFAULT_DELTA;

using std::vector;
using std::string;
using namespace Cti::Database;

namespace Cti {
namespace CapControl {
namespace Database {

const string PointResponseDatabaseDao::_selectSql = "select BankID,PointID,PreOpValue,Delta,StaticDelta from dynamicccmonitorpointresponse ";

PointResponseDatabaseDao::PointResponseDatabaseDao()
{

}

vector<PointResponse> PointResponseDatabaseDao::getPointResponsesByBankId(int bankId)
{
    DatabaseConnection databaseConnection;
    static const string sql = _selectSql + " where BankID = ? ";

    vector<PointResponse> pointResponses;

    DatabaseReader dbReader(databaseConnection,sql);

    dbReader << bankId;

    performDatabaseOperation(dbReader,pointResponses);

    return pointResponses;
}

vector<PointResponse> PointResponseDatabaseDao::getPointResponsesByPointId(int pointId)
{
    DatabaseConnection databaseConnection;
    static const string sql = _selectSql + " where PointID = ? ";

    vector<PointResponse> pointResponses;

    DatabaseReader dbReader(databaseConnection,sql);

    dbReader << pointId;

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
                                    " WHERE BankId = ? AND PointId = ? ";

    DatabaseWriter dbUpdater(databaseConnection, sql);

    string tempString = "N";

    if (pointResponse.getStaticDelta())
    {
        tempString = "Y";
    }

    dbUpdater << pointResponse.getPreOpValue()
              << pointResponse.getDelta()
              << tempString
              << pointResponse.getBankId()
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

    dbInserter << pointResponse.getBankId()
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
        long bankId,pointId;
        double preOpValue,delta;
        bool staticDelta;
        string tempString;

        reader["BankID"] >> bankId;
        reader["PointID"] >> pointId;
        reader["PreOpValue"] >> preOpValue;
        reader["Delta"] >> delta;
        reader["StaticDelta"] >> tempString;

        staticDelta = (tempString=="Y"?true:false);

        PointResponse pointResponse(pointId,bankId,preOpValue,delta,staticDelta);

        pointResponses.push_back(pointResponse);
    }
}

}
}
}
