/*-----------------------------------------------------------------------------*
*
* File:   mgr_point
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_point.cpp-arc  $
* REVISION     :  $Revision: 1.23 $
* DATE         :  $Date: 2005/02/17 23:11:19 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/db/db.h>

#include "ctidbgmem.h"  // CTIDBG_new
#include "pt_base.h"
#include "mgr_point.h"
#include "dbaccess.h"
#include "devicetypes.h"

#include "logger.h"

#include "pt_accum.h"
#include "pt_analog.h"
#include "pt_status.h"

#include "resolvers.h"
#include "tbl_pt_alarm.h"
#include "utility.h"


bool findHasAlarming(CtiPoint *pTP, void* d)
{
    return pTP->hasAlarming();
}


BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
    case DLL_PROCESS_ATTACH:
        {
            identifyProject(CompileInfo);
            break;
        }
    case DLL_THREAD_ATTACH:
        {
            break;
        }
    case DLL_THREAD_DETACH:
        {
            break;
        }
    case DLL_PROCESS_DETACH:
        {
            break;
        }
    }
    return TRUE;
}


static RWDBReader& operator >> (RWDBReader& rdr, CtiPointBase& p);
CtiPointBase* PointFactory(RWDBReader &rdr);


inline RWBoolean
isPointIdStaticId(CtiPoint *pPoint, void* d)
{
    CtiPointBase *pSp = (CtiPointBase *)d;

    return(pPoint->getID() == pSp->getID());
}

inline RWBoolean
isPointNotUpdated(CtiPoint *pPoint, void* d)
{
    // Return TRUE if it is NOT SET
    return(RWBoolean(!pPoint->getUpdatedFlag()));
}

void
ApplyPointResetUpdated(const CtiHashKey *key, CtiPoint *&pPoint, void* d)
{
    pPoint->resetUpdatedFlag();
    pPoint->setValid();
    return;
}

void
ApplyInvalidateNotUpdated(const CtiHashKey *key, CtiPoint *&pPt, void* d)
{
    if(!pPt->getUpdatedFlag())
    {
        pPt->resetValid();   //   NOT NOT NOT Valid
    }
    return;
}

static void
ApplyInvalidateLimtsForPAO(const CtiHashKey *key, CtiPoint *&pPt, void* d)
{
    LONG paoID = (LONG)d;

    if(pPt->getDeviceID() == paoID && pPt->isNumeric())
    {
        ((CtiPointNumeric*)pPt)->invalidateLimits();
    }

    return;
}

void CtiPointManager::refreshList(BOOL (*testFunc)(CtiPointBase*,void*), void *arg, LONG pntID, LONG paoID)
{
    CtiPoint *pTempCtiPoint = NULL;
    bool     rowFound = false;

    RWTime start, stop;

    try
    {
        LockGuard  guard(monitor());

        {   // Make sure all objects that that store results
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            // are out of scope when the release is called

            start = start.now();
            if(pntID == 0 && paoID == 0)
            {
                // Reset everyone's Updated flag.
                Map.apply(ApplyPointResetUpdated, NULL);

            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds for ApplyPointResetUpdated" << endl;
            }

            RWDBDatabase   db       = conn.database();
            RWDBSelector   selector = conn.database().selector();
            RWDBTable      keyTable;
            RWDBReader     rdr;

            start = start.now();
            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for System Points" << endl;
            }
            /* Go after the system defined points! */
            CtiPointBase().getSQL( db, keyTable, selector );
            // Make sure I pick up only those devices which are System devices.
            selector.where( rwdbUpper(keyTable["pointtype"]) == RWDBExpr("SYSTEM") && selector.where());
            if(pntID != 0) selector.where( keyTable["pointid"] == RWDBExpr( pntID ) && selector.where() );
            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

            rdr = selector.reader(conn);
            if(DebugLevel & 0x00010000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
            }
            refreshPoints(rowFound, rdr, testFunc, arg);
            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done Looking for System Points" << endl;
            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds for System Devices " << endl;
            }
            start = start.now();
            selector = conn.database().selector();    // Clear the selector.

            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Status/Control" << endl;
            }
            /* Go after the status points! */
            CtiPointStatus().getSQL( db, keyTable, selector );
            if(pntID != 0) selector.where( keyTable["pointid"] == RWDBExpr( pntID ) && selector.where() );
            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

            rdr = selector.reader(conn);
            if(DebugLevel & 0x00010000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
            }
            refreshPoints(rowFound, rdr, testFunc, arg);
            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done Looking for Status/Control" << endl;
            }

            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds for Status/Control " << endl;
            }
            start = start.now();
            selector = conn.database().selector();    // Clear the selector.

            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Analogs" << endl;
            }
            /* Go after the analog points! */
            CtiPointAnalog().getSQL( db, keyTable, selector );
            if(pntID != 0) selector.where( keyTable["pointid"] == RWDBExpr( pntID ) && selector.where() );
            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

            rdr = selector.reader(conn);
            if(DebugLevel & 0x00010000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
            }
            refreshPoints(rowFound, rdr, testFunc, arg);
            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "DONE Looking for Analogs" << endl;
            }

            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds for Analogs " << endl;
            }
            start = start.now();
            selector = conn.database().selector();    // Clear the selector.

            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Accum" << endl;
            }
            /* Go after the accumulator points! */
            CtiPointAccumulator().getSQL( db, keyTable, selector );
            if(pntID != 0) selector.where( keyTable["pointid"] == RWDBExpr( pntID ) && selector.where() );
            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

            rdr = selector.reader(conn);
            if(DebugLevel & 0x00010000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
            }
            refreshPoints(rowFound, rdr, testFunc, arg);
            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "DONE Looking for Accum" << endl;
            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds for Accumulators " << endl;
            }
            start = start.now();
            selector = conn.database().selector();    // Clear the selector.

            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for CALC" << endl;
            }
            /* Go after the calc points! */
            CtiPointNumeric().getSQL( db, keyTable, selector );

            selector.where( ( rwdbUpper(keyTable["pointtype"]) == RWDBExpr("CALCULATED") ||
                              rwdbUpper(keyTable["pointtype"]) == RWDBExpr("CALCANALOG")) && selector.where());
            if(pntID != 0) selector.where( keyTable["pointid"] == RWDBExpr( pntID ) && selector.where() );
            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

            rdr = selector.reader(conn);
            if(DebugLevel & 0x00010000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
            }
            refreshPoints(rowFound, rdr, testFunc, arg);
            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "DONE Looking for CALC" << endl;
            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds for Calc points " << endl;
            }

            // Now I need to check for any Point removals based upon the
            // Updated Flag being NOT set

            if(pntID == 0 && paoID == 0 && rowFound)
            {
                Map.apply(ApplyInvalidateNotUpdated, NULL);

                do
                {
                    pTempCtiPoint = remove(isPointNotUpdated, NULL);
                    if(pTempCtiPoint != NULL)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "  Evicting " << pTempCtiPoint->getName() << " from list" << endl;
                        }
                        delete pTempCtiPoint;
                    }

                } while(pTempCtiPoint != NULL);
            }
        }   // Temporary results are destroyed to free the connection

        // Now load the properties onto the points we have collected.
        refreshPointProperties(pntID,paoID);

    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Attempting to clear point list..." << endl;}
        Map.clearAndDestroy();

        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "getPoints:  " << e.why() << endl;}
        RWTHROW(e);

    }
}

