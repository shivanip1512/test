/*---------------------------------------------------------------------------
        Filename:  lmcontrolareastore.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMControlAreaStore
                        CtiLMControlAreaStore maintains a pool of
                        CtiLMControlAreas.

        Initial Date:  2/8/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <rw/thr/thrfunc.h>

#include "lmcontrolareastore.h"
#include "lmcurtailcustomer.h"
#include "lmenergyexchangecustomer.h"
#include "lmenergyexchangecustomerreply.h"
#include "lmenergyexchangeofferrevision.h"
#include "lmenergyexchangeoffer.h"
#include "lmenergyexchangehourlyoffer.h"
#include "lmenergyexchangehourlycustomer.h"
#include "lmprogramcurtailment.h"
#include "lmprogramdirect.h"
#include "lmprogramenergyexchange.h"
#include "lmgroupversacom.h"
#include "lmgroupemetcon.h"
#include "lmgroupripple.h"
#include "lmgrouppoint.h"
#include "lmcontrolareatrigger.h"
#include "lmprogramdirectgear.h"
#include "lmprogramcontrolwindow.h"
#include "resolvers.h"
#include "desolvers.h"
#include "devicetypes.h"
#include "dbaccess.h"
#include "ctibase.h"
#include "logger.h"
#include "configparms.h"
#include "msg_dbchg.h"
#include "loadmanager.h"

extern BOOL _LM_DEBUG;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMControlAreaStore::CtiLMControlAreaStore() : _doreset(TRUE), _isvalid(FALSE), _reregisterforpoints(TRUE)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlAreas = new RWOrdered();
    //Start the reset thread
    RWThreadFunction func = rwMakeThreadFunction( *this, &CtiLMControlAreaStore::doResetThr );
    _resetthr = func;
    func.start();
}

/*--------------------------------------------------------------------------
    Destrutor
-----------------------------------------------------------------------------*/
CtiLMControlAreaStore::~CtiLMControlAreaStore()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    if( _resetthr.isValid() )
    {
        _resetthr.requestCancellation();
    }

    shutdown();
}

/*---------------------------------------------------------------------------
    getControlAreas

    Returns a RWOrdered of CtiLMControlAreas
---------------------------------------------------------------------------*/
RWOrdered* CtiLMControlAreaStore::getControlAreas()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( _doreset || !_isvalid )
    {
        reset();

        if( isValid() )
            _doreset = FALSE;
    }

    return _controlAreas;
}

