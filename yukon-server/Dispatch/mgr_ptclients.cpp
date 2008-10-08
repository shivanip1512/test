/*-----------------------------------------------------------------------------*
*
* File:   mgr_ptclients
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/mgr_ptclients.cpp-arc  $
* REVISION     :  $Revision: 1.44 $
* DATE         :  $Date: 2008/10/08 15:13:02 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/db/db.h>
#include <rw/db/status.h>
#include <rw/toolpro/neterr.h>

#include "dllvg.h"
#include "pt_base.h"
#include "logger.h"
#include "mgr_ptclients.h"
#include "dbaccess.h"
#include "devicetypes.h"
#include "msg_ptreg.h"
#include "msg_pcreturn.h"
#include "msg_signal.h"
#include "pt_dyn_dispatch.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"


#include "con_mgr_vg.h"
#include "pointdefs.h"
#include "resolvers.h"
#include "tbl_ptdispatch.h"
#include <list>

using namespace std;

/*
 *  This method initializes each point's dynamic data to it's default/initial values.
 */
void ApplyInitialDynamicConditions(const long key, CtiPointSPtr pTempPoint, void* d)
{
    CtiPointClientManager *pointManager = (CtiPointClientManager*)d;
    CtiDynamicPointDispatch *pDyn = pointManager->getDynamic(pTempPoint->getID());

    if(pDyn == NULL)
    {
        if(NULL != (pDyn = CTIDBG_new CtiDynamicPointDispatch(pTempPoint->getID())))
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

    return;
}

//This gives the point its initial data. We should not need a mutex for this.
//If loading by pao, it is assumed we are ADDING a point.
void CtiPointClientManager::processPointDynamicData(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    CtiPointSPtr pTempPoint;
    if(pntID)
    {
        //Lets be smart about handling a single point.
        pTempPoint = getEqual(pntID);
        if(pTempPoint)
        {
            ApplyInitialDynamicConditions(0,pTempPoint,this);

            const CtiDynamicPointDispatch *dynamic = getDynamic(pntID);

            //This will probably always be true, but why not check.
            if(dynamic && !dynamic->getDispatch().getUpdatedFlag())
            {
                RefreshDynamicData(pntID);
                //ApplyInsertNonUpdatedDynamicData(0, pTempPoint, NULL);
            }
        }
    }
    else if(paoID)
    {
        vector<Inherited::ptr_type>           pointVec;
        vector<Inherited::ptr_type>::iterator iter;
        getEqualByPAO(paoID, pointVec);

        for(iter = pointVec.begin(); iter != pointVec.end(); iter++)
        {
            if(*iter)
            {
                ApplyInitialDynamicConditions(0,*iter,this);
            }
        }

        //A PAO has nothing in dynamic point dispatch, so it cant be loaded.
    }
    else if(!pointIds.empty())
    {
        set<long>::const_iterator pointid_itr = pointIds.begin(),
                                  pointid_end = pointIds.end();

        for( ; pointid_itr != pointid_end; ++pointid_itr )
        {
            if( pTempPoint = getEqual(*pointid_itr) )
            {
                ApplyInitialDynamicConditions(0, pTempPoint, this);
            }
        }

        RefreshDynamicData(0, pointIds);
    }
}


struct non_updated_check
{
    bool operator()(pair<long, CtiDynamicPointDispatch *> dynamic)
    {
        return dynamic.second && !dynamic.second->getDispatch().getUpdatedFlag();
    }
};


//Points can be loaded by pao, not updated!
void CtiPointClientManager::refreshList(LONG pntID, LONG paoID, CtiPointType_t pntType)
{
    Inherited::refreshList(pntID, paoID, pntType);                // Load all points in the system
    refreshAlarming(pntID, paoID);
    refreshReasonabilityLimits(pntID, paoID);
    refreshPointLimits(pntID, paoID);

    if(pntID != 0 || paoID != 0)
    {
        processPointDynamicData(pntID, paoID);
    }
    else
    {
        CtiPointSPtr pTempPoint;
        Inherited::apply(ApplyInitialDynamicConditions, this);     // Make sure everyone has been initialized with Dynamic data.

        if( find_if(_dynamic.begin(), _dynamic.end(), non_updated_check()) != _dynamic.end() )
        {
            RefreshDynamicData();
        }
    }
}

void CtiPointClientManager::refreshListByPointIDs(const set<long> &ids)
{
    Inherited::refreshListByPointIDs(ids);
    refreshAlarming(0, 0, ids);
    refreshReasonabilityLimits(0, 0, ids);
    refreshPointLimits(0, 0, ids);

    processPointDynamicData(0, 0, ids);
}

//Load based on PAO assumes it is in add!
void CtiPointClientManager::refreshAlarming(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    coll_type::writer_lock_guard_t guard(getLock());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBDatabase   db       = conn.database();
    RWDBSelector   selector = conn.database().selector();
    RWDBTable      keyTable;
    RWDBReader     rdr;
    string         sql;
    CtiTime start, stop;

    start = start.now();
    CtiTablePointAlarming::getSQL(sql, pntID, paoID, pointIds);

    rdr = ExecuteQuery( conn, sql );

    if( rdr.status().errorCode() != RWDBStatus::ok )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << sql << endl;
    }

    LONG pID;
    ptr_type pPt;

    while( rdr() )
    {
        _alarming.insert(CtiTablePointAlarming(rdr));
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Alarming " << endl;
    }
}


