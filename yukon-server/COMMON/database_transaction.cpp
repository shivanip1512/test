#include "precompiled.h"

#include "database_transaction.h"

namespace Cti {
namespace Database {

DatabaseTransaction::DatabaseTransaction(DatabaseConnection &conn) :
    _conn(&conn)
{
    _conn->beginTransaction();
}

bool DatabaseTransaction::rollback()
{
    bool success = _conn->rollbackTransaction();

    _conn = 0;

    return success;
}

DatabaseTransaction::~DatabaseTransaction()
{
    _conn && _conn->commitTransaction();
}

}
}
