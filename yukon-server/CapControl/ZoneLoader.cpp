
#include "precompiled.h"

#include <string>

#include "ccid.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "ZoneLoader.h"
#include "ccsubstationbusstore.h"

using std::endl;

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
        std::string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << loggedSQLstring << endl;
        }
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
        std::string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << loggedSQLstring << endl;
        }
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
    static const std::string sql =   "SELECT PointId, ZoneId, Phase FROM PointToZoneMapping";
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
        std::string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << loggedSQLstring << endl;
        }
    }

    while ( rdr() )
    {
        long Id;

        rdr["ZoneId"] >> Id;

        ZoneManager::ZoneMap::iterator zone = zones.find(Id);

        if ( zone != zones.end() )
        {
            std::string phase;

            rdr["PointId"] >> Id;
            rdr["Phase"]   >> phase;

            if ( rdr.isValid() )                    // reader is ~still~ valid
            {
                zone->second->addPointId( resolvePhase( phase ), Id );
            }
        }
    }
}


void ZoneDBLoader::loadRegulatorParameters(const long Id, ZoneManager::ZoneMap &zones)
{

    static const std::string sql =   "SELECT RegulatorId, ZoneId, Phase FROM RegulatorToZoneMapping";
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
        std::string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << loggedSQLstring << endl;
        }
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
                RWRecursiveLock<RWMutexLock>::LockGuard  guard( store->getMux() );

                VoltageRegulatorManager::SharedPtr  regulator
                    = store->getVoltageRegulatorManager()->getVoltageRegulator( regulatorId );

                regulator->setPhase( phase );
            }
            catch ( const Cti::CapControl::NoVoltageRegulator & noRegulator )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);

                dout << CtiTime() << " - ** " << noRegulator.what() << std::endl;
            }
        }
    }
}


}
}

