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
#include <rw/tphdict.h>

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
#include "lmprogramthermostatgear.h"
#include "lmprogramenergyexchange.h"
#include "lmgroupversacom.h"
#include "lmgroupemetcon.h"
#include "lmgroupexpresscom.h"
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

struct id_hash{LONG operator()(LONG x) const { return x; } };


/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMControlAreaStore::CtiLMControlAreaStore() : _isvalid(false), _reregisterforpoints(true), _lastdbreloadtime(RWDBDateTime(1990,1,1,0,0,0,0))
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - CtiLMControlAreaStore destructor called..." << endl;
    }*/
    if( _resetthr.isValid() )
    {
        _resetthr.requestCancellation();
        _resetthr.join();
    }

    shutdown();
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - CtiLMControlAreaStore destructor done!!!" << endl;
    }*/
}

/*---------------------------------------------------------------------------
    getControlAreas

    Returns a RWOrdered of CtiLMControlAreas
---------------------------------------------------------------------------*/
RWOrdered* CtiLMControlAreaStore::getControlAreas(ULONG secondsFrom1901)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+90 )
    {//is not valid and has been at 1.5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
        reset();
    }

    return _controlAreas;
}

/*---------------------------------------------------------------------------
    dumpAllDynamicData

    Writes out the dynamic information for each of the control areas.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::dumpAllDynamicData()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Begin writing dynamic data to the database..." << endl;
    }*/
    if( _controlAreas->entries() > 0 )
    {
        RWDBDateTime currentDateTime = RWDBDateTime();
        RWCString dynamicLoadManagement("dynamicLoadManagement");
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        conn.beginTransaction(dynamicLoadManagement);

        for(LONG i=0;i<_controlAreas->entries();i++)
        {
            CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];
            currentLMControlArea->dumpDynamicData(conn,currentDateTime);

            RWOrdered& lmControlAreaTriggers = currentLMControlArea->getLMControlAreaTriggers();
            if( lmControlAreaTriggers.entries() > 0 )
            {
                for(LONG x=0;x<lmControlAreaTriggers.entries();x++)
                {
                    CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)lmControlAreaTriggers[x];
                    currentLMControlAreaTrigger->dumpDynamicData(conn,currentDateTime);
                }
            }

            RWOrdered& lmPrograms = currentLMControlArea->getLMPrograms();
            if( lmPrograms.entries() > 0 )
            {
                for(LONG j=0;j<lmPrograms.entries();j++)
                {
                    CtiLMProgramBase* currentLMProgramBase = (CtiLMProgramBase*)lmPrograms[j];
                    currentLMProgramBase->dumpDynamicData(conn,currentDateTime);

                    if( currentLMProgramBase->getPAOType() ==  TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirect* currentLMProgramDirect = (CtiLMProgramDirect*)currentLMProgramBase;
                        currentLMProgramDirect->dumpDynamicData(conn,currentDateTime);

                        RWOrdered& lmGroups = currentLMProgramDirect->getLMProgramDirectGroups();
                        if( lmGroups.entries() > 0 )
                        {
                            for(LONG k=0;k<lmGroups.entries();k++)
                            {
                                CtiLMGroupBase* currentLMGroupBase = (CtiLMGroupBase*)lmGroups[k];
                                currentLMGroupBase->dumpDynamicData(conn,currentDateTime);
                            }
                        }
                    }
                    else if( currentLMProgramBase->getPAOType() ==  TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CtiLMProgramCurtailment* currentLMProgramCurtailment = (CtiLMProgramCurtailment*)currentLMProgramBase;

                        if( currentLMProgramCurtailment->getManualControlReceivedFlag() )
                        {
                            currentLMProgramCurtailment->dumpDynamicData(conn,currentDateTime);

                            RWOrdered& lmCurtailCustomers = currentLMProgramCurtailment->getLMProgramCurtailmentCustomers();
                            if( lmCurtailCustomers.entries() > 0 )
                            {
                                for(LONG k=0;k<lmCurtailCustomers.entries();k++)
                                {
                                    CtiLMCurtailCustomer* currentLMCurtailCustomer = (CtiLMCurtailCustomer*)lmCurtailCustomers[k];
                                    currentLMCurtailCustomer->dumpDynamicData(conn,currentDateTime);
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
        conn.commitTransaction(dynamicLoadManagement);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - ***No control areas to write to dynamic tables***" << endl;
    }

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Done writing dynamic data to the database!!!" << endl;
    }*/
}

/*---------------------------------------------------------------------------
    reset

    Reset attempts to read in all the control areas from the database.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::reset()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    bool wasAlreadyRunning = false;
    try
    {
        //if( _LM_DEBUG )
        RWTimer overallTimer;
        overallTimer.start();
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
                        dumpAllDynamicData();
                        _controlAreas->clearAndDestroy();
                        wasAlreadyRunning = true;
                    }
    
                    RWDBDateTime currentDateTime;
                    RWDBDatabase db = getDatabase();
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");

                    RWOrdered allGroupList;
                    RWTimer allGroupTimer;
                    allGroupTimer.start();
                    {//loading direct groups start
                        RWDBTable lmGroupMacroExpanderView = db.table("lmgroupmacroexpander_view");
                        RWDBTable lmGroupPointTable = db.table("lmgrouppoint");
                        RWDBTable lmGroupRippleTable = db.table("lmgroupripple");
                        RWDBTable dynamicLMGroupTable = db.table("dynamiclmgroup");
                        RWDBTable pointTable = db.table("point");

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
                        << lmGroupMacroExpanderView["deviceid"]
                        << rwdbName("pointdeviceidusage",lmGroupPointTable["deviceidusage"])
                        << rwdbName("pointpointidusage",lmGroupPointTable["pointidusage"])
                        << rwdbName("pointstartcontrolrawstate",lmGroupPointTable["startcontrolrawstate"])
                        << rwdbName("rippleshedtime",lmGroupRippleTable["shedtime"])
                        << dynamicLMGroupTable["groupcontrolstate"]
                        << dynamicLMGroupTable["currenthoursdaily"]
                        << dynamicLMGroupTable["currenthoursmonthly"]
                        << dynamicLMGroupTable["currenthoursseasonal"]
                        << dynamicLMGroupTable["currenthoursannually"]
                        << dynamicLMGroupTable["lastcontrolsent"]
                        << dynamicLMGroupTable["timestamp"]
                        << pointTable["pointid"]
                        << pointTable["pointoffset"]
                        << pointTable["pointtype"];

                        selector.from(lmGroupMacroExpanderView);
                        selector.from(lmGroupPointTable);
                        selector.from(lmGroupRippleTable);
                        selector.from(dynamicLMGroupTable);
                        selector.from(pointTable);

                        selector.where( ( ( lmGroupMacroExpanderView["childid"].isNull() &&
                                            lmGroupMacroExpanderView["paobjectid"]==lmGroupMacroExpanderView["lmgroupdeviceid"] ) ||
                                          ( !lmGroupMacroExpanderView["childid"].isNull() &&
                                            lmGroupMacroExpanderView["paobjectid"]==lmGroupMacroExpanderView["childid"] ) ) &&
                                        lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(lmGroupPointTable["deviceid"]) &&
                                        lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(lmGroupRippleTable["deviceid"]) &&
                                        lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(dynamicLMGroupTable["deviceid"]) &&
                                        lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(pointTable["paobjectid"]) );

                        selector.orderBy( lmGroupMacroExpanderView["deviceid"] );
                        selector.orderBy( lmGroupMacroExpanderView["grouporder"] );
                        selector.orderBy( lmGroupMacroExpanderView["childorder"] );

                        /*if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/

                        RWDBReader rdr = selector.reader(conn);

                        CtiLMGroupBase* currentLMGroupBase = NULL;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            LONG tempPAObjectId = 0;
                            RWCString tempPAOCategory;
                            RWCString tempPAOType;
                            rdr["category"] >> tempPAOCategory;
                            rdr["type"] >> tempPAOType;
                            rdr["paobjectid"] >> tempPAObjectId;

                            if( currentLMGroupBase != NULL &&
                                tempPAObjectId == currentLMGroupBase->getPAOId() )
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
                                        if( tempPointOffset == DAILYCONTROLHISTOFFSET )
                                        {
                                            currentLMGroupBase->setHoursDailyPointId(tempPointId);
                                        }
                                        else if( tempPointOffset == MONTHLYCONTROLHISTOFFSET )
                                        {
                                            currentLMGroupBase->setHoursMonthlyPointId(tempPointId);
                                        }
                                        else if( tempPointOffset == SEASONALCONTROLHISTOFFSET )
                                        {
                                            currentLMGroupBase->setHoursSeasonalPointId(tempPointId);
                                        }
                                        else if( tempPointOffset == ANNUALCONTROLHISTOFFSET )
                                        {
                                            currentLMGroupBase->setHoursAnnuallyPointId(tempPointId);
                                        }
                                        /*else
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - Undefined Cap Bank point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                        }*/
                                    }
                                    /*else( resolvePointType(tempPointType) != StatusPointType )
                                    {//undefined group point
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - Undefined Group point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                    }*/
                                }
                            }
                            else
                            {
                                RWCString tempPAOCategory;
                                RWCString tempPAOType;
                                rdr["category"] >> tempPAOCategory;
                                rdr["type"] >> tempPAOType;
                                INT tempTypeInt = resolvePAOType(tempPAOCategory,tempPAOType);
                                if( tempTypeInt == TYPE_LMGROUP_VERSACOM )
                                {
                                    currentLMGroupBase = new CtiLMGroupVersacom(rdr);
                                }
                                else if( tempTypeInt == TYPE_LMGROUP_EMETCON )
                                {
                                    currentLMGroupBase = new CtiLMGroupEmetcon(rdr);
                                }
                                else if( tempTypeInt == TYPE_LMGROUP_RIPPLE )
                                {
                                    currentLMGroupBase = new CtiLMGroupRipple(rdr);
                                }
                                else if( tempTypeInt == TYPE_LMGROUP_POINT )
                                {
                                    currentLMGroupBase = new CtiLMGroupPoint(rdr);
                                }
                                else if( tempTypeInt == TYPE_LMGROUP_EXPRESSCOM )
                                {
                                    currentLMGroupBase = new CtiLMGroupExpresscom(rdr);
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Group device type = " << tempPAOType << " not supported yet.  In: " << __FILE__ << " at:" << __LINE__ << endl;
                                }

                                if( currentLMGroupBase != NULL )
                                {
                                    allGroupList.insert(currentLMGroupBase);
                                }
                            }
                        }
                    }//loading direct groups end
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for All Groups is: " << allGroupTimer.elapsedTime() << endl;
                        allGroupTimer.reset();
                    }*/


                    RWTValHashMap<LONG,CtiLMProgramBase*,id_hash,equal_to<LONG> > directProgramHashMap;

                    RWDBTable lmProgramTable = db.table("lmprogram");
                    RWDBTable dynamicLMProgramTable = db.table("dynamiclmprogram");

                    RWTimer dirProgsTimer;
                    dirProgsTimer.start();
                    {//loading direct programs start
                        RWDBTable lmProgramDirectTable = db.table("lmprogramdirect");
                        RWDBTable dynamicLMProgramDirectTable = db.table("dynamiclmprogramdirect");

                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                                 << yukonPAObjectTable["category"]
                                 << yukonPAObjectTable["paoclass"]
                                 << yukonPAObjectTable["paoname"]
                                 << yukonPAObjectTable["type"]
                                 << yukonPAObjectTable["description"]
                                 << yukonPAObjectTable["disableflag"]
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
                                 << dynamicLMProgramDirectTable["currentgearnumber"]
                                 << dynamicLMProgramDirectTable["lastgroupcontrolled"]
                                 << dynamicLMProgramDirectTable["starttime"]
                                 << dynamicLMProgramDirectTable["stoptime"];
    
                        selector.from(yukonPAObjectTable);
                        selector.from(lmProgramTable);
                        selector.from(lmProgramDirectTable);
                        selector.from(dynamicLMProgramTable);
                        selector.from(dynamicLMProgramDirectTable);
    
                        selector.where( yukonPAObjectTable["paobjectid"]==lmProgramDirectTable["deviceid"] &&
                                        lmProgramDirectTable["deviceid"]==lmProgramTable["deviceid"] &&
                                        lmProgramTable["deviceid"].leftOuterJoin(dynamicLMProgramTable["deviceid"]) &&
                                        lmProgramTable["deviceid"].leftOuterJoin(dynamicLMProgramDirectTable["deviceid"]) );

                        selector.orderBy(yukonPAObjectTable["paobjectid"]);

                        /*if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/

                        RWDBReader rdr = selector.reader(conn);
                        while( rdr() )
                        {
                            CtiLMProgramDirect* newDirProg = new CtiLMProgramDirect(rdr);
                            RWOrdered& directGroups = newDirProg->getLMProgramDirectGroups();
                            //Inserting this program's groups
                            while( allGroupList.entries() > 0 && newDirProg->getPAOId() == ((CtiLMGroupBase*)allGroupList[0])->getPAOId() )
                            {
                                directGroups.insert(allGroupList.removeAt(0));
                            }

                            //Inserting this direct program into hash map
                            directProgramHashMap.insert( newDirProg->getPAOId(), newDirProg );
                        }
                    }//loading direct programs end
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Direct Programs is: " << dirProgsTimer.elapsedTime() << endl;
                        dirProgsTimer.reset();
                    }*/


                    RWTimer gearsTimer;
                    gearsTimer.start();
                    {//loading direct gears start
                        RWDBTable lmProgramDirectGearTable = db.table("lmprogramdirectgear");
                        RWDBTable lmThermoStatGearTable = db.table("lmthermostatgear");
    
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
                                 << lmProgramDirectGearTable["methodoptionmax"]
                                 << lmThermoStatGearTable["settings"]
                                 << lmThermoStatGearTable["minvalue"]
                                 << lmThermoStatGearTable["maxvalue"]
                                 << lmThermoStatGearTable["valueb"]
                                 << lmThermoStatGearTable["valued"]
                                 << lmThermoStatGearTable["valuef"]
                                 << lmThermoStatGearTable["random"]
                                 << lmThermoStatGearTable["valueta"]
                                 << lmThermoStatGearTable["valuetb"]
                                 << lmThermoStatGearTable["valuetc"]
                                 << lmThermoStatGearTable["valuetd"]
                                 << lmThermoStatGearTable["valuete"]
                                 << lmThermoStatGearTable["valuetf"];

                        selector.from(lmProgramDirectGearTable);
                        selector.from(lmThermoStatGearTable);
    
                        selector.where( lmProgramDirectGearTable["gearid"].leftOuterJoin(lmThermoStatGearTable["gearid"]) );
    
                        selector.orderBy( lmProgramDirectGearTable["deviceid"] );
                        selector.orderBy( lmProgramDirectGearTable["gearnumber"] );
    
                        /*if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/
    
                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        while( rdr() )
                        {
                            rdr["settings"] >> isNull;
                            CtiLMProgramDirectGear* newDirectGear = NULL;
                            if( isNull )
                            {
                                newDirectGear = new CtiLMProgramDirectGear(rdr);
                            }
                            else
                            {
                                newDirectGear = new CtiLMProgramThermoStatGear(rdr);
                            }
                            if( newDirectGear != NULL )
                            {
                                CtiLMProgramBase* programToPutGearIn = NULL;
                                if( directProgramHashMap.findValue(newDirectGear->getPAOId(),programToPutGearIn) )
                                {
                                    RWOrdered& lmProgramDirectGearList = ((CtiLMProgramDirect*)programToPutGearIn)->getLMProgramDirectGears();
                                    lmProgramDirectGearList.insert(newDirectGear);
                                }
                            }
                        }
                    }//loading direct gears end
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Direct Gears is: " << gearsTimer.elapsedTime() << endl;
                        gearsTimer.reset();
                    }*/


                    RWTValHashMap<LONG,CtiLMProgramBase*,id_hash,equal_to<LONG> > curtailmentProgramHashMap;

                    RWTimer curtailProgsTimer;
                    curtailProgsTimer.start();
                    {//loading curtailment programs start
                        RWDBTable lmProgramCurtailmentTable = db.table("lmprogramcurtailment");

                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                                 << yukonPAObjectTable["category"]
                                 << yukonPAObjectTable["paoclass"]
                                 << yukonPAObjectTable["paoname"]
                                 << yukonPAObjectTable["type"]
                                 << yukonPAObjectTable["description"]
                                 << yukonPAObjectTable["disableflag"]
                                 << lmProgramTable["controltype"]
                                 << lmProgramTable["availableseasons"]
                                 << lmProgramTable["availableweekdays"]
                                 << lmProgramTable["maxhoursdaily"]
                                 << lmProgramTable["maxhoursmonthly"]
                                 << lmProgramTable["maxhoursseasonal"]
                                 << lmProgramTable["maxhoursannually"]
                                 << lmProgramTable["minactivatetime"]
                                 << lmProgramTable["minrestarttime"]
                                 << lmProgramCurtailmentTable["minnotifytime"]
                                 << lmProgramCurtailmentTable["heading"]
                                 << lmProgramCurtailmentTable["messageheader"]
                                 << lmProgramCurtailmentTable["messagefooter"]
                                 << lmProgramCurtailmentTable["acktimelimit"]
                                 << lmProgramCurtailmentTable["canceledmsg"]
                                 << lmProgramCurtailmentTable["stoppedearlymsg"]
                                 << dynamicLMProgramTable["programstate"]
                                 << dynamicLMProgramTable["reductiontotal"]
                                 << dynamicLMProgramTable["startedcontrolling"]
                                 << dynamicLMProgramTable["lastcontrolsent"]
                                 << dynamicLMProgramTable["manualcontrolreceivedflag"]
                                 << dynamicLMProgramTable["timestamp"];

                        selector.from(yukonPAObjectTable);
                        selector.from(lmProgramTable);
                        selector.from(lmProgramCurtailmentTable);
                        selector.from(dynamicLMProgramTable);

                        selector.where( yukonPAObjectTable["paobjectid"]==lmProgramCurtailmentTable["deviceid"] &&
                                        lmProgramCurtailmentTable["deviceid"]==lmProgramTable["deviceid"] &&
                                        lmProgramTable["deviceid"].leftOuterJoin(dynamicLMProgramTable["deviceid"]) );

                        selector.orderBy(yukonPAObjectTable["paobjectid"]);

                        /*if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/

                        RWDBReader rdr = selector.reader(conn);
                        while( rdr() )
                        {
                            CtiLMProgramCurtailment* newCurtProg = new CtiLMProgramCurtailment(rdr);
                            if( newCurtProg->getManualControlReceivedFlag() )
                            {
                                newCurtProg->restoreDynamicData(rdr);
                            }
                            //Inserting this curtailment program into hash map
                            curtailmentProgramHashMap.insert( newCurtProg->getPAOId(), newCurtProg );
                        }

                        RWTValHashMapIterator<LONG,CtiLMProgramBase*,id_hash,equal_to<LONG> > itr(curtailmentProgramHashMap);

                        for(;itr();)
                        {
                            CtiLMProgramCurtailment* currentLMProgramCurtailment = (CtiLMProgramCurtailment*)itr.value();

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

                            selector.where( lmProgramCurtailCustomerListTable["deviceid"]==currentLMProgramCurtailment->getPAOId() &&
                                            yukonPAObjectTable["paobjectid"]==lmProgramCurtailCustomerListTable["lmcustomerdeviceid"] &&
                                            ciCustomerBaseTable["deviceid"]==yukonPAObjectTable["paobjectid"] );

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
                                for(LONG temp=0;temp<lmProgramCurtailmentCustomers.entries();temp++)
                                {
                                    ((CtiLMCurtailCustomer*)lmProgramCurtailmentCustomers[temp])->restoreDynamicData(rdr);
                                }
                            }
                        }
                    }//loading curtailment programs end
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Curtailment Programs is: " << curtailProgsTimer.elapsedTime() << endl;
                        curtailProgsTimer.reset();
                    }*/


                    RWTValHashMap<LONG,CtiLMProgramBase*,id_hash,equal_to<LONG> > energyExchangeProgramHashMap;

                    RWTimer eeProgsTimer;
                    eeProgsTimer.start();
                    {//loading energy exchange programs start
                        RWDBTable lmProgramEnergyExchangeTable = db.table("lmprogramenergyexchange");

                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                                 << yukonPAObjectTable["category"]
                                 << yukonPAObjectTable["paoclass"]
                                 << yukonPAObjectTable["paoname"]
                                 << yukonPAObjectTable["type"]
                                 << yukonPAObjectTable["description"]
                                 << yukonPAObjectTable["disableflag"]
                                 << lmProgramTable["controltype"]
                                 << lmProgramTable["availableseasons"]
                                 << lmProgramTable["availableweekdays"]
                                 << lmProgramTable["maxhoursdaily"]
                                 << lmProgramTable["maxhoursmonthly"]
                                 << lmProgramTable["maxhoursseasonal"]
                                 << lmProgramTable["maxhoursannually"]
                                 << lmProgramTable["minactivatetime"]
                                 << lmProgramTable["minrestarttime"]
                                 << lmProgramEnergyExchangeTable["minnotifytime"]
                                 << lmProgramEnergyExchangeTable["heading"]
                                 << lmProgramEnergyExchangeTable["messageheader"]
                                 << lmProgramEnergyExchangeTable["messagefooter"]
                                 << lmProgramEnergyExchangeTable["canceledmsg"]
                                 << lmProgramEnergyExchangeTable["stoppedearlymsg"]
                                 << dynamicLMProgramTable["programstate"]
                                 << dynamicLMProgramTable["reductiontotal"]
                                 << dynamicLMProgramTable["startedcontrolling"]
                                 << dynamicLMProgramTable["lastcontrolsent"]
                                 << dynamicLMProgramTable["manualcontrolreceivedflag"]
                                 << dynamicLMProgramTable["timestamp"];

                        selector.from(yukonPAObjectTable);
                        selector.from(lmProgramTable);
                        selector.from(lmProgramEnergyExchangeTable);
                        selector.from(dynamicLMProgramTable);

                        selector.where( yukonPAObjectTable["paobjectid"]==lmProgramEnergyExchangeTable["deviceid"] &&
                                        lmProgramEnergyExchangeTable["deviceid"]==lmProgramTable["deviceid"] &&
                                        lmProgramTable["deviceid"].leftOuterJoin(dynamicLMProgramTable["deviceid"]) );

                        selector.orderBy(yukonPAObjectTable["paobjectid"]);

                        /*if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/

                        RWDBReader rdr = selector.reader(conn);
                        while( rdr() )
                        {
                            CtiLMProgramEnergyExchange* newEEProg = new CtiLMProgramEnergyExchange(rdr);
                            //Inserting this curtailment program into hash map
                            energyExchangeProgramHashMap.insert( newEEProg->getPAOId(), newEEProg );
                        }

                        RWTValHashMapIterator<LONG,CtiLMProgramBase*,id_hash,equal_to<LONG> > itr(energyExchangeProgramHashMap);

                        for(;itr();)
                        {
                            CtiLMProgramEnergyExchange* currentLMProgramEnergyExchange = (CtiLMProgramEnergyExchange*)itr.value();

                            if( currentLMProgramEnergyExchange->getManualControlReceivedFlag() )
                            {
                                RWDBDateTime currentDateTime;
                                RWDBDateTime compareDateTime = RWDBDateTime(currentDateTime.year(),currentDateTime.month(),currentDateTime.dayOfMonth(),0,0,0,0);
                                RWDBTable lmEnergyExchangeProgramOfferTable = db.table("lmenergyexchangeprogramoffer");

                                RWDBSelector selector = db.selector();
                                selector << lmEnergyExchangeProgramOfferTable["deviceid"]
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

                                for(LONG r=0;r<offers.entries();r++)
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

                                    for(LONG s=0;s<offerRevisions.entries();s++)
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
                                    for(LONG a=0;a<lmEnergyExchangeCustomers.entries();a++)
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

                                        for(LONG b=0;b<lmEnergyExchangeCustomerReplies.entries();b++)
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
                    }//loading energy exchange programs end
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Energy Exchange Programs is: " << eeProgsTimer.elapsedTime() << endl;
                        eeProgsTimer.reset();
                    }*/


                    RWTimer progWinTimer;
                    progWinTimer.start();
                    {//loading program control windows start
                        RWDBTable lmProgramControlWindow = db.table("lmprogramcontrolwindow");

                        RWDBSelector selector = db.selector();
                        selector << lmProgramControlWindow["deviceid"]
                                 << lmProgramControlWindow["windownumber"]
                                 << lmProgramControlWindow["availablestarttime"]
                                 << lmProgramControlWindow["availablestoptime"];

                        selector.from(lmProgramControlWindow);

                        selector.orderBy( lmProgramControlWindow["deviceid"] );
                        selector.orderBy( lmProgramControlWindow["windownumber"] );

                        /*if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/

                        RWDBReader rdr = selector.reader(conn);
                        while( rdr() )
                        {
                            CtiLMProgramControlWindow* newWindow = new CtiLMProgramControlWindow(rdr);
                            CtiLMProgramBase* programToPutWindowIn = NULL;
                            if( directProgramHashMap.findValue(newWindow->getPAOId(),programToPutWindowIn) ||
                                curtailmentProgramHashMap.findValue(newWindow->getPAOId(),programToPutWindowIn) ||
                                energyExchangeProgramHashMap.findValue(newWindow->getPAOId(),programToPutWindowIn) )
                            {
                                RWOrdered& lmProgramControlWindowList = programToPutWindowIn->getLMProgramControlWindows();
                                lmProgramControlWindowList.insert(newWindow);
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << "Control Window with Id: " << newWindow->getPAOId() << " cannot find a program to associate with, in: " << __FILE__ << " at:" << __LINE__ << endl;
                                delete newWindow;
                            }
                        }
                    }//loading program control windows end
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Program Control Windows is: " << progWinTimer.elapsedTime() << endl;
                        progWinTimer.reset();
                    }*/


                    RWTimer caTimer;
                    caTimer.start();
                    {//loading control areas start
                        RWDBTable lmControlAreaTable = db.table("lmcontrolarea");
                        RWDBTable lmControlAreaProgramTable = db.table("lmcontrolareaprogram");
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
                                 << lmControlAreaProgramTable["lmprogramdeviceid"]
                                 << lmControlAreaProgramTable["userorder"]
                                 << lmControlAreaProgramTable["stoporder"]
                                 << lmControlAreaProgramTable["defaultpriority"]
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
                        selector.from(lmControlAreaProgramTable);
                        selector.from(dynamicLMControlAreaTable);

                        selector.where( yukonPAObjectTable["paobjectid"]==lmControlAreaTable["deviceid"] &&
                                        lmControlAreaTable["deviceid"].leftOuterJoin(lmControlAreaProgramTable["deviceid"]) &&
                                        lmControlAreaTable["deviceid"].leftOuterJoin(dynamicLMControlAreaTable["deviceid"]) );

                        selector.orderBy(yukonPAObjectTable["paobjectid"]);
                        selector.orderBy(lmControlAreaProgramTable["defaultpriority"]);
                        selector.orderBy(lmControlAreaProgramTable["userorder"]);
                        selector.orderByDescending(lmControlAreaProgramTable["stoporder"]);

                        /*if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/

                        RWDBReader rdr = selector.reader(conn);

                        CtiLMControlArea* currentLMControlArea = NULL;
                        RWDBNullIndicator isNull;
                        while( rdr() )
                        {
                            LONG tempControlAreaId = 0;
                            rdr["paobjectid"] >> tempControlAreaId;
                            if( currentLMControlArea == NULL ||
                                tempControlAreaId != currentLMControlArea->getPAOId() )
                            {
                                currentLMControlArea = new CtiLMControlArea(rdr);
                                _controlAreas->insert(currentLMControlArea);
                            }
                            rdr["lmprogramdeviceid"] >> isNull;
                            if( !isNull )
                            {
                                LONG tempProgramId = 0;
                                LONG tempUserOrder = 0;
                                LONG tempStopOrder = 0;
                                LONG tempDefaultPriority = 0;
                                rdr["lmprogramdeviceid"] >> tempProgramId;
                                rdr["userorder"] >> tempUserOrder;
                                rdr["stoporder"] >> tempStopOrder;
                                rdr["defaultpriority"] >> tempDefaultPriority;

                                RWOrdered& lmControlAreaProgramList = currentLMControlArea->getLMPrograms();

                                CtiLMProgramBase* programToPutInControlArea = NULL;
                                if( directProgramHashMap.findValue(tempProgramId,programToPutInControlArea) ||
                                    curtailmentProgramHashMap.findValue(tempProgramId,programToPutInControlArea) ||
                                    energyExchangeProgramHashMap.findValue(tempProgramId,programToPutInControlArea) )
                                {
                                    programToPutInControlArea->setUserOrder(tempUserOrder);
                                    programToPutInControlArea->setStopOrder(tempStopOrder);
                                    programToPutInControlArea->setDefaultPriority(tempDefaultPriority);
                                    lmControlAreaProgramList.insert(programToPutInControlArea);
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << "Cannot find LM Program with Id: " << programToPutInControlArea->getPAOId() << " to insert into Control Area: " << currentLMControlArea->getPAOName() << ", in: " << __FILE__ << " at:" << __LINE__ << endl;
                                }
                            }
                        }
                    }//loading control areas end
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Control Areas is: " << caTimer.elapsedTime() << endl;
                        caTimer.reset();
                    }*/


                    RWTimer trigTimer;
                    trigTimer.start();
                    {//loading control area triggers start
                        RWDBTable lmControlAreaTriggerTable = db.table("lmcontrolareatrigger");
                        RWDBTable dynamicLMControlAreaTriggerTable = db.table("dynamiclmcontrolareatrigger");

                        RWDBSelector selector = db.selector();
                        selector << lmControlAreaTriggerTable["deviceid"]
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

                        selector.where( lmControlAreaTriggerTable["deviceid"].leftOuterJoin(dynamicLMControlAreaTriggerTable["deviceid"]) &&
                                        lmControlAreaTriggerTable["triggernumber"].leftOuterJoin(dynamicLMControlAreaTriggerTable["triggernumber"]) );

                        selector.orderBy(lmControlAreaTriggerTable["deviceid"]);
                        selector.orderBy(lmControlAreaTriggerTable["triggernumber"]);

                        /*if( _LM_DEBUG )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }*/

                        RWDBReader rdr = selector.reader(conn);
                        while( rdr() )
                        {
                            CtiLMControlAreaTrigger* newTrigger = new CtiLMControlAreaTrigger(rdr);

                            /****************************************************************
                            *******  Inserting Trigger into the correct Control Area  *******
                            ****************************************************************/
                            LONG tempControlAreaId = 0;
                            rdr["deviceid"] >> tempControlAreaId;
                            for(LONG i=0;i<_controlAreas->entries();i++)
                            {
                                CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)((*_controlAreas)[i]);
                                if( currentLMControlArea->getPAOId() == tempControlAreaId )
                                {
                                    currentLMControlArea->getLMControlAreaTriggers().insert(newTrigger);
                                    break;
                                }
                            }
                        }
                    }//loading control area triggers end
                    /*if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Triggers is: " << trigTimer.elapsedTime() << endl;
                        trigTimer.reset();
                    }*/
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

        /*if( _LM_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << "DB Load Timer for entire LM DB: " << overallTimer.elapsedTime() << endl;
            overallTimer.reset();
        }*/
        _isvalid = TRUE;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Control areas reset" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        _reregisterforpoints = true;
        _lastdbreloadtime.now();
        if( !wasAlreadyRunning )
        {
            dumpAllDynamicData();
        }
        CtiLMExecutorFactory f;
        CtiLMExecutor* executor = f.createExecutor(new CtiLMControlAreaMsg(*_controlAreas));
        executor->Execute();
        delete executor;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    shutdown

    Dumps the sub bus list.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::shutdown()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Calling dumpAllDynamicData()" << endl;
    }*/
    dumpAllDynamicData();
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Done with dumpAllDynamicData()" << endl;
    }*/
    _controlAreas->clearAndDestroy();
    delete _controlAreas;
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
            int tempRefreshRate = atoi(str);
            if( tempRefreshRate > 0 )
            {
                refreshrate = tempRefreshRate;
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << var << " is ZERO!!!" << endl;
            }

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

        time_t start = time(NULL);

        RWDBDateTime currenttime = RWDBDateTime();
        ULONG tempsum = currenttime.seconds()+refreshrate;
        RWDBDateTime nextDatabaseRefresh = RWDBDateTime(RWTime(tempsum));
    
        while(TRUE)
        {
            rwRunnable().serviceCancellation();
    
            if( RWDBDateTime().seconds() >= nextDatabaseRefresh.seconds() )
            {
                RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Periodic restore of control area list from the database" << endl;
                }
    
                if( currenttime.rwdate() != RWDBDateTime().rwdate() )
                {//check to see if it is midnight
                    checkMidnightDefaultsForReset();
                }
                currenttime = RWDBDateTime();

                setValid(false);

                tempsum = currenttime.seconds()+refreshrate;
                nextDatabaseRefresh = RWDBDateTime(RWTime(tempsum));
            }
            else
            {
                rwRunnable().sleep(500);
            }
        }
    }
    catch(RWCancellation& )
    {
        throw;
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
bool CtiLMControlAreaStore::isValid()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _isvalid;
}

/*---------------------------------------------------------------------------
    setValid

    Sets the _isvalid flag
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::setValid(bool valid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _isvalid = valid;
}

/*---------------------------------------------------------------------------
    getReregisterForPoints

    Gets _reregisterforpoints
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::getReregisterForPoints()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _reregisterforpoints;
}

/*---------------------------------------------------------------------------
    setReregisterForPoints

    Sets _reregisterforpoints
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::setReregisterForPoints(bool reregister)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _reregisterforpoints = reregister;
}

/*---------------------------------------------------------------------------
    UpdateControlAreaDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the control area.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateControlAreaDisableFlagInDB(CtiLMControlArea* controlArea)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    bool returnBool = false;
    RWOrdered& controlAreas = *getControlAreas(RWDBDateTime().seconds());
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
                    if( _LM_DEBUG )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << text << ", " << additional << endl;
                    }
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
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
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
                if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << text << ", " << additional << endl;
                }
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

