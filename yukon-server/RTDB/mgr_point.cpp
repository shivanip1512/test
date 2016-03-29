#include "precompiled.h"

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
#include "desolvers.h"
#include "module_util.h"

#include "cparms.h"
#include "database_reader.h"
#include "database_util.h"

#include "std_helper.h"

#include <boost/range/algorithm/find_if.hpp>

using namespace std;

#define PERF_TO_MS(b,a,p) (UINT)(((b).QuadPart - (a).QuadPart) / ((p).QuadPart / 1000L))


BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            Cti::identifyProject(CompileInfo);

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


static Cti::RowReader& operator >> (Cti::RowReader& rdr, CtiPointBase& p);
CtiPointBase* PointFactory(Cti::RowReader &rdr);


// Return TRUE if it is NOT SET
inline bool isPointNotUpdated(CtiPointSPtr &pPoint, void* d)  {  return ! pPoint->getUpdatedFlag();  }

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


CtiPointManager::CtiPointManager() :
_all_paoids_loaded(false)
{}

CtiPointManager::~CtiPointManager()
{
}


std::set<long> CtiPointManager::refreshList(LONG pntID, LONG paoID, CtiPointType_t pntType)
{
    ptr_type pTempCtiPoint;
    std::set<long> pointIdsFound;
    bool errors = false;

    CtiTime start, stop;

    {   // Make sure all objects that that store results
        start = start.now();
        if(pntID == 0 && paoID == 0)
        {
            coll_type::writer_lock_guard_t guard(getLock());

            // Reset everyone's Updated flag.
            _smartMap.apply(ApplyPointResetUpdated, NULL);

        }
        if((stop = stop.now()).seconds() - start.seconds() > 5 )
        {
            CTILOG_INFO(dout, (stop.seconds() - start.seconds()) <<" seconds for ApplyPointResetUpdated");
        }

        if(pntType == InvalidPointType || pntType == SystemPointType)
        {
            start = start.now();
            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "Looking for System Points");
            }

            string sql = CtiPointBase().getSQLCoreStatement() + " WHERE upper (PT.pointtype) = 'SYSTEM'";

            if( pntID != 0 )
            {
                sql += " AND PT.pointid = ?";
            }
            if( paoID != 0 )
            {
                sql += " AND PT.paobjectid = ?";
            }

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            if( pntID != 0 )
            {
                rdr << pntID;
            }
            if( paoID != 0 )
            {
                rdr << paoID;
            }

            rdr.execute();

            if( ! rdr.isValid() )
            {
                CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
            }
            else if( DebugLevel & 0x00010000 )
            {
                CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "Done Looking for System Points");
            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CTILOG_INFO(dout, (stop.seconds() - start.seconds()) <<" seconds for System Devices");
            }
        }

        if(pntType == InvalidPointType || pntType == StatusOutputPointType ||
           pntType == StatusPointType || pntType == CalculatedStatusPointType)
        {
            start = start.now();

            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "Looking for Status/Control");
            }

            string sql =
                CtiPointStatus::getSQLCoreStatement() +
                " WHERE"
                    " (upper (PT.pointtype) = 'STATUS' OR upper (PT.pointtype) = 'CALCSTATUS')";

            if( pntID != 0 )
            {
                sql += " AND PT.pointid = ?";
            }
            if( paoID != 0 )
            {
                sql += " AND PT.paobjectid = ?";
            }

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            if( pntID != 0 )
            {
                rdr << pntID;
            }
            if( paoID != 0 )
            {
                rdr << paoID;
            }

            rdr.execute();

            if( ! rdr.isValid() )
            {
                CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
            }
            else if( DebugLevel & 0x00010000 )
            {
                CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "Done Looking for Status/Control");
            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CTILOG_INFO(dout, (stop.seconds() - start.seconds()) <<" seconds for Status/Control");
            }
        }

        if(pntType == InvalidPointType || pntType == AnalogOutputPointType ||
           pntType == AnalogPointType )
        {
            start = start.now();

            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "Looking for Analogs");
            }

            string sql = CtiPointAnalog::getSQLCoreStatement();

            if( pntID && paoID )
            {
                sql += " WHERE PT.pointid = ? AND PT.paobjectid = ?";
            }
            else if( pntID )
            {
                sql += " WHERE PT.pointid = ?";
            }
            else if( paoID )
            {
                sql += " WHERE PT.paobjectid = ?";
            }

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            if( pntID != 0 )
            {
                rdr << pntID;
            }
            if( paoID != 0 )
            {
                rdr << paoID;
            }

            rdr.execute();

            if( ! rdr.isValid() )
            {
                CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
            }
            else if( DebugLevel & 0x00010000 )
            {
                CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "DONE Looking for Analogs");
            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CTILOG_INFO(dout, (stop.seconds() - start.seconds()) <<" seconds for Analogs");
            }
        }

        if(pntType == InvalidPointType || pntType == DemandAccumulatorPointType ||
           pntType == PulseAccumulatorPointType )
        {
            start = start.now();

            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "Looking for Accum");
            }
            /* Go after the accumulator points! */
            string sql = CtiPointAccumulator().getSQLCoreStatement();

            if( pntID != 0 )
            {
                sql += " AND PT.pointid = ?";
            }
            if( paoID != 0 )
            {
                sql += " AND PT.paobjectid = ?";
            }

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            if( pntID != 0 )
            {
                rdr << pntID;
            }
            if( paoID != 0 )
            {
                rdr << paoID;
            }

            rdr.execute();

            if( ! rdr.isValid() )
            {
                CTILOG_ERROR(dout, "DB read failed for SQL query: " << rdr.asString());
            }
            if( DebugLevel & 0x00010000 )
            {
                CTILOG_DEBUG(dout, "DB read: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "DONE Looking for Accum");
            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CTILOG_INFO(dout, (stop.seconds() - start.seconds()) <<" seconds for Accumulators");
            }
        }

        if(pntType == InvalidPointType || pntType == CalculatedPointType)
        {
            start = start.now();

            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "Looking for CALC");
            }

            string sql = CtiPointNumeric::getSQLCoreStatement() +
                         " AND (upper (PT.pointtype) = 'CALCULATED' OR upper (PT.pointtype) = 'CALCANALOG')";

            if(pntID != 0)
            {
                sql += " AND PT.pointid = ?";
            }
            if(paoID != 0)
            {
                sql += " AND PT.paobjectid = ?";
            }

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            if(pntID != 0)
            {
                rdr << pntID;
            }
            if(paoID != 0)
            {
                rdr << paoID;
            }

            rdr.execute();

            if( ! rdr.isValid() )
            {
                CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
            }
            else if( DebugLevel & 0x00010000 )
            {
                CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if(DebugLevel & 0x00010000)
            {
                CTILOG_DEBUG(dout, "DONE Looking for CALC");
            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CTILOG_INFO(dout, (stop.seconds() - start.seconds()) <<" seconds for Calc points");
            }
        }
    }

    //  No errors, so we can safely determine if points were loaded or deleted
    if( ! errors )
    {
        // Now I need to check for any Point removals based upon the
        // Updated Flag being NOT set

        if( pntID == 0 && paoID == 0 && !pointIdsFound.empty() )
        {
            coll_type::writer_lock_guard_t guard(getLock());

            //  paoid == 0 and pntid == 0 means all points were loaded
            _all_paoids_loaded = true;

            while( pTempCtiPoint = _smartMap.find(isPointNotUpdated, NULL) )
            {
                CTILOG_INFO(dout, "Evicting "<< pTempCtiPoint->getName() <<" from list");

                erase(pTempCtiPoint->getPointID());
            }
        }
        else if( paoID )
        {
            coll_type::writer_lock_guard_t guard(getLock());

            _paoids_loaded.insert(paoID);
        }
    }

    return pointIdsFound;
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
    // Make sure all objects that that store results
    // are out of scope when the release is called
    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection);

    int ids_per_select = min(static_cast<int>(id_list.size()), gConfigParms.getValueAsInt("MAX_IDS_PER_POINT_SELECT", 256));

    set<long> ids_to_load;

    if( paoids )
    {
        coll_type::reader_lock_guard_t guard(_smartMap.getLock());

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

    const string keyColString = paoids ? "paobjectid IN ":"pointid IN ";

    //  ACCUMULATOR points
    const string sql_accum  = CtiPointAccumulator::getSQLCoreStatement() + " AND PT.";

    //  ANALOG points
    const string sql_analog = CtiPointAnalog::getSQLCoreStatement() + " WHERE PT.";

    //  CALC points
    const string sql_calc   = CtiPointNumeric::getSQLCoreStatement() +
                              " AND (upper (PT.pointtype) = 'CALCULATED' OR upper (PT.pointtype) = 'CALCANALOG')"
                              " AND PT.";

    //  STATUS points
    const string sql_status = CtiPointStatus::getSQLCoreStatement() +
                              " WHERE"
                                  " (upper (PT.pointtype) = 'STATUS' OR upper (PT.pointtype) = 'CALCSTATUS') "
                                  " AND PT.";

    //  SYSTEM points
    const string sql_system = CtiPointBase::getSQLCoreStatement() +
                              " WHERE upper (PT.pointtype) = 'SYSTEM' AND PT.";

    set<long>::const_iterator   id_itr = ids_to_load.begin(),
                                id_end = ids_to_load.end();

    bool errors = false;

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
        }

        in_list = "(" + in_list + ")";

        std::stringstream ss_accum, ss_analog, ss_calc, ss_status, ss_system;

        ss_accum    << sql_accum    << keyColString << in_list.c_str();
        ss_analog   << sql_analog   << keyColString << in_list.c_str();
        ss_calc     << sql_calc     << keyColString << in_list.c_str();
        ss_status   << sql_status   << keyColString << in_list.c_str();
        ss_system   << sql_system   << keyColString << in_list.c_str();

        if( DebugLevel & 0x00010000 )
        {
            string loggedSQLaccum  = ss_accum .str();
            string loggedSQLanalog = ss_analog.str();
            string loggedSQLcalc   = ss_calc  .str();
            string loggedSQLstatus = ss_status.str();
            string loggedSQLsystem = ss_system.str();

            CTILOG_DEBUG(dout,
                    endl << loggedSQLaccum  <<
                    endl << loggedSQLanalog <<
                    endl << loggedSQLcalc   <<
                    endl << loggedSQLstatus <<
                    endl << loggedSQLsystem
                    );
        }

        std::set<long> pointIdsFound;  //  placeholder

        rdr.setCommandText(ss_accum .str());
        rdr.execute();
        errors |= refreshPoints(pointIdsFound, rdr);

        rdr.setCommandText(ss_analog.str());
        rdr.execute();
        errors |= refreshPoints(pointIdsFound, rdr);

        rdr.setCommandText(ss_calc  .str());
        rdr.execute();
        errors |= refreshPoints(pointIdsFound, rdr);

        rdr.setCommandText(ss_status.str());
        rdr.execute();
        errors |= refreshPoints(pointIdsFound, rdr);

        rdr.setCommandText(ss_system.str());
        rdr.execute();
        errors |= refreshPoints(pointIdsFound, rdr);
    }

    //  No errors, so note down which PAOs were fully loaded
    if( paoids && ! errors )
    {
        coll_type::reader_lock_guard_t guard(_smartMap.getLock());

        copy(ids_to_load.begin(), ids_to_load.end(), inserter(_paoids_loaded, _paoids_loaded.begin()));
    }
}


