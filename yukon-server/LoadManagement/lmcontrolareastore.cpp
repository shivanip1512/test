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

#include <map>

#include <rw/thr/thrfunc.h>
#include <rw/tphdict.h>

#include "mgr_holiday.h"
#include "mgr_season.h"

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
#include "lmgroupmct.h"
#include "lmgroupripple.h"
#include "lmgrouppoint.h"
#include "lmgroupsa105.h"
#include "lmgroupsa205.h"
#include "lmgroupsa305.h"
#include "lmgroupsadigital.h"
#include "lmgroupgolay.h"
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
#include "lmfactory.h"
#include "utility.h"
#include "rwutil.h"
#include "tbl_paoexclusion.h"

extern ULONG _LM_DEBUG;

struct id_hash{LONG operator()(LONG x) const { return x; } };

void lmprogram_delete(const LONG& program_id, CtiLMProgramBase*const& lm_program, void* d)
{
    delete lm_program;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMControlAreaStore::CtiLMControlAreaStore() : _isvalid(false), _reregisterforpoints(true), _lastdbreloadtime(RWDBDateTime(1990,1,1,0,0,0,0)), _wascontrolareadeletedflag(false)
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

    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+30 )
    {//is not valid and has been at .5 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
        reset();
    }

    return _controlAreas;
}


