/*-----------------------------------------------------------------------------*
*
* File:   mgr_point
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_point.cpp-arc  $
* REVISION     :  $Revision: 1.36 $
* DATE         :  $Date: 2007/10/18 21:12:18 $
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

#include "rwutil.h"

#define PERF_TO_MS(b,a,p) (UINT)(((b).QuadPart - (a).QuadPart) / ((p).QuadPart / 1000L))

bool findHasAlarming(CtiPointSPtr &pTP, void* d)
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

/*
inline RWBoolean
isPointIdStaticId(CtiPoint *pPoint, void* d)
{
    CtiPointSPtr pSp = (CtiPointSPtr )d;

    return(pPoint->getID() == pSp->getID());
}*/

inline RWBoolean
isPointNotUpdated(CtiPointSPtr &pPoint, void* d)
{
    // Return TRUE if it is NOT SET
    return(RWBoolean(!pPoint->getUpdatedFlag()));
}

void
ApplyPointResetUpdated(const long key, CtiPointSPtr pPoint, void* d)
{
    pPoint->resetUpdatedFlag();
    pPoint->setValid();
    return;
}

void
ApplyInvalidateNotUpdated(const long key, CtiPointSPtr pPoint, void* d)
{
    if(!pPoint->getUpdatedFlag())
    {
        pPoint->resetValid();   //   NOT NOT NOT Valid
    }
    return;
}

static void
ApplyInvalidateLimtsForPAO(const long key, CtiPointSPtr pPoint, void* d)
{
    LONG paoID = (LONG)d;

    if(pPoint->getDeviceID() == paoID && pPoint->isNumeric())
    {
        ((CtiPointNumeric*)pPoint.get())->invalidateLimits();
    }

    return;
}

void CtiPointManager::refreshList(BOOL (*testFunc)(CtiPointBase*,void*), void *arg, LONG pntID, LONG paoID)
{
    ptr_type pTempCtiPoint;
    bool     rowFound = false;

    CtiTime start, stop;

    try
    {

        {   // Make sure all objects that that store results
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            // are out of scope when the release is called

            start = start.now();
            if(pntID == 0 && paoID == 0)
            {
                // Reset everyone's Updated flag.
                _smartMap.apply(ApplyPointResetUpdated, NULL);

            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for ApplyPointResetUpdated" << endl;
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
            if(DebugLevel & 0x00010000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
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
                dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for System Devices " << endl;
            }
            start = start.now();
            selector = conn.database().selector();    // Clear the selector.

            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Status/Control" << endl;
            }
            /* Go after the status points! */
            CtiPointStatus().getSQL( db, keyTable, selector );

            selector.where( ( rwdbUpper(keyTable["pointtype"]) == RWDBExpr("STATUS") ||
                              rwdbUpper(keyTable["pointtype"]) == RWDBExpr("CALCSTATUS")) && selector.where());

            if(pntID != 0) selector.where( keyTable["pointid"] == RWDBExpr( pntID ) && selector.where() );
            if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

            rdr = selector.reader(conn);
            if(DebugLevel & 0x00010000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
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
                dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Status/Control " << endl;
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
            if(DebugLevel & 0x00010000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
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
                dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Analogs " << endl;
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
            if(DebugLevel & 0x00010000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
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
                dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Accumulators " << endl;
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
            if(DebugLevel & 0x00010000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
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
                dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Calc points " << endl;
            }           

            // Now I need to check for any Point removals based upon the
            // Updated Flag being NOT set

            if(pntID == 0 && paoID == 0 && rowFound)
            {
                _smartMap.apply(ApplyInvalidateNotUpdated, NULL);

                while( pTempCtiPoint = _smartMap.remove(isPointNotUpdated, NULL) )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  Evicting " << pTempCtiPoint->getName() << " from list" << endl;
                    }

                    //  Remove the point from the type/offset lookup map
                    std::multimap<pao_offset_t, long>::iterator type_itr;
                    for( type_itr  = _type_offsets.lower_bound(pao_offset_t(pTempCtiPoint->getDeviceID(), pTempCtiPoint->getPointOffset()));
                         type_itr != _type_offsets.upper_bound(pao_offset_t(pTempCtiPoint->getDeviceID(), pTempCtiPoint->getPointOffset()));
                         type_itr++ )
                    {
                         if( pTempCtiPoint->getType() == type_itr->second )
                         {
                             _type_offsets.erase(type_itr);

                             break;
                         }
                    }

                    //  If it's a status point with control, remove it from the control point lookup as well
                    if( pTempCtiPoint->getType() == StatusPointType &&
                        pTempCtiPoint->getControlOffset() )
                    {
                        _control_offsets.erase(pao_offset_t(pTempCtiPoint->getDeviceID(), pTempCtiPoint->getControlOffset()));
                    }
                }
            }
        }   // Temporary results are destroyed to free the connection

        // Now load the properties onto the points we have collected.
        refreshPointProperties(pntID,paoID);

    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Attempting to clear point list..." << endl;}
        _smartMap.removeAll(NULL, 0);
        _type_offsets.erase(_type_offsets.begin(), _type_offsets.end());
        _control_offsets.erase(_control_offsets.begin(), _control_offsets.end());

        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "getPoints:  " << e.why() << endl;}
        RWTHROW(e);

    }
}

