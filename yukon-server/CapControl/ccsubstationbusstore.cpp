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

extern BOOL _CC_DEBUG;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCSubstationBusStore::CtiCCSubstationBusStore() : _isvalid(FALSE), _reregisterforpoints(TRUE), _reloadfromamfmsystemflag(FALSE), _lastdbreloadtime(RWDBDateTime(1990,1,1,0,0,0,0))
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _resetthr.isValid() )
    {
        _resetthr.requestCancellation();
    }
    if( _amfmthr.isValid() )
    {
        _amfmthr.requestCancellation();
    }

    shutdown();
}

/*---------------------------------------------------------------------------
    getSubstationBuses

    Returns a RWOrdered of CtiCCSubstationBuses
---------------------------------------------------------------------------*/
RWOrdered* CtiCCSubstationBusStore::getCCSubstationBuses(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+90 )
    {//is not valid and has been at 1.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+90 )
    {//is not valid and has been at 1.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( _ccSubstationBuses->entries() > 0 )
    {
        for(ULONG i=0;i<_ccSubstationBuses->entries();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];
            currentCCSubstationBus->dumpDynamicData();

            RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.entries() > 0 )
            {
                for(ULONG j=0;j<ccFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                    currentFeeder->dumpDynamicData();

                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                    if( ccCapBanks.entries() > 0 )
                    {
                        for(ULONG k=0;k<ccCapBanks.entries();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                            currentCapBank->dumpDynamicData();
                        }
                    }
                }
            }
        }
    }
}