void CtiPointManager::DumpList(void)
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
                p->DumpData();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << endl;
                }
            }
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Attempting to clear device list..." << endl;}

        Map.clearAndDestroy();

        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "DumpPoints:  " << e.why() << endl;}
        RWTHROW(e);

    }
}


CtiPointBase* PointFactory(RWDBReader &rdr)
{
    INT       PtType;
    INT       PseudoPt = FALSE;
    RWCString rwsType;
    RWCString rwsPseudo;

    CtiPointBase *Point = NULL;

    rdr["pointtype"]  >> rwsType;
    rdr["pseudoflag"] >> rwsPseudo;

    if(getDebugLevel() & 0x00000400)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Creating a Point of type " << rwsType << endl;
    }
    PtType = resolvePointType(rwsType);

    if(getDebugLevel() & 0x00000400)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "  Is point a pseudo point? " << rwsPseudo << endl;
    }
    rwsPseudo.toLower();
    PseudoPt = (rwsPseudo == "y" ? TRUE : FALSE );

    switch(PtType)
    {
    case StatusPointType:
        {
            Point = (CtiPointBase*) CTIDBG_new CtiPointStatus;
            break;
        }
    case AnalogPointType:
        {
            if(PseudoPt)
                Point = (CtiPointBase*) CTIDBG_new CtiPointPseudoAnalog;     // Really a numeric!
            else
                Point = (CtiPointBase*) CTIDBG_new CtiPointAnalog;

            break;
        }
    case PulseAccumulatorPointType:
    case DemandAccumulatorPointType:
        {
            Point = (CtiPointBase*) CTIDBG_new CtiPointAccumulator;
            break;
        }
    case CalculatedPointType:
        {
            Point = (CtiPointBase*) CTIDBG_new CtiPointCalculated;          // This too is really a numeric!
            break;
        }
    case CalculatedStatusPointType:
        {
            Point = (CtiPointBase*) CTIDBG_new CtiPointCalculatedStatus;
            break;
        }
    case SystemPointType:
        {
            Point = CTIDBG_new CtiPointBase;
            break;
        }
    default:
        {
            { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Unknown point type!" << endl;}
            break;
        }
    }

    return Point;
}