void CtiPointManager::DumpList(void)
{
    CtiPointSPtr p;
    try
    {
        LockGuard  guard(getMux());

        spiterator itr;

        for( itr = begin(); itr != end(); itr++)
        {
            p = itr->second;

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

        _smartMap.removeAll(NULL, 0);
        _type_offsets.erase(_type_offsets.begin(), _type_offsets.end());
        _control_offsets.erase(_control_offsets.begin(), _control_offsets.end());

        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "DumpPoints:  " << e.why() << endl;}
        RWTHROW(e);

    }
}


CtiPointBase* PointFactory(RWDBReader &rdr)
{
    static const RWCString pointtype = "pointtype";
    static const RWCString pseudoflag = "pseudoflag";

    INT       PtType;
    INT       PseudoPt = FALSE;
    string rwsType;
    string rwsPseudo;

    CtiPointBase *Point = NULL;

    rdr[pointtype]  >> rwsType;
    rdr[pseudoflag] >> rwsPseudo;

    if(getDebugLevel() & DEBUGLEVEL_FACTORY)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Creating a Point of type " << rwsType << endl;
    }
    PtType = resolvePointType(rwsType);

    if(getDebugLevel() & DEBUGLEVEL_FACTORY)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "  Is point a pseudo point? " << rwsPseudo << endl;
    }
    std::transform(rwsPseudo.begin(), rwsPseudo.end(), rwsPseudo.begin(), tolower);

    PseudoPt = (rwsPseudo == "y" ? TRUE : FALSE );

    switch(PtType)
    {
    case StatusPointType:
        {
            Point = (CtiPointBase*) CTIDBG_new CtiPointStatus;
            break;
        }
    case AnalogOutputPointType:
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

    // Identify the point so it knows what it is...  should really be part of the constructor
    if(Point) Point->setType((CtiPointType_t)PtType);

    return Point;
}

void CtiPointManager::refreshPoints(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg)
{
    LONG        lTemp = 0;
    ptr_type Point;

    LockGuard  guard(getMux());

    while( rdr() )
    {
        rowFound = true;

        rdr["pointid"] >> lTemp;            // get the PointID

        if( _smartMap.entries() > 0 && (Point = _smartMap.find(lTemp)) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the CTIDBG_new settings!
             */
            Point->DecodeDatabaseReader(rdr);        // Fills himself in from the reader
            Point->setUpdatedFlag();       // Mark it updated
        }
        else
        {
            CtiPoint *pTempCtiPoint = PointFactory(rdr);               // Use the reader to get me an object of the proper type
            pTempCtiPoint->DecodeDatabaseReader(rdr);        // Fills himself in from the reader

            if(((*testFunc)(pTempCtiPoint, arg)))            // If I care about this point in the db in question....
            {
                // Add it to my list....
                pTempCtiPoint->setUpdatedFlag();               // Mark it updated
                addPoint(pTempCtiPoint);
            }
            else
            {
                delete pTempCtiPoint;                         // I don't want it!
            }
        }
    }
}

