#include "precompiled.h"

#include "dllvg.h"
#include "pt_base.h"
#include "logger.h"
#include "mgr_ptclients.h"
#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"
#include "database_transaction.h"
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

#include <list>

using namespace std;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

#define POINT_REFRESH_SIZE 1000 //This is overriden by cparm.

/*
 *  This method initializes each point's dynamic data to it's default/initial values.
 */
void ApplyInitialDynamicConditions(const long key, CtiPointSPtr pTempPoint, void* d)
{
    CtiPointClientManager *pointManager = (CtiPointClientManager*)d;
    CtiDynamicPointDispatchSPtr pDyn = pointManager->getDynamic(*pTempPoint);

    if(!pDyn)
    {
        if((pDyn = CtiDynamicPointDispatchSPtr(CTIDBG_new CtiDynamicPointDispatch(pTempPoint->getID()))))
        {
            UINT statictags = pDyn->getDispatch().getTags();
            pTempPoint->adjustStaticTags(statictags);

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
            pDyn->getDispatch().resetTags(CtiTablePointBase::MASK_POINT_BASE_TAGS);
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
        for each( long pointid in pointIds )
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

/**
 * Loads the data from the database for all point id's
 * requested. Recursively calls itself if more id's are
 * requested than the max number allowed.
 *
 * @param ids const set<long>&
 */
void CtiPointClientManager::refreshListByPointIDs(const set<long> &ids)
{
    unsigned long max_size = gConfigParms.getValueAsULong("MAX_IDS_PER_POINT_SELECT", POINT_REFRESH_SIZE);

    if( ids.size() > max_size )
    {
        set<long> tempIds;

        for( set<long>::const_iterator iter = ids.begin(); iter != ids.end(); )
        {
            tempIds.insert(*iter);
            iter++;
            if( tempIds.size() == max_size || iter == ids.end() )
            {
                refreshListByPointIDs(tempIds);
                tempIds.clear();
            }
        }
    }
    else
    {
        Inherited::refreshListByPointIDs(ids);
        refreshAlarming(0, 0, ids);
        refreshReasonabilityLimits(0, 0, ids);
        refreshPointLimits(0, 0, ids);
        processPointDynamicData(0, ids);
        //refreshProperties(0, 0, ids);
    }


}

void CtiPointClientManager::refreshAlarming(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    string         sql;
    CtiTime start, stop;

    if( DebugLevel & DEBUGLEVEL_MGR_POINT )
    {
        CTILOG_DEBUG(dout, "Looking for Alarming");
    }

    start = start.now();
    CtiTablePointAlarming::getSQL(sql, pntID, paoID, pointIds);

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
    rdr.execute();

    if( ! rdr.isValid() )
    {
        CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
    }
    else if( DebugLevel & DEBUGLEVEL_MGR_POINT )
    {
        CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
    }

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
        for each( long tempPointID in pointIds )
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

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CTILOG_INFO(dout, (stop.seconds() - start.seconds()) <<" seconds for Alarming");
    }

    if(DebugLevel & DEBUGLEVEL_MGR_POINT)
    {
        CTILOG_DEBUG(dout, "Done looking for Alarming");
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
        ostringstream in_list;

        csv_output_iterator<long, ostringstream> csv_out(in_list);

        in_list << "(";

        copy(pointIds.begin(), pointIds.end(), csv_out);

        in_list << ")";

        sql += ((!pntID && !paoID) ? " WHERE" : " AND");
        sql += " PPV.pointid IN ";
        sql += in_list.str();
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
        sql += " AND point.pointid = ";
        sql += CtiNumStr(pntID);
    }

    //  pao is assumed to be an add
    if(paoID)
    {
        sql += " AND point.paobjectid = ";
        sql += CtiNumStr(paoID);
    }

    if(!pointIds.empty())
    {
        ostringstream in_list;

        csv_output_iterator<long, ostringstream> csv_out(in_list);

        in_list << "(";

        copy(pointIds.begin(), pointIds.end(), csv_out);

        in_list << ")";

        sql += " AND point.pointid in ";
        sql += in_list.str();
    }

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
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

int CtiPointClientManager::InsertConnectionManager(CtiServer::ptr_type &CM, const CtiPointRegistrationMsg &aReg, DebugPrint debugprint)
{
    int nRet = 0;
    CTILOG_ENTRY_RC(dout, "CM=" << reinterpret_cast<size_t>(CM.get()) << ", aReg.getFlags()=" << aReg.getFlags(), nRet);

    CtiTime   NowTime;
    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());
    const int ptcnt = aReg.getCount();
    ConnectionMgrPointMap::iterator conIter = _conMgrPointMap.end();

    if(!(aReg.getFlags() & (REG_ADD_POINTS | REG_REMOVE_POINTS)) )     // If add/remove is set, we are augmenting or removing an existing registration (Not the whole thing).
        RemoveConnectionManager(CM);

    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

    if(debugprint == DebugPrint::True)
    {
        Cti::StreamBuffer outLog;
        outLog << CM->getClientName() <<" " << reinterpret_cast<size_t>(CM.get()) << " has registered for "<< ptcnt <<" points:";
        for(int pointNbr = 0 ; pointNbr < ptcnt; pointNbr++)
        {
            outLog << endl << aReg[pointNbr];
        }

        CTILOG_DEBUG(dout, outLog);
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
            WeakPointMap tempMap;
            pair<ConnectionMgrPointMap::iterator, bool> tempVal;
            tempVal = _conMgrPointMap.insert(ConnectionMgrPointMap::value_type(CM->hash(*CM.get()), tempMap));
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

    for( int i = 0; i < ptcnt; i++ )
    {
        /*
         *  OK, now I walk the list of points looking at each one's ID to find who to add this guy to
         */

        {
            CtiPointSPtr temp = getPoint(aReg[i]);
            if(temp)
            {
                if(!((const CtiVanGoghConnectionManager *)CM.get())->isRegForChangeType(temp->getType())) // Make sure we didn't already register for ALL points of this type.
                {
                    // Prevent _pointConnectionMap from wiggling while we operate.
                    coll_type::writer_lock_guard_t guard( getLock() );

                    if(aReg.getFlags() & REG_REMOVE_POINTS)
                    {
                        PointConnectionMap::iterator iter = _pointConnectionMap.find(temp->getPointID());
                        if(iter != _pointConnectionMap.end())
                        {
                            iter->second.RemoveConnectionManager(CM);
                            if(iter->second.IsEmpty())
                            {
                                _pointConnectionMap.erase(iter);
                            }
                        }

                        if( conIter != _conMgrPointMap.end() )
                        {
                            conIter->second.erase(temp->getPointID());
                            CTILOG_DEBUG(dout, "Removing point " << temp->getPointID() 
                                << " from _conMgrPointMap(" << hex << CM->hash(*CM.get()) << ") = 0x" << hex << &conIter->second);
                        }
                    }
                    else
                    {
                        PointConnectionMap::iterator iter = _pointConnectionMap.find( temp->getPointID() );
                        if(iter == _pointConnectionMap.end())
                        {
                            pair<PointConnectionMap::iterator, bool> insertResult = _pointConnectionMap.insert(PointConnectionMap::value_type(temp->getPointID(), CtiPointConnection()));
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
                            CTILOG_DEBUG(dout, "Adding point " << temp->getPointID()
                                << " to _conMgrPointMap(" << hex << CM->hash(*CM.get()) << ") = 0x" << hex << &conIter->second);

                            pair<WeakPointMap::iterator, bool> was = conIter->second.insert(WeakPointMap::value_type(temp->getPointID(), temp));

                            CTILOG_DEBUG(dout, "_conMgrPointMap(" << hex << CM->hash(*CM.get()) << ") has " << dec 
                                << conIter->second.size() << " entries.  Insert return was " << was.second);
                        }
                    }
                }
            }
        }
    }
    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

    return nRet;
}

int CtiPointClientManager::RemoveConnectionManager(CtiServer::ptr_type &CM, DebugPrint debugprint)
{
    int nRet = 0;
    CTILOG_ENTRY_RC(dout, "CM=" << reinterpret_cast<size_t>(CM.get()), nRet);

    // OK, now I walk the list of points looking at each one's list to remove the CM
    {
        coll_type::writer_lock_guard_t guard(getLock());
        ConnectionMgrPointMap::iterator conIter = _conMgrPointMap.find(CM->hash(*CM.get()));

        CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

        if( conIter != _conMgrPointMap.end() && conIter->second.size() > 0 )
        {
            CTILOG_DEBUG(dout, " point list size=" << conIter->second.size());

            long pointID;
            for(WeakPointMap::iterator pointIter = conIter->second.begin(); pointIter != conIter->second.end(); pointIter++)
            {
                pointID = pointIter->first;

                PointConnectionMap::iterator iter = _pointConnectionMap.find( pointID );
                if(iter != _pointConnectionMap.end())
                {
                    if(debugprint == DebugPrint::True)
                    {
                        CTILOG_DEBUG(dout, "Removing CM " << reinterpret_cast<size_t>(CM.get()) << " from pointid " << pointID);
                    }
                    iter->second.RemoveConnectionManager(CM);
                    if(iter->second.IsEmpty())
                    {
                        _pointConnectionMap.erase(iter);
                    }
                }
                else
                {
                    CTILOG_DEBUG(dout, "Cant find pointid " << pointID);
                }
            }
        }
    }

    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

    _conMgrPointMap.erase(CM->hash(*CM.get()));

    CTILOG_DEBUG(dout, CM->getClientName() << " " << reinterpret_cast<size_t>(CM.get()) << " use_count=" << CM.use_count());

    return nRet;
}

CtiTime CtiPointClientManager::findNextNearestArchivalTime()
{
    CtiTime   closeTime(YUKONEOT);

    vector<long> points;
    getPointsWithProperty(CtiTablePointProperty::ARCHIVE_ON_TIMER, points);

    for each( long ptid in points )
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

void CtiPointClientManager::scanForArchival(const CtiTime &Now, boost::ptr_vector<CtiTableRawPointHistory> &Que)
{
    {
        Cti::Timing::DebugTimer debugTime("Scanning For Archival", false, 1);
        coll_type::writer_lock_guard_t guard(getLock());
        vector<long> points;
        getPointsWithProperty(CtiTablePointProperty::ARCHIVE_ON_TIMER, points);

        for each( long ptid in points )
        {
            if( CtiPointSPtr pPt = getPoint(ptid) )
            {
                if( CtiDynamicPointDispatchSPtr pDyn = getDynamic(*pPt) )
                {
                    // Make sure the point is not disabled.
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
                                        Que.push_back(new CtiTableRawPointHistory(pPt->getID(), pDyn->getQuality(), pDyn->getValue(), Now, 0));
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
}

void CtiPointClientManager::getDirtyRecordList(list<CtiDynamicPointDispatchSPtr> &updateList)
{
    ptr_type pPt;
    coll_type::writer_lock_guard_t guard(getLock());
    DynamicPointDispatchIterator itr = _dynamic.begin();
    DynamicPointDispatchIterator listEnd = _dynamic.end();

    for( ;itr != listEnd; itr++)
    {
        try
        {
            CtiDynamicPointDispatchSPtr pDyn = itr->second;

            if(pDyn && (pDyn->getDispatch().isDirty() || !pDyn->getDispatch().getUpdatedFlag()) )
            {
                UINT statictags = pDyn->getDispatch().getTags();
                pDyn->getDispatch().resetTags();                    // clear them all!
                if( (pPt = getCachedPoint(itr->first)) )
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
}

void CtiPointClientManager::writeRecordsToDB(list<CtiDynamicPointDispatchSPtr> &updateList)
{
    Cti::Database::DatabaseConnection   conn;

    if ( ! conn.isValid() )
    {
        CTILOG_ERROR(dout, "Invalid Connection to Database");
        return;
    }

    const int total = updateList.size();

    CTILOG_INFO(dout, "WRITING "<< total <<" dynamic dispatch records");

    int listCount = 0;

    for each( CtiDynamicPointDispatchSPtr pdyn in updateList )
    {
        CtiTablePointDispatch& dispatch = pdyn->getDispatch();

        if( ! dispatch.writeToDB(conn) )
        {
            if( dispatch.isPointIdInvalid() )
            {
                CTILOG_WARN(dout, "Removing record for invalid point ID "<< dispatch.getPointID());

                erase( dispatch.getPointID() );
            }
        }
        else if(++listCount % 1000 == 0)
        {
            CTILOG_INFO(dout, "WRITING dynamic dispatch records to DB, "<< listCount <<" of "<< total <<" records written");
        }
    }
}

void CtiPointClientManager::removeOldDynamicData()
{
    int recordCount = 0;
    coll_type::writer_lock_guard_t guard(getLock());
    DynamicPointDispatchIterator itr = _dynamic.begin();
    DynamicPointDispatchIterator ptsEnd = _dynamic.end();

    for( ;itr != ptsEnd;)
    {
        try
        {
            if(!isPointLoaded(itr->first))
            {
                itr = _dynamic.erase(itr);
                recordCount ++;
            }
            else
            {
                itr++;
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            break;
        }
    }

    CTILOG_INFO(dout, "PURGED "<< recordCount <<" records from memory");
}

void CtiPointClientManager::storeDirtyRecords()
{
    int recordCount = 0;
    list<CtiDynamicPointDispatchSPtr> updateList;

    getDirtyRecordList(updateList);

    recordCount = updateList.size();

    writeRecordsToDB(updateList);

    removeOldDynamicData();

    if(recordCount > 0 && gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
    {
        CTILOG_DEBUG(dout, "Updated "<< recordCount <<" dynamic dispatch records");
    }

    return;
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

    const vector<string> queries(1, CtiTablePointDispatch::getSQLCoreStatement(id));

    executeDynamicDataQueries(queries);
}

void CtiPointClientManager::refreshDynamicDataForPointSet(const set<long> &pointIds)
{
    if( pointIds.empty() )
    {
        CTILOG_ERROR(dout, "No pointIds were provided. Unable to refresh dynamic data for points.");
        return;
    }

    const vector<string> queries = generateSqlStatements(pointIds);

    executeDynamicDataQueries(queries);
}

void CtiPointClientManager::refreshDynamicDataForAllPoints()
{
    // We want the sql without a single-id where clause.
    const vector<string> queries(1, CtiTablePointDispatch::getSQLCoreStatement(0));

    executeDynamicDataQueries(queries);
}

vector<string> CtiPointClientManager::generateSqlStatements(const set<long> &pointIds)
{
    // We want the sql without a single-id where clause.
    const string sql = CtiTablePointDispatch::getSQLCoreStatement(0);

    vector<string> queries;

    Cti::Database::id_set_itr pointid_itr = pointIds.begin();

    int max_ids_per_select = gConfigParms.getValueAsInt("MAX_IDS_PER_POINT_SELECT", POINT_REFRESH_SIZE);

    do
    {
        // Let the chunking begin.
        string chunk = sql;

        int subset_size = min(distance(pointid_itr, pointIds.end()), max_ids_per_select);

        Cti::Database::id_set_itr subset_end = pointid_itr;
        advance(subset_end, subset_size);
        Cti::Database::id_set pointid_subset(pointid_itr, subset_end);

        std::ostringstream in_list;
        csv_output_iterator<long, std::ostringstream> csv_itr(in_list);

        in_list << "(";

        copy(pointid_subset.begin(), pointid_subset.end(), csv_itr);

        in_list << ")";

        chunk += " WHERE DPD.pointid IN ";
        chunk += in_list.str();

        // Sql is completed.
        queries.push_back(chunk);

        // Grab the next chunk.
        advance(pointid_itr, subset_size);

    } while( pointid_itr != pointIds.end() );

    return queries;
}

void CtiPointClientManager::executeDynamicDataQueries(const vector<string> &queries)
{
    CtiTime start, stop;

    if(DebugLevel & DEBUGLEVEL_MGR_POINT)
    {
        CTILOG_DEBUG(dout, "Looking for Dynamic Dispatch Data");
    }

    Cti::Database::DatabaseConnection connection;

    for each( const string &sql in queries )
    {
        Cti::Database::DatabaseReader rdr(connection, sql);

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
    CtiTime start, stop;
    string sql;

    if(DebugLevel & DEBUGLEVEL_MGR_POINT)
    {
        CTILOG_DEBUG(dout, "Looking for Reasonability limits");
    }

    sql = "select pointid, highreasonabilitylimit, lowreasonabilitylimit from pointunit where "
          "(highreasonabilitylimit != 1E30 OR lowreasonabilitylimit != -1E30) "
          "AND highreasonabilitylimit != lowreasonabilitylimit";

    if(pntID)
    {
        sql += " AND pointid = " + CtiNumStr(pntID);
    }
    else if(paoID)
    {
        //This assumes it is an ADD, an update needs to do an erase here!
        sql += " AND pointid in (select pointid from point where paobjectid = " + CtiNumStr(paoID) + ")";
    }
    else if(!pointIds.empty())
    {
        sql += " AND pointid in (";

        ostringstream in_list;
        csv_output_iterator<long, ostringstream> csv_out(in_list);

        copy(pointIds.begin(), pointIds.end(), csv_out);

        sql += in_list.str();

        sql += ")";
    }
    else
    {
        _reasonabilityLimits.clear();
    }

    start = start.now();

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
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
        for each( long tempPointID in pointIds )
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

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CTILOG_INFO(dout , (stop.seconds() - start.seconds()) <<" seconds for Reasonability Limits");
    }

    if(DebugLevel & DEBUGLEVEL_MGR_POINT)
    {
        CTILOG_DEBUG(dout, "Done looking for Reasonability Limits");
    }
}


void CtiPointClientManager::refreshPointLimits(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    LONG   lTemp = 0;
    string sql;

    CtiTime         start, stop;

    start = start.now();

    if(DebugLevel & DEBUGLEVEL_MGR_POINT)
    {
        CTILOG_DEBUG(dout, "Looking for Limits");
    }
    /* Go after the point limits! */
    CtiTablePointLimit::getSQL( sql, pntID, paoID, pointIds );

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
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
        for each( long tempPointID in pointIds )
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

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CTILOG_INFO(dout , (stop.seconds() - start.seconds()) <<" seconds for Limits");
    }

    if(DebugLevel & DEBUGLEVEL_MGR_POINT)
    {
        CTILOG_DEBUG(dout, "Done looking for Limits");
    }
}

void CtiPointClientManager::expire(long pid)
{
    removePoint(pid, true);
    Inherited::expire(pid);
}

void CtiPointClientManager::erase(long pid)
{
    removePoint(pid, false);
    Inherited::erase(pid);
}

//Removes almost all internal references to a single point
//If this is an expiration, the dynamic data is not erased.
void CtiPointClientManager::removePoint(long pointID, bool isExpiration)
{
    CTILOG_ENTRY(dout, "pointID=" << pointID << ", isExpiration=" << isExpiration);

    coll_type::writer_lock_guard_t guard( getLock() );

    auto pointConIter = _pointConnectionMap.find( pointID );
    if(pointConIter != _pointConnectionMap.end())
    {
        CtiPointConnection::CollectionType collection = pointConIter->second.getManagerList();
        for( auto cm : collection )
        {
            CTILOG_INFO(dout, "Pre removePoint " << reinterpret_cast<size_t>(cm.get()) << ", use_count=" << cm.use_count());
            RemoveConnectionManager(cm);
            // Warning: RemoveConnectionManager could modify _pointConnectionMap so as of this point, pointConIter is invalid.
            CTILOG_INFO( dout, "Post removePoint " << reinterpret_cast<size_t>( cm.get() ) << ", use_count=" << cm.use_count() );
        }
    }

    for(ConnectionMgrPointMap::iterator iter = _conMgrPointMap.begin(); iter != _conMgrPointMap.end(); iter++)
    {
        CTILOG_DEBUG(dout, "Removing point " << pointID << " from _conMgrPointMap @ 0x" << hex << &iter->second);
        iter->second.erase(pointID);
    }

    //Either this is an expiration and the dynamic values need to be written to the DB, or
    //this is a deletion, and the values cannot be written to the db.
    if(!isExpiration)
    {
        coll_type::reader_lock_guard_t guard(getLock());

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
    return (Inherited::getPoint(Pt) && getDynamic(Pt));
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

CtiPointClientManager::WeakPointMap CtiPointClientManager::getRegistrationMap(LONG mgrID)
{
    WeakPointMap temp;
    ConnectionMgrPointMap::iterator conIter;

    if( (conIter = _conMgrPointMap.find(mgrID)) != _conMgrPointMap.end() )
    {
        return conIter->second;
    }
    else
    {
        return temp;
    }
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