/*---------------------------------------------------------------------------
    findProgram

    Returns true if program exists with the given programID, false otherwise.
    
    If program isn't NULL it is set to point to the program with programID.
    
    If controlArea isn't NULL it is set to point to the control area that
    the program with programID is in.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::findProgram(LONG programID, CtiLMProgramBase** program, CtiLMControlArea** controlArea)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    RWOrdered* controlAreas = getControlAreas(RWDBDateTime().seconds());
    for(LONG i=0; i < controlAreas->entries(); i++)
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*) (*controlAreas)[i];
        RWOrdered lmPrograms = currentControlArea->getLMPrograms();
        for(LONG j=0; j < lmPrograms.entries(); j++)
        {
            CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*) lmPrograms[j];
            if(programID == currentLMProgram->getPAOId())
            {
                if(controlArea != NULL)
                {
                    *controlArea = currentControlArea;
                }
                if(program != NULL)
                {
                    *program = currentLMProgram;
                }
                return true;
            }
        }
    }
    return false;

}

/*----------------------------------------------------------------------------
  findGroupByCtrlHistPointID

  Attempts to locate a lmgroup given a control history point id.
  The given point could be any of the control history points.
  Returns 0 if no group is found.
  This member exists mostly for efficiency in updating groups when point
  data shows up.
----------------------------------------------------------------------------*/  
CtiLMGroupBase* CtiLMControlAreaStore::findGroupByPointID(long point_id)
{
    map< long, CtiLMGroupBase* >::iterator iter = _point_group_map.find(point_id);
    return (iter == _point_group_map.end() ? 0 : iter->second);
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
    try
    {
        RWOrdered temp_control_areas;
        map<long, CtiLMGroupBase*> temp_all_group_map;
        map<long, CtiLMGroupBase*> temp_point_group_map;
        
        LONG currentAllocations = ResetBreakAlloc();
        if( _LM_DEBUG & LM_DEBUG_EXTENDED )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Current Number of Historical Memory Allocations: " << currentAllocations << endl;
        }

        RWTimer overallTimer;
        overallTimer.start();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Starting Database Reload..." << endl;
        }
        
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
                if( conn.isValid() )
                {
                {   
                    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
                    if( _controlAreas->entries() > 0 )
                    {   //Save off current data to database so that if can be loaded by new objects on reload
                        dumpAllDynamicData();
                        saveAnyProjectionData();
                        saveAnyControlStringData(); 
                    }
                }
                    RWDBDateTime currentDateTime;
                    RWDBDatabase db = getDatabase();

                    RWTValHashMap<LONG,LONG,id_hash,equal_to<LONG> > controlPointHashMap;
                    {//loading controllable statuses
                        RWDBTable pointStatusTable = db.table("pointstatus");

                        RWDBSelector selector = db.selector();
                        selector.distinct();
                        selector << pointStatusTable["pointid"];

                        selector.from(pointStatusTable);

                        selector.where( pointStatusTable["controloffset"]==1 );

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);

                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            LONG tempPointId = 0;
                            rdr["pointid"] >> tempPointId;

                            controlPointHashMap.insert( tempPointId, tempPointId );
                        }
                    }

                    RWTimer allGroupTimer;
                    allGroupTimer.start();
                    
                    /* First load all the groups, and put them into a map by group id */
                    map< long, CtiLMGroupBase* > all_assigned_group_map; //remember which groups we have assigned
                    map< long, vector<CtiLMGroupBase*> > all_program_group_map;
                    
                {
                    RWDBTable paObjectTable = db.table("yukonpaobject");
                    RWDBTable deviceTable = db.table("device");
                    RWDBTable lmGroupTable = db.table("lmgroup");


                    RWDBSelector selector = db.selector();
                    selector << rwdbName("groupid", paObjectTable["paobjectid"])
                             << paObjectTable["category"]
                             << paObjectTable["paoclass"]
                             << paObjectTable["paoname"]
                             << paObjectTable["type"]
                             << paObjectTable["description"]
                             << paObjectTable["disableflag"]
                             << deviceTable["alarminhibit"]
                             << deviceTable["controlinhibit"]
                             << lmGroupTable["kwcapacity"];
                    selector.from(paObjectTable);
                    selector.from(deviceTable);
                    selector.from(lmGroupTable);

                    selector.where( paObjectTable["paobjectid"] == lmGroupTable["deviceid"] &&
                                    paObjectTable["paobjectid"] == deviceTable["deviceid"] &&
                                    paObjectTable["paobjectid"] > 0 ); // Stars gives meaning to an lmgroup with an id of 0, we don't care about it though
                
                        
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }

                    CtiLMGroupFactory lm_group_factory;
                    RWDBReader rdr = selector.reader(conn);
                    while(rdr())
                    {
                        string category;
                        string type;
                        rdr["category"] >> category;
                        rdr["type"] >> type;
                        CtiLMGroupBase* lm_group = lm_group_factory.createLMGroup(rdr);
                        temp_all_group_map.insert(make_pair(lm_group->getPAOId(), lm_group));
                        attachControlStringData(lm_group);
                    }

                } //end main group loading

            { /* Load up any group point specific information */
                RWDBTable lmGroupPointTable = db.table("lmgrouppoint");
                RWDBSelector selector = db.selector();
                selector << rwdbName("groupid", lmGroupPointTable["deviceid"])
                         << rwdbName("deviceid", lmGroupPointTable["deviceidusage"])
                         << rwdbName("pointid", lmGroupPointTable["pointidusage"])
                         << lmGroupPointTable["startcontrolrawstate"];
                selector.from(lmGroupPointTable);
                RWDBReader rdr = selector.reader(conn);
                while(rdr())
                {
                    int group_id;
                    int device_id;
                    int point_id;
                    int start_control_raw_state;

                    rdr >> group_id;
                    rdr >> device_id;
                    rdr >> point_id;
                    rdr >> start_control_raw_state;

                    map< long, CtiLMGroupBase* >::iterator iter = temp_all_group_map.find(group_id);
                    if(iter != temp_all_group_map.end())
                    {
                        CtiLMGroupPoint* lm_group = (CtiLMGroupPoint*) iter->second;
                        lm_group->setDeviceIdUsage(device_id);
                        lm_group->setPointIdUsage(point_id);
                        lm_group->setStartControlRawState(start_control_raw_state);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> dout_guard(dout);
                        dout << RWTime() << " **Checkpoint** " <<  " Rows exist in the LMGroupPoint table exist but do not correspond with any lm groups already loaded.  Either groups didn't get loaded correctly or the LMGroupPoint table has missing constraints?" << __FILE__ << "(" << __LINE__ << ")" << endl;
                    }
                } 
            }// end loading group point specific info
                    
            { /* Start loading ripple group specific info */
                RWDBTable lmGroupRippleTable = db.table("lmgroupripple");
                RWDBSelector selector = db.selector();

                selector << rwdbName("groupid", lmGroupRippleTable["deviceid"])
                         << lmGroupRippleTable["shedtime"];

                selector.from(lmGroupRippleTable);
                
                RWDBReader rdr = selector.reader(conn);
                while(rdr())
                {
                    int group_id;
                    int shed_time;

                    rdr >> group_id;
                    rdr >> shed_time;

                    map< long, CtiLMGroupBase* >::iterator iter = temp_all_group_map.find(group_id);
                    if(iter != temp_all_group_map.end())
                    {
                        CtiLMGroupRipple* lm_group = (CtiLMGroupRipple*) iter->second;
                        lm_group->setShedTime(shed_time);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> dout_guard(dout);
                        dout << RWTime() << " **Checkpoint** " <<  " Rows exist in the LMGroupRipple table exist but do not correspond with any lm groups already loaded.  Either groups didn't get loaded correctly or the LMGroupRipple table has missing constraints?" << __FILE__ << "(" << __LINE__ << ")" << endl;
                    }
                }
            } // end loading ripple group specific info */

        { /* Start loading sa dumb group specific info */
            /* We would load the nominal timeout here, I don't think it is used yet,
               so add it if necessary */
        } // end loading sa dumb group specific info */
                
            /* Attach any points necessary to groups */
            {
                temp_point_group_map.clear();

                RWDBTable pointTable = db.table("point");
                RWDBTable lmGroupTable = db.table("lmgroup");

                RWDBSelector selector = db.selector();
                selector << pointTable["paobjectid"]
                         << pointTable["pointid"]
                         << pointTable["pointoffset"]
                         << pointTable["pointtype"];

                selector.from(pointTable);
                selector.from(lmGroupTable);
                    
                selector.where( pointTable["paobjectid"] == lmGroupTable["deviceid"] );
                    
                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);

                while( rdr() )
                {
                    long group_id;
                    int point_id;
                    string point_type;
                    int point_offset;

                    rdr["paobjectid"] >> group_id;
                    rdr["pointid"] >> point_id;
                    rdr["pointtype"] >> point_type;
                    rdr["pointoffset"] >> point_offset;

                    map< long, CtiLMGroupBase* >::iterator iter = temp_all_group_map.find(group_id);
                    CtiLMGroupBase* lm_group = iter->second;

                    switch(resolvePointType(point_type.data()))
                    {
                    case AnalogPointType:
                        switch(point_offset)
                        {
                        case DAILYCONTROLHISTOFFSET:
                            lm_group->setHoursDailyPointId(point_id);
                            temp_point_group_map.insert(make_pair(point_id,lm_group));
                            break;
                        case MONTHLYCONTROLHISTOFFSET:
                            lm_group->setHoursMonthlyPointId(point_id);
                            temp_point_group_map.insert(make_pair(point_id,lm_group));
                            break;
                        case SEASONALCONTROLHISTOFFSET:
                            lm_group->setHoursSeasonalPointId(point_id);
                            temp_point_group_map.insert(make_pair(point_id,lm_group));
                            break;
                        case ANNUALCONTROLHISTOFFSET:
                            lm_group->setHoursAnnuallyPointId(point_id);
                            temp_point_group_map.insert(make_pair(point_id,lm_group));
                            break;
                        default:
                        {
                            CtiLockGuard<CtiLogger> dout_guard(dout);
                            dout << RWTime() << " **Checkpoint** " <<  " Unknown point offset: " << point_offset
                                 << "  Expected daily, monthly, seasonal, or annual control history point offset" << __FILE__ << "(" << __LINE__ << ")" << endl;
                        }
                        break;
                        }
                        break;
                    case StatusPointType:
                        if(point_offset == 0)
                        {
                            long control_status_point_id;
                            if(controlPointHashMap.findValue(point_id, control_status_point_id))
                            {
                                lm_group->setControlStatusPointId(control_status_point_id);
                                temp_point_group_map.insert(make_pair(point_id,lm_group));
                            }
                        }
                        break;
                    default:
                    {
                        CtiLockGuard<CtiLogger> dout_guard(dout);
                        dout << RWTime() << " **Checkpoint** " <<  " Unknown point type:  " << resolvePointType(point_type.data()) << __FILE__ << "(" << __LINE__ << ")" << endl;
                    }
                    }
                }
            }

                /* Load dynamic group information */
            {
                RWDBTable dynamicLMGroupTable = db.table("dynamiclmgroup");
                RWDBSelector selector = db.selector();
                selector << rwdbName("groupid", dynamicLMGroupTable["deviceid"])
                         << dynamicLMGroupTable["groupcontrolstate"]
                         << dynamicLMGroupTable["currenthoursdaily"]
                         << dynamicLMGroupTable["currenthoursmonthly"]
                         << dynamicLMGroupTable["currenthoursseasonal"]
                         << dynamicLMGroupTable["currenthoursannually"]
                         << dynamicLMGroupTable["lastcontrolsent"]
                         << dynamicLMGroupTable["timestamp"]
                         << dynamicLMGroupTable["controlstarttime"]
                         << dynamicLMGroupTable["controlcompletetime"]
                         << dynamicLMGroupTable["nextcontroltime"]
                         << dynamicLMGroupTable["internalstate"];
                selector.from(dynamicLMGroupTable);

                RWDBReader rdr = selector.reader(conn);
                while(rdr())
                {
                    long group_id;
                    long group_control_state;
                    long cur_hours_daily;
                    long cur_hours_monthly;
                    long cur_hours_seasonal;
                    long cur_hours_annually;
                    RWDBDateTime last_control_sent;
                    RWDBDateTime timestamp;
                    RWDBDateTime control_start_time;
                    RWDBDateTime control_complete_time;
                    RWDBDateTime next_control_time;
                    long internal_state;

                    rdr["groupid"] >> group_id;
                    rdr["groupcontrolstate"] >> group_control_state;
                    rdr["currenthoursdaily"] >> cur_hours_daily;
                    rdr["currenthoursmonthly"] >> cur_hours_monthly;
                    rdr["currenthoursseasonal"] >> cur_hours_seasonal;
                    rdr["currenthoursannually"] >> cur_hours_annually;
                    rdr["lastcontrolsent"] >> last_control_sent;
                    rdr["timestamp"] >> timestamp;
                    rdr["controlstarttime"] >> control_start_time;
                    rdr["controlcompletetime"] >> control_complete_time;
                    rdr["nextcontroltime"] >> next_control_time;
                    rdr["internalstate"] >> internal_state;
                    
                    CtiLMGroupBase* lm_group = temp_all_group_map.find(group_id)->second;
                    lm_group->setGroupControlState(group_control_state);
                    lm_group->setCurrentHoursDaily(cur_hours_daily);
                    lm_group->setCurrentHoursMonthly(cur_hours_monthly);
                    lm_group->setCurrentHoursSeasonal(cur_hours_seasonal);
                    lm_group->setCurrentHoursAnnually(cur_hours_annually);
                    lm_group->setLastControlSent(last_control_sent);
                    //timestamp?
                    lm_group->setControlStartTime(control_start_time);
                    lm_group->setControlCompleteTime(control_complete_time);
                    lm_group->setNextControlTime(next_control_time);
                    lm_group->setInternalState(internal_state);
                    lm_group->setDirty(false);
                    lm_group->_insertDynamicDataFlag = FALSE;
                }
            }
                /* Now lets load up info about macro groups */
                map< long, vector<long> > group_macro_map;  //ownerid, <childid>
            {
                RWDBTable genericMacroTable = db.table("genericmacro");
                RWDBTable lmGroupTable = db.table("lmgroup");

                RWDBSelector selector = db.selector();
                selector << genericMacroTable["ownerid"]
                         << genericMacroTable["childid"];

                selector.from(genericMacroTable);
                selector.from(lmGroupTable);

                selector.where(genericMacroTable["ownerid"] == lmGroupTable["deviceid"]);
                selector.orderBy(genericMacroTable["ownerid"]);
                selector.orderBy(genericMacroTable["childorder"]);

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);
                map<long, vector<long> >::iterator iter;
                while(rdr())
                {
                    long owner_id;
                    long child_id;

                    rdr["ownerid"] >> owner_id;
                    rdr["childid"] >> child_id;

                    iter = group_macro_map.find(owner_id);
                    if(iter == group_macro_map.end())
                    {
                        vector<long> child_vec;
                        child_vec.push_back(child_id);
                        group_macro_map.insert(make_pair(owner_id, child_vec));
                    }
                    else
                    {
                        iter->second.push_back(child_id);
                    }
                }
            }
                      
            /* now lets load info about how groups attach to programs */
            {
                RWDBTable lmProgramDirectGroupTable = db.table("lmprogramdirectgroup");

                RWDBSelector selector = db.selector();
                selector << rwdbName("programid", lmProgramDirectGroupTable["deviceid"])
                         << rwdbName("groupid", lmProgramDirectGroupTable["lmgroupdeviceid"]);

                selector.from(lmProgramDirectGroupTable);
                selector.orderBy(lmProgramDirectGroupTable["deviceid"]);

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);
                map<long, vector<CtiLMGroupBase*> >::iterator cur_iter;
                long cur_program = -1;
                while(rdr())
                {
                    long program_id;
                    long group_id;
                    rdr["programid"] >> program_id;
                    rdr["groupid"] >> group_id;

                    cur_iter = all_program_group_map.find(program_id);
                    if(cur_iter == all_program_group_map.end())
                    {
                        vector<CtiLMGroupBase*> group_vec;
                        CtiLMGroupBase* lm_group = temp_all_group_map.find(group_id)->second;

                        map<long, vector<long> >::iterator macro_iter = group_macro_map.find(lm_group->getPAOId());
                        if(macro_iter != group_macro_map.end())
                        { //must be a macro group
                            vector<long> macro_vec = macro_iter->second;
                            for(vector<long>::iterator iter = macro_vec.begin();
                                iter != macro_vec.end();
                                iter++)
                            { //iterate over all the children in this macro group and insert them in place of the owner (macrogroup)
                                CtiLMGroupBase* child_group = temp_all_group_map.find(*iter)->second;
                                group_vec.push_back(child_group);
                                child_group->setGroupOrder(group_vec.size());
                                all_assigned_group_map.insert(make_pair(child_group->getPAOId(), child_group));
                            }
                        }
                        else
                        { //not a macro group, assign it to the program
                            group_vec.push_back(lm_group);
                            lm_group->setGroupOrder(group_vec.size());
                            all_program_group_map.insert(make_pair(program_id, group_vec));
                            all_assigned_group_map.insert(make_pair(lm_group->getPAOId(), lm_group));
                        }
                    }
                    else
                    {
                        CtiLMGroupBase* lm_group = temp_all_group_map.find(group_id)->second;
                        
                        map<long, vector<long> >::iterator macro_iter = group_macro_map.find(lm_group->getPAOId());
                        if(macro_iter != group_macro_map.end())
                        { //must be a macro group
                            vector<long> macro_vec = macro_iter->second;
                            for(vector<long>::iterator iter = macro_vec.begin();
                                iter != macro_vec.end();
                                iter++)
                            { //iterate over all the children in this macro group and insert them in place of the owner (macrogroup)
                                CtiLMGroupBase* child_group = temp_all_group_map.find(*iter)->second;
                                cur_iter->second.push_back(child_group);
                                child_group->setGroupOrder(cur_iter->second.size());
                                all_assigned_group_map.insert(make_pair(child_group->getPAOId(), child_group));
                            }
                        }
                        else
                        {
                            cur_iter->second.push_back(lm_group);
                            lm_group->setGroupOrder(cur_iter->second.size());
                            all_assigned_group_map.insert(make_pair(lm_group->getPAOId(), lm_group));
                        }

                    }
                }
            }                   
            
            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "DB Load Timer for All Groups is: " << allGroupTimer.elapsedTime() << endl;
                dout << "Loaded a total of " << temp_all_group_map.size() << " groups, " << group_macro_map.size() << " of them are in macro groups" << endl;
                dout << all_program_group_map.size() << "==" << all_assigned_group_map.size() << " groups are assigned to programs" << endl;
                allGroupTimer.reset();
            }


                 
                    
