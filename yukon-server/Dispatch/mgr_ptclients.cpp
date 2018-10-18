#include "precompiled.h"

#include "ctidate.h"
#include "pt_base.h"
#include "logger.h"
#include "mgr_ptclients.h"
#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"
#include "database_bulk_writer.h"
#include "database_exceptions.h"
#include "database_util.h"
#include "devicetypes.h"
#include "msg_ptreg.h"
#include "msg_pcreturn.h"
#include "msg_signal.h"
#include "pt_dyn_dispatch.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "debug_timer.h"
#include "ptconnect.h"

#include "con_mgr_vg.h"
#include "pointdefs.h"
#include "resolvers.h"
#include "desolvers.h"
#include "tbl_pt_alarm.h"

#include "coroutine_util.h"
#include "std_helper.h"

#include <boost/range/algorithm/for_each.hpp>
#include <boost/range/algorithm/remove_if.hpp>
#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/adaptor/map.hpp>

#include <boost/make_shared.hpp>

#include <list>

using namespace std;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseBulkUpdater;
using Cti::Database::DatabaseReader;

#define POINT_REFRESH_SIZE 950 //This is overriden by cparm.

static const CtiTime UninitializedTimestamp { CtiDate { 1, 1, 2010 }, 12, 0, 0 };

/*
 *  This method initializes each point's dynamic data to its default/initial values.
 */
void ApplyInitialDynamicConditions(const long key, CtiPointSPtr pTempPoint, void* d)
{
    CtiPointClientManager *pointManager = (CtiPointClientManager*)d;
    CtiDynamicPointDispatchSPtr pDyn = pointManager->getDynamic(*pTempPoint);

    if( ! pDyn )
    {
        if( pDyn = boost::make_shared<CtiDynamicPointDispatch>(pTempPoint->getID()) )
        {
            UINT statictags = pDyn->getDispatch().getTags();
            pTempPoint->adjustStaticTags(statictags);

            pDyn->getDispatch().setTimeStamp(UninitializedTimestamp);
            pDyn->getDispatch().setValue(pTempPoint->getDefaultValue());
            pDyn->getDispatch().setTags(statictags);
            pDyn->getDispatch().setDirty( TRUE );                           // Make it update if it doesn't get reloaded!

            pointManager->setDynamic(pTempPoint->getID(), pDyn);
        }
    }
    else
    {
        UINT statictags = pDyn->getDispatch().getTags();
        pTempPoint->adjustStaticTags(statictags);

        if(statictags != pDyn->getDispatch().getTags())
        {
            static constexpr int DB_CONTROLLED_TAGS = 
                TAG_DISABLE_ALARM_BY_POINT
                | TAG_DISABLE_CONTROL_BY_POINT
                | TAG_DISABLE_POINT_BY_POINT
                | TAG_ATTRIB_PSEUDO
                | TAG_ATTRIB_CONTROL_AVAILABLE;

            pDyn->getDispatch().resetTags(DB_CONTROLLED_TAGS);
            pDyn->getDispatch().setTags(statictags);
        }
    }
}

// This gives the point its initial data. We should not need a mutex for this.
// This gives initial data to points that are loaded and thus should not be called on an entire pao.
// If a PAO load is desired, the point ID's need to be found before calling and the callee
// is responsible for loading the points before calling.
void CtiPointClientManager::processPointDynamicData(LONG pntID, const set<long> &pointIds)
{
    if( pntID )
    {
        //Lets be smart about handling a single point.
        if( CtiPointSPtr pPoint = getCachedPoint(pntID) )
        {
            ApplyInitialDynamicConditions(0, pPoint, this);

            const CtiDynamicPointDispatchSPtr dynamic = getDynamic(*pPoint);

            //This will probably always be true, but why not check.
            if(dynamic && !dynamic->getDispatch().getUpdatedFlag())
            {
                refreshDynamicDataForSinglePoint(pntID);
            }
        }
    }
    else if( ! pointIds.empty() )
    {
        for( long pointid : pointIds )
        {
            if( CtiPointSPtr pPoint = getCachedPoint(pointid) )
            {
                ApplyInitialDynamicConditions(0, pPoint, this);
            }
        }

        refreshDynamicDataForPointSet(pointIds);
    }
}


struct non_updated_check
{
    bool operator()(pair<long, CtiDynamicPointDispatchSPtr> dynamic)
    {
        return dynamic.second && !dynamic.second->getDispatch().getUpdatedFlag();
    }
};

/**
 * If the store needs to be updated when this pointID or paoid
 * changes, it will be updated by this function call. This
 * probably means a dbchange has been received.
 *
 * @param pntID LONG
 * @param pntType CtiPointType_t
 */
void CtiPointClientManager::updatePoints(LONG pntID, LONG paoID, CtiPointType_t pntType)
{
    if( pntID != 0 )
    {
        if( isPointLoaded(pntID) )
        {
            refreshList(pntID, 0, pntType);
            refreshProperties(pntID,0);
            refreshArchivalList(pntID, 0);
        }
        else
        {
            refreshProperties(pntID);
            refreshArchivalList(pntID);
        }
    }
    else if( paoID != 0 )
    {
        //As always, this assumes pao's are only used on add.
        refreshProperties(0, paoID);
        refreshArchivalList(0, paoID);
    }
    else
    {
        loadAllStaticData();
    }
}

/**
 * Loads all of the data that is kept for every point. This
 * should be called on startup.
 *
 */
void CtiPointClientManager::loadAllStaticData()
{
    refreshProperties(0);
    refreshArchivalList(0);
}


/**
 * This loads the specified points. In Dispatch, this should be
 * called sparingly. This is not to be called when db changes
 * occur.
 *
 * @param pntID LONG
 * @param paoID LONG
 * @param pntType CtiPointType_t
 */
std::set<long> CtiPointClientManager::refreshList(LONG pntID, LONG paoID, CtiPointType_t pntType)
{
    const std::set<long> pointsFound = Inherited::refreshList(pntID, paoID, pntType);
    refreshAlarming(pntID, paoID);
    refreshReasonabilityLimits(pntID, paoID);
    refreshPointLimits(pntID, paoID);
    //refreshProperties(pntID, paoID); Properties are currently loaded seperately

    if(pntID != 0 || paoID != 0)
    {
        processPointDynamicData(pntID, pointsFound);
    }
    else
    {
        CtiPointSPtr pTempPoint;
        Inherited::apply(ApplyInitialDynamicConditions, this);     // Make sure everyone has been initialized with Dynamic data.

        if( find_if(_dynamic.begin(), _dynamic.end(), non_updated_check()) != _dynamic.end() )
        {
            refreshDynamicDataForAllPoints();
        }
    }

    return pointsFound;
}

