/*---------------------------------------------------------------------------
        Filename:  strategystore.cpp
        
        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiCCStrategyStore
                        CtiCCStrategyStore maintains a pool of 
                        CtiCCStrategies.
                        
        Initial Date:  8/18/2000
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2000
---------------------------------------------------------------------------*/
#include <rw/thr/thrfunc.h>

#include "strategylist.h"
#include "strategystore.h"
#include "dbaccess.h"
#include "ctibase.h"
#include "logger.h"
#include "configparms.h"

#include <rw/collstr.h>

extern BOOL _CAP_DEBUG;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCStrategyStore::CtiCCStrategyStore() : _doreset(TRUE), _isvalid(FALSE), _reregisterforpoints(TRUE)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _strategyList = new CtiCCStrategyList();
    _stateList = new RWOrdered(8);
    _areaList = new RWOrdered();
    //Start the reset thread
    RWThreadFunction func = rwMakeThreadFunction( *this, &CtiCCStrategyStore::doResetThr );
    _resetthr = func;
    func.start();
}

/*--------------------------------------------------------------------------
    Destrutor
-----------------------------------------------------------------------------*/
CtiCCStrategyStore::~CtiCCStrategyStore()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _resetthr.isValid() )
    {
        _resetthr.requestCancellation();
    }

    shutdown();
}

/*---------------------------------------------------------------------------
    Strategies
    
    Returns a list of the CtiCCStrategies
---------------------------------------------------------------------------*/    
RWOrdered &CtiCCStrategyStore::Strategies()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if ( _doreset || !_isvalid )
    {
        reset();

        if( isValid() )
            _doreset = FALSE;
    }

    return _strategyList->Strategies();
}

/*---------------------------------------------------------------------------
    StrategyList
    
    Returns a CtiCCStrategyList
---------------------------------------------------------------------------*/    
CtiCCStrategyList* CtiCCStrategyStore::StrategyList()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if ( _doreset || !_isvalid )
    {
        reset();

        if( isValid() )
            _doreset = FALSE;
    }

    return _strategyList;
}

/*---------------------------------------------------------------------------
    StateList
    
    Returns a RWOrdered of CtiCCStates
---------------------------------------------------------------------------*/    
RWOrdered* CtiCCStrategyStore::StateList()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if ( _doreset || !_isvalid )
    {
        reset();

        if( isValid() )
            _doreset = FALSE;
    }

    return _stateList;
}

/*---------------------------------------------------------------------------
    AreaList
    
    Returns a RWOrdered of RWCStrings
---------------------------------------------------------------------------*/    
RWOrdered* CtiCCStrategyStore::AreaList()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if ( _doreset || !_isvalid )
    {
        reset();

        if( isValid() )
            _doreset = FALSE;
    }

    return _areaList;
}

/*---------------------------------------------------------------------------
    UpdateStrategy
    
    Updates a strategy in the database.
---------------------------------------------------------------------------*/
bool CtiCCStrategyStore::UpdateStrategy(CtiCCStrategy* strategy)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBTable table = getDatabase().table( "capcontrolstrategy" );
    RWDBUpdater updater = table.updater();

    updater.where( table["capstrategyid"] == strategy->Id() );

    updater << table["status"].assign(strategy->Status());

    updater.execute( conn );

    return updater.status().isValid();
}

/*---------------------------------------------------------------------------
    UpdateCapBank
    
    Updates a cap bank in the database.
---------------------------------------------------------------------------*/
bool CtiCCStrategyStore::UpdateCapBank(CtiCapBank* bank)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBTable device = getDatabase().table("device");
    RWDBUpdater updater = device.updater();

    updater.where( device["deviceid"] == bank->Id() );

    updater << device["disableflag"].assign(RWCString( ( bank->DisableFlag() ? 'Y': 'N' ) ));

    updater.execute( conn );

    return updater.status().isValid();
}

