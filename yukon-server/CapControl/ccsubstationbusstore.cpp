/*---------------------------------------------------------------------------
        Filename:  ccsubstationbusstore.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCSubstationBusStore
                        CtiCCSubstationBusStore maintains a pool of
                        CtiCCSubstationBuses.

        Initial Date:  8/27/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <rw/rwfile.h>
#include <rw/thr/thrfunc.h>
#include <rw/collstr.h>
//#include <fstream>

#include "ccsubstationbusstore.h"
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    bool wasAlreadyRunning = false;
    try
    {
        LONG currentAllocations = ResetBreakAlloc();
        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
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
                    dout << RWTime() << " - Reseting substation buses from database..." << endl;
                }

                if ( conn.isValid() )
                {
                    if( _ccSubstationBuses->entries() > 0 )
                    {
                        dumpAllDynamicData();
                        _ccSubstationBuses->clearAndDestroy();
                        wasAlreadyRunning = true;
                    }
                    if( _ccCapBankStates->entries() > 0 )
                    {
                        _ccCapBankStates->clearAndDestroy();
                    }
                    if( _ccGeoAreas->entries() > 0 )
                    {
                        _ccGeoAreas->clearAndDestroy();
                    }
    
                    RWDBDateTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                    RWDBTable capControlSubstationBusTable = db.table("capcontrolsubstationbus");
                    RWDBTable pointTable = db.table("point");
                    RWDBTable pointUnitTable = db.table("pointunit");
                    RWDBTable dynamicCCSubstationBusTable = db.table("dynamicccsubstationbus");
    
                    {
                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                                 << yukonPAObjectTable["category"]
                                 << yukonPAObjectTable["paoclass"]
                                 << yukonPAObjectTable["paoname"]
                                 << yukonPAObjectTable["type"]
                                 << yukonPAObjectTable["description"]
                                 << yukonPAObjectTable["disableflag"]
                                 << capControlSubstationBusTable["controlmethod"]
                                 << capControlSubstationBusTable["maxdailyoperation"]
                                 << capControlSubstationBusTable["maxoperationdisableflag"]
                                 << capControlSubstationBusTable["peaksetpoint"]
                                 << capControlSubstationBusTable["offpeaksetpoint"]
                                 << capControlSubstationBusTable["peakstarttime"]
                                 << capControlSubstationBusTable["peakstoptime"]
                                 << capControlSubstationBusTable["currentvarloadpointid"]
                                 << capControlSubstationBusTable["currentwattloadpointid"]
                                 << capControlSubstationBusTable["upperbandwidth"]
                                 << capControlSubstationBusTable["controlinterval"]
                                 << capControlSubstationBusTable["minresponsetime"]//will become "minconfirmtime" in the DB in 3.1
                                 << capControlSubstationBusTable["minconfirmpercent"]
                                 << capControlSubstationBusTable["failurepercent"]
                                 << capControlSubstationBusTable["daysofweek"]
                                 << capControlSubstationBusTable["maplocationid"]
                                 << capControlSubstationBusTable["lowerbandwidth"]
                                 << capControlSubstationBusTable["controlunits"]
                                 << capControlSubstationBusTable["controldelaytime"]
                                 << capControlSubstationBusTable["controlsendretries"]
                                 << pointUnitTable["decimalplaces"]
                                 << dynamicCCSubstationBusTable["substationbusid"]
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
                                 << pointTable["pointid"]
                                 << pointTable["pointoffset"]
                                 << pointTable["pointtype"];
    
                        selector.from(yukonPAObjectTable);
                        selector.from(capControlSubstationBusTable);
                        selector.from(pointUnitTable);
                        selector.from(dynamicCCSubstationBusTable);
                        selector.from(pointTable);
    
                        selector.where(yukonPAObjectTable["paobjectid"]==capControlSubstationBusTable["substationbusid"] &&
                                       capControlSubstationBusTable["currentvarloadpointid"].leftOuterJoin(pointUnitTable["pointid"]) &&
                                       capControlSubstationBusTable["substationbusid"].leftOuterJoin(dynamicCCSubstationBusTable["substationbusid"]) &&
                                       capControlSubstationBusTable["substationbusid"].leftOuterJoin(pointTable["paobjectid"]) );
    
                        selector.orderBy(yukonPAObjectTable["description"]);
                        selector.orderBy(yukonPAObjectTable["paoname"]);
                        selector.orderBy(pointTable["pointoffset"]);
    
                        if( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - DataBase Reload Begin - " << endl;
                        }
                        if( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }
    
                        RWDBReader rdr = selector.reader(conn);
    
                        CtiCCSubstationBus* currentCCSubstationBus = NULL;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            LONG tempPAObjectId = -1000;
                            rdr["paobjectid"] >> tempPAObjectId;
                            if( currentCCSubstationBus != NULL &&
                                tempPAObjectId == currentCCSubstationBus->getPAOId() )
                            {
                                rdr["pointid"] >> isNull;
                                if( !isNull )
                                {
                                    LONG tempPointId = -1000;
                                    LONG tempPointOffset = -1000;
                                    RWCString tempPointType = "(none)";
                                    rdr["pointid"] >> tempPointId;
                                    rdr["pointoffset"] >> tempPointOffset;
                                    rdr["pointtype"] >> tempPointType;
                                    if( resolvePointType(tempPointType) == AnalogPointType )
                                    {
                                        if( tempPointOffset==1 )
                                        {//estimated vars point
                                            currentCCSubstationBus->setEstimatedVarLoadPointId(tempPointId);
                                        }
                                        else if( tempPointOffset==2 )
                                        {//daily operations point
                                            currentCCSubstationBus->setDailyOperationsAnalogPointId(tempPointId);
                                        }
                                        else if( tempPointOffset==3 )
                                        {//power factor point
                                            currentCCSubstationBus->setPowerFactorPointId(tempPointId);
                                        }
                                        else if( tempPointOffset==4 )
                                        {//estimated power factor point
                                            currentCCSubstationBus->setEstimatedPowerFactorPointId(tempPointId);
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
                            else
                            {
                                currentCCSubstationBus = new CtiCCSubstationBus(rdr);
                                _ccSubstationBuses->insert( currentCCSubstationBus );
                            }
                        }
                    }

                    /*{
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Done loading Sub Buses, now loading Feeders" << endl;
                    }*/
                    /************************************************************
                    *******  Loading Feeders                              *******
                    ************************************************************/
                    RWDBTable ccFeederSubAssignmentTable = db.table("ccfeedersubassignment");
                    RWDBTable capControlFeederTable = db.table("capcontrolfeeder");
                    RWDBTable dynamicCCFeederTable = db.table("dynamicccfeeder");
    
                    if( _ccSubstationBuses->entries() > 0 )
                    {
                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                                 << yukonPAObjectTable["category"]
                                 << yukonPAObjectTable["paoclass"]
                                 << yukonPAObjectTable["paoname"]
                                 << yukonPAObjectTable["type"]
                                 << yukonPAObjectTable["description"]
                                 << yukonPAObjectTable["disableflag"]
                                 << capControlFeederTable["peaksetpoint"]
                                 << capControlFeederTable["offpeaksetpoint"]
                                 << capControlFeederTable["upperbandwidth"]
                                 << capControlFeederTable["currentvarloadpointid"]
                                 << capControlFeederTable["currentwattloadpointid"]
                                 << capControlFeederTable["maplocationid"]
                                 << capControlFeederTable["lowerbandwidth"]
                                 << ccFeederSubAssignmentTable["substationbusid"]
                                 << ccFeederSubAssignmentTable["displayorder"]
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
                                 << pointTable["pointid"]
                                 << pointTable["pointoffset"]
                                 << pointTable["pointtype"];
    
                        selector.from(yukonPAObjectTable);
                        selector.from(capControlFeederTable);
                        selector.from(ccFeederSubAssignmentTable);
                        selector.from(dynamicCCFeederTable);
                        selector.from(pointTable);
    
                        selector.where( yukonPAObjectTable["paobjectid"]==ccFeederSubAssignmentTable["feederid"] &&
                                        capControlFeederTable["feederid"]==ccFeederSubAssignmentTable["feederid"] &&
                                        capControlFeederTable["feederid"].leftOuterJoin(dynamicCCFeederTable["feederid"]) &&
                                        capControlFeederTable["feederid"].leftOuterJoin(pointTable["paobjectid"]) );
    
                        selector.orderBy(ccFeederSubAssignmentTable["substationbusid"]);
                        selector.orderBy(ccFeederSubAssignmentTable["displayorder"]);
                        selector.orderBy(pointTable["pointoffset"]);
    
                        if( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }
    
                        RWDBReader rdr = selector.reader(conn);
    
                        CtiCCFeeder* currentCCFeeder = NULL;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            LONG tempPAObjectId = 0;
                            rdr["paobjectid"] >> tempPAObjectId;
                            if( currentCCFeeder != NULL &&
                                tempPAObjectId == currentCCFeeder->getPAOId() )
                            {
                                rdr["pointid"] >> isNull;
                                if( !isNull )
                                {
                                    LONG tempPointId = -1000;
                                    LONG tempPointOffset = -1000;
                                    RWCString tempPointType = "(none)";
                                    rdr["pointid"] >> tempPointId;
                                    rdr["pointoffset"] >> tempPointOffset;
                                    rdr["pointtype"] >> tempPointType;
                                    if( resolvePointType(tempPointType) == AnalogPointType )
                                    {
                                        if( tempPointOffset==1 )
                                        {//estimated vars point
                                            currentCCFeeder->setEstimatedVarLoadPointId(tempPointId);
                                        }
                                        else if( tempPointOffset==2 )
                                        {//daily operations point
                                            currentCCFeeder->setDailyOperationsAnalogPointId(tempPointId);
                                        }
                                        else if( tempPointOffset==3 )
                                        {//power factor point
                                            currentCCFeeder->setPowerFactorPointId(tempPointId);
                                        }
                                        else if( tempPointOffset==4 )
                                        {//estimated power factor point
                                            currentCCFeeder->setEstimatedPowerFactorPointId(tempPointId);
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
                            else
                            {
                                currentCCFeeder = new CtiCCFeeder(rdr);
    
                                /************************************************************
                                *******  Inserting Feeder into the correct Bus        *******
                                ************************************************************/
                                LONG tempSubstationBusId = 0;
                                rdr["substationbusid"] >> tempSubstationBusId;
                                for(LONG i=0;i<_ccSubstationBuses->entries();i++)
                                {
                                    CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)((*_ccSubstationBuses)[i]);
                                    if( currentCCSubstationBus->getPAOId() == tempSubstationBusId )
                                    {
                                        currentCCSubstationBus->getCCFeeders().insert( currentCCFeeder );
                                        break;
                                    }
                                }
                            }
                        }
    
                        /*{
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Done loading Feeder, now loading Cap Banks" << endl;
                        }*/
                        /************************************************************
                        ********    Loading Cap Banks                        ********
                        ************************************************************/
                        {
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
                                         << deviceTable["alarminhibit"]
                                         << deviceTable["controlinhibit"]
                                         << capBankTable["operationalstate"]
                                         << capBankTable["controllertype"]
                                         << capBankTable["controldeviceid"]
                                         << capBankTable["controlpointid"]
                                         << capBankTable["banksize"]
                                         << capBankTable["typeofswitch"]
                                         << capBankTable["switchmanufacture"]
                                         << capBankTable["maplocationid"]
                                         << capBankTable["reclosedelay"]
                                         << ccFeederBankListTable["feederid"]
                                         << ccFeederBankListTable["controlorder"]
                                         << dynamicCCCapBankTable["controlstatus"]
                                         << dynamicCCCapBankTable["currentdailyoperations"]
                                         << dynamicCCCapBankTable["laststatuschangetime"]
                                         << dynamicCCCapBankTable["tagscontrolstatus"]
                                         << dynamicCCCapBankTable["ctitimestamp"]
                                         << dynamicCCCapBankTable["originalfeederid"]
                                         << dynamicCCCapBankTable["originalswitchingorder"]
                                         << pointTable["pointid"]
                                         << pointTable["pointoffset"]
                                         << pointTable["pointtype"];
    
                                selector.from(yukonPAObjectTable);
                                selector.from(deviceTable);
                                selector.from(capBankTable);
                                selector.from(dynamicCCCapBankTable);
                                selector.from(pointTable);
                                selector.from(ccFeederSubAssignmentTable);
                                selector.from(ccFeederBankListTable);
    
                                selector.where(deviceTable["deviceid"]==capBankTable["deviceid"] &&
                                               yukonPAObjectTable["paobjectid"]==deviceTable["deviceid"] &&
                                               ccFeederSubAssignmentTable["feederid"]==ccFeederBankListTable["feederid"] &&
                                               capBankTable["deviceid"]==ccFeederBankListTable["deviceid"] &&
                                               capBankTable["deviceid"].leftOuterJoin(dynamicCCCapBankTable["capbankid"]) &&
                                               capBankTable["deviceid"].leftOuterJoin(pointTable["paobjectid"]) );
    
                                selector.orderBy(ccFeederSubAssignmentTable["substationbusid"]);
                                selector.orderBy(ccFeederSubAssignmentTable["displayorder"]);
                                selector.orderBy(ccFeederBankListTable["controlorder"]);
    
                                if( _CC_DEBUG & CC_DEBUG_DATABASE )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }
    
                                RWDBReader rdr = selector.reader(conn);
    
                                CtiCCCapBank* currentCCCapBank = NULL;
                                RWDBNullIndicator isNull;
                                while ( rdr() )
                                {
                                    LONG tempPAObjectId = 0;
                                    rdr["paobjectid"] >> tempPAObjectId;
                                    if( currentCCCapBank != NULL &&
                                        tempPAObjectId == currentCCCapBank->getPAOId() )
                                    {
                                        rdr["pointid"] >> isNull;
                                        if( !isNull )
                                        {
                                            LONG tempPointId = -1000;
                                            LONG tempPointOffset = -1000;
                                            RWCString tempPointType = "(none)";
                                            rdr["pointid"] >> tempPointId;
                                            rdr["pointoffset"] >> tempPointOffset;
                                            rdr["pointtype"] >> tempPointType;
                                            if( tempPointOffset == 1 )
                                            {
                                                if( resolvePointType(tempPointType) == StatusPointType )
                                                {//control status point
                                                    currentCCCapBank->setStatusPointId(tempPointId);
                                                }
                                                else if( resolvePointType(tempPointType) == AnalogPointType )
                                                {//daily operations point
                                                    currentCCCapBank->setOperationAnalogPointId(tempPointId);
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
                                    else
                                    {
                                        currentCCCapBank = new CtiCCCapBank(rdr);
    
                                        /************************************************************
                                        *******  Inserting Cap Bank into the correct Feeder   *******
                                        ************************************************************/
                                        LONG tempFeederId = 0;
                                        rdr["feederid"] >> tempFeederId;
                                        BOOL found = FALSE;
                                        for(LONG j=0;j<_ccSubstationBuses->entries();j++)
                                        {
                                            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)((*_ccSubstationBuses)[j]);
                                            for(LONG k=0;k<currentCCSubstationBus->getCCFeeders().entries();k++)
                                            {
                                                CtiCCFeeder* currentCCFeeder = (CtiCCFeeder*)((currentCCSubstationBus->getCCFeeders())[k]);
                                                if( currentCCFeeder->getPAOId() == tempFeederId )
                                                {
                                                    currentCCFeeder->getCCCapBanks().insert( currentCCCapBank );
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
                                }
                            }
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - No Substations in: " << __FILE__ << " at: " << __LINE__ << endl;
                    }
                    {
                        if( _ccCapBankStates->entries() > 0 )
                        {
                            _ccCapBankStates->clearAndDestroy();
                        }
    
                        RWDBTable stateTable = db.table("state");
    
                        RWDBSelector selector = db.selector();
                        selector << stateTable["text"]
                                 << stateTable["foregroundcolor"]
                                 << stateTable["backgroundcolor"];
    
                        selector.from(stateTable);
    
                        selector.where(stateTable["stategroupid"]==3 && stateTable["rawstate"]>=0);
    
                        selector.orderBy(stateTable["rawstate"]);
    
                        if( _CC_DEBUG & CC_DEBUG_DATABASE )
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
    
                    {
                        if( _ccGeoAreas->entries() > 0 )
                        {
                            _ccGeoAreas->clearAndDestroy();
                        }
    
                        RWDBSelector selector = db.selector();
                        selector.distinct();
                        selector << yukonPAObjectTable["description"];
    
                        selector.from(yukonPAObjectTable);
                        selector.from(capControlSubstationBusTable);
    
                        selector.where(yukonPAObjectTable["paobjectid"]==capControlSubstationBusTable["substationbusid"]);
    
                        selector.orderBy(yukonPAObjectTable["description"]);
    
                        if( _CC_DEBUG & CC_DEBUG_DATABASE )
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
                    if( _CC_DEBUG & CC_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - DataBase Reload End - " << endl;
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
    catch(...)
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
        if( !wasAlreadyRunning )
        {
            dumpAllDynamicData();
        }
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Store START sending messages to clients." << endl;
        }
        ULONG msgBitMask = CtiCCSubstationBusMsg::AllSubBusesSent;
        if( _wassubbusdeletedflag )
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
    catch(...)
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
        if( amfmConn.isValid() && amfmConn.isReady() )
        {
            RWDBDateTime lastAMFMUpdateTime = RWDBDateTime(1990,1,1,0,0,0,0);
            RWCString tempStr = DefaultMasterConfigFileName;
            tempStr.remove(tempStr.length()-10);
            RWFile amfmFile((tempStr+"amfm.dat").data());

            if( amfmFile.Exists() )
            {
                if( !amfmFile.IsEmpty() )
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
                CtiCapController::getInstance()->sendMessageToDispatch( new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent) );
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
    RWDBDateTime    switch_datetime                   = RWDBDateTime(1990,1,1,0,0,0,0);
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

    LONG capacitor_id = atol(capacitor_id_string);

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
                            if( currentCapBank->getMapLocationId() == capacitor_id )
                            {
                                LONG capswitchingorder = atol(switching_seq);
                                if( currentFeeder->getMapLocationId() != circt_id_current )
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
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
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
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
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
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
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
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
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
                                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
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
        dout << RWTime() << " - Cap Bank not found MapLocationId: " << capacitor_id << " in: " << __FILE__ << " at: " << __LINE__ << endl;
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

                if( currentFeeder->getMapLocationId() == feederid )
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
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
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
        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
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
                            if( currentCapBank->getMapLocationId() == capid )
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

                    if( currentFeeder->getMapLocationId() == feederid )
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

            time_t start = ::std::time(NULL);

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

                            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                            {
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Setting status to close questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                }
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType));
                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
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

                        if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName() << " questionable because feeder is not recently controlled in: " << __FILE__ << " at: " << __LINE__ << endl;
                            }
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType));
                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
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

                    if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Setting status to close questionable, Cap Bank: " << currentCapBank->getPAOName() << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::CloseQuestionable,NormalQuality,StatusPointType));
                    }
                    else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
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
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
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
            /*for(int k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];*/
                /*{
                    char tempchar[64] = "";
                    RWCString text = RWCString("Daily Operations were ");
                    _ltoa(currentCapBank->getCurrentDailyOperations(),tempchar,10);
                    text += tempchar;
                    RWCString additional = RWCString("Cap Bank: ");
                    additional += currentCapBank->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent));
                }*/
                /*currentCapBank->setCurrentDailyOperations(0);
            }*/
            //**********************************************************************
            //The operation count on a cap bank is actually a total not a daily, doh
            //**********************************************************************
        }
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