void CtiPointManager::refreshPoints(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg)
{
    LONG        lTemp = 0;
    CtiPoint*   pTempCtiPoint = NULL;

    LockGuard  guard(monitor());

    while( rdr() )
    {
        rowFound = true;

        rdr["pointid"] >> lTemp;            // get the PointID
        CtiHashKey key(lTemp);

        if( Map.entries() > 0 && ((pTempCtiPoint = Map.findValue(&key)) != NULL) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the CTIDBG_new settings!
             */
            pTempCtiPoint->DecodeDatabaseReader(rdr);        // Fills himself in from the reader
            pTempCtiPoint->setUpdatedFlag();       // Mark it updated
        }
        else
        {
            pTempCtiPoint = PointFactory(rdr);               // Use the reader to get me an object of the proper type
            pTempCtiPoint->DecodeDatabaseReader(rdr);        // Fills himself in from the reader

            if(((*testFunc)(pTempCtiPoint, arg)))            // If I care about this point in the db in question....
            {
                // Add it to my list....
                pTempCtiPoint->setUpdatedFlag();               // Mark it updated
                Map.insert( CTIDBG_new CtiHashKey(pTempCtiPoint->getID()), pTempCtiPoint ); // Stuff it in the list
            }
            else
            {
                delete pTempCtiPoint;                         // I don't want it!
            }
        }
    }
}