#ifdef _THIS_DOESNT_WORK_ON_ORACLE
                    
                    RWDBTable yukonPAObjectTable = db.table("yukonpaobject");

                    RWOrdered allGroupList;
                    multimap< long, CtiLMGroupBase* > all_program_group_map_bung;
                    map< long, CtiLMGroupBase* > _all_group_map;
                    
                    RWTimer allGroupTimer;
                    allGroupTimer.start();


                    {//loading direct groups start
                                                
                        //On the first pass we will run into all the macro groups attached
                        //to lm programs, store them in here for use in the second pass
                        //where we need to determine the rank of each group in a macro group
                        //as well as the attached program id
                        // key = macrogroupid, value = pair<programid, grouporder>
                        map< long, pair< long, unsigned int > > macrogroup_map;
                    {
                        
/* first pass creates and stores all the regular groups */

                        RWDBTable paObjectTable = db.table("yukonpaobject");
                        RWDBTable deviceTable = db.table("device");
                        RWDBTable lmProgramDirectGroupTable = db.table("lmprogramdirectgroup");
                        RWDBTable lmGroupTable = db.table("lmgroup");
                        RWDBTable lmGroupPointTable = db.table("lmgrouppoint");
                        RWDBTable lmGroupRippleTable = db.table("lmgroupripple");
                        RWDBTable lmGroupSASimpleTable = db.table("lmgroupsasimple");
                        RWDBTable dynamicLMGroupTable = db.table("dynamiclmgroup");

                        RWDBSelector selector = db.selector();
                        selector << rwdbName("groupid", paObjectTable["paobjectid"])
                        << paObjectTable["category"]
                        << paObjectTable["paoclass"]
                        << paObjectTable["paoname"]
                        << paObjectTable["type"]
                        << paObjectTable["description"]
                        << paObjectTable["disableflag"]
                        << deviceTable["alarminhibit"]
                        << deviceTable["controlinhibit"]
                        << lmProgramDirectGroupTable["grouporder"]
                        << rwdbName("programid", lmProgramDirectGroupTable["deviceid"])
                        << lmGroupTable["kwcapacity"]
                        << rwdbName("pointdeviceidusage", lmGroupPointTable["deviceidusage"])
                        << rwdbName("pointpointidusage", lmGroupPointTable["pointidusage"])
                        << rwdbName("pointstartcontrolrawstate", lmGroupPointTable["startcontrolrawstate"])
                        << rwdbName("rippleshedtime", lmGroupRippleTable["shedtime"])
                        << rwdbName("sasimplenominaltimeout", lmGroupSASimpleTable["nominaltimeout"])
                        << dynamicLMGroupTable["groupcontrolstate"]
                        << dynamicLMGroupTable["currenthoursdaily"]
                        << dynamicLMGroupTable["currenthoursmonthly"]
                        << dynamicLMGroupTable["currenthoursseasonal"]
                        << dynamicLMGroupTable["currenthoursannually"]
                        << dynamicLMGroupTable["lastcontrolsent"]
                        << dynamicLMGroupTable["timestamp"]
                        << dynamicLMGroupTable["lmprogramid"]
                        << dynamicLMGroupTable["controlstarttime"]
                        << dynamicLMGroupTable["controlcompletetime"]
                        << dynamicLMGroupTable["nextcontroltime"]
                        << dynamicLMGroupTable["internalstate"];

                        selector.from(paObjectTable);
                        selector.from(deviceTable);
                        selector.from(lmProgramDirectGroupTable);
                        selector.from(lmGroupTable);
                        selector.from(lmGroupPointTable);
                        selector.from(lmGroupRippleTable);
                        selector.from(lmGroupSASimpleTable);
                        selector.from(dynamicLMGroupTable);

                        selector.where(lmGroupTable["deviceid"] == lmProgramDirectGroupTable["lmgroupdeviceid"] &&
                                       paObjectTable["paobjectid"] == lmProgramDirectGroupTable["lmgroupdeviceid"] &&
                                       deviceTable["deviceid"] == lmProgramDirectGroupTable["lmgroupdeviceid"] &&
                                       paObjectTable["paobjectid"].leftOuterJoin(lmGroupPointTable["deviceid"]) &&
                                       paObjectTable["paobjectid"].leftOuterJoin(lmGroupRippleTable["deviceid"]) &&
                                       paObjectTable["paobjectid"].leftOuterJoin(lmGroupSASimpleTable["groupid"]) &&
                                       paObjectTable["paobjectid"].leftOuterJoin(dynamicLMGroupTable["deviceid"]) &&
                                       lmProgramDirectGroupTable["deviceid"].leftOuterJoin(dynamicLMGroupTable["lmprogramid"]) );
                        
                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);

                        CtiLMGroupFactory lm_group_factory;
                        while(rdr())
                        {
                            //check to see if we got a macro group
                            string category;
                            string type;
                            rdr["category"] >> category;
                            rdr["type"] >> type;
                            if(resolvePAOType(category.data(),type.data()) == TYPE_MACRO)
                            {   // We found a macro group, save it away so we remember
                                // the programid and order on the seconds pass.
                                long groupid;
                                pair< long, unsigned int > macrogroup_info;
                                rdr["groupid"] >> groupid;
                                rdr["programid"] >> macrogroup_info.first;
                                rdr["grouporder"] >> macrogroup_info.second;
                                if(!macrogroup_map.insert( make_pair(groupid, macrogroup_info)).second)
                                {
                                    CtiLockGuard<CtiLogger> dout_guard(dout);
                                    dout << RWTime() << " **Checkpoint** " <<  " Loaded macro groupid: " << groupid << " twice!" << __FILE__ << "(" << __LINE__ << ")" << endl;
                                }
                            }
                            else
                            {
                                int group_order;
                                long programid;
                                
                                rdr["grouporder"] >> group_order;
                                rdr["programid"] >> programid;
                                
                                CtiLMGroupBase* lm_group = lm_group_factory.createLMGroup(rdr);
                                lm_group->setGroupOrder(group_order);
                                lm_group->setChildOrder(-1);
                                lm_group->setLMProgramId(programid);
                                
                                attachControlStringData(lm_group);
                                allGroupList.insert(lm_group);
                                all_program_group_map_bung.insert(make_pair(programid, lm_group));
                                _all_group_map.insert(make_pair(lm_group->getPAOId(), lm_group));
                            }
                        }
                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> dout_guard(dout);
                            dout << RWTime() << " - Found " << macrogroup_map.size() << " macrogroups, and " << all_program_group_map_bung.size() << " LMGroups so far." << endl;
                        }
                    }

                {  // second pass creates and stores all the groups in macro groups 
                    /* NOTE:
                       The point of this query is to load macro groups.
                       If a group is attached directly to a program via lmprogramdirectgroup as well
                       as part of a macro group then we will get more rows than expected back.  This is
                       due to joining with the dynamiclmgroup table.
                       This is ok as long as we check to see that the programid corresponds to a program
                       that a macro group is attached to. */
                  
                    RWDBTable paObjectTable = db.table("yukonpaobject");
                    RWDBTable deviceTable = db.table("device");
                    RWDBTable genericMacroTable = db.table("genericmacro");
                    RWDBTable lmGroupTable = db.table("lmgroup");
                    RWDBTable lmGroupPointTable = db.table("lmgrouppoint");
                    RWDBTable lmGroupRippleTable = db.table("lmgroupripple");
                    RWDBTable lmGroupSASimpleTable = db.table("lmgroupsasimple");
                    RWDBTable dynamicLMGroupTable = db.table("dynamiclmgroup");

                    RWDBSelector selector = db.selector();
                    selector << rwdbName("groupid", paObjectTable["paobjectid"])
                             << paObjectTable["category"]
                             << paObjectTable["paoclass"]
                             << paObjectTable["paoname"]
                             << paObjectTable["type"]
                             << paObjectTable["description"]
                             << paObjectTable["disableflag"]
                             << deviceTable["alarminhibit"]
                             << deviceTable["controlinhibit"]
                             << genericMacroTable["childorder"]
                             << genericMacroTable["ownerid"]
                             << lmGroupTable["kwcapacity"]
                             << rwdbName("pointdeviceidusage", lmGroupPointTable["deviceidusage"])
                             << rwdbName("pointpointidusage", lmGroupPointTable["pointidusage"])
                             << rwdbName("pointstartcontrolrawstate", lmGroupPointTable["startcontrolrawstate"])
                             << rwdbName("rippleshedtime", lmGroupRippleTable["shedtime"])
                             << rwdbName("sasimplenominaltimeout", lmGroupSASimpleTable["nominaltimeout"])
                             << dynamicLMGroupTable["groupcontrolstate"]
                             << dynamicLMGroupTable["currenthoursdaily"]
                             << dynamicLMGroupTable["currenthoursmonthly"]
                             << dynamicLMGroupTable["currenthoursseasonal"]
                             << dynamicLMGroupTable["currenthoursannually"]
                             << dynamicLMGroupTable["lastcontrolsent"]
                             << dynamicLMGroupTable["timestamp"]
                             << dynamicLMGroupTable["lmprogramid"]
                             << dynamicLMGroupTable["controlstarttime"]
                             << dynamicLMGroupTable["controlcompletetime"]
                             << dynamicLMGroupTable["nextcontroltime"]
                             << dynamicLMGroupTable["internalstate"];

                    selector.from(paObjectTable);
                    selector.from(deviceTable);
                    selector.from(genericMacroTable);
                    selector.from(lmGroupTable);
                    selector.from(lmGroupPointTable);
                    selector.from(lmGroupRippleTable);
                    selector.from(lmGroupSASimpleTable);
                    selector.from(dynamicLMGroupTable);

                    selector.where(lmGroupTable["deviceid"] == genericMacroTable["childid"]  &&
                                   paObjectTable["paobjectid"] == genericMacroTable["childid"] &&
                                   deviceTable["deviceid"] == genericMacroTable["childid"] &&
                                   paObjectTable["paobjectid"].leftOuterJoin(lmGroupPointTable["deviceid"]) &&
                                   paObjectTable["paobjectid"].leftOuterJoin(lmGroupRippleTable["deviceid"]) &&
                                   paObjectTable["paobjectid"].leftOuterJoin(lmGroupSASimpleTable["groupid"]) &&
                                   paObjectTable["paobjectid"].leftOuterJoin(dynamicLMGroupTable["deviceid"]));

                        
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);

                    CtiLMGroupFactory lm_group_factory;
                      
                    while(rdr())
                    {
                        int program_id;
                        int child_order;
                        int ownerid;

                        rdr["childorder"] >> child_order;
                        rdr["ownerid"] >> ownerid;
                        rdr["lmprogramid"] >> program_id;
                        
                        // Find the saved macro group information
                        pair< long, unsigned int > macrogroup_info = macrogroup_map.find(ownerid)->second;

                        if(macrogroup_info.first == program_id)
                        {
                            CtiLMGroupBase* lm_group = lm_group_factory.createLMGroup(rdr);

                            lm_group->setChildOrder(child_order);
                            lm_group->setLMProgramId(macrogroup_info.first);
                            lm_group->setGroupOrder(macrogroup_info.second);
                        
                            attachControlStringData(lm_group);
                            allGroupList.insert(lm_group);
                            all_program_group_map_bung.insert(make_pair(macrogroup_info.first, lm_group));
                            _all_group_map.insert(make_pair(lm_group->getPAOId(), lm_group));
                        }
                    }

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> dout_guard(dout);
                    dout << RWTime() << " - Loaded a total of " << all_program_group_map_bung.size() << " LMGroups" << endl;
                }
            
                }// end second group pass - make sure to fix up group ordering after groups are linked with programs below.
                { // Begin loading group point information
                    _point_group_map.clear();
                    
                    RWDBTable pointTable = db.table("point");
                    RWDBTable lmGroupTable = db.table("lmgroup");

                    RWDBSelector selector = db.selector();
                    selector << pointTable["paobjectid"]
                             << pointTable["pointid"]
                             << pointTable["pointoffset"]
                             << pointTable["pointtype"];

                    selector.from(pointTable);
                    selector.from(lmGroupTable);
                    
                    selector.where( pointTable["paobjectid"] == lmGroupTable["deviceid"] );
                    
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);

                    while( rdr() )
                    {
                        long group_id;
                        int point_id;
                        string point_type;
                        int point_offset;

                        rdr["paobjectid"] >> group_id;
                        rdr["pointid"] >> point_id;
                        rdr["pointtype"] >> point_type;
                        rdr["pointoffset"] >> point_offset;

                        map< long, CtiLMGroupBase* >::iterator iter = _all_group_map.find(group_id);
                        if(iter == _all_group_map.end())
                        {  //Group may not be attached and so not loaded, ok
                            continue;
                        }
                        
                        CtiLMGroupBase* lm_group = iter->second;

                        switch(resolvePointType(point_type.data()))
                        {
                        case AnalogPointType:
                            switch(point_offset)
                            {
                            case DAILYCONTROLHISTOFFSET:
                                lm_group->setHoursDailyPointId(point_id);
                                _point_group_map.insert(make_pair(point_id,lm_group));
                                break;
                            case MONTHLYCONTROLHISTOFFSET:
                                lm_group->setHoursMonthlyPointId(point_id);
                                _point_group_map.insert(make_pair(point_id,lm_group));
                                break;
                            case SEASONALCONTROLHISTOFFSET:
                                lm_group->setHoursSeasonalPointId(point_id);
                                _point_group_map.insert(make_pair(point_id,lm_group));
                                break;
                            case ANNUALCONTROLHISTOFFSET:
                                lm_group->setHoursAnnuallyPointId(point_id);
                                _point_group_map.insert(make_pair(point_id,lm_group));
                                break;
                            default:
                            {
                                CtiLockGuard<CtiLogger> dout_guard(dout);
                                dout << RWTime() << " **Checkpoint** " <<  " Unknown point offset: " << point_offset
                                     << "  Expected daily, monthly, seasonal, or annual control history point offset" << __FILE__ << "(" << __LINE__ << ")" << endl;
                            }
                                break;
                            }
                            break;
                        case StatusPointType:
                            if(point_offset == 0)
                            {
                                long control_status_point_id;
                                if(controlPointHashMap.findValue(point_id, control_status_point_id))
                                {
                                    lm_group->setControlStatusPointId(control_status_point_id);
                                    _point_group_map.insert(make_pair(point_id,lm_group));
                                }
                            }
                            break;
                        default:
                        {
                            CtiLockGuard<CtiLogger> dout_guard(dout);
                            dout << RWTime() << " **Checkpoint** " <<  " Unknown point type:  " << resolvePointType(point_type.data()) << __FILE__ << "(" << __LINE__ << ")" << endl;
                        }
                        }
                    }
                }