CtiPointBase* PointFactory(Cti::RowReader &rdr)
{
    static const string pointtype = "pointtype";
    static const string pseudoflag = "pseudoflag";

    string typeStr;
    string pseudoStr;

    rdr[pointtype]  >> typeStr;
    rdr[pseudoflag] >> pseudoStr;

    if(getDebugLevel() & DEBUGLEVEL_FACTORY)
    {
        CTILOG_DEBUG(dout, "Creating a Point of type "<< typeStr);
    }
    const CtiPointType_t PtType = resolvePointType(typeStr);

    if(getDebugLevel() & DEBUGLEVEL_FACTORY)
    {
        CTILOG_DEBUG(dout, "Is point a pseudo point? "<< pseudoStr);
    }

    const bool PseudoPt = ciStringEqual(pseudoStr, "y");

    CtiPointBase *Point = NULL;

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
                Point = CTIDBG_new CtiPointNumeric;
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
            Point = CTIDBG_new CtiPointNumeric;
            break;
        }
        case CalculatedStatusPointType:
        {
            Point = CTIDBG_new CtiPointStatus;
            break;
        }
        case SystemPointType:
        {
            Point = CTIDBG_new CtiPointBase;
            break;
        }
        default:
        {
            CTILOG_ERROR(dout, "Unknown point type! ("<< PtType <<")");
            break;
        }
    }

    // Identify the point so it knows what it is...  should really be part of the constructor
    if(Point) Point->setType((CtiPointType_t)PtType);

    return Point;
}