/*---------------------------------------------------------------------------
    dumpAllDynamicData
    
    Writes out the dynamic information for each of the strategies.
---------------------------------------------------------------------------*/
void CtiCCStrategyStore::dumpAllDynamicData()
{
    RWDBDateTime currentTime = RWDBDateTime();

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);
    
    RWDBTable dynamicCapControlStrategy = getDatabase().table( "DYNAMICCAPCONTROLSTRATEGY" );
    RWDBDeleter deleter = dynamicCapControlStrategy.deleter();
    deleter.execute( conn );

    for(UINT i=0;i<_strategyList->Strategies().entries();i++)
    {
        CtiCCStrategy* currentStrat = (CtiCCStrategy*)_strategyList->Strategies()[i];

        RWDBInserter inserter = dynamicCapControlStrategy.inserter();

        inserter << currentStrat->Id()
                 << RWCString( ( currentStrat->NewPointDataReceived() ? 'Y': 'N' ) )
                 << RWCString( ( currentStrat->StrategyUpdated() ? 'Y': 'N' ) )
                 << currentStrat->ActualVarPointValue()
                 << currentStrat->NextCheckTime()
                 << currentStrat->CalculatedVarPointValue()
                 << currentStrat->Operations()
                 << currentStrat->LastOperation()
                 << currentStrat->LastCapBankControlled()
                 << RWCString( ( currentStrat->PeakOrOffPeak() ? 'Y': 'N' ) )
                 << RWCString( ( currentStrat->RecentlyControlled() ? 'Y': 'N' ) )
                 << currentStrat->CalculatedValueBeforeControl()
                 << currentTime
                 << currentStrat->LastPointUpdate();

        /*{
            RWMutexLock::LockGuard guard(coutMux);
            cout << inserter.asString() << endl;
        }*/
        inserter.execute( conn );
    }
}

