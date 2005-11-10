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

#include <rw/rwfile.h>
#include <rw/thr/thrfunc.h>
#include <rw/collstr.h>
//#include <fstream>

#include "ccsubstationbusstore.h"
#include "ccstrategy.h"
#include "ccsubstationbus.h"
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

extern ULONG _CC_DEBUG;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCSubstationBusStore::CtiCCSubstationBusStore() : _isvalid(FALSE), _reregisterforpoints(TRUE), _reloadfromamfmsystemflag(FALSE), _lastdbreloadtime(RWDBDateTime(1990,1,1,0,0,0,0)), _wassubbusdeletedflag(FALSE)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _ccSubstationBuses = new RWOrdered();
    _ccCapBankStates = new RWOrdered(8);
    _ccGeoAreas = new RWOrdered();
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

    Returns a RWOrdered of CtiCCSubstationBuses
---------------------------------------------------------------------------*/
RWOrdered* CtiCCSubstationBusStore::getCCSubstationBuses(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+30 )
    {//is not valid and has been at 0.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
        reset();
    }
    else
    {
        dumpAllDynamicData();
        checkDBReloadList();
    } 
    
    if( _reloadfromamfmsystemflag )
    {
        checkAMFMSystemForUpdates();
    }

    return _ccSubstationBuses;
}

/*---------------------------------------------------------------------------
    getCCGeoAreas

    Returns a RWOrdered of CtiCCGeoAreas
---------------------------------------------------------------------------*/
RWOrdered* CtiCCSubstationBusStore::getCCGeoAreas(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+30 )
    {//is not valid and has been at 0.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
        reset();
    }

    return _ccGeoAreas;
}

/*---------------------------------------------------------------------------
    getCCCapBankStates

    Returns a RWOrdered of CtiCCStates
---------------------------------------------------------------------------*/
RWOrdered* CtiCCSubstationBusStore::getCCCapBankStates(ULONG secondsFrom1901)
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
CtiCCSubstationBusPtr CtiCCSubstationBusStore::findSubBusByPointID(long point_id)
{
    map< long, CtiCCSubstationBusPtr >::iterator iter = _pointid_subbus_map.find(point_id);
    return (iter == _pointid_subbus_map.end() ? CtiCCSubstationBusPtr() : iter->second);
}

CtiCCFeederPtr CtiCCSubstationBusStore::findFeederByPointID(long point_id)
{
    map< long, CtiCCFeederPtr >::iterator iter = _pointid_feeder_map.find(point_id);
    return (iter == _pointid_feeder_map.end() ? CtiCCFeederPtr() : iter->second);
}

CtiCCCapBankPtr CtiCCSubstationBusStore::findCapBankByPointID(long point_id)
{
    map< long, CtiCCCapBankPtr >::iterator iter = _pointid_capbank_map.find(point_id);
    return (iter == _pointid_capbank_map.end() ? CtiCCCapBankPtr() : iter->second);
}

CtiCCSubstationBusPtr CtiCCSubstationBusStore::findSubBusByPAObjectID(long paobject_id)
{
    map< long, CtiCCSubstationBusPtr >::iterator iter = _paobject_subbus_map.find(paobject_id);
    return (iter == _paobject_subbus_map.end() ? CtiCCSubstationBusPtr() : iter->second);
}

CtiCCFeederPtr CtiCCSubstationBusStore::findFeederByPAObjectID(long paobject_id)
{
    map< long, CtiCCFeederPtr >::iterator iter = _paobject_feeder_map.find(paobject_id);
    return (iter == _paobject_feeder_map.end() ? CtiCCFeederPtr() : iter->second);
}

CtiCCCapBankPtr CtiCCSubstationBusStore::findCapBankByPAObjectID(long paobject_id)
{
    map< long, CtiCCCapBankPtr >::iterator iter = _paobject_capbank_map.find(paobject_id);
    return (iter == _paobject_capbank_map.end() ? CtiCCCapBankPtr() : iter->second);
}


