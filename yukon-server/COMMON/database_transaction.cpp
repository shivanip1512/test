#include "precompiled.h"

#include "database_transaction.h"

namespace Cti {
namespace Database {

DatabaseTransaction::DatabaseTransaction(DatabaseConnection &conn) :
    _conn(conn)
{
    _conn.beginTransaction();
}

DatabaseTransaction::~DatabaseTransaction()
{
    _conn.commitTransaction();
}

}
}// Namespace Cti::Database
