/*---------------------------------------------------------------------------
        Filename:  ccsubstationbusstore.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCSubstationBusStore
                        CtiCCSubstationBusStore maintains a pool of
                        CtiCCSubstationBuses.

        Initial Date:  8/27/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include <map>


#include <rw/rwfile.h>
#include <rw/thr/thrfunc.h>
//#include <rw/collstr.h>

#include "ccsubstationbusstore.h"
#include "ccstrategy.h"
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
#include "configparms.h"
#include "msg_dbchg.h"
#include "msg_signal.h"
#include "capcontroller.h"
#include "utility.h"
#include "thread_monitor.h"

#include "ctistring.h"
#include <string>
//#include <rwutil.h>


extern ULONG _CC_DEBUG;
extern ULONG _DB_RELOAD_WAIT;
extern ULONG _MAX_KVAR;
extern ULONG _MAX_KVAR_TIMEOUT;

using namespace std;


CtiTime timeSaver;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCSubstationBusStore::CtiCCSubstationBusStore() : _isvalid(FALSE), _reregisterforpoints(TRUE), _reloadfromamfmsystemflag(FALSE), _lastdbreloadtime(CtiTime(CtiDate(1,1,1990),0,0,0)), _wassubbusdeletedflag(FALSE), _lastindividualdbreloadtime(CtiTime(CtiDate(1,1,1990),0,0,0))
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
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
    _pointid_feeder_map.clear();
    _pointid_capbank_map.clear();

    _strategyid_strategy_map.clear();

    _substation_area_map.clear();
    _subbus_substation_map.clear();
    _feeder_subbus_map.clear(); 
    _capbank_subbus_map.clear();
    _capbank_feeder_map.clear();
    _cbc_capbank_map.clear();

    _reloadList.clear();
    _orphanedCapBanks.clear();
    _orphanedFeeders.clear();

    _linkStatusPointId = 0;
    _linkStatusFlag = OPENED;
    _linkDropOutTime = CtiTime(); 

    _voltReductionSystemDisabled = FALSE;
    _voltDisabledCount = 0;

    //Start the reset thread
    RWThreadFunction func = rwMakeThreadFunction( *this, &CtiCCSubstationBusStore::doResetThr );
    _resetthr = func;
    func.start();
    //Start the amfm thread
    RWThreadFunction func2 = rwMakeThreadFunction( *this, &CtiCCSubstationBusStore::doAMFMThr );
    _amfmthr = func2;
    func2.start();
}

/*--------------------------------------------------------------------------
    Destrutor
-----------------------------------------------------------------------------*/
CtiCCSubstationBusStore::~CtiCCSubstationBusStore()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    if( _resetthr.isValid() )
    {
        _resetthr.requestCancellation();
        _resetthr.join();
    }
    if( _amfmthr.isValid() )
    {
        _amfmthr.requestCancellation();
        _amfmthr.join();
    }

    shutdown();
}



/*---------------------------------------------------------------------------
    getSubstationBuses

    Returns a CtiCCSubstationBus_vec of CtiCCSubstationBuses
---------------------------------------------------------------------------*/
CtiCCSubstationBus_vec* CtiCCSubstationBusStore::getCCSubstationBuses(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

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
            setReregisterForPoints(false);
            clearDBReloadList();
        }
    }
    else if (_2wayFlagUpdate) 
    {
        dumpAllDynamicData();
        _2wayFlagUpdate = FALSE;
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
CtiCCArea_vec* CtiCCSubstationBusStore::getCCGeoAreas(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

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
CtiCCSpArea_vec* CtiCCSubstationBusStore::getCCSpecialAreas(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

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
CtiCCSubstation_vec* CtiCCSubstationBusStore::getCCSubstations(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

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
CtiCCState_vec* CtiCCSubstationBusStore::getCCCapBankStates(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

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
  Returns 0 if no subbus is found.
  This member exists mostly for efficiency in updating subbuses when point
  data shows up.
----------------------------------------------------------------------------*/  
multimap< long, CtiCCSpecialPtr >::iterator CtiCCSubstationBusStore::findSpecialAreaByPointID(long point_id, int &saCount)
{
    multimap< long, CtiCCSpecialPtr >::iterator iter = _pointid_specialarea_map.lower_bound(point_id);
    saCount = _pointid_specialarea_map.count(point_id);

    if (saCount > 0) 
    {
        if (iter != _pointid_specialarea_map.end()) 
            return iter;
        else
            return NULL;
    }
    else 
        return NULL;
}

multimap< long, CtiCCAreaPtr >::iterator CtiCCSubstationBusStore::findAreaByPointID(long point_id, int &areaCount)
{
    multimap< long, CtiCCAreaPtr >::iterator iter = _pointid_area_map.lower_bound(point_id);
    areaCount = _pointid_area_map.count(point_id);

    if (areaCount > 0) 
    {
        if (iter != _pointid_area_map.end()) 
            return iter;
        else
            return NULL;
    }
    else 
        return NULL;
}

multimap< long, CtiCCSubstationBusPtr >::iterator CtiCCSubstationBusStore::findSubBusByPointID(long point_id, int &subCount)
{
    multimap< long, CtiCCSubstationBusPtr >::iterator iter = _pointid_subbus_map.lower_bound(point_id);
    subCount = _pointid_subbus_map.count(point_id);

    if (subCount > 0) 
    {
        if (iter != _pointid_subbus_map.end()) 
            return iter;
        else
            return NULL;
    }
    else 
        return NULL;
}

multimap< long, CtiCCFeederPtr >::iterator CtiCCSubstationBusStore::findFeederByPointID(long point_id, int &feedCount)
{
    multimap< long, CtiCCFeederPtr >::iterator iter = _pointid_feeder_map.lower_bound(point_id);
    feedCount = _pointid_feeder_map.count(point_id);
    if (feedCount > 0) 
    {
        if (iter != _pointid_feeder_map.end()) 
            return iter;
        else
            return NULL;
    }
    else
        return NULL;

}

multimap< long, CtiCCCapBankPtr >::iterator CtiCCSubstationBusStore::findCapBankByPointID(long point_id, int &capCount)
{
    multimap< long, CtiCCCapBankPtr >::iterator iter = _pointid_capbank_map.lower_bound(point_id);
    capCount = _pointid_capbank_map.count(point_id);
    if (capCount > 0)
    {
        if (iter != _pointid_capbank_map.end()) 
            return iter;
        else
            return NULL;
    }
    else
        return NULL;

}

int CtiCCSubstationBusStore::getNbrOfAreasWithPointID(long point_id)
{
    return _pointid_area_map.count(point_id);
}
int CtiCCSubstationBusStore::getNbrOfSpecialAreasWithPointID(long point_id)
{
    return _pointid_specialarea_map.count(point_id);
}
int CtiCCSubstationBusStore::getNbrOfSubBusesWithPointID(long point_id)
{
    return _pointid_subbus_map.count(point_id);
}
int CtiCCSubstationBusStore::getNbrOfSubsWithAltSubID(long altSubId)
{
    return _altsub_sub_idmap.count(altSubId);
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
    map< long, CtiCCAreaPtr >::iterator iter = _paobject_area_map.find(paobject_id);
    return (iter == _paobject_area_map.end() ? NULL : iter->second);
}
CtiCCSpecialPtr CtiCCSubstationBusStore::findSpecialAreaByPAObjectID(long paobject_id)
{
    map< long, CtiCCSpecialPtr >::iterator iter = _paobject_specialarea_map.find(paobject_id);
    return (iter == _paobject_specialarea_map.end() ? NULL : iter->second);
}
CtiCCSubstationPtr CtiCCSubstationBusStore::findSubstationByPAObjectID(long paobject_id)
{
    map< long, CtiCCSubstationPtr >::iterator iter = _paobject_substation_map.find(paobject_id);
    return (iter == _paobject_substation_map.end() ? NULL : iter->second);
}
CtiCCSubstationBusPtr CtiCCSubstationBusStore::findSubBusByPAObjectID(long paobject_id)
{
    map< long, CtiCCSubstationBusPtr >::iterator iter = _paobject_subbus_map.find(paobject_id);
    return (iter == _paobject_subbus_map.end() ? NULL : iter->second);
}

CtiCCFeederPtr CtiCCSubstationBusStore::findFeederByPAObjectID(long paobject_id)
{
    map< long, CtiCCFeederPtr >::iterator iter = _paobject_feeder_map.find(paobject_id);
    return (iter == _paobject_feeder_map.end() ? NULL : iter->second);
}

CtiCCCapBankPtr CtiCCSubstationBusStore::findCapBankByPAObjectID(long paobject_id)
{
    map< long, CtiCCCapBankPtr >::iterator iter = _paobject_capbank_map.find(paobject_id);
    return (iter == _paobject_capbank_map.end() ? NULL : iter->second);
}


CtiCCStrategyPtr CtiCCSubstationBusStore::findStrategyByStrategyID(long strategy_id)
{
    map< long, CtiCCStrategyPtr >::iterator iter = _strategyid_strategy_map.find(strategy_id);
    return (iter == _strategyid_strategy_map.end() ? NULL : iter->second);
}

long CtiCCSubstationBusStore::findAreaIDbySubstationID(long substationId)
{
    map< long, long >::iterator iter = _substation_area_map.find(substationId);
    return (iter == _substation_area_map.end() ? NULL : iter->second);
}
long CtiCCSubstationBusStore::findSpecialAreaIDbySubstationID(long substationId)
{
    map< long, long >::iterator iter = _substation_specialarea_map.find(substationId);
    return (iter == _substation_specialarea_map.end() ? NULL : iter->second);
}

long CtiCCSubstationBusStore::findSubstationIDbySubBusID(long subBusId)
{
    map< long, long >::iterator iter = _subbus_substation_map.find(subBusId);
    return (iter == _subbus_substation_map.end() ? NULL : iter->second);
}

long CtiCCSubstationBusStore::findSubBusIDbyFeederID(long feederId)
{
    map< long, long >::iterator iter = _feeder_subbus_map.find(feederId);
    return (iter == _feeder_subbus_map.end() ? NULL : iter->second);
}
long CtiCCSubstationBusStore::findSubBusIDbyCapBankID(long capBankId)
{
    map< long, long >::iterator iter = _capbank_subbus_map.find(capBankId);
    return (iter == _capbank_subbus_map.end() ? NULL : iter->second);
}
long CtiCCSubstationBusStore::findFeederIDbyCapBankID(long capBankId)
{
    map< long, long >::iterator iter = _capbank_feeder_map.find(capBankId);
    return (iter == _capbank_feeder_map.end() ? NULL : iter->second);
}
long CtiCCSubstationBusStore::findCapBankIDbyCbcID(long cbcId)
{
    map< long, long >::iterator iter = _cbc_capbank_map.find(cbcId);
    return (iter == _cbc_capbank_map.end() ? NULL : iter->second);
}



long CtiCCSubstationBusStore::findSubIDbyAltSubID(long altSubId, int index)
{ 
    multimap< long, long >::iterator iter = _altsub_sub_idmap.find(altSubId);
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

/*---------------------------------------------------------------------------
    dumpAllDynamicData

    Writes out the dynamic information for each of the substation buses.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::dumpAllDynamicData()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Store START dumpAllDynamicData" << endl;
    }*/
    try
    {   

        if( _ccSubstationBuses->size() > 0 )
        {
            CtiTime currentDateTime = CtiTime();
            string dynamicCapControl("dynamicCapControl");
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            conn.beginTransaction(dynamicCapControl.c_str());
            if (conn.isValid())
            {

                if(_ccGeoAreas->size() > 0 )
                {
                    for(LONG i=0;i<_ccGeoAreas->size();i++)
                    {
                        CtiCCArea* currentCCArea = (CtiCCArea*)(*_ccGeoAreas)[i];
                        if( currentCCArea->isDirty() )
                        {
                            try
                            {
                                currentCCArea->dumpDynamicData(conn,currentDateTime);
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                    }
                }
                if(_ccSpecialAreas->size() > 0 )
                {
                    for(LONG i=0;i<_ccSpecialAreas->size();i++)
                    {
                        CtiCCSpecial* currentSpecial = (CtiCCSpecial*)(*_ccSpecialAreas)[i];
                        if( currentSpecial->isDirty() )
                        {
                            try
                            {
                                currentSpecial->dumpDynamicData(conn,currentDateTime);
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                    }
                }
                if( _ccSubstations->size() > 0 )
                {
                    for(LONG i=0;i<_ccSubstations->size();i++)
                    {
                        CtiCCSubstation* currentCCSubstation = (CtiCCSubstation*)(*_ccSubstations)[i];
                        if( currentCCSubstation->isDirty() )
                        {
                            try
                            {
                                currentCCSubstation->dumpDynamicData(conn,currentDateTime);
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                    }
                }



                for(LONG i=0;i<_ccSubstationBuses->size();i++)
                {
                    CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];
                    if( currentCCSubstationBus->isDirty() )
                    {
                        /*{
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWDBDateTime().second() << "." << clock() << " - Store START Sub Bus dumpDynamicData" << endl;
                        }*/
                        try
                        {
                            currentCCSubstationBus->dumpDynamicData(conn,currentDateTime);
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }

                    CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
                    if( ccFeeders.size() > 0 )
                    {
                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                            if( currentFeeder->isDirty() )
                            {
                                /*{
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWDBDateTime().second() << "." << clock() << " -     Store START Feeder dumpDynamicData" << endl;
                                }*/
                                try
                                {
                                    currentFeeder->dumpDynamicData(conn,currentDateTime);
                                }
                                catch(...)
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }

                            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                            if( ccCapBanks.size() > 0 )
                            {
                                for(LONG k=0;k<ccCapBanks.size();k++)
                                {
                                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                                    if( currentCapBank->isDirty() )
                                    {
                                        /*{
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime().second() << "." << clock() << " -         Store START Cap Bank dumpDynamicData" << endl;
                                        }*/
                                        try
                                        {
                                            currentCapBank->dumpDynamicData(conn,currentDateTime);
                                        }
                                        catch(...)
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                        }
                                        try
                                        {
                                            if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(), "CBC 702") )
                                            {
                                                CtiCCTwoWayPoints* twoWayPts = currentCapBank->getTwoWayPoints();
                                                if (twoWayPts->isDirty()) 
                                                {
                                                    twoWayPts->dumpDynamicData(conn,currentDateTime);
                                                }
                                            }
                                        }
                                        catch(...)
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                        }
                                    }
                                    vector <CtiCCMonitorPointPtr>& monPoints = currentCapBank->getMonitorPoint();
                                    for (LONG l = 0; l < monPoints.size(); l++)
                                    {
                                        if (((CtiCCMonitorPointPtr)monPoints[l])->isDirty())
                                        {   
                                            ((CtiCCMonitorPointPtr)monPoints[l])->dumpDynamicData(conn,currentDateTime);
                                        }
                                    }
                                    vector <CtiCCPointResponsePtr>& ptResponses = currentCapBank->getPointResponse();
                                    for (LONG m = 0; m < ptResponses.size(); m++)
                                    {
                                        if (((CtiCCPointResponsePtr)ptResponses[m])->isDirty())
                                        {   
                                            ((CtiCCPointResponsePtr)ptResponses[m])->dumpDynamicData(conn,currentDateTime);
                                        }
                                    }                            
                                }
                            }
                        }
                    }
                }
            }
            conn.commitTransaction(dynamicCapControl.c_str());
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
        CtiCCSubstationBus_vec tempCCSubstationBuses;
        CtiCCSubstation_vec tempCCSubstations;
        CtiCCArea_vec tempCCGeoAreas;
        CtiCCSpArea_vec tempCCSpecialAreas;


        map< long, CtiCCAreaPtr > temp_paobject_area_map;
        map< long, CtiCCSpecialPtr > temp_paobject_specialarea_map;
        map< long, CtiCCSubstationPtr > temp_paobject_substation_map;
        map< long, CtiCCSubstationBusPtr > temp_paobject_subbus_map;
        map< long, CtiCCFeederPtr > temp_paobject_feeder_map;
        map< long, CtiCCCapBankPtr > temp_paobject_capbank_map;

        multimap< long, CtiCCAreaPtr > temp_point_area_map;
        multimap< long, CtiCCSpecialPtr > temp_point_specialarea_map;
        multimap< long, CtiCCSubstationBusPtr > temp_point_subbus_map;
        multimap< long, CtiCCFeederPtr > temp_point_feeder_map;
        multimap< long, CtiCCCapBankPtr > temp_point_capbank_map;
        map< long, CtiCCStrategyPtr > temp_strategyid_strategy_map;

        map< long, long > temp_capbank_subbus_map;
        map< long, long > temp_capbank_feeder_map;
        map< long, long > temp_feeder_subbus_map;
        map< long, long > temp_subbus_substation_map;
        map< long, long > temp_substation_area_map;
        map< long, long > temp_substation_specialarea_map;
        map< long, long > temp_cbc_capbank_map;

        multimap<long, long> temp_altsub_sub_idmap;

        list <long> orphanCaps;
        list <long> orphanFeeders;


        LONG currentAllocations = ResetBreakAlloc();
        if ( _CC_DEBUG & CC_DEBUG_EXTENDED )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Current Number of Historical Memory Allocations: " << currentAllocations << endl;
        }

        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            if ( !conn.isValid() )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Unable to get valid database connection." << endl;
                _isvalid = FALSE;
                return;
            }

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Obtained connection to the database..." << endl;
                dout << CtiTime() << " - Resetting substation buses from database..." << endl;
            }   
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
            try
            {
                map <long, CtiCCStrategyPtr>::iterator iter = _strategyid_strategy_map.begin();
                while (iter != _strategyid_strategy_map.end())
                {
                    CtiCCStrategyPtr strat = iter->second;
                    iter++;
                    if (strat != NULL)
                    {
                        delete strat;
                    }
                }
           
                _strategyid_strategy_map.clear();

                _paobject_area_map.clear();
                _paobject_specialarea_map.clear();
                _paobject_substation_map.clear();

                while (!_orphanedFeeders.empty())
                {
                    long feederId = _orphanedFeeders.front();
                    _orphanedFeeders.pop_front();
                    CtiCCFeederPtr feeder = findFeederByPAObjectID(feederId);
                    if (feeder != NULL)
                    {
                        delete feeder;
                    }
                }

                while (!_orphanedCapBanks.empty())
                {
                    long capId = _orphanedCapBanks.front();
                    _orphanedCapBanks.pop_front();

                    CtiCCCapBankPtr cap = findCapBankByPAObjectID(capId);
                    if (cap != NULL)
                    {
                        delete cap;
                    }
                }

                _paobject_subbus_map.clear();
                _paobject_feeder_map.clear();
                _paobject_capbank_map.clear();
                _pointid_area_map.clear();
                _pointid_specialarea_map.clear();
                _pointid_subbus_map.clear();
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

                if ( _ccSubstationBuses->size() > 0 )
                {
                    dumpAllDynamicData();
                    wasAlreadyRunning = true;
                }
                delete_vector( _ccSubstationBuses );
                _ccSubstationBuses->clear();
                delete_vector( _ccSubstations );
                _ccSubstations->clear();
                delete_vector( _ccCapBankStates );
                _ccCapBankStates->clear();
                delete_vector(_ccGeoAreas);
                _ccGeoAreas->clear();     
                delete_vector(_ccSpecialAreas);
                _ccSpecialAreas->clear();
            }
            catch( ... )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Exception clearing old data for reload. Location: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            CtiTime currentDateTime;
            /************************************************************* 
            ******  Loading Strategies                              ****** 
            **************************************************************/ 
            if ( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - DataBase Reload Begin - " << endl;
            }
            
            reloadStrategyFromDataBase(-1, &temp_strategyid_strategy_map);

            /***********************************************************
            *******  Loading Areas                               *******
            ************************************************************/
            reloadSpecialAreaFromDatabase(0, &temp_strategyid_strategy_map, &temp_paobject_specialarea_map, &temp_point_specialarea_map, &tempCCSpecialAreas);

            reloadAreaFromDatabase(0, &temp_strategyid_strategy_map, &temp_paobject_area_map, &temp_point_area_map, &tempCCGeoAreas);

            /***********************************************************
            *******  Loading Substations                         *******
            ************************************************************/

            reloadSubstationFromDatabase(0, &temp_paobject_substation_map, &temp_paobject_area_map, &temp_paobject_specialarea_map, 
                                         &temp_substation_area_map, &temp_substation_specialarea_map, &tempCCSubstations);

            try
            {
                _paobject_area_map = temp_paobject_area_map;
                _paobject_specialarea_map = temp_paobject_specialarea_map;
                _paobject_substation_map = temp_paobject_substation_map;
                _strategyid_strategy_map = temp_strategyid_strategy_map;
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            /***********************************************************
            *******  Loading SubBuses                            *******
            ************************************************************/

            reloadSubBusFromDatabase(0, &temp_strategyid_strategy_map, &temp_paobject_subbus_map, & temp_paobject_substation_map,
                                     &temp_point_subbus_map, &temp_altsub_sub_idmap, &temp_subbus_substation_map, &tempCCSubstationBuses);

            /***********************************************************
            *******  Loading Feeders                             *******
            ************************************************************/

            if (tempCCSubstationBuses.size() > 0)
            {
                {   
                    reloadFeederFromDatabase(0, &temp_strategyid_strategy_map, &temp_paobject_feeder_map,
                                             &temp_paobject_subbus_map, &temp_point_feeder_map, 
                                             &temp_feeder_subbus_map);//, &temp_feeder_area_map );
                }

               /************************************************************
                ********    Loading Cap Banks                       ********
                ************************************************************/
                {
                    reloadCapBankFromDatabase(0, &temp_paobject_capbank_map, &temp_paobject_feeder_map, &temp_paobject_subbus_map,
                                          &temp_point_capbank_map, &temp_capbank_subbus_map,
                                          &temp_capbank_feeder_map, &temp_feeder_subbus_map, &temp_cbc_capbank_map );
                }

                /************************************************************
                ********    Loading Monitor Points                   ********
                ************************************************************/
                {
                    reloadMonitorPointsFromDatabase(0, &temp_paobject_capbank_map, &temp_paobject_feeder_map, &temp_paobject_subbus_map,
                                          &temp_point_capbank_map);
                }


            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - No Substations in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
            /************************************************************
             ********    Loading Cap Banks States                ********
             ************************************************************/
            {
                reloadCapBankStatesFromDatabase();
            }

            {
               reloadClientLinkStatusPointFromDatabase();
            } 

            /************************************************************
             ********    Loading Operation Statistics            ********
             ************************************************************/
            {
/*                 reloadOperationStatsFromDatabase(0,&temp_paobject_capbank_map, &temp_paobject_feeder_map, &temp_paobject_subbus_map,
                                                       &_paobject_substation_map,
                                                       &_paobject_area_map,
                                                       &_paobject_specialarea_map);
*/                                                       
            }

            /************************************************************
             ********    Loading Orphans                         ********
             ************************************************************/
            try
            {
               locateOrphans(&orphanCaps, &orphanFeeders, temp_paobject_capbank_map, temp_paobject_feeder_map,
                             temp_capbank_feeder_map, temp_feeder_subbus_map);
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
                dout << CtiTime() << " - Loading values into capctonrol - " << endl;
            }                   

            try
            {
                _altsub_sub_idmap = temp_altsub_sub_idmap;
                _paobject_subbus_map = temp_paobject_subbus_map;
                _paobject_feeder_map = temp_paobject_feeder_map;
                _paobject_capbank_map = temp_paobject_capbank_map;

            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            
            try
            {
                _pointid_area_map = temp_point_area_map;
                _pointid_specialarea_map = temp_point_specialarea_map;
                _pointid_subbus_map = temp_point_subbus_map;
                _pointid_feeder_map = temp_point_feeder_map;
                _pointid_capbank_map = temp_point_capbank_map;
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            try
            {
                _subbus_substation_map = temp_subbus_substation_map;
                _substation_area_map = temp_substation_area_map;
                _substation_specialarea_map = temp_substation_specialarea_map;
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            try
            {
                _feeder_subbus_map = temp_feeder_subbus_map;
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            try
            {
                _capbank_subbus_map = temp_capbank_subbus_map;
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            try
            {
                _capbank_feeder_map = temp_capbank_feeder_map;
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            try
            {
                _cbc_capbank_map = temp_cbc_capbank_map;
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            try
            {
                _orphanedCapBanks = orphanCaps;
                _orphanedFeeders = orphanFeeders;
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            try
            {
                *_ccSubstationBuses = tempCCSubstationBuses;
                *_ccSubstations = tempCCSubstations;
                *_ccGeoAreas = tempCCGeoAreas;
                *_ccSpecialAreas = tempCCSpecialAreas;
            }
            catch (...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            if ( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Done Loading values into capctonrol - " << endl;
            }       
        }

        _isvalid = TRUE;
        _2wayFlagUpdate = FALSE;
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

        _reregisterforpoints = TRUE;
        _lastdbreloadtime.now();
        if ( !wasAlreadyRunning )
        {
            dumpAllDynamicData();
        }
        CtiTime currentDateTime = CtiTime();
        BOOL systemEnabled = FALSE;
        for(LONG h=0;h<_ccGeoAreas->size();h++)
        {
            CtiCCAreaPtr area =  (CtiCCAreaPtr)(*_ccGeoAreas).at(h);
            if (!area->getDisableFlag()) 
            {
                systemEnabled = TRUE;
                break;
            }
        }
        CtiCCExecutorFactory f;
        CtiCCExecutor*executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::SYSTEM_STATUS, systemEnabled));
        executor->Execute();
        delete executor;
        
        for(LONG i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(i);

            currentSubstationBus->getMultipleMonitorPoints().clear();
            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                BOOL peakFlag = currentSubstationBus->isPeakTime(currentDateTime);
                if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) &&
                    stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)") &&
                    (currentFeeder->getPeakStartTime() > 0 && currentFeeder->getPeakStopTime() > 0))
                {
                    currentFeeder->isPeakTime(currentDateTime);
                }
                else
                {
                    currentFeeder->setPeakTimeFlag(peakFlag);
                }

                currentFeeder->getMultipleMonitorPoints().clear();
                CtiCCCapBank_SVector& capBanks = currentFeeder->getCCCapBanks();

                for (LONG k=0;k<capBanks.size();k++) 
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)capBanks[k];

                    vector <CtiCCMonitorPointPtr>& monPoints = currentCapBank->getMonitorPoint();
                    for (LONG l=0; l<monPoints.size();l++) 
                    {
                        currentFeeder->getMultipleMonitorPoints().push_back(monPoints[l]);
                        currentSubstationBus->getMultipleMonitorPoints().push_back(monPoints[l]);
                    }
                }

            }
        }
        executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::REQUEST_ALL_DATA));
        executor->Execute();
        delete executor;
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    //if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Checking AMFM system for updates..." << endl;
    }

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection amfmConn = getConnection(1);
        if ( amfmConn.isValid() && amfmConn.isReady() )
        {
            CtiTime lastAMFMUpdateTime = gInvalidCtiTime;
            string tempStr = DefaultMasterConfigFileName;
            tempStr.erase(tempStr.length()-10);
            RWFile amfmFile((tempStr+"amfm.dat").c_str());

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
            RWDBDatabase amfmDB = getDatabase(1);
            RWDBTable dni_capacitorTable = amfmDB.table("dni_capacitor");

            RWDBSelector selector = amfmDB.selector();
            selector << dni_capacitorTable["capacitor_id"]
                     << dni_capacitorTable["circt_id_normal"]
                     << dni_capacitorTable["circt_nam_normal"]
                     << dni_capacitorTable["circt_id_current"]
                     << dni_capacitorTable["circt_name_current"]
                     << dni_capacitorTable["switch_datetime"]
                     << dni_capacitorTable["owner"]
                     << dni_capacitorTable["capacitor_name"]
                     << dni_capacitorTable["kvar_rating"]
                     << dni_capacitorTable["cap_fs"]
                     << dni_capacitorTable["cbc_model"]
                     << dni_capacitorTable["serial_no"]
                     << dni_capacitorTable["location"]
                     << dni_capacitorTable["switching_seq"]
                     << dni_capacitorTable["cap_disable_flag"]
                     << dni_capacitorTable["cap_disable_type"]
                     << dni_capacitorTable["inoperable_bad_order_equipnote"]
                     << dni_capacitorTable["open_tag_note"]
                     << dni_capacitorTable["cap_change_type"];

            selector.from(dni_capacitorTable);

            selector.where(dni_capacitorTable["datetimestamp"]>toRWDBDT(lastAMFMUpdateTime));

            selector.orderBy(dni_capacitorTable["datetimestamp"]);

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << RW2String(selector.asString()) << endl;
            }

            RWDBReader rdr = selector.reader(amfmConn);

            BOOL newAMFMChanges = FALSE;
            while( rdr() )
            {
                handleAMFMChanges(rdr);
                CtiTime datetimestamp;
                rdr["datetimestamp"] >> datetimestamp;
                if( datetimestamp>lastAMFMUpdateTime )
                {
                    lastAMFMUpdateTime = datetimestamp;
                }
                newAMFMChanges = TRUE;
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
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to get valid AMFM database connection." << endl;
        }
    }

    setReloadFromAMFMSystemFlag(FALSE);
}

/*---------------------------------------------------------------------------
    handleAMFMChanges

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::handleAMFMChanges(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    RWDBNullIndicator isNull;

    string       capacitor_id_string;
    LONG            circt_id_normal;
    string       circt_nam_normal                  = m3iAMFMNullString;
    LONG            circt_id_current                  = -1;
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
    rdr["circt_nam_normal"] >> isNull;
    if( !isNull )
    {
        rdr["circt_nam_normal"] >> circt_nam_normal;
    }
    rdr["circt_id_current"] >> isNull;
    if( !isNull )
    {
        rdr["circt_id_current"] >> circt_id_current;
    }

    rdr["circt_name_current"] >> isNull;
    if( !isNull )
    {
        rdr["circt_name_current"] >> circt_name_current;
    }

    rdr["switch_datetime"] >> isNull;
    if( !isNull )
    {
        rdr["switch_datetime"] >> switch_datetime;
    }

    rdr["owner"] >> isNull;
    if( !isNull )
    {
        rdr["owner"] >> owner;
    }

    rdr["capacitor_name"] >> isNull;
    if( !isNull )
    {
        rdr["capacitor_name"] >> capacitor_name;
    }

    rdr["kvar_rating"] >> isNull;
    if( !isNull )
    {
        rdr["kvar_rating"] >> kvar_rating;
    }

    rdr["cap_fs"] >> isNull;
    if( !isNull )
    {
        rdr["cap_fs"] >> cap_fs;
    }

    rdr["cbc_model"] >> isNull;
    if( !isNull )
    {
        rdr["cbc_model"] >> cbc_model;
    }

    rdr["serial_no"] >> isNull;
    if( !isNull )
    {
        rdr["serial_no"] >> serial_no;
    }

    rdr["location"] >> isNull;
    if( !isNull )
    {
        rdr["location"] >> location;
    }

    rdr["switching_seq"] >> isNull;
    if( !isNull )
    {
        rdr["switching_seq"] >> switching_seq;
    }

    rdr["cap_disable_flag"] >> isNull;
    if( !isNull )
    {
        rdr["cap_disable_flag"] >> cap_disable_flag;
    }

    rdr["cap_disable_type"] >> isNull;
    if( !isNull )
    {
        rdr["cap_disable_type"] >> cap_disable_type;
    }

    rdr["inoperable_bad_order_equipnote"] >> isNull;
    if( !isNull )
    {
        rdr["inoperable_bad_order_equipnote"] >> inoperable_bad_order_equipnote;
    }

    rdr["open_tag_note"] >> isNull;
    if( !isNull )
    {
        rdr["open_tag_note"] >> open_tag_note;
    }

    rdr["cap_change_type"] >> isNull;
    if( !isNull )
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
    if( !stringCompareIgnoreCase(cbc_model,"CBC5010") )
    {
        returnString = "CTI Paging";
    }
    else if( !stringCompareIgnoreCase(cbc_model,"CBC3010") )
    {
        returnString = "CTI DLC";
    }
    else if( !stringCompareIgnoreCase(cbc_model,"CBC2010") )
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
void CtiCCSubstationBusStore::feederReconfigureM3IAMFM( string& capacitor_id_string, LONG circt_id_normal,
                                                        string& circt_nam_normal, LONG circt_id_current,
                                                        string& circt_name_current, CtiTime& switch_datetime,
                                                        string& owner, string& capacitor_name, string& kvar_rating,
                                                        string& cap_fs, string& cbc_model, string& serial_no,
                                                        string& location, string& switching_seq, string& cap_disable_flag,
                                                        string& cap_disable_type, string& inoperable_bad_order_equipnote,
                                                        string& open_tag_note, string& cap_change_type )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    //LONG capacitor_id = atol(capacitor_id_string);

    BOOL found = FALSE;
    if( _ccSubstationBuses->size() > 0 )
    {
        for(LONG i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(i);

            CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.size() > 0 )
            {
                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        //LONG capMapId;
                        //if( (capMapId = atol(currentCapBank->getMapLocationId())) == capacitor_id )
                        if( currentCapBank->getMapLocationId() == capacitor_id_string )
                        {
                            LONG capswitchingorder = atol(switching_seq.c_str());
                            LONG feedMapId;
                            if( (feedMapId = atol( currentFeeder->getMapLocationId().c_str() ) ) != circt_id_current )
                            {
                                capBankMovedToDifferentFeeder(currentFeeder, currentCapBank, circt_id_current, capswitchingorder);
                                currentCCSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            else if( currentCapBank->getControlOrder() != capswitchingorder &&
                                     capswitchingorder < 11 )
                            {// if capswitchingorder 11-99 and on the same feeder ignore
                             // the amfm switching order according to a conversation with
                             // Peter Schuster and Steve Fischer of MidAmerican
                                capBankDifferentOrderSameFeeder(currentFeeder, currentCapBank, capswitchingorder);
                                currentCCSubstationBus->setBusUpdatedFlag(TRUE);
                            }

                            string tempOperationalState = currentCapBank->getOperationalState();
                            
                            std::transform(tempOperationalState.begin(),tempOperationalState.end(),tempOperationalState.begin(), ::toupper);
                            std::transform(cap_fs.begin(),cap_fs.end(),cap_fs.begin(), ::toupper);
                            std::transform(cap_disable_flag.begin(),cap_disable_flag.end(),cap_disable_flag.begin(), ::toupper);                                
                            LONG kvarrating = atol(kvar_rating.c_str());
                            bool updateCapBankFlag = false;

                            if( currentCapBank->getBankSize() != kvarrating )
                            {
                                {
                                    char tempchar[64] = "";
                                    string text = string("M3i Change, Cap Bank Bank Size: ");
                                    text += currentCapBank->getPAOName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                    text += tempchar;
                                    string additional = string("Was: ");
                                    _ltoa(currentCapBank->getBankSize(),tempchar,10);
                                    additional += tempchar;
                                    additional += ", Now: ";
                                    _ltoa(kvarrating,tempchar,10);
                                    additional += tempchar;
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPAOName();
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
                                    text += currentCapBank->getPAOName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                    text += tempchar;
                                    string additional = string("Was: ");
                                    additional += currentCapBank->getOperationalState();
                                    additional += ", Now: ";
                                    additional += tempFixedOperationalStateString;
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPAOName();
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                                    }
                                }
                                currentCapBank->setOperationalState(!stringCompareIgnoreCase(cap_fs,tempFixedOperationalStateString)?CtiCCCapBank::FixedOperationalState:CtiCCCapBank::SwitchedOperationalState);
                                updateCapBankFlag = true;
                            }
                            if( (bool)currentCapBank->getDisableFlag() != (!stringCompareIgnoreCase(cap_disable_flag,m3iAMFMDisabledString)) )
                            {
                                {
                                    char tempchar[64] = "";
                                    string text = string("M3i Change, Cap Bank Disable Flag: ");
                                    text += currentCapBank->getPAOName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                    text += tempchar;
                                    string additional = string("Was: ");
                                    additional += (currentCapBank->getDisableFlag()?m3iAMFMDisabledString:m3iAMFMEnabledString);
                                    additional += ", Now: ";
                                    additional += cap_disable_flag;
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPAOName();
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                                    }
                                }
                                currentCapBank->setDisableFlag(!stringCompareIgnoreCase(cap_disable_flag,m3iAMFMDisabledString));
                                updateCapBankFlag = true;
                            }
                            if( !stringCompareIgnoreCase(currentCapBank->getControllerType(),translateCBCModelToControllerType(cbc_model)) )
                            {
                                {
                                    char tempchar[64] = "";
                                    string text = string("M3i Change, Cap Bank Controller Type: ");
                                    text += currentCapBank->getPAOName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                    text += tempchar;
                                    string additional = string("Was: ");
                                    additional += currentCapBank->getControllerType();
                                    additional += ", Now: ";
                                    additional += translateCBCModelToControllerType(cbc_model);
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPAOName();
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                                    }
                                }
                                currentCapBank->setControllerType(translateCBCModelToControllerType(cbc_model));
                                updateCapBankFlag = true;
                            }
                            if( !stringCompareIgnoreCase(currentCapBank->getPAODescription(),location) )
                            {
                                {
                                    char tempchar[64] = "";
                                    string text = string("M3i Change, Cap Bank Location: ");
                                    text += currentCapBank->getPAOName();
                                    text += ", PAO Id: ";
                                    _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                    text += tempchar;
                                    string additional("Was: ");
                                    additional += currentCapBank->getPAODescription();
                                    additional += ", Now: ";
                                    additional += location;
                                    additional += ", on Feeder: ";
                                    additional += currentFeeder->getPAOName();
                                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                                    }
                                }
                                currentCapBank->setPAODescription(location);
                                updateCapBankFlag = true;
                            }
                            if( updateCapBankFlag )
                            {
                                UpdateCapBankInDB(currentCapBank);
                                currentCCSubstationBus->setBusUpdatedFlag(TRUE);
                            }
                            /*{
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - M3I change, 'Feeder Reconfigure' or 'AIM Import' PAO Id: "
                                         << currentCapBank->getPAOId() << ", name: "
                                         << currentCapBank->getPAOName()
                                         << ", was updated in database with bank size: "
                                         << currentCapBank->getBankSize() << ", operational state: "
                                         << currentCapBank->getOperationalState() << ", and was "
                                         << cap_disable_flag << endl;
                                }
                            }*/
                            found = TRUE;
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
void CtiCCSubstationBusStore::capBankMovedToDifferentFeeder(CtiCCFeeder* oldFeeder, CtiCCCapBank* movedCapBank, LONG feederid, LONG capswitchingorder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    CtiCCCapBank_SVector& oldFeederCapBanks = oldFeeder->getCCCapBanks();

    BOOL found = FALSE;
    for(LONG i=0;i<_ccSubstationBuses->size();i++)
    {
        CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(i);

        CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

            LONG feedMapId;
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
                        for(LONG k=0;k<newFeederCapBanks.size();k++)
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
                    text += movedCapBank->getPAOName();
                    text += ", PAO Id: ";
                    _ltoa(movedCapBank->getPAOId(),tempchar,10);
                    text += tempchar;
                    string additional = string("Moved from: ");
                    additional += oldFeeder->getPAOName();
                    additional += ", id: ";
                    _ltoa(oldFeeder->getPAOId(),tempchar,10);
                    additional += tempchar;
                    additional += " To: ";
                    additional += currentFeeder->getPAOName();
                    additional += ", id: ";
                    _ltoa(currentFeeder->getPAOId(),tempchar,10);
                    additional += tempchar;
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << text << ", " << additional << endl;
                    }
                }
                /*{
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - M3I change, 'Feeder Reconfigure' or 'AIM Import' PAO Id: "
                         << movedCapBank->getPAOId() << ", name: "
                         << movedCapBank->getPAOName()
                         << ", was moved from feeder PAO Id: "
                         << oldFeeder->getPAOId() << ", name: "
                         << oldFeeder->getPAOName() << ", to feeder PAO Id: "
                         << currentFeeder->getPAOId() << ", name: "
                         << currentFeeder->getPAOName() << ", with order: "
                         << movedCapBank->getControlOrder() << endl;
                }*/
                found = TRUE;
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
void CtiCCSubstationBusStore::capBankDifferentOrderSameFeeder(CtiCCFeeder* currentFeeder, CtiCCCapBank* currentCapBank, LONG capswitchingorder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    LONG oldControlOrder = currentCapBank->getControlOrder();

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
        text += currentCapBank->getPAOName();
        text += ", PAO Id: ";
        _ltoa(currentCapBank->getPAOId(),tempchar,10);
        text += tempchar;
        string additional = string("Moved from: ");
        _ltoa(oldControlOrder,tempchar,10);
        additional += tempchar;
        additional += ", to: ";
        _ltoa(capswitchingorder,tempchar,10);
        additional += tempchar;
        additional += ", on Feeder: ";
        additional += currentFeeder->getPAOName();
        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << text << ", " << additional << endl;
        }
    }
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - M3I change, 'Feeder Reconfigure' or 'AIM Import' PAO Id: "
             << currentCapBank->getPAOId() << ", name: "
             << currentCapBank->getPAOName()
             << ", was rearranged in feeder PAO Id: "
             << currentFeeder->getPAOId() << ", name: "
             << currentFeeder->getPAOName() << ", with order: "
             << currentCapBank->getControlOrder() << endl;
    }*/
}