#endif          
#ifdef _OLD_                    
                        RWDBTable lmGroupMacroExpanderView = db.table("lmgroupmacroexpander_view");
                        RWDBTable lmGroupPointTable = db.table("lmgrouppoint");
                        RWDBTable lmGroupRippleTable = db.table("lmgroupripple");
                        RWDBTable lmGroupSASimpleTable = db.table("lmgroupsasimple");
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
                                 << rwdbName("sasimplenominaltimeout", lmGroupSASimpleTable["nominaltimeout"])
                                 << dynamicLMGroupTable["groupcontrolstate"]
                                 << dynamicLMGroupTable["currenthoursdaily"]
                                 << dynamicLMGroupTable["currenthoursmonthly"]
                                 << dynamicLMGroupTable["currenthoursseasonal"]
                                 << dynamicLMGroupTable["currenthoursannually"]
                                 << dynamicLMGroupTable["lastcontrolsent"]
                                 << dynamicLMGroupTable["timestamp"]
                                 << dynamicLMGroupTable["lmprogramid"]
                                 << dynamicLMGroupTable["controlstarttime"]
                                 << dynamicLMGroupTable["controlcompletetime"]
                                 << dynamicLMGroupTable["nextcontroltime"]
                                 << dynamicLMGroupTable["internalstate"]
                                 << pointTable["pointid"]
                                 << pointTable["pointoffset"]
                                 << pointTable["pointtype"];

                        selector.from(lmGroupMacroExpanderView);
                        selector.from(lmGroupPointTable);
                        selector.from(lmGroupRippleTable);
                        selector.from(lmGroupSASimpleTable);
                        selector.from(dynamicLMGroupTable);
                        selector.from(pointTable);

                        selector.where( ( ( lmGroupMacroExpanderView["childid"].isNull() && 
                                            lmGroupMacroExpanderView["paobjectid"]==lmGroupMacroExpanderView["lmgroupdeviceid"] ) ||
                                          ( !lmGroupMacroExpanderView["childid"].isNull() &&
                                            lmGroupMacroExpanderView["paobjectid"]==lmGroupMacroExpanderView["childid"] ) ) &&
                                        lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(lmGroupPointTable["deviceid"]) &&
                                        lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(lmGroupRippleTable["deviceid"]) &&
                                        lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(lmGroupSASimpleTable["groupid"]) &&
                                        lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(dynamicLMGroupTable["deviceid"]) &&
                                        lmGroupMacroExpanderView["deviceid"].leftOuterJoin(dynamicLMGroupTable["lmprogramid"]) &&
                                        lmGroupMacroExpanderView["paobjectid"].leftOuterJoin(pointTable["paobjectid"]) );

                        selector.orderBy( lmGroupMacroExpanderView["deviceid"] );
                        selector.orderBy( lmGroupMacroExpanderView["grouporder"] );
                        selector.orderBy( lmGroupMacroExpanderView["childorder"] );

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);

                        CtiLMGroupBase* currentLMGroupBase = NULL;
                        RWDBNullIndicator isNull;
                        while ( rdr() )
                        {
                            LONG tempPAObjectId = 0;
                            LONG tempProgramId = 0;
                            RWCString tempPAOCategory;
                            RWCString tempPAOType;
                            rdr["category"] >> tempPAOCategory;
                            rdr["type"] >> tempPAOType;
                            rdr["paobjectid"] >> tempPAObjectId;
                            rdr["deviceid"] >> tempProgramId;

                            if( currentLMGroupBase != NULL &&
                                tempPAObjectId == currentLMGroupBase->getPAOId() &&
                                tempProgramId == currentLMGroupBase->getLMProgramId() )
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
                                    else if( resolvePointType(tempPointType) == StatusPointType )
                                    {
                                        if( tempPointOffset == 0 )
                                        {
                                            LONG controlStatusPointId = -10000000;
                                            if( controlPointHashMap.findValue(tempPointId,controlStatusPointId) )
                                            {//just trying to see if this peusdo status point is controllable and has controlOffset of 1
                                                currentLMGroupBase->setControlStatusPointId(controlStatusPointId);
                                            }
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
                                else if( tempTypeInt == TYPE_LMGROUP_MCT )
                                {
                                    currentLMGroupBase = new CtiLMGroupMCT(rdr);
                                }
                                else if( tempTypeInt == TYPE_LMGROUP_SA105 )
                                {
                                    currentLMGroupBase = new CtiLMGroupSA105(rdr);
                                }
                                else if( tempTypeInt == TYPE_LMGROUP_SA205 )
                                {
                                    currentLMGroupBase = new CtiLMGroupSA205(rdr);
                                }
                                else if( tempTypeInt == TYPE_LMGROUP_SA305 )
                                {
                                    currentLMGroupBase = new CtiLMGroupSA305(rdr);
                                }
                                else if( tempTypeInt == TYPE_LMGROUP_SADIGITAL )
                                {
                                    currentLMGroupBase = new CtiLMGroupSADigital(rdr);
                                }
                                else if( tempTypeInt == TYPE_LMGROUP_GOLAY )
                                {
                                    currentLMGroupBase = new CtiLMGroupGolay(rdr);
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - Group device type = " << tempPAOType << " not supported yet.  In: " << __FILE__ << " at:" << __LINE__ << endl;
                                }

                                if( currentLMGroupBase != NULL )
                                {
                                    rdr["pointid"] >> isNull;
                                    if( !isNull )
                                    {//have to do this block to get the controllable pseudo status point
                                        LONG tempPointId = -1000;
                                        LONG tempPointOffset = -1000;
                                        RWCString tempPointType = "(none)";
                                        rdr["pointid"] >> tempPointId;
                                        rdr["pointoffset"] >> tempPointOffset;
                                        rdr["pointtype"] >> tempPointType;
                                        if( resolvePointType(tempPointType) == StatusPointType )
                                        {
                                            if( tempPointOffset == 0 )
                                            {
                                                LONG controlStatusPointId = -10000000;
                                                if( controlPointHashMap.findValue(tempPointId,controlStatusPointId) )
                                                {//just trying to see if this peusdo status point is controllable and has controlOffset of 1
                                                    currentLMGroupBase->setControlStatusPointId(controlStatusPointId);
                                                }
                                            }
                                            /*else
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - Undefined Cap Bank point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
                                            }*/
                                        }
                                    }
                                    attachControlStringData(currentLMGroupBase);
                                    allGroupList.insert(currentLMGroupBase);
                                }
                            }
                        }
                    }//loading direct groups end
