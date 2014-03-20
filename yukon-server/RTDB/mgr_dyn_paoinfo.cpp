#include "precompiled.h"

#include "mgr_dyn_paoinfo.h"

#include "database_connection.h"
#include "database_reader.h"
#include "database_util.h"

#include "std_helper.h"

#include "yukon.h"

#include "boostutil.h"

#include <boost/range/algorithm/set_algorithm.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>
#include <boost/smart_ptr/make_shared.hpp>

namespace Cti {

using Database::DatabaseConnection;
using Database::DatabaseReader;

namespace {
struct DynamicPaoInfoManager_Singleton : DynamicPaoInfoManager {};
}

std::auto_ptr<DynamicPaoInfoManager> gDynamicPaoInfoManager(new DynamicPaoInfoManager_Singleton);

namespace {
//  !!!  Any changes to these std::strings will require a DB update - this is what the DB keys on  !!!
const std::map<Applications, std::string> OwnerMap =
    boost::assign::map_list_of
        (Application_Dispatch, "dispatch")
        (Application_Porter,   "porter"  )
        (Application_Scanner,  "scanner" );

const std::auto_ptr<DynamicPaoInfoManager> &instance = gDynamicPaoInfoManager;

}

DynamicPaoInfoManager::DynamicPaoInfoManager() :
    owner(Application_Invalid)
{
}


void DynamicPaoInfoManager::setOwner(const Applications owner)
{
    instance->owner = owner;
}


void DynamicPaoInfoManager::loadInfo(const long paoId)
{
    Database::id_set paoIds;

    paoIds.insert(paoId);

    loadInfo(paoIds);
}

void DynamicPaoInfoManager::loadInfo(const Database::id_set &paoids)
{
    readers_writer_lock_t::writer_lock_guard_t guard(mux);

    Database::id_set paoidsToLoad;

    boost::range::set_difference(paoids, loadedPaos, std::inserter(paoidsToLoad, paoidsToLoad.begin()));

    //  Need to write before reading to ensure dirty info is written out before it's overwritten?

    {
        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            dout << CtiTime() << " Looking for Dynamic PAO Info " << __FILE__ << "(" << __LINE__ << ")" << std::endl;
        }

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection);

        std::string sql = CtiTableDynamicPaoInfo::getSQLCoreStatement();

        boost::optional<std::string> ownerString = mapFind(OwnerMap, owner);

        if( ! ownerString || ownerString->empty() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            dout << CtiTime() << " Owner string not set " << __FILE__ << "(" << __LINE__ << ")" << std::endl;

            return;
        }

        sql += " WHERE owner = ? AND " + Database::createIdSqlClause(paoidsToLoad, "DPI", "paobjectid");

        rdr.setCommandText(sql);
        rdr << *ownerString;
        Database::executeCommand(rdr, __FILE__, __LINE__, Database::LogDebug(DebugLevel & 0x00020000));

        if(rdr.isValid())
        {
            while( rdr() )
            {
                try
                {
                    DynInfoSPtr dynInfo = boost::make_shared<CtiTableDynamicPaoInfo>(boost::ref(rdr));

                    paoInfoPerId[dynInfo->getPaoID()][dynInfo->getKey()] = dynInfo;
                }
                catch( CtiTableDynamicPaoInfo::BadKeyException )
                {
                    long paoid;
                    std::string key;
                    std::string owner;

                    rdr["paoid"] >> paoid;
                    rdr["entrykey"] >> key;
                    rdr["owner"] >> owner;

                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
                    dout << "Invalid key - paoid = " << paoid << ", key = " << key << ", owner = " << owner << std::endl;
                }
            }

            loadedPaos.insert(paoids.begin(), paoids.end());
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
            dout << "Error reading Dynamic PAO Info from database. " << std::endl;
        }

        if(DebugLevel & 0x00020000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Dynamic PAO Info" << std::endl;
        }
    }
}