bool CtiPointManager::refreshPoints(std::set<long>& pointIdsFound, Cti::RowReader& rdr)
{
    bool valid = true;

    vector<CtiPointBase *> newPoints;

    while( (valid = rdr.isValid()) && rdr() )
    {
        CtiPointBase *newPoint = PointFactory(rdr);    // Use the reader to get me an object of the proper type
        newPoint->DecodeDatabaseReader(rdr);       // Fills himself in from the reader

        // Add it to my list....
        newPoint->setUpdatedFlag();            // Mark it updated

        newPoints.push_back(newPoint);
        pointIdsFound.insert(newPoint->getPointID());
    }

    {
        coll_type::writer_lock_guard_t guard(getLock());

        for( auto newPoint : newPoints )
        {
            addPoint(newPoint);
        }
    }

    return ! valid;
}


void CtiPointManager::updateAccess(long pointid)
{
    lru_guard_t lru_guard(_lru_mux);

    auto time_index = currentTime() / 10;

    //  we know about this pointid
    if( const auto oldTimeslice = Cti::mapFind(_lru_points, pointid) )
    {
        //  it's already in the right timeslice - we're done
        if( oldTimeslice == time_index )
        {
            return;
        }

        if( auto oldTimesliceSet = Cti::mapFindRef(_lru_timeslices, *oldTimeslice) )
        {
            //  remove the old entry
            oldTimesliceSet->erase(pointid);
        }
    }

    _lru_timeslices[time_index].insert(pointid);

    //  update the point's timeslice
    _lru_points[pointid] = time_index;
}


