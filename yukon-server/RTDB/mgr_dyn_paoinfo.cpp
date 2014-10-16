#include "precompiled.h"

#include "mgr_dyn_paoinfo.h"

#include "database_connection.h"
#include "database_reader.h"
#include "database_util.h"
#include "logger.h"
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
using Database::DatabaseWriter;

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
 * @return true if the command was successfully executed (even if no rows are affected), false otherwise
 */
bool deleteIndexedInfo(const std::string& ownerString, const long paoId, CtiTableDynamicPaoInfoIndexed::PaoInfoKeysIndexed k)
{
    const std::string keyString = CtiTableDynamicPaoInfoIndexed::getKeyString(k);
    if( keyString.empty() )
    {
        return false;
    }

    const std::string sqlDelete =
            "DELETE "
            "FROM dynamicpaoinfo "
            "WHERE paobjectid = ? AND owner = ? "
            "AND infokey LIKE '" + keyString + "%'";

    DatabaseConnection   connection;
    DatabaseWriter       deleter(connection, sqlDelete);

    deleter << paoId
            << ownerString;

    return Database::executeCommand( deleter, __FILE__, __LINE__ );
}

/**
 * Copy only if the destination shared_ptr is null
 */
template <typename T>
void copyIfDestIsNull( boost::shared_ptr<T> &dst, const boost::shared_ptr<T> &src )
{
    if( ! dst )
    {
        dst = src;
    }
}

} // namespace anonymous

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
    bool loaded = false, reload = false;

    {
        readers_writer_lock_t::reader_lock_guard_t guard(mux);

        loaded = loadedPaos.count(paoId);
        reload = paosToReload.count(paoId);
    }

    if( reload )
    {
        readers_writer_lock_t::writer_lock_guard_t lock(mux);

        if( loaded )
        {
            if( ! dirtyInfo.empty() || ! dirtyInfoIndexed.empty() )
            {
                // if we have dirty info, try to write it to the DB before continuing
                writeInfo();

                if( ! dirtyInfo.empty() || ! dirtyInfoIndexed.empty() )
                {
                    // if writing info to the database as fail, dont go any further
                    return;
                }
            }

            // unload all dynamic info associated with the Pao ID
            paoInfoPerId.erase(paoId);
            paoInfoPerIdIndexed.erase(paoId);

            // mark the Pao ID as unloaded
            loadedPaos.erase(paoId);
            loaded = false;
        }

        // NOTE:
        // even if nothing was loaded from the DB we may still have dirty Info associated with the PaoId
        // however the dirty info will not be lost (overwritten) after we successfully load.
        paosToReload.erase(paoId);
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

    // There is a possibility of having no PAOs to load if 2 threads try to load the same IDs,
    // lets allow the first one to continue and log
    if( paoidsToLoad.empty() )
    {
        return;
    }

    //  Need to write before reading to ensure dirty info is written out before it's overwritten?

    if(DebugLevel & 0x00020000)
    {
        CTILOG_DEBUG(dout, "Looking for Dynamic PAO Info");
    }

    DatabaseConnection connection;
    DatabaseReader rdr(connection);

    std::string sql = CtiTableDynamicPaoInfo::getSQLCoreStatement();

    boost::optional<std::string> ownerString = mapFind(OwnerMap, owner);

    if( ! ownerString || ownerString->empty() )
    {
        CTILOG_ERROR(dout, "Owner string not set");

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
            // try non-indexed key
            try
            {
                const DynInfoSPtr dynInfo = boost::make_shared<CtiTableDynamicPaoInfo>(boost::ref(rdr));

                DynInfoSPtr& currInfo = paoInfoPerId[dynInfo->getPaoID()][dynInfo->getKey()];

                // assume that cached info is always newer then what's from the DB
                copyIfDestIsNull(currInfo, dynInfo);

                continue;
            }
            catch( CtiTableDynamicPaoInfo::BadKeyException& )
            {
                // we will re-try
            }

            // if the key was not recognize, retry with indexed key
            try
            {
                const DynInfoIndexSPtr dynInfo = boost::make_shared<CtiTableDynamicPaoInfoIndexed>(boost::ref(rdr));

                PaoInfoIndexed& paoInfoIndexed = paoInfoPerIdIndexed[dynInfo->getPaoID()][dynInfo->getKey()];

                if( dynInfo->getIndex() )
                {
                    const unsigned index = *dynInfo->getIndex();

                    if( paoInfoIndexed.valuesInfo.size() < index + 1 )
                    {
                        // resize shall create empty null DynInfoSPtr
                        // make sure we check this later when we retrieve indexed information
                        paoInfoIndexed.valuesInfo.resize(index + 1);
                    }

                    DynInfoIndexSPtr& currInfo = paoInfoIndexed.valuesInfo[index];

                    // assume that cached info is always newer then what's from the DB
                    copyIfDestIsNull(currInfo, dynInfo);
                }
                else
                {
                    DynInfoIndexSPtr& currInfo = paoInfoIndexed.sizeInfo;

                    // assume that cached info is always newer then what's from the DB
                    copyIfDestIsNull(currInfo, dynInfo);
                }

            }
            catch( CtiTableDynamicPaoInfo::BadKeyException &ex )
            {
                CTILOG_ERROR(dout, "Invalid key - paoid = " << ex.paoid << ", key = " << ex.key << ", owner = " << ex.owner);
            }
        }

        loadedPaos.insert(paoids.begin(), paoids.end());
    }
    else
    {
        CTILOG_ERROR(dout, "Could not read Dynamic PAO Info from database");
    }

    if(DebugLevel & 0x00020000)
    {
        CTILOG_DEBUG(dout, "Done looking for Dynamic PAO Info");
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

    DatabaseConnection   connection;
    DatabaseWriter       deleter(connection, sqlPurge);

    deleter << paoId << *ownerString;

    Database::executeCommand( deleter, __FILE__, __LINE__ );
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

    DatabaseConnection   connection;
    DatabaseWriter       deleter(connection, sqlPurge);

    deleter << paoId
            << keyString
            << *ownerString;

    Database::executeCommand( deleter, __FILE__, __LINE__ );
}

