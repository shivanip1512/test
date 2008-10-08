/*-----------------------------------------------------------------------------*
*
* File:   mgr_point
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_point.cpp-arc  $
* REVISION     :  $Revision: 1.58 $
* DATE         :  $Date: 2008/10/08 19:57:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "boost/mem_fn.hpp"

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


// Return TRUE if it is NOT SET
inline RWBoolean isPointNotUpdated(CtiPointSPtr &pPoint, void* d)  {  return(RWBoolean(!pPoint->getUpdatedFlag()));  }

void ApplyPointResetUpdated(const long key, CtiPointSPtr pPoint, void* d)
{
    pPoint->resetUpdatedFlag();
    pPoint->setValid();
    return;
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


CtiPointManager::CtiPointManager() {}

CtiPointManager::~CtiPointManager()
{
}


void CtiPointManager::refreshList(LONG pntID, LONG paoID, CtiPointType_t pntType)
{
    ptr_type pTempCtiPoint;
    bool     rowFound = false;

    CtiTime start, stop;

    try
    {
        coll_type::writer_lock_guard_t guard(getLock());

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
                refreshPoints(rowFound, rdr);
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
                refreshPoints(rowFound, rdr);
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
                refreshPoints(rowFound, rdr);
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
                /*CtiPointAccumulator::getSQL( db, keyTable, selector );
                if(pntID != 0) selector.where( keyTable["pointid"] == RWDBExpr( pntID ) && selector.where() );
                if(paoID != 0) selector.where( keyTable["paobjectid"] == RWDBExpr( paoID ) && selector.where() );

                rdr = selector.reader(conn);
                if(DebugLevel & 0x00010000 || _smartMap.setErrorCode(selector.status().errorCode()) != RWDBStatus::ok)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl;
                }
                */
                refreshPoints(rowFound, rdr);
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
                refreshPoints(rowFound, rdr);
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
                while( pTempCtiPoint = _smartMap.find(isPointNotUpdated, NULL) )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  Evicting " << pTempCtiPoint->getName() << " from list" << endl;
                    }

                    erase(pTempCtiPoint->getPointID());
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

void CtiPointManager::refreshListByPAOIDs(const set<long> &id_list)
{
    refreshListByIDs(id_list, true);
}

void CtiPointManager::refreshListByPointIDs(const set<long> &id_list)
{
    refreshListByIDs(id_list, false);
}