void DynamicPaoInfoManager::purgeInfo(const long paoId)
{
    boost::optional<PaoInfoMap &> paoInfo = mapFindRef(instance->paoInfoPerId, paoId);

    if( ! paoInfo ) // Nothing to purge, let's get out of here!
    {
        return;
    }

    // Purge the dynamic info from memory.
    paoInfo->clear();

    boost::optional<std::string> ownerString = mapFind(OwnerMap, instance->owner);

    if( ! ownerString || ownerString->empty() )
    {
        return;
    }

    // Purge the dynamic info from the database.
    static const std::string sqlPurge =
            "DELETE "
            "FROM dynamicpaoinfo "
            "WHERE paobjectid = ? AND owner = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseWriter       deleter(connection, sqlPurge);

    deleter << paoId << *ownerString;

    Cti::Database::executeCommand( deleter, __FILE__, __LINE__ );
}

void DynamicPaoInfoManager::purgeInfo(long paoId, CtiTableDynamicPaoInfo::PaoInfoKeys key)
{
    boost::optional<PaoInfoMap &> paoInfo = mapFindRef(instance->paoInfoPerId, paoId);

    if( ! paoInfo ) // Nothing to purge, let's get out of here!
    {
        return;
    }

    if( ! paoInfo->erase(key) ) // Nothing to purge, let's get out of here!
    {
        return;
    }

    boost::optional<std::string> ownerString = mapFind(OwnerMap, instance->owner);

    if( ! ownerString || ownerString->empty() )
    {
        return;
    }

    const std::string keyString = CtiTableDynamicPaoInfo::getKeyString(key);

    // Purge the dynamic info from memory.
    if( keyString.empty() )
    {
        return;
    }

    // Purge the dynamic info from the database.
    static const std::string sqlPurge =
            "DELETE "
            "FROM dynamicpaoinfo "
            "WHERE paobjectid = ? AND infokey = ? AND owner = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseWriter       deleter(connection, sqlPurge);

    deleter << paoId
            << keyString
            << ownerString;

    Cti::Database::executeCommand( deleter, __FILE__, __LINE__ );
}


void DynamicPaoInfoManager::writeInfo( void )
{
    Cti::Database::DatabaseConnection conn;

    if ( ! conn.isValid() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

        return;
    }

    readers_writer_lock_t::writer_lock_guard_t lock(instance->mux);

    boost::optional<std::string> ownerString = mapFind(OwnerMap, instance->owner);

    if ( ! ownerString || ownerString->empty() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** ERROR **** Cannot write DynamicPaoInfo - invalid owner (" << instance->owner << ")" << __FILE__ << " (" << __LINE__ << ")" << std::endl;

        instance->dirtyInfo.clear();

        return;
    }

    DynInfoRefSet::iterator dirtyItr = instance->dirtyInfo.begin();

    while( dirtyItr != instance->dirtyInfo.end() )
    {
        if( DynInfoSPtr dirtyInfo = dirtyItr->lock() )
        {
            bool written = false;

            if( dirtyInfo->isFromDb() )
            {
                written = dirtyInfo->Update(conn, *ownerString);
            }
            else
            {
                written = dirtyInfo->Insert(conn, *ownerString);

                if( ! written )
                {
                    written = dirtyInfo->Update(conn, *ownerString);
                }
            }

            if( ! written )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - error inserting/updating DynamicPaoInfo **** " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
                }

                dirtyInfo->dump();

                //  bypass it, try again next time
                dirtyItr++;

                continue;
            }
        }

        instance->dirtyInfo.erase(dirtyItr++);
    }
}


void DynamicPaoInfoManager::setInfo(DynInfoSPtr &newInfo)
{
    readers_writer_lock_t::writer_lock_guard_t guard(instance->mux);

    const long        paoId = newInfo->getPaoID();
    const PaoInfoKeys key   = newInfo->getKey();

    if( ! loadedPaos.count(paoId) )
    {
        loadInfo(paoId);
    }

    PaoInfoMap &paoInfo = instance->paoInfoPerId[paoId];

    DynInfoSPtr &oldInfo = paoInfo[key];

    if( oldInfo && oldInfo->isFromDb() )
    {
        newInfo->setFromDb();
    }

    oldInfo = newInfo;

    dirtyInfo.insert(newInfo);
}