void CtiPointClientManager::refreshProperties(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    coll_type::writer_lock_guard_t guard(getLock());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBDatabase   db       = conn.database();
    RWDBSelector   selector = conn.database().selector();
    RWDBTable      keyTable;
    RWDBReader     rdr;
    string         sql;
    CtiTime start, stop;

    RWDBSelector attributeSelector = conn.database().selector();
    start = start.now();
    CtiTablePointProperty::getSQL( db, keyTable, attributeSelector );

    if(pntID)
    {
        attributeSelector.where( keyTable["pointid"] == pntID && attributeSelector.where() );

        _properties.erase(pntID);
    }

    //  pao is assumed to be an add
    if(paoID)
    {
        RWDBSelector paoSelector = conn.database().selector();
        RWDBTable pointTable = db.table("point");
        paoSelector << pointTable["pointid"];
        paoSelector.from(pointTable);
        paoSelector.where(pointTable["paobjectid"] == paoID);
        selector.where(keyTable["pointid"].in(paoSelector) && selector.where());
    }

    if(!pointIds.empty())
    {
        ostringstream in_list;

        csv_output_iterator<long, ostringstream> csv_out(in_list);

        in_list << "(";

        copy(pointIds.begin(), pointIds.end(), csv_out);

        in_list << ")";

        selector.where(selector.where() && keyTable["pointid"].in(RWDBExpr(in_list.str().c_str(), false)));
    }

    rdr = attributeSelector.reader( conn );

    if( rdr.status().errorCode() != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << attributeSelector.asString() << endl;
    }

    while( rdr() )
    {
        CtiTablePointProperty *p = CTIDBG_new CtiTablePointProperty(rdr);
        _properties.insert(make_pair(p->getPointID(), p));
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Properties " << endl;
    }
}