Database::id_set DynamicPaoInfoManager::writeInfo( void )
{
    Database::id_set paoIdsWritten;

    DatabaseConnection conn;

    if ( ! conn.isValid() )
    {
        CTILOG_ERROR(dout, "Invalid Database Connection");

        return Database::id_set();
    }

    readers_writer_lock_t::writer_lock_guard_t lock(instance->mux);

    boost::optional<std::string> ownerString = mapFind(OwnerMap, instance->owner);

    if ( ! ownerString || ownerString->empty() )
    {
        CTILOG_ERROR(dout, "Cannot write DynamicPaoInfo - invalid owner ("<< instance->owner <<")");

        instance->dirtyInfo.clear();

        return Database::id_set();
    }

    // insert/update CtiTableDynamicPaoInfo items
    {
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
                    CTILOG_ERROR(dout, "Could not insert/update DynamicPaoInfo" << dirtyInfo);

                    //  bypass it, try again next time
                    dirtyItr++;

                    continue;
                }

                paoIdsWritten.insert(dirtyInfo->getPaoID());
            }

            instance->dirtyInfo.erase(dirtyItr++);
        }
    }

    // delete old CtiTableDynamicPaoInfoIndexed before inserting new items
    {
        PaoIdAndIndexedKeySet::iterator dirtyItr = instance->dirtyInfoIndexedToDelete.begin();

        while( dirtyItr != instance->dirtyInfoIndexedToDelete.end() )
        {
            const PaoIdAndIndexedKeySet::value_type val = *dirtyItr;

            if( ! deleteIndexedInfo(*ownerString, val.first, val.second ) )
            {
                //  bypass it, try again next time
                dirtyItr++;

                continue;
            }

            instance->dirtyInfoIndexedToDelete.erase(dirtyItr++);
        }
    }

    // insert CtiTableDynamicPaoInfoIndexed items
    {
        DynInfoIndexRefSet::iterator dirtyItr = instance->dirtyInfoIndexed.begin();

        while( dirtyItr != instance->dirtyInfoIndexed.end() )
        {
            if( DynInfoIndexSPtr dirtyInfo = dirtyItr->lock() )
            {
                // check that previous indexed items where successfully deleted before continuing
                if( instance->dirtyInfoIndexedToDelete.count( std::make_pair(dirtyInfo->getPaoID(), dirtyInfo->getKey()) ))
                {
                    //  bypass it, try again next time
                    dirtyItr++;

                    continue;
                }

                if( ! dirtyInfo->Insert(conn, *ownerString) )
                {
                    //  bypass it, try again next time
                    dirtyItr++;

                    continue;
                }

                paoIdsWritten.insert(dirtyInfo->getPaoID());
            }

            instance->dirtyInfoIndexed.erase(dirtyItr++);
        }
    }

    return paoIdsWritten;
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

