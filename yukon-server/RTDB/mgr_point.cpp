/*-----------------------------------------------------------------------------*
*
* File:   mgr_point
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_point.cpp-arc  $
* REVISION     :  $Revision: 1.52 $
* DATE         :  $Date: 2008/07/14 14:49:55 $
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
#include "cparms.h"

using namespace std;

using namespace std;

#define PERF_TO_MS(b,a,p) (UINT)(((b).QuadPart - (a).QuadPart) / ((p).QuadPart / 1000L))


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


void CtiPointManager::refreshList(BOOL (*testFunc)(CtiPointBase*,void*), void *arg, LONG pntID, LONG paoID, CtiPointType_t pntType)
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

            if(pntType == InvalidPointType || pntType == SystemPointType)
            {
                
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
            }

            if(pntType == InvalidPointType || pntType == StatusOutputPointType ||
               pntType == StatusPointType )
            {
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
            }

            if(pntType == InvalidPointType || pntType == AnalogOutputPointType ||
               pntType == AnalogPointType )
            {
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
            }

            if(pntType == InvalidPointType || pntType == DemandAccumulatorPointType ||
               pntType == PulseAccumulatorPointType )
            {
                start = start.now();
                string sql;
                selector = conn.database().selector();    // Clear the selector.
    
                if(DebugLevel & 0x00010000)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Accum" << endl;
                }
                /* Go after the accumulator points! */
                CtiPointAccumulator().getSQL(sql, pntID, paoID);
    
                rdr = ExecuteQuery( conn, sql );
                if(DebugLevel & 0x00010000 || _smartMap.setErrorCode(rdr.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << sql << endl;
                }
                /*CtiPointAccumulator().getSQL( db, keyTable, selector );
                if(pntID != 0) selector.where( keyTable["pointid"] == RWDBExpr( pntID ) && selector.where() );
                if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );
    
                rdr = selector.reader(conn);
                if(DebugLevel & 0x00010000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                } 
                */ 
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
            }

            if(pntType == InvalidPointType || pntType == CalculatedPointType ||
               pntType == CalculatedStatusPointType )
            {
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

                    //  Remove the point from the lookup maps
                    removePoint(pTempCtiPoint);
                }
            }
        }   // Temporary results are destroyed to free the connection

        if( paoID )
        {
            _paoids_loaded.insert(paoID);
        }
        else if( !pntID )
        {
            //  paoid == 0 and pntid == 0 means all points were loaded
            _all_paoids_loaded = true;
        }

    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Attempting to clear point list..." << endl;}

        ClearList();

        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "getPoints:  " << e.why() << endl;}
        RWTHROW(e);

    }
}