void CtiPointClientManager::DumpList(void)
{
    CtiPointSPtr p;
    try
    {
        coll_type::reader_lock_guard_t guard(getLock());

        spiterator itr = Inherited::begin();
        spiterator end = Inherited::end();

        for( ;itr != end; itr++)
        {
            p = itr->second;

            {
                const CtiDynamicPointDispatch *pDyn = getDynamic(p->getPointID());

                if(p->isValid() && pDyn != NULL)
                {
                    cout << "MemoryPoint \"" << p->getID( ) << "\" defined and initialized" << endl;
                    cout << " Point Value         : " << pDyn->getValue() << endl;
                    cout << " Point Quality       : 0x" << hex << pDyn->getQuality() << dec << endl;
                    cout << " Point Time          : " << pDyn->getTimeStamp() << endl;
                }
                else
                {
                    cout << " Point \"" << p->getID( ) << "\" has been deleted from the database... cleaning up is recommended" << endl;
                }
            }
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        cout << "Attempting to clear point list..." << endl;

        DeleteList();

        cout << "DumpMemoryPoints:  " << e.why() << endl;
        RWTHROW(e);

    }
}

int CtiPointClientManager::InsertConnectionManager(CtiServer::ptr_type CM, const CtiPointRegistrationMsg &aReg, bool debugprint)
{
    int nRet = 0;
    CtiTime   NowTime;
    int ptcnt = aReg.getCount();
    ConnectionMgrPointMap::iterator conIter = _conMgrPointMap.end();

    if(!(aReg.getFlags() & (REG_ADD_POINTS | REG_REMOVE_POINTS)) )     // If add/remove is set, we are augmenting or removing an existing registration (Not the whole thing).
        RemoveConnectionManager(CM);

    /*
     *  if count is greater than zero (there are point id's in the list),
     *  we must be attached to specific points' lists as well.
     */

    if(ptcnt > 0)
    {
        if(debugprint)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << NowTime << " " << CM->getClientName() << " has registered for " << ptcnt << " points" << endl;
        }

        conIter = _conMgrPointMap.find(CM->hash(*CM.get()));
        if( conIter == _conMgrPointMap.end() )
        {
            WeakPointMap tempMap;
            pair<ConnectionMgrPointMap::iterator, bool> tempVal;
            tempVal = _conMgrPointMap.insert(ConnectionMgrPointMap::value_type(CM->hash(*CM.get()), tempMap));
            conIter = tempVal.first;
        }
    }

    for(int i = 0 ; i < ptcnt; i++)
    {
        /*
         *  OK, now I walk the list of points looking at each one's ID to find who to add this guy to
         */

        {
            CtiPointSPtr temp = getEqual(aReg[i]);
            if(temp)
            {
                if(!((const CtiVanGoghConnectionManager *)CM.get())->isRegForChangeType(temp->getType())) // Make sure we didn't already register for ALL points of this type.
                {
                    if(debugprint)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << NowTime << " " << CM->getClientName() << " has registered for point " << aReg[i] << endl;
                        }
                    }

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
                        }
                    }
                    else
                    {
                        PointConnectionMap::iterator iter = _pointConnectionMap.find(temp->getPointID());
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
                            iter->second.AddConnectionManager(CM);
                        }

                        if( conIter != _conMgrPointMap.end() )
                        {
                            conIter->second.insert(WeakPointMap::value_type(temp->getPointID(), temp));
                        }
                    }
                }
            }
        }
    }

    return nRet;
}

int CtiPointClientManager::RemoveConnectionManager(CtiServer::ptr_type CM)
{
    int nRet = 0;

    // OK, now I walk the list of points looking at each one's list to remove the CM
    {
        coll_type::writer_lock_guard_t guard(getLock());
        ConnectionMgrPointMap::iterator conIter = _conMgrPointMap.find(CM->hash(*CM.get()));

        if( conIter != _conMgrPointMap.end() && conIter->second.size() > 0 )
        {
            for(WeakPointMap::iterator pointIter = conIter->second.begin(); pointIter != conIter->second.end(); pointIter++)
            {
                CtiPointSPtr temp = pointIter->second.lock();

                if( temp )
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
                }
            }
        }
    }
    _conMgrPointMap.erase(CM->hash(*CM.get()));

    return nRet;
}

CtiTime CtiPointClientManager::findNextNearestArchivalTime()
{
    CtiTime   closeTime(YUKONEOT);

    {
        coll_type::reader_lock_guard_t guard(getLock());
        spiterator itr = Inherited::begin();
        spiterator end = Inherited::end();

        for( ;itr != end; itr++)
        {
            CtiPointSPtr pPt = itr->second;
            {
                const CtiDynamicPointDispatch *pDyn = getDynamic(pPt->getPointID());

                if(pDyn != NULL)
                {
                    if(pDyn->getNextArchiveTime() < closeTime)
                    {
                        closeTime = pDyn->getNextArchiveTime();
                    }
                }
            }
        }
    }

    return closeTime;
}

