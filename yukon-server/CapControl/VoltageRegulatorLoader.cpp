

#include "precompiled.h"
#include "ccid.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "VoltageRegulatorLoader.h"
#include "GangOperatedVoltageRegulator.h"
#include "PhaseOperatedVoltageRegulator.h"
#include "resolvers.h"

#include <string>

extern unsigned long _CC_DEBUG;


namespace Cti           {
namespace CapControl    {

VoltageRegulatorManager::VoltageRegulatorMap VoltageRegulatorDBLoader::load(const long Id)
{
    VoltageRegulatorManager::VoltageRegulatorMap loaded;

    loadCore(Id, loaded);
    loadPoints(Id, loaded);

    return loaded;
}


void VoltageRegulatorDBLoader::loadCore(const long Id, VoltageRegulatorManager::VoltageRegulatorMap &voltageRegulators)
{
    static const std::string sql =
        "SELECT "
            "Y.PAObjectID, "
            "Y.Category, "
            "Y.PAOClass, "
            "Y.PAOName, "
            "Y.Type, "
            "Y.Description, "
            "Y.DisableFlag "
        "FROM "
            "YukonPAObject Y "
        "WHERE "
            "Y.Type IN";

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
        CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
    }

    while ( rdr() )
    {
        std::string regulatorType;

        rdr["Type"] >> regulatorType;

        if ( rdr.isValid() )
        {
            VoltageRegulatorManager::SharedPtr regulator;

            if ( regulatorType == VoltageRegulator::LoadTapChanger )
            {
                regulator.reset( new GangOperatedVoltageRegulator(rdr) );
            }
            else if ( regulatorType == VoltageRegulator::PhaseOperatedVoltageRegulator ||
                      regulatorType == VoltageRegulator::GangOperatedVoltageRegulator )
            {
                regulator.reset( new PhaseOperatedVoltageRegulator(rdr) );
            }
            else
            {
                CTILOG_WARN(dout, "Unsupported Regulator Type: " << regulatorType);
            }

            if ( regulator && rdr.isValid() )            // reader is ~still~ valid
            {
                voltageRegulators[ regulator->getPaoId() ] = regulator;      // insert/update...
            }
        }
    }
}


void VoltageRegulatorDBLoader::loadPoints(const long Id, VoltageRegulatorManager::VoltageRegulatorMap &voltageRegulators)
{
    static const std::string sql = "SELECT P.PAObjectID, P.pointid, P.pointoffset, P.pointtype "
                                   " FROM Point P, YukonPAObject Y "
                                   " WHERE Y.PAObjectID = P.PAObjectID AND Y.Type IN";

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
        CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
    }

    while ( rdr() )
    {
        long regulatorId;

        rdr["PAObjectID"] >> regulatorId;

        VoltageRegulatorManager::SharedPtr regulator = voltageRegulators[ regulatorId ];

        if ( !rdr["pointid"].isNull() )
        {
            long        pointId,
                        pointOffset;
            std::string pointType;

            rdr["pointid"]     >> pointId;
            rdr["pointoffset"] >> pointOffset;
            rdr["pointtype"]   >> pointType;

            if ( resolvePointType(pointType) == StatusPointType &&
                 pointOffset == Cti::CapControl::Offset_PaoIsDisabled )
            {
                regulator->setDisabledStatePointId( pointId );
            }
            else
            {
                CTILOG_WARN(dout, "Undefined Voltage Regulator point offset: " << pointOffset
                                  << " of type: " << pointType);
            }
        }
    }
}

}
}