#endif                  



                    RWTValHashMap<LONG,CtiLMProgramBase*,id_hash,equal_to<LONG> > directProgramHashMap;

                    RWDBTable lmProgramTable = db.table("lmprogram_view");
                    RWDBTable dynamicLMProgramTable = db.table("dynamiclmprogram");

                    RWTimer dirProgsTimer;
                    dirProgsTimer.start();
                    {//loading direct programs start
                        RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                        RWDBTable lmProgramDirectTable = db.table("lmprogramdirect");
                        RWDBTable dynamicLMProgramDirectTable = db.table("dynamiclmprogramdirect");
                        RWDBTable pointTable = db.table("point");

                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                                 << yukonPAObjectTable["category"]
                                 << yukonPAObjectTable["paoclass"]
                                 << yukonPAObjectTable["paoname"]
                                 << yukonPAObjectTable["type"]
                                 << yukonPAObjectTable["description"]
                                 << yukonPAObjectTable["disableflag"]
                                 << lmProgramTable["controltype"]
                                 << lmProgramTable["constraintid"]
                                 << lmProgramTable["constraintname"]
                                 << lmProgramTable["availableweekdays"]
                                 << lmProgramTable["maxhoursdaily"]
                                 << lmProgramTable["maxhoursmonthly"]
                                 << lmProgramTable["maxhoursseasonal"]
                                 << lmProgramTable["maxhoursannually"]
                                 << lmProgramTable["minactivatetime"]
                                 << lmProgramTable["minrestarttime"]
                                 << lmProgramTable["maxdailyops"]
                                 << lmProgramTable["maxactivatetime"]
                                 << lmProgramTable["holidayscheduleid"]
                                 << lmProgramTable["seasonscheduleid"]
                                 << lmProgramDirectTable["heading"]
                                 << lmProgramDirectTable["messageheader"]
                                 << lmProgramDirectTable["messagefooter"]
                                 << lmProgramDirectTable["notifyoffset"]
                                 << dynamicLMProgramTable["programstate"]
                                 << dynamicLMProgramTable["reductiontotal"]
                                 << dynamicLMProgramTable["startedcontrolling"]
                                 << dynamicLMProgramTable["lastcontrolsent"]
                                 << dynamicLMProgramTable["manualcontrolreceivedflag"]
                                 << dynamicLMProgramDirectTable["currentgearnumber"]
                                 << dynamicLMProgramDirectTable["lastgroupcontrolled"]
                                 << dynamicLMProgramDirectTable["starttime"]
                                 << dynamicLMProgramDirectTable["stoptime"]
                                 << dynamicLMProgramDirectTable["timestamp"]
                                 << dynamicLMProgramDirectTable["dailyops"]
                                 << dynamicLMProgramDirectTable["notifytime"]
                                 << dynamicLMProgramDirectTable["startedrampingout"]
                                 << pointTable["pointid"]
                                 << pointTable["pointoffset"]
                                 << pointTable["pointtype"];

                        selector.from(yukonPAObjectTable);
                        selector.from(lmProgramTable);
                        selector.from(lmProgramDirectTable);
                        selector.from(dynamicLMProgramTable);
                        selector.from(dynamicLMProgramDirectTable);
                        selector.from(pointTable);

                        selector.where( yukonPAObjectTable["paobjectid"]==lmProgramDirectTable["deviceid"] &&
                                        lmProgramDirectTable["deviceid"]==lmProgramTable["deviceid"] &&
                                        lmProgramTable["deviceid"].leftOuterJoin(dynamicLMProgramTable["deviceid"]) &&
                                        lmProgramTable["deviceid"].leftOuterJoin(dynamicLMProgramDirectTable["deviceid"]) &&
                                        lmProgramTable["deviceid"].leftOuterJoin(pointTable["paobjectid"]) );

                        selector.orderBy(yukonPAObjectTable["paobjectid"]);

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }
 
                        CtiLMProgramDirect* currentLMProgramDirect = NULL;
                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        int total_groups_assigned = 0; //keep track of how many groups we attach to programs for sanity
                        while( rdr() )
                        {
                            LONG tempProgramId = 0;
                            rdr["paobjectid"] >> tempProgramId;

                            if( currentLMProgramDirect == NULL ||
                                tempProgramId != currentLMProgramDirect->getPAOId() )
                            {
                                currentLMProgramDirect = new CtiLMProgramDirect(rdr);
                                RWOrdered& directGroups = currentLMProgramDirect->getLMProgramDirectGroups();
                                
                                //Inserting this program's groups
                                vector<CtiLMGroupBase*> group_vec = all_program_group_map.find(tempProgramId)->second;
                                for(vector<CtiLMGroupBase*>::iterator iter = group_vec.begin();
                                    iter != group_vec.end();
                                    iter++)
                                {
                                    directGroups.insert(*iter);
                                    total_groups_assigned++;
                                }

                                //Inserting this direct program into hash map
                                directProgramHashMap.insert( currentLMProgramDirect->getPAOId(), currentLMProgramDirect );
                            }

                            rdr["pointid"] >> isNull;
                            if( !isNull )
                            {
                                LONG tempPointId = 0;
                                LONG tempPointOffset = 0;
                                RWCString tempPointType = "(none)";
                                rdr["pointid"] >> tempPointId;
                                rdr["pointoffset"] >> tempPointOffset;
                                rdr["pointtype"] >> tempPointType;
                                if( currentLMProgramDirect->getProgramStatusPointId() <= 0 &&
                                    resolvePointType(tempPointType) == StatusPointType &&
                                    tempPointOffset == 1 )
                                {
                                    currentLMProgramDirect->setProgramStatusPointId(tempPointId);
                                }
                            }
                        }
                    }//loading direct programs end
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Direct Programs is: " << dirProgsTimer.elapsedTime() << endl;
                        dirProgsTimer.reset();
                    }

                    RWTimer masterSubordinateTimer;
                    masterSubordinateTimer.start();
                    
                { // loading master - subordinate direct program relationships
                    RWDBTable paoExclusionTable = db.table("paoexclusion");
                    RWDBSelector selector = db.selector();
                    selector << paoExclusionTable["paoid"]
                             << paoExclusionTable["excludedpaoid"];
                    selector.from(paoExclusionTable);
                    selector.where(paoExclusionTable["functionid"] == CtiTablePaoExclusion::ExFunctionLMSubordination);

                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    while( rdr() )
                    {
                        long master_program_id;
                        long subordinate_program_id;
                        rdr["paoid"] >> master_program_id;
                        rdr["excludedpaoid"] >> subordinate_program_id;
                        
                        CtiLMProgramBase* master_program = 0;
                        CtiLMProgramBase* subordinate_program = 0;
                        
                        if(directProgramHashMap.findValue(master_program_id, master_program) &&
                           directProgramHashMap.findValue(subordinate_program_id, subordinate_program))
                        {
                            ((CtiLMProgramDirect*)master_program)->getSubordinatePrograms().insert((CtiLMProgramDirect*)subordinate_program);
                            ((CtiLMProgramDirect*)subordinate_program)->getMasterPrograms().insert((CtiLMProgramDirect*)master_program);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> dout_guard(dout);
                            dout << RWTime() << " **Checkpoint** " <<  "Unable to find either master program with id: " <<
                                master_program_id << " or subordinate program with id: " << subordinate_program_id <<
                                " -- This suggests that database constraints on table PAOExclusion aren't correct or that we failed to load one or both of the programs."  <<
                                __FILE__ << "(" << __LINE__ << ")" << endl;
                        }
                        
                    }
                }

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << "DB Load Timer for associating master/subordinate programs is: " << dirProgsTimer.elapsedTime() << endl;
                    dirProgsTimer.reset();
                }
                    
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
                                 << lmProgramDirectGearTable["rampininterval"]
                                 << lmProgramDirectGearTable["rampinpercent"]
                                 << lmProgramDirectGearTable["rampoutinterval"]
                                 << lmProgramDirectGearTable["rampoutpercent"]
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

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

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
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Direct Gears is: " << gearsTimer.elapsedTime() << endl;
                        gearsTimer.reset();
                    }

                    RWTimer notificationGroupTimer;
                    notificationGroupTimer.start();
                    
                { //loading notification groups start
                    RWDBTable lmProgramDirectNotifGrpListTable = db.table("lmdirectnotifgrplist");
                    RWDBSelector selector = db.selector();

                    selector << lmProgramDirectNotifGrpListTable["programid"]
                             << lmProgramDirectNotifGrpListTable["notificationgrpid"];

                    selector.from(lmProgramDirectNotifGrpListTable);

                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    while(rdr())
                    {
                        int program_id;
                        int notif_grp_id;
                        rdr["programid"] >> program_id;
                        rdr["notificationgrpid"] >> notif_grp_id;

                        CtiLMProgramBase* program = NULL;
                        if( directProgramHashMap.findValue(program_id, program ) )
                        {
                            ((CtiLMProgramDirect*)program)->getNotificationGroupIDs().insert(notif_grp_id);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> dout_guard(dout);
                            dout << RWTime() << " **Checkpoint** No program found. " << __FILE__ << "(" << __LINE__ << ")" << endl;
                        }
                    }
                } //loading notification groups end

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << "DB Load Timer for Direct Program Notification Groups is: " << notificationGroupTimer.elapsedTime() << endl;
                    notificationGroupTimer.reset();
                }
                
                    RWTValHashMap<LONG,CtiLMProgramBase*,id_hash,equal_to<LONG> > curtailmentProgramHashMap;

                    RWTimer curtailProgsTimer;
                    curtailProgsTimer.start();
                    {//loading curtailment programs start
                        RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                        RWDBTable lmProgramCurtailmentTable = db.table("lmprogramcurtailment");
                        RWDBTable pointTable = db.table("point");

                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                                 << yukonPAObjectTable["category"]
                                 << yukonPAObjectTable["paoclass"]
                                 << yukonPAObjectTable["paoname"]
                                 << yukonPAObjectTable["type"]
                                 << yukonPAObjectTable["description"]
                                 << yukonPAObjectTable["disableflag"]
                                 << lmProgramTable["controltype"]
                                 << lmProgramTable["constraintid"]
                                 << lmProgramTable["constraintname"]
                                 << lmProgramTable["availableweekdays"]
                                 << lmProgramTable["maxhoursdaily"]
                                 << lmProgramTable["maxhoursmonthly"]
                                 << lmProgramTable["maxhoursseasonal"]
                                 << lmProgramTable["maxhoursannually"]
                                 << lmProgramTable["minactivatetime"]
                                 << lmProgramTable["minrestarttime"]
                                 << lmProgramTable["maxdailyops"]
                                 << lmProgramTable["maxactivatetime"]
                                 << lmProgramTable["holidayscheduleid"]
                                 << lmProgramTable["seasonscheduleid"]
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
                                 << dynamicLMProgramTable["timestamp"]
                                 << pointTable["pointid"]
                                 << pointTable["pointoffset"]
                                 << pointTable["pointtype"];

                        selector.from(yukonPAObjectTable);
                        selector.from(lmProgramTable);
                        selector.from(lmProgramCurtailmentTable);
                        selector.from(dynamicLMProgramTable);
                        selector.from(pointTable);

                        selector.where( yukonPAObjectTable["paobjectid"]==lmProgramCurtailmentTable["deviceid"] &&
                                        lmProgramCurtailmentTable["deviceid"]==lmProgramTable["deviceid"] &&
                                        lmProgramTable["deviceid"].leftOuterJoin(dynamicLMProgramTable["deviceid"]) &&
                                        lmProgramTable["deviceid"].leftOuterJoin(pointTable["paobjectid"]) );

                        selector.orderBy(yukonPAObjectTable["paobjectid"]);

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        CtiLMProgramCurtailment* currentLMProgramCurtailment = NULL;
                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        while( rdr() )
                        {
                            LONG tempProgramId = 0;
                            rdr["paobjectid"] >> tempProgramId;

                            if( currentLMProgramCurtailment == NULL ||
                                tempProgramId != currentLMProgramCurtailment->getPAOId() )
                            {
                                currentLMProgramCurtailment = new CtiLMProgramCurtailment(rdr);
                                if( currentLMProgramCurtailment->getManualControlReceivedFlag() )
                                {
                                    currentLMProgramCurtailment->restoreDynamicData(rdr);
                                }
                                //Inserting this curtailment program into hash map
                                curtailmentProgramHashMap.insert( currentLMProgramCurtailment->getPAOId(), currentLMProgramCurtailment );
                            }

                            rdr["pointid"] >> isNull;
                            if( !isNull )
                            {
                                LONG tempPointId = 0;
                                LONG tempPointOffset = 0;
                                RWCString tempPointType = "(none)";
                                rdr["pointid"] >> tempPointId;
                                rdr["pointoffset"] >> tempPointOffset;
                                rdr["pointtype"] >> tempPointType;
                                if( currentLMProgramCurtailment->getProgramStatusPointId() <= 0 &&
                                    resolvePointType(tempPointType) == StatusPointType &&
                                    tempPointOffset == 1 )
                                {
                                    currentLMProgramCurtailment->setProgramStatusPointId(tempPointId);
                                }
                            }
                        }

                        RWTValHashMapIterator<LONG,CtiLMProgramBase*,id_hash,equal_to<LONG> > itr(curtailmentProgramHashMap);

                        for(;itr();)
                        {
                            CtiLMProgramCurtailment* currentLMProgramCurtailment = (CtiLMProgramCurtailment*)itr.value();

                            RWDBTable ciCustomerBaseTable = db.table("cicustomerbase");
                            RWDBTable customerTable = db.table("customer");
                            RWDBTable lmProgramCurtailCustomerListTable = db.table("lmprogramcurtailcustomerlist");

                            RWDBSelector selector = db.selector();
                            selector << ciCustomerBaseTable["customerid"]
                                     << ciCustomerBaseTable["companyname"]
                                     << ciCustomerBaseTable["customerdemandlevel"]
                                     << ciCustomerBaseTable["curtailamount"]
                                     << ciCustomerBaseTable["curtailmentagreement"]
                                     << customerTable["timezone"]
                                     << lmProgramCurtailCustomerListTable["customerorder"]
                                     << lmProgramCurtailCustomerListTable["requireack"];

                            selector.from(ciCustomerBaseTable);
                            selector.from(customerTable);
                            selector.from(lmProgramCurtailCustomerListTable);

                            selector.where( lmProgramCurtailCustomerListTable["programid"]==currentLMProgramCurtailment->getPAOId() &&
                                            ciCustomerBaseTable["customerid"]==lmProgramCurtailCustomerListTable["customerid"] &&
                                            customerTable["customerid"]==ciCustomerBaseTable["customerid"] );

                            selector.orderBy( lmProgramCurtailCustomerListTable["customerorder"] );

                            if( _LM_DEBUG & LM_DEBUG_DATABASE )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - " << selector.asString().data() << endl;
                            }

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
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Curtailment Programs is: " << curtailProgsTimer.elapsedTime() << endl;
                        curtailProgsTimer.reset();
                    }


                    RWTValHashMap<LONG,CtiLMProgramBase*,id_hash,equal_to<LONG> > energyExchangeProgramHashMap;

                    RWTimer eeProgsTimer;
                    eeProgsTimer.start();
                    {//loading energy exchange programs start
                        RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                        RWDBTable lmProgramEnergyExchangeTable = db.table("lmprogramenergyexchange");
                        RWDBTable pointTable = db.table("point");

                        RWDBSelector selector = db.selector();
                        selector << yukonPAObjectTable["paobjectid"]
                                 << yukonPAObjectTable["category"]
                                 << yukonPAObjectTable["paoclass"]
                                 << yukonPAObjectTable["paoname"]
                                 << yukonPAObjectTable["type"]
                                 << yukonPAObjectTable["description"]
                                 << yukonPAObjectTable["disableflag"]
                                 << lmProgramTable["controltype"]
                                 << lmProgramTable["constraintid"]
                                 << lmProgramTable["constraintname"]
                                 << lmProgramTable["availableweekdays"]
                                 << lmProgramTable["maxhoursdaily"]
                                 << lmProgramTable["maxhoursmonthly"]
                                 << lmProgramTable["maxhoursseasonal"]
                                 << lmProgramTable["maxhoursannually"]
                                 << lmProgramTable["minactivatetime"]
                                 << lmProgramTable["minrestarttime"]
                                 << lmProgramTable["maxdailyops"]
                                 << lmProgramTable["maxactivatetime"]
                                 << lmProgramTable["holidayscheduleid"]
                                 << lmProgramTable["seasonscheduleid"]
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
                                 << dynamicLMProgramTable["timestamp"]
                                 << pointTable["pointid"]
                                 << pointTable["pointoffset"]
                                 << pointTable["pointtype"];

                        selector.from(yukonPAObjectTable);
                        selector.from(lmProgramTable);
                        selector.from(lmProgramEnergyExchangeTable);
                        selector.from(dynamicLMProgramTable);
                        selector.from(pointTable);

                        selector.where( yukonPAObjectTable["paobjectid"]==lmProgramEnergyExchangeTable["deviceid"] &&
                                        lmProgramEnergyExchangeTable["deviceid"]==lmProgramTable["deviceid"] &&
                                        lmProgramTable["deviceid"].leftOuterJoin(dynamicLMProgramTable["deviceid"]) &&
                                        lmProgramTable["deviceid"].leftOuterJoin(pointTable["paobjectid"]) );

                        selector.orderBy(yukonPAObjectTable["paobjectid"]);

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        CtiLMProgramBase* currentLMProgramEnergyExchange = NULL;
                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        while( rdr() )
                        {
                            LONG tempProgramId = 0;
                            rdr["paobjectid"] >> tempProgramId;

                            if( currentLMProgramEnergyExchange == NULL ||
                                tempProgramId != currentLMProgramEnergyExchange->getPAOId() )
                            {
                                currentLMProgramEnergyExchange = new CtiLMProgramEnergyExchange(rdr);
                                //Inserting this curtailment program into hash map
                                energyExchangeProgramHashMap.insert( currentLMProgramEnergyExchange->getPAOId(), currentLMProgramEnergyExchange );
                            }

                            rdr["pointid"] >> isNull;
                            if( !isNull )
                            {
                                LONG tempPointId = 0;
                                LONG tempPointOffset = 0;
                                RWCString tempPointType = "(none)";
                                rdr["pointid"] >> tempPointId;
                                rdr["pointoffset"] >> tempPointOffset;
                                rdr["pointtype"] >> tempPointType;
                                if( currentLMProgramEnergyExchange->getProgramStatusPointId() <= 0 &&
                                    resolvePointType(tempPointType) == StatusPointType &&
                                    tempPointOffset == 1 )
                                {
                                    currentLMProgramEnergyExchange->setProgramStatusPointId(tempPointId);
                                }
                            }
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

                                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }

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

                                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << RWTime() << " - " << selector.asString().data() << endl;
                                    }

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

                                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - " << selector.asString().data() << endl;
                                        }

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
                                RWDBTable ciCustomerBaseTable = db.table("cicustomerbase");
                                RWDBTable customerTable = db.table("customer");
                                RWDBTable lmEnergyExchangeCustomerListTable = db.table("lmenergyexchangecustomerlist");

                                RWDBSelector selector = db.selector();
                                selector << ciCustomerBaseTable["customerid"]
                                         << ciCustomerBaseTable["companyname"]
                                         << ciCustomerBaseTable["customerdemandlevel"]
                                         << ciCustomerBaseTable["curtailamount"]
                                         << ciCustomerBaseTable["curtailmentagreement"]
                                         << customerTable["timezone"]
                                         << lmEnergyExchangeCustomerListTable["customerorder"];

                                selector.from(ciCustomerBaseTable);
                                selector.from(customerTable);
                                selector.from(lmEnergyExchangeCustomerListTable);

                                selector.where( lmEnergyExchangeCustomerListTable["programid"]==currentLMProgramEnergyExchange->getPAOId() &&
                                                ciCustomerBaseTable["customerid"]==lmEnergyExchangeCustomerListTable["customerid"] &&
                                                customerTable["customerid"]==ciCustomerBaseTable["customerid"] );

                                selector.orderBy( lmEnergyExchangeCustomerListTable["customerorder"] );

                                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " - " << selector.asString().data() << endl;
                                }

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

                                        selector.where(lmEnergyExchangeCustomerReplyTable["customerid"]==currentLMEnergyExchangeCustomer->getCustomerId() &&
                                                       lmEnergyExchangeCustomerReplyTable["offerid"]==lmEnergyExchangeProgramOfferTable["offerid"] &&
                                                       lmEnergyExchangeProgramOfferTable["offerdate"]>=compareDateTime);

                                        selector.orderBy(lmEnergyExchangeCustomerReplyTable["offerid"]);
                                        selector.orderBy(lmEnergyExchangeCustomerReplyTable["revisionnumber"]);

                                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << RWTime() << " - " << selector.asString().data() << endl;
                                        }

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

                                            if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << RWTime() << " - " << selector.asString().data() << endl;
                                            }

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
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Energy Exchange Programs is: " << eeProgsTimer.elapsedTime() << endl;
                        eeProgsTimer.reset();
                    }


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

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

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
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Program Control Windows is: " << progWinTimer.elapsedTime() << endl;
                        progWinTimer.reset();
                    }


                    RWTimer caTimer;
                    caTimer.start();
                    {//loading control areas start
                        RWDBTable yukonPAObjectTable = db.table("yukonpaobject");
                        RWDBTable lmControlAreaTable = db.table("lmcontrolarea");
                        RWDBTable lmControlAreaProgramTable = db.table("lmcontrolareaprogram");
                        RWDBTable dynamicLMControlAreaTable = db.table("dynamiclmcontrolarea");
                        RWDBTable pointTable = db.table("point");

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
                                 << lmControlAreaProgramTable["startpriority"]
                                 << lmControlAreaProgramTable["stoppriority"]
                                 << dynamicLMControlAreaTable["nextchecktime"]
                                 << dynamicLMControlAreaTable["newpointdatareceivedflag"]
                                 << dynamicLMControlAreaTable["updatedflag"]
                                 << dynamicLMControlAreaTable["controlareastate"]
                                 << dynamicLMControlAreaTable["currentpriority"]
                                 << dynamicLMControlAreaTable["currentdailystarttime"]
                                 << dynamicLMControlAreaTable["currentdailystoptime"]
                                 << dynamicLMControlAreaTable["timestamp"]
                                 << pointTable["pointid"]
                                 << pointTable["pointoffset"]
                                 << pointTable["pointtype"];

                        selector.from(yukonPAObjectTable);
                        selector.from(lmControlAreaTable);
                        selector.from(lmControlAreaProgramTable);
                        selector.from(dynamicLMControlAreaTable);
                        selector.from(pointTable);

                        //fixThis;
                        selector.where( yukonPAObjectTable["paobjectid"]==lmControlAreaTable["deviceid"] &&
                                        lmControlAreaTable["deviceid"].leftOuterJoin(lmControlAreaProgramTable["deviceid"]) &&
                                        lmControlAreaTable["deviceid"].leftOuterJoin(dynamicLMControlAreaTable["deviceid"]) &&
                                        lmControlAreaTable["deviceid"].leftOuterJoin(pointTable["paobjectid"]) );

                        selector.orderBy(yukonPAObjectTable["paobjectid"]);
                        selector.orderBy(lmControlAreaProgramTable["startpriority"]);

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);

                        CtiLMControlArea* currentLMControlArea = NULL;
                        CtiLMProgramBase* currentLMProgramBase = NULL;
                        RWDBNullIndicator isNull;
                        while( rdr() )
                        {
                            LONG tempControlAreaId = 0;
                            rdr["paobjectid"] >> tempControlAreaId;
                            if( currentLMControlArea == NULL ||
                                tempControlAreaId != currentLMControlArea->getPAOId() )
                            {
                                currentLMControlArea = new CtiLMControlArea(rdr);
                                temp_control_areas.insert(currentLMControlArea);
                            }

                            rdr["lmprogramdeviceid"] >> isNull;
                            if( !isNull )
                            {
                                LONG tempProgramId = 0;
                                int start_priority;
                                int stop_priority;

                                rdr["lmprogramdeviceid"] >> tempProgramId;
                                rdr["startpriority"] >> start_priority;
                                rdr["stoppriority"] >> stop_priority;

                                if( currentLMProgramBase == NULL ||
                                    ( currentLMProgramBase != NULL &&
                                      currentLMProgramBase->getPAOId() != tempProgramId ) )
                                {
                                    RWOrdered& lmControlAreaProgramList = currentLMControlArea->getLMPrograms();

                                    if( directProgramHashMap.findValue(tempProgramId,currentLMProgramBase) )
                                    {
                                        currentLMProgramBase->setStartPriority(start_priority);
                                        currentLMProgramBase->setStopPriority(stop_priority);
                                        lmControlAreaProgramList.insert(currentLMProgramBase);
                                        directProgramHashMap.remove(tempProgramId);
                                    }
                                    else if( curtailmentProgramHashMap.findValue(tempProgramId,currentLMProgramBase) )
                                    {
                                        currentLMProgramBase->setStartPriority(start_priority);
                                        currentLMProgramBase->setStopPriority(stop_priority);                                        
                                        lmControlAreaProgramList.insert(currentLMProgramBase);
                                        curtailmentProgramHashMap.remove(tempProgramId);
                                    }
                                    else if( energyExchangeProgramHashMap.findValue(tempProgramId,currentLMProgramBase) )
                                    {
                                        currentLMProgramBase->setStartPriority(start_priority);
                                        currentLMProgramBase->setStopPriority(stop_priority);                                        
                                        lmControlAreaProgramList.insert(currentLMProgramBase);
                                        energyExchangeProgramHashMap.remove(tempProgramId);
                                    }
                                    else
                                    {
                                        CtiLockGuard<CtiLogger> logger_guard(dout);
                                        dout << "Cannot find LM Program with Id: " << tempProgramId << " to insert into Control Area: " << currentLMControlArea->getPAOName() << ", in: " << __FILE__ << " at:" << __LINE__ << endl;
                                        dout << "The LM Program may be in more than one Control Area which is not allowed, in: " << __FILE__ << " at:" << __LINE__ << endl;
                                    }
                                }
                            }

                            rdr["pointid"] >> isNull;
                            if( !isNull )
                            {
                                LONG tempPointId = 0;
                                LONG tempPointOffset = 0;
                                RWCString tempPointType = "(none)";
                                rdr["pointid"] >> tempPointId;
                                rdr["pointoffset"] >> tempPointOffset;
                                rdr["pointtype"] >> tempPointType;
                                if( currentLMControlArea->getControlAreaStatusPointId() <= 0 &&
                                    resolvePointType(tempPointType) == StatusPointType &&
                                    tempPointOffset == 1 )
                                {
                                    currentLMControlArea->setControlAreaStatusPointId(tempPointId);
                                }
                            }
                        }

                        //clean up all the remaining, unassigned programs
                        //(most of the should have been attached to control areas and removed from these maps alreadY)
                        directProgramHashMap.apply(lmprogram_delete, 0);
                        curtailmentProgramHashMap.apply(lmprogram_delete, 0);
                        energyExchangeProgramHashMap.apply(lmprogram_delete, 0);
                        
                        
                    }//loading control areas end
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Control Areas is: " << caTimer.elapsedTime() << endl;
                        dout << "directprghashmap size: " << directProgramHashMap.entries() << endl;
                        caTimer.reset();
                    }


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

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);
                        while( rdr() )
                        {
                            CtiLMControlAreaTrigger* newTrigger = new CtiLMControlAreaTrigger(rdr);
                            attachProjectionData(newTrigger);

                            /****************************************************************
                            *******  Inserting Trigger into the correct Control Area  *******
                            ****************************************************************/
                            LONG tempControlAreaId = 0;
                            rdr["deviceid"] >> tempControlAreaId;
                            for(LONG i=0;i<temp_control_areas.entries();i++)
                            {
                                CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(temp_control_areas[i]);
                                if( currentLMControlArea->getPAOId() == tempControlAreaId )
                                {
                                    currentLMControlArea->getLMControlAreaTriggers().insert(newTrigger);
                                    break;
                                }
                            }
                        }
                    }//loading control area triggers end
                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << "DB Load Timer for Triggers is: " << trigTimer.elapsedTime() << endl;
                        trigTimer.reset();
                    }

                        //Make sure holidays and season schedules are refreshed
                    CtiHolidayManager::getInstance().refresh();
                    CtiSeasonManager::getInstance().refresh();
        
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

