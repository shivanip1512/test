#include "precompiled.h"

#include "ccsubstationbusstore.h"
#include "ControlStrategy.h"
#include "ccsubstationbus.h"
#include "ccsubstation.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "ccstate.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "logger.h"
#include "msg_dbchg.h"
#include "msg_signal.h"
#include "capcontroller.h"
#include "mgr_holiday.h"
#include "mgr_season.h"
#include "utility.h"
#include "thread_monitor.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_transaction.h"
#include "database_writer.h"
#include "database_util.h"
#include "database_exceptions.h"
#include "PointResponse.h"
#include "PointResponseDao.h"
#include "ThreadStatusKeeper.h"
#include "ExecutorFactory.h"
#include "mgr_config.h"
#include "std_helper.h"
#include "config_data_dnp.h"
#include "config_helpers.h"
#include "config_exceptions.h"

#include <boost/range/algorithm/for_each.hpp>
#include <boost/range/adaptor/map.hpp>
#include <boost/range/adaptor/indirected.hpp>

#include <boost/algorithm/string/join.hpp>
#include <boost/algorithm/string/predicate.hpp>
#include <boost/assign/list_of.hpp>

#include <boost/filesystem.hpp>
#include <boost/filesystem/fstream.hpp>
#include <boost/range/algorithm_ext/erase.hpp>

#define HOURLY_RATE 3600

extern unsigned long _CC_DEBUG;
extern unsigned long _DB_RELOAD_WAIT;
extern unsigned long _MAX_KVAR;
extern unsigned long _MAX_KVAR_TIMEOUT;
extern unsigned long _OP_STATS_USER_DEF_PERIOD;
extern unsigned long _OP_STATS_REFRESH_RATE;
extern std::string _MAXOPS_ALARM_CAT;
extern long _MAXOPS_ALARM_CATID;
extern bool _OP_STATS_DYNAMIC_UPDATE;
extern double _IVVC_DEFAULT_DELTA;
extern long  _VOLT_REDUCTION_SYSTEM_POINTID;

using namespace std;
using namespace Cti::CapControl;

using Database::DatabaseDaoFactory;
using Cti::ThreadStatusKeeper;

using Cti::CapControl::PaoIdVector;
using Cti::CapControl::PointIdVector;

CtiTime timeSaver;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCSubstationBusStore::CtiCCSubstationBusStore() :
    CtiCCSubstationBusStore(&CtiCCSubstationBusStore::dumpAllDynamicDataImpl)
{
}

CtiCCSubstationBusStore::CtiCCSubstationBusStore(Cti::Test::use_in_unit_tests_only&) :
    CtiCCSubstationBusStore(&CtiCCSubstationBusStore::noOp)
{
}

CtiCCSubstationBusStore::CtiCCSubstationBusStore(DynamicDumpFn dynamicDumpFn) :
    _dynamicDumpFn(dynamicDumpFn),
    _isvalid(false),
    _attributeService(new AttributeService),
    _reloadfromamfmsystemflag(false),
    _lastdbreloadtime(CtiTime(CtiDate(1,1,1990),0,0,0)),
    _lastindividualdbreloadtime(CtiTime(CtiDate(1,1,1990),0,0,0)),
    _strategyManager        ( make_unique<StrategyManager>( make_unique<StrategyDBLoader>() ) ),
    _zoneManager            ( make_unique<ZoneDBLoader>() ),
    _voltageRegulatorManager( make_unique<Cti::CapControl::VoltageRegulatorManager>( make_unique<VoltageRegulatorDBLoader>() ) )
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    _ccSubstationBuses = new CtiCCSubstationBus_vec;
    _ccCapBankStates = new CtiCCState_vec;
    _ccGeoAreas = new CtiCCArea_vec;
    _ccSpecialAreas = new CtiCCSpArea_vec;

    _linkStatusPointId = 0;
    _linkStatusFlag = STATE_OPENED;
    _linkDropOutTime = CtiTime();

    _voltReductionSystemDisabled = false;

    _pointDataHandler.setPointDataListener(this);
    _daoFactory = boost::shared_ptr<DaoFactory>(new DatabaseDaoFactory());

    _voltageRegulatorManager->setAttributeService( _attributeService.get() );
    _voltageRegulatorManager->setPointDataHandler( & _pointDataHandler );

    Cti::ConfigManager::initialize();
}

/*--------------------------------------------------------------------------
    Destrutor
-----------------------------------------------------------------------------*/
CtiCCSubstationBusStore::~CtiCCSubstationBusStore()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    shutdown();
}

void CtiCCSubstationBusStore::startThreads()
{
    CTILOG_DEBUG(dout, "Starting the SubstationBusStore");

    _resetThr  = boost::thread( &CtiCCSubstationBusStore::doResetThr, this );
    _amfmThr   = boost::thread( &CtiCCSubstationBusStore::doAMFMThr, this );
    _opStatThr = boost::thread( &CtiCCSubstationBusStore::doOpStatsThr, this );

    CTILOG_DEBUG(dout, "SubstationBusStore is running");
}

void CtiCCSubstationBusStore::stopThreads()
{
    try
    {
        CTILOG_DEBUG(dout, "Stopping the SubstationBusStore");

        _resetThr.interrupt();
        _amfmThr.interrupt();
        _opStatThr.interrupt();

        if ( ! _resetThr.timed_join( boost::posix_time::seconds( 30 ) ) )
        {
            CTILOG_WARN( dout, "SubstationBusStore reset thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _resetThr.native_handle(), EXIT_SUCCESS );
        }

        if ( ! _amfmThr.timed_join( boost::posix_time::seconds( 30 ) ) )
        {
            CTILOG_WARN( dout, "SubstationBusStore AMFM thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _amfmThr.native_handle(), EXIT_SUCCESS );
        }

        if ( ! _opStatThr.timed_join( boost::posix_time::seconds( 30 ) ) )
        {
            CTILOG_WARN( dout, "SubstationBusStore OpStats thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _opStatThr.native_handle(), EXIT_SUCCESS );
        }

        CTILOG_DEBUG(dout, "SubstationBusStore is stopped");
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*
    This function is called at startup to evaluate the connection to and the ability to read data from
        the database.  We try to grab a count of all the Yukon PAOs, if the connection or query fails
        we catch any exception and return false.  If we successfully read a number we make sure it's at least
        non-zero, since the default created SYSTEM device will always be present.
*/
bool CtiCCSubstationBusStore::testDatabaseConnectivity() const
{
    static const std::string sql = "SELECT COUNT(*) AS PAOCount FROM YukonPAObject";

    try
    {
        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader     rdr( connection, sql );

        rdr.execute();

        if ( rdr() )
        {
            const long paoCount = rdr["PAOCount"].as<long>();

            return paoCount > 0;
        }
    }
    catch ( Cti::Database::DatabaseException & ex )
    {
        CTILOG_EXCEPTION_ERROR( dout, ex, "Unable to access database." );
    }

    return false;
}

/* 
    This function is the location where all the DBChange messages and global resets that need to
        occur are actually processed and the database is hit.
*/
void CtiCCSubstationBusStore::processAnyDBChangesOrResets( const CtiTime & rightNow )
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    if ( ! _isvalid && rightNow >= _lastdbreloadtime + 30 )
    {
        // is not valid and has been at least 30 seconds from the last db reload
        //  so we don't do this a bunch of times in a row on multiple updates
        
        reset();
        clearDBReloadList();
    }
    else if ( rightNow >= _lastindividualdbreloadtime + _DB_RELOAD_WAIT )   // _DB_RELOAD_WAIT defaults to 5 seconds
    {
        if ( ! _isvalid )       // if the whole thing is invalid just reload all from scratch
        {
            reset();
            clearDBReloadList();
        }
        else                    // else process the individual dbchange items in the list
        {
            checkDBReloadList();
        }
    }
    else if ( _2wayFlagUpdate )
    {
        dumpAllDynamicData();
        _2wayFlagUpdate = false;
    }

    if ( _reloadfromamfmsystemflag )
    {
        checkAMFMSystemForUpdates();
    }

    setStoreRecentlyReset( false );
}

/*---------------------------------------------------------------------------
    getSubstationBuses

    Returns a CtiCCSubstationBus_vec of CtiCCSubstationBuses
---------------------------------------------------------------------------*/
CtiCCSubstationBus_vec* CtiCCSubstationBusStore::getCCSubstationBuses()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    return _ccSubstationBuses;
}

/*---------------------------------------------------------------------------
    getCCGeoAreas

    Returns a CtiCCGeoArea_vec of CtiCCGeoAreas
---------------------------------------------------------------------------*/
CtiCCArea_vec* CtiCCSubstationBusStore::getCCGeoAreas()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    return _ccGeoAreas;
}

/*---------------------------------------------------------------------------
    getCCSpecialAreas

    Returns a CtiCCSpecialArea_vec of CtiCCAreas
---------------------------------------------------------------------------*/
CtiCCSpArea_vec* CtiCCSubstationBusStore::getCCSpecialAreas()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    return _ccSpecialAreas;
}
/*---------------------------------------------------------------------------
    getCCSubstations

    Returns a CtiCCSubstation_vec of CtiCCSubstations
---------------------------------------------------------------------------*/
const CtiCCSubstation_vec& CtiCCSubstationBusStore::getCCSubstations()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    return _ccSubstations;
}


/*---------------------------------------------------------------------------
    getCCCapBankStates

    Returns a CtiCCState_vec* of CtiCCStates
---------------------------------------------------------------------------*/
CtiCCState_vec* CtiCCSubstationBusStore::getCCCapBankStates()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    return _ccCapBankStates;
}