void CtiPointManager::refreshListByIDs(const set<long> &id_list, bool paoids)
{
    //  first thing we do before we grab any other locks
    coll_type::writer_lock_guard_t guard(_smartMap.getLock());

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

    int ids_per_select = min(static_cast<int>(id_list.size()), gConfigParms.getValueAsInt("MAX_IDS_PER_POINT_SELECT", 256));

    RWDBCriterion id_list_criterion_accum,
                  id_list_criterion_analog,
                  id_list_criterion_calc,
                  id_list_criterion_status,
                  id_list_criterion_system;

    set<long> ids_to_load;

    if( paoids )
    {
        set_difference(id_list.begin(),
                       id_list.end(),
                       _paoids_loaded.begin(),
                       _paoids_loaded.end(),
                       inserter(ids_to_load, ids_to_load.begin()));
    }
    else
    {
        //  we will need to get smarter about what points are and aren't loaded
        copy(id_list.begin(),
             id_list.end(),
             inserter(ids_to_load, ids_to_load.begin()));
    }

    set<long>::const_iterator id_itr = ids_to_load.begin(),
                              id_end = ids_to_load.end();

    const RWCString key_column(paoids?"paobjectid":"pointid");

    while( id_itr != id_end )
    {
        string in_list;

        in_list.erase();

        for( int i = 0; (i < ids_per_select) && (id_itr != id_end); i++, id_itr++ )
        {
            if( !in_list.empty() )
            {
                in_list += ",";
            }

            in_list += CtiNumStr(*id_itr);

            if( paoids )
            {
                //  this is presumptive - we could run into trouble here with a select that went wrong
                _paoids_loaded.insert(*id_itr);
            }
        }

        in_list = "(" + in_list + ")";

        selector_accum .where(base_selector_accum .where() && key_table_accum [key_column].in(RWDBExpr(in_list.c_str(), false)));
        selector_analog.where(base_selector_analog.where() && key_table_analog[key_column].in(RWDBExpr(in_list.c_str(), false)));
        selector_calc  .where(base_selector_calc  .where() && key_table_calc  [key_column].in(RWDBExpr(in_list.c_str(), false)));
        selector_status.where(base_selector_status.where() && key_table_status[key_column].in(RWDBExpr(in_list.c_str(), false)));
        selector_system.where(base_selector_system.where() && key_table_system[key_column].in(RWDBExpr(in_list.c_str(), false)));

        if( DebugLevel & 0x00010000 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << selector_accum .asString() << endl;
            dout << selector_analog.asString() << endl;
            dout << selector_calc  .asString() << endl;
            dout << selector_status.asString() << endl;
            dout << selector_system.asString() << endl;
        }

        bool rowFound;  //  placeholder

        refreshPoints(rowFound, selector_accum .reader(conn));
        refreshPoints(rowFound, selector_analog.reader(conn));
        refreshPoints(rowFound, selector_calc  .reader(conn));
        refreshPoints(rowFound, selector_status.reader(conn));
        refreshPoints(rowFound, selector_system.reader(conn));
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

void CtiPointManager::refreshPoints(bool &rowFound, RWDBReader& rdr)
{
    LONG     lTemp = 0;
    ptr_type Point;

    coll_type::writer_lock_guard_t guard(getLock());

    while( rdr() )
    {
        rowFound = true;

        rdr["pointid"] >> lTemp;            // get the PointID

        if( !_smartMap.empty() && (Point = _smartMap.find(lTemp)) )
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

            // Add it to my list....
            pTempCtiPoint->setUpdatedFlag();            // Mark it updated

            addPoint(pTempCtiPoint);                    // add the point to the maps
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


void CtiPointManager::updateAccess(long pointid, time_t time_now)
{
    lru_guard_t lru_guard(_lru_mux);

    time_t time_index = time_now / 10;

    lru_point_lookup_map::iterator lru_point = _lru_points.find(pointid);

    //  we know about this pointid
    if( lru_point != _lru_points.end() )
    {
        lru_timeslice_map::iterator &timeslice = lru_point->second;

        //  it's already in the right timeslice - we're done
        if( timeslice->first == time_index )  return;

        //  invalidate the old entry
        timeslice->second.erase(pointid);
    }

    //  insert the pointid into the current timeslice's set
    _lru_timeslices[time_index].insert(pointid);

    //  update the point's iterator
    _lru_points[pointid] = _lru_timeslices.find(time_now / 10);
}


void CtiPointManager::addPoint( CtiPointBase *point )
{
    if( point )
    {
        _smartMap.insert(point->getID(), point); // Stuff it in the list

        updateAccess(point->getID());

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
    CtiPointManager::ptr_type retVal = _smartMap.find(Pt);

    if(retVal)
    {
        updateAccess(Pt);
    }

    return retVal;
}

CtiPointManager::ptr_type CtiPointManager::getEqualByName(LONG pao, string pname)
{
    ptr_type p, pRet;

    bool loadPAO = false;

    std::transform(pname.begin(), pname.end(), pname.begin(), tolower);

    if(_smartMap.entries() == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " There are no entries in the point manager list" << endl;
        }
    }

    {
        coll_type::reader_lock_guard_t guard(getLock());

        loadPAO = !_all_paoids_loaded && (_paoids_loaded.find(pao) == _paoids_loaded.end());
    }

    if( loadPAO )
    {
        refreshList(0, pao);
    }

    {
        coll_type::reader_lock_guard_t guard(getLock());
        spiterator itr = begin(), itr_end = end();

        for( ; itr != itr_end; itr++ )
        {
            p = (itr->second);

            string ptname = p->getName();
            std::transform(ptname.begin(), ptname.end(), ptname.begin(), ::tolower);

            if(ptname == pname)
            {
                if(p->getUpdatedFlag())
                {
                    updateAccess(p->getPointID());

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
    }

    return pRet;
}


void CtiPointManager::getEqualByPAO(long pao, vector<ptr_type> &points)
{
    ptr_type p;
    ptr_type pRet;

    bool loadPAO = false;

    {
        coll_type::reader_lock_guard_t guard(getLock());

        loadPAO = !_all_paoids_loaded && (_paoids_loaded.find(pao) == _paoids_loaded.end());
    }

    if( loadPAO )
    {
        refreshList(0, pao);
    }

    {
        coll_type::reader_lock_guard_t guard(getLock());

        multimap<long, long>::const_iterator itr         = _pao_pointids.lower_bound(pao),
                                             lower_bound = _pao_pointids.upper_bound(pao);

        time_t time_now = std::time(0);

        for( ; itr != lower_bound; itr++ )
        {
            ptr_type p = getEqual(itr->second);

            if( p )
            {
                updateAccess(p->getPointID(), time_now);

                points.push_back(p);
            }
        }
    }
}


CtiPointManager::ptr_type CtiPointManager::getOffsetTypeEqual(LONG pao, INT Offset, CtiPointType_t Type)
{
    ptr_type p;
    ptr_type pRet;

    bool loadPAO = false;

    {
        coll_type::reader_lock_guard_t guard(getLock());

        loadPAO = !_all_paoids_loaded && (_paoids_loaded.find(pao) == _paoids_loaded.end());
    }

    if( loadPAO )
    {
        if( DebugLevel & 0x00010000 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " refreshing points for paoid " << pao << endl;
        }

        refreshList(0, pao);
    }

    {
        coll_type::reader_lock_guard_t guard(getLock());

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
                        updateAccess(p->getPointID());

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
    }

    return pRet;
}

CtiPointManager::ptr_type CtiPointManager::getControlOffsetEqual(LONG pao, INT Offset)
{
    ptr_type p;
    ptr_type pRet;

    bool loadPAO = false;

    {
        coll_type::reader_lock_guard_t guard(getLock());

        loadPAO = !_all_paoids_loaded && (_paoids_loaded.find(pao) == _paoids_loaded.end());
    }

    if( loadPAO )
    {
        refreshList(0, pao);
    }

    {
        coll_type::reader_lock_guard_t guard(getLock());

        std::map<pao_offset_t, long>::const_iterator control_itr = _control_offsets.find(pao_offset_t(pao, Offset));

        if( control_itr != _control_offsets.end() )
        {
            if( p = getEqual(control_itr->second) )
            {
                if( p->getUpdatedFlag() )
                {
                    updateAccess(p->getPointID());

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


CtiPointManager::spiterator         CtiPointManager::begin()    {  return _smartMap.getMap().begin();  }
CtiPointManager::spiterator         CtiPointManager::end()      {  return _smartMap.getMap().end();    }
size_t                              CtiPointManager::entries()  {  return _smartMap.entries();         }
CtiPointManager::coll_type::lock_t &CtiPointManager::getLock()  {  return _smartMap.getLock();         }


void CtiPointManager::apply(void (*applyFun)(const long, ptr_type, void*), void* d)
{
    try
    {
        int trycount = 0;

        #if 1
        coll_type::writer_lock_guard_t guard(getLock());

        while(!guard.isAcquired())
        {
            if(trycount++ > 6)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint: Unable to lock point mutex **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  CtiPointManger::apply " << endl;
                }
                break;
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint: Unable to lock point mutex.  Will retry. **** " << __FILE__ << " (" << __LINE__ << ") Last Acquired By TID: " << static_cast<string>(getLock()) << " Faddr: 0x" << applyFun << endl;
            }
            guard.tryAcquire(30000);
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


long CtiPointManager::getPAOIdForPointId(long pointid)
{
    ptr_type p = getEqual(pointid);

    if( p )  return p->getDeviceID();

    return -1;
}


void CtiPointManager::DumpList(void)
{
    try
    {
        coll_type::reader_lock_guard_t guard(getLock());

        spiterator itr, itr_end = end();

        for( itr = begin(); itr != itr_end; itr++)
        {
            itr->second->DumpData();

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl;
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


void CtiPointManager::ClearList(void)
{
    coll_type::writer_lock_guard_t guard(getLock());

    _smartMap.removeAll(NULL, 0);

    _pao_pointids.clear();
    _type_offsets.clear();
    _control_offsets.clear();

    _paoids_loaded.clear();
    _all_paoids_loaded = false;
}


void CtiPointManager::processExpired()
{
    coll_type::writer_lock_guard_t guard(getLock());

    lru_guard_t lru_guard(_lru_mux);

    lru_timeslice_map::iterator timeslice_itr = _lru_timeslices.begin(),
                                timeslice_end = _lru_timeslices.end();

    const int cache_max = 100 * 1000;
    int valid_points = 0;

    time_t expiration_time  = 300;  //  only expire points that were created longer than this ago
    time_t expiration_index = (std::time(0) - expiration_time) / 10;

    //  add up to our maximum number of points
    for( ; timeslice_itr != timeslice_end && (valid_points + timeslice_itr->second.size()) < cache_max; ++timeslice_itr )
    {
        valid_points += timeslice_itr->second.size();
    }

    //  if we're not already into the expired timeslices, find the first expired entry
    if( timeslice_itr != timeslice_end && _lru_timeslices.key_comp()(timeslice_itr->first, expiration_index) )
    {
        timeslice_itr = _lru_timeslices.upper_bound(expiration_index);
    }

    set<long> expired_pointids;

    //  go through the remaining timeslices and expire everything
    while( timeslice_itr != timeslice_end )
    {
        set<long>::iterator point_itr = timeslice_itr->second.begin(),
                            point_end = timeslice_itr->second.end();

        while( point_itr != point_end )
        {
            expired_pointids.insert(*point_itr++);
        }

        timeslice_itr = _lru_timeslices.erase(timeslice_itr);
    }

    vector<ptr_type> expired;

    set<long>::iterator pointid_itr = expired_pointids.begin(),
                        pointid_end = expired_pointids.end();

    //  erase all expired points
    for( ; pointid_itr != pointid_end; ++pointid_itr )
    {
        erase(*pointid_itr);

        _lru_points.erase(*pointid_itr);
    }
}


void CtiPointManager::erase(long pid)
{
    ptr_type deleted = _smartMap.remove(pid);

    _pendingDeletions.insert(make_pair(pid, CtiPointWPtr(deleted)));

    removePoint(deleted);
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