/*---------------------------------------------------------------------------
    capOutOfServiceM3IAMFM

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capOutOfServiceM3IAMFM(LONG feederid, LONG capid, string& enableddisabled, string& fixedswitched)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    BOOL found = FALSE;
    if( _ccSubstationBuses->size() > 0 )
    {
        for(LONG i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(i);

            CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.size() > 0 )
            {
                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        LONG capMapId;
                        if( (capMapId = atol(currentCapBank->getMapLocationId().c_str() )) == capid )
                        {
                            std::transform(enableddisabled.begin(),enableddisabled.end(),enableddisabled.begin(), ::toupper);
                          if( (bool)currentCapBank->getDisableFlag() != (!stringCompareIgnoreCase(enableddisabled,m3iAMFMDisabledString)) )
                            {
                                currentCapBank->setDisableFlag(!stringCompareIgnoreCase(enableddisabled,m3iAMFMDisabledString));
                                UpdateCapBankDisableFlagInDB(currentCapBank);
                                currentCCSubstationBus->setBusUpdatedFlag(TRUE);
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - M3I change, 'Cap Out of Service' PAO Id: "
                                         << currentCapBank->getPAOId() << ", name: "
                                         << currentCapBank->getPAOName() << ", was "
                                         << enableddisabled << endl;
                                }
                            }
                            found = TRUE;
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
void CtiCCSubstationBusStore::feederOutOfServiceM3IAMFM(LONG feederid, string& fixedswitched, string& enableddisabled)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    BOOL found = FALSE;
    if( _ccSubstationBuses->size() > 0 )
    {
        for(LONG i=0;i<_ccSubstationBuses->size();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(i);

            CtiFeeder_vec& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.size() > 0 )
            {
                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    LONG feedMapId;
                    if( (feedMapId = atol(currentFeeder->getMapLocationId().c_str() )) == feederid )
                    {
                        std::transform(enableddisabled.begin(),enableddisabled.end(),enableddisabled.begin(), ::toupper);

                        if( (bool)currentFeeder->getDisableFlag() != (!stringCompareIgnoreCase(enableddisabled,m3iAMFMDisabledString)) )
                        {
                            currentFeeder->setDisableFlag(!stringCompareIgnoreCase(enableddisabled,m3iAMFMDisabledString));
                            UpdateFeederDisableFlagInDB(currentFeeder);
                            currentCCSubstationBus->setBusUpdatedFlag(TRUE);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - M3I change, 'Feeder Out of Service' PAO Id: "
                                     << currentFeeder->getPAOId() << ", name: "
                                     << currentFeeder->getPAOName() << ", was "
                                     << enableddisabled << endl;
                            }
                        }
                        found = TRUE;
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    dumpAllDynamicData();
    delete_vector(_ccSubstationBuses);
    _ccSubstationBuses->clear();
    delete _ccSubstationBuses;
    delete_vector(_ccSubstations);
    _ccSubstations->clear();
    delete _ccSubstations;
    delete_vector(_ccCapBankStates);
    _ccCapBankStates->clear();
    delete _ccCapBankStates;
    delete_vector(_ccGeoAreas);
    _ccGeoAreas->clear();
    delete _ccGeoAreas;
    delete_vector(_ccSpecialAreas);
    _ccSpecialAreas->clear();
    delete _ccSpecialAreas;

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
    ThreadMonitor.start(); 
    CtiTime rwnow;
    CtiTime announceTime((unsigned long) 0);
    CtiTime tickleTime((unsigned long) 0);

    while(TRUE)
    {
        rwRunnable().serviceCancellation();

        CtiTime currentTime;
        currentTime.now();

        if( currentTime.seconds() >= lastPeriodicDatabaseRefresh.seconds()+refreshrate )
        {
            //if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Periodic restore of substation list from the database" << endl;
            }

            dumpAllDynamicData();
            setValid(FALSE);

            lastPeriodicDatabaseRefresh = CtiTime();
        }
        else
        {
            rwRunnable().sleep(500);
        }
       rwnow = rwnow.now();
        if(rwnow.seconds() > tickleTime.seconds())
        {
            tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
            if( rwnow.seconds() > announceTime.seconds() )
            {
                announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " CapControl doResetThr. TID: " << rwThreadId() << endl;
            }

           /* if(!_shutdownOnThreadTimeout)
            { */
                ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl doResetThr", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::periodicComplain, 0) );
            /*}
            else
            {   
                ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl doResetThr", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::sendUserQuit, CTIDBG_new string("CapControl doResetThr")) );
            } */
        }  

    }

    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl doResetThr", CtiThreadRegData::LogOut ) );
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
    ThreadMonitor.start(); 

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

    if( !stringCompareIgnoreCase(amfm_interface,m3iAMFMInterfaceString) )
    {
        int refreshrate = 3600;
        string dbDll = "none";
        string dbName = "none";
        string dbUser = "none";
        string dbPassword = "none";

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

        std::strcpy(var, "CAP_CONTROL_AMFM_DB_RWDBDLL");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            dbDll = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << dbDll << endl;
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
            dbName = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << dbName << endl;
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
            dbUser = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << dbUser << endl;
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
            dbPassword = str.c_str();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << dbPassword << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }


        if( dbDll != "none" && dbName != "none" && dbUser != "none" && dbPassword != "none" )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Obtaining connection to the AMFM database..." << endl;
            }
            setDatabaseParams(1,dbDll,dbName,dbUser,dbPassword);

            time_t start = ::time(NULL);

            CtiTime currenttime = CtiTime();
            ULONG tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+(2*refreshrate);
            CtiTime nextAMFMRefresh = CtiTime(CtiTime(tempsum));

            CtiTime rwnow;
            CtiTime announceTime((unsigned long) 0);
            CtiTime tickleTime((unsigned long) 0);


            while(TRUE)
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
                    setReloadFromAMFMSystemFlag(TRUE);

                    currenttime = CtiTime();
                    tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+refreshrate;
                    nextAMFMRefresh = CtiTime(CtiTime(tempsum));
                }
                else
                {
                    rwRunnable().sleep(500);
                }
               /* rwnow = rwnow.now();
                if(rwnow.seconds() > tickleTime.seconds())
                {
                    tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                    if( rwnow.seconds() > announceTime.seconds() )
                    {
                        announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " CapControl doAMFMThr. TID: " << rwThreadId() << endl;
                    }

                   /* if(!_shutdownOnThreadTimeout)
                    {
                        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl doAMFMThr", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::periodicComplain, 0) );
                    }
                    else
                    {   
                        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl doAMFMThr", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::sendUserQuit, CTIDBG_new string("CapControl doAMFMThr")) );
                    //}
                } */

            }

         //   ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl doAMFMThr", CtiThreadRegData::LogOut ) );
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
    isValid

    Returns a TRUE if the strategystore was able to initialize properly
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBusStore::isValid()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _isvalid;
}

/*---------------------------------------------------------------------------
    setValid

    Sets the _isvalid flag
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setValid(BOOL valid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _isvalid = valid;
}

/*---------------------------------------------------------------------------
    getReregisterForPoints

    Gets _reregisterforpoints
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBusStore::getReregisterForPoints()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _reregisterforpoints;
}

/*---------------------------------------------------------------------------
    setReregisterForPoints

    Sets _reregisterforpoints
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setReregisterForPoints(BOOL reregister)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _reregisterforpoints = reregister;
}

/*---------------------------------------------------------------------------
    getReloadFromAMFMSystemFlag

    Gets _reloadfromamfmsystemflag
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBusStore::getReloadFromAMFMSystemFlag()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _reloadfromamfmsystemflag;
}

/*---------------------------------------------------------------------------
    setReloadFromAMFMSystemFlag

    Sets _reloadfromamfmsystemflag
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setReloadFromAMFMSystemFlag(BOOL reload)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _reloadfromamfmsystemflag = reload;
}

BOOL CtiCCSubstationBusStore::getWasSubBusDeletedFlag()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _wassubbusdeletedflag;
}

void CtiCCSubstationBusStore::setWasSubBusDeletedFlag(BOOL wasDeleted)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _wassubbusdeletedflag = wasDeleted;
}

BOOL CtiCCSubstationBusStore::get2wayFlagUpdate()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _2wayFlagUpdate;
}

void CtiCCSubstationBusStore::set2wayFlagUpdate(BOOL flag)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    for(int i=0;i<_ccSubstationBuses->size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)((*_ccSubstationBuses).at(i));

        LONG numberOfFeedersRecentlyControlled = 0;
        LONG numberOfCapBanksPending = 0;

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
                        currentFeeder->setRecentlyControlledFlag(FALSE);
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Feeder: " << currentFeeder->getPAOName() << ", no longer recently controlled because no banks were pending in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                    else if( numberOfCapBanksPending > 1 )
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Multiple cap banks pending in Feeder: " << currentFeeder->getPAOName() << ", setting status to questionable in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        for(int k=0;k<ccCapBanks.size();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending && currentCapBank->getStatusPointId() > 0)
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Setting status to close questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                                currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                                currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                                currentCapBank->setAfterVarsString("abnormal data");
                                currentCapBank->setPercentChangeString(" --- ");
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::CloseQuestionable, "Var: Multiple banks pending, CloseQuestionable", "cap control");
                                eventMsg->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));
                                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);

                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && currentCapBank->getStatusPointId() > 0)
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Setting status to open questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                                currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                                currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                                currentCapBank->setAfterVarsString("abnormal data");
                                currentCapBank->setPercentChangeString(" --- ");
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::OpenQuestionable, "Var: Multiple banks pending, OpenQuestionable", "cap control");
                                eventMsg->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));
                                eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);
                            }
                        }
                    }
                    else if (currentFeeder->getPostOperationMonitorPointScanFlag())
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Feeder: " << currentFeeder->getPAOName() << " waiting for Post Op Monitor Scan " << endl;
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
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName() << " questionable because feeder is not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::CloseQuestionable, "Var: Feeder not recently controlled, CloseQuestionable", "cap control");
                            eventMsg->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);
                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && currentCapBank->getStatusPointId() > 0)
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName() << " questionable because feeder is not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));

                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::OpenQuestionable, "Var: Feeder not recently controlled, OpenQuestionable", "cap control");
                            eventMsg->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);
                        }
                    }
                }
            }

            if( numberOfFeedersRecentlyControlled == 0 )
            {
                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                currentSubstationBus->setWaitToFinishRegularControlFlag(FALSE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
            }
        }
        else if (currentSubstationBus->getPostOperationMonitorPointScanFlag()) 
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Sub: " << currentSubstationBus->getPAOName() << " waiting for Post Op Monitor Scan " << endl;
            }
        }
        else//sub bus not recently controlled
        {

            if (!currentSubstationBus->getPerformingVerificationFlag())
            {

                currentSubstationBus->setWaitToFinishRegularControlFlag(FALSE);
                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                for(int j=0;j<ccFeeders.size();j++)
                {
                    numberOfCapBanksPending = 0;
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                    if( currentFeeder->getRecentlyControlledFlag() )
                    {
                        currentFeeder->setRecentlyControlledFlag(FALSE);
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Feeder: " << currentFeeder->getPAOName() << ", no longer recently controlled because sub bus not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
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
                                dout << CtiTime() << " - Setting status to close questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));

                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::CloseQuestionable, "Var: Sub not recently controlled, CloseQuestionable", "cap control");
                            eventMsg->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));
                            eventMsg->setStateInfo(currentCapBank->getControlStatusQualityString());
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg);

                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && !(currentCapBank->getVerificationFlag() && currentCapBank->getPerformingVerificationFlag()) && currentCapBank->getStatusPointId() > 0)
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Setting status to open questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                            currentCapBank->setAfterVarsString("abnormal data");
                            currentCapBank->setPercentChangeString(" --- ");
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType, "cap control",  TAG_POINT_FORCE_UPDATE));
                            
                            CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), CtiCCCapBank::OpenQuestionable, "Var: Sub not recently controlled, OpenQuestionable", "cap control");
                            eventMsg->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    for(int i=0;i<_ccSubstationBuses->size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)((*_ccSubstationBuses).at(i));
        {
            char tempchar[64] = "";
            string text("Daily Operations were ");
            _ltoa(currentSubstationBus->getCurrentDailyOperations(),tempchar,10);
            text += tempchar;
            string additional("Sub Bus: ");
            additional += currentSubstationBus->getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent, "cap control"));
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << text << ", " << additional << endl;
            }

            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlSetOperationCount, 1, currentSubstationBus->getCurrentDailyOperations(), text, "cap control"));
              
        }
        currentSubstationBus->setCurrentDailyOperations(0);
        currentSubstationBus->setMaxDailyOpsHitFlag(FALSE);
        currentSubstationBus->setBusUpdatedFlag(TRUE);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(int j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            /*{
                char tempchar[64] = "";
                string text("Daily Operations were ");
                _ltoa(currentFeeder->getCurrentDailyOperations(),tempchar,10);
                text += tempchar;
                string additional("Feeder: ");
                additional += currentFeeder->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
            }*/
            currentFeeder->setCurrentDailyOperations(0);
            currentFeeder->setMaxDailyOpsHitFlag(FALSE);

            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            //**********************************************************************
            //The operation count on a cap bank is actually a total not a daily, doh
            //**********************************************************************
            for(int k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                /*{
                    char tempchar[64] = "";
                    string text("Daily Operations were ");
                    _ltoa(currentCapBank->getCurrentDailyOperations(),tempchar,10);
                    text += tempchar;
                    string additional("Cap Bank: ");
                    additional += currentCapBank->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
                }*/
                currentCapBank->setCurrentDailyOperations(0);
                currentCapBank->setMaxDailyOpsHitFlag(FALSE);
            }
            //**********************************************************************
            //The operation count on a cap bank is actually a total not a daily, doh
            //**********************************************************************
        }
    }
}