/*----------------------------------------------------------------------------
  findSubBusByPointID

  Attempts to locate a subbus given a point id.
  The given point could be any of the points.
  Returns false if no subbus is found.
  This member exists mostly for efficiency in updating subbuses when point
  data shows up.
----------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::findSpecialAreaByPointID(long point_id, PointIdToSpecialAreaMultiMap::iterator &begin, PointIdToSpecialAreaMultiMap::iterator &end)
{
    begin = _pointid_specialarea_map.lower_bound(point_id);
    end   = _pointid_specialarea_map.upper_bound(point_id);

    return begin != end;
}

bool CtiCCSubstationBusStore::findAreaByPointID(long point_id, PointIdToAreaMultiMap::iterator &begin, PointIdToAreaMultiMap::iterator &end)
{
    begin = _pointid_area_map.lower_bound(point_id);
    end   = _pointid_area_map.upper_bound(point_id);

    return begin != end;
}

bool CtiCCSubstationBusStore::findSubstationByPointID(long point_id, PointIdToSubstationMultiMap::iterator &begin, PointIdToSubstationMultiMap::iterator &end)
{
    begin = _pointid_station_map.lower_bound(point_id);
    end   = _pointid_station_map.upper_bound(point_id);

    return begin != end;
}

bool CtiCCSubstationBusStore::findSubBusByPointID(long point_id, PointIdToSubBusMultiMap::iterator &begin, PointIdToSubBusMultiMap::iterator &end)
{
    begin = _pointid_subbus_map.lower_bound(point_id);
    end   = _pointid_subbus_map.upper_bound(point_id);

    return begin != end;
}


bool CtiCCSubstationBusStore::findFeederByPointID(long point_id, PointIdToFeederMultiMap::iterator &begin, PointIdToFeederMultiMap::iterator &end)
{
    begin = _pointid_feeder_map.lower_bound(point_id);
    end   = _pointid_feeder_map.upper_bound(point_id);

    return begin != end;
}

bool CtiCCSubstationBusStore::findCapBankByPointID(long point_id, PointIdToCapBankMultiMap::iterator &begin, PointIdToCapBankMultiMap::iterator &end)
{
    begin = _pointid_capbank_map.lower_bound(point_id);
    end   = _pointid_capbank_map.upper_bound(point_id);

    return begin != end;
}

pair<PaoIdToPointIdMultiMap::iterator,PaoIdToPointIdMultiMap::iterator> CtiCCSubstationBusStore::getSubsWithAltSubID(int altSubId)
{
    return _altsub_sub_idmap.equal_range(altSubId);
}

CtiCCAreaPtr CtiCCSubstationBusStore::findAreaByPAObjectID(long paobject_id)
{
    return Cti::mapFindOrDefault( _paobject_area_map, paobject_id, nullptr );
}
CtiCCSpecialPtr CtiCCSubstationBusStore::findSpecialAreaByPAObjectID(long paobject_id)
{
    return Cti::mapFindOrDefault( _paobject_specialarea_map, paobject_id, nullptr );
}
CtiCCSubstationPtr CtiCCSubstationBusStore::findSubstationByPAObjectID(long paobject_id)
{
    return Cti::mapFindPtr( _paobject_substation_map, paobject_id );
}
CtiCCSubstationBusPtr CtiCCSubstationBusStore::findSubBusByPAObjectID(long paobject_id)
{
    return Cti::mapFindOrDefault( _paobject_subbus_map, paobject_id, nullptr );
}

CtiCCSubstationBusPtr CtiCCSubstationBusStore::findSubBusByCapBankID(long cap_bank_id)
{
    if ( long subBusId = findSubBusIDbyCapBankID( cap_bank_id ) )
    {
        return findSubBusByPAObjectID( subBusId );
    }

    return nullptr;
}

CtiCCFeederPtr CtiCCSubstationBusStore::findFeederByPAObjectID(long paobject_id)
{
    return Cti::mapFindOrDefault( _paobject_feeder_map, paobject_id, nullptr );
}
CtiCCCapBankPtr CtiCCSubstationBusStore::findCapBankByPAObjectID(long paobject_id)
{
    return Cti::mapFindOrDefault( _paobject_capbank_map, paobject_id, nullptr );
}

long CtiCCSubstationBusStore::findAreaIDbySubstationID(long substationId)
{
    return Cti::mapFindOrDefault( _substation_area_map, substationId, 0 );
}
bool CtiCCSubstationBusStore::findSpecialAreaIDbySubstationID(long substationId, ChildToParentMultiMap::iterator &begin, ChildToParentMultiMap::iterator &end)
{
    begin = _substation_specialarea_map.lower_bound(substationId);
    end   = _substation_specialarea_map.upper_bound(substationId);
    return begin != end;
}

long CtiCCSubstationBusStore::findSubstationIDbySubBusID(long subBusId)
{
    return Cti::mapFindOrDefault( _subbus_substation_map, subBusId, 0 );
}
long CtiCCSubstationBusStore::findSubBusIDbyFeederID(long feederId)
{
    return Cti::mapFindOrDefault( _feeder_subbus_map, feederId, 0 );
}
long CtiCCSubstationBusStore::findSubBusIDbyCapBankID(long capBankId)
{
    return Cti::mapFindOrDefault( _capbank_subbus_map, capBankId, 0 );
}
long CtiCCSubstationBusStore::findFeederIDbyCapBankID(long capBankId)
{
    return Cti::mapFindOrDefault( _capbank_feeder_map, capBankId, 0 );
}
long CtiCCSubstationBusStore::findCapBankIDbyCbcID(long cbcId)
{
    return Cti::mapFindOrDefault( _cbc_capbank_map, cbcId, 0 );
}

void CtiCCSubstationBusStore::addAreaToPaoMap(CtiCCAreaPtr area)
{
    _paobject_area_map[area->getPaoId()] = area;
}
void CtiCCSubstationBusStore::addSubstationToPaoMap(CtiCCSubstationUnqPtr&& station, Cti::Test::use_in_unit_tests_only&)
{
    _paobject_substation_map.emplace(station->getPaoId(), std::move(station));
}
void CtiCCSubstationBusStore::addSubBusToPaoMap(CtiCCSubstationBusPtr bus)
{
    _paobject_subbus_map[bus->getPaoId()] = bus;
}
void CtiCCSubstationBusStore::addSubBusToAltBusMap(CtiCCSubstationBusPtr bus)
{
    _altsub_sub_idmap.insert(make_pair(bus->getAltDualSubId(), bus->getPaoId()));
}
void CtiCCSubstationBusStore::addFeederToPaoMap(CtiCCFeederPtr feeder)
{
    _paobject_feeder_map[feeder->getPaoId()] = feeder;
}
void CtiCCSubstationBusStore::addCapBankToCBCMap(CtiCCCapBankPtr capbank)
{
    _cbc_capbank_map[capbank->getControlDeviceId()] = capbank->getPaoId();
}

std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBusStore::getSubBusesByAreaId(int areaId)
{
    std::vector<CtiCCSubstationBusPtr> subBuses;
    CtiCCAreaPtr area = findAreaByPAObjectID(areaId);

    if (area == NULL)
    {
        return subBuses;
    }

    for each(long stationId in area->getSubstationIds())
    {
        CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
        if (station == NULL)
        {
            continue;
        }

        for each(int subId in station->getCCSubIds())
        {
            CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subId);
            if (subBus == NULL)
            {
                continue;
            }
            subBuses.push_back(subBus);
        }
    }
    return subBuses;
}

std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBusStore::getSubBusesBySpecialAreaId(int areaId)
{
    std::vector<CtiCCSubstationBusPtr> subBuses;
    CtiCCSpecialPtr area = findSpecialAreaByPAObjectID(areaId);

    if (area == NULL)
    {
        return subBuses;
    }

    for each(long stationId in area->getSubstationIds())
    {
        CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
        if (station == NULL)
        {
            continue;
        }

        for each(int subId in station->getCCSubIds())
        {
            CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subId);
            if (subBus == NULL)
            {
                continue;
            }
            subBuses.push_back(subBus);
        }
    }
    return subBuses;
}

std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBusStore::getSubBusesByStationId(int stationId)
{
    std::vector<CtiCCSubstationBusPtr> subBuses;

    CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
    if (station == NULL)
    {
        return subBuses;
    }
    for each(int subId in station->getCCSubIds())
    {
        CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subId);
        if (subBus == NULL)
        {
            continue;
        }
        subBuses.push_back(subBus);
    }
    return subBuses;
}

std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBusStore::getSubBusesByFeederId(int feederId)
{
    std::vector<CtiCCSubstationBusPtr> subBuses;

    CtiCCFeederPtr feeder = findFeederByPAObjectID(feederId);
    if (feeder == NULL)
    {
        return subBuses;
    }

    CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(feeder->getParentId());
    if (subBus != NULL)
    {
        subBuses.push_back(subBus);
    }

    return subBuses;
}

std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBusStore::getSubBusesByCapControlByIdAndType(int paoId, CapControlType type)
{
    std::vector<CtiCCSubstationBusPtr> subBuses;

    switch(type)
    {
        case SpecialArea:
        {
            subBuses = getSubBusesBySpecialAreaId(paoId);
            break;
        }
        case Area:
        {
            subBuses = getSubBusesByAreaId(paoId);
            break;
        }
        case Substation:
        {
            subBuses = getSubBusesByStationId(paoId);
            break;
        }
        case SubBus:
        {
            CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(paoId);
            if (subBus != NULL)
            {
                subBuses.push_back(subBus);
            }
            break;
        }
        case Feeder:
        {
            subBuses = getSubBusesByFeederId(paoId);
            break;
        }
        case CapBank:
        default:
        {
            break;
        }
    }

    return subBuses;
}

CapBankList CtiCCSubstationBusStore::getCapBanksByPaoId(int paoId)
{
    CapControlType type = determineTypeById(paoId);
    return getCapBanksByPaoIdAndType(paoId,type);
}

CtiCCSubstationBus_vec CtiCCSubstationBusStore::getAllSubBusesByIdAndType(int paoId, CapControlType type)
{
    CtiCCSubstationBus_vec buses;
    switch (type)
    {
        case SubBus:
        {
            CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(paoId);
            if (subBus == NULL)
            {
                break;
            }
            buses.push_back(subBus);
            break;
        }
        case Substation:
        {
            CtiCCSubstationPtr station = findSubstationByPAObjectID(paoId);
            if (station == NULL)
            {
                break;
            }

            for each(int subId in station->getCCSubIds())
            {
                CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subId);
                if (subBus == NULL)
                {
                    continue;
                }
                buses.push_back(subBus);
            }
            break;
        }
        case Area:
        {
            CtiCCAreaPtr area = findAreaByPAObjectID(paoId);
            if (area == NULL)
            {
                break;
            }

            for each(long stationId in area->getSubstationIds())
            {
                CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
                if (station == NULL)
                {
                    continue;
                }

                for each(int subId in station->getCCSubIds())
                {
                    CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subId);
                    if (subBus == NULL)
                    {
                        continue;
                    }

                    buses.push_back(subBus);
                }
            }
            break;
        }
        case SpecialArea:
        {
            CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(paoId);

            if (spArea == NULL || spArea->getDisableFlag())
            {
                break;
            }

            for each(long stationId in spArea->getSubstationIds())
            {
                CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
                if (station == NULL)
                {
                    continue;
                }

                for each(int subId in station->getCCSubIds())
                {
                    CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subId);
                    if (subBus == NULL)
                    {
                        continue;
                    }

                    buses.push_back(subBus);
                }
            }
            break;
        }
        case CapBank:
        case Feeder:
        case Undefined:
        default:
        {
            break;
        }
    }

    return buses;
}
CapBankList CtiCCSubstationBusStore::getCapBanksByPaoIdAndType(int paoId, CapControlType type)
{
    CapBankList banks;

    switch (type)
    {
        case CapBank:
        {
            CtiCCCapBankPtr bank = findCapBankByPAObjectID(paoId);
            if (bank == NULL)
            {
                break;
            }

            banks.push_back(bank);
            break;
        }
        case Feeder:
        {
            CtiCCFeederPtr feeder = findFeederByPAObjectID(paoId);
            if (feeder == NULL)
            {
                break;
            }

            CtiCCCapBank_SVector sBanks = feeder->getCCCapBanks();
            banks.insert(banks.end(),sBanks.get_container().begin(),sBanks.get_container().end());
            break;
        }
        case SubBus:
        {
            CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(paoId);
            if (subBus == NULL)
            {
                break;
            }

            CtiFeeder_vec feeders = subBus->getCCFeeders();
            for each(CtiCCFeederPtr feeder in feeders)
            {
                CtiCCCapBank_SVector sBanks = feeder->getCCCapBanks();
                banks.insert(banks.end(),sBanks.get_container().begin(),sBanks.get_container().end());
            }
            break;
        }
        case Substation:
        {
            CtiCCSubstationPtr station = findSubstationByPAObjectID(paoId);
            if (station == NULL)
            {
                break;
            }

            for each(int subId in station->getCCSubIds())
            {
                CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subId);
                if (subBus == NULL)
                {
                    continue;
                }
                CtiFeeder_vec feeders = subBus->getCCFeeders();

                for each(CtiCCFeederPtr feeder in feeders)
                {
                    CtiCCCapBank_SVector sBanks = feeder->getCCCapBanks();
                    banks.insert(banks.end(),sBanks.get_container().begin(),sBanks.get_container().end());
                }
            }
            break;
        }
        case Area:
        {
            CtiCCAreaPtr area = findAreaByPAObjectID(paoId);
            if (area == NULL)
            {
                break;
            }

            for each(long stationId in area->getSubstationIds())
            {
                CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
                if (station == NULL)
                {
                    continue;
                }

                for each(int subId in station->getCCSubIds())
                {
                    CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subId);
                    if (subBus == NULL)
                    {
                        continue;
                    }

                    CtiFeeder_vec feeders = subBus->getCCFeeders();
                    for each(CtiCCFeederPtr feeder in feeders)
                    {
                        CtiCCCapBank_SVector sBanks = feeder->getCCCapBanks();
                        banks.insert(banks.end(),sBanks.get_container().begin(),sBanks.get_container().end());
                    }
                }
            }
            break;
        }
        case SpecialArea:
        {
            CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(paoId);

            if (spArea == NULL || spArea->getDisableFlag())
            {
                break;
            }

            for each(long stationId in spArea->getSubstationIds())
            {
                CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
                if (station == NULL)
                {
                    break;
                }

                for each(int subId in station->getCCSubIds())
                {
                    CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subId);
                    if (subBus == NULL)
                    {
                        continue;
                    }

                    CtiFeeder_vec feeders = subBus->getCCFeeders();
                    for each(CtiCCFeederPtr feeder in feeders)
                    {
                        CtiCCCapBank_SVector sBanks = feeder->getCCCapBanks();
                        banks.insert(banks.end(),sBanks.get_container().begin(),sBanks.get_container().end());
                    }
                }
            }
            break;
        }
        case Undefined:
        default:
        {
            break;
        }
    }

    return banks;
}

CapControlType CtiCCSubstationBusStore::determineTypeById(int paoId)
{
    CapControlPao* ptr = NULL;
    ptr = findCapBankByPAObjectID(paoId);
    if (ptr != NULL)
    {
        return CapBank;
    }

    ptr = findFeederByPAObjectID(paoId);
    if (ptr != NULL)
    {
        return Feeder;
    }

    ptr = findSubBusByPAObjectID(paoId);
    if (ptr != NULL)
    {
        return SubBus;
    }

    ptr = findSubstationByPAObjectID(paoId);
    if (ptr != NULL)
    {
        return Substation;
    }

    ptr = findAreaByPAObjectID(paoId);
    if (ptr != NULL)
    {
        return Area;
    }

    ptr = findSpecialAreaByPAObjectID(paoId);
    if (ptr != NULL)
    {
        return SpecialArea;
    }

    try
    {
        VoltageRegulatorManager::SharedPtr  regulator = _voltageRegulatorManager->getVoltageRegulator(paoId);
        if ( regulator )    // I know the if() isn't needed because of the throw on the lookup.  It just seemed weird without it.
        {
            return VoltageRegulatorType;
        }
    }
    catch ( const NoVoltageRegulator & noRegulator )
    {
        // Swallow this exception
    }

    //Unknown
    return Undefined;
}

CapControlPointDataHandler& CtiCCSubstationBusStore::getPointDataHandler()
{
    return _pointDataHandler;
}

bool CtiCCSubstationBusStore::handlePointDataByPaoId( const int paoId, const CtiPointDataMsg & message )
{
    // Currently only Voltage Regulator types do this.
    // The idea will be all types do it, this can be accomplished once all objects inherit off CapControlPao

    bool handled = false;

    if (message.isA() == MSG_POINTDATA)
    {
        CapControlType type = determineTypeById(paoId);
        switch (type)
        {
            case VoltageRegulatorType:
                {
                    try
                    {
                        VoltageRegulatorManager::SharedPtr  regulator = _voltageRegulatorManager->getVoltageRegulator(paoId);

                        regulator->handlePointData( message );
                        handled = true;
                    }
                    catch ( const NoVoltageRegulator & noRegulator )
                    {
                        // Swallow this exception
                    }
                }
                break;
            default:
                break;
        }
    }

    return handled;
}



/*---------------------------------------------------------------------------
    dumpAllDynamicData

    Executes the dumpAllDynamicData function assigned at construction.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::dumpAllDynamicData()
{
    (this->*_dynamicDumpFn)();
}

/*---------------------------------------------------------------------------
    noOp

    A no-operation dumpAllDynamicData substitute for use in unit tests.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::noOp()
{
}

/*---------------------------------------------------------------------------
    dumpAllDynamicDataImpl

    Writes out the dynamic information for each of the substation buses.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::dumpAllDynamicDataImpl()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    /*{
        CTILOG_INFO(dout, "Store START dumpAllDynamicData");
    }*/
    try
    {

        if( _ccSubstationBuses->size() > 0 )
        {
            CtiTime currentDateTime = CtiTime();

            Cti::Database::DatabaseConnection conn;

            if ( ! conn.isValid() )
            {
                CTILOG_ERROR(dout, "Invalid Connection to Database");

                return;
            }

            for ( CtiCCAreaPtr currentCCArea : *_ccGeoAreas )
            {
                currentCCArea->dumpDynamicData( conn, currentDateTime );
            }
            for ( CtiCCSpecialPtr currentSpecial : *_ccSpecialAreas )
            {
                currentSpecial->dumpDynamicData( conn, currentDateTime );
            }
            for ( CtiCCSubstationPtr currentCCSubstation : _ccSubstations )
            {
                currentCCSubstation->dumpDynamicData( conn, currentDateTime );
            }

            for(long i=0;i<_ccSubstationBuses->size();i++)
            {
                CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];
                currentCCSubstationBus->dumpDynamicData(conn,currentDateTime);

                CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
                if( ccFeeders.size() > 0 )
                {
                    for(long j=0;j<ccFeeders.size();j++)
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                        currentFeeder->dumpDynamicData(conn,currentDateTime);
                        currentFeeder->getOperationStats().dumpDynamicData(conn,currentDateTime);

                        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                        if( ccCapBanks.size() > 0 )
                        {
                            for(long k=0;k<ccCapBanks.size();k++)
                            {
                                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                                currentCapBank->dumpDynamicData(conn,currentDateTime);
                                currentCapBank->getOperationStats().dumpDynamicData(conn,currentDateTime);
                                for each(CtiCCMonitorPointPtr monPoint in currentCapBank->getMonitorPoint())
                                {
                                    monPoint->dumpDynamicData(conn,currentDateTime);
                                }
                                //Update Point Responses
                                currentCapBank->dumpDynamicPointResponseData(conn);
                            }
                        }
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    /*{
        CTILOG_INFO(dout, "Store STOP dumpAllDynamicData");
    }*/
}



bool CtiCCSubstationBusStore::deleteCapControlMaps()
{
    bool wasAlreadyRunning = false;
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    try
    {
        setStoreRecentlyReset(true);
        _unsolicitedCapBanks.clear();
        _rejectedCapBanks.clear();
        for each (long orphanId in _orphanedFeeders)
        {
            CtiCCFeederPtr feeder = findFeederByPAObjectID(orphanId);
            if (feeder != NULL)
            {
                delete feeder;
            }
        }

        _orphanedFeeders.clear();

        for each (long orphanId in _orphanedCapBanks)
        {
            CtiCCCapBankPtr cap = findCapBankByPAObjectID(orphanId);
            if (cap != NULL)
            {
                delete cap;
            }
        }
        _orphanedCapBanks.clear();

        if ( _ccSubstationBuses->size() > 0 )
        {
            dumpAllDynamicData();
            wasAlreadyRunning = true;
        }
        delete_container( *_ccSubstationBuses );
        _ccSubstationBuses->clear();
        _ccSubstations.clear();
        delete_container( *_ccCapBankStates );
        _ccCapBankStates->clear();
        delete_container(*_ccGeoAreas);
        _ccGeoAreas->clear();
        delete_container(*_ccSpecialAreas);
        _ccSpecialAreas->clear();

        _paobject_area_map.clear();
        _paobject_specialarea_map.clear();
        _paobject_substation_map.clear();
        _paobject_subbus_map.clear();
        _paobject_feeder_map.clear();
        _paobject_capbank_map.clear();
        _pointid_area_map.clear();
        _pointid_specialarea_map.clear();
        _pointid_subbus_map.clear();
        _pointid_station_map.clear();
        _pointid_feeder_map.clear();
        _pointid_capbank_map.clear();
        _substation_area_map.clear();

        _subbus_substation_map.clear();
        _substation_specialarea_map.clear();
        _feeder_subbus_map.clear();
        _capbank_subbus_map.clear();
        _capbank_feeder_map.clear();
        _altsub_sub_idmap.clear();
        _cbc_capbank_map.clear();


        _pointID_to_pao.clear();
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed to clear old data for reload");
    }
    return wasAlreadyRunning;
}


bool CtiCCSubstationBusStore::getStoreRecentlyReset()
{
    return _storeRecentlyReset;
}
void CtiCCSubstationBusStore::setStoreRecentlyReset(bool flag)
{
    _storeRecentlyReset = flag;
}

/*---------------------------------------------------------------------------
    reset

    Reset attempts to read in all the substation buses from the database.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reset()
{
    bool wasAlreadyRunning = false;
    try
    {
        {
            CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
            {
                CTILOG_INFO(dout, "Obtained connection to the database..." << " - Resetting substation buses from database...");            
            }

            using boost::adaptors::map_values;
            using boost::adaptors::indirected;

            CTILOG_INFO( dout, "Unregistering for point changes." );
            {
                auto unregisterPaoPoints = 
                    [ ]( auto & x )
                    {
                        CtiCapController::getInstance()->unregisterPaoForPointUpdates( x );
                    };

                boost::for_each( _paobject_specialarea_map  | map_values | indirected, unregisterPaoPoints );
                boost::for_each( _paobject_area_map         | map_values | indirected, unregisterPaoPoints );
                boost::for_each( _paobject_substation_map   | map_values | indirected, unregisterPaoPoints );
                boost::for_each( _paobject_subbus_map       | map_values | indirected, unregisterPaoPoints );
                boost::for_each( _paobject_feeder_map       | map_values | indirected, unregisterPaoPoints );
                boost::for_each( _paobject_capbank_map      | map_values | indirected, unregisterPaoPoints );

                // Other

                if ( getLinkStatusPointId() > 0 )
                {
                    CtiCapController::getInstance()->unregisterPointIDsForPointUpdates( { getLinkStatusPointId() } );
                }

                if ( _VOLT_REDUCTION_SYSTEM_POINTID > 0 )
                {
                    CtiCapController::getInstance()->unregisterPointIDsForPointUpdates( { _VOLT_REDUCTION_SYSTEM_POINTID } );
                }
            }
            CTILOG_INFO( dout, "Done unregistering points." );

            wasAlreadyRunning = deleteCapControlMaps();

            /*************************************************************
            ******  Loading Strategies                              ******
            **************************************************************/
            if ( _CC_DEBUG )
            {
                CTILOG_DEBUG(dout, "Database reload beginning");
            }

            /*
                Refresh the schedules.
            */
            CtiSeasonManager::getInstance().refresh();
            CtiHolidayManager::getInstance().refresh();

            /*************************************************************
            ******  Loading Strategies                              ******
            **************************************************************/
            if ( !reloadStrategyFromDatabase(-1) )
            {
                _isvalid = false;
                return;
            }

            /***********************************************************
            *******  Loading Areas                               *******
            ************************************************************/
            reloadSpecialAreaFromDatabase(&_paobject_specialarea_map, &_pointid_specialarea_map, _ccSpecialAreas);

            reloadAreaFromDatabase(0, &_paobject_area_map, &_pointid_area_map, _ccGeoAreas);

            /***********************************************************
            *******  Loading Substations                         *******
            ************************************************************/

            reloadSubstationFromDatabase(0, &_paobject_substation_map, &_paobject_area_map, &_paobject_specialarea_map,
                                         &_pointid_station_map,
                                         &_substation_area_map, &_substation_specialarea_map, &_ccSubstations);


            /***********************************************************
            *******  Loading SubBuses                            *******
            ************************************************************/

            reloadSubBusFromDatabase(0, &_paobject_subbus_map, &_paobject_substation_map,
                                     &_pointid_subbus_map, &_altsub_sub_idmap, &_subbus_substation_map, _ccSubstationBuses);

            /***********************************************************
            *******  Loading Feeders                             *******
            ************************************************************/

            if (_ccSubstationBuses->size() > 0)
            {
                {
                    reloadFeederFromDatabase(0, &_paobject_feeder_map,
                                             &_paobject_subbus_map, &_pointid_feeder_map,
                                             &_feeder_subbus_map);//, &temp_feeder_area_map );
                }

               /************************************************************
                ********    Loading Cap Banks                       ********
                ************************************************************/
                {
                    reloadCapBankFromDatabase(0, &_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map,
                                          &_pointid_capbank_map, &_capbank_subbus_map,
                                          &_capbank_feeder_map, &_feeder_subbus_map, &_cbc_capbank_map );
                }

                /************************************************************
                ********    Loading Monitor Points                   ********
                ************************************************************/
                {
                    reloadMonitorPointsFromDatabase(0, &_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map,
                                          &_pointid_capbank_map, &_pointid_subbus_map);
                }


            }
            else
            {
                CTILOG_WARN(dout, "No Substations");
            }

            /*************************************************************
            ******  Loading Voltage Regulators                 ******
            **************************************************************/
            if ( ! reloadVoltageRegulatorFromDatabase( -1 ) )
            {
                _isvalid = false;
                return;
            }

            /*************************************************************
            ******  Loading Zones                              ******
            **************************************************************/
            if ( ! reloadZoneFromDatabase( -1 ) )
            {
                _isvalid = false;
                return;
            }

            /************************************************************
             ********    Loading Cap Banks States                ********
             ************************************************************/
            {
                reloadMiscFromDatabase();
            }
            /************************************************************
             ********    Loading Operation Statistics            ********
             ************************************************************/
            {
                 reloadOperationStatsFromDatabase(0,&_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map,
                                                       &_paobject_substation_map,
                                                       &_paobject_area_map,
                                                      &_paobject_specialarea_map);
            }

            CTILOG_INFO( dout, "Registering for point changes." );
            {
                auto registerPaoPoints = 
                    [ ]( auto & x )
                    {
                        CtiCapController::getInstance()->registerPaoForPointUpdates( x );
                    };

                boost::for_each( _paobject_specialarea_map  | map_values | indirected, registerPaoPoints );
                boost::for_each( _paobject_area_map         | map_values | indirected, registerPaoPoints );
                boost::for_each( _paobject_substation_map   | map_values | indirected, registerPaoPoints );
                boost::for_each( _paobject_subbus_map       | map_values | indirected, registerPaoPoints );
                boost::for_each( _paobject_feeder_map       | map_values | indirected, registerPaoPoints );
                boost::for_each( _paobject_capbank_map      | map_values | indirected, registerPaoPoints );

                // Other

                if ( getLinkStatusPointId() > 0 )
                {
                    CtiCapController::getInstance()->registerPointIDsForPointUpdates( { getLinkStatusPointId() } );
                }

                if ( _VOLT_REDUCTION_SYSTEM_POINTID > 0 )
                {
                    CtiCapController::getInstance()->registerPointIDsForPointUpdates( { _VOLT_REDUCTION_SYSTEM_POINTID } );
                }
            }
            CTILOG_INFO( dout, "Done registering for point changes." );

            /************************************************************
             ********    Loading Orphans                         ********
             ************************************************************/
            try
            {
               locateOrphans(&_orphanedCapBanks, &_orphanedFeeders, _paobject_capbank_map, _paobject_feeder_map,
                             _capbank_feeder_map, _feeder_subbus_map);
            }
            catch (...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            if ( _CC_DEBUG )
            {
                CTILOG_DEBUG(dout, "Database reload end, done Loading values into Cap Control");
            }

            _isvalid = true;
            _2wayFlagUpdate = false;
        }


    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    try
    {
        CTILOG_INFO(dout, "Store reset.");

        CtiMultiMsg_vec capMessages;
        _lastdbreloadtime = _lastdbreloadtime.now();
        if ( !wasAlreadyRunning )
        {
            dumpAllDynamicData();
        }

        bool systemEnabled = false;
        for(long h=0;h<_ccGeoAreas->size();h++)
        {
            CtiCCAreaPtr area =  (CtiCCAreaPtr)(*_ccGeoAreas).at(h);
            if (!area->getDisableFlag())
            {
                systemEnabled = true;
                break;
            }
        }
        CtiCCExecutorFactory::createExecutor(new SystemStatus(systemEnabled))->execute();

        for ( CtiCCSpecialPtr spArea : *_ccSpecialAreas )
        {
             cascadeAreaStrategySettings(spArea);
        }

        for ( CtiCCAreaPtr area : *_ccGeoAreas )
        {
            cascadeAreaStrategySettings(area);

            //disables verification on a subbus if the parent area or substation have been disabled.
            area->checkForAndStopVerificationOnChildSubBuses(capMessages);
        }
        try
        {
            for(int i=0;i<capMessages.size( );i++)
            {
                CtiCCExecutorFactory::createExecutor((CtiMessage*)capMessages[i])->execute();
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }


        initializeAllPeakTimeFlagsAndMonitorPoints(true);

        CtiCCExecutorFactory::createExecutor(new CapControlCommand(CapControlCommand::REQUEST_ALL_DATA))->execute();
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    if( _CC_DEBUG )
    {
        CTILOG_DEBUG(dout, "reset complete - ");
    }
}

/*---------------------------------------------------------------------------
    checkAMFMSystemForUpdates

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::checkAMFMSystemForUpdates()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    CTILOG_INFO(dout, "Checking AMFM system for updates...");

    {
        Cti::Database::DatabaseConnection connection;
        CtiTime lastAMFMUpdateTime = gInvalidCtiTime;

        //  This code was changed in http://fisheye.cooperpowereas.net/browse/software-yukon/trunk/yukon-server/CapControl/ccsubstationbusstore.cpp?r1=14596&r2=14999#seg37
        //  and no longer loads or saves the lastAMFMUpdateTime to a file.

        const std::string   filename = gConfigParms.getYukonBase() + "\\server\\config\\amfm.dat";
        const std::wstring  wideFilename( filename.begin(), filename.end() );

        boost::filesystem::path amfmFile( wideFilename );

        boost::filesystem::ofstream fileWriter( amfmFile );

        if ( boost::filesystem::exists( amfmFile ) )
        {
            if ( ! boost::filesystem::is_empty( amfmFile ) )
            {
                lastAMFMUpdateTime = timeSaver;
            }
            else
            {
                CTILOG_INFO(dout, "Creating amfm.dat");
                timeSaver = lastAMFMUpdateTime;

                boost::filesystem::resize_file( amfmFile, 0 );

//                fileWriter
//                    << timeSaver;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Can not create amfm.dat to store date!!!!!!!!!");
        }

        CtiTime currentDateTime;

        static const string sql =  "SELECT DNI.capacitor_id, DNI.circt_id_normal, DNI.circt_nam_normal, "
                                     "DNI.circt_id_current, DNI.circt_name_current, DNI.switch_datetime, DNI.owner, "
                                     "DNI.capacitor_name, DNI.kvar_rating, DNI.cap_fs, DNI.cbc_model, DNI.serial_no, "
                                     "DNI.location, DNI.switching_seq, DNI.cap_disable_flag, DNI.cap_disable_type, "
                                     "DNI.inoperable_bad_order_equipnote, DNI.open_tag_note, DNI.cap_change_type "
                                   "FROM dni_capacitor DNI "
                                   "WHERE DNI.datetimestamp > ? "
                                   "ORDER BY DNI.datetimestamp ASC";

        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr << lastAMFMUpdateTime;

        rdr.execute();

        if( _CC_DEBUG & CC_DEBUG_DATABASE )
        {
            CTILOG_INFO(dout, "DB read for SQL query: " << rdr.asString());
        }

        bool newAMFMChanges = false;
        while( rdr() )
        {
            handleAMFMChanges(rdr);
            CtiTime datetimestamp;
            rdr["datetimestamp"] >> datetimestamp;
            if( datetimestamp>lastAMFMUpdateTime )
            {
                lastAMFMUpdateTime = datetimestamp;
            }
            newAMFMChanges = true;
        }

        if( newAMFMChanges )
        {
            if ( boost::filesystem::exists( amfmFile ) )
            {
                timeSaver = lastAMFMUpdateTime;

                boost::filesystem::resize_file( amfmFile, 0 );

//                fileWriter
//                    << timeSaver;
            }

            //sending a signal message to dispatch so that changes from the amfm are in the system log
            string text("Import from AMFM system caused database changes");
            string additional = string();
            CtiCapController::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE );
        }
    }

    setReloadFromAMFMSystemFlag(false);
}

/*---------------------------------------------------------------------------
    handleAMFMChanges

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::handleAMFMChanges(Cti::RowReader& rdr)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    string       capacitor_id_string;
    long            circt_id_normal;
    string       circt_nam_normal                  = m3iAMFMNullString;
    long            circt_id_current                  = -1;
    string       circt_name_current                = m3iAMFMNullString;
    CtiTime    switch_datetime                   = gInvalidCtiTime;
    string       owner                             = m3iAMFMNullString;
    string       capacitor_name                    = m3iAMFMNullString;
    string       kvar_rating                       = m3iAMFMNullString;
    string       cap_fs                            = m3iAMFMNullString;
    string       cbc_model                         = m3iAMFMNullString;
    string       serial_no                         = m3iAMFMNullString;
    string       location                          = m3iAMFMNullString;
    string       switching_seq                     = m3iAMFMNullString;
    string       cap_disable_flag                  = m3iAMFMNullString;
    string       cap_disable_type                  = m3iAMFMNullString;
    string       inoperable_bad_order_equipnote    = m3iAMFMNullString;
    string       open_tag_note                     = m3iAMFMNullString;
    string       cap_change_type                   = m3iAMFMNullString;

    rdr["capacitor_id"] >> capacitor_id_string;
    rdr["circt_id_normal"] >> circt_id_normal;
    if( !rdr["circt_nam_normal"].isNull() )
    {
        rdr["circt_nam_normal"] >> circt_nam_normal;
    }
    if( !rdr["circt_id_current"].isNull() )
    {
        rdr["circt_id_current"] >> circt_id_current;
    }

    if( !rdr["circt_name_current"].isNull() )
    {
        rdr["circt_name_current"] >> circt_name_current;
    }

    if( !rdr["switch_datetime"].isNull() )
    {
        rdr["switch_datetime"] >> switch_datetime;
    }

    if( !rdr["owner"].isNull() )
    {
        rdr["owner"] >> owner;
    }

    if( !rdr["capacitor_name"].isNull() )
    {
        rdr["capacitor_name"] >> capacitor_name;
    }

    if( !rdr["kvar_rating"].isNull() )
    {
        rdr["kvar_rating"] >> kvar_rating;
    }

    if( !rdr["cap_fs"].isNull() )
    {
        rdr["cap_fs"] >> cap_fs;
    }

    if( !rdr["cbc_model"].isNull() )
    {
        rdr["cbc_model"] >> cbc_model;
    }

    if( !rdr["serial_no"].isNull() )
    {
        rdr["serial_no"] >> serial_no;
    }

    if( !rdr["location"].isNull() )
    {
        rdr["location"] >> location;
    }

    if( !rdr["switching_seq"].isNull() )
    {
        rdr["switching_seq"] >> switching_seq;
    }

    if( !rdr["cap_disable_flag"].isNull() )
    {
        rdr["cap_disable_flag"] >> cap_disable_flag;
    }

    if( !rdr["cap_disable_type"].isNull() )
    {
        rdr["cap_disable_type"] >> cap_disable_type;
    }

    if( !rdr["inoperable_bad_order_equipnote"].isNull() )
    {
        rdr["inoperable_bad_order_equipnote"] >> inoperable_bad_order_equipnote;
    }

    if( !rdr["open_tag_note"].isNull() )
    {
        rdr["open_tag_note"] >> open_tag_note;
    }

    if( !rdr["cap_change_type"].isNull() )
    {
        rdr["cap_change_type"] >> cap_change_type;
    }

    if( _CC_DEBUG )
    {
        Cti::FormattedList list;

        list << "AMFM system update:";
        list.add("capacitor_id_string")             << capacitor_id_string;
        list.add("circt_id_normal")                 << circt_id_normal;
        list.add("circt_nam_normal")                << circt_nam_normal;
        list.add("circt_id_current")                << circt_id_current;
        list.add("circt_name_current")              << circt_name_current;
        list.add("switch_datetime")                 << switch_datetime;
        list.add("owner")                           << owner;
        list.add("capacitor_name")                  << capacitor_name;
        list.add("kvar_rating")                     << kvar_rating;
        list.add("cap_fs")                          << cap_fs;
        list.add("cbc_model")                       << cbc_model;
        list.add("serial_no")                       << serial_no;
        list.add("location")                        << location;
        list.add("switching_seq")                   << switching_seq;
        list.add("cap_disable_flag")                << cap_disable_flag;
        list.add("cap_disable_type")                << cap_disable_type;
        list.add("inoperable_bad_order_equipnote")  << inoperable_bad_order_equipnote;
        list.add("open_tag_note")                   << open_tag_note;
        list.add("cap_change_type")                 << cap_change_type;

        CTILOG_DEBUG(dout, list);
    }

    feederReconfigureM3IAMFM( capacitor_id_string, circt_id_normal, circt_nam_normal, circt_id_current, circt_name_current,
                              switch_datetime, owner, capacitor_name, kvar_rating, cap_fs, cbc_model, serial_no,
                              location, switching_seq, cap_disable_flag, cap_disable_type,
                              inoperable_bad_order_equipnote, open_tag_note, cap_change_type );
}

string translateCBCModelToControllerType(string& cbc_model)
{
    string returnString = "(none)";
    if( ciStringEqual(cbc_model,"CBC5010") )
    {
        returnString = "CTI Paging";
    }
    else if( ciStringEqual(cbc_model,"CBC3010") )
    {
        returnString = "CTI DLC";
    }
    else if( ciStringEqual(cbc_model,"CBC2010") )
    {
        returnString = "CTI FM";
    }
    /*else if( !stingCompareIgnoreCase(cbc_model, ) )
    {
    }*/
    else
    {
        returnString = cbc_model;
    }

    return returnString;
}

/*---------------------------------------------------------------------------
    feederReconfigureM3IAMFM

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::feederReconfigureM3IAMFM( string& capacitor_id_string, long circt_id_normal,
                                                        string& circt_nam_normal, long circt_id_current,
                                                        string& circt_name_current, CtiTime& switch_datetime,
                                                        string& owner, string& capacitor_name, string& kvar_rating,
                                                        string& cap_fs, string& cbc_model, string& serial_no,
                                                        string& location, string& switching_seq, string& cap_disable_flag,
                                                        string& cap_disable_type, string& inoperable_bad_order_equipnote,
                                                        string& open_tag_note, string& cap_change_type )
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    //long capacitor_id = atol(capacitor_id_string);

    bool found = false;
    if( _ccSubstationBuses->size() > 0 )
    {
        for(long i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(i);

            CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.size() > 0 )
            {
                for(long j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                    for(long k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        //long capMapId;
                        //if( (capMapId = atol(currentCapBank->getMapLocationId())) == capacitor_id )
                        if( currentCapBank->getMapLocationId() == capacitor_id_string )
                        {
                            long capswitchingorder = atol(switching_seq.c_str());
                            long feedMapId;
                            if( (feedMapId = atol( currentFeeder->getMapLocationId().c_str() ) ) != circt_id_current )
                            {
                                capBankMovedToDifferentFeeder(currentFeeder, currentCapBank, circt_id_current, capswitchingorder);
                                currentCCSubstationBus->setBusUpdatedFlag(true);
                            }
                            else if( currentCapBank->getControlOrder() != capswitchingorder &&
                                     capswitchingorder < 11 )
                            {// if capswitchingorder 11-99 and on the same feeder ignore
                             // the amfm switching order according to a conversation with
                             // Peter Schuster and Steve Fischer of MidAmerican
                                capBankDifferentOrderSameFeeder(currentFeeder, currentCapBank, capswitchingorder);
                                currentCCSubstationBus->setBusUpdatedFlag(true);
                            }

                            string tempOperationalState = currentCapBank->getOperationalState();

                            std::transform(tempOperationalState.begin(),tempOperationalState.end(),tempOperationalState.begin(), ::toupper);
                            std::transform(cap_fs.begin(),cap_fs.end(),cap_fs.begin(), ::toupper);
                            std::transform(cap_disable_flag.begin(),cap_disable_flag.end(),cap_disable_flag.begin(), ::toupper);
                            long kvarrating = atol(kvar_rating.c_str());
                            bool updateCapBankFlag = false;

                            if( currentCapBank->getBankSize() != kvarrating )
                            {
                                {
                                    char tempchar[64] = "";
                                    string text = string("M3i Change, Cap Bank Bank Size: ");
                                    text += currentCapBank->getPaoName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPaoId(),tempchar,10);
                                    text += tempchar;
                                    string additional = string("Was: ");
                                    _ltoa(currentCapBank->getBankSize(),tempchar,10);
                                    additional += tempchar;
                                    additional += ", Now: ";
                                    _ltoa(kvarrating,tempchar,10);
                                    additional += tempchar;
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPaoName();
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE);
                                    CTILOG_INFO(dout, text << ", " << additional);
                                }
                                currentCapBank->setBankSize(kvarrating);
                                updateCapBankFlag = true;
                            }
                            if( tempOperationalState != cap_fs )
                            {
                                string tempFixedOperationalStateString = CtiCCCapBank::FixedOperationalState;
                                std::transform(tempOperationalState.begin(),tempOperationalState.end(),tempOperationalState.begin(), ::toupper);
                                {
                                    char tempchar[64] = "";
                                    string text = string("M3i Change, Cap Bank Op State: ");
                                    text += currentCapBank->getPaoName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPaoId(),tempchar,10);
                                    text += tempchar;
                                    string additional = string("Was: ");
                                    additional += currentCapBank->getOperationalState();
                                    additional += ", Now: ";
                                    additional += tempFixedOperationalStateString;
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPaoName();
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE);
                                    CTILOG_INFO(dout, text << ", " << additional);
                                }
                                currentCapBank->setOperationalState(ciStringEqual(cap_fs,tempFixedOperationalStateString)?CtiCCCapBank::FixedOperationalState:CtiCCCapBank::SwitchedOperationalState);
                                updateCapBankFlag = true;
                            }
                            if( (bool)currentCapBank->getDisableFlag() != (ciStringEqual(cap_disable_flag,m3iAMFMDisabledString)) )
                            {
                                {
                                    char tempchar[64] = "";
                                    string text = string("M3i Change, Cap Bank Disable Flag: ");
                                    text += currentCapBank->getPaoName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPaoId(),tempchar,10);
                                    text += tempchar;
                                    string additional = string("Was: ");
                                    additional += (currentCapBank->getDisableFlag()?m3iAMFMDisabledString:m3iAMFMEnabledString);
                                    additional += ", Now: ";
                                    additional += cap_disable_flag;
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPaoName();
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE);
                                    CTILOG_INFO(dout, text << ", " << additional);
                                }
                                currentCapBank->setDisableFlag(ciStringEqual(cap_disable_flag,m3iAMFMDisabledString));
                                updateCapBankFlag = true;
                            }
                            if( ciStringEqual(currentCapBank->getControllerType(),translateCBCModelToControllerType(cbc_model)) )
                            {
                                {
                                    char tempchar[64] = "";
                                    string text = string("M3i Change, Cap Bank Controller Type: ");
                                    text += currentCapBank->getPaoName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPaoId(),tempchar,10);
                                    text += tempchar;
                                    string additional = string("Was: ");
                                    additional += currentCapBank->getControllerType();
                                    additional += ", Now: ";
                                    additional += translateCBCModelToControllerType(cbc_model);
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPaoName();
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE);
                                    CTILOG_INFO(dout, text << ", " << additional);
                                }
                                currentCapBank->setControllerType(translateCBCModelToControllerType(cbc_model));
                                updateCapBankFlag = true;
                            }
                            if( ciStringEqual(currentCapBank->getPaoDescription(),location) )
                            {
                                {
                                    char tempchar[64] = "";
                                    string text = string("M3i Change, Cap Bank Location: ");
                                    text += currentCapBank->getPaoName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPaoId(),tempchar,10);
                                    text += tempchar;
                                    string additional("Was: ");
                                    additional += currentCapBank->getPaoDescription();
                                    additional += ", Now: ";
                                    additional += location;
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPaoName();
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE);
                                    CTILOG_INFO(dout, text << ", " << additional);
                                }
                                currentCapBank->setPaoDescription(location);
                                updateCapBankFlag = true;
                            }
                            if( updateCapBankFlag )
                            {
                                UpdateCapBankInDB(currentCapBank);
                                currentCCSubstationBus->setBusUpdatedFlag(true);
                            }
                            found = true;
                            break;
                        }
                    }

                    if( found )
                    {
                        break;
                    }
                }
            }
            if( found )
            {
                break;
            }
        }
    }
    if( !found )
    {
        CTILOG_WARN(dout, "Cap Bank not found MapLocationId: " << capacitor_id_string);
    }
}


/*---------------------------------------------------------------------------
    capBankMovedToDifferentFeeder

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capBankMovedToDifferentFeeder(CtiCCFeeder* oldFeeder, CtiCCCapBank* movedCapBank, long feederid, long capswitchingorder)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    CtiCCCapBank_SVector& oldFeederCapBanks = oldFeeder->getCCCapBanks();

    bool found = false;
    for(long i=0;i<_ccSubstationBuses->size();i++)
    {
        CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(i);

        CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
        for(long j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

            long feedMapId;
            if( (feedMapId = atol( currentFeeder->getMapLocationId().c_str() ) ) == feederid )
            {
                CtiCCCapBank_SVector::iterator itr = oldFeederCapBanks.begin();
                while(  itr != oldFeederCapBanks.end() )
                {
                    if( *itr == movedCapBank)//if address itr is pointing to matches the address passed in.
                        itr = oldFeederCapBanks.erase(itr);
                    else
                        ++itr;
                }
                CtiCCCapBank_SVector& newFeederCapBanks = currentFeeder->getCCCapBanks();

                if( newFeederCapBanks.size() > 0 )
                {
                    //search through the list to see if there is a cap bank in the
                    //list that already has the switching order
                    if( capswitchingorder >= ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.size()-1])->getControlOrder() )
                    {
                        movedCapBank->setControlOrder( ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.size()-1])->getControlOrder() + 1 );
                    }
                    else
                    {
                        for(long k=0;k<newFeederCapBanks.size();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)newFeederCapBanks[k];
                            if( capswitchingorder == currentCapBank->getControlOrder() )
                            {
                                //if the new switching order matches a current control
                                //order, then insert at the end of the cap bank list
                                movedCapBank->setControlOrder( ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.size()-1])->getControlOrder() + 1 );
                                break;
                            }
                            else if( currentCapBank->getControlOrder() > capswitchingorder )
                            {
                                movedCapBank->setControlOrder(capswitchingorder);
                                break;
                            }
                        }
                    }
                }
                else
                {
                    movedCapBank->setControlOrder(1);
                }

                newFeederCapBanks.insert(movedCapBank);

                UpdateFeederBankListInDB(oldFeeder);
                UpdateFeederBankListInDB(currentFeeder);

                {
                    char tempchar[64] = "";
                    string text = string("M3i Change, Cap Bank moved feeders: ");
                    text += movedCapBank->getPaoName();
                    text += ", PAO Id: ";
                    _ltoa(movedCapBank->getPaoId(),tempchar,10);
                    text += tempchar;
                    string additional = string("Moved from: ");
                    additional += oldFeeder->getPaoName();
                    additional += ", id: ";
                    _ltoa(oldFeeder->getPaoId(),tempchar,10);
                    additional += tempchar;
                    additional += " To: ";
                    additional += currentFeeder->getPaoName();
                    additional += ", id: ";
                    _ltoa(currentFeeder->getPaoId(),tempchar,10);
                    additional += tempchar;
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE);
                    CTILOG_INFO(dout, text << ", " << additional);
                }

                found = true;
                break;
            }
        }

        if( found )
        {
            break;
        }
    }

    if( !found )
    {
        CTILOG_WARN(dout, "Feeder not found, id: " << feederid);
    }
}

/*---------------------------------------------------------------------------
    capBankDifferentOrderSameFeeder

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capBankDifferentOrderSameFeeder(CtiCCFeeder* currentFeeder, CtiCCCapBank* currentCapBank, long capswitchingorder)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    long oldControlOrder = currentCapBank->getControlOrder();

    for( CtiCCCapBank_SVector::iterator itr = currentFeeder->getCCCapBanks().begin();
         itr != currentFeeder->getCCCapBanks().end();
         itr++)
    {
        if( *itr == currentCapBank)//if address itr is pointing to matches the address passed in.
            currentFeeder->getCCCapBanks().erase(itr);
    }

    currentCapBank->setControlOrder(capswitchingorder);
    currentFeeder->getCCCapBanks().insert(currentCapBank);
    UpdateFeederBankListInDB(currentFeeder);

    {
        char tempchar[64] = "";
        string text("M3i Change, Cap Bank changed order: ");
        text += currentCapBank->getPaoName();
        text += ", PAO Id: ";
        _ltoa(currentCapBank->getPaoId(),tempchar,10);
        text += tempchar;
        string additional = string("Moved from: ");
        _ltoa(oldControlOrder,tempchar,10);
        additional += tempchar;
        additional += ", to: ";
        _ltoa(capswitchingorder,tempchar,10);
        additional += tempchar;
        additional += ", on Feeder: ";
        additional += currentFeeder->getPaoName();
        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE);
        CTILOG_INFO(dout, text << ", " << additional);
    }
}

/*---------------------------------------------------------------------------
    capOutOfServiceM3IAMFM

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capOutOfServiceM3IAMFM(long feederid, long capid, string& enableddisabled, string& fixedswitched)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    bool found = false;
    if( _ccSubstationBuses->size() > 0 )
    {
        for(long i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(i);

            CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.size() > 0 )
            {
                for(long j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                    for(long k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        long capMapId;
                        if( (capMapId = atol(currentCapBank->getMapLocationId().c_str() )) == capid )
                        {
                            std::transform(enableddisabled.begin(),enableddisabled.end(),enableddisabled.begin(), ::toupper);
                          if( (bool)currentCapBank->getDisableFlag() != (ciStringEqual(enableddisabled,m3iAMFMDisabledString)) )
                            {
                                UpdatePaoDisableFlagInDB(currentCapBank, ciStringEqual(enableddisabled,m3iAMFMDisabledString));
                                currentCCSubstationBus->setBusUpdatedFlag(true);
                                {
                                    CTILOG_INFO(dout, "M3I change, 'Cap Out of Service' PAO Id: "
                                         << currentCapBank->getPaoId() << ", name: "
                                         << currentCapBank->getPaoName() << ", was "
                                         << enableddisabled);
                                }
                            }
                            found = true;
                            break;
                        }
                    }

                    if( found )
                    {
                        break;
                    }
                }
            }
            if( found )
            {
                break;
            }
        }
    }
    if( !found )
    {
        CTILOG_WARN(dout, "Cap Bank not found, id: " << capid);
    }
}

/*---------------------------------------------------------------------------
    feederOutOfServiceM3IAMFM

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::feederOutOfServiceM3IAMFM(long feederid, string& fixedswitched, string& enableddisabled)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    bool found = false;
    if( _ccSubstationBuses->size() > 0 )
    {
        for(long i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(i);

            CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.size() > 0 )
            {
                for(long j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    long feedMapId;
                    if( (feedMapId = atol(currentFeeder->getMapLocationId().c_str() )) == feederid )
                    {
                        std::transform(enableddisabled.begin(),enableddisabled.end(),enableddisabled.begin(), ::toupper);

                        if( (bool)currentFeeder->getDisableFlag() != (ciStringEqual(enableddisabled,m3iAMFMDisabledString)) )
                        {
                            UpdatePaoDisableFlagInDB(currentFeeder, ciStringEqual(enableddisabled,m3iAMFMDisabledString));
                            currentCCSubstationBus->setBusUpdatedFlag(true);
                            {
                                CTILOG_INFO(dout, "M3I change, 'Feeder Out of Service' PAO Id: "
                                     << currentFeeder->getPaoId() << ", name: "
                                     << currentFeeder->getPaoName() << ", was "
                                     << enableddisabled);
                            }
                        }
                        found = true;
                        break;
                    }
                }
            }
            if( found )
            {
                break;
            }
        }
    }
    if( !found )
    {
        CTILOG_WARN(dout, "Feeder not found, id: " << feederid);
    }
}

/*---------------------------------------------------------------------------
    shutdown

    Dumps the substations list.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::shutdown()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    dumpAllDynamicData();
    delete_container(*_ccSubstationBuses);
    _ccSubstationBuses->clear();
    delete _ccSubstationBuses;
    _ccSubstations.clear();
    delete_container(*_ccCapBankStates);
    _ccCapBankStates->clear();
    delete _ccCapBankStates;
    delete_container(*_ccGeoAreas);
    _ccGeoAreas->clear();
    delete _ccGeoAreas;
    delete_container(*_ccSpecialAreas);
    _ccSpecialAreas->clear();
    delete _ccSpecialAreas;

    _paobject_substation_map.clear();
}

/*---------------------------------------------------------------------------
    doOpStatsThr

    Starts on construction and simply forces a call to reset every 60 minutes
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::doOpStatsThr()
{
    CTILOG_DEBUG(dout, "SubstationBusStore OpStats thread is starting");

    string str;
    char var[128];
    int refreshrate = 3600;

    std::strcpy(var, "CAP_CONTROL_OP_STATS_REFRESH_RATE");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        refreshrate = atoi(str.c_str());
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << refreshrate);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }
    ThreadStatusKeeper threadStatus("CapControl doOpsThr");
    bool startUpSendStats = true;
    long lastOpStatsThreadPulse = 0;
    CtiTime currentTime;
    CtiTime opStatRefreshRate =  nextScheduledTimeAlignedOnRate( currentTime,  _OP_STATS_REFRESH_RATE );

    unsigned long secondsFrom1901 = 0;

    try
    {
        while(true)
        {
            currentTime = CtiTime();
            secondsFrom1901 = currentTime.seconds();

            if( (currentTime.seconds() > opStatRefreshRate.seconds() && secondsFrom1901 != lastOpStatsThreadPulse) ||
                startUpSendStats )
            {
                CTILOG_INFO(dout, "Controller refreshing OP STATS");

                {
                    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

                    CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
                    CtiMultiMsg_vec& pointChanges = multiDispatchMsg->getData();

                    resetAllOperationStats();
                    resetAllConfirmationStats();
                    reCalculateOperationStatsFromDatabase( );
                    reCalculateConfirmationStatsFromDatabase( );
                    try
                    {
                        reCalculateAllStats( );
                        lastOpStatsThreadPulse = secondsFrom1901;
                        opStatRefreshRate =  nextScheduledTimeAlignedOnRate( currentTime,  _OP_STATS_REFRESH_RATE );
                        CTILOG_INFO(dout, "Next OP STATS CHECKTIME : "<<opStatRefreshRate);

                        createAllStatsPointDataMsgs(pointChanges);
                        try
                        {
                            //send point changes to dispatch
                            if( multiDispatchMsg->getCount() > 0 )
                            {
                                multiDispatchMsg->resetTime(); // CGP 5/21/04 Update its time to current time.
                                CtiCapController::getInstance()->sendMessageToDispatch(multiDispatchMsg, CALLSITE);
                            }
                            else
                            {
                                delete multiDispatchMsg;
                            }
                        }
                        catch ( boost::thread_interrupted & )
                        {
                            throw;
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        }
                    }
                    catch ( boost::thread_interrupted & )
                    {
                        throw;
                    }
                    catch(...)
                    {
                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                    }
                }
                startUpSendStats = false;
            }

            threadStatus.monitorCheck();

            boost::this_thread::sleep( boost::posix_time::milliseconds( 500 ) );
        }
    }
    catch ( boost::thread_interrupted & )
    {
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_DEBUG(dout, "SubstationBusStore OpStats thread is stopping");
}


/*---------------------------------------------------------------------------
    doResetThr

    Starts on construction and simply forces a call to reset every 60 minutes
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::doResetThr()
{
    CTILOG_DEBUG(dout, "SubstationBusStore reset thread is starting");

    string str;
    char var[128];
    int refreshrate = 3600;

    std::strcpy(var, "CAP_CONTROL_REFRESH");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        refreshrate = atoi(str.c_str());
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << refreshrate);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    CtiTime lastPeriodicDatabaseRefresh = CtiTime();
    ThreadStatusKeeper threadStatus("CapControl doResetThr");

    try
    {
        while(true)
        {
            CtiTime currentTime;
            currentTime = currentTime.now();

            if( currentTime.seconds() >= lastPeriodicDatabaseRefresh.seconds()+refreshrate )
            {
                CTILOG_INFO(dout, "Periodic restore of substation list from the database");

                dumpAllDynamicData();
                setValid(false);

                lastPeriodicDatabaseRefresh = CtiTime();
            }

            threadStatus.monitorCheck();

            boost::this_thread::sleep( boost::posix_time::milliseconds( 500 ) );
        }
    }
    catch ( boost::thread_interrupted & )
    {
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_DEBUG(dout, "SubstationBusStore reset thread is stopping");
}

/*---------------------------------------------------------------------------
    doAMFMThr

    Starts on construction and simply forces a call to reset every 60 minutes
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::doAMFMThr()
{
    CTILOG_DEBUG(dout, "SubstationBusStore AMFM thread is starting");

    string str;
    char var[128];
    string amfm_interface = "NONE";

    std::strcpy(var, "CAP_CONTROL_AMFM_INTERFACE");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        amfm_interface = str.c_str();
        std::transform(amfm_interface.begin(),amfm_interface.end(),amfm_interface.begin(), ::toupper);
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << amfm_interface);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    if( ciStringEqual(amfm_interface,m3iAMFMInterfaceString) )
    {
        int refreshrate = 3600;
        string amFmDbType = "none";
        string amFmDbName = "none";
        string amFmDbUser = "none";
        string amFmDbPassword = "none";

        std::strcpy(var, "CAP_CONTROL_AMFM_RELOAD_RATE");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            refreshrate = atoi(str.c_str());
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, var << ":  " << refreshrate);
            }
        }
        else
        {
            CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
        }

        std::strcpy(var, "CAP_CONTROL_AMFM_DB_TYPE");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            amFmDbType = boost::algorithm::to_lower_copy(str);

            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, var << ":  " << amFmDbType);
            }
        }
        else
        {
            CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
        }

        std::strcpy(var, "CAP_CONTROL_AMFM_DB_SQLSERVER");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            amFmDbName = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, var << ":  " << amFmDbName);
            }
        }
        else
        {
            CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
        }

        std::strcpy(var, "CAP_CONTROL_AMFM_DB_USERNAME");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            amFmDbUser = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, var << ":  " << amFmDbUser);
            }
        }
        else
        {
            CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
        }

        std::strcpy(var, "CAP_CONTROL_AMFM_DB_PASSWORD");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            amFmDbPassword = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, var << ":  " << amFmDbPassword);
            }
        }
        else
        {
            CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
        }


        if( amFmDbType != "none" && amFmDbName != "none" && amFmDbUser != "none" && amFmDbPassword != "none" )
        {
            CTILOG_INFO(dout, "Obtaining connection to the AMFM database...");
            Cti::Database::setDatabaseParams(amFmDbType,amFmDbName,amFmDbUser,amFmDbPassword);

            CtiTime currenttime = CtiTime();
            unsigned long tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+(2*refreshrate);
            CtiTime nextAMFMRefresh = CtiTime(CtiTime(tempsum));

            try
            {
                while(true)
                {
                    if ( CtiTime() >= nextAMFMRefresh )
                    {
                        if( _CC_DEBUG & CC_DEBUG_STANDARD )
                        {
                            CTILOG_DEBUG(dout, "Setting AMFM reload flag.");
                        }

                        dumpAllDynamicData();
                        setReloadFromAMFMSystemFlag(true);

                        currenttime = CtiTime();
                        tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+refreshrate;
                        nextAMFMRefresh = CtiTime(CtiTime(tempsum));
                    }

                    boost::this_thread::sleep( boost::posix_time::milliseconds( 500 ) );
                }
            }
            catch ( boost::thread_interrupted & )
            {
            }
        }
        else
        {
            CTILOG_INFO(dout, "Can't find AMFM DB setting in master.cfg!!!");
        }
    }

    CTILOG_DEBUG(dout, "SubstationBusStore AMFM thread is stopping");
}

/* Pointer to the singleton instance of CtiCCSubstationBusStore
   Instantiate lazily by Instance */
CtiCCSubstationBusStore* CtiCCSubstationBusStore::_instance = NULL;

/*---------------------------------------------------------------------------
    getInstance

    Returns a pointer to the singleton instance of CtiCCSubstationBusStore
---------------------------------------------------------------------------*/
CtiCCSubstationBusStore* CtiCCSubstationBusStore::getInstance()
{
    if ( _instance == NULL )
    {
        _instance = new CtiCCSubstationBusStore();
    }

    return _instance;
}

/*---------------------------------------------------------------------------
    deleteInstance

    Deletes the singleton instance of CtiCCSubstationBusStore
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::deleteInstance()
{
    if( _instance != NULL )
    {
        delete _instance;
        _instance = NULL;
    }
}

/*---------------------------------------------------------------------------
    setInstance

    Sets the instance to the passed instance. This should only be used in Unit Tests.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setInstance(CtiCCSubstationBusStore* substationBusStore)
{
    if (_instance != NULL)
    {
        deleteInstance();
    }
    _instance = substationBusStore;

}


AttributeService &CtiCCSubstationBusStore::getAttributeService()
{
    return *(getInstance()->_attributeService);
}


void CtiCCSubstationBusStore::setAttributeService(std::unique_ptr<AttributeService> service)
{
    if( service.get() )
    {
        _attributeService = std::move(service);

        _voltageRegulatorManager->setAttributeService( _attributeService.get() );
    }
}

void CtiCCSubstationBusStore::setStrategyManager(std::unique_ptr<StrategyManager> manager)
{
    if( manager.get() )
    {
        _strategyManager.swap(manager);
    }
}

/*---------------------------------------------------------------------------
    isValid

    Returns a true if the strategystore was able to initialize properly
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::isValid()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    return _isvalid;
}

/*---------------------------------------------------------------------------
    setValid

    Sets the _isvalid flag
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setValid(bool valid)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    _isvalid = valid;
}

/*---------------------------------------------------------------------------
    getReloadFromAMFMSystemFlag

    Gets _reloadfromamfmsystemflag
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::getReloadFromAMFMSystemFlag()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    return _reloadfromamfmsystemflag;
}

/*---------------------------------------------------------------------------
    setReloadFromAMFMSystemFlag

    Sets _reloadfromamfmsystemflag
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setReloadFromAMFMSystemFlag(bool reload)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    _reloadfromamfmsystemflag = reload;
}

bool CtiCCSubstationBusStore::get2wayFlagUpdate()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    return _2wayFlagUpdate;
}

void CtiCCSubstationBusStore::set2wayFlagUpdate(bool flag)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    _2wayFlagUpdate = flag;
}

/*---------------------------------------------------------------------------
    verifySubBusAndFeedersStates

    This method goes through the entire list of sub buses, feeders, and
    cap banks and determines whether there are sub buses or feeders with a
    recently controlled flag of true with no pending cap banks or pending
    cap banks where the sub bus or feeder isn't recently controlled.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::verifySubBusAndFeedersStates()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    for(int i=0;i<_ccSubstationBuses->size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)((*_ccSubstationBuses).at(i));

        long numberOfFeedersRecentlyControlled = 0;
        long numberOfCapBanksPending = 0;

        if( currentSubstationBus->getRecentlyControlledFlag() )
        {
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(int j=0;j<ccFeeders.size();j++)
            {
                numberOfCapBanksPending = 0;
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(int k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                        if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                            currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                        {
                            numberOfCapBanksPending++;
                        }
                    }

                    if( numberOfCapBanksPending == 0 && !currentFeeder->getPostOperationMonitorPointScanFlag())
                    {
                        currentFeeder->setRecentlyControlledFlag(false);
                        CTILOG_INFO(dout, "Feeder: " << currentFeeder->getPaoName() << ", no longer recently controlled because no banks were pending");
                    }
                    else if( numberOfCapBanksPending > 1 )
                    {
                        CTILOG_INFO(dout, "Multiple cap banks pending in Feeder: " << currentFeeder->getPaoName() << ", setting status to questionable");
                        for(int k=0;k<ccCapBanks.size();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending && currentCapBank->getStatusPointId() > 0)
                            {
                                CTILOG_INFO(dout, "Setting status to close questionable, Cap Bank: " << currentCapBank->getPaoName());
                                currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                                currentCapBank->setControlRecentlySentFlag(false);
                                currentFeeder->setRetryIndex(0);
                                currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                                currentCapBank->setAfterVarsString("abnormal data");
                                currentCapBank->setPercentChangeString(" --- ");
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE), CALLSITE);
                                long stationId, areaId, spAreaId;
                                getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::CloseQuestionable, "Var: Multiple banks pending, CloseQuestionable", "cap control");
                                eventMsg.setActionId(currentCapBank->getActionId());
                                eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                                CtiCapController::submitEventLogEntry(eventMsg);

                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && currentCapBank->getStatusPointId() > 0)
                            {
                                CTILOG_INFO(dout, "Setting status to open questionable, Cap Bank: " << currentCapBank->getPaoName());
                                currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                                currentCapBank->setControlRecentlySentFlag(false);
                                currentFeeder->setRetryIndex(0);
                                currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                                currentCapBank->setAfterVarsString("abnormal data");
                                currentCapBank->setPercentChangeString(" --- ");
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE), CALLSITE);
                                long stationId, areaId, spAreaId;
                                getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::OpenQuestionable, "Var: Multiple banks pending, OpenQuestionable", "cap control");
                                eventMsg.setActionId(currentCapBank->getActionId());
                                eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                                CtiCapController::submitEventLogEntry(eventMsg);
                            }
                        }
                    }
                    else if (currentFeeder->getPostOperationMonitorPointScanFlag())
                    {
                        CTILOG_INFO(dout, "Feeder: " << currentFeeder->getPaoName() << " waiting for Post Op Monitor Scan ");
                    }
                    else
                    {//normal pending feeder
                        numberOfFeedersRecentlyControlled++;
                    }
                }
                else
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(int k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                        if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending && currentCapBank->getStatusPointId() > 0)
                        {
                            CTILOG_INFO(dout, "Cap Bank: " << currentCapBank->getPaoName() << " questionable because feeder is not recently controlled");
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            currentCapBank->setControlRecentlySentFlag(false);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE), CALLSITE);
                            long stationId, areaId, spAreaId;
                            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::CloseQuestionable, "Var: Feeder not recently controlled, CloseQuestionable", "cap control");
                            eventMsg.setActionId(currentCapBank->getActionId());
                            eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::submitEventLogEntry(eventMsg);
                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && currentCapBank->getStatusPointId() > 0)
                        {
                            CTILOG_INFO(dout, "Cap Bank: " << currentCapBank->getPaoName() << " questionable because feeder is not recently controlled");
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            currentCapBank->setControlRecentlySentFlag(false);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE), CALLSITE);
                            long stationId, areaId, spAreaId;
                            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::OpenQuestionable, "Var: Feeder not recently controlled, OpenQuestionable", "cap control");
                            eventMsg.setActionId(currentCapBank->getActionId());
                            eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::submitEventLogEntry(eventMsg);
                        }
                    }
                }
            }

            if( numberOfFeedersRecentlyControlled == 0 )
            {
                currentSubstationBus->setRecentlyControlledFlag(false);
                currentSubstationBus->setWaitToFinishRegularControlFlag(false);
                currentSubstationBus->setBusUpdatedFlag(true);
            }
        }
        else if (currentSubstationBus->getPostOperationMonitorPointScanFlag())
        {
            CTILOG_INFO(dout, "Sub: " << currentSubstationBus->getPaoName() << " waiting for Post Op Monitor Scan ");
        }
        else//sub bus not recently controlled
        {

            if (!currentSubstationBus->getPerformingVerificationFlag())
            {

                currentSubstationBus->setWaitToFinishRegularControlFlag(false);
                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                for(int j=0;j<ccFeeders.size();j++)
                {
                    numberOfCapBanksPending = 0;
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    if( currentFeeder->getRecentlyControlledFlag() )
                    {
                        currentFeeder->setRecentlyControlledFlag(false);
                        CTILOG_INFO(dout, "Feeder: " << currentFeeder->getPaoName() << ", no longer recently controlled because sub bus not recently controlled");
                    }

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(int k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                        if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending && !(currentCapBank->getVerificationFlag() && currentCapBank->getPerformingVerificationFlag()) && currentCapBank->getStatusPointId() > 0)
                        {
                            CTILOG_INFO(dout, "Setting status to close questionable, Cap Bank: " << currentCapBank->getPaoName());
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            currentCapBank->setControlRecentlySentFlag(false);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE), CALLSITE);
                            long stationId, areaId, spAreaId;
                            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                            EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::CloseQuestionable, "Var: Sub not recently controlled, CloseQuestionable", "cap control");
                            eventMsg.setActionId(currentCapBank->getActionId());
                            eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::submitEventLogEntry(eventMsg);

                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && !(currentCapBank->getVerificationFlag() && currentCapBank->getPerformingVerificationFlag()) && currentCapBank->getStatusPointId() > 0)
                        {
                            CTILOG_INFO(dout, "Setting status to open questionable, Cap Bank: " << currentCapBank->getPaoName());
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            currentCapBank->setControlRecentlySentFlag(false);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE), CALLSITE);
                            long stationId, areaId, spAreaId;
                            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                            EventLogEntry eventMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::OpenQuestionable, "Var: Sub not recently controlled, OpenQuestionable", "cap control");
                            eventMsg.setActionId(currentCapBank->getActionId());
                            eventMsg.setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::submitEventLogEntry(eventMsg);
                        }
                    }
                }
            }
        }
    }
}

/*---------------------------------------------------------------------------
    resetDailyOperations

    Updates a disable flag in the yukonpaobject table in the database for
    the substation bus.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::resetDailyOperations()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    CtiMultiMsg* multiPointMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multiPointMsg->getData();


    for(int i=0;i<_ccSubstationBuses->size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)((*_ccSubstationBuses).at(i));
        {
            char tempchar[64] = "";
            string text("Daily Operations were ");
            _ltoa(currentSubstationBus->getCurrentDailyOperations(),tempchar,10);
            text += tempchar;
            string additional("Sub Bus: ");
            additional += currentSubstationBus->getPaoName();

            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                pointChanges.push_back(new CtiSignalMsg(currentSubstationBus->getDailyOperationsAnalogPointId(),0,text,additional,CapControlLogType,SignalEvent, "cap control",
                                                                                        0,0,0, currentSubstationBus->getCurrentDailyOperations() ));
            CTILOG_INFO(dout, text << ", " << additional);
            long stationId, areaId, spAreaId;
            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
                CtiCapController::submitEventLogEntry(EventLogEntry(0, currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, 1, currentSubstationBus->getCurrentDailyOperations(), text, "cap control"));
            else
                CtiCapController::submitEventLogEntry(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, 1, currentSubstationBus->getCurrentDailyOperations(), text, "cap control"));


        }
        currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
        if ( currentSubstationBus->getMaxDailyOpsHitFlag() )    // re-enable maxOps disabled bus
        {
            currentSubstationBus->setMaxDailyOpsHitFlag( false );
            if ( currentSubstationBus->getDisableFlag() )
            {
                UpdatePaoDisableFlagInDB( currentSubstationBus, false );
            }
        }
        currentSubstationBus->setBusUpdatedFlag(true);
        currentSubstationBus->setCorrectionNeededNoBankAvailFlag(false);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(int j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

            currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
            if ( currentFeeder->getMaxDailyOpsHitFlag() )   // re-enable maxOps disabled feeder
            {
                currentFeeder->setMaxDailyOpsHitFlag( false );
                if ( currentFeeder->getDisableFlag() )
                {
                    UpdatePaoDisableFlagInDB( currentFeeder, false );
                }
            }
            currentFeeder->setCorrectionNeededNoBankAvailFlag(false);

            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            //**********************************************************************
            //The operation count on a cap bank is actually a total not a daily, doh
            //**********************************************************************
            for(int k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                currentCapBank->setCurrentDailyOperations(0);
                if ( currentCapBank->getMaxDailyOpsHitFlag() )  // re-enable maxOps disabled capbank
                {
                    currentCapBank->setMaxDailyOpsHitFlag( false );
                    if ( currentCapBank->getDisableFlag() )
                    {
                        UpdatePaoDisableFlagInDB( currentCapBank, false );
                    }
                }
                currentCapBank->setRetryCloseFailedFlag(false);
                currentCapBank->setRetryOpenFailedFlag(false);
            }
            //**********************************************************************
            //The operation count on a cap bank is actually a total not a daily, doh
            //**********************************************************************
        }
    }
    CtiHolidayManager::getInstance().refresh();

    const CtiDate today = CtiDate();
    const CtiDate yesterday = today - 1;

    if (CtiHolidayManager::getInstance().isHolidayForAnySchedule(today) )
    {
        setValid(false);
    }
    if (CtiHolidayManager::getInstance().isHolidayForAnySchedule(yesterday) )
    {
        setValid(false);
    }
    if( pointChanges.size() > 0 )
    {
        multiPointMsg->resetTime(); // CGP 5/21/04 Update its time to current time.
        CtiCapController::getInstance()->sendMessageToDispatch(multiPointMsg, CALLSITE);
    }
    else
        delete multiPointMsg;

}

/*---------------------------------------------------------------------------
    UpdateBusVerificationFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the substation bus.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateBusVerificationFlagsInDB(CtiCCSubstationBus* bus)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    static const string updateSql = "update dynamicccsubstationbus set "
                                    "verificationflag = ? "
                                    " where paobjectid = ?";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseWriter updater(conn, updateSql);

    updater << (string)(bus->getVerificationFlag()?"Y":"N") << bus->getPaoId();

    if( ! Cti::Database::executeUpdater( updater, CALLSITE ))
    {
        return false;
    }

    CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(bus->getPaoId(), ChangePAODb,
                                              bus->getPaoCategory(), bus->getPaoType(),
                                              ChangeTypeUpdate);
    dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
    CtiCapController::getInstance()->sendMessageToDispatch(dbChange, CALLSITE);

    return true; // No error occurred!
}

// Updates the yukonpaobject table with the paoid and disable flag given.
bool CtiCCSubstationBusStore::updateDisableFlag(unsigned int paoid, bool isDisabled)
{
    static const string updateSql = "update yukonpaobject set disableflag = ? "
                                    " where paobjectid = ?";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseWriter updater(conn, updateSql);

    updater << (string)(isDisabled?"Y":"N") << paoid;

    return Cti::Database::executeUpdater( updater, CALLSITE );
}


/*---------------------------------------------------------------------------
    UpdatePaoDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the cap bank.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdatePaoDisableFlagInDB(CapControlPao* pao, bool disableFlag, bool forceFullReload)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    bool updateSuccessful = updateDisableFlag(pao->getPaoId(), disableFlag);

    CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(pao->getPaoId(), ChangePAODb,
                                                  pao->getPaoCategory(),
                                                  pao->getPaoType(),
                                                  ChangeTypeUpdate);

    if (forceFullReload)
    {
        dbChange->setSource(CAP_CONTROL_RELOAD_DBCHANGE_MSG_SOURCE);
    }
    else
    {
        dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
    }

    if (disableFlag)
    {
        pao->setDisableFlag(disableFlag, MAXPRIORITY);//high priority, process before DB Change
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange, CALLSITE);
    }
    else
    {
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange, CALLSITE);
        pao->setDisableFlag(disableFlag); //normal priority, DB Change will be processed first
    }

    return updateSuccessful;
}

/*---------------------------------------------------------------------------
    UpdateCapBankOperationalStateInDB

    Updates the operational state in the yukonpaobject table in the database for
    the cap bank.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateCapBankOperationalStateInDB(CtiCCCapBank* capbank)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    static const string updateSql = "update capbank set operationalstate = ? "
                                    " where deviceid = ?";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseWriter updater(conn, updateSql);

    updater << capbank->getOperationalState() << capbank->getPaoId();

    if( ! Cti::Database::executeUpdater( updater, CALLSITE ))
    {
        return false;
    }

    CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(capbank->getPaoId(), ChangePAODb,
                                                  capbank->getPaoCategory(),
                                                  capbank->getPaoType(),
                                                  ChangeTypeUpdate);
    dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
    CtiCapController::getInstance()->sendMessageToDispatch(dbChange, CALLSITE);

    return true;
}

/*---------------------------------------------------------------------------
    UpdateCapBankInDB

    Updates multiple fields in tables associated with cap banks in the
    database.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateCapBankInDB(CtiCCCapBank* capbank)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    static const string paobjectUpdateSql = "update yukonpaobject set paoname = ?, disableflag = ?, description = ? "
                                    " where capbank = ?";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseWriter updater(conn, paobjectUpdateSql);

    updater << capbank->getPaoName()
            << (string)(capbank->getDisableFlag()?"Y":"N")
            << capbank->getPaoDescription().c_str()
            << capbank->getPaoId();

    bool paobjectUpdateSuccessful = Cti::Database::executeUpdater( updater, CALLSITE );

    static const string capbankUpdateSql = "update capbank set banksize = ?, operationalstate = ?"
                                           " where deviceid = ?";
    updater.setCommandText(capbankUpdateSql);

    updater << capbank->getBankSize()
            << capbank->getOperationalState()
            << capbank->getPaoId();

    bool capbankUpdateSuccessful = Cti::Database::executeUpdater( updater, CALLSITE );

    if ( paobjectUpdateSuccessful || capbankUpdateSuccessful )
    {
        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(capbank->getPaoId(), ChangePAODb,
                                                      capbank->getPaoCategory(),
                                                      capbank->getPaoType(),
                                                      ChangeTypeUpdate);
        dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange, CALLSITE);
    }

    return paobjectUpdateSuccessful && capbankUpdateSuccessful;
}

/*---------------------------------------------------------------------------
    UpdateFeederBankListInDB

    Updates multiple fields in tables associated with cap banks in the
    database.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateFeederBankListInDB(CtiCCFeeder* feeder)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    bool insertSuccessful = true;

    Cti::Database::DatabaseConnection   connection;
    {
        static const std::string deleteSql = "delete from ccfeederbanklist where feederid = ?";

        Cti::Database::DatabaseWriter       deleter(connection, deleteSql);

        deleter << feeder->getPaoId();

        Cti::Database::executeCommand( deleter, CALLSITE );
    }

    static const std::string insertSql = "insert into ccfeederbanklist values(?, ?, ?, ?, ?)";
    Cti::Database::DatabaseWriter dbInserter(connection, insertSql);

    CtiCCCapBank_SVector& ccCapBanks = feeder->getCCCapBanks();
    for(long i=0;i<ccCapBanks.size();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[i];

        dbInserter << feeder->getPaoId()
                 << currentCapBank->getPaoId()
                 << currentCapBank->getControlOrder()
                 << currentCapBank->getCloseOrder()
                 << currentCapBank->getTripOrder();

        if( ! Cti::Database::executeCommand( dbInserter, CALLSITE ))
        {
            insertSuccessful = false;
        }
    }

    CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(feeder->getPaoId(), ChangePAODb,
                                                  feeder->getPaoCategory(),
                                                  feeder->getPaoType(),
                                                  ChangeTypeUpdate);
    dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
    CtiCapController::getInstance()->sendMessageToDispatch(dbChange, CALLSITE);

    return insertSuccessful;
}


bool CtiCCSubstationBusStore::UpdateFeederSubAssignmentInDB(CtiCCSubstationBus* bus)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    CtiTime currentDateTime = CtiTime();
    Cti::Database::DatabaseConnection   connection;
    {
        static const std::string sql = "delete from ccfeedersubassignment where substationbusid = ?";

        Cti::Database::DatabaseWriter       deleter(connection, sql);

        deleter << bus->getPaoId();
        deleter.execute();
    }

    static const string insertSql = "insert into ccfeedersubassignment values( "
                                    "?, ?, ?)";

    Cti::Database::DatabaseWriter dbInserter(connection, insertSql);
    bool insertSuccesful = true;

    CtiFeeder_vec& ccFeeders = bus->getCCFeeders();
    for(long i=0;i<ccFeeders.size();i++)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[i];

        currentFeeder->dumpDynamicData(connection, currentDateTime);

        dbInserter << bus->getPaoId()
                 << currentFeeder->getPaoId()
                 << currentFeeder->getDisplayOrder();

        insertSuccesful = dbInserter.execute( );
    }

    CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(bus->getPaoId(), ChangePAODb,
                                                  bus->getPaoCategory(),
                                                  bus->getPaoType(),
                                                  ChangeTypeUpdate);
    dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
    CtiCapController::getInstance()->sendMessageToDispatch(dbChange, CALLSITE);

    return insertSuccesful;
}

/*---------------------------------------------------------------------------
    InsertCCEventLogInDB

    Inserts EventLogEntry entries into the database.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::InsertCCEventLogInDB(const EventLogEntry &msg)
{

    static const std::string insertSql =
        "INSERT INTO "
            "CCEventLog (LogID, "
                        "PointID, "
                        "DateTime, "
                        "SubID, "
                        "FeederID, "
                        "EventType, "
                        "SeqID, "
                        "Value, "
                        "Text, "
                        "UserName, "
                        "KVARBefore, "
                        "KVARAfter, "
                        "KVARChange, "
                        "AdditionalInfo, "
                        "actionId, "
                        "CapBankStateInfo, "
                        "aVar, "
                        "bVar, "
                        "cVar, "
                        "StationID, "
                        "AreaID, "
                        "SpAreaID, "
                        "RegulatorId, "
                        "EventSubtype) "
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseWriter dbInserter(connection, insertSql);

    dbInserter
        << CCEventLogIdGen()
        << msg.pointId
        << CtiTime(msg.timeStamp)
        << msg.subId
        << msg.feederId
        << msg.eventType
        << msg.seqId
        << msg.value
        << msg.text
        << msg.userName
        << msg.kvarBefore
        << msg.kvarAfter
        << msg.kvarChange
        << msg.ipAddress
        << msg.actionId
        << msg.stateInfo
        << msg.aVar
        << msg.bVar
        << msg.cVar
        << msg.stationId
        << msg.areaId
        << msg.spAreaId
        << msg.regulatorId
        << msg.eventSubtype;

    if( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO(dout, dbInserter.asString());
    }

    if( ! dbInserter.execute() )
    {
        CTILOG_ERROR(dout, "CCEventLog Table insert failed: " << dbInserter.asString());

        return false;
    }

    return true;
}


/*---------------------------------------------------------------------------
    reloadStrategyFromDataBase

    Reloads all or single strategy from strategy table in the database.  Updates
    SubStationBus/Feeder Strategy values if a single strategyId is specified.
    StrategyId = 0 indicates reloaded all strategies from db.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::reloadStrategyFromDatabase(long strategyId)
{

    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    try
    {
        // First save states, then reload the strategy.
        if (strategyId == -1)
        {
            _strategyManager->saveAllStates();
            _strategyManager->reloadAll();
        }
        else
        {
            _strategyManager->saveStates(strategyId);
            _strategyManager->reload(strategyId);
        }
        {
            reloadTimeOfDayStrategyFromDatabase(strategyId);

            if (strategyId >= 0)
            {
                long paObjectId;
                //Update Area Strategy Values with dbChanged/editted strategy values
                for (Cti::CapControl::CapControlType objectType = SpecialArea; objectType >= Feeder; objectType = Cti::CapControl::CapControlType(int(objectType) -1))
                {
                    string paObjectColumn = getDbColumnString(objectType);
                    string capControlObjectTable = getDbTableString(objectType);

                    if ( !paObjectColumn.empty() )
                    {
                        const string sql = "SELECT SSA.paobjectid, SSA.seasonscheduleid, SSA.seasonname, "
                                                 "SSA.strategyid, DS.seasonstartmonth, DS.seasonendmonth, "
                                                 "DS.seasonstartday, DS.seasonendday "
                                           "FROM ccseasonstrategyassignment SSA, dateofseason DS, "
                                             + capControlObjectTable +
                                           "WHERE SSA.seasonscheduleid = DS.seasonscheduleid AND "
                                                 "SSA.seasonname = DS.seasonname AND SSA.strategyid = ? "
                                                 "AND SSA.paobjectid = " + paObjectColumn;

                        Cti::Database::DatabaseConnection connection;
                        Cti::Database::DatabaseReader dbRdr(connection, sql);

                        dbRdr << strategyId;

                        dbRdr.execute();

                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                             CTILOG_INFO(dout, dbRdr.asString());
                        }

                        while ( dbRdr() )
                        {
                            int startMon, startDay, endMon, endDay;
                            dbRdr["seasonstartmonth"] >> startMon;
                            dbRdr["seasonendmonth"] >> endMon;
                            dbRdr["seasonstartday"] >> startDay;
                            dbRdr["seasonendday"] >> endDay;

                            CtiDate today = CtiDate();

                            if (today >= CtiDate(startDay, startMon, today.year()) &&
                                today <= CtiDate(endDay, endMon, today.year())  )
                            {
                                assignStrategyToCCObject(dbRdr, objectType);
                             }
                         }
                     }
                }

                CtiHolidayManager::getInstance().refresh();
                if (CtiHolidayManager::getInstance().isHolidayForAnySchedule(CtiDate()) )
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {
                        CTILOG_DEBUG(dout, "HOLIDAY!  CELEBRATE...");
                    }

                    reloadAndAssignHolidayStrategysFromDatabase(strategyId);

                }
                else
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {
                        CTILOG_DEBUG(dout, "No HOLIDAY!  No CELEBRATE...");
                    }
                }
            }
        }

        // After the strategy has been properly assigned everywhere, restore the states
        if (strategyId == -1)
        {
            _strategyManager->restoreAllStates();
        }
        else
        {
            _strategyManager->restoreStates(strategyId);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return true;
}




/*---------------------------------------------------------------------------
    reloadStrategyFromDataBase

    Reloads all or single strategy from strategy table in the database.  Updates
    SubStationBus/Feeder Strategy values if a single strategyId is specified.
    StrategyId = 0 indicates reloaded all strategies from db.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadAndAssignHolidayStrategysFromDatabase(long strategyId)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    try
    {
        CtiTime currentDateTime;

        if (strategyId >= 0)
        {
            long paObjectId;
            //Update Area Strategy Values with dbChanged/editted strategy values
            for (Cti::CapControl::CapControlType objectType = Feeder; objectType <= SpecialArea; objectType = Cti::CapControl::CapControlType(int(objectType) + 1))
            {
                string paObjectColumn = getDbColumnString(objectType);
                string capControlObjectTable = getDbTableString(objectType);
                if ( !paObjectColumn.empty() )
                {

                     const string sqlMain =  "SELECT HSA.paobjectid, HSA.holidayscheduleid, HSA.strategyid, "
                                               "DH.holidayname, DH.holidaymonth, DH.holidayday, DH.holidayyear "
                                             "FROM ccholidaystrategyassignment HSA, dateofholiday DH, "
                                               + capControlObjectTable +
                                             "WHERE HSA.holidayscheduleid = DH.holidayscheduleid AND "
                                               "HSA.strategyid = ? AND HSA.paobjectid = " + paObjectColumn;

                     Cti::Database::DatabaseConnection connection;
                     Cti::Database::DatabaseReader rdr(connection, sqlMain);

                     rdr << strategyId;

                     rdr.execute();

                     if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                     {
                         CTILOG_INFO(dout, rdr.asString());
                     }

                     while ( rdr() )
                     {
                         int holMon, holDay, holYear, tempYear;
                         rdr["holidaymonth"] >> holMon;
                         rdr["holidayday"] >> holDay;
                         rdr["holidayyear"] >> holYear;

                         CtiDate today = CtiDate();
                         if (holYear == -1)
                         {
                             tempYear = today.year();
                         }
                         else
                             tempYear = holYear;

                         if (today == CtiDate(holDay, holMon, tempYear) )
                         {
                             assignStrategyToCCObject(rdr, objectType);
                         }
                     }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*---------------------------------------------------------------------------
    reloadStrategyFromDataBase

    Reloads all or single strategy from strategy table in the database.  Updates
    SubStationBus/Feeder Strategy values if a single strategyId is specified.
    StrategyId = 0 indicates reloaded all strategies from db.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadTimeOfDayStrategyFromDatabase(long strategyId)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    try
{
        CtiTime currentDateTime;

        if (strategyId >= 0)
        {
            //Update Area Strategy Values with dbChanged/editted strategy values
            for (Cti::CapControl::CapControlType objectType = SpecialArea; objectType >= Feeder; objectType = Cti::CapControl::CapControlType(int(objectType) -1))
            {
                string paObjectColumn = getDbColumnString(objectType);
                string capControlObjectTable = getDbTableString(objectType);

                if ( !paObjectColumn.empty() )
                {
                   const string sql =  "SELECT SSA.paobjectid, SSA.seasonscheduleid, SSA.seasonname, "
                                         "SSA.strategyid, DS.seasonstartmonth, DS.seasonendmonth, "
                                         "DS.seasonstartday, DS.seasonendday "
                                       "FROM ccseasonstrategyassignment SSA, dateofseason DS, " + capControlObjectTable +
                                       "WHERE SSA.seasonscheduleid = DS.seasonscheduleid AND "
                                         "SSA.seasonname = DS.seasonname AND SSA.strategyid = ? "
                                         "AND SSA.paobjectid = " + paObjectColumn;

                   Cti::Database::DatabaseConnection connection;
                   Cti::Database::DatabaseReader dbRdr(connection, sql);

                   dbRdr << strategyId;

                   dbRdr.execute();

                   if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                   {
                       CTILOG_INFO(dout, dbRdr.asString());
                   }

                   while ( dbRdr() )
                   {
                       int startMon, startDay, endMon, endDay;
                       dbRdr["seasonstartmonth"] >> startMon;
                       dbRdr["seasonendmonth"] >> endMon;
                       dbRdr["seasonstartday"] >> startDay;
                       dbRdr["seasonendday"] >> endDay;

                       CtiDate today = CtiDate();

                       if (today  >= CtiDate(startDay, startMon, today.year()) &&
                           today <= CtiDate(endDay, endMon, today.year())  )
                       {
                           assignStrategyToCCObject(dbRdr, objectType);
                       }
                   }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

namespace {

std::set<long> loadSubstationIntoMap(const long substationId, PaoIdToSubstationMap* paobject_substation_map, const PaoIdToSpecialAreaMap& paobject_specialarea_map);
void loadSubstationPoints(const long substationId, const PaoIdToSubstationMap* paobject_substation_map, std::multimap<long, CapControlPao*>& pointid_to_pao, const std::set<long>& modifiedPaoIDs, PointIdToSubstationMultiMap *pointid_station_map);
void loadSubstationAreaAssignments(const long substationId, const PaoIdToSubstationMap* paobject_substation_map, const PaoIdToAreaMap* paobject_area_map, ChildToParentMap* substation_area_map, CtiCCSubstation_vec* ccSubstations);
void loadSubstationSpecialAreaAssignments(const long substationId, const PaoIdToSubstationMap* paobject_substation_map, const PaoIdToSpecialAreaMap* paobject_specialarea_map, ChildToParentMultiMap* substation_specialarea_map, CtiCCSubstation_vec* ccSubstations);
std::vector<long> loadSubstationSubBuses(const long substationId);

}

/*---------------------------------------------------------------------------
    reloadSubstationFromDatabase

    Reloads one (if a positive substationId is supplied)
        or all substations from the database
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadSubstationFromDatabase(long substationId,
                                  PaoIdToSubstationMap *paobject_substation_map,
                                  PaoIdToAreaMap *paobject_area_map,
                                  PaoIdToSpecialAreaMap *paobject_specialarea_map,
                                  PointIdToSubstationMultiMap *pointid_station_map,
                                  ChildToParentMap *substation_area_map,
                                  ChildToParentMultiMap *substation_specialarea_map,
                                  CtiCCSubstation_vec *ccSubstations)
try
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    {
        if ( substationId > 0 )
        {
            deleteSubstation( substationId );
        }

        const auto modifiedPaoIDs = loadSubstationIntoMap(substationId, paobject_substation_map, *paobject_specialarea_map);

        loadSubstationPoints(substationId, paobject_substation_map, _pointID_to_pao, modifiedPaoIDs, pointid_station_map);

        loadSubstationAreaAssignments(substationId, paobject_substation_map, paobject_area_map, substation_area_map, ccSubstations);
        loadSubstationSpecialAreaAssignments(substationId, paobject_substation_map, paobject_specialarea_map, substation_specialarea_map, ccSubstations);

        if ( substationId > 0 ) // else, when reloading all, then the reload of feeders will be called after subBusReload and take care of it.
        {
            const auto reloadBusIDs = loadSubstationSubBuses(substationId);

            for ( const long currentSubBusId : reloadBusIDs )
            {
                reloadSubBusFromDatabase( currentSubBusId, &_paobject_subbus_map,
                    &_paobject_substation_map, &_pointid_subbus_map,
                    &_altsub_sub_idmap, &_subbus_substation_map,
                    _ccSubstationBuses );
            }

            reloadOperationStatsFromDatabase( substationId, &_paobject_capbank_map,
                                              &_paobject_feeder_map, &_paobject_subbus_map,
                                              &_paobject_substation_map, &_paobject_area_map,
                                              &_paobject_specialarea_map);

            // start point registrations for this particular substation
            if ( CtiCCSubstationPtr station = findSubstationByPAObjectID( substationId ) )
            {
                CtiCapController::getInstance()->registerPaoForPointUpdates( *station );
            }
        }
    }
}
catch ( ... )
{
    CTILOG_UNKNOWN_EXCEPTION_ERROR( dout );
}

namespace {

std::set<long> loadSubstationIntoMap(const long substationId, PaoIdToSubstationMap* paobject_substation_map, const PaoIdToSpecialAreaMap& paobject_specialarea_map)
{
    static const std::string sql =
        "SELECT "
            "Y.PAObjectID, "
            "Y.Category, "
            "Y.PAOClass, "
            "Y.PAOName, "
            "Y.Type, "
            "Y.Description, "
            "Y.DisableFlag, "
            "S.VoltReductionPointId, "
            "D.AdditionalFlags, "
            "D.SAEnabledID "
        "FROM "
            "YukonPAObject Y "
                "JOIN CAPCONTROLSUBSTATION S "
                    "ON Y.PAObjectID = S.SubstationID "
                "LEFT OUTER JOIN DYNAMICCCSUBSTATION D "
                    "ON S.SubstationID = D.SubStationID";

    static const std::string sqlID = sql +
        " WHERE Y.PAObjectID = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader     rdr( connection );

    if ( substationId > 0 )
    {
        rdr.setCommandText( sqlID );
        rdr << substationId;
    }
    else
    {
        rdr.setCommandText( sql );
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO( dout, rdr.asString() );
    }

    std::set<long> modifiedPaoIDs;

    while ( rdr() )
    {
        CtiCCSubstationPtr currentCCSubstation = CtiCCSubstationPtr( new CtiCCSubstation( rdr ) );

        paobject_substation_map->emplace( currentCCSubstation->getPaoId(), currentCCSubstation );

        if ( ! paobject_specialarea_map.count( currentCCSubstation->getSaEnabledId() ) )
        {
            currentCCSubstation->setSaEnabledId( 0 );
        }

        modifiedPaoIDs.insert( currentCCSubstation->getPaoId() );
    }

    return modifiedPaoIDs;
}


void loadSubstationPoints(const long substationId, const PaoIdToSubstationMap* paobject_substation_map, std::multimap<long, CapControlPao*>& pointid_to_pao, const std::set<long>& modifiedPaoIDs, PointIdToSubstationMultiMap *pointid_station_map)
{
    {
        static const std::string sql = 
            "SELECT "
                "P.POINTID, "
                "P.POINTTYPE, "
                "P.PAObjectID, "
                "P.POINTOFFSET "
            "FROM "
                "POINT P "
                    "JOIN CAPCONTROLSUBSTATION S "
                        "ON P.PAObjectID = S.SubstationID";

        static const std::string sqlID = sql +
            " WHERE P.PAObjectID = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader     rdr( connection );

        if ( substationId > 0 )
        {
            rdr.setCommandText( sqlID );
            rdr << substationId;
        }
        else
        {
            rdr.setCommandText( sql );
        }

        rdr.execute();

        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
        {
            CTILOG_INFO( dout, rdr.asString() );
        }

        while ( rdr() )
        {
            long substationID;

            rdr["PAObjectID"] >> substationID;

            if ( auto substation = Cti::mapFindPtr( *paobject_substation_map, substationID ) )
            {
                substation->assignPoint( rdr );
            }
        }
    }

    for ( const long paoID : modifiedPaoIDs )
    {
        if ( auto substation = Cti::mapFindPtr( *paobject_substation_map, paoID ) )
        {
            for ( const long pointID : *substation->getPointIds() )
            {
                pointid_station_map->emplace( pointID, substation );

                pointid_to_pao.emplace( pointID, substation );
            }
        }
    }
}

void loadSubstationAreaAssignments(const long substationId, const PaoIdToSubstationMap* paobject_substation_map, const PaoIdToAreaMap* paobject_area_map, ChildToParentMap* substation_area_map, CtiCCSubstation_vec* ccSubstations)
{
    static const std::string sql = 
        "SELECT "
            "SubstationBusID, "  //  actually SubstationID, which is horrible and confusing
            "AreaID "
        "FROM "
            "CCSUBAREAASSIGNMENT";

    static const std::string sqlID = sql +
        " WHERE SubstationBusID = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader     rdr( connection );

    if ( substationId > 0 )
    {
        rdr.setCommandText( sqlID );
        rdr << substationId;
    }
    else
    {
        rdr.setCommandText( sql );
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO( dout, rdr.asString() );
    }

    while ( rdr() ) 
    {
        const long substationID = rdr["SubstationBusID"].as<long>();

        if ( auto substation = Cti::mapFindPtr( *paobject_substation_map, substationID ) )
        {
            const long areaID = rdr["AreaID"].as<long>();

            if ( auto area = Cti::mapFind( *paobject_area_map, areaID ) )
            {
                substation->setParentId( areaID );

                (*area)->addSubstationId( substationID );

                ccSubstations->push_back( substation );

                substation_area_map->emplace( substationID, areaID );
            }
        }
    }
}

void loadSubstationSpecialAreaAssignments(const long substationId, const PaoIdToSubstationMap* paobject_substation_map, const PaoIdToSpecialAreaMap* paobject_specialarea_map, ChildToParentMultiMap* substation_specialarea_map, CtiCCSubstation_vec* ccSubstations)
{
    static const std::string sql = 
        "SELECT "
            "SubstationBusID, "  //  actually SubstationID, which is horrible and confusing
            "AreaID "
        "FROM "
            "CCSUBSPECIALAREAASSIGNMENT";

    static const std::string sqlID = sql +
        " WHERE SubstationBusID = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader     rdr( connection );

    if ( substationId > 0 )
    {
        rdr.setCommandText( sqlID );
        rdr << substationId;
    }
    else
    {
        rdr.setCommandText( sql );
    }

    rdr.execute();

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO( dout, rdr.asString() );
    }

    while ( rdr() ) 
    {
        const long substationID = rdr["SubstationBusID"].as<long>();

        if ( auto substation = Cti::mapFindPtr( *paobject_substation_map, substationID ) )
        {
            long specialAreaID = rdr["AreaID"].as<long>();

            if ( auto specialArea = Cti::mapFind( *paobject_specialarea_map, specialAreaID ) )
            {
                //  If the substation doesn't have an assigned special area ID yet, 
                //    set the first one as its special area.
                if ( substation->getSaEnabledId() == 0 )
                {
                    substation->setSaEnabledId( specialAreaID );
                }
                //  However, if we encounter an active special area,
                //    override any other special area ID with the 
                //    current and mark it as enabled.
                if ( ! (*specialArea)->getDisableFlag() )
                {
                    substation->setSaEnabledId( specialAreaID );
                    substation->setSaEnabledFlag( true );
                }
                //  If the substation doesn't have a parent yet, mark this special area as 
                //    its parent and add it to the ccSubstations messaging vector.
                if ( substation->getParentId() <= 0 )
                {
                    substation->setParentId( specialAreaID );

                    ccSubstations->push_back( substation );
                }

                (*specialArea)->addSubstationId( substationID );

                substation_specialarea_map->emplace( substationID, specialAreaID );
            }
        }
    }
}

std::vector<long> loadSubstationSubBuses(const long substationId)
{
    std::vector<long>   reloadBusIDs;

    {
        static const std::string sql = 
            "SELECT "
                "SubStationBusID "
            "FROM "
                "CCSUBSTATIONSUBBUSLIST "
            "WHERE "
                "SubStationID = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader     rdr( connection, sql );

        rdr << substationId;

        rdr.execute();

        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
        {
            CTILOG_INFO( dout, rdr.asString() );
        }

        while ( rdr() )
        {
            reloadBusIDs.push_back( rdr["SubStationBusID"].as<long>() );
        }
    }

    return reloadBusIDs;
}

}

/*---------------------------------------------------------------------------
    reloadAreaFromDatabase

    Reloads one (if a positive areaId is supplied)
        or all areas from the database
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadAreaFromDatabase(long areaId,
                                  PaoIdToAreaMap *paobject_area_map,
                                  PointIdToAreaMultiMap *pointid_area_map,
                                  CtiCCArea_vec *ccGeoAreas)
try
{
    PaoIdToAreaMap      loadedAreas;
    std::vector<long>   substations;

    {
        Cti::Database::DatabaseConnection connection;

        {
            static const std::string sql = 
                "SELECT "
                    "Y.PAObjectID, "
                    "Y.Category, "
                    "Y.PAOClass, "
                    "Y.PAOName, "
                    "Y.Type, "
                    "Y.Description, "
                    "Y.DisableFlag, "
                    "A.VoltReductionPointID, "
                    "D.additionalflags, "
                    "D.ControlValue "
                "FROM "
                    "YukonPAObject Y "
                        "JOIN CAPCONTROLAREA A "
                            "ON Y.PAObjectID = A.AreaID "
                        "LEFT OUTER JOIN DYNAMICCCAREA D "
                            "ON A.AreaID = D.AreaID";

            static const std::string sqlID = sql +
                " WHERE Y.PAObjectID = ?";

            Cti::Database::DatabaseReader   rdr( connection );

            if ( areaId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << areaId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                CtiCCAreaPtr currentCCArea = CtiCCAreaPtr( new CtiCCArea( rdr, _strategyManager.get() ) );

                loadedAreas.emplace( currentCCArea->getPaoId(), currentCCArea );
            }
        }

        {
            static const std::string sql = 
                "SELECT "
                    "P.POINTID, "
                    "P.POINTTYPE, "
                    "P.PAObjectID, "
                    "P.POINTOFFSET "
                "FROM "
                    "POINT P "
                        "JOIN CAPCONTROLAREA A "
                            "ON P.PAObjectID = A.AreaID";

            static const std::string sqlID = sql +
                " WHERE P.PAObjectID = ?";

            Cti::Database::DatabaseReader   rdr( connection );

            if ( areaId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << areaId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long currentAreaId;

                rdr["PAObjectID"] >> currentAreaId;

                if ( auto currentArea = Cti::mapFind( loadedAreas, currentAreaId ) )
                {
                    (*currentArea)->assignPoint( rdr );
                }
            }
        }

        const CtiDate   today;

        {
            static const std::string sql = 
                "SELECT "
                    "S.seasonscheduleid, "
                    "S.seasonname, "
                    "S.paobjectid, "
                    "S.strategyid  "
                "FROM "
                    "CCSEASONSTRATEGYASSIGNMENT S "
                        "JOIN CAPCONTROLAREA A "
                            "ON S.paobjectid = A.AreaID";

            static const std::string sqlID = sql +
                " WHERE S.paobjectid = ?";

            Cti::Database::DatabaseReader   rdr( connection );

            if ( areaId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << areaId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long        scheduleID;
                std::string seasonName;

                rdr["seasonscheduleid"] >> scheduleID;
                rdr["seasonname"]       >> seasonName;

                if ( CtiSeasonManager::getInstance().isInNamedSeason( scheduleID, seasonName, today ) )
                {
                    long currentAreaID,
                         strategyID;

                    rdr["paobjectid"] >> currentAreaID;
                    rdr["strategyid"] >> strategyID;

                    if ( auto currentArea = Cti::mapFind( loadedAreas, currentAreaID ) )
                    {
                        (*currentArea)->setStrategy( strategyID );
                    }
                }
            }
        }

        if ( CtiHolidayManager::getInstance().isHolidayForAnySchedule( today ) )
        {
            static const std::string sql = 
                "SELECT "
                    "H.HolidayScheduleId, "
                    "H.PAObjectId, "
                    "H.StrategyId "
                "FROM "
                    "CCHOLIDAYSTRATEGYASSIGNMENT H "
                        "JOIN CAPCONTROLAREA A "
                            "ON H.PAObjectId = A.AreaID";

            static const std::string sqlID = sql +
                " WHERE H.PAObjectId = ?";

            Cti::Database::DatabaseReader   rdr( connection );

            if ( areaId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << areaId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long scheduleID;

                rdr["HolidayScheduleId"] >> scheduleID;

                if ( CtiHolidayManager::getInstance().isHoliday( scheduleID, today ) )
                {
                    long currentAreaID,
                         strategyID;

                    rdr["PAObjectId"] >> currentAreaID;
                    rdr["StrategyId"] >> strategyID;

                    if ( auto currentArea = Cti::mapFind( loadedAreas, currentAreaID ) )
                    {
                        (*currentArea)->setStrategy( strategyID );
                    }
                }
            }
        }

        if ( areaId > 0 )
        {
            static const std::string sql = 
                "SELECT "
                    "SubstationBusID "          // <--- ummm, this should be SubstationID
                "FROM "
                    "CCSUBAREAASSIGNMENT "
                "WHERE "
                    "AreaID = ?";

            Cti::Database::DatabaseReader   rdr( connection, sql );

            rdr << areaId;

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long substationID;

                rdr["SubstationBusID"] >> substationID;

                substations.push_back( substationID ); 
            }
        }
    }

    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    if ( areaId > 0 )
    {
        deleteArea( areaId );
    }

    for ( auto idAreaPair : loadedAreas )
    {
        const CtiCCAreaPtr  area = idAreaPair.second;

        paobject_area_map->insert( idAreaPair );

        ccGeoAreas->push_back( area );

        for ( const long pointID : *area->getPointIds() )
        {
            pointid_area_map->emplace( pointID, area );

            _pointID_to_pao.emplace( pointID, area );
        }
    }

    if ( areaId > 0 ) // else, when reloading all, then the reload of substations will be called afterwards and take care of it.
    {
        // if areaId <= 0 then this collection will be empty anyway...

        for ( const long substationID : substations )
        {
            reloadSubstationFromDatabase( substationID, &_paobject_substation_map,
                                          &_paobject_area_map, &_paobject_specialarea_map,
                                          &_pointid_station_map, &_substation_area_map,
                                          &_substation_specialarea_map, &_ccSubstations );
        }

        reloadOperationStatsFromDatabase( areaId, &_paobject_capbank_map, &_paobject_feeder_map,
                                          &_paobject_subbus_map, &_paobject_substation_map, 
                                          &_paobject_area_map, &_paobject_specialarea_map );

        cascadeAreaStrategySettings( findAreaByPAObjectID( areaId ) );

        // start point registrations for this particular area
        if ( CtiCCAreaPtr area = findAreaByPAObjectID( areaId ) )
        {
            CtiCapController::getInstance()->registerPaoForPointUpdates( *area );
        }
    }
}
catch ( ... )
{
    CTILOG_UNKNOWN_EXCEPTION_ERROR( dout );
}


/*---------------------------------------------------------------------------
    reloadSpecialAreaFromDatabase

    Reloads all special areas form the database
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadSpecialAreaFromDatabase( PaoIdToSpecialAreaMap *paobject_specialarea_map,
                                                             PointIdToSpecialAreaMultiMap *pointid_specialarea_map,
                                                             CtiCCSpArea_vec *ccSpecialAreas)
try
{
    PaoIdToSpecialAreaMap   loadedAreas;

    {
        Cti::Database::DatabaseConnection connection;

        {
            static const std::string sql = 
                "SELECT "
                    "Y.PAObjectID, "
                    "Y.Category, "
                    "Y.PAOClass, "
                    "Y.PAOName, "
                    "Y.Type, "
                    "Y.Description, "
                    "Y.DisableFlag, "
                    "A.VoltReductionPointID, "
                    "D.additionalflags, "
                    "D.ControlValue "
                "FROM "
                    "YukonPAObject Y "
                        "JOIN CAPCONTROLSPECIALAREA A "
                            "ON Y.PAObjectID = A.AreaID "
                        "LEFT OUTER JOIN DYNAMICCCSPECIALAREA D "
                            "ON A.AreaID = D.AreaID";

            Cti::Database::DatabaseReader   rdr( connection, sql );

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                CtiCCSpecialPtr currentCCSpArea = CtiCCSpecialPtr( new CtiCCSpecial( rdr, _strategyManager.get() ) );

                loadedAreas.emplace( currentCCSpArea->getPaoId(), currentCCSpArea );
            }
        }

        {
            static const std::string sql = 
                "SELECT "
                    "P.POINTID, "
                    "P.POINTTYPE, "
                    "P.PAObjectID, "
                    "P.POINTOFFSET "
                "FROM "
                    "POINT P "
                        "JOIN CAPCONTROLSPECIALAREA A "
                            "ON P.PAObjectID = A.AreaID";

            Cti::Database::DatabaseReader   rdr( connection, sql );

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long currentAreaId;

                rdr["PAObjectID"] >> currentAreaId;

                if ( auto currentSpArea = Cti::mapFind( loadedAreas, currentAreaId ) )
                {
                    (*currentSpArea)->assignPoint( rdr );
                }
            }
        }

        const CtiDate   today;

        {
            static const std::string sql = 
                "SELECT "
                    "S.seasonscheduleid, "
                    "S.seasonname, "
                    "S.paobjectid, "
                    "S.strategyid  "
                "FROM "
                    "CCSEASONSTRATEGYASSIGNMENT S "
                        "JOIN CAPCONTROLSPECIALAREA A "
                            "ON S.paobjectid = A.AreaID";

            Cti::Database::DatabaseReader   rdr( connection, sql );

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long        scheduleID;
                std::string seasonName;

                rdr["seasonscheduleid"] >> scheduleID;
                rdr["seasonname"]       >> seasonName;

                if ( CtiSeasonManager::getInstance().isInNamedSeason( scheduleID, seasonName, today ) )
                {
                    long currentAreaID,
                         strategyID;

                    rdr["paobjectid"] >> currentAreaID;
                    rdr["strategyid"] >> strategyID;

                    if ( auto currentSpArea = Cti::mapFind( loadedAreas, currentAreaID ) )
                    {
                        (*currentSpArea)->setStrategy( strategyID );
                    }
                }
            }
        }

        if ( CtiHolidayManager::getInstance().isHolidayForAnySchedule( today ) )
        {
            static const std::string sql = 
                "SELECT "
                    "H.HolidayScheduleId, "
                    "H.PAObjectId, "
                    "H.StrategyId "
                "FROM "
                    "CCHOLIDAYSTRATEGYASSIGNMENT H "
                        "JOIN CAPCONTROLSPECIALAREA A "
                            "ON H.PAObjectId = A.AreaID";

            Cti::Database::DatabaseReader   rdr( connection, sql );

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long scheduleID;

                rdr["HolidayScheduleId"] >> scheduleID;

                if ( CtiHolidayManager::getInstance().isHoliday( scheduleID, today ) )
                {
                    long currentAreaID,
                         strategyID;

                    rdr["PAObjectId"] >> currentAreaID;
                    rdr["StrategyId"] >> strategyID;

                    if ( auto currentSpArea = Cti::mapFind( loadedAreas, currentAreaID ) )
                    {
                        (*currentSpArea)->setStrategy( strategyID );
                    }
                }
            }
        }
    }

    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    for ( auto idAreaPair : loadedAreas )
    {
        const CtiCCSpecialPtr   specialArea = idAreaPair.second;

        paobject_specialarea_map->insert( idAreaPair );

        ccSpecialAreas->push_back( specialArea );

        for ( const long pointID : *specialArea->getPointIds() )
        {
            pointid_specialarea_map->emplace( pointID, specialArea );

            _pointID_to_pao.emplace( pointID, specialArea );
        }

        // start point registrations for this particular special area
        CtiCapController::getInstance()->registerPaoForPointUpdates( *specialArea );
    }
}
catch ( ... )
{
    CTILOG_UNKNOWN_EXCEPTION_ERROR( dout );
}

/*---------------------------------------------------------------------------
    reloadSubBusFromDB

    Reloads a single subbus from the database.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadSubBusFromDatabase(long subBusId,
                                                       PaoIdToSubBusMap *paobject_subbus_map,
                                                       PaoIdToSubstationMap *paobject_substation_map,
                                                       PointIdToSubBusMultiMap *pointid_subbus_map,
                                                       PaoIdToPointIdMultiMap *altsub_sub_idmap,
                                                       ChildToParentMap *subbus_substation_map,
                                                       CtiCCSubstationBus_vec *cCSubstationBuses )
{
    CtiCCSubstationBusPtr subBusToUpdate = NULL;

    if (subBusId > 0)
    {
        subBusToUpdate = findSubBusByPAObjectID(subBusId);
    }

    bool reEnableDualBusForThisBus = false;

    if ( subBusToUpdate )
    {
        const long altSubID = subBusToUpdate->getAltDualSubId();

        if ( altSubID > 0 )
        {
            CtiCCSubstationBusPtr altSub = findSubBusByPAObjectID( altSubID );

            reEnableDualBusForThisBus = ( altSub && altSub->getPrimaryBusFlag() );

            // disable dual bus until we finish reloading the bus

            if ( reEnableDualBusForThisBus )
            {
                CtiCapController::getInstance()->handleAlternateBusModeValues( subBusToUpdate->getSwitchOverPointId(),
                                                                               0.0,
                                                                               subBusToUpdate );
            }

            dumpAllDynamicData();
        }
    }

    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    try
    {
        if (subBusToUpdate != NULL)
        {
            deleteSubBus(subBusId);
        }

        CtiTime currentDateTime;
        {
            static const std::string sql = 
                "SELECT "
                    "Y.PAObjectID, "
                    "Y.Category, "
                    "Y.PAOClass, "
                    "Y.PAOName, "
                    "Y.Type, "
                    "Y.Description, "
                    "Y.DisableFlag, "
                    "B.CurrentVarLoadPointID, "
                    "B.CurrentWattLoadPointID, "
                    "B.MapLocationID, "
                    "B.CurrentVoltLoadPointID, "
                    "B.AltSubID, "
                    "B.SwitchPointID, "
                    "B.DualBusEnabled, "
                    "B.MultiMonitorControl, "
                    "B.usephasedata, "
                    "B.phaseb, "
                    "B.phasec, "
                    "B.ControlFlag, "
                    "B.VoltReductionPointId, "
                    "B.DisableBusPointId, "
                    "D.CurrentVarPointValue, "
                    "D.CurrentWattPointValue, "
                    "D.NextCheckTime, "
                    "D.NewPointDataReceivedFlag, "
                    "D.BusUpdatedFlag, "
                    "D.LastCurrentVarUpdateTime, "
                    "D.EstimatedVarPointValue, "
                    "D.CurrentDailyOperations, "
                    "D.PeakTimeFlag, "
                    "D.RecentlyControlledFlag, "
                    "D.LastOperationTime, "
                    "D.VarValueBeforeControl, "
                    "D.LastFeederPAOid, "
                    "D.LastFeederPosition, "
                    "D.PowerFactorValue, "
                    "D.KvarSolution, "
                    "D.EstimatedPFValue, "
                    "D.CurrentVarPointQuality, "
                    "D.WaiveControlFlag, "
                    "D.AdditionalFlags, "
                    "D.CurrVerifyCBId, "
                    "D.CurrVerifyFeederId, "
                    "D.CurrVerifyCBOrigState, "
                    "D.VerificationStrategy, "
                    "D.CbInactivityTime, "
                    "D.CurrentVoltPointValue, "
                    "D.SwitchPointStatus, "
                    "D.AltSubControlValue, "
                    "D.EventSeq, "
                    "D.CurrentWattPointQuality, "
                    "D.CurrentVoltPointQuality, "
                    "D.iVControlTot, "
                    "D.iVCount, "
                    "D.iWControlTot, "
                    "D.iWCount, "
                    "D.phaseavalue, "
                    "D.phasebvalue, "
                    "D.phasecvalue, "
                    "D.LastWattPointTime, "
                    "D.LastVoltPointTime, "
                    "D.PhaseAValueBeforeControl, "
                    "D.PhaseBValueBeforeControl, "
                    "D.PhaseCValueBeforeControl, "
                    "P.DECIMALPLACES "
                "FROM "
                    "YukonPAObject Y "
                        "JOIN CAPCONTROLSUBSTATIONBUS B "
                            "ON Y.PAObjectID = B.SubstationBusID "
                        "LEFT OUTER JOIN DynamicCCSubstationBus D "
                            "ON B.SubstationBusID = D.SubstationBusID "
                        "LEFT OUTER JOIN POINTUNIT P "
                            "ON B.CurrentVarLoadPointID = P.POINTID";

            static const std::string sqlID = sql +
                " WHERE "
                    "Y.PAObjectID = ? OR B.AltSubID = ?";

            Cti::Database::DatabaseConnection   connection;
            Cti::Database::DatabaseReader       rdr( connection );

            if ( subBusId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << subBusId
                    << subBusId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long busId;
                long altBusId;

                rdr["PAObjectID"] >> busId;
                rdr["AltSubID"]   >> altBusId;

                CtiCCSubstationBusPtr  currentCCSubstationBus;

                if (subBusId > 0 && busId != subBusId && altBusId == subBusId && paobject_subbus_map->find(busId) != paobject_subbus_map->end() )
                {
                    currentCCSubstationBus = findInMap(busId, paobject_subbus_map);
                }
                else
                {
                    currentCCSubstationBus = CtiCCSubstationBusPtr(new CtiCCSubstationBus(rdr, _strategyManager.get()));
                    paobject_subbus_map->insert(make_pair(currentCCSubstationBus->getPaoId(),currentCCSubstationBus));
                }

                for ( const long pointID : *currentCCSubstationBus->getPointIds() )
                {
                    pointid_subbus_map->emplace( pointID, currentCCSubstationBus );
                }

                if (currentCCSubstationBus->getDualBusEnable() &&
                    currentCCSubstationBus->getAltDualSubId() != currentCCSubstationBus->getPaoId())
                {
                    altsub_sub_idmap->insert(make_pair(currentCCSubstationBus->getAltDualSubId(), currentCCSubstationBus->getPaoId()));
                }
            }
        }
        {
            static const string sqlNoID = "SELECT SBL.substationid, SBL.substationbusid, SBL.displayorder "
                                          "FROM ccsubstationsubbuslist SBL";
            static const string sqlAppendOrder = " ORDER BY SBL.substationid, SBL.displayorder";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader dbRdr(connection);

            if( subBusId > 0 )
            {
                static const string sqlID = string(sqlNoID + " WHERE SBL.substationbusid = ?" + sqlAppendOrder);
                dbRdr.setCommandText(sqlID);
                dbRdr << subBusId;
            }
            else
            {
                dbRdr.setCommandText(sqlNoID + sqlAppendOrder);
            }


            dbRdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, dbRdr.asString());
            }

            while ( dbRdr() )
            {
                long currentSubstationId;
                long currentSubBusId;
                long displayOrder;
                dbRdr["substationid"] >> currentSubstationId;
                dbRdr["substationbusid"] >> currentSubBusId;
                dbRdr["displayorder"] >>displayOrder;

                if (CtiCCSubstationBusPtr currentCCSubstationBus = findInMap(currentSubBusId, paobject_subbus_map))
                {
                    currentCCSubstationBus->setParentId(currentSubstationId);
                    currentCCSubstationBus->setDisplayOrder(displayOrder);
                    
                    const auto currentCCSubstation = Cti::mapFindPtr(*paobject_substation_map, currentSubstationId);

                    if (currentCCSubstation != NULL)
                    {
                        currentCCSubstationBus->setParentName(currentCCSubstation->getPaoName());
                        currentCCSubstation->addCCSubId(currentSubBusId);
                    }

                    cCSubstationBuses->push_back(currentCCSubstationBus);
                    subbus_substation_map->insert(make_pair(currentSubBusId, currentSubstationId));
                }
            }
        }
        {
             static const string sqlNoID =  "SELECT SSA.paobjectid, SSB.substationbusid, CCA.areaid, SSA.seasonscheduleid, SSA.seasonname, "
                                              "SSA.strategyid, DS.seasonstartmonth, DS.seasonendmonth, "
                                              "DS.seasonstartday, DS.seasonendday "
                                            "FROM ccseasonstrategyassignment SSA "
                                              "left join capcontrolsubstationbus SSB on SSB.SubstationBusID = SSA.paobjectid "
                                              "left join capcontrolarea CCA on CCA.AreaID = SSA.paobjectid "
                                              "join  dateofseason DS on SSA.seasonscheduleid = DS.seasonscheduleid AND SSA.seasonname = DS.seasonname ";

             Cti::Database::DatabaseConnection connection;
             Cti::Database::DatabaseReader dbRdr(connection);

             if( subBusId > 0 )
             {
                 string sqlID = string(sqlNoID + " WHERE  SSA.paobjectid = ?");
                 dbRdr.setCommandText(sqlID);
                 dbRdr << subBusId;

                 CtiCCSubstationPtr currentStation = findSubstationByPAObjectID(findSubstationIDbySubBusID(subBusId) != NULL ? findSubstationIDbySubBusID(subBusId) : 0 );
                 if (currentStation != NULL)
                 {
                     sqlID += string(" OR SSA.paobjectid = ? ORDER BY SSB.substationbusid");
                     dbRdr.setCommandText(sqlID);
                     dbRdr << subBusId;
                     dbRdr << currentStation->getParentId();
                 }

             }
             else
             {
                 string sqlID = string(sqlNoID + " ORDER BY SSB.substationbusid ");
                 dbRdr.setCommandText(sqlID);
             }

             dbRdr.execute();

             while ( dbRdr() )
             {
                 int startMon, startDay, endMon, endDay;
                 dbRdr["seasonstartmonth"] >> startMon;
                 dbRdr["seasonendmonth"] >> endMon;
                 dbRdr["seasonstartday"] >> startDay;
                 dbRdr["seasonendday"] >> endDay;

                 CtiDate today = CtiDate();

                 if (today  >= CtiDate(startDay, startMon, today.year()) &&
                      today <= CtiDate(endDay, endMon, today.year())  )
                 {
                     long busId, stratId, areaId = 0;
                     CtiCCSubstationBusPtr currentCCSubstationBus = NULL;
                     if (subBusId > 0)
                         currentCCSubstationBus = findSubBusByPAObjectID(subBusId);

                     if (!dbRdr["substationbusid"].isNull())
                     {
                         dbRdr["substationbusid"] >> busId;
                         if (subBusId == 0)
                             currentCCSubstationBus = findInMap(busId, paobject_subbus_map);
                     }
                     else if (!dbRdr["areaid"].isNull())
                     {
                         dbRdr["areaid"] >> areaId;
                     }

                     dbRdr["strategyid"] >> stratId;


                     if (currentCCSubstationBus != NULL)
                     {
                         assignStrategyAtBus(currentCCSubstationBus, stratId);
                     }
                 }
             }
        }
        {
            //CHECK HOLIDAY SETTINGS
            CtiHolidayManager::getInstance().refresh();
            if (CtiHolidayManager::getInstance().isHolidayForAnySchedule(CtiDate()) )
            {
                 static const string sqlNoID =  "SELECT HSA.paobjectid, SSB.substationbusid, CCA.areaid, HSA.holidayscheduleid, HSA.strategyid, "
                                                  "DH.holidayname, DH.holidaymonth, DH.holidayday, DH.holidayyear "
                                                "FROM ccholidaystrategyassignment HSA "
                                                  "left join capcontrolsubstationbus SSB on SSB.substationbusid = HSA.paobjectid "
                                                  "left join capcontrolarea CCA on CCA.AreaID = HSA.paobjectid "
                                                  "join  dateofholiday DH on HSA.holidayscheduleid = DH.holidayscheduleid ";

                 Cti::Database::DatabaseConnection connection;
                 Cti::Database::DatabaseReader dbRdr(connection);

                 if( subBusId > 0 )
                 {
                     string sqlID = string(sqlNoID + " WHERE  HSA.paobjectid = ?");
                     dbRdr.setCommandText(sqlID);
                     dbRdr << subBusId;

                     CtiCCSubstationPtr currentStation = findSubstationByPAObjectID(findSubstationIDbySubBusID(subBusId) != NULL ? findSubstationIDbySubBusID(subBusId) : 0 );
                     if (currentStation != NULL)
                     {
                         sqlID += string(" OR HSA.paobjectid = ?");
                         dbRdr.setCommandText(sqlID);
                         dbRdr << subBusId;
                         dbRdr << currentStation->getParentId();
                     }
                 }
                 else
                 {
                     dbRdr.setCommandText(sqlNoID);
                 }

                 dbRdr.execute();

                 if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                 {
                     CTILOG_INFO(dout, dbRdr.asString());
                 }

                 while ( dbRdr() )
                 {
                     int holMon, holDay, holYear, tempYear;
                     dbRdr["holidaymonth"] >> holMon;
                     dbRdr["holidayday"] >> holDay;
                     dbRdr["holidayyear"] >> holYear;

                     CtiDate today = CtiDate();
                     if (holYear == -1)
                     {
                         tempYear = today.year();
                     }
                     else
                         tempYear = holYear;

                     if (today == CtiDate(holDay, holMon, tempYear) )
                     {
                         CTILOG_INFO(dout, "TODAY is: " << today << " HOLIDAY is: "<<CtiDate(holDay, holMon, tempYear) );
                         long busId, stratId, areaId = 0;
                         CtiCCSubstationBusPtr currentCCSubstationBus = NULL;
                         if (subBusId > 0)
                             currentCCSubstationBus = findSubBusByPAObjectID(subBusId);

                         if (!dbRdr["substationbusid"].isNull())
                         {
                             dbRdr["substationbusid"] >> busId;
                             if (subBusId == 0)
                                 currentCCSubstationBus = findInMap(busId, paobject_subbus_map);
                         }
                         else if (!dbRdr["areaid"].isNull())
                         {
                             dbRdr["areaid"] >> areaId;
                         }

                         dbRdr["strategyid"] >> stratId;
                         if (currentCCSubstationBus != NULL)
                         {
                             assignStrategyAtBus(currentCCSubstationBus, stratId);
                         }
                     }
                 }
            }

            if (!altsub_sub_idmap->empty())
            {
                PaoIdToPointIdMultiMap::iterator iter = altsub_sub_idmap->begin();
                while (iter != altsub_sub_idmap->end())
                {
                    long dualBusId  = iter->second;
                    iter++;

                    if (CtiCCSubstationBusPtr currentCCSubstationBus = findInMap(dualBusId, paobject_subbus_map))
                    {
                        if (CtiCCSubstationBusPtr dualBus = findInMap(currentCCSubstationBus->getAltDualSubId(), paobject_subbus_map))
                        {
                            if ( currentCCSubstationBus->getStrategy()->getUnitType() == ControlStrategy::KVar ||
                                 currentCCSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar ||
                                 currentCCSubstationBus->getStrategy()->getUnitType() == ControlStrategy::PFactorKWKQ )
                            {
                                if (dualBus->getCurrentVarLoadPointId() > 0)
                                {
                                    pointid_subbus_map->insert(make_pair(dualBus->getCurrentVarLoadPointId(), currentCCSubstationBus ));
                                    currentCCSubstationBus->addPointId(dualBus->getCurrentVarLoadPointId());
                                }
                                else
                                {
                                    CTILOG_WARN(dout, "Sub: "<<currentCCSubstationBus->getPaoName()<<" will NOT operate in Dual Mode."
                                                    "Alternate Sub: "<<dualBus->getPaoName()<<" does not have a VAR Point ID attached.");
                                }
                            }
                            else if ( currentCCSubstationBus->getStrategy()->getUnitType() == ControlStrategy::Volts )
                            {
                                if (dualBus->getCurrentVoltLoadPointId() > 0)
                                {
                                    pointid_subbus_map->insert(make_pair(dualBus->getCurrentVoltLoadPointId(), currentCCSubstationBus));
                                    currentCCSubstationBus->addPointId(dualBus->getCurrentVoltLoadPointId());
                                }
                                else
                                {
                                     CTILOG_WARN(dout, "Sub: "<<currentCCSubstationBus->getPaoName()<<" will NOT operate in Dual Mode."
                                                    "Alternate Sub: "<<dualBus->getPaoName()<<" does not have a Volt Point ID attached.");
                                }
                            }
                        }
                    }
                }
            }

        }

        {   // there was a section of code in the setDynamicData function that initialized some
            //  strategy stuff.  this *was* occuring after the strategy loading.  setDynamicData
            //  was folded into the constructor so that section of code needed to be moved out
            //  and executed after the strategy was assigned.  so here it is!

            auto initStrategy = 
                []( CtiCCSubstationBusPtr bus )
                {
                    if ( bus->getStrategy()->getMaxConfirmTime() == 0 )
                    {
                        bus->setSendMoreTimeControlledCommandsFlag( false );
                    }

                    if ( bus->getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod ||
                         bus->getLikeDayControlFlag() )
                    {
                        bus->figureNextCheckTime();
                    }
                };

            if ( subBusId > 0 )
            {
                if ( auto bus = Cti::mapFind( *paobject_subbus_map, subBusId ) )
                {
                    initStrategy( *bus );
                }
            }
            else
            {
                for ( auto & element : *paobject_subbus_map )
                {
                    initStrategy( element.second );
                }
            }
        }

        if (subBusId > 0) // else, when reloading all, then the reload of feeders will be called after subBusReload and take care of it.
        {
            static const string sql =  "SELECT FSA.feederid, FSA.substationbusid, FSA.displayorder "
                                       "FROM ccfeedersubassignment FSA "
                                       "WHERE FSA.substationbusid = ? "
                                       "ORDER BY FSA.substationbusid, FSA.displayorder";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr << subBusId;

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            long currentFeederId;
            while ( rdr() )
            {
                rdr["feederid"] >> currentFeederId;
                reloadFeederFromDatabase(currentFeederId, &_paobject_feeder_map,
                                        &_paobject_subbus_map, &_pointid_feeder_map, &_feeder_subbus_map );
            }
            CtiCCSubstationBusPtr myTempBus = findSubBusByPAObjectID(subBusId);
            if(myTempBus != NULL)
            {
                myTempBus->figureAndSetTargetVarValue();
            }
        }
        {
            static const string sqlNoID =  "SELECT PT.paobjectid, PT.pointid, PT.pointoffset, PT.pointtype "
                                           "FROM capcontrolsubstationbus SSB, point PT "
                                           "WHERE SSB.substationbusid = PT.paobjectid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( subBusId > 0 )
            {
                static const string sqlID = string(sqlNoID + " AND SSB.substationbusid = ?");
                rdr.setCommandText(sqlID);
                rdr << subBusId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            while ( rdr() )
            {
                long currentSubBusId;

                rdr["paobjectid"] >> currentSubBusId;
                CtiCCSubstationBusPtr currentCCSubstationBus = findInMap(currentSubBusId, paobject_subbus_map);
                if (!currentCCSubstationBus)
                {
                    continue;
                }

                if ( !rdr["pointid"].isNull() )
                {
                    long tempPointId = -1000;
                    long tempPointOffset = -1000;
                    string tempPointType = "(none)";

                    rdr["pointid"] >> tempPointId;
                    rdr["pointoffset"] >> tempPointOffset;
                    rdr["pointtype"] >> tempPointType;

                    if ( resolvePointType(tempPointType) == StatusPointType &&
                         tempPointOffset == Cti::CapControl::Offset_PaoIsDisabled )
                    {
                        currentCCSubstationBus->setDisabledStatePointId(tempPointId, subBusId);
                        pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                        currentCCSubstationBus->addPointId(tempPointId);
                    }
                    else if ( resolvePointType(tempPointType) == StatusPointType &&
                              tempPointOffset == Cti::CapControl::Offset_CommsState )
                    {
                        currentCCSubstationBus->setCommsStatePointId(tempPointId);
                        pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                        currentCCSubstationBus->addPointId(tempPointId);
                    }
                    else if ( resolvePointType(tempPointType) == AnalogPointType )
                    {
                        if ( tempPointOffset == Cti::CapControl::Offset_EstimatedVarLoad )
                        {
                            currentCCSubstationBus->setEstimatedVarLoadPointId(tempPointId);
                            pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                            currentCCSubstationBus->addPointId(tempPointId);
                        }
                        else if ( tempPointOffset == Cti::CapControl::Offset_DailyOperations )
                        {
                            currentCCSubstationBus->setDailyOperationsAnalogPointId(tempPointId);
                            pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                            currentCCSubstationBus->addPointId(tempPointId);
                        }
                        else if ( tempPointOffset == Cti::CapControl::Offset_PowerFactor )
                        {
                            currentCCSubstationBus->setPowerFactorPointId(tempPointId);
                            pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                            currentCCSubstationBus->addPointId(tempPointId);
                        }
                        else if ( tempPointOffset == Cti::CapControl::Offset_EstimatedPowerFactor )
                        {
                            currentCCSubstationBus->setEstimatedPowerFactorPointId(tempPointId);
                            pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                            currentCCSubstationBus->addPointId(tempPointId);
                        }
                        else if ( tempPointOffset >= Cti::CapControl::Offset_OperationSuccessPercentRangeMin &&
                                  tempPointOffset <= Cti::CapControl::Offset_OperationSuccessPercentRangeMax )
                        {
                            if (currentCCSubstationBus->getOperationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                                currentCCSubstationBus->addPointId(tempPointId);
                            }
                        }
                        else if ( tempPointOffset >= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMin &&
                                  tempPointOffset <= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMax )
                        {
                            if (currentCCSubstationBus->getConfirmationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                                currentCCSubstationBus->addPointId(tempPointId);
                            }
                        }
                        else
                        {//undefined bus point
                            CTILOG_INFO(dout, "Undefined Substation Bus point offset: " << tempPointOffset);
                        }
                    }
                    else if ( !(resolvePointType(tempPointType) == StatusPointType && tempPointOffset == -1)) //tag point = status with -1 offset
                    {
                        CTILOG_INFO(dout, "Undefined Substation Bus point type: " << tempPointType);
                    }
                }
            }
        }

        if ( subBusId > 0 )     //  restore dual bus if necessary
        {
            subBusToUpdate = findSubBusByPAObjectID(subBusId);

            if ( subBusToUpdate && reEnableDualBusForThisBus )
            {
                const long altSubID          = subBusToUpdate->getAltDualSubId();
                const long switchOverPointID = subBusToUpdate->getSwitchOverPointId();

                if ( altSubID > 0 && switchOverPointID > 0 )    // we still have a dual bus assignment and switchover point - enable it if it was enabled at the start of the reload
                {
                    CtiCapController::getInstance()->handleAlternateBusModeValues( switchOverPointID,
                                                                                   1.0,
                                                                                   subBusToUpdate );
                }
            }
        }

        if (subBusId > 0) //incase feeders/capbanks were moved?????  need to check this $#*! out.
        {
            reloadOperationStatsFromDatabase(subBusId,&_paobject_capbank_map, &_paobject_feeder_map, paobject_subbus_map,
                                             &_paobject_substation_map, &_paobject_area_map, &_paobject_specialarea_map);

            // start point registrations for this particular bus
            if ( CtiCCSubstationBusPtr bus = findSubBusByPAObjectID( subBusId ) )
            {
                CtiCapController::getInstance()->registerPaoForPointUpdates( *bus );
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

string CtiCCSubstationBusStore::getDbTableString(Cti::CapControl::CapControlType objectType)
{
    string capControlObjectTable = "";
    switch (objectType)
    {
        case SpecialArea:
            {
                capControlObjectTable = "capcontrolspecialarea CSA ";
            }
            break;
        case Area:
            {
                capControlObjectTable = "capcontrolarea CCA ";
            }
            break;
        case SubBus:
            {
                capControlObjectTable = "capcontrolsubstationbus SSB ";
            }
            break;
        case Feeder:
            {
                capControlObjectTable = "capcontrolfeeder CCF ";
            }
            break;
        case Undefined:
        case CapBank:
        case Substation:
        case Strategy:
        case Schedule:
        default:
            break;
    }
    return capControlObjectTable;
}

string CtiCCSubstationBusStore::getDbColumnString(Cti::CapControl::CapControlType objectType)
{
    string paObjectColumn = "";
    switch (objectType)
    {
        case SpecialArea:
            {
                paObjectColumn = "CSA.areaid";
            }
            break;
        case Area:
            {
                paObjectColumn = "CCA.areaid";
            }
            break;
        case SubBus:
            {
                paObjectColumn = "SSB.substationbusid";
            }
            break;
        case Feeder:
            {
                paObjectColumn = "CCF.feederid";
            }
            break;
        case Undefined:
        case CapBank:
        case Substation:
        case Strategy:
        case Schedule:
        default:
            break;
    }
    return paObjectColumn;
}
void CtiCCSubstationBusStore::assignStrategyToCCObject(Cti::RowReader& dbRdr, Cti::CapControl::CapControlType objectType)
{

    long objectId, stratId;
    dbRdr["paobjectid"] >> objectId;
    dbRdr["strategyid"] >> stratId;


    switch (objectType)
    {
        case SpecialArea:
            {
               CtiCCSpecialPtr currentSpArea = findSpecialAreaByPAObjectID(objectId);
               if (currentSpArea != NULL)
               {
                   currentSpArea->setStrategy( stratId );
                   cascadeAreaStrategySettings(currentSpArea);
               }
            }
            break;
        case Area:
            {
                CtiCCAreaPtr currentArea = findAreaByPAObjectID(objectId);
                if (currentArea != NULL)
                {
                    currentArea->setStrategy( stratId );
                    cascadeAreaStrategySettings(currentArea);
                }
            }
            break;
        case SubBus:
            {
                CtiCCSubstationBusPtr currentCCSubstationBus = findSubBusByPAObjectID(objectId);
                if (currentCCSubstationBus != NULL)
                {
                    assignStrategyAtBus(currentCCSubstationBus, stratId);
                }
            }
            break;
        case Feeder:
            {
                CtiCCFeederPtr currentCCFeeder = findFeederByPAObjectID(objectId);

                if (currentCCFeeder != NULL)
                {
                    assignStrategyAtFeeder(currentCCFeeder, stratId);
                }
            }
            break;
        default:
            break;
    }
}

void CtiCCSubstationBusStore::assignStrategyAtFeeder(CtiCCFeederPtr feeder, long stratId)
{
    long strategyID = _strategyManager->getDefaultId();   // default NoStrategy

    CtiCCSubstationBusPtr currentSubBus = findSubBusByPAObjectID(feeder->getParentId());
    if (currentSubBus != NULL)
    {
         CtiCCSubstationPtr currentStation = findSubstationByPAObjectID(currentSubBus->getParentId());
         if (currentStation != NULL)
         {
             if (currentStation->getSaEnabledFlag())
             {
                 CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(currentStation->getSaEnabledId());
                 if (spArea != NULL)
                 {
                     strategyID = spArea->getStrategy()->getStrategyId();
                 }
             }
             else
             {
                 strategyID = stratId;
             }
         }
    }
    feeder->setStrategy( strategyID );
}

void CtiCCSubstationBusStore::assignStrategyAtBus(CtiCCSubstationBusPtr bus, long stratId)
{

    CtiCCSubstationPtr currentStation = findSubstationByPAObjectID(bus->getParentId());
    if (currentStation != NULL)
    {
        long strategyID = _strategyManager->getDefaultId();

        if (currentStation->getSaEnabledFlag())
        {
            CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(currentStation->getSaEnabledId());
            if (spArea != NULL)
            {
                strategyID = spArea->getStrategy()->getStrategyId();
            }
        }
        else
        {
            strategyID = stratId;
        }

        bus->setStrategy(strategyID);

        if ( bus->getStrategy()->getUnitType() == ControlStrategy::None)
        {
            cascadeAreaStrategySettings(findAreaByPAObjectID(currentStation->getParentId()));
        }
        if ( bus->getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
        {
            bus->figureNextCheckTime();
        }
    }
}

void CtiCCSubstationBusStore::reloadFeederFromDatabase(long feederId,
                                                       PaoIdToFeederMap *paobject_feeder_map,
                                                       PaoIdToSubBusMap *paobject_subbus_map,
                                                       PointIdToFeederMultiMap *pointid_feeder_map,
                                                       ChildToParentMap *feeder_subbus_map )
{
    if ( feederId > 0 )
    {
        deleteFeeder( feederId );
    }

    try
    {
        {
            static const std::string sql =
                "SELECT "
                    "Y.PAObjectID, "
                    "Y.Category, "
                    "Y.PAOClass, "
                    "Y.PAOName, "
                    "Y.Type, "
                    "Y.Description, "
                    "Y.DisableFlag, "
                    "F.CurrentVarLoadPointID, "
                    "F.CurrentWattLoadPointID, "
                    "F.MapLocationID, "
                    "F.CurrentVoltLoadPointID, "
                    "F.MultiMonitorControl, "
                    "F.usephasedata, "
                    "F.phaseb, "
                    "F.phasec, "
                    "F.ControlFlag, "
                    "D.CurrentVarPointValue, "
                    "D.CurrentWattPointValue, "
                    "D.NewPointDataReceivedFlag, "
                    "D.LastCurrentVarUpdateTime, "
                    "D.EstimatedVarPointValue, "
                    "D.CurrentDailyOperations, "
                    "D.RecentlyControlledFlag, "
                    "D.LastOperationTime, "
                    "D.VarValueBeforeControl, "
                    "D.LastCapBankDeviceID, "
                    "D.BusOptimizedVarCategory, "
                    "D.BusOptimizedVarOffset, "
                    "D.PowerFactorValue, "
                    "D.KvarSolution, "
                    "D.EstimatedPFValue, "
                    "D.CurrentVarPointQuality, "
                    "D.WaiveControlFlag, "
                    "D.AdditionalFlags, "
                    "D.CurrentVoltPointValue, "
                    "D.EventSeq, "
                    "D.CurrVerifyCBId, "
                    "D.CurrVerifyCBOrigState, "
                    "D.CurrentWattPointQuality, "
                    "D.CurrentVoltPointQuality, "
                    "D.iVControlTot, "
                    "D.iVCount, "
                    "D.iWControlTot, "
                    "D.iWCount, "
                    "D.phaseavalue, "
                    "D.phasebvalue, "
                    "D.phasecvalue, "
                    "D.LastWattPointTime, "
                    "D.LastVoltPointTime, "
                    "D.retryIndex, "
                    "D.PhaseAValueBeforeControl, "
                    "D.PhaseBValueBeforeControl, "
                    "D.PhaseCValueBeforeControl, "
                    "P.OriginalParentId, "
                    "P.OriginalSwitchingOrder, "
                    "P.OriginalCloseOrder, "
                    "P.OriginalTripOrder, "
                    "U.DECIMALPLACES "
                "FROM "
                    "YukonPAObject Y "
                        "JOIN CapControlFeeder F "
                            "ON Y.PAObjectID = F.FeederID "
                        "LEFT OUTER JOIN DynamicCCFeeder D "
                            "ON F.FeederID = D.FeederID "
                        "LEFT OUTER JOIN DynamicCCOriginalParent P "
                            "ON F.FeederID = P.PAObjectId "
                        "LEFT OUTER JOIN POINTUNIT U "
                            "ON F.CurrentVarLoadPointID = U.POINTID";

            static const std::string sqlID = sql +
                " WHERE Y.PAObjectID = ?";

            Cti::Database::DatabaseConnection   connection;
            Cti::Database::DatabaseReader       rdr( connection );

            if ( feederId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << feederId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                CtiCCFeederPtr currentCCFeeder = CtiCCFeederPtr(new CtiCCFeeder(rdr, _strategyManager.get()));

                paobject_feeder_map->insert(make_pair(currentCCFeeder->getPaoId(),currentCCFeeder));

                for ( const long pointID : *currentCCFeeder->getPointIds() )
                {
                    pointid_feeder_map->emplace( pointID, currentCCFeeder );
                }
            }
        }
        {
            static const string sqlNoID = "SELECT SAA.feederid, SAA.substationbusid, SAA.displayorder "
                                          "FROM ccfeedersubassignment SAA";
            static const string sqlAppendOrder = " ORDER BY SAA.substationbusid, SAA.displayorder";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if (feederId > 0)
            {
                static const string sqlID = string(sqlNoID + " WHERE SAA.feederid = ?" + sqlAppendOrder);
                rdr.setCommandText(sqlID);
                rdr << feederId;
            }
            else
            {
                rdr.setCommandText(sqlNoID + sqlAppendOrder);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            CtiCCFeederPtr currentCCFeeder;
            while ( rdr() )
            {
                long currentSubBusId;
                long currentFeederId;
                float displayOrder;
                rdr["feederid"] >> currentFeederId;
                rdr["substationbusid"] >> currentSubBusId;
                rdr["displayorder"] >>displayOrder;
                currentCCFeeder = findInMap(currentFeederId, paobject_feeder_map);
                if (!currentCCFeeder)
                {
                    continue;
                }
                currentCCFeeder->setParentId(currentSubBusId);
                currentCCFeeder->setDisplayOrder(displayOrder);
                CtiCCSubstationBusPtr currentCCSubstationBus = NULL;

                if (feederId > 0)
                    currentCCSubstationBus = findSubBusByPAObjectID(currentSubBusId);
                else
                {
                    currentCCSubstationBus = findInMap(currentSubBusId, paobject_subbus_map);
                }

                if (currentCCSubstationBus != NULL)
                {
                    long strategyID = currentCCSubstationBus->getStrategy()->getStrategyId();

                    currentCCFeeder->setParentControlUnits(currentCCSubstationBus->getStrategy()->getControlUnits());
                    currentCCFeeder->setParentName(currentCCSubstationBus->getPaoName());

                    if (currentCCFeeder->getStrategy()->getUnitType() == ControlStrategy::None)
                    {
                        currentCCFeeder->setStrategy( strategyID );
                    }
                }

                CtiFeeder_vec& tempFeeders = currentCCSubstationBus->getCCFeeders();
                CtiCCFeederPtr tempFeeder;
                int insertPoint = tempFeeders.size();
                int j = insertPoint;

                while (j > 0)
                {
                    tempFeeder = (CtiCCFeeder*)tempFeeders.at(j-1);
                    if (displayOrder <= tempFeeder->getDisplayOrder())
                    {
                        insertPoint =  j - 1;
                    }

                    j--;
                }
                CtiFeeder_vec& ccF = currentCCSubstationBus->getCCFeeders();
                ccF.insert( ccF.begin()+insertPoint, currentCCFeeder );
                feeder_subbus_map->insert(make_pair(currentFeederId, currentSubBusId));
            }
        }
        {
             static const string sqlNoID =  "SELECT SSA.paobjectid, SSA.seasonscheduleid, SSA.seasonname, "
                                              "SSA.strategyid, DS.seasonstartmonth, DS.seasonendmonth, "
                                              "DS.seasonstartday, DS.seasonendday "
                                            "FROM capcontrolfeeder CCF, ccseasonstrategyassignment SSA, "
                                              "dateofseason DS "
                                            "WHERE SSA.paobjectid = CCF.feederid AND "
                                              "SSA.seasonscheduleid = DS.seasonscheduleid AND "
                                              "SSA.seasonname = DS.seasonname";

             Cti::Database::DatabaseConnection connection;
             Cti::Database::DatabaseReader rdr(connection);

             if (feederId > 0)
             {
                 static const string sqlID = string(sqlNoID + " AND SSA.paobjectid = ?");
                 rdr.setCommandText(sqlID);
                 rdr << feederId;
             }
             else
             {
                 rdr.setCommandText(sqlNoID);
             }

             rdr.execute();

             while ( rdr() )
             {
                 int startMon, startDay, endMon, endDay;
                 rdr["seasonstartmonth"] >> startMon;
                 rdr["seasonendmonth"] >> endMon;
                 rdr["seasonstartday"] >> startDay;
                 rdr["seasonendday"] >> endDay;

                 CtiDate today = CtiDate();

                 if (today  >= CtiDate(startDay, startMon, today.year()) &&
                     today <= CtiDate(endDay, endMon, today.year()) )
                 {
                     long feedId, stratId;
                     rdr["paobjectid"] >> feedId;
                     rdr["strategyid"] >> stratId;


                     CtiCCFeederPtr currentCCFeeder = NULL;
                     CtiCCSubstationBusPtr currentSubBus = NULL;
                     CtiCCSubstationPtr currentStation = NULL;

                     if (feederId > 0)
                         currentCCFeeder = findFeederByPAObjectID(feedId);
                     else
                     {
                         currentCCFeeder = findInMap(feedId, paobject_feeder_map);
                     }
                     if (currentCCFeeder != NULL)
                     {
                         assignStrategyAtFeeder(currentCCFeeder, stratId);
                     }
                 }
             }
        }

        CtiHolidayManager::getInstance().refresh();
        if (CtiHolidayManager::getInstance().isHolidayForAnySchedule(CtiDate()) )
        {
             static const string sqlNoID =  "SELECT HSA.paobjectid, HSA.holidayscheduleid, HSA.strategyid, "
                                              "DH.holidayname, DH.holidaymonth, DH.holidayday, DH.holidayyear "
                                            "FROM capcontrolfeeder CCF, ccholidaystrategyassignment HSA, "
                                              "dateofholiday DH "
                                            "WHERE HSA.paobjectid = CCF.feederid AND "
                                              "HSA.holidayscheduleid = DH.holidayscheduleid";

             Cti::Database::DatabaseConnection connection;
             Cti::Database::DatabaseReader rdr(connection);

             if( feederId > 0 )
             {
                 static const string sqlID = string(sqlNoID + " AND HSA.paobjectid = ?");
                 rdr.setCommandText(sqlID);
                 rdr << feederId;
             }
             else
             {
                 rdr.setCommandText(sqlNoID);
             }

             rdr.execute();

             if ( _CC_DEBUG & CC_DEBUG_DATABASE )
             {
                 CTILOG_INFO(dout, rdr.asString());
             }

             while ( rdr() )
             {
                 int holMon, holDay, holYear, tempYear;
                 rdr["holidaymonth"] >> holMon;
                 rdr["holidayday"] >> holDay;
                 rdr["holidayyear"] >> holYear;

                 CtiDate today = CtiDate();
                 if (holYear == -1)
                 {
                     tempYear = today.year();
                 }
                 else
                     tempYear = holYear;

                 if (today == CtiDate(holDay, holMon, tempYear) )
                 {
                     long feedId, stratId;
                     rdr["paobjectid"] >> feedId;
                     rdr["strategyid"] >> stratId;

                     CtiCCFeederPtr currentCCFeeder = NULL;
                     CtiCCSubstationBusPtr currentSubBus = NULL;
                     CtiCCSubstationPtr currentStation = NULL;

                     currentCCFeeder = findFeederByPAObjectID(feedId);

                     if (currentCCFeeder != NULL)
                     {
                         assignStrategyAtFeeder(currentCCFeeder, stratId);
                     }
                 }
             }
        }
        if (feederId > 0)
        {
            static const string sql =  "SELECT FBL.deviceid, FBL.feederid, FBL.controlorder, FBL.closeorder, "
                                         "FBL.triporder "
                                       "FROM ccfeederbanklist FBL "
                                       "WHERE FBL.feederid = ?";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr << feederId;

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            long capbankid, feedid, controlorder, closeorder, triporder;
            while ( rdr() )
            {
                rdr["deviceid"] >> capbankid;
                rdr["feederid"] >> feedid;
                rdr["controlorder"] >> controlorder;
                rdr["closeorder"] >> closeorder;
                rdr["triporder"] >> triporder;
                reloadCapBankFromDatabase(capbankid, &_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map,
                                          &_pointid_capbank_map, &_capbank_subbus_map,
                                          &_capbank_feeder_map, &_feeder_subbus_map, &_cbc_capbank_map );
            }

            if (CtiCCFeederPtr currentFeeder = findInMap(feederId, paobject_feeder_map))
            {
                reloadMonitorPointsFromDatabase(currentFeeder->getParentId(), &_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map, &_pointid_capbank_map, &_pointid_subbus_map);
            }
        }

        {
            // there was a section of code in the dynamic table read that initialized the target VAr
            //  value.  this *was* occuring after the strategy loading.  dynamic data reading was folded into
            //  the constructor so that section of code needed to be moved out and executed after the
            //  strategy was assigned.  so here it is!

            if ( feederId > 0 )
            {
                if ( CtiCCFeederPtr feeder = findInMap( feederId, paobject_feeder_map ) )
                {
                    if ( CtiCCSubstationBusPtr bus = findSubBusByPAObjectID( feeder->getParentId() ) )
                    {
                        feeder->figureAndSetTargetVarValue( bus->getStrategy()->getControlMethod(),
                                                            bus->getStrategy()->getControlUnits(),
                                                            bus->getPeakTimeFlag() );
                    }
                }
            }
        }
        {
            static const string sqlNoID =  "SELECT PT.paobjectid, PT.pointid, PT.pointoffset, PT.pointtype "
                                           "FROM point PT, capcontrolfeeder CCF "
                                           "WHERE CCF.feederid = PT.paobjectid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(feederId > 0)
            {
                static const string sqlID = string(sqlNoID + " AND CCF.feederid = ?");
                rdr.setCommandText(sqlID);
                rdr << feederId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            while ( rdr() )
            {
                long currentCCFeederId;

                rdr["paobjectid"] >> currentCCFeederId;
                CtiCCFeederPtr currentCCFeeder = findInMap(currentCCFeederId, paobject_feeder_map);
                if (currentCCFeeder == NULL)
                {
                    continue;
                }

                long tempPAObjectId = 0;
                rdr["paobjectid"] >> tempPAObjectId;
                if (    tempPAObjectId == currentCCFeeder->getPaoId() )
                {
                    if ( !rdr["pointid"].isNull() )
                    {
                        long tempPointId = -1000;
                        long tempPointOffset = -1000;
                        string tempPointType = "(none)";
                        rdr["pointid"] >> tempPointId;
                        rdr["pointoffset"] >> tempPointOffset;
                        rdr["pointtype"] >> tempPointType;

                        if ( resolvePointType(tempPointType) == StatusPointType &&
                             tempPointOffset == Cti::CapControl::Offset_PaoIsDisabled )
                        {
                            currentCCFeeder->setDisabledStatePointId(tempPointId, feederId);
                            pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                            currentCCFeeder->addPointId(tempPointId);
                        }
                        else if ( resolvePointType(tempPointType) == AnalogPointType )
                        {
                            if ( tempPointOffset == Cti::CapControl::Offset_EstimatedVarLoad )
                            {
                                currentCCFeeder->setEstimatedVarLoadPointId(tempPointId);
                                pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                currentCCFeeder->addPointId(tempPointId);
                            }
                            else if ( tempPointOffset == Cti::CapControl::Offset_DailyOperations )
                            {
                                currentCCFeeder->setDailyOperationsAnalogPointId(tempPointId);
                                pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                currentCCFeeder->addPointId(tempPointId);
                            }
                            else if ( tempPointOffset == Cti::CapControl::Offset_PowerFactor )
                            {
                                currentCCFeeder->setPowerFactorPointId(tempPointId);
                                pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                currentCCFeeder->addPointId(tempPointId);
                            }
                            else if ( tempPointOffset == Cti::CapControl::Offset_EstimatedPowerFactor )
                            {
                                currentCCFeeder->setEstimatedPowerFactorPointId(tempPointId);
                                pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                currentCCFeeder->addPointId(tempPointId);
                            }
                            else if ( tempPointOffset >= Cti::CapControl::Offset_OperationSuccessPercentRangeMin &&
                                      tempPointOffset <= Cti::CapControl::Offset_OperationSuccessPercentRangeMax )
                            {
                                if (currentCCFeeder->getOperationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                                {
                                    pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                    currentCCFeeder->addPointId(tempPointId);
                                }
                            }
                            else if ( tempPointOffset >= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMin &&
                                      tempPointOffset <= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMax )
                            {
                                if (currentCCFeeder->getConfirmationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                                {
                                    pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                    currentCCFeeder->addPointId(tempPointId);
                                }
                            }
                            else
                            {//undefined feeder point
                                CTILOG_INFO(dout, "Undefined Feeder point offset: " << tempPointOffset);
                            }
                        }
                        else if ( !(resolvePointType(tempPointType) == StatusPointType && tempPointOffset == -1)) //tag point = status with -1 offset
                        {
                            CTILOG_INFO(dout, "Undefined Feeder point type: " << tempPointType);
                        }
                    }
                }
            }
        }
        if (feederId > 0)
        {
            reloadOperationStatsFromDatabase(feederId,&_paobject_capbank_map, paobject_feeder_map, &_paobject_subbus_map,
                                             &_paobject_substation_map, &_paobject_area_map, &_paobject_specialarea_map);

            // start point registrations for this particular feeder
            if ( CtiCCFeederPtr feeder = findFeederByPAObjectID( feederId ) )
            {
                CtiCapController::getInstance()->registerPaoForPointUpdates( *feeder );
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCCSubstationBusStore::reloadCapBankFromDatabase(long capBankId, PaoIdToCapBankMap *paobject_capbank_map,
                                                        PaoIdToFeederMap *paobject_feeder_map,
                                                        PaoIdToSubBusMap *paobject_subbus_map,
                                                        PointIdToCapBankMultiMap *pointid_capbank_map,
                                                       ChildToParentMap *capbank_subbus_map,
                                                       ChildToParentMap *capbank_feeder_map,
                                                       ChildToParentMap *feeder_subbus_map,
                                                       ChildToParentMap *cbc_capbank_map )
{
    if (capBankId > 0)
    {
        deleteCapBank(capBankId);
    }

    try
    {
        {
            static const std::string sql =
                "SELECT "
                    "Y.PAObjectID, "
                    "Y.Category, "
                    "Y.PAOClass, "
                    "Y.PAOName, "
                    "Y.Type, "
                    "Y.Description, "
                    "Y.DisableFlag, "
                    "C.OPERATIONALSTATE, "
                    "C.ControllerType, "
                    "C.CONTROLDEVICEID, "
                    "C.CONTROLPOINTID, "
                    "C.BANKSIZE, "
                    "C.TypeOfSwitch, "
                    "C.SwitchManufacture, "
                    "C.MapLocationID, "
                    "C.RecloseDelay, "
                    "C.MaxDailyOps, "
                    "C.MaxOpDisable, "
                    "D.ALARMINHIBIT, "
                    "D.CONTROLINHIBIT, "
                    "X.Type AS CbcType, "
                    "DC.ControlStatus, "
                    "DC.TotalOperations, "
                    "DC.LastStatusChangeTime, "
                    "DC.TagsControlStatus, "
                    "DC.AssumedStartVerificationStatus, "
                    "DC.PrevVerificationControlStatus, "
                    "DC.VerificationControlIndex, "
                    "DC.AdditionalFlags, "
                    "DC.CurrentDailyOperations, "
                    "DC.TwoWayCBCState, "
                    "DC.TwoWayCBCStateTime, "
                    "DC.beforeVar, "
                    "DC.afterVar, "
                    "DC.changeVar, "
                    "DC.twoWayCBCLastControl, "
                    "DC.PartialPhaseInfo, "
                    "DP.OriginalParentId, "
                    "DP.OriginalSwitchingOrder, "
                    "DP.OriginalCloseOrder, "
                    "DP.OriginalTripOrder "
                "FROM "
                    "YukonPAObject Y "
                        "JOIN CAPBANK C "
                            "ON Y.PAObjectID = C.DEVICEID "
                        "JOIN DEVICE D "
                            "ON Y.PAObjectID = D.DEVICEID "
                        "JOIN YukonPAObject X "
                            "ON C.CONTROLDEVICEID = X.PAObjectID "
                        "LEFT OUTER JOIN DynamicCCCapBank DC "
                            "ON Y.PAObjectID = DC.CapBankID "
                        "LEFT OUTER JOIN DynamicCCOriginalParent DP "
                            "ON Y.PAObjectID = DP.PAObjectId";

            static const std::string sqlID = sql +
                " WHERE "
                    "Y.PAObjectID = ?";

            Cti::Database::DatabaseConnection   connection;
            Cti::Database::DatabaseReader       rdr( connection );

            if ( capBankId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                CtiCCCapBankPtr bank = CtiCCCapBankPtr( new CtiCCCapBank( rdr ) );

                paobject_capbank_map->insert( std::make_pair( bank->getPaoId(), bank ) );

                cbc_capbank_map->insert( std::make_pair( bank->getControlDeviceId(), bank->getPaoId() ) );
            }
        }
        {
            static const std::string sql =
                "SELECT "
                    "F.DeviceID, "
                    "F.FeederID, "
                    "F.ControlOrder, "
                    "F.CloseOrder, "
                    "F.TripOrder "
                "FROM "
                    "CCFeederBankList F "
                        "JOIN CAPBANK C "
                            "ON F.DeviceID = C.DEVICEID";

            static const std::string sqlID = sql +
                " WHERE "
                    "C.DEVICEID = ?";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if ( capBankId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long deviceid = rdr["DeviceID"].as<long>();

                if ( CtiCCCapBankPtr currentCCCapBank = findInMap( deviceid, paobject_capbank_map ) )
                {
                    long feederid = rdr["FeederID"].as<long>();

                    if ( CtiCCFeederPtr currentCCFeeder = findInMap( feederid, paobject_feeder_map ) )
                    {
                        float controlOrder = rdr["ControlOrder"].as<float>(),
                              closeOrder   = rdr["CloseOrder"].as<float>(),
                              tripOrder    = rdr["TripOrder"].as<float>();

                        currentCCCapBank->setParentId( feederid );

                        currentCCCapBank->setControlOrder( controlOrder );
                        currentCCCapBank->setCloseOrder( closeOrder );
                        currentCCCapBank->setTripOrder( tripOrder );

                        if ( ! ciStringEqual( currentCCCapBank->getOperationalState(), CtiCCCapBank::UninstalledState ) )
                        {
                            currentCCFeeder->getCCCapBanks().insert( currentCCCapBank );
                            capbank_feeder_map->insert( std::make_pair( deviceid, feederid ) );
                            if ( feeder_subbus_map->find( feederid ) != feeder_subbus_map->end() )
                            {
                                long subbusid = feeder_subbus_map->find( feederid )->second;
                                capbank_subbus_map->insert( std::make_pair( deviceid, subbusid ) );
                            }
                        }
                    }
                }
            }
        }
        {
            static const std::string sql =
                "SELECT "
                    "P.PAObjectID, "
                    "P.POINTID, "
                    "P.POINTOFFSET, "
                    "P.POINTTYPE "
                "FROM "
                    "POINT P "
                        "JOIN CAPBANK C "
                            "ON P.PAObjectID = C.DEVICEID";

            static const std::string sqlID = sql +
                " WHERE "
                    "C.DEVICEID = ?";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if ( capBankId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                long currentCCCapBankId = rdr["PAObjectID"].as<long>();

                if ( auto currentCCCapBank = findInMap( currentCCCapBankId, paobject_capbank_map ) )
                {
                    currentCCCapBank->assignPoint( rdr );
                }
            }

            if ( capBankId > 0 )
            {
                if ( auto currentCCCapBank = findInMap( capBankId, paobject_capbank_map ) )
                {
                    for ( long pointID : *currentCCCapBank->getPointIds() )
                    {
                        pointid_capbank_map->emplace( pointID, currentCCCapBank );
                    }
                }
            }
            else
            {
                for ( auto entry : *paobject_capbank_map )
                {
                    for ( long pointID : *entry.second->getPointIds() )
                    {
                        pointid_capbank_map->emplace( pointID, entry.second );
                    }
                }
            }
        }

        // load the 2-way point dynamic data into the transport object mappings

        std::map< long, Transport::TwoWayDynamicDataTransport >     dynamicDataCache;

        {
            static const std::string sql =
                "SELECT "
                    "D.DeviceID, "
                    "D.RecloseBlocked, "
                    "D.ControlMode, "
                    "D.AutoVoltControl, "
                    "D.LastControl, "
                    "D.Condition, "
                    "D.OpFailedNeutralCurrent, "
                    "D.NeutralCurrentFault, "
                    "D.BadRelay, "
                    "D.DailyMaxOps, "
                    "D.VoltageDeltaAbnormal, "
                    "D.TempAlarm, "
                    "D.DSTActive, "
                    "D.NeutralLockout, "
                    "D.IgnoredIndicator, "
                    "D.Voltage, "
                    "D.HighVoltage, "
                    "D.LowVoltage, "
                    "D.DeltaVoltage, "
                    "D.AnalogInputOne, "
                    "D.Temp, "
                    "D.RSSI, "
                    "D.IgnoredReason, "
                    "D.TotalOpCount, "
                    "D.UvOpCount, "
                    "D.OvOpCount, "
                    "D.OvUvCountResetDate, "
                    "D.UvSetPoint, "
                    "D.OvSetPoint, "
                    "D.OvUvTrackTime, "
                    "D.LastOvUvDateTime, "
                    "D.NeutralCurrentSensor, "
                    "D.NeutralCurrentAlarmSetPoint, "
                    "D.IPAddress, "
                    "D.UDPPort "
                "FROM "
                    "DYNAMICCCTWOWAYCBC D "
                        "JOIN CAPBANK C "
                            "ON D.DeviceID = C.CONTROLDEVICEID "
                "WHERE "
                    "C.CONTROLDEVICEID != 0";

            static const std::string sqlID = sql +
                " AND C.DeviceID = ?";

            Cti::Database::DatabaseConnection   connection;
            Cti::Database::DatabaseReader       rdr( connection );

            if ( capBankId > 0 )
            {
                rdr.setCommandText( sqlID );
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            while ( rdr() )
            {
                dynamicDataCache.emplace( rdr[ "DeviceID" ].as<long>(), rdr );
            }
        }
        // load points for all 2 way CBCs and CBC Logical type CBCs
        {
            static const std::string sql =
                "SELECT "
                    "P.POINTID, "
                    "P.POINTTYPE, "
                    "P.POINTNAME, "
                    "P.PAObjectID, "
                    "P.POINTOFFSET, "
                    "P.STATEGROUPID, "
                    "PC.ControlOffset, "
                    "PSC.ControlType, "
                    "PSC.StateZeroControl, "
                    "PSC.StateOneControl, "
                    "PSC.CloseTime1, "
                    "PSC.CloseTime2, "
                    "COALESCE(PACC.MULTIPLIER, PA.MULTIPLIER) AS MULTIPLIER "
                "FROM "
                    "POINT P "
                        "JOIN CAPBANK C "
                            "ON P.PAObjectID = C.CONTROLDEVICEID "
                        "JOIN YukonPAObject Y "
                            "ON C.CONTROLDEVICEID = Y.PAObjectID "
                        "LEFT OUTER JOIN PointControl PC "
                            "ON P.POINTID = PC.PointId "
                        "LEFT OUTER JOIN PointStatusControl PSC "
                            "ON P.POINTID = PSC.PointId "
                        "LEFT OUTER JOIN POINTACCUMULATOR PACC "
                            "ON P.POINTID = PACC.POINTID "
                        "LEFT OUTER JOIN POINTANALOG PA "
                            "ON P.POINTID = PA.POINTID "
                "WHERE "
                    "C.CONTROLDEVICEID <> 0 ";

            static const std::string sqlID = sql +
                " AND C.DEVICEID = ?";

            static const std::string ordering =
                " ORDER BY P.PAObjectID";

            Cti::Database::DatabaseConnection   connection;
            Cti::Database::DatabaseReader       rdr( connection );

            if ( capBankId > 0 )
            {
                rdr.setCommandText( sqlID + ordering );
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText( sql + ordering );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO( dout, rdr.asString() );
            }

            // We sort the SQL output by PaoID, the idea being that we collect all the points for a particular PaoID
            //  in the cache.  When we've read all the points for the particular PaoID we then look up the Pao and add
            //  the cached points to it.
            
            std::vector<LitePoint>  cache;

            long previousCbcID = -1,
                 currentCbcId  = -1;

            bool validRow;

            do
            {
                validRow = rdr();       // advance the reader

                previousCbcID = currentCbcId;

                if ( validRow )
                {
                    rdr["PAObjectID"] >> currentCbcId;
                }
                
                if ( ( previousCbcID > 0 && previousCbcID != currentCbcId ) || ! validRow )
                {
                    // we are at the boundry where the previousCbcID differs from the currentCbcId, this means the cache is
                    //  full of points for the previousCbcID -- or we have an invalid row, which means the cache has all the
                    //  points for the last CBC (still previousCbcID) in it.

                    if ( cache.size() > 0 )
                    {
                        long currentCapBankId = Cti::mapFindOrDefault( *cbc_capbank_map, previousCbcID, 0 );

                        if ( currentCapBankId > 0 )
                        {
                            if ( CtiCCCapBankPtr bank = findInMap( currentCapBankId, paobject_capbank_map ) )
                            {
                                if ( bank->isControlDeviceTwoWay() )
                                {
                                    std::map<Attribute, std::string>    pointOverloads;

                                    // config
                                    auto deviceConfig = 
                                        Cti::ConfigManager::getConfigForIdAndType(
                                            bank->getControlDeviceId(),
                                            resolveDeviceType( bank->getControlDeviceType() ) );

                                    if ( deviceConfig )
                                    {
                                        // build the Attribute -> PointName overlay

                                        using AMC  = Cti::Config::DNPStrings::AttributeMappingConfiguration;
                                        using AAPN = Cti::Devices::AttributeAndPointName;
                                        using Cti::Devices::getConfigDataVector;

                                        try
                                        {
                                            // channel selection configuration data
                                            const auto cfgAttributesPointName = getConfigDataVector<AAPN>( deviceConfig, AMC::AttributeMappings_Prefix );

                                            for ( const auto & entry : cfgAttributesPointName )
                                            {
                                                pointOverloads[ Attribute::Lookup( entry.attributeName ) ] = entry.pointName;
                                            }                                            
                                        }
                                        catch ( Cti::Devices::MissingConfigDataException & ex )
                                        {
                                            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                                            {
                                                CTILOG_EXCEPTION_WARN( dout, ex, "No Attribute->PointName mappings in the device configuration for bank: " << bank->getPaoName() );
                                            }
                                        }
                                    }

                                    // add points and the attribute mappings to the bank

                                    bank->getTwoWayPoints().assignTwoWayPointsAndAttributes( cache, 
                                                                                             pointOverloads, 
                                                                                             Cti::mapFind( dynamicDataCache, bank->getControlDeviceId() ),
                                                                                             *bank );

                                    for ( const LitePoint & point : cache )
                                    {
                                        bank->addPointId( point.getPointId() );

                                        pointid_capbank_map->insert( std::make_pair( point.getPointId(), bank ) );
                                    }
                                }
                            }
                        }

                        cache.clear();
                    }
                }
                
                if ( validRow )
                {
                    cache.emplace_back( rdr );
                }
            } 
            while ( validRow );
        }

// jmoc -- what to do about dynamic data that doesn't have a 2way CBC ????

        {   // load the point attributes for the CBC heartbeat if available

            if ( capBankId > 0 )    // this guy only
            {
                if ( CtiCCCapBankPtr bank = findInMap( capBankId, paobject_capbank_map ) )
                {
                    bank->loadAttributes( _attributeService.get() );

                    for ( auto pointID : bank->heartbeat._policy->getRegistrationPointIDs() )
                    {
                        pointid_capbank_map->insert( std::make_pair( pointID, bank ) );
                    }
                }
            }
            else    // do them all
            {
                for ( auto & item : *paobject_capbank_map )
                {
                    item.second->loadAttributes( _attributeService.get() );

                    for ( auto pointID : item.second->heartbeat._policy->getRegistrationPointIDs() )
                    {
                        pointid_capbank_map->insert( std::make_pair( pointID, item.second ) );
                    }
                }
            }
        }
        if (capBankId > 0)
        {
            reloadOperationStatsFromDatabase(capBankId, paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map,
                                             &_paobject_substation_map, &_paobject_area_map, &_paobject_specialarea_map);

            // start point registrations for this particular bank
            if ( CtiCCCapBankPtr bank = findCapBankByPAObjectID( capBankId ) )
            {
                CtiCapController::getInstance()->registerPaoForPointUpdates( *bank );
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


void CtiCCSubstationBusStore::reloadOperationStatsFromDatabase(long paoId, PaoIdToCapBankMap *paobject_capbank_map,
                                                        PaoIdToFeederMap *paobject_feeder_map,
                                                        PaoIdToSubBusMap *paobject_subbus_map,
                                                        PaoIdToSubstationMap *paobject_substation_map,
                                                        PaoIdToAreaMap *paobject_area_map,
                                                        PaoIdToSpecialAreaMap *paobject_specialarea_map )
{
    try
    {
        CtiTime currentDateTime;
        {
            static const string sqlNoID =  "SELECT DC.paobjectid, DC.userdefopcount, DC.userdefconffail, "
                                               "DC.dailyopcount, DC.dailyconffail, DC.weeklyopcount, DC.weeklyconffail, "
                                               "DC.monthlyopcount, DC.monthlyconffail "
                                           "FROM dynamicccoperationstatistics DC";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(paoId > 0)
            {
                static const string sqlID = string(sqlNoID + " WHERE DC.paobjectid = ?");
                rdr.setCommandText(sqlID);
                rdr << paoId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            CtiCCCapBankPtr currentCCCapBank = NULL;
            CtiCCFeederPtr currentFeeder = NULL;
            CtiCCSubstationBusPtr currentSubBus = NULL;
            CtiCCSubstationPtr currentStation = NULL;
            CtiCCAreaPtr currentArea = NULL;
            CtiCCSpecialPtr currentSpArea = NULL;
            while ( rdr() )
            {
                long currentPaoId;
                rdr["paobjectid"] >> currentPaoId;

                currentCCCapBank = findInMap(currentPaoId, paobject_capbank_map);
                currentFeeder = findInMap(currentPaoId, paobject_feeder_map);
                currentSubBus = findInMap(currentPaoId, paobject_subbus_map);

                currentStation = findSubstationByPAObjectID(currentPaoId);
                currentArea = findAreaByPAObjectID(currentPaoId);
                currentSpArea = findSpecialAreaByPAObjectID(currentPaoId);
                if (currentCCCapBank != NULL)
                {
                    currentCCCapBank->getOperationStats().setDynamicData(rdr);
                }
                if (currentFeeder != NULL)
                {
                    currentFeeder->getOperationStats().setDynamicData(rdr);
                }
                if (currentSubBus != NULL )
                {
                    currentSubBus->getOperationStats().setDynamicData(rdr);
                }
                if (currentStation != NULL)
                {
                    currentStation->getOperationStats().setDynamicData(rdr);
                }
                if (currentArea != NULL)
                {
                    currentArea->getOperationStats().setDynamicData(rdr);
                }
                if (currentSpArea != NULL)
                {
                    currentSpArea->getOperationStats().setDynamicData(rdr);
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}


void CtiCCSubstationBusStore::reloadMonitorPointsFromDatabase(long subBusId, PaoIdToCapBankMap *paobject_capbank_map,
                                                        PaoIdToFeederMap *paobject_feeder_map,
                                                        PaoIdToSubBusMap *paobject_subbus_map,
                                                        PointIdToCapBankMultiMap *pointid_capbank_map,
                                                        PointIdToSubBusMultiMap *pointid_subbus_map)
{
    try
    {
        std::set< std::pair<long, int> >    requiredPointResponses;

        {
            static const std::string sql =
                "SELECT "
                    "YP.Type, "
                    "X.SubstationBusID, "
                    "MB.DeviceId, "
                    "MB.PointId, "
                    "MB.DisplayOrder, "
                    "MB.Scannable, "
                    "MB.NINAvg, "
                    "MB.UpperBandwidth, "
                    "MB.LowerBandwidth, "
                    "MB.Phase, "
                    "MB.OverrideStrategy, "
                    "YP.PAOName, "
                    "P.POINTNAME, "
                    "MBH.Value, "
                    "MBH.DateTime, "
                    "MBH.ScanInProgress "
                "FROM ( "
                    "SELECT "
                        "FB.DeviceID AS PAObjectID, FS.SubstationBusId "
                    "FROM "
                        "CCFeederBankList FB "
                        "JOIN CCFeederSubAssignment FS ON FB.FeederID = FS.FeederID "
                    "UNION "
                    "SELECT "
                        "RTZ.RegulatorId AS PAObjectID, Z.SubstationBusId "
                    "FROM "
                        "RegulatorToZoneMapping RTZ "
                        "JOIN Zone Z ON RTZ.ZoneId = Z.ZoneId "
                    "UNION "
                    "SELECT "
                        "P.PAObjectID AS PAObjectID, Z.SubstationBusId "
                    "FROM "
                        "POINT P "
                        "JOIN PointToZoneMapping PTZ ON P.POINTID = PTZ.PointId "
                        "JOIN Zone Z ON PTZ.ZoneId = Z.ZoneId "
                    ") X "
                "JOIN YukonPAObject YP ON X.PAObjectID = YP.PAObjectID "
                "JOIN CCMonitorBankList MB ON YP.PAObjectID = MB.DeviceId "
                "JOIN POINT P on MB.PointId = P.POINTID "
                "LEFT OUTER JOIN DynamicCCMonitorBankHistory MBH ON MB.DeviceId = MBH.DeviceId AND MB.PointID = MBH.PointID";

            static const std::string where_sql =
                " WHERE "
                    "X.SubstationBusID = ?";


            Cti::Database::DatabaseConnection   connection;
            Cti::Database::DatabaseReader       rdr(connection);

            if ( subBusId > 0 )
            {
                rdr.setCommandText( sql + where_sql );

                rdr << subBusId;
            }
            else
            {
                rdr.setCommandText( sql );
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            while ( rdr() )
            {
                std::string devType;
                long        currentBankId,
                            currentPointId,
                            currentBusId;

                rdr["Type"]             >> devType;
                rdr["DeviceId"]         >> currentBankId;
                rdr["PointId"]          >> currentPointId;
                rdr["SubstationBusID"]  >> currentBusId;

                CtiCCMonitorPointPtr currentMonPoint = CtiCCMonitorPointPtr( new CtiCCMonitorPoint(rdr) );

                if ( stringContainsIgnoreCase( devType, "CAP BANK" ) )
                {
                    if ( ! loadCapBankMonitorPoint( currentMonPoint, requiredPointResponses,
                                                    paobject_capbank_map, paobject_feeder_map, paobject_subbus_map, pointid_capbank_map))
                    {
                        continue;   // can't load... bail out!
                    }
                }

                //Get all banks on SubBus all Feeders.

                if ( CtiCCSubstationBusPtr subBusPtr = findInMap( currentBusId, paobject_subbus_map ) )
                {
                    if ( subBusPtr->addMonitorPoint( currentPointId, currentMonPoint ) )
                    {
                        subBusPtr->addDefaultPointResponses( requiredPointResponses );
                        pointid_subbus_map->insert( std::make_pair( currentMonPoint->getPointId(), subBusPtr ) );
                        subBusPtr->addPointId( currentPointId );
                    }
                }
            }
        }

        {
            //Loading new point responses
            PointResponseDaoPtr pointResponseDao = _daoFactory->getPointResponseDao();

            std::vector<PointResponse> pointResponses;
            if (subBusId > 0)
            {
                pointResponses = pointResponseDao->getPointResponsesBySubBusId(subBusId);
            }
            else
            {
                pointResponses = pointResponseDao->getAllPointResponses();
            }


            for each (PointResponse pointResponse in pointResponses)
            {
                requiredPointResponses.erase( std::make_pair(pointResponse.getPointId(), pointResponse.getDeviceId() ) );

                if (CtiCCCapBankPtr bank = findInMap(pointResponse.getDeviceId(), paobject_capbank_map))
                {
                    bank->addPointResponse(pointResponse);
                }

                if (CtiCCSubstationBusPtr bus = findInMap(pointResponse.getSubBusId(), paobject_subbus_map))
                {
                    bus->updatePointResponse(PointResponseKey(pointResponse.getDeviceId(),pointResponse.getPointId()), PointResponsePtr(new PointResponse(pointResponse)));
                }

            }
            for each ( std::pair<long, int>  thePair in requiredPointResponses )
            {
                if ( long busId = findSubBusIDbyCapBankID(thePair.second) )
                {
                    if ( CtiCCCapBankPtr bank = findInMap(thePair.second, paobject_capbank_map) )
                    {
                        std::string
                            deviceName = "<uninitialized>",
                            pointName  = "<uninitialized>";

                        if ( auto searchResult = pointid_capbank_map->find( thePair.first ); searchResult != pointid_capbank_map->end() )
                        {
                            CtiCCCapBankPtr otherBank = searchResult->second;

                            deviceName = otherBank->getPaoName();
                            pointName  = otherBank->getTwoWayPoints().getPointById( thePair.first ).getPointName();
                        }

                        PointResponse defaultPointResponse( thePair.first, thePair.second, 0, _IVVC_DEFAULT_DELTA, false, busId, pointName, deviceName );

                        bank->addPointResponse(defaultPointResponse);
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}



bool CtiCCSubstationBusStore::loadCapBankMonitorPoint(CtiCCMonitorPointPtr currentMonPoint, std::set< std::pair<long, int> >  &requiredPointResponses,
                                                        PaoIdToCapBankMap *paobject_capbank_map,
                                                        PaoIdToFeederMap *paobject_feeder_map,
                                                        PaoIdToSubBusMap *paobject_subbus_map,
                                                        PointIdToCapBankMultiMap *pointid_capbank_map)
{
    CtiCCCapBankPtr currentBankPtr = findInMap(currentMonPoint->getDeviceId(), paobject_capbank_map);
    if (currentBankPtr == NULL)
    {
        return false;
    }

    //This is to store and track the monitor point
    if (!currentBankPtr->addMonitorPoint(currentMonPoint))
    {
        return false;
    }

    pointid_capbank_map->insert(make_pair(currentMonPoint->getPointId(),currentBankPtr));

    //The following is to setup the defaults for dynamic tables.
    if (currentBankPtr->getParentId() <= 0)
    {
        //This is an orphaned Bank
        return false;
    }
    CtiCCFeederPtr feederPtr = findInMap(currentBankPtr->getParentId(), paobject_feeder_map);
    if (feederPtr == NULL)
    {
        return false;
    }

    if( feederPtr->getStrategy()->getMethodType() == ControlStrategy::SubstationBus )
    {
        //Get all banks on SubBus all Feeders.
        CtiCCSubstationBusPtr subBusPtr = findInMap(feederPtr->getParentId(), paobject_subbus_map);
        if (subBusPtr == NULL)
        {
            return false;
        }
        for each (CtiCCFeeder* feeder in subBusPtr->getCCFeeders())
        {
            for each (CtiCCCapBankPtr bank in feeder->getCCCapBanks())
            {
                requiredPointResponses.insert( std::make_pair(currentMonPoint->getPointId(), bank->getPaoId() ) );
            }
        }
    }
    else
    {
        //Get only this feeders banks
        CtiCCCapBank_SVector& banks = feederPtr->getCCCapBanks();
        for each (CtiCCCapBankPtr bank in banks)
        {
            requiredPointResponses.insert( std::make_pair(currentMonPoint->getPointId(), bank->getPaoId() ) );
        }
    }
    return true;


}

void CtiCCSubstationBusStore::reloadMiscFromDatabase()
{
    try
    {
        CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
        {
            if ( _ccCapBankStates->size() > 0 )
            {
                delete_container(*_ccCapBankStates);
                _ccCapBankStates->clear();
                if (_ccCapBankStates->size() > 0)
                {
                    CTILOG_INFO(dout, "_ccCapBankStates did NOT get destroyed ");
                }
            }
            {
                static const string sql =  "SELECT ST.text, ST.foregroundcolor, ST.backgroundcolor "
                                           "FROM state ST "
                                           "WHERE ST.stategroupid = 3 AND "
                                             "ST.rawstate >= 0 "
                                           "ORDER BY ST.rawstate ASC";

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                {
                    CTILOG_INFO(dout, rdr.asString());
                }

                while ( rdr() )
                {
                    CtiCCState* ccState = new CtiCCState(rdr);
                    _ccCapBankStates->push_back( ccState );
                }
            }
            {
                static const string sql =  "SELECT AC.alarmcategoryid, AC.categoryname "
                                           "FROM alarmcategory AC "
                                           "WHERE AC.categoryname LIKE ?";

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection, sql);

                rdr << _MAXOPS_ALARM_CAT;

                rdr.execute();

                if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                {
                    CTILOG_INFO(dout, rdr.asString());
                }

                while ( rdr() )
                {
                    rdr["alarmcategoryid"] >> _MAXOPS_ALARM_CATID;
                }
            }
            {
                static const string sql =  "SELECT FDR.pointid, FDR.directiontype, FDR.interfacetype, FDR.destination, "
                                             "FDR.translation "
                                           "FROM fdrtranslation FDR "
                                           "WHERE FDR.directiontype = 'Link Status' AND FDR.interfacetype = 'SYSTEM'";

                Cti::Database::DatabaseConnection connection;
                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                {
                    CTILOG_INFO(dout, rdr.asString());
                }

                std::string str;
                char var[128];
                std::strcpy(var, "FDR_INTERFACES");
                if( (str = gConfigParms.getValueAsString(var)).empty() )
                {
                    str = "none";
                }

                while ( rdr() )
                {
                    long pointID;
                    string translation;
                    rdr["pointid"] >> pointID;
                    rdr["translation"] >> translation;

                    if (boost::algorithm::istarts_with(str, "fdr"))
                    {
                        str.erase(0, 3);  //  trim off "fdr"
                    }
                    if (stringContainsIgnoreCase(translation, str))
                    {
                        CTILOG_INFO(dout, "FDR Link Status POINT FOUND: "<<pointID);
                        _linkStatusPointId = pointID;
                        _linkStatusFlag = STATE_OPENED;
                        break;
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCCSubstationBusStore::reloadMapOfBanksToControlByLikeDay(long subbusId, long feederId,
                                  std::map <long, long> *controlid_action_map,
                                  CtiTime &lastSendTime, int fallBackConstant)
{
    try
    {
        CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
        {
            const CtiTime fallbackTimeNow( CtiTime() - fallBackConstant ),
                          fallbackLastSendTime( lastSendTime - fallBackConstant );

            static const std::string sql =
                "SELECT "
                    "CEL.PointID, "
                    "CEL.DateTime, "
                    "CEL.Value "
                "FROM "
                    "CCEventLog CEL "
                "WHERE "
                    "CEL.EventType = 1 AND CEL.EventSubtype = 0 AND "
                    "CEL.DateTime > ? AND CEL.DateTime <= ?";

            // Q: Why only 'Close Sent...' or 'Open Sent...' and not 'Flip Sent...'?

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( subbusId != 0)
            {
                rdr.setCommandText( sql + " AND CEL.SubID = ?" );
                rdr << fallbackLastSendTime
                    << fallbackTimeNow
                    << subbusId;
            }
            else
            {
                rdr.setCommandText( sql + " AND CEL.FeederID = ?" );
                rdr << fallbackLastSendTime
                    << fallbackTimeNow
                    << feederId;
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            while ( rdr() )
            {
                long pointId;
                CtiTime controlTime;
                long controlValue;

                rdr["PointID"] >> pointId;
                rdr["DateTime"] >> controlTime;
                rdr["Value"] >> controlValue;

                if (controlValue == CtiCCCapBank::ClosePending ||
                    controlValue == CtiCCCapBank::CloseFail ||
                    controlValue == CtiCCCapBank::CloseQuestionable ||
                    controlValue == CtiCCCapBank::Close )
                {
                    controlValue = CtiCCCapBank::Close;
                }
                else
                {
                    controlValue = CtiCCCapBank::Open;
                }

                if (controlTime <= fallbackTimeNow)
                {
                    controlid_action_map->insert(make_pair(pointId,controlValue));
                }

                if (controlTime > lastSendTime)     // update lastSendTime to the 'latest' time selected from the DB
                {
                    lastSendTime = controlTime;
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


void CtiCCSubstationBusStore::locateOrphans(PaoIdVector *orphanCaps, PaoIdVector *orphanFeeders, PaoIdToCapBankMap paobject_capbank_map,
                       PaoIdToFeederMap paobject_feeder_map, ChildToParentMap capbank_feeder_map, ChildToParentMap feeder_subbus_map)
{
    try
    {
        CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
        {
        orphanFeeders->clear();
        PaoIdToFeederMap::iterator iter = paobject_feeder_map.begin();
        while (iter != paobject_feeder_map.end())
        {
            if (feeder_subbus_map.find(iter->first) == feeder_subbus_map.end())
            {
                orphanFeeders->push_back(iter->first);
            }
            iter++;
        }
        orphanCaps->clear();
        PaoIdToCapBankMap::iterator capIter = paobject_capbank_map.begin();
        while (capIter != paobject_capbank_map.end())
        {
            if (capbank_feeder_map.find(capIter->first) == capbank_feeder_map.end())
            {
                orphanCaps->push_back(capIter->first);
            }
            capIter++;
        }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

bool CtiCCSubstationBusStore::isFeederOrphan(long feederId)
{
   try
    {
       CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
        {
            for each(long orphanId in _orphanedFeeders)
            {
                if (orphanId== feederId)
                    return true;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return false;
}
bool CtiCCSubstationBusStore::isCapBankOrphan(long capBankId)
{
    try
    {
        CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
        {
            for each (long orphanId in _orphanedCapBanks)
            {
                if (orphanId == capBankId)
                {
                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                    {
                        CTILOG_DEBUG(dout, "Cap " <<capBankId<<" is on Orphan list. ");
                    }
                    return true;
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return false;
}
void CtiCCSubstationBusStore::removeFromOrphanList(long ccId)
{
    try
    {
        CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
        _orphanedFeeders.erase(remove(_orphanedFeeders.begin(), _orphanedFeeders.end(), ccId), _orphanedFeeders.end());
        _orphanedCapBanks.erase(remove(_orphanedCapBanks.begin(), _orphanedCapBanks.end(), ccId), _orphanedCapBanks.end());
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCCSubstationBusStore::deleteSubstation(long substationId)
{
    try
    {
        if ( CtiCCSubstationPtr substationToDelete = findSubstationByPAObjectID( substationId ) )
        {
            CtiCapController::getInstance()->unregisterPaoForPointUpdates( *substationToDelete );
            
            setStoreRecentlyReset(true);
            //Using the list because deleteSubbus(int) removes the subbus from the map, invalidating our counter for the loop.
            //Quick fix, a more elegant solution should be found.
            std::list<int> deleteList;
            for(long h=0;h<_ccSubstationBuses->size();h++)
            {
                CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(h);
                if (currentCCSubstationBus->getParentId() == substationId)
                {
                     deleteList.push_back(currentCCSubstationBus->getPaoId());
                }
            }
            for( std::list<int>::iterator itr = deleteList.begin(); itr != deleteList.end(); itr++)
            {
                deleteSubBus(*itr);
            }

            try
            {
                //Delete pointids on this sub
                for ( const long pointid : *substationToDelete->getPointIds() )
                {
                    {    // _pointid_station_map
                        for ( auto range = _pointid_station_map.equal_range( pointid );
                              range.first != range.second;
                              ++range.first )
                        {
                            if ( range.first->second == substationToDelete )
                            {
                                _pointid_station_map.erase( range.first );
                                break;
                            }
                        }
                    }
                    {    // _pointID_to_pao
                        for ( auto range = _pointID_to_pao.equal_range( pointid );
                              range.first != range.second;
                              ++range.first )
                        {
                            if ( range.first->second == substationToDelete )
                            {
                                _pointID_to_pao.erase( range.first );
                                break;
                            }
                        }
                    }
                }
                substationToDelete->getPointIds()->clear();
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            try
            {
                if ( const long areaId = substationToDelete->getParentId() )
                {
                    if ( CtiCCAreaPtr area = findAreaByPAObjectID(areaId) )
                    {
                        area->removeSubstationId(substationId);
                    }
                }
                for ( CtiCCSpecialPtr spArea : *_ccSpecialAreas )
                {
                    if ( spArea )
                    {
                        spArea->removeSubstationId(substationId);
                    }
                }

                std::string substationName = substationToDelete->getPaoName();

                _substation_area_map.erase(substationToDelete->getPaoId());
                _substation_specialarea_map.erase(substationToDelete->getPaoId());
                _ccSubstations.erase( std::remove( _ccSubstations.begin(), _ccSubstations.end(), substationToDelete ),
                                       _ccSubstations.end() );

                _paobject_substation_map.erase(substationToDelete->getPaoId());

                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    CTILOG_INFO(dout, "SUBSTATION: " << substationName <<" has been deleted.");
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


void CtiCCSubstationBusStore::deleteArea(long areaId)
{
    try
    {
        if ( CtiCCAreaPtr areaToDelete = findAreaByPAObjectID( areaId ) )
        {
            CtiCapController::getInstance()->unregisterPaoForPointUpdates( *areaToDelete );

            setStoreRecentlyReset(true);
            try
            {
                //Delete pointids on this area
                for ( const long pointid : *areaToDelete->getPointIds() )
                {
                    {   // _pointid_area_map
                        for ( auto range = _pointid_area_map.equal_range( pointid );
                              range.first != range.second;
                              ++range.first )
                        {
                            if ( range.first->second == areaToDelete )
                            {
                                _pointid_area_map.erase( range.first );
                                break;
                            }
                        }
                    }
                    {   // _pointID_to_pao
                        for ( auto range = _pointID_to_pao.equal_range( pointid );
                              range.first != range.second;
                              ++range.first )
                        {
                            if ( range.first->second == areaToDelete )
                            {
                                _pointID_to_pao.erase( range.first );
                                break;
                            }
                        }
                    }
                }
                areaToDelete->getPointIds()->clear();
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            for ( const long stationId : areaToDelete->getSubstationIds() )
            {
                if ( CtiCCSubstationPtr station = findSubstationByPAObjectID( stationId ) )
                {
                    if (station->getSaEnabledId() > 0)
                    {
                        continue;
                    }
                    for ( const long subBusId : station->getCCSubIds() )
                    {
                        deleteSubBus(subBusId);
                    }
                    station->getCCSubIds().clear();
                }
                deleteSubstation(stationId);
                areaToDelete->removeSubstationId(stationId);
            }

            try
            {
                std::string areaName = areaToDelete->getPaoName();
                _paobject_area_map.erase(areaToDelete->getPaoId());
                _ccGeoAreas->erase( std::remove( _ccGeoAreas->begin(), _ccGeoAreas->end(), areaToDelete ),
                                    _ccGeoAreas->end() );

                delete areaToDelete;

                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    CTILOG_INFO(dout, "AREA: " << areaName <<" has been deleted.");
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCCSubstationBusStore::deleteSpecialArea(long areaId)
{
    try
    {
        if ( CtiCCSpecialPtr spAreaToDelete = findSpecialAreaByPAObjectID( areaId ) )
        {
            CtiCapController::getInstance()->unregisterPaoForPointUpdates( *spAreaToDelete );

            setStoreRecentlyReset(true);
            try
            {
                //Delete pointids on this special area
                for ( const long pointid : *spAreaToDelete->getPointIds() )
                {
                    {   // _pointid_specialarea_map
                        for ( auto range = _pointid_specialarea_map.equal_range( pointid );
                              range.first != range.second;
                              ++range.first )
                        {
                            if ( range.first->second == spAreaToDelete )
                            {
                                _pointid_specialarea_map.erase( range.first );
                                break;
                            }
                        }
                    }
                    {   // _pointID_to_pao
                        for ( auto range = _pointID_to_pao.equal_range( pointid );
                              range.first != range.second;
                              ++range.first )
                        {
                            if ( range.first->second == spAreaToDelete )
                            {
                                _pointID_to_pao.erase( range.first );
                                break;
                            }
                        }
                    }
                }
                spAreaToDelete->getPointIds()->clear();
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            ChildToParentMultiMap::const_iterator iter = _substation_specialarea_map.begin();
            while (iter != _substation_specialarea_map.end())
            {
               CtiCCSubstationPtr station = findSubstationByPAObjectID(iter->first);
               if (station != NULL && station->getSaEnabledId() == areaId)
               {
                   station->setSaEnabledId(0);
                   station->setSaEnabledFlag(false);
               }
               iter++;
            }

            _substation_specialarea_map.erase(areaId);

            try
            {
                std::string areaName = spAreaToDelete->getPaoName();
                _paobject_specialarea_map.erase(spAreaToDelete->getPaoId());
                _ccSpecialAreas->erase( std::remove( _ccSpecialAreas->begin(), _ccSpecialAreas->end(), spAreaToDelete ),
                                        _ccSpecialAreas->end() );

                delete spAreaToDelete;

                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    CTILOG_INFO(dout, "SPECIAL AREA: " << areaName <<" has been deleted.");
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

namespace
{

template <typename MultiMap>
void multimapRemoveOne( MultiMap & mmap, const typename MultiMap::key_type key, const typename MultiMap::mapped_type oneToRemove )
{
    for ( auto range = mmap.equal_range( key );
          range.first != range.second;
          ++range.first )
    {
        if ( range.first->second == oneToRemove )
        {
            mmap.erase( range.first );
            break;
        }
    }
}

}

void CtiCCSubstationBusStore::deleteSubBus(long subBusId)
{
    try
    {
        if ( CtiCCSubstationBusPtr subToDelete = findSubBusByPAObjectID(subBusId) )
        {
            CtiCapController::getInstance()->unregisterPaoForPointUpdates( *subToDelete );

            setStoreRecentlyReset(true);
            for ( int feederId : subToDelete->getCCFeederIds() )
            {
                deleteFeeder(feederId);
            }

            try
            {
                //Delete pointids on this sub
                for ( long pointid : *subToDelete->getPointIds() )
                {
                    multimapRemoveOne( _pointid_subbus_map, pointid, subToDelete );
                }
                subToDelete->getPointIds()->clear();

                for ( long pointid : subToDelete->getAllMonitorPointIds() )
                {
                    multimapRemoveOne( _pointid_subbus_map, pointid, subToDelete );
                }
                subToDelete->removeAllMonitorPoints();
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            //DUAL BUS Alt Sub deletion...
            try
            {
                //Deleting subs that have this sub as altSub
                for ( auto range = _altsub_sub_idmap.equal_range( subBusId );
                      range.first != range.second;
                      ++range.first )
                {
                    if ( CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID( range.first->second ) )
                    {
                        long pointID = 0;

                        switch ( tempSub->getStrategy()->getUnitType() )
                        {
                            case ControlStrategy::KVar:
                            case ControlStrategy::PFactorKWKVar:
                            case ControlStrategy::PFactorKWKQ:
                            {
                                pointID = subToDelete->getCurrentVarLoadPointId();
                                break;
                            }
                            case ControlStrategy::Volts:
                            {
                                pointID = subToDelete->getCurrentVoltLoadPointId();
                                break;
                            }
                            default:
                                continue;
                        }

                        multimapRemoveOne( _pointid_subbus_map, pointID, tempSub );
                        tempSub->removePointId( pointID );
                    }
                }
                _altsub_sub_idmap.erase( subBusId );

                //Deleting this sub from altSubMap
                if (subToDelete->getAltDualSubId() != subBusId)
                {
                    PaoIdToPointIdMultiMap::iterator iter = _altsub_sub_idmap.begin();
                    while (iter != _altsub_sub_idmap.end())
                    {
                        if (iter->second == subBusId)
                        {
                            _altsub_sub_idmap.erase(iter);
                            iter = _altsub_sub_idmap.end();
                        }
                        else
                            iter++;
                    }
                }

                // this is a sanity check to make sure we've removed all the references to the bus
                //  that we are deleting from the _pointid_subbus_map
                for ( auto iter = _pointid_subbus_map.begin(); iter != _pointid_subbus_map.end();  )
                {
                    if ( iter->second == subToDelete )
                    {
                        iter = _pointid_subbus_map.erase( iter );
                    }
                    else
                    {
                        ++iter;
                    }
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            try
            {
                std::string subBusName = subToDelete->getPaoName();

                _paobject_subbus_map.erase( subBusId );
                _subbus_substation_map.erase( subBusId );
                boost::remove_erase( *_ccSubstationBuses, subToDelete );

                delete subToDelete;

                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    CTILOG_INFO(dout, "SUBBUS: " << subBusName <<" has been deleted.");
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCCSubstationBusStore::deleteFeeder(long feederId)
{
    try
    {
        if ( CtiCCFeederPtr feederToDelete = findFeederByPAObjectID( feederId ) )
        {
            CtiCapController::getInstance()->unregisterPaoForPointUpdates( *feederToDelete );

            setStoreRecentlyReset(true);

            for ( int capBankId : feederToDelete->getAllCapBankIds() )
            {
                deleteCapBank(capBankId);
            }

            //Delete pointids on this feeder
            for ( long pointid : *feederToDelete->getPointIds() )
            {
                multimapRemoveOne( _pointid_feeder_map, pointid, feederToDelete );
            }
            feederToDelete->getPointIds()->clear();

            try
            {
                if ( long subBusId = findSubBusIDbyFeederID( feederId ) )
                {
                    if ( CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID( subBusId ) )
                    {
                        subBus->deleteCCFeeder(feederId);
                    }
                }

                std::string feederName = feederToDelete->getPaoName();

                _paobject_feeder_map.erase( feederId );
                _feeder_subbus_map.erase( feederId );

                delete feederToDelete;

                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    CTILOG_INFO(dout, "FEEDER: " << feederName <<" has been deleted.");
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    } 
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCCSubstationBusStore::deleteCapBank(long capBankId)
{
    try
    {
        if ( CtiCCCapBankPtr capBankToDelete = findCapBankByPAObjectID( capBankId ) )
        {
            CtiCapController::getInstance()->unregisterPaoForPointUpdates( *capBankToDelete );

            setStoreRecentlyReset(true);

            //Delete pointids on this feeder
            for ( long pointid : *capBankToDelete->getPointIds() )
            {
                multimapRemoveOne( _pointid_capbank_map, pointid, capBankToDelete );
            }
            capBankToDelete->getPointIds()->clear();

            for ( CtiCCMonitorPointPtr mPoint : capBankToDelete->getMonitorPoint() )
            {
                multimapRemoveOne( _pointid_capbank_map, mPoint->getPointId(), capBankToDelete );
                mPoint.reset();
            }
            capBankToDelete->getMonitorPoint().clear();

            for ( long pointID : capBankToDelete->heartbeat._policy->getRegistrationPointIDs() )
            {
                multimapRemoveOne( _pointid_capbank_map, pointID, capBankToDelete );
            }

            try
            {
                if ( long feederId = findFeederIDbyCapBankID( capBankId ) )
                {
                    if ( CtiCCFeederPtr feeder = findFeederByPAObjectID( feederId ) )
                    {
                        feeder->deleteCCCapBank(capBankId);
                    }
                }

                std::string capBankName = capBankToDelete->getPaoName();

                _paobject_capbank_map.erase( capBankId );
                _capbank_subbus_map.erase( capBankId );
                _capbank_feeder_map.erase( capBankId );
                _cbc_capbank_map.erase( capBankToDelete->getControlDeviceId() );

                delete capBankToDelete;

                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    CTILOG_INFO(dout, "CAPBANK: " << capBankName <<" has been deleted.");
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

bool CtiCCSubstationBusStore::handleAreaDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                 PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{
    bool forceFullReload = false;

    if (reloadAction == ChangeTypeDelete)
    {
        deleteArea(reloadId);

        CtiCCExecutorFactory::createExecutor(new DeleteItem(reloadId))->execute();

        msgSubsBitMask = CtiCCSubstationsMsg::AllSubsSent;;
        msgBitMask  = CtiCCSubstationBusMsg::AllSubBusesSent;;
    }
    else  // ChangeTypeAdd, ChangeTypeUpdate
    {
        CtiCCAreaPtr tempArea = findAreaByPAObjectID(reloadId);

        if (tempArea != NULL &&tempArea->getSubstationIds().size() > 1)
        {
            setValid(false);
            _reloadList.clear();
            forceFullReload = true;
        }
        else
        {

            reloadAreaFromDatabase(reloadId, &_paobject_area_map, &_pointid_area_map, _ccGeoAreas);

            tempArea = findAreaByPAObjectID(reloadId);
            if (tempArea != NULL)
            {
                updateModifiedStationsAndBusesSets(tempArea->getSubstationIds(),msgBitMask, msgSubsBitMask,
                                                     modifiedBusIdsSet,  modifiedStationIdsSet);
                if (tempArea->getDisableFlag())
                    tempArea->checkForAndStopVerificationOnChildSubBuses(capMessages);
            }

        }
    }
    return forceFullReload;
}

void CtiCCSubstationBusStore::handleCapBankDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                 PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{

    if (reloadAction == ChangeTypeUpdate)
     {
        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
        {
          CTILOG_DEBUG(dout, "Reload Cap "<< reloadId<<" because of ChangeTypeUpdate message ");
        }
        if (long oldBusId = findSubBusIDbyCapBankID(reloadId))
        {
            insertDBReloadList(CcDbReloadInfo(oldBusId, ChangeTypeUpdate, Cti::CapControl::SubBus));

        }
        reloadCapBankFromDatabase(reloadId, &_paobject_capbank_map, &_paobject_feeder_map,
                                   &_paobject_subbus_map, &_pointid_capbank_map, &_capbank_subbus_map,
                                   &_capbank_feeder_map, &_feeder_subbus_map, &_cbc_capbank_map );

         if (long busId = findSubBusIDbyCapBankID(reloadId))
         {
             reloadMonitorPointsFromDatabase(busId, &_paobject_capbank_map, &_paobject_feeder_map,
                                       &_paobject_subbus_map, &_pointid_capbank_map, &_pointid_subbus_map);
         }

         if(isCapBankOrphan(reloadId) )
            removeFromOrphanList(reloadId);

         CtiCCCapBankPtr cap = findCapBankByPAObjectID(reloadId);
         if (cap != NULL)
         {
             //This finds the subId if a capbank is on a substation.
             if (long subId = findSubBusIDbyCapBankID(reloadId))
             {
                modifiedBusIdsSet.insert(subId);
                msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
             }
             else
             {
                 if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                 {
                 CTILOG_DEBUG(dout, " Update Cap NOT found on sub  ");
                 }
             }
         }
     }
     else if (reloadAction == ChangeTypeDelete)
     {
         if (long subId = findSubBusIDbyCapBankID(reloadId))
         {
             modifiedBusIdsSet.insert(subId);
             msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
             if (CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(subId))
             {
                 if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                 {
                      CTILOG_DEBUG(dout, "Delete Cap "<<reloadId <<" was found on sub; Sub " <<tempSub->getPaoName()<<" modified ");
                 }
             }
         }
         else
         {
             if( _CC_DEBUG & CC_DEBUG_EXTENDED )
             {
                  CTILOG_DEBUG(dout, "Delete Cap was NOT found on sub ");
             }
         }

         deleteCapBank(reloadId);

         if (isCapBankOrphan(reloadId))
         {
             removeFromOrphanList(reloadId);
         }

         CtiCCExecutorFactory::createExecutor(new DeleteItem(reloadId))->execute();
     }

}

void CtiCCSubstationBusStore::handleSubstationDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                 PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{
    if (reloadAction == ChangeTypeDelete)
    {
        deleteSubstation(reloadId);

        CtiCCExecutorFactory::createExecutor(new DeleteItem(reloadId))->execute();
    }
    else  // ChangeTypeAdd, ChangeTypeUpdate
    {

        CtiCCSubstationPtr tempStation = findSubstationByPAObjectID(reloadId);
        if (tempStation != NULL)
        {
            msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
        }
        else
        {
            msgSubsBitMask |= CtiCCSubstationsMsg::SubAdded;
        }
        reloadSubstationFromDatabase(reloadId, &_paobject_substation_map,
                           &_paobject_area_map, &_paobject_specialarea_map,
                                     &_pointid_station_map, &_substation_area_map,
                           &_substation_specialarea_map, &_ccSubstations);

        CtiCCSubstationPtr station = findSubstationByPAObjectID(reloadId);
        if (station != NULL)
        {
            addVectorIdsToSet(station->getCCSubIds(), modifiedBusIdsSet);
            modifiedStationIdsSet.insert(reloadId);
            if (station->getDisableFlag())
                station->checkForAndStopVerificationOnChildSubBuses(capMessages);
        }

    }
}

void CtiCCSubstationBusStore::handleSubBusDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                     PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{
    if (reloadAction == ChangeTypeDelete)
    {
        long parentStationId = NULL;
        CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(reloadId);
        if (tempSub != NULL)
        {
            parentStationId = tempSub->getParentId();
        }
        deleteSubBus(reloadId);

        CtiCCExecutorFactory::createExecutor(new DeleteItem(reloadId))->execute();

        if (parentStationId != NULL)
        {
            modifiedStationIdsSet.insert(parentStationId);
            msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
        }


    }
    else  // ChangeTypeAdd, ChangeTypeUpdate
    {

        CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(reloadId);
        if (tempSub != NULL)
        {
            msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
            msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
        }
        else
        {
            msgBitMask |= CtiCCSubstationBusMsg::SubBusAdded;
        }

        reloadSubBusFromDatabase(reloadId, &_paobject_subbus_map,
                                 &_paobject_substation_map, &_pointid_subbus_map,
                                 &_altsub_sub_idmap, &_subbus_substation_map, _ccSubstationBuses);

        modifiedBusIdsSet.insert(reloadId);
    }
}

void CtiCCSubstationBusStore::handleFeederDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                     PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{

    if (reloadAction == ChangeTypeUpdate)
    {
        if (CtiCCFeederPtr tempFeed = findFeederByPAObjectID(reloadId))
        {
            long oldBusId = tempFeed->getParentId();
            if (oldBusId > 0)
            {
                insertDBReloadList(CcDbReloadInfo(oldBusId, ChangeTypeUpdate, Cti::CapControl::SubBus));
            }
        }
        reloadFeederFromDatabase(reloadId, &_paobject_feeder_map,
                                  &_paobject_subbus_map, &_pointid_feeder_map, &_feeder_subbus_map );

        if(isFeederOrphan(reloadId))
              removeFromOrphanList(reloadId);
    }
    if (long subId = findSubBusIDbyFeederID(reloadId))
    {
        modifiedBusIdsSet.insert(subId);
        msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
    }

    if (reloadAction == ChangeTypeDelete)
    {
        deleteFeeder(reloadId);
        if (isFeederOrphan(reloadId))
        {
            removeFromOrphanList(reloadId);
        }

        CtiCCExecutorFactory::createExecutor(new DeleteItem(reloadId))->execute();
    }
}

bool CtiCCSubstationBusStore::handleSpecialAreaDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                 PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{
    deleteSpecialArea(reloadId);
    setValid(false);
    _reloadList.clear();
    return true;
}


void CtiCCSubstationBusStore::updateModifiedStationsAndBusesSets(PaoIdVector stationIdList, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet)
{
    for each (long stationId in stationIdList)
    {
        CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
        if (station != NULL)
        {
            addVectorIdsToSet(station->getCCSubIds(), modifiedBusIdsSet);
            msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
        }
    }
    addVectorIdsToSet(stationIdList, modifiedStationIdsSet);
    msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
}
void CtiCCSubstationBusStore::addVectorIdsToSet(const PaoIdVector idVector, PaoIdSet &idSet)
{
    for each (long paoId in idVector)
    {
        idSet.insert(paoId);
    }
}

void CtiCCSubstationBusStore::handleStrategyDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                 PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{
    if (reloadAction != ChangeTypeDelete)
    {
        reloadStrategyFromDatabase(reloadId);

        for ( CtiCCSpecialPtr spArea : *_ccSpecialAreas )
        {
            if (!spArea->getDisableFlag() && spArea->getStrategy()->getStrategyId() == reloadId)
            {
                addVectorIdsToSet(spArea->getSubstationIds(), modifiedStationIdsSet);
            }
        }

        for ( CtiCCAreaPtr area : *_ccGeoAreas )
        {
            if (!area->getDisableFlag() && area->getStrategy()->getStrategyId() == reloadId)
            {
                addVectorIdsToSet(area->getSubstationIds(), modifiedStationIdsSet);
            }
        }

        for ( CtiCCSubstationBusPtr bus : *_ccSubstationBuses )
        {
            if ( bus->getStrategy()->getStrategyId() == reloadId )
            {
                modifiedBusIdsSet.insert( bus->getPaoId() );

                msgBitMask     |= CtiCCSubstationBusMsg::SubBusModified;
                msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
            }
            else
            {
                for ( CtiCCFeederPtr feeder : bus->getCCFeeders() )
                {
                    if ( feeder->getStrategy()->getStrategyId() == reloadId )
                    {
                        modifiedBusIdsSet.insert( bus->getPaoId() );    // bus message updates feeders too

                        msgBitMask     |= CtiCCSubstationBusMsg::SubBusModified;
                        msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                    }
                }
            }
        }
    }
    else
    {
        _strategyManager->unload(reloadId);
    }
}

void CtiCCSubstationBusStore::initializeAllPeakTimeFlagsAndMonitorPoints(bool setTargetVarFlag)
{

    CtiTime currentDateTime;
    for(long i=0;i<_ccSubstationBuses->size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];
        calculateParentPowerFactor(currentSubstationBus->getPaoId());
        currentSubstationBus->getMultipleMonitorPoints().clear();

        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(long j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            bool peakFlag = currentSubstationBus->isPeakTime(currentDateTime);

            if ( currentSubstationBus->getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder &&
                !(ciStringEqual(currentFeeder->getStrategy()->getStrategyName(),"(none)"))  &&
                (currentFeeder->getStrategy()->getPeakStartTime() > 0 && currentFeeder->getStrategy()->getPeakStopTime() > 0 ))
            {
                currentFeeder->isPeakTime(currentDateTime);
            }
            else
            {
                currentFeeder->setPeakTimeFlag(peakFlag);
            }


            currentFeeder->getMultipleMonitorPoints().clear();
            CtiCCCapBank_SVector& capBanks = currentFeeder->getCCCapBanks();

            for (long k=0;k<capBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)capBanks[k];

                vector <CtiCCMonitorPointPtr>& monPoints = currentCapBank->getMonitorPoint();
                for (long l=0; l<monPoints.size();l++)
                {
                    currentFeeder->getMultipleMonitorPoints().push_back(monPoints[l]);
                    currentSubstationBus->getMultipleMonitorPoints().push_back(monPoints[l]);
                }
            }
        }
        if (setTargetVarFlag)
        {
            //currentSubstationBus->figureAndSetTargetVarValue();
        }
        checkAndUpdateVoltReductionFlagsByBus(currentSubstationBus);
        currentSubstationBus->figureAndSetPowerFactorByFeederValues();
    }
}

CtiCCSubstationBus_set CtiCCSubstationBusStore::getAllSubBusesByIds(PaoIdSet  modifiedBusIdsSet)
{
    CtiCCSubstationBus_set buses;
    for each (long busId in modifiedBusIdsSet)
    {
        if (CtiCCSubstationBusPtr bus = findSubBusByPAObjectID(busId))
        {
            buses.insert(bus);
        }
    }
    return buses;
}


void CtiCCSubstationBusStore::createAndSendClientMessages( unsigned long &msgBitMask, unsigned long &msgSubsBitMask, PaoIdSet &modifiedBusIdsSet,
                                                           PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages)
{
    if (msgBitMask & CtiCCSubstationBusMsg::AllSubBusesSent)
    {
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(*_ccSubstationBuses,msgBitMask))->execute();
    }
    else if (modifiedBusIdsSet.size() > 0)
    {
        CtiCCSubstationBus_set modifiedBusSet = getAllSubBusesByIds(modifiedBusIdsSet);
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationBusMsg(modifiedBusSet,msgBitMask))->execute();
    }

    CtiCCExecutorFactory::createExecutor(new CtiCCCapBankStatesMsg(*_ccCapBankStates))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCGeoAreasMsg(*_ccGeoAreas))->execute();
    CtiCCExecutorFactory::createExecutor(new CtiCCSpecialAreasMsg(*_ccSpecialAreas))->execute();

    if (msgSubsBitMask & CtiCCSubstationsMsg::AllSubsSent)
    {
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(_ccSubstations, msgSubsBitMask))->execute();
    }
    else if (modifiedStationIdsSet.size() > 0 )
    {
        CtiCCSubstation_set modifiedStationSet;

        // add called out stations
        for each ( long stationID in modifiedStationIdsSet )
        {
            if ( CtiCCSubstationPtr station = findSubstationByPAObjectID(stationID) )
            {
                modifiedStationSet.insert( station );
            }
        }

        // add stations from called out busses
        for each (long busId in modifiedBusIdsSet)
        {
            if (CtiCCSubstationBusPtr bus = findSubBusByPAObjectID(busId))
            {
                if (CtiCCSubstationPtr station = findSubstationByPAObjectID(bus->getParentId()))
                {
                    modifiedStationSet.insert(station);
                }
            }
        }
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(modifiedStationSet, msgSubsBitMask))->execute();
    }
    try
    {
        for(int i=0;i<capMessages.size( );i++)
        {
            CtiCCExecutorFactory::createExecutor((CtiMessage*)capMessages[i])->execute();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}

void CtiCCSubstationBusStore::checkDBReloadList()
{
    bool sendBusInfo = false;
    bool forceFullReload = false;

    PaoIdSet modifiedBusIdsSet;
    PaoIdSet modifiedStationIdsSet;
    CtiMultiMsg_vec capMessages;
    unsigned long msgBitMask = 0x00000000;
    unsigned long msgSubsBitMask = 0x00000000;

    try
    {
        CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
        {

            if (_reloadList.empty() && _2wayFlagUpdate)
            {
                dumpAllDynamicData();
                _2wayFlagUpdate = false;
            }
            else if (!_reloadList.empty())
            {
                dumpAllDynamicData();
                _2wayFlagUpdate = false;
            }
            while (!_reloadList.empty())
            {
                CcDbReloadInfo reloadTemp = _reloadList.front();

                switch (reloadTemp.objecttype)
                {
                    //capbank
                    case CapBank:
                    {
                        handleCapBankDBChange(reloadTemp.objectId, reloadTemp.action, msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );
                        break;
                    }
                    //feeder
                    case Feeder:
                    {
                        handleFeederDBChange(reloadTemp.objectId, reloadTemp.action,  msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );

                        break;
                    }
                    //subbus
                    case SubBus:
                    {
                        handleSubBusDBChange(reloadTemp.objectId, reloadTemp.action,  msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );

                        break;
                    }
                    //substation
                    case Substation:
                    {
                        handleSubstationDBChange(reloadTemp.objectId, reloadTemp.action,  msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );

                        break;
                    }
                    //area
                    case Area:
                    {
                        forceFullReload = handleAreaDBChange(reloadTemp.objectId, reloadTemp.action, msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );

                        break;
                    }
                    //special area
                    case SpecialArea:
                    {
                        forceFullReload = handleSpecialAreaDBChange(reloadTemp.objectId, reloadTemp.action, msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );

                        break;
                    }
                    case Strategy:
                    {
                        handleStrategyDBChange(reloadTemp.objectId, reloadTemp.action,  msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );

                        break;
                    }
                    case ZoneType:
                    {
                        handleZoneDBChange(reloadTemp.objectId, reloadTemp.action,  msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );
                        break;
                    }
                    case VoltageRegulatorType:
                    {
                        handleVoltageRegulatorDBChange(reloadTemp.objectId, reloadTemp.action,  msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );
                        break;
                    }
                    case MonitorPoint:
                    {
                        handleMonitorPointDBChange(reloadTemp.objectId, reloadTemp.action,  msgBitMask, msgSubsBitMask,
                                                 modifiedBusIdsSet,  modifiedStationIdsSet, capMessages );
                        break;
                    }
                    default:
                        break;
                }
                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                {
                    CTILOG_DEBUG(dout, "DBChange ");
                }
                if (!forceFullReload)
                {
                    sendBusInfo = true;
                    _reloadList.pop_front();
                }
            }

            if (sendBusInfo)
            {
                locateOrphans(&_orphanedCapBanks, &_orphanedFeeders, _paobject_capbank_map, _paobject_feeder_map,
                                     _capbank_feeder_map, _feeder_subbus_map);

                try
                {
                    for(int i=0;i<capMessages.size( );i++)
                    {
                        CtiCCExecutorFactory::createExecutor((CtiMessage*)capMessages[i])->execute();
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
                _lastindividualdbreloadtime = _lastindividualdbreloadtime.now();

                initializeAllPeakTimeFlagsAndMonitorPoints(false);

                createAndSendClientMessages(msgBitMask, msgSubsBitMask, modifiedBusIdsSet, modifiedStationIdsSet, capMessages);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}

void CtiCCSubstationBusStore::checkAndUpdateVoltReductionFlagsByBus(CtiCCSubstationBusPtr bus)
{

    CtiCCSubstationPtr currentStation = findSubstationByPAObjectID(bus->getParentId());
    if (currentStation != NULL)
    {
        currentStation->checkAndUpdateChildVoltReductionFlags();
        CtiCCAreaPtr currentArea = findAreaByPAObjectID(currentStation->getParentId());
        if (currentArea != NULL)
        {
            currentArea->checkAndUpdateChildVoltReductionFlags();
        }
    }
}

void CtiCCSubstationBusStore::updateSubstationObjectSet(long substationId, CtiCCSubstation_set &modifiedStationsSet)
{
    if ( CtiCCSubstationPtr station = findSubstationByPAObjectID( substationId ) )
    {
        modifiedStationsSet.insert(station);
    }
}

void CtiCCSubstationBusStore::updateAreaObjectSet(long areaId, CtiCCArea_set &modifiedAreasSet)
{
    if ( CtiCCAreaPtr area = findAreaByPAObjectID( areaId ) )
    {
        modifiedAreasSet.insert(area);
    }
}


void CtiCCSubstationBusStore::insertDBReloadList(CcDbReloadInfo x)
{
    if (x.objecttype == Strategy)
    {
        _reloadList.push_front(x);
    }
    else
    {

        if ( std::find (_reloadList.begin(), _reloadList.end(),  x ) == _reloadList.end())
        {
            _reloadList.push_back(x);
        }
    }
}

void CtiCCSubstationBusStore::clearDBReloadList()
{
    if (!_reloadList.empty())
    {
        _reloadList.clear();
    }
}

void CtiCCSubstationBusStore::insertUnsolicitedCapBankList(CtiCCCapBankPtr x)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    if (!list_contains(_unsolicitedCapBanks, x))
    {
        _unsolicitedCapBanks.push_back(x);
    }
}

void CtiCCSubstationBusStore::removeCapbankFromUnsolicitedCapBankList(CtiCCCapBankPtr x)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    _unsolicitedCapBanks.remove(x);
}

void CtiCCSubstationBusStore::clearUnsolicitedCapBankList()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    if (!_unsolicitedCapBanks.empty())
    {
        //_unsolicitedCapBanks.clear();
        CapBankList::iterator listIter = _unsolicitedCapBanks.begin();
        while (listIter != _unsolicitedCapBanks.end())
        {
            CtiCCCapBankPtr currentCapBank = *listIter;
            listIter++;
            if (!currentCapBank->getUnsolicitedPendingFlag())
            {
                removeCapbankFromUnsolicitedCapBankList(currentCapBank);
            }
        }
    }

}


void CtiCCSubstationBusStore::checkUnsolicitedList()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    CapBankList tempList = getUnsolicitedCapBankList();
    CapBankList::iterator listIter = tempList.begin();
    while (listIter != tempList.end())
    {
        CtiCCCapBankPtr currentCapBank = *listIter;

        if (currentCapBank->getUnsolicitedChangeTimeUpdated().seconds() <= CtiTime().seconds() - 2)
        {
            CtiCCFeederPtr currentFeeder = findFeederByPAObjectID(currentCapBank->getParentId());
            if (currentFeeder == NULL)
            {
                listIter++;
                continue;
            }
            CtiCCSubstationBusPtr currentSubstationBus = findSubBusByPAObjectID(currentFeeder->getParentId());
            if (currentSubstationBus == NULL)
            {
                listIter++;
                continue;
            }

            CtiCapController::getInstance()->handleUnsolicitedMessaging(currentCapBank, currentFeeder, currentSubstationBus, currentCapBank->getTwoWayPoints());
        }
        else
        {
            if (_CC_DEBUG & CC_DEBUG_UNSOLICITED)
            {

                // print out something about it not processing the thing....
                CTILOG_INFO(dout, "UNSOLICTED MSG for: "<< currentCapBank->getPaoName()<<" Not Processed - TIME: " << currentCapBank->getUnsolicitedChangeTimeUpdated());
            }
        }
        listIter++;
    }

    clearUnsolicitedCapBankList();
}


void CtiCCSubstationBusStore::insertUnexpectedUnsolicitedList(CtiCCCapBankPtr x)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    if (!list_contains(_unexpectedUnsolicited, x))
    {
        _unexpectedUnsolicited.push_back(x);
    }
}

void CtiCCSubstationBusStore::removeFromUnexpectedUnsolicitedList(CtiCCCapBankPtr x)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    _unexpectedUnsolicited.remove(x);
}

void CtiCCSubstationBusStore::clearUnexpectedUnsolicitedList()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    if (!_unexpectedUnsolicited.empty())
    {
        CapBankList::iterator listIter = _unexpectedUnsolicited.begin();
        while (listIter != _unexpectedUnsolicited.end())
        {
            CtiCCCapBankPtr currentCapBank = *listIter;
            listIter++;
            if (!currentCapBank->getUnsolicitedPendingFlag())
            {
                removeFromUnexpectedUnsolicitedList(currentCapBank);
            }
        }
    }

}


void CtiCCSubstationBusStore::checkUnexpectedUnsolicitedList()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    for (CapBankList::iterator listIter = _unexpectedUnsolicited.begin(); listIter != _unexpectedUnsolicited.end(); listIter++)
    {
        CtiCCCapBankPtr currentCapBank = *listIter;

        if (currentCapBank->getUnsolicitedChangeTimeUpdated().seconds() <= CtiTime().seconds() - 2)
        {
            CtiCCFeederPtr currentFeeder = findFeederByPAObjectID(currentCapBank->getParentId());
            if (currentFeeder == NULL)
            {
                continue;
            }
            CtiCCSubstationBusPtr currentSubstationBus = findSubBusByPAObjectID(currentFeeder->getParentId());
            if (currentSubstationBus == NULL)
            {
                continue;
            }

            CtiCapController::getInstance()->handleUnexpectedUnsolicitedMessaging(currentCapBank, currentFeeder, currentSubstationBus, currentCapBank->getTwoWayPoints());
        }
        else
        {
            if (_CC_DEBUG & CC_DEBUG_UNSOLICITED)
            {

                // print out something about it not processing the thing....
                CTILOG_INFO(dout, "UNSOLICTED MSG for: "<< currentCapBank->getPaoName()<<" Not Processed - TIME: " << currentCapBank->getUnsolicitedChangeTimeUpdated());
            }
        }
    }

    clearUnexpectedUnsolicitedList();
}


void CtiCCSubstationBusStore::insertRejectedCapBankList(CtiCCCapBankPtr x)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    if (!list_contains(_rejectedCapBanks, x))
    {
        _rejectedCapBanks.push_back(x);
    }
}

void CtiCCSubstationBusStore::removeCapbankFromRejectedCapBankList(CtiCCCapBankPtr x)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    _rejectedCapBanks.remove(x);
}

void CtiCCSubstationBusStore::clearRejectedCapBankList()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    if (!_rejectedCapBanks.empty())
    {
        _rejectedCapBanks.clear();
    }
}
void CtiCCSubstationBusStore::checkRejectedList()
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    CapBankList tempList = getRejectedControlCapBankList();
    CapBankList::iterator listIter = tempList.begin();
    while (listIter != tempList.end())
    {
        CtiCCCapBankPtr currentCapBank = *listIter;


        CtiCCFeederPtr currentFeeder = findFeederByPAObjectID(currentCapBank->getParentId());
        if (currentFeeder == NULL)
        {
            listIter++;
            continue;
        }
        CtiCCSubstationBusPtr currentSubstationBus = findSubBusByPAObjectID(currentFeeder->getParentId());
        if (currentSubstationBus == NULL)
        {
            listIter++;
            continue;
        }

        CtiCapController::getInstance()->handleRejectionMessaging(currentCapBank, currentFeeder, currentSubstationBus, currentCapBank->getTwoWayPoints());
        listIter++;

    }
    clearRejectedCapBankList();
}

void CtiCCSubstationBusStore::insertItemsIntoMap(int mapType, long* first, long* second)
{
    switch (mapType)
    {
        case PaobjectSubBusMap:
        {
            long paobjectId = *first;
            CtiCCSubstationBus* subToInsert = (CtiCCSubstationBus*) second;
            _paobject_subbus_map.insert(make_pair(paobjectId, subToInsert));
            break;
        }
        case PaobjectFeederMap:
        {
            long paobjectId = *first;
            CtiCCFeeder* feederToInsert = (CtiCCFeeder*) second;
            _paobject_feeder_map.insert(make_pair(paobjectId, feederToInsert));

            break;
        }
        case PaobjectCapBankMap:
        {
            long paobjectId = *first;
            CtiCCCapBank* capBankToInsert = (CtiCCCapBank*) second;
            _paobject_capbank_map.insert(make_pair(paobjectId, capBankToInsert));
            break;
        }
        case PointIdSubBusMap:
        {
            long pointId = *first;
            CtiCCSubstationBus* subToInsert = (CtiCCSubstationBus*) second;
            _pointid_subbus_map.insert(make_pair(pointId, subToInsert));
            break;
        }
        case PointIdSubstationMap:
        {
            long pointId = *first;
            CtiCCSubstationPtr subToInsert = (CtiCCSubstationPtr) second;
            _pointid_station_map.insert(make_pair(pointId, subToInsert));
            break;
        }
        case PointIdFeederMap:
        {
            long pointId = *first;
            CtiCCFeeder* feederToInsert = (CtiCCFeeder*) second;
            _pointid_feeder_map.insert(make_pair(pointId, feederToInsert));
            break;
        }
        case PointIdCapBankMap:
        {
            long pointId = *first;
            CtiCCCapBank* capBankToInsert = (CtiCCCapBank*) second;
            _pointid_capbank_map.insert(make_pair(pointId, capBankToInsert));
            break;
        }
        case FeederIdSubBusIdMap:
        {
            long feederId = *first;
            long subBusId = *second;
            _feeder_subbus_map.insert(make_pair(feederId, subBusId));
            break;
        }
        case CapBankIdSubBusIdMap:
        {
            long capBankId = *first;
            long subBusId = *second;
            _capbank_subbus_map.insert(make_pair(capBankId, subBusId));
            break;
        }
        case CapBankIdFeederIdMap:
        {
            long capBankId = *first;
            long feederId = *second;
            _capbank_feeder_map.insert(make_pair(capBankId, feederId));
            break;
        }
        default:
            break;
    }
}

void CtiCCSubstationBusStore::removeItemsFromMap(int mapType, long first)
{
    switch (mapType)
    {
        case PaobjectSubBusMap:
        {
            long paobjectId = first;
            _paobject_subbus_map.erase(paobjectId);
            break;
        }
        case PaobjectFeederMap:
        {
            long paobjectId = first;
            _paobject_feeder_map.erase(paobjectId);
            break;
        }
        case PaobjectCapBankMap:
        {
            long paobjectId = first;
            _paobject_capbank_map.erase(paobjectId);
            break;
        }
        case PointIdSubBusMap:
        {
            long pointId = first;
            _pointid_subbus_map.erase(pointId);
            break;
        }
        case PointIdSubstationMap:
        {
            long pointId = first;
            _pointid_station_map.erase(pointId);
            break;
        }
        case PointIdFeederMap:
        {
            long pointId = first;
            _pointid_feeder_map.erase(pointId);
            break;
        }
        case PointIdCapBankMap:
        {
            long pointId = first;
            _pointid_capbank_map.erase(pointId);
            break;
        }
        case FeederIdSubBusIdMap:
        {
            long feederId = first;
            _feeder_subbus_map.erase(feederId);
            break;
        }
        case CapBankIdSubBusIdMap:
        {
            long capBankId = first;
            _capbank_subbus_map.erase(capBankId);
            break;
        }
        case CapBankIdFeederIdMap:
        {
            long capBankId = first;
            _capbank_feeder_map.erase(capBankId);
            break;
        }
        default:
            break;
    }
}
PaoIdToSubBusMap* CtiCCSubstationBusStore::getPAOSubMap()
{
    return &_paobject_subbus_map;
}

PaoIdToAreaMap & CtiCCSubstationBusStore::getPAOAreaMap()
{
    return _paobject_area_map;
}
PaoIdToSubstationMap & CtiCCSubstationBusStore::getPAOStationMap()
{
    return _paobject_substation_map;
}
PaoIdToSpecialAreaMap & CtiCCSubstationBusStore::getPAOSpecialAreaMap()
{
    return _paobject_specialarea_map;
}


void CtiCCSubstationBusStore::setLinkStatusPointId(long pointId)
{
    _linkStatusPointId = pointId;
}
long CtiCCSubstationBusStore::getLinkStatusPointId(void)
{
    return _linkStatusPointId;
}

void CtiCCSubstationBusStore::setLinkStatusFlag(bool flag)
{
    _linkStatusFlag = flag;
}
bool CtiCCSubstationBusStore::getLinkStatusFlag(void)
{
    return _linkStatusFlag;
}


const CtiTime& CtiCCSubstationBusStore::getLinkDropOutTime() const
{
    return _linkDropOutTime;
}

void CtiCCSubstationBusStore::setLinkDropOutTime(const CtiTime& dropOutTime)
{
    _linkDropOutTime = dropOutTime;
}

bool CtiCCSubstationBusStore::getVoltReductionSystemDisabled()
{
    return _voltReductionSystemDisabled;
}

void CtiCCSubstationBusStore::setVoltReductionSystemDisabled(bool disableFlag)
{
    _voltReductionSystemDisabled = disableFlag;
}

void CtiCCSubstationBusStore::calculateParentPowerFactor( long subBusId )
{
    if ( const long stationId = findSubstationIDbySubBusID( subBusId ) )
    {
        if ( CtiCCSubstationPtr station = findSubstationByPAObjectID( stationId ) )
        {
            station->updatePowerFactorData();
        }

        if ( const long areaId = findAreaIDbySubstationID( stationId ) )
        {
            if ( CtiCCAreaPtr area = findAreaByPAObjectID( areaId ) )
            {
                area->updatePowerFactorData();
            }
        }

        ChildToParentMultiMap::iterator spAreaIter, end;
        findSpecialAreaIDbySubstationID( stationId, spAreaIter, end );

        for ( ; spAreaIter != end; ++spAreaIter )
        {
            if ( CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID( spAreaIter->second ) )
            {
                spArea->updatePowerFactorData();
            }
        }
    }
}

/* Relating to Max Kvar Cparm */
bool CtiCCSubstationBusStore::addKVAROperation( long capbankId, long kvar )
{
    if( !isKVARAvailable( kvar ) )
        return false;

    MaxKvarObject obj(kvar,capbankId,CtiTime());
    _maxKvarMap.insert( make_pair(capbankId, obj ) );

    return true;
}

bool CtiCCSubstationBusStore::removeKVAROperation( long capbankId )
{
    int removed = _maxKvarMap.erase(capbankId);

    return ( removed > 0 );
}

/* Relating to Max Kvar Cparm */
long CtiCCSubstationBusStore::isKVARAvailable( long kvarNeeded )
{
    if( _MAX_KVAR < 0 )
        return true;

    long kvarInList = 0;
    for( CapBankIdToKvarMap::iterator itr = _maxKvarMap.begin(); itr != _maxKvarMap.end();  )
    {
        MaxKvarObject obj = (*itr).second;
        CtiTime now;
        ctitime_t deltaTime = now.seconds() - obj.getTimestamp().seconds();
        if( deltaTime > _MAX_KVAR_TIMEOUT)
        {
            CTILOG_INFO(dout, "Expired kvar request, removing: " << obj.getPaoId());
            _maxKvarMap.erase(itr++);
        }
        else
        {
            kvarInList += obj.getValue();
            itr++;
        }
    }
    if( (kvarInList + kvarNeeded) < _MAX_KVAR )
        return true;
    return false;
}



/*---------------------------------------------------------------------------
    setControlStatus

    Sets the control status of the capbank
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setControlStatusAndIncrementOpCount(CtiMultiMsg_vec& pointChanges, long status, CtiCCCapBank* cap,
                                                                  bool controlRecentlySentFlag)
{
    if (cap == NULL)
        return;

    if( status < 0 )
    {
        CTILOG_WARN(dout, "Control state value (" << status << ") is not valid. Not adjusting the cap bank state.");
        return;
    }

    cap->setControlStatus(status);
    cap->setControlRecentlySentFlag(controlRecentlySentFlag);

    if (_OP_STATS_DYNAMIC_UPDATE)
    {
         CtiCCFeederPtr feeder = findFeederByPAObjectID(cap->getParentId());
         if (feeder == NULL)
             return;

         CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(feeder->getParentId());
         if (subBus == NULL)
             return;

         CtiCCSubstationPtr station = findSubstationByPAObjectID(subBus->getParentId());
         if (station == NULL)
             return;

         CtiCCSpecialPtr spArea = NULL;
         CtiCCAreaPtr area = NULL;
         if (station->getSaEnabledFlag())
         {
             spArea = findSpecialAreaByPAObjectID(station->getSaEnabledId());
         }
         else
         {
             area = findAreaByPAObjectID(station->getParentId());
         }

         cap->getOperationStats().incrementAllOpCounts();


        createOperationStatPointDataMsgs(pointChanges, cap, feeder, subBus, station, area, spArea);
    }
}


/*---------------------------------------------------------------------------
    setControlStatus

    Sets the control status of the capbank
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::resetAllOperationStats()
{
    std::function<void (CapControlPao *)>  initOpStats =
        []( CapControlPao * pao )
        {
            pao->getOperationStats().init();
        };

    boost::for_each( *_ccGeoAreas, initOpStats );
    boost::for_each( *_ccSpecialAreas, initOpStats );
    boost::for_each( _ccSubstations, initOpStats );

    for ( auto currentSubstationBus : *_ccSubstationBuses )
    {
        currentSubstationBus->getOperationStats().init();

        for ( auto currentFeeder : currentSubstationBus->getCCFeeders() )
        {
            currentFeeder->getOperationStats().init();

            boost::for_each( currentFeeder->getCCCapBanks(), initOpStats );
        }
    }
}


/*---------------------------------------------------------------------------
    setControlStatus

    Sets the control status of the capbank
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::resetAllConfirmationStats()
{
    std::function<void (CapControlPao *)>  initConfStats =
        []( CapControlPao * pao )
        {
            pao->getConfirmationStats().init();
        };

    boost::for_each( *_ccGeoAreas, initConfStats );
    boost::for_each( *_ccSpecialAreas, initConfStats );
    boost::for_each( _ccSubstations, initConfStats );

    for ( auto currentSubstationBus : *_ccSubstationBuses )
    {
        currentSubstationBus->getConfirmationStats().init();

        for ( auto currentFeeder : currentSubstationBus->getCCFeeders() )
        {
            currentFeeder->getConfirmationStats().init();

            boost::for_each( currentFeeder->getCCCapBanks(), initConfStats );
        }
    }
}


void CtiCCSubstationBusStore::reCalculateAllStats( )
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );
    {
        for ( auto currentSubstationBus : *_ccSubstationBuses )
        {
            CCStatsObject subBusUserDef, subBusDaily, subBusWeekly, subBusMonthly,
                          subBusUserDefOp, subBusDailyOp, subBusWeeklyOp, subBusMonthlyOp;

            for ( auto currentFeeder : currentSubstationBus->getCCFeeders() )
            {
                CCStatsObject feederUserDef, feederDaily, feederWeekly, feederMonthly,
                              feederUserDefOp, feederDailyOp, feederWeeklyOp, feederMonthlyOp;

                for ( auto currentCapBank : currentFeeder->getCCCapBanks() )
                {
                    if(currentCapBank->getControlDeviceId() > 0 && !currentCapBank->getDisableFlag())
                    {
                        //Confirmation Stats Total
                        if (currentCapBank->getConfirmationStats().getUserDefCommCount() > 0)
                        {
                            feederUserDef.addSuccessSample( currentCapBank->getConfirmationStats().calculateSuccessPercent(capcontrol::USER_DEF_CCSTATS));
                        }
                        if (currentCapBank->getConfirmationStats().getDailyCommCount() > 0)
                        {
                            feederDaily.addSuccessSample( currentCapBank->getConfirmationStats().calculateSuccessPercent(capcontrol::DAILY_CCSTATS));
                        }
                        if (currentCapBank->getConfirmationStats().getWeeklyCommCount() > 0)
                        {
                            feederWeekly.addSuccessSample( currentCapBank->getConfirmationStats().calculateSuccessPercent(capcontrol::WEEKLY_CCSTATS));
                        }
                        if (currentCapBank->getConfirmationStats().getMonthlyCommCount() > 0)
                        {
                            feederMonthly.addSuccessSample( currentCapBank->getConfirmationStats().calculateSuccessPercent(capcontrol::MONTHLY_CCSTATS));
                        }

                        //Operations Stats Total
                        if (currentCapBank->getOperationStats().getUserDefOpCount() > 0)
                        {
                            feederUserDefOp.addSuccessSample( currentCapBank->getOperationStats().calculateSuccessPercent(capcontrol::USER_DEF_CCSTATS));
                        }
                        if (currentCapBank->getOperationStats().getDailyOpCount() > 0)
                        {
                            feederDailyOp.addSuccessSample( currentCapBank->getOperationStats().calculateSuccessPercent(capcontrol::DAILY_CCSTATS));
                        }
                        if (currentCapBank->getOperationStats().getWeeklyOpCount() > 0)
                        {
                            feederWeeklyOp.addSuccessSample( currentCapBank->getOperationStats().calculateSuccessPercent(capcontrol::WEEKLY_CCSTATS));
                        }
                        if (currentCapBank->getOperationStats().getMonthlyOpCount() > 0)
                        {
                            feederMonthlyOp.addSuccessSample( currentCapBank->getOperationStats().calculateSuccessPercent(capcontrol::MONTHLY_CCSTATS));
                        }
                    }
                }
                setConfirmationSuccessPercents( *currentFeeder, feederUserDef, feederDaily, feederWeekly, feederMonthly);
                setOperationSuccessPercents( *currentFeeder, feederUserDefOp, feederDailyOp, feederWeeklyOp, feederMonthlyOp);

                incrementConfirmationPercentTotals( *currentFeeder, subBusUserDef, subBusDaily, subBusWeekly, subBusMonthly);
                incrementOperationPercentTotals( *currentFeeder, subBusUserDefOp, subBusDailyOp, subBusWeeklyOp, subBusMonthlyOp);
            }
            setConfirmationSuccessPercents( *currentSubstationBus, subBusUserDef, subBusDaily, subBusWeekly, subBusMonthly);
            setOperationSuccessPercents( *currentSubstationBus, subBusUserDefOp, subBusDailyOp, subBusWeeklyOp, subBusMonthlyOp);
        }

        for ( auto currentStation : _ccSubstations )
        {
            CCStatsObject subUserDef, subDaily, subWeekly, subMonthly,
                          subUserDefOp, subDailyOp, subWeeklyOp, subMonthlyOp;

            for ( long busId : currentStation->getCCSubIds() )
            {
                if ( auto currentSubstationBus = findSubBusByPAObjectID( busId ) )
                {
                    incrementConfirmationPercentTotals( *currentSubstationBus, subUserDef, subDaily, subWeekly, subMonthly);
                    incrementOperationPercentTotals( *currentSubstationBus, subUserDefOp, subDailyOp, subWeeklyOp, subMonthlyOp);
                }
            }
            setConfirmationSuccessPercents( *currentStation, subUserDef, subDaily, subWeekly, subMonthly);
            setOperationSuccessPercents( *currentStation, subUserDefOp, subDailyOp, subWeeklyOp, subMonthlyOp);
        }

        for ( auto currentArea : *_ccGeoAreas )
        {
            CCStatsObject areaUserDef, areaDaily, areaWeekly, areaMonthly,
                          areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp;

            for ( long subId : currentArea->getSubstationIds() )
            {
                if ( auto currentStation = findSubstationByPAObjectID( subId ) )
                {
                   incrementConfirmationPercentTotals( *currentStation, areaUserDef, areaDaily, areaWeekly, areaMonthly);
                   incrementOperationPercentTotals( *currentStation, areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp);
                }
           }
           setConfirmationSuccessPercents( *currentArea, areaUserDef, areaDaily, areaWeekly, areaMonthly);
           setOperationSuccessPercents( *currentArea, areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp);
        }

        for ( auto currentSpArea : *_ccSpecialAreas )
        {
            CCStatsObject areaUserDef, areaDaily, areaWeekly, areaMonthly,
                          areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp;

            for ( long subId : currentSpArea->getSubstationIds() )
            {
                if ( auto currentStation = findSubstationByPAObjectID( subId ) )
                {
                    incrementConfirmationPercentTotals( *currentStation, areaUserDef, areaDaily, areaWeekly, areaMonthly);
                    incrementOperationPercentTotals( *currentStation, areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp);
                }
            }
            setConfirmationSuccessPercents( *currentSpArea, areaUserDef, areaDaily, areaWeekly, areaMonthly);
            setOperationSuccessPercents( *currentSpArea, areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp);
        }
    }
}

void CtiCCSubstationBusStore::reCalculateOperationStatsFromDatabase( )
{
    try
    {
        CtiTime currentDateTime;
        CtiTime userDefWindow = currentDateTime.seconds() - (_OP_STATS_USER_DEF_PERIOD * 60);
        CtiDate oneMonthAgo = CtiDate() -  30; //today - 30 days
        CtiDate lastWeek = CtiDate() -  7;
        CtiDate yesterday = CtiDate() -  1;
        {
            static const string sql =
                "SELECT "
                    "CEL.PointID, "
                    "CEL.DateTime "
                "FROM "
                    "CCEventLog CEL "
                "WHERE "
                    "CEL.EventType = 1 AND CEL.EventSubtype IN (0, 1, 2, 4) AND "
                    "CEL.DateTime > ?";

            // Q: Why are we ignoring 'Manual Flip Sent...' but not 'Flip Sent...' while counting 'Manual Close Sent...' etc?

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr << CtiTime { oneMonthAgo };

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
            {
                CTILOG_DEBUG(dout,
                             "userDefWindow "   << userDefWindow
                             << "yesterday "    << yesterday
                             << "lastWeek "     << lastWeek
                             << "oneMonthAgo "  << oneMonthAgo);
            }

            while ( rdr() )
            {
                CtiTime logDateTime;
                long pointId;

                rdr["pointid"] >> pointId;
                rdr["datetime"] >> logDateTime;

                CtiCCCapBankPtr cap = NULL;
                PointIdToCapBankMultiMap::iterator capBeginIter, capEndIter;
                if (findCapBankByPointID(pointId, capBeginIter, capEndIter))
                    cap = capBeginIter->second;

                if (logDateTime >= userDefWindow && cap != NULL )
                {
                    if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                    {
                        CTILOG_DEBUG(dout, "logDateTime >= userDefWindow " << CtiTime(logDateTime) <<">="<<CtiTime(userDefWindow));
                    }
                    cap->getOperationStats().incrementAllOpCounts();
                }
                else if (logDateTime >= yesterday && cap != NULL )
                {
                    if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                    {
                        CTILOG_DEBUG(dout, "logDateTime >= yesterday " << CtiTime(logDateTime) <<">="<<CtiTime(yesterday));
                    }
                    cap->getOperationStats().incrementDailyOpCounts();
                }
                else if (logDateTime >= lastWeek && cap != NULL )
                {
                    if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                    {
                        CTILOG_DEBUG(dout, "logDateTime >= lastWeek " << CtiTime(logDateTime) <<">="<<CtiTime(lastWeek));
                    }
                    cap->getOperationStats().incrementWeeklyOpCounts();
                }
                else if (logDateTime >= oneMonthAgo && cap != NULL )
                {
                    if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                    {
                        CTILOG_DEBUG(dout, "logDateTime >= oneMonthAgo " << CtiTime(logDateTime) <<">="<<CtiTime(oneMonthAgo));
                    }
                    cap->getOperationStats().incrementMonthlyOpCounts();
                }
                else if ( (_CC_DEBUG & CC_DEBUG_OPSTATS) &&
                         (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    if (cap != NULL)
                    {
                        CTILOG_INFO(dout, "irregular LOG datetime: " << CtiTime(logDateTime) << " for CAPBANK: " << cap->getPaoName());
                    }
                    else
                    {
                        CTILOG_INFO(dout, "irregular LOG datetime: " << CtiTime(logDateTime) <<" CAPBANK with Bank Status PID: "<< pointId <<" no longer exists.");
                    }
                }
            }
        }

        {
            //MONTHLY FAILS
            static const string sql =
                "SELECT "
                    "CEL.PointID, "
                    "CEL.DateTime "
                "FROM "
                    "CCEventLog CEL "
                "WHERE "
                    "CEL.Text LIKE 'Var:%Fail' AND "
                    "CEL.DateTime > ?";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr << CtiTime { oneMonthAgo };

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            while ( rdr() )
            {
                CtiTime logDateTime;
                long pointId;

                rdr["pointid"] >> pointId;
                rdr["datetime"] >> logDateTime;

                CtiCCCapBankPtr cap = NULL;
                PointIdToCapBankMultiMap::iterator capBeginIter, capEndIter;
                if (findCapBankByPointID(pointId, capBeginIter, capEndIter))
                    cap = capBeginIter->second;

                if (logDateTime >= userDefWindow && cap != NULL )
                {
                    cap->getOperationStats().incrementAllOpFails();
                }
                else if (logDateTime >= yesterday && cap != NULL )
                {
                    cap->getOperationStats().incrementDailyOpFails();
                }
                else if (logDateTime >= lastWeek && cap != NULL )
                {
                    if (cap != NULL) cap->getOperationStats().incrementWeeklyOpFails();
                }
                else if (logDateTime >= oneMonthAgo && cap != NULL )
                {
                    cap->getOperationStats().incrementMonthlyOpFails();
                }
                else if ( (_CC_DEBUG & CC_DEBUG_OPSTATS) &&
                         (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    if (cap != NULL)
                    {
                        CTILOG_INFO(dout, "irregular LOG datetime: " << CtiTime(logDateTime) << " for CAPBANK: " << cap->getPaoName());
                    }
                    else
                    {
                        CTILOG_INFO(dout, "irregular LOG datetime: " << CtiTime(logDateTime) <<" CAPBANK with Bank Status PID: "<< pointId <<" no longer exists.");
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}

void CtiCCSubstationBusStore::reCalculateConfirmationStatsFromDatabase( )
{
    try
    {
        CtiTime currentDateTime;
        CtiTime userDefWindow = currentDateTime.seconds() - ((_OP_STATS_USER_DEF_PERIOD * 60 ) + 3600);
        CtiDate oneMonthAgo = CtiDate() -  31; //today - 30 days
        CtiDate yesterday = CtiDate() -  1;

        {
            static const string sql =  "SELECT DPS.paobjectid, DPS.attempts, DPS.commerrors, DPS.protocolerrors, "
                                         "DPS.systemerrors, DPS.statistictype, DPS.startdatetime, "
                                         "YP.paobjectid, YP.type "
                                       "FROM dynamicpaostatistics DPS, yukonpaobject YP "
                                       "WHERE DPS.paobjectid = YP.paobjectid AND DPS.attempts > 0 AND "
                                         "YP.type LIKE '%CBC%' AND DPS.startdatetime >= ?";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr << CtiTime { oneMonthAgo };

            rdr.executeWithRetries();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            while ( rdr() )
            {

                long paobjectId, attempts, commErrors, protocolErrors, systemErrors, errorTotal;
                string statisticType;
                double successPercent = 100;
                CtiTime start;

                rdr["paobjectid"] >> paobjectId; //cbc Id.
                rdr["attempts"]  >> attempts;
                rdr["commerrors"] >> commErrors;
                rdr["protocolerrors"] >> protocolErrors;
                rdr["systemerrors"] >> systemErrors;
                rdr["statistictype"] >> statisticType;
                rdr["startdatetime"] >> start;


                errorTotal = commErrors + protocolErrors + systemErrors;

                CtiCCCapBankPtr cap = NULL;
                long capId = findCapBankIDbyCbcID(paobjectId);
                if (capId != NULL)
                    cap = findCapBankByPAObjectID(capId);

                if (ciStringEqual(statisticType, "Monthly")  && cap != NULL &&
                    start > oneMonthAgo)
                {
                    cap->getConfirmationStats().setMonthlyCommCount(attempts);
                    cap->getConfirmationStats().setMonthlyCommFail(errorTotal);
                    successPercent = cap->getConfirmationStats().calculateSuccessPercent(capcontrol::MONTHLY_CCSTATS);
                    cap->getConfirmationStats().setMonthlyCommSuccessPercent(successPercent);
                }
                else if (ciStringEqual(statisticType, "Weekly") && cap != NULL )
                {
                    cap->getConfirmationStats().setWeeklyCommCount(attempts);
                    cap->getConfirmationStats().setWeeklyCommFail(errorTotal);
                    successPercent = cap->getConfirmationStats().calculateSuccessPercent(capcontrol::WEEKLY_CCSTATS);
                    cap->getConfirmationStats().setWeeklyCommSuccessPercent(successPercent);
                }
                else if (ciStringEqual(statisticType, "Daily") && cap != NULL &&
                         start > yesterday)
                {
                    cap->getConfirmationStats().setDailyCommCount(attempts);
                    cap->getConfirmationStats().setDailyCommFail(errorTotal);
                    successPercent = cap->getConfirmationStats().calculateSuccessPercent(capcontrol::DAILY_CCSTATS);
                    cap->getConfirmationStats().setDailyCommSuccessPercent(successPercent);
                }
                else if (stringContainsIgnoreCase(statisticType, "Hour") && cap != NULL )
                {
                    if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                    {
                        CTILOG_DEBUG(dout, "Start Time: " << start <<"  *** UserDefined Window: " << userDefWindow);
                    }
                    if (start > userDefWindow )
                    {
                        if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                        {
                            CTILOG_DEBUG(dout, "Incrementing User Def Comm Counts for Cap: "<< cap->getPaoName());
                        }
                        cap->getConfirmationStats().incrementUserDefCommCounts(attempts);
                        cap->getConfirmationStats().incrementUserDefCommFails(errorTotal);
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}

void CtiCCSubstationBusStore::setOperationSuccessPercents( CapControlPao & object,
                                                           const CCStatsObject & userDef,
                                                           const CCStatsObject & daily,
                                                           const CCStatsObject & weekly,
                                                           const CCStatsObject & monthly )
{
    object.getOperationStats().setUserDefOpSuccessPercent( userDef.getAverage() );
    object.getOperationStats().setUserDefOpCount( userDef.getOpCount() );
    object.getOperationStats().setUserDefConfFail( userDef.getFailCount() );
    object.getOperationStats().setDailyOpSuccessPercent(  daily.getAverage() );
    object.getOperationStats().setDailyOpCount( daily.getOpCount() );
    object.getOperationStats().setDailyConfFail( daily.getFailCount() );
    object.getOperationStats().setWeeklyOpSuccessPercent( weekly.getAverage() );
    object.getOperationStats().setWeeklyOpCount( weekly.getOpCount() );
    object.getOperationStats().setWeeklyConfFail( weekly.getFailCount() );
    object.getOperationStats().setMonthlyOpSuccessPercent( monthly.getAverage() );
    object.getOperationStats().setMonthlyOpCount( monthly.getOpCount() );
    object.getOperationStats().setMonthlyConfFail( monthly.getFailCount() );
}

void CtiCCSubstationBusStore::setConfirmationSuccessPercents( CapControlPao & object,
                                                              const CCStatsObject & userDef,
                                                              const CCStatsObject & daily,
                                                              const CCStatsObject & weekly,
                                                              const CCStatsObject & monthly )
{
    object.getConfirmationStats().setUserDefCommSuccessPercent( userDef.getAverage() );
    object.getConfirmationStats().setUserDefCommCount( userDef.getOpCount() );
    object.getConfirmationStats().setUserDefCommFail( userDef.getFailCount() );
    object.getConfirmationStats().setDailyCommSuccessPercent(  daily.getAverage() );
    object.getConfirmationStats().setDailyCommCount( daily.getOpCount() );
    object.getConfirmationStats().setDailyCommFail( daily.getFailCount() );
    object.getConfirmationStats().setWeeklyCommSuccessPercent( weekly.getAverage() );
    object.getConfirmationStats().setWeeklyCommCount( weekly.getOpCount() );
    object.getConfirmationStats().setWeeklyCommFail( weekly.getFailCount() );
    object.getConfirmationStats().setMonthlyCommSuccessPercent( monthly.getAverage() );
    object.getConfirmationStats().setMonthlyCommCount( monthly.getOpCount() );
    object.getConfirmationStats().setMonthlyCommFail( monthly.getFailCount() );
}

void CtiCCSubstationBusStore::incrementConfirmationPercentTotals( CapControlPao & object,
                                                                  CCStatsObject & userDef,
                                                                  CCStatsObject & daily,
                                                                  CCStatsObject & weekly,
                                                                  CCStatsObject & monthly )
{
    if ( object.getConfirmationStats().getUserDefCommCount() > 0 )
    {
        userDef.addSuccessSample( object.getConfirmationStats().getUserDefCommSuccessPercent() );
    }
    if ( object.getConfirmationStats().getDailyCommCount() > 0 )
    {
        daily.addSuccessSample( object.getConfirmationStats().getDailyCommSuccessPercent() );
    }
    if ( object.getConfirmationStats().getWeeklyCommCount() > 0 )
    {
        weekly.addSuccessSample( object.getConfirmationStats().getWeeklyCommSuccessPercent() );
    }
    if ( object.getConfirmationStats().getMonthlyCommCount() > 0 )
    {
        monthly.addSuccessSample( object.getConfirmationStats().getMonthlyCommSuccessPercent() );
    }
}

void CtiCCSubstationBusStore::incrementOperationPercentTotals( CapControlPao & object,
                                                               CCStatsObject & userDef,
                                                               CCStatsObject & daily,
                                                               CCStatsObject & weekly,
                                                               CCStatsObject & monthly )
{
    if ( object.getOperationStats().getUserDefOpCount() > 0 )
    {
        userDef.addSuccessSample( object.getOperationStats().getUserDefOpSuccessPercent() );
    }
    if ( object.getOperationStats().getDailyOpCount() > 0 )
    {
        daily.addSuccessSample( object.getOperationStats().getDailyOpSuccessPercent() );
    }
    if ( object.getOperationStats().getWeeklyOpCount() > 0 )
    {
        weekly.addSuccessSample( object.getOperationStats().getWeeklyOpSuccessPercent() );
    }
    if ( object.getOperationStats().getMonthlyOpCount() > 0 )
    {
        monthly.addSuccessSample( object.getOperationStats().getMonthlyOpSuccessPercent() );
    }
}

/*---------------------------------------------------------------------------
    setControlStatus

    Sets the control status of the capbank
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setControlStatusAndIncrementFailCount(CtiMultiMsg_vec& pointChanges, long status, CtiCCCapBank* cap)
{
    if (cap == NULL)
        return;

    if( status < 0 )
    {
        CTILOG_WARN(dout, "Control state value (" << status << ") is not valid. Not adjusting the cap bank state.");
        return;
    }

    cap->setControlStatus(status);
    cap->setControlRecentlySentFlag(false);

    if (_OP_STATS_DYNAMIC_UPDATE)
    {
        CtiCCFeederPtr feeder = findFeederByPAObjectID(cap->getParentId());
        if (feeder == NULL)
            return;

        CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(feeder->getParentId());
        if (subBus == NULL)
            return;

        CtiCCSubstationPtr station = findSubstationByPAObjectID(subBus->getParentId());
        if (station == NULL)
            return;

        CtiCCSpecialPtr spArea = NULL;
        CtiCCAreaPtr area = NULL;
        if (station->getSaEnabledFlag())
        {
            spArea = findSpecialAreaByPAObjectID(station->getSaEnabledId());
        }
        else
        {
            area = findAreaByPAObjectID(station->getParentId());
        }

        cap->getOperationStats().incrementAllOpFails();
        createOperationStatPointDataMsgs(pointChanges, cap, feeder, subBus, station, area, spArea);
    }
}

void CtiCCSubstationBusStore::createAllStatsPointDataMsgs(CtiMultiMsg_vec& pointChanges)
{
    long i=0;
    try
    {
        for ( CtiCCAreaPtr currentArea : *_ccGeoAreas )
        {
            currentArea->getOperationStats().createPointDataMsgs(pointChanges);
            currentArea->getConfirmationStats().createPointDataMsgs(pointChanges);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    try
    {
        for ( CtiCCSpecialPtr currentSpArea : *_ccSpecialAreas )
        {
            currentSpArea->getOperationStats().createPointDataMsgs(pointChanges);
            currentSpArea->getConfirmationStats().createPointDataMsgs(pointChanges);

        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    try
    {
        for ( CtiCCSubstationPtr currentStation : _ccSubstations )
        {
            currentStation->getOperationStats().createPointDataMsgs(pointChanges);
            currentStation->getConfirmationStats().createPointDataMsgs(pointChanges);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    try
    {
        for(i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)_ccSubstationBuses->at(i);

            try
            {
                currentSubstationBus->getOperationStats().createPointDataMsgs(pointChanges);
                currentSubstationBus->getConfirmationStats().createPointDataMsgs(pointChanges);
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            CtiFeeder_vec &ccFeeders = currentSubstationBus->getCCFeeders();

            for(long j=0; j < ccFeeders.size(); j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders.at(j));
                try
                {
                    currentFeeder->getOperationStats().createPointDataMsgs(pointChanges);
                    currentFeeder->getConfirmationStats().createPointDataMsgs(pointChanges);

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(long k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[k]);
                        try
                        {
                            currentCapBank->getOperationStats().createPointDataMsgs(pointChanges);
                            currentCapBank->getConfirmationStats().createPointDataMsgs(pointChanges);
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        }
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


void CtiCCSubstationBusStore::createOperationStatPointDataMsgs(CtiMultiMsg_vec& pointChanges, CtiCCCapBank* cap, CtiCCFeeder* feed, CtiCCSubstationBus* bus,
                                                       CtiCCSubstationPtr station, CtiCCAreaPtr area, CtiCCSpecialPtr spArea)
{
    if (cap != NULL)
        cap->getOperationStats().createPointDataMsgs(pointChanges);

    if (feed != NULL)
        feed->getOperationStats().createPointDataMsgs(pointChanges);

    if (bus != NULL)
        bus->getOperationStats().createPointDataMsgs(pointChanges);

    if (station != NULL)
        station->getOperationStats().createPointDataMsgs(pointChanges);

    if (area != NULL)
        area->getOperationStats().createPointDataMsgs(pointChanges);

    if (spArea != NULL)
        spArea->getOperationStats().createPointDataMsgs(pointChanges);
}

void CtiCCSubstationBusStore::cascadeAreaStrategySettings(CtiCCAreaBasePtr object)
{
    if (object == NULL || object->getStrategy()->getUnitType() == ControlStrategy::None)
        return;

    long strategyID = object->getStrategy()->getStrategyId();

    for each (long stationId in object->getSubstationIds())
    {
        CtiCCSubstationPtr station =findSubstationByPAObjectID(stationId);
        if (station == NULL)
            continue;
        for each ( long busId in station->getCCSubIds() )
        {
            CtiCCSubstationBus* currentCCSubstationBus = findSubBusByPAObjectID(busId);
            if (currentCCSubstationBus == NULL)
                continue;
            if ((object->isSpecial() && station->getSaEnabledFlag() && station->getSaEnabledId() == object->getPaoId() ) ||
                (!object->isSpecial() && !station->getSaEnabledFlag() && currentCCSubstationBus->getStrategy()->getUnitType() == ControlStrategy::None))
            {
                currentCCSubstationBus->setStrategy(strategyID);

                if ( currentCCSubstationBus->getStrategy()->getMethodType() == ControlStrategy::TimeOfDayMethod )
                {
                    currentCCSubstationBus->figureNextCheckTime();
                }
                for (int i = 0; i < currentCCSubstationBus->getCCFeeders().size(); i++ )
                {
                    CtiCCFeederPtr fdr = currentCCSubstationBus->getCCFeeders()[i];
                    if ( fdr->getStrategy()->getUnitType() == ControlStrategy::None )
                        fdr->setStrategy( strategyID );
                }
            }
        }
    }
}

void CtiCCSubstationBusStore::getSubBusParentInfo(CtiCCSubstationBus* bus, long &spAreaId, long &areaId, long &stationId)
{
    stationId = 0;
    areaId = 0;
    spAreaId = 0;
    if (bus != NULL)
    {
        stationId = bus->getParentId();
        CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
        if (station != NULL)
        {
            if (station->getSaEnabledFlag())
            {
                spAreaId = station->getSaEnabledId();
            }
            else
            {
                areaId = station->getParentId();
            }
        }
    }
}


void CtiCCSubstationBusStore::getFeederParentInfo(CtiCCFeeder* feeder, long &spAreaId, long &areaId, long &stationId)
{
    stationId = 0;
    areaId = 0;
    spAreaId = 0;
    if (feeder != NULL)
    {
        CtiCCSubstationBusPtr bus = findSubBusByPAObjectID(feeder->getParentId());
        if (bus != NULL)
        {
            stationId = bus->getParentId();
            CtiCCSubstationPtr station = findSubstationByPAObjectID(stationId);
            if (station != NULL)
            {
                if (station->getSaEnabledFlag())
                {
                    spAreaId = station->getSaEnabledId();
                }
                else
                {
                    areaId = station->getParentId();
                }
            }
        }
    }
}



bool CtiCCSubstationBusStore::isAnyBankClosed(int paoId, CapControlType type)
{
    CapBankList banks = getCapBanksByPaoIdAndType(paoId,type);

    for each (CtiCCCapBankPtr bank in banks)
    {
        if (bank->getControlStatus() == CtiCCCapBank::Close ||
            bank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
        {
            return true;
        }
    }

    return false;
}

bool CtiCCSubstationBusStore::isAnyBankOpen(int paoId, CapControlType type)
{
    CapBankList banks = getCapBanksByPaoIdAndType(paoId,type);

    for each (CtiCCCapBankPtr bank in banks)
    {
        if (bank->getControlStatus() == CtiCCCapBank::Open ||
            bank->getControlStatus() == CtiCCCapBank::OpenQuestionable )
        {
            return true;
        }
    }

    return false;
}


void CtiCCSubstationBusStore::executeAllStrategies() const
{
    _strategyManager->executeAll();
}


Cti::CapControl::ZoneManager & CtiCCSubstationBusStore::getZoneManager()
{
    return _zoneManager;
}


bool CtiCCSubstationBusStore::reloadZoneFromDatabase(const long zoneId)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    try
    {
        if (zoneId == -1)
        {
            _zoneManager.reloadAll();
        }
        else
        {
            _zoneManager.reload(zoneId);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return true;
}


Cti::CapControl::VoltageRegulatorManager *CtiCCSubstationBusStore::getVoltageRegulatorManager()
{
    return _voltageRegulatorManager.get();
}


bool CtiCCSubstationBusStore::reloadVoltageRegulatorFromDatabase(const long regulatorId)
{
    CTILOCKGUARD( CtiCriticalSection, guard, getMux() );

    try
    {
        if (regulatorId == -1)
        {
            _voltageRegulatorManager->reloadAll();
        }
        else
        {
            _voltageRegulatorManager->reload(regulatorId);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return true;
}


void CtiCCSubstationBusStore::handleZoneDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                 PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{
    if (reloadAction != ChangeTypeDelete)
    {
        reloadZoneFromDatabase(reloadId);
    }
    else
    {
        _zoneManager.unload(reloadId);
    }
}


void CtiCCSubstationBusStore::handleVoltageRegulatorDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                 PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{
    if (reloadAction != ChangeTypeDelete)
    {
        reloadVoltageRegulatorFromDatabase(reloadId);
    }
    else
    {
        _voltageRegulatorManager->unload(reloadId);
    }
}


void CtiCCSubstationBusStore::handleMonitorPointDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                 PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{
    if ( reloadAction == ChangeTypeUpdate )
    {
        static const std::string sql =
            "SELECT "
                "MB.DeviceId, "
                "MB.PointId, "
                "MB.DisplayOrder, "
                "MB.Scannable, "
                "MB.NINAvg, "
                "MB.UpperBandwidth, "
                "MB.LowerBandwidth, "
                "MB.Phase, "
                "MB.OverrideStrategy, "
                "YP.PAOName, "
                "P.POINTNAME, "
                "MBH.Value, "
                "MBH.DateTime, "
                "MBH.ScanInProgress "
            "FROM "
                "CCMonitorBankList MB "
                    "JOIN YukonPAObject YP "
                        "ON YP.PAObjectID = MB.DeviceId "
                    "JOIN POINT P "
                        "ON MB.PointId = P.POINTID "
                    "LEFT OUTER JOIN DynamicCCMonitorBankHistory MBH "
                        "ON MB.DeviceId = MBH.DeviceId AND MB.PointID = MBH.PointID "
            "WHERE "
                "MB.PointId = ?";

        Cti::Database::DatabaseConnection   connection;
        Cti::Database::DatabaseReader       rdr(connection, sql);

        rdr << reloadId;

        rdr.execute();

        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
        {
            CTILOG_INFO(dout, rdr.asString());
        }

        while ( rdr() )
        {
            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_DEBUG(dout, "Updating Monitor Point ID: " << reloadId);
            }

            CtiCCMonitorPointPtr    updatedMonitorPoint = CtiCCMonitorPointPtr( new CtiCCMonitorPoint(rdr) );

            // always update the monitor point in the subbus collection

            PointIdToSubBusMultiMap::iterator   busIter, busEnd;

            findSubBusByPointID( reloadId, busIter, busEnd );

            for ( ; busIter != busEnd; ++busIter )
            {
                CtiCCSubstationBusPtr   currentBus = busIter->second;

                currentBus->updateExistingMonitorPoint( updatedMonitorPoint );
            }

            // update the monitor point in the capbank collection if attached to a bank

            PointIdToCapBankMultiMap::iterator  bankIter, bankEnd;

            findCapBankByPointID( reloadId, bankIter, bankEnd );

            for ( ; bankIter != bankEnd; ++bankIter )
            {
                CtiCCCapBankPtr currentBank = bankIter->second;

                currentBank->updateExistingMonitorPoint( updatedMonitorPoint );
            }
        }
    }
}


/* Private Static members */
const string CtiCCSubstationBusStore::m3iAMFMInterfaceString = "M3I";

const string CtiCCSubstationBusStore::m3iAMFMChangeTypeCircuitOutOfService = "Circuit or Section Out of Service";
const string CtiCCSubstationBusStore::m3iAMFMChangeTypeCircuitReturnToService = "Circuit or Section Returned to Service";
const string CtiCCSubstationBusStore::m3iAMFMChangeTypeCapOutOfService = "Cap Control Out of Service";
const string CtiCCSubstationBusStore::m3iAMFMChangeTypeCapReturnedToService = "Cap Control Returned to Service";
const string CtiCCSubstationBusStore::m3iAMFMChangeTypeCircuitReconfigured = "Circuit Reconfigured";
const string CtiCCSubstationBusStore::m3iAMFMChangeTypeCircuitReconfiguredToNormal = "Circuit Reconfigured to Normal";

const string CtiCCSubstationBusStore::m3iAMFMEnabledString = "ENABLED";
const string CtiCCSubstationBusStore::m3iAMFMDisabledString = "DISABLED";
const string CtiCCSubstationBusStore::m3iAMFMFixedString = "FIXED";
const string CtiCCSubstationBusStore::m3iAMFMSwitchedString = "SWITCHED";

const string CtiCCSubstationBusStore::m3iAMFMNullString = "(NULL)";

const string CtiCCSubstationBusStore::CAP_CONTROL_DBCHANGE_MSG_SOURCE = "CAP_CONTROL_SERVER";
const string CtiCCSubstationBusStore::CAP_CONTROL_RELOAD_DBCHANGE_MSG_SOURCE = "CAP_CONTROL_SERVER_FORCED_RELOAD";