CtiPointManager::ptr_type CtiPointManager::getEqual (LONG Pt)
{
    return _smartMap.find(Pt);
}

CtiPointManager::ptr_type CtiPointManager::getEqualByName(LONG pao, string pname)
{
    ptr_type p, pRet;

    std::transform(pname.begin(), pname.end(), pname.begin(), tolower);

    if(_smartMap.entries() == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the point manager list" << endl;
        }
    }

    LockGuard  guard(getMux());
    spiterator itr;

    for(itr = begin(); itr != end(); itr++)
    {
        p = (itr->second);

        string ptname = p->getName();
        std::transform(ptname.begin(), ptname.end(), ptname.begin(), ::tolower);

        if(ptname == pname)
        {
            if(p->getUpdatedFlag())
            {
                pRet = p;
                break;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << p->getName() << " point is non-updated" << endl;
            }
        }
    }

    return pRet;
}

CtiPointManager::ptr_type CtiPointManager::getOffsetTypeEqual(LONG pao, INT Offset, INT Type)
{
    ptr_type p;
    ptr_type pRet;

    LockGuard  guard(getMux());

    std::multimap<pao_offset_t, long>::const_iterator type_itr, upper_bound;

    type_itr    = _type_offsets.lower_bound(pao_offset_t(pao, Offset));
    upper_bound = _type_offsets.upper_bound(pao_offset_t(pao, Offset));

    for( ; type_itr != upper_bound; type_itr++ )
    {
        if( (p = getEqual(type_itr->second)) && (p->getType() == Type) /* && p->getDeviceID() == pao */ )
        {
            //  make sure we don't return any pseudo status points
            if( !(p->getType() == StatusPointType && p->isPseudoPoint()) )
            {
                if( p->getUpdatedFlag() )
                {
                    pRet = p;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << p->getName() << " point is non-updated" << endl;
                }
            }
        }
    }

    return pRet;
}

CtiPointManager::ptr_type CtiPointManager::getControlOffsetEqual(LONG pao, INT Offset)
{
    ptr_type p;
    ptr_type pRet;

    LockGuard  guard(getMux());

    std::map<pao_offset_t, long>::const_iterator control_itr = _control_offsets.find(pao_offset_t(pao, Offset));

    if( control_itr != _control_offsets.end() )
    {
        if( p = getEqual(control_itr->second) /* && p->getDeviceID() == pao */ )
        {
            if( p->getUpdatedFlag() )
            {
                pRet = p;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << p->getName() << " point is non-updated" << endl;
            }
        }
    }

    return pRet;
}

void CtiPointManager::addPoint( CtiPointBase *point )
{
    if( point )
    {
        _smartMap.insert(point->getID(), point); // Stuff it in the list
    
        //  add it into the offset lookup map
        _type_offsets.insert(std::make_pair(pao_offset_t(point->getDeviceID(), point->getPointOffset()), point->getPointID()));
    
        //  if it's a control point, add it into the control offset lookup map
        if( point->getType() == StatusPointType &&
            point->getControlOffset() )
        {
            _control_offsets.insert(std::make_pair(pao_offset_t(point->getDeviceID(), point->getControlOffset()), point->getPointID()));
        }
    }
}


CtiPointManager::spiterator CtiPointManager::begin()
{
    return _smartMap.getMap().begin();
}

CtiPointManager::spiterator CtiPointManager::end()
{
    return _smartMap.getMap().end();
}

size_t CtiPointManager::entries()
{
    return _smartMap.entries();
}

CtiPointManager::CtiPointManager() {}

extern void cleanupDB();

CtiPointManager::~CtiPointManager()
{
    // cleanupDB();  // Deallocate all the DB stuff.
}


void CtiPointManager::DeleteList(void)
{
    _smartMap.removeAll(NULL, 0);

    //  do these need to be muxed?
    _type_offsets.erase(_type_offsets.begin(), _type_offsets.end());
    _control_offsets.erase(_control_offsets.begin(), _control_offsets.end());
}

