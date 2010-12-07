#pragma once

#include <rw/tvslist.h>
#include <rw/tvhdict.h>


#include <rw/thr/threadid.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/guard.h>

#include "dlldefs.h"
#include "dllbase.h"

#include "sema.h"
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
void setDatabaseParams(const string& dbDll, const string& dbName,
                       const string& dbUser, const string& dbPassword );

// returns a SAConnection if successful and connection is valid, returns NULL if not.
IM_EX_CTIBASE SAConnection*  getNewConnection();
IM_EX_CTIBASE void releaseDBConnection(SAConnection *connection);

IM_EX_CTIBASE std::string assignSQLPlaceholders(const std::string &sql);

IM_EX_CTIBASE bool executeUpdater( Cti::Database::DatabaseWriter &updater );

