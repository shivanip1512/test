#pragma once

#include "database_connection.h"

namespace Cti {
namespace Database {

class IM_EX_CTIBASE DatabaseTransaction
{
    DatabaseConnection *_conn;

public:

    DatabaseTransaction(DatabaseConnection &conn);

    bool rollback();  //  roll back the transaction immediately

    ~DatabaseTransaction();
};

}
}// Namespace Cti::Database