void CtiPointClientManager::refreshListByPointIDs(const set<long> &ids)
{
    unsigned long max_size = gConfigParms.getValueAsULong("MAX_IDS_PER_SELECT", POINT_REFRESH_SIZE);

    for( const auto& id_chunk : Cti::Coroutines::chunked(ids, max_size) )
    {
        std::set<long> idSubset{ id_chunk.begin(), id_chunk.end() };

        Inherited::refreshListByPointIDs(idSubset);
        refreshAlarming(0, 0, idSubset);
        refreshReasonabilityLimits(0, 0, idSubset);
        refreshPointLimits(0, 0, idSubset);
        processPointDynamicData(0, idSubset);
        //refreshProperties(0, 0, idSubset);
    }
}


void CtiPointClientManager::refreshAlarming(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    Cti::Timing::DebugTimer timer{ "Looking for Alarming", !!(DebugLevel & DEBUGLEVEL_MGR_POINT) };

    DatabaseConnection conn;
    DatabaseReader rdr(conn);

    //  This case doesn't make sense, and should probably be prevented by splitting this method into a couple of different overloads.
    if( pntID && paoID )
    {
        rdr.setCommandText(CtiTablePointAlarming::getSqlForPaoIdAndPointId());
        rdr << paoID << pntID;
    }
    else if( pntID )
    {
        rdr.setCommandText(CtiTablePointAlarming::getSqlForPointId());
        rdr << pntID;
    }
    else if( paoID )
    {
        rdr.setCommandText(CtiTablePointAlarming::getSqlForPaoId());
        rdr << paoID;
    }
    else if( ! pointIds.empty() )
    {
        rdr.setCommandText(CtiTablePointAlarming::getSqlForPointIds(pointIds.size()));
        rdr << pointIds;
    }
    else
    {
        rdr.setCommandText(CtiTablePointAlarming::getSqlForFullLoad());
    }

    rdr.execute();

    LONG pID;
    ptr_type pPt;
    std::list<CtiTablePointAlarming> tempList;
    while( rdr() )
    {
        tempList.push_back(CtiTablePointAlarming(rdr));
    }

    {
        coll_type::writer_lock_guard_t guard(getLock());
        // This is explicitly done here because alarming may have been removed for these points.
        // If alarming was not removed, it will be re-loaded in the following loop.
        if( pntID )
        {
            removeAlarming(pntID);
        }
        for( long tempPointID : pointIds )
        {
            removeAlarming(tempPointID);
        }
        for( std::list<CtiTablePointAlarming>::iterator iter = tempList.begin(); iter != tempList.end(); iter++ )
        {
            if( paoID != 0 )
            {
                // The new alarming will not overwrite the old so we have to remove the old first.
                removeAlarming(iter->getPointID());
            }
            addAlarming(*iter);
        }
    }
}


void CtiPointClientManager::refreshProperties(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    CtiTime start, stop;

    start = start.now();

    string sql = CtiTablePointProperty::getSQLCoreStatement();

    if(pntID || paoID)
    {
        sql += " WHERE";
        if(pntID)
        {
            sql += " PPV.pointid = ?";
        }
        if(paoID)
        {
            sql += (pntID ? " AND" : "");
            sql += " PPV.pointid IN (SELECT PT.pointid "
                                    "FROM point PT "
                                    "WHERE PT.paobjectid = ?)";
        }
    }

    if(!pointIds.empty())
    {
        sql += ((!pntID && !paoID) ? " WHERE" : " AND");
        sql += Cti::Database::createIdInClause( "PPV", "pointid", pointIds.size() );
    }

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    if(pntID)
    {
        rdr << pntID;
    }
    if(paoID)
    {
        rdr << paoID;
    }

    if(!pointIds.empty())
    {
        rdr << pointIds;
    }

    rdr.execute();

    if( ! rdr.isValid() )
    {
        CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
    }

    std::list<CtiTablePointProperty*> tempList;
    while( rdr() )
    {
        CtiTablePointProperty *p = CTIDBG_new CtiTablePointProperty(rdr);
        tempList.push_back(p);
    }

    {
        coll_type::writer_lock_guard_t guard(getLock());
        if( pntID )
        {
            _properties.erase(pntID);
        }
        for( std::list<CtiTablePointProperty *>::iterator iter = tempList.begin(); iter != tempList.end(); iter++ )
        {
            _properties.insert(make_pair((*iter)->getPointID(), CtiTablePointPropertySPtr(*iter)));
        }
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CTILOG_INFO(dout , (stop.seconds() - start.seconds()) <<" seconds for Properties");
    }
}

//This loads the points that have archival on timer and sets the archive on timer point property
void CtiPointClientManager::refreshArchivalList(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    string         sql;
    CtiTime start, stop;

    start = start.now();
    sql = "SELECT pointid from point WHERE ( "
          "archivetype = 'On Timer' OR "
          "archivetype = 'time&update' OR "
          "archivetype = 'timer|update')";

    if(pntID)
    {
        sql += " AND " + Cti::Database::createIdEqualClause( "point", "pointid" );
    }

    //  pao is assumed to be an add
    if(paoID)
    {
        sql += " AND " + Cti::Database::createIdEqualClause( "point", "paobjectid" );
    }

    if(!pointIds.empty())
    {
        sql += " AND " + Cti::Database::createIdInClause( "point", "pointid", pointIds.size() );
    }

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);

    if(pntID)
    {
        rdr << pntID;
    }

    if(paoID)
    {
        rdr << paoID;
    }

    if(!pointIds.empty())
    {
        rdr << pointIds;
    }

    rdr.execute();

    std::list<CtiTablePointProperty*> tempList;
    long pointid;
    while( rdr() )
    {
        rdr >> pointid;
        CtiTablePointProperty *p = CTIDBG_new CtiTablePointProperty(pointid, CtiTablePointProperty::ARCHIVE_ON_TIMER, 1);
        tempList.push_back(p);
    }

    {
        coll_type::writer_lock_guard_t guard(getLock());
        if( pntID )
        {
            PointPropertyRange range = _properties.equal_range(pntID);
            for( ; range.first != range.second; ++range.first )
            {
                if( range.first->second && range.first->second->getPropertyID() == CtiTablePointProperty::ARCHIVE_ON_TIMER)
                {
                    _properties.erase(range.first);
                }
            }
        }
        for( std::list<CtiTablePointProperty *>::iterator iter = tempList.begin(); iter != tempList.end(); iter++ )
        {
            _properties.insert(make_pair((*iter)->getPointID(), CtiTablePointPropertySPtr(*iter)));
        }
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CTILOG_INFO(dout , (stop.seconds() - start.seconds()) <<" seconds for Archival List");
    }
}