/*---------------------------------------------------------------------------
    reset
    
    Reset attempts to read in all the strategies from the database.
---------------------------------------------------------------------------*/
void CtiCCStrategyStore::reset()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

	RWOrdered& strats = _strategyList->Strategies();
	if( strats.entries() > 0 )
    {
		strats.clearAndDestroy();
    }

    if( _CAP_DEBUG )
    {    
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Obtaining connection to the database..." << endl;
        dout << RWTime().asString() << " - " << "Reseting strategies from database..." << endl;
    }

    {
        RWDBConnection conn = getConnection();
        RWLockGuard<RWDBConnection> conn_guard(conn);

        if ( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable capControlStrategy = db.table("CapControlStrategy");
            RWDBTable pointUnit = db.table("PointUnit");

            RWDBSelector selector = db.selector();
            selector << capControlStrategy["capstrategyid"]
            << capControlStrategy["strategyname"]
            << capControlStrategy["districtname"]
            << capControlStrategy["actualvarpointid"]
            << capControlStrategy["maxdailyoperation"]
            << capControlStrategy["peaksetpoint"]
            << capControlStrategy["offpeaksetpoint"]
            << capControlStrategy["peakstarttime"]
            << capControlStrategy["peakstoptime"]
            << capControlStrategy["calculatedvarloadpointid"]
            << capControlStrategy["bandwidth"]
            << capControlStrategy["controlinterval"]
            << capControlStrategy["minresponsetime"]
            << capControlStrategy["minconfirmpercent"]
            << capControlStrategy["failurepercent"]
            << capControlStrategy["status"]
            << capControlStrategy["daysofweek"]
            << pointUnit["decimalplaces"];

            selector.from(capControlStrategy);
			selector.from(pointUnit);

            selector.where(capControlStrategy["calculatedvarloadpointid"]==pointUnit["pointid"]);

            /*if( _CAP_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << selector.asString().data() << endl;
            }*/

            RWDBReader rdr = selector.reader(conn);

            while ( rdr() )
            {
                CtiCCStrategy* strat = new CtiCCStrategy(rdr);
                _strategyList->Strategies().insert( strat );
            }

            /************************************************************
            ********    Loading Dynamic Data for each Strategy   ********
            ************************************************************/
            for(ULONG k=0;k<_strategyList->Strategies().entries();k++)
            {
                CtiCCStrategy* currentStrat = (CtiCCStrategy*)_strategyList->Strategies()[k];

                RWDBTable dynamicCapControlStrategy = db.table("DynamicCapControlStrategy");

                RWDBSelector selector = db.selector();
                selector << dynamicCapControlStrategy["newpointdatareceived"]
                         << dynamicCapControlStrategy["strategyupdated"]
                         << dynamicCapControlStrategy["actualvarpointvalue"]
                         << dynamicCapControlStrategy["nextchecktime"]
                         << dynamicCapControlStrategy["calcvarpointvalue"]
                         << dynamicCapControlStrategy["operations"]
                         << dynamicCapControlStrategy["lastoperation"]
                         << dynamicCapControlStrategy["lastcapbankcontrolled"]
                         << dynamicCapControlStrategy["peakoroffpeak"]
                         << dynamicCapControlStrategy["recentlycontrolled"]
                         << dynamicCapControlStrategy["calcvaluebeforecontrol"]
                         << dynamicCapControlStrategy["timestamp"]
                         << dynamicCapControlStrategy["lastpointupdate"];

                selector.from(dynamicCapControlStrategy);

                selector.where(dynamicCapControlStrategy["capstrategyid"]==currentStrat->Id());

                /*if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << selector.asString().data() << endl;
                }*/

                RWDBReader rdr = selector.reader(conn);
                while ( rdr() )
                {
                    currentStrat->restoreDynamicData(rdr);
                }
            }

            /************************************************************
            ********     Loading Cap Banks for each Strategy     ********
            ************************************************************/
            for(UINT i=0;i<_strategyList->Strategies().entries();i++)
            {
                CtiCCStrategy* currentStrat = (CtiCCStrategy*)_strategyList->Strategies()[i];

                RWDBTable ccStrategyBankList = db.table("CCStrategyBankList");
                RWDBTable device = db.table("Device");
                RWDBTable capBank = db.table("CapBank");
                RWDBTable point = db.table("Point");

                RWDBSelector selector = db.selector();
                selector << device["deviceid"]
                << device["name"]
                << device["type"]
                << device["currentstate"]
                << device["disableflag"]
                << device["alarminhibit"]
                << device["controlinhibit"]
                << device["class"]
                << capBank["bankaddress"]
                << capBank["operationalstate"]
                << capBank["controlpointid"]
                << capBank["banksize"]
                << capBank["controldeviceid"]
                << point["pointid"];

                selector.from(ccStrategyBankList);
                selector.from(device);
                selector.from(capBank);
                selector.from(point);

                selector.where(ccStrategyBankList["CapStrategyID"]==currentStrat->Id()&&
                               ccStrategyBankList["DeviceID"]==device["DeviceID"]&&
                               device["DeviceID"]==capBank["DeviceID"]&&
                               device["DeviceID"]==point["DeviceID"]&&
                               point["PointOffset"]==1&&
                               point["PointType"]=="Status");

                selector.orderBy(ccStrategyBankList["ControlOrder"]);

                /*if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << selector.asString().data() << endl;
                }*/

                RWDBReader rdr = selector.reader(conn);
                while ( rdr() )
                {
                    CtiCapBank* bank = new CtiCapBank(rdr);
                    currentStrat->insertCapBank(bank);
                }

                RWOrdered &capBankList = currentStrat->CapBankList();
                for(ULONG j=0;j<capBankList.entries();j++)
                {
                    CtiCapBank* currentCapBank = (CtiCapBank*)capBankList[j];

                    RWDBTable device = db.table("Device");
                    RWDBTable point = db.table("Point");

                    RWDBSelector selector = db.selector();
                    selector << point["pointid"];

                    selector.from(device);
                    selector.from(point);

                    selector.where(device["DeviceID"]==currentCapBank->Id()&&
                                   device["DeviceID"]==point["DeviceID"]&&
                                   point["PointOffset"]==1&&
                                   point["PointType"]=="Analog");

                    /*if( _CAP_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime().asString() << " - " << selector.asString().data() << endl;
                    }*/

                    RWDBReader rdr = selector.reader(conn);
                    while ( rdr() )
                    {
                        currentCapBank->restoreOperationFields(rdr);
                    }
                }
            }

            {
                if( _stateList->entries() > 0 )
                {
                    _stateList->clearAndDestroy();
                }

                RWDBTable state = db.table("State");

                RWDBSelector selector = db.selector();
                selector << state["text"]
                         << state["foregroundcolor"]
                         << state["backgroundcolor"];

                selector.from(state);

                selector.where(state["stategroupid"]==3&&state["rawstate"]>=0);

                selector.orderBy(state["rawstate"]);
                /*if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << selector.asString().data() << endl;
                }*/

                RWDBReader rdr = selector.reader(conn);

                while ( rdr() )
                {
                    CtiCCState* ccState = new CtiCCState(rdr);
                    _stateList->insert( ccState );
                }
            }

            {
                if( _areaList->entries() > 0 )
                {
                    _areaList->clearAndDestroy();
                }

                RWDBTable capcontrolstrategy = db.table("capcontrolstrategy");

                RWDBSelector selector = db.selector();
                selector.distinct();
                selector << capcontrolstrategy["districtname"];

                selector.from(capcontrolstrategy);

                selector.orderBy(capcontrolstrategy["districtname"]);
                /*if( _CAP_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << selector.asString().data() << endl;
                }*/

                RWDBReader rdr = selector.reader(conn);

                while ( rdr() )
                {
                    RWDBSchema schema = rdr.table().schema();

                    RWCollectableString* areaString = NULL;
                    for(UINT k=0;k<schema.entries();k++)
                    {
                        RWCString col = schema[k].qualifiedName();
                        col.toLower();

                        //   cout << "col is:  " << col << endl;
                        if ( col == "districtname" )
                        {
                            RWCString tempStr;
                            rdr[col] >> tempStr;

                            areaString = new RWCollectableString(tempStr);
                        }
                    }
                    if(areaString != NULL)
                    {
                        _areaList->insert( areaString );
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - " << "Unable to get valid database connection." << endl;
            _isvalid = FALSE;
            return;
        }
    }

    dumpAllDynamicData();
    _isvalid = TRUE;
    return;
}

/*---------------------------------------------------------------------------
    shutdown
    
    Dumps the strategy list.
---------------------------------------------------------------------------*/
void CtiCCStrategyStore::shutdown()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    dumpAllDynamicData();
    _strategyList->Strategies().clearAndDestroy();
	delete _strategyList;
    _stateList->clearAndDestroy();
    delete _stateList;
    _areaList->clearAndDestroy();
    delete _areaList;

    if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "done shutting down the strategystore" << endl;
    }
}