#ifdef _BUNG_
        for(int i = _controlAreas.entries(); i++)
        {
            CtiLMControlArea* control_area = (CtiLMControlArea*) _controlAreas[i];
            RWOrdered& programs = control_area->getLMPrograms();
            for(int j = 0; j < programs.entries(); j++)
            {
                CtiLMProgramBase* program = (CtiLMProgramBase*) programs[j];
                if(program->getPAOType() == TYPE_LMPROGRAM_DIRECT)
                {
                    CtiLMProgramDirect* direct_program = (CtiLMProgramDirect*) program;
                    RWOrdered& groups = direct_program->getLMProgramDirectGroups();
                    
                }
            }
        }
#endif

    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
                            
        // Clear out our old working objects
        _controlAreas->clearAndDestroy(); //89
        //the line above doesn't delete groups, do that now
        for(map<long,CtiLMGroupBase*>::iterator iter = _all_group_map.begin();
            iter != _all_group_map.end();
            iter++)
        {
            delete iter->second;
        }
        _all_group_map.clear();
        _point_group_map.clear();

        // Lets start using the new objects we just loaded.
        // do a swap, make sure we have the mux
        *_controlAreas = temp_control_areas;
        _all_group_map = temp_all_group_map;
        _point_group_map = temp_point_group_map;
        
        _isvalid = TRUE;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Control areas reset" << endl;
    }
                       
    if( _LM_DEBUG & LM_DEBUG_DATABASE )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "DB Load Timer for entire LM DB: " << overallTimer.elapsedTime() << endl;
        overallTimer.reset();
    }

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

        ULONG msgBitMask = CtiLMControlAreaMsg::AllControlAreasSent;
        if( _wascontrolareadeletedflag )
        {
            msgBitMask = CtiLMControlAreaMsg::AllControlAreasSent | CtiLMControlAreaMsg::ControlAreaDeleted;
        }
        _wascontrolareadeletedflag = false;
        CtiLMExecutorFactory f;
        CtiLMExecutor* executor = f.createExecutor(new CtiLMControlAreaMsg(*_controlAreas,msgBitMask));
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

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
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
 
        RWDBDateTime lastPeriodicDatabaseRefresh = RWDBDateTime();

        while(TRUE)
        {
            rwRunnable().serviceCancellation();
            RWDBDateTime now;
            if( now.rwdate() != lastPeriodicDatabaseRefresh.rwdate() )
            {//check to see if it is midnight
                checkMidnightDefaultsForReset();
            }

            if( now.seconds() >= lastPeriodicDatabaseRefresh.seconds()+refreshrate )
            {
                RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Periodic restore of control area list from the database" << endl;
                }

                setValid(false);

                lastPeriodicDatabaseRefresh = RWDBDateTime();
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

bool CtiLMControlAreaStore::getWasControlAreaDeletedFlag()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _wascontrolareadeletedflag;
}