/* Insert a connection manager and register points for that connection.
 *
 *   If the registration is a refresh (ADD and REMOVE), we remove the points from the Connection Manager.
 *   If the Connection Manager is not in _conMgrPointMap, insert it.
 *   For each point:
 *     If the registration is to REMOVE the points
 *       If the point is in _pointConnectionMap
 *         Call removeConnectionManagersFromPoint to remove the point from the CtiPointConnection::ConnectionManagerCollection map
 *         If this is the last point in _pointConnectionMap remove the CtiPointConnection from that map
 *       Remove the point from the _conMgrPointMap entry
 *     If the registration is an ADD
 *       Add the CtiPointConnection for the point to the _pointConnectionMap
 *       Call AddConnectionManager to add the point to the CtiPointConnection::ConnectionManagerCollection map
 *       Add the point to the _conMgrPointMap entry
 *    Go home exhausted.
 *         
 */
std::set<long> CtiPointClientManager::InsertConnectionManager(CtiServer::ptr_type &CM, const CtiPointRegistrationMsg &aReg, DebugPrint debugprint)
{
    CTILOG_ENTRY(dout, "CM=" << reinterpret_cast<size_t>(CM.get()) << ", aReg.getFlags()=" << aReg.getFlags());

    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());
    const int ptcnt = aReg.getCount();
    ConnectionMgrPointMap::iterator conIter = _conMgrPointMap.end();

    if( ! (aReg.isAddingPoints() || aReg.isRemovingPoints()) )     // If add/remove is set, we are augmenting or removing an existing registration (Not the whole thing).
    {
        removePointsFromConnectionManager( CM );
    }

    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

    if(debugprint == DebugPrint::True)
    {
        CTILOG_DEBUG(dout, CM->getClientName() <<" " << reinterpret_cast<size_t>(CM.get()) << " has registered for "<< ptcnt <<" points:" 
                            << endl << Cti::join(aReg.getPointList(), ","));
        CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());
    }

    /*
     *  if count is greater than zero (there are point id's in the list),
     *  we must be attached to specific points' lists as well.
     */

    if(ptcnt > 0)
    {
        conIter = _conMgrPointMap.find(CM->hash(*CM.get()));
        if( conIter == _conMgrPointMap.end() )
        {
            pair<ConnectionMgrPointMap::iterator, bool> tempVal;
            tempVal = _conMgrPointMap.emplace(CM->hash(*CM), std::set<long>{});
            if(!tempVal.second)
            {
                CTILOG_DEBUG(dout, "Unable to insert into _conMgrPointMap");
            }

            conIter = tempVal.first;
        }
        else
        {
            CTILOG_DEBUG(dout, "Already have _conMgrPointMap entry for " << hex << CM->hash(*CM.get()));
        }
    }

    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

    std::set<long> inserted, existing;

    for( const auto ptId : aReg.getPointList() )
    {
        /*
         *  OK, now I walk the list of points looking at each one's ID to find who to add this guy to
         */
        if(!((const CtiVanGoghConnectionManager *)CM.get())->isRegForAll()) // Make sure we didn't already register for ALL points.
        {
            // Prevent _pointConnectionMap from wiggling while we operate.
            coll_type::writer_lock_guard_t guard( getLock() );

            if( aReg.isRemovingPoints() )
            {
                PointConnectionMap::iterator iter = _pointConnectionMap.find(ptId);
                if(iter != _pointConnectionMap.end())
                {
                    iter->second.removeConnectionManagersFromPoint( CM );
                    if(iter->second.IsEmpty())
                    {
                        _pointConnectionMap.erase(iter);
                    }
                }

                if( conIter != _conMgrPointMap.end() )
                {
                    conIter->second.erase(ptId);
                    CTILOG_DEBUG(dout, "Removing point " << ptId
                        << " from _conMgrPointMap(" << hex << CM->hash(*CM.get()) << ") = 0x" << hex << &conIter->second);
                }
            }
            else
            {
                PointConnectionMap::iterator iter = _pointConnectionMap.find( ptId );
                if(iter == _pointConnectionMap.end())
                {
                    auto insertResult = _pointConnectionMap.emplace(ptId, CtiPointConnection());
                    if(insertResult.second == true)
                    {
                        iter = insertResult.first;
                    }
                }
                if(iter != _pointConnectionMap.end())
                {
                    iter->second.AddConnectionManager( CM );
                }

                if( conIter != _conMgrPointMap.end() )
                {
                    if(debugprint == DebugPrint::True)
                    {
                        CTILOG_DEBUG(dout, "Adding point " << ptId
                            << " to _conMgrPointMap(" << hex << CM->hash(*CM.get()) << ") = 0x" << hex << &conIter->second);
                    }

                    const auto was = conIter->second.insert(ptId);

                    if(debugprint == DebugPrint::True)
                    {
                        CTILOG_DEBUG(dout, "_conMgrPointMap(" << hex << CM->hash(*CM.get()) << ") has " << dec 
                            << conIter->second.size() << " entries.  Insert return was " << was.second);
                    }
                }
            }
        }
    }
    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

    return conIter != _conMgrPointMap.end() 
        ? conIter->second
        : std::set<long>{};
}

/** Remove all points from the specified ConnectionManager */
void CtiPointClientManager::removePointsFromConnectionManager(CtiServer::ptr_type &CM, DebugPrint debugprint)
{
    CTILOG_ENTRY(dout, "CM=" << reinterpret_cast<size_t>(CM.get()));

    // OK, now I walk the list of points looking at each one's list to remove the CM
    {
        coll_type::writer_lock_guard_t guard(getLock());
        ConnectionMgrPointMap::iterator conIter = _conMgrPointMap.find(CM->hash(*CM.get()));

        CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

        if( conIter != _conMgrPointMap.end() && conIter->second.size() > 0 )
        {
            CTILOG_DEBUG(dout, " point list size=" << conIter->second.size());

            for( const auto pointID : conIter->second )
            {
                PointConnectionMap::iterator iter = _pointConnectionMap.find( pointID );
                if(iter != _pointConnectionMap.end())
                {
                    if(debugprint == DebugPrint::True)
                    {
                        CTILOG_DEBUG(dout, "Removing CM " << reinterpret_cast<size_t>(CM.get()) << " from pointid " << pointID);
                    }
                    iter->second.removeConnectionManagersFromPoint( CM );
                    if(iter->second.IsEmpty())
                    {
                        _pointConnectionMap.erase(iter);
                    }
                }
                else
                {
                    CTILOG_DEBUG(dout, "Can't find pointid " << pointID);
                }
            }
        }
    }

    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

    _conMgrPointMap.erase(CM->hash(*CM));

    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());
}