CtiPoint* CtiPointManager::getOffsetTypeEqual(LONG pao, INT Offset, INT Type)
{
    CtiPoint *p    = NULL;
    CtiPoint *pRet = NULL;

    try
    {
        LockGuard  guard(monitor());
        CtiRTDBIterator itr(Map);

        for(;itr() && !pRet;)
        {
            p = itr.value();

            if(pao == p->getDeviceID() && p->getType() == Type)
            {
                switch(Type)
                {
                case StatusPointType:
                    {
                        if(!(p->isPseudoPoint()) && ((CtiPointStatus*)p)->getPointOffset() == Offset)
                        {
                            if(p->getUpdatedFlag())
                            {
                                pRet = p;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " << p->getName() << " point is non-updated" << endl;
                            }
                        }
                        break;
                    }
                case AnalogPointType:
                    {
                        if(((CtiPointAnalog*)p)->getPointOffset() == Offset)
                        {
                            if(p->getUpdatedFlag())
                            {
                                pRet = p;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " << p->getName() << " point is non-updated" << endl;
                            }
                        }
                        break;
                    }
                case PulseAccumulatorPointType:
                case DemandAccumulatorPointType:
                    {
                        if(((CtiPointAccumulator*)p)->getPointOffset() == Offset)
                        {
                            if(p->getUpdatedFlag())
                            {
                                pRet = p;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " << p->getName() << " point is non-updated" << endl;
                            }
                        }
                        break;
                    }
                case SystemPointType:
                    {
                        if(p->getPointOffset() == Offset)
                        {
                            if(p->getUpdatedFlag())
                            {
                                pRet = p;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " << p->getName() << " point is non-updated" << endl;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Attempting to clear point list..." << __FILE__ << " " << __LINE__ << endl;}

        Map.clearAndDestroy();

        RWTHROW(e);
    }

    return pRet;
}

CtiPoint* CtiPointManager::getEqual (LONG Pt)
{
    LockGuard  guard(monitor());
    CtiHashKey key(Pt);
    return Map.findValue(&key);
}

bool CtiPointManager::isIdValid(LONG Pt)
{
    LockGuard  guard(monitor());
    CtiHashKey key(Pt);
    return Map.contains(&key);
}

CtiPoint* CtiPointManager::getEqualByName(LONG pao, RWCString pname)
{
    CtiPoint *p    = NULL;
    CtiPoint *pRet = NULL;

    try
    {
        LockGuard  guard(monitor());
        CtiRTDBIterator itr(Map);

        for(;itr() && !pRet;)
        {
            p = itr.value();

            if(pao == p->getDeviceID())
            {
                RWCString ptname = p->getName();
                ptname.toLower();
                pname.toLower();

                if(ptname == pname)
                {
                    if(p->getUpdatedFlag())
                    {
                        pRet = p;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << p->getName() << " point is non-updated" << endl;
                    }
                }
            }
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Attempting to clear point list..." << __FILE__ << " " << __LINE__ << endl;}

        Map.clearAndDestroy();

        RWTHROW(e);
    }

    return pRet;
}


CtiPointManager::CtiPointManager() {}

extern void cleanupDB();

CtiPointManager::~CtiPointManager()
{
    cleanupDB();  // Deallocate all the DB stuff.
}


void CtiPointManager::DeleteList(void)
{
    LockGuard guard(monitor());
    Map.clearAndDestroy();
}

CtiPoint* CtiPointManager::getControlOffsetEqual(LONG pao, INT Offset)
{
    CtiPoint *p    = NULL;
    CtiPoint *pRet = NULL;

    try
    {
        LockGuard  guard(monitor());
        CtiRTDBIterator itr(Map);

        for(;itr() && !pRet;)
        {
            p = itr.value();

            if(pao == p->getDeviceID() && p->getType() == StatusPointType)
            {
                CtiPointStatus *pCont = (CtiPointStatus*)p;

                if(pCont->getPointStatus().getControlOffset() == Offset)
                {
                    if(p->getUpdatedFlag())
                    {
                        pRet = p;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << p->getName() << " point is non-updated" << endl;
                    }
                }
            }
        }
    }
    catch(RWExternalErr e)
    {
        //Make sure the list is cleared
        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Attempting to clear point list..." << __FILE__ << " " << __LINE__ << endl;}

        Map.clearAndDestroy();

        RWTHROW(e);
    }

    return pRet;
}

void CtiPointManager::refreshAlarming(LONG pntID, LONG paoID)
{
    LockGuard  guard(monitor());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBDatabase   db       = conn.database();
    RWDBSelector   selector = conn.database().selector();
    RWDBTable      keyTable;
    RWDBReader     rdr;


    CtiTablePointAlarming::getSQL( db, keyTable, selector );

    if(pntID) selector.where( keyTable["pointid"] == pntID && selector.where() );
    // There is no pao in this table!// if(paoID) selector.where( keyTable["paobjectid"] == paoID && selector.where() );

    rdr = selector.reader( conn );

    if(setErrorCode(rdr.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << selector.asString() << endl;
    }

    LONG pID;
    CtiPointBase *pPt;

    while( rdr() )
    {
        rdr["pointid"] >> pID;

        // Find it in our list and decode it.
        pPt = getMap().findValue(&CtiHashKey(pID));
        if(pPt != 0 && pPt->hasAlarming())
        {
            pPt->DecodeAlarmingDatabaseReader(rdr);
        }
    }
}



void CtiPointManager::refreshPointLimits(LONG pntID, LONG paoID)
{
    LONG        lTemp = 0;
    CtiPoint*   pTempCtiPoint = NULL;
    CtiHashKey  key(-1);;

    LockGuard  guard(monitor());


    // Make sure any limits this point previously had are marked as removed!
    if(pntID != 0 && Map.entries() > 0)
    {
        key = CtiHashKey(pntID);
        if(((pTempCtiPoint = Map.findValue(&key)) != NULL) && pTempCtiPoint->isNumeric())
        {
            ((CtiPointNumeric*)pTempCtiPoint)->invalidateLimits();
        }
    }
    else if(pntID == 0 && paoID != 0)       // This is much more work...
    {
        Map.apply(ApplyInvalidateLimtsForPAO, (void*)paoID);
    }

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase   db       = conn.database();
    RWDBSelector   selector = conn.database().selector();
    RWDBTable      keyTable;
    RWDBReader     rdr;
    RWTime         start, stop;

    start = start.now();
    selector = conn.database().selector();    // Clear the selector.

    if(DebugLevel & 0x00010000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Limits" << endl;
    }
    /* Go after the point limits! */
    CtiTablePointLimit().getSQL( db, keyTable, selector );
    if(pntID != 0) selector.where( keyTable["pointid"] == RWDBExpr( pntID ) && selector.where() );
    if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

    rdr = selector.reader(conn);
    if(DebugLevel & 0x00010000 || setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    while( rdr() )
    {
        rdr["pointid"] >> lTemp;            // get the PointID
        key = CtiHashKey(lTemp);

        if( Map.entries() > 0 && ((pTempCtiPoint = Map.findValue(&key)) != NULL) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the new limit settings!
             */
            ((CtiPointNumeric*)pTempCtiPoint)->DecodeLimitsDatabaseReader(rdr);        // Fills himself in from the reader
        }
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds for Limits " << endl;
    }
}

/*
 *
 */
void CtiPointManager::refreshPointProperties(LONG pntID, LONG paoID)
{
    refreshPointLimits(pntID, paoID);

    if(find(findHasAlarming, NULL) != NULL) // If there is at least one point with alarming data available.
    {
        refreshAlarming(pntID, paoID);
    }
}
