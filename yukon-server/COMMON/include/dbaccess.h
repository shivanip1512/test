#pragma once

#include "dllbase.h"
#include "dlldefs.h"

#include <functional>

class SAConnection;

//various database connection options
IM_EX_CTIBASE
void setDatabaseParams(const std::string& dbType, const std::string& dbName,
                       const std::string& dbUser, const std::string& dbPassword );

// returns a SAConnection if successful and connection is valid, returns NULL if not.
IM_EX_CTIBASE SAConnection*  getNewConnection();
IM_EX_CTIBASE extern std::function<SAConnection*(void)> gDatabaseConnectionFactory;  //  to override in unit tests
IM_EX_CTIBASE void releaseDBConnection(SAConnection *connection);

IM_EX_CTIBASE std::string assignSQLPlaceholders(const std::string &sql);