CtiTime CtiPointClientManager::findNextNearestArchivalTime()
{
    CtiTime   closeTime(YUKONEOT);

    vector<long> points;
    getPointsWithProperty(CtiTablePointProperty::ARCHIVE_ON_TIMER, points);

    for( long ptid : points )
    {
        if( CtiPointSPtr pPt = getPoint(ptid) )
        {
            if( const CtiDynamicPointDispatchSPtr pDyn = getDynamic(*pPt) )
            {
                if( pDyn->getNextArchiveTime() < closeTime )
                {
                    closeTime = pDyn->getNextArchiveTime();
                }
            }
        }
    }

    return closeTime;
}

std::vector<std::unique_ptr<CtiTableRawPointHistory>> CtiPointClientManager::scanForArchival(const CtiTime &Now)
{
    std::vector<std::unique_ptr<CtiTableRawPointHistory>> toArchive;

    {
        Cti::Timing::DebugTimer debugTime("Scanning For Archival", false, 1);
        coll_type::writer_lock_guard_t guard(getLock());
        vector<long> points;
        getPointsWithProperty(CtiTablePointProperty::ARCHIVE_ON_TIMER, points);

        for( long ptid : points )
        {
            if( CtiPointSPtr pPt = getPoint(ptid) )
            {
                if( CtiDynamicPointDispatchSPtr pDyn = getDynamic(*pPt) )
                {
                    // Make sure the point is not disabled.  We kept the tag check here in order to minimize
                    //  the effect on existing systems. See YUK-18824 for details.
                    if( !(pDyn->getDispatch().getTags() & MASK_ANY_SERVICE_DISABLE) )
                    {
                        if( pPt->getArchiveType() == ArchiveTypeOnTimer           ||
                            pPt->getArchiveType() == ArchiveTypeOnTimerAndUpdated ||
                            pPt->getArchiveType() == ArchiveTypeOnTimerOrUpdated )
                        {
                            if( pDyn->getNextArchiveTime() <= Now )
                            {
                                switch( pPt->getArchiveType() )
                                {
                                case ArchiveTypeOnTimer:
                                case ArchiveTypeOnTimerOrUpdated:
                                    {
                                        toArchive.emplace_back(std::make_unique<CtiTableRawPointHistory>(pPt->getID(), pDyn->getQuality(), pDyn->getValue(), Now, 0));
                                        break;
                                    }
                                case ArchiveTypeOnTimerAndUpdated:
                                    {
                                        pDyn->setArchivePending(true);                   // Mark him so the next one gets archived!
                                        break;
                                    }
                                }

                                /*
                                 *  Now make the time correct for the next archive.
                                 */
                                pDyn->setNextArchiveTime( nextScheduledTimeAlignedOnRate(Now, pPt->getArchiveInterval()) );
                            }
                            else if( pPt->getArchiveInterval() >= 0 &&
                                     pDyn->getNextArchiveTime() > Now + (2 * pPt->getArchiveInterval()))
                            {
                                /*
                                 *  Now make the time correct for the next archive.
                                 */
                                pDyn->setNextArchiveTime( nextScheduledTimeAlignedOnRate(Now, pPt->getArchiveInterval()) );
                            }
                        }
                    }
                }
            }
        }
    }

    return toArchive;
}