/*---------------------------------------------------------------------------
    UpdateBusVerificationFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the substation bus.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateBusVerificationFlagsInDB(CtiCCSubstationBus* bus)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        if (conn.isValid()) 
        {
            RWDBTable dynamicSubBusTable = getDatabase().table("dynamicccsubstationbus");
            RWDBUpdater updater = dynamicSubBusTable.updater();

            updater.where( dynamicSubBusTable["paobjectid"] == bus->getPAOId() );

            updater << dynamicSubBusTable["verificationflag"].assign( (bus->getVerificationFlag()?"Y":"N") );

            updater.execute( conn );

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(bus->getPAOId(), ChangePAODb,
                                                      bus->getPAOCategory(), bus->getPAOType(),
                                                      ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return updater.status().isValid();
        }
    }
    return false;
}




/*---------------------------------------------------------------------------
    UpdateBusDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the substation bus.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateAreaDisableFlagInDB(CtiCCArea* area)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        if (conn.isValid()) 
        {
            RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
            RWDBUpdater updater = yukonPAObjectTable.updater();

            updater.where( yukonPAObjectTable["paobjectid"] == area->getPAOId() );

            updater << yukonPAObjectTable["disableflag"].assign( (area->getDisableFlag()?"Y":"N") );

            updater.execute( conn );

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(area->getPAOId(), ChangePAODb,
                                                          area->getPAOCategory(), area->getPAOType(),
                                                          ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return updater.status().isValid();
        }
    }
    return false;
} 

/*---------------------------------------------------------------------------
    UpdateBusDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the substation bus.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateSpecialAreaDisableFlagInDB(CtiCCSpecial* area)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        if (conn.isValid()) 
        {
            RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
            RWDBUpdater updater = yukonPAObjectTable.updater();

            updater.where( yukonPAObjectTable["paobjectid"] == area->getPAOId() );

            updater << yukonPAObjectTable["disableflag"].assign( (area->getDisableFlag()?"Y":"N") );

            updater.execute( conn );

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(area->getPAOId(), ChangePAODb,
                                                          area->getPAOCategory(), area->getPAOType(),
                                                          ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE2);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return updater.status().isValid();
        }
    }
    return false;
} 

/*---------------------------------------------------------------------------
    UpdateBusDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the substation bus.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateSubstationDisableFlagInDB(CtiCCSubstation* station)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        if (conn.isValid()) 
        {
            RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
            RWDBUpdater updater = yukonPAObjectTable.updater();

            updater.where( yukonPAObjectTable["paobjectid"] == station->getPAOId() );

            updater << yukonPAObjectTable["disableflag"].assign( (station->getDisableFlag()?"Y":"N") );

            updater.execute( conn );

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(station->getPAOId(), ChangePAODb,
                                                          station->getPAOCategory(), station->getPAOType(),
                                                          ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return updater.status().isValid();
        }
    }
    return false;
} 


/*---------------------------------------------------------------------------
    UpdateBusDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the substation bus.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateBusDisableFlagInDB(CtiCCSubstationBus* bus)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        if (conn.isValid()) 
        {
            RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
            RWDBUpdater updater = yukonPAObjectTable.updater();

            updater.where( yukonPAObjectTable["paobjectid"] == bus->getPAOId() );

            updater << yukonPAObjectTable["disableflag"].assign( (bus->getDisableFlag()?"Y":"N") );

            updater.execute( conn );

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(bus->getPAOId(), ChangePAODb,
                                                          bus->getPAOCategory(), bus->getPAOType(),
                                                          ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return updater.status().isValid();
        }
    }
    return false;
} 

/*---------------------------------------------------------------------------
    UpdateFeederDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the feeder.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateFeederDisableFlagInDB(CtiCCFeeder* feeder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        if (conn.isValid()) 
        {
            RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
            RWDBUpdater updater = yukonPAObjectTable.updater();

            updater.where( yukonPAObjectTable["paobjectid"] == feeder->getPAOId() );

            updater << yukonPAObjectTable["disableflag"].assign( (feeder->getDisableFlag()?"Y":"N") );

            updater.execute( conn );

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(feeder->getPAOId(), ChangePAODb,
                                                          feeder->getPAOCategory(), feeder->getPAOType(),
                                                          ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return updater.status().isValid();
        }
    }
    return false;
}

/*---------------------------------------------------------------------------
    UpdateCapBankDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the cap bank.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateCapBankDisableFlagInDB(CtiCCCapBank* capbank)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        if (conn.isValid()) 
        {
            RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
            RWDBUpdater updater = yukonPAObjectTable.updater();

            updater.where( yukonPAObjectTable["paobjectid"] == capbank->getPAOId() );

            updater << yukonPAObjectTable["disableflag"].assign( (capbank->getDisableFlag()?"Y":"N") );

            updater.execute( conn );

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(capbank->getPAOId(), ChangePAODb,
                                                          capbank->getPAOCategory(),
                                                          capbank->getPAOType(),
                                                          ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return updater.status().isValid();
        }
    }
    return false;
}

/*---------------------------------------------------------------------------
    UpdateCapBankOperationalStateInDB

    Updates the operational state in the yukonpaobject table in the database for
    the cap bank.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateCapBankOperationalStateInDB(CtiCCCapBank* capbank)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        if (conn.isValid()) 
        {
            RWDBTable yukonPAObjectTable = getDatabase().table("capbank");
            RWDBUpdater updater = yukonPAObjectTable.updater();

            updater.where( yukonPAObjectTable["deviceid"] == capbank->getPAOId() );

            updater << yukonPAObjectTable["operationalstate"].assign( capbank->getOperationalState().c_str() );

            updater.execute( conn );

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(capbank->getPAOId(), ChangePAODb,
                                                          capbank->getPAOCategory(),
                                                          capbank->getPAOType(),
                                                          ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return updater.status().isValid();
        }
    }
    return false;
}

/*---------------------------------------------------------------------------
    UpdateCapBankInDB

    Updates multiple fields in tables associated with cap banks in the
    database.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateCapBankInDB(CtiCCCapBank* capbank)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        if (conn.isValid()) 
        {
            RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
            RWDBUpdater updater = yukonPAObjectTable.updater();

            updater.where( yukonPAObjectTable["paobjectid"] == capbank->getPAOId() );

            updater << yukonPAObjectTable["paoname"].assign( capbank->getPAOName().c_str() )
                    << yukonPAObjectTable["disableflag"].assign( (capbank->getDisableFlag()?"Y":"N") )
                    << yukonPAObjectTable["description"].assign( capbank->getPAODescription().c_str() );

            updater.execute( conn );

            RWDBTable capBankTable = getDatabase().table("capbank");
            updater = capBankTable.updater();

            updater.where( capBankTable["deviceid"] == capbank->getPAOId() );

            updater << capBankTable["banksize"].assign( capbank->getBankSize() )
                    << capBankTable["operationalstate"].assign( capbank->getOperationalState().c_str() );

            updater.execute( conn );

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(capbank->getPAOId(), ChangePAODb,
                                                          capbank->getPAOCategory(),
                                                          capbank->getPAOType(),
                                                          ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return updater.status().isValid();
        }
    }
    return false;
}

/*---------------------------------------------------------------------------
    UpdateFeederBankListInDB

    Updates multiple fields in tables associated with cap banks in the
    database.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateFeederBankListInDB(CtiCCFeeder* feeder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        if (conn.isValid()) 
        {
        
            RWDBTable ccFeederBankListTable = getDatabase().table("ccfeederbanklist");
            RWDBDeleter deleter = ccFeederBankListTable.deleter();

            deleter.where( ccFeederBankListTable["feederid"] == feeder->getPAOId() );

            deleter.execute( conn );


            RWDBInserter inserter = ccFeederBankListTable.inserter();

            CtiCCCapBank_SVector& ccCapBanks = feeder->getCCCapBanks();
            for(LONG i=0;i<ccCapBanks.size();i++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[i];

                inserter << feeder->getPAOId()
                         << currentCapBank->getPAOId()
                         << currentCapBank->getControlOrder()
                         << currentCapBank->getCloseOrder()
                         << currentCapBank->getTripOrder();

                inserter.execute( conn );
            }

            CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(feeder->getPAOId(), ChangePAODb,
                                                          feeder->getPAOCategory(),
                                                          feeder->getPAOType(),
                                                          ChangeTypeUpdate);
            dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
            CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

            return inserter.status().isValid();
        }
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    {
        INT logId = CCEventLogIdGen();
        
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        if (conn.isValid()) 
        {
            RWDBTable ccEventLogTable = getDatabase().table("cceventlog");
            RWDBInserter inserter = ccEventLogTable.inserter();
            inserter << logId
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
                     << msg->getCVar();

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << inserter.asString().data() << endl;
            }
            
            inserter.execute( conn );

            if(inserter.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
            }

            return inserter.status().isValid();
        }
    }
    return false;
}







/*---------------------------------------------------------------------------
    reloadStrategyFromDataBase

    Reloads all or single strategy from strategy table in the database.  Updates
    SubStationBus/Feeder Strategy values if a single strategyId is specified.  
    StrategyId = 0 indicates reloaded all strategies from db.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadStrategyFromDataBase(long strategyId, map< long, CtiCCStrategyPtr > *strategy_map)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());


    CtiCCStrategyPtr strategyToUpdate = NULL;
    try
    {
        if (strategyId >= 0)
        {   
            strategyToUpdate = findStrategyByStrategyID(strategyId);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        if (strategyToUpdate != NULL)
        {
            deleteStrategy(strategyId);
        }

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        {

            if ( conn.isValid() )
            {   
                
                CtiTime currentDateTime;
                RWDBDatabase db = getDatabase();
                RWDBTable capControlStrategy = db.table("capcontrolstrategy");
                RWDBTable ccScheduleStrat = db.table("ccseasonstrategyassignment");
                RWDBTable dateOfSeason = db.table("dateofseason");
                RWDBTable capControlSubstationBusTable = db.table("capcontrolsubstationbus");
                RWDBTable capControlFeederTable = db.table("capcontrolfeeder");
                RWDBTable capControlAreaTable = db.table("capcontrolarea");
                RWDBTable capControlSpecialAreaTable = db.table("capcontrolspecialarea");

                /************************************************************** 
                ******  Loading Strategies                               ****** 
                **************************************************************/ 
                RWDBSelector selector = db.selector();
                selector << capControlStrategy["strategyid"]
                << capControlStrategy["strategyname"]
                << capControlStrategy["controlmethod"]
                << capControlStrategy["maxdailyoperation"]
                << capControlStrategy["maxoperationdisableflag"]
                << capControlStrategy["peakstarttime"]
                << capControlStrategy["peakstoptime"]
                << capControlStrategy["controlinterval"]
                << capControlStrategy["minresponsetime"]//will become "minconfirmtime" in the DB in 3.1
                << capControlStrategy["minconfirmpercent"]
                << capControlStrategy["failurepercent"]
                << capControlStrategy["daysofweek"]
                << capControlStrategy["controlunits"]
                << capControlStrategy["controldelaytime"]
                << capControlStrategy["controlsendretries"]
                << capControlStrategy["peaklag"]
                << capControlStrategy["peaklead"]
                << capControlStrategy["offpklag"]
                << capControlStrategy["offpklead"] 
                << capControlStrategy["peakvarlag"]  
                << capControlStrategy["peakvarlead"] 
                << capControlStrategy["offpkvarlag"] 
                << capControlStrategy["offpkvarlead"]
                << capControlStrategy["peakpfsetpoint"] 
                << capControlStrategy["offpkpfsetpoint"]
                << capControlStrategy["integrateflag"] 
                << capControlStrategy["integrateperiod"]
                << capControlStrategy["likedayfallback"];
                

                selector.from(capControlStrategy);
                if (strategyId >= 0)
                    selector.where(capControlStrategy["strategyid"]==strategyId );


                if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);    
                    dout << CtiTime() << " - " << selector.asString().data() << endl;
                }
                RWDBReader rdr = selector.reader(conn);

                CtiCCStrategyPtr currentCCStrategy;


                while ( rdr() )
                {                
                    currentCCStrategy = CtiCCStrategyPtr(new CtiCCStrategy(rdr));
                    strategy_map->insert(make_pair(currentCCStrategy->getStrategyId(),currentCCStrategy));
                }
                reloadTimeOfDayStrategyFromDataBase(strategyId, strategy_map);

                if (strategyId > 0)
                {    
                    //Update Area Strategy Values with dbChanged/editted strategy values
                    {
                        RWDBSelector selector = db.selector();
                                     selector <<  ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlSpecialAreaTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         selector.where( ccScheduleStrat["paobjectid"]==capControlSpecialAreaTable["areaid"] &&
                                        ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                        ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] &&
                                        ccScheduleStrat["strategyid"] == strategyId );

                         if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);    
                             dout << CtiTime() << " - " << selector.asString().data() << endl;
                         }
                         RWDBReader rdr = selector.reader(conn);
                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year())  )
                             { 
                                 long areaId, stratId;
                                 rdr["paobjectid"] >> areaId;
                                 rdr["strategyid"] >> stratId;

                                 CtiCCSpecialPtr currentSpArea = NULL; 

                                 currentSpArea = findSpecialAreaByPAObjectID(areaId);
                                 if (currentSpArea != NULL) 
                                 {
                                     CtiCCStrategyPtr currentCCStrategy = NULL;

                                     currentCCStrategy = findStrategyByStrategyID(stratId);
                                     if (currentCCStrategy == NULL)
                                     {
                                         currentSpArea->setStrategyId(0);
                                         currentCCStrategy = findStrategyByStrategyID(0);
                                     }
                                     currentSpArea->setStrategyId(currentCCStrategy->getStrategyId());
                                     currentSpArea->setStrategyValues(currentCCStrategy);
                                 }
                             }
                         }
                    }

                    //Update Area Strategy Values with dbChanged/editted strategy values
                    {
                        RWDBSelector selector = db.selector();
                                     selector <<  ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlAreaTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         selector.where( ccScheduleStrat["paobjectid"]==capControlAreaTable["areaid"] &&
                                        ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                        ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] &&
                                        ccScheduleStrat["strategyid"] == strategyId );

                         if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);    
                             dout << CtiTime() << " - " << selector.asString().data() << endl;
                         }
                         RWDBReader rdr = selector.reader(conn);
                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year())  )
                             { 
                                 long areaId, stratId;
                                 rdr["paobjectid"] >> areaId;
                                 rdr["strategyid"] >> stratId;

                                 CtiCCAreaPtr currentArea = NULL; 

                                 currentArea = findAreaByPAObjectID(areaId);
                                 if (currentArea != NULL) 
                                 {
                                     CtiCCStrategyPtr currentCCStrategy = NULL;

                                     currentCCStrategy = findStrategyByStrategyID(stratId);
                                     if (currentCCStrategy == NULL)
                                     {
                                         currentArea->setStrategyId(0);
                                         currentCCStrategy = findStrategyByStrategyID(0);
                                     }
                                     currentArea->setStrategyId(currentCCStrategy->getStrategyId());
                                     currentArea->setStrategyValues(currentCCStrategy);
                                 }
                             }
                         }
                    }


                    //Update SubstationBus Strategy Values with dbChanged/editted strategy values
                    {
                        RWDBSelector selector = db.selector();
                                     selector <<  ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlSubstationBusTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         selector.where(ccScheduleStrat["paobjectid"]==capControlSubstationBusTable["substationbusid"]  &&
                                        ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                        ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] &&
                                        ccScheduleStrat["strategyid"] == strategyId );

                         if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);    
                             dout << CtiTime() << " - " << selector.asString().data() << endl;
                         }
                         RWDBReader rdr = selector.reader(conn);
                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year())  )
                             { 
                                 long busId, stratId;
                                 rdr["paobjectid"] >> busId;
                                 rdr["strategyid"] >> stratId;

                                 CtiCCSubstationBusPtr currentCCSubstationBus = NULL; 

                                 currentCCSubstationBus = findSubBusByPAObjectID(busId);
                                 if (currentCCSubstationBus != NULL) 
                                 {
                                     CtiCCStrategyPtr currentCCStrategy = NULL;

                                     CtiCCSubstationPtr currentStation = NULL;
                                     currentStation = findSubstationByPAObjectID(currentCCSubstationBus->getParentId());
                                     if (currentStation != NULL)
                                     {
                                         if (currentStation->getSaEnabledFlag())
                                         {
                                             CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(currentStation->getSaEnabledId());
                                             if (spArea != NULL)
                                                 currentCCStrategy =  strategy_map->find(spArea->getStrategyId())->second;
                                         }
                                         else
                                         {
                                             currentCCStrategy = findStrategyByStrategyID(stratId);
                                         }
                                         if (currentCCStrategy == NULL)
                                         {
                                             currentCCSubstationBus->setStrategyId(0);
                                             currentCCStrategy = findStrategyByStrategyID(0);
                                         }
                                         currentCCSubstationBus->setStrategyId(currentCCStrategy->getStrategyId());
                                         currentCCSubstationBus->setStrategyValues(currentCCStrategy);
                                         for (int i = 0; i < currentCCSubstationBus->getCCFeeders().size(); i++ )
                                         {
                                             ((CtiCCFeederPtr)currentCCSubstationBus->getCCFeeders()[i])->setStrategyValues(currentCCStrategy);
                                             ((CtiCCFeederPtr)currentCCSubstationBus->getCCFeeders()[i])->setStrategyId(0);
                                             ((CtiCCFeederPtr)currentCCSubstationBus->getCCFeeders()[i])->setStrategyName("(none)");
                                         }
                                     }
                                 }
                             }
                         }
                    }

                    {
                        RWDBSelector selector = db.selector();
                                     selector <<  ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlFeederTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         selector.where(ccScheduleStrat["paobjectid"]==capControlFeederTable["feederid"]  &&
                                        ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                        ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] &&
                                        ccScheduleStrat["strategyid"] == strategyId);
                         if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);    
                             dout << CtiTime() << " - " << selector.asString().data() << endl;
                         }
                         RWDBReader rdr = selector.reader(conn);

                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year()) )
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
                                     CtiCCStrategyPtr currentCCStrategy = NULL;

                                     currentSubBus = findSubBusByPAObjectID(currentCCFeeder->getParentId());
                                     if (currentSubBus != NULL)
                                     {
                                         currentStation = findSubstationByPAObjectID(currentSubBus->getParentId());
                                         if (currentStation != NULL)
                                         {
                                             if (currentStation->getSaEnabledFlag())
                                             {
                                                 CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(currentStation->getSaEnabledId());
                                                  if (spArea != NULL)
                                                      currentCCStrategy =  strategy_map->find(spArea->getStrategyId())->second;
                                            }
                                             else
                                             {
                                                 currentCCStrategy = findStrategyByStrategyID(stratId);
                                             }
                                         }

                                     }
                                     if (currentCCStrategy == NULL)
                                     {
                                         currentCCFeeder->setStrategyId(0);
                                         currentCCStrategy = findStrategyByStrategyID(0);
                                     }
                                     currentCCFeeder->setStrategyValues(currentCCStrategy);

                                 }
                             }
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
void CtiCCSubstationBusStore::reloadTimeOfDayStrategyFromDataBase(long strategyId, map< long, CtiCCStrategyPtr > *strategy_map)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());


    CtiCCStrategyPtr strategyToUpdate = NULL;
    try
    {
        if (strategyId >= 0)
        {   
            strategyToUpdate = findStrategyByStrategyID(strategyId);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        {

            if ( conn.isValid() )
            {   
                
                CtiTime currentDateTime;
                RWDBDatabase db = getDatabase();
                RWDBTable capControlStrategy = db.table("capcontrolstrategy");
                RWDBTable capControlTimeOfDayStrategy = db.table("ccstrategytimeofday");
                RWDBTable ccScheduleStrat = db.table("ccseasonstrategyassignment");
                RWDBTable dateOfSeason = db.table("dateofseason");
                RWDBTable capControlSubstationBusTable = db.table("capcontrolsubstationbus");
                RWDBTable capControlFeederTable = db.table("capcontrolfeeder");
                RWDBTable capControlAreaTable = db.table("capcontrolarea");
                RWDBTable capControlSpecialAreaTable = db.table("capcontrolspecialarea");

                /************************************************************** 
                ******  Loading Strategies                               ****** 
                **************************************************************/ 
                RWDBSelector selector = db.selector();
                selector << capControlStrategy["strategyid"]
                << capControlStrategy["controlmethod"]
                << capControlTimeOfDayStrategy["starttimeseconds"]
                << capControlTimeOfDayStrategy["percentclose"];
                

                selector.from(capControlStrategy);
                selector.from(capControlTimeOfDayStrategy);
                if (strategyId >= 0)
                    selector.where(capControlStrategy["strategyid"]==strategyId &&
                                   capControlStrategy["strategyid"]==capControlTimeOfDayStrategy["strategyid"] &&
                                   capControlStrategy["controlmethod"]=="timeofday" );
                else
                    selector.where(capControlStrategy["strategyid"]==capControlTimeOfDayStrategy["strategyid"] &&
                                   capControlStrategy["controlmethod"]=="timeofday" );



                if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);    
                    dout << CtiTime() << " - " << selector.asString().data() << endl;
                }
                RWDBReader rdr = selector.reader(conn);

                CtiCCStrategyPtr currentCCStrategy;

                long stratId = 0;

                while ( rdr() )
                {                
                    rdr["strategyid"] >> stratId;
                    currentCCStrategy =  strategy_map->find(stratId)->second;
                    if (currentCCStrategy != NULL)
                    {
                        currentCCStrategy->setTimeAndCloseValues(rdr);
                    }

                    //currentCCStrategy->dumpTimeOfDayControllers();
                }


                if (strategyId > 0)
                {    
                    //Update Area Strategy Values with dbChanged/editted strategy values
                    {
                        RWDBSelector selector = db.selector();
                                     selector <<  ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlSpecialAreaTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         selector.where( ccScheduleStrat["paobjectid"]==capControlSpecialAreaTable["areaid"] &&
                                        ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                        ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] &&
                                        ccScheduleStrat["strategyid"] == strategyId );

                         if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);    
                             dout << CtiTime() << " - " << selector.asString().data() << endl;
                         }
                         RWDBReader rdr = selector.reader(conn);
                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year())  )
                             { 
                                 long areaId, stratId;
                                 rdr["paobjectid"] >> areaId;
                                 rdr["strategyid"] >> stratId;

                                 CtiCCSpecialPtr currentSpArea = NULL; 

                                 currentSpArea = findSpecialAreaByPAObjectID(areaId);
                                 if (currentSpArea != NULL) 
                                 {
                                     CtiCCStrategyPtr currentCCStrategy = NULL;

                                     currentCCStrategy = findStrategyByStrategyID(stratId);
                                     if (currentCCStrategy == NULL)
                                     {
                                         currentSpArea->setStrategyId(0);
                                         currentCCStrategy = findStrategyByStrategyID(0);
                                     }
                                     currentSpArea->setStrategyId(currentCCStrategy->getStrategyId());
                                     currentSpArea->setStrategyValues(currentCCStrategy);
                                 }
                             }
                         }
                    }

                    //Update Area Strategy Values with dbChanged/editted strategy values
                    {
                        RWDBSelector selector = db.selector();
                                     selector <<  ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlAreaTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         selector.where( ccScheduleStrat["paobjectid"]==capControlAreaTable["areaid"] &&
                                        ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                        ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] &&
                                        ccScheduleStrat["strategyid"] == strategyId );

                         if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);    
                             dout << CtiTime() << " - " << selector.asString().data() << endl;
                         }
                         RWDBReader rdr = selector.reader(conn);
                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year())  )
                             { 
                                 long areaId, stratId;
                                 rdr["paobjectid"] >> areaId;
                                 rdr["strategyid"] >> stratId;

                                 CtiCCAreaPtr currentArea = NULL; 

                                 currentArea = findAreaByPAObjectID(areaId);
                                 if (currentArea != NULL) 
                                 {
                                     CtiCCStrategyPtr currentCCStrategy = NULL;

                                     currentCCStrategy = findStrategyByStrategyID(stratId);
                                     if (currentCCStrategy == NULL)
                                     {
                                         currentArea->setStrategyId(0);
                                         currentCCStrategy = findStrategyByStrategyID(0);
                                     }
                                     currentArea->setStrategyId(currentCCStrategy->getStrategyId());
                                     currentArea->setStrategyValues(currentCCStrategy);
                                 }
                             }
                         }
                    }


                    //Update SubstationBus Strategy Values with dbChanged/editted strategy values
                    {
                        RWDBSelector selector = db.selector();
                                     selector <<  ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlSubstationBusTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         selector.where(ccScheduleStrat["paobjectid"]==capControlSubstationBusTable["substationbusid"]  &&
                                        ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                        ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] &&
                                        ccScheduleStrat["strategyid"] == strategyId );

                         if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);    
                             dout << CtiTime() << " - " << selector.asString().data() << endl;
                         }
                         RWDBReader rdr = selector.reader(conn);
                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year())  )
                             { 
                                 long busId, stratId;
                                 rdr["paobjectid"] >> busId;
                                 rdr["strategyid"] >> stratId;

                                 CtiCCSubstationBusPtr currentCCSubstationBus = NULL; 

                                 currentCCSubstationBus = findSubBusByPAObjectID(busId);
                                 if (currentCCSubstationBus != NULL) 
                                 {
                                     CtiCCStrategyPtr currentCCStrategy = NULL;

                                     CtiCCSubstationPtr currentStation = NULL;
                                     currentStation = findSubstationByPAObjectID(currentCCSubstationBus->getParentId());
                                     if (currentStation != NULL)
                                     {
                                         if (currentStation->getSaEnabledFlag())
                                         {
                                             CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(currentStation->getSaEnabledId());
                                             if (spArea != NULL)
                                                 currentCCStrategy =  strategy_map->find(spArea->getStrategyId())->second;
                                         }
                                         else
                                         {
                                             currentCCStrategy = findStrategyByStrategyID(stratId);
                                         }
                                         if (currentCCStrategy == NULL)
                                         {
                                             currentCCSubstationBus->setStrategyId(0);
                                             currentCCStrategy = findStrategyByStrategyID(0);
                                         }
                                         currentCCSubstationBus->setStrategyId(currentCCStrategy->getStrategyId());
                                         currentCCSubstationBus->setStrategyValues(currentCCStrategy);
                                         currentCCSubstationBus->figureNextCheckTime();
                                         for (int i = 0; i < currentCCSubstationBus->getCCFeeders().size(); i++ )
                                         {
                                             ((CtiCCFeederPtr)currentCCSubstationBus->getCCFeeders()[i])->setStrategyValues(currentCCStrategy);
                                             ((CtiCCFeederPtr)currentCCSubstationBus->getCCFeeders()[i])->setStrategyId(0);
                                             ((CtiCCFeederPtr)currentCCSubstationBus->getCCFeeders()[i])->setStrategyName("(none)");
                                         }
                                     }
                                 }
                             }
                         }
                    }

                    {
                        RWDBSelector selector = db.selector();
                                     selector <<  ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlFeederTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         selector.where(ccScheduleStrat["paobjectid"]==capControlFeederTable["feederid"]  &&
                                        ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                        ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] &&
                                        ccScheduleStrat["strategyid"] == strategyId);
                         if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);    
                             dout << CtiTime() << " - " << selector.asString().data() << endl;
                         }
                         RWDBReader rdr = selector.reader(conn);

                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year()) )
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
                                     CtiCCStrategyPtr currentCCStrategy = NULL;

                                     currentSubBus = findSubBusByPAObjectID(currentCCFeeder->getParentId());
                                     if (currentSubBus != NULL)
                                     {
                                         currentStation = findSubstationByPAObjectID(currentSubBus->getParentId());
                                         if (currentStation != NULL)
                                         {
                                             if (currentStation->getSaEnabledFlag())
                                             {
                                                 CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(currentStation->getSaEnabledId());
                                                  if (spArea != NULL)
                                                      currentCCStrategy =  strategy_map->find(spArea->getStrategyId())->second;
                                            }
                                             else
                                             {
                                                 currentCCStrategy = findStrategyByStrategyID(stratId);
                                             }
                                         }

                                     }
                                     if (currentCCStrategy == NULL)
                                     {
                                         currentCCFeeder->setStrategyId(0);
                                         currentCCStrategy = findStrategyByStrategyID(0);
                                     }
                                     currentCCFeeder->setStrategyValues(currentCCStrategy);

                                 }
                             }
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
                                  map< long, CtiCCSubstationPtr > *paobject_substation_map,
                                  map <long, CtiCCAreaPtr> *paobject_area_map,
                                  map <long, CtiCCSpecialPtr> *paobject_specialarea_map,
                                  map< long, long> *substation_area_map,
                                  map< long, long> *substation_specialarea_map,
                                  CtiCCSubstation_vec *ccSubstations)
{
    CtiCCSubstationPtr substationToUpdate = NULL;

    if (substationId > 0)
    {                
        substationToUpdate = findSubstationByPAObjectID(substationId);
    }
    
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    try
    {
        if (substationToUpdate != NULL)
        {
            deleteSubstation(substationId);
        }
        
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {

                if ( conn.isValid() )
                {

                    CtiTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable capControlSubstationTable = db.table("capcontrolsubstation");
                    RWDBTable ccSubAreaAssignmentTable = db.table("ccsubareaassignment");
                    RWDBTable ccSubstationSubBusListTable = db.table("ccsubstationsubbuslist");
                    RWDBTable ccScheduleStrat = db.table("ccseasonstrategyassignment");
                    RWDBTable dateOfSeason = db.table("dateofseason");
                    RWDBTable dynamicCCSubstationTable = db.table("dynamicccsubstation");
                    RWDBTable capControlSpecialAreaTable = db.table("capcontrolspecialarea");
                    RWDBTable ccSubSpecialAreaAssignmentTable = db.table("ccsubspecialareaassignment");

                    {
                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                        << yukonPAObjectTable["category"]
                        << yukonPAObjectTable["paoclass"]
                        << yukonPAObjectTable["paoname"]
                        << yukonPAObjectTable["type"]
                        << yukonPAObjectTable["description"]
                        << yukonPAObjectTable["disableflag"];
 
                        selector.from(yukonPAObjectTable);
                        selector.from(capControlSubstationTable);

                        if (substationId > 0)
                        {                
                            selector.where(yukonPAObjectTable["paobjectid"]==capControlSubstationTable["substationid"] && 
                                          yukonPAObjectTable["paobjectid"] == substationId);
                        }
                        else
                            selector.where(yukonPAObjectTable["paobjectid"]==capControlSubstationTable["substationid"]  );
                            
                        
                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        while ( rdr() )
                        {

                            CtiCCSubstationPtr currentCCSubstation = CtiCCSubstationPtr(new CtiCCSubstation(rdr));
                            paobject_substation_map->insert(make_pair(currentCCSubstation->getPAOId(),currentCCSubstation));

                            
                            ccSubstations->push_back( currentCCSubstation );
                        }

                    }

                    {
                        RWDBSelector selector = db.selector();
                        selector << dynamicCCSubstationTable["substationid"]
                        << dynamicCCSubstationTable["additionalflags"]
                        << dynamicCCSubstationTable["saenabledid"];

                        selector.from(capControlSubstationTable);
                        selector.from(dynamicCCSubstationTable);
                        if (substationId > 0)
                        {               
                            selector.where(capControlSubstationTable["substationid"]==dynamicCCSubstationTable["substationid"] &&
                                           capControlSubstationTable["substationid"] == substationId);
                        }
                        else
                            selector.where(capControlSubstationTable["substationid"]==dynamicCCSubstationTable["substationid"]);


                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);

                        while ( rdr() )
                        {
                            long currentSubstationId;

                            rdr["substationid"] >> currentSubstationId;
                            CtiCCSubstationPtr currentCCSubstation = paobject_substation_map->find(currentSubstationId)->second;

                            if (currentCCSubstation->getPAOId() == currentSubstationId)
                            {
                                 currentCCSubstation->setDynamicData(rdr);
                            }

                        }
                    }
                    {


                        RWDBSelector selector = db.selector();
                        
                        selector << ccSubAreaAssignmentTable["areaid"]
                                 << ccSubAreaAssignmentTable["substationbusid"]
                                 << ccSubAreaAssignmentTable["displayorder"] ;

                        selector.from(ccSubAreaAssignmentTable);

                        if (substationId > 0)
                        {               
                            selector.where( substationId==ccSubAreaAssignmentTable["substationbusid"] );

                        }
                       
                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                       
                        CtiCCSubstationPtr currentCCSubstation;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            long currentAreaId;
                            long currentSubstationId;
                            long displayOrder;
                            rdr["areaid"] >> currentAreaId;
                            rdr["substationbusid"] >> currentSubstationId;
                            rdr["displayorder"] >>displayOrder;
                            currentCCSubstation = paobject_substation_map->find(currentSubstationId)->second;
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
                                currentCCArea->getSubStationList()->push_back(currentSubstationId);
                            }
                        }
                        
                    }
                    {
                        RWDBSelector selector = db.selector();
                        
                        selector << ccSubSpecialAreaAssignmentTable["substationbusid"]
                                 << ccSubSpecialAreaAssignmentTable["areaid"]
                                 << ccSubSpecialAreaAssignmentTable["displayorder"] ;

                        selector.from(ccSubSpecialAreaAssignmentTable);

                        if (substationId > 0)
                        {               
                            selector.where( substationId==ccSubSpecialAreaAssignmentTable["substationbusid"] );

                        }

                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        long currentSubId;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {

                            rdr["substationbusid"] >> currentSubId;
                            //add substationbusids to special area list...;
                            long currentSpAreaId;

                            rdr["areaid"] >> currentSpAreaId;
                            CtiCCSpecialPtr currentCCSpArea = NULL;
                            if (substationId > 0)
                                currentCCSpArea = findSpecialAreaByPAObjectID(currentSpAreaId);
                            else
                            {
                                if (paobject_specialarea_map->find(currentSpAreaId) != paobject_specialarea_map->end())
                                    currentCCSpArea = paobject_specialarea_map->find(currentSpAreaId)->second;
                            }
                            if (currentCCSpArea != NULL) 
                            {
                                CtiCCSubstationPtr currentStation = NULL;
                                if (paobject_substation_map->find(currentSubId) != paobject_substation_map->end())
                                    currentStation = paobject_substation_map->find(currentSubId)->second;
                                if (currentStation != NULL)
                                {
                                    if (!currentCCSpArea->getDisableFlag())
                                    {
                                        currentStation->setSaEnabledFlag(TRUE);
                                    }
                                    else
                                    {
                                        currentStation->setSaEnabledFlag(FALSE);
                                    }
                                    currentStation->setSaEnabledId(currentSpAreaId);
                                }
                                substation_specialarea_map->insert(make_pair(currentSubId, currentSpAreaId));
                                currentCCSpArea->getSubstationIds()->push_back(currentSubId);
                            }
                        }
                    }
                    if (substationId > 0) // else, when reloading all, then the reload of feeders will be called after subBusReload and take care of it.
                    {
                        RWDBSelector selector = db.selector();
                        
                        selector << ccSubstationSubBusListTable["substationid"]
                                 << ccSubstationSubBusListTable["substationbusid"]
                                 << ccSubstationSubBusListTable["displayorder"] ;

                        selector.from(ccSubstationSubBusListTable);

                        selector.where( substationId==ccSubstationSubBusListTable["substationid"] );

                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        long currentSubBusId;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {

                            rdr["substationbusid"] >> currentSubBusId;
                           reloadSubBusFromDatabase(currentSubBusId, &_strategyid_strategy_map, &_paobject_subbus_map,
                                                     &_paobject_substation_map, &_pointid_subbus_map, 
                                                     &_altsub_sub_idmap, &_subbus_substation_map, 
                                                     _ccSubstationBuses );


                        }
                    }
                    
                }

            }

            //_reregisterforpoints = TRUE;
        
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
void CtiCCSubstationBusStore::reloadAreaFromDatabase(long areaId, map< long, CtiCCStrategyPtr > *strategy_map, 
                                  map< long, CtiCCAreaPtr > *paobject_area_map,
                                  multimap< long, CtiCCAreaPtr > *pointid_area_map, 
                                  CtiCCArea_vec *ccGeoAreas)
{
    CtiCCAreaPtr areaToUpdate = NULL;

    if (areaId > 0)
    {                
        areaToUpdate = findAreaByPAObjectID(areaId);
    }
    
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    try
    {
        if (areaToUpdate != NULL)
        {
            deleteArea(areaId);
        }
        
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {

                if ( conn.isValid() )
                {

                    CtiTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable capControlAreaTable = db.table("capcontrolarea");
                    RWDBTable ccSubAreaAssignmentTable = db.table("ccsubareaassignment");
                    RWDBTable ccScheduleStrat = db.table("ccseasonstrategyassignment");
                    RWDBTable dateOfSeason = db.table("dateofseason");
                    RWDBTable dynamicCCAreaTable = db.table("dynamicccarea");


                    {
                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                        << yukonPAObjectTable["category"]
                        << yukonPAObjectTable["paoclass"]
                        << yukonPAObjectTable["paoname"]
                        << yukonPAObjectTable["type"]
                        << yukonPAObjectTable["description"]
                        << yukonPAObjectTable["disableflag"]
                        << capControlAreaTable["controlpointid"];
 
                        selector.from(yukonPAObjectTable);
                        selector.from(capControlAreaTable);

                        if (areaId > 0)
                        {                
                            selector.where(yukonPAObjectTable["paobjectid"]==capControlAreaTable["areaid"] && 
                                          yukonPAObjectTable["paobjectid"] == areaId);
                        }
                        else
                            selector.where(yukonPAObjectTable["paobjectid"]==capControlAreaTable["areaid"]  );
                            
                        
                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        while ( rdr() )
                        {

                            CtiCCAreaPtr currentCCArea = CtiCCAreaPtr(new CtiCCArea(rdr));
                            paobject_area_map->insert(make_pair(currentCCArea->getPAOId(),currentCCArea));

                            
                            ccGeoAreas->push_back( currentCCArea );

                            if (currentCCArea->getVoltReductionControlPointId() > 0 )
                            {   
                                pointid_area_map->insert(make_pair(currentCCArea->getVoltReductionControlPointId(), currentCCArea));
                            }
                        }

                    }
                    {
                        RWDBSelector selector = db.selector();
                                     selector << ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlAreaTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         if (areaId > 0)
                         {                
                             selector.where(ccScheduleStrat["paobjectid"]==capControlAreaTable["areaid"] && 
                                           ccScheduleStrat["paobjectid"] == areaId &&
                                           ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                           ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] );
                         }
                         else
                             selector.where(ccScheduleStrat["paobjectid"]==capControlAreaTable["areaid"]  &&
                                           ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                           ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"]);

                         RWDBReader rdr = selector.reader(conn);
                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year())  )
                             { 
                                 long currentAreaId, stratId;
                                 rdr["paobjectid"] >> currentAreaId;
                                 rdr["strategyid"] >> stratId;


                                 CtiCCAreaPtr currentCCArea = NULL;
                                 if (areaId > 0)
                                     findAreaByPAObjectID(currentAreaId);
                                 else
                                    paobject_area_map->find(currentAreaId)->second;

                                 if (currentCCArea != NULL) 
                                 {
                                     CtiCCStrategyPtr currentCCStrategy = NULL;
                                     if (areaId > 0)
                                         currentCCStrategy = findStrategyByStrategyID(stratId);
                                     else
                                         currentCCStrategy =  strategy_map->find(stratId)->second;

                                     if (currentCCStrategy == NULL)
                                     {
                                         currentCCArea->setStrategyId(0);
                                         if (areaId > 0)
                                             currentCCStrategy = findStrategyByStrategyID(0);
                                         else
                                             currentCCStrategy =  strategy_map->find(0)->second;
                                     }
                                     currentCCArea->setStrategyValues(currentCCStrategy);
                                 }
                                 
                             }
                         }
                    }
                    {
                        RWDBSelector selector = db.selector();
                        selector << dynamicCCAreaTable["areaid"]
                        << dynamicCCAreaTable["additionalflags"]
                        << dynamicCCAreaTable["controlvalue"];

                        selector.from(capControlAreaTable);
                        selector.from(dynamicCCAreaTable);
                        if (areaId > 0)
                        {               
                            selector.where(capControlAreaTable["areaid"]==dynamicCCAreaTable["areaid"] &&
                                           capControlAreaTable["areaid"] == areaId);
                        }
                        else
                            selector.where(capControlAreaTable["areaid"]==dynamicCCAreaTable["areaid"]);


                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);

                        while ( rdr() )
                        {
                            long currentAreaId;

                            rdr["areaid"] >> currentAreaId;
                            CtiCCAreaPtr currentCCArea = paobject_area_map->find(currentAreaId)->second;

                            if (currentCCArea->getPAOId() == currentAreaId)
                            {
                                 currentCCArea->setDynamicData(rdr);
                            }

                        }
                    }
                    if (areaId > 0) // else, when reloading all, then the reload of subs will be called after areaReload and take care of it.
                    {
                        RWDBSelector selector = db.selector();
                        
                        selector << ccSubAreaAssignmentTable["substationbusid"]
                                 << ccSubAreaAssignmentTable["areaid"]
                                 << ccSubAreaAssignmentTable["displayorder"] ;

                        selector.from(ccSubAreaAssignmentTable);

                        selector.where( areaId==ccSubAreaAssignmentTable["areaid"] );

                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        long currentSubstationId;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {

                            rdr["substationbusid"] >> currentSubstationId;
                            reloadSubstationFromDatabase(currentSubstationId,&_paobject_substation_map,
                                                   &_paobject_area_map, &_paobject_specialarea_map, &_substation_area_map,
                                                   &_substation_specialarea_map, _ccSubstations );


                        }
                    }
                    
                }

            }

            //_reregisterforpoints = TRUE;
        
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
void CtiCCSubstationBusStore::reloadSpecialAreaFromDatabase(long areaId, map< long, CtiCCStrategyPtr > *strategy_map, 
                                  map< long, CtiCCSpecialPtr > *paobject_specialarea_map,
                                  multimap< long, CtiCCSpecialPtr > *pointid_specialarea_map,
                                  CtiCCSpArea_vec *ccSpecialAreas)
{
    CtiCCSpecialPtr spAreaToUpdate = NULL;

    if (areaId > 0)
    {                
        spAreaToUpdate = findSpecialAreaByPAObjectID(areaId);
    }
    
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    try
    {
        if (spAreaToUpdate != NULL)
        {
            deleteSpecialArea(areaId);
        }
        
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {

                if ( conn.isValid() )
                {   

                    CtiTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable capControlSpecialAreaTable = db.table("capcontrolspecialarea");
                    RWDBTable ccSubSpecialAreaAssignmentTable = db.table("ccsubspecialareaassignment");
                    RWDBTable ccScheduleStrat = db.table("ccseasonstrategyassignment");
                    RWDBTable dateOfSeason = db.table("dateofseason");
                    RWDBTable dynamicCCSpecialAreaTable = db.table("dynamicccspecialarea");


                    {
                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                        << yukonPAObjectTable["category"]
                        << yukonPAObjectTable["paoclass"]
                        << yukonPAObjectTable["paoname"]
                        << yukonPAObjectTable["type"]
                        << yukonPAObjectTable["description"]
                        << yukonPAObjectTable["disableflag"]
                        << capControlSpecialAreaTable["controlpointid"];
 
                        selector.from(yukonPAObjectTable);
                        selector.from(capControlSpecialAreaTable);

                        if (areaId > 0)
                        {                
                            selector.where(yukonPAObjectTable["paobjectid"]==capControlSpecialAreaTable["areaid"] && 
                                          yukonPAObjectTable["paobjectid"] == areaId);
                        }
                        else
                            selector.where(yukonPAObjectTable["paobjectid"]==capControlSpecialAreaTable["areaid"]  );
                            
                        
                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        while ( rdr() )
                        {

                            CtiCCSpecialPtr currentCCSpArea = CtiCCSpecialPtr(new CtiCCSpecial(rdr));
                            paobject_specialarea_map->insert(make_pair(currentCCSpArea->getPAOId(),currentCCSpArea));

                            
                            ccSpecialAreas->push_back( currentCCSpArea );
                            if (currentCCSpArea->getVoltReductionControlPointId() > 0 )
                            {   
                                pointid_specialarea_map->insert(make_pair(currentCCSpArea->getVoltReductionControlPointId(), currentCCSpArea));
                            }
                        }

                    }
                    {
                        RWDBSelector selector = db.selector();
                                     selector << ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlSpecialAreaTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         if (areaId > 0)
                         {                
                             selector.where(ccScheduleStrat["paobjectid"]==capControlSpecialAreaTable["areaid"] && 
                                           ccScheduleStrat["paobjectid"] == areaId &&
                                           ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                           ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] );
                         }
                         else
                             selector.where(ccScheduleStrat["paobjectid"]==capControlSpecialAreaTable["areaid"]  &&
                                           ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                           ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"]);
                         if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                         {
                             CtiLockGuard<CtiLogger> logger_guard(dout);
                             dout << CtiTime() << " - " << selector.asString().data() << endl;
                         }
                         RWDBReader rdr = selector.reader(conn);
                         while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year())  )
                             { 
                                 long currentAreaId, stratId;
                                 rdr["paobjectid"] >> currentAreaId;
                                 rdr["strategyid"] >> stratId;


                                 CtiCCSpecialPtr currentCCSpArea = NULL;
                                 if (areaId > 0)
                                     currentCCSpArea = findSpecialAreaByPAObjectID(currentAreaId);
                                 else
                                     currentCCSpArea = paobject_specialarea_map->find(currentAreaId)->second;

                                 if (currentCCSpArea != NULL) 
                                 {
                                     CtiCCStrategyPtr currentCCStrategy = NULL;
                                     if (areaId > 0)
                                         currentCCStrategy = findStrategyByStrategyID(stratId);
                                     else
                                         currentCCStrategy =  strategy_map->find(stratId)->second;

                                     if (currentCCStrategy == NULL)
                                     {
                                         stratId = 0;
                                         if (areaId > 0)
                                             currentCCStrategy = findStrategyByStrategyID(stratId);
                                         else
                                             currentCCStrategy =  strategy_map->find(stratId)->second;
                                     }
                                     currentCCSpArea->setStrategyId(stratId);
                                     currentCCSpArea->setStrategyValues(currentCCStrategy);

                                 }
                             }
                         }
                    }
                    {
                        RWDBSelector selector = db.selector();
                        selector << dynamicCCSpecialAreaTable["areaid"]
                        << dynamicCCSpecialAreaTable["additionalflags"]
                        << dynamicCCSpecialAreaTable["controlvalue"];

                        selector.from(capControlSpecialAreaTable);
                        selector.from(dynamicCCSpecialAreaTable);
                        if (areaId > 0)
                        {               
                            selector.where(capControlSpecialAreaTable["areaid"]==dynamicCCSpecialAreaTable["areaid"] &&
                                           dynamicCCSpecialAreaTable["areaid"] == areaId);
                        }
                        else
                            selector.where(capControlSpecialAreaTable["areaid"]==dynamicCCSpecialAreaTable["areaid"]);


                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);

                        while ( rdr() )
                        {
                            long currentSpAreaId;

                            rdr["areaid"] >> currentSpAreaId;
                            CtiCCSpecialPtr currentCCSpArea = paobject_specialarea_map->find(currentSpAreaId)->second;

                            if (currentCCSpArea->getPAOId() == currentSpAreaId)
                            {
                                 currentCCSpArea->setDynamicData(rdr);
                            }

                        }
                    }
                    if (areaId > 0) // else, when reloading all, then the reload of subs will be called after areaReload and take care of it.
                    {
                        RWDBSelector selector = db.selector();
                        
                        selector << ccSubSpecialAreaAssignmentTable["substationbusid"]
                                 << ccSubSpecialAreaAssignmentTable["areaid"]
                                 << ccSubSpecialAreaAssignmentTable["displayorder"] ;

                        selector.from(ccSubSpecialAreaAssignmentTable);

                        selector.where( areaId==ccSubSpecialAreaAssignmentTable["areaid"] );

                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        long currentSubId;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {

                            rdr["substationbusid"] >> currentSubId;
                            //add substationbusids to special area list...;
                            reloadSubstationFromDatabase(currentSubId,&_paobject_substation_map,
                                                   &_paobject_area_map, &_paobject_specialarea_map, &_substation_area_map,
                                                   &_substation_specialarea_map, _ccSubstations );

                        }
                    }
                    
                }

            }

            //_reregisterforpoints = TRUE;
        
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
void CtiCCSubstationBusStore::reloadSubBusFromDatabase(long subBusId, map< long, CtiCCStrategyPtr > *strategy_map, 
                                                       map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                                       map< long, CtiCCSubstationPtr > *paobject_substation_map,
                                                       multimap< long, CtiCCSubstationBusPtr > *pointid_subbus_map, 
                                                       multimap<long, long> *altsub_sub_idmap,
                                                       map< long, long> *subbus_substation_map,
                                                       CtiCCSubstationBus_vec *cCSubstationBuses )
{
    CtiCCSubstationBusPtr subBusToUpdate = NULL;

    if (subBusId > 0)
    {                
        subBusToUpdate = findSubBusByPAObjectID(subBusId);
    }
    
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    try
    {
        if (subBusToUpdate != NULL)
        {
            deleteSubBus(subBusId);
        }
        
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {

                if ( conn.isValid() )
                {   

                    CtiTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable capControlSubstationBusTable = db.table("capcontrolsubstationbus");
                    RWDBTable pointTable = db.table("point");
                    RWDBTable pointUnitTable = db.table("pointunit");
                    RWDBTable dynamicCCSubstationBusTable = db.table("dynamicccsubstationbus");
                    RWDBTable ccFeederSubAssignmentTable = db.table("ccfeedersubassignment");
                    //RWDBTable ccSubAreaAssignmentTable = db.table("ccsubareaassignment");
                    RWDBTable ccSubstationSubBusListTable = db.table("ccsubstationsubbuslist");
                    RWDBTable ccScheduleStrat = db.table("ccseasonstrategyassignment");
                    RWDBTable dateOfSeason = db.table("dateofseason");

                    {
                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                        << yukonPAObjectTable["category"]
                        << yukonPAObjectTable["paoclass"]
                        << yukonPAObjectTable["paoname"]
                        << yukonPAObjectTable["type"]
                        << yukonPAObjectTable["description"]
                        << yukonPAObjectTable["disableflag"]
                        << capControlSubstationBusTable["currentvarloadpointid"]
                        << capControlSubstationBusTable["currentwattloadpointid"]
                        << capControlSubstationBusTable["maplocationid"]
                        << capControlSubstationBusTable["currentvoltloadpointid"]
                        << capControlSubstationBusTable["AltSubID"] 
                        << capControlSubstationBusTable["SwitchPointID"] 
                        << capControlSubstationBusTable["DualBusEnabled"]
                        << capControlSubstationBusTable["multiMonitorControl"]
                        << capControlSubstationBusTable["usephasedata"]
                        << capControlSubstationBusTable["phaseb"]
                        << capControlSubstationBusTable["phasec"]
                        << capControlSubstationBusTable["controlflag"];
                        
                        selector.from(yukonPAObjectTable);
                        selector.from(capControlSubstationBusTable);
                        if (subBusId > 0)
                        {                
                            selector.where(yukonPAObjectTable["paobjectid"]==capControlSubstationBusTable["substationbusid"] && 
                                     yukonPAObjectTable["paobjectid"] == subBusId);
                        }
                        else
                            selector.where(yukonPAObjectTable["paobjectid"]==capControlSubstationBusTable["substationbusid"]);

                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);

                        CtiCCSubstationBusPtr  currentCCSubstationBus;
                        //CtiCCStrategyPtr currentCCStrategy = NULL;
                        while ( rdr() )
                        {
                            currentCCSubstationBus = CtiCCSubstationBusPtr(new CtiCCSubstationBus(rdr));
                            paobject_subbus_map->insert(make_pair(currentCCSubstationBus->getPAOId(),currentCCSubstationBus));

                            if (currentCCSubstationBus->getCurrentVarLoadPointId() > 0 )
                            {   
                                pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getCurrentVarLoadPointId(), currentCCSubstationBus));
                                currentCCSubstationBus->getPointIds()->push_back(currentCCSubstationBus->getCurrentVarLoadPointId());
                            }
                            if (currentCCSubstationBus->getCurrentWattLoadPointId() > 0 )
                            {
                                pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getCurrentWattLoadPointId(), currentCCSubstationBus));
                                currentCCSubstationBus->getPointIds()->push_back(currentCCSubstationBus->getCurrentWattLoadPointId());
                            }
                            if (currentCCSubstationBus->getCurrentVoltLoadPointId() > 0 )
                            {
                                pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getCurrentVoltLoadPointId(), currentCCSubstationBus));
                                currentCCSubstationBus->getPointIds()->push_back(currentCCSubstationBus->getCurrentVoltLoadPointId());
                            }
                            if (currentCCSubstationBus->getSwitchOverPointId() > 0 )
                            {
                                pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getSwitchOverPointId(), currentCCSubstationBus));
                                currentCCSubstationBus->getPointIds()->push_back(currentCCSubstationBus->getSwitchOverPointId());
                            }

                            if (currentCCSubstationBus->getDualBusEnable() && 
                                currentCCSubstationBus->getAltDualSubId() != currentCCSubstationBus->getPAOId())
                            {
                                altsub_sub_idmap->insert(make_pair(currentCCSubstationBus->getAltDualSubId(), currentCCSubstationBus->getPAOId()));
                            }
                            if (currentCCSubstationBus->getUsePhaseData()) 
                            {
                                if (currentCCSubstationBus->getPhaseBId() > 0)
                                    pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getPhaseBId(), currentCCSubstationBus));
                                if (currentCCSubstationBus->getPhaseCId() > 0)
                                    pointid_subbus_map->insert(make_pair(currentCCSubstationBus->getPhaseCId(), currentCCSubstationBus));

                            }

                            
                                cCSubstationBuses->push_back(currentCCSubstationBus);

                        }
                        {
                            RWDBSelector selector = db.selector();

                            selector << ccSubstationSubBusListTable["substationid"]
                                     << ccSubstationSubBusListTable["substationbusid"]
                                     << ccSubstationSubBusListTable["displayorder"] ;

                            selector.from(ccSubstationSubBusListTable);

                            if (subBusId > 0)
                            {               
                                selector.where( subBusId==ccSubstationSubBusListTable["substationbusid"] );

                            }

                            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - " << selector.asString().data() << endl;
                            }
                            RWDBReader rdr = selector.reader(conn);

                            CtiCCSubstationBusPtr currentCCSubstationBus;
                            RWDBNullIndicator isNull;
                            while ( rdr() )
                            {
                                long currentSubstationId;
                                long currentSubBusId;
                                long displayOrder;
                                rdr["substationid"] >> currentSubstationId;
                                rdr["substationbusid"] >> currentSubBusId;
                                rdr["displayorder"] >>displayOrder;
                                currentCCSubstationBus = paobject_subbus_map->find(currentSubBusId)->second;
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
                                    currentCCSubstationBus->setParentName(currentCCSubstation->getPAOName());
                                    list <LONG>::const_iterator iterBus = currentCCSubstation->getCCSubIds()->begin();
                                    bool found = false;
                                    for( ;iterBus != currentCCSubstation->getCCSubIds()->end(); iterBus++)
                                    { 
                                        if (*iterBus == currentSubBusId)
                                        {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if(!found)
                                    {
                                        currentCCSubstation->getCCSubIds()->push_back(currentSubBusId);
                                    }
                                }

                                subbus_substation_map->insert(make_pair(currentSubBusId, currentSubstationId));
                            }
                        }
                        {
                            RWDBSelector selector = db.selector();
                                         selector <<  ccScheduleStrat["paobjectid"]
                                                  <<  ccScheduleStrat["seasonscheduleid"]
                                                  <<  ccScheduleStrat["seasonname"]
                                                  <<  ccScheduleStrat["strategyid"]
                                                  <<  dateOfSeason["seasonstartmonth"]
                                                  <<  dateOfSeason["seasonendmonth"]
                                                  <<  dateOfSeason["seasonstartday"]
                                                  <<  dateOfSeason["seasonendday"];

                             selector.from(capControlSubstationBusTable);
                             selector.from(ccScheduleStrat);
                             selector.from(dateOfSeason);

                             if (subBusId > 0)
                             {                
                                 selector.where(ccScheduleStrat["paobjectid"]==capControlSubstationBusTable["substationbusid"] && 
                                               ccScheduleStrat["paobjectid"] == subBusId &&
                                               ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                               ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] );
                             }
                             else
                                 selector.where(ccScheduleStrat["paobjectid"]==capControlSubstationBusTable["substationbusid"]  &&
                                               ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                               ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"]);
                             RWDBReader rdr = selector.reader(conn);
                             while ( rdr() )
                             {
                                 int startMon, startDay, endMon, endDay;
                                 rdr["seasonstartmonth"] >> startMon;
                                 rdr["seasonendmonth"] >> endMon; 
                                 rdr["seasonstartday"] >> startDay; 
                                 rdr["seasonendday"] >> endDay; 

                                 CtiDate today = CtiDate();

                                 if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                      today < CtiDate(endDay, endMon, today.year())  )
                                 { 
                                     long busId, stratId;
                                     rdr["paobjectid"] >> busId;
                                     rdr["strategyid"] >> stratId;

                                     CtiCCSubstationBusPtr currentCCSubstationBus = NULL; 

                                     if (subBusId > 0)
                                         currentCCSubstationBus = findSubBusByPAObjectID(busId);
                                      else
                                      {
                                          if (paobject_subbus_map->find(busId) != paobject_subbus_map->end())
                                              currentCCSubstationBus = paobject_subbus_map->find(busId)->second;
                                      }
                                      if (currentCCSubstationBus != NULL) 
                                      {
                                          CtiCCStrategyPtr currentCCStrategy = NULL;

                                          CtiCCSubstationPtr currentStation = NULL;
                                          currentStation = findSubstationByPAObjectID(currentCCSubstationBus->getParentId());
                                          if (currentStation != NULL)
                                          {
                                              if (currentStation->getSaEnabledFlag())
                                              {
                                                  CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(currentStation->getSaEnabledId());
                                                  if (spArea != NULL)
                                                      currentCCStrategy =  strategy_map->find(spArea->getStrategyId())->second;
                                              }
                                              else
                                              {
                                              
                                                  if (subBusId > 0)
                                                      currentCCStrategy = findStrategyByStrategyID(stratId);
                                                  else
                                                      currentCCStrategy =  strategy_map->find(stratId)->second;
                                              
                                              }
                                              if (currentCCStrategy == NULL)
                                              {
                                                  currentCCSubstationBus->setStrategyId(0);
                                                  if (subBusId > 0)
                                                      currentCCStrategy = findStrategyByStrategyID(0);
                                                  else
                                                      currentCCStrategy =  strategy_map->find(0)->second;
                                              }
                                              currentCCSubstationBus->setStrategyId(currentCCStrategy->getStrategyId());

                                              currentCCSubstationBus->setStrategyValues(currentCCStrategy);
                                              if (!stringCompareIgnoreCase(currentCCStrategy->getControlMethod(),CtiCCSubstationBus::TimeOfDayMethod) )
                                              {
                                                  currentCCSubstationBus->figureNextCheckTime();
                                              } 
                                          }
                                      
                                      }
                                 }

                             }
                        }

                        

                        //while (!dualBusEnabledSubs.empty())
                        if (!altsub_sub_idmap->empty())
                        {
                            multimap <long, long>::iterator iter = altsub_sub_idmap->begin();
                            while (iter != altsub_sub_idmap->end())
                            {
                                long dualBusId  = iter->second;
                                iter++;

                                if (paobject_subbus_map->find(dualBusId) != paobject_subbus_map->end())
                                {
                                    CtiCCSubstationBusPtr dualBus = NULL;
                                    currentCCSubstationBus = paobject_subbus_map->find(dualBusId)->second;
                                    if (paobject_subbus_map->find(currentCCSubstationBus->getAltDualSubId()) != paobject_subbus_map->end())
                                    {
                                        dualBus = paobject_subbus_map->find(currentCCSubstationBus->getAltDualSubId())->second;
                                        if (!stringCompareIgnoreCase(currentCCSubstationBus->getControlUnits(),CtiCCSubstationBus::KVARControlUnits) )
                                        {
                                            if (dualBus->getCurrentVarLoadPointId() > 0)
                                            {
                                                pointid_subbus_map->insert(make_pair(dualBus->getCurrentVarLoadPointId(), currentCCSubstationBus ));
                                                currentCCSubstationBus->getPointIds()->push_back(dualBus->getCurrentVarLoadPointId());
                                            }
                                            else
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << CtiTime() << " ***WARNING*** Sub: "<<currentCCSubstationBus->getPAOName()<<" will NOT operate in Dual Mode."<<endl;
                                                dout << "   Alternate Sub: "<<dualBus->getPAOName()<<" does not have a VAR Point ID attached."<< endl;
                                            }
                                        }
                                        else if(!stringCompareIgnoreCase(currentCCSubstationBus->getControlUnits(),CtiCCSubstationBus::VoltControlUnits) )
                                        {   
                                            if (dualBus->getCurrentVoltLoadPointId() > 0)
                                            {
                                                pointid_subbus_map->insert(make_pair(dualBus->getCurrentVoltLoadPointId(), currentCCSubstationBus));
                                                currentCCSubstationBus->getPointIds()->push_back(dualBus->getCurrentVoltLoadPointId());
                                            }
                                            else
                                            {
                                                 CtiLockGuard<CtiLogger> logger_guard(dout);
                                                 dout << CtiTime() << " ***WARNING*** Sub: "<<currentCCSubstationBus->getPAOName()<<" will NOT operate in Dual Mode."<<endl;
                                                 dout << "   Alternate Sub: "<<dualBus->getPAOName()<<" does not have a Volt Point ID attached."<< endl;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                    {
                        RWDBSelector selector = db.selector();
                        selector << capControlSubstationBusTable["substationbusid"]
                        << pointUnitTable["decimalplaces"];
                        selector.from(pointUnitTable);
                        selector.from(capControlSubstationBusTable);

                        if (subBusId > 0)
                        {               
                            selector.where(capControlSubstationBusTable["currentvarloadpointid"]==pointUnitTable["pointid"] &&
                                           capControlSubstationBusTable["substationbusid"] == subBusId);
                        }
                        else
                            selector.where(capControlSubstationBusTable["currentvarloadpointid"]==pointUnitTable["pointid"]);

                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);

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
                        RWDBSelector selector = db.selector();
                        selector << dynamicCCSubstationBusTable["substationbusid"]
                        << dynamicCCSubstationBusTable["currentvarpointvalue"]
                        << dynamicCCSubstationBusTable["currentwattpointvalue"]
                        << dynamicCCSubstationBusTable["nextchecktime"]
                        << dynamicCCSubstationBusTable["newpointdatareceivedflag"]
                        << dynamicCCSubstationBusTable["busupdatedflag"]
                        << dynamicCCSubstationBusTable["lastcurrentvarupdatetime"]
                        << dynamicCCSubstationBusTable["estimatedvarpointvalue"]
                        << dynamicCCSubstationBusTable["currentdailyoperations"]
                        << dynamicCCSubstationBusTable["peaktimeflag"]
                        << dynamicCCSubstationBusTable["recentlycontrolledflag"]
                        << dynamicCCSubstationBusTable["lastoperationtime"]
                        << dynamicCCSubstationBusTable["varvaluebeforecontrol"]
                        << dynamicCCSubstationBusTable["lastfeederpaoid"]
                        << dynamicCCSubstationBusTable["lastfeederposition"]
                        << dynamicCCSubstationBusTable["ctitimestamp"]
                        << dynamicCCSubstationBusTable["powerfactorvalue"]
                        << dynamicCCSubstationBusTable["kvarsolution"]
                        << dynamicCCSubstationBusTable["estimatedpfvalue"]
                        << dynamicCCSubstationBusTable["currentvarpointquality"]
                        << dynamicCCSubstationBusTable["waivecontrolflag"]
                        << dynamicCCSubstationBusTable["additionalflags"]
                        << dynamicCCSubstationBusTable["currverifycbid"]
                        << dynamicCCSubstationBusTable["currverifyfeederid"]
                        << dynamicCCSubstationBusTable["currverifycborigstate"]
                        << dynamicCCSubstationBusTable["verificationstrategy"]
                        << dynamicCCSubstationBusTable["cbinactivitytime"]
                        << dynamicCCSubstationBusTable["currentvoltpointvalue"]
                        << dynamicCCSubstationBusTable["switchPointStatus"]
                        << dynamicCCSubstationBusTable["altSubControlValue"]
                        << dynamicCCSubstationBusTable["eventSeq"]
                        << dynamicCCSubstationBusTable["currentwattpointquality"]
                        << dynamicCCSubstationBusTable["currentvoltpointquality"]
                        << dynamicCCSubstationBusTable["ivcontroltot"]
                        << dynamicCCSubstationBusTable["ivcount"]
                        << dynamicCCSubstationBusTable["iwcontroltot"]
                        << dynamicCCSubstationBusTable["iwcount"]
                        << dynamicCCSubstationBusTable["phaseavalue"]
                        << dynamicCCSubstationBusTable["phasebvalue"]
                        << dynamicCCSubstationBusTable["phasecvalue"]
                        << dynamicCCSubstationBusTable["lastwattpointtime"]
                        << dynamicCCSubstationBusTable["lastvoltpointtime"];

                        selector.from(capControlSubstationBusTable);
                        selector.from(dynamicCCSubstationBusTable);
                        if (subBusId > 0)
                        {               
                            selector.where(capControlSubstationBusTable["substationbusid"]==dynamicCCSubstationBusTable["substationbusid"] &&
                                           capControlSubstationBusTable["substationbusid"] == subBusId);
                        }
                        else
                            selector.where(capControlSubstationBusTable["substationbusid"]==dynamicCCSubstationBusTable["substationbusid"]);


                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);

                        while ( rdr() )
                        {
                            long currentSubBusId;

                            rdr["substationbusid"] >> currentSubBusId;
                            CtiCCSubstationBusPtr currentCCSubstationBus = paobject_subbus_map->find(currentSubBusId)->second;

                            if (currentCCSubstationBus->getPAOId() == currentSubBusId)
                            {
                                 currentCCSubstationBus->setDynamicData(rdr);
                            }

                        }
                    }
                    if (subBusId > 0) // else, when reloading all, then the reload of feeders will be called after subBusReload and take care of it.
                    {
                        RWDBSelector selector = db.selector();
                        
                        selector << ccFeederSubAssignmentTable["feederid"]
                                 << ccFeederSubAssignmentTable["substationbusid"]
                                 << ccFeederSubAssignmentTable["displayorder"] ;

                        selector.from(ccFeederSubAssignmentTable);

                        selector.where( subBusId==ccFeederSubAssignmentTable["substationbusid"] );

                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        long currentFeederId;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {

                            rdr["feederid"] >> currentFeederId;
                            //reloadFeederFromDatabase(currentFeederId);
                            reloadFeederFromDatabase(currentFeederId, &_strategyid_strategy_map, &_paobject_feeder_map,
                                                    &_paobject_subbus_map, &_pointid_feeder_map, &_feeder_subbus_map );

                        }
                    }

                    {
                        RWDBSelector selector = db.selector();
                        selector << pointTable["paobjectid"]
                        << pointTable["pointid"]
                        << pointTable["pointoffset"]
                        << pointTable["pointtype"];

                        selector.from(capControlSubstationBusTable);
                        selector.from(pointTable);

                        if (subBusId > 0)
                        {               
                            selector.where(capControlSubstationBusTable["substationbusid"]==pointTable["paobjectid"] &&
                                           capControlSubstationBusTable["substationbusid"] == subBusId);
                        }
                        else
                            selector.where(capControlSubstationBusTable["substationbusid"]==pointTable["paobjectid"]);


                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);

                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            long currentSubBusId;

                            rdr["paobjectid"] >> currentSubBusId;
                            CtiCCSubstationBusPtr currentCCSubstationBus = paobject_subbus_map->find(currentSubBusId)->second;


                            rdr["pointid"] >> isNull;
                            if ( !isNull )
                            {
                                long tempPointId = -1000;
                                long tempPointOffset = -1000;
                                string tempPointType = "(none)";
                                rdr["pointid"] >> tempPointId;
                                rdr["pointoffset"] >> tempPointOffset;
                                rdr["pointtype"] >> tempPointType;
                                if ( resolvePointType(tempPointType) == AnalogPointType )
                                {
                                    if ( tempPointOffset==1 )
                                    {//estimated vars point
                                        currentCCSubstationBus->setEstimatedVarLoadPointId(tempPointId);
                                        pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                                        currentCCSubstationBus->getPointIds()->push_back(tempPointId);
                                    }
                                    else if ( tempPointOffset==2 )
                                    {//daily operations point
                                        currentCCSubstationBus->setDailyOperationsAnalogPointId(tempPointId);
                                        pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                                        currentCCSubstationBus->getPointIds()->push_back(tempPointId);
                                    }
                                    else if ( tempPointOffset==3 )
                                    {//power factor point
                                        currentCCSubstationBus->setPowerFactorPointId(tempPointId);
                                        pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                                        currentCCSubstationBus->getPointIds()->push_back(tempPointId);
                                    }
                                    else if ( tempPointOffset==4 )
                                    {//estimated power factor point
                                        currentCCSubstationBus->setEstimatedPowerFactorPointId(tempPointId);
                                        pointid_subbus_map->insert(make_pair(tempPointId,currentCCSubstationBus));
                                        currentCCSubstationBus->getPointIds()->push_back(tempPointId);
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
                }

                if (subBusId > 0) //incase feeders/capbanks were moved?????  need to check this $#*! out.
                {
                    //feederAssignment here???
                    // capfeederassignment here???
                    /*CtiCCSubstationBusPtr oldCCSubstationBus = findSubBusByPAObjectID(subBusId);
                    if (oldCCSubstationBus != NULL)
                    {
                        _ccSubstationBuses->remove(oldCCSubstationBus);
                        _paobject_subbus_map.erase(subBusId);
                    }
                    CtiCCSubstationBusPtr currentCCSubstationBus = tempSubBusMap->find(subBusId)->second;
                    _paobject_subbus_map.insert(make_pair(subBusId, currentCCSubstationBus));
                    _ccSubstationBuses->insert(oldCCSubstationBus);

                    map <long, CtiCCSubstationBusPtr> :: const_iterator iter = tempSubBusPointIdMap->begin();
                    while (iter != tempSubBusPointIdMap->end())
                    {
                        if (findSubBusByPointID(iter->first))
                            _pointid_subbus_map.erase(iter->first);
                        _pointid_subbus_map.insert(make_pair(iter->first, currentCCSubstationBus));
                    }
                      */

                    

                }

            }

            //_reregisterforpoints = TRUE;
        
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCSubstationBusStore::reloadFeederFromDatabase(long feederId, map< long, CtiCCStrategyPtr > *strategy_map, 
                                                       map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                                       map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                                       multimap< long, CtiCCFeederPtr > *pointid_feeder_map, 
                                                       map< long, long> *feeder_subbus_map )
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
            deleteFeeder(feederToUpdate->getPAOId());
        }

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        {

                if ( conn.isValid() )
                {  
                    CtiTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable pointTable = db.table("point");
                    RWDBTable pointUnitTable = db.table("pointunit");
                    RWDBTable ccFeederSubAssignmentTable = db.table("ccfeedersubassignment");
                    RWDBTable ccFeederBankListTable = db.table("ccfeederbanklist");
                    RWDBTable capControlFeederTable = db.table("capcontrolfeeder");
                    RWDBTable dynamicCCFeederTable = db.table("dynamicccfeeder");
                    RWDBTable ccScheduleStrat = db.table("ccseasonstrategyassignment");
                    RWDBTable dateOfSeason = db.table("dateofseason");

                    {

                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                        << yukonPAObjectTable["category"]
                        << yukonPAObjectTable["paoclass"]
                        << yukonPAObjectTable["paoname"]
                        << yukonPAObjectTable["type"]
                        << yukonPAObjectTable["description"]
                        << yukonPAObjectTable["disableflag"]
                        << capControlFeederTable["currentvarloadpointid"]
                        << capControlFeederTable["currentwattloadpointid"]
                        << capControlFeederTable["maplocationid"]
                        << capControlFeederTable["currentvoltloadpointid"]
                        << capControlFeederTable["multiMonitorControl"]
                        << capControlFeederTable["usephasedata"]
                        << capControlFeederTable["phaseb"]
                        << capControlFeederTable["phasec"]
                        << capControlFeederTable["controlflag"];
                        
                        selector.from(yukonPAObjectTable);
                        selector.from(capControlFeederTable);

                        if (feederId > 0)
                        {               
                            selector.where( yukonPAObjectTable["paobjectid"]==capControlFeederTable["feederid"] && 
                                        yukonPAObjectTable["paobjectid"] == feederId );
                        }
                        else
                            selector.where( yukonPAObjectTable["paobjectid"]==capControlFeederTable["feederid"] );

                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);

                        CtiCCFeederPtr oldCCFeeder, currentCCFeeder;
                        CtiCCStrategyPtr currentCCStrategy = NULL;
                        CtiCCSubstationBusPtr oldFeederParentSub = NULL;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            currentCCFeeder = CtiCCFeederPtr(new CtiCCFeeder(rdr));
                            
                            paobject_feeder_map->insert(make_pair(currentCCFeeder->getPAOId(),currentCCFeeder));

                            if (currentCCFeeder->getCurrentVarLoadPointId() > 0 )
                            {
                                pointid_feeder_map->insert(make_pair(currentCCFeeder->getCurrentVarLoadPointId(), currentCCFeeder));
                                currentCCFeeder->getPointIds()->push_back(currentCCFeeder->getCurrentVarLoadPointId());
                            }
                            if (currentCCFeeder->getCurrentWattLoadPointId() > 0)
                            {
                                pointid_feeder_map->insert(make_pair(currentCCFeeder->getCurrentWattLoadPointId(), currentCCFeeder));
                                currentCCFeeder->getPointIds()->push_back(currentCCFeeder->getCurrentWattLoadPointId());
                            }
                            if (currentCCFeeder->getCurrentVoltLoadPointId() > 0)
                            {
                                pointid_feeder_map->insert(make_pair(currentCCFeeder->getCurrentVoltLoadPointId(), currentCCFeeder));
                                currentCCFeeder->getPointIds()->push_back(currentCCFeeder->getCurrentVoltLoadPointId());
                            }
                            if (currentCCFeeder->getUsePhaseData()) 
                            {
                                if (currentCCFeeder->getPhaseBId() > 0)
                                    pointid_feeder_map->insert(make_pair(currentCCFeeder->getPhaseBId(), currentCCFeeder));
                                if (currentCCFeeder->getPhaseCId() > 0)
                                    pointid_feeder_map->insert(make_pair(currentCCFeeder->getPhaseCId(), currentCCFeeder));

                            }
                        } 
                    }
                    {
                        RWDBSelector selector = db.selector();
                        
                        selector << ccFeederSubAssignmentTable["feederid"]
                                 << ccFeederSubAssignmentTable["substationbusid"]
                                 << ccFeederSubAssignmentTable["displayorder"] ;
                       
                        selector.from(ccFeederSubAssignmentTable);
                        //selector.from(capControlFeederTable);
                       
                        if (feederId > 0)
                        {               
                            selector.where( ccFeederSubAssignmentTable["feederid"] == feederId);
                        }
                       
                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                       
                        CtiCCFeederPtr currentCCFeeder;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                       
                            long currentSubBusId;
                            long currentFeederId;
                            long displayOrder;
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
                       
                                CtiCCStrategyPtr newStrategy = NULL;
                                if (feederId > 0)
                                    newStrategy = findStrategyByStrategyID(currentCCSubstationBus->getStrategyId());
                                else
                                    newStrategy =  strategy_map->find(currentCCSubstationBus->getStrategyId())->second;
                       
                                currentCCFeeder->setParentControlUnits(currentCCSubstationBus->getControlUnits());
                                currentCCFeeder->setParentName(currentCCSubstationBus->getPAOName());
                       
                                if (currentCCFeeder->getStrategyId() == 0 && newStrategy != NULL)
                                {                      
                                    currentCCFeeder->setStrategyValues(newStrategy);
                                    currentCCFeeder->setStrategyId(0);
                                    currentCCFeeder->setStrategyName("(none)");
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
                        RWDBSelector selector = db.selector();
                                     selector <<  ccScheduleStrat["paobjectid"]
                                              <<  ccScheduleStrat["seasonscheduleid"]
                                              <<  ccScheduleStrat["seasonname"]
                                              <<  ccScheduleStrat["strategyid"]
                                              <<  dateOfSeason["seasonstartmonth"]
                                              <<  dateOfSeason["seasonendmonth"]
                                              <<  dateOfSeason["seasonstartday"]
                                              <<  dateOfSeason["seasonendday"];

                         selector.from(capControlFeederTable);
                         selector.from(ccScheduleStrat);
                         selector.from(dateOfSeason);

                         if (feederId > 0)
                         {                
                             selector.where(ccScheduleStrat["paobjectid"]==capControlFeederTable["feederid"] && 
                                           ccScheduleStrat["paobjectid"] == feederId &&
                                           ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                           ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"] );
                         }
                         else
                             selector.where(ccScheduleStrat["paobjectid"]==capControlFeederTable["feederid"]  &&
                                           ccScheduleStrat["seasonscheduleid"] == dateOfSeason["seasonscheduleid"] &&
                                           ccScheduleStrat["seasonname"] == dateOfSeason["seasonname"]);
                         RWDBReader rdr = selector.reader(conn);
                        while ( rdr() )
                         {
                             int startMon, startDay, endMon, endDay;
                             rdr["seasonstartmonth"] >> startMon;
                             rdr["seasonendmonth"] >> endMon; 
                             rdr["seasonstartday"] >> startDay; 
                             rdr["seasonendday"] >> endDay; 

                             CtiDate today = CtiDate();

                             if (today  >= CtiDate(startDay, startMon, today.year()) &&
                                 today < CtiDate(endDay, endMon, today.year()) )
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
                                     CtiCCStrategyPtr currentCCStrategy = NULL;

                                     if (paobject_subbus_map->find(currentCCFeeder->getParentId()) != paobject_subbus_map->end())
                                         currentSubBus = paobject_subbus_map->find(currentCCFeeder->getParentId())->second;
                                     
                                     if (currentSubBus != NULL)
                                     {
                                         currentStation = findSubstationByPAObjectID(currentSubBus->getParentId());
                                         if (currentStation != NULL)
                                         {
                                             if (currentStation->getSaEnabledFlag())
                                             {
                                                 CtiCCSpecialPtr spArea = findSpecialAreaByPAObjectID(currentStation->getSaEnabledId());
                                                  if (spArea != NULL)
                                                      currentCCStrategy =  strategy_map->find(spArea->getStrategyId())->second;
                                             }
                                             else
                                             {
                                                 if (stratId != 0 )
                                                     currentCCStrategy = findStrategyByStrategyID(stratId);
                                                 else
                                                 {
                                                     currentCCStrategy = findStrategyByStrategyID(currentSubBus->getStrategyId());
                                                 }
                                             }
                                         }

                                     }
                                     if (currentCCStrategy == NULL)
                                     {
                                         currentCCFeeder->setStrategyId(0);
                                         currentCCStrategy = findStrategyByStrategyID(0);
                                     }
                                     currentCCFeeder->setStrategyValues(currentCCStrategy);
                                     if (stratId == 0)
                                     {
                                         currentCCFeeder->setStrategyId(0);
                                         currentCCFeeder->setStrategyName("(none)");
                                     }
                                 }
                             }
                         }
                    }


                

                if (feederId > 0)
                {    
                    RWDBSelector selector = db.selector();
                    
                    selector << ccFeederBankListTable["deviceid"]
                          << ccFeederBankListTable["feederid"]
                          << ccFeederBankListTable["controlorder"]
                          << ccFeederBankListTable["closeorder"]
                          << ccFeederBankListTable["triporder"];


                    selector.from(ccFeederBankListTable);
                    selector.where(ccFeederBankListTable["feederid"]== feederId );

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    RWDBNullIndicator isNull;
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
                        CtiCCCapBank_SVector& capBanks = currentFeeder->getCCCapBanks();
                        for (int i = 0; i < capBanks.size(); i++) 
                        { 
                            CtiCCCapBankPtr bank = (CtiCCCapBankPtr)capBanks[i];
                             
                            reloadMonitorPointsFromDatabase(bank->getPAOId(), &_paobject_capbank_map, &_paobject_feeder_map, &_paobject_subbus_map, &_pointid_capbank_map);
                        }
                    }
                }
                {

                    RWDBSelector selector = db.selector();
                    selector << capControlFeederTable["feederid"]
                    << pointUnitTable["decimalplaces"];
                    selector.from(pointUnitTable);
                    selector.from(capControlFeederTable);
                    if (feederId > 0)
                    {               
                        selector.where(capControlFeederTable["currentvarloadpointid"]==pointUnitTable["pointid"] &&
                                   capControlFeederTable["feederid"] == feederId);
                    }
                    else
                        selector.where(capControlFeederTable["currentvarloadpointid"]==pointUnitTable["pointid"] );

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }
                    RWDBReader rdr = selector.reader(conn);

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

                {   RWDBSelector selector = db.selector();
                    
                    selector << dynamicCCFeederTable["feederid"]
                    << dynamicCCFeederTable["currentvarpointvalue"]
                    << dynamicCCFeederTable["currentwattpointvalue"]
                    << dynamicCCFeederTable["newpointdatareceivedflag"]
                    << dynamicCCFeederTable["lastcurrentvarupdatetime"]
                    << dynamicCCFeederTable["estimatedvarpointvalue"]
                    << dynamicCCFeederTable["currentdailyoperations"]
                    << dynamicCCFeederTable["recentlycontrolledflag"]
                    << dynamicCCFeederTable["lastoperationtime"]
                    << dynamicCCFeederTable["varvaluebeforecontrol"]
                    << dynamicCCFeederTable["lastcapbankdeviceid"]
                    << dynamicCCFeederTable["busoptimizedvarcategory"]
                    << dynamicCCFeederTable["busoptimizedvaroffset"]
                    << dynamicCCFeederTable["ctitimestamp"]
                    << dynamicCCFeederTable["powerfactorvalue"]
                    << dynamicCCFeederTable["kvarsolution"]
                    << dynamicCCFeederTable["estimatedpfvalue"]
                    << dynamicCCFeederTable["currentvarpointquality"]
                    << dynamicCCFeederTable["waivecontrolflag"]
                    << dynamicCCFeederTable["additionalflags"] 
                    << dynamicCCFeederTable["currentvoltpointvalue"]
                    << dynamicCCFeederTable["eventSeq"]
                    << dynamicCCFeederTable["currverifycbid"]
                    << dynamicCCFeederTable["currverifycborigstate"]
                    << dynamicCCFeederTable["currentwattpointquality"]
                    << dynamicCCFeederTable["currentvoltpointquality"]
                    << dynamicCCFeederTable["ivcontroltot"]
                    << dynamicCCFeederTable["ivcount"]
                    << dynamicCCFeederTable["iwcontroltot"]
                    << dynamicCCFeederTable["iwcount"]
                    << dynamicCCFeederTable["phaseavalue"]
                    << dynamicCCFeederTable["phasebvalue"]
                    << dynamicCCFeederTable["phasecvalue"]
                    << dynamicCCFeederTable["lastwattpointtime"]
                    << dynamicCCFeederTable["lastvoltpointtime"];


                    selector.from(dynamicCCFeederTable);
                    selector.from(capControlFeederTable);

                    if (feederId > 0)
                    {               
                        selector.where(capControlFeederTable["feederid"]==dynamicCCFeederTable["feederid"] && 
                                   capControlFeederTable["feederid"]==feederId);
                    }
                    else
                        selector.where(capControlFeederTable["feederid"]==dynamicCCFeederTable["feederid"]);

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    while ( rdr() )
                    {
                        long currentCCFeederId;
                        rdr["feederid"] >> currentCCFeederId;
                        if (currentCCFeederId != NULL)
                        {
                            CtiCCFeederPtr currentCCFeeder = paobject_feeder_map->find(currentCCFeederId)->second;
                            currentCCFeeder->setDynamicData(rdr);

                        }
                    }
                }
                {

                    RWDBSelector selector = db.selector();
                    selector << pointTable["paobjectid"]
                    << pointTable["pointid"]
                    << pointTable["pointoffset"]
                    << pointTable["pointtype"];

                    selector.from(pointTable);
                    selector.from(capControlFeederTable);
                    if (feederId > 0)
                    {               
                        selector.where( capControlFeederTable["feederid"]==pointTable["paobjectid"] &&
                                    capControlFeederTable["feederid"] == feederId);
                    }
                    else
                        selector.where( capControlFeederTable["feederid"]==pointTable["paobjectid"] );

                    selector.orderBy(pointTable["pointoffset"]);

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);

                    RWDBNullIndicator isNull;
                    while ( rdr() )
                    {
                        long currentCCFeederId;

                        rdr["paobjectid"] >> currentCCFeederId;
                        CtiCCFeederPtr currentCCFeeder = paobject_feeder_map->find(currentCCFeederId)->second;

                        LONG tempPAObjectId = 0;
                        rdr["paobjectid"] >> tempPAObjectId;
                        if (    tempPAObjectId == currentCCFeeder->getPAOId() )
                        {
                            rdr["pointid"] >> isNull;
                            if ( !isNull )
                            {
                                LONG tempPointId = -1000;
                                LONG tempPointOffset = -1000;
                                string tempPointType = "(none)";
                                rdr["pointid"] >> tempPointId;
                                rdr["pointoffset"] >> tempPointOffset;
                                rdr["pointtype"] >> tempPointType;
                                if ( resolvePointType(tempPointType) == AnalogPointType )
                                {
                                    if ( tempPointOffset==1 )
                                    {//estimated vars point
                                        currentCCFeeder->setEstimatedVarLoadPointId(tempPointId);
                                        pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                        currentCCFeeder->getPointIds()->push_back(tempPointId);
                                    }
                                    else if ( tempPointOffset==2 )
                                    {//daily operations point
                                        currentCCFeeder->setDailyOperationsAnalogPointId(tempPointId);
                                        pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                        currentCCFeeder->getPointIds()->push_back(tempPointId);
                                    }
                                    else if ( tempPointOffset==3 )
                                    {//power factor point
                                        currentCCFeeder->setPowerFactorPointId(tempPointId);
                                        pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                        currentCCFeeder->getPointIds()->push_back(tempPointId);
                                    }
                                    else if ( tempPointOffset==4 )
                                    {//estimated power factor point
                                        currentCCFeeder->setEstimatedPowerFactorPointId(tempPointId);
                                        pointid_feeder_map->insert(make_pair(tempPointId,currentCCFeeder));
                                        currentCCFeeder->getPointIds()->push_back(tempPointId);
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

            }
            
        }

        //_reregisterforpoints = TRUE;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCSubstationBusStore::reloadCapBankFromDatabase(long capBankId, map< long, CtiCCCapBankPtr > *paobject_capbank_map,
                                                        map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                                        map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                                        multimap< long, CtiCCCapBankPtr > *pointid_capbank_map,
                                                       map< long, long> *capbank_subbus_map,
                                                       map< long, long> *capbank_feeder_map,
                                                       map< long, long> *feeder_subbus_map,
                                                       map< long, long> *cbc_capbank_map )
{
    CtiCCCapBankPtr capBankToUpdate = NULL;
    LONG monPointId = 0;

    if (capBankId > 0)
    {
        deleteCapBank(capBankId);
    }
    
    try
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        {
            if ( conn.isValid() )
            {   

                CtiTime currentDateTime;
                RWDBDatabase db = getDatabase();
                RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                RWDBTable pointTable = db.table("point");
                RWDBTable deviceTable = db.table("device");
                RWDBTable capBankTable = db.table("capbank");
                RWDBTable dynamicCCCapBankTable = db.table("dynamiccccapbank");
                RWDBTable ccFeederBankListTable = db.table("ccfeederbanklist");
                RWDBTable ccMonitorBankListTable = db.table("ccmonitorbanklist");
                RWDBTable dynamicCCMonitorBankHistoryTable = db.table("dynamicccmonitorbankhistory");
                RWDBTable dynamicCCMonitorPointResponseTable = db.table("dynamicccmonitorpointresponse");
                RWDBTable dynamicCCTwoWayTable = db.table("dynamiccctwowaycbc");


                {
                    RWDBSelector selector = db.selector();
                    selector << yukonPAObjectTable["paobjectid"]
                    << yukonPAObjectTable["category"]
                    << yukonPAObjectTable["paoclass"]
                    << yukonPAObjectTable["paoname"]
                    << yukonPAObjectTable["type"]
                    << yukonPAObjectTable["description"]
                    << yukonPAObjectTable["disableflag"]
                    << capBankTable["operationalstate"]
                    << capBankTable["controllertype"]
                    << capBankTable["controldeviceid"]
                    << capBankTable["controlpointid"]
                    << capBankTable["banksize"]
                    << capBankTable["typeofswitch"]
                    << capBankTable["switchmanufacture"]
                    << capBankTable["maplocationid"]
                    << capBankTable["reclosedelay"]
                    << capBankTable["maxdailyops"]
                    << capBankTable["maxopdisable"];

                    selector.from(yukonPAObjectTable);
                    selector.from(capBankTable);

                    if (capBankId > 0)
                    {                
                        selector.where(yukonPAObjectTable["paobjectid"]==capBankTable["deviceid"] &&
                                       yukonPAObjectTable["paobjectid"] == capBankId);
                    }
                    else
                        selector.where(yukonPAObjectTable["paobjectid"]==capBankTable["deviceid"] );


                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    CtiCCCapBankPtr currentCCCapBank;
                    RWDBNullIndicator isNull;
                    while ( rdr() )
                    {
                        CtiCCCapBankPtr currentCCCapBank = CtiCCCapBankPtr(new CtiCCCapBank(rdr));
                        
                        paobject_capbank_map->insert(make_pair(currentCCCapBank->getPAOId(),currentCCCapBank));
                    }
                }
                {
                    RWDBSelector selector = db.selector();
                    selector <<  deviceTable["deviceid"]
                             <<  deviceTable["alarminhibit"]
                            << deviceTable["controlinhibit"];

                    selector.from(deviceTable);
                    selector.from(capBankTable);

                    if (capBankId > 0)
                    {                
                        selector.where(deviceTable["deviceid"]==capBankTable["deviceid"] && 
                                       deviceTable["deviceid"] == capBankId);
                    }
                    else
                        selector.where(deviceTable["deviceid"]==capBankTable["deviceid"] );

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    RWDBNullIndicator isNull;
                    while ( rdr() )
                    {
                        long deviceid;
                        string tempBoolString;

                        rdr["deviceid"] >> deviceid;
                        CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(deviceid)->second;

                        rdr["alarminhibit"] >> tempBoolString;
                        std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), ::tolower);

                        currentCCCapBank->setAlarmInhibitFlag(tempBoolString=="y"?TRUE:FALSE);

                        rdr["controlinhibit"] >> tempBoolString;
                        std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), ::tolower);

                        currentCCCapBank->setControlInhibitFlag(tempBoolString=="y"?TRUE:FALSE);
                    }
                }


                {
                    RWDBSelector selector = db.selector();
                    selector << capBankTable["deviceid"]
                             <<  capBankTable["controldeviceid"]
                             <<  yukonPAObjectTable["type"];
                            // <<  yukonPAObjectTable["paobjectid"];

                    selector.from(capBankTable);
                    selector.from(yukonPAObjectTable);

                    if (capBankId > 0)
                    {                
                        selector.where(yukonPAObjectTable["paobjectid"]==capBankTable["controldeviceid"] &&
                                       capBankTable["deviceid"] == capBankId );
                    }
                    else
                        selector.where(yukonPAObjectTable["paobjectid"]==capBankTable["controldeviceid"] &&
                                       capBankTable["controldeviceid"] != 0 );


                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    RWDBNullIndicator isNull;
                    while ( rdr() )
                    {
                        long capBankId;
                        long controlDeviceId;
                        string controlDeviceType;

                        rdr["deviceid"] >> capBankId;
                        rdr["controldeviceid"] >> controlDeviceId;
                        rdr["type"] >> controlDeviceType;
                        CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(capBankId)->second;

                        if (currentCCCapBank->getControlDeviceId() == controlDeviceId)
                        {
                            currentCCCapBank->setControlDeviceType(controlDeviceType);

                            cbc_capbank_map->insert(make_pair(controlDeviceId,capBankId));
                        }
                    }
                }


                {
                    RWDBSelector selector = db.selector();
                    selector << ccFeederBankListTable["deviceid"]
                          << ccFeederBankListTable["feederid"]
                          << ccFeederBankListTable["controlorder"]
                          << ccFeederBankListTable["closeorder"]
                          << ccFeederBankListTable["triporder"];


                    selector.from(capBankTable);
                    selector.from(ccFeederBankListTable);
                    if (capBankId > 0)
                    {                
                        selector.where(ccFeederBankListTable["deviceid"]==capBankTable["deviceid"] &&
                                       capBankTable["deviceid"] == capBankId );
                    }
                    else
                        selector.where(ccFeederBankListTable["deviceid"]==capBankTable["deviceid"] );


                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    RWDBNullIndicator isNull;
                    long subbusid;
                    long tempFeederId;

                    while ( rdr() )
                    {
                        long deviceid;
                        long feederid;
                        FLOAT controlOrder;
                        FLOAT tripOrder;
                        FLOAT closeOrder;

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

                        //DON'T ADD !... Supposed to be !=UninstalledState...
                        if (stringCompareIgnoreCase(currentCCCapBank->getOperationalState(),CtiCCCapBank::UninstalledState))
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
                    RWDBSelector selector = db.selector();
                    selector << dynamicCCCapBankTable["capbankid"]
                    << dynamicCCCapBankTable["controlstatus"]
                    << dynamicCCCapBankTable["totaloperations"]
                    << dynamicCCCapBankTable["laststatuschangetime"]
                    << dynamicCCCapBankTable["tagscontrolstatus"]
                    << dynamicCCCapBankTable["ctitimestamp"]
                    << dynamicCCCapBankTable["originalfeederid"]
                    << dynamicCCCapBankTable["originalswitchingorder"]
                    << dynamicCCCapBankTable["assumedstartverificationstatus"]
                    << dynamicCCCapBankTable["prevverificationcontrolstatus"]
                    << dynamicCCCapBankTable["verificationcontrolindex"]
                    << dynamicCCCapBankTable["additionalflags"]
                    << dynamicCCCapBankTable["currentdailyoperations"]
                    << dynamicCCCapBankTable["twowaycbcstate"]
                    << dynamicCCCapBankTable["twowaycbcstatetime"]
                    << dynamicCCCapBankTable["beforevar"]
                    << dynamicCCCapBankTable["aftervar"] 
                    << dynamicCCCapBankTable["changevar"];

                    selector.from(dynamicCCCapBankTable);
                    selector.from(capBankTable);

                    if (capBankId > 0)
                    {                
                        selector.where(capBankTable["deviceid"]==dynamicCCCapBankTable["capbankid"] &&
                                   capBankTable["deviceid"] == capBankId);
                    }
                    else
                        selector.where(capBankTable["deviceid"]==dynamicCCCapBankTable["capbankid"] );

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }
                    RWDBReader rdr = selector.reader(conn);

                    while (rdr())
                    {
                        long currentCCCapBankId;
                        rdr["capbankid"] >> currentCCCapBankId;
                        CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(currentCCCapBankId)->second;

                        currentCCCapBank->setDynamicData(rdr);
                    }
                }
                {
                    RWDBSelector selector = db.selector();
                    selector << pointTable["paobjectid"]
                    << pointTable["pointid"]
                    << pointTable["pointoffset"]
                    << pointTable["pointtype"];
    
    
                    selector.from(pointTable);
                    selector.from(capBankTable);
                    if (capBankId > 0)
                    {                
                        selector.where(capBankTable["deviceid"] == pointTable["paobjectid"] &&
                                   capBankTable["deviceid"] == capBankId);
                    }
                    else
                        selector.where(capBankTable["deviceid"] == pointTable["paobjectid"] );
    
                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }
    
                    RWDBReader rdr = selector.reader(conn);
                    RWDBNullIndicator isNull;
                    while ( rdr() )
                    {
                        long currentCCCapBankId;
                        rdr["paobjectid"] >> currentCCCapBankId;
    
                        CtiCCCapBankPtr currentCCCapBank = NULL;
    
                        if (paobject_capbank_map->find(currentCCCapBankId) != paobject_capbank_map->end())
                            currentCCCapBank = paobject_capbank_map->find(currentCCCapBankId)->second;
    
                        if ( currentCCCapBank != NULL )
                        {
                            rdr["pointid"] >> isNull;
                            if ( !isNull )
                            {
                                LONG tempPointId = -1000;
                                LONG tempPointOffset = -1000;
                                string tempPointType = "(none)";
                                rdr["pointid"] >> tempPointId;
                                rdr["pointoffset"] >> tempPointOffset;
                                rdr["pointtype"] >> tempPointType;
                                if ( tempPointOffset == 1 )
                                {
                                    if ( resolvePointType(tempPointType) == StatusPointType )
                                    {//control status point
                                        currentCCCapBank->setStatusPointId(tempPointId);
                                        pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                                        currentCCCapBank->getPointIds()->push_back(tempPointId);
                                    }
                                    else if ( resolvePointType(tempPointType) == AnalogPointType )
                                    {//daily operations point
                                        currentCCCapBank->setOperationAnalogPointId(tempPointId);
                                        pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                                        currentCCCapBank->getPointIds()->push_back(tempPointId);
                                    }
                                    else
                                    {//undefined cap bank point
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " - Undefined Cap Bank point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
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
                    RWDBSelector selector = db.selector();
                    selector << pointTable["paobjectid"]
                    << pointTable["pointid"]
                    << pointTable["pointoffset"]
                    << pointTable["pointtype"];
    
    
                    selector.from(pointTable);
                    selector.from(capBankTable);
                    if (capBankId > 0)
                    {                
                        selector.where(capBankTable["controldeviceid"] == pointTable["paobjectid"] &&
                                   capBankTable["deviceid"] == capBankId);
                    }
                    else
                        selector.where(capBankTable["controldeviceid"] == pointTable["paobjectid"] &&
                                       capBankTable["controldeviceid"] != 0);
    
                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }
    
                    RWDBReader rdr = selector.reader(conn);
                    RWDBNullIndicator isNull;
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
                            if (stringContainsIgnoreCase(currentCCCapBank->getControlDeviceType(), "CBC 702")) 
                            {
                                rdr["pointid"] >> isNull;
                                if ( !isNull )
                                {
                                    LONG tempPointId = -1000;
                                    LONG tempPointOffset = -1000;
                                    string tempPointType = "(none)";
                                    rdr["pointid"] >> tempPointId;
                                    rdr["pointoffset"] >> tempPointOffset;
                                    rdr["pointtype"] >> tempPointType;
    
                                    CtiCCTwoWayPoints* twoWayPts = (CtiCCTwoWayPoints*)currentCCCapBank->getTwoWayPoints();
                                    if (resolvePointType(tempPointType) == StatusPointType) 
                                    {
                                    
                                        if (twoWayPts->setTwoWayPointId(StatusPointType, tempPointOffset, tempPointId) )
                                        {
                                            currentCCCapBank->getPointIds()->push_back(tempPointId);
                                            pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                                        } 
                                    }
                                    else if (resolvePointType(tempPointType) == AnalogPointType) 
                                    {
                                        if (twoWayPts->setTwoWayPointId(AnalogPointType, tempPointOffset, tempPointId) )
                                        {
                                            currentCCCapBank->getPointIds()->push_back(tempPointId);
                                            pointid_capbank_map->insert(make_pair(tempPointId,currentCCCapBank));
                                        }
                                    }
                                    else if (resolvePointType(tempPointType) == PulseAccumulatorPointType) 
                                    {
                                        if (twoWayPts->setTwoWayPointId(PulseAccumulatorPointType, tempPointOffset, tempPointId) )
                                        {
                                            currentCCCapBank->getPointIds()->push_back(tempPointId);
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
                    RWDBSelector selector = db.selector();
                    selector << dynamicCCTwoWayTable["deviceid"]
                             << dynamicCCTwoWayTable["recloseblocked"]
                             << dynamicCCTwoWayTable["controlmode"] 
                             << dynamicCCTwoWayTable["autovoltcontrol"]
                             << dynamicCCTwoWayTable["lastcontrol"]
                             << dynamicCCTwoWayTable["condition"]
                             << dynamicCCTwoWayTable["opfailedneutralcurrent"]
                             << dynamicCCTwoWayTable["neutralcurrentfault"]
                             << dynamicCCTwoWayTable["badrelay"]
                             << dynamicCCTwoWayTable["dailymaxops"]
                             << dynamicCCTwoWayTable["voltagedeltaabnormal"]
                             << dynamicCCTwoWayTable["tempalarm"]
                             << dynamicCCTwoWayTable["dstactive"]
                             << dynamicCCTwoWayTable["neutrallockout"]
                             << dynamicCCTwoWayTable["ignoredindicator"]
                             << dynamicCCTwoWayTable["voltage"]
                             << dynamicCCTwoWayTable["highvoltage"]
                             << dynamicCCTwoWayTable["lowvoltage"]
                             << dynamicCCTwoWayTable["deltavoltage"]
                             << dynamicCCTwoWayTable["analoginputone"]
                             << dynamicCCTwoWayTable["temp"]
                             << dynamicCCTwoWayTable["rssi"] 
                             << dynamicCCTwoWayTable["ignoredreason"]
                             << dynamicCCTwoWayTable["totalopcount"]
                             << dynamicCCTwoWayTable["uvopcount"]
                             << dynamicCCTwoWayTable["ovopcount"]
                             << dynamicCCTwoWayTable["ovuvcountresetdate"]
                             << dynamicCCTwoWayTable["uvsetpoint"]
                             << dynamicCCTwoWayTable["ovsetpoint"]
                             << dynamicCCTwoWayTable["ovuvtracktime"]
                             << dynamicCCTwoWayTable["lastovuvdatetime"]
                             << dynamicCCTwoWayTable["neutralcurrentsensor"]
                             << dynamicCCTwoWayTable["neutralcurrentalarmsetpoint"]
                             << dynamicCCTwoWayTable["ipaddress"]
                             << dynamicCCTwoWayTable["udpport"];
    
    
                    selector.from(dynamicCCTwoWayTable);
                    selector.from(capBankTable);
                    if (capBankId > 0)
                    {                
                        selector.where(capBankTable["controldeviceid"] == dynamicCCTwoWayTable["deviceid"] &&
                                   capBankTable["deviceid"] == capBankId);
                    }
                    else
                        selector.where(capBankTable["controldeviceid"] == dynamicCCTwoWayTable["deviceid"] &&
                                       capBankTable["controldeviceid"] != 0);
    
                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }
    
                    RWDBReader rdr = selector.reader(conn);
                    RWDBNullIndicator isNull;
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
                                ((CtiCCTwoWayPoints*)currentCCCapBank->getTwoWayPoints())->setDynamicData(rdr);
                            }
                        }
                    }
                }


            }
        }

        //_reregisterforpoints = TRUE;

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCCSubstationBusStore::reloadOperationStatsFromDatabase(long paoId, map< long, CtiCCCapBankPtr > *paobject_capbank_map,
                                                        map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                                        map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                                        map< long, CtiCCSubstationPtr > *paobject_substation_map,
                                                        map< long, CtiCCAreaPtr > *paobject_area_map,
                                                        map< long, CtiCCSpecialPtr > *paobject_specialarea_map )
{   
    try
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        {
            if ( conn.isValid() )
            {   

                CtiTime currentDateTime;
                RWDBDatabase db = getDatabase();
                RWDBTable dynamicCCOpStatsTable = db.table("dynamicccoperationstatistics");
                RWDBTable capBankTable = db.table("capbank");
                
                {
                    RWDBSelector selector = db.selector();
                    selector << dynamicCCOpStatsTable["paobjectid"]
                    << dynamicCCOpStatsTable["userdefopcount"] 
                    << dynamicCCOpStatsTable["userdefconffail"]
                    << dynamicCCOpStatsTable["dailyopcount"] 
                    << dynamicCCOpStatsTable["dailyconffail"] 
                    << dynamicCCOpStatsTable["weeklyopcount"] 
                    << dynamicCCOpStatsTable["weeklyconffail"] 
                    << dynamicCCOpStatsTable["monthlyopcount"] 
                    << dynamicCCOpStatsTable["montlyconffail"]; 

                    selector.from(dynamicCCOpStatsTable);

                    if (paoId > 0)
                    {                
                        selector.where(dynamicCCOpStatsTable["paobjectid"]== paoId);
                    }

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    CtiCCCapBankPtr currentCCCapBank = NULL;
                    CtiCCFeederPtr currentFeeder = NULL;
                    CtiCCSubstationBusPtr currentSubBus = NULL;
                    CtiCCSubstationPtr currentStation = NULL;
                    CtiCCAreaPtr currentArea = NULL;
                    RWDBNullIndicator isNull;
                    while ( rdr() )
                    {
                        LONG paoId;
                        rdr["paobjectid"] >> paoId;
                        currentCCCapBank = findCapBankByPAObjectID(paoId);
                        currentFeeder = findFeederByPAObjectID(paoId);
                        currentSubBus = findSubBusByPAObjectID(paoId);
                        currentStation = findSubstationByPAObjectID(paoId);
                        currentArea = findAreaByPAObjectID(paoId);
                        if (currentCCCapBank != NULL)
                        {
                            currentCCCapBank->getOperationStats().setDynamicData(rdr);
                        }
                        if (currentFeeder != NULL)
                        {
                           // currentFeeder->getOpsStatistics().setDynamicData(rdr);
                        }
                        if (currentSubBus != NULL)
                        {
                           // currentSubBus->getOpsStatistics().setDynamicData(rdr);
                        }

                        if (currentStation != NULL)
                        {
                           // currentStation->getOpsStatistics().setDynamicData(rdr);
                        }

                        if (currentFeeder != NULL)
                        {
                           // currentArea->getOpsStatistics().setDynamicData(rdr);
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


void CtiCCSubstationBusStore::reloadMonitorPointsFromDatabase(long capBankId, map< long, CtiCCCapBankPtr > *paobject_capbank_map,
                                                        map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                                        map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                                        multimap< long, CtiCCCapBankPtr > *pointid_capbank_map)
{
    try
    {

        long monPointId = 0;

            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {

                if ( conn.isValid() )
                {   

                    CtiTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    //RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    //RWDBTable pointTable = db.table("point");
                   // RWDBTable deviceTable = db.table("device");
                    //RWDBTable capBankTable = db.table("capbank");
                   // RWDBTable dynamicCCCapBankTable = db.table("dynamiccccapbank");
                   // RWDBTable ccFeederBankListTable = db.table("ccfeederbanklist");
                    RWDBTable ccMonitorBankListTable = db.table("ccmonitorbanklist");
                    RWDBTable dynamicCCMonitorBankHistoryTable = db.table("dynamicccmonitorbankhistory");
                    RWDBTable dynamicCCMonitorPointResponseTable = db.table("dynamicccmonitorpointresponse");

                {
                    //LOADING OF MONITOR POINTS.
                    RWDBSelector selector = db.selector();
                    selector << ccMonitorBankListTable["bankid"]
                    << ccMonitorBankListTable["pointid"]
                    << ccMonitorBankListTable["displayorder"]
                    << ccMonitorBankListTable["scannable"]
                    << ccMonitorBankListTable["ninavg"]
                    << ccMonitorBankListTable["upperbandwidth"]
                    << ccMonitorBankListTable["lowerbandwidth"];     
                    selector.from(ccMonitorBankListTable);
                    //selector.from(capBankTable);

                    if (capBankId > 0) 
                    {
                        selector.where(capBankId == ccMonitorBankListTable["bankid"]);
                    }
                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    CtiCCMonitorPointPtr currentMonPoint = NULL;
                    CtiCCPointResponsePtr currentPointResponse = NULL;
                    while ( rdr() )
                    {

                        long currentCapBankId;
                        long currentPointId;
                        rdr["bankid"] >> currentCapBankId;
                        rdr["pointid"] >> currentPointId;

                        currentMonPoint = new CtiCCMonitorPoint(rdr);
                        if (capBankId > 0) 
                        {
                            monPointId = currentPointId;
                        }

                        CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(currentMonPoint->getBankId())->second;
                        if (currentCCCapBank != NULL) 
                        {
                            currentCCCapBank->getMonitorPoint().push_back(currentMonPoint);
                            pointid_capbank_map->insert(make_pair(currentMonPoint->getPointId(),currentCCCapBank));

                            if (paobject_feeder_map->find(currentCCCapBank->getParentId()) != paobject_feeder_map->end())
                            {

                                CtiCCFeederPtr currentCCFeeder = paobject_feeder_map->find(currentCCCapBank->getParentId())->second;
                                if (currentCCFeeder != NULL) 
                                {
                                    if (!stringCompareIgnoreCase(currentCCFeeder->getControlMethod(),CtiCCSubstationBus::SubstationBusControlMethod) )
                                    {
                                        CtiCCSubstationBusPtr currentSubstationBus = paobject_subbus_map->find(currentCCFeeder->getParentId())->second;
                                        if (currentSubstationBus != NULL) 
                                        {
                                            CtiFeeder_vec& feeds = currentSubstationBus->getCCFeeders();

                                            for (LONG aa = 0; aa < feeds.size(); aa++) 
                                            {
                                                currentCCFeeder = (CtiCCFeeder*)feeds[aa];
                                                CtiCCCapBank_SVector& banks = currentCCFeeder->getCCCapBanks();
                                                for (LONG a = 0; a < banks.size(); a++) 
                                                {
                                                    CtiCCCapBank* bank = (CtiCCCapBank*)banks[a];
                                                    
                                                    currentPointResponse = new CtiCCPointResponse(rdr);
                                                    currentPointResponse->setBankId(bank->getPAOId());
                                                    currentPointResponse->setPointId(currentMonPoint->getPointId());                                        
                                              
                                                    bank->getPointResponse().push_back(currentPointResponse);
                                                    {
                                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                                        dout << CtiTime() << " currentPointResponse bankId: "<<bank->getPAOId()<<" pointId: "<<currentMonPoint->getPointId() << endl;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        CtiCCCapBank_SVector& banks = currentCCFeeder->getCCCapBanks();
                                        for (LONG a = 0; a < banks.size(); a++) 
                                        {
                                            CtiCCCapBank* bank = (CtiCCCapBank*)banks[a];
                                            currentPointResponse = new CtiCCPointResponse(rdr);
                                            currentPointResponse->setBankId(bank->getPAOId());
                                            currentPointResponse->setPointId(currentMonPoint->getPointId());                                        

                                            bank->getPointResponse().push_back(currentPointResponse);
                                            
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << CtiTime() << " currentPointResponse bankId: "<<bank->getPAOId()<<" pointId: "<<currentMonPoint->getPointId() << endl;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                    }
                }
                {
                    //LOADING OF MONITOR POINTS.
                    RWDBSelector selector = db.selector();
                    selector << dynamicCCMonitorBankHistoryTable["bankid"]
                    << dynamicCCMonitorBankHistoryTable["pointid"]
                    << dynamicCCMonitorBankHistoryTable["value"]
                    << dynamicCCMonitorBankHistoryTable["datetime"]
                    << dynamicCCMonitorBankHistoryTable["scaninprogress"];
                    
                    selector.from(dynamicCCMonitorBankHistoryTable);
                    //selector.from(capBankTable);
                    // 
                    if (capBankId > 0) 
                    {
                        selector.where(dynamicCCMonitorBankHistoryTable["bankid"] == capBankId ||
                                       dynamicCCMonitorBankHistoryTable["pointid"] == monPointId);
                    }

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    CtiCCMonitorPoint* currentMonPoint = NULL;

                    while ( rdr() )
                    {
                        long currentCapBankId, currentPointId;
                        float value;
                        rdr["bankid"] >> currentCapBankId;
                        rdr["pointid"] >> currentPointId;

                        CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(currentCapBankId)->second;
                        vector <CtiCCMonitorPoint*>& monPoints = currentCCCapBank->getMonitorPoint();
                        for (int i = 0; i < monPoints.size(); i++)
                        {
                            currentMonPoint = (CtiCCMonitorPoint*)monPoints[i];
                            if (currentMonPoint->getPointId() == currentPointId)
                            {
                                currentMonPoint->setDynamicData(rdr);
                                break;
                            }
                        }
                        
                    }
                }
                {
                    //LOADING OF MONITOR POINTS.
                    RWDBSelector selector = db.selector();
                    selector << dynamicCCMonitorPointResponseTable["bankid"]
                    << dynamicCCMonitorPointResponseTable["pointid"]
                    << dynamicCCMonitorPointResponseTable["preopvalue"]
                    << dynamicCCMonitorPointResponseTable["delta"];
                    
                    selector.from(dynamicCCMonitorPointResponseTable);

                    if (capBankId > 0) 
                    {
                        selector.where( dynamicCCMonitorPointResponseTable["bankid"] == capBankId ||
                                        dynamicCCMonitorPointResponseTable["pointid"] == monPointId);
                    }

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    CtiCCPointResponsePtr currentPointResponse = NULL;
                    while ( rdr() )
                    {
                        long currentCapBankId;
                        long currentPointId;
                        float delta;
                        float preopvalue;

                        rdr["bankid"] >> currentCapBankId;
                        rdr["pointid"] >> currentPointId;
                        CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(currentCapBankId)->second;
                        vector <CtiCCPointResponsePtr>& ptResponses = currentCCCapBank->getPointResponse();

                       // if (ptResponses != NULL) 
                        {
                            LONG numResponses = ptResponses.size();
                            for (int i = 0; i < numResponses; i++)
                            {
                                currentPointResponse = (CtiCCPointResponsePtr)ptResponses[i];
                                if (currentPointResponse->getPointId() == currentPointId)
                                {
                                    currentPointResponse->setDynamicData(rdr);
                                    break;
                                }
                            }
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


void CtiCCSubstationBusStore::reloadCapBankStatesFromDatabase()
{
    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
                if ( conn.isValid() )
                {   
                    if ( _ccCapBankStates->size() > 0 )
                    {
                        delete_vector(_ccCapBankStates);
                        _ccCapBankStates->clear();
                        if (_ccCapBankStates->size() > 0)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " _ccCapBankStates did NOT get destroyed " << endl;
                        }
                    }

                    RWDBDatabase db = getDatabase();
                    RWDBTable stateTable = db.table("state");

                    RWDBSelector selector = db.selector();
                    selector << stateTable["text"]
                    << stateTable["foregroundcolor"]
                    << stateTable["backgroundcolor"];

                    selector.from(stateTable);

                    selector.where(stateTable["stategroupid"]==3 && stateTable["rawstate"]>=0);

                    selector.orderBy(stateTable["rawstate"]);

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);

                    while ( rdr() )
                    {
                        CtiCCState* ccState = new CtiCCState(rdr);
                        _ccCapBankStates->push_back( ccState );
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


void CtiCCSubstationBusStore::reloadClientLinkStatusPointFromDatabase()
{
    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
                if ( conn.isValid() )
                {   
                    /*if ( _ccLinkStatusPoint > 0 )
                    {
                        
                    } */

                    RWDBDatabase db = getDatabase();
                    RWDBTable fdrTranslationTable = db.table("fdrtranslation");

                    RWDBSelector selector = db.selector();
                    selector << fdrTranslationTable["pointid"]
                    << fdrTranslationTable["directiontype"]
                    << fdrTranslationTable["interfacetype"]
                    << fdrTranslationTable["destination"]
                    << fdrTranslationTable["translation"];

                    selector.from(fdrTranslationTable);

                    selector.where(fdrTranslationTable["directiontype"]=="Link Status" && 
                                   fdrTranslationTable["interfacetype"]=="SYSTEM" );


                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
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

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCCSubstationBusStore::reloadMapOfBanksToControlByLikeDay(long subbusId, long feederId,  
                                  map< long, long> *controlid_action_map,
                                  CtiTime &lastSendTime, int fallBackConstant)
{
    try
    {   
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        {

            RWDBConnection conn = getConnection();
            {
                if ( conn.isValid() )
                {   
                    RWDBDatabase db = getDatabase();
                    RWDBTable ccEventLogTable = db.table("cceventlog");
                    CtiTime timeNow = CtiTime();

                    RWDBSelector selector = db.selector();
                    selector << ccEventLogTable["pointid"]
                    << ccEventLogTable["datetime"]
                    << ccEventLogTable["subid"]
                    << ccEventLogTable["feederid"]
                    << ccEventLogTable["eventtype"]
                    << ccEventLogTable["value"]
                    << ccEventLogTable["text"];

                    selector.from(ccEventLogTable);

                    if (subbusId != 0)
                    {
                        selector.where( (ccEventLogTable["text"].like("Open sent%") || 
                                    ccEventLogTable["text"].like("Close sent%") ) && 
                                    ccEventLogTable["subid"]==subbusId  && 
                                    (ccEventLogTable["datetime"] > toRWDBDT(lastSendTime - fallBackConstant) && 
                                    ccEventLogTable["datetime"] <= toRWDBDT(timeNow - fallBackConstant) ) );
                    }
                    else
                    {
                        selector.where( (ccEventLogTable["text"].like("Open sent%") || 
                                    ccEventLogTable["text"].like("Close sent%") ) && 
                                    ccEventLogTable["feederid"]==feederId  && 
                                    (ccEventLogTable["datetime"] > toRWDBDT(lastSendTime - fallBackConstant) && 
                                    ccEventLogTable["datetime"] <= toRWDBDT(timeNow - fallBackConstant) ) );
                    }

                    selector.orderBy(ccEventLogTable["datetime"]);



                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    
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
                            controlValue == CtiCCCapBank::CloseFail )
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
        
        }

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return;
}

void CtiCCSubstationBusStore::locateOrphans(list<long> *orphanCaps, list<long> *orphanFeeders, map<long, CtiCCCapBankPtr> paobject_capbank_map,
                       map<long, CtiCCFeederPtr> paobject_feeder_map, map<long, long> capbank_feeder_map, map<long, long> feeder_subbus_map)
{
    try
    {   
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
        {
        orphanFeeders->clear();
        map <long, CtiCCFeederPtr>::iterator iter = paobject_feeder_map.begin();
        while (iter != paobject_feeder_map.end())
        {
            if (feeder_subbus_map.find(iter->first) == feeder_subbus_map.end())
            {
                orphanFeeders->push_back(iter->first);
            }
            iter++;
        }
        orphanCaps->clear();
        map <long, CtiCCCapBankPtr>::iterator capIter = paobject_capbank_map.begin();
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

BOOL CtiCCSubstationBusStore::isFeederOrphan(long feederId)
{
    BOOL retVal = FALSE;

    try
    {   
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
        {   
            list <long>::iterator iter = _orphanedFeeders.begin();

            while (iter != _orphanedFeeders.end())
            {
                if (*iter == feederId)
                    return TRUE;
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
    return retVal;
}
BOOL CtiCCSubstationBusStore::isCapBankOrphan(long capBankId)
{
    BOOL retVal = FALSE;

    try
    {   
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
        {   
            list <long>::iterator iter = _orphanedCapBanks.begin();

            while (iter != _orphanedCapBanks.end())
            {
                if (*iter == capBankId)
                {
                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " Cap " <<capBankId<<" is on Orphan list. "<< endl;
                    }
                    return TRUE;
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
    return retVal;
}
void CtiCCSubstationBusStore::removeFromOrphanList(long ccId)
{
    try
    {   
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
        {   
            _orphanedCapBanks.remove(ccId);
            _orphanedFeeders.remove(ccId);
        }
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
        for(LONG h=0;h<_ccSubstationBuses->size();h++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses).at(h);
            if (currentCCSubstationBus->getParentId() == substationId) 
            {
                 deleteList.push_back(currentCCSubstationBus->getPAOId());
            }
        }
        for( std::list<int>::iterator itr = deleteList.begin(); itr != deleteList.end(); itr++)
        {
            deleteSubBus(*itr);
        }

        try
        {
            LONG areaId = substationToDelete->getParentId();
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
                    spArea->getSubstationIds()->remove(substationId);
                }
                itrSp++;
            }
            
            string substationName = substationToDelete->getPAOName();
            _paobject_substation_map.erase(substationToDelete->getPAOId());
            _substation_area_map.erase(substationToDelete->getPAOId());
            _substation_specialarea_map.erase(substationToDelete->getPAOId());
            CtiCCSubstation_vec::iterator itr = _ccSubstations->begin();
            for( ;itr != _ccSubstations->end(); itr++ )
            {
                CtiCCSubstation *substation = *itr;
                if (substation->getPAOId() == substationId)
                {
                    _ccSubstations->erase(itr);
                    break;
                }
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
                _pointid_area_map.clear();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            LONG stationId;
            LONG subBusId;
            
            CtiCCSubstationPtr station = NULL;
            list <LONG>::iterator iter = areaToDelete->getSubStationList()->begin();
            while (iter != areaToDelete->getSubStationList()->end())
            {   
                stationId = *iter;
                station = findSubstationByPAObjectID(stationId);
                if (station != NULL)
                {                   
                    list <LONG>::iterator iterBus = station->getCCSubIds()->begin();
                    while (iterBus  != station->getCCSubIds()->end())
                    { 
                        subBusId = *iterBus;
                        //_subbus_substation_map.erase(subBusId);
                        deleteSubBus(subBusId);
                        iterBus = station->getCCSubIds()->erase(iterBus);
                    }
                }
                //_substation_area_map.erase(stationId);
                deleteSubstation(stationId);
                iter = areaToDelete->getSubStationList()->erase(iter);
            }                

            try
            {
                string areaName = areaToDelete->getPAOName();
                _paobject_area_map.erase(areaToDelete->getPAOId());
                CtiCCArea_vec::iterator itr = _ccGeoAreas->begin();
                while ( itr != _ccGeoAreas->end() )
                {
                    CtiCCArea *area = *itr;
                    if (area->getPAOId() == areaId)
                    {
                        itr = _ccGeoAreas->erase(itr);
                        break;
                    }else
                        ++itr;

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
                _pointid_specialarea_map.clear();
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

            LONG stationId;
            LONG subBusId;
            list <LONG>::const_iterator iter = spAreaToDelete->getSubstationIds()->begin();
            while (iter != spAreaToDelete->getSubstationIds()->end())
            {
                stationId = *iter;
                CtiCCSubstationPtr station = NULL;
                if (_paobject_substation_map.find(stationId) != _paobject_substation_map.end())
                    station = _paobject_substation_map.find(stationId)->second;
                if ( station != NULL)
                {
                    station->setSaEnabledFlag(FALSE);
                }
                _substation_specialarea_map.erase(stationId);
                iter++;
            }

            try
            {
                string areaName = spAreaToDelete->getPAOName();
                _paobject_specialarea_map.erase(spAreaToDelete->getPAOId());
                CtiCCSpArea_vec::iterator itr = _ccSpecialAreas->begin();
                while ( itr != _ccSpecialAreas->end() )
                {
                    CtiCCSpecial *area = *itr;
                    if (area->getPAOId() == areaId)
                    {
                        itr = _ccSpecialAreas->erase(itr);
                        break;
                    }else
                        ++itr;

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
        return;

    try
    {            
        CtiFeeder_vec& ccFeeders = subToDelete->getCCFeeders();
        std::list<int> deleteFeederList;
        long feedCount = ccFeeders.size();
        for(LONG i=0;i<feedCount;i++)
        {
            CtiCCFeederPtr feederToDelete = (CtiCCFeeder*)ccFeeders.front();
            deleteFeederList.push_back(feederToDelete->getPAOId());
        }
        for( std::list<int>::iterator itr = deleteFeederList.begin(); itr != deleteFeederList.end(); itr++ )
        {
            deleteFeeder(*itr);
        }

        try
        {
            //Delete pointids on this sub
            list <LONG> *pointIds  = subToDelete->getPointIds();
            while (!pointIds->empty())
            {
                LONG pointid = pointIds->front();
                pointIds->pop_front();
                int ptCount = getNbrOfSubBusesWithPointID(pointid);
                if (ptCount > 1)
                {
                    multimap< long, CtiCCSubstationBusPtr >::iterator iter1 = _pointid_subbus_map.lower_bound(pointid);
                    while (iter1 != _pointid_subbus_map.end() || iter1 != _pointid_subbus_map.upper_bound(pointid))
                    {
                       if (((CtiCCSubstationBusPtr)iter1->second)->getPAOId() == subToDelete->getPAOId())
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
                    if (!stringCompareIgnoreCase(tempSub->getControlUnits(), CtiCCSubstationBus::KVARControlUnits))
                    {
                        LONG pointid = subToDelete->getCurrentVarLoadPointId();
                        _pointid_subbus_map.erase(pointid);
                        tempSub->getPointIds()->remove(pointid);
                    }
                    else if (!!stringCompareIgnoreCase(tempSub->getControlUnits(), CtiCCSubstationBus::VoltControlUnits))
                    {
                        LONG pointid = subToDelete->getCurrentVoltLoadPointId();
                        _pointid_subbus_map.erase(pointid);
                        tempSub->getPointIds()->remove(pointid);
                    }
                }
                _altsub_sub_idmap.erase(subBusId);
            }
            //Deleting this sub from altSubMap
            if (subToDelete->getAltDualSubId() != subToDelete->getPAOId())
            {
                multimap <long, long>::iterator iter = _altsub_sub_idmap.begin();
                while (iter != _altsub_sub_idmap.end())
                {
                    if (iter->second == subToDelete->getPAOId())
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
            string subBusName = subToDelete->getPAOName();
            _paobject_subbus_map.erase(subToDelete->getPAOId());
            _subbus_substation_map.erase(subToDelete->getPAOId());
            CtiCCSubstationBus_vec::iterator itr = _ccSubstationBuses->begin();
            while ( itr != _ccSubstationBuses->end() )
            {
                CtiCCSubstationBus *subBus = *itr;
                if (subBus->getPAOId() == subBusId)
                {
                    itr = _ccSubstationBuses->erase(itr);
                    break;
                }else
                    ++itr;
        
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
        CtiCCCapBank_SVector &ccCapBanks = feederToDelete->getCCCapBanks();
        std::list<int> capbankDeleteList;
        long capCount = ccCapBanks.size();
        for (LONG j = 0; j < capCount; j++)
        {
            CtiCCCapBankPtr capBankToDelete = (CtiCCCapBank*)ccCapBanks.front();
            capbankDeleteList.push_back(capBankToDelete->getPAOId());
        }
        for( std::list<int>::iterator itr = capbankDeleteList.begin(); itr != capbankDeleteList.end(); itr++ )
        {
            deleteCapBank(*itr);
        }

        list <LONG> *pointIds  = feederToDelete->getPointIds();
        //Delete pointids on this feeder
        while (!pointIds->empty())
        {
            LONG pointid = pointIds->front();
            pointIds->pop_front();
            int ptCount = getNbrOfFeedersWithPointID(pointid);
            if (ptCount > 1)
            {
                multimap< long, CtiCCFeederPtr >::iterator iter1 = _pointid_feeder_map.lower_bound(pointid);
                while (iter1 != _pointid_feeder_map.end() || iter1 != _pointid_feeder_map.upper_bound(pointid))
                {
                    if (((CtiCCFeederPtr)iter1->second)->getPAOId() == feederToDelete->getPAOId())
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

            LONG subBusId = findSubBusIDbyFeederID(feederId);
            if (subBusId != NULL)
            {                   
                CtiCCSubstationBusPtr subBus = findSubBusByPAObjectID(subBusId);
                if (subBus != NULL)
                {
                    subBus->deleteCCFeeder(feederId);
                }
            }

            string feederName = feederToDelete->getPAOName();
            _paobject_feeder_map.erase(feederToDelete->getPAOId());
            _feeder_subbus_map.erase(feederToDelete->getPAOId());

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

void CtiCCSubstationBusStore::deleteStrategy(long strategyId)
{
    CtiCCStrategyPtr strategyToDelete = findStrategyByStrategyID(strategyId);

    try
    {
        if (strategyToDelete != NULL)
        {
            _strategyid_strategy_map.erase(strategyId);
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
          //  capBankToDelete->dumpDynamicData();

            list <LONG>* pointIds  = capBankToDelete->getPointIds();
            //Delete pointids on this feeder
            while (!pointIds->empty())
            {
                LONG pointid = pointIds->front();
                pointIds->pop_front();
                int ptCount = getNbrOfCapBanksWithPointID(pointid);
                if (ptCount > 1)
                {
                    multimap< long, CtiCCCapBankPtr >::iterator iter1 = _pointid_capbank_map.lower_bound(pointid);
                    while (iter1 != _pointid_capbank_map.end() || iter1 != _pointid_capbank_map.upper_bound(pointid))
                    {
                        if (((CtiCCCapBankPtr)iter1->second)->getPAOId() == capBankToDelete->getPAOId())
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

            if( _CC_DEBUG & CC_DEBUG_DELETION )
            {
                if (_pointid_capbank_map.size() > 0) 
                {
                    multimap< long, CtiCCCapBankPtr >::iterator iter = _pointid_capbank_map.begin();
                    while (iter != _pointid_capbank_map.end()) 
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " pointid " <<iter->first<< endl;
                        }
                        iter++;
                    }
                }
            }
            


            for (int i = 0; i < capBankToDelete->getMonitorPoint().size(); i++)
            {
                LONG pointid =  ((CtiCCMonitorPointPtr)capBankToDelete->getMonitorPoint()[i])->getPointId();
                _pointid_capbank_map.erase(pointid);
            }
            try
            {   
                CtiCCFeederPtr feeder = NULL;
                LONG feederId = findFeederIDbyCapBankID(capBankId);
                if (feederId != NULL)
                {                   
                    feeder = findFeederByPAObjectID(feederId);
                    if (feeder != NULL)
                    {                  
                        feeder->deleteCCCapBank(capBankId);
                    }
                }

                string capBankName = capBankToDelete->getPAOName();
                _paobject_capbank_map.erase(capBankToDelete->getPAOId());
                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    if (_paobject_capbank_map.size() > 0) 
                    {
                        map< long, CtiCCCapBankPtr >::iterator iter = _paobject_capbank_map.begin();
                        while (iter != _paobject_capbank_map.end()) 
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " capbankid " <<iter->first<< endl;
                            }
                            iter++;
                        }
                    }
                }

                _capbank_subbus_map.erase(capBankToDelete->getPAOId());
                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    if (_capbank_subbus_map.size() > 0) 
                    {
                        map< long, long >::iterator iter = _capbank_subbus_map.begin();
                        while (iter != _capbank_subbus_map.end()) 
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " capid " <<iter->first<<" on sub "<<iter->second<<endl;
                            }
                            iter++;
                        }
                    }
                }
                _capbank_feeder_map.erase(capBankToDelete->getPAOId());
                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    if (_capbank_feeder_map.size() > 0) 
                    {
                        map< long, long >::iterator iter = _capbank_feeder_map.begin();
                        while (iter != _capbank_feeder_map.end()) 
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " capid " <<iter->first<<" on feed "<<iter->second<<endl;
                            }
                            iter++;
                        }
                    }
                }
                _cbc_capbank_map.erase(capBankToDelete->getControlDeviceId());
                if( _CC_DEBUG & CC_DEBUG_DELETION )
                {
                    if (_cbc_capbank_map.size() > 0) 
                    {
                        map< long, long >::iterator iter = _cbc_capbank_map.begin();
                        while (iter != _cbc_capbank_map.end()) 
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " cbcid " <<iter->first<< endl;
                            }
                            iter++;
                        }
                    }
                }
                capBankToDelete = findCapBankByPAObjectID(capBankId);
                if (capBankToDelete != NULL)
                {   
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << "What the $#@%!." << endl;
                    capBankToDelete = NULL;
                }
                else
                {
                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << "CAPBANK: " << capBankName <<" has been deleted." << endl;
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
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCSubstationBusStore::checkDBReloadList()
{
    BOOL sendBusInfo = false;
    BOOL forceFullReload = false;
    CtiTime currentDateTime;
    //list <CtiCCSubstationBusPtr> modifiedSubsList;
    CtiMultiMsg_vec modifiedSubsList;
    CtiMultiMsg_vec modifiedStationsList;
    ULONG msgBitMask = 0x00000000;
    ULONG msgSubsBitMask = 0x00000000;
    
    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
        {

            if (_reloadList.empty() && _2wayFlagUpdate) 
            {
                dumpAllDynamicData();
                _2wayFlagUpdate = FALSE;
            }
            else if (!_reloadList.empty()) 
            {
                dumpAllDynamicData();
                _2wayFlagUpdate = FALSE;
            }
            while (!_reloadList.empty())
            {
                CC_DBRELOAD_INFO reloadTemp = _reloadList.front();

                switch (reloadTemp.objecttype)
                {
                    //capbank
                    case CapBank:
                    {
                        if (reloadTemp.action == ChangeTypeUpdate)
                        {
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                 CtiLockGuard<CtiLogger> logger_guard(dout);
                                 dout << CtiTime() << " Reload Cap "<< reloadTemp.objectId<<" because of ChangeTypeUpdate message " << endl;
                            }
                            //reloadCapBankFromDatabase(reloadTemp.objectId);
                            reloadCapBankFromDatabase(reloadTemp.objectId, &_paobject_capbank_map, &_paobject_feeder_map,
                                                      &_paobject_subbus_map, &_pointid_capbank_map, &_capbank_subbus_map,
                                                      &_capbank_feeder_map, &_feeder_subbus_map, &_cbc_capbank_map );
                            reloadMonitorPointsFromDatabase(reloadTemp.objectId, &_paobject_capbank_map, &_paobject_feeder_map,
                                                      &_paobject_subbus_map, &_pointid_capbank_map);

                            if(isCapBankOrphan(reloadTemp.objectId) )
                               removeFromOrphanList(reloadTemp.objectId);

                            CtiCCCapBankPtr cap = findCapBankByPAObjectID(reloadTemp.objectId);
                            if (cap != NULL) 
                            {
                                //This finds the subId if a capbank is on a substation.
                                long subId = NULL;
                                long feederId = cap->getParentId();
                                CtiCCFeederPtr feeder = findFeederByPAObjectID(feederId);
                                if( feeder != NULL )
                                    subId = feeder->getParentId();
                                if (subId != NULL) 
                                {
                                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << CtiTime() << " Update Cap was found on sub " << endl;
                                    }
                                    CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(subId);
                                    if (tempSub != NULL)
                                    {
                                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " Sub " <<tempSub->getPAOName()<<" modified "<< endl;
                                        }
                                        modifiedSubsList.push_back(tempSub);
                                        msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                        msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                    }
                                    else
                                    {
                                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " Sub not found "<< endl;
                                        }
                                    }
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
                        else if (reloadTemp.action == ChangeTypeDelete)
                        {
                            long subId = findSubBusIDbyCapBankID(reloadTemp.objectId);
                            if (subId != NULL) 
                            {
                                if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                {
                                     CtiLockGuard<CtiLogger> logger_guard(dout);
                                     dout << CtiTime() << " Delete Cap "<<reloadTemp.objectId <<" was found on sub " << endl;
                                }
                                CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(subId);
                                if (tempSub != NULL)
                                {
                                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                    {
                                         CtiLockGuard<CtiLogger> logger_guard(dout);
                                         dout << CtiTime() << " Sub " <<tempSub->getPAOName()<<" modified "<< endl;
                                    }
                                    modifiedSubsList.push_back(tempSub);
                                    msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                    msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
 
                                }
                                else
                                {
                                    if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                                    {
                                         CtiLockGuard<CtiLogger> logger_guard(dout);
                                         dout << CtiTime() << " Sub " <<tempSub->getPAOName()<<" NOT modified "<< endl;
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

                            deleteCapBank(reloadTemp.objectId);
                            if(isCapBankOrphan(reloadTemp.objectId) )
                               removeFromOrphanList(reloadTemp.objectId); 

                            CtiCCExecutorFactory f;
                            CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::DELETE_ITEM, reloadTemp.objectId));
                            executor->Execute();
                            delete executor;

                        }
                        break;
                    }
                    //feeder                           
                    case Feeder:
                    {
                        if (reloadTemp.action == ChangeTypeUpdate)
                        {
                            CtiCCFeederPtr tempFeed = findFeederByPAObjectID(reloadTemp.objectId);
                            if (tempFeed != NULL) 
                            {  

                                reloadSubBusFromDatabase(tempFeed->getParentId(), &_strategyid_strategy_map, &_paobject_subbus_map, 
                                                        &_paobject_substation_map, &_pointid_subbus_map, 
                                                        &_altsub_sub_idmap, &_subbus_substation_map, _ccSubstationBuses);
                                if(isFeederOrphan(reloadTemp.objectId))
                                  removeFromOrphanList(reloadTemp.objectId);
                                CtiCCFeederPtr feed = findFeederByPAObjectID(reloadTemp.objectId);

                                if (feed != NULL) 
                                {
                                    long subId = findSubBusIDbyFeederID(reloadTemp.objectId);
                                    if (subId != NULL) 
                                    {
                                        CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(subId);
                                        if (tempSub != NULL)
                                        {
                                            modifiedSubsList.push_back(tempSub);
                                            msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                            msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                        }
                                    }
                                } 
                            }
                            else
                            {
                                reloadFeederFromDatabase(reloadTemp.objectId, &_strategyid_strategy_map, &_paobject_feeder_map,
                                                      &_paobject_subbus_map, &_pointid_feeder_map, &_feeder_subbus_map );

                                if(isFeederOrphan(reloadTemp.objectId))
                                    removeFromOrphanList(reloadTemp.objectId);

                                CtiCCFeederPtr feed = findFeederByPAObjectID(reloadTemp.objectId);
                                
                                if (feed != NULL) 
                                {
                                    long subId = findSubBusIDbyFeederID(reloadTemp.objectId);
                                    if (subId != NULL) 
                                    {
                                
                                        CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(subId);
                                        if (tempSub != NULL)
                                        {
                                            modifiedSubsList.push_back(tempSub);
                                            msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                            msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                        }
                                    }
                                }
                            }
                           
                        }
                        else if (reloadTemp.action == ChangeTypeDelete)
                        {
                            long subId = findSubBusIDbyFeederID(reloadTemp.objectId);
                            if (subId != NULL) 
                            {

                                CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(subId);
                                if (tempSub != NULL)
                                {
                                    modifiedSubsList.push_back(tempSub);
                                    msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                    msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                }
                            }
                            deleteFeeder(reloadTemp.objectId);
                            if(isFeederOrphan(reloadTemp.objectId) )
                               removeFromOrphanList(reloadTemp.objectId);

                            CtiCCExecutorFactory f;
                            CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::DELETE_ITEM, reloadTemp.objectId));
                            executor->Execute();
                            delete executor;

                        
                        }
                        break;
                    }
                    //subbus
                    case SubBus:
                    {
                        if (reloadTemp.action == ChangeTypeDelete)
                        {
                            LONG parentStationId = NULL;
                            CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(reloadTemp.objectId);
                            if (tempSub != NULL) 
                            {
                                parentStationId = tempSub->getParentId();
                            }
                            deleteSubBus(reloadTemp.objectId); 

                            CtiCCExecutorFactory f;
                            CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::DELETE_ITEM, reloadTemp.objectId));
                            executor->Execute();
                            delete executor;

                            if (parentStationId != NULL)
                            {
                                updateSubstationObjectList(parentStationId, modifiedStationsList);
                                msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                            }


                        }
                        else  // ChangeTypeAdd, ChangeTypeUpdate
                        {

                            CtiCCSubstationBusPtr tempSub = findSubBusByPAObjectID(reloadTemp.objectId);
                            if (tempSub != NULL) 
                            {  
                                reloadSubBusFromDatabase(tempSub->getPAOId(), &_strategyid_strategy_map, &_paobject_subbus_map, 
                                             &_paobject_substation_map, &_pointid_subbus_map, 
                                             &_altsub_sub_idmap, &_subbus_substation_map, _ccSubstationBuses);

                                CtiMultiMsg_vec::iterator iter = modifiedSubsList.begin();
                                while (iter != modifiedSubsList.end()) 
                                {
                                    CtiCCSubstationBus* temp =   (CtiCCSubstationBus *)*iter;
                                    if (temp->getPAOId() == reloadTemp.objectId)
                                    {  
                                        modifiedSubsList.erase(iter);
                                        iter = modifiedSubsList.end();
                                    }
                                    else
                                        iter++;
                                }
                                tempSub = findSubBusByPAObjectID(reloadTemp.objectId);

                                if (tempSub != NULL) 
                                {
                                    modifiedSubsList.push_back(tempSub);
                                }

                                msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;

                            }
                            else
                            {
                                reloadSubBusFromDatabase(reloadTemp.objectId, &_strategyid_strategy_map, &_paobject_subbus_map, 
                                             &_paobject_substation_map, &_pointid_subbus_map, 
                                             &_altsub_sub_idmap, &_subbus_substation_map, _ccSubstationBuses);

                                tempSub = findSubBusByPAObjectID(reloadTemp.objectId);

                                CtiMultiMsg_vec::iterator iter = modifiedSubsList.begin();
                                while (iter != modifiedSubsList.end()) 
                                {
                                    CtiCCSubstationBus* temp =   (CtiCCSubstationBus *)*iter;
                                    if (temp->getPAOId() == reloadTemp.objectId)
                                    {  
                                        modifiedSubsList.erase(iter);
                                        iter = modifiedSubsList.end();
                                    }
                                    else
                                        iter++;
                                }
                                if (tempSub != NULL) 
                                {
                                    modifiedSubsList.push_back(tempSub);
                                }
                                msgBitMask |= CtiCCSubstationBusMsg::SubBusAdded;
                            }
                       }
                        break;
                    }
                    //substation
                    case Substation:
                    {
                        if (reloadTemp.action == ChangeTypeDelete)
                        {
                            deleteSubstation(reloadTemp.objectId); 
                    
                            CtiCCExecutorFactory f;
                            CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::DELETE_ITEM, reloadTemp.objectId));
                            executor->Execute();
                            delete executor;

                        }
                        else  // ChangeTypeAdd, ChangeTypeUpdate
                        {   
                    
                            CtiCCSubstationPtr tempStation = findSubstationByPAObjectID(reloadTemp.objectId);
                            if (tempStation != NULL) 
                            {  
                                reloadSubstationFromDatabase(reloadTemp.objectId, &_paobject_substation_map,
                                                   &_paobject_area_map, &_paobject_specialarea_map, &_substation_area_map,
                                                   &_substation_specialarea_map, _ccSubstations);
                    
                                CtiCCSubstation *station = findSubstationByPAObjectID(reloadTemp.objectId);
                                if (station != NULL)
                                {        
                                    addSubBusObjectsToList(station->getCCSubIds(), modifiedSubsList);
                                    updateSubstationObjectList(station->getPAOId(), modifiedStationsList);
                                    msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                }
                                
                            }
                            else
                            {
                                reloadSubstationFromDatabase(reloadTemp.objectId, &_paobject_substation_map,
                                                   &_paobject_area_map, &_paobject_specialarea_map, &_substation_area_map, 
                                                   &_substation_specialarea_map, _ccSubstations);
                    
                                CtiCCSubstation *station = findSubstationByPAObjectID(reloadTemp.objectId);
                                if (station != NULL)
                                {        
                                    addSubBusObjectsToList(station->getCCSubIds(), modifiedSubsList);
                                    updateSubstationObjectList(station->getPAOId(), modifiedStationsList);

                                    msgSubsBitMask |= CtiCCSubstationsMsg::SubAdded;
                                }
                            }
                        }
                        break;
                    }
                    //area
                    case Area:
                    {
                        if (reloadTemp.action == ChangeTypeDelete)
                        {
                            deleteArea(reloadTemp.objectId); 

                            CtiCCExecutorFactory f;
                            CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::DELETE_ITEM, reloadTemp.objectId));
                            executor->Execute();
                            delete executor;


                            msgSubsBitMask = CtiCCSubstationsMsg::AllSubsSent;;
                            msgBitMask  = CtiCCSubstationBusMsg::AllSubBusesSent;;
                        }
                        else  // ChangeTypeAdd, ChangeTypeUpdate
                        {   
                            CtiCCAreaPtr tempArea = findAreaByPAObjectID(reloadTemp.objectId);

                            if (tempArea != NULL) 
                            {
                                if (tempArea->getSubStationList()->size() > 1)
                                {
                                    setValid(false);
                                    _reloadList.clear();
                                    forceFullReload = TRUE;
                                }
                                else
                                {   
                                    reloadAreaFromDatabase(reloadTemp.objectId, &_strategyid_strategy_map, 
                                                             &_paobject_area_map, &_pointid_area_map, _ccGeoAreas);
                               
                                    tempArea = findAreaByPAObjectID(reloadTemp.objectId);
                                    if (tempArea != NULL) 
                                    {
                                        list <LONG>::const_iterator iter = tempArea->getSubStationList()->begin();
                                        while (iter != tempArea->getSubStationList()->end())
                                        {   
                                            LONG stationId = *iter;
                                            CtiCCSubstation *station = findSubstationByPAObjectID(stationId);
                                            if (station != NULL)
                                            {        
                                                addSubBusObjectsToList(station->getCCSubIds(), modifiedSubsList);
                                                updateSubstationObjectList(stationId, modifiedStationsList);

                                                msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                                msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                            }
                                            iter++;
                                        }
                                    }
                                }
                            }
                            else
                            {   
                                reloadAreaFromDatabase(reloadTemp.objectId, &_strategyid_strategy_map, 
                                                         &_paobject_area_map, &_pointid_area_map, _ccGeoAreas);
                            
                                tempArea = findAreaByPAObjectID(reloadTemp.objectId);
                                if (tempArea != NULL) 
                                {
                                    list <LONG>::const_iterator iter = tempArea->getSubStationList()->begin();
                                    while (iter != tempArea->getSubStationList()->end())
                                    {   
                                        LONG stationId = *iter;
                                        CtiCCSubstation *station = findSubstationByPAObjectID(stationId);
                                        if (station != NULL)
                                        {        
                                            addSubBusObjectsToList(station->getCCSubIds(), modifiedSubsList);
                                            updateSubstationObjectList(stationId, modifiedStationsList);

                                            msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                            msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;

                                        }
                                        iter++;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    //special area
                    case SpecialArea:
                    {
                        if (reloadTemp.action == ChangeTypeDelete)
                        {
                            deleteSpecialArea(reloadTemp.objectId); 

                            CtiCCExecutorFactory f;
                            CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::DELETE_ITEM, reloadTemp.objectId));
                            executor->Execute();
                            delete executor;   


                            msgSubsBitMask = CtiCCSubstationsMsg::AllSubsSent;
                            msgBitMask  = CtiCCSubstationBusMsg::AllSubBusesSent;;
                        }
                        else  // ChangeTypeAdd, ChangeTypeUpdate
                        {   
                            CtiCCSpecialPtr tempSpArea = findSpecialAreaByPAObjectID(reloadTemp.objectId);

                            if (tempSpArea != NULL) 
                            {
                                if (tempSpArea->getSubstationIds()->size() > 1)
                                {
                                    deleteSpecialArea(reloadTemp.objectId); //this forces substation flags to saEnabled = false;
                                    setValid(false);
                                    _reloadList.clear();
                                    forceFullReload = TRUE;
                                }
                                else
                                {
                                       
                                    reloadSpecialAreaFromDatabase(reloadTemp.objectId, &_strategyid_strategy_map, 
                                                             &_paobject_specialarea_map, &_pointid_specialarea_map, _ccSpecialAreas);
                                    tempSpArea = findSpecialAreaByPAObjectID(reloadTemp.objectId);
                                    
                                    
                                    if (tempSpArea != NULL) 
                                    {   
                                    
                                        list <LONG>::const_iterator iter = tempSpArea->getSubstationIds()->begin();
                                        while (iter != tempSpArea->getSubstationIds()->end())
                                        {   
                                            LONG stationId = *iter;
                                            CtiCCSubstation *station = findSubstationByPAObjectID(stationId);
                                            if (station != NULL)
                                            {
                                                addSubBusObjectsToList(station->getCCSubIds(), modifiedSubsList);
                                                updateSubstationObjectList(stationId, modifiedStationsList);
                                                msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                                msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                            }
                                            iter++;
                                        } 
                                    }
                                }
                            }
                            else
                            {
                                reloadSpecialAreaFromDatabase(reloadTemp.objectId, &_strategyid_strategy_map, 
                                                             &_paobject_specialarea_map, &_pointid_specialarea_map, _ccSpecialAreas);
                                    tempSpArea = findSpecialAreaByPAObjectID(reloadTemp.objectId);
                                    
                                    
                                    if (tempSpArea != NULL) 
                                    {   
                                    
                                        list <LONG>::const_iterator iter = tempSpArea->getSubstationIds()->begin();
                                        while (iter != tempSpArea->getSubstationIds()->end())
                                        {   
                                            LONG stationId = *iter;
                                            CtiCCSubstation *station = findSubstationByPAObjectID(stationId);
                                            if (station != NULL)
                                            {
                                                addSubBusObjectsToList(station->getCCSubIds(), modifiedSubsList);
                                                updateSubstationObjectList(stationId, modifiedStationsList);
                                                msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                            }
                                            iter++;
                                        } 
                                    }
                            }
                        }
                        break;
                    }
                case Strategy:
                    {
                        if (reloadTemp.action != ChangeTypeDelete)
                        {
                            reloadStrategyFromDataBase(reloadTemp.objectId, &_strategyid_strategy_map);
                            LONG i=0;
                            for(i=0;i<_ccSpecialAreas->size();i++)
                            {
                                CtiCCSpecialPtr spArea = (CtiCCSpecialPtr)(*_ccSpecialAreas)[i];
                                if (!spArea->getDisableFlag() && spArea->getStrategyId() == reloadTemp.objectId)
                                {
                                    addSubstationObjectsToList(spArea->getSubstationIds(), modifiedSubsList);
                                }
                            }
                            for(i=0;i<_ccSubstationBuses->size();i++)
                            {
                                CtiCCSubstationBus* tempSub = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];
                                if (tempSub->getStrategyId() == reloadTemp.objectId) 
                                {
                                    CtiMultiMsg_vec::iterator iter = modifiedSubsList.begin();
                                    while (iter != modifiedSubsList.end()) 
                                    {
                                        if (((CtiCCSubstationBusPtr)*iter)->getPAOId() == tempSub->getPAOId()) 
                                        {  
                                            modifiedSubsList.erase(iter);
                                            iter = modifiedSubsList.end();
                                        }
                                        else
                                            iter++;
                                    }
                                    modifiedSubsList.push_back(tempSub);
                                    msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                    msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                }
                                else
                                {
                                    CtiFeeder_vec& tempFeeds = tempSub->getCCFeeders();
                                    for (int j = 0; j < tempFeeds.size(); j++) 
                                    {
                                        CtiCCFeederPtr fdr = (CtiCCFeederPtr)tempFeeds[j];
                                        if (fdr->getStrategyId() ==  reloadTemp.objectId) 
                                        {
                                            CtiMultiMsg_vec::iterator iter = modifiedSubsList.begin();
                                            while (iter != modifiedSubsList.end()) 
                                            {
                                                if (((CtiCCSubstationBusPtr)*iter)->getPAOId() == tempSub->getPAOId()) 
                                                {  
                                                    modifiedSubsList.erase(iter);
                                                    iter = modifiedSubsList.end();
                                                }
                                                else
                                                    iter++;
                                            }
                                            modifiedSubsList.push_back(tempSub);
                                            msgBitMask |= CtiCCSubstationBusMsg::SubBusModified;
                                            msgSubsBitMask |= CtiCCSubstationsMsg::SubModified;
                                        }
                                    }
                                }
                            }

                        }
                        else
                        {
                            deleteStrategy(reloadTemp.objectId);
                        }
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
                    sendBusInfo = TRUE;
                    _reloadList.pop_front();
                }
                
            }


            if (sendBusInfo)
            {     
                locateOrphans(&_orphanedCapBanks, &_orphanedFeeders, _paobject_capbank_map, _paobject_feeder_map,
                                     _capbank_feeder_map, _feeder_subbus_map);

                try
                {

                     CtiCommandMsg *pointAddMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::PointDataRequest, 15);
                     for(LONG i=0;i<modifiedSubsList.size();i++)
                     {
                         CtiCCSubstationBus* sub = (CtiCCSubstationBusPtr)modifiedSubsList[i];
                         sub->addAllSubPointsToMsg(pointAddMsg);
                         CtiFeeder_vec& feeds = sub->getCCFeeders();
                         for (LONG j = 0; j < feeds.size(); j++) 
                         {
                             CtiCCFeederPtr feed = (CtiCCFeederPtr)feeds[j];
                             feed->addAllFeederPointsToMsg(pointAddMsg);
                             CtiCCCapBank_SVector& caps = feed->getCCCapBanks();
                             for (LONG k = 0; k < caps.size(); k++) 
                             {
                                 CtiCCCapBankPtr cap = (CtiCCCapBankPtr)caps[k];
                                 cap->addAllCapBankPointsToMsg(pointAddMsg);
                                 if (stringContainsIgnoreCase(cap->getControlDeviceType(), "CBC 702") ) 
                                 {
                                     CtiCCTwoWayPoints* twoWayPts = (CtiCCTwoWayPoints*)cap->getTwoWayPoints();
                                     twoWayPts->addAllCBCPointsToMsg(pointAddMsg);
                                 }
                             }
                         }
                         
                     }

                     CtiCapController::getInstance()->sendMessageToDispatch(pointAddMsg);
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }


                _lastindividualdbreloadtime.now();

                /*if ( _wassubbusdeletedflag )
                {
                    msgBitMask = CtiCCSubstationBusMsg::AllSubBusesSent | CtiCCSubstationBusMsg::SubBusDeleted;
                } */

                for(LONG i=0;i<_ccSubstationBuses->size();i++)
                {
                    CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];
                    CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
                    for(LONG j=0;j<ccFeeders.size();j++)
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                        BOOL peakFlag = currentSubstationBus->isPeakTime(currentDateTime);
                        if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod)  &&
                            stringCompareIgnoreCase(currentFeeder->getStrategyName(),"(none)")  &&
                            (currentFeeder->getPeakStartTime() > 0 && currentFeeder->getPeakStopTime() > 0 ))
                        {
                            currentFeeder->isPeakTime(currentDateTime);
                        }
                        else
                        {
                            currentFeeder->setPeakTimeFlag(peakFlag);
                        }


                        currentFeeder->getMultipleMonitorPoints().clear();
                        CtiCCCapBank_SVector& capBanks = currentFeeder->getCCCapBanks();

                        for (LONG k=0;k<capBanks.size();k++) 
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)capBanks[k];

                            vector <CtiCCMonitorPointPtr>& monPoints = currentCapBank->getMonitorPoint();
                            for (LONG l=0; l<monPoints.size();l++) 
                            {
                                currentFeeder->getMultipleMonitorPoints().push_back(monPoints[l]);
                                currentSubstationBus->getMultipleMonitorPoints().push_back(monPoints[l]);
                            }
                        }
                    }
                }

                CtiCCExecutorFactory f;
                CtiCCExecutor* executor;

                if (msgBitMask & CtiCCSubstationBusMsg::AllSubBusesSent) 
                    executor = f.createExecutor(new CtiCCSubstationBusMsg(*_ccSubstationBuses,msgBitMask));
                else
				{

                    if (_CC_DEBUG & CC_DEBUG_RIDICULOUS) 
                    {
                        for (int ii = 0; ii < modifiedSubsList.size(); ii++) 
                        {
                            CtiCCSubstationBusPtr subby = (CtiCCSubstationBusPtr)modifiedSubsList[ii];
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - SUBBY! "<<subby->getPAOName() << endl;
                            }
                            for (int iii = 0; iii < subby->getCCFeeders().size(); iii++) 
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - FEEDY! "<<((CtiCCFeederPtr)subby->getCCFeeders()[iii])->getPAOName() << endl;
                                }
                            }
                        }
                    }
                    executor = f.createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,msgBitMask));
				}
                executor->Execute();
                delete executor;
                executor = f.createExecutor(new CtiCCCapBankStatesMsg(*_ccCapBankStates));
                executor->Execute();
                delete executor;
                executor = f.createExecutor(new CtiCCGeoAreasMsg(*_ccGeoAreas));
                executor->Execute();
                delete executor;
                executor = f.createExecutor(new CtiCCSpecialAreasMsg(*_ccSpecialAreas));
                executor->Execute();
                delete executor; 
                if (msgSubsBitMask & CtiCCSubstationsMsg::AllSubsSent) 
                    executor = f.createExecutor(new CtiCCSubstationsMsg(*_ccSubstations, msgSubsBitMask));
                else
				{
                    for (int ii = 0; ii < modifiedSubsList.size(); ii++) 
                    {
                        CtiCCSubstationBusPtr subby = (CtiCCSubstationBusPtr)modifiedSubsList[ii];
                        CtiCCSubstationPtr station = findSubstationByPAObjectID(subby->getParentId());
                        if (station != NULL)
                        {  
                            modifiedStationsList.push_back(station);
                            updateSubstationObjectList(subby->getParentId(), modifiedStationsList);

                        }
                    }
                    executor = f.createExecutor(new CtiCCSubstationsMsg((CtiCCSubstation_vec&)modifiedStationsList, msgSubsBitMask));
                } 
                executor->Execute();
                delete executor; 

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

void CtiCCSubstationBusStore::updateSubstationObjectList(LONG substationId, CtiMultiMsg_vec &modifiedStationsList)
{  
    CtiMultiMsg_vec::iterator iter = modifiedStationsList.begin();
    CtiCCSubstationPtr station = findSubstationByPAObjectID(substationId);
    if (station != NULL)
    {
        while (iter != modifiedStationsList.end()) 
        {
            if (((CtiCCSubstationPtr)*iter)->getPAOId() == substationId) 
            {  
                modifiedStationsList.erase(iter);
                iter = modifiedStationsList.end();
            }
            else
                iter++;
        }
        modifiedStationsList.push_back(station);
    }

}


void CtiCCSubstationBusStore::addSubBusObjectsToList(list <LONG> *subBusIds, CtiMultiMsg_vec &modifiedSubsList)
{
    list <LONG>::const_iterator busIter = subBusIds->begin();
    while (busIter != subBusIds->end())
    {
    
        CtiCCSubstationBus* tempSub = findSubBusByPAObjectID(*busIter);
        if (tempSub != NULL)
        {
            CtiMultiMsg_vec::iterator iter = modifiedSubsList.begin();
            while (iter != modifiedSubsList.end()) 
            {
                if (((CtiCCSubstationBusPtr)*iter)->getPAOId() == tempSub->getPAOId()) 
                {  
                    modifiedSubsList.erase(iter);
                    iter = modifiedSubsList.end();
                }
                else
                    iter++;
            }
            modifiedSubsList.push_back(tempSub);
    
        }
        busIter++;
    }
    return;
}


void CtiCCSubstationBusStore::addSubstationObjectsToList(list <LONG> *substationIds, CtiMultiMsg_vec &modifiedSubsList)
{
    list <LONG>::const_iterator stationIter = substationIds->begin();

    while (stationIter != substationIds->end())
    {
        CtiCCSubstationPtr station = findSubstationByPAObjectID(*stationIter);
        if (station != NULL)
        {
            list <LONG>::const_iterator busIter = station->getCCSubIds()->begin();
            while (busIter != station->getCCSubIds()->end())
            {
            
                CtiCCSubstationBus* tempSub = findSubBusByPAObjectID(*busIter);
                if (tempSub != NULL)
                {
                    CtiMultiMsg_vec::iterator iter = modifiedSubsList.begin();
                    while (iter != modifiedSubsList.end()) 
                    {
                        if (((CtiCCSubstationBusPtr)*iter)->getPAOId() == tempSub->getPAOId()) 
                        {  
                            modifiedSubsList.erase(iter);
                            iter = modifiedSubsList.end();
                        }
                        else
                            iter++;
                    }
                    modifiedSubsList.push_back(tempSub);
           
                }
                busIter++;
            }
        }
        stationIter++;
    }
    return;
}

void CtiCCSubstationBusStore::sendUserQuit(void *who)
{
    string *strPtr = (string *) who;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << *strPtr << " has asked for shutdown."<< endl;
    }
    //UserQuit = TRUE;
    CtiCCExecutorFactory f;
    CtiCCExecutor* executor = f.createExecutor(new CtiCCShutdown());
    executor->Execute();


}


void CtiCCSubstationBusStore::periodicComplain( void *la )
{
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << CtiTime( ) << " CapControl periodic thread is AWOL" << endl;
   }
}


void CtiCCSubstationBusStore::insertDBReloadList(CC_DBRELOAD_INFO x)
{
    if (x.objecttype == Strategy)
    {
        _reloadList.push_front(x);
    }
    else
    {
        _reloadList.push_back(x);
    }
} 

void CtiCCSubstationBusStore::clearDBReloadList()
{
    if (!_reloadList.empty())
    { 
        _reloadList.clear();
    }
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
        case StrategyIdStrategyMap:
        {
            long strategyId = *first;
            CtiCCStrategy* strategyToInsert = (CtiCCStrategy*) second;
            _strategyid_strategy_map.insert(make_pair(strategyId, strategyToInsert));
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

void CtiCCSubstationBusStore::setRegMask(LONG mask)
{
    _regMask = mask;
    return;
}
LONG CtiCCSubstationBusStore::getRegMask(void)
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
        case StrategyIdStrategyMap:
        {
            long strategyId = first;
            _strategyid_strategy_map.erase(strategyId);
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
map <long, CtiCCSubstationBusPtr>* CtiCCSubstationBusStore::getPAOSubMap()
{
    return &_paobject_subbus_map;
}


void CtiCCSubstationBusStore::setLinkStatusPointId(LONG pointId)
{
    _linkStatusPointId = pointId;
    return;
}
LONG CtiCCSubstationBusStore::getLinkStatusPointId(void)
{
    return _linkStatusPointId;
}

void CtiCCSubstationBusStore::setLinkStatusFlag(BOOL flag)
{
    _linkStatusFlag = flag;
    return;
}
BOOL CtiCCSubstationBusStore::getLinkStatusFlag(void)
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

BOOL CtiCCSubstationBusStore::getVoltReductionSystemDisabled() 
{
    return _voltReductionSystemDisabled;
}

void CtiCCSubstationBusStore::setVoltReductionSystemDisabled(BOOL disableFlag)
{
    _voltReductionSystemDisabled = disableFlag;
    return;
}

LONG CtiCCSubstationBusStore::getVoltDisabledCount() 
{
    return _voltDisabledCount;
}

void CtiCCSubstationBusStore::setVoltDisabledCount(LONG value)
{
    _voltDisabledCount = value;
    return;
}

void CtiCCSubstationBusStore::calculateParentPowerFactor(LONG subBusId)
{
    DOUBLE varTotal = 0;     
    DOUBLE wattTotal = 0;    
    DOUBLE estVarTotal  = 0; 
    DOUBLE pf = 0;
    DOUBLE epf = 0;
    LONG numStations = 0;

    LONG stationId = findSubstationIDbySubBusID(subBusId); 
    if (stationId != NULL)
    {
        CtiCCSubstation* station = findSubstationByPAObjectID(stationId);
        if (station != NULL)
        {
            varTotal = 0;    
            wattTotal = 0;   
            estVarTotal  = 0;
            list <LONG>::const_iterator iter = station->getCCSubIds()->begin();
            while (iter != station->getCCSubIds()->end())
            {   
                LONG busId = *iter;
                CtiCCSubstationBus *bus = findSubBusByPAObjectID(busId);
                if (bus != NULL)
                {
                     varTotal += bus->getCurrentVarLoadPointValue();
                     wattTotal += bus->getCurrentWattLoadPointValue();
                     estVarTotal += bus->getEstimatedVarLoadPointValue();
                }
                iter++;
            }
            station->setPFactor( station->calculatePowerFactor( varTotal, wattTotal));
            station->setEstPFactor( station->calculatePowerFactor( varTotal, wattTotal));
        }

        LONG areaId = findAreaIDbySubstationID(stationId);
        if (areaId != NULL)
        {
            CtiCCArea* area = findAreaByPAObjectID(areaId);
            if (area != NULL)
            {
                pf = 0;
                epf = 0;
                numStations = 0;
                list <LONG>::const_iterator iter = area->getSubStationList()->begin();
                while (iter != area->getSubStationList()->end())
                {   
                    LONG stationId = *iter;
                    CtiCCSubstation *station = findSubstationByPAObjectID(stationId);
                    if (station != NULL)
                    {
                         pf += station->getPFactor();
                         epf += station->getEstPFactor();
                    }
                    iter++;
                    numStations++;
                }
                if (numStations != 0)
                {
                    area->setPFactor( pf/numStations );
                    area->setEstPFactor( epf/numStations );
                }
            }
        }
        LONG spAreaId = findSpecialAreaIDbySubstationID(stationId);
        if (spAreaId != NULL)
        {
            CtiCCSpecial* spArea = findSpecialAreaByPAObjectID(spAreaId);
            if (spArea != NULL)
            {   
                pf = 0;
                epf = 0;
                numStations = 0;
                list <LONG>::const_iterator iter = spArea->getSubstationIds()->begin();
                while (iter != spArea->getSubstationIds()->end())
                {   
                    LONG stationId = *iter;
                    CtiCCSubstation *station = findSubstationByPAObjectID(stationId);
                    if (station != NULL)
                    {
                         pf += station->getPFactor();
                         epf += station->getEstPFactor();
                    }
                    iter++;
                    numStations++;
                }
                if (numStations != 0)
                {
                    spArea->setPFactor( pf/numStations );
                    spArea->setEstPFactor( epf/numStations );
                }
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
    maxKvarMap.insert( make_pair(capbankId, obj ) );

    return true;
}

bool CtiCCSubstationBusStore::removeKVAROperation( long capbankId )
{
    int removed = maxKvarMap.erase(capbankId);

    return ( removed > 0 );
}

/* Relating to Max Kvar Cparm */
long CtiCCSubstationBusStore::isKVARAvailable( long kvarNeeded )
{
    if( _MAX_KVAR < 0 )
        return true;

    long kvarInList = 0;
    for( std::map<long,MaxKvarObject>::iterator itr = maxKvarMap.begin(); itr != maxKvarMap.end();  )
    {
        MaxKvarObject obj = (*itr).second;
        CtiTime now;
        ctitime_t deltaTime = now.seconds() - obj.getTimestamp().seconds();
        if( deltaTime > _MAX_KVAR_TIMEOUT)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Expired kvar request, removing: " << obj.getPaoId() << endl;
            maxKvarMap.erase(itr++);
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
const string CtiCCSubstationBusStore::CAP_CONTROL_DBCHANGE_MSG_SOURCE2 = "CAP_CONTROL_SERVER_FORCED_RELOAD";

