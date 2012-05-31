#include "precompiled.h"

#include <map>
#include <iostream>
#include <sstream>

#include <rw/rwfile.h>
#include <rw/thr/thrfunc.h>

#include "ccsubstationbusstore.h"
#include "ControlStrategy.h"
#include "ccsubstationbus.h"
#include "ccsubstation.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "ccstate.h"
#include "desolvers.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "dbaccess.h"
#include "ctibase.h"
#include "logger.h"
#include "msg_dbchg.h"
#include "msg_signal.h"
#include "capcontroller.h"
#include "mgr_holiday.h"
#include "utility.h"
#include "thread_monitor.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"
#include "ctistring.h"
#include "PointResponse.h"
#include "PointResponseDao.h"
#include "ThreadStatusKeeper.h"
#include "ExecutorFactory.h"

#include <string>

#define HOURLY_RATE 3600

extern unsigned long _CC_DEBUG;
extern unsigned long _DB_RELOAD_WAIT;
extern unsigned long _MAX_KVAR;
extern unsigned long _MAX_KVAR_TIMEOUT;
extern unsigned long _OP_STATS_USER_DEF_PERIOD;
extern unsigned long _OP_STATS_REFRESH_RATE;
extern string _MAXOPS_ALARM_CAT;
extern long _MAXOPS_ALARM_CATID;
extern bool _OP_STATS_DYNAMIC_UPDATE;
extern double _IVVC_DEFAULT_DELTA;

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
CtiCCSubstationBusStore::CtiCCSubstationBusStore() : _isvalid(false),
                                                    _reregisterforpoints(true),
                                                    _reloadfromamfmsystemflag(false),
                                                    _lastdbreloadtime(CtiTime(CtiDate(1,1,1990),0,0,0)),
                                                    _wassubbusdeletedflag(false),
                                                    _lastindividualdbreloadtime(CtiTime(CtiDate(1,1,1990),0,0,0)),
                                                    _strategyManager( std::auto_ptr<StrategyDBLoader>( new StrategyDBLoader ) ),
                                                    _zoneManager( std::auto_ptr<ZoneDBLoader>( new ZoneDBLoader ) ),
_voltageRegulatorManager( new Cti::CapControl::VoltageRegulatorManager(std::auto_ptr<VoltageRegulatorDBLoader>( new VoltageRegulatorDBLoader ) ) )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    _ccSubstationBuses = new CtiCCSubstationBus_vec;
    _ccSubstations = new CtiCCSubstation_vec;
    _ccCapBankStates = new CtiCCState_vec;
    _ccGeoAreas = new CtiCCArea_vec;
    _ccSpecialAreas = new CtiCCSpArea_vec;

    _paobject_specialarea_map.clear();
    _paobject_area_map.clear();
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
    _feeder_subbus_map.clear();
    _capbank_subbus_map.clear();
    _capbank_feeder_map.clear();
    _cbc_capbank_map.clear();

    _reloadList.clear();
    _orphanedCapBanks.clear();
    _orphanedFeeders.clear();
    _unsolicitedCapBanks.clear();
    _rejectedCapBanks.clear();

    _linkStatusPointId = 0;
    _linkStatusFlag = OPENED;
    _linkDropOutTime = CtiTime();

    _voltReductionSystemDisabled = false;
    _voltDisabledCount = 0;

    _pointDataHandler.setPointDataListener(this);
    _daoFactory = boost::shared_ptr<DaoFactory>(new DatabaseDaoFactory());

    _voltageRegulatorManager->setAttributeService( & _attributeService );
    _voltageRegulatorManager->setPointDataHandler( & _pointDataHandler );

}

/*--------------------------------------------------------------------------
    Destrutor
-----------------------------------------------------------------------------*/
CtiCCSubstationBusStore::~CtiCCSubstationBusStore()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    stopThreads();
    shutdown();
}



void CtiCCSubstationBusStore::startThreads()
{
    //Start the reset thread
    RWThreadFunction func = rwMakeThreadFunction( *this, &CtiCCSubstationBusStore::doResetThr );
    _resetthr = func;
    func.start();
    //Start the amfm thread
    RWThreadFunction func2 = rwMakeThreadFunction( *this, &CtiCCSubstationBusStore::doAMFMThr );
    _amfmthr = func2;
    func2.start();
    //Start the opstats thread
    RWThreadFunction func3 = rwMakeThreadFunction( *this, &CtiCCSubstationBusStore::doOpStatsThr );
    _opstatsthr = func3;
    func3.start();
}

void CtiCCSubstationBusStore::stopThreads()
{
    try
    {
        if (_resetthr.isValid() && _resetthr.requestCancellation() == RW_THR_ABORTED)
        {
            _resetthr.terminate();

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Store Reset Thread forced to terminate." << endl;
            }
        }
        else
        {
            _resetthr.requestCancellation();
            _resetthr.join();
        }

        if (_amfmthr.isValid() && _amfmthr.requestCancellation() == RW_THR_ABORTED)
        {
            _amfmthr.terminate();

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - AMFM Thread forced to terminate." << endl;
            }
        }
        else
        {
            _amfmthr.requestCancellation();
            _amfmthr.join();
        }

        if (_opstatsthr.isValid() && _opstatsthr.requestCancellation() == RW_THR_ABORTED)
        {
            _opstatsthr.terminate();

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Store Op Stat Thread forced to terminate." << endl;
            }
        }
        else
        {
            _opstatsthr.requestCancellation();
            _opstatsthr.join();
        }
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    getSubstationBuses

    Returns a CtiCCSubstationBus_vec of CtiCCSubstationBuses
---------------------------------------------------------------------------*/
CtiCCSubstationBus_vec* CtiCCSubstationBusStore::getCCSubstationBuses(unsigned long secondsFrom1901, bool checkReload)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if (!checkReload)
    {
        return _ccSubstationBuses;
    }

    if( (!_isvalid) && (secondsFrom1901 >= _lastdbreloadtime.seconds()+30) )
    {//is not valid and has been at 0.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
        reset();
        clearDBReloadList();
    }
    else if (secondsFrom1901 >= _lastindividualdbreloadtime.seconds() + _DB_RELOAD_WAIT)
    {
        checkDBReloadList();
        if (!_isvalid)
        {
            reset();
            clearDBReloadList();
        }
    }
    else if (_2wayFlagUpdate)
    {
        dumpAllDynamicData();
        _2wayFlagUpdate = false;
    }

    if( _reloadfromamfmsystemflag )
    {
        checkAMFMSystemForUpdates();
    }

    return _ccSubstationBuses;
}

/*---------------------------------------------------------------------------
    getCCGeoAreas

    Returns a CtiCCGeoArea_vec of CtiCCGeoAreas
---------------------------------------------------------------------------*/
CtiCCArea_vec* CtiCCSubstationBusStore::getCCGeoAreas(unsigned long secondsFrom1901, bool checkReload)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if (!checkReload)
    {
        return _ccGeoAreas;
    }

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+30 )
    {//is not valid and has been at 0.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
        reset();
    }

    return _ccGeoAreas;
}

/*---------------------------------------------------------------------------
    getCCSpecialAreas

    Returns a CtiCCSpecialArea_vec of CtiCCAreas
---------------------------------------------------------------------------*/
CtiCCSpArea_vec* CtiCCSubstationBusStore::getCCSpecialAreas(unsigned long secondsFrom1901, bool checkReload)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if (!checkReload)
    {
        return _ccSpecialAreas;
    }

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+30 )
    {//is not valid and has been at 0.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
        reset();
    }

    return _ccSpecialAreas;
}
/*---------------------------------------------------------------------------
    getCCSubstations

    Returns a CtiCCSubstation_vec of CtiCCSubstations
---------------------------------------------------------------------------*/
CtiCCSubstation_vec* CtiCCSubstationBusStore::getCCSubstations(unsigned long secondsFrom1901, bool checkReload)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if (!checkReload)
    {
        return _ccSubstations;
    }

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+30 )
    {//is not valid and has been at 0.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
        reset();
    }

    return _ccSubstations;
}


/*---------------------------------------------------------------------------
    getCCCapBankStates

    Returns a CtiCCState_vec* of CtiCCStates
---------------------------------------------------------------------------*/
CtiCCState_vec* CtiCCSubstationBusStore::getCCCapBankStates(unsigned long secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+90 )
    {//is not valid and has been at 1.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
        reset();
    }

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

int CtiCCSubstationBusStore::getNbrOfAreasWithPointID(long point_id)
{
    return _pointid_area_map.count(point_id);
}
int CtiCCSubstationBusStore::getNbrOfSpecialAreasWithPointID(long point_id)
{
    return _pointid_specialarea_map.count(point_id);
}
int CtiCCSubstationBusStore::getNbrOfSubstationsWithPointID(long point_id)
{
    return _pointid_station_map.count(point_id);
}
int CtiCCSubstationBusStore::getNbrOfSubBusesWithPointID(long point_id)
{
    return _pointid_subbus_map.count(point_id);
}

int CtiCCSubstationBusStore::getNbrOfSubsWithAltSubID(long altSubId)
{
    return _altsub_sub_idmap.count(altSubId);
}
pair<PaoIdToPointIdMultiMap::iterator,PaoIdToPointIdMultiMap::iterator> CtiCCSubstationBusStore::getSubsWithAltSubID(int altSubId)
{
    return _altsub_sub_idmap.equal_range(altSubId);
}

int CtiCCSubstationBusStore::getNbrOfFeedersWithPointID(long point_id)
{
    return _pointid_feeder_map.count(point_id);
}
int CtiCCSubstationBusStore::getNbrOfCapBanksWithPointID(long point_id)
{
    return _pointid_capbank_map.count(point_id);
}
CtiCCAreaPtr CtiCCSubstationBusStore::findAreaByPAObjectID(long paobject_id)
{
    PaoIdToAreaMap::iterator iter = _paobject_area_map.find(paobject_id);
    return (iter == _paobject_area_map.end() ? NULL : iter->second);
}
CtiCCSpecialPtr CtiCCSubstationBusStore::findSpecialAreaByPAObjectID(long paobject_id)
{
    PaoIdToSpecialAreaMap::iterator iter = _paobject_specialarea_map.find(paobject_id);
    return (iter == _paobject_specialarea_map.end() ? NULL : iter->second);
}
CtiCCSubstationPtr CtiCCSubstationBusStore::findSubstationByPAObjectID(long paobject_id)
{
    PaoIdToSubstationMap::iterator iter = _paobject_substation_map.find(paobject_id);
    return (iter == _paobject_substation_map.end() ? NULL : iter->second);
}

CtiCCSubstationBusPtr CtiCCSubstationBusStore::findSubBusByPAObjectID(long paobject_id)
{
    PaoIdToSubBusMap::iterator iter = _paobject_subbus_map.find(paobject_id);
    return (iter == _paobject_subbus_map.end() ? NULL : iter->second);
}

CtiCCSubstationBusPtr CtiCCSubstationBusStore::findSubBusByCapBankID(long cap_bank_id)
{
    long subBusId = findSubBusIDbyCapBankID(cap_bank_id);
    if (subBusId == NULL)
        return NULL;
    return findSubBusByPAObjectID(subBusId);
}

CtiCCFeederPtr CtiCCSubstationBusStore::findFeederByPAObjectID(long paobject_id)
{
    PaoIdToFeederMap::iterator iter = _paobject_feeder_map.find(paobject_id);
    return (iter == _paobject_feeder_map.end() ? NULL : iter->second);
}

CtiCCCapBankPtr CtiCCSubstationBusStore::findCapBankByPAObjectID(long paobject_id)
{
    PaoIdToCapBankMap::iterator iter = _paobject_capbank_map.find(paobject_id);
    return (iter == _paobject_capbank_map.end() ? NULL : iter->second);
}

long CtiCCSubstationBusStore::findAreaIDbySubstationID(long substationId)
{
    ChildToParentMap::iterator iter = _substation_area_map.find(substationId);
    return (iter == _substation_area_map.end() ? NULL : iter->second);
}
bool CtiCCSubstationBusStore::findSpecialAreaIDbySubstationID(long substationId, ChildToParentMultiMap::iterator &begin, ChildToParentMultiMap::iterator &end)
{
    begin = _substation_specialarea_map.lower_bound(substationId);
    end   = _substation_specialarea_map.upper_bound(substationId);
    return begin != end;
}

long CtiCCSubstationBusStore::findSubstationIDbySubBusID(long subBusId)
{
    ChildToParentMap::iterator iter = _subbus_substation_map.find(subBusId);
    return (iter == _subbus_substation_map.end() ? NULL : iter->second);
}

long CtiCCSubstationBusStore::findSubBusIDbyFeederID(long feederId)
{
    ChildToParentMap::iterator iter = _feeder_subbus_map.find(feederId);
    return (iter == _feeder_subbus_map.end() ? NULL : iter->second);
}
long CtiCCSubstationBusStore::findSubBusIDbyCapBankID(long capBankId)
{
    ChildToParentMap::iterator iter = _capbank_subbus_map.find(capBankId);
    return (iter == _capbank_subbus_map.end() ? NULL : iter->second);
}
long CtiCCSubstationBusStore::findFeederIDbyCapBankID(long capBankId)
{
    ChildToParentMap::iterator iter = _capbank_feeder_map.find(capBankId);
    return (iter == _capbank_feeder_map.end() ? NULL : iter->second);
}
long CtiCCSubstationBusStore::findCapBankIDbyCbcID(long cbcId)
{
    ChildToParentMap::iterator iter = _cbc_capbank_map.find(cbcId);
    return (iter == _cbc_capbank_map.end() ? NULL : iter->second);
}

long CtiCCSubstationBusStore::findSubIDbyAltSubID(long altSubId, int index)
{
    PaoIdToPointIdMultiMap::iterator iter = _altsub_sub_idmap.find(altSubId);
    if (iter == _altsub_sub_idmap.end())
    {
        return NULL;
    }
    while (index > 1)
    {
        iter++;
        index--;
    }
    return (iter == _altsub_sub_idmap.end() ? NULL : iter->second);

}