//This is meant to load a list of point id's at once, not a single pao.
void CtiPointManager::refreshListByPAO(const vector<long> &paoids, BOOL (*testFunc)(CtiPointBase*,void*), void *arg)
{
    {
        // Make sure all objects that that store results
        // are out of scope when the release is called
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBDatabase  db = conn.database();
        RWDBSelector  base_selector_accum  = db.selector(), selector_accum  = db.selector(),
                      base_selector_analog = db.selector(), selector_analog = db.selector(),
                      base_selector_calc   = db.selector(), selector_calc   = db.selector(),
                      base_selector_status = db.selector(), selector_status = db.selector(),
                      base_selector_system = db.selector(), selector_system = db.selector();
        RWDBTable     key_table_system,
                      key_table_status,
                      key_table_analog,
                      key_table_accum,
                      key_table_calc;
        RWDBReader    rdr;

        //  ACCUMULATOR points
        CtiPointAccumulator().getSQL(db, key_table_accum, selector_accum);
        base_selector_accum.where(selector_accum.where());

        //  ANALOG points
        CtiPointAnalog().getSQL(db, key_table_analog, selector_analog);
        base_selector_analog.where(selector_analog.where());

        //  CALC points
        CtiPointNumeric().getSQL(db, key_table_calc, selector_calc);
        base_selector_calc.where(selector_calc.where()     && (rwdbUpper(key_table_calc["pointtype"]) == RWDBExpr("CALCULATED") ||
                                                                    rwdbUpper(key_table_calc["pointtype"]) == RWDBExpr("CALCANALOG")));

        //  STATUS points
        CtiPointStatus().getSQL(db, key_table_status, selector_status );
        base_selector_status.where(selector_status.where() && (rwdbUpper(key_table_status["pointtype"]) == RWDBExpr("STATUS") ||
                                                                    rwdbUpper(key_table_status["pointtype"]) == RWDBExpr("CALCSTATUS")));

        //  SYSTEM points
        CtiPointBase().getSQL(db, key_table_system, selector_system );
        base_selector_system.where(selector_system.where() && rwdbUpper(key_table_system["pointtype"]) == RWDBExpr("SYSTEM"));

        int paoids_per_select = gConfigParms.getValueAsInt("MAX_PAOIDS_PER_SELECT", 256);

        if( paoids_per_select > paoids.size() )
        {
            paoids_per_select = paoids.size();
        }

        vector<long> selector_paoids(paoids_per_select, -1);  //  initialize to a list of -1s
        vector<long>::iterator selector_paoid_itr;

        RWDBCriterion paoid_list_criterion_accum,
                      paoid_list_criterion_analog,
                      paoid_list_criterion_calc,
                      paoid_list_criterion_status,
                      paoid_list_criterion_system;

        bool rowFound = false;

        vector<long>::const_iterator paoid_itr, paoid_itr_end;

        paoid_itr     = paoids.begin();
        paoid_itr_end = paoids.end();

        while( distance(paoid_itr, paoid_itr_end) > 0 )
        {
            string in_list;

            in_list.erase();

            for( int i = 0; (i < paoids_per_select) && (paoid_itr != paoid_itr_end); i++, paoid_itr++ )
            {
                if( !in_list.empty() )
                {
                    in_list += ",";
                }

                in_list += CtiNumStr(*paoid_itr);

                _paoids_loaded.insert(*paoid_itr);
            }

            in_list = "(" + in_list + ")";

            selector_accum .where(base_selector_accum .where() && key_table_accum ["paobjectid"].in(RWDBExpr(in_list.c_str(), false)));
            selector_analog.where(base_selector_analog.where() && key_table_analog["paobjectid"].in(RWDBExpr(in_list.c_str(), false)));
            selector_calc  .where(base_selector_calc  .where() && key_table_calc  ["paobjectid"].in(RWDBExpr(in_list.c_str(), false)));
            selector_status.where(base_selector_status.where() && key_table_status["paobjectid"].in(RWDBExpr(in_list.c_str(), false)));
            selector_system.where(base_selector_system.where() && key_table_system["paobjectid"].in(RWDBExpr(in_list.c_str(), false)));

            if( DebugLevel & 0x00010000 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << selector_accum .asString() << endl;
                dout << selector_analog.asString() << endl;
                dout << selector_calc  .asString() << endl;
                dout << selector_status.asString() << endl;
                dout << selector_system.asString() << endl;
            }

            refreshPoints(rowFound, selector_accum .reader(conn), testFunc, arg);
            refreshPoints(rowFound, selector_analog.reader(conn), testFunc, arg);
            refreshPoints(rowFound, selector_calc  .reader(conn), testFunc, arg);
            refreshPoints(rowFound, selector_status.reader(conn), testFunc, arg);
            refreshPoints(rowFound, selector_system.reader(conn), testFunc, arg);
        }
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

        ClearList();

        { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "DumpPoints:  " << e.why() << endl;}
        RWTHROW(e);

    }
}


CtiPointBase* PointFactory(RWDBReader &rdr)
{
    static const RWCString pointtype = "pointtype";
    static const RWCString pseudoflag = "pseudoflag";

    INT    PtType;
    INT    PseudoPt = FALSE;
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
            Point = CTIDBG_new CtiPointStatus;
            break;
        }
        case AnalogOutputPointType:
        case AnalogPointType:
        {
            if(PseudoPt)
                Point = CTIDBG_new CtiPointPseudoAnalog;     // Really a numeric!
            else
                Point = CTIDBG_new CtiPointAnalog;

            break;
        }
        case PulseAccumulatorPointType:
        case DemandAccumulatorPointType:
        {
            Point = CTIDBG_new CtiPointAccumulator;
            break;
        }
        case CalculatedPointType:
        {
            Point = CTIDBG_new CtiPointCalculated;          // This too is really a numeric!
            break;
        }
        case CalculatedStatusPointType:
        {
            Point = CTIDBG_new CtiPointCalculatedStatus;
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
             *  update my list entry to the new settings!
             */

            long           old_pao            = Point->getDeviceID();
            CtiPointType_t old_type           = Point->getType();
            int            old_offset         = Point->getPointOffset();
            int            old_control_offset = Point->getControlOffset();

            Point->DecodeDatabaseReader(rdr);  // Fills himself in from the reader
            Point->setUpdatedFlag();           // Mark it updated

            updatePointMaps(*Point, old_pao, old_type, old_offset, old_control_offset);  // update the type and offset maps
        }
        else
        {
            CtiPoint *pTempCtiPoint = PointFactory(rdr);    // Use the reader to get me an object of the proper type
            pTempCtiPoint->DecodeDatabaseReader(rdr);       // Fills himself in from the reader

            if(((*testFunc)(pTempCtiPoint, arg)))           // If I care about this point in the db in question....
            {
                // Add it to my list....
                pTempCtiPoint->setUpdatedFlag();            // Mark it updated

                addPoint(pTempCtiPoint);                    // add the point to the maps
            }
            else
            {
                delete pTempCtiPoint;                       // I don't want it!
            }
        }
    }
}