void CtiPointManager::addPoint( CtiPointBase *point )
{
    if( point )
    {
        if( _smartMap.find(point->getID()) )
        {
            refresh(point->getID());
        }

        _smartMap.insert(point->getID(), point); // Stuff it in the list

        updateAccess(point->getID());

        //  add it into the offset lookup map
        _type_offsets.insert(std::make_pair(pao_offset_t(point->getDeviceID(), (long) point->getPointOffset()), point->getPointID()));
        _pao_pointids.insert(std::make_pair(point->getDeviceID(), point->getPointID()));

        //  if it's a control point, add it into the control offset lookup map
        if( point->getType() == StatusPointType )
        {
            CtiPointStatus *pStatus = static_cast<CtiPointStatus *>(point);

            if( const boost::optional<CtiTablePointStatusControl> controlParameters = pStatus->getControlParameters() )
            {
                if( controlParameters->getControlOffset() )
                {
                    _control_offsets.insert(std::make_pair(pao_offset_t(point->getDeviceID(), controlParameters->getControlOffset()), point->getPointID()));
                }
            }
        }
    }
}


void CtiPointManager::loadPao(const long paoId, const Cti::CallSite cs)
{
    if( ! paoId )
    {
        return;
    }

    {
        coll_type::reader_lock_guard_t guard(getLock());

        if( _all_paoids_loaded || _paoids_loaded.count(paoId) )
        {
            return;
        }
    }

    if( DebugLevel & 0x00010000 )
    {
        CTILOG_DEBUG(dout, "Called from " << cs.func << ":" << cs.file << ":" << cs.line << " - refreshing points for paoid " << paoId);
    }

    refreshList(0, paoId);
}


CtiPointManager::ptr_type CtiPointManager::getPoint (LONG Pt, LONG pao)
{
    loadPao(pao, CALLSITE);

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

    std::transform(pname.begin(), pname.end(), pname.begin(), tolower);

    if(_smartMap.entries() == 0)
    {
        CTILOG_ERROR(dout, "There are no entries in the point manager list");
    }

    loadPao(pao, CALLSITE);

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
                    CTILOG_WARN(dout, "Device ID: "<< p->getDeviceID() <<" : "<< p->getName()<<" point is non-updated");
                }
            }
        }
    }

    return pRet;
}