void DynamicPaoInfoManager::setInfo(const long paoId, PaoInfoKeys k, const std::string value)    {  instance->setInfo(boost::make_shared<CtiTableDynamicPaoInfo>(paoId, k, value));  }
void DynamicPaoInfoManager::setInfo(const long paoId, PaoInfoKeys k, const int value)            {  instance->setInfo(boost::make_shared<CtiTableDynamicPaoInfo>(paoId, k, value));  }
void DynamicPaoInfoManager::setInfo(const long paoId, PaoInfoKeys k, const unsigned int value)   {  instance->setInfo(boost::make_shared<CtiTableDynamicPaoInfo>(paoId, k, value));  }
void DynamicPaoInfoManager::setInfo(const long paoId, PaoInfoKeys k, const long value)           {  instance->setInfo(boost::make_shared<CtiTableDynamicPaoInfo>(paoId, k, value));  }
void DynamicPaoInfoManager::setInfo(const long paoId, PaoInfoKeys k, const unsigned long value)  {  instance->setInfo(boost::make_shared<CtiTableDynamicPaoInfo>(paoId, k, value));  }
void DynamicPaoInfoManager::setInfo(const long paoId, PaoInfoKeys k, const double value)         {  instance->setInfo(boost::make_shared<CtiTableDynamicPaoInfo>(paoId, k, value));  }
void DynamicPaoInfoManager::setInfo(const long paoId, PaoInfoKeys k, const CtiTime value)        {  instance->setInfo(paoId, k, (unsigned long) value.seconds());  }


template <class T>
bool DynamicPaoInfoManager::getInfoForId(long paoId, CtiTableDynamicPaoInfo::PaoInfoKeys k, T &destination)
{
    bool loaded = false;

    {
        readers_writer_lock_t::reader_lock_guard_t guard(instance->mux);

        loaded = loadedPaos.count(paoId);
    }

    if( ! loaded )
    {
        loadInfo(paoId);
    }

    {
        readers_writer_lock_t::reader_lock_guard_t guard(instance->mux);

        boost::optional<PaoInfoMap &> paoInfo = mapFindRef(instance->paoInfoPerId, paoId);

        if( ! paoInfo )
        {
            return false;
        }

        boost::optional<DynInfoSPtr> dynInfo = mapFind(*paoInfo, k);

        if( ! dynInfo )
        {
            return false;
        }

        (*dynInfo)->getValue(destination);

        return true;
    }
}

bool DynamicPaoInfoManager::getInfo(long paoId, PaoInfoKeys k,   std::string &destination)  {  return instance->getInfoForId(paoId, k, destination);  }
bool DynamicPaoInfoManager::getInfo(long paoId, PaoInfoKeys k,           int &destination)  {  return instance->getInfoForId(paoId, k, destination);  }
bool DynamicPaoInfoManager::getInfo(long paoId, PaoInfoKeys k,          long &destination)  {  return instance->getInfoForId(paoId, k, destination);  }
bool DynamicPaoInfoManager::getInfo(long paoId, PaoInfoKeys k, unsigned long &destination)  {  return instance->getInfoForId(paoId, k, destination);  }
bool DynamicPaoInfoManager::getInfo(long paoId, PaoInfoKeys k,        double &destination)  {  return instance->getInfoForId(paoId, k, destination);  }
bool DynamicPaoInfoManager::getInfo(long paoId, PaoInfoKeys k,  unsigned int &destination)  {  return instance->getInfoForId(paoId, k, destination);  }
bool DynamicPaoInfoManager::getInfo(long paoId, PaoInfoKeys k,       CtiTime &destination)
{
    ctitime_t t;

    bool retval = instance->getInfoForId(paoId, k, (unsigned long&)t);

    destination = t;

    return retval;
}

long DynamicPaoInfoManager::getInfo(long paoId, PaoInfoKeys k)
{
    long l = std::numeric_limits<long>::min();

    instance->getInfoForId(paoId, k, l);

    return l;
}

bool DynamicPaoInfoManager::hasInfo(long paoId, PaoInfoKeys k)
{
    readers_writer_lock_t::reader_lock_guard_t guard(instance->mux);

    boost::optional<PaoInfoMap &> paoInfo = mapFindRef(instance->paoInfoPerId, paoId);

    if( ! paoInfo )
    {
        return false;
    }

    return paoInfo->count(k);
}



}