void CtiPointManager::addPoint( CtiPointBase *point )
{
    if( point )
    {
        _smartMap.insert(point->getID(), point); // Stuff it in the list

        //  add it into the offset lookup map
        _type_offsets.insert(std::make_pair(pao_offset_t(point->getDeviceID(), point->getPointOffset()), point->getPointID()));
        _pao_pointids.insert(std::make_pair(point->getDeviceID(), point->getPointID()));

        //  if it's a control point, add it into the control offset lookup map
        if( point->getType() == StatusPointType &&
            point->getControlOffset() )
        {
            _control_offsets.insert(std::make_pair(pao_offset_t(point->getDeviceID(), point->getControlOffset()), point->getPointID()));
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

    if( !_all_paoids_loaded && (_paoids_loaded.find(pao) == _paoids_loaded.end()) )
    {
        refreshList(isPoint, NULL, 0, pao);
    }

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


void CtiPointManager::getEqualByPAO(long pao, vector<ptr_type> &points)
{
    ptr_type p;
    ptr_type pRet;

    LockGuard  guard(getMux());

    if( !_all_paoids_loaded && (_paoids_loaded.find(pao) == _paoids_loaded.end()) )
    {
        refreshList(isPoint, NULL, 0, pao);
    }

    multimap<long, long>::const_iterator itr         = _pao_pointids.lower_bound(pao),
                                         lower_bound = _pao_pointids.upper_bound(pao);

    for( ; itr != lower_bound; itr++ )
    {
        ptr_type p = getEqual(itr->second);

        if( p )  points.push_back(p);
    }
}


CtiPointManager::ptr_type CtiPointManager::getOffsetTypeEqual(LONG pao, INT Offset, CtiPointType_t Type)
{
    ptr_type p;
    ptr_type pRet;

    LockGuard  guard(getMux());

    if( !_all_paoids_loaded && (_paoids_loaded.find(pao) == _paoids_loaded.end()) )
    {
        if( DebugLevel & 0x00010000 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " refreshing points for paoid " << pao << endl;
        }

        refreshList(isPoint, NULL, 0, pao);
    }

    multimap<pao_offset_t, long>::const_iterator type_itr, upper_bound;

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

    if( !_all_paoids_loaded && (_paoids_loaded.find(pao) == _paoids_loaded.end()) )
    {
        refreshList(isPoint, NULL, 0, pao);
    }

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


long CtiPointManager::getPAOIdForPointId(long pointid)
{
    long retval = -1;

    ptr_type p = getEqual(pointid);

    if( p )
    {
        retval = p->getDeviceID();
    }

    return retval;
}


CtiPointManager::CtiPointManager() {}

extern void cleanupDB();

CtiPointManager::~CtiPointManager()
{
    // cleanupDB();  // Deallocate all the DB stuff.
}


void CtiPointManager::ClearList(void)
{
    _smartMap.removeAll(NULL, 0);

    _pao_pointids.clear();
    _type_offsets.clear();
    _control_offsets.clear();

    _paoids_loaded.clear();
    _all_paoids_loaded = false;
}


template <class K, class _Ty>
void erase_from_multimap(multimap<K, _Ty> &coll, const K key, const _Ty value)
{
    multimap<K, _Ty>::iterator itr, upper_bound;

    itr         = coll.lower_bound(key);
    upper_bound = coll.upper_bound(key);

    for( ; itr != upper_bound; itr++ )
    {
         if( itr->second == value )
         {
             coll.erase(itr);

             break;
         }
    }
}


void CtiPointManager::updatePointMaps( const CtiPointBase &point, long old_pao, CtiPointType_t old_type, int old_offset, int old_control_offset )
{
    long point_id = point.getPointID();

    long           new_pao            = point.getDeviceID();
    CtiPointType_t new_type           = point.getType();
    int            new_offset         = point.getPointOffset();
    int            new_control_offset = point.getControlOffset();

    bool pao_change            = new_pao            != old_pao,
         type_change           = new_type           != old_type,
         offset_change         = new_offset         != old_offset,
         control_offset_change = new_control_offset != old_control_offset;

    if( pao_change || type_change || offset_change || control_offset_change )
    {
        //  refresh the control offset if necessary
        if( old_type == StatusPointType && old_control_offset && (pao_change || type_change || control_offset_change) )
        {
            _control_offsets.erase(pao_offset_t(old_pao, old_control_offset));

            if( new_type == StatusPointType &&
                new_control_offset )
            {
                _control_offsets.insert(std::make_pair(pao_offset_t(new_pao, new_control_offset), point_id));
            }
        }

        if( pao_change || type_change || offset_change )
        {
            erase_from_multimap(_type_offsets, pao_offset_t(old_pao, old_offset), point_id);
            _type_offsets.insert(std::make_pair(pao_offset_t(new_pao, new_offset), point_id));
        }

        if( pao_change )
        {
            erase_from_multimap(_pao_pointids, old_pao, point_id);
            _pao_pointids.insert(std::make_pair(new_pao, point_id));
        }
    }
}


void CtiPointManager::removePoint(ptr_type pTempCtiPoint)
{
    if( pTempCtiPoint )
    {
        //  If it's a status point with control, remove it from the control point lookup as well
        if( pTempCtiPoint->getType() == StatusPointType &&
            pTempCtiPoint->getControlOffset() )
        {
            _control_offsets.erase(pao_offset_t(pTempCtiPoint->getDeviceID(), pTempCtiPoint->getControlOffset()));
        }

        erase_from_multimap(_type_offsets, pao_offset_t(pTempCtiPoint->getDeviceID(), pTempCtiPoint->getPointOffset()), pTempCtiPoint->getPointID());

        erase_from_multimap(_pao_pointids, pTempCtiPoint->getDeviceID(), pTempCtiPoint->getPointID());
    }
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

//Load based on PAO assumes it is in add!
void CtiPointManager::refreshAlarming(LONG pntID, LONG paoID)
{
    LockGuard  guard(getMux());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBDatabase   db       = conn.database();
    RWDBSelector   selector = conn.database().selector();
    RWDBTable      keyTable;
    RWDBReader     rdr;
    string         sql;
    CtiTime start, stop;

    start = start.now();
    CtiTablePointAlarming::getSQL(sql, pntID, paoID);

    rdr = ExecuteQuery( conn, sql );

    if(_smartMap.setErrorCode(rdr.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << sql << endl;
    }

    LONG pID;
    ptr_type pPt;

    while( rdr() )
    {
        rdr["pointid"] >> pID;

        // Find it in our list and decode it.
        pPt = _smartMap.find(pID);
        if(pPt)
        {
            pPt->DecodeAlarmingDatabaseReader(rdr);
        }
    }

    RWDBSelector attributeSelector = conn.database().selector();
    start = start.now();
    CtiTablePointProperty::getSQL( db, keyTable, attributeSelector );
    RWDBReader attribRdr;

    if(pntID)
    {
        attributeSelector.where( keyTable["pointid"] == pntID && attributeSelector.where() );
        pPt = _smartMap.find(pID);
        if(pPt && pPt->getProperties() != NULL)
        {
            pPt->getProperties()->resetTable();
        }
    }
    if(paoID)
    {
        RWDBSelector paoSelector = conn.database().selector();
        RWDBTable pointTable = db.table("point");
        paoSelector << pointTable["pointid"];
        paoSelector.from(pointTable);
        paoSelector.where(pointTable["paobjectid"] == paoID);
        selector.where(keyTable["pointid"].in(paoSelector) && selector.where());
    }

    attribRdr = attributeSelector.reader( conn );

    if(_smartMap.setErrorCode(attribRdr.status().errorCode()) != RWDBStatus::ok)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << attributeSelector.asString() << endl;
    }

    while( attribRdr() )
    {
        attribRdr["pointid"] >> pID;

        // Find it in our list and decode it.
        pPt = _smartMap.find(pID);
        if(pPt)
        {
            pPt->DecodeAttributeDatabaseReader(attribRdr);
        }
    }

    if((stop = stop.now()).seconds() - start.seconds() > 5 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Alarming " << endl;
    }
}


void CtiPointManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        int trycount = 0;

        #if 1
        LockGuard guard(getMux(), 30000);

        while(!guard.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock point mutex.  Will retry. **** " << __FILE__ << " (" << __LINE__ << ") Last Acquired By TID: " << getMux().lastAcquiredByTID() << " Faddr: 0x" << applyFun << endl;
            }
            guard.tryAcquire(30000);

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

CtiPointManager::ptr_type CtiPointManager::find(bool (*findFun)(const long, const ptr_type &, void*), void* d)
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

