
#include "yukon.h"

#include <string>

#include "ccid.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "ZoneLoader.h"

extern ULONG _CC_DEBUG;


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
    static const std::string sql =   "SELECT PointId, ZoneId FROM PointToZoneMapping";
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
            rdr["PointId"] >> Id;

            if ( rdr.isValid() )                    // reader is ~still~ valid
            {
                zone->second->addPointId(Id);
            }
        }
    }
}


void ZoneDBLoader::loadRegulatorParameters(const long Id, ZoneManager::ZoneMap &zones)
{

    static const std::string sql =   "SELECT RegulatorId, ZoneId, Phase FROM ZoneRegulator";
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
            long        regulatorId;
            std::string phase("X");         // 'X' will be the designator for a gang-operated regulator (all phases)

            rdr["RegulatorId"] >> regulatorId;

            if ( ! rdr["Phase"].isNull() )
            {
                rdr["Phase"] >> phase;
            }

            zone->second->addRegulatorId( phase[0], regulatorId );
        }
    }
}

}   // namespace Cti
}   // namespace CapControl