void CtiPointManager::getEqualByPAO(long pao, vector<ptr_type> &points)
{
    loadPao(pao, CALLSITE);

    {
        coll_type::reader_lock_guard_t guard(getLock());

        multimap<long, long>::const_iterator itr         = _pao_pointids.lower_bound(pao),
                                             lower_bound = _pao_pointids.upper_bound(pao);

        for( ; itr != lower_bound; itr++ )
        {
            ptr_type p = getPoint(itr->second);

            if( p )
            {
                points.push_back(p);
            }
        }
    }
}


CtiPointManager::ptr_type CtiPointManager::getOffsetTypeEqual(LONG pao, INT Offset, CtiPointType_t Type)
{
    ptr_type p;
    ptr_type pRet;

    loadPao(pao, CALLSITE);

    {
        coll_type::reader_lock_guard_t guard(getLock());

        multimap<pao_offset_t, long>::const_iterator type_itr, upper_bound;

        type_itr    = _type_offsets.lower_bound(pao_offset_t(pao, Offset));
        upper_bound = _type_offsets.upper_bound(pao_offset_t(pao, Offset));

        for( ; type_itr != upper_bound; type_itr++ )
        {
            if( (p = getPoint(type_itr->second)) && (p->getType() == Type) )
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
                        CTILOG_WARN(dout, "Device ID: "<< p->getDeviceID() <<" : "<< p->getName()<<" point is non-updated");
                    }
                }
            }
        }
    }

    return pRet;
}

boost::optional<long> CtiPointManager::getIdForOffsetAndType(long pao, int offset, CtiPointType_t type)
{
    {
        coll_type::reader_lock_guard_t guard(getLock());

        multimap<pao_offset_t, long>::const_iterator type_itr, upper_bound;

        type_itr    = _type_offsets.lower_bound(pao_offset_t(pao, offset));
        upper_bound = _type_offsets.upper_bound(pao_offset_t(pao, offset));

        for( ; type_itr != upper_bound; type_itr++ )
        {
            if( const ptr_type p = getPoint(type_itr->second) )
            {
                if( p->getType() == type )
                {
                    return p->getPointID();
                }
            }
        }
    }

    //  not found in our local store, try hitting the DB
    static const char *sql =
        "SELECT pointid"
        " FROM Point"
        " WHERE paobjectid=?"
        " AND pointtype=?"
        " AND pointoffset=?";

    Cti::Database::DatabaseConnection conn;

    Cti::Database::DatabaseReader rdr(conn, sql);

    Cti::Database::executeCommand(rdr, __FILE__,  __LINE__);

    rdr << pao;
    rdr << desolvePointType(type);
    rdr << offset;

    if( rdr() )
    {
        long pointid;

        rdr >> pointid;

        return pointid;
    }

    return boost::none;
}

