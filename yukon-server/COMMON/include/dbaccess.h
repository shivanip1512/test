#pragma once

#include "dllbase.h"
#include "dlldefs.h"

class SAConnection;

namespace Cti
{
    namespace Database
    {
        class DatabaseWriter;
    }
}

//various database connection options
IM_EX_CTIBASE
void setDatabaseParams(const std::string& dbDll, const std::string& dbName,
                       const std::string& dbUser, const std::string& dbPassword );

// returns a SAConnection if successful and connection is valid, returns NULL if not.
IM_EX_CTIBASE SAConnection*  getNewConnection();
IM_EX_CTIBASE void releaseDBConnection(SAConnection *connection);

IM_EX_CTIBASE std::string assignSQLPlaceholders(const std::string &sql);

IM_EX_CTIBASE bool executeUpdater( Cti::Database::DatabaseWriter &updater );

