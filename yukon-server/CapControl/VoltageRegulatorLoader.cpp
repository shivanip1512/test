

#include "yukon.h"
#include "ccid.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "VoltageRegulatorLoader.h"
#include "GangOperatedVoltageRegulator.h"
#include "PhaseOperatedVoltageRegulator.h"

#include <string>

extern ULONG _CC_DEBUG;


namespace Cti           {
namespace CapControl    {

VoltageRegulatorManager::VoltageRegulatorMap VoltageRegulatorDBLoader::load(const long Id)
{
    VoltageRegulatorManager::VoltageRegulatorMap loaded;

    loadCore(Id, loaded);

    return loaded;
}


void VoltageRegulatorDBLoader::loadCore(const long Id, VoltageRegulatorManager::VoltageRegulatorMap &voltageRegulators)
{
    static const std::string sql = "SELECT Y.PAObjectID, Y.Category, Y.PAOClass, Y.PAOName, Y.Type, Y.Description,"
                                   " Y.DisableFlag, R.KeepAliveTimer, R.KeepAliveConfig"
                                   " FROM YukonPAObject Y LEFT OUTER JOIN Regulator R ON Y.PAObjectID = R.RegulatorId"
                                   " WHERE Y.Type IN";

    static const std::string and_clause = " AND Y.PAObjectID = ?";

    const std::string inClause = " ('"  + VoltageRegulator::LoadTapChanger +
                                 "', '" + VoltageRegulator::GangOperatedVoltageRegulator + 
                                 "', '" + VoltageRegulator::PhaseOperatedVoltageRegulator + 
                                 "')";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection);

    std::string query = sql + inClause;

    if( Id >= 0 )
    {
        rdr.setCommandText(query + and_clause);
        rdr << Id;
    }
    else
    {
        rdr.setCommandText(query);
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        std::string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << loggedSQLstring << std::endl;
        }
    }

    while ( rdr() )
    {
        std::string regulatorType;

        rdr["Type"] >> regulatorType;
  
        if ( rdr.isValid() )
        {
            VoltageRegulatorManager::SharedPtr regulator;

            if ( regulatorType == VoltageRegulator::LoadTapChanger || 
                 regulatorType == VoltageRegulator::GangOperatedVoltageRegulator )
            {
                regulator.reset( new GangOperatedVoltageRegulator(rdr) );
            }
            else if ( regulatorType == VoltageRegulator::PhaseOperatedVoltageRegulator )
            {
                regulator.reset( new PhaseOperatedVoltageRegulator(rdr) );
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Unsupported Regulator Type: " << regulatorType << std::endl;
            }

            if ( regulator && rdr.isValid() )            // reader is ~still~ valid
            {
                voltageRegulators[ regulator->getPaoId() ] = regulator;      // insert/update...
            }
        }
    }
}

}
}

