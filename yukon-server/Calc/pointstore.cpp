#include "precompiled.h"
#include "pointstore.h"

#include "std_helper.h"
#include "logger.h"

#include <boost/range/algorithm/transform.hpp>
#include <boost/smart_ptr/scoped_ptr.hpp>

using std::endl;

CtiPointStoreElement *CtiPointStore::insert( long pointNum, long dependentId, CalcUpdateType updateType )
{
    try
    {
        if( pointNum )
        {
            IdToElementPtrMap::iterator itr;
            boost::tie(itr, boost::tuples::ignore) = instance()._store.insert(pointNum, new CtiPointStoreElement(pointNum));

            //  we append the pointID of the calc point that is dependent on it...
            if( (updateType == CalcUpdateType::AllUpdate 
                    || updateType == CalcUpdateType::AnyUpdate 
                    || updateType == CalcUpdateType::PeriodicPlusUpdate 
                    || dependentId == 0) 
                && dependentId >= 0 )
            {
                itr->second->appendDependent(dependentId);
            }

            return itr->second;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return 0;
}

void CtiPointStore::remove( long pointNum )
{
    try
    {
        if( pointNum )
        {
            instance()._store.erase(pointNum);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

std::set<long> CtiPointStore::getPointIds()
{
    std::set<long> results;

    for( auto& element : instance()._store )
    {
        results.insert(element.second->getPointNum());
    }

    return results;
}

CtiPointStoreElement *CtiPointStore::find( long pointId )
{
    IdToElementPtrMap::iterator itr = instance()._store.find(pointId);

    if( itr != instance()._store.end() )
    {
        return itr->second;
    }

    return 0;
}


namespace {
/* Pointer to the singleton instance of CtiPointStore
   Instantiate lazily by Instance */
boost::scoped_ptr<CtiPointStore> scopedInstance;
}

CtiPointStore::CtiPointStore()
{
}

CtiPointStore &CtiPointStore::instance()
{
    if( ! scopedInstance )
    {
        scopedInstance.reset(new CtiPointStore);
    }

    return *scopedInstance;
}

void CtiPointStore::freeInstance()
{
    scopedInstance.reset();
}