void CtiCCSubstationBusStore::addAreaToPaoMap(CtiCCAreaPtr area)
{
    _paobject_area_map.insert(make_pair(area->getPaoId(),area));
}
void CtiCCSubstationBusStore::addSubstationToPaoMap(CtiCCSubstationPtr station)
{
    _paobject_substation_map.insert(make_pair(station->getPaoId(),station));
}
void CtiCCSubstationBusStore::addSubBusToPaoMap(CtiCCSubstationBusPtr bus)
{
    _paobject_subbus_map.insert(make_pair(bus->getPaoId(),bus));
}
void CtiCCSubstationBusStore::addSubBusToAltBusMap(CtiCCSubstationBusPtr bus)
{
    _altsub_sub_idmap.insert(make_pair(bus->getAltDualSubId(),bus->getPaoId()));
}
void CtiCCSubstationBusStore::addFeederToPaoMap(CtiCCFeederPtr feeder)
{
    _paobject_feeder_map.insert(make_pair(feeder->getPaoId(),feeder));
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

CtiCCCapBankPtr CtiCCSubstationBusStore::getCapBankByPaoId(int paoId)
{
    CtiCCCapBankPtr bank = findCapBankByPAObjectID(paoId);
    return bank;
}

CapBankList CtiCCSubstationBusStore::getCapBanksByPaoId(int paoId)
{
    CapControlType type = determineTypeById(paoId);
    return getCapBanksByPaoIdAndType(paoId,type);
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
    RWCollectable* ptr = NULL;
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

bool CtiCCSubstationBusStore::handlePointDataByPaoId(int paoId, CtiPointDataMsg* message)
{
    // Currently only Voltage Regulator types do this.
    // The idea will be all types do it, this can be accomplished once all objects inherit off CapControlPao

    bool handled = false;

    if (message->isA() == MSG_POINTDATA)
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

    Writes out the dynamic information for each of the substation buses.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::dumpAllDynamicData()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Store START dumpAllDynamicData" << endl;
    }*/
    try
    {

        if( _ccSubstationBuses->size() > 0 )
        {
            CtiTime currentDateTime = CtiTime();

            Cti::Database::DatabaseConnection conn;

            if ( ! conn.isValid() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                return;
            }

            conn.beginTransaction();
            {

                if(_ccGeoAreas->size() > 0 )
                {
                    for(long i=0;i<_ccGeoAreas->size();i++)
                    {
                        CtiCCArea* currentCCArea = (CtiCCArea*)(*_ccGeoAreas)[i];
                        currentCCArea->dumpDynamicData(conn,currentDateTime);
                        currentCCArea->getOperationStats().dumpDynamicData(conn,currentDateTime);
                    }
                }
                if(_ccSpecialAreas->size() > 0 )
                {
                    for(long i=0;i<_ccSpecialAreas->size();i++)
                    {
                        CtiCCSpecial* currentSpecial = (CtiCCSpecial*)(*_ccSpecialAreas)[i];
                        currentSpecial->dumpDynamicData(conn,currentDateTime);
                        currentSpecial->getOperationStats().dumpDynamicData(conn,currentDateTime);
                    }
                }
                if( _ccSubstations->size() > 0 )
                {
                    for(long i=0;i<_ccSubstations->size();i++)
                    {
                        CtiCCSubstation* currentCCSubstation = (CtiCCSubstation*)(*_ccSubstations)[i];
                        currentCCSubstation->dumpDynamicData(conn,currentDateTime);
                        currentCCSubstation->getOperationStats().dumpDynamicData(conn,currentDateTime);
                    }
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
            conn.commitTransaction();
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Store STOP dumpAllDynamicData" << endl;
    }*/
}



bool CtiCCSubstationBusStore::deleteCapControlMaps()
{
    bool wasAlreadyRunning = false;
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    try
    {
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
        delete_container( *_ccSubstations );
        _ccSubstations->clear();
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
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Exception clearing old data for reload. Location: " << __FILE__ << " at:" << __LINE__ << endl;
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
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    bool wasAlreadyRunning = false;
    try
    {
        long currentAllocations = ResetBreakAlloc();
        if ( _CC_DEBUG & CC_DEBUG_EXTENDED )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Current Number of Historical Memory Allocations: " << currentAllocations << endl;
        }

        {
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Obtained connection to the database..." << endl;
                dout << CtiTime() << " - Resetting substation buses from database..." << endl;
            }

            wasAlreadyRunning = deleteCapControlMaps();

            CtiTime currentDateTime;
            /*************************************************************
            ******  Loading Strategies                              ******
            **************************************************************/
            if ( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - DataBase Reload Begin - " << endl;
            }

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
                                         &_substation_area_map, &_substation_specialarea_map, _ccSubstations);


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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - No Substations in: " << __FILE__ << " at: " << __LINE__ << endl;
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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            if ( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - DataBase Reload End - " << endl;
                dout << CtiTime() << " - Done Loading values into capcontrol - " << endl;
            }

            setStoreRecentlyReset(true);
            _isvalid = true;
            _2wayFlagUpdate = false;
        }


    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    try
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Store reset." << endl;
        }

        CtiMultiMsg_vec capMessages;
        _reregisterforpoints = true;
        _lastdbreloadtime = _lastdbreloadtime.now();
        if ( !wasAlreadyRunning )
        {
            dumpAllDynamicData();
        }
        CtiTime currentDateTime = CtiTime();

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

        long i=0;
        for(i = 0; i< _ccSpecialAreas->size();i++)
        {
             CtiCCSpecialPtr spArea =  (CtiCCSpecialPtr)(*_ccSpecialAreas).at(i);
             cascadeAreaStrategySettings(spArea);
        }

        for(i=0;i< _ccGeoAreas->size();i++)
        {
            CtiCCAreaPtr area =  (CtiCCAreaPtr)(*_ccGeoAreas).at(i);
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
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }


        initializeAllPeakTimeFlagsAndMonitorPoints(true);

        CtiCCExecutorFactory::createExecutor(new CapControlCommand(CapControlCommand::REQUEST_ALL_DATA))->execute();
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - reset complete - " << endl;
    }
}

/*---------------------------------------------------------------------------
    checkAMFMSystemForUpdates

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::checkAMFMSystemForUpdates()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    //if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Checking AMFM system for updates..." << endl;
    }

    {
        Cti::Database::DatabaseConnection connection;
        CtiTime lastAMFMUpdateTime = gInvalidCtiTime;

        RWFile amfmFile((gConfigParms.getYukonBase() + "\\server\\config\\amfm.dat").c_str());

        if ( amfmFile.Exists() )
        {
            if ( !amfmFile.IsEmpty() )
            {
                lastAMFMUpdateTime = timeSaver;
                CtiLockGuard<CtiLogger> logger_guard(dout);
                //dout << CtiTime() << " - After restoreFrom lastAMFMUpdateTime = " << lastAMFMUpdateTime.asString() << endl;
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Creating amfm.dat" << endl;
                //dout << CtiTime() << " - Initial saveOn lastAMFMUpdateTime = " << lastAMFMUpdateTime.asString() << endl;
                amfmFile.Erase();
                timeSaver = lastAMFMUpdateTime;
                amfmFile.Flush();
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Can not create amfm.dat to store date!!!!!!!!!" << endl;
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
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << loggedSQLstring << endl;
            }
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
            if( amfmFile.Exists() )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                //dout << CtiTime() << " - Periodic saveOn lastAMFMUpdateTime = " << lastAMFMUpdateTime.asString() << endl;
                amfmFile.Erase();
                timeSaver = lastAMFMUpdateTime;
                amfmFile.Flush();
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                //dout << CtiTime() << " - Can not create amfm.dat to store date!!!!!!!!!" << endl;
            }

            //sending a signal message to dispatch so that changes from the amfm are in the system log
            string text("Import from AMFM system caused database changes");
            string additional = string();
            CtiCapController::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control") );

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - AMFM system update:"
             << " capacitor_id_string: "                << capacitor_id_string
             << " circt_id_normal: "                    << circt_id_normal
             << " circt_nam_normal: "                   << circt_nam_normal
             << " circt_id_current: "                   << circt_id_current
             << " circt_name_current: "                 << circt_name_current
             << " switch_datetime: "                    << switch_datetime.asString()
             << " owner: "                              << owner
             << " capacitor_name: "                     << capacitor_name
             << " kvar_rating: "                        << kvar_rating
             << " cap_fs: "                             << cap_fs
             << " cbc_model: "                          << cbc_model
             << " serial_no: "                          << serial_no
             << " location: "                           << location
             << " switching_seq: "                      << switching_seq
             << " cap_disable_flag: "                   << cap_disable_flag
             << " cap_disable_type: "                   << cap_disable_type
             << " inoperable_bad_order_equipnote: "     << inoperable_bad_order_equipnote
             << " open_tag_note: "                      << open_tag_note
             << " cap_change_type: "                    << cap_change_type << endl;
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                                    }
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
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                                    }
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
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                                    }
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
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                                    }
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
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                                    }
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Bank not found MapLocationId: " << capacitor_id_string << " in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    capBankMovedToDifferentFeeder

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capBankMovedToDifferentFeeder(CtiCCFeeder* oldFeeder, CtiCCCapBank* movedCapBank, long feederid, long capswitchingorder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << text << ", " << additional << endl;
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

    if( !found )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Feeder not found MapLocationId: " << feederid << " in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    capBankDifferentOrderSameFeeder

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capBankDifferentOrderSameFeeder(CtiCCFeeder* currentFeeder, CtiCCCapBank* currentCapBank, long capswitchingorder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << text << ", " << additional << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    capOutOfServiceM3IAMFM

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capOutOfServiceM3IAMFM(long feederid, long capid, string& enableddisabled, string& fixedswitched)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - M3I change, 'Cap Out of Service' PAO Id: "
                                         << currentCapBank->getPaoId() << ", name: "
                                         << currentCapBank->getPaoName() << ", was "
                                         << enableddisabled << endl;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Bank not found MapLocationId: " << capid << " in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    feederOutOfServiceM3IAMFM

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::feederOutOfServiceM3IAMFM(long feederid, string& fixedswitched, string& enableddisabled)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - M3I change, 'Feeder Out of Service' PAO Id: "
                                     << currentFeeder->getPaoId() << ", name: "
                                     << currentFeeder->getPaoName() << ", was "
                                     << enableddisabled << endl;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Feeder not found MapLocationId: " << feederid << " in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    shutdown

    Dumps the substations list.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::shutdown()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    dumpAllDynamicData();
    delete_container(*_ccSubstationBuses);
    _ccSubstationBuses->clear();
    delete _ccSubstationBuses;
    delete_container(*_ccSubstations);
    _ccSubstations->clear();
    delete _ccSubstations;
    delete_container(*_ccCapBankStates);
    _ccCapBankStates->clear();
    delete _ccCapBankStates;
    delete_container(*_ccGeoAreas);
    _ccGeoAreas->clear();
    delete _ccGeoAreas;
    delete_container(*_ccSpecialAreas);
    _ccSpecialAreas->clear();
    delete _ccSpecialAreas;

}

/*---------------------------------------------------------------------------
    doOpStatsThr

    Starts on construction and simply forces a call to reset every 60 minutes
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::doOpStatsThr()
{
    string str;
    char var[128];
    int refreshrate = 3600;

    std::strcpy(var, "CAP_CONTROL_OP_STATS_REFRESH_RATE");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        refreshrate = atoi(str.c_str());
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << refreshrate << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    bool startUpSendStats = true;
    long lastOpStatsThreadPulse = 0;
    CtiTime rwnow;
    CtiTime announceTime((unsigned long) 0);
    CtiTime tickleTime((unsigned long) 0);
    CtiTime currentTime;
    CtiTime opStatRefreshRate =  nextScheduledTimeAlignedOnRate( currentTime,  _OP_STATS_REFRESH_RATE );

    CtiTime resetMemAllocRefreshRate =  nextScheduledTimeAlignedOnRate( currentTime,  HOURLY_RATE );

    unsigned long secondsFrom1901 = 0;

    while(true)
    {
        currentTime = CtiTime();
        secondsFrom1901 = currentTime.seconds();

        if( (currentTime.seconds() > opStatRefreshRate.seconds() && secondsFrom1901 != lastOpStatsThreadPulse) ||
            startUpSendStats )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Controller refreshing OP STATS" << endl;
            }
            if( currentTime.seconds() > resetMemAllocRefreshRate.seconds())
            {
                long currentAllocations = ResetBreakAlloc();
                if ( _CC_DEBUG & CC_DEBUG_EXTENDED )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Current Number of Historical Memory Allocations: " << currentAllocations << endl;
                }

                resetMemAllocRefreshRate =  nextScheduledTimeAlignedOnRate( currentTime,  HOURLY_RATE );
            }

            {
                RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Next OP STATS CHECKTIME : "<<opStatRefreshRate << endl;
                    }

                    createAllStatsPointDataMsgs(pointChanges);
                    try
                    {
                        //send point changes to dispatch
                        if( multiDispatchMsg->getCount() > 0 )
                        {
                            multiDispatchMsg->resetTime(); // CGP 5/21/04 Update its time to current time.
                            CtiCapController::getInstance()->sendMessageToDispatch(multiDispatchMsg);
                        }
                        else
                            delete multiDispatchMsg;
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }

            }
            startUpSendStats = false;
        }

        {
            rwRunnable().sleep(500);
            rwRunnable().serviceCancellation();
        }
    }
}


/*---------------------------------------------------------------------------
    doResetThr

    Starts on construction and simply forces a call to reset every 60 minutes
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::doResetThr()
{
    string str;
    char var[128];
    int refreshrate = 3600;

    std::strcpy(var, "CAP_CONTROL_REFRESH");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        refreshrate = atoi(str.c_str());
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << refreshrate << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    CtiTime lastPeriodicDatabaseRefresh = CtiTime();
    ThreadStatusKeeper threadStatus("CapControl doResetThr");

    while(true)
    {
        CtiTime currentTime;
        currentTime = currentTime.now();

        if( currentTime.seconds() >= lastPeriodicDatabaseRefresh.seconds()+refreshrate )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Periodic restore of substation list from the database" << endl;
            }

            dumpAllDynamicData();
            setValid(false);

            lastPeriodicDatabaseRefresh = CtiTime();
        }

        threadStatus.monitorCheck();

        {
            rwRunnable().serviceCancellation();
            rwRunnable().sleep(500);
        }
    }
}

/*---------------------------------------------------------------------------
    doAMFMThr

    Starts on construction and simply forces a call to reset every 60 minutes
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::doAMFMThr()
{
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
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << amfm_interface << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    if( ciStringEqual(amfm_interface,m3iAMFMInterfaceString) )
    {
        int refreshrate = 3600;
        string amFmDbDll = "none";
        string amFmDbName = "none";
        string amFmDbUser = "none";
        string amFmDbPassword = "none";

        std::strcpy(var, "CAP_CONTROL_AMFM_RELOAD_RATE");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            refreshrate = atoi(str.c_str());
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << refreshrate << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        std::strcpy(var, "CAP_CONTROL_AMFM_DB_TYPE");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            std::transform(str.begin(), str.end(), str.begin(), ::tolower);
            if(str == "oracle")
            {
                #ifdef _DEBUG
                amFmDbDll = "ora15d.dll";
                #else
                amFmDbDll = "ora12d.dll";
                #endif
            }
            else if(str == "mssql")
            {
                #ifdef _DEBUG
                amFmDbDll = "msq15d.dll";
                #else
                amFmDbDll = "msq12d.dll";
                #endif
            }

            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << amFmDbDll << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        std::strcpy(var, "CAP_CONTROL_AMFM_DB_SQLSERVER");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            amFmDbName = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << amFmDbName << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        std::strcpy(var, "CAP_CONTROL_AMFM_DB_USERNAME");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            amFmDbUser = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << amFmDbUser << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        std::strcpy(var, "CAP_CONTROL_AMFM_DB_PASSWORD");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            amFmDbPassword = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << amFmDbPassword << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }


        if( amFmDbDll != "none" && amFmDbName != "none" && amFmDbUser != "none" && amFmDbPassword != "none" )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Obtaining connection to the AMFM database..." << endl;
            }
            setDatabaseParams(amFmDbDll,amFmDbName,amFmDbUser,amFmDbPassword);

            time_t start = ::time(NULL);

            CtiTime currenttime = CtiTime();
            unsigned long tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+(2*refreshrate);
            CtiTime nextAMFMRefresh = CtiTime(CtiTime(tempsum));

            CtiTime rwnow;
            CtiTime announceTime((unsigned long) 0);
            CtiTime tickleTime((unsigned long) 0);


            while(true)
            {
                rwRunnable().serviceCancellation();

                if ( CtiTime() >= nextAMFMRefresh )
                {
                    if( _CC_DEBUG & CC_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Setting AMFM reload flag." << endl;
                    }

                    dumpAllDynamicData();
                    setReloadFromAMFMSystemFlag(true);

                    currenttime = CtiTime();
                    tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+refreshrate;
                    nextAMFMRefresh = CtiTime(CtiTime(tempsum));
                }
                else
                {
                    rwRunnable().sleep(500);
                }

            }

        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Can't find AMFM DB setting in master.cfg!!!" << endl;
        }
    }
}

/* Pointer to the singleton instance of CtiCCSubstationBusStore
   Instantiate lazily by Instance */
CtiCCSubstationBusStore* CtiCCSubstationBusStore::_instance = NULL;

/*---------------------------------------------------------------------------
    getInstance

    Returns a pointer to the singleton instance of CtiCCSubstationBusStore
---------------------------------------------------------------------------*/
CtiCCSubstationBusStore* CtiCCSubstationBusStore::getInstance(bool startCCThreads)
{
    if ( _instance == NULL )
    {
        _instance = new CtiCCSubstationBusStore();
        if (startCCThreads)
        {
            _instance->startThreads();
        }
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

/*---------------------------------------------------------------------------
    isValid

    Returns a true if the strategystore was able to initialize properly
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::isValid()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    return _isvalid;
}

/*---------------------------------------------------------------------------
    setValid

    Sets the _isvalid flag
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setValid(bool valid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    _isvalid = valid;
}

/*---------------------------------------------------------------------------
    getReregisterForPoints

    Gets _reregisterforpoints
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::getReregisterForPoints()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    return _reregisterforpoints;
}

/*---------------------------------------------------------------------------
    setReregisterForPoints

    Sets _reregisterforpoints
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setReregisterForPoints(bool reregister)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    _reregisterforpoints = reregister;
}

/*---------------------------------------------------------------------------
    getReloadFromAMFMSystemFlag

    Gets _reloadfromamfmsystemflag
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::getReloadFromAMFMSystemFlag()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    return _reloadfromamfmsystemflag;
}

/*---------------------------------------------------------------------------
    setReloadFromAMFMSystemFlag

    Sets _reloadfromamfmsystemflag
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setReloadFromAMFMSystemFlag(bool reload)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    _reloadfromamfmsystemflag = reload;
}

bool CtiCCSubstationBusStore::getWasSubBusDeletedFlag()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    return _wassubbusdeletedflag;
}

void CtiCCSubstationBusStore::setWasSubBusDeletedFlag(bool wasDeleted)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    _wassubbusdeletedflag = wasDeleted;
}

bool CtiCCSubstationBusStore::get2wayFlagUpdate()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    return _2wayFlagUpdate;
}

void CtiCCSubstationBusStore::set2wayFlagUpdate(bool flag)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Feeder: " << currentFeeder->getPaoName() << ", no longer recently controlled because no banks were pending in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                    else if( numberOfCapBanksPending > 1 )
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Multiple cap banks pending in Feeder: " << currentFeeder->getPaoName() << ", setting status to questionable in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        for(int k=0;k<ccCapBanks.size();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending && currentCapBank->getStatusPointId() > 0)
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Setting status to close questionable, Cap Bank: " << currentCapBank->getPaoName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                                currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                                currentCapBank->setControlRecentlySentFlag(false);
                                currentFeeder->setRetryIndex(0);
                                currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                                currentCapBank->setAfterVarsString("abnormal data");
                                currentCapBank->setPercentChangeString(" --- ");
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                                long stationId, areaId, spAreaId;
                                getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::CloseQuestionable, "Var: Multiple banks pending, CloseQuestionable", "cap control");
                                eventMsg->setActionId(currentCapBank->getActionId());
                                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);

                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && currentCapBank->getStatusPointId() > 0)
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Setting status to open questionable, Cap Bank: " << currentCapBank->getPaoName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                                currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                                currentCapBank->setControlRecentlySentFlag(false);
                                currentFeeder->setRetryIndex(0);
                                currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                                currentCapBank->setAfterVarsString("abnormal data");
                                currentCapBank->setPercentChangeString(" --- ");
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                                long stationId, areaId, spAreaId;
                                getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::OpenQuestionable, "Var: Multiple banks pending, OpenQuestionable", "cap control");
                                eventMsg->setActionId(currentCapBank->getActionId());
                                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);
                            }
                        }
                    }
                    else if (currentFeeder->getPostOperationMonitorPointScanFlag())
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Feeder: " << currentFeeder->getPaoName() << " waiting for Post Op Monitor Scan " << endl;
                        }
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
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName() << " questionable because feeder is not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            currentCapBank->setControlRecentlySentFlag(false);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                            long stationId, areaId, spAreaId;
                            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::CloseQuestionable, "Var: Feeder not recently controlled, CloseQuestionable", "cap control");
                            eventMsg->setActionId(currentCapBank->getActionId());
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);
                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && currentCapBank->getStatusPointId() > 0)
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPaoName() << " questionable because feeder is not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            currentCapBank->setControlRecentlySentFlag(false);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                            long stationId, areaId, spAreaId;
                            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::OpenQuestionable, "Var: Feeder not recently controlled, OpenQuestionable", "cap control");
                            eventMsg->setActionId(currentCapBank->getActionId());
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);
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
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Sub: " << currentSubstationBus->getPaoName() << " waiting for Post Op Monitor Scan " << endl;
            }
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
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Feeder: " << currentFeeder->getPaoName() << ", no longer recently controlled because sub bus not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(int k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                        if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending && !(currentCapBank->getVerificationFlag() && currentCapBank->getPerformingVerificationFlag()) && currentCapBank->getStatusPointId() > 0)
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Setting status to close questionable, Cap Bank: " << currentCapBank->getPaoName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            currentCapBank->setControlRecentlySentFlag(false);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                            long stationId, areaId, spAreaId;
                            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::CloseQuestionable, "Var: Sub not recently controlled, CloseQuestionable", "cap control");
                            eventMsg->setActionId(currentCapBank->getActionId());
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);

                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && !(currentCapBank->getVerificationFlag() && currentCapBank->getPerformingVerificationFlag()) && currentCapBank->getStatusPointId() > 0)
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Setting status to open questionable, Cap Bank: " << currentCapBank->getPaoName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            currentCapBank->setControlRecentlySentFlag(false);
                            currentFeeder->setRetryIndex(0);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                            long stationId, areaId, spAreaId;
                            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);

                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), currentFeeder->getPaoId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::OpenQuestionable, "Var: Sub not recently controlled, OpenQuestionable", "cap control");
                            eventMsg->setActionId(currentCapBank->getActionId());
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
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
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << text << ", " << additional << endl;
            }
            long stationId, areaId, spAreaId;
            getSubBusParentInfo(currentSubstationBus, spAreaId, areaId, stationId);
            if (currentSubstationBus->getDailyOperationsAnalogPointId() > 0)
               CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, currentSubstationBus->getDailyOperationsAnalogPointId(), spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, 1, currentSubstationBus->getCurrentDailyOperations(), text, "cap control"));
            else
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, currentSubstationBus->getPaoId(), 0, capControlSetOperationCount, 1, currentSubstationBus->getCurrentDailyOperations(), text, "cap control"));


        }
        currentSubstationBus->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
        currentSubstationBus->setMaxDailyOpsHitFlag(false);
        currentSubstationBus->setBusUpdatedFlag(true);
        currentSubstationBus->setCorrectionNeededNoBankAvailFlag(false);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(int j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

            currentFeeder->setCurrentDailyOperationsAndSendMsg(0, pointChanges);
            currentFeeder->setMaxDailyOpsHitFlag(false);
            currentFeeder->setCorrectionNeededNoBankAvailFlag(false);

            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            //**********************************************************************
            //The operation count on a cap bank is actually a total not a daily, doh
            //**********************************************************************
            for(int k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                currentCapBank->setCurrentDailyOperations(0);
                currentCapBank->setMaxDailyOpsHitFlag(false);
                currentCapBank->setRetryCloseFailedFlag(false);
                currentCapBank->setRetryOpenFailedFlag(false);
            }
            //**********************************************************************
            //The operation count on a cap bank is actually a total not a daily, doh
            //**********************************************************************
        }
    }
    CtiHolidayManager::getInstance().refresh();
    if (CtiHolidayManager::getInstance().isHolidayForAnySchedule(CtiDate()) )
    {
        setValid(false);
    }
    if( pointChanges.size() > 0 )
    {
        multiPointMsg->resetTime(); // CGP 5/21/04 Update its time to current time.
        CtiCapController::getInstance()->sendMessageToDispatch(multiPointMsg);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    static const string updateSql = "update dynamicccsubstationbus set "
                                    "verificationflag = ? "
                                    " where paobjectid = ?";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseWriter updater(conn, updateSql);

    updater << (string)(bus->getVerificationFlag()?"Y":"N") << bus->getPaoId();

    bool success = executeUpdater(updater);

    if ( success )
    {
        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(bus->getPaoId(), ChangePAODb,
                                                  bus->getPaoCategory(), bus->getPaoType(),
                                                  ChangeTypeUpdate);
        dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange);
    }

    return success;
}

// Updates the yukonpaobject table with the paoid and disable flag given.
bool CtiCCSubstationBusStore::updateDisableFlag(unsigned int paoid, bool isDisabled)
{
    static const string updateSql = "update yukonpaobject set disableflag = ? "
                                    " where paobjectid = ?";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseWriter updater(conn, updateSql);

    updater << (string)(isDisabled?"Y":"N") << paoid;

    return executeUpdater(updater);
}


/*---------------------------------------------------------------------------
    UpdatePaoDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the cap bank.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdatePaoDisableFlagInDB(CapControlPao* pao, bool disableFlag, bool forceFullReload)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange);
    }
    else
    {
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    static const string updateSql = "update capbank set operationalstate = ? "
                                    " where deviceid = ?";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseWriter updater(conn, updateSql);

    updater << capbank->getOperationalState() << capbank->getPaoId();

    bool updateSuccessful = executeUpdater(updater);

    if ( updateSuccessful )
    {
        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(capbank->getPaoId(), ChangePAODb,
                                                      capbank->getPaoCategory(),
                                                      capbank->getPaoType(),
                                                      ChangeTypeUpdate);
        dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange);
    }

    return updateSuccessful;
}

/*---------------------------------------------------------------------------
    UpdateCapBankInDB

    Updates multiple fields in tables associated with cap banks in the
    database.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateCapBankInDB(CtiCCCapBank* capbank)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    static const string paobjectUpdateSql = "update yukonpaobject set paoname = ?, disableflag = ?, description = ? "
                                    " where capbank = ?";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseWriter updater(conn, paobjectUpdateSql);

    updater << capbank->getPaoName()
            << (string)(capbank->getDisableFlag()?"Y":"N")
            << capbank->getPaoDescription().c_str()
            << capbank->getPaoId();

    bool paobjectUpdateSuccessful = executeUpdater(updater);

    static const string capbankUpdateSql = "update capbank set banksize = ?, operationalstate = ?"
                                           " where deviceid = ?";
    updater.setCommandText(capbankUpdateSql);

    updater << capbank->getBankSize()
            << capbank->getOperationalState()
            << capbank->getPaoId();

    bool capbankUpdateSuccessful = executeUpdater(updater);

    if ( paobjectUpdateSuccessful || capbankUpdateSuccessful )
    {
        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(capbank->getPaoId(), ChangePAODb,
                                                      capbank->getPaoCategory(),
                                                      capbank->getPaoType(),
                                                      ChangeTypeUpdate);
        dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    bool insertSuccessful = false;

    Cti::Database::DatabaseConnection   connection;
    {
        static const std::string deleteSql = "delete from ccfeederbanklist where feederid = ?";

        Cti::Database::DatabaseWriter       deleter(connection, deleteSql);

        deleter << feeder->getPaoId();

        deleter.execute();
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

        insertSuccessful = dbInserter.execute( );
    }

    CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(feeder->getPaoId(), ChangePAODb,
                                                  feeder->getPaoCategory(),
                                                  feeder->getPaoType(),
                                                  ChangeTypeUpdate);
    dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
    CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

    return insertSuccessful;
}


bool CtiCCSubstationBusStore::UpdateFeederSubAssignmentInDB(CtiCCSubstationBus* bus)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if (!_resetthr.isValid())
    {
        return false;
    }

    {

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
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

        return insertSuccesful;
    }
    return false;
}

/*---------------------------------------------------------------------------
    UpdateCapBankInDB

    Updates multiple fields in tables associated with cap banks in the
    database.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::InsertCCEventLogInDB(CtiCCEventLogMsg* msg)
{
    bool insertSuccessful = false;
    INT logId = CCEventLogIdGen();

    static const std::string insertSql = "insert into cceventlog values ( "
                                           "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                           "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                           "?, ?, ?)";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseWriter dbInserter(connection, insertSql);

    dbInserter << logId
                 << msg->getPointId()
                 << CtiTime(msg->getTimeStamp())
                 << msg->getSubId()
                 << msg->getFeederId()
                 << msg->getEventType()
                 << msg->getSeqId()
                 << msg->getValue()
                 << msg->getText()
                 << msg->getUserName()
                 << msg->getKvarBefore()
                 << msg->getKvarAfter()
                 << msg->getKvarChange()
                 << msg->getIpAddress()
                 << msg->getActionId()
                 << msg->getStateInfo()
                 << msg->getAVar()
                 << msg->getBVar()
                 << msg->getCVar()
                 << msg->getStationId()
                 << msg->getAreaId()
                 << msg->getSpecialAreaId()
                 << msg->getRegulatorId();

    if( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        string loggedSQLstring = dbInserter.asString();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << loggedSQLstring << endl;
        }
    }

    insertSuccessful = dbInserter.execute();

    if( !insertSuccessful )
    {
        if( _CC_DEBUG & CC_DEBUG_DATABASE )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " **** ERROR **** inserting entry into CCEventLog Table" << endl;
        }
    }

    return insertSuccessful;
}







/*---------------------------------------------------------------------------
    reloadStrategyFromDataBase

    Reloads all or single strategy from strategy table in the database.  Updates
    SubStationBus/Feeder Strategy values if a single strategyId is specified.
    StrategyId = 0 indicates reloaded all strategies from db.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::reloadStrategyFromDatabase(long strategyId)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    try
    {
        // First save states, then reload the strategy.
        if (strategyId == -1)
        {
            _strategyManager.saveAllStates();
            _strategyManager.reloadAll();
        }
        else
        {
            _strategyManager.saveStates(strategyId);
            _strategyManager.reload(strategyId);
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
                             string loggedSQLstring = dbRdr.asString();
                             {
                                 CtiLockGuard<CtiLogger> logger_guard(dout);
                                 dout << CtiTime() << " - " << loggedSQLstring << endl;
                             }
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
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - HOLIDAY!  CELEBRATE..." << endl;
                    }

                    reloadAndAssignHolidayStrategysFromDatabase(strategyId);

                }
                else
                {
                    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - No HOLIDAY!  No CELEBRATE..." << endl;
                    }
                }
            }
        }

        // After the strategy has been properly assigned everywhere, restore the states
        if (strategyId == -1)
        {
            _strategyManager.restoreAllStates();
        }
        else
        {
            _strategyManager.restoreStates(strategyId);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());


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
                         string loggedSQLstring = rdr.asString();
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);
                             dout << CtiTime() << " - " << loggedSQLstring << endl;
                         }
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
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
                       string loggedSQLstring = dbRdr.asString();
                       {
                           CtiLockGuard<CtiLogger> logger_guard(dout);
                           dout << CtiTime() << " - " << loggedSQLstring << endl;
                       }
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    reloadSubstationFromDatabase

    Reloads a single substation from the database.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadSubstationFromDatabase(long substationId,
                                  PaoIdToSubstationMap *paobject_substation_map,
                                  PaoIdToAreaMap *paobject_area_map,
                                  PaoIdToSpecialAreaMap *paobject_specialarea_map,
                                  PointIdToSubstationMultiMap *pointid_station_map,
                                  ChildToParentMap *substation_area_map,
                                  ChildToParentMultiMap *substation_specialarea_map,
                                  CtiCCSubstation_vec *ccSubstations)
{
    CtiCCSubstationPtr substationToUpdate = NULL;

    if (substationId > 0)
    {
        substationToUpdate = findSubstationByPAObjectID(substationId);
    }

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    try
    {
        if (substationToUpdate != NULL)
        {
            deleteSubstation(substationId);
        }
        CtiTime currentDateTime;
        {
            static const string sqlNoID =   "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                                "YP.description, YP.disableflag, CSS.voltreductionpointid "
                                            "FROM yukonpaobject YP, capcontrolsubstation CSS "
                                            "WHERE YP.paobjectid = CSS.substationid";

            static const string sqlWithID =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                                "YP.description, YP.disableflag, CSS.voltreductionpointid "
                                             "FROM yukonpaobject YP, capcontrolsubstation CSS "
                                             "WHERE YP.paobjectid = CSS.substationid AND YP.paobjectid = ?";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if ( substationId > 0 )
            {
                rdr.setCommandText(sqlWithID);
                rdr << substationId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {

                CtiCCSubstationPtr currentCCSubstation = CtiCCSubstationPtr(new CtiCCSubstation(rdr));
                paobject_substation_map->insert(make_pair(currentCCSubstation->getPaoId(),currentCCSubstation));

                if (currentCCSubstation->getVoltReductionControlId() > 0 )
                {
                    pointid_station_map->insert(make_pair(currentCCSubstation->getVoltReductionControlId(), currentCCSubstation));
                    currentCCSubstation->addPointId(currentCCSubstation->getVoltReductionControlId());
                }
            }

        }
        {
            static const string sqlNoID =  "SELECT DSS.substationid, DSS.additionalflags, DSS.saenabledid "
                                           "FROM capcontrolsubstation CCS, dynamicccsubstation DSS "
                                           "WHERE CCS.substationid = DSS.substationid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( substationId > 0 )
            {
                static const string sqlID = string(sqlNoID + " AND CCS.substationid = ?");
                rdr.setCommandText(sqlID);
                rdr << substationId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentSubstationId;

                rdr["substationid"] >> currentSubstationId;
                CtiCCSubstationPtr currentCCSubstation = paobject_substation_map->find(currentSubstationId)->second;

                if (currentCCSubstation->getPaoId() == currentSubstationId)
                {
                     currentCCSubstation->setDynamicData(rdr);
                     CtiCCSpecialPtr currentSA = findSpecialAreaByPAObjectID(currentCCSubstation->getSaEnabledId());
                     if (!currentSA)
                     {
                         currentCCSubstation->setSaEnabledId(0);
                     }
                }

            }
        }
        {
            static const string sqlNoID = "SELECT CAS.areaid, CAS.substationbusid, CAS.displayorder "
                                          "FROM ccsubareaassignment CAS";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if (substationId > 0)
            {
                static const string sqlID = string(sqlNoID + " WHERE CAS.substationbusid = ?");
                rdr.setCommandText(sqlID);
                rdr << substationId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            CtiCCSubstationPtr currentCCSubstation;
            while ( rdr() )
            {
                long currentAreaId;
                long currentSubstationId;
                long displayOrder;
                rdr["areaid"] >> currentAreaId;
                rdr["substationbusid"] >> currentSubstationId;
                rdr["displayorder"] >>displayOrder;
                currentCCSubstation = paobject_substation_map->find(currentSubstationId)->second;
                if (currentCCSubstation != NULL)
                {

                    currentCCSubstation->setParentId(currentAreaId);
                    currentCCSubstation->setDisplayOrder(displayOrder);
                    CtiCCAreaPtr currentCCArea = NULL;

                    if (substationId > 0)
                        currentCCArea = findAreaByPAObjectID(currentAreaId);
                    else
                    {
                        if (paobject_area_map->find(currentAreaId) != paobject_area_map->end())
                            currentCCArea = paobject_area_map->find(currentAreaId)->second;
                    }
                    substation_area_map->insert(make_pair(currentSubstationId, currentAreaId));
                    if (currentCCArea != NULL)
                    {
                        currentCCArea->addSubstationId(currentSubstationId);
                    }

                    ccSubstations->push_back( currentCCSubstation );
                }
            }

        }
        {
            static const string sqlNoID = "SELECT CSR.substationbusid, CSR.areaid, CSR.displayorder "
                                          "FROM ccsubspecialareaassignment CSR";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( substationId > 0 )
            {
                static const string sqlID = string(sqlNoID + " WHERE CSR.substationbusid = ?");
                rdr.setCommandText(sqlID);
                rdr << substationId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            long currentSubId;
            while ( rdr() )
            {
                rdr["substationbusid"] >> currentSubId;
                //add substationbusids to special area list...;
                long currentSpAreaId;

                rdr["areaid"] >> currentSpAreaId;
                CtiCCSpecialPtr currentCCSpArea = NULL;
                currentCCSpArea = findSpecialAreaByPAObjectID(currentSpAreaId);

                if (currentCCSpArea != NULL)
                {
                    CtiCCSubstationPtr currentStation = findSubstationByPAObjectID(currentSubId);
                    if (currentStation != NULL)
                    {
                        if (currentStation->getSaEnabledId() == 0)
                        {
                            currentStation->setSaEnabledId(currentSpAreaId);
                        }
                        if (!currentCCSpArea->getDisableFlag())
                        {
                            currentStation->setSaEnabledId(currentSpAreaId);
                            currentStation->setSaEnabledFlag(true);
                        }
                        if (currentStation->getParentId() <= 0)
                        {
                            currentStation->setParentId(currentSpAreaId);
                            ccSubstations->push_back( currentStation );
                        }

                    }
                    substation_specialarea_map->insert(make_pair(currentSubId, currentSpAreaId));
                    currentCCSpArea->addSubstationId(currentSubId);
                }
            }
        }
        {
            static const string sqlNoID =  "SELECT PT.paobjectid, PT.pointid, PT.pointoffset, PT.pointtype "
                                           "FROM capcontrolsubstation CCS, point PT "
                                           "WHERE CCS.substationid = PT.paobjectid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( substationId > 0 )
            {
                static const string sqlID = string(sqlNoID + " AND CCS.substationid = ?");
                rdr.setCommandText(sqlID);
                rdr << substationId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentStationId;

                rdr["paobjectid"] >> currentStationId;
                CtiCCSubstationPtr currentStation = paobject_substation_map->find(currentStationId)->second;


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
                        currentStation->setDisabledStatePointId(tempPointId, substationId);
                        pointid_station_map->insert(make_pair(tempPointId,currentStation));
                        currentStation->addPointId(tempPointId);
                    }
                    else if ( resolvePointType(tempPointType) == AnalogPointType )
                    {
                        if ( tempPointOffset >= Cti::CapControl::Offset_OperationSuccessPercentRangeMin &&
                             tempPointOffset <= Cti::CapControl::Offset_OperationSuccessPercentRangeMax )
                        {
                            if (currentStation->getOperationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                pointid_station_map->insert(make_pair(tempPointId,currentStation));
                                currentStation->addPointId(tempPointId);
                            }
                        }
                        else if ( tempPointOffset >= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMin &&
                                  tempPointOffset <= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMax )
                        {
                            if (currentStation->getConfirmationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                pointid_station_map->insert(make_pair(tempPointId,currentStation));
                                currentStation->addPointId(tempPointId);
                            }
                        }
                        else
                        {//undefined area point
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Undefined Substation point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                    else if ( !(resolvePointType(tempPointType) == StatusPointType && tempPointOffset == -1)) //tag point = status with -1 offset
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Undefined Substation point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                }
            }
        }

        if (substationId > 0) // else, when reloading all, then the reload of feeders will be called after subBusReload and take care of it.
        {
            static const string sql =  "SELECT SBL.substationid, SBL.substationbusid, SBL.displayorder "
                                       "FROM ccsubstationsubbuslist SBL "
                                       "WHERE SBL.substationid = ? "
                                       "ORDER BY SBL.substationid, SBL.displayorder";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr << substationId;

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            long currentSubBusId;
            while ( rdr() )
            {

                rdr["substationbusid"] >> currentSubBusId;
               reloadSubBusFromDatabase(currentSubBusId, &_paobject_subbus_map,
                                         &_paobject_substation_map, &_pointid_subbus_map,
                                         &_altsub_sub_idmap, &_subbus_substation_map,
                                         _ccSubstationBuses );


            }
            reloadOperationStatsFromDatabase(substationId,&_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map,
                                         &_paobject_substation_map, &_paobject_area_map, &_paobject_specialarea_map);
        }
        //_reregisterforpoints = true;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}
/*---------------------------------------------------------------------------
    reloadSubBusFromDB

    Reloads a single subbus from the database.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadAreaFromDatabase(long areaId,
                                  PaoIdToAreaMap *paobject_area_map,
                                  PointIdToAreaMultiMap *pointid_area_map,
                                  CtiCCArea_vec *ccGeoAreas)
{
    CtiCCAreaPtr areaToUpdate = NULL;

    if (areaId > 0)
    {
        areaToUpdate = findAreaByPAObjectID(areaId);
    }

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    try
    {
        if (areaToUpdate != NULL)
        {
            deleteArea(areaId);
        }

        CtiTime currentDateTime;
        {
            static const string sqlNoID =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                              "YP.description, YP.disableflag, CCA.voltreductionpointid "
                                           "FROM yukonpaobject YP, capcontrolarea CCA "
                                           "WHERE YP.paobjectid = CCA.areaid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(areaId > 0)
            {
                static const string sqlID = string(sqlNoID + " AND YP.paobjectid = ?");
                rdr.setCommandText(sqlID);
                rdr << areaId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                CtiCCAreaPtr currentCCArea = CtiCCAreaPtr(new CtiCCArea(rdr, &_strategyManager));

                paobject_area_map->insert(make_pair(currentCCArea->getPaoId(),currentCCArea));

                ccGeoAreas->push_back( currentCCArea );

                if (currentCCArea->getVoltReductionControlPointId() > 0 )
                {
                    pointid_area_map->insert(make_pair(currentCCArea->getVoltReductionControlPointId(), currentCCArea));
                    currentCCArea->addPointId(currentCCArea->getVoltReductionControlPointId());
                }
            }

        }
        {
             static const string sqlNoID =  "SELECT SA.paobjectid, SA.seasonscheduleid, SA.seasonname, "
                                               "SA.strategyid, DS.seasonstartmonth, DS.seasonendmonth, "
                                               "DS.seasonstartday, DS.seasonendday "
                                            "FROM capcontrolarea CCA, ccseasonstrategyassignment SA, dateofseason DS "
                                            "WHERE SA.paobjectid = CCA.areaid AND SA.seasonname = DS.seasonname AND "
                                               "SA.seasonscheduleid = DS.seasonscheduleid";

             Cti::Database::DatabaseConnection connection;
             Cti::Database::DatabaseReader rdr(connection);

             if( areaId > 0 )
             {
                 static const string sqlID = string(sqlNoID + " AND SA.paobjectid = ?");
                 rdr.setCommandText(sqlID);
                 rdr << areaId;
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
                     today <= CtiDate(endDay, endMon, today.year())  )
                 {
                     long currentAreaId, stratId;
                     rdr["paobjectid"] >> currentAreaId;
                     rdr["strategyid"] >> stratId;


                     CtiCCAreaPtr currentCCArea = NULL;
                     if (areaId > 0)
                          currentCCArea = findAreaByPAObjectID(currentAreaId);
                     else
                         currentCCArea = paobject_area_map->find(currentAreaId)->second;

                     if (currentCCArea != NULL)
                     {
                         currentCCArea->setStrategy( stratId );
                     }

                 }
             }
        }

        //CHECK HOLIDAY SETTINGS
        CtiHolidayManager::getInstance().refresh();
        if (CtiHolidayManager::getInstance().isHolidayForAnySchedule(CtiDate()) )
        {
             static const string sqlNoID =  "SELECT HSA.paobjectid, HSA.holidayscheduleid, HSA.strategyid, "
                                               "DH.holidayname, DH.holidaymonth, DH.holidayday, DH.holidayyear "
                                            "FROM capcontrolarea CCA, ccholidaystrategyassignment HSA, "
                                               "dateofholiday DH "
                                            "WHERE HSA.paobjectid = CCA.areaid AND "
                                               "HSA.holidayscheduleid = DH.holidayscheduleid";

             Cti::Database::DatabaseConnection connection;
             Cti::Database::DatabaseReader rdr(connection);

             if( areaId > 0)
             {
                 static const string sqlID = string(sqlNoID + " AND HSA.paobjectid = ?");
                 rdr.setCommandText(sqlID);
                 rdr << areaId;
             }
             else
             {
                 rdr.setCommandText(sqlNoID);
             }

             rdr.execute();

             if ( _CC_DEBUG & CC_DEBUG_DATABASE )
             {
                 string loggedSQLstring = rdr.asString();
                 {
                     CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - " << loggedSQLstring << endl;
                 }
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
                     long currAreaId, stratId;
                     rdr["paobjectid"] >> currAreaId;
                     rdr["strategyid"] >> stratId;

                     CtiCCAreaPtr currentArea = NULL;

                     if (currAreaId > 0)
                          currentArea = findAreaByPAObjectID(currAreaId);
                     else
                         currentArea = paobject_area_map->find(currAreaId)->second;

                     if (currentArea != NULL)
                     {
                         currentArea->setStrategy( stratId );
                     }
                 }
             }
        }
        {
            static const string sqlNoID =  "SELECT DCA.areaid, DCA.additionalflags, DCA.controlvalue "
                                           "FROM capcontrolarea CCA, dynamicccarea DCA "
                                           "WHERE CCA.areaid = DCA.areaid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(areaId > 0)
            {
                static const string sqlID = string(sqlNoID + " AND CCA.areaid = ?");
                rdr.setCommandText(sqlID);
                rdr << areaId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentAreaId;

                rdr["areaid"] >> currentAreaId;
                CtiCCAreaPtr currentCCArea = paobject_area_map->find(currentAreaId)->second;

                if (currentCCArea->getPaoId() == currentAreaId)
                {
                     currentCCArea->setDynamicData(rdr);
                }

            }
        }
        {
            static const string sqlNoID =  "SELECT PT.paobjectid, PT.pointid, PT.pointoffset, PT.pointtype "
                                           "FROM capcontrolarea CCA, point PT "
                                           "WHERE CCA.areaid = PT.paobjectid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( areaId > 0 )
            {
                static const string sqlID = string(sqlNoID + " AND CCA.areaId = ?");
                rdr.setCommandText(sqlID);
                rdr << areaId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentAreaId;

                rdr["paobjectid"] >> currentAreaId;
                CtiCCAreaPtr currentArea = paobject_area_map->find(currentAreaId)->second;


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
                        currentArea->setDisabledStatePointId(tempPointId, areaId);
                        pointid_area_map->insert(make_pair(tempPointId,currentArea));
                        currentArea->addPointId(tempPointId);
                    }
                    else if ( resolvePointType(tempPointType) == AnalogPointType )
                    {
                        if ( tempPointOffset >= Cti::CapControl::Offset_OperationSuccessPercentRangeMin &&
                             tempPointOffset <= Cti::CapControl::Offset_OperationSuccessPercentRangeMax )
                        {
                            if (currentArea->getOperationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                pointid_area_map->insert(make_pair(tempPointId,currentArea));
                                currentArea->addPointId(tempPointId);
                            }
                        }
                        else if ( tempPointOffset >= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMin &&
                                  tempPointOffset <= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMax )
                        {
                            if (currentArea->getConfirmationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                pointid_area_map->insert(make_pair(tempPointId,currentArea));
                                currentArea->addPointId(tempPointId);
                            }
                        }
                        else
                        {//undefined area point
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Undefined Area point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                    else if ( !(resolvePointType(tempPointType) == StatusPointType && tempPointOffset == -1)) //tag point = status with -1 offset
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Undefined Area point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                }
            }
        }
        if (areaId > 0) // else, when reloading all, then the reload of subs will be called after areaReload and take care of it.
        {
            static const string sql = "SELECT SAA.substationbusid, SAA.areaid, SAA.displayorder "
                                      "FROM ccsubareaassignment SAA "
                                      "WHERE SAA.areaid = ?";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr << areaId;

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            long currentSubstationId;
            while ( rdr() )
            {
                rdr["substationbusid"] >> currentSubstationId;
                reloadSubstationFromDatabase(currentSubstationId,&_paobject_substation_map,
                                       &_paobject_area_map, &_paobject_specialarea_map,
                                             &_pointid_station_map, &_substation_area_map,
                                       &_substation_specialarea_map, _ccSubstations );
            }

            reloadOperationStatsFromDatabase(areaId,&_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map,
                                             &_paobject_substation_map, &_paobject_area_map, &_paobject_specialarea_map);


            cascadeAreaStrategySettings(findAreaByPAObjectID(areaId));
        }
        //_reregisterforpoints = true;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    reloadSubBusFromDB

    Reloads a single subbus from the database.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadSpecialAreaFromDatabase(PaoIdToSpecialAreaMap *paobject_specialarea_map,
                                  PointIdToSpecialAreaMultiMap *pointid_specialarea_map,
                                  CtiCCSpArea_vec *ccSpecialAreas)
{
    CtiCCSpecialPtr spAreaToUpdate = NULL;
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    try
    {
        CtiTime currentDateTime;
        {
            static const string sqlNoID =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                              "YP.description, YP.disableflag, CSA.voltreductionpointid "
                                           "FROM yukonpaobject YP, capcontrolspecialarea CSA "
                                           "WHERE YP.paobjectid = CSA.areaid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            rdr.setCommandText(sqlNoID);
            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                CtiCCSpecialPtr currentCCSpArea = CtiCCSpecialPtr(new CtiCCSpecial(rdr, &_strategyManager));

                paobject_specialarea_map->insert(make_pair(currentCCSpArea->getPaoId(),currentCCSpArea));

                ccSpecialAreas->push_back( currentCCSpArea );
                if (currentCCSpArea->getVoltReductionControlPointId() > 0 )
                {
                    pointid_specialarea_map->insert(make_pair(currentCCSpArea->getVoltReductionControlPointId(), currentCCSpArea));
                    currentCCSpArea->addPointId(currentCCSpArea->getVoltReductionControlPointId());
                }
            }
        }
        {
             static const string sqlNoID =  "SELECT SSA.paobjectid, SSA.seasonscheduleid, SSA.seasonname, "
                                               "SSA.strategyid, DS.seasonstartmonth, DS.seasonendmonth, "
                                               "DS.seasonstartday, DS.seasonendday "
                                            "FROM capcontrolspecialarea CSA, ccseasonstrategyassignment SSA, "
                                               "dateofseason DS "
                                            "WHERE SSA.paobjectid = CSA.areaid AND SSA.seasonname = DS.seasonname "
                                               "AND SSA.seasonscheduleid = DS.seasonscheduleid";

             Cti::Database::DatabaseConnection connection;
             Cti::Database::DatabaseReader rdr(connection);

             rdr.setCommandText(sqlNoID);
             rdr.execute();

             if ( _CC_DEBUG & CC_DEBUG_DATABASE )
             {
                 string loggedSQLstring = rdr.asString();
                 {
                     CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - " << loggedSQLstring << endl;
                 }
             }

             while ( rdr() )
             {
                 int startMon, startDay, endMon, endDay;
                 rdr["seasonstartmonth"] >> startMon;
                 rdr["seasonendmonth"] >> endMon;
                 rdr["seasonstartday"] >> startDay;
                 rdr["seasonendday"] >> endDay;

                 CtiDate today = CtiDate();

                 if (today  >= CtiDate(startDay, startMon, today.year()) &&
                     today <= CtiDate(endDay, endMon, today.year())  )
                 {
                     long currentAreaId, stratId;
                     rdr["paobjectid"] >> currentAreaId;
                     rdr["strategyid"] >> stratId;


                     CtiCCSpecialPtr currentCCSpArea = NULL;
                     currentCCSpArea = paobject_specialarea_map->find(currentAreaId)->second;

                     if (currentCCSpArea != NULL)
                     {
                         currentCCSpArea->setStrategy( stratId );
                     }
                 }
             }
        }

        //CHECK FOR HOLIDAY SETTTINGS
        CtiHolidayManager::getInstance().refresh();
        if (CtiHolidayManager::getInstance().isHolidayForAnySchedule(CtiDate()) )
        {
             static const string sqlNoID =  "SELECT HSA.paobjectid, HSA.holidayscheduleid, HSA.strategyid, "
                                               "DH.holidayname, DH.holidaymonth, DH.holidayday, DH.holidayyear "
                                            "FROM capcontrolspecialarea CSA, ccholidaystrategyassignment HSA, "
                                               "dateofholiday DH "
                                            "WHERE HSA.paobjectid = CSA.areaid AND "
                                               "HSA.holidayscheduleid = DH.holidayscheduleid";

             Cti::Database::DatabaseConnection connection;
             Cti::Database::DatabaseReader rdr(connection);

             rdr.setCommandText(sqlNoID);
             rdr.execute();

             if ( _CC_DEBUG & CC_DEBUG_DATABASE )
             {
                 string loggedSQLstring = rdr.asString();
                 {
                     CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - " << loggedSQLstring << endl;
                 }
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
                     long areaId, stratId;
                     rdr["paobjectid"] >> areaId;
                     rdr["strategyid"] >> stratId;

                     CtiCCSpecialPtr currentSpArea = NULL;

                     currentSpArea = findSpecialAreaByPAObjectID(areaId);
                     if (currentSpArea != NULL)
                     {
                         currentSpArea->setStrategy( stratId );
                     }
                 }
             }
        }
        {
            static const string sqlNoID =  "SELECT DSA.areaid, DSA.additionalflags, DSA.controlvalue "
                                           "FROM capcontrolspecialarea CSA, dynamicccspecialarea DSA "
                                           "WHERE CSA.areaid = DSA.areaid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            rdr.setCommandText(sqlNoID);
            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentSpAreaId;

                rdr["areaid"] >> currentSpAreaId;
                CtiCCSpecialPtr currentCCSpArea = paobject_specialarea_map->find(currentSpAreaId)->second;

                if (currentCCSpArea->getPaoId() == currentSpAreaId)
                {
                     currentCCSpArea->setDynamicData(rdr);
                }
            }
        }
        {
            static const string sqlNoID =  "SELECT PT.paobjectid, PT.pointid, PT.pointoffset, PT.pointtype "
                                           "FROM capcontrolspecialarea CSA, point PT "
                                           "WHERE CSA.areaid = PT.paobjectid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            rdr.setCommandText(sqlNoID);
            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentAreaId;

                rdr["paobjectid"] >> currentAreaId;
                CtiCCSpecialPtr currentSpArea = paobject_specialarea_map->find(currentAreaId)->second;


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
                        currentSpArea->setDisabledStatePointId(tempPointId, true);
                        pointid_specialarea_map->insert(make_pair(tempPointId,currentSpArea));
                        currentSpArea->addPointId(tempPointId);
                    }
                    else if ( resolvePointType(tempPointType) == AnalogPointType )
                    {
                        if ( tempPointOffset >= Cti::CapControl::Offset_OperationSuccessPercentRangeMin &&
                             tempPointOffset <= Cti::CapControl::Offset_OperationSuccessPercentRangeMax )
                        {
                            if (currentSpArea->getOperationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                pointid_specialarea_map->insert(make_pair(tempPointId,currentSpArea));
                                currentSpArea->addPointId(tempPointId);
                            }
                        }
                        else if ( tempPointOffset >= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMin &&
                                  tempPointOffset <= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMax )
                        {
                            if (currentSpArea->getConfirmationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                pointid_specialarea_map->insert(make_pair(tempPointId,currentSpArea));
                                currentSpArea->addPointId(tempPointId);
                            }
                        }
                        else
                        {//undefined area point
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Undefined Special Area point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                    else if ( !(resolvePointType(tempPointType) == StatusPointType && tempPointOffset == -1)) //tag point = status with -1 offset
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Undefined Special Area point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                }
            }
         }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
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

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    try
    {
        if (subBusToUpdate != NULL)
        {
            deleteSubBus(subBusId);
        }

        CtiTime currentDateTime;
        {
            static const string sqlNoID =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                             "YP.description, YP.disableflag, CSB.currentvarloadpointid, "
                                             "CSB.currentwattloadpointid, CSB.maplocationid, CSB.currentvoltloadpointid, "
                                             "CSB.AltSubID, CSB.SwitchPointID, CSB.DualBusEnabled, "
                                             "CSB.multiMonitorControl, CSB.usephasedata, CSB.phaseb, CSB.phasec, "
                                             "CSB.controlflag, CSB.voltreductionpointid, CSB.disablebuspointid "
                                           "FROM yukonpaobject YP, capcontrolsubstationbus CSB "
                                           "WHERE YP.paobjectid = CSB.substationbusid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( subBusId > 0 )
            {
                static const string sqlID = string(sqlNoID + " AND (YP.paobjectid = ? OR CSB.AltSubID = ?)");
                rdr.setCommandText(sqlID);
                rdr << subBusId
                    << subBusId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            CtiCCSubstationBusPtr  currentCCSubstationBus;
            while ( rdr() )
            {
                long busId;
                long altBusId;
                rdr["paobjectid"] >> busId;
                rdr["AltSubID"] >> altBusId;
                if (subBusId > 0 && busId != subBusId && altBusId == subBusId && paobject_subbus_map->find(busId) != paobject_subbus_map->end() )
                {
                    currentCCSubstationBus = paobject_subbus_map->find( busId )->second;
                }
                else
                {
                    currentCCSubstationBus = CtiCCSubstationBusPtr(new CtiCCSubstationBus(rdr, &_strategyManager));
                    paobject_subbus_map->insert(make_pair(currentCCSubstationBus->getPaoId(),currentCCSubstationBus));
                }


                if (currentCCSubstationBus->getCurrentVarLoadPointId() > 0 )
                {
                    pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getCurrentVarLoadPointId(), currentCCSubstationBus));
                    currentCCSubstationBus->addPointId(currentCCSubstationBus->getCurrentVarLoadPointId());
                }
                if (currentCCSubstationBus->getCurrentWattLoadPointId() > 0 )
                {
                    pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getCurrentWattLoadPointId(), currentCCSubstationBus));
                    currentCCSubstationBus->addPointId(currentCCSubstationBus->getCurrentWattLoadPointId());
                }
                if (currentCCSubstationBus->getCurrentVoltLoadPointId() > 0 )
                {
                    pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getCurrentVoltLoadPointId(), currentCCSubstationBus));
                    currentCCSubstationBus->addPointId(currentCCSubstationBus->getCurrentVoltLoadPointId());
                }
                if (currentCCSubstationBus->getSwitchOverPointId() > 0 )
                {
                    pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getSwitchOverPointId(), currentCCSubstationBus));
                    currentCCSubstationBus->addPointId(currentCCSubstationBus->getSwitchOverPointId());
                }

                if (currentCCSubstationBus->getDualBusEnable() &&
                    currentCCSubstationBus->getAltDualSubId() != currentCCSubstationBus->getPaoId())
                {
                    altsub_sub_idmap->insert(make_pair(currentCCSubstationBus->getAltDualSubId(), currentCCSubstationBus->getPaoId()));
                }
                if (currentCCSubstationBus->getUsePhaseData())
                {
                    if (currentCCSubstationBus->getPhaseBId() > 0)
                    {
                        pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getPhaseBId(), currentCCSubstationBus));
                        currentCCSubstationBus->addPointId(currentCCSubstationBus->getPhaseBId());
                    }
                    if (currentCCSubstationBus->getPhaseCId() > 0)
                    {
                        pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getPhaseCId(), currentCCSubstationBus));
                        currentCCSubstationBus->addPointId(currentCCSubstationBus->getPhaseCId());
                    }

                }

                if (currentCCSubstationBus->getVoltReductionControlId() > 0 )
                {
                    pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getVoltReductionControlId(), currentCCSubstationBus));
                    currentCCSubstationBus->addPointId(currentCCSubstationBus->getVoltReductionControlId());
                }
                if (currentCCSubstationBus->getDisableBusPointId() > 0)
                {
                    pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getDisableBusPointId(), currentCCSubstationBus));
                    currentCCSubstationBus->addPointId(currentCCSubstationBus->getDisableBusPointId());

                }
                    //cCSubstationBuses->push_back(currentCCSubstationBus);
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
                string loggedSQLstring = dbRdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( dbRdr() )
            {
                long currentSubstationId;
                long currentSubBusId;
                long displayOrder;
                dbRdr["substationid"] >> currentSubstationId;
                dbRdr["substationbusid"] >> currentSubBusId;
                dbRdr["displayorder"] >>displayOrder;
                CtiCCSubstationBusPtr currentCCSubstationBus = paobject_subbus_map->find(currentSubBusId)->second;
                if (currentCCSubstationBus != NULL)
                {

                    currentCCSubstationBus->setParentId(currentSubstationId);
                    currentCCSubstationBus->setDisplayOrder(displayOrder);
                    CtiCCSubstationPtr currentCCSubstation = NULL;

                    if (subBusId > 0)
                        currentCCSubstation = findSubstationByPAObjectID(currentSubstationId);
                    else
                    {
                        if (paobject_substation_map->find(currentSubstationId) != paobject_substation_map->end())
                            currentCCSubstation = paobject_substation_map->find(currentSubstationId)->second;
                    }

                    if (currentCCSubstation != NULL)
                    {
                        currentCCSubstationBus->setParentName(currentCCSubstation->getPaoName());
                        bool found = false;
                        for each (long busId in currentCCSubstation->getCCSubIds())
                        {
                            if (busId == currentSubBusId)
                            {
                                found = true;
                                break;
                            }
                        }
                        if(!found)
                        {
                            currentCCSubstation->addCCSubId(currentSubBusId);
                        }
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
                         if (subBusId == 0 && paobject_subbus_map->find(busId) != paobject_subbus_map->end())
                             currentCCSubstationBus = paobject_subbus_map->find(busId)->second;
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
                     string loggedSQLstring = dbRdr.asString();
                     {
                         CtiLockGuard<CtiLogger> logger_guard(dout);
                         dout << CtiTime() << " - " << loggedSQLstring << endl;
                     }
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
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);
                             dout << CtiTime() << " TODAY is: " << today << " HOLIDAY is: "<<CtiDate(holDay, holMon, tempYear)  << endl;
                         }
                         long busId, stratId, areaId = 0;
                         CtiCCSubstationBusPtr currentCCSubstationBus = NULL;
                         if (subBusId > 0)
                             currentCCSubstationBus = findSubBusByPAObjectID(subBusId);

                         if (!dbRdr["substationbusid"].isNull())
                         {
                             dbRdr["substationbusid"] >> busId;
                             if (subBusId == 0 && paobject_subbus_map->find(busId) != paobject_subbus_map->end())
                                 currentCCSubstationBus = paobject_subbus_map->find(busId)->second;
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

                    if (paobject_subbus_map->find(dualBusId) != paobject_subbus_map->end())
                    {
                        CtiCCSubstationBusPtr dualBus = NULL;
                        CtiCCSubstationBusPtr currentCCSubstationBus = paobject_subbus_map->find(dualBusId)->second;
                        if (paobject_subbus_map->find(currentCCSubstationBus->getAltDualSubId()) != paobject_subbus_map->end())
                        {
                            dualBus = paobject_subbus_map->find(currentCCSubstationBus->getAltDualSubId())->second;
                            if (ciStringEqual(currentCCSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::KVarControlUnit) ||
                                ciStringEqual(currentCCSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKVarControlUnit) ||
                                ciStringEqual(currentCCSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::PFactorKWKQControlUnit) )
                            {
                                if (dualBus->getCurrentVarLoadPointId() > 0)
                                {
                                    pointid_subbus_map->insert(make_pair(dualBus->getCurrentVarLoadPointId(), currentCCSubstationBus ));
                                    currentCCSubstationBus->addPointId(dualBus->getCurrentVarLoadPointId());
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " ***WARNING*** Sub: "<<currentCCSubstationBus->getPaoName()<<" will NOT operate in Dual Mode."<<endl;
                                    dout << "   Alternate Sub: "<<dualBus->getPaoName()<<" does not have a VAR Point ID attached."<< endl;
                                }
                            }
                            else if(ciStringEqual(currentCCSubstationBus->getStrategy()->getControlUnits(),ControlStrategy::VoltsControlUnit) )
                            {
                                if (dualBus->getCurrentVoltLoadPointId() > 0)
                                {
                                    pointid_subbus_map->insert(make_pair(dualBus->getCurrentVoltLoadPointId(), currentCCSubstationBus));
                                    currentCCSubstationBus->addPointId(dualBus->getCurrentVoltLoadPointId());
                                }
                                else
                                {
                                     CtiLockGuard<CtiLogger> logger_guard(dout);
                                     dout << CtiTime() << " ***WARNING*** Sub: "<<currentCCSubstationBus->getPaoName()<<" will NOT operate in Dual Mode."<<endl;
                                     dout << "   Alternate Sub: "<<dualBus->getPaoName()<<" does not have a Volt Point ID attached."<< endl;
                                }
                            }
                        }
                    }
                }
            }

        }
        {
            static const string sqlNoID =  "SELECT SSB.substationbusid, PTU.decimalplaces "
                                           "FROM pointunit PTU, capcontrolsubstationbus SSB "
                                           "WHERE SSB.currentvarloadpointid = PTU.pointid";

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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentSubBusId;
                long tempDecimalPlaces;
                rdr["substationbusid"] >> currentSubBusId;

                CtiCCSubstationBusPtr currentCCSubstationBus = paobject_subbus_map->find(currentSubBusId)->second;
                if (currentCCSubstationBus != NULL)
                {
                    rdr["decimalplaces"] >> tempDecimalPlaces;
                    currentCCSubstationBus->setDecimalPlaces(tempDecimalPlaces);
                }
            }
        }
        {
            static const string sqlNoID =  "SELECT DSB.substationbusid, DSB.currentvarpointvalue, "
                                               "DSB.currentwattpointvalue, DSB.nextchecktime, DSB.newpointdatareceivedflag, "
                                               "DSB.busupdatedflag, DSB.lastcurrentvarupdatetime, DSB.estimatedvarpointvalue, "
                                               "DSB.currentdailyoperations, DSB.peaktimeflag, DSB.recentlycontrolledflag, "
                                               "DSB.lastoperationtime, DSB.varvaluebeforecontrol, DSB.lastfeederpaoid, "
                                               "DSB.lastfeederposition, DSB.ctitimestamp, DSB.powerfactorvalue, "
                                               "DSB.kvarsolution, DSB.estimatedpfvalue, DSB.currentvarpointquality, "
                                               "DSB.waivecontrolflag, DSB.additionalflags, DSB.currverifycbid, "
                                               "DSB.currverifyfeederid, DSB.currverifycborigstate, DSB.verificationstrategy, "
                                               "DSB.cbinactivitytime, DSB.currentvoltpointvalue, DSB.switchPointStatus, "
                                               "DSB.altSubControlValue, DSB.eventSeq, DSB.currentwattpointquality, "
                                               "DSB.currentvoltpointquality, DSB.ivcontroltot, DSB.ivcount, DSB.iwcontroltot, "
                                               "DSB.iwcount, DSB.phaseavalue, DSB.phasebvalue, DSB.phasecvalue, "
                                               "DSB.lastwattpointtime, DSB.lastvoltpointtime, DSB.phaseavaluebeforecontrol, "
                                               "DSB.phasebvaluebeforecontrol, DSB.phasecvaluebeforecontrol "
                                           "FROM capcontrolsubstationbus CSB, dynamicccsubstationbus DSB "
                                           "WHERE CSB.substationbusid = DSB.substationbusid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( subBusId > 0 )
            {
                static const string sqlID = string(sqlNoID + " AND CSB.substationbusid = ?");
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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentSubBusId;

                rdr["substationbusid"] >> currentSubBusId;
                CtiCCSubstationBusPtr currentCCSubstationBus = paobject_subbus_map->find(currentSubBusId)->second;

                if (currentCCSubstationBus->getPaoId() == currentSubBusId)
                {
                     currentCCSubstationBus->setDynamicData(rdr);
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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentSubBusId;

                rdr["paobjectid"] >> currentSubBusId;
                CtiCCSubstationBusPtr currentCCSubstationBus = paobject_subbus_map->find(currentSubBusId)->second;

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
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Undefined Substation Bus point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                    else if ( !(resolvePointType(tempPointType) == StatusPointType && tempPointOffset == -1)) //tag point = status with -1 offset
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Undefined Substation Bus point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                }
            }
        }

        if (subBusId > 0) //incase feeders/capbanks were moved?????  need to check this $#*! out.
        {
            reloadOperationStatsFromDatabase(subBusId,&_paobject_capbank_map, &_paobject_feeder_map, paobject_subbus_map,
                                             &_paobject_substation_map, &_paobject_area_map, &_paobject_specialarea_map);

        }
        //_reregisterforpoints = true;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
    long strategyID = _strategyManager.getDefaultId();   // default NoStrategy

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
        long strategyID = _strategyManager.getDefaultId();

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

        if (bus->getStrategy()->getUnitType() == ControlStrategy::None)
        {
            cascadeAreaStrategySettings(findAreaByPAObjectID(currentStation->getParentId()));
        }
        if (ciStringEqual(bus->getStrategy()->getControlMethod(),ControlStrategy::TimeOfDayControlMethod) )
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
    CtiCCFeederPtr feederToUpdate = NULL;

    if (feederId > 0)
    {
        feederToUpdate = findFeederByPAObjectID(feederId);
    }

    try
    {
        if (feederToUpdate != NULL)
        {
            CtiCCSubstationBusPtr subBus = NULL;
            deleteFeeder(feederToUpdate->getPaoId());
        }

        CtiTime currentDateTime;
        {
            static const string sqlNoID =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                               "YP.description, YP.disableflag, CCF.currentvarloadpointid, "
                                               "CCF.currentwattloadpointid, CCF.maplocationid, CCF.currentvoltloadpointid, "
                                               "CCF.multiMonitorControl, CCF.usephasedata, CCF.phaseb, CCF.phasec, "
                                               "CCF.controlflag "
                                           "FROM yukonpaobject YP, capcontrolfeeder CCF "
                                           "WHERE YP.paobjectid = CCF.feederid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( feederId > 0 )
            {
                static const string sqlID = string(sqlNoID + " AND YP.paobjectid = ?");
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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            CtiCCFeederPtr oldCCFeeder, currentCCFeeder;
            CtiCCSubstationBusPtr oldFeederParentSub = NULL;
            while ( rdr() )
            {
                currentCCFeeder = CtiCCFeederPtr(new CtiCCFeeder(rdr, &_strategyManager));

                paobject_feeder_map->insert(make_pair(currentCCFeeder->getPaoId(),currentCCFeeder));

                if (currentCCFeeder->getCurrentVarLoadPointId() > 0 )
                {
                    pointid_feeder_map->insert(make_pair(currentCCFeeder->getCurrentVarLoadPointId(), currentCCFeeder));
                    currentCCFeeder->addPointId(currentCCFeeder->getCurrentVarLoadPointId());
                }
                if (currentCCFeeder->getCurrentWattLoadPointId() > 0)
                {
                    pointid_feeder_map->insert(make_pair(currentCCFeeder->getCurrentWattLoadPointId(), currentCCFeeder));
                    currentCCFeeder->addPointId(currentCCFeeder->getCurrentWattLoadPointId());
                }
                if (currentCCFeeder->getCurrentVoltLoadPointId() > 0)
                {
                    pointid_feeder_map->insert(make_pair(currentCCFeeder->getCurrentVoltLoadPointId(), currentCCFeeder));
                    currentCCFeeder->addPointId(currentCCFeeder->getCurrentVoltLoadPointId());
                }
                if (currentCCFeeder->getUsePhaseData())
                {
                    if (currentCCFeeder->getPhaseBId() > 0)
                    {
                        pointid_feeder_map->insert(make_pair(currentCCFeeder->getPhaseBId(), currentCCFeeder));
                        currentCCFeeder->addPointId(currentCCFeeder->getPhaseBId());
                    }
                    if (currentCCFeeder->getPhaseCId() > 0)
                    {
                        pointid_feeder_map->insert(make_pair(currentCCFeeder->getPhaseCId(), currentCCFeeder));
                        currentCCFeeder->addPointId(currentCCFeeder->getPhaseCId());
                    }
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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
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
                currentCCFeeder = paobject_feeder_map->find(currentFeederId)->second;
                currentCCFeeder->setParentId(currentSubBusId);
                currentCCFeeder->setDisplayOrder(displayOrder);
                CtiCCSubstationBusPtr currentCCSubstationBus = NULL;

                if (feederId > 0)
                    currentCCSubstationBus = findSubBusByPAObjectID(currentSubBusId);
                else
                {
                    if (paobject_subbus_map->find(currentSubBusId) != paobject_subbus_map->end())
                        currentCCSubstationBus = paobject_subbus_map->find(currentSubBusId)->second;
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
                         if (paobject_feeder_map->find(feedId) != paobject_feeder_map->end())
                             currentCCFeeder = paobject_feeder_map->find(feedId)->second;
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
                 string loggedSQLstring = rdr.asString();
                 {
                     CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - " << loggedSQLstring << endl;
                 }
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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
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

            CtiCCFeederPtr currentFeeder = paobject_feeder_map->find(feederId)->second;
            if (currentFeeder != NULL)
            {
                reloadMonitorPointsFromDatabase(currentFeeder->getParentId(), &_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map, &_pointid_capbank_map, &_pointid_subbus_map);
            }
        }
        {
            static const string sqlNoID =  "SELECT CCF.feederid, PTU.decimalplaces "
                                           "FROM pointunit PTU, capcontrolfeeder CCF "
                                           "WHERE CCF.currentvarloadpointid = PTU.pointid";

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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentFeederId;
                long tempDecimalPlaces;
                rdr["feederid"] >> currentFeederId;

                CtiCCFeederPtr currentFeeder = paobject_feeder_map->find(currentFeederId)->second;
                rdr["decimalplaces"] >> tempDecimalPlaces;
                currentFeeder->setDecimalPlaces(tempDecimalPlaces);
            }


        }
        {
            static const string sqlNoID =  "SELECT DCF.feederid, DCF.currentvarpointvalue, DCF.currentwattpointvalue, "
                                               "DCF.newpointdatareceivedflag, DCF.lastcurrentvarupdatetime, "
                                               "DCF.estimatedvarpointvalue, DCF.currentdailyoperations, "
                                               "DCF.recentlycontrolledflag, DCF.lastoperationtime, DCF.varvaluebeforecontrol, "
                                               "DCF.lastcapbankdeviceid, DCF.busoptimizedvarcategory, "
                                               "DCF.busoptimizedvaroffset, DCF.ctitimestamp, DCF.powerfactorvalue, "
                                               "DCF.kvarsolution, DCF.estimatedpfvalue, DCF.currentvarpointquality, "
                                               "DCF.waivecontrolflag, DCF.additionalflags, DCF.currentvoltpointvalue, "
                                               "DCF.eventSeq, DCF.currverifycbid, DCF.currverifycborigstate, "
                                               "DCF.currentwattpointquality, DCF.currentvoltpointquality, DCF.ivcontroltot, "
                                               "DCF.ivcount, DCF.iwcontroltot, DCF.iwcount, DCF.phaseavalue, DCF.phasebvalue, "
                                               "DCF.phasecvalue, DCF.lastwattpointtime, DCF.lastvoltpointtime, DCF.retryindex, "
                                               "DCF.phaseavaluebeforecontrol, DCF.phasebvaluebeforecontrol, "
                                               "DCF.phasecvaluebeforecontrol, DOP.originalparentid, "
                                               "DOP.originalswitchingorder, DOP.originalcloseorder, DOP.originaltriporder "
                                           "FROM dynamicccfeeder DCF, capcontrolfeeder CCF, dynamicccoriginalparent DOP "
                                           "WHERE CCF.feederid = DCF.feederid AND CCF.feederid = DOP.paobjectid";

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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentCCFeederId;
                rdr["feederid"] >> currentCCFeederId;
                if (currentCCFeederId != NULL)
                {
                    CtiCCFeederPtr currentCCFeeder = paobject_feeder_map->find(currentCCFeederId)->second;
                    currentCCFeeder->setDynamicData(rdr);
                    if(feederId > 0 )
                    {
                        CtiCCSubstationBusPtr myTempBus = findSubBusByPAObjectID(currentCCFeeder->getParentId());
                        if(myTempBus != NULL)
                        {
                            currentCCFeeder->figureAndSetTargetVarValue(myTempBus->getStrategy()->getControlMethod(), myTempBus->getStrategy()->getControlUnits(), myTempBus->getPeakTimeFlag());
                        }

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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentCCFeederId;

                rdr["paobjectid"] >> currentCCFeederId;
                CtiCCFeederPtr currentCCFeeder = paobject_feeder_map->find(currentCCFeederId)->second;

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
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Undefined Feeder point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                        }
                        else if ( !(resolvePointType(tempPointType) == StatusPointType && tempPointOffset == -1)) //tag point = status with -1 offset
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Undefined Feeder point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                }
            }
        }
        if (feederId > 0)
        {
            reloadOperationStatsFromDatabase(feederId,&_paobject_capbank_map, paobject_feeder_map, &_paobject_subbus_map,
                                             &_paobject_substation_map, &_paobject_area_map, &_paobject_specialarea_map);

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
    CtiCCCapBankPtr capBankToUpdate = NULL;
    long monPointId = 0;

    if (capBankId > 0)
    {
        deleteCapBank(capBankId);
    }

    try
    {
        CtiTime currentDateTime;
        {
            static const string sqlNoID =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                               "YP.description, YP.disableflag, CB.operationalstate, CB.controllertype, "
                                               "CB.controldeviceid, CB.controlpointid, CB.banksize, CB.typeofswitch, "
                                               "CB.switchmanufacture, CB.maplocationid, CB.reclosedelay, CB.maxdailyops, "
                                               "CB.maxopdisable "
                                           "FROM yukonpaobject YP, capbank CB "
                                           "WHERE YP.paobjectid = CB.deviceid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(capBankId > 0)
            {
                static const string sqlID = string(sqlNoID + " AND YP.paobjectid = ?");
                rdr.setCommandText(sqlID);
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                CtiCCCapBankPtr currentCCCapBank = CtiCCCapBankPtr(new CtiCCCapBank(rdr));

                paobject_capbank_map->insert(make_pair(currentCCCapBank->getPaoId(),currentCCCapBank));
            }
        }
        {
            static const string sqlNoID =  "SELECT DV.deviceid, DV.alarminhibit, DV.controlinhibit "
                                           "FROM device DV, capbank CB "
                                           "WHERE DV.deviceid = CB.deviceid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(capBankId > 0)
            {
                static const string sqlID = string(sqlNoID + " AND DV.deviceid = ?");
                rdr.setCommandText(sqlID);
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long deviceid;
                string tempBoolString;

                rdr["deviceid"] >> deviceid;
                CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(deviceid)->second;

                rdr["alarminhibit"] >> tempBoolString;
                std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), ::tolower);

                currentCCCapBank->setAlarmInhibitFlag(tempBoolString=="y");

                rdr["controlinhibit"] >> tempBoolString;
                std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), ::tolower);

                currentCCCapBank->setControlInhibitFlag(tempBoolString=="y");
            }
        }


        {
            static const string sqlNoID =  "SELECT CB.deviceid, CB.controldeviceid, YP.type "
                                           "FROM capbank CB, yukonpaobject YP "
                                           "WHERE YP.paobjectid = CB.controldeviceid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(capBankId > 0)
            {
                static const string sqlID = string(sqlNoID + " AND CB.deviceid = ?");
                rdr.setCommandText(sqlID);
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentCapBankId;
                long controlDeviceId;
                string controlDeviceType;

                rdr["deviceid"] >> currentCapBankId;
                rdr["controldeviceid"] >> controlDeviceId;
                rdr["type"] >> controlDeviceType;
                CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(currentCapBankId)->second;

                if (currentCCCapBank->getControlDeviceId() == controlDeviceId)
                {
                    currentCCCapBank->setControlDeviceType(controlDeviceType);

                    cbc_capbank_map->insert(make_pair(controlDeviceId,currentCapBankId));
                }
            }
        }


        {
            static const string sqlNoID =  "SELECT FBL.deviceid, FBL.feederid, FBL.controlorder, FBL.closeorder, "
                                             "FBL.triporder "
                                           "FROM capbank CB, ccfeederbanklist FBL "
                                           "WHERE FBL.deviceid = CB.deviceid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(capBankId > 0)
            {
                static const string sqlID = string(sqlNoID + " AND CB.deviceid = ?");
                rdr.setCommandText(sqlID);
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            long subbusid;
            long tempFeederId;

            while ( rdr() )
            {
                long deviceid;
                long feederid;
                float controlOrder;
                float tripOrder;
                float closeOrder;

                rdr["deviceid"] >> deviceid;
                rdr["feederid"] >> feederid;
                rdr["controlorder"] >> controlOrder;
                rdr["closeorder"] >> closeOrder;
                rdr["triporder"] >> tripOrder;

                CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(deviceid)->second;
                currentCCCapBank->setControlOrder(controlOrder);
                currentCCCapBank->setTripOrder(tripOrder);
                currentCCCapBank->setCloseOrder(closeOrder);
                currentCCCapBank->setParentId(feederid);
                CtiCCFeederPtr currentCCFeeder = paobject_feeder_map->find(feederid)->second;

                if (!ciStringEqual(currentCCCapBank->getOperationalState(),CtiCCCapBank::UninstalledState))
                {
                    currentCCFeeder->getCCCapBanks().insert(currentCCCapBank);
                    capbank_feeder_map->insert(make_pair(deviceid,feederid));
                    if ( feeder_subbus_map->find(feederid) != feeder_subbus_map->end() )
                    {
                        subbusid = feeder_subbus_map->find(feederid)->second;
                        capbank_subbus_map->insert(make_pair(deviceid, subbusid));
                    }
                }
            }
        }
        {
            static const string sqlNoID =  "SELECT DCP.capbankid, DCP.controlstatus, DCP.totaloperations, "
                                               "DCP.laststatuschangetime, DCP.tagscontrolstatus, DCP.ctitimestamp, "
                                               "DCP.assumedstartverificationstatus, DCP.prevverificationcontrolstatus, "
                                               "DCP.verificationcontrolindex, DCP.additionalflags, DCP.currentdailyoperations, "
                                               "DCP.twowaycbcstate, DCP.twowaycbcstatetime, DCP.beforevar, DCP.aftervar, "
                                               "DCP.changevar, DCP.twowaycbclastcontrol, DCP.partialphaseinfo, "
                                               "DOP.originalparentid, DOP.originalswitchingorder, DOP.originalcloseorder, "
                                               "DOP.originaltriporder "
                                           "FROM dynamiccccapbank DCP, capbank CB, dynamicccoriginalparent DOP "
                                           "WHERE CB.deviceid = DCP.capbankid AND CB.deviceid = DOP.paobjectid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(capBankId > 0)
            {
                static const string sqlID = string(sqlNoID + " AND CB.deviceid = ?");
                rdr.setCommandText(sqlID);
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while (rdr())
            {
                long currentCCCapBankId;
                rdr["capbankid"] >> currentCCCapBankId;
                CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(currentCCCapBankId)->second;

                currentCCCapBank->setDynamicData(rdr);
            }
        }
        {
            static const string sqlNoID =  "SELECT PT.paobjectid, PT.pointid, PT.pointoffset, PT.pointtype "
                                           "FROM point PT, capbank CB "
                                           "WHERE CB.deviceid = PT.paobjectid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(capBankId > 0)
            {
                static const string sqlID = string(sqlNoID + " AND CB.deviceid = ?");
                rdr.setCommandText(sqlID);
                rdr << capBankId;
            }
            else
            {
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentCCCapBankId;
                rdr["paobjectid"] >> currentCCCapBankId;

                CtiCCCapBankPtr currentCCCapBank = NULL;

                if (paobject_capbank_map->find(currentCCCapBankId) != paobject_capbank_map->end())
                    currentCCCapBank = paobject_capbank_map->find(currentCCCapBankId)->second;

                if ( currentCCCapBank != NULL )
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
                            currentCCCapBank->setDisabledStatePointId(tempPointId, capBankId);
                            pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                            currentCCCapBank->addPointId(tempPointId);
                        }
                        else if ( resolvePointType(tempPointType) == StatusPointType &&
                                  tempPointOffset == Cti::CapControl::Offset_CapbankControlStatus )
                        {
                            currentCCCapBank->setStatusPointId(tempPointId);
                            pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                            currentCCCapBank->addPointId(tempPointId);
                        }
                        else if ( resolvePointType(tempPointType) == AnalogPointType &&
                                  tempPointOffset == Cti::CapControl::Offset_CapbankOperationAnalog )
                        {
                            currentCCCapBank->setOperationAnalogPointId(tempPointId);
                            pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                            currentCCCapBank->addPointId(tempPointId);
                        }
                        else if ( resolvePointType(tempPointType) == AnalogPointType &&
                                  tempPointOffset >= Cti::CapControl::Offset_OperationSuccessPercentRangeMin &&
                                  tempPointOffset <= Cti::CapControl::Offset_OperationSuccessPercentRangeMax )
                        {
                            if (currentCCCapBank->getOperationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                currentCCCapBank->addPointId(tempPointId);
                                pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                            }
                        }
                        else if ( tempPointOffset >= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMin &&
                                  tempPointOffset <= Cti::CapControl::Offset_ConfirmationSuccessPercentRangeMax )
                        {
                            if (currentCCCapBank->getConfirmationStats().setSuccessPercentPointId(tempPointId, tempPointOffset))
                            {
                                currentCCCapBank->addPointId(tempPointId);
                                pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                            }
                        }
                        else if ( !(resolvePointType(tempPointType) == StatusPointType && tempPointOffset == -1)) //tag point = status with -1 offset
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Undefined Cap Bank point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                }
            }
        }
        //load points off of cbc 70xx devices (two way device points)
        {
            static const string sql = "SELECT PT.paobjectid, PT.pointid, PT.pointoffset, PT.pointtype "
                                      "FROM point PT, capbank CB "
                                      "WHERE CB.controldeviceid = PT.paobjectid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(capBankId > 0)
            {
                static const string sqlID = string(sql + " AND CB.deviceid = ?");
                rdr.setCommandText(sqlID);
                rdr << capBankId;
            }
            else
            {
                static const string sqlNoID = string(sql + " AND CB.controldeviceid != 0");
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentCbcId;
                long currentCapBankId;
                rdr["paobjectid"] >> currentCbcId;

                CtiCCCapBankPtr currentCCCapBank = NULL;

                if (cbc_capbank_map->find(currentCbcId) != cbc_capbank_map->end())
                    currentCapBankId = cbc_capbank_map->find(currentCbcId)->second;
                if (paobject_capbank_map->find(currentCapBankId) != paobject_capbank_map->end())
                    currentCCCapBank = paobject_capbank_map->find(currentCapBankId)->second;


                if ( currentCCCapBank != NULL )
                {
                    if (currentCCCapBank->isControlDeviceTwoWay())
                    {
                        if ( !rdr["pointid"].isNull() )
                        {
                            long tempPointId = -1000;
                            long tempPointOffset = -1000;
                            string tempPointType = "(none)";
                            rdr["pointid"] >> tempPointId;
                            rdr["pointoffset"] >> tempPointOffset;
                            rdr["pointtype"] >> tempPointType;

                            CtiCCTwoWayPoints* twoWayPts = (CtiCCTwoWayPoints*)currentCCCapBank->getTwoWayPoints();

                            CtiPointType_t pointType = resolvePointType(tempPointType);
                            if (pointType == StatusPointType ||
                                pointType == AnalogPointType ||
                                pointType == PulseAccumulatorPointType)
                            {
                                if (twoWayPts->setTwoWayPointId(pointType, tempPointOffset, tempPointId) )
                                {
                                    currentCCCapBank->addPointId(tempPointId);
                                    pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                                }
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Undefined Cap Bank point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                        }
                    }
                }
            }
        }
        //load dynamiccctwowaycbc (two way device points)
        {
            static const string sql = "SELECT DTW.deviceid, DTW.recloseblocked, DTW.controlmode, DTW.autovoltcontrol, "
                                          "DTW.lastcontrol, DTW.condition, DTW.opfailedneutralcurrent, "
                                          "DTW.neutralcurrentfault, DTW.badrelay, DTW.dailymaxops, "
                                          "DTW.voltagedeltaabnormal, DTW.tempalarm, DTW.dstactive, DTW.neutrallockout, "
                                          "DTW.ignoredindicator, DTW.voltage, DTW.highvoltage, DTW.lowvoltage, "
                                          "DTW.deltavoltage, DTW.analoginputone, DTW.temp, DTW.rssi, DTW.ignoredreason, "
                                          "DTW.totalopcount, DTW.uvopcount, DTW.ovopcount, DTW.ovuvcountresetdate, "
                                          "DTW.uvsetpoint, DTW.ovsetpoint, DTW.ovuvtracktime, DTW.lastovuvdatetime, "
                                          "DTW.neutralcurrentsensor, DTW.neutralcurrentalarmsetpoint, DTW.ipaddress, "
                                          "DTW.udpport "
                                      "FROM dynamiccctwowaycbc DTW, capbank CB "
                                      "WHERE CB.controldeviceid = DTW.deviceid";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(capBankId > 0)
            {
                static const string sqlID = string(sql + " AND CB.deviceid = ?");
                rdr.setCommandText(sqlID);
                rdr << capBankId;
            }
            else
            {
                static const string sqlNoID =  string(sql + " AND CB.controldeviceid != 0");
                rdr.setCommandText(sqlNoID);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }
            while ( rdr() )
            {
                long currentCbcId;
                long currentCapBankId;
                rdr["deviceid"] >> currentCbcId;

                CtiCCCapBankPtr currentCCCapBank = NULL;

                if (cbc_capbank_map->find(currentCbcId) != cbc_capbank_map->end())
                    currentCapBankId = cbc_capbank_map->find(currentCbcId)->second;
                if (paobject_capbank_map->find(currentCapBankId) != paobject_capbank_map->end())
                    currentCCCapBank = paobject_capbank_map->find(currentCapBankId)->second;


                if ( currentCCCapBank != NULL )
                {
                    if (stringContainsIgnoreCase(currentCCCapBank->getControlDeviceType(), "CBC 702"))
                    {
                        ((CtiCCTwoWayPoints*)currentCCCapBank->getTwoWayPoints())->setPAOId(currentCbcId);
                        ((CtiCCTwoWayPoints*)currentCCCapBank->getTwoWayPoints())->setDynamicData(rdr, currentCCCapBank->getReportedCBCStateTime());
                    }
                }
            }
        }
        if (capBankId > 0)
        {
            reloadOperationStatsFromDatabase(capBankId, paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map,
                                             &_paobject_substation_map, &_paobject_area_map, &_paobject_specialarea_map);
        }
        //_reregisterforpoints = true;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
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
                if (paobject_capbank_map->find(currentPaoId) != paobject_capbank_map->end())
                    currentCCCapBank = paobject_capbank_map->find(currentPaoId)->second;
                else
                    currentCCCapBank = NULL;

                if (paobject_feeder_map->find(currentPaoId) != paobject_feeder_map->end())
                    currentFeeder = paobject_feeder_map->find(currentPaoId)->second;
                else
                    currentFeeder = NULL;

                if (paobject_subbus_map->find(currentPaoId) != paobject_subbus_map->end())
                    currentSubBus = paobject_subbus_map->find(currentPaoId)->second;
                else
                    currentSubBus = NULL;

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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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

        CtiTime currentDateTime;
        {
            //LOADING OF MONITOR POINTS.
            static const string sqlCapBankNoID =  "SELECT YP.type, MB.deviceid, MB.pointid, MB.displayorder, MB.scannable, MB.ninavg, "
                                               "MB.upperbandwidth, MB.lowerbandwidth, MB.Phase, MB.OverrideStrategy, FS.substationbusid "
                                           "FROM ccmonitorbanklist MB, yukonpaobject YP, ccfeederbanklist FB, ccfeedersubassignment FS "
                                           "WHERE MB.deviceid = FB.deviceid "
                                           "AND FB.feederid = FS.feederid "
                                           "AND YP.paobjectid = MB.deviceid ";

             static const string sqlRegulatorNoID =  "SELECT YP.type, MB.deviceid, MB.pointid, MB.displayorder, MB.scannable, MB.ninavg, "
                                               "MB.upperbandwidth, MB.lowerbandwidth, MB.Phase, MB.OverrideStrategy, Z.substationbusid "
                                           "FROM ccmonitorbanklist MB, yukonpaobject YP, regulatortozonemapping RTZ, zone Z "
                                           "WHERE MB.deviceid = RTZ.regulatorid "
                                           "AND RTZ.zoneid = Z.zoneid "
                                           "AND YP.paobjectid = MB.deviceid ";

              static const string sqlAdditionalNoID =  "SELECT YP.type, MB.deviceid, MB.pointid, MB.displayorder, MB.scannable, MB.ninavg, "
                                               "MB.upperbandwidth, MB.lowerbandwidth, MB.Phase, MB.OverrideStrategy, Z.substationbusid "
                                           "FROM ccmonitorbanklist MB, yukonpaobject YP, pointtozonemapping PTZ, zone Z, point P "
                                           "WHERE MB.deviceid = YP.paobjectid "
                                           "AND PTZ.pointid = P.pointid "
                                           "AND P.paobjectid = YP.paobjectid "
                                           "AND PTZ.zoneid = Z.zoneid ";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(subBusId > 0)
            {
                static const string sqlUnionID = string(sqlCapBankNoID + " AND FS.substationbusid = ?") +
                                            string(" UNION " + sqlRegulatorNoID + " AND Z.substationbusid = ?") +
                                            string(" UNION " + sqlAdditionalNoID + " AND Z.substationbusid = ?");
                rdr.setCommandText(sqlUnionID);
                rdr << subBusId << subBusId << subBusId;
            }
            else
            {
                static const string sqlUnion = string(sqlCapBankNoID) + " UNION " + sqlRegulatorNoID + " UNION " + sqlAdditionalNoID;
                rdr.setCommandText(sqlUnion);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                string devType;
                long currentBankId, currentPointId, currentBusId;
                rdr["type"] >> devType;
                rdr["deviceid"] >> currentBankId;
                rdr["pointid"] >> currentPointId;
                rdr["substationbusid"] >> currentBusId;

                CtiCCMonitorPointPtr currentMonPoint = CtiCCMonitorPointPtr(new CtiCCMonitorPoint(rdr));
                if (stringContainsIgnoreCase(devType, "cap bank"))
                {
                    if (!loadCapBankMonitorPoint(currentMonPoint, requiredPointResponses, paobject_capbank_map, paobject_feeder_map, paobject_subbus_map, pointid_capbank_map))
                    {
                        currentMonPoint.reset();
                        continue;
                    }
                }
                //Get all banks on SubBus all Feeders.
                if (paobject_subbus_map->find(currentBusId) == paobject_subbus_map->end())
                {
                    continue;
                }

                CtiCCSubstationBusPtr subBusPtr = paobject_subbus_map->find(currentBusId)->second;
                if (!subBusPtr->addMonitorPoint(currentPointId, currentMonPoint))
                {
                    currentMonPoint.reset();
                    continue;
                }
                subBusPtr->addDefaultPointResponses( );
                pointid_subbus_map->insert(make_pair(currentMonPoint->getPointId(),subBusPtr));
                subBusPtr->addPointId(currentPointId);

            }
        }

        {
            static const string sqlCapBankNoID = "SELECT MBH.deviceid, MBH.pointid, MBH.value, MBH.datetime, MBH.scaninprogress, FS.substationbusid "
                                          "FROM dynamicccmonitorbankhistory MBH, yukonpaobject YP, ccfeederbanklist FB, ccfeedersubassignment FS "
                                           "WHERE MBH.deviceid = FB.deviceid "
                                           "AND FB.feederid = FS.feederid "
                                           "AND YP.paobjectid = MBH.deviceid ";

             static const string sqlRegulatorNoID =  "SELECT MBH.deviceid, MBH.pointid, MBH.value, MBH.datetime, MBH.scaninprogress, Z.substationbusid "
                                          "FROM dynamicccmonitorbankhistory MBH, yukonpaobject YP, regulatortozonemapping RTZ, zone Z "
                                           "WHERE MBH.deviceid = RTZ.regulatorid "
                                           "AND RTZ.zoneid = Z.zoneid "
                                           "AND YP.paobjectid = MBH.deviceid ";

              static const string sqlAdditionalNoID =  "SELECT MBH.deviceid, MBH.pointid, MBH.value, MBH.datetime, MBH.scaninprogress, Z.substationbusid  "
                                          "FROM dynamicccmonitorbankhistory MBH, yukonpaobject YP, pointtozonemapping PTZ, zone Z, point P "
                                           "WHERE MBH.deviceid = YP.paobjectid "
                                           "AND PTZ.pointid = P.pointid "
                                           "AND P.paobjectid = YP.paobjectid "
                                           "AND PTZ.zoneid = Z.zoneid ";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if(subBusId > 0)
            {
                static const string sqlUnionID = string(sqlCapBankNoID + " AND FS.substationbusid = ?") +
                                            string(" UNION " + sqlRegulatorNoID + " AND Z.substationbusid = ?") +
                                            string(" UNION " + sqlAdditionalNoID + " AND Z.substationbusid = ?");
                rdr.setCommandText(sqlUnionID);
                rdr << subBusId << subBusId << subBusId;
            }
            else
            {
                static const string sqlUnion = string(sqlCapBankNoID) + " UNION " + sqlRegulatorNoID + " UNION " + sqlAdditionalNoID;
                rdr.setCommandText(sqlUnion);
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long currentDevId, currentPointId, currentBusId;
                float value;
                rdr["deviceid"] >> currentDevId;
                rdr["pointid"] >> currentPointId;
                rdr["substationbusid"] >> currentBusId;

                if ( paobject_subbus_map->find(currentBusId) ==  paobject_subbus_map->end())
                {
                    continue;
                }
                CtiCCSubstationBusPtr bus = paobject_subbus_map->find(currentBusId)->second;
                CtiCCMonitorPointPtr currentMonPoint = bus->getMonitorPoint(currentPointId);
                if (currentMonPoint != NULL)
                    currentMonPoint->setDynamicData(rdr);

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

                PaoIdToCapBankMap::const_iterator bank_itr = paobject_capbank_map->find(pointResponse.getDeviceId());
                if (bank_itr != paobject_capbank_map->end())
                {
                    CtiCCCapBankPtr bank = bank_itr->second;
                    bank->addPointResponse(pointResponse);
                }

                if ( paobject_subbus_map->find(pointResponse.getSubBusId()) ==  paobject_subbus_map->end())
                {
                    continue;
                }
                CtiCCSubstationBusPtr bus = paobject_subbus_map->find(pointResponse.getSubBusId())->second;
                bus->updatePointResponse(PointResponseKey(pointResponse.getDeviceId(),pointResponse.getPointId()), PointResponsePtr(new PointResponse(pointResponse)));

            }
            for each ( std::pair<long, int>  thePair in requiredPointResponses )
            {
                long busId = findSubBusIDbyCapBankID(thePair.second);
                if (busId == NULL)
                {
                    continue;
                }

                PointResponse defaultPointResponse(thePair.first, thePair.second, 0, _IVVC_DEFAULT_DELTA, false, busId);
                if (paobject_capbank_map->find(thePair.second) != paobject_capbank_map->end())
                {
                    CtiCCCapBankPtr bank = paobject_capbank_map->find(thePair.second)->second;
                    bank->addPointResponse(defaultPointResponse);
                }
            }


        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}



bool CtiCCSubstationBusStore::loadCapBankMonitorPoint(CtiCCMonitorPointPtr currentMonPoint, std::set< std::pair<long, int> >  &requiredPointResponses,
                                                        PaoIdToCapBankMap *paobject_capbank_map,
                                                        PaoIdToFeederMap *paobject_feeder_map,
                                                        PaoIdToSubBusMap *paobject_subbus_map,
                                                        PointIdToCapBankMultiMap *pointid_capbank_map)
{
    if (paobject_capbank_map->find(currentMonPoint->getDeviceId()) == paobject_capbank_map->end())
    {
        return false;
    }
    CtiCCCapBankPtr currentBankPtr = paobject_capbank_map->find(currentMonPoint->getDeviceId())->second;

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

    if (paobject_feeder_map->find(currentBankPtr->getParentId()) == paobject_feeder_map->end())
    {
        return false;
    }
    CtiCCFeederPtr feederPtr = paobject_feeder_map->find(currentBankPtr->getParentId())->second;

    if(ciStringEqual(feederPtr->getStrategy()->getControlMethod(),ControlStrategy::SubstationBusControlMethod))
    {
        //Get all banks on SubBus all Feeders.
        if (paobject_subbus_map->find(feederPtr->getParentId()) == paobject_subbus_map->end())
        {
            return false;
        }
        CtiCCSubstationBusPtr subBusPtr = paobject_subbus_map->find(feederPtr->getParentId())->second;
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
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
        {
            if ( _ccCapBankStates->size() > 0 )
            {
                delete_container(*_ccCapBankStates);
                _ccCapBankStates->clear();
                if (_ccCapBankStates->size() > 0)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " _ccCapBankStates did NOT get destroyed " << endl;
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
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << loggedSQLstring << endl;
                    }
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
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << loggedSQLstring << endl;
                    }
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
                    string loggedSQLstring = rdr.asString();
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << loggedSQLstring << endl;
                    }
                }

                CtiString str;
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

                    if (stringContainsIgnoreCase(str, "fdr"))
                    {
                        str = str.strip(CtiString::leading, 'f');
                        str = str.strip(CtiString::leading, 'd');
                        str = str.strip(CtiString::leading, 'r');
                    }
                    if (stringContainsIgnoreCase(translation, str))
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " FDR Link Status POINT FOUND: "<<pointID << endl;
                        }
                        _linkStatusPointId = pointID;
                        _linkStatusFlag = OPENED;
                        break;
                    }
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCSubstationBusStore::reloadMapOfBanksToControlByLikeDay(long subbusId, long feederId,
                                  std::map <long, long> *controlid_action_map,
                                  CtiTime &lastSendTime, int fallBackConstant)
{
    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
        {
            CtiTime timeNow = CtiTime();

            static const string sqlSub =   "SELECT CEL.pointid, CEL.datetime, CEL.subid, CEL.feederid, CEL.eventtype, "
                                             "CEL.value, CEL.text "
                                           "FROM cceventlog CEL "
                                           "WHERE (CEL.text LIKE 'Open sent%' OR CEL.text LIKE 'Close sent%') AND "
                                             "(CEL.datetime > ? AND CEL.datetime <= ?) AND CEL.subid = ? "
                                           "ORDER BY CEL.datetime ASC";

            static const string sqlFeeder =   "SELECT CEL.pointid, CEL.datetime, CEL.subid, CEL.feederid, CEL.eventtype, "
                                                 "CEL.value, CEL.text "
                                              "FROM cceventlog CEL "
                                              "WHERE (CEL.text LIKE 'Open sent%' OR CEL.text LIKE 'Close sent%') AND "
                                                 "(CEL.datetime > ? AND CEL.datetime <= ?) AND CEL.feederid = ? "
                                              "ORDER BY CEL.datetime ASC";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            if( subbusId != 0)
            {
                rdr.setCommandText(sqlSub);
                rdr << (lastSendTime - fallBackConstant)
                    << (timeNow - fallBackConstant)
                    << subbusId;
            }
            else
            {
                rdr.setCommandText(sqlFeeder);
                rdr << (lastSendTime - fallBackConstant)
                    << (timeNow - fallBackConstant)
                    << feederId;
            }

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                long pointId;
                CtiTime controlTime;
                long controlValue;

                rdr["pointid"] >> pointId;
                rdr["datetime"] >> controlTime;
                rdr["value"] >> controlValue;

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
                if (controlTime <= timeNow - fallBackConstant)
                {
                    controlid_action_map->insert(make_pair(pointId,controlValue));
                }
                lastSendTime = controlTime;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return;
}

void CtiCCSubstationBusStore::locateOrphans(PaoIdVector *orphanCaps, PaoIdVector *orphanFeeders, PaoIdToCapBankMap paobject_capbank_map,
                       PaoIdToFeederMap paobject_feeder_map, ChildToParentMap capbank_feeder_map, ChildToParentMap feeder_subbus_map)
{
    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

bool CtiCCSubstationBusStore::isFeederOrphan(long feederId)
{
   try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return false;
}
bool CtiCCSubstationBusStore::isCapBankOrphan(long capBankId)
{
    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
        {
            for each (long orphanId in _orphanedCapBanks)
            {
                if (orphanId == capBankId)
                {
                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " Cap " <<capBankId<<" is on Orphan list. "<< endl;
                    }
                    return true;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return false;
}
void CtiCCSubstationBusStore::removeFromOrphanList(long ccId)
{
    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
        _orphanedFeeders.erase(remove(_orphanedFeeders.begin(), _orphanedFeeders.end(), ccId), _orphanedFeeders.end());
        _orphanedCapBanks.erase(remove(_orphanedCapBanks.begin(), _orphanedCapBanks.end(), ccId), _orphanedCapBanks.end());
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCSubstationBusStore::deleteSubstation(long substationId)
{
    CtiCCSubstationPtr substationToDelete = findSubstationByPAObjectID(substationId);

    if (substationToDelete == NULL)
        return;
    try
    {
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
           for each (long pointid in *substationToDelete->getPointIds())
           {

                int ptCount = getNbrOfSubstationsWithPointID(pointid);
                if (ptCount > 1)
                {
                    PointIdToSubstationMultiMap::iterator iter1 = _pointid_station_map.lower_bound(pointid);
                    while (iter1 != _pointid_station_map.end() || iter1 != _pointid_station_map.upper_bound(pointid))
                    {
                       if (((CtiCCSubstationPtr)iter1->second)->getPaoId() == substationToDelete->getPaoId())
                       {
                           _pointid_station_map.erase(iter1);
                           break;
                       }
                       iter1++;
                    }
                }
                else
                    _pointid_station_map.erase(pointid);
           }
           substationToDelete->getPointIds()->clear();
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

        try
        {
            long areaId = substationToDelete->getParentId();
            if (areaId != NULL)
            {
                CtiCCAreaPtr area = findAreaByPAObjectID(areaId);
            }
            CtiCCSpArea_vec::iterator itrSp = _ccSpecialAreas->begin();
            while ( itrSp != _ccSpecialAreas->end() )
            {
                CtiCCSpecial *spArea = *itrSp;
                if (spArea != NULL)
                {
                    spArea->removeSubstationId(substationId);
                }
                itrSp++;
            }

            string substationName = substationToDelete->getPaoName();
            _paobject_substation_map.erase(substationToDelete->getPaoId());
            _substation_area_map.erase(substationToDelete->getPaoId());
            _substation_specialarea_map.erase(substationToDelete->getPaoId());
            CtiCCSubstation_vec::iterator itr = _ccSubstations->begin();
            for( ;itr != _ccSubstations->end(); itr++ )
            {
                CtiCCSubstation *substation = *itr;
                if (substation->getPaoId() == substationId)
                {
                    _ccSubstations->erase(itr);
                    break;
                }
            }
            if ( substationToDelete != NULL )
            {
                delete substationToDelete;
                substationToDelete = NULL;
            }
            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << "SUBSTATION: " << substationName <<" has been deleted." << endl;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}


void CtiCCSubstationBusStore::deleteArea(long areaId)
{
    CtiCCAreaPtr areaToDelete = findAreaByPAObjectID(areaId);

    try
    {
        if (areaToDelete != NULL)
        {
            try
            {
                //Delete pointids on this sub
                for each (long pointid in *areaToDelete->getPointIds())
                {
                    int ptCount = getNbrOfAreasWithPointID(pointid);
                    if (ptCount > 1)
                    {
                        PointIdToAreaMultiMap::iterator iter1 = _pointid_area_map.lower_bound(pointid);
                        while (iter1 != _pointid_area_map.end() || iter1 != _pointid_area_map.upper_bound(pointid))
                        {
                           if (((CtiCCAreaPtr)iter1->second)->getPaoId() == areaToDelete->getPaoId())
                           {
                               _pointid_area_map.erase(iter1);
                               break;
                           }
                           iter1++;
                        }
                    }
                    else
                        _pointid_area_map.erase(pointid);
               }
               areaToDelete->getPointIds()->clear();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            long stationId;
            long subBusId;

            CtiCCSubstationPtr station = NULL;
            for each (long stationId in areaToDelete->getSubstationIds())
            {
                station = findSubstationByPAObjectID(stationId);
                if (station != NULL)
                {
                    if (station->getSaEnabledId() > 0)
                    {
                        continue;
                    }
                    for each ( long subBusId in station->getCCSubIds() )
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
                string areaName = areaToDelete->getPaoName();
                _paobject_area_map.erase(areaToDelete->getPaoId());
                CtiCCArea_vec::iterator itr = _ccGeoAreas->begin();
                while ( itr != _ccGeoAreas->end() )
                {
                    CtiCCArea *area = *itr;
                    if (area->getPaoId() == areaId)
                    {
                        itr = _ccGeoAreas->erase(itr);
                        break;
                    }else
                        ++itr;
                }
                if ( areaToDelete != NULL )
                {
                    delete areaToDelete;
                    areaToDelete = NULL;
                }
                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << "AREA: " << areaName <<" has been deleted." << endl;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}


void CtiCCSubstationBusStore::deleteSpecialArea(long areaId)
{
    CtiCCSpecialPtr spAreaToDelete = findSpecialAreaByPAObjectID(areaId);

    try
    {
        if (spAreaToDelete != NULL)
        {

            try
            {
                //Delete pointids on this sub
                for each (long pointid in *spAreaToDelete->getPointIds())
                {
                    int ptCount = getNbrOfSpecialAreasWithPointID(pointid);
                    if (ptCount > 1)
                    {
                        PointIdToSpecialAreaMultiMap::iterator iter1 = _pointid_specialarea_map.lower_bound(pointid);
                        while (iter1 != _pointid_specialarea_map.end() || iter1 != _pointid_specialarea_map.upper_bound(pointid))
                        {
                           if (((CtiCCSpecialPtr)iter1->second)->getPaoId() == spAreaToDelete->getPaoId())
                           {
                               _pointid_specialarea_map.erase(iter1);
                               break;
                           }
                           iter1++;
                        }
                    }
                    else
                        _pointid_specialarea_map.erase(pointid);
               }
               spAreaToDelete->getPointIds()->clear();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                string areaName = spAreaToDelete->getPaoName();
                _paobject_specialarea_map.erase(spAreaToDelete->getPaoId());
                CtiCCSpArea_vec::iterator itr = _ccSpecialAreas->begin();
                while ( itr != _ccSpecialAreas->end() )
                {
                    CtiCCSpecial *area = *itr;
                    if (area->getPaoId() == areaId)
                    {
                        itr = _ccSpecialAreas->erase(itr);
                        break;
                    }else
                        ++itr;

                }
                if ( spAreaToDelete != NULL )
                {
                    delete spAreaToDelete;
                    spAreaToDelete = NULL;
                }
                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << "SPECIAL AREA: " << areaName <<" has been deleted." << endl;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

void CtiCCSubstationBusStore::deleteSubBus(long subBusId)
{
    CtiCCSubstationBusPtr subToDelete = findSubBusByPAObjectID(subBusId);

    if( subToDelete == NULL )
    {
        return;
    }

    try
    {
        for each(int feederId in subToDelete->getCCFeederIds())
        {
            deleteFeeder(feederId);
        }

        try
        {
            //Delete pointids on this sub
            for each (long pointid in *subToDelete->getPointIds())
            {
               int ptCount = getNbrOfSubBusesWithPointID(pointid);
                if (ptCount > 1)
                {
                    PointIdToSubBusMultiMap::iterator iter1 = _pointid_subbus_map.lower_bound(pointid);
                    while (iter1 != _pointid_subbus_map.end() || iter1 != _pointid_subbus_map.upper_bound(pointid))
                    {
                       if (((CtiCCSubstationBusPtr)iter1->second)->getPaoId() == subToDelete->getPaoId())
                       {
                           _pointid_subbus_map.erase(iter1);
                           break;
                       }
                       iter1++;
                    }
                }
                else
                    _pointid_subbus_map.erase(pointid);
           }
           subToDelete->getPointIds()->clear();
           for each(long pointid in subToDelete->getAllMonitorPointIds() )
           {
               _pointid_subbus_map.erase(pointid);
           }

           subToDelete->removeAllMonitorPoints();
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        //DUAL BUS Alt Sub deletion...
        try
        {
            //Deleting subs that have this sub as altSub
            while (_altsub_sub_idmap.find(subBusId) != _altsub_sub_idmap.end())
            {
                long tempSubId = _altsub_sub_idmap.find(subBusId)->second;
                CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(tempSubId);
                if (tempSub != NULL)
                {
                    if (ciStringEqual(tempSub->getStrategy()->getControlUnits(), ControlStrategy::KVarControlUnit))
                    {
                        long pointid = subToDelete->getCurrentVarLoadPointId();
                        _pointid_subbus_map.erase(pointid);
                        tempSub->removePointId(pointid);
                    }
                    else if (!ciStringEqual(tempSub->getStrategy()->getControlUnits(), ControlStrategy::VoltsControlUnit))
                    {
                        long pointid = subToDelete->getCurrentVoltLoadPointId();
                        _pointid_subbus_map.erase(pointid);
                        tempSub->removePointId(pointid);
                    }
                }
                _altsub_sub_idmap.erase(subBusId);
            }
            //Deleting this sub from altSubMap
            if (subToDelete->getAltDualSubId() != subToDelete->getPaoId())
            {
                PaoIdToPointIdMultiMap::iterator iter = _altsub_sub_idmap.begin();
                while (iter != _altsub_sub_idmap.end())
                {
                    if (iter->second == subToDelete->getPaoId())
                    {
                        _altsub_sub_idmap.erase(iter);
                        iter = _altsub_sub_idmap.end();
                    }
                    else
                        iter++;
                }

            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

        try
        {
            string subBusName = subToDelete->getPaoName();
            _paobject_subbus_map.erase(subToDelete->getPaoId());
            _subbus_substation_map.erase(subToDelete->getPaoId());
            CtiCCSubstationBus_vec::iterator itr = _ccSubstationBuses->begin();
            while ( itr != _ccSubstationBuses->end() )
            {
                CtiCCSubstationBus *subBus = *itr;
                if (subBus->getPaoId() == subBusId)
                {
                    itr = _ccSubstationBuses->erase(itr);
                    break;
                }else
                    ++itr;

            }
            if ( subToDelete != NULL )
            {
                delete subToDelete;
                subToDelete = NULL;
            }
            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << "SUBBUS: " << subBusName <<" has been deleted." << endl;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

void CtiCCSubstationBusStore::deleteFeeder(long feederId)
{
    CtiCCFeederPtr feederToDelete = findFeederByPAObjectID(feederId);

    if (feederToDelete == NULL)
        return;
    try
    {
        for each(int capBankId in feederToDelete->getAllCapBankIds())
        {
            deleteCapBank(capBankId);
        }

        //Delete pointids on this feeder
        for each (long pointid in *feederToDelete->getPointIds())
        {
            int ptCount = getNbrOfFeedersWithPointID(pointid);
            if (ptCount > 1)
            {
                PointIdToFeederMultiMap::iterator iter1 = _pointid_feeder_map.lower_bound(pointid);
                while (iter1 != _pointid_feeder_map.end() || iter1 != _pointid_feeder_map.upper_bound(pointid))
                {
                    if (((CtiCCFeederPtr)iter1->second)->getPaoId() == feederToDelete->getPaoId())
                    {
                        _pointid_feeder_map.erase(iter1);
                        break;
                    }
                    iter1++;
                }
            }
            else
                _pointid_feeder_map.erase(pointid);
        }
        feederToDelete->getPointIds()->clear();
        try
        {

            long subBusId = findSubBusIDbyFeederID(feederId);
            if (subBusId != NULL)
            {
                CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subBusId);
                if (subBus != NULL)
                {
                    subBus->deleteCCFeeder(feederId);
                }
            }

            string feederName = feederToDelete->getPaoName();
            _paobject_feeder_map.erase(feederToDelete->getPaoId());
            _feeder_subbus_map.erase(feederToDelete->getPaoId());

            if (feederToDelete != NULL)
            {
                delete feederToDelete;
                feederToDelete = NULL;
            }

            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << "FEEDER: " << feederName <<" has been deleted." << endl;
            }

        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCCSubstationBusStore::deleteCapBank(long capBankId)
{
    CtiCCCapBankPtr capBankToDelete = findCapBankByPAObjectID(capBankId);

    try
    {
        if (capBankToDelete != NULL)
        {
          //Delete pointids on this feeder
            for each (long pointid in *capBankToDelete->getPointIds())
            {
                int ptCount = getNbrOfCapBanksWithPointID(pointid);
                if (ptCount > 1)
                {
                    PointIdToCapBankMultiMap::iterator iter1 = _pointid_capbank_map.lower_bound(pointid);
                    while (iter1 != _pointid_capbank_map.end() || iter1 != _pointid_capbank_map.upper_bound(pointid))
                    {
                        if (((CtiCCCapBankPtr)iter1->second)->getPaoId() == capBankToDelete->getPaoId())
                        {
                            _pointid_capbank_map.erase(iter1);
                            break;
                        }
                        iter1++;
                    }
                }
                else
                    _pointid_capbank_map.erase(pointid);
            }
            capBankToDelete->getPointIds()->clear();
            for each (CtiCCMonitorPointPtr mPoint in capBankToDelete->getMonitorPoint())
            {
                _pointid_capbank_map.erase(mPoint->getPointId());
                mPoint.reset();
            }
            capBankToDelete->getMonitorPoint().clear();

            try
            {
                CtiCCFeederPtr feeder = NULL;
                long feederId = findFeederIDbyCapBankID(capBankId);
                if (feederId != NULL)
                {
                    feeder = findFeederByPAObjectID(feederId);
                    if (feeder != NULL)
                    {
                        feeder->deleteCCCapBank(capBankId);
                    }
                }

                string capBankName = capBankToDelete->getPaoName();
                _paobject_capbank_map.erase(capBankToDelete->getPaoId());
                _capbank_subbus_map.erase(capBankToDelete->getPaoId());
                _capbank_feeder_map.erase(capBankToDelete->getPaoId());
                _cbc_capbank_map.erase(capBankToDelete->getControlDeviceId());

                if (capBankToDelete != NULL)
                {
                    delete capBankToDelete;
                    capBankToDelete = NULL;
                }
                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << "CAPBANK: " << capBankName <<" has been deleted." << endl;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
          CtiLockGuard<CtiLogger> logger_guard(dout);
          dout << CtiTime() << " Reload Cap "<< reloadId<<" because of ChangeTypeUpdate message " << endl;
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
                 CtiLockGuard<CtiLogger> logger_guard(dout);
                 dout << CtiTime() << "  Update Cap NOT found on sub  "<< endl;
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
                      CtiLockGuard<CtiLogger> logger_guard(dout);
                      dout << CtiTime() << " Delete Cap "<<reloadId <<" was found on sub " << endl;
                      dout << CtiTime() << " Sub " <<tempSub->getPaoName()<<" modified "<< endl;
                 }
             }
         }
         else
         {
             if( _CC_DEBUG & CC_DEBUG_EXTENDED )
             {
                  CtiLockGuard<CtiLogger> logger_guard(dout);
                  dout << CtiTime() << " Delete Cap was NOT found on sub " << endl;
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
                           &_substation_specialarea_map, _ccSubstations);

        CtiCCSubstation *station = findSubstationByPAObjectID(reloadId);
        if (station != NULL)
        {
            std::copy( station->getCCSubIds().begin(), station->getCCSubIds().end(), std::inserter( modifiedBusIdsSet, modifiedBusIdsSet.end() ) );
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
        CtiCCSubstation *station = findSubstationByPAObjectID(stationId);
        if (station != NULL)
        {
            std::copy( station->getCCSubIds().begin(), station->getCCSubIds().end(), std::inserter( modifiedBusIdsSet, modifiedBusIdsSet.end() ) );
            msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
        }
    }
    std::copy( stationIdList.begin(), stationIdList.end(), std::inserter( modifiedStationIdsSet, modifiedStationIdsSet.end() ) );
    msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
    return;
}
void CtiCCSubstationBusStore::handleStrategyDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                                                 PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages )
{

    if (reloadAction != ChangeTypeDelete)
    {
        reloadStrategyFromDatabase(reloadId);

        long i=0;
        for(i=0;i<_ccSpecialAreas->size();i++)
        {
            CtiCCSpecialPtr spArea = (CtiCCSpecialPtr)(*_ccSpecialAreas)[i];
            if (!spArea->getDisableFlag() && spArea->getStrategy()->getStrategyId() == reloadId)
            {
                std::copy( spArea->getSubstationIds().begin(), spArea->getSubstationIds().end(), std::inserter( modifiedStationIdsSet, modifiedStationIdsSet.end() ) );
            }
        }
        for(i=0;i<_ccGeoAreas->size();i++)
        {
            CtiCCArea* area = (CtiCCAreaPtr)(*_ccGeoAreas)[i];
            if (!area->getDisableFlag() && area->getStrategy()->getStrategyId() == reloadId)
            {
                std::copy( area->getSubstationIds().begin(), area->getSubstationIds().end(), std::inserter( modifiedStationIdsSet, modifiedStationIdsSet.end() ) );
            }
        }
        for(i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* tempSub = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];
            if (tempSub->getStrategy()->getStrategyId() == reloadId)
            {
                modifiedBusIdsSet.insert(reloadId);
                msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
            }
            else
            {
                CtiFeeder_vec& tempFeeds = tempSub->getCCFeeders();
                for (int j = 0; j < tempFeeds.size(); j++)
                {
                    CtiCCFeederPtr fdr = (CtiCCFeederPtr)tempFeeds[j];
                    if (fdr->getStrategy()->getStrategyId() ==  reloadId)
                    {
                        modifiedBusIdsSet.insert(reloadId);
                        msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                        msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                    }
                }
            }
        }

    }
    else
    {
        _strategyManager.unload(reloadId);
    }
}

void CtiCCSubstationBusStore::registerForAdditionalPoints(PaoIdSet &modifiedBusIdsSet)
{
   try
   {
       PaoIdSet pointList;

       for(std::set<long>::iterator it = modifiedBusIdsSet.begin(); it != modifiedBusIdsSet.end();it++)
       {
           CtiCCSubstationBus* sub = findSubBusByPAObjectID(*it);
           if (sub == NULL)
           {
               continue;
           }
           sub->addAllSubPointsToMsg(pointList);
           CtiFeeder_vec& feeds = sub->getCCFeeders();
           for (long j = 0; j < feeds.size(); j++)
           {
               CtiCCFeederPtr feed = (CtiCCFeederPtr)feeds[j];
               feed->addAllFeederPointsToMsg(pointList);
               CtiCCCapBank_SVector& caps = feed->getCCCapBanks();
               for (long k = 0; k < caps.size(); k++)
               {
                   CtiCCCapBankPtr cap = (CtiCCCapBankPtr)caps[k];
                   cap->addAllCapBankPointsToMsg(pointList);
                   if ( cap->isControlDeviceTwoWay() )
                   {
                       CtiCCTwoWayPoints* twoWayPts = (CtiCCTwoWayPoints*)cap->getTwoWayPoints();
                       twoWayPts->addAllCBCPointsToRegMsg(pointList);
                   }
               }
           }
       }

       getPointDataHandler().getAllPointIds(pointList);
       CtiCapController::getInstance()->getDispatchConnection()->registerForPoints(CtiCapController::getInstance(),pointList);
   }
   catch(...)
   {
       CtiLockGuard<CtiLogger> logger_guard(dout);
       dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
            if (ciStringEqual(currentSubstationBus->getStrategy()->getControlMethod(),ControlStrategy::IndividualFeederControlMethod)  &&
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

    CtiMultiMsg_set::iterator it;

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
        CtiCCExecutorFactory::createExecutor(new CtiCCSubstationsMsg(*_ccSubstations, msgSubsBitMask))->execute();
    }
    else if (modifiedStationIdsSet.size() > 0 )
    {
        CtiCCSubstation_set modifiedStationSet;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

void CtiCCSubstationBusStore::checkDBReloadList()
{
    bool sendBusInfo = false;
    bool forceFullReload = false;
    CtiTime currentDateTime;

    CtiMultiMsg_set::iterator it;
    PaoIdSet modifiedBusIdsSet;
    PaoIdSet modifiedStationIdsSet;
    CtiMultiMsg_vec capMessages;
    unsigned long msgBitMask = 0x00000000;
    unsigned long msgSubsBitMask = 0x00000000;

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
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
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " DBChange " << endl;
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
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
                registerForAdditionalPoints(modifiedBusIdsSet);

                _lastindividualdbreloadtime = _lastindividualdbreloadtime.now();

                initializeAllPeakTimeFlagsAndMonitorPoints(false);

                createAndSendClientMessages(msgBitMask, msgSubsBitMask, modifiedBusIdsSet, modifiedStationIdsSet, capMessages);

                _wassubbusdeletedflag = false;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

void CtiCCSubstationBusStore::checkAndUpdateVoltReductionFlagsByBus(CtiCCSubstationBusPtr bus)
{

    CtiCCSubstation* currentStation = findSubstationByPAObjectID(bus->getParentId());
    if (currentStation != NULL)
    {
        currentStation->checkAndUpdateChildVoltReductionFlags();
        CtiCCArea* currentArea = findAreaByPAObjectID(currentStation->getParentId());
        if (currentArea != NULL)
        {
            currentArea->checkAndUpdateChildVoltReductionFlags();
        }
    }
}

void CtiCCSubstationBusStore::updateSubstationObjectSet(long substationId, CtiMultiMsg_set &modifiedStationsSet)
{
    CtiCCSubstationPtr station = findSubstationByPAObjectID(substationId);
    if (station != NULL)
    {
        modifiedStationsSet.insert(station);
    }

}

void CtiCCSubstationBusStore::updateAreaObjectSet(long areaId, CtiMultiMsg_set &modifiedAreasSet)
{
    CtiCCAreaPtr area = findAreaByPAObjectID(areaId);
    if (area != NULL)
    {
        modifiedAreasSet.insert(area);
    }

}


void CtiCCSubstationBusStore::sendUserQuit(void *who)
{
    string *strPtr = (string *) who;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << *strPtr << " has asked for shutdown."<< endl;
    }

    CtiCCExecutorFactory::createExecutor(new CtiCCShutdown())->execute();
}


void CtiCCSubstationBusStore::periodicComplain( void *la )
{
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << CtiTime( ) << " CapControl periodic thread is AWOL" << endl;
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if (!list_contains(_unsolicitedCapBanks, x))
    {
        _unsolicitedCapBanks.push_back(x);
    }
}

void CtiCCSubstationBusStore::removeCapbankFromUnsolicitedCapBankList(CtiCCCapBankPtr x)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    _unsolicitedCapBanks.remove(x);
}

void CtiCCSubstationBusStore::clearUnsolicitedCapBankList()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " UNSOLICTED MSG for: "<< currentCapBank->getPaoName()<<" Not Processed - TIME: " << currentCapBank->getUnsolicitedChangeTimeUpdated() << endl;
            }
        }
        listIter++;
    }

    clearUnsolicitedCapBankList();
}


void CtiCCSubstationBusStore::insertUnexpectedUnsolicitedList(CtiCCCapBankPtr x)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if (!list_contains(_unexpectedUnsolicited, x))
    {
        _unexpectedUnsolicited.push_back(x);
    }
}

void CtiCCSubstationBusStore::removeFromUnexpectedUnsolicitedList(CtiCCCapBankPtr x)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    _unexpectedUnsolicited.remove(x);
}

void CtiCCSubstationBusStore::clearUnexpectedUnsolicitedList()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " UNSOLICTED MSG for: "<< currentCapBank->getPaoName()<<" Not Processed - TIME: " << currentCapBank->getUnsolicitedChangeTimeUpdated() << endl;
            }
        }
    }

    clearUnexpectedUnsolicitedList();
}


void CtiCCSubstationBusStore::insertRejectedCapBankList(CtiCCCapBankPtr x)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if (!list_contains(_rejectedCapBanks, x))
    {
        _rejectedCapBanks.push_back(x);
    }
}

void CtiCCSubstationBusStore::removeCapbankFromRejectedCapBankList(CtiCCCapBankPtr x)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    _rejectedCapBanks.remove(x);
}

void CtiCCSubstationBusStore::clearRejectedCapBankList()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

    if (!_rejectedCapBanks.empty())
    {
        _rejectedCapBanks.clear();
    }
}
void CtiCCSubstationBusStore::checkRejectedList()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
            CtiCCSubstation* subToInsert = (CtiCCSubstation*) second;
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
    return;
}

void CtiCCSubstationBusStore::setRegMask(long mask)
{
    _regMask = mask;
    return;
}
long CtiCCSubstationBusStore::getRegMask(void)
{
    return _regMask;
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
    return;
}
PaoIdToSubBusMap* CtiCCSubstationBusStore::getPAOSubMap()
{
    return &_paobject_subbus_map;
}

PaoIdToAreaMap* CtiCCSubstationBusStore::getPAOAreaMap()
{
    return &_paobject_area_map;
}
PaoIdToSubstationMap* CtiCCSubstationBusStore::getPAOStationMap()
{
    return &_paobject_substation_map;
}
PaoIdToSpecialAreaMap* CtiCCSubstationBusStore::getPAOSpecialAreaMap()
{
    return &_paobject_specialarea_map;
}


void CtiCCSubstationBusStore::setLinkStatusPointId(long pointId)
{
    _linkStatusPointId = pointId;
    return;
}
long CtiCCSubstationBusStore::getLinkStatusPointId(void)
{
    return _linkStatusPointId;
}

void CtiCCSubstationBusStore::setLinkStatusFlag(bool flag)
{
    _linkStatusFlag = flag;
    return;
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
    return;
}

bool CtiCCSubstationBusStore::getVoltReductionSystemDisabled()
{
    return _voltReductionSystemDisabled;
}

void CtiCCSubstationBusStore::setVoltReductionSystemDisabled(bool disableFlag)
{
    _voltReductionSystemDisabled = disableFlag;
    return;
}

long CtiCCSubstationBusStore::getVoltDisabledCount()
{
    return _voltDisabledCount;
}

void CtiCCSubstationBusStore::setVoltDisabledCount(long value)
{
    _voltDisabledCount = value;
    return;
}

void CtiCCSubstationBusStore::calculateParentPowerFactor(long subBusId)
{
    double varTotal = 0;
    double wattTotal = 0;
    double estVarTotal  = 0;
    double pf = 0;
    double epf = 0;
    long numStations = 0;
    long numBuses = 0;

    long stationId = findSubstationIDbySubBusID(subBusId);
    if (stationId != NULL)
    {
        CtiCCSubstation* station = findSubstationByPAObjectID(stationId);
        if (station != NULL)
        {
            pf = 0;
            epf = 0;
            numBuses = 0;
            for each (long busId in station->getCCSubIds())
            {
                CtiCCSubstationBus *bus = findSubBusByPAObjectID(busId);
                if (bus != NULL)
                {
                     pf += bus->getPowerFactorValue();
                     epf += bus->getEstimatedPowerFactorValue();
                }
                numBuses++;
            }
            if (numBuses != 0)
            {
                station->setPFactor( pf/numBuses );
                station->setEstPFactor( epf/numBuses );
            }

        }

        long areaId = findAreaIDbySubstationID(stationId);
        if (areaId != NULL)
        {
            CtiCCArea* area = findAreaByPAObjectID(areaId);
            if (area != NULL)
            {
                pf = 0;
                epf = 0;
                numStations = 0;
                for each (long stationId in area->getSubstationIds())
                {
                    CtiCCSubstation *station = findSubstationByPAObjectID(stationId);
                    if (station != NULL)
                    {
                         pf += station->getPFactor();
                         epf += station->getEstPFactor();
                    }
                    numStations++;
                }
                if (numStations != 0)
                {
                    area->setPFactor( pf/numStations );
                    area->setEstPFactor( epf/numStations );
                }
            }
        }
        ChildToParentMultiMap::iterator spAreaIter, end;
        findSpecialAreaIDbySubstationID(stationId, spAreaIter, end);

        while (spAreaIter != end)
        {
            long spAreaId = spAreaIter->second;
            CtiCCSpecial* spArea = findSpecialAreaByPAObjectID(spAreaId);
            if (spArea != NULL)
            {
                pf = 0;
                epf = 0;
                numStations = 0;
                for each (long stationId in spArea->getSubstationIds())
                {
                    station = findSubstationByPAObjectID(stationId);
                    if (station != NULL)
                    {
                         pf += station->getPFactor();
                         epf += station->getEstPFactor();
                    }
                    numStations++;
                }
                if (numStations != 0)
                {
                    spArea->setPFactor( pf/numStations );
                    spArea->setEstPFactor( epf/numStations );
                }
            }
            spAreaIter++;
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
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Expired kvar request, removing: " << obj.getPaoId() << endl;
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

    return;
}


/*---------------------------------------------------------------------------
    setControlStatus

    Sets the control status of the capbank
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::resetAllOperationStats()
{

    long i=0;
    for(i=0;i<_ccGeoAreas->size();i++)
    {
        CtiCCArea* currentArea = (CtiCCArea*)_ccGeoAreas->at(i);
        currentArea->getOperationStats().init();
    }

    for(i=0;i<_ccSpecialAreas->size();i++)
    {
        CtiCCSpecial* currentSpArea = (CtiCCSpecial*)_ccSpecialAreas->at(i);
        currentSpArea->getOperationStats().init();

    }

    for(i=0;i<_ccSubstations->size();i++)
    {
        CtiCCSubstation* currentStation = (CtiCCSubstation*)_ccSubstations->at(i);
        currentStation->getOperationStats().init();

    }

    for(i=0;i<_ccSubstationBuses->size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)_ccSubstationBuses->at(i);
        currentSubstationBus->getOperationStats().init();

        CtiFeeder_vec &ccFeeders = currentSubstationBus->getCCFeeders();

        for(long j=0; j < ccFeeders.size(); j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders.at(j));
            currentFeeder->getOperationStats().init();

            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(long k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[k]);
                currentCapBank->getOperationStats().init();
            }
        }
    }

    return;
}


/*---------------------------------------------------------------------------
    setControlStatus

    Sets the control status of the capbank
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::resetAllConfirmationStats()
{

    long i=0;
    for(i=0;i<_ccGeoAreas->size();i++)
    {
        CtiCCArea* currentArea = (CtiCCArea*)_ccGeoAreas->at(i);
        currentArea->getConfirmationStats().init();
    }

    for(i=0;i<_ccSpecialAreas->size();i++)
    {
        CtiCCSpecial* currentSpArea = (CtiCCSpecial*)_ccSpecialAreas->at(i);
        currentSpArea->getConfirmationStats().init();

    }

    for(i=0;i<_ccSubstations->size();i++)
    {
        CtiCCSubstation* currentStation = (CtiCCSubstation*)_ccSubstations->at(i);
        currentStation->getConfirmationStats().init();

    }

    for(i=0;i<_ccSubstationBuses->size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)_ccSubstationBuses->at(i);
        currentSubstationBus->getConfirmationStats().init();

        CtiFeeder_vec &ccFeeders = currentSubstationBus->getCCFeeders();

        for(long j=0; j < ccFeeders.size(); j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders.at(j));
            currentFeeder->getConfirmationStats().init();

            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(long k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[k]);
                currentCapBank->getConfirmationStats().init();
            }
        }
    }

    return;
}


void CtiCCSubstationBusStore::reCalculateAllStats( )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());
    {
        long i = 0;
        for(i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)_ccSubstationBuses->at(i);

            CtiFeeder_vec &ccFeeders = currentSubstationBus->getCCFeeders();
            CCStatsObject subBusUserDef, subBusDaily, subBusWeekly, subBusMonthly;
            CCStatsObject subBusUserDefOp, subBusDailyOp, subBusWeeklyOp, subBusMonthlyOp;
            long numOfFdrs = ccFeeders.size();
            for(long j=0; j < numOfFdrs; j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders.at(j));

                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                CCStatsObject feederUserDef, feederDaily, feederWeekly, feederMonthly;
                CCStatsObject feederUserDefOp, feederDailyOp, feederWeeklyOp, feederMonthlyOp;
                for(long k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)(ccCapBanks[k]);
                    if(currentCapBank->getControlDeviceId() > 0 && !currentCapBank->getDisableFlag())
                    {
                        //Confirmation Stats Total
                        if (currentCapBank->getConfirmationStats().getUserDefCommCount() > 0)
                        {
                            feederUserDef.incrementTotal(currentCapBank->getConfirmationStats().calculateSuccessPercent(capcontrol::USER_DEF_CCSTATS));
                            feederUserDef.incrementOpCount(1);
                        }
                        if (currentCapBank->getConfirmationStats().getDailyCommCount() > 0)
                        {
                            feederDaily.incrementTotal( currentCapBank->getConfirmationStats().calculateSuccessPercent(capcontrol::DAILY_CCSTATS));
                            feederDaily.incrementOpCount(1);
                        }
                        if (currentCapBank->getConfirmationStats().getWeeklyCommCount() > 0)
                        {
                            feederWeekly.incrementTotal( currentCapBank->getConfirmationStats().calculateSuccessPercent(capcontrol::WEEKLY_CCSTATS));
                            feederWeekly.incrementOpCount(1);
                        }
                        if (currentCapBank->getConfirmationStats().getMonthlyCommCount() > 0)
                        {
                            feederMonthly.incrementTotal( currentCapBank->getConfirmationStats().calculateSuccessPercent(capcontrol::MONTHLY_CCSTATS));
                            feederMonthly.incrementOpCount(1);
                        }

                        //Operations Stats Total
                        if (currentCapBank->getOperationStats().getUserDefOpCount() > 0)
                        {
                            feederUserDefOp.incrementTotal( currentCapBank->getOperationStats().calculateSuccessPercent(capcontrol::USER_DEF_CCSTATS));
                            feederUserDefOp.incrementOpCount(1);
                        }
                        if (currentCapBank->getOperationStats().getDailyOpCount() > 0)
                        {
                            feederDailyOp.incrementTotal( currentCapBank->getOperationStats().calculateSuccessPercent(capcontrol::DAILY_CCSTATS));
                            feederDailyOp.incrementOpCount(1);
                        }
                        if (currentCapBank->getOperationStats().getWeeklyOpCount() > 0)
                        {
                            feederWeeklyOp.incrementTotal( currentCapBank->getOperationStats().calculateSuccessPercent(capcontrol::WEEKLY_CCSTATS));
                            feederWeeklyOp.incrementOpCount(1);
                        }
                        if (currentCapBank->getOperationStats().getMonthlyOpCount() > 0)
                        {
                            feederMonthlyOp.incrementTotal( currentCapBank->getOperationStats().calculateSuccessPercent(capcontrol::MONTHLY_CCSTATS));
                            feederMonthlyOp.incrementOpCount(1);
                        }
                    }

                }
                setConfirmationSuccessPercents(currentFeeder, feederUserDef, feederDaily, feederWeekly, feederMonthly);
                setOperationSuccessPercents(currentFeeder, feederUserDefOp, feederDailyOp, feederWeeklyOp, feederMonthlyOp);

                incrementConfirmationPercentTotals(currentFeeder, subBusUserDef, subBusDaily, subBusWeekly, subBusMonthly);
                incrementOperationPercentTotals(currentFeeder, subBusUserDefOp, subBusDailyOp, subBusWeeklyOp, subBusMonthlyOp);

            }
            setConfirmationSuccessPercents(currentSubstationBus, subBusUserDef, subBusDaily, subBusWeekly, subBusMonthly);
            setOperationSuccessPercents(currentSubstationBus, subBusUserDefOp, subBusDailyOp, subBusWeeklyOp, subBusMonthlyOp);
        }

        for(i=0;i<_ccSubstations->size();i++)
        {
            CtiCCSubstation* currentStation = (CtiCCSubstation*)_ccSubstations->at(i);
            CCStatsObject subUserDef, subDaily, subWeekly, subMonthly;
            CCStatsObject subUserDefOp, subDailyOp, subWeeklyOp, subMonthlyOp;
            long numOfBuses = currentStation->getCCSubIds().size();
            for each (long busId in currentStation->getCCSubIds())
            {
                CtiCCSubstationBusPtr currentSubstationBus = findSubBusByPAObjectID(busId);
                if (currentSubstationBus != NULL)
                {
                    incrementConfirmationPercentTotals(currentSubstationBus, subUserDef, subDaily, subWeekly, subMonthly);
                    incrementOperationPercentTotals(currentSubstationBus, subUserDefOp, subDailyOp, subWeeklyOp, subMonthlyOp);

                }
            }
            setConfirmationSuccessPercents(currentStation, subUserDef, subDaily, subWeekly, subMonthly);
            setOperationSuccessPercents(currentStation, subUserDefOp, subDailyOp, subWeeklyOp, subMonthlyOp);

        }

        for(i=0;i<_ccGeoAreas->size();i++)
        {
            CtiCCArea* currentArea = (CtiCCArea*)_ccGeoAreas->at(i);
            CCStatsObject areaUserDef, areaDaily, areaWeekly, areaMonthly;
            CCStatsObject areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp;
            long numOfSubs = currentArea->getSubstationIds().size();
            for each (long subId in currentArea->getSubstationIds())
            {
                CtiCCSubstationPtr currentStation = findSubstationByPAObjectID(subId);
                if (currentStation != NULL)
                {
                   incrementConfirmationPercentTotals(currentStation, areaUserDef, areaDaily, areaWeekly, areaMonthly);
                   incrementOperationPercentTotals(currentStation, areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp);

               }
           }
           setConfirmationSuccessPercents(currentArea, areaUserDef, areaDaily, areaWeekly, areaMonthly);
           setOperationSuccessPercents(currentArea, areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp);

        }

        for(i=0;i<_ccSpecialAreas->size();i++)
        {
            CtiCCSpecial* currentSpArea = (CtiCCSpecial*)_ccSpecialAreas->at(i);
            CCStatsObject areaUserDef, areaDaily, areaWeekly, areaMonthly;
            CCStatsObject areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp;
            long numOfSubs = currentSpArea->getSubstationIds().size();
            for each (long subId in currentSpArea->getSubstationIds())
            {
                CtiCCSubstationPtr currentStation = findSubstationByPAObjectID(subId);
                if (currentStation != NULL)
                {
                    incrementConfirmationPercentTotals(currentStation, areaUserDef, areaDaily, areaWeekly, areaMonthly);
                    incrementOperationPercentTotals(currentStation, areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp);

                }
           }
           setConfirmationSuccessPercents(currentSpArea, areaUserDef, areaDaily, areaWeekly, areaMonthly);
           setOperationSuccessPercents(currentSpArea, areaUserDefOp, areaDailyOp, areaWeeklyOp, areaMonthlyOp);
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
            static const string sql =  "SELECT CEL.pointid, CEL.datetime, CEL.spareaid, CEL.areaid, CEL.stationid, "
                                         "CEL.subid, CEL.feederid "
                                       "FROM cceventlog CEL "
                                       "WHERE (CEL.text LIKE '%Close Sent,%' OR CEL.text LIKE '%Open Sent,%' OR "
                                         "CEL.text LIKE 'Flip Sent,%') AND CEL.datetime > ?";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr << oneMonthAgo;

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "*** userDefWindow " << CtiTime(userDefWindow)
                << "*** yesterday " << CtiTime(yesterday)
                << "*** lastWeek " << CtiTime(lastWeek)
                << "*** oneMonthAgo " <<CtiTime(oneMonthAgo) << endl;
            }

            while ( rdr() )
            {
                CtiTime logDateTime;

                long pointId, spareaid, areaid, stationid, subid, feederid;
                rdr["pointid"] >> pointId;
                rdr["datetime"] >> logDateTime;
                rdr["spareaid"] >> spareaid;
                rdr["areaid"] >> areaid;
                rdr["stationid"] >> stationid;
                rdr["subid"] >> subid;
                rdr["feederid"] >> feederid;

                CtiCCCapBankPtr cap = NULL;
                PointIdToCapBankMultiMap::iterator capBeginIter, capEndIter;
                if (findCapBankByPointID(pointId, capBeginIter, capEndIter))
                    cap = capBeginIter->second;

                if (logDateTime >= userDefWindow && cap != NULL )
                {
                    if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " logDateTime >= userDefWindow " << CtiTime(logDateTime) <<">="<<CtiTime(userDefWindow) << endl;
                    }
                    cap->getOperationStats().incrementAllOpCounts();
                }
                else if (logDateTime >= yesterday && cap != NULL )
                {
                    if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " logDateTime >= yesterday " << CtiTime(logDateTime) <<">="<<CtiTime(yesterday) << endl;
                    }
                    cap->getOperationStats().incrementDailyOpCounts();
                }
                else if (logDateTime >= lastWeek && cap != NULL )
                {
                    if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " logDateTime >= lastWeek " << CtiTime(logDateTime) <<">="<<CtiTime(lastWeek) << endl;
                    }
                    cap->getOperationStats().incrementWeeklyOpCounts();
                }
                else if (logDateTime >= oneMonthAgo && cap != NULL )
                {
                    if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " logDateTime >= oneMonthAgo " << CtiTime(logDateTime) <<">="<<CtiTime(oneMonthAgo) << endl;
                    }
                    cap->getOperationStats().incrementMonthlyOpCounts();
                }
                else if ( (_CC_DEBUG & CC_DEBUG_OPSTATS) &&
                         (_CC_DEBUG & CC_DEBUG_EXTENDED) )
                {
                    if (cap != NULL)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " irregular LOG datetime: " << CtiTime(logDateTime) << " for CAPBANK: " << cap->getPaoName()<< endl;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " irregular LOG datetime: " << CtiTime(logDateTime) <<" CAPBANK with Bank Status PID: "<< pointId <<" no longer exists."<< endl;
                    }
                }
            }
        }

        {
            //MONTHLY FAILS
            static const string sql =  "SELECT CEL.pointid, CEL.datetime, CEL.spareaid, CEL.areaid, CEL.stationid, "
                                         "CEL.subid, CEL.feederid "
                                       "FROM cceventlog CEL "
                                       "WHERE CEL.text LIKE 'Var:%Fail' AND CEL.datetime > ?";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr << oneMonthAgo;

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            while ( rdr() )
            {
                CtiTime logDateTime;

                long pointId, spareaid, areaid, stationid, subid, feederid;
                rdr["pointid"] >> pointId;
                rdr["datetime"] >> logDateTime;
                rdr["spareaid"] >> spareaid;
                rdr["areaid"] >> areaid;
                rdr["stationid"] >> stationid;
                rdr["subid"] >> subid;
                rdr["feederid"] >> feederid;

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
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " irregular LOG datetime: " << CtiTime(logDateTime) << " for CAPBANK: " << cap->getPaoName()<< endl;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " irregular LOG datetime: " << CtiTime(logDateTime) <<" CAPBANK with Bank Status PID: "<< pointId <<" no longer exists."<< endl;
                    }
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

void CtiCCSubstationBusStore::reCalculateConfirmationStatsFromDatabase( )
{
    try
    {
        CtiTime currentDateTime;
        CtiTime userDefWindow = currentDateTime.seconds() - ((_OP_STATS_USER_DEF_PERIOD * 60 ) + 3600);
        INT capCount = 0;
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

            rdr << oneMonthAgo;

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
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
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " Start Time: " << CtiTime(start) <<"  *** UserDefined Window: "
                            << CtiTime(userDefWindow) << endl;
                    }
                    if (start > userDefWindow )
                    {
                        if ( _CC_DEBUG & CC_DEBUG_OPSTATS )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " ##### Incrementing User Def Comm Counts for Cap: "<< cap->getPaoName() << endl;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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

    return;
}

void CtiCCSubstationBusStore::createAllStatsPointDataMsgs(CtiMultiMsg_vec& pointChanges)
{
    long i=0;
    try
    {
        for(i=0;i<_ccGeoAreas->size();i++)
        {
            CtiCCArea* currentArea = (CtiCCArea*)_ccGeoAreas->at(i);
            currentArea->getOperationStats().createPointDataMsgs(pointChanges);
            currentArea->getConfirmationStats().createPointDataMsgs(pointChanges);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        for(i=0;i<_ccSpecialAreas->size();i++)
        {
            CtiCCSpecial* currentSpArea = (CtiCCSpecial*)_ccSpecialAreas->at(i);
            currentSpArea->getOperationStats().createPointDataMsgs(pointChanges);
            currentSpArea->getConfirmationStats().createPointDataMsgs(pointChanges);

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        for(i=0;i<_ccSubstations->size();i++)
        {
            CtiCCSubstation* currentStation = (CtiCCSubstation*)_ccSubstations->at(i);
            currentStation->getOperationStats().createPointDataMsgs(pointChanges);
            currentStation->getConfirmationStats().createPointDataMsgs(pointChanges);

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return;
}


void CtiCCSubstationBusStore::createOperationStatPointDataMsgs(CtiMultiMsg_vec& pointChanges, CtiCCCapBank* cap, CtiCCFeeder* feed, CtiCCSubstationBus* bus,
                                                       CtiCCSubstation* station, CtiCCArea* area, CtiCCSpecial* spArea)
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
    return;
}

void CtiCCSubstationBusStore::cascadeAreaStrategySettings(CtiCCAreaBase* object)
{
    if (object == NULL || object->getStrategy()->getUnitType() == ControlStrategy::None)
        return;

    long strategyID = object->getStrategy()->getStrategyId();

    for each (long stationId in object->getSubstationIds())
    {
        CtiCCSubstation *station =findSubstationByPAObjectID(stationId);
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

                if (ciStringEqual(currentCCSubstationBus->getStrategy()->getControlMethod(),ControlStrategy::TimeOfDayControlMethod) )
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
    return;
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
    return;
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
    _strategyManager.executeAll();
}


Cti::CapControl::ZoneManager & CtiCCSubstationBusStore::getZoneManager()
{
    return _zoneManager;
}


bool CtiCCSubstationBusStore::reloadZoneFromDatabase(const long zoneId)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return true;
}


boost::shared_ptr<Cti::CapControl::VoltageRegulatorManager> CtiCCSubstationBusStore::getVoltageRegulatorManager()
{
    return _voltageRegulatorManager;
}


bool CtiCCSubstationBusStore::reloadVoltageRegulatorFromDatabase(const long regulatorId)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(getMux());

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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        PointIdToSubBusMultiMap::iterator   busIter, end;

        findSubBusByPointID( reloadId, busIter, end );

        for ( ; busIter != end; ++busIter )
        {
            long subbusId = busIter->second->getPaoId();

            reloadMonitorPointsFromDatabase( subbusId, &_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map,
                                             &_pointid_capbank_map, &_pointid_subbus_map);

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);

                dout << CtiTime() << " - Reloaded Monitor Points for bus ID: " << subbusId << endl;
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