/*---------------------------------------------------------------------------
    dumpAllDynamicData

    Writes out the dynamic information for each of the control areas.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::dumpAllDynamicData()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( _controlAreas->entries() > 0 )
    {
        for(ULONG i=0;i<_controlAreas->entries();i++)
        {
            CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];
            currentLMControlArea->dumpDynamicData();

            RWOrdered& lmControlAreaTriggers = currentLMControlArea->getLMControlAreaTriggers();
            if( lmControlAreaTriggers.entries() > 0 )
            {
                for(ULONG x=0;x<lmControlAreaTriggers.entries();x++)
                {
                    CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)lmControlAreaTriggers[x];
                    currentLMControlAreaTrigger->dumpDynamicData();
                }
            }

            RWOrdered& lmPrograms = currentLMControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(ULONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    currentLMProgramBase->dumpDynamicData();

                    if( currentLMProgramBase->getPAOType() ==  TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirect* currentLMProgramDirect = (CtiLMProgramDirect*)currentLMProgramBase;
                        currentLMProgramDirect->dumpDynamicData();

                        RWOrdered& lmGroups = currentLMProgramDirect->getLMProgramDirectGroups();
                        if( lmGroups.entries() > 0 )
                        {
                            for(ULONG k=0;k<lmGroups.entries();k++)
                            {
                                CtiLMGroupBase* currentLMGroupBase = (CtiLMGroupBase*)lmGroups[k];
                                currentLMGroupBase->dumpDynamicData();
                            }
                        }
                    }
                    else if( currentLMProgramBase->getPAOType() ==  TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CtiLMProgramCurtailment* currentLMProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgramBase;

                        if( currentLMProgramCurtailment->getManualControlReceivedFlag() )
                        {
                            currentLMProgramCurtailment->dumpDynamicData();

                            RWOrdered& lmCurtailCustomers = currentLMProgramCurtailment->getLMProgramCurtailmentCustomers();
                            if( lmCurtailCustomers.entries() > 0 )
                            {
                                for(ULONG k=0;k<lmCurtailCustomers.entries();k++)
                                {
                                    CtiLMCurtailCustomer* currentLMCurtailCustomer = (CtiLMCurtailCustomer*)lmCurtailCustomers[k];
                                    currentLMCurtailCustomer->dumpDynamicData();
                                }
                            }
                        }
                    }
                    else
                    {
                    }
                }
            }
        }
    }
}

/*---------------------------------------------------------------------------
    reset

    Reset attempts to read in all the control areas from the database.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::reset()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Obtaining connection to the database..." << endl;
        dout << RWTime() << " - Reseting control areas from database..." << endl;
    }

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();
        {

            if( conn.isValid() )
            {
                if( _controlAreas->entries() > 0 )
                {
                    _controlAreas->clearAndDestroy();
                }

                RWDBDateTime currentDateTime;
                RWDBDatabase db = getDatabase();
                RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                RWDBTable lmControlAreaTable = db.table("lmcontrolarea");
                RWDBTable dynamicLMControlAreaTable = db.table("dynamiclmcontrolarea");

                RWDBSelector selector = db.selector();
                selector << yukonPAObjectTable["paobjectid"]
                << yukonPAObjectTable["category"]
                << yukonPAObjectTable["paoclass"]
                << yukonPAObjectTable["paoname"]
                << yukonPAObjectTable["type"]
                << yukonPAObjectTable["description"]
                << yukonPAObjectTable["disableflag"]
                << lmControlAreaTable["defoperationalstate"]
                << lmControlAreaTable["controlinterval"]
                << lmControlAreaTable["minresponsetime"]
                << lmControlAreaTable["defdailystarttime"]
                << lmControlAreaTable["defdailystoptime"]
                << lmControlAreaTable["requirealltriggersactiveflag"]
                << dynamicLMControlAreaTable["nextchecktime"]
                << dynamicLMControlAreaTable["newpointdatareceivedflag"]
                << dynamicLMControlAreaTable["updatedflag"]
                << dynamicLMControlAreaTable["controlareastate"]
                << dynamicLMControlAreaTable["currentpriority"]
                << dynamicLMControlAreaTable["currentdailystarttime"]
                << dynamicLMControlAreaTable["currentdailystoptime"]
                << dynamicLMControlAreaTable["timestamp"];

                selector.from(yukonPAObjectTable);
                selector.from(lmControlAreaTable);
                selector.from(dynamicLMControlAreaTable);

                selector.where(yukonPAObjectTable["paobjectid"]==lmControlAreaTable["deviceid"] && //will be paobjectid
                               lmControlAreaTable["deviceid"].leftOuterJoin(dynamicLMControlAreaTable["deviceid"]));

                selector.orderBy(yukonPAObjectTable["description"]);
                selector.orderBy(yukonPAObjectTable["paoname"]);

                /*if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << selector.asString().data() << endl;
                }*/

                RWDBReader rdr = selector.reader(conn);

                while( rdr() )
                {
                    CtiLMControlArea* newControlArea = new CtiLMControlArea(rdr);
                    _controlAreas->insert( newControlArea );
                }

                /************************************************************
                ********    Loading Triggers for each Control Area   ********
                ************************************************************/
                for(UINT i=0;i<_controlAreas->entries();i++)
                {
                    CtiLMControlArea* currentArea = (CtiLMControlArea*)((*_controlAreas)[i]);
                    currentArea->setUpdatedFlag(TRUE);
                    //HACK: make sure all the control areas are sent on a DB reload
                    //only redundant on the first load from the DB

                    RWDBTable lmControlAreaTriggerTable = db.table("lmcontrolareatrigger");
                    RWDBTable dynamicLMControlAreaTriggerTable = db.table("dynamiclmcontrolareatrigger");

                    RWDBSelector selector = db.selector();
                    selector << lmControlAreaTriggerTable["deviceid"]//will be paobjectid
                    << lmControlAreaTriggerTable["triggernumber"]
                    << lmControlAreaTriggerTable["triggertype"]
                    << lmControlAreaTriggerTable["pointid"]
                    << lmControlAreaTriggerTable["normalstate"]
                    << lmControlAreaTriggerTable["threshold"]
                    << lmControlAreaTriggerTable["projectiontype"]
                    << lmControlAreaTriggerTable["projectionpoints"]
                    << lmControlAreaTriggerTable["projectaheadduration"]
                    << lmControlAreaTriggerTable["thresholdkickpercent"]
                    << lmControlAreaTriggerTable["minrestoreoffset"]
                    << lmControlAreaTriggerTable["peakpointid"]
                    << dynamicLMControlAreaTriggerTable["pointvalue"]
                    << dynamicLMControlAreaTriggerTable["lastpointvaluetimestamp"]
                    << dynamicLMControlAreaTriggerTable["peakpointvalue"]
                    << dynamicLMControlAreaTriggerTable["lastpeakpointvaluetimestamp"];

                    selector.from(lmControlAreaTriggerTable);
                    selector.from(dynamicLMControlAreaTriggerTable);

                    selector.where( lmControlAreaTriggerTable["deviceid"]==currentArea->getPAOId() &&
                                    lmControlAreaTriggerTable["deviceid"].leftOuterJoin(dynamicLMControlAreaTriggerTable["deviceid"]) &&
                                    lmControlAreaTriggerTable["triggernumber"].leftOuterJoin(dynamicLMControlAreaTriggerTable["triggernumber"]) );//will be paobjectid

                    selector.orderBy(lmControlAreaTriggerTable["triggernumber"]);

                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }*/

                    RWDBReader rdr = selector.reader(conn);
                    while( rdr() )
                    {
                        CtiLMControlAreaTrigger* trigger = new CtiLMControlAreaTrigger(rdr);
                        currentArea->getLMControlAreaTriggers().insert(trigger);
                    }
                }

                /************************************************************
                *******   Loading Program List for each Control Area  *******
                ************************************************************/
                for(UINT x=0;x<_controlAreas->entries();x++)
                {
                    CtiLMControlArea* currentArea = (CtiLMControlArea*)((*_controlAreas)[x]);

                    RWDBTable lmControlAreaProgramTable = db.table("lmcontrolareaprogram");
                    RWDBTable lmProgramTable = db.table("lmprogram");
                    RWDBTable dynamicLMProgramTable = db.table("dynamiclmprogram");

                    RWDBSelector selector = db.selector();
                    selector << yukonPAObjectTable["paobjectid"]
                    << yukonPAObjectTable["category"]
                    << yukonPAObjectTable["paoclass"]
                    << yukonPAObjectTable["paoname"]
                    << yukonPAObjectTable["type"]
                    << yukonPAObjectTable["description"]
                    << yukonPAObjectTable["disableflag"]
                    << lmControlAreaProgramTable["userorder"]
                    << lmControlAreaProgramTable["stoporder"]
                    << lmControlAreaProgramTable["defaultpriority"]
                    << lmProgramTable["controltype"]
                    << lmProgramTable["availableseasons"]
                    << lmProgramTable["availableweekdays"]
                    << lmProgramTable["maxhoursdaily"]
                    << lmProgramTable["maxhoursmonthly"]
                    << lmProgramTable["maxhoursseasonal"]
                    << lmProgramTable["maxhoursannually"]
                    << lmProgramTable["minactivatetime"]
                    << lmProgramTable["minrestarttime"]
                    << dynamicLMProgramTable["programstate"]
                    << dynamicLMProgramTable["reductiontotal"]
                    << dynamicLMProgramTable["startedcontrolling"]
                    << dynamicLMProgramTable["lastcontrolsent"]
                    << dynamicLMProgramTable["manualcontrolreceivedflag"]
                    << dynamicLMProgramTable["timestamp"];

                    selector.from(yukonPAObjectTable);
                    selector.from(lmControlAreaProgramTable);
                    selector.from(lmProgramTable);
                    selector.from(dynamicLMProgramTable);

                    selector.where( lmControlAreaProgramTable["deviceid"]==currentArea->getPAOId() &&//will be paobjectid
                                    yukonPAObjectTable["paobjectid"]==lmControlAreaProgramTable["lmprogramdeviceid"] &&//will be paobjectid
                                    lmProgramTable["deviceid"]==lmControlAreaProgramTable["lmprogramdeviceid"] &&
                                    lmProgramTable["deviceid"].leftOuterJoin(dynamicLMProgramTable["deviceid"]) );//will be paobjectid

                    selector.orderBy(lmControlAreaProgramTable["defaultpriority"]);
                    selector.orderBy(lmControlAreaProgramTable["userorder"]);
                    selector.orderByDescending(lmControlAreaProgramTable["stoporder"]);

                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }*/

                    RWOrdered& lmProgramList = currentArea->getLMPrograms();
                    RWDBReader rdr = selector.reader(conn);
                    while( rdr() )
                    {
                        RWCString tempPAOCategory;
                        RWCString tempPAOType;
                        rdr["category"] >> tempPAOCategory;
                        rdr["type"] >> tempPAOType;
                        if( resolvePAOType(tempPAOCategory,tempPAOType) == TYPE_LMPROGRAM_DIRECT )
                        {
                            lmProgramList.insert( new CtiLMProgramDirect(rdr) );
                        }
                        else if( resolvePAOType(tempPAOCategory,tempPAOType) == TYPE_LMPROGRAM_CURTAILMENT )
                        {
                            lmProgramList.insert( new CtiLMProgramCurtailment(rdr) );
                        }
                        else if( resolvePAOType(tempPAOCategory,tempPAOType) == TYPE_LMPROGRAM_ENERGYEXCHANGE )
                        {
                            lmProgramList.insert( new CtiLMProgramEnergyExchange(rdr) );
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - PAO Category = " << tempPAOCategory << "; PAO Type = " << tempPAOType << " not supported yet.  In: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }

                    for(ULONG y=0;y<lmProgramList.entries();y++)
                    {
                        CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmProgramList[y];

                        if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                        {
                            CtiLMProgramDirect* currentLMProgramDirect = (CtiLMProgramDirect*)currentLMProgramBase;

                            {
                                RWDBTable lmProgramDirectTable = db.table("lmprogramdirect");
                                RWDBTable dynamicLMProgramDirectTable = db.table("dynamiclmprogramdirect");

                                RWDBSelector selector = db.selector();
                                selector << dynamicLMProgramDirectTable["currentgearnumber"]
                                << dynamicLMProgramDirectTable["lastgroupcontrolled"]
                                << dynamicLMProgramDirectTable["starttime"]
                                << dynamicLMProgramDirectTable["stoptime"]
                                << dynamicLMProgramDirectTable["timestamp"];

                                selector.from(lmProgramDirectTable);
                                selector.from(dynamicLMProgramDirectTable);

                                selector.where( lmProgramDirectTable["deviceid"]==currentLMProgramDirect->getPAOId() &&
                                                lmProgramDirectTable["deviceid"].leftOuterJoin(dynamicLMProgramDirectTable["deviceid"]) );//will be paobjectid

                                /*if( _LM_DEBUG )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }*/

                                RWDBReader rdr = selector.reader(conn);
                                if( rdr() )
                                {
                                    currentLMProgramDirect->restoreDirectSpecificDatabaseEntries(rdr);
                                }
                            }

                            {
                                RWDBTable lmProgramDirectGearTable = db.table("lmprogramdirectgear");

                                RWDBSelector selector = db.selector();
                                selector << lmProgramDirectGearTable["deviceid"]//will be paobjectid
                                << lmProgramDirectGearTable["gearname"]
                                << lmProgramDirectGearTable["gearnumber"]
                                << lmProgramDirectGearTable["controlmethod"]
                                << lmProgramDirectGearTable["methodrate"]
                                << lmProgramDirectGearTable["methodperiod"]
                                << lmProgramDirectGearTable["methodratecount"]
                                << lmProgramDirectGearTable["cyclerefreshrate"]
                                << lmProgramDirectGearTable["methodstoptype"]
                                << lmProgramDirectGearTable["changecondition"]
                                << lmProgramDirectGearTable["changeduration"]
                                << lmProgramDirectGearTable["changepriority"]
                                << lmProgramDirectGearTable["changetriggernumber"]
                                << lmProgramDirectGearTable["changetriggeroffset"]
                                << lmProgramDirectGearTable["percentreduction"]
                                << lmProgramDirectGearTable["groupselectionmethod"]
                                << lmProgramDirectGearTable["methodoptiontype"]
                                << lmProgramDirectGearTable["methodoptionmax"];

                                selector.from(lmProgramDirectGearTable);

                                selector.where( lmProgramDirectGearTable["deviceid"]==currentLMProgramDirect->getPAOId() );//will be paobjectid

                                selector.orderBy( lmProgramDirectGearTable["gearnumber"] );

                                /*if( _LM_DEBUG )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }*/

                                RWOrdered& lmProgramDirectGearList = currentLMProgramDirect->getLMProgramDirectGears();
                                RWDBReader rdr = selector.reader(conn);
                                while( rdr() )
                                {
                                    lmProgramDirectGearList.insert( new CtiLMProgramDirectGear(rdr) );
                                }
                            }

                            {
                                RWDBTable lmGroupMacroExpanderView = db.table("lmgroupmacroexpander_view");
                                RWDBTable dynamicLMGroupTable = db.table("dynamiclmgroup");

                                RWDBSelector selector = db.selector();
                                selector.distinct();
                                selector << lmGroupMacroExpanderView["paobjectid"]
                                << lmGroupMacroExpanderView["category"]
                                << lmGroupMacroExpanderView["paoclass"]
                                << lmGroupMacroExpanderView["paoname"]
                                << lmGroupMacroExpanderView["type"]
                                << lmGroupMacroExpanderView["description"]
                                << lmGroupMacroExpanderView["disableflag"]
                                << lmGroupMacroExpanderView["alarminhibit"]
                                << lmGroupMacroExpanderView["controlinhibit"]
                                << lmGroupMacroExpanderView["kwcapacity"]
                                << lmGroupMacroExpanderView["grouporder"]
                                << lmGroupMacroExpanderView["childorder"]
                                << dynamicLMGroupTable["groupcontrolstate"]
                                << dynamicLMGroupTable["currenthoursdaily"]
                                << dynamicLMGroupTable["currenthoursmonthly"]
                                << dynamicLMGroupTable["currenthoursseasonal"]
                                << dynamicLMGroupTable["currenthoursannually"]
                                << dynamicLMGroupTable["lastcontrolsent"]
                                << dynamicLMGroupTable["timestamp"];

                                selector.from(lmGroupMacroExpanderView);
                                selector.from(dynamicLMGroupTable);

                                selector.where( ( ( lmGroupMacroExpanderView["childid"].isNull() &&
                                                    lmGroupMacroExpanderView["paobjectid"]==lmGroupMacroExpanderView["lmgroupdeviceid"] ) ||
                                                  ( !lmGroupMacroExpanderView["childid"].isNull() &&
                                                    lmGroupMacroExpanderView["paobjectid"]==lmGroupMacroExpanderView["childid"] ) ) &&
                                                lmGroupMacroExpanderView["deviceid"]==currentLMProgramDirect->getPAOId() &&
                                                lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(dynamicLMGroupTable["deviceid"]) );

                                selector.orderBy( lmGroupMacroExpanderView["grouporder"] );
                                selector.orderBy( lmGroupMacroExpanderView["childorder"] );

                                /*if( _LM_DEBUG )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }*/

                                RWOrdered& lmProgramDirectGroupList = currentLMProgramDirect->getLMProgramDirectGroups();
                                RWDBReader rdr = selector.reader(conn);
                                while( rdr() )
                                {
                                    RWCString tempPAOCategory;
                                    RWCString tempPAOType;
                                    rdr["category"] >> tempPAOCategory;
                                    rdr["type"] >> tempPAOType;
                                    if( resolvePAOType(tempPAOCategory,tempPAOType) == TYPE_LMGROUP_VERSACOM )
                                    {
                                        lmProgramDirectGroupList.insert(new CtiLMGroupVersacom(rdr));
                                    }
                                    else if( resolvePAOType(tempPAOCategory,tempPAOType) == TYPE_LMGROUP_EMETCON )
                                    {
                                        lmProgramDirectGroupList.insert(new CtiLMGroupEmetcon(rdr));
                                    }
                                    else if( resolvePAOType(tempPAOCategory,tempPAOType) == TYPE_LMGROUP_RIPPLE )
                                    {
                                        lmProgramDirectGroupList.insert(new CtiLMGroupRipple(rdr));
                                    }
                                    else if( resolvePAOType(tempPAOCategory,tempPAOType) == TYPE_LMGROUP_POINT )
                                    {
                                        lmProgramDirectGroupList.insert(new CtiLMGroupPoint(rdr));
                                    }
                                    else
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - Group device type = " << tempPAOType << " not supported yet.  In: " << __FILE__ << " at:" << __LINE__ << endl;
                                    }
                                }

                                for(int z=0;z<lmProgramDirectGroupList.entries();z++)
                                {
                                    CtiLMGroupBase* currentLMGroupBase = (CtiLMGroupBase*)lmProgramDirectGroupList[z];

                                    {
                                        if( currentLMGroupBase->getPAOType() == TYPE_LMGROUP_VERSACOM )
                                        {
                                            RWDBTable lmGroupVersacomTable = db.table("lmgroupversacom");

                                            RWDBSelector selector = db.selector();
                                            selector << lmGroupVersacomTable["utilityaddress"]
                                            << lmGroupVersacomTable["sectionaddress"]
                                            << lmGroupVersacomTable["classaddress"]
                                            << lmGroupVersacomTable["divisionaddress"]
                                            << lmGroupVersacomTable["addressusage"]
                                            << lmGroupVersacomTable["relayusage"]
                                            << lmGroupVersacomTable["routeid"];

                                            selector.from(lmGroupVersacomTable);

                                            selector.where( lmGroupVersacomTable["deviceid"]==currentLMGroupBase->getPAOId() );//will be paobjectid

                                            /*if( _LM_DEBUG )
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - " << selector.asString().data() << endl;
                                            }*/

                                            RWDBReader rdr = selector.reader(conn);
                                            if( rdr() )
                                            {
                                                ((CtiLMGroupVersacom*)currentLMGroupBase)->restoreVersacomSpecificDatabaseEntries(rdr);
                                            }
                                        }
                                        else if( currentLMGroupBase->getPAOType() == TYPE_LMGROUP_EMETCON )
                                        {
                                            RWDBTable lmGroupEmetconTable = db.table("lmgroupemetcon");

                                            RWDBSelector selector = db.selector();
                                            selector << lmGroupEmetconTable["goldaddress"]
                                            << lmGroupEmetconTable["silveraddress"]
                                            << lmGroupEmetconTable["addressusage"]
                                            << lmGroupEmetconTable["relayusage"]
                                            << lmGroupEmetconTable["routeid"];

                                            selector.from(lmGroupEmetconTable);

                                            selector.where(lmGroupEmetconTable["deviceid"]==currentLMGroupBase->getPAOId() );//will be paobjectid

                                            /*if( _LM_DEBUG )
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - " << selector.asString().data() << endl;
                                            }*/

                                            RWDBReader rdr = selector.reader(conn);
                                            if( rdr() )
                                            {
                                                ((CtiLMGroupEmetcon*)currentLMGroupBase)->restoreEmetconSpecificDatabaseEntries(rdr);
                                            }
                                        }
                                        else if( currentLMGroupBase->getPAOType() == TYPE_LMGROUP_RIPPLE )
                                        {
                                            RWDBTable lmGroupRippleTable = db.table("lmgroupripple");

                                            RWDBSelector selector = db.selector();
                                            selector << lmGroupRippleTable["routeid"]
                                            << lmGroupRippleTable["shedtime"]
                                            << lmGroupRippleTable["controlvalue"]
                                            << lmGroupRippleTable["restorevalue"];

                                            selector.from(lmGroupRippleTable);

                                            selector.where(lmGroupRippleTable["deviceid"]==currentLMGroupBase->getPAOId() );//will be paobjectid

                                            /*if( _LM_DEBUG )
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - " << selector.asString().data() << endl;
                                            }*/

                                            RWDBReader rdr = selector.reader(conn);
                                            if( rdr() )
                                            {
                                                ((CtiLMGroupRipple*)currentLMGroupBase)->restoreRippleSpecificDatabaseEntries(rdr);
                                            }
                                        }
                                        else if( currentLMGroupBase->getPAOType() == TYPE_LMGROUP_POINT )
                                        {
                                            RWDBTable lmGroupPointTable = db.table("lmgrouppoint");

                                            RWDBSelector selector = db.selector();
                                            selector << lmGroupPointTable["deviceidusage"]
                                            << lmGroupPointTable["pointidusage"]
                                            << lmGroupPointTable["startcontrolrawstate"];

                                            selector.from(lmGroupPointTable);

                                            selector.where(lmGroupPointTable["deviceid"]==currentLMGroupBase->getPAOId() );//will be paobjectid

                                            /*if( _LM_DEBUG )
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - " << selector.asString().data() << endl;
                                            }*/

                                            RWDBReader rdr = selector.reader(conn);
                                            if( rdr() )
                                            {
                                                ((CtiLMGroupPoint*)currentLMGroupBase)->restorePointSpecificDatabaseEntries(rdr);
                                            }
                                        }
                                        else
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - Group device type = " << currentLMGroupBase->getPAOType() << " not supported yet.  In: " << __FILE__ << " at:" << __LINE__ << endl;
                                        }
                                    }
                                }
                            }
                        }
                        else if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_CURTAILMENT )
                        {
                            CtiLMProgramCurtailment* currentLMProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgramBase;

                            {
                                RWDBTable lmProgramCurtailmentTable = db.table("lmprogramcurtailment");

                                RWDBSelector selector = db.selector();
                                selector << lmProgramCurtailmentTable["minnotifytime"]
                                << lmProgramCurtailmentTable["heading"]
                                << lmProgramCurtailmentTable["messageheader"]
                                << lmProgramCurtailmentTable["messagefooter"]
                                << lmProgramCurtailmentTable["acktimelimit"]
                                << lmProgramCurtailmentTable["canceledmsg"]
                                << lmProgramCurtailmentTable["stoppedearlymsg"];

                                selector.from(lmProgramCurtailmentTable);

                                selector.where(lmProgramCurtailmentTable["deviceid"]==currentLMProgramCurtailment->getPAOId());//will be paobjectid

                                /*if( _LM_DEBUG )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }*/

                                RWDBReader rdr = selector.reader(conn);
                                if( rdr() )
                                {
                                    currentLMProgramCurtailment->restoreCurtailmentSpecificDatabaseEntries(rdr);
                                    if( currentLMProgramCurtailment->getManualControlReceivedFlag() )
                                    {
                                        currentLMProgramCurtailment->restoreDynamicData(rdr);
                                    }
                                }
                            }

                            {
                                RWDBTable lmProgramCurtailCustomerListTable = db.table("lmprogramcurtailcustomerlist");
                                RWDBTable ciCustomerBaseTable = db.table("cicustomerbase");

                                RWDBSelector selector = db.selector();
                                selector << yukonPAObjectTable["paobjectid"]
                                << yukonPAObjectTable["category"]
                                << yukonPAObjectTable["paoclass"]
                                << yukonPAObjectTable["paoname"]
                                << yukonPAObjectTable["type"]
                                << yukonPAObjectTable["description"]
                                << yukonPAObjectTable["disableflag"]
                                << lmProgramCurtailCustomerListTable["customerorder"]
                                << lmProgramCurtailCustomerListTable["requireack"]
                                << ciCustomerBaseTable["custfpl"]
                                << ciCustomerBaseTable["custtimezone"];

                                selector.from(yukonPAObjectTable);
                                selector.from(lmProgramCurtailCustomerListTable);
                                selector.from(ciCustomerBaseTable);

                                selector.where( lmProgramCurtailCustomerListTable["deviceid"]==currentLMProgramCurtailment->getPAOId() &&//will be paobjectid
                                                yukonPAObjectTable["paobjectid"]==lmProgramCurtailCustomerListTable["lmcustomerdeviceid"] &&//will be paobjectid
                                                ciCustomerBaseTable["deviceid"]==yukonPAObjectTable["paobjectid"] );//will be paobjectid

                                selector.orderBy( lmProgramCurtailCustomerListTable["customerorder"] );

                                /*if( _LM_DEBUG )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }*/

                                RWOrdered& lmProgramCurtailmentCustomers = currentLMProgramCurtailment->getLMProgramCurtailmentCustomers();
                                RWDBReader rdr = selector.reader(conn);
                                while( rdr() )
                                {
                                    lmProgramCurtailmentCustomers.insert(new CtiLMCurtailCustomer(rdr));
                                }

                                if( currentLMProgramCurtailment->getManualControlReceivedFlag() && lmProgramCurtailmentCustomers.entries() > 0 )
                                {
                                    for(ULONG temp=0;temp<lmProgramCurtailmentCustomers.entries();temp++)
                                    {
                                        ((CtiLMCurtailCustomer*)lmProgramCurtailmentCustomers[temp])->restoreDynamicData(rdr);
                                    }
                                }
                            }
                        }
                        else if( currentLMProgramBase->getPAOType() == TYPE_LMPROGRAM_ENERGYEXCHANGE )
                        {
                            CtiLMProgramEnergyExchange* currentLMProgramEnergyExchange = (CtiLMProgramEnergyExchange*)currentLMProgramBase;

                            {
                                RWDBTable lmProgramEnergyExchangeTable = db.table("lmprogramenergyexchange");

                                RWDBSelector selector = db.selector();
                                selector << lmProgramEnergyExchangeTable["minnotifytime"]
                                << lmProgramEnergyExchangeTable["heading"]
                                << lmProgramEnergyExchangeTable["messageheader"]
                                << lmProgramEnergyExchangeTable["messagefooter"]
                                << lmProgramEnergyExchangeTable["canceledmsg"]
                                << lmProgramEnergyExchangeTable["stoppedearlymsg"];

                                selector.from(lmProgramEnergyExchangeTable);

                                selector.where(lmProgramEnergyExchangeTable["deviceid"]==currentLMProgramEnergyExchange->getPAOId());//will be paobjectid

                                /*if( _LM_DEBUG )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }*/

                                RWDBReader rdr = selector.reader(conn);
                                if( rdr() )
                                {
                                    currentLMProgramEnergyExchange->restoreEnergyExchangeSpecificDatabaseEntries(rdr);
                                    if( currentLMProgramEnergyExchange->getManualControlReceivedFlag() )
                                    {
                                        RWDBDateTime currentDateTime;
                                        RWDBDateTime compareDateTime = RWDBDateTime(currentDateTime.year(),currentDateTime.month(),currentDateTime.dayOfMonth(),0,0,0,0);
                                        RWDBTable lmEnergyExchangeProgramOfferTable = db.table("lmenergyexchangeprogramoffer");

                                        RWDBSelector selector = db.selector();
                                        selector << lmEnergyExchangeProgramOfferTable["deviceid"]//will be paobjectid
                                        << lmEnergyExchangeProgramOfferTable["offerid"]
                                        << lmEnergyExchangeProgramOfferTable["runstatus"]
                                        << lmEnergyExchangeProgramOfferTable["offerdate"];

                                        selector.from(lmEnergyExchangeProgramOfferTable);

                                        selector.where(lmEnergyExchangeProgramOfferTable["deviceid"]==currentLMProgramEnergyExchange->getPAOId() &&//will be paobjectid
                                                       lmEnergyExchangeProgramOfferTable["offerdate"]>=compareDateTime);

                                        selector.orderBy(lmEnergyExchangeProgramOfferTable["offerdate"]);
                                        selector.orderBy(lmEnergyExchangeProgramOfferTable["offerid"]);

                                        /*if( _LM_DEBUG )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - " << selector.asString().data() << endl;
                                        }*/

                                        RWDBReader rdr = selector.reader(conn);
                                        RWOrdered& offers = currentLMProgramEnergyExchange->getLMEnergyExchangeOffers();
                                        while( rdr() )
                                        {
                                            offers.insert(new CtiLMEnergyExchangeOffer(rdr));
                                        }

                                        for(ULONG r=0;r<offers.entries();r++)
                                        {
                                            CtiLMEnergyExchangeOffer* currentLMEnergyExchangeOffer = (CtiLMEnergyExchangeOffer*)offers[r];
                                            RWDBTable lmEnergyExchangeOfferRevisionTable = db.table("lmenergyexchangeofferrevision");

                                            RWDBSelector selector = db.selector();
                                            selector << lmEnergyExchangeOfferRevisionTable["offerid"]
                                            << lmEnergyExchangeOfferRevisionTable["revisionnumber"]
                                            << lmEnergyExchangeOfferRevisionTable["actiondatetime"]
                                            << lmEnergyExchangeOfferRevisionTable["notificationdatetime"]
                                            << lmEnergyExchangeOfferRevisionTable["offerexpirationdatetime"]
                                            << lmEnergyExchangeOfferRevisionTable["additionalinfo"];

                                            selector.from(lmEnergyExchangeOfferRevisionTable);

                                            selector.where(lmEnergyExchangeOfferRevisionTable["offerid"]==currentLMEnergyExchangeOffer->getOfferId());

                                            selector.orderBy(lmEnergyExchangeOfferRevisionTable["revisionnumber"]);

                                            /*if( _LM_DEBUG )
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - " << selector.asString().data() << endl;
                                            }*/

                                            RWDBReader rdr = selector.reader(conn);
                                            RWOrdered& offerRevisions = currentLMEnergyExchangeOffer->getLMEnergyExchangeOfferRevisions();
                                            while( rdr() )
                                            {
                                                offerRevisions.insert(new CtiLMEnergyExchangeOfferRevision(rdr));
                                            }

                                            for(ULONG s=0;s<offerRevisions.entries();s++)
                                            {
                                                CtiLMEnergyExchangeOfferRevision* currentLMEnergyExchangeOfferRevision = (CtiLMEnergyExchangeOfferRevision*)offerRevisions[s];
                                                RWDBTable lmEnergyExchangeHourlyOfferTable = db.table("lmenergyexchangehourlyoffer");

                                                RWDBSelector selector = db.selector();
                                                selector << lmEnergyExchangeHourlyOfferTable["offerid"]
                                                << lmEnergyExchangeHourlyOfferTable["revisionnumber"]
                                                << lmEnergyExchangeHourlyOfferTable["hour"]
                                                << lmEnergyExchangeHourlyOfferTable["price"]
                                                << lmEnergyExchangeHourlyOfferTable["amountrequested"];

                                                selector.from(lmEnergyExchangeHourlyOfferTable);

                                                selector.where(lmEnergyExchangeHourlyOfferTable["offerid"]==currentLMEnergyExchangeOfferRevision->getOfferId() &&
                                                               lmEnergyExchangeHourlyOfferTable["revisionnumber"]==currentLMEnergyExchangeOfferRevision->getRevisionNumber());

                                                selector.orderBy(lmEnergyExchangeHourlyOfferTable["hour"]);

                                                /*if( _LM_DEBUG )
                                                {
                                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                                }*/

                                                RWDBReader rdr = selector.reader(conn);
                                                RWOrdered& hourlyOffers = currentLMEnergyExchangeOfferRevision->getLMEnergyExchangeHourlyOffers();
                                                while( rdr() )
                                                {
                                                    hourlyOffers.insert(new CtiLMEnergyExchangeHourlyOffer(rdr));
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            {
                                RWDBTable lmEnergyExchangeCustomerListTable = db.table("lmenergyexchangecustomerlist");
                                RWDBTable ciCustomerBaseTable = db.table("cicustomerbase");

                                RWDBSelector selector = db.selector();
                                selector << yukonPAObjectTable["paobjectid"]
                                << yukonPAObjectTable["category"]
                                << yukonPAObjectTable["paoclass"]
                                << yukonPAObjectTable["paoname"]
                                << yukonPAObjectTable["type"]
                                << yukonPAObjectTable["description"]
                                << yukonPAObjectTable["disableflag"]
                                << lmEnergyExchangeCustomerListTable["customerorder"]
                                << ciCustomerBaseTable["custtimezone"];

                                selector.from(yukonPAObjectTable);
                                selector.from(lmEnergyExchangeCustomerListTable);
                                selector.from(ciCustomerBaseTable);

                                selector.where( lmEnergyExchangeCustomerListTable["deviceid"]==currentLMProgramEnergyExchange->getPAOId() &&//will be paobjectid
                                                yukonPAObjectTable["paobjectid"]==lmEnergyExchangeCustomerListTable["lmcustomerdeviceid"] &&//will be paobjectid
                                                ciCustomerBaseTable["deviceid"]==yukonPAObjectTable["paobjectid"] );//will be paobjectid

                                selector.orderBy( lmEnergyExchangeCustomerListTable["customerorder"] );

                                /*if( _LM_DEBUG )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }*/

                                RWOrdered& lmEnergyExchangeCustomers = currentLMProgramEnergyExchange->getLMEnergyExchangeCustomers();
                                RWDBReader rdr = selector.reader(conn);
                                while( rdr() )
                                {
                                    lmEnergyExchangeCustomers.insert(new CtiLMEnergyExchangeCustomer(rdr));
                                }

                                if( currentLMProgramEnergyExchange->getManualControlReceivedFlag() && lmEnergyExchangeCustomers.entries() > 0 )
                                {
                                    for(ULONG a=0;a<lmEnergyExchangeCustomers.entries();a++)
                                    {
                                        CtiLMEnergyExchangeCustomer* currentLMEnergyExchangeCustomer = (CtiLMEnergyExchangeCustomer*)lmEnergyExchangeCustomers[a];
                                        RWDBDateTime currentDateTime;
                                        RWDBDateTime compareDateTime = RWDBDateTime(currentDateTime.year(),currentDateTime.month(),currentDateTime.dayOfMonth(),0,0,0,0);
                                        RWDBTable lmEnergyExchangeCustomerReplyTable = db.table("lmenergyexchangecustomerreply");
                                        RWDBTable lmEnergyExchangeProgramOfferTable = db.table("lmenergyexchangeprogramoffer");

                                        RWDBSelector selector = db.selector();
                                        selector << lmEnergyExchangeCustomerReplyTable["customerid"]
                                        << lmEnergyExchangeCustomerReplyTable["offerid"]
                                        << lmEnergyExchangeCustomerReplyTable["acceptstatus"]
                                        << lmEnergyExchangeCustomerReplyTable["acceptdatetime"]
                                        << lmEnergyExchangeCustomerReplyTable["revisionnumber"]
                                        << lmEnergyExchangeCustomerReplyTable["ipaddressofacceptuser"]
                                        << lmEnergyExchangeCustomerReplyTable["useridname"]
                                        << lmEnergyExchangeCustomerReplyTable["nameofacceptperson"]
                                        << lmEnergyExchangeCustomerReplyTable["energyexchangenotes"];

                                        selector.from(lmEnergyExchangeCustomerReplyTable);
                                        selector.from(lmEnergyExchangeProgramOfferTable);

                                        selector.where(lmEnergyExchangeCustomerReplyTable["customerid"]==currentLMEnergyExchangeCustomer->getPAOId() &&
                                                       lmEnergyExchangeCustomerReplyTable["offerid"]==lmEnergyExchangeProgramOfferTable["offerid"] &&
                                                       lmEnergyExchangeProgramOfferTable["offerdate"]>=compareDateTime);

                                        selector.orderBy(lmEnergyExchangeCustomerReplyTable["offerid"]);
                                        selector.orderBy(lmEnergyExchangeCustomerReplyTable["revisionnumber"]);

                                        /*if( _LM_DEBUG )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - " << selector.asString().data() << endl;
                                        }*/

                                        RWDBReader rdr = selector.reader(conn);
                                        RWOrdered& lmEnergyExchangeCustomerReplies = currentLMEnergyExchangeCustomer->getLMEnergyExchangeCustomerReplies();
                                        while( rdr() )
                                        {
                                            lmEnergyExchangeCustomerReplies.insert(new CtiLMEnergyExchangeCustomerReply(rdr));
                                        }

                                        for(ULONG b=0;b<lmEnergyExchangeCustomerReplies.entries();b++)
                                        {
                                            CtiLMEnergyExchangeCustomerReply* currentCustomerReply = (CtiLMEnergyExchangeCustomerReply*)lmEnergyExchangeCustomerReplies[b];
                                            RWDBDateTime currentDateTime;
                                            RWDBDateTime compareDateTime = RWDBDateTime(currentDateTime.year(),currentDateTime.month(),currentDateTime.dayOfMonth(),0,0,0,0);
                                            RWDBTable lmEnergyExchangeHourlyCustomerTable = db.table("lmenergyexchangehourlycustomer");
                                            RWDBTable lmEnergyExchangeProgramOfferTable = db.table("lmenergyexchangeprogramoffer");

                                            RWDBSelector selector = db.selector();
                                            selector << lmEnergyExchangeHourlyCustomerTable["customerid"]
                                            << lmEnergyExchangeHourlyCustomerTable["offerid"]
                                            << lmEnergyExchangeHourlyCustomerTable["revisionnumber"]
                                            << lmEnergyExchangeHourlyCustomerTable["hour"]
                                            << lmEnergyExchangeHourlyCustomerTable["amountcommitted"];

                                            selector.from(lmEnergyExchangeHourlyCustomerTable);
                                            selector.from(lmEnergyExchangeProgramOfferTable);

                                            selector.where(lmEnergyExchangeHourlyCustomerTable["customerid"]==currentCustomerReply->getCustomerId() &&
                                                           lmEnergyExchangeHourlyCustomerTable["offerid"]==currentCustomerReply->getOfferId() &&
                                                           lmEnergyExchangeHourlyCustomerTable["offerid"]==lmEnergyExchangeProgramOfferTable["offerid"] &&
                                                           lmEnergyExchangeProgramOfferTable["offerdate"]>=compareDateTime);

                                            selector.orderBy(lmEnergyExchangeHourlyCustomerTable["customerid"]);
                                            selector.orderBy(lmEnergyExchangeHourlyCustomerTable["offerid"]);
                                            selector.orderBy(lmEnergyExchangeHourlyCustomerTable["revisionnumber"]);
                                            selector.orderBy(lmEnergyExchangeHourlyCustomerTable["hour"]);

                                            /*if( _LM_DEBUG )
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - " << selector.asString().data() << endl;
                                            }*/

                                            RWDBReader rdr = selector.reader(conn);
                                            RWOrdered& hourlyCustomers = currentCustomerReply->getLMEnergyExchangeHourlyCustomers();
                                            while( rdr() )
                                            {
                                                hourlyCustomers.insert(new CtiLMEnergyExchangeHourlyCustomer(rdr));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - PAO type = " << currentLMProgramBase->getPAOType() << " not supported yet.  In: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }

                    for(ULONG j=0;j<lmProgramList.entries();j++)
                    {
                        CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmProgramList[j];

                        RWDBTable lmProgramControlWindow = db.table("lmprogramcontrolwindow");

                        RWDBSelector selector = db.selector();
                        selector << lmProgramControlWindow["deviceid"]//will be paobjectid
                        << lmProgramControlWindow["windownumber"]
                        << lmProgramControlWindow["availablestarttime"]
                        << lmProgramControlWindow["availablestoptime"];

                        selector.from(lmProgramControlWindow);

                        selector.where( lmProgramControlWindow["deviceid"]==currentLMProgramBase->getPAOId() );//will be paobjectid

                        selector.orderBy( lmProgramControlWindow["windownumber"] );

                        /*if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/

                        RWOrdered& lmProgramControlWindowList = currentLMProgramBase->getLMProgramControlWindows();
                        RWDBReader rdr = selector.reader(conn);
                        while( rdr() )
                        {
                            lmProgramControlWindowList.insert( new CtiLMProgramControlWindow(rdr) );
                        }
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

    dumpAllDynamicData();
}

/*---------------------------------------------------------------------------
    shutdown

    Dumps the strategy list.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::shutdown()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    dumpAllDynamicData();
    _controlAreas->clearAndDestroy();
    delete _controlAreas;

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - done shutting down the control area store" << endl;
    }
}

/*---------------------------------------------------------------------------
    doResetThr

    Starts on construction and simply forces a call to reset every 5 minutes
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::doResetThr()
{
    try
    {
        //defaults
        int refreshrate = 3600;

        RWCString str;
        char var[128];

        strcpy(var, "LOAD_MANAGEMENT_REFRESH");
        if( !(str = gConfigParms.getValueAsString(var)).isNull() )
        {
            refreshrate = atoi(str);
            if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }
        
        /*char temp[80];
        HINSTANCE hLib = LoadLibrary("cparms.dll");
    
        if(hLib)
        {
            CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );
    
            bool trouble = FALSE;
    
            if( (*fpGetAsString)("LOAD_MANAGEMENT_REFRESH", temp, 80) )
            {
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - LOAD_MANAGEMENT_REFRESH:  " << temp << endl;
                }
    
                refreshrate = atoi(temp);
            }
            else
                trouble = TRUE;
    
            if( trouble == TRUE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Unable to obtain 'LOAD_MANAGEMENT_REFRESH' value from cparms." << endl;
            }
    
            FreeLibrary(hLib);
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to load cparms.dll" << endl;
        }*/
    
        time_t start = time(NULL);
    
        RWDBDateTime currenttime = RWDBDateTime();
        ULONG tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+refreshrate;
        RWDBDateTime nextDatabaseRefresh = RWDBDateTime(RWTime(tempsum));
    
        while(TRUE)
        {
            rwRunnable().serviceCancellation();
    
            if( RWDBDateTime() >= nextDatabaseRefresh )
            {
                RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Restoring control area list from the database" << endl;
                }
    
                if( currenttime.rwdate() != RWDBDateTime().rwdate() )
                {//check to see if it is midnight
                    checkMidnightDefaultsForReset();
                }
                currenttime = RWDBDateTime();
    
                dumpAllDynamicData();
                setValid(false);
                setReregisterForPoints(TRUE);
    
                tempsum = (currenttime.seconds()-(currenttime.seconds()%refreshrate))+refreshrate;
                nextDatabaseRefresh = RWDBDateTime(RWTime(tempsum));
            }
            else
            {
                rwRunnable().sleep(500);
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/* Pointer to the singleton instance of CtiLMControlAreaStore
   Instantiate lazily by Instance */
CtiLMControlAreaStore* CtiLMControlAreaStore::_instance = NULL;

/*---------------------------------------------------------------------------
    getInstance

    Returns a pointer to the singleton instance of CtiLMControlAreaStore
---------------------------------------------------------------------------*/
CtiLMControlAreaStore* CtiLMControlAreaStore::getInstance()
{
    if( _instance == NULL )
    {
        _instance = new CtiLMControlAreaStore();
    }

    return _instance;
}

/*---------------------------------------------------------------------------
    deleteInstance

    Deletes the singleton instance of CtiLMControlAreaStore
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::deleteInstance()
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
bool CtiLMControlAreaStore::isValid() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return _isvalid;
}

/*---------------------------------------------------------------------------
    setValid

    Sets the _isvalid flag
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::setValid(bool valid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _isvalid = valid;
    dumpAllDynamicData();
}

/*---------------------------------------------------------------------------
    getReregisterForPoints

    Gets _reregisterforpoints
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::getReregisterForPoints() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return _reregisterforpoints;
}

/*---------------------------------------------------------------------------
    setReregisterForPoints

    Sets _reregisterforpoints
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::setReregisterForPoints(bool reregister)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _reregisterforpoints = reregister;
}

/*---------------------------------------------------------------------------
    UpdateControlAreaDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the control area.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateControlAreaDisableFlagInDB(CtiLMControlArea* controlArea)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == controlArea->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( RWCString((controlArea->getDisableFlag()?'Y':'N')) );

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(controlArea->getPAOId(), ChangePAODb,
                                                      controlArea->getPAOCategory(), desolveLoadManagementType(controlArea->getPAOType()),
                                                      ChangeTypeUpdate);
        dbChange->setSource(LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE);
        CtiLoadManager::getInstance()->sendMessageToDispatch(dbChange);

        return updater.status().isValid();
    }
}

/*---------------------------------------------------------------------------
    UpdateProgramDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the program.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateProgramDisableFlagInDB(CtiLMProgramBase* program)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == program->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( RWCString((program->getDisableFlag()?'Y':'N')) );

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(program->getPAOId(), ChangePAODb,
                                                      program->getPAOCategory(), desolveLoadManagementType(program->getPAOType()),
                                                      ChangeTypeUpdate);
        dbChange->setSource(LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE);
        CtiLoadManager::getInstance()->sendMessageToDispatch(dbChange);

        return updater.status().isValid();
    }
}

/*---------------------------------------------------------------------------
    UpdateTriggerInDB

    Updates a trigger threshold, restore offset, etc. in the database either
    by TDC user or ATKU.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateTriggerInDB(CtiLMControlArea* controlArea, CtiLMControlAreaTrigger* trigger)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable lmControlAreaTriggerTable = getDatabase().table( "lmcontrolareatrigger" );
        RWDBUpdater updater = lmControlAreaTriggerTable.updater();

        updater << lmControlAreaTriggerTable["threshold"].assign( trigger->getThreshold() )
        << lmControlAreaTriggerTable["minrestoreoffset"].assign( trigger->getMinRestoreOffset() );

        updater.where( lmControlAreaTriggerTable["deviceid"] == trigger->getPAOId() &&
                       lmControlAreaTriggerTable["triggernumber"] == trigger->getTriggerNumber() );

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(controlArea->getPAOId(), ChangePAODb,
                                                      controlArea->getPAOCategory(), desolveLoadManagementType(controlArea->getPAOType()),
                                                      ChangeTypeUpdate);
        dbChange->setSource(LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE);
        CtiLoadManager::getInstance()->sendMessageToDispatch(dbChange);

        return updater.status().isValid();
    }
}

/*---------------------------------------------------------------------------
    checkMidnightDefaultsForReset

    Only called at midnight!  Checks each of the control area default
    operational states and updates the area's disable flag if not synched with
    the def op state.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::checkMidnightDefaultsForReset()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    bool returnBool = false;
    RWOrdered& controlAreas = *getControlAreas();
    for(long i=0;i<controlAreas.entries();i++)
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
        if( currentControlArea->getDefOperationalState() != CtiLMControlArea::DefOpStateNone )
        {//check default operational state
            if( ( currentControlArea->getDefOperationalState() == CtiLMControlArea::DefOpStateEnabled &&
                  currentControlArea->getDisableFlag() ) ||
                ( currentControlArea->getDefOperationalState() == CtiLMControlArea::DefOpStateDisabled &&
                  !currentControlArea->getDisableFlag() ) )
            {
                {
                    RWCString text = RWCString("Automatic Disable Flag Update");
                    RWCString additional = RWCString(" Disable Flag not set to Default Operational State.  Control Area: ");
                    additional += currentControlArea->getPAOName();
                    additional += " was automatically ";
                    additional += currentControlArea->getDefOperationalState();
                    additional += ".";
                    CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                }
                currentControlArea->setDisableFlag(!currentControlArea->getDisableFlag());
                UpdateControlAreaDisableFlagInDB(currentControlArea);
                returnBool = true;
            }
        }
        if( currentControlArea->getCurrentDailyStartTime() != currentControlArea->getDefDailyStartTime() ||
            currentControlArea->getCurrentDailyStopTime() != currentControlArea->getDefDailyStopTime() )
        {//check default daily start and stop times
            if( currentControlArea->getCurrentDailyStartTime() != currentControlArea->getDefDailyStartTime() )
            {
                char tempchar[80] = "";
                RWCString text = RWCString("Automatic Daily Start Time Update");
                RWCString additional = RWCString(" Current Daily Start Time not set to Default Start Time.  Control Area: ");
                additional += currentControlArea->getPAOName();
                additional += " Daily Start Time was automatically set back to default of ";
                LONG defStartTimeHours = currentControlArea->getDefDailyStartTime() / 3600;
                LONG defStartTimeMinutes = (currentControlArea->getDefDailyStartTime() - (defStartTimeHours * 3600)) / 60;
                _snprintf(tempchar,80,"%d",defStartTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",defStartTimeMinutes);
                additional += tempchar;
                additional += " from ";
                LONG currentStartTimeHours = currentControlArea->getCurrentDailyStartTime() / 3600;
                LONG currentStartTimeMinutes = (currentControlArea->getCurrentDailyStartTime() - (currentStartTimeHours * 3600)) / 60;
                _snprintf(tempchar,80,"%d",currentStartTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",currentStartTimeMinutes);
                additional += tempchar;
                additional += ".";
                CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
            }
            if( currentControlArea->getCurrentDailyStopTime() != currentControlArea->getDefDailyStopTime() )
            {
                char tempchar[80] = "";
                RWCString text = RWCString("Automatic Daily Stop Time Update");
                RWCString additional = RWCString(" Current Daily Stop Time not set to Default Stop Time.  Control Area: ");
                additional += currentControlArea->getPAOName();
                additional += " Daily Stop Time was automatically set back to default of ";
                LONG defStopTimeHours = currentControlArea->getDefDailyStopTime() / 3600;
                LONG defStopTimeMinutes = (currentControlArea->getDefDailyStopTime() - (defStopTimeHours * 3600)) / 60;
                _snprintf(tempchar,80,"%d",defStopTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",defStopTimeMinutes);
                additional += tempchar;
                additional += " from ";
                LONG currentStopTimeHours = currentControlArea->getCurrentDailyStopTime() / 3600;
                LONG currentStopTimeMinutes = (currentControlArea->getCurrentDailyStopTime() - (currentStopTimeHours * 3600)) / 60;
                _snprintf(tempchar,80,"%d",currentStopTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",currentStopTimeMinutes);
                additional += tempchar;
                additional += ".";
                CtiLoadManager::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
            }
            currentControlArea->setCurrentDailyStartTime(currentControlArea->getDefDailyStartTime());
            currentControlArea->setCurrentDailyStopTime(currentControlArea->getDefDailyStopTime());
            currentControlArea->dumpDynamicData();
            returnBool = true;
        }
    }

    return returnBool;
}

const RWCString CtiLMControlAreaStore::LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE = "LOAD_MANAGEMENT_SERVER";

