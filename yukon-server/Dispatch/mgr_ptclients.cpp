#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   mgr_ptclients
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/mgr_ptclients.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/07/13 16:09:04 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


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

#include "con_mgr_vg.h"
#include "pointdefs.h"
#include "resolvers.h"

static void verifyDynamicData(CtiPoint *&pTempPoint);

bool findNonUpdatedDynamicData(CtiPoint *pTempPoint, void* d)
{
    bool bRet = false;

    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pTempPoint->getDynamic();

    if(pDyn != NULL)
    {
        if(pDyn->getDispatch().getUpdatedFlag() == FALSE) bRet = true;
    }

    return bRet;
}

bool findDirtyDynamicData(CtiPoint *pTempPoint, void* d)
{
    bool bRet = false;

    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pTempPoint->getDynamic();

    if(pDyn != NULL)
    {
        if(pDyn->getDispatch().isDirty()) bRet = true;
    }

    return bRet;
}

/*
 *  This method attempts an insert on all non-valid tbl_ptdispatch objects to make sure they are in there..
 */
void ApplyInsertNonUpdatedDynamicData(const CtiHashKey *key, CtiPoint *&pTempPoint, void* d)
{
    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pTempPoint->getDynamic();

    if(pDyn != NULL && !pDyn->getDispatch().getUpdatedFlag())
    {
        if( pDyn->getDispatch().Insert().errorCode() != RWDBStatus::ok )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Unable to insert dynamicpointdata for " << pTempPoint->getName() << endl;
            }
        }
        pDyn->getDispatch().setUpdatedFlag();
    }
    return;
}

void verifyInitialDynamicData(CtiPoint *&pTempPoint)
{
    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pTempPoint->getDynamic();

    if(pDyn == NULL)
    {
        if(NULL != (pDyn = CTIDBG_new CtiDynamicPointDispatch(pTempPoint->getID())))
        {
            UINT statictags = pDyn->getDispatch().getTags();
            pTempPoint->adjustStaticTags(statictags);

            pDyn->getDispatch().setValue(pTempPoint->getDefaultValue());
            pDyn->getDispatch().setTags(statictags);
            pDyn->getDispatch().setDirty( TRUE );                           // Make it update if it doesn't get reloaded!

            if(pDyn->getAttachment() == NULL)
            {
                pDyn->setAttachment((VOID*)(CTIDBG_new CtiPointConnection));
            }

            pTempPoint->setDynamic(pDyn);
        }
    }
    else
    {
        UINT statictags = pDyn->getDispatch().getTags();
        pTempPoint->adjustStaticTags(statictags);

        if(statictags != pDyn->getDispatch().getTags())
        {
            pDyn->getDispatch().setTags(statictags);
        }
    }
}

/*
 *  This method initializes each point's dynamic data to it's default/initial values.
 */
void ApplyInitialDynamicConditions(const CtiHashKey *key, CtiPoint *&pTempPoint, void* d)
{
    LONG id = 0;
    verifyInitialDynamicData(pTempPoint);
    return;
}


void CtiPointClientManager::refreshList(BOOL (*testFunc)(CtiPoint*,void*), void *arg, LONG pntID, LONG paoID)
{
    CtiPoint *pTempPoint = NULL;

    LockGuard  guard(monitor());

    Inherited::refreshList(testFunc, arg, pntID, paoID);                // Load all points in the system
    Inherited::getMap().apply(ApplyInitialDynamicConditions, NULL);     // Make sure everyone has been initialized with Dynamic data.
    if((pTempPoint = Inherited::find(findNonUpdatedDynamicData, NULL)) != NULL) // If there is at least one nonupdated dynamic entry.
    {
        RefreshDynamicData();
        Inherited::getMap().apply(ApplyInsertNonUpdatedDynamicData, NULL);
    }
    if((pTempPoint = Inherited::find(findDirtyDynamicData, NULL)) != NULL)      // If there is at least one dynamic entry which needs writing to the database.
    {
        storeDirtyRecords();
    }
}



void CtiPointClientManager::DumpList(void)
{
    CtiPoint *p = NULL;
    try
    {
        LockGuard  guard(monitor());

        CtiRTDBIterator itr(Map);

        for(;itr();)
        {
            p = itr.value();

            {
                CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)p->getDynamic();

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
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        cout << "Attempting to clear device list..." << endl;

        Map.clearAndDestroy();

        cout << "DumpMemoryPoints:  " << e.why() << endl;
        RWTHROW(e);

    }
}

int CtiPointClientManager::InsertConnectionManager(CtiConnectionManager* CM, const CtiPointRegistrationMsg &aReg, bool debugprint)
{
    int nRet = 0;
    RWTime   NowTime;
    int ptcnt = aReg.getCount();

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
    }

    for(int i = 0 ; i < ptcnt; i++)
    {
        /*
         *  OK, now I walk the list of points looking at each one's ID to find who to add this guy to
         */

        {
            CtiPoint* temp = Map.findValue(&CtiHashKey(aReg[i]));
            if(temp != 0)
            {
                if(!((CtiVanGoghConnectionManager*)CM)->isRegForChangeType(temp->getType())) // Make sure we didn't already register for ALL points of this type.
                {
                    if(debugprint)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << NowTime << " " << CM->getClientName() << " has registered for point " << aReg[i] << endl;
                        }
                    }

                    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)temp->getDynamic();

                    if(pDyn != NULL)
                    {
                        if(pDyn->getAttachment() != NULL)
                        {
                            ((CtiPointConnection*)(pDyn->getAttachment()))->AddConnectionManager(CM);
                        }
                    }
                }
            }
        }
    }

    return nRet;
}