CtiPointManager::ptr_type CtiPointManager::getControlOffsetEqual(LONG pao, INT Offset)
{
    ptr_type p;
    ptr_type pRet;

    loadPao(pao, CALLSITE);

    {
        coll_type::reader_lock_guard_t guard(getLock());

        std::map<pao_offset_t, long>::const_iterator control_itr = _control_offsets.find(pao_offset_t(pao, Offset));

        if( control_itr != _control_offsets.end() )
        {
            if( p = getPoint(control_itr->second) )
            {
                if( p->getUpdatedFlag() )
                {
                    pRet = p;
                }
                else
                {
                    CTILOG_WARN(dout, "Device ID: "<< p->getDeviceID() <<" : "<< p->getName()<<" point is non-updated");
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
CtiPointManager::coll_type::lock_t &CtiPointManager::getLock() const  {  return _smartMap.getLock();         }


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
                CTILOG_ERROR(dout, "Unable to lock point mutex");

                break;
            }

            CTILOG_WARN(dout, "Unable to lock point mutex.  Will retry.. Last Acquired By TID: "<< static_cast<string>(getLock()) <<" Faddr: 0x"<< applyFun);

            guard.tryAcquire(30000);
        }
        #endif

        _smartMap.apply(applyFun,d);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


long CtiPointManager::getPAOIdForPointId(long pointid)
{
    ptr_type p = getPoint(pointid);

    if( p )  return p->getDeviceID();

    return -1;
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


int CtiPointManager::maxPointsAllowed()
{
    return gConfigParms.getValueAsInt("MAX_POINT_CACHE_SIZE", 100000);
}


void CtiPointManager::processExpired()
{
    coll_type::writer_lock_guard_t guard(getLock());

    lru_guard_t lru_guard(_lru_mux);

    //default cache size is 100k, or 100*1000, or 100000
    int points_allowed  = maxPointsAllowed();
    int expiration_time = gConfigParms.getValueAsInt("MIN_POINT_CACHE_TIME", 300);

    time_t expiration_index = (currentTime() - expiration_time) / 10;

    //  iterate through until a timeslice exceeds the max cache size
    auto itr =
        boost::range::find_if(
            _lru_timeslices,
            [&points_allowed](lru_timeslice_map::value_type &kv)
            {
                return (points_allowed -= kv.second.size()) <= 0;
            });

    if( itr == _lru_timeslices.end() )
    {
        return;
    }

    //  if we're not already into the expired timeslices, find the first expired entry
    if( _lru_timeslices.key_comp()(itr->first, expiration_index) )
    {
        itr = _lru_timeslices.upper_bound(expiration_index);
    }

    set<long> expired_pointids;

    //  go through the remaining timeslices and expire everything
    while( itr != _lru_timeslices.end() )
    {
        const auto &pointids = itr->second;

        expired_pointids.insert(pointids.begin(), pointids.end());

        itr = _lru_timeslices.erase(itr);
    }

    if( ! expired_pointids.empty() )
    {
        CTILOG_WARN(dout, "expiring "<< expired_pointids.size() <<" points");
    }

    //  erase all expired points
    for( const auto pointid : expired_pointids )
    {
        expire(pointid);

        _lru_points.erase(pointid);
    }
}

/**
 * Expires the given point ID.
 *
 * @param pid long
 */
void CtiPointManager::expire(long pid)
{
    //Calling smartMap.find here as getPoint can have side effects
    ptr_type point = _smartMap.find(pid);

    if(point)
    {
        //  protected by the writer_lock_guard in processExpired()
        _paoids_loaded.erase(point->getDeviceID());
        _all_paoids_loaded = false;
    }

    CtiPointManager::erase(pid);
}

/**
 * Prepares the point to be reloaded.
 *
 * @param pid long
 */
void CtiPointManager::refresh(long pid)
{
    CtiPointManager::erase(pid);
}

/**
 * Completely erases the point. Should be used on delete.
 *
 * @param pid long
 */
void CtiPointManager::erase(long pid)
{
    ptr_type deleted = _smartMap.remove(pid);

    removePoint(deleted);
}


void CtiPointManager::removePoint(ptr_type pTempCtiPoint)
{
    if( pTempCtiPoint )
    {
        //  If it's a status point with control, remove it from the control point lookup as well
        if( pTempCtiPoint->isStatus() )
        {
            CtiPointStatusSPtr pStatus = boost::static_pointer_cast<CtiPointStatus>(pTempCtiPoint);

            if( const boost::optional<CtiTablePointStatusControl> controlParameters = pStatus->getControlParameters() )
            {
                if( controlParameters->getControlOffset() )
                {
                    _control_offsets.erase(pao_offset_t(pTempCtiPoint->getDeviceID(), controlParameters->getControlOffset()));
                }
            }
        }

        erase_from_multimap(_type_offsets, pao_offset_t(pTempCtiPoint->getDeviceID(), pTempCtiPoint->getPointOffset()), pTempCtiPoint->getPointID());

        erase_from_multimap(_pao_pointids, pTempCtiPoint->getDeviceID(), pTempCtiPoint->getPointID());
    }
}

void CtiPointManager::erasePao(long paoId)
{
    // we use a seperate vector because the multimap will have items removed
    std::vector<long> point_ids;

    std::transform( _pao_pointids.lower_bound(paoId),
                    _pao_pointids.upper_bound(paoId),
                    std::back_inserter(point_ids),
                    boost::bind(&std::multimap<long,long>::value_type::second,_1) ); // does a select2nd

    for( long pid : point_ids )
    {
        erase(pid);
    }
}

