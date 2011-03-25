
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

    return loaded;
}


void ZoneDBLoader::loadCore(const long Id, ZoneManager::ZoneMap &zones)
{
    static const std::string sql =   "SELECT ZoneId, ZoneName, RegulatorId, SubstationBusId, ParentId FROM Zone";
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
        long regulatorId;
        long subbusId;
        std::string name;

        rdr["ZoneId"]           >> Id;
        rdr["ZoneName"]         >> name;
        rdr["RegulatorId"]      >> regulatorId;
        rdr["SubstationBusId"]  >> subbusId;

        parentId = Id;      // zone has no parent --> Id == parentId

        if ( ! rdr["ParentId"].isNull() )
        {
            rdr["ParentId"] >> parentId;
        }
  
        ZoneManager::SharedPtr zone = boost::make_shared<ZoneManager::SharedPtr::element_type>( Id, parentId, regulatorId, subbusId, name );
              
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

}   // namespace Cti
}   // namespace CapControl

