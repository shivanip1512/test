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
#include <boost/make_shared.hpp>

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

/**
 * Delete indexed infoKeys from DynamicPaoInfo
 * infokey format: <base key> <digits>
 *
 * Note(1) : does not delete the base key, index indexed.
 * Note(2) : expects a space character (' ') before the digits.
 *
 * @param ownerString
 * @param paoId
 * @param k
 *
 * @return true if the command was successfully executed (even if no rows are affected), false otherwise
 */
bool deleteIndexed(const std::string& ownerString, const long paoId, CtiTableDynamicPaoInfo::PaoInfoKeys k)
{
    const std::string keyString = CtiTableDynamicPaoInfo::getKeyString(k);
    if( keyString.empty() )
    {
        return false;
    }

    const std::string sqlDelete =
            "DELETE "
            "FROM dynamicpaoinfo "
            "WHERE paobjectid = ? AND owner = ? "
            "AND infokey LIKE '" + keyString + " _%' AND infokey NOT LIKE '" + keyString + " %[^0-9]%'";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseWriter       deleter(connection, sqlDelete);

    deleter << paoId
            << ownerString;

    return Cti::Database::executeCommand( deleter, __FILE__, __LINE__ );
}

} // anonymous

DynamicPaoInfoManager::DynamicPaoInfoManager() :
    owner(Application_Invalid)
{
}


void DynamicPaoInfoManager::setOwner(const Applications owner)
{
    instance->owner = owner;
}


void DynamicPaoInfoManager::loadInfoIfNecessary(const long paoId)
{
    bool loaded = false;

    {
        readers_writer_lock_t::reader_lock_guard_t guard(mux);

        loaded = loadedPaos.count(paoId);
    }

    if( ! loaded )
    {
        loadInfo(paoId);
    }
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

                    if( dynInfo->getIndex() )
                    {
                        // we use a seperated map for indexed values
                        PaoInfoVec& paoInfoVec = paoInfoPerIdIndexed[dynInfo->getPaoID()][dynInfo->getKey()];

                        unsigned index = *dynInfo->getIndex();
                        if( paoInfoVec.size() < index + 1 )
                        {
                            // resize shall create empty null DynInfoSPtr
                            // make sure we check this later when we retrieve indexed information
                            paoInfoVec.resize(index + 1); 
                        }

                        paoInfoVec[index] = dynInfo;
                    }
                    else
                    {
                        paoInfoPerId[dynInfo->getPaoID()][dynInfo->getKey()] = dynInfo;
                    }
                }
                catch( CtiTableDynamicPaoInfo::BadKeyException &ex )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
                    dout << "Invalid key - paoid = " << ex.paoid << ", key = " << ex.key << ", owner = " << ex.owner << std::endl;
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
    readers_writer_lock_t::writer_lock_guard_t lock(instance->mux);

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
    readers_writer_lock_t::writer_lock_guard_t lock(instance->mux);

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

    //
    // delete previous indexed data before writing new ones
    //

    DynIndexedInfoSet::iterator dirtyIndexedItr = instance->dirtyIndexInfo.begin();

    while( dirtyIndexedItr != instance->dirtyIndexInfo.end() )
    {
        const DynIndexedInfoSet::value_type val = *dirtyIndexedItr;

        if( ! deleteIndexed(*ownerString, val.first, val.second ) )
        {
            //  bypass it, try again next time
            dirtyIndexedItr++;

            continue;
        }

        instance->dirtyIndexInfo.erase(dirtyIndexedItr++);
    }

    //
    // write / update items
    //

    DynInfoRefSet::iterator dirtyItr = instance->dirtyInfo.begin();

    while( dirtyItr != instance->dirtyInfo.end() )
    {
        if( DynInfoSPtr dirtyInfo = dirtyItr->lock() )
        {
            if( dirtyInfo->getIndex() )
            {
                // check that previous indexed items where successfully deleted before continuing
                if( instance->dirtyIndexInfo.count( std::make_pair(dirtyInfo->getPaoID(), dirtyInfo->getKey()) ))
                {
                    //  bypass it, try again next time
                    dirtyItr++;

                    continue;
                }
            }

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
    const long        paoId = newInfo->getPaoID();
    const PaoInfoKeys key   = newInfo->getKey();

    loadInfoIfNecessary(paoId);

    {
        readers_writer_lock_t::writer_lock_guard_t guard(mux);

        PaoInfoMap &paoInfo = paoInfoPerId[paoId];

        DynInfoSPtr &oldInfo = paoInfo[key];

        if( oldInfo && oldInfo->isFromDb() )
        {
            newInfo->setFromDb();
        }

        oldInfo = newInfo;

        dirtyInfo.insert(newInfo);
    }
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
    loadInfoIfNecessary(paoId);

    {
        readers_writer_lock_t::reader_lock_guard_t guard(mux);

        boost::optional<PaoInfoMap &> paoInfo = mapFindRef(paoInfoPerId, paoId);

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
    instance->loadInfoIfNecessary(paoId);

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


template <typename T>
void DynamicPaoInfoManager::setIndexedInfo(const long paoId, PaoInfoKeys k, const std::vector<T> &values)
{
    instance->loadInfoIfNecessary(paoId);

    const unsigned long numberOfIndex = values.size();

    {
        readers_writer_lock_t::writer_lock_guard_t guard(instance->mux);

        PaoInfoIndexedMap &paoInfo = instance->paoInfoPerIdIndexed[paoId];
        std::vector<DynInfoSPtr> &oldInfoVec = paoInfo[k];

        oldInfoVec.resize( numberOfIndex );

        for( unsigned index=0; index < numberOfIndex; index++ )
        {
            DynInfoSPtr &oldInfo = oldInfoVec[index];
            DynInfoSPtr newInfo = boost::make_shared<CtiTableDynamicPaoInfo>(paoId, k, values[index]);
            newInfo->setIndex( index );

            // NOTE: we do not use setFromDb() on the new value since previous values will be deleted, before re-inserted
            oldInfo = newInfo;

            // add the indexed value
            instance->dirtyInfo.insert(newInfo);
        }

        // insert/update an items that contains the number of indexes
        instance->setInfo( boost::make_shared<CtiTableDynamicPaoInfo>(paoId, k, numberOfIndex) );
        
        // indicate that we will delete previous values, before inserting new ones
        instance->dirtyIndexInfo.insert( std::make_pair(paoId, k) );
    }
}

template <typename T>
boost::optional<std::vector<T>> DynamicPaoInfoManager::getIndexedInfo(const long paoId, PaoInfoKeys k)
{
    instance->loadInfoIfNecessary(paoId);

    {
        readers_writer_lock_t::reader_lock_guard_t guard(instance->mux);

        unsigned long numberOfIndex;
        if( ! getInfo(paoId, k, numberOfIndex) )
        {
            return boost::none;
        }

        boost::optional<PaoInfoIndexedMap &> paoInfoIndexedMap = mapFindRef(instance->paoInfoPerIdIndexed, paoId);
        if( ! paoInfoIndexedMap )
        {
            return boost::none;
        }

        boost::optional<PaoInfoVec &> dynInfoVec = mapFindRef(*paoInfoIndexedMap, k);
        if( ! dynInfoVec || dynInfoVec->size() != numberOfIndex )
        {
            return boost::none;
        }

        std::vector<T> result;
        for each(const DynInfoSPtr& info in *dynInfoVec)
        {
            // check that we have all indexed values
            if( ! info )
            {
                return boost::none;
            }

            T value;
            info->getValue(value);
            result.push_back(value);
        }

        return result;
    }
}

}