void CtiPointClientManager::scanForArchival(const CtiTime &Now, CtiFIFOQueue<CtiTableRawPointHistory> &Que)
{
    {
        coll_type::writer_lock_guard_t guard(getLock());
        spiterator itr = Inherited::begin();
        spiterator end = Inherited::end();

        for( ;itr != end; itr++)
        {
            CtiPointSPtr pPt = itr->second;

            {
                CtiDynamicPointDispatch *pDyn = getDynamic(pPt->getPointID());

                if(pDyn != NULL && !(pDyn->getDispatch().getTags() & MASK_ANY_SERVICE_DISABLE))  // Point is disabled.
                {
                    if(
                      pPt->getArchiveType() == ArchiveTypeOnTimer             ||
                      pPt->getArchiveType() == ArchiveTypeOnTimerAndUpdated   ||
                      pPt->getArchiveType() == ArchiveTypeOnTimerOrUpdated
                      )
                    {
                        if( pDyn->getNextArchiveTime() <= Now )
                        {
                            switch( pPt->getArchiveType() )
                            {
                            case ArchiveTypeOnTimer:
                            case ArchiveTypeOnTimerOrUpdated:
                                {
                                    Que.putQueue(CTIDBG_new CtiTableRawPointHistory(pPt->getID(), pDyn->getQuality(), pDyn->getValue(), Now));
                                    break;
                                }
                            case ArchiveTypeOnTimerAndUpdated:
                                {
                                    pDyn->setArchivePending(TRUE);                   // Mark him so the next one gets archived!
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

    return;
}

void CtiPointClientManager::storeDirtyRecords()
{
    int count = 0;
    list<CtiTablePointDispatch>           updateList;
    list<CtiTablePointDispatch>::iterator updateListIter;

    {
        coll_type::writer_lock_guard_t guard(getLock());
        spiterator itr = Inherited::begin();
        spiterator end = Inherited::end();

        for( ;itr != end; itr++)
        {
            CtiPointSPtr pPt = itr->second;

            try
            {
                CtiDynamicPointDispatch *pDyn = getDynamic(pPt->getPointID());

                if(pDyn != NULL && (pDyn->getDispatch().isDirty() || !pDyn->getDispatch().getUpdatedFlag()) )
                {
                    UINT statictags = pDyn->getDispatch().getTags();
                    pDyn->getDispatch().resetTags();                    // clear them all!
                    pDyn->getDispatch().setTags(pPt->adjustStaticTags(statictags));   // make the static tags match...

                    updateList.push_back(pDyn->getDispatch());
                    pDyn->getDispatch().resetDirty();
                    pDyn->getDispatch().setUpdatedFlag();

                    count++;
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }

    {
        string dyndisp("dyndisp");
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        conn.beginTransaction(dyndisp.c_str());

        for(updateListIter = updateList.begin(); updateListIter != updateList.end(); updateListIter++)
        {
            if(!updateListIter->getUpdatedFlag())
            {
                updateListIter->Insert(conn);
            }
            else
            {
                updateListIter->Update(conn);
            }
        }
        updateList.clear();

        conn.commitTransaction(dyndisp.c_str());
    }

    if(count > 0 && gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Updated " << count << " dynamic dispatch records. " << endl;
    }

    return;
}

CtiPointClientManager::CtiPointClientManager() {}

CtiPointClientManager::~CtiPointClientManager()
{
    DeleteList();
}

void CtiPointClientManager::DeleteList(void)
{
    coll_type::writer_lock_guard_t guard(getLock());

    _conMgrPointMap.clear();
    _pointConnectionMap.clear();
    _reasonabilityLimits.clear();
    _limits.clear();

    Inherited::ClearList();

}

/*
 *  This method reloads all dynamic point data into memory.  It will only update the memory image if
 *  this point has never previously been loaded (updated).
 */
void CtiPointClientManager::RefreshDynamicData(LONG id, const set<long> &pointIds)
{
    coll_type::writer_lock_guard_t guard(getLock());

    LONG lTemp = 0;
    CtiPointSPtr pTempPoint;

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase db = getDatabase();
    RWDBTable   keyTable;
    RWDBSelector selector = db.selector();
    CtiTime start, stop;

    if(DebugLevel & 0x00000001)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " Looking for Dynamic Dispatch Data" << endl;
    }
    CtiTablePointDispatch::getSQL( db, keyTable, selector );

    if(id)
    {
        selector.where( keyTable["pointid"] == id && selector.where() );
    }

    if(!pointIds.empty())
    {
        ostringstream in_list;
        csv_output_iterator<long, ostringstream> csv_itr(in_list);

        in_list << "(";

        copy(pointIds.begin(), pointIds.end(), csv_itr);

        in_list << ")";

        selector.where(selector.where() && keyTable["pointid"].in(RWDBExpr(in_list.str().c_str(), false)));
    }

    RWDBReader rdr = selector.reader(conn);

    if(DebugLevel & 0x00000001 || selector.status().errorCode() != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    start = start.now();
    while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
    {
        rdr["pointid"] >> lTemp;                        // get the point id
        pTempPoint = Inherited::getEqual( lTemp );

        if(pTempPoint)
        {
            CtiDynamicPointDispatch *pDyn = getDynamic(pTempPoint->getPointID());

            if(pDyn != NULL)
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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** WARNING **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Point id " << lTemp << " found in "  << CtiTablePointDispatch::getTableName() << ", no other point info available" << endl;
        }
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Dynamic Data " << endl;
    }

    if(DebugLevel & 0x00000001)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " Done looking for Dynamic Dispatch Data" << endl;
    }
}

//Grab reasonability limits from TBL_UOM
void CtiPointClientManager::refreshReasonabilityLimits(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    coll_type::writer_lock_guard_t guard(getLock());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    CtiTime start, stop;
    string sql;

    if(DebugLevel & 0x00000001)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " Looking for Reasonability limits" << endl;
    }
    sql = "select pointid, highreasonabilitylimit, lowreasonabilitylimit from pointunit where "
          "(highreasonabilitylimit != 1E30 OR lowreasonabilitylimit != -1E30) "
          "AND highreasonabilitylimit != lowreasonabilitylimit";

    if(pntID)
    {
        sql += " AND pointid = " + CtiNumStr(pntID);
        _reasonabilityLimits.erase(pntID);
    }
    else if(paoID)
    {
        //This assumes it is an ADD, an update needs to do an erase here!
        sql += " AND pointid in (select pointid from point where paobjectid = " + CtiNumStr(paoID) + ")";
    }
    else if(!pointIds.empty())
    {
        sql += " AND pointid in (";
        set<long>::const_iterator pointid_itr = pointIds.begin(),
                                  pointid_end = pointIds.end();

        for( ; pointid_itr != pointid_end; ++pointid_itr )
        {
           sql += ", " + CtiNumStr(*pointid_itr);
        }
        sql += ")";
    }
    else
    {
        _reasonabilityLimits.clear();
    }

    start = start.now();
    RWDBReader rdr = ExecuteQuery( conn, sql );

    if(DebugLevel & 0x00000001)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << sql << endl;
    }

    int pointid;
    ReasonabilityLimitStruct limitValues;
    while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
    {
        rdr >> pointid >> limitValues.highLimit >> limitValues.lowLimit;

        _reasonabilityLimits.insert(make_pair(pointid, limitValues));
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Reasonability Limits" << endl;
    }

    if(DebugLevel & 0x00000001)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " Done looking for Reasonability Limits" << endl;
    }
}


void CtiPointClientManager::refreshPointLimits(LONG pntID, LONG paoID, const set<long> &pointIds)
{
    LONG   lTemp = 0;
    string sql;

    coll_type::writer_lock_guard_t guard(getLock());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase   db       = conn.database();
    RWDBReader     rdr;
    CtiTime         start, stop;

    start = start.now();

    if(pntID != 0)
    {
        _limits.erase(CtiTablePointLimit(pntID, 0));
        _limits.erase(CtiTablePointLimit(pntID, 1));
    }
    else if(paoID != 0)
    {
        //  is this right?  If we refresh limits by pao, we clear all limits for all points?
        _limits.clear();
    }
    if(DebugLevel & 0x00010000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Limits" << endl;
    }
    /* Go after the point limits! */
    CtiTablePointLimit::getSQL( sql, pntID, paoID, pointIds );

    rdr = ExecuteQuery( conn, sql );

    if(DebugLevel & 0x00010000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << sql << endl;
    }

    while( rdr() )
    {
        _limits.insert(CtiTablePointLimit(rdr));
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Limits " << endl;
    }
}



//Virtual function, removes all internal references to a single point
void CtiPointClientManager::removePoint(Inherited::ptr_type pTempCtiPoint)
{
    if( pTempCtiPoint )
    {
        for( ConnectionMgrPointMap::iterator iter = _conMgrPointMap.begin(); iter != _conMgrPointMap.end(); iter++ )
        {
            iter->second.erase(pTempCtiPoint->getPointID());
        }
    }

    Inherited::removePoint(pTempCtiPoint);
}

bool CtiPointClientManager::checkEqual(LONG Pt)
{
    Inherited::ptr_type retVal = Inherited::getEqual(Pt);

    return retVal ? true : false;
}

CtiPointManager::ptr_type CtiPointClientManager::getEqual(LONG Pt)
{
    Inherited::ptr_type retVal = Inherited::getEqual(Pt);

    if(!retVal)
    {
        refreshList(Pt);
        retVal = Inherited::getEqual(Pt);
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


bool CtiPointClientManager::hasReasonabilityLimits(LONG pointid)
{
    bool retVal = false;
    coll_type::reader_lock_guard_t guard(getLock());

    if(!_reasonabilityLimits.empty())
    {
        if(_reasonabilityLimits.find(pointid) != _reasonabilityLimits.end())
        {
            retVal = true;
        }
    }

    return retVal;
}

//Returns pair<HighLimit, LowLimit>
//returns pair<0, 0> if the limit is invalid.
CtiPointClientManager::ReasonabilityLimitStruct CtiPointClientManager::getReasonabilityLimits(LONG pointID)
{
    ReasonabilityLimitStruct retVal;
    retVal.highLimit = 0;
    retVal.lowLimit = 0;
    coll_type::reader_lock_guard_t guard(getLock());

    if(!_reasonabilityLimits.empty())
    {
        ReasonabilityLimitMap::iterator iter;
        iter = _reasonabilityLimits.find(pointID);
        if(iter != _reasonabilityLimits.end())
        {
            retVal = iter->second;
        }
    }

    return retVal;
}

CtiTablePointLimit CtiPointClientManager::getPointLimit(LONG pointID, LONG limitNum)
{
    coll_type::reader_lock_guard_t guard(getLock());

    CtiTablePointLimit retVal(pointID, limitNum);

    PointLimitSet::iterator iter = _limits.find(retVal);
    if(iter != _limits.end())
    {
        retVal = *iter;
    }

    return retVal;
}

CtiTablePointAlarming CtiPointClientManager::getAlarming(LONG pointID)
{
    coll_type::reader_lock_guard_t guard(getLock());

    CtiTablePointAlarming retVal(pointID);

    PointAlarmingSet::iterator iter = _alarming.find(retVal);
    if(iter != _alarming.end())
    {
        retVal = *iter;
    }

    return retVal;
}

bool CtiPointClientManager::setDynamic(long pointID, CtiDynamicPointDispatch *point)
{
    coll_type::writer_lock_guard_t guard(getLock());

    return (_dynamic.insert(make_pair(pointID, point))).second;
}

CtiDynamicPointDispatch *CtiPointClientManager::getDynamic(LONG pointID)
{
    coll_type::reader_lock_guard_t guard(getLock());

    CtiDynamicPointDispatch *retval = 0;

    DynamicPointDispatchMap::iterator iter = _dynamic.find(pointID);
    if(iter != _dynamic.end())
    {
        retval = iter->second;
    }

    return retval;
}

int CtiPointClientManager::getProperty(LONG pointID, unsigned int propertyID)
{
    coll_type::reader_lock_guard_t guard(getLock());

    pair<PointPropertyMap::const_iterator,
         PointPropertyMap::const_iterator> range = _properties.equal_range(pointID);

    for( ; range.first != range.second; ++range.first )
    {
        const CtiTablePointProperty *property_ptr = range.first->second;
        if( property_ptr && property_ptr->getPropertyID() == propertyID )
        {
            return property_ptr->getIntProperty();
        }
    }

    return numeric_limits<int>::min();
}

bool CtiPointClientManager::hasProperty(LONG pointID, unsigned int propertyID)
{
    coll_type::reader_lock_guard_t guard(getLock());

    pair<PointPropertyMap::const_iterator,
         PointPropertyMap::const_iterator> range = _properties.equal_range(pointID);

    for( ; range.first != range.second; ++range.first )
    {
        const CtiTablePointProperty *property_ptr = range.first->second;
        if( property_ptr && property_ptr->getPropertyID() == propertyID )
        {
            return true;
        }
    }

    return false;
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