void CtiLMControlAreaStore::setWasControlAreaDeletedFlag(bool wasDeleted)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _wascontrolareadeletedflag = wasDeleted;
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

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << updater.asString().data() << endl;
        }

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

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << updater.asString().data() << endl;
        }

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
    UpdateGroupDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the group.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateGroupDisableFlagInDB(CtiLMGroupBase* group)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == group->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( RWCString((group->getDisableFlag()?'Y':'N')) );

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << updater.asString().data() << endl;
        }

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(group->getPAOId(), ChangePAODb,
                                                      group->getPAOCategory(), desolveDeviceType(group->getPAOType()),
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

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << updater.asString().data() << endl;
        }

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
        // not equal, so no "!" on the compareTo()
        if( currentControlArea->getDefOperationalState().compareTo(CtiLMControlArea::DefOpStateNone,RWCString::ignoreCase) )
        {//check default operational state
            if( ( !currentControlArea->getDefOperationalState().compareTo(CtiLMControlArea::DefOpStateEnabled,RWCString::ignoreCase) &&
                  currentControlArea->getDisableFlag() ) ||
                ( !currentControlArea->getDefOperationalState().compareTo(CtiLMControlArea::DefOpStateDisabled,RWCString::ignoreCase) &&
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


/*---------------------------------------------------------------------------
    saveAnyProjectionData

    .
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::saveAnyProjectionData()
{
    if( _projectionQueues.entries() > 0 )
        _projectionQueues.clear();

    for(LONG i=0;i<_controlAreas->entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];

        RWOrdered& lmControlAreaTriggers = currentLMControlArea->getLMControlAreaTriggers();
        if( lmControlAreaTriggers.entries() > 0 )
        {
            for(LONG j=0;j<lmControlAreaTriggers.entries();j++)
            {
                CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)lmControlAreaTriggers[j];
                if( !currentLMControlAreaTrigger->getProjectionType().compareTo(CtiLMControlAreaTrigger::LSFProjectionType,RWCString::ignoreCase) &&
                    currentLMControlAreaTrigger->getTriggerType().compareTo(CtiLMControlAreaTrigger::StatusTriggerType,RWCString::ignoreCase) )
                {// don't need "!" on compareTo() because supposed to be !=
                    /*{
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << " - Saved Projection Point Entries Queue entries for Point Id: " << currentLMControlAreaTrigger->getPointId() << endl;
                        for(int k=0;k<currentLMControlAreaTrigger->getProjectionPointEntriesQueue().entries();k++)
                        {
                            dout << " Entry number: " << k << " value: " << currentLMControlAreaTrigger->getProjectionPointEntriesQueue()[k].getValue() << " timestamp: " << currentLMControlAreaTrigger->getProjectionPointEntriesQueue()[k].getTimestamp() << endl;
                        }
                    }*/
                    _projectionQueues.insert(CtiLMSavedProjectionQueue(currentLMControlAreaTrigger->getPointId(), currentLMControlAreaTrigger->getProjectionPointEntriesQueue()));
                }
            }
        }
    }
}