CtiCCStrategyPtr CtiCCSubstationBusStore::findStrategyByStrategyID(long strategy_id)
{
    map< long, CtiCCStrategyPtr >::iterator iter = _strategyid_strategy_map.find(strategy_id);
    return (iter == _strategyid_strategy_map.end() ? CtiCCStrategyPtr() : iter->second);
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






/*---------------------------------------------------------------------------
    dumpAllDynamicData

    Writes out the dynamic information for each of the substation buses.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::dumpAllDynamicData()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Store START dumpAllDynamicData" << endl;
    }*/
    try
    {
        if( _ccSubstationBuses->entries() > 0 )
        {
            RWDBDateTime currentDateTime = RWDBDateTime();
            RWCString dynamicCapControl("dynamicCapControl");
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            conn.beginTransaction(dynamicCapControl);

            for(LONG i=0;i<_ccSubstationBuses->entries();i++)
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
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }

                RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
                if( ccFeeders.entries() > 0 )
                {
                    for(LONG j=0;j<ccFeeders.entries();j++)
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
                                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }

                        RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                        if( ccCapBanks.entries() > 0 )
                        {
                            for(LONG k=0;k<ccCapBanks.entries();k++)
                            {
                                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                                if( currentCapBank->isDirty() )
                                {
                                    /*{
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWDBDateTime().second() << "." << clock() << " -         Store START Cap Bank dumpDynamicData" << endl;
                                    }*/
                                    try
                                    {
                                        currentCapBank->dumpDynamicData(conn,currentDateTime);
                                    }
                                    catch(...)
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            conn.commitTransaction(dynamicCapControl);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Store STOP dumpAllDynamicData" << endl;
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
        RWOrdered tempCCSubstationBuses;

        map< long, CtiCCSubstationBusPtr > temp_paobject_subbus_map;
        map< long, CtiCCFeederPtr > temp_paobject_feeder_map;
        map< long, CtiCCCapBankPtr > temp_paobject_capbank_map;

        map< long, CtiCCSubstationBusPtr > temp_point_subbus_map;
        map< long, CtiCCFeederPtr > temp_point_feeder_map;
        map< long, CtiCCCapBankPtr > temp_point_capbank_map;
        map< long, CtiCCStrategyPtr > temp_strategyid_strategy_map;

        map< long, long > temp_capbank_subbus_map;
        map< long, long > temp_capbank_feeder_map;
        map< long, long > temp_feeder_subbus_map;

        list <long> temp_point_list;



        LONG currentAllocations = ResetBreakAlloc();
        if ( _CC_DEBUG & CC_DEBUG_EXTENDED )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Current Number of Historical Memory Allocations: " << currentAllocations << endl;
        }

        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Obtained connection to the database..." << endl;
                    dout << RWTime() << " - Resetting substation buses from database..." << endl;
                }

                if ( conn.isValid() )
                {   
                    {
                        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

                    
                        if ( _ccSubstationBuses->entries() > 0 )
                        {
                            dumpAllDynamicData();

                            wasAlreadyRunning = true;
                        }
                        if ( _ccCapBankStates->entries() > 0 )
                        {
                            _ccCapBankStates->clearAndDestroy();
                        }
                        if ( _ccGeoAreas->entries() > 0 )
                        {
                            _ccGeoAreas->clearAndDestroy();
                        }
                    }

                    RWDBDateTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable capControlSubstationBusTable = db.table("capcontrolsubstationbus");
                    RWDBTable pointTable = db.table("point");
                    RWDBTable pointUnitTable = db.table("pointunit");
                    RWDBTable dynamicCCSubstationBusTable = db.table("dynamicccsubstationbus");
                    RWDBTable capControlStrategy = db.table("capcontrolstrategy");

                    /************************************************************** 
                    ******  Loading Strategies                               ****** 
                    **************************************************************/ 
                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - DataBase Reload Begin - " << endl;
                    }

                    
                    reloadStrategyFromDataBase(-1, &temp_strategyid_strategy_map);

                    /************************************************************
                    *******  Loading SubBuses                              ******
                    ************************************************************/

                    reloadSubBusFromDatabase(0, &temp_strategyid_strategy_map, &temp_paobject_subbus_map, &temp_point_subbus_map, &tempCCSubstationBuses);

                    /************************************************************
                    *******  Loading Feeders                              *******
                    ************************************************************/

                    
                    RWDBTable ccFeederSubAssignmentTable = db.table("ccfeedersubassignment");
                    RWDBTable capControlFeederTable = db.table("capcontrolfeeder");
                    RWDBTable dynamicCCFeederTable = db.table("dynamicccfeeder");
                    
                    if (tempCCSubstationBuses.entries() > 0)
                    {
                        {   
                            reloadFeederFromDatabase(0, &temp_strategyid_strategy_map, &temp_paobject_feeder_map,
                                                     &temp_paobject_subbus_map, &temp_point_feeder_map, 
                                                     &temp_feeder_subbus_map );
                        }

                       /************************************************************
                        ********    Loading Cap Banks                        ********
                        ************************************************************/
                        {
                            reloadCapBankFromDatabase(0, &temp_paobject_capbank_map, &temp_paobject_feeder_map,
                                                  &temp_point_capbank_map, &temp_capbank_subbus_map,
                                                  &temp_capbank_feeder_map, &temp_feeder_subbus_map );
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - No Substations in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                  /************************************************************
                   ********    Loading Cap Banks States                ********
                   ************************************************************/
                    {
                        reloadCapBankStatesFromDatabase();
                    }
                  /************************************************************
                   ********    Loading Geo Areas                       ********
                   ************************************************************/
                   {
                       reloadGeoAreasFromDatabase();
                   }

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - DataBase Reload End - " << endl;
                    }

                    {
                        
                        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

                        try
                        {   
                            //THIS IS WHERE CAPCONTROL IS BREAKING!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            if (_ccSubstationBuses->entries() > 0)
                            {                                   
                                _ccSubstationBuses->clearAndDestroy();
                            }
                        }
                        catch (...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        
                        if (!_paobject_subbus_map.empty())
                            _paobject_subbus_map.clear();

                        if (!_paobject_feeder_map.empty())
                            _paobject_feeder_map.clear();

                        if (!_paobject_capbank_map.empty())
                            _paobject_capbank_map.clear();

                        if (!_strategyid_strategy_map.empty())
                            _strategyid_strategy_map.clear();
                        
                        if (!_pointid_subbus_map.empty())
                           _pointid_subbus_map.clear();
                        if (!_pointid_feeder_map.empty())
                            _pointid_feeder_map.clear(); 
                        if (!_pointid_capbank_map.empty())
                            _pointid_capbank_map.clear();

                        if (!_feeder_subbus_map.empty())
                            _feeder_subbus_map.clear();
                        if (!_capbank_subbus_map.empty())
                            _capbank_subbus_map.clear();
                        if (!_capbank_feeder_map.empty())
                            _capbank_feeder_map.clear();


                        _paobject_subbus_map = temp_paobject_subbus_map;
                        _paobject_feeder_map = temp_paobject_feeder_map;
                        _paobject_capbank_map = temp_paobject_capbank_map;

                        try
                        {
                            _strategyid_strategy_map = temp_strategyid_strategy_map;
                        }
                        catch (...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        _pointid_subbus_map = temp_point_subbus_map;
                        _pointid_feeder_map = temp_point_feeder_map;
                        _pointid_capbank_map = temp_point_capbank_map;
                        try
                        {
                            _feeder_subbus_map = temp_feeder_subbus_map;
                        }
                        catch (...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        try
                        {
                            _capbank_subbus_map = temp_capbank_subbus_map;
                        }
                        catch (...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        try
                        {
                            _capbank_feeder_map = temp_capbank_feeder_map;
                        }
                        catch (...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                        try
                        {
                            *_ccSubstationBuses = tempCCSubstationBuses;
                        }
                        catch (...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                    

                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Unable to get valid database connection." << endl;
                    _isvalid = FALSE;
                    return;
                }
            }
        }

        _isvalid = TRUE;
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    try
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Store reset." << endl;
        }

        _reregisterforpoints = TRUE;
        _lastdbreloadtime.now();
        if ( !wasAlreadyRunning )
        {
            dumpAllDynamicData();
        }
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Store START sending messages to clients." << endl;
        }
        ULONG msgBitMask = CtiCCSubstationBusMsg::AllSubBusesSent;
        if ( _wassubbusdeletedflag )
        {
            msgBitMask = CtiCCSubstationBusMsg::AllSubBusesSent | CtiCCSubstationBusMsg::SubBusDeleted;
        }
        _wassubbusdeletedflag = false;
        CtiCCExecutorFactory f;
        CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*_ccSubstationBuses,msgBitMask));
        executor->Execute();
        delete executor;
        executor = f.createExecutor(new CtiCCCapBankStatesMsg(*_ccCapBankStates));
        executor->Execute();
        delete executor;
        executor = f.createExecutor(new CtiCCGeoAreasMsg(*_ccGeoAreas));
        executor->Execute();
        delete executor;  
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Store DONE sending messages to clients." << endl;
        }
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        dout << RWTime() << " - Checking AMFM system for updates..." << endl;
    }

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection amfmConn = getConnection(1);
        if ( amfmConn.isValid() && amfmConn.isReady() )
        {
            RWDBDateTime lastAMFMUpdateTime = gInvalidRWDBDateTime;
            RWCString tempStr = DefaultMasterConfigFileName;
            tempStr.remove(tempStr.length()-10);
            RWFile amfmFile((tempStr+"amfm.dat").data());

            if ( amfmFile.Exists() )
            {
                if ( !amfmFile.IsEmpty() )
                {
                    lastAMFMUpdateTime.restoreFrom(amfmFile);
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    //dout << RWTime() << " - After restoreFrom lastAMFMUpdateTime = " << lastAMFMUpdateTime.asString() << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Creating amfm.dat" << endl;
                    //dout << RWTime() << " - Initial saveOn lastAMFMUpdateTime = " << lastAMFMUpdateTime.asString() << endl;
                    amfmFile.Erase();
                    lastAMFMUpdateTime.saveOn(amfmFile);
                    amfmFile.Flush();
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Can not create amfm.dat to store date!!!!!!!!!" << endl;
            }

            RWDBDateTime currentDateTime;
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

            selector.where(dni_capacitorTable["datetimestamp"]>lastAMFMUpdateTime);

            selector.orderBy(dni_capacitorTable["datetimestamp"]);

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << selector.asString() << endl;
            }

            RWDBReader rdr = selector.reader(amfmConn);

            BOOL newAMFMChanges = FALSE;
            while( rdr() )
            {
                handleAMFMChanges(rdr);
                RWDBDateTime datetimestamp;
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
                    //dout << RWTime() << " - Periodic saveOn lastAMFMUpdateTime = " << lastAMFMUpdateTime.asString() << endl;
                    amfmFile.Erase();
                    lastAMFMUpdateTime.saveOn(amfmFile);
                    amfmFile.Flush();
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    //dout << RWTime() << " - Can not create amfm.dat to store date!!!!!!!!!" << endl;
                }

                //sending a signal message to dispatch so that changes from the amfm are in the system log
                RWCString text = RWCString("Import from AMFM system caused database changes");
                RWCString additional = RWCString();
                CtiCapController::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent) );
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to get valid AMFM database connection." << endl;
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

    RWCString       capacitor_id_string;
    LONG            circt_id_normal;
    RWCString       circt_nam_normal                  = m3iAMFMNullString;
    LONG            circt_id_current                  = -1;
    RWCString       circt_name_current                = m3iAMFMNullString;
    RWDBDateTime    switch_datetime                   = gInvalidRWDBDateTime;
    RWCString       owner                             = m3iAMFMNullString;
    RWCString       capacitor_name                    = m3iAMFMNullString;
    RWCString       kvar_rating                       = m3iAMFMNullString;
    RWCString       cap_fs                            = m3iAMFMNullString;
    RWCString       cbc_model                         = m3iAMFMNullString;
    RWCString       serial_no                         = m3iAMFMNullString;
    RWCString       location                          = m3iAMFMNullString;
    RWCString       switching_seq                     = m3iAMFMNullString;
    RWCString       cap_disable_flag                  = m3iAMFMNullString;
    RWCString       cap_disable_type                  = m3iAMFMNullString;
    RWCString       inoperable_bad_order_equipnote    = m3iAMFMNullString;
    RWCString       open_tag_note                     = m3iAMFMNullString;
    RWCString       cap_change_type                   = m3iAMFMNullString;

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
        dout << RWTime() << " - AMFM system update:"
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

RWCString translateCBCModelToControllerType(RWCString& cbc_model)
{
    RWCString returnString = "(none)";
    if( !cbc_model.compareTo("CBC5010", RWCString::ignoreCase) )
    {
        returnString = "CTI Paging";
    }
    else if( !cbc_model.compareTo("CBC3010", RWCString::ignoreCase) )
    {
        returnString = "CTI DLC";
    }
    else if( !cbc_model.compareTo("CBC2010", RWCString::ignoreCase) )
    {
        returnString = "CTI FM";
    }
    /*else if( !cbc_model.compareTo(, RWCString::ignoreCase) )
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
void CtiCCSubstationBusStore::feederReconfigureM3IAMFM( RWCString& capacitor_id_string, LONG circt_id_normal,
                                                        RWCString& circt_nam_normal, LONG circt_id_current,
                                                        RWCString& circt_name_current, RWDBDateTime& switch_datetime,
                                                        RWCString& owner, RWCString& capacitor_name, RWCString& kvar_rating,
                                                        RWCString& cap_fs, RWCString& cbc_model, RWCString& serial_no,
                                                        RWCString& location, RWCString& switching_seq, RWCString& cap_disable_flag,
                                                        RWCString& cap_disable_type, RWCString& inoperable_bad_order_equipnote,
                                                        RWCString& open_tag_note, RWCString& cap_change_type )
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    //LONG capacitor_id = atol(capacitor_id_string);

    BOOL found = FALSE;
    if( _ccSubstationBuses->entries() > 0 )
    {
        for(LONG i=0;i<_ccSubstationBuses->entries();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];

            RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.entries() > 0 )
            {
                for(LONG j=0;j<ccFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];

                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                    if( ccCapBanks.entries() > 0 )
                    {
                        for(LONG k=0;k<ccCapBanks.entries();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                            //LONG capMapId;
                            //if( (capMapId = atol(currentCapBank->getMapLocationId())) == capacitor_id )
                            if( currentCapBank->getMapLocationId() == capacitor_id_string )
                            {
                                LONG capswitchingorder = atol(switching_seq);
                                LONG feedMapId;
                                if( (feedMapId = atol(currentFeeder->getMapLocationId())) != circt_id_current )
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

                                RWCString tempOperationalState = currentCapBank->getOperationalState();
                                tempOperationalState.toUpper();
                                cap_fs.toUpper();
                                cap_disable_flag.toUpper();
                                LONG kvarrating = atol(kvar_rating);
                                bool updateCapBankFlag = false;

                                if( currentCapBank->getBankSize() != kvarrating )
                                {
                                    {
                                        char tempchar[64] = "";
                                        RWCString text = RWCString("M3i Change, Cap Bank Bank Size: ");
                                        text += currentCapBank->getPAOName();
                                        text += ", PAO Id: ";
                                        _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                        text += tempchar;
                                        RWCString additional = RWCString("Was: ");
                                        _ltoa(currentCapBank->getBankSize(),tempchar,10);
                                        additional += tempchar;
                                        additional += ", Now: ";
                                        _ltoa(kvarrating,tempchar,10);
                                        additional += tempchar;
                                        additional += ", on Feeder: ";
                                        additional += currentFeeder->getPAOName();
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent));
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - " << text << ", " << additional << endl;
                                        }
                                    }
                                    currentCapBank->setBankSize(kvarrating);
                                    updateCapBankFlag = true;
                                }
                                if( tempOperationalState != cap_fs )
                                {
                                    RWCString tempFixedOperationalStateString = CtiCCCapBank::FixedOperationalState;
                                    tempFixedOperationalStateString.toUpper();
                                    {
                                        char tempchar[64] = "";
                                        RWCString text = RWCString("M3i Change, Cap Bank Op State: ");
                                        text += currentCapBank->getPAOName();
                                        text += ", PAO Id: ";
                                        _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                        text += tempchar;
                                        RWCString additional = RWCString("Was: ");
                                        additional += currentCapBank->getOperationalState();
                                        additional += ", Now: ";
                                        additional += tempFixedOperationalStateString;
                                        additional += ", on Feeder: ";
                                        additional += currentFeeder->getPAOName();
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent));
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - " << text << ", " << additional << endl;
                                        }
                                    }
                                    currentCapBank->setOperationalState(!cap_fs.compareTo(tempFixedOperationalStateString,RWCString::ignoreCase)?CtiCCCapBank::FixedOperationalState:CtiCCCapBank::SwitchedOperationalState);
                                    updateCapBankFlag = true;
                                }
                                if( (bool)currentCapBank->getDisableFlag() != (!cap_disable_flag.compareTo(m3iAMFMDisabledString,RWCString::ignoreCase)) )
                                {
                                    {
                                        char tempchar[64] = "";
                                        RWCString text = RWCString("M3i Change, Cap Bank Disable Flag: ");
                                        text += currentCapBank->getPAOName();
                                        text += ", PAO Id: ";
                                        _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                        text += tempchar;
                                        RWCString additional = RWCString("Was: ");
                                        additional += (currentCapBank->getDisableFlag()?m3iAMFMDisabledString:m3iAMFMEnabledString);
                                        additional += ", Now: ";
                                        additional += cap_disable_flag;
                                        additional += ", on Feeder: ";
                                        additional += currentFeeder->getPAOName();
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent));
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - " << text << ", " << additional << endl;
                                        }
                                    }
                                    currentCapBank->setDisableFlag(!cap_disable_flag.compareTo(m3iAMFMDisabledString,RWCString::ignoreCase));
                                    updateCapBankFlag = true;
                                }
                                if( !currentCapBank->getControllerType().compareTo(translateCBCModelToControllerType(cbc_model), RWCString::ignoreCase) )
                                {
                                    {
                                        char tempchar[64] = "";
                                        RWCString text = RWCString("M3i Change, Cap Bank Controller Type: ");
                                        text += currentCapBank->getPAOName();
                                        text += ", PAO Id: ";
                                        _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                        text += tempchar;
                                        RWCString additional = RWCString("Was: ");
                                        additional += currentCapBank->getControllerType();
                                        additional += ", Now: ";
                                        additional += translateCBCModelToControllerType(cbc_model);
                                        additional += ", on Feeder: ";
                                        additional += currentFeeder->getPAOName();
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent));
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - " << text << ", " << additional << endl;
                                        }
                                    }
                                    currentCapBank->setControllerType(translateCBCModelToControllerType(cbc_model));
                                    updateCapBankFlag = true;
                                }
                                if( !currentCapBank->getPAODescription().compareTo(location, RWCString::ignoreCase) )
                                {
                                    {
                                        char tempchar[64] = "";
                                        RWCString text = RWCString("M3i Change, Cap Bank Location: ");
                                        text += currentCapBank->getPAOName();
                                        text += ", PAO Id: ";
                                        _ltoa(currentCapBank->getPAOId(),tempchar,10);
                                        text += tempchar;
                                        RWCString additional = RWCString("Was: ");
                                        additional += currentCapBank->getPAODescription();
                                        additional += ", Now: ";
                                        additional += location;
                                        additional += ", on Feeder: ";
                                        additional += currentFeeder->getPAOName();
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent));
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - " << text << ", " << additional << endl;
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
                                        dout << RWTime() << " - M3I change, 'Feeder Reconfigure' or 'AIM Import' PAO Id: "
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
        dout << RWTime() << " - Cap Bank not found MapLocationId: " << capacitor_id_string << " in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    capBankMovedToDifferentFeeder

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capBankMovedToDifferentFeeder(CtiCCFeeder* oldFeeder, CtiCCCapBank* movedCapBank, LONG feederid, LONG capswitchingorder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    RWSortedVector& oldFeederCapBanks = oldFeeder->getCCCapBanks();

    BOOL found = FALSE;
    for(LONG i=0;i<_ccSubstationBuses->entries();i++)
    {
        CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];

        RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
        if( ccFeeders.entries() > 0 )
        {
            for(LONG j=0;j<ccFeeders.entries();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];

                LONG feedMapId;
                if( (feedMapId = atol(currentFeeder->getMapLocationId())) == feederid )
                {
                    oldFeederCapBanks.remove(movedCapBank);

                    RWSortedVector& newFeederCapBanks = currentFeeder->getCCCapBanks();

                    if( newFeederCapBanks.entries() > 0 )
                    {
                        //search through the list to see if there is a cap bank in the
                        //list that already has the switching order
                        if( capswitchingorder >= ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.entries()-1])->getControlOrder() )
                        {
                            movedCapBank->setControlOrder( ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.entries()-1])->getControlOrder() + 1 );
                        }
                        else
                        {
                            for(LONG k=0;k<newFeederCapBanks.entries();k++)
                            {
                                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)newFeederCapBanks[k];
                                if( capswitchingorder == currentCapBank->getControlOrder() )
                                {
                                    //if the new switching order matches a current control
                                    //order, then insert at the end of the cap bank list
                                    movedCapBank->setControlOrder( ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.entries()-1])->getControlOrder() + 1 );
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
                        RWCString text = RWCString("M3i Change, Cap Bank moved feeders: ");
                        text += movedCapBank->getPAOName();
                        text += ", PAO Id: ";
                        _ltoa(movedCapBank->getPAOId(),tempchar,10);
                        text += tempchar;
                        RWCString additional = RWCString("Moved from: ");
                        additional += oldFeeder->getPAOName();
                        additional += ", id: ";
                        _ltoa(oldFeeder->getPAOId(),tempchar,10);
                        additional += tempchar;
                        additional += " To: ";
                        additional += currentFeeder->getPAOName();
                        additional += ", id: ";
                        _ltoa(currentFeeder->getPAOId(),tempchar,10);
                        additional += tempchar;
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent));
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << text << ", " << additional << endl;
                        }
                    }
                    /*{
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - M3I change, 'Feeder Reconfigure' or 'AIM Import' PAO Id: "
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
        }
        if( found )
        {
            break;
        }
    }

    if( !found )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Feeder not found MapLocationId: " << feederid << " in: " << __FILE__ << " at: " << __LINE__ << endl;
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

    currentFeeder->getCCCapBanks().remove(currentCapBank);
    currentCapBank->setControlOrder(capswitchingorder);
    currentFeeder->getCCCapBanks().insert(currentCapBank);
    UpdateFeederBankListInDB(currentFeeder);

    {
        char tempchar[64] = "";
        RWCString text = RWCString("M3i Change, Cap Bank changed order: ");
        text += currentCapBank->getPAOName();
        text += ", PAO Id: ";
        _ltoa(currentCapBank->getPAOId(),tempchar,10);
        text += tempchar;
        RWCString additional = RWCString("Moved from: ");
        _ltoa(oldControlOrder,tempchar,10);
        additional += tempchar;
        additional += ", to: ";
        _ltoa(capswitchingorder,tempchar,10);
        additional += tempchar;
        additional += ", on Feeder: ";
        additional += currentFeeder->getPAOName();
        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent));
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << text << ", " << additional << endl;
        }
    }
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - M3I change, 'Feeder Reconfigure' or 'AIM Import' PAO Id: "
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
void CtiCCSubstationBusStore::capOutOfServiceM3IAMFM(LONG feederid, LONG capid, RWCString& enableddisabled, RWCString& fixedswitched)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    BOOL found = FALSE;
    if( _ccSubstationBuses->entries() > 0 )
    {
        for(LONG i=0;i<_ccSubstationBuses->entries();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];

            RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.entries() > 0 )
            {
                for(LONG j=0;j<ccFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];

                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                    if( ccCapBanks.entries() > 0 )
                    {
                        for(LONG k=0;k<ccCapBanks.entries();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                            LONG capMapId;
                            if( (capMapId = atol(currentCapBank->getMapLocationId())) == capid )
                            {
                                enableddisabled.toUpper();
                                if( (bool)currentCapBank->getDisableFlag() != (!enableddisabled.compareTo(m3iAMFMDisabledString,RWCString::ignoreCase)) )
                                {
                                    currentCapBank->setDisableFlag(!enableddisabled.compareTo(m3iAMFMDisabledString,RWCString::ignoreCase));
                                    UpdateCapBankDisableFlagInDB(currentCapBank);
                                    currentCCSubstationBus->setBusUpdatedFlag(TRUE);
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - M3I change, 'Cap Out of Service' PAO Id: "
                                             << currentCapBank->getPAOId() << ", name: "
                                             << currentCapBank->getPAOName() << ", was "
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
            if( found )
            {
                break;
            }
        }
    }
    if( !found )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Cap Bank not found MapLocationId: " << capid << " in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    feederOutOfServiceM3IAMFM

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::feederOutOfServiceM3IAMFM(LONG feederid, RWCString& fixedswitched, RWCString& enableddisabled)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    BOOL found = FALSE;
    if( _ccSubstationBuses->entries() > 0 )
    {
        for(LONG i=0;i<_ccSubstationBuses->entries();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];

            RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.entries() > 0 )
            {
                for(LONG j=0;j<ccFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];

                    LONG feedMapId;
                    if( (feedMapId = atol(currentFeeder->getMapLocationId())) == feederid )
                    {
                        enableddisabled.toUpper();
                        if( (bool)currentFeeder->getDisableFlag() != (!enableddisabled.compareTo(m3iAMFMDisabledString,RWCString::ignoreCase)) )
                        {
                            currentFeeder->setDisableFlag(!enableddisabled.compareTo(m3iAMFMDisabledString,RWCString::ignoreCase));
                            UpdateFeederDisableFlagInDB(currentFeeder);
                            currentCCSubstationBus->setBusUpdatedFlag(TRUE);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - M3I change, 'Feeder Out of Service' PAO Id: "
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
        dout << RWTime() << " - Feeder not found MapLocationId: " << feederid << " in: " << __FILE__ << " at: " << __LINE__ << endl;
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
    _ccSubstationBuses->clearAndDestroy();
    delete _ccSubstationBuses;
    _ccCapBankStates->clearAndDestroy();
    delete _ccCapBankStates;
    _ccGeoAreas->clearAndDestroy();
    delete _ccGeoAreas;
}

/*---------------------------------------------------------------------------
    doResetThr

    Starts on construction and simply forces a call to reset every 60 minutes
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::doResetThr()
{
    RWCString str;
    char var[128];
    int refreshrate = 3600;

    strcpy(var, "CAP_CONTROL_REFRESH");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        refreshrate = atoi(str.data());
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << refreshrate << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    RWDBDateTime lastPeriodicDatabaseRefresh = RWDBDateTime();

    while(TRUE)
    {
        rwRunnable().serviceCancellation();

        RWDBDateTime currentTime;
        currentTime.now();

        if( currentTime.seconds() >= lastPeriodicDatabaseRefresh.seconds()+refreshrate )
        {
            //if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Periodic restore of substation list from the database" << endl;
            }

            dumpAllDynamicData();
            setValid(FALSE);

            lastPeriodicDatabaseRefresh = RWDBDateTime();
        }
        else
        {
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
    RWCString str;
    char var[128];
    RWCString amfm_interface = "NONE";

    strcpy(var, "CAP_CONTROL_AMFM_INTERFACE");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        amfm_interface = str.data();
        amfm_interface.toUpper();
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << amfm_interface << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    if( !amfm_interface.compareTo(m3iAMFMInterfaceString,RWCString::ignoreCase) )
    {
        int refreshrate = 3600;
        RWCString dbDll = "none";
        RWCString dbName = "none";
        RWCString dbUser = "none";
        RWCString dbPassword = "none";

        strcpy(var, "CAP_CONTROL_AMFM_RELOAD_RATE");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            refreshrate = atoi(str.data());
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << refreshrate << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        strcpy(var, "CAP_CONTROL_AMFM_DB_RWDBDLL");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            dbDll = str.data();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << dbDll << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        strcpy(var, "CAP_CONTROL_AMFM_DB_SQLSERVER");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            dbName = str.data();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << dbName << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        strcpy(var, "CAP_CONTROL_AMFM_DB_USERNAME");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            dbUser = str.data();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << dbUser << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        strcpy(var, "CAP_CONTROL_AMFM_DB_PASSWORD");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            dbPassword = str.data();
            if( _CC_DEBUG & CC_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << dbPassword << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }


        if( dbDll != "none" && dbName != "none" && dbUser != "none" && dbPassword != "none" )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Obtaining connection to the AMFM database..." << endl;
            }
            setDatabaseParams(1,dbDll,dbName,dbUser,dbPassword);

            time_t start = time(NULL);

            RWDBDateTime currenttime = RWDBDateTime();
            ULONG tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+(2*refreshrate);
            RWDBDateTime nextAMFMRefresh = RWDBDateTime(RWTime(tempsum));

            while(TRUE)
            {
                rwRunnable().serviceCancellation();

                if ( RWDBDateTime() >= nextAMFMRefresh )
                {
                    if( _CC_DEBUG & CC_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Setting AMFM reload flag." << endl;
                    }

                    dumpAllDynamicData();
                    setReloadFromAMFMSystemFlag(TRUE);

                    currenttime = RWDBDateTime();
                    tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+refreshrate;
                    nextAMFMRefresh = RWDBDateTime(RWTime(tempsum));
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
            dout << RWTime() << " - Can't find AMFM DB setting in master.cfg!!!" << endl;
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

    for(int i=0;i<_ccSubstationBuses->entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)((*_ccSubstationBuses)[i]);

        LONG numberOfFeedersRecentlyControlled = 0;
        LONG numberOfCapBanksPending = 0;

        if( currentSubstationBus->getRecentlyControlledFlag() )
        {
            RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

            for(int j=0;j<ccFeeders.entries();j++)
            {
                numberOfCapBanksPending = 0;
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(int k=0;k<ccCapBanks.entries();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                        if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                            currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                        {
                            numberOfCapBanksPending++;
                        }
                    }

                    if( numberOfCapBanksPending == 0 )
                    {
                        currentFeeder->setRecentlyControlledFlag(FALSE);
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Feeder: " << currentFeeder->getPAOName() << ", no longer recently controlled because no banks were pending in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                    else if( numberOfCapBanksPending > 1 )
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Multiple cap banks pending in Feeder: " << currentFeeder->getPAOName() << ", setting status to questionable in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        for(int k=0;k<ccCapBanks.entries();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending && currentCapBank->getStatusPointId() > 0)
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Setting status to close questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType));
                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && currentCapBank->getStatusPointId() > 0)
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Setting status to open questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType));
                            }
                        }
                    }
                    else
                    {//normal pending feeder
                        numberOfFeedersRecentlyControlled++;
                    }
                }
                else
                {
                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(int k=0;k<ccCapBanks.entries();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                        if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending && currentCapBank->getStatusPointId() > 0)
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName() << " questionable because feeder is not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType));
                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && currentCapBank->getStatusPointId() > 0)
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName() << " questionable because feeder is not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType));
                        }
                    }
                }
            }

            if( numberOfFeedersRecentlyControlled == 0 )
            {
                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
            }
        }
        else//sub bus not recently controlled
        {
            RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

            for(int j=0;j<ccFeeders.entries();j++)
            {
                numberOfCapBanksPending = 0;
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];

                if( currentFeeder->getRecentlyControlledFlag() )
                {
                    currentFeeder->setRecentlyControlledFlag(FALSE);
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Feeder: " << currentFeeder->getPAOName() << ", no longer recently controlled because sub bus not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                }

                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(int k=0;k<ccCapBanks.entries();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                    if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending && !(currentCapBank->getVerificationFlag() && currentCapBank->getPerformingVerificationFlag()) && currentCapBank->getStatusPointId() > 0)
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Setting status to close questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType));
                    }
                    else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending && !(currentCapBank->getVerificationFlag() && currentCapBank->getPerformingVerificationFlag()) && currentCapBank->getStatusPointId() > 0)
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Setting status to open questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::OpenQuestionable,NormalQuality,StatusPointType));
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

    for(int i=0;i<_ccSubstationBuses->entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)((*_ccSubstationBuses)[i]);
        {
            char tempchar[64] = "";
            RWCString text = RWCString("Daily Operations were ");
            _ltoa(currentSubstationBus->getCurrentDailyOperations(),tempchar,10);
            text += tempchar;
            RWCString additional = RWCString("Sub Bus: ");
            additional += currentSubstationBus->getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent));
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << text << ", " << additional << endl;
            }
        }
        currentSubstationBus->setCurrentDailyOperations(0);

        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(int j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            /*{
                char tempchar[64] = "";
                RWCString text = RWCString("Daily Operations were ");
                _ltoa(currentFeeder->getCurrentDailyOperations(),tempchar,10);
                text += tempchar;
                RWCString additional = RWCString("Feeder: ");
                additional += currentFeeder->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
            }*/
            currentFeeder->setCurrentDailyOperations(0);

            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            //**********************************************************************
            //The operation count on a cap bank is actually a total not a daily, doh
            //**********************************************************************
            for(int k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                /*{
                    char tempchar[64] = "";
                    RWCString text = RWCString("Daily Operations were ");
                    _ltoa(currentCapBank->getCurrentDailyOperations(),tempchar,10);
                    text += tempchar;
                    RWCString additional = RWCString("Cap Bank: ");
                    additional += currentCapBank->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
                }*/
                currentCapBank->setCurrentDailyOperations(0);
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

        RWDBTable dynamicSubBusTable = getDatabase().table("dynamicccsubstationbus");
        RWDBUpdater updater = dynamicSubBusTable.updater();

        updater.where( dynamicSubBusTable["paobjectid"] == bus->getPAOId() );

        updater << dynamicSubBusTable["verificationflag"].assign( RWCString((bus->getVerificationFlag()?'Y':'N')) );

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(bus->getPAOId(), ChangePAODb,
                                                      bus->getPAOCategory(), bus->getPAOType(),
                                                      ChangeTypeUpdate);
        dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

        return updater.status().isValid();
    }
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

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == bus->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( RWCString((bus->getDisableFlag()?'Y':'N')) );

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(bus->getPAOId(), ChangePAODb,
                                                      bus->getPAOCategory(), bus->getPAOType(),
                                                      ChangeTypeUpdate);
        dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

        return updater.status().isValid();
    }
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

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == feeder->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( RWCString((feeder->getDisableFlag()?'Y':'N')) );

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(feeder->getPAOId(), ChangePAODb,
                                                      feeder->getPAOCategory(), feeder->getPAOType(),
                                                      ChangeTypeUpdate);
        dbChange->setSource(CAP_CONTROL_DBCHANGE_MSG_SOURCE);
        CtiCapController::getInstance()->sendMessageToDispatch(dbChange);

        return updater.status().isValid();
    }
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

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == capbank->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( RWCString((capbank->getDisableFlag()?'Y':'N')) );

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

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == capbank->getPAOId() );

        updater << yukonPAObjectTable["paoname"].assign( capbank->getPAOName() )
                << yukonPAObjectTable["disableflag"].assign( RWCString((capbank->getDisableFlag()?'Y':'N')) )
                << yukonPAObjectTable["description"].assign( capbank->getPAODescription() );

        updater.execute( conn );

        RWDBTable capBankTable = getDatabase().table("capbank");
        updater = capBankTable.updater();

        updater.where( capBankTable["deviceid"] == capbank->getPAOId() );

        updater << capBankTable["banksize"].assign( capbank->getBankSize() )
                << capBankTable["operationalstate"].assign( capbank->getOperationalState() );

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

        RWDBTable ccFeederBankListTable = getDatabase().table("ccfeederbanklist");
        RWDBDeleter deleter = ccFeederBankListTable.deleter();

        deleter.where( ccFeederBankListTable["feederid"] == feeder->getPAOId() );

        deleter.execute( conn );


        RWDBInserter inserter = ccFeederBankListTable.inserter();

        RWOrdered& ccCapBanks = feeder->getCCCapBanks();
        for(LONG i=0;i<ccCapBanks.entries();i++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[i];

            inserter << feeder->getPAOId()
                     << currentCapBank->getPAOId()
                     << currentCapBank->getControlOrder();

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
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                
                RWDBDateTime currentDateTime;
                RWDBDatabase db = getDatabase();
                RWDBTable capControlStrategy = db.table("capcontrolstrategy");

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
                << capControlStrategy["offpklead"] ;


                selector.from(capControlStrategy);
                if (strategyId >= 0)
                    selector.where(capControlStrategy["strategyid"]==strategyId );


                if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << selector.asString().data() << endl;
                }
                RWDBReader rdr = selector.reader(conn);

                CtiCCStrategyPtr currentCCStrategy;


                while ( rdr() )
                {                
                    currentCCStrategy = CtiCCStrategyPtr(new CtiCCStrategy(rdr));
                    strategy_map->insert(make_pair(currentCCStrategy->getStrategyId(),currentCCStrategy));
                }


                if (strategyId > 0)
                {                
                    //Update SubstationBus Strategy Values with dbChanged/editted strategy values
                    RWDBTable capControlSubstationBus = db.table("capcontrolsubstationbus");
                    selector = db.selector();
                    selector << capControlSubstationBus["substationbusid"]
                    << capControlSubstationBus["strategyid"];

                    selector.from(capControlSubstationBus);
                    selector.where(capControlSubstationBus["substationbusid"] == strategyId );


                    CtiCCSubstationBusPtr currentCCSubstationBus;
                    long currentCCSubstationBusId;
                    while ( rdr() )
                    {
                        rdr["substationbusid"] >> currentCCSubstationBusId;
                        currentCCSubstationBus = findSubBusByPAObjectID(currentCCSubstationBusId);
                        if (currentCCSubstationBus != NULL)
                        {
                            currentCCSubstationBus->setStrategyValues(currentCCStrategy);
                        }
                    }

                    //Update Feeder Strategy Values with dbChanged/editted strategy values
                    RWDBTable capControlFeeder = db.table("capcontrolfeeder");
                    selector = db.selector();
                    selector << capControlFeeder["feederid"]
                    << capControlFeeder["strategyid"];

                    selector.from(capControlFeeder);
                    selector.where(capControlFeeder["feederid"] == strategyId );


                    CtiCCFeederPtr currentCCFeeder;
                    long currentCCFeederId;
                    while ( rdr() )
                    {
                        rdr["feederid"] >> currentCCFeederId;
                        currentCCFeeder = findFeederByPAObjectID(currentCCFeederId);
                        if (currentCCFeeder != NULL)
                        {
                            currentCCFeeder->setStrategyValues(currentCCStrategy);
                        }
                    }
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}
/*---------------------------------------------------------------------------
    reloadSubBusFromDB

    Reloads a single subbus from the database.  
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reloadSubBusFromDatabase(long subBusId, map< long, CtiCCStrategyPtr > *strategy_map, 
                                                       map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                                       map< long, CtiCCSubstationBusPtr > *pointid_subbus_map, 
                                                       RWOrdered *cCSubstationBuses )
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

                    RWDBDateTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable capControlSubstationBusTable = db.table("capcontrolsubstationbus");
                    RWDBTable pointTable = db.table("point");
                    RWDBTable pointUnitTable = db.table("pointunit");
                    RWDBTable dynamicCCSubstationBusTable = db.table("dynamicccsubstationbus");
                    RWDBTable ccFeederSubAssignmentTable = db.table("ccfeedersubassignment");

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
                        << capControlSubstationBusTable["strategyid"]
                        << capControlSubstationBusTable["currentvoltloadpointid"];

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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);

                        CtiCCSubstationBusPtr  currentCCSubstationBus;
                        CtiCCStrategyPtr currentCCStrategy = NULL;
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

                            if (subBusId > 0)
                            {               
                                currentCCStrategy = findStrategyByStrategyID(currentCCSubstationBus->getStrategyId());
                                if (currentCCStrategy == NULL)
                                {
                                    currentCCSubstationBus->setStrategyId(0);
                                    currentCCStrategy = findStrategyByStrategyID(0);
                                }
                                currentCCSubstationBus->setStrategyValues(currentCCStrategy);
                            }
                            else
                            {
                               if (strategy_map->find(currentCCSubstationBus->getStrategyId()) == strategy_map->end())
                               {
                                   currentCCSubstationBus->setStrategyId(0);
                               }
                               currentCCStrategy = strategy_map->find(currentCCSubstationBus->getStrategyId())->second;
                               currentCCSubstationBus->setStrategyValues(currentCCStrategy);
                            }

                            //if (currentCCSubstationBus->getStrategyId() > 0)
                                cCSubstationBuses->insert(currentCCSubstationBus);

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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
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
                                currentCCSubstationBus->setParentId(0);
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
                        << dynamicCCSubstationBusTable["currentvoltpointvalue"];

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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
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
                                RWCString tempPointType = "(none)";
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
                                        dout << RWTime() << " - Undefined Substation Bus point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                    }
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Undefined Substation Bus point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
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

            _reregisterforpoints = TRUE;
        
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCSubstationBusStore::reloadFeederFromDatabase(long feederId, map< long, CtiCCStrategyPtr > *strategy_map, 
                                                       map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                                       map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                                       map< long, CtiCCFeederPtr > *pointid_feeder_map, 
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
                    RWDBDateTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable pointTable = db.table("point");
                    RWDBTable pointUnitTable = db.table("pointunit");
                    RWDBTable ccFeederSubAssignmentTable = db.table("ccfeedersubassignment");
                    RWDBTable ccFeederBankListTable = db.table("ccfeederbanklist");
                    RWDBTable capControlFeederTable = db.table("capcontrolfeeder");
                    RWDBTable dynamicCCFeederTable = db.table("dynamicccfeeder");

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
                        << capControlFeederTable["strategyid"]
                        << capControlFeederTable["currentvoltloadpointid"];

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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
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

                            if (feederId > 0)
                            {               
                                currentCCStrategy = findStrategyByStrategyID(currentCCFeeder->getStrategyId());
                                if (currentCCStrategy == NULL)
                                {
                                    currentCCFeeder->setStrategyId(0);
                                    currentCCStrategy = findStrategyByStrategyID(0);
                                }
                                currentCCFeeder->setStrategyValues(currentCCStrategy);
                            }
                            else
                            {
                                if (strategy_map->find(currentCCFeeder->getStrategyId()) == strategy_map->end())
                                {
                                    currentCCFeeder->setStrategyId(0);
                                }
                                currentCCStrategy = strategy_map->find(currentCCFeeder->getStrategyId())->second;
                                currentCCFeeder->setStrategyValues(currentCCStrategy);
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
                        dout << RWTime() << " - " << selector.asString().data() << endl;
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

                        if (currentCCFeeder->getStrategyId() == 0 && currentCCSubstationBus != NULL)
                        {

                            CtiCCStrategyPtr newStrategy = NULL;
                            if (feederId > 0)
                                newStrategy = findStrategyByStrategyID(currentCCSubstationBus->getStrategyId());
                            else
                                newStrategy =  strategy_map->find(currentCCSubstationBus->getStrategyId())->second;

                            if (newStrategy != NULL)
                            {                      
                                currentCCFeeder->setStrategyValues(newStrategy);
                                currentCCFeeder->setStrategyId(0);
                                currentCCFeeder->setStrategyName("(none)");
                            }
                        }

                        RWOrdered& tempFeeders = currentCCSubstationBus->getCCFeeders();
                        CtiCCFeederPtr tempFeeder;
                        int insertPoint = tempFeeders.entries();
                        int j = insertPoint;

                        while (j > 0)
                        {
                            tempFeeder = (CtiCCFeeder*)tempFeeders[j-1];
                            if (displayOrder <= tempFeeder->getDisplayOrder())
                            {
                                insertPoint =  j - 1;
                            }

                            j--;
                        }

                        currentCCSubstationBus->getCCFeeders().insertAt( insertPoint,currentCCFeeder );
                        feeder_subbus_map->insert(make_pair(currentFeederId, currentSubBusId));
                    }
                }

                if (feederId > 0)
                {    
                    RWDBSelector selector = db.selector();
                    
                    selector << ccFeederBankListTable["deviceid"]
                          << ccFeederBankListTable["feederid"]
                          << ccFeederBankListTable["controlorder"];


                    selector.from(ccFeederBankListTable);
                    selector.where(ccFeederBankListTable["feederid"]== feederId );

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    RWDBNullIndicator isNull;
                    long capbankid, feedid, controlorder;
                    
                    while ( rdr() )
                    {
                        rdr["deviceid"] >> capbankid;
                        rdr["feederid"] >> feedid;
                        rdr["controlorder"] >> controlorder;
                        reloadCapBankFromDatabase(capbankid, &_paobject_capbank_map, &_paobject_feeder_map,
                                                  &_pointid_capbank_map, &_capbank_subbus_map,
                                                  &_capbank_feeder_map, &_feeder_subbus_map );
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
                        dout << RWTime() << " - " << selector.asString().data() << endl;
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
                    << dynamicCCFeederTable["currentvoltpointvalue"];

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
                        dout << RWTime() << " - " << selector.asString().data() << endl;
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
                        dout << RWTime() << " - " << selector.asString().data() << endl;
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
                                RWCString tempPointType = "(none)";
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
                                        dout << RWTime() << " - Undefined Feeder point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                    }
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Undefined Feeder point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                        }
                    }
                }

            }
            
        }

        _reregisterforpoints = TRUE;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCSubstationBusStore::reloadCapBankFromDatabase(long capBankId, map< long, CtiCCCapBankPtr > *paobject_capbank_map,
                                                        map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                                        map< long, CtiCCCapBankPtr > *pointid_capbank_map,
                                                       map< long, long> *capbank_subbus_map,
                                                       map< long, long> *capbank_feeder_map,
                                                       map< long, long> *feeder_subbus_map )
{
    CtiCCCapBankPtr capBankToUpdate = NULL;
    if (capBankId > 0)
        capBankToUpdate = findCapBankByPAObjectID(capBankId);
    
    try
    {
        if (capBankToUpdate != NULL)
        {
            deleteCapBank(capBankToUpdate->getPAOId());
        }
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {

                if ( conn.isValid() )
                {   

                    RWDBDateTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable pointTable = db.table("point");
                    RWDBTable deviceTable = db.table("device");
                    RWDBTable capBankTable = db.table("capbank");
                    RWDBTable dynamicCCCapBankTable = db.table("dynamiccccapbank");
                    RWDBTable ccFeederBankListTable = db.table("ccfeederbanklist");

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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            long deviceid;
                            RWCString tempBoolString;

                            rdr["deviceid"] >> deviceid;
                            CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(deviceid)->second;

                            rdr["alarminhibit"] >> tempBoolString;
                            tempBoolString.toLower();
                            currentCCCapBank->setAlarmInhibitFlag(tempBoolString=="y"?TRUE:FALSE);

                            rdr["controlinhibit"] >> tempBoolString;
                            tempBoolString.toLower();
                            currentCCCapBank->setControlInhibitFlag(tempBoolString=="y"?TRUE:FALSE);
                        }
                    }


                    {
                        RWDBSelector selector = db.selector();
                        selector << capBankTable["deviceid"]
                                 <<  capBankTable["controldeviceid"]
                                 <<  yukonPAObjectTable["type"]
                                 <<  yukonPAObjectTable["paobjectid"];

                        selector.from(capBankTable);
                        selector.from(yukonPAObjectTable);

                        if (capBankId > 0)
                        {                
                            selector.where(yukonPAObjectTable["paobjectid"]==capBankTable["controldeviceid"] &&
                                           yukonPAObjectTable["paobjectid"] == capBankId );
                        }
                        else
                            selector.where(yukonPAObjectTable["paobjectid"]==capBankTable["controldeviceid"] );


                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            long capBankId;
                            long controlDeviceId;
                            RWCString controlDeviceType;

                            rdr["deviceid"] >> capBankId;
                            CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(capBankId)->second;

                            rdr["controldeviceid"] >> controlDeviceId;

                            if (currentCCCapBank->getControlDeviceId() == controlDeviceId)
                            {
                                rdr["type"] >> controlDeviceType;
                                currentCCCapBank->setControlDeviceType(controlDeviceType);

                            }
                        }
                    }


                    {
                        RWDBSelector selector = db.selector();
                        selector << ccFeederBankListTable["deviceid"]
                              << ccFeederBankListTable["feederid"]
                              << ccFeederBankListTable["controlorder"];


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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        long subbusid;
                        long tempFeederId;

                        while ( rdr() )
                        {
                            long deviceid;
                            long feederid;
                            long controlOrder;

                            rdr["deviceid"] >> deviceid;
                            rdr["feederid"] >> feederid;
                            rdr["controlorder"] >> controlOrder;

                            CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(deviceid)->second;
                            currentCCCapBank->setControlOrder(controlOrder);
                            currentCCCapBank->setParentId(feederid);
                            CtiCCFeederPtr currentCCFeeder = paobject_feeder_map->find(feederid)->second;

                            //DON'T ADD !... Supposed to be !=UninstalledState...
                            if (currentCCCapBank->getOperationalState().compareTo(CtiCCCapBank::UninstalledState, RWCString::ignoreCase))
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
                    << dynamicCCCapBankTable["currentdailyoperations"];


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
                        dout << RWTime() << " - " << selector.asString().data() << endl;
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
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    RWDBNullIndicator isNull;
                    while ( rdr() )
                    {
                        long currentCCCapBankId;
                        rdr["paobjectid"] >> currentCCCapBankId;

                        CtiCCCapBankPtr currentCCCapBank = paobject_capbank_map->find(currentCCCapBankId)->second;
                        LONG tempPAObjectId = 0;
                        rdr["paobjectid"] >> tempPAObjectId;
                        if (// currentCCCapBank != NULL &&
                            tempPAObjectId == currentCCCapBank->getPAOId() )
                        {
                            rdr["pointid"] >> isNull;
                            if ( !isNull )
                            {
                                LONG tempPointId = -1000;
                                LONG tempPointOffset = -1000;
                                RWCString tempPointType = "(none)";
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
                                        dout << RWTime() << " - Undefined Cap Bank point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                    }
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Undefined Cap Bank point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                            }
                        }
                    }
                }
            }
        }

        _reregisterforpoints = TRUE;

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                    if ( _ccCapBankStates->entries() > 0 )
                    {
                        _ccCapBankStates->clearAndDestroy();
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
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);

                    while ( rdr() )
                    {
                        CtiCCState* ccState = new CtiCCState(rdr);
                        _ccCapBankStates->insert( ccState );
                    }
                }
            }
        
        }

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}
void CtiCCSubstationBusStore::reloadGeoAreasFromDatabase()
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
                    if ( _ccGeoAreas->entries() > 0 )
                    {
                        _ccGeoAreas->clearAndDestroy();
                    }


                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable capControlSubstationBusTable = db.table("capcontrolsubstationbus");
                    RWDBSelector selector = db.selector();
                    selector.distinct();
                    selector << yukonPAObjectTable["description"];

                    selector.from(yukonPAObjectTable);
                    selector.from(capControlSubstationBusTable);

                    selector.where(yukonPAObjectTable["paobjectid"]==capControlSubstationBusTable["substationbusid"]);

                    selector.orderBy(yukonPAObjectTable["description"]);

                    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);

                    while ( rdr() )
                    {
                        RWCollectableString* areaString = NULL;
                        RWCString tempStr;
                        rdr["description"] >> tempStr;
                        areaString = new RWCollectableString(tempStr);
                        _ccGeoAreas->insert( areaString );
                    }
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCSubstationBusStore::deleteSubBus(long subBusId)
{
    CtiCCSubstationBusPtr subToDelete = findSubBusByPAObjectID(subBusId);

    try
    {
        if (subToDelete != NULL)
        {
            //subToDelete->dumpDynamicData();

            RWOrdered& ccFeeders = subToDelete->getCCFeeders();
            long feedCount = ccFeeders.entries();
            for(LONG i=0;i<feedCount;i++)
            {
                CtiCCFeederPtr feederToDelete = (CtiCCFeeder*)ccFeeders.first();
                RWOrdered& ccCapBanks = feederToDelete->getCCCapBanks();
                long capCount = ccCapBanks.entries();
                for (LONG j = 0; j < capCount; j++)
                {
                    CtiCCCapBankPtr capBankToDelete = (CtiCCCapBank*)ccCapBanks.first();

                    deleteCapBank(capBankToDelete->getPAOId());
                }
                deleteFeeder(feederToDelete->getPAOId());
            }
            
            list <LONG> *pointIds  = subToDelete->getPointIds();
            while (!pointIds->empty())
            {
                LONG pointid = pointIds->front();
                pointIds->pop_front();
                _pointid_subbus_map.erase(pointid);
            }
            try
            {
                RWCString subBusName = subToDelete->getPAOName();
                _paobject_subbus_map.erase(subToDelete->getPAOId());
                //_ccSubstationBuses->removeAndDestroy(subToDelete);
                for (LONG j = 0; j < _ccSubstationBuses->entries(); j++)
                {
                    CtiCCSubstationBus *subBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[j];
                    if (subBus->getPAOId() == subBusId)
                    {
                        _ccSubstationBuses->removeAt(j);
                        break;
                    }

                }

                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << "SUBBUS: " << subBusName <<" has been deleted." << endl;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

void CtiCCSubstationBusStore::deleteFeeder(long feederId)
{
    CtiCCFeederPtr feederToDelete = findFeederByPAObjectID(feederId);

    try
    {
        if (feederToDelete != NULL)
        {
            //feederToDelete->dumpDynamicData();

            RWOrdered &ccCapBanks = feederToDelete->getCCCapBanks();
            long capCount = ccCapBanks.entries();
            for (LONG j = 0; j < capCount; j++)
            {
                CtiCCCapBankPtr capBankToDelete = (CtiCCCapBank*)ccCapBanks.first();

                deleteCapBank(capBankToDelete->getPAOId());
            }


            list <LONG> *pointIds  = feederToDelete->getPointIds();
            while (!pointIds->empty())
            {
                LONG pointid = pointIds->front();
                pointIds->pop_front();
                _pointid_feeder_map.erase(pointid);
            }
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

                RWCString feederName = feederToDelete->getPAOName();
                _paobject_feeder_map.erase(feederToDelete->getPAOId());
                _feeder_subbus_map.erase(feederToDelete->getPAOId());

                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << "FEEDER: " << feederName <<" has been deleted." << endl;
                }

            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
            while (!pointIds->empty())
            {
                LONG pointid = pointIds->front();
                pointIds->pop_front();
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

                RWCString capBankName = capBankToDelete->getPAOName();
                _paobject_capbank_map.erase(capBankToDelete->getPAOId());
                _capbank_subbus_map.erase(capBankToDelete->getPAOId());
                _capbank_feeder_map.erase(capBankToDelete->getPAOId());
                
                capBankToDelete = findCapBankByPAObjectID(capBankId);
                if (capBankToDelete != NULL)
                {   
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << "What the $#@%!." << endl;
                    capBankToDelete = NULL;
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << "CAPBANK: " << capBankName <<" has been deleted." << endl;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCSubstationBusStore::checkDBReloadList()
{
    BOOL sendBusInfo = false;
    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
        {

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
                            //reloadCapBankFromDatabase(reloadTemp.objectId);
                            reloadCapBankFromDatabase(reloadTemp.objectId, &_paobject_capbank_map, &_paobject_feeder_map,
                                                      &_pointid_capbank_map, &_capbank_subbus_map, &_capbank_feeder_map, 
                                                      &_feeder_subbus_map );
                        }
                        else if (reloadTemp.action == ChangeTypeDelete)
                        {
                            deleteCapBank(reloadTemp.objectId);
                        }
                        break;
                    }
                    //feeder                           
                    case Feeder:
                    {
                        if (reloadTemp.action == ChangeTypeUpdate)
                        {
                            //reloadFeederFromDatabase(reloadTemp.objectId);
                             reloadFeederFromDatabase(reloadTemp.objectId, &_strategyid_strategy_map, &_paobject_feeder_map,
                                                     &_paobject_subbus_map, &_pointid_feeder_map, &_feeder_subbus_map );
                        }
                        else if (reloadTemp.action == ChangeTypeDelete)
                        {
                            deleteFeeder(reloadTemp.objectId);
                         
                        }
                        break;
                    }
                    //subbus
                    case SubBus:
                    {
                        if (reloadTemp.action == ChangeTypeDelete)
                        {
                            deleteSubBus(reloadTemp.objectId); 

                            CtiCCExecutorFactory f;
                            CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::DELETE_ITEM, reloadTemp.objectId));
                            executor->Execute();
                            delete executor;

                        }
                        else  // ChangeTypeAdd, ChangeTypeUpdate
                        {
                            reloadSubBusFromDatabase(reloadTemp.objectId, &_strategyid_strategy_map, &_paobject_subbus_map, &_pointid_subbus_map, _ccSubstationBuses);
                            
                        }
                        break;
                    }
                case Strategy:
                    {
                        if (reloadTemp.action != ChangeTypeDelete)
                        {
                            reloadStrategyFromDataBase(reloadTemp.objectId, &_strategyid_strategy_map);
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

                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " DBChange " << endl;
                }  
                sendBusInfo = TRUE;
                _reloadList.pop_front();
            }

            if (sendBusInfo)
            {              
                ULONG msgBitMask = CtiCCSubstationBusMsg::AllSubBusesSent;
                if ( _wassubbusdeletedflag )
                {
                    msgBitMask = CtiCCSubstationBusMsg::AllSubBusesSent | CtiCCSubstationBusMsg::SubBusDeleted;
                }
                CtiCCExecutorFactory f;
                CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*_ccSubstationBuses,msgBitMask));
                executor->Execute();
                delete executor;
                executor = f.createExecutor(new CtiCCCapBankStatesMsg(*_ccCapBankStates));
                executor->Execute();
                delete executor;
                executor = f.createExecutor(new CtiCCGeoAreasMsg(*_ccGeoAreas));
                executor->Execute();
                delete executor; 

                _wassubbusdeletedflag = false;


            }

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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


/* Private Static members */
const RWCString CtiCCSubstationBusStore::m3iAMFMInterfaceString = "M3I";