DynamicPaoInfoManager::PaoIds DynamicPaoInfoManager::getPaoIdsHavingInfo(PaoInfoKeys k)
{
    static const std::string sql =
            "SELECT paobjectid"
            " FROM DynamicPaoInfo"
            " WHERE InfoKey=?";

    DatabaseConnection connection;
    DatabaseReader rdr(connection);

    rdr.setCommandText(sql);

    rdr << CtiTableDynamicPaoInfo::getKeyString(k);

    Database::executeCommand(rdr,  __FILE__, __LINE__, Database::LogDebug(DebugLevel & 0x00020000));

    PaoIds ids;

    long paoid;

    while( rdr() )
    {
        rdr >> paoid;
        ids.insert(paoid);
    }

    return ids;
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
void DynamicPaoInfoManager::setInfo(const long paoId, PaoInfoKeysIndexed k, const std::vector<T> &values)
{
    instance->loadInfoIfNecessary(paoId);

    const unsigned long numberOfIndex = values.size();

    {
        readers_writer_lock_t::writer_lock_guard_t guard(instance->mux);

        PaoInfoIndexedMap &paoInfoIndexedMap = instance->paoInfoPerIdIndexed[paoId];
        PaoInfoIndexed    &paoInfoIndexed    = paoInfoIndexedMap[k];

        // number of indexed info
        DynInfoIndexSPtr newSizeInfo = boost::make_shared<CtiTableDynamicPaoInfoIndexed>(paoId, k, numberOfIndex );

        paoInfoIndexed.sizeInfo = newSizeInfo;

        instance->dirtyInfoIndexed.insert(newSizeInfo);

        // indexed values
        paoInfoIndexed.valuesInfo.resize(numberOfIndex);

        for( unsigned index=0; index < numberOfIndex; index++ )
        {
            DynInfoIndexSPtr newValueInfo = boost::make_shared<CtiTableDynamicPaoInfoIndexed>(paoId, k, index, values[index]);

            paoInfoIndexed.valuesInfo[index] = newValueInfo; // copy over the old one, if it exist

            instance->dirtyInfoIndexed.insert(newValueInfo);
        }

        // indicate that we will delete previous values, before inserting new ones
        instance->dirtyInfoIndexedToDelete.insert( std::make_pair(paoId, k) );
    }
}

template <typename T>
boost::optional<std::vector<T>> DynamicPaoInfoManager::getInfo(const long paoId, PaoInfoKeysIndexed k)
{
    instance->loadInfoIfNecessary(paoId);

    {
        readers_writer_lock_t::reader_lock_guard_t guard(instance->mux);

        // Check that we have available info for the Pao ID
        boost::optional<PaoInfoIndexedMap &> paoInfoIndexedMap = mapFindRef( instance->paoInfoPerIdIndexed, paoId );
        if( ! paoInfoIndexedMap )
        {
            return boost::none;
        }

        // check that PaoInfoIndexed exist for the key
        boost::optional<PaoInfoIndexed &> paoInfoIndexed = mapFindRef(*paoInfoIndexedMap, k);
        if( ! paoInfoIndexed || ! paoInfoIndexed->sizeInfo )
        {
            return boost::none;
        }

        // validate the size
        unsigned long numberOfvalues;
        paoInfoIndexed->sizeInfo->getValue(numberOfvalues);
        if( paoInfoIndexed->valuesInfo.size() != numberOfvalues )
        {
            return boost::none;
        }

        std::vector<T> result;
        for each( const DynInfoIndexSPtr& info in paoInfoIndexed->valuesInfo )
        {
            // make sure that we have all indexed values
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

void DynamicPaoInfoManager::schedulePaoIdsToReload(const Database::id_set& paoIds)
{
    readers_writer_lock_t::writer_lock_guard_t lock(instance->mux);

    instance->paosToReload.insert(paoIds.begin(), paoIds.end());
}

}
