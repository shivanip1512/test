#include "precompiled.h"

#include <boost/mem_fn.hpp>

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
#include <boost/range/algorithm/set_algorithm.hpp>

using namespace std;

using Cti::Logging::Map::operator<<;

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            Cti::identifyLibrary(CompileInfo);

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
            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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
            else if( DebugLevel & DEBUGLEVEL_MGR_POINT )
            {
                CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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
            else if( DebugLevel & DEBUGLEVEL_MGR_POINT )
            {
                CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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
            else if( DebugLevel & DEBUGLEVEL_MGR_POINT )
            {
                CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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
            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
            {
                CTILOG_DEBUG(dout, "DB read: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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
            else if( DebugLevel & DEBUGLEVEL_MGR_POINT )
            {
                CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
            }

            errors |= refreshPoints(pointIdsFound, rdr);

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
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

    const auto max_ids_per_select = gConfigParms.getValueAsInt("MAX_IDS_PER_SELECT", 256);

    vector<long> ids_to_load;

    if( paoids )
    {
        coll_type::reader_lock_guard_t guard(_smartMap.getLock());

        boost::range::set_difference(
            id_list,
            _paoids_loaded,
            std::back_inserter(ids_to_load));
    }
    else
    {
        //  we will need to get smarter about what points are and aren't loaded
        ids_to_load.assign(id_list.begin(), id_list.end());
    }

    const string keyColString = paoids ? "paobjectid IN ":"pointid IN ";

    static const std::array<std::string, 5> baseLoadingSql{
        //  ACCUMULATOR points
        CtiPointAccumulator::getSQLCoreStatement()
            + " AND PT.",

        //  ANALOG points
        CtiPointAnalog::getSQLCoreStatement()
            + " WHERE PT.",

        //  CALC points
        CtiPointNumeric::getSQLCoreStatement()
            + " AND (upper (PT.pointtype) = 'CALCULATED' OR upper (PT.pointtype) = 'CALCANALOG')"
                " AND PT.",

        //  STATUS points
        CtiPointStatus::getSQLCoreStatement()
            + " WHERE"
                " (upper (PT.pointtype) = 'STATUS' OR upper (PT.pointtype) = 'CALCSTATUS') "
                " AND PT.",

        //  SYSTEM points
        CtiPointBase::getSQLCoreStatement()
            + " WHERE upper (PT.pointtype) = 'SYSTEM' AND PT."
    };

    const auto buildInList = [](const size_t chunk_size) 
    {
        std::string in_list(chunk_size * 2 + 1, '?');  //  N '?' + N-1 ',' + '(' + ')' = N * 2 + 1

        *in_list.begin() = '(';
        *in_list.rbegin() = ')';

        for( auto idx = 1; idx < chunk_size; ++idx )
        {
            in_list[idx * 2] = ',';  //  replace index 2, 4, 6, etc:  "(?,?,?,?..."
        }

        return in_list;
    };

    auto itr = ids_to_load.cbegin(),
         end = ids_to_load.cend();

    bool errors = false;

    auto full_chunks = ids_to_load.size() / max_ids_per_select;
    
    auto chunk_size = full_chunks ? max_ids_per_select : ids_to_load.size();
    auto in_list_placeholders = buildInList(chunk_size);

    while( itr != end )
    {
        for( const auto &sql : baseLoadingSql )
        {
            std::set<long> pointIdsFound;  //  placeholder

            rdr.setCommandText(sql + keyColString + in_list_placeholders);

            std::for_each(itr, itr + chunk_size, [&rdr](const long id) 
            {
                rdr << id;
            });

            if( DebugLevel & DEBUGLEVEL_MGR_POINT )
            {
                CTILOG_DEBUG(dout, rdr.asString());
            }

            rdr.execute();
            errors |= refreshPoints(pointIdsFound, rdr);
        }

        itr += chunk_size;

        if( ! --full_chunks )
        {
            chunk_size = ids_to_load.size() % max_ids_per_select;
            in_list_placeholders = buildInList(chunk_size);
        }
    }

    //  No errors, so note down which PAOs were fully loaded
    if( paoids && ! errors )
    {
        coll_type::writer_lock_guard_t guard(_smartMap.getLock());

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
        switch( point->getType() )
        {
            case StatusPointType:
            {
                CtiPointStatus & status = static_cast<CtiPointStatus &>(*point);

                if( const auto controlParameters = status.getControlParameters() )
                {
                    if( controlParameters->getControlOffset() )
                    {
                        _control_offsets.emplace(pao_offset_t(status.getDeviceID(), controlParameters->getControlOffset()), status.getPointID());
                    }
                }
                return;
            }
            case AnalogPointType:
            {
                CtiPointAnalog & analog = static_cast<CtiPointAnalog &>(*point);

                if( const auto controlParameters = analog.getControl() )
                {
                    if( controlParameters->getControlOffset() )
                    {
                        _analog_outputs.emplace(pao_offset_t(analog.getDeviceID(), controlParameters->getControlOffset()), analog.getPointID());
                    }
                }
                else if( analog.getPointOffset() > CtiPointAnalog::AnalogOutputOffset )
                {
                    _analog_outputs.emplace(pao_offset_t(analog.getDeviceID(), analog.getPointOffset() % CtiPointAnalog::AnalogOutputOffset), analog.getPointID());
                }
                return;
            }
        }
    }
}


void CtiPointManager::loadPao(const long paoId, const Cti::CallSite callSite)
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

    if( DebugLevel & DEBUGLEVEL_MGR_POINT )
    {
        CTILOG_DEBUG(dout, "Called from " << callSite << " - refreshing points for paoid " << paoId);
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

CtiPointManager::ptr_type CtiPointManager::getEqualByName(LONG pao, const string& pname)
{
    if(_smartMap.entries() == 0)
    {
        CTILOG_ERROR(dout, "There are no entries in the point manager list");
    }

    vector<ptr_type> paoPoints;

    getEqualByPAO(pao, paoPoints);

    for( auto& p : paoPoints )
    {
        if( ciStringEqual(p->getName(), pname) )
        {
            updateAccess(p->getPointID());

            return p;
        }
    }

    return nullptr;
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

    Cti::Database::executeCommand(rdr, CALLSITE);

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
    loadPao(pao, CALLSITE);

    return lookupByPaoOffset(pao, Offset, _control_offsets);
}

CtiPointManager::ptr_type CtiPointManager::getAnalogOutput(LONG pao, INT Offset)
{
    loadPao(pao, CALLSITE);

    return lookupByPaoOffset(pao, Offset, _analog_outputs);
}

CtiPointManager::ptr_type CtiPointManager::lookupByPaoOffset(long pao, int offset, std::map<pao_offset_t, long>& paoOffsetLookup)
{
    coll_type::reader_lock_guard_t guard(getLock());

    if( auto pointId = Cti::mapFind(paoOffsetLookup, pao_offset_t(pao, offset)) )
    {
        if( auto p = getPoint(*pointId) )
        {
            if( p->getUpdatedFlag() )
            {
                return p;
            }
            else
            {
                CTILOG_WARN(dout, "Device ID: " << p->getDeviceID() << " : " << p->getName() << " point is non-updated");
            }
        }
    }

    return nullptr;
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
    _analog_outputs.clear();

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
                if( const auto controlOffset = controlParameters->getControlOffset() )
                {
                    _control_offsets.erase(pao_offset_t(pTempCtiPoint->getDeviceID(), controlOffset));
                }
            }
        }
        else if( pTempCtiPoint->getType() == AnalogPointType )
        {
            const auto & analog = static_cast<const CtiPointAnalog&>(*pTempCtiPoint);

            if( auto control = analog.getControl() )
            {
                if( const auto offset = control->getControlOffset() )
                {
                    _analog_outputs.erase(pao_offset_t(pTempCtiPoint->getDeviceID(), offset));
                }
                else if( analog.getPointOffset() > CtiPointAnalog::AnalogOutputOffset )
                {
                    _analog_outputs.erase(pao_offset_t(pTempCtiPoint->getDeviceID(), analog.getPointOffset() % CtiPointAnalog::AnalogOutputOffset));
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