int CtiPointClientManager::RemoveConnectionManager(CtiConnectionManager* CM)
{
    int nRet = 0;

    // OK, now I walk the list of points looking at each one's list to remove the CM
    {
        LockGuard  guard(monitor());
        CtiRTDBIterator itr(Map);

        for(; ++itr ;)
        {
            CtiPoint* temp = itr.value();

            {
                CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)temp->getDynamic();

                if(pDyn != NULL)
                {
                    if(pDyn->getAttachment() != NULL)
                    {
                        ((CtiPointConnection*)(pDyn->getAttachment()))->RemoveConnectionManager(CM);
                    }
                }
            }
        }
    }

    return nRet;
}

RWTime CtiPointClientManager::findNextNearestArchivalTime()
{
    RWTime   closeTime(YUKONEOT);

    {
        LockGuard  guard(monitor());
        CtiRTDBIterator itr(Map);

        for(; ++itr ;)
        {
            CtiPoint* pPt = itr.value();
            {
                CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPt->getDynamic();

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

void CtiPointClientManager::scanForArchival(const RWTime &Now, CtiQueue<CtiTableRawPointHistory, less<CtiTableRawPointHistory> > &Que)
{
    {
        LockGuard  guard(monitor());
        CtiRTDBIterator itr(Map);

        for(; ++itr ;)
        {
            CtiPoint* pPt = itr.value();

            {
                CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPt->getDynamic();

                if(pDyn != NULL)
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
                        else if( pPt->getArchiveInterval() >= 0 &&                                      // pPt->getArchiveInterval() != ULONG_MAX &&
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

    {
        LockGuard  guard(monitor());
        CtiRTDBIterator itr(Map);

        {
            RWCString dyndisp("dyndisp");
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            conn.beginTransaction(dyndisp);

            for(; ++itr ;)
            {
                CtiPoint* pPt = itr.value();

                try
                {
                    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPt->getDynamic();

                    if(pDyn != NULL && pDyn->getDispatch().isDirty())
                    {
                        UINT statictags = pDyn->getDispatch().getTags();
                        pDyn->getDispatch().resetTags();                    // clear them all!
                        pDyn->getDispatch().setTags(pPt->adjustStaticTags(statictags));   // make the static tags match...

                        count++;
                        RWDBStatus dbstat = pDyn->getDispatch().Update(conn);

                        if(dbstat.isValid())                        // We are in there.
                        {
                            pDyn->getDispatch().setUpdatedFlag(TRUE);       // Memory image is the boss now!
                        }
                    }
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }

            conn.commitTransaction(dyndisp);
        }
    }

    if(count > 0 && gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Updated " << count << " dynamic dispatch records. " << endl;
    }

    return;
}

//typedef CtiPointManager Inherited;
//typedef RWTPtrSlist<CtiConnectionManager> ConnectionList;

CtiPointClientManager::CtiPointClientManager() {}

CtiPointClientManager::~CtiPointClientManager()
{
    DeleteList();
}

void CtiPointClientManager::DeleteList(void)
{
    LockGuard  guard(monitor());

    CtiRTDBIterator itr(Map);
    for(; ++itr ;)
    {
        CtiPoint* temp = itr.value();

        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)temp->getDynamic();

        if(pDyn != NULL &&  pDyn->getAttachment() != NULL)
        {
            delete ((CtiPointConnection*)(pDyn->getAttachment()));
        }
    }

    if(Map.entries())
    {
        Map.clearAndDestroy();
    }
}

/*
 *  This method reloads all dynamic point data into memory.  It will only update the memory image if
 *  this point has never previously been loaded (updated).
 */
void CtiPointClientManager::RefreshDynamicData(LONG id)
{
    LockGuard  guard(monitor());

    LONG lTemp = 0;
    CtiPoint* pTempPoint = NULL;

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase db = getDatabase();
    RWDBTable   keyTable;
    RWDBSelector selector = db.selector();

    if(DebugLevel & 0x00000001)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << RWTime() << " Looking for Dynamic Dispatch Data" << endl;
    }
    CtiTablePointDispatch::getSQL( db, keyTable, selector );

    if(id)
    {
        selector.where( keyTable["pointid"] == id && selector.where() );
    }

    RWDBReader rdr = selector.reader(conn);

    if(DebugLevel & 0x00000001 || selector.status().errorCode() != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
    {
        rdr["pointid"] >> lTemp;                        // get the point id
        pTempPoint = Inherited::getEqual( lTemp );

        if(pTempPoint)
        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pTempPoint->getDynamic();

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
            dout << RWTime() << " **** WARNING **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Point id " << lTemp << " found in "  << CtiTablePointDispatch::getTableName() << ", no other point info available" << endl;
        }
    }

    if(DebugLevel & 0x00000001)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << RWTime() << " Done looking for Dynamic Dispatch Data" << endl;
    }
}