/*---------------------------------------------------------------------------
    attachProjectionData

    .
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::attachProjectionData(CtiLMControlAreaTrigger* trigger)
{
    for(LONG i=0;i<_projectionQueues.entries();i++)
    {
        CtiLMSavedProjectionQueue currentSavedQueue =  _projectionQueues.at(i);
        if( trigger->getPointId() == currentSavedQueue.getPointId() )
        {
            RWTValDlist<CtiLMProjectionPointEntry> queueToCopyFrom = currentSavedQueue.getProjectionEntryList();
            RWTValDlist<CtiLMProjectionPointEntry>& queueToFillUp = trigger->getProjectionPointEntriesQueue();
            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << " - Restored Projection Point Entries Queue entries for PointId: " << trigger->getPointId() << endl;
                for(int k=0;k<queueToCopyFrom.entries();k++)
                {
                    dout << " Entry number: " << k << " value: " << queueToCopyFrom[k].getValue() << " timestamp: " << queueToCopyFrom[k].getTimestamp() << endl;
                }
            }*/
            for(LONG j=0;j<queueToCopyFrom.entries();j++)
            {
                queueToFillUp.insert(queueToCopyFrom.at(j));
            }
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    saveAnyControlStringData

    .
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::saveAnyControlStringData()
{
    if( _controlStrings.entries() > 0 )
        _controlStrings.clear();

    for(LONG i=0;i<_controlAreas->entries();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];

        RWOrdered& lmPrograms = currentLMControlArea->getLMPrograms();
        if( lmPrograms.entries() > 0 )
        {
            for(LONG j=0;j<lmPrograms.entries();j++)
            {
                CtiLMProgramBase* currentLMProgram = (CtiLMProgramBase*)lmPrograms[j];
                if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                {
                    RWOrdered& lmGroups = ((CtiLMProgramDirect*)currentLMProgram)->getLMProgramDirectGroups();
                    for(LONG k=0;k<lmGroups.entries();k++)
                    {
                        CtiLMGroupBase* currentLMGroup = (CtiLMGroupBase*)lmGroups[k];
                        if( currentLMGroup->getLastControlString().length() > 0 )
                        {
                            _controlStrings.insert(CtiLMSavedControlString(currentLMGroup->getPAOId(), currentLMGroup->getLastControlString()));
                        }
                    }
                }
            }
        }
    }
}

/*---------------------------------------------------------------------------
    attachControlStringData

    .
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::attachControlStringData(CtiLMGroupBase* group)
{
    for(LONG i=0;i<_controlStrings.entries();i++)
    {
        CtiLMSavedControlString currentSavedString =  _controlStrings.at(i);
        if( group->getPAOId() == currentSavedString.getPAOId() )
        {
            group->setLastControlString(currentSavedString.getControlString());
            break;
        }
    }
}

const RWCString CtiLMControlAreaStore::LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE = "LOAD_MANAGEMENT_SERVER";


//*************************************************************
//**********  CtiLMSavedProjectionQueue              **********
//**********                                         **********
//**********  This is equivalent to an inner class,  **********
//**********  only used for saving projections       **********
//*************************************************************
CtiLMSavedProjectionQueue::CtiLMSavedProjectionQueue(LONG pointId, const RWTValDlist<CtiLMProjectionPointEntry>& projectionEntryList)
{
    setPointId(pointId);
    setProjectionEntryList(projectionEntryList);
}

CtiLMSavedProjectionQueue::CtiLMSavedProjectionQueue(const CtiLMSavedProjectionQueue& savedProjectionQueue)
{
    operator=(savedProjectionQueue);
}

CtiLMSavedProjectionQueue::~CtiLMSavedProjectionQueue()
{
}

LONG CtiLMSavedProjectionQueue::getPointId() const
{
    return _pointId;
}
const RWTValDlist<CtiLMProjectionPointEntry>& CtiLMSavedProjectionQueue::getProjectionEntryList() const
{
    return _projectionEntryList;
}

CtiLMSavedProjectionQueue& CtiLMSavedProjectionQueue::setPointId(LONG pointId)
{
    _pointId = pointId;
    return *this;
}
CtiLMSavedProjectionQueue& CtiLMSavedProjectionQueue::setProjectionEntryList(const RWTValDlist<CtiLMProjectionPointEntry>& projectionEntryList)
{
    _projectionEntryList = projectionEntryList;
    return *this;
}

CtiLMSavedProjectionQueue& CtiLMSavedProjectionQueue::operator=(const CtiLMSavedProjectionQueue& right)
{
    if( this != &right )
    {
        _pointId = right.getPointId();
        _projectionEntryList = right.getProjectionEntryList();
    }

    return *this;
}

//*************************************************************
//**********  CtiLMSavedControlString                **********
//**********                                         **********
//**********  This is equivalent to an inner class,  **********
//**********  only used for saving control strings   **********
//*************************************************************
CtiLMSavedControlString::CtiLMSavedControlString(LONG paoId, const RWCString& controlString)
{
    setPAOId(paoId);
    setControlString(controlString);
}

CtiLMSavedControlString::CtiLMSavedControlString(const CtiLMSavedControlString& savedControlString)
{
    operator=(savedControlString);
}

CtiLMSavedControlString::~CtiLMSavedControlString()
{
}

LONG CtiLMSavedControlString::getPAOId() const
{
    return _paoId;
}
const RWCString& CtiLMSavedControlString::getControlString() const
{
    return _controlString;
}

CtiLMSavedControlString& CtiLMSavedControlString::setPAOId(LONG paoId)
{
    _paoId = paoId;
    return *this;
}
CtiLMSavedControlString& CtiLMSavedControlString::setControlString(const RWCString& controlstr)
{
    _controlString = controlstr;
    return *this;
}

CtiLMSavedControlString& CtiLMSavedControlString::operator=(const CtiLMSavedControlString& right)
{
    if( this != &right )
    {
        _paoId = right.getPAOId();
        _controlString = right.getControlString();
    }

    return *this;
}