/*---------------------------------------------------------------------------
    doResetThr
    
    Starts on construction and simply forces a call to reset every 5 minutes
---------------------------------------------------------------------------*/
void CtiCCStrategyStore::doResetThr()
{
    char temp[80];
    int refreshrate = 3600;

    HINSTANCE hLib = LoadLibrary("cparms.dll");

    if (hLib)
    {
        CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

        bool trouble = FALSE;

        if ( (*fpGetAsString)("CAP_CONTROL_REFRESH", temp, 80) )
        {
            if( _CAP_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "CAP_CONTROL_REFRESH:  " << temp << endl;
            }

            refreshrate = atoi(temp);
        }
        else
            trouble = TRUE;

        if ( trouble == TRUE )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - " << "Unable to obtain one or more values from cparms." << endl;
        }

        FreeLibrary(hLib);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Unable to load cparms.dll" << endl;
    }

    time_t start = time(NULL);

    RWDBDateTime currenttime = RWDBDateTime();
    ULONG tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+refreshrate;
    RWDBDateTime nextDatabaseRefresh = RWDBDateTime(RWTime(tempsum));

    while (1)
    {
        rwRunnable().serviceCancellation();

        if ( RWDBDateTime() >= nextDatabaseRefresh )
        {
            if( _CAP_DEBUG )
            {    
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "Restoring strategy list from the database" << endl;
            }

            dumpAllDynamicData();
            notValid();
            setReregisterForPoints(TRUE);

            currenttime = RWDBDateTime();
            tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+refreshrate;
            nextDatabaseRefresh = RWDBDateTime(RWTime(tempsum));
        }
        else
        {
            rwRunnable().sleep(500);
        }
    }
}

/* Pointer to the singleton instance of CtiCCStrategyStore 
   Instantiate lazily by Instance */
CtiCCStrategyStore* CtiCCStrategyStore::_instance = NULL;

/*---------------------------------------------------------------------------
    Instance
    
    Returns a pointer to the singleton instance of CtiCCStrategyStore
---------------------------------------------------------------------------*/
CtiCCStrategyStore* CtiCCStrategyStore::Instance()
{
    if ( _instance == NULL )
    {
        _instance = new CtiCCStrategyStore();
    }

    return _instance;
}

/*---------------------------------------------------------------------------
    DeleteInstance
    
    Deletes the singleton instance of CtiCCStrategyStore
---------------------------------------------------------------------------*/
void CtiCCStrategyStore::DeleteInstance()
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
bool CtiCCStrategyStore::isValid() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return _isvalid;
}

/*---------------------------------------------------------------------------
    notValid
    
    Sets the _isvalid flag to FALSE so the strategystore will reload
---------------------------------------------------------------------------*/
void CtiCCStrategyStore::notValid()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _isvalid = FALSE;
}

/*---------------------------------------------------------------------------
    getReregisterForPoints
    
    Gets _reregisterforpoints
---------------------------------------------------------------------------*/
bool CtiCCStrategyStore::getReregisterForPoints() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return _reregisterforpoints;
}

/*---------------------------------------------------------------------------
    setReregisterForPoints
    
    Sets _reregisterforpoints
---------------------------------------------------------------------------*/
void CtiCCStrategyStore::setReregisterForPoints(bool reregister)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _reregisterforpoints = reregister;
}
