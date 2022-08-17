#include "precompiled.h"

#include "ccid.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "ZoneLoader.h"
#include "ccsubstationbusstore.h"

extern unsigned long _CC_DEBUG;


namespace Cti           {
namespace CapControl    {

ZoneManager::ZoneMap ZoneDBLoader::load(const long Id)
{
    ZoneManager::ZoneMap loaded;

    loadCore(Id, loaded);
    loadBankParameters(Id, loaded);
    loadPointParameters(Id, loaded);
    loadRegulatorParameters(Id, loaded);

    return loaded;
}


void ZoneDBLoader::loadCore(const long Id, ZoneManager::ZoneMap &zones)
{
    static const std::string sql =   "SELECT ZoneId, ZoneName, SubstationBusId, ParentId, ZoneType FROM Zone";
    static const std::string where = " WHERE ZoneId = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection);

    if( Id >= 0 )
    {
        rdr.setCommandText(sql + where);
        rdr << Id;
    }
    else
    {
        rdr.setCommandText(sql);
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO(dout, rdr.asString());
    }

    while ( rdr() )
    {
        long Id;
        long parentId;
        long subbusId;
        std::string name;
        std::string type;

        rdr["ZoneId"]           >> Id;
        rdr["ZoneName"]         >> name;
        rdr["SubstationBusId"]  >> subbusId;
        rdr["ZoneType"]         >> type;

        parentId = Id;      // zone has no parent --> Id == parentId

        if ( ! rdr["ParentId"].isNull() )
        {
            rdr["ParentId"] >> parentId;
        }

        ZoneManager::SharedPtr zone = boost::make_shared<ZoneManager::SharedPtr::element_type>( Id, parentId, subbusId, name, type );

        if ( zone && rdr.isValid() )            // reader is ~still~ valid
        {
            zones[ zone->getId() ] = zone;      // insert/update...
        }
    }
}


void ZoneDBLoader::loadBankParameters(const long Id, ZoneManager::ZoneMap &zones)
{
    static const std::string sql =   "SELECT DeviceId, ZoneId FROM CapBankToZoneMapping";
    static const std::string where = " WHERE ZoneId = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection);

    if( Id >= 0 )
    {
        rdr.setCommandText(sql + where);
        rdr << Id;
    }
    else
    {
        rdr.setCommandText(sql);
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO(dout, rdr.asString());
    }

    while ( rdr() )
    {
        long Id;

        rdr["ZoneId"] >> Id;

        ZoneManager::ZoneMap::iterator zone = zones.find(Id);

        if ( zone != zones.end() )
        {
            rdr["DeviceId"] >> Id;

            if ( rdr.isValid() )                    // reader is ~still~ valid
            {
                zone->second->addBankId(Id);
            }
        }
    }
}


void ZoneDBLoader::loadPointParameters(const long Id, ZoneManager::ZoneMap &zones)
{
    static const std::string sql =
        "SELECT "
            "P.PointId, "
            "P.ZoneId, "
            "P.Ignore, "
            "M.Phase "
        "FROM "
            "PointToZoneMapping P "
                "LEFT OUTER JOIN CCMonitorBankList M "
                    "ON P.PointId = M.PointId";

    static const std::string sqlID = sql +
        " WHERE "
            "P.ZoneId = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection);

    if( Id >= 0 )
    {
        rdr.setCommandText(sqlID);
        rdr << Id;
    }
    else
    {
        rdr.setCommandText(sql);
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO(dout, rdr.asString());
    }

    while ( rdr() )
    {
        long Id;

        rdr["ZoneId"] >> Id;

        ZoneManager::ZoneMap::iterator zone = zones.find(Id);

        if ( zone != zones.end() )
        {
            Phase   phase = Phase_Unknown;

            rdr["PointId"] >> Id;

            if ( ! rdr["Phase"].isNull() )
            {
                std::string phaseStr;

                rdr["Phase"] >> phaseStr;

                phase = resolvePhase( phaseStr );
            }

            /*
                The ccmonitorbanklist table allows NULL entries for the Phase column.  The UI shouldn't allow a
                    NULL entry to be created or a polyphase assignment.  Check and warn if either of these conditions
                    occur as it is likely a misconfiguration of some sort.
            */
            if ( phase == Phase_Unknown || phase == Phase_Poly )
            {
                CTILOG_WARN(dout, "Zone: " << zone->second->getName() << " has assigned voltage point ID: " << Id
                     << " assigned with phase: " << ( phase == Phase_Unknown ? "(?) unknown" : "(*) polyphase" ));
            }

            /*
                Unsure as of yet if we need to keep track of the ignored voltage points at all, or if it is
                    enough to just not load them.  For now we'll just not load them.
            */
            const bool isIgnored = deserializeFlag( rdr["Ignore"].as<std::string>() );

            if ( ! isIgnored )
            {
                zone->second->addPointId( phase, Id );
            }
        }
    }
}


void ZoneDBLoader::loadRegulatorParameters(const long Id, ZoneManager::ZoneMap &zones)
{

    static const std::string sql =   "SELECT RZM.RegulatorId, RZM.ZoneId, MBL.Phase"
                                     " FROM RegulatorToZoneMapping RZM LEFT OUTER JOIN ccmonitorbanklist MBL"
                                     " ON RZM.RegulatorId = MBL.DeviceId";
    static const std::string where = " WHERE RZM.ZoneId = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection);

    if( Id >= 0 )
    {
        rdr.setCommandText(sql + where);
        rdr << Id;
    }
    else
    {
        rdr.setCommandText(sql);
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO(dout, rdr.asString());
    }

    while ( rdr() )
    {
        long Id;

        rdr["ZoneId"] >> Id;

        ZoneManager::ZoneMap::iterator zone = zones.find(Id);

        if ( zone != zones.end() )
        {
            long    regulatorId;
            Phase   phase = Phase_Poly;

            rdr["RegulatorId"] >> regulatorId;

            if ( ! rdr["Phase"].isNull() )
            {
                std::string phaseStr;

                rdr["Phase"] >> phaseStr;

                phase = resolvePhase( phaseStr );
            }

            zone->second->addRegulatorId( phase, regulatorId );

            // Assign the phase info directly to the regulator of interest
            try
            {
                CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
                CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

                VoltageRegulatorManager::SharedPtr  regulator
                    = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorId );

                regulator->setPhase( phase );
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                CTILOG_EXCEPTION_ERROR(dout, noRegulator);
            }
        }
    }
}


}
}