auto CtiPointClientManager::getDirtyRecordList() -> DynamicPointDispatchList
{
    DynamicPointDispatchList updateList;

    coll_type::writer_lock_guard_t guard(getLock());

    for( auto& kv : _dynamic )
    {
        try
        {
            auto& pDyn = kv.second;

            if(pDyn && (pDyn->getDispatch().isDirty() || !pDyn->getDispatch().getUpdatedFlag()) )
            {
                UINT statictags = pDyn->getDispatch().getTags();
                pDyn->getDispatch().resetTags();                    // clear them all!
                if( auto pPt = getCachedPoint(kv.first) )
                {
                    pDyn->getDispatch().setTags(pPt->adjustStaticTags(statictags));   // make the static tags match...
                }

                updateList.push_back(pDyn);
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }

    return updateList;
}

void CtiPointClientManager::writeRecordsToDB(const DynamicPointDispatchList& updateList)
{
    Cti::Database::DatabaseConnection   conn;

    if ( ! conn.isValid() )
    {
        CTILOG_ERROR(dout, "Invalid Connection to Database");

        return;
    }

    const auto total = updateList.size();

    CTILOG_INFO(dout, "WRITING "<< total <<" dynamic dispatch records");

    size_t listCount = 0;

    DatabaseBulkUpdater<7> bu { conn.getClientType(), CtiTablePointDispatch::getTempTableSchema(), "DPD", "DynamicPointDispatch", "Point" };

    //  Get a RowSources view of the DynamicPointDispatch table rows
    const auto asRowSource = boost::adaptors::transformed ([](const CtiDynamicPointDispatchSPtr& dpd) { return &dpd->getDispatch(); });

    auto rowSources = boost::copy_range<std::vector<const Cti::RowSource*>> (updateList | asRowSource);

    try
    {
        auto rejectedRows = bu.writeRows(conn, std::move(rowSources));

        for( auto pointId : rejectedRows )
        {
            CTILOG_WARN(dout, "Removing record for invalid point ID " << pointId);

            erase(pointId);
        }
        for( auto& row : updateList )
        {
            //  Make sure the row wasn't deleted
            if( ! rejectedRows.count(row->getDispatch().getPointID()) )
            {
                row->getDispatch().resetDirty();
            }
        }
    }
    catch( const Cti::Database::DatabaseException& ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex, "Unable to update rows in DynamicPointDispatch");
    }
}

void CtiPointClientManager::removeOldDynamicData()
{
    size_t purged = 0;

    coll_type::writer_lock_guard_t guard(getLock());

    const auto end = _dynamic.end();

    for( auto itr = _dynamic.begin(); itr != end; )
    {
        const auto pointId = itr->first;
        const auto& dynTable = itr->second->getDispatch();

        if( ! getCachedPoint(itr->first) && ! dynTable.isDirty() && dynTable.getUpdatedFlag() )
        {
            itr = _dynamic.erase(itr);
            ++purged;
        }
        else
        {
            ++itr;
        }
    }

    CTILOG_INFO(dout, "PURGED "<< purged <<" records from memory");
}

void CtiPointClientManager::storeDirtyRecords()
{
    auto updateList = getDirtyRecordList();

    if( ! updateList.empty() )
    {
        CTILOG_DEBUG(dout, "Updating " << updateList.size() << " dynamic dispatch records");

        writeRecordsToDB(std::move(updateList));
    }

    removeOldDynamicData();
}

CtiPointClientManager::~CtiPointClientManager()
{
    DeleteList();
}

void CtiPointClientManager::DeleteList(void)
{
    coll_type::writer_lock_guard_t guard(getLock());

    _conMgrPointMap.clear();
    CTILOG_DEBUG(dout, "Clearing _conMgrPointMap");
    _pointConnectionMap.clear();
    _reasonabilityLimits.clear();
    _limits.clear();
    _alarming.clear();
    _properties.clear();
    _dynamic.clear();

    Inherited::ClearList();

}

void CtiPointClientManager::refreshDynamicDataForSinglePoint(const long id)
{
    if( ! id )
    {
        CTILOG_ERROR(dout, "A valid pointId was not provided. Unable to refresh dynamic data for point");
        return;
    }

    const vector<ParameterizedIdQuery> queries{ 1, { CtiTablePointDispatch::getSQLCoreStatement(1), { id } } };

    executeDynamicDataQueries(queries);
}

void CtiPointClientManager::refreshDynamicDataForPointSet(const set<long> &pointIds)
{
    if( pointIds.empty() )
    {
        CTILOG_ERROR(dout, "No pointIds were provided. Unable to refresh dynamic data for points.");
        return;
    }

    const vector<ParameterizedIdQuery> queries = generateSqlStatements(pointIds);

    executeDynamicDataQueries(queries);
}

void CtiPointClientManager::refreshDynamicDataForAllPoints()
{
    const vector<ParameterizedIdQuery> queries{ 1, { CtiTablePointDispatch::getSQLCoreStatement(0), {} } };

    executeDynamicDataQueries(queries);
}

auto CtiPointClientManager::generateSqlStatements(const set<long> &pointIds) -> std::vector<ParameterizedIdQuery>
{
    vector<ParameterizedIdQuery> queries;

    int max_ids_per_select = gConfigParms.getValueAsInt("MAX_IDS_PER_SELECT", POINT_REFRESH_SIZE);

    for ( const auto& range : Cti::Coroutines::chunked(pointIds, max_ids_per_select) )
    {
        std::vector<long> subset { range.begin(), range.end() };

        queries.push_back({CtiTablePointDispatch::getSQLCoreStatement(subset.size()), subset});
    }

    return queries;
}

void CtiPointClientManager::executeDynamicDataQueries(const vector<ParameterizedIdQuery> &queries)
{
    CtiTime start, stop;

    if(DebugLevel & DEBUGLEVEL_MGR_POINT)
    {
        CTILOG_DEBUG(dout, "Looking for Dynamic Dispatch Data");
    }

    Cti::Database::DatabaseConnection connection;

    for( const auto& parameterizedQuery : queries )
    {
        Cti::Database::DatabaseReader rdr(connection, parameterizedQuery.sql);

        if( ! parameterizedQuery.pointIds.empty() )
        {
            rdr << parameterizedQuery.pointIds;
        }

        rdr.execute();

        if( ! rdr.isValid() )
        {
            CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
        }
        else if( DebugLevel & DEBUGLEVEL_MGR_POINT )
        {
            CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
        }

        start = start.now();

        while( rdr() )
        {
            loadDynamicPoint(rdr);
        }

        if((stop = stop.now()).seconds() - start.seconds() > 5 )
        {
            CTILOG_INFO(dout , (stop.seconds() - start.seconds()) <<" seconds for Dynamic Data");
        }
    }

    if(DebugLevel & DEBUGLEVEL_MGR_POINT)
    {
        CTILOG_DEBUG(dout, "Done looking for Dynamic Dispatch Data");
    }
}

void CtiPointClientManager::loadDynamicPoint(Cti::Database::DatabaseReader &rdr)
{
    long lTemp = 0;

    rdr["pointid"] >> lTemp;                        // get the point id

    if( CtiPointSPtr pTempPoint = getCachedPoint(lTemp) )
    {
        if( CtiDynamicPointDispatchSPtr pDyn = getDynamic(*pTempPoint) )
        {
            if(pDyn->getDispatch().getUpdatedFlag() == FALSE)
            {
                pDyn->getDispatch().DecodeDatabaseReader(rdr);              // Decode the current row.

                UINT statictags = pDyn->getDispatch().getTags();
                pDyn->getDispatch().resetTags();                    // clear them all!
                pDyn->getDispatch().setTags(pTempPoint->adjustStaticTags(statictags));   // make the static tags match...
                pDyn->getDispatch().setUpdatedFlag();
                pDyn->getDispatch().resetDirty();                           // Set tags would normally dirty things up!
            }
        }
    }
    else
    {
        CTILOG_WARN(dout, "Point id "<< lTemp <<" found in "<< CtiTablePointDispatch::getTableName() <<", no other point info available");
    }
}

//Grab reasonability limits from TBL_UOM
void CtiPointClientManager::refreshReasonabilityLimits(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    Cti::Timing::DebugTimer timer{ "Looking for Reasonability limits",  !!(DebugLevel & DEBUGLEVEL_MGR_POINT) };

    const auto baseSql = 
        "select PU.pointid, PU.highreasonabilitylimit, PU.lowreasonabilitylimit from pointunit PU"s;
    const auto whereSql = 
        " where (highreasonabilitylimit != 1E30 OR lowreasonabilitylimit != -1E30)"
        " AND highreasonabilitylimit != lowreasonabilitylimit"s;

    DatabaseConnection conn;
    DatabaseReader rdr(conn);

    if(pntID)
    {
        rdr.setCommandText(
            baseSql 
            + whereSql 
            + " AND " + Cti::Database::createIdEqualClause("PU", "pointid"));
        
        rdr << pntID;
    }
    else if(paoID)
    {
        //This assumes it is an ADD, an update needs to do an erase here!
        rdr.setCommandText(
            baseSql
            + " join Point P on PU.pointid = P.pointid"
            + whereSql
            + " AND " + Cti::Database::createIdEqualClause("P", "paobjectid"));

        rdr << paoID;
    }
    else if(!pointIds.empty())
    {
        rdr.setCommandText(
            baseSql
            + whereSql
            + " AND " + Cti::Database::createIdInClause("PU", "pointid", pointIds.size()));

        rdr << pointIds;
    }
    else
    {
        rdr.setCommandText(baseSql + whereSql);

        _reasonabilityLimits.clear();
    }

    rdr.execute();

    if( ! rdr.isValid() )
    {
        CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
    }
    else if( DebugLevel & DEBUGLEVEL_MGR_POINT )
    {
        CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
    }

    int pointid;
    ReasonabilityLimitStruct limitValues;
    std::list<pair<long, ReasonabilityLimitStruct> > tempList;
    while( rdr() )
    {
        rdr >> pointid >> limitValues.highLimit >> limitValues.lowLimit;
        tempList.push_back(make_pair(pointid, limitValues));
    }

    {
        coll_type::writer_lock_guard_t guard(getLock());
        // If this is a reload we must erase regardless of whether or not this loads.
        if( pntID != 0 )
        {
            _reasonabilityLimits.erase(pntID);
        }
        for( long tempPointID : pointIds )
        {
            _reasonabilityLimits.erase(tempPointID);
        }
        for( std::list<pair<long, ReasonabilityLimitStruct> >::iterator iter = tempList.begin(); iter != tempList.end(); iter++ )
        {
            // This is here as a precaution of the future use of PAO's in this function call
            // If this was used for reloads, this would be necessary
            if( paoID != 0 )
            {
                _reasonabilityLimits.erase(iter->first);
            }
            _reasonabilityLimits.insert(*iter);
        }
    }
}


void CtiPointClientManager::refreshPointLimits(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    Cti::Timing::DebugTimer timer{ "Looking for Limits",  !!(DebugLevel & DEBUGLEVEL_MGR_POINT) };

    DatabaseConnection conn;
    DatabaseReader     rdr(conn);

    /* Go after the point limits! */

    if ( pntID )
    {
        rdr.setCommandText( CtiTablePointLimit::getSqlForPointId() );

        rdr << pntID;
    }
    else if ( paoID )
    {
        rdr.setCommandText( CtiTablePointLimit::getSqlForPaoId() );

        rdr << paoID;
    }
    else if ( ! pointIds.empty() )
    {
        rdr.setCommandText( CtiTablePointLimit::getSqlForPointIds( pointIds.size() ) ); 

        rdr << pointIds;
    }
    else
    {
        rdr.setCommandText( CtiTablePointLimit::getSqlForFullLoad() );
    }

    rdr.execute();

    if( ! rdr.isValid() )
    {
        CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
    }
    else if( DebugLevel & DEBUGLEVEL_MGR_POINT )
    {
        CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
    }

    std::list<CtiTablePointLimit> tempList;
    while( rdr() )
    {
        tempList.push_back(CtiTablePointLimit(rdr));
    }

    {
        coll_type::writer_lock_guard_t guard(getLock());
        // If this is a reload we must erase regardless of whether or not this loads.
        if(pntID != 0)
        {
            _limits.erase(CtiTablePointLimit(pntID, 1));
            _limits.erase(CtiTablePointLimit(pntID, 2));
        }
        for( long tempPointID : pointIds )
        {
            _limits.erase(CtiTablePointLimit(tempPointID, 1));
            _limits.erase(CtiTablePointLimit(tempPointID, 2));
        }
        for( std::list<CtiTablePointLimit>::iterator iter = tempList.begin(); iter != tempList.end(); iter++ )
        {
            // This is here as a precaution of the future use of PAO's in this function call
            // If this was used for reloads, this would be necessary
            if( paoID != 0 )
            {
                _limits.erase(CtiTablePointLimit(*iter));
            }
            _limits.insert(CtiTablePointLimit(*iter));
        }
    }
}

void CtiPointClientManager::expire(long pid)
{
    CTILOG_DEBUG( dout, "Expiring point " << pid );

    removePoint( pid, true );
    Inherited::expire(pid);
}

void CtiPointClientManager::erase(long pid)
{
    CTILOG_DEBUG(dout, "Erasing point " << pid );

    coll_type::writer_lock_guard_t guard( getLock() );

    auto pointConIter = _pointConnectionMap.find( pid );
    if( pointConIter != _pointConnectionMap.end() )
    {
        /*
         * We get the connection manager collection out of pointConIter here because
         *   pointConIter may get invalidated in RemoveConnectionManager().
         *   connectionManagers is safe to iterate on, pointConIter is not.
         */
        CtiPointConnection::CollectionType connectionManagers = pointConIter->second.getManagerList();
        for( auto cm : connectionManagers )
        {
            CTILOG_INFO( dout, "Pre Remove Point " << pid << " from " << cm->getClientName() << " " << reinterpret_cast<size_t>( cm.get() ) << ", use_count=" << cm.use_count() );
            pointConIter->second.removeConnectionManagersFromPoint( cm );

            auto pointMapIter=_conMgrPointMap.find( cm->hash( *cm.get() ) );
            if( pointMapIter != _conMgrPointMap.end() )
            {
                pointMapIter->second.erase( pid );
            }

            // Warning: RemoveConnectionManager could modify _pointConnectionMap so as of this point, pointConIter is invalid.
            CTILOG_INFO( dout, "Post Remove Point " << pid << " from " << cm->getClientName() << " " << reinterpret_cast<size_t>( cm.get() ) << ", use_count=" << cm.use_count() );
        }
    }

    PointConnectionMap::iterator iter = _pointConnectionMap.find( pid );
    if( iter != _pointConnectionMap.end() )
    {
        if( iter->second.IsEmpty() )
        {
            _pointConnectionMap.erase( iter );
        }
    }

    removePoint(pid, false);
    Inherited::erase(pid);
}

//Removes almost all internal references to a single point
//If this is an expiration, the dynamic data is not erased.
void CtiPointClientManager::removePoint(long pointID, bool isExpiration)
{
    coll_type::writer_lock_guard_t guard( getLock() );

    //Either this is an expiration and the dynamic values need to be written to the DB, or
    //this is a deletion, and the values cannot be written to the db.
    if(!isExpiration)
    {
        _dynamic.erase(pointID);
        _pointConnectionMap.erase(pointID);

        //Properties are always loaded!
        _properties.erase(pointID);
    }

    removeAlarming(pointID);
    _reasonabilityLimits.erase(pointID);
    _limits.erase(CtiTablePointLimit(pointID, 1));
    _limits.erase(CtiTablePointLimit(pointID, 2));
}

bool CtiPointClientManager::isPointLoaded(LONG Pt)
{
    // The point must be loaded in both locations to exist. If it is loaded
    // in just the inherited container, the dynamic may not yet be loaded.
    // The point will be in only dynamic for a time when the point expires.
    return (getCachedPoint(Pt) && getDynamic(Pt));
}

CtiPointManager::ptr_type CtiPointClientManager::getCachedPoint(LONG Pt)
{
    return Inherited::getPoint(Pt);
}

// This must never be called by refreshList!
CtiPointManager::ptr_type CtiPointClientManager::getPoint(LONG Pt, LONG pao)
{
    Inherited::ptr_type retVal = Inherited::getPoint(Pt);

    if(!retVal)
    {
        refreshList(Pt);
        retVal = Inherited::getPoint(Pt);
    }
    return retVal;
}

std::set<long> CtiPointClientManager::getRegistrationSet(LONG mgrID, Cti::Test::use_in_unit_tests_only&)
{
    return Cti::mapFindOrDefault(_conMgrPointMap, mgrID, {});
}


//Returns pair<HighLimit, LowLimit>
//returns pair<0, 0> if the limit is invalid.
CtiPointClientManager::ReasonabilityLimitStruct CtiPointClientManager::getReasonabilityLimits(const CtiPointBase &point) const
{
    ReasonabilityLimitStruct retVal = { 0, 0 };

    {
        coll_type::reader_lock_guard_t guard(getLock());

        ReasonabilityLimitMap::const_iterator iter;
        iter = _reasonabilityLimits.find(point.getPointID());
        if(iter != _reasonabilityLimits.end())
        {
            retVal = iter->second;
        }
    }

    return retVal;
}

CtiTablePointLimit CtiPointClientManager::getPointLimit(const CtiPointBase &point, LONG limitNum) const
{
    CtiTablePointLimit retVal(point.getPointID(), limitNum);

    {
        coll_type::reader_lock_guard_t guard(getLock());

        PointLimitSet::const_iterator iter = _limits.find(retVal);
        if(iter != _limits.end())
        {
            retVal = *iter;
        }
    }

    return retVal;
}

/**
 * Returns the stored alarming value, or a default alarming
 * value for the given point.
 *
 * @param point CtiPointSPtr
 *
 * @return CtiTablePointAlarming
 */
CtiTablePointAlarming CtiPointClientManager::getAlarming(const CtiPointBase &point) const
{
    CtiTablePointAlarming retVal(point.getPointID());

    {
        coll_type::reader_lock_guard_t guard(getLock());

        PointAlarmingSet::const_iterator iter = _alarming.find(retVal);
        if(iter != _alarming.end())
        {
            retVal = *iter;
        }
    }

    return retVal;
}

bool CtiPointClientManager::setDynamic(long pointID, CtiDynamicPointDispatchSPtr &point)
{
    coll_type::writer_lock_guard_t guard(getLock());

    return (_dynamic.insert(make_pair(pointID, point))).second;
}

CtiDynamicPointDispatchSPtr CtiPointClientManager::getDynamic(const CtiPointBase &point) const
{
    CtiDynamicPointDispatchSPtr retVal;

    coll_type::reader_lock_guard_t guard(getLock());
    DynamicPointDispatchIterator iter = _dynamic.find(point.getPointID());
    if( iter != _dynamic.end() )
    {
        retVal = iter->second;
    }

    return retVal;
}

/**
 * Private function, allows the use of a point id instead of
 * requiring a pointer.
 *
 * @param pointID long
 *
 * @return CtiDynamicPointDispatch*
 */
CtiDynamicPointDispatchSPtr CtiPointClientManager::getDynamic(unsigned long pointID) const
{
    CtiDynamicPointDispatchSPtr retVal;

    {
        coll_type::reader_lock_guard_t guard(getLock());
        DynamicPointDispatchIterator iter = _dynamic.find(pointID);
        if( iter != _dynamic.end() )
        {
            retVal = iter->second;
        }
    }

    return retVal;
}

int CtiPointClientManager::getProperty(LONG point, unsigned int propertyID) const
{
    coll_type::reader_lock_guard_t guard(getLock());

    PointPropertyRange range = _properties.equal_range(point);

    for( ; range.first != range.second; ++range.first )
    {
        const CtiTablePointPropertySPtr property_ptr = range.first->second;
        if( property_ptr && property_ptr->getPropertyID() == propertyID )
        {
            return property_ptr->getIntProperty();
        }
    }

    return numeric_limits<int>::min();
}

bool CtiPointClientManager::hasProperty(LONG point, unsigned int propertyID) const
{
    coll_type::reader_lock_guard_t guard(getLock());

    PointPropertyRange range = _properties.equal_range(point);

    for( ; range.first != range.second; ++range.first )
    {
        const CtiTablePointPropertySPtr property_ptr = range.first->second;
        if( property_ptr && property_ptr->getPropertyID() == propertyID )
        {
            return true;
        }
    }

    return false;
}

void CtiPointClientManager::getPointsWithProperty(unsigned int propertyID, vector<long> &points)
{
    coll_type::reader_lock_guard_t guard(getLock());

    pair<PointPropertyMap::const_iterator,
         PointPropertyMap::const_iterator> range = make_pair(_properties.begin(), _properties.end());

    for( ; range.first != range.second; ++range.first )
    {
        const CtiTablePointPropertySPtr property_ptr = range.first->second;
        if( property_ptr && property_ptr->getPropertyID() == propertyID )
        {
            points.push_back(property_ptr->getPointID());
        }
    }
}

bool CtiPointClientManager::pointHasConnection(LONG pointID, const CtiServer::ptr_type &Conn)
{
    bool retVal = false;
    coll_type::reader_lock_guard_t guard(getLock());

    PointConnectionMap::iterator iter = _pointConnectionMap.find(pointID);
    if(iter != _pointConnectionMap.end())
    {
        retVal = iter->second.HasConnection(Conn);
    }

    return retVal;
}

CtiPointManager::ptr_type CtiPointClientManager::getOffsetTypeEqual(LONG pao, INT offset, CtiPointType_t type)
{
    if( pao )
    {
        if( const int pointID = GetPIDFromDeviceAndOffsetAndType(pao, offset, type) )
        {
            return getPoint(pointID);
        }
    }

    return ptr_type();
}

/**
 * This looks up the point id from the database, then loads the
 * point from the cache or the database. This can result in
 * several database hits.
 *
 * @param pao LONG
 * @param offset INT
 *
 * @return CtiPointManager::ptr_type
 */
CtiPointManager::ptr_type CtiPointClientManager::getControlOffsetEqual(LONG pao, INT offset)
{
    Inherited::ptr_type retVal;

    if( pao != 0 )
    {
        int pointID = GetPIDFromDeviceAndControlOffset(pao, offset);

        if( pointID != 0 )
        {
            retVal = getPoint(pointID);
        }
    }

    return retVal;
}


auto CtiPointClientManager::generatePointDataSqlStatements(const std::vector<int>& pointIds) -> std::vector<ParameterizedIdQuery>
{
    int max_ids_per_select = gConfigParms.getValueAsInt("MAX_IDS_PER_SELECT", POINT_REFRESH_SIZE);

    std::vector<ParameterizedIdQuery> results;

    for( const auto chunk : Cti::Coroutines::chunked(pointIds, max_ids_per_select) )
    {
        auto chunkVector = boost::copy_range<std::vector<long>>(chunk);  //  upcast to long

        results.push_back(ParameterizedIdQuery{ CtiTablePointDispatch::getSQLforPointValues(chunkVector.size()), std::move(chunkVector) });
    }

    return results;
}


long createBaseTags(DatabaseReader& rdr)
{
    const long offset = rdr["POINTOFFSET"].as<long>();
    const auto pseudoFlag   = rdr["PSEUDOFLAG"  ].is<'p'>();
    const auto serviceFlag  = rdr["SERVICEFLAG" ].is<'y'>();
    const auto alarmInhibit = rdr["ALARMINHIBIT"].is<'y'>();

    return CtiPointBase::makeStaticTags( ! offset || pseudoFlag, serviceFlag, alarmInhibit);
}

long createAnalogTags(DatabaseReader& rdr, const CtiPointType_t type)
{
    auto tags = createBaseTags(rdr);

    rdr["ControlInhibit"];  //  move the reader to the ControlInhibit column

    const bool isControlAvailable = type == AnalogOutputPointType || rdr.isNotNull();
    const bool isControlInhibited = rdr.is<'y'>();

    return tags | CtiPointBase::makeStaticControlTags(isControlAvailable, isControlInhibited);
}

long createStatusTags(DatabaseReader& rdr)
{
    auto tags = createBaseTags(rdr);

    rdr["ControlType"];  //  move the reader to the ControlType column

    if( rdr.isNotNull() )
    {
        const bool isControlAvailable = ControlType_Invalid != resolveControlType(rdr.as<std::string>());
        const bool isControlInhibited = rdr["ControlInhibit"].is<'y'>();

        tags |= CtiPointBase::makeStaticControlTags(isControlAvailable, isControlInhibited);
    }

    return tags;
}


std::unique_ptr<CtiPointDataMsg> createPointDataMsg(DatabaseReader& rdr, const long pointId)
{
    std::unique_ptr<CtiPointDataMsg> pDat;

    //  No DynamicPaoInfo entry
    if( rdr["value"].isNull() )
    {
        const auto pointType = resolvePointType(rdr["pointtype"].as<std::string>());
        const bool isStatus  = CtiPointBase::isStatus(pointType);

        const double value = isStatus ? rdr["initialstate"].as<double>() : 0.0;

        const long tags = isStatus ? createStatusTags(rdr) : createAnalogTags(rdr, pointType);

        pDat = std::make_unique<CtiPointDataMsg>(
            pointId,
            value,
            UnintializedQuality,
            pointType,
            string(),
            tags);

        pDat->setTime(UninitializedTimestamp);
    }
    else
    {
        pDat = std::make_unique<CtiPointDataMsg>(
            pointId,
            rdr["value"].as<double>(),
            rdr["quality"].as<int>(),
            resolvePointType(rdr["pointtype"].as<std::string>()),
            string(),
            rdr["tags"].as<long>());

        pDat->setTime(rdr["timestamp"].as<CtiTime>());
    }

    return pDat;
}



//  Collects current point values from cached points if loaded, and reads the rest from the database
auto CtiPointClientManager::getCurrentPointData(const std::vector<int>& pointIds) -> PointDataResults
{
    PointDataResults results;

    results.pointData.reserve(pointIds.size());

    std::map<int, size_t> cacheMisses;

    for( const auto pid : pointIds )
    {
        if( const auto pPt = getCachedPoint(pid) )
        {
            if( const auto pDyn = getDynamic(*pPt) )
            {
                auto pDat = 
                    std::make_unique<CtiPointDataMsg>(
                        pPt->getID(),
                        pDyn->getValue(),
                        pDyn->getQuality(),
                        pPt->getType(),
                        string(),
                        pDyn->getDispatch().getTags());

                pDat->setTime( pDyn->getTimeStamp() );  // Make the time match the point's last received time
                
                results.pointData.emplace_back(std::move(pDat));

                continue;
            }
        }

        cacheMisses.emplace(pid, results.pointData.size());  //  results.size() is the zero-based index of the new item we're adding

        results.pointData.emplace_back(nullptr);
    }

    if( ! cacheMisses.empty() )
    {
        CTILOG_DEBUG(dout, "Dynamic points not loaded for " << cacheMisses.size() << " points, retrieving values directly from DynamicPointDispatch");

        const auto pointDataStatements = generatePointDataSqlStatements(boost::copy_range<std::vector<int>>(cacheMisses | boost::adaptors::map_keys));

        std::map<int, std::unique_ptr<CtiPointDataMsg>> cacheMissPointData;

        DatabaseConnection conn;

        for( const auto& pointDataStatement : pointDataStatements )
        {
            DatabaseReader rdr{ conn, pointDataStatement.sql };

            rdr << pointDataStatement.pointIds;

            rdr.execute();

            while( rdr() )
            {
                const auto pointId = rdr["pointid"].as<long>();

                auto pDat = createPointDataMsg(rdr, pointId);

                auto itr = cacheMisses.find(pointId);
                if( itr != cacheMisses.end() )
                {
                    results.pointData[itr->second] = std::move(pDat);
                    cacheMisses.erase(itr);
                }
                else
                {
                    CTILOG_WARN(dout, "Unknown point ID returned:" << *pDat);
                }
            }
        }

        //  We didn't load all of the cache misses - these IDs are unexpectedly missing
        if( ! cacheMisses.empty() )
        {
            //  Remove the null entries from the pointData list
            auto itr = boost::remove_if(results.pointData, [](const auto& pd) { return !pd; });
            results.pointData.erase(itr, results.pointData.end());

            results.missingIds = boost::copy_range<std::vector<int>>(cacheMisses | boost::adaptors::map_keys);
        }
    }

    return results;
}


/**
 * Adds the alarming table to the internal alarming stores
 *
 * @param table CtiTablePointAlarming&
 */
void CtiPointClientManager::addAlarming(CtiTablePointAlarming &table)
{
    _alarming.insert(table);
}

/**
 * Removes the alarming entry from internal stores with the
 * given point ID.
 *
 * @param pointID unsigned long
 */
void CtiPointClientManager::removeAlarming(unsigned long pointID)
{
    _alarming.erase(pointID);
}

void CtiPointClientManager::refreshPoints( std::set<long> &pointIdsFound, Cti::RowReader& rdr )
{
    Inherited::refreshPoints( pointIdsFound, rdr );
}
