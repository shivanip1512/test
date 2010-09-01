#include "yukon.h"

#include "PointResponseDatabaseDao.h"
#include "PointResponse.h"
#include "database_util.h"

#include "ccid.h"

extern ULONG _CC_DEBUG;
extern double _IVVC_DEFAULT_DELTA;

using std::vector;
using namespace Cti::Database;

namespace Cti {
namespace CapControl {
namespace Database {

const string PointResponseDatabaseDao::_selectSql = "select BankID,PointID,PreOpValue,Delta from dynamicccmonitorpointresponse ";

PointResponseDatabaseDao::PointResponseDatabaseDao()
{

}

vector<PointResponse> PointResponseDatabaseDao::getPointResponsesByBankId(int bankId)
{
    DatabaseConnection databaseConnection;
    static const string sql = _selectSql + " where BankID = ? ";

    vector<PointResponse> pointResponses;

    DatabaseReader dbReader(databaseConnection,sql);

    performDatabaseOperation(dbReader,pointResponses);

    return pointResponses;
}

vector<PointResponse> PointResponseDatabaseDao::getPointResponsesByPointId(int pointId)
{
    DatabaseConnection databaseConnection;
    static const string sql = _selectSql + " where PointID = ? ";

    vector<PointResponse> pointResponses;

    DatabaseReader dbReader(databaseConnection,sql);

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
    static const string sql = "update dynamicccmonitorpointresponse "
                                    " set preopvalue = ?,delta = ? "
                                    " where bankid = ? AND pointid = ? ";

    DatabaseWriter dbUpdater(databaseConnection, sql);

    dbUpdater << pointResponse.getPreOpValue()
              << pointResponse.getDelta()
              << pointResponse.getBankId()
              << pointResponse.getPointId();

    bool success = executeDbCommand(dbUpdater,(_CC_DEBUG & CC_DEBUG_DATABASE));

    return success;
}

bool PointResponseDatabaseDao::insert(PointResponse pointResponse)
{
    DatabaseConnection databaseConnection;
    static const string sql = "insert into dynamicccmonitorpointresponse values(?, ?, ?, ?)";

    DatabaseWriter dbInserter(databaseConnection, sql);

    dbInserter << pointResponse.getBankId()
               << pointResponse.getPointId()
               << pointResponse.getPreOpValue()
               << pointResponse.getDelta();

    bool success = executeDbCommand(dbInserter,(_CC_DEBUG & CC_DEBUG_DATABASE));

    return success;
}

bool PointResponseDatabaseDao::save(PointResponse pointResponse)
{
    //Attempt to update first, if that fails. Insert it.

    bool ret = update(pointResponse);

    if (ret == false)
    {
        ret = insert(pointResponse);
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

        reader["BankID"] >> bankId;
        reader["PointID"] >> pointId;
        reader["PreOpValue"] >> preOpValue;
        reader["Delta"] >> delta;

        PointResponse pointResponse(pointId,bankId,preOpValue,delta);

        pointResponses.push_back(pointResponse);
    }
}

}
}
}