bool CtiPointManager::orphan(long pid)
{
    bool retVal = false;

    if( _smartMap.remove(pid) )
    {
        retVal = true;
    }
    return retVal;
}

void CtiPointManager::refreshAlarming(LONG pntID, LONG paoID)
{
    LockGuard  guard(getMux());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBDatabase   db       = conn.database();
    RWDBSelector   selector = conn.database().selector();
    RWDBTable      keyTable;
    RWDBReader     rdr;
    CtiTime start, stop;

    start = start.now();
    CtiTablePointAlarming::getSQL( db, keyTable, selector );

    if(pntID) selector.where( keyTable["pointid"] == pntID && selector.where() );
    // There is no pao in this table!// if(paoID) selector.where( keyTable["paobjectid"] == paoID && selector.where() );

    rdr = selector.reader( conn );

    if(_smartMap.setErrorCode(rdr.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << selector.asString() << endl;
    }

    LONG pID;
    ptr_type pPt;

    while( rdr() )
    {
        rdr["pointid"] >> pID;

        // Find it in our list and decode it.
        pPt = _smartMap.find(pID);
        if(pPt && pPt->hasAlarming())
        {
            pPt->DecodeAlarmingDatabaseReader(rdr);
        }
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Alarming " << endl;
    }
}



void CtiPointManager::refreshPointLimits(LONG pntID, LONG paoID)
{
    LONG        lTemp = 0;
    ptr_type    pTempCtiPoint;

    // Make sure any limits this point previously had are marked as removed!
    if(pntID != 0 && _smartMap.entries() > 0)
    {
        if(((pTempCtiPoint = _smartMap.find(pntID))) && pTempCtiPoint->isNumeric())
        {
            ((CtiPointNumeric*)pTempCtiPoint.get())->invalidateLimits();
        }
    }
    else if(pntID == 0 && paoID != 0)       // This is much more work...
    {
        _smartMap.apply(ApplyInvalidateLimtsForPAO, (void*)paoID);
    }

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    RWDBDatabase   db       = conn.database();
    RWDBSelector   selector = conn.database().selector();
    RWDBTable      keyTable;
    RWDBReader     rdr;
    CtiTime         start, stop;

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
    if(DebugLevel & 0x00010000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
    }

    while( rdr() )
    {
        rdr["pointid"] >> lTemp;            // get the PointID

        if( _smartMap.entries() > 0 && (pTempCtiPoint = _smartMap.find(lTemp)) )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the new limit settings!
             */
            ((CtiPointNumeric*)pTempCtiPoint.get())->DecodeLimitsDatabaseReader(rdr);        // Fills himself in from the reader
        }
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Limits " << endl;
    }
}

/*
 *
 */
void CtiPointManager::refreshPointProperties(LONG pntID, LONG paoID)
{
    refreshPointLimits(pntID, paoID);

    if(_smartMap.find(findHasAlarming, NULL)) // If there is at least one point with alarming data available.
    {
        refreshAlarming(pntID, paoID);
    }
}

void CtiPointManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        int trycount = 0;

        #if 1
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock point mutex.  Will retry. **** " << __FILE__ << " (" << __LINE__ << ") Last Acquired By TID: " << getMux().lastAcquiredByTID() << " Faddr: 0x" << applyFun << endl;
            }
            gaurd.tryAcquire(30000);

            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint: Unable to lock point mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  CtiPointManger::apply " << endl;
                }
                break;
            }
        }
        #endif

        _smartMap.apply(applyFun,d);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiPointManager::ptr_type CtiPointManager::find(bool (*findFun)(const long, ptr_type, void*), void* d)
{
    ptr_type p;

    try
    {
        int trycount = 0;
        LockGuard gaurd(getMux(), 30000);

        while(!gaurd.isAcquired())
        {
            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint: Unable to lock device mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  CtiDeviceManager::find " << endl;
                }
                break;
            }
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock device mutex.  Will retry. **** " << __FILE__ << " (" << __LINE__ << ") Last Acquired By TID: " << getMux().lastAcquiredByTID() << " Faddr: 0x" << findFun << endl;
            }
            gaurd.tryAcquire(30000);
        }

        p = _smartMap.find(findFun, d);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return p;
}