const RWCString CtiCCSubstationBusStore::m3iAMFMChangeTypeCircuitOutOfService = "Circuit or Section Out of Service";
const RWCString CtiCCSubstationBusStore::m3iAMFMChangeTypeCircuitReturnToService = "Circuit or Section Returned to Service";
const RWCString CtiCCSubstationBusStore::m3iAMFMChangeTypeCapOutOfService = "Cap Control Out of Service";
const RWCString CtiCCSubstationBusStore::m3iAMFMChangeTypeCapReturnedToService = "Cap Control Returned to Service";
const RWCString CtiCCSubstationBusStore::m3iAMFMChangeTypeCircuitReconfigured = "Circuit Reconfigured";
const RWCString CtiCCSubstationBusStore::m3iAMFMChangeTypeCircuitReconfiguredToNormal = "Circuit Reconfigured to Normal";

const RWCString CtiCCSubstationBusStore::m3iAMFMEnabledString = "ENABLED";
const RWCString CtiCCSubstationBusStore::m3iAMFMDisabledString = "DISABLED";
const RWCString CtiCCSubstationBusStore::m3iAMFMFixedString = "FIXED";
const RWCString CtiCCSubstationBusStore::m3iAMFMSwitchedString = "SWITCHED";

const RWCString CtiCCSubstationBusStore::m3iAMFMNullString = "(NULL)";

const RWCString CtiCCSubstationBusStore::CAP_CONTROL_DBCHANGE_MSG_SOURCE = "CAP_CONTROL_SERVER";