/*---------------------------------------------------------------------------
    reset

    Reset attempts to read in all the substation buses from the database.
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::reset()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    bool wasAlreadyRunning = false;
    try
    {
        //if( _CC_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Obtaining connection to the database..." << endl;
            dout << RWTime() << " - Reseting substation buses from database..." << endl;
        }
    
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
    
                if ( conn.isValid() )
                {
                    if( _ccSubstationBuses->entries() > 0 )
                    {
                        dumpAllDynamicData();
                        _ccSubstationBuses->clearAndDestroy();
                        wasAlreadyRunning = true;
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
                                 << capControlSubstationBusTable["minresponsetime"]
                                 << capControlSubstationBusTable["minconfirmpercent"]
                                 << capControlSubstationBusTable["failurepercent"]
                                 << capControlSubstationBusTable["daysofweek"]
                                 << capControlSubstationBusTable["maplocationid"]
                                 << capControlSubstationBusTable["lowerbandwidth"]
                                 << capControlSubstationBusTable["controlunits"]
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
                                 << pointTable["pointid"]
                                 << pointTable["pointoffset"]
                                 << pointTable["pointtype"];
    
                        selector.from(yukonPAObjectTable);
                        selector.from(capControlSubstationBusTable);
                        selector.from(pointUnitTable);
                        selector.from(dynamicCCSubstationBusTable);
                        selector.from(pointTable);
    
                        selector.where(yukonPAObjectTable["paobjectid"]==capControlSubstationBusTable["substationbusid"] &&
                                       capControlSubstationBusTable["currentvarloadpointid"]==pointUnitTable["pointid"] &&
                                       capControlSubstationBusTable["substationbusid"].leftOuterJoin(dynamicCCSubstationBusTable["substationbusid"]) &&
                                       capControlSubstationBusTable["substationbusid"].leftOuterJoin(pointTable["paobjectid"]) );
    
                        selector.orderBy(yukonPAObjectTable["description"]);
                        selector.orderBy(yukonPAObjectTable["paoname"]);
                        selector.orderBy(pointTable["pointoffset"]);
    
                        /*if( _CC_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/
    
                        RWDBReader rdr = selector.reader(conn);
    
                        CtiCCSubstationBus* currentCCSubstationBus = NULL;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            ULONG tempPAObjectId = -1000;
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
    
                        /*if( _CC_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/
    
                        RWDBReader rdr = selector.reader(conn);
    
                        CtiCCFeeder* currentCCFeeder = NULL;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            ULONG tempPAObjectId = 0;
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
                                for(ULONG i=0;i<_ccSubstationBuses->entries();i++)
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
                                         << ccFeederBankListTable["feederid"]
                                         << ccFeederBankListTable["controlorder"]
                                         << dynamicCCCapBankTable["controlstatus"]
                                         << dynamicCCCapBankTable["currentdailyoperations"]
                                         << dynamicCCCapBankTable["laststatuschangetime"]
                                         << dynamicCCCapBankTable["tagscontrolstatus"]
                                         << dynamicCCCapBankTable["ctitimestamp"]
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
    
                                /*if( _CC_DEBUG )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }*/
    
                                RWDBReader rdr = selector.reader(conn);
    
                                CtiCCCapBank* currentCCCapBank = NULL;
                                RWDBNullIndicator isNull;
                                while ( rdr() )
                                {
                                    ULONG tempPAObjectId = 0;
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
                                        for(ULONG j=0;j<_ccSubstationBuses->entries();j++)
                                        {
                                            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)((*_ccSubstationBuses)[j]);
                                            for(ULONG k=0;k<currentCCSubstationBus->getCCFeeders().entries();k++)
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
    
                        /*if( _CC_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/
    
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
    
                        /*if( _CC_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/
    
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
        _reregisterforpoints = TRUE;
        _lastdbreloadtime.now();
        if( !wasAlreadyRunning )
        {
            dumpAllDynamicData();
        }
        CtiCCExecutorFactory f;
        RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
        CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*_ccSubstationBuses));
        executor->Execute(queue);
        delete executor;
        executor = f.createExecutor(new CtiCCCapBankStatesMsg(_ccCapBankStates));
        executor->Execute(queue);
        delete executor;
        executor = f.createExecutor(new CtiCCGeoAreasMsg(_ccGeoAreas));
        executor->Execute(queue);
        delete executor;
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
            selector << dni_capacitorTable["datetimestamp"]
                     << dni_capacitorTable["capid"]
                     << dni_capacitorTable["capname"]
                     << dni_capacitorTable["kvarrating"]
                     << dni_capacitorTable["fixedswitched"]
                     << dni_capacitorTable["cbcmodel"]
                     << dni_capacitorTable["cbcserialnumber"]
                     << dni_capacitorTable["location"]
                     << dni_capacitorTable["capswitchingorder"]
                     << dni_capacitorTable["enableddisabled"]
                     << dni_capacitorTable["feederid"]
                     << dni_capacitorTable["feedername"]
                     << dni_capacitorTable["typeofcapchange"]
                     << dni_capacitorTable["originator"];

            selector.from(dni_capacitorTable);

            selector.where(dni_capacitorTable["datetimestamp"]>lastAMFMUpdateTime);

            selector.orderBy(dni_capacitorTable["datetimestamp"]);

            /*if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << selector.asString().data() << endl;
            }*/

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBDateTime currentDateTime = RWDBDateTime();

    RWDBDateTime datetimestamp;
    LONG capid;
    RWCString capname;
    DOUBLE kvarrating;
    RWCString fixedswitched;
    RWCString cbcmodel;
    LONG cbcserialnumber;
    RWCString location;
    LONG capswitchingorder;
    RWCString enableddisabled;
    LONG feederid;
    RWCString feedername;
    RWCString typeofcapchange;
    RWCString originator;

    rdr["datetimestamp"]     >> datetimestamp;
    rdr["capid"]             >> capid;
    rdr["capname"]           >> capname;
    rdr["kvarrating"]        >> kvarrating;
    rdr["fixedswitched"]     >> fixedswitched;
    rdr["cbcmodel"]          >> cbcmodel;
    rdr["cbcserialnumber"]   >> cbcserialnumber;
    rdr["location"]          >> location;
    rdr["capswitchingorder"] >> capswitchingorder;
    rdr["enableddisabled"]   >> enableddisabled;
    rdr["feederid"]          >> feederid;
    rdr["feedername"]        >> feedername;
    rdr["typeofcapchange"]   >> typeofcapchange;
    rdr["originator"]        >> originator;

    typeofcapchange.toUpper();
    if( typeofcapchange == feederReconfigureM3IAMFMChangeTypeString ||
        typeofcapchange == aimImportM3IAMFMChangeTypeString )
    {
        feederReconfigureM3IAMFM(capid,capname,kvarrating,fixedswitched,cbcmodel,
                                 cbcserialnumber,location,capswitchingorder,
                                 enableddisabled,feederid,feedername,typeofcapchange);
    }
    else if( typeofcapchange == capOutOfServiceM3IAMFMChangeTypeString )
    {
        capOutOfServiceM3IAMFM(feederid,capid,enableddisabled,fixedswitched);
    }
    else if( typeofcapchange == feederOutOfServiceM3IAMFMChangeTypeString )
    {
        feederOutOfServiceM3IAMFM(feederid,fixedswitched,enableddisabled);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Invalid Type of Cap Change in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    feederReconfigureM3IAMFM

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::feederReconfigureM3IAMFM(LONG capid, RWCString capname, DOUBLE kvarrating,
                                                       RWCString fixedswitched, RWCString cbcmodel,
                                                       LONG cbcserialnumber, RWCString location,
                                                       LONG capswitchingorder, RWCString enableddisabled,
                                                       LONG feederid, RWCString feedername, RWCString typeofcapchange)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL found = FALSE;
    if( _ccSubstationBuses->entries() > 0 )
    {
        for(ULONG i=0;i<_ccSubstationBuses->entries();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];

            RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.entries() > 0 )
            {
                for(ULONG j=0;j<ccFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];

                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                    if( ccCapBanks.entries() > 0 )
                    {
                        for(ULONG k=0;k<ccCapBanks.entries();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                            if( currentCapBank->getMapLocationId() == capid )
                            {
                                if( currentFeeder->getMapLocationId() != feederid )
                                {
                                    capBankMovedToDifferentFeeder(currentFeeder, currentCapBank, feederid, capswitchingorder);
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
                                fixedswitched.toUpper();
                                enableddisabled.toUpper();
                                if( currentCapBank->getBankSize() != kvarrating ||
                                    tempOperationalState != fixedswitched ||
                                    (bool)currentCapBank->getDisableFlag() != (enableddisabled == m3iAMFMDisabledString) )
                                {
                                    currentCapBank->setBankSize(kvarrating);
                                    RWCString tempFixedOperationalStateString = CtiCCCapBank::FixedOperationalState;
                                    tempFixedOperationalStateString.toUpper();
                                    currentCapBank->setOperationalState(fixedswitched==tempFixedOperationalStateString?CtiCCCapBank::FixedOperationalState:CtiCCCapBank::SwitchedOperationalState);
                                    currentCapBank->setDisableFlag(enableddisabled == m3iAMFMDisabledString);
                                    UpdateCapBankInDB(currentCapBank);
                                    currentCCSubstationBus->setBusUpdatedFlag(TRUE);

                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - M3I change, 'Feeder Reconfigure' or 'AIM Import' PAO Id: "
                                             << currentCapBank->getPAOId() << ", name: "
                                             << currentCapBank->getPAOName()
                                             << ", was updated in database with bank size: "
                                             << currentCapBank->getBankSize() << ", operational state: "
                                             << currentCapBank->getOperationalState() << ", and was "
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
    capBankMovedToDifferentFeeder

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capBankMovedToDifferentFeeder(CtiCCFeeder* oldFeeder, CtiCCCapBank* movedCapBank, LONG feederid, LONG capswitchingorder)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWSortedVector& oldFeederCapBanks = oldFeeder->getCCCapBanks();

    BOOL found = FALSE;
    for(ULONG i=0;i<_ccSubstationBuses->entries();i++)
    {
        CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];

        RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
        if( ccFeeders.entries() > 0 )
        {
            for(ULONG j=0;j<ccFeeders.entries();j++)
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
                            for(ULONG k=0;k<newFeederCapBanks.entries();k++)
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    currentFeeder->getCCCapBanks().remove(currentCapBank);
    currentCapBank->setControlOrder(capswitchingorder);
    currentFeeder->getCCCapBanks().insert(currentCapBank);
    UpdateFeederBankListInDB(currentFeeder);

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - M3I change, 'Feeder Reconfigure' or 'AIM Import' PAO Id: "
             << currentCapBank->getPAOId() << ", name: "
             << currentCapBank->getPAOName()
             << ", was rearranged in feeder PAO Id: "
             << currentFeeder->getPAOId() << ", name: "
             << currentFeeder->getPAOName() << ", with order: "
             << currentCapBank->getControlOrder() << endl;
    }
}

/*---------------------------------------------------------------------------
    capOutOfServiceM3IAMFM

    .
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::capOutOfServiceM3IAMFM(LONG feederid, LONG capid, RWCString enableddisabled, RWCString fixedswitched)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL found = FALSE;
    if( _ccSubstationBuses->entries() > 0 )
    {
        for(ULONG i=0;i<_ccSubstationBuses->entries();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];

            RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.entries() > 0 )
            {
                for(ULONG j=0;j<ccFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];

                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                    if( ccCapBanks.entries() > 0 )
                    {
                        for(ULONG k=0;k<ccCapBanks.entries();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                            if( currentCapBank->getMapLocationId() == capid )
                            {
                                enableddisabled.toUpper();
                                if( (bool)currentCapBank->getDisableFlag() != (enableddisabled == m3iAMFMDisabledString) )
                                {
                                    currentCapBank->setDisableFlag(enableddisabled == m3iAMFMDisabledString);
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
void CtiCCSubstationBusStore::feederOutOfServiceM3IAMFM(LONG feederid, RWCString fixedswitched, RWCString enableddisabled)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    BOOL found = FALSE;
    if( _ccSubstationBuses->entries() > 0 )
    {
        for(ULONG i=0;i<_ccSubstationBuses->entries();i++)
        {
            CtiCCSubstationBus* currentCCSubstationBus = (CtiCCSubstationBus*)(*_ccSubstationBuses)[i];

            RWOrdered& ccFeeders = currentCCSubstationBus->getCCFeeders();
            if( ccFeeders.entries() > 0 )
            {
                for(ULONG j=0;j<ccFeeders.entries();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];

                    if( currentFeeder->getMapLocationId() == feederid )
                    {
                        enableddisabled.toUpper();
                        if( (bool)currentFeeder->getDisableFlag() != (enableddisabled == m3iAMFMDisabledString) )
                        {
                            currentFeeder->setDisableFlag(enableddisabled == m3iAMFMDisabledString);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    dumpAllDynamicData();
    _ccSubstationBuses->clearAndDestroy();
    delete _ccSubstationBuses;

    //if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - done shutting down the cc substation store." << endl;
    }
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
        if( _CC_DEBUG )
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

    time_t start = time(NULL);

    RWDBDateTime currenttime = RWDBDateTime();
    ULONG tempsum = currenttime.seconds()+refreshrate;
    RWDBDateTime nextDatabaseRefresh = RWDBDateTime(RWTime(tempsum));

    while(TRUE)
    {
        rwRunnable().serviceCancellation();

        if ( RWDBDateTime() >= nextDatabaseRefresh )
        {
            //if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Restoring substation list from the database" << endl;
            }

            dumpAllDynamicData();
            setValid(FALSE);

            currenttime = RWDBDateTime();
            tempsum = currenttime.seconds()+refreshrate;
            nextDatabaseRefresh = RWDBDateTime(RWTime(tempsum));
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
        if( _CC_DEBUG )
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

    if( amfm_interface == m3iAMFMInterfaceString )
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
            if( _CC_DEBUG )
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
            if( _CC_DEBUG )
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
            if( _CC_DEBUG )
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
            if( _CC_DEBUG )
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
            if( _CC_DEBUG )
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
                    if( _CC_DEBUG )
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
BOOL CtiCCSubstationBusStore::isValid() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return _isvalid;
}

/*---------------------------------------------------------------------------
    setValid

    Sets the _isvalid flag
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setValid(BOOL valid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _isvalid = valid;
}

/*---------------------------------------------------------------------------
    getReregisterForPoints

    Gets _reregisterforpoints
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBusStore::getReregisterForPoints() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return _reregisterforpoints;
}

/*---------------------------------------------------------------------------
    setReregisterForPoints

    Sets _reregisterforpoints
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setReregisterForPoints(BOOL reregister)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _reregisterforpoints = reregister;
}

/*---------------------------------------------------------------------------
    getReloadFromAMFMSystemFlag

    Gets _reloadfromamfmsystemflag
---------------------------------------------------------------------------*/
BOOL CtiCCSubstationBusStore::getReloadFromAMFMSystemFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return _reloadfromamfmsystemflag;
}

/*---------------------------------------------------------------------------
    setReloadFromAMFMSystemFlag

    Sets _reloadfromamfmsystemflag
---------------------------------------------------------------------------*/
void CtiCCSubstationBusStore::setReloadFromAMFMSystemFlag(BOOL reload)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _reloadfromamfmsystemflag = reload;
}

/*---------------------------------------------------------------------------
    UpdateBusDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the substation bus.
---------------------------------------------------------------------------*/
bool CtiCCSubstationBusStore::UpdateBusDisableFlagInDB(CtiCCSubstationBus* bus)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == capbank->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( RWCString((capbank->getDisableFlag()?'Y':'N')) );

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable ccFeederBankListTable = getDatabase().table("ccfeederbanklist");
        RWDBDeleter deleter = ccFeederBankListTable.deleter();

        deleter.where( ccFeederBankListTable["feederid"] == feeder->getPAOId() );

        deleter.execute( conn );


        RWDBInserter inserter = ccFeederBankListTable.inserter();

        RWOrdered& ccCapBanks = feeder->getCCCapBanks();
        for(ULONG i=0;i<ccCapBanks.entries();i++)
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

const RWCString CtiCCSubstationBusStore::feederReconfigureM3IAMFMChangeTypeString = "FEEDER RECONFIGURE";
const RWCString CtiCCSubstationBusStore::capOutOfServiceM3IAMFMChangeTypeString = "CAP OUT OF SERVICE";
const RWCString CtiCCSubstationBusStore::feederOutOfServiceM3IAMFMChangeTypeString = "FEEDER OUT OF SERVICE";
const RWCString CtiCCSubstationBusStore::aimImportM3IAMFMChangeTypeString = "AIM IMPORT";

const RWCString CtiCCSubstationBusStore::m3iAMFMEnabledString = "ENABLED";
const RWCString CtiCCSubstationBusStore::m3iAMFMDisabledString = "DISABLED";
const RWCString CtiCCSubstationBusStore::m3iAMFMFixedString = "FIXED";
const RWCString CtiCCSubstationBusStore::m3iAMFMSwitchedString = "SWITCHED";

const RWCString CtiCCSubstationBusStore::CAP_CONTROL_DBCHANGE_MSG_SOURCE = "CAP_CONTROL_SERVER";

