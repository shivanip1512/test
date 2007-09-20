/*---------------------------------------------------------------------------
        Filename:  lmcontrolareastore.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMControlAreaStore
                        CtiLMControlAreaStore maintains a pool of
                        CtiLMControlAreas.

        Initial Date:  2/8/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include <map>

#include <rw/thr/thrfunc.h>
#include <rw/tphdict.h>

#include "mgr_holiday.h"
#include "mgr_season.h"

#include "msg_signal.h"

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

using namespace std;

extern ULONG _LM_DEBUG;
extern set<long> _CHANGED_GROUP_LIST;
extern set<long> _CHANGED_CONTROL_AREA_LIST;
extern set<long> _CHANGED_PROGRAM_LIST;

struct id_hash{LONG operator()(LONG x) const { return x; } };

void lmprogram_delete(const LONG& program_id, CtiLMProgramBase*const& lm_program, void* d)
{
    delete lm_program;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMControlAreaStore::CtiLMControlAreaStore() : _isvalid(false), _reregisterforpoints(true), _lastdbreloadtime(gInvalidCtiTime), _wascontrolareadeletedflag(false)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _controlAreas = CTIDBG_new vector<CtiLMControlArea*>;
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
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiLMControlAreaStore destructor called..." << endl;
    }*/
    if( _resetthr.isValid() )
    {
        _resetthr.requestCancellation();
        _resetthr.join();
    }

    shutdown();
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - CtiLMControlAreaStore destructor done!!!" << endl;
    }*/
}

/*---------------------------------------------------------------------------
    getControlAreas

    Returns a Vector of CtiLMControlAreas
---------------------------------------------------------------------------*/
vector<CtiLMControlArea*>* CtiLMControlAreaStore::getControlAreas(ULONG secondsFrom1901)
{
    if( !_isvalid && secondsFrom1901 >= _lastdbreloadtime.seconds()+ 90 )
    {//is not valid and has been at 1 1/2 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
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
bool CtiLMControlAreaStore::findProgram(LONG programID, CtiLMProgramBaseSPtr& program, CtiLMControlArea** controlArea)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    vector<CtiLMControlArea*>* controlAreas = getControlAreas(CtiTime().seconds());
    for(LONG i=0; i < controlAreas->size(); i++)
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*) (*controlAreas)[i];
        vector<CtiLMProgramBaseSPtr>& lmPrograms = currentControlArea->getLMPrograms();
        for(LONG j=0; j < lmPrograms.size(); j++)
        {
            CtiLMProgramBaseSPtr currentLMProgram = (CtiLMProgramBaseSPtr) lmPrograms[j];
            if(programID == currentLMProgram->getPAOId())
            {
                if(controlArea != NULL)
                {
                    *controlArea = currentControlArea;
                }

                program = currentLMProgram;

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
CtiLMGroupPtr CtiLMControlAreaStore::findGroupByPointID(long point_id)
{
    std::map< long, CtiLMGroupPtr >::iterator iter = _point_group_map.find(point_id);
    return (iter == _point_group_map.end() ? CtiLMGroupPtr() : iter->second);
}

/*---------------------------------------------------------------------------
    dumpAllDynamicData

    Writes out the dynamic information for each of the control areas.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::dumpAllDynamicData()
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Begin writing dynamic data to the database..." << endl;
    }*/
    if( _controlAreas->size() > 0 )
    {
        CtiTime currentDateTime = CtiTime();
        string dynamicLoadManagement("dynamicLoadManagement");
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        conn.beginTransaction(dynamicLoadManagement.c_str());

        for(LONG i=0;i<_controlAreas->size();i++)
        {
            CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];
            currentLMControlArea->dumpDynamicData(conn,currentDateTime);

            vector<CtiLMControlAreaTrigger*>& lmControlAreaTriggers = currentLMControlArea->getLMControlAreaTriggers();
            if( lmControlAreaTriggers.size() > 0 )
            {
                for(LONG x=0;x<lmControlAreaTriggers.size();x++)
                {
                    CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)lmControlAreaTriggers.at(x);
                    currentLMControlAreaTrigger->dumpDynamicData(conn,currentDateTime);
                }
            }

            vector<CtiLMProgramBaseSPtr>& lmPrograms = currentLMControlArea->getLMPrograms();
            if( lmPrograms.size() > 0 )
            {
                for(LONG j=0;j<lmPrograms.size();j++)
                {
                    CtiLMProgramBaseSPtr currentLMProgramBase = (CtiLMProgramBaseSPtr)lmPrograms[j];
                    currentLMProgramBase->dumpDynamicData(conn,currentDateTime);

                    if( currentLMProgramBase->getPAOType() ==  TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirectSPtr currentLMProgramDirect = boost::static_pointer_cast< CtiLMProgramDirect>(currentLMProgramBase);
                        currentLMProgramDirect->dumpDynamicData(conn,currentDateTime);

                        CtiLMGroupVec groups  = currentLMProgramDirect->getLMProgramDirectGroups();
                        for(CtiLMGroupIter k = groups.begin(); k != groups.end(); k++)
                        {
                            CtiLMGroupPtr currentLMGroup  = *k;
                            currentLMGroup->dumpDynamicData(conn,currentDateTime);
                        }
                    }
                    else if( currentLMProgramBase->getPAOType() ==  TYPE_LMPROGRAM_CURTAILMENT )
                    {
                        CtiLMProgramCurtailmentSPtr currentLMProgramCurtailment = boost::static_pointer_cast< CtiLMProgramCurtailment>(currentLMProgramBase);

                        if( currentLMProgramCurtailment->getManualControlReceivedFlag() )
                        {
                            currentLMProgramCurtailment->dumpDynamicData(conn,currentDateTime);

                            vector<CtiLMCurtailCustomer*>& lmCurtailCustomers = currentLMProgramCurtailment->getLMProgramCurtailmentCustomers();

                            for(LONG k=0;k<lmCurtailCustomers.size();k++)
                            {
                                CtiLMCurtailCustomer* currentLMCurtailCustomer = (CtiLMCurtailCustomer*)lmCurtailCustomers[k];
                                currentLMCurtailCustomer->dumpDynamicData(conn,currentDateTime);
                            }
                        
                        }
                    }
                    else
                    {
                    }
                }
            }
        }
        conn.commitTransaction(string2RWCString(dynamicLoadManagement));
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - ***No control areas to write to dynamic tables***" << endl;
    }

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Done writing dynamic data to the database!!!" << endl;
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
        vector<CtiLMControlArea*> temp_control_areas;
        std::map<long, CtiLMGroupPtr > temp_all_group_map;
        std::map<long, CtiLMGroupPtr > temp_point_group_map;
        std::map<long, CtiLMProgramBaseSPtr > temp_all_program_map;
        std::map<long, CtiLMControlArea* > temp_all_control_area_map;

        LONG currentAllocations = ResetBreakAlloc();
        if( _LM_DEBUG & LM_DEBUG_EXTENDED )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Current Number of Historical Memory Allocations: " << currentAllocations << endl;
        }

        RWTimer overallTimer;
        overallTimer.start();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Starting Database Reload..." << endl;
        }

        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
                if( conn.isValid() )
                {
                {
                    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
                    if( _controlAreas->size() > 0 )
                    {   //Save off current data to database so that if can be loaded by new objects on reload
                        dumpAllDynamicData();
                        saveAnyProjectionData();
                        saveAnyControlStringData();
                    }
                }
                    CtiTime currentDateTime;
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
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
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
                    std::map< long, CtiLMGroupPtr > all_assigned_group_map; //remember which groups we have assigned
                    std::map< long, vector<CtiLMGroupPtr> > all_program_group_map;

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
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    CtiLMGroupFactory lm_group_factory;
                    RWDBReader rdr = selector.reader(conn);
                    while(rdr())
                    {
                        string category;
                        string type;
                        rdr["category"] >> category;
                        rdr["type"] >> type;
                        CtiLMGroupPtr lm_group = lm_group_factory.createLMGroup(rdr);
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

                    std::map< long, CtiLMGroupPtr >::iterator iter = temp_all_group_map.find(group_id);
                    if(iter != temp_all_group_map.end())
                    {
                        CtiLMGroupPoint* lm_group = (CtiLMGroupPoint*) iter->second.get();
                        lm_group->setDeviceIdUsage(device_id);
                        lm_group->setPointIdUsage(point_id);
                        lm_group->setStartControlRawState(start_control_raw_state);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> dout_guard(dout);
                        dout << CtiTime() << " **Checkpoint** " <<  " Rows exist in the LMGroupPoint table exist but do not correspond with any lm groups already loaded.  Either groups didn't get loaded correctly or the LMGroupPoint table has missing constraints?" << __FILE__ << "(" << __LINE__ << ")" << endl;
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

                    std::map< long, CtiLMGroupPtr >::iterator iter = temp_all_group_map.find(group_id);
                    if(iter != temp_all_group_map.end())
                    {
                        CtiLMGroupRipple* lm_group = (CtiLMGroupRipple*) iter->second.get();
                        lm_group->setShedTime(shed_time);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> dout_guard(dout);
                        dout << CtiTime() << " **Checkpoint** " <<  " Rows exist in the LMGroupRipple table exist but do not correspond with any lm groups already loaded.  Either groups didn't get loaded correctly or the LMGroupRipple table has missing constraints?" << __FILE__ << "(" << __LINE__ << ")" << endl;
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
                    dout << CtiTime() << " - " << selector.asString().data() << endl;
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

                    if(group_id <= 0) //we don't deal with groups with 0 or negative ids
                    {
                        continue;
                    }

                    std::map< long, CtiLMGroupPtr >::iterator iter = temp_all_group_map.find(group_id);
                    CtiLMGroupPtr lm_group = iter->second;

                    switch(resolvePointType(point_type.c_str()))
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
                        /*{
                            CtiLockGuard<CtiLogger> dout_guard(dout);
                            dout << CtiTime() << " **Checkpoint** " <<  " Unknown point offset: " << point_offset
                                 << "  Expected daily, monthly, seasonal, or annual control history point offset" << __FILE__ << "(" << __LINE__ << ")" << endl;
                        }*/
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
                        dout << CtiTime() << " **Checkpoint** " <<  " Unknown point type:  " << resolvePointType(point_type.c_str()) << __FILE__ << "(" << __LINE__ << ")" << endl;
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
                         << dynamicLMGroupTable["internalstate"]
                         << dynamicLMGroupTable["dailyops"];

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
                    CtiTime last_control_sent;
                    CtiTime timestamp;
                    CtiTime control_start_time;
                    CtiTime control_complete_time;
                    CtiTime next_control_time;
                    long internal_state;
                    long daily_ops;
                    bool constraint_override;

                    string tempBoolString;
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
                    rdr["dailyops"] >> daily_ops;


                    CtiLMGroupPtr& lm_group = temp_all_group_map.find(group_id)->second;
                    lm_group->setGroupControlState(group_control_state);
                    lm_group->setCurrentHoursDaily(cur_hours_daily);
                    lm_group->setCurrentHoursMonthly(cur_hours_monthly);
                    lm_group->setCurrentHoursSeasonal(cur_hours_seasonal);
                    lm_group->setCurrentHoursAnnually(cur_hours_annually);
                    lm_group->setLastControlSent(last_control_sent);
                    lm_group->setDynamicTimestamp(timestamp);
                    lm_group->setControlStartTime(control_start_time);
                    lm_group->setControlCompleteTime(control_complete_time);
                    lm_group->setNextControlTime(next_control_time);
                    lm_group->setInternalState(internal_state);
                    lm_group->resetDailyOps(daily_ops);

                    lm_group->setDirty(false);
                    lm_group->_insertDynamicDataFlag = FALSE;
                }
            }
                /* Now lets load up info about macro groups */
                std::map< long, vector<long> > group_macro_map;  //ownerid, <childid>
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
                    dout << CtiTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);
                std::map<long, vector<long> >::iterator iter;
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
                selector.orderBy(lmProgramDirectGroupTable["grouporder"]);

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);
                std::map<long, vector<CtiLMGroupPtr> >::iterator cur_iter;
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
                        vector<CtiLMGroupPtr> group_vec;
                        CtiLMGroupPtr lm_group = temp_all_group_map.find(group_id)->second;

                        std::map<long, vector<long> >::iterator macro_iter = group_macro_map.find(lm_group->getPAOId());
                        if(macro_iter != group_macro_map.end())
                        { //must be a macro group
                            vector<long> macro_vec = macro_iter->second;
                            for(vector<long>::iterator iter = macro_vec.begin();
                                iter != macro_vec.end();
                                iter++)
                            { //iterate over all the children in this macro group and insert them in place of the owner (macrogroup)
                                CtiLMGroupPtr child_group = temp_all_group_map.find(*iter)->second;
                                group_vec.push_back(child_group);
                                child_group->setGroupOrder(group_vec.size());
                                all_assigned_group_map.insert(make_pair(child_group->getPAOId(), child_group));
                            }
                        }
                        else
                        { //not a macro group, assign it to the program
                            group_vec.push_back(lm_group);
                            lm_group->setGroupOrder(group_vec.size());
//                            all_program_group_map.insert(make_pair(program_id, group_vec));
                            all_assigned_group_map.insert(make_pair(lm_group->getPAOId(), lm_group));
                        }
                        all_program_group_map.insert(make_pair(program_id, group_vec));
                    }
                    else
                    {
                        CtiLMGroupPtr& lm_group = temp_all_group_map.find(group_id)->second;

                        std::map<long, vector<long> >::iterator macro_iter = group_macro_map.find(lm_group->getPAOId());
                        if(macro_iter != group_macro_map.end())
                        { //must be a macro group
                            vector<long> macro_vec = macro_iter->second;
                            for(vector<long>::iterator iter = macro_vec.begin();
                                iter != macro_vec.end();
                                iter++)
                            { //iterate over all the children in this macro group and insert them in place of the owner (macrogroup)
                                CtiLMGroupPtr& child_group = temp_all_group_map.find(*iter)->second;
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

                    RWTValHashMap<LONG,CtiLMProgramBaseSPtr,id_hash,equal_to<LONG> > directProgramHashMap;

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
                                 << lmProgramDirectTable["notifyactiveoffset"]
                                 << lmProgramDirectTable["notifyinactiveoffset"]
                                 << lmProgramDirectTable["triggeroffset"]
                                 << lmProgramDirectTable["restoreoffset"]
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
                                 << dynamicLMProgramDirectTable["notifyactivetime"]
                                 << dynamicLMProgramDirectTable["notifyinactivetime"]
                                 << dynamicLMProgramDirectTable["startedrampingout"]
                                 << dynamicLMProgramDirectTable["constraintoverride"]
                                 << dynamicLMProgramDirectTable["additionalinfo"]
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
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }

                        CtiLMProgramDirectSPtr currentLMProgramDirect;
                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        int total_groups_assigned = 0; //keep track of how many groups we attach to programs for sanity
                        while( rdr() )
                        {
                            LONG tempProgramId = 0;
                            rdr["paobjectid"] >> tempProgramId;

                            if( !currentLMProgramDirect ||
                                tempProgramId != currentLMProgramDirect->getPAOId() )
                            {
                                currentLMProgramDirect.reset(CTIDBG_new CtiLMProgramDirect(rdr));
                                CtiLMGroupVec& directGroups = currentLMProgramDirect->getLMProgramDirectGroups();

                                //Inserting this program's groups
                                CtiLMGroupVec group_vec = all_program_group_map.find(tempProgramId)->second;
                                for(CtiLMGroupIter iter = group_vec.begin();
                                    iter != group_vec.end();
                                    iter++)
                                {
                                    directGroups.push_back(*iter);
                                    total_groups_assigned++;
                                }

                                //Inserting this direct program into hash map
                                directProgramHashMap.insert( currentLMProgramDirect->getPAOId(), currentLMProgramDirect );
                                temp_all_program_map.insert(make_pair(currentLMProgramDirect->getPAOId(), currentLMProgramDirect));
                            }

                            rdr["pointid"] >> isNull;
                            if( !isNull )
                            {
                                LONG tempPointId = 0;
                                LONG tempPointOffset = 0;
                                string tempPointType = "(none)";
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
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    while( rdr() )
                    {
                        long master_program_id;
                        long subordinate_program_id;
                        rdr["paoid"] >> master_program_id;
                        rdr["excludedpaoid"] >> subordinate_program_id;

                        CtiLMProgramBaseSPtr master_program;
                        CtiLMProgramBaseSPtr subordinate_program;

                        if(directProgramHashMap.findValue(master_program_id, master_program) &&
                           directProgramHashMap.findValue(subordinate_program_id, subordinate_program))
                        {
                            (boost::static_pointer_cast< CtiLMProgramDirect >(master_program))->getSubordinatePrograms().insert(boost::static_pointer_cast< CtiLMProgramDirect >(subordinate_program));
                            (boost::static_pointer_cast< CtiLMProgramDirect >(subordinate_program))->getMasterPrograms().insert(boost::static_pointer_cast< CtiLMProgramDirect >(master_program));
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> dout_guard(dout);
                            dout << CtiTime() << " **Checkpoint** " <<  "Unable to find either master program with id: " <<
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
                                 << lmProgramDirectGearTable["kwreduction"]
				 << lmProgramDirectGearTable["frontrampoption"]
				 << lmProgramDirectGearTable["frontramptime"]
				 << lmProgramDirectGearTable["backrampoption"]
				 << lmProgramDirectGearTable["backramptime"]
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
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        while( rdr() )
                        {
                            rdr["settings"] >> isNull;
                            CtiLMProgramDirectGear* newDirectGear = NULL;
                            if( isNull )
                            {
                                newDirectGear = CTIDBG_new CtiLMProgramDirectGear(rdr);
                            }
                            else
                            {
                                newDirectGear = CTIDBG_new CtiLMProgramThermoStatGear(rdr);
                            }
                            if( newDirectGear != NULL )
                            {
                                CtiLMProgramBaseSPtr programToPutGearIn;
                                if( directProgramHashMap.findValue(newDirectGear->getPAOId(),programToPutGearIn) )
                                {
                                    vector<CtiLMProgramDirectGear*>& lmProgramDirectGearList = boost::static_pointer_cast< CtiLMProgramDirect>(programToPutGearIn)->getLMProgramDirectGears();
                                    lmProgramDirectGearList.push_back(newDirectGear);
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
                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                    }

                    RWDBReader rdr = selector.reader(conn);
                    while(rdr())
                    {
                        int program_id;
                        int notif_grp_id;
                        rdr["programid"] >> program_id;
                        rdr["notificationgrpid"] >> notif_grp_id;

                        CtiLMProgramBaseSPtr program;
                        if( directProgramHashMap.findValue(program_id, program ) )
                        {
                            boost::static_pointer_cast< CtiLMProgramDirect >(program)->getNotificationGroupIDs().push_back(notif_grp_id);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> dout_guard(dout);
                            dout << CtiTime() << " **Checkpoint** No program found. " << __FILE__ << "(" << __LINE__ << ")" << endl;
                        }
                    }
                } //loading notification groups end

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << "DB Load Timer for Direct Program Notification Groups is: " << notificationGroupTimer.elapsedTime() << endl;
                    notificationGroupTimer.reset();
                }

                    RWTValHashMap<LONG,CtiLMProgramBaseSPtr,id_hash,equal_to<LONG> > curtailmentProgramHashMap;

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
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }

                        CtiLMProgramCurtailmentSPtr currentLMProgramCurtailment;
                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        while( rdr() )
                        {
                            LONG tempProgramId = 0;
                            rdr["paobjectid"] >> tempProgramId;

                            if( !currentLMProgramCurtailment ||
                                tempProgramId != currentLMProgramCurtailment->getPAOId() )
                            {
                                currentLMProgramCurtailment.reset(CTIDBG_new CtiLMProgramCurtailment(rdr));
                                if( currentLMProgramCurtailment->getManualControlReceivedFlag() )
                                {
                                    currentLMProgramCurtailment->restoreDynamicData(rdr);
                                }
                                //Inserting this curtailment program into hash map
                                curtailmentProgramHashMap.insert( currentLMProgramCurtailment->getPAOId(), currentLMProgramCurtailment );
                                temp_all_program_map.insert(make_pair(currentLMProgramCurtailment->getPAOId(), currentLMProgramCurtailment));
                            }

                            rdr["pointid"] >> isNull;
                            if( !isNull )
                            {
                                LONG tempPointId = 0;
                                LONG tempPointOffset = 0;
                                string tempPointType = "(none)";
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

                        RWTValHashMapIterator<LONG,CtiLMProgramBaseSPtr,id_hash,equal_to<LONG> > itr(curtailmentProgramHashMap);

                        for(;itr();)
                        {
                            CtiLMProgramCurtailmentSPtr currentLMProgramCurtailment = boost::static_pointer_cast< CtiLMProgramCurtailment >(itr.value());

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
                                dout << CtiTime() << " - " << selector.asString().data() << endl;
                            }

                            vector<CtiLMCurtailCustomer*>& lmProgramCurtailmentCustomers = currentLMProgramCurtailment->getLMProgramCurtailmentCustomers();
                            RWDBReader rdr = selector.reader(conn);
                            while( rdr() )
                            {
                                lmProgramCurtailmentCustomers.push_back(CTIDBG_new CtiLMCurtailCustomer(rdr));
                            }

                            if( currentLMProgramCurtailment->getManualControlReceivedFlag() && lmProgramCurtailmentCustomers.size() > 0 )
                            {
                                for(LONG temp=0;temp<lmProgramCurtailmentCustomers.size();temp++)
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


                    RWTValHashMap<LONG,CtiLMProgramBaseSPtr,id_hash,equal_to<LONG> > energyExchangeProgramHashMap;

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
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }

                        CtiLMProgramBaseSPtr currentLMProgramEnergyExchange;
                        RWDBReader rdr = selector.reader(conn);
                        RWDBNullIndicator isNull;
                        while( rdr() )
                        {
                            LONG tempProgramId = 0;
                            rdr["paobjectid"] >> tempProgramId;

                            if( !currentLMProgramEnergyExchange ||
                                tempProgramId != currentLMProgramEnergyExchange->getPAOId() )
                            {
                                currentLMProgramEnergyExchange.reset(CTIDBG_new CtiLMProgramEnergyExchange(rdr));
                                //Inserting this curtailment program into hash map
                                energyExchangeProgramHashMap.insert( currentLMProgramEnergyExchange->getPAOId(), currentLMProgramEnergyExchange );
                                temp_all_program_map.insert(make_pair(currentLMProgramEnergyExchange->getPAOId(), currentLMProgramEnergyExchange));
                            }

                            rdr["pointid"] >> isNull;
                            if( !isNull )
                            {
                                LONG tempPointId = 0;
                                LONG tempPointOffset = 0;
                                string tempPointType = "(none)";
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

                        RWTValHashMapIterator<LONG,CtiLMProgramBaseSPtr,id_hash,equal_to<LONG> > itr(energyExchangeProgramHashMap);

                        for(;itr();)
                        {
                            CtiLMProgramEnergyExchangeSPtr currentLMProgramEnergyExchange = boost::static_pointer_cast< CtiLMProgramEnergyExchange >(itr.value());

                            if( currentLMProgramEnergyExchange->getManualControlReceivedFlag() )
                            {
                                CtiTime currentDateTime;
                                CtiDate currentDate(currentDateTime);
                                CtiTime compareDateTime = CtiTime(currentDate,0,0,0);
                                RWDBTable lmEnergyExchangeProgramOfferTable = db.table("lmenergyexchangeprogramoffer");

                                RWDBSelector selector = db.selector();
                                selector << lmEnergyExchangeProgramOfferTable["deviceid"]
                                         << lmEnergyExchangeProgramOfferTable["offerid"]
                                         << lmEnergyExchangeProgramOfferTable["runstatus"]
                                         << lmEnergyExchangeProgramOfferTable["offerdate"];

                                selector.from(lmEnergyExchangeProgramOfferTable);

                                selector.where(lmEnergyExchangeProgramOfferTable["deviceid"]==currentLMProgramEnergyExchange->getPAOId() &&//will be paobjectid
                                               lmEnergyExchangeProgramOfferTable["offerdate"]>=toRWDBDT(compareDateTime));

                                selector.orderBy(lmEnergyExchangeProgramOfferTable["offerdate"]);
                                selector.orderBy(lmEnergyExchangeProgramOfferTable["offerid"]);

                                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - " << selector.asString().data() << endl;
                                }

                                RWDBReader rdr = selector.reader(conn);
                                std::vector<CtiLMEnergyExchangeOffer*>& offers = currentLMProgramEnergyExchange->getLMEnergyExchangeOffers();
                                while( rdr() )
                                {
                                    offers.push_back(CTIDBG_new CtiLMEnergyExchangeOffer(rdr));
                                }

                                for(LONG r=0;r<offers.size();r++)
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
                                        dout << CtiTime() << " - " << selector.asString().data() << endl;
                                    }

                                    RWDBReader rdr = selector.reader(conn);
                                    vector<CtiLMEnergyExchangeOfferRevision*>& offerRevisions = currentLMEnergyExchangeOffer->getLMEnergyExchangeOfferRevisions();
                                    while( rdr() )
                                    {
                                        offerRevisions.push_back(CTIDBG_new CtiLMEnergyExchangeOfferRevision(rdr));
                                    }

                                    for(LONG s=0;s<offerRevisions.size();s++)
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
                                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                                        }

                                        RWDBReader rdr = selector.reader(conn);
                                        vector<CtiLMEnergyExchangeHourlyOffer*>& hourlyOffers = currentLMEnergyExchangeOfferRevision->getLMEnergyExchangeHourlyOffers();
                                        while( rdr() )
                                        {
                                            hourlyOffers.push_back(CTIDBG_new CtiLMEnergyExchangeHourlyOffer(rdr));
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
                                    dout << CtiTime() << " - " << selector.asString().data() << endl;
                                }

                                std::vector<CtiLMEnergyExchangeCustomer*>& lmEnergyExchangeCustomers = currentLMProgramEnergyExchange->getLMEnergyExchangeCustomers();
                                RWDBReader rdr = selector.reader(conn);
                                while( rdr() )
                                {
                                    lmEnergyExchangeCustomers.push_back(CTIDBG_new CtiLMEnergyExchangeCustomer(rdr));
                                }

                                if( currentLMProgramEnergyExchange->getManualControlReceivedFlag() && lmEnergyExchangeCustomers.size() > 0 )
                                {
                                    for(LONG a=0;a<lmEnergyExchangeCustomers.size();a++)
                                    {
                                        CtiLMEnergyExchangeCustomer* currentLMEnergyExchangeCustomer = (CtiLMEnergyExchangeCustomer*)lmEnergyExchangeCustomers[a];
                                        CtiTime currentDateTime;
                                        CtiDate currentDate(currentDateTime);
                                        CtiTime compareDateTime = CtiTime(currentDate,0,0,0);
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
                                                       lmEnergyExchangeProgramOfferTable["offerdate"]>=toRWDBDT(compareDateTime));

                                        selector.orderBy(lmEnergyExchangeCustomerReplyTable["offerid"]);
                                        selector.orderBy(lmEnergyExchangeCustomerReplyTable["revisionnumber"]);

                                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                                        }

                                        RWDBReader rdr = selector.reader(conn);
                                        vector<CtiLMEnergyExchangeCustomerReply*>& lmEnergyExchangeCustomerReplies = currentLMEnergyExchangeCustomer->getLMEnergyExchangeCustomerReplies();
                                        while( rdr() )
                                        {
                                            lmEnergyExchangeCustomerReplies.push_back(CTIDBG_new CtiLMEnergyExchangeCustomerReply(rdr));
                                        }

                                        for(LONG b=0;b<lmEnergyExchangeCustomerReplies.size();b++)
                                        {
                                            CtiLMEnergyExchangeCustomerReply* currentCustomerReply = (CtiLMEnergyExchangeCustomerReply*)lmEnergyExchangeCustomerReplies[b];
                                            CtiTime currentDateTime;
                                            CtiDate currentDate(currentDateTime);
                                            CtiTime compareDateTime = CtiTime(currentDate,0,0,0);
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
                                                           lmEnergyExchangeProgramOfferTable["offerdate"]>=toRWDBDT(compareDateTime));

                                            selector.orderBy(lmEnergyExchangeHourlyCustomerTable["customerid"]);
                                            selector.orderBy(lmEnergyExchangeHourlyCustomerTable["offerid"]);
                                            selector.orderBy(lmEnergyExchangeHourlyCustomerTable["revisionnumber"]);
                                            selector.orderBy(lmEnergyExchangeHourlyCustomerTable["hour"]);

                                            if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                            {
                                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                                dout << CtiTime() << " - " << selector.asString().data() << endl;
                                            }

                                            RWDBReader rdr = selector.reader(conn);
                                            vector<CtiLMEnergyExchangeHourlyCustomer*>& hourlyCustomers = currentCustomerReply->getLMEnergyExchangeHourlyCustomers();
                                            while( rdr() )
                                            {
                                                hourlyCustomers.push_back(CTIDBG_new CtiLMEnergyExchangeHourlyCustomer(rdr));
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
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);
                        while( rdr() )
                        {
                            CtiLMProgramControlWindow* newWindow = CTIDBG_new CtiLMProgramControlWindow(rdr);
                            CtiLMProgramBaseSPtr programToPutWindowIn;
                            if( directProgramHashMap.findValue(newWindow->getPAOId(),programToPutWindowIn) ||
                                curtailmentProgramHashMap.findValue(newWindow->getPAOId(),programToPutWindowIn) ||
                                energyExchangeProgramHashMap.findValue(newWindow->getPAOId(),programToPutWindowIn) )
                            {
                                std::vector<CtiLMProgramControlWindow*>& lmProgramControlWindowList = programToPutWindowIn->getLMProgramControlWindows();
                                lmProgramControlWindowList.push_back(newWindow);
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
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);

                        CtiLMControlArea* currentLMControlArea = NULL;
                        CtiLMProgramBaseSPtr currentLMProgramBase;
                        RWDBNullIndicator isNull;
                        while( rdr() )
                        {
                            LONG tempControlAreaId = 0;
                            rdr["paobjectid"] >> tempControlAreaId;
                            if( currentLMControlArea == NULL ||
                                tempControlAreaId != currentLMControlArea->getPAOId() )
                            {
                                currentLMControlArea = CTIDBG_new CtiLMControlArea(rdr);
                                temp_control_areas.push_back(currentLMControlArea);
                                temp_all_control_area_map.insert(make_pair(currentLMControlArea->getPAOId(), currentLMControlArea));
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

                                if( !currentLMProgramBase ||
                                    ( currentLMProgramBase &&
                                      currentLMProgramBase->getPAOId() != tempProgramId ) )
                                {
                                    vector<CtiLMProgramBaseSPtr>& lmControlAreaProgramList = currentLMControlArea->getLMPrograms();

                                    if( directProgramHashMap.findValue(tempProgramId,currentLMProgramBase) )
                                    {
                                        currentLMProgramBase->setStartPriority(start_priority);
                                        currentLMProgramBase->setStopPriority(stop_priority);
                                        currentLMProgramBase->setControlArea(currentLMControlArea);
                                        lmControlAreaProgramList.push_back(currentLMProgramBase);
                                        directProgramHashMap.remove(tempProgramId);
                                    }
                                    else if( curtailmentProgramHashMap.findValue(tempProgramId,currentLMProgramBase) )
                                    {
                                        currentLMProgramBase->setStartPriority(start_priority);
                                        currentLMProgramBase->setStopPriority(stop_priority);
                                        currentLMProgramBase->setControlArea(currentLMControlArea);
                                        lmControlAreaProgramList.push_back(currentLMProgramBase);
                                        curtailmentProgramHashMap.remove(tempProgramId);
                                    }
                                    else if( energyExchangeProgramHashMap.findValue(tempProgramId,currentLMProgramBase) )
                                    {
                                        currentLMProgramBase->setStartPriority(start_priority);
                                        currentLMProgramBase->setStopPriority(stop_priority);
                                        currentLMProgramBase->setControlArea(currentLMControlArea);
                                        lmControlAreaProgramList.push_back(currentLMProgramBase);
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
                                string tempPointType = "(none)";
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

                        //This doesnt need to happen with smart pointers
                        //directProgramHashMap.apply(lmprogram_delete, 0);
                        //curtailmentProgramHashMap.apply(lmprogram_delete, 0);
                        //energyExchangeProgramHashMap.apply(lmprogram_delete, 0);


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
                        selector << lmControlAreaTriggerTable["triggerid"]
                                 << lmControlAreaTriggerTable["deviceid"]
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
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
                        }

                        RWDBReader rdr = selector.reader(conn);
                        while( rdr() )
                        {
                            CtiLMControlAreaTrigger* newTrigger = CTIDBG_new CtiLMControlAreaTrigger(rdr);
                            attachProjectionData(newTrigger);
			    newTrigger->calculateProjectedValue();

                            /****************************************************************
                            *******  Inserting Trigger into the correct Control Area  *******
                            ****************************************************************/
                            LONG tempControlAreaId = 0;
                            rdr["deviceid"] >> tempControlAreaId;
                            for(LONG i=0;i<temp_control_areas.size();i++)
                            {
                                CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(temp_control_areas[i]);
                                if( currentLMControlArea->getPAOId() == tempControlAreaId )
                                {
                                    currentLMControlArea->getLMControlAreaTriggers().push_back(newTrigger);
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
                    dout << CtiTime() << " - Unable to get valid database connection." << endl;
                    _isvalid = FALSE;
                    return;
                }
            }
        }

    {
        //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

        // Clear out our old working objects
        _all_control_area_map.clear();
        delete_vector(_controlAreas);
        _controlAreas->clear(); //89
        _all_group_map.clear();
        _all_program_map.clear();
        _point_group_map.clear();

        // Lets start using the new objects we just loaded.
        // do a swap, make sure we have the mux
        *_controlAreas = temp_control_areas;
        _all_group_map = temp_all_group_map;
        _all_program_map = temp_all_program_map;
        _point_group_map = temp_point_group_map;
        _all_control_area_map = temp_all_control_area_map;

        _CHANGED_GROUP_LIST.clear();
        _CHANGED_PROGRAM_LIST.clear();
        _CHANGED_CONTROL_AREA_LIST.clear();

        _isvalid = TRUE;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Control areas reset" << endl;
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        _reregisterforpoints = true;
        _lastdbreloadtime = CtiTime();

        ULONG msgBitMask = CtiLMControlAreaMsg::AllControlAreasSent;
        if( _wascontrolareadeletedflag )
        {
            msgBitMask = CtiLMControlAreaMsg::AllControlAreasSent | CtiLMControlAreaMsg::ControlAreaDeleted;
        }
        _wascontrolareadeletedflag = false;

        // Make sure all the clients get the new control areas, let the main thread do it
        CtiLoadManager::getInstance()->handleMessage(CTIDBG_new CtiLMControlAreaMsg(*_controlAreas,msgBitMask));
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    shutdown

    Dumps the sub bus list.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::shutdown()
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Calling dumpAllDynamicData()" << endl;
    }*/
    dumpAllDynamicData();
    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Done with dumpAllDynamicData()" << endl;
    }*/
    delete_vector(_controlAreas);
    _controlAreas->clear();
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

        string str;
        char var[128];

        strcpy(var, "LOAD_MANAGEMENT_REFRESH");
        if( !(str = gConfigParms.getValueAsString(var)).empty() )
        {
            int tempRefreshRate = atoi(str.c_str());
            if( tempRefreshRate > 0 )
            {
                refreshrate = tempRefreshRate;
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << " is ZERO!!!" << endl;
            }

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << str << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
        }

        CtiTime lastPeriodicDatabaseRefresh;
        CtiTime lastCheck;

        while(TRUE)
        {
            rwRunnable().serviceCancellation();
            CtiTime now;

            // When we cross midnight these dates won't match and we can do our daily midnight maintenance
            if( now.day() != lastCheck.day() )
            {//check to see if it is midnight
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Resetting midnight defaults" << endl;
                }
                checkMidnightDefaultsForReset();
            }

            if( now.seconds() >= lastPeriodicDatabaseRefresh.seconds()+refreshrate )
            {
                //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Periodic restore of control area list from the database" << endl;
                }

                setValid(false);
                lastPeriodicDatabaseRefresh = CtiTime();
            }
            else
            {
                lastCheck = now;
                rwRunnable().sleep(5000);
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        _instance = CTIDBG_new CtiLMControlAreaStore();
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
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _isvalid;
}

/*---------------------------------------------------------------------------
    setValid

    Sets the _isvalid flag
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::setValid(bool valid)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _isvalid = valid;
}

/*---------------------------------------------------------------------------
    getReregisterForPoints

    Gets _reregisterforpoints
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::getReregisterForPoints()
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _reregisterforpoints;
}

/*---------------------------------------------------------------------------
    setReregisterForPoints

    Sets _reregisterforpoints
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::setReregisterForPoints(bool reregister)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _reregisterforpoints = reregister;
}

bool CtiLMControlAreaStore::getWasControlAreaDeletedFlag()
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    return _wascontrolareadeletedflag;
}

void CtiLMControlAreaStore::setWasControlAreaDeletedFlag(bool wasDeleted)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    _wascontrolareadeletedflag = wasDeleted;
}

/*---------------------------------------------------------------------------
    UpdateControlAreaDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the control area.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateControlAreaDisableFlagInDB(CtiLMControlArea* controlArea)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == controlArea->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( (controlArea->getDisableFlag()?"Y":"N") );

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << updater.asString().data() << endl;
        }

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(controlArea->getPAOId(), ChangePAODb,
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
bool CtiLMControlAreaStore::UpdateProgramDisableFlagInDB(CtiLMProgramBaseSPtr program)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == program->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( (program->getDisableFlag()?"Y":"N") );

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << updater.asString().data() << endl;
        }

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(program->getPAOId(), ChangePAODb,
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
bool CtiLMControlAreaStore::UpdateGroupDisableFlagInDB(CtiLMGroupPtr& group)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
        RWDBUpdater updater = yukonPAObjectTable.updater();

        updater.where( yukonPAObjectTable["paobjectid"] == group->getPAOId() );

        updater << yukonPAObjectTable["disableflag"].assign( (group->getDisableFlag()?"Y":"N") );

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << updater.asString().data() << endl;
        }

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(group->getPAOId(), ChangePAODb,
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
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

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
            dout << CtiTime() << " - " << updater.asString().data() << endl;
        }

        updater.execute( conn );

        CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(controlArea->getPAOId(), ChangePAODb,
                                                      controlArea->getPAOCategory(), desolveLoadManagementType(controlArea->getPAOType()),
                                                      ChangeTypeUpdate);
        dbChange->setSource(LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE);
        CtiLoadManager::getInstance()->sendMessageToDispatch(dbChange);

        return updater.status().isValid();
    }
}

/*---------------------------------------------------------------------------
    getLMGroup

    Returns the group based on the ID, returns NULL if the group is not
    in the list.
---------------------------------------------------------------------------*/
CtiLMGroupPtr CtiLMControlAreaStore::getLMGroup(long groupID)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    CtiLMGroupPtr retVal;

    typedef map< long, CtiLMGroupPtr >::iterator GroupMapIter;
    GroupMapIter iter = _all_group_map.find(groupID);

    if( iter != _all_group_map.end() )
    {
        retVal = iter->second;
    }

    return retVal;
}

/*---------------------------------------------------------------------------
    getLMProgram

    Returns the program based on the ID, returns NULL if the program is not
    in the list.
---------------------------------------------------------------------------*/
CtiLMProgramBaseSPtr CtiLMControlAreaStore::getLMProgram(long programID)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    CtiLMProgramBaseSPtr retVal;

    typedef map< long, CtiLMProgramBaseSPtr >::iterator ProgramMapIter;
    ProgramMapIter iter = _all_program_map.find(programID);

    if( iter != _all_program_map.end() )
    {
        retVal = iter->second;
    }

    return retVal;
}

/*---------------------------------------------------------------------------
    getLMControlArea

    Returns the control area based on the ID, returns NULL if the control
    area is not in the list.
---------------------------------------------------------------------------*/
CtiLMControlArea* CtiLMControlAreaStore::getLMControlArea(long controlAreaID)
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());
    CtiLMControlArea* retVal = NULL;

    typedef map< long, CtiLMControlArea* >::iterator ControlAreaMapIter;
    ControlAreaMapIter iter = _all_control_area_map.find(controlAreaID);

    if( iter != _all_control_area_map.end() )
    {
        retVal = iter->second;
    }

    return retVal;
}

/*---------------------------------------------------------------------------
    checkMidnightDefaultsForReset

    Only called at midnight!  Checks each of the control area default
    operational states and updates the area's disable flag if not synched with
    the def op state.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::checkMidnightDefaultsForReset()
{
    //RWRecursiveLock<RWMutexLock>::LockGuard  guard(mutex());

    bool returnBool = false;
    vector<CtiLMControlArea*>& controlAreas = *getControlAreas(CtiTime().seconds());
    for(long i=0;i<controlAreas.size();i++)
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];
        // not equal, so no "!" on the compareTo()
        if( stringCompareIgnoreCase(currentControlArea->getDefOperationalState(),CtiLMControlArea::DefOpStateNone ) )
        {//check default operational state
            if( ( !stringCompareIgnoreCase(currentControlArea->getDefOperationalState(),CtiLMControlArea::DefOpStateEnabled ) &&
                  currentControlArea->getDisableFlag() ) ||
                ( !stringCompareIgnoreCase(currentControlArea->getDefOperationalState(),CtiLMControlArea::DefOpStateDisabled ) &&
                  !currentControlArea->getDisableFlag() ) )
            {
                {
                    string text = ("Automatic Disable Flag Update");
                    string additional = (" Disable Flag not set to Default Operational State.  Control Area: ");
                    additional += currentControlArea->getPAOName();
                    additional += " was automatically ";
                    additional += currentControlArea->getDefOperationalState();
                    additional += ".";
                    CtiLoadManager::getInstance()->sendMessageToDispatch(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - " << text << ", " << additional << endl;
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
                string text = ("Automatic Daily Start Time Update");
                string additional = (" Current Daily Start Time not set to Default Start Time.  Control Area: ");
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
                CtiLoadManager::getInstance()->sendMessageToDispatch(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << text << ", " << additional << endl;
                }
            }
            if( currentControlArea->getCurrentDailyStopTime() != currentControlArea->getDefDailyStopTime() )
            {
                char tempchar[80] = "";
                string text = ("Automatic Daily Stop Time Update");
                string additional = (" Current Daily Stop Time not set to Default Stop Time.  Control Area: ");
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
                CtiLoadManager::getInstance()->sendMessageToDispatch(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << text << ", " << additional << endl;
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
    if( _projectionQueues.size() > 0 )
        _projectionQueues.clear();

    for(LONG i=0;i<_controlAreas->size();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];

        vector<CtiLMControlAreaTrigger*>& lmControlAreaTriggers = currentLMControlArea->getLMControlAreaTriggers();
        if( lmControlAreaTriggers.size() > 0 )
        {
            for(LONG j=0;j<lmControlAreaTriggers.size();j++)
            {
                CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)lmControlAreaTriggers.at(j);
                if( !stringCompareIgnoreCase(currentLMControlAreaTrigger->getProjectionType(),CtiLMControlAreaTrigger::LSFProjectionType ) &&
                    stringCompareIgnoreCase(currentLMControlAreaTrigger->getTriggerType(),CtiLMControlAreaTrigger::StatusTriggerType ) )
                {// don't need "!" on compareTo() because supposed to be !=
                    /*{
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << " - Saved Projection Point Entries Queue entries for Point Id: " << currentLMControlAreaTrigger->getPointId() << endl;
                        for(int k=0;k<currentLMControlAreaTrigger->getProjectionPointEntriesQueue().entries();k++)
                        {
                            dout << " Entry number: " << k << " value: " << currentLMControlAreaTrigger->getProjectionPointEntriesQueue()[k].getValue() << " timestamp: " << currentLMControlAreaTrigger->getProjectionPointEntriesQueue()[k].getTimestamp() << endl;
                        }
                    }*/
                    _projectionQueues.push_back(CtiLMSavedProjectionQueue(currentLMControlAreaTrigger->getPointId(), currentLMControlAreaTrigger->getProjectionPointEntriesQueue()));
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
    for(LONG i=0;i<_projectionQueues.size();i++)
    {
        CtiLMSavedProjectionQueue currentSavedQueue =  _projectionQueues[i];
        if( trigger->getPointId() == currentSavedQueue.getPointId() )
        {
            std::vector<CtiLMProjectionPointEntry> queueToCopyFrom = currentSavedQueue.getProjectionEntryList();
            std::vector<CtiLMProjectionPointEntry>& queueToFillUp = trigger->getProjectionPointEntriesQueue();
            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << " - Restored Projection Point Entries Queue entries for PointId: " << trigger->getPointId() << endl;
                for(int k=0;k<queueToCopyFrom.entries();k++)
                {
                    dout << " Entry number: " << k << " value: " << queueToCopyFrom[k].getValue() << " timestamp: " << queueToCopyFrom[k].getTimestamp() << endl;
                }
            }*/
            std::vector<CtiLMProjectionPointEntry>::iterator itr = queueToCopyFrom.begin();
            for( ; itr != queueToCopyFrom.end(); ++itr)
            {
                queueToFillUp.push_back( *itr );
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
    if( _controlStrings.size() > 0 )
        _controlStrings.clear();

    for(LONG i=0;i<_controlAreas->size();i++)
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];

        vector<CtiLMProgramBaseSPtr>& lmPrograms = currentLMControlArea->getLMPrograms();
        if( lmPrograms.size() > 0 )
        {
            for(LONG j=0;j<lmPrograms.size();j++)
            {
                CtiLMProgramBaseSPtr currentLMProgram = (CtiLMProgramBaseSPtr)lmPrograms[j];
                if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                {
                    CtiLMGroupVec groups  = boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->getLMProgramDirectGroups();
                    for(CtiLMGroupIter k = groups.begin(); k != groups.end(); k++)
                    {
                        CtiLMGroupPtr currentLMGroup  = *k;
                        if( currentLMGroup->getLastControlString().length() > 0 )
                        {
                            _controlStrings.push_back(CtiLMSavedControlString(currentLMGroup->getPAOId(), currentLMGroup->getLastControlString()));
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
void CtiLMControlAreaStore::attachControlStringData(CtiLMGroupPtr& group)
{
    for(LONG i=0;i<_controlStrings.size();i++)
    {
        CtiLMSavedControlString currentSavedString =  _controlStrings[i];
        if( group->getPAOId() == currentSavedString.getPAOId() )
        {
            group->setLastControlString(currentSavedString.getControlString());
            break;
        }
    }
}

const string CtiLMControlAreaStore::LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE = "LOAD_MANAGEMENT_SERVER";


//*************************************************************
//**********  CtiLMSavedProjectionQueue              **********
//**********                                         **********
//**********  This is equivalent to an inner class,  **********
//**********  only used for saving projections       **********
//*************************************************************
CtiLMSavedProjectionQueue::CtiLMSavedProjectionQueue(LONG pointId, const std::vector<CtiLMProjectionPointEntry>& projectionEntryList)
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
const std::vector<CtiLMProjectionPointEntry>& CtiLMSavedProjectionQueue::getProjectionEntryList() const
{
    return _projectionEntryList;
}

CtiLMSavedProjectionQueue& CtiLMSavedProjectionQueue::setPointId(LONG pointId)
{
    _pointId = pointId;
    return *this;
}
CtiLMSavedProjectionQueue& CtiLMSavedProjectionQueue::setProjectionEntryList(const std::vector<CtiLMProjectionPointEntry>& projectionEntryList)
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
CtiLMSavedControlString::CtiLMSavedControlString(LONG paoId, const string& controlString)
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
const string& CtiLMSavedControlString::getControlString() const
{
    return _controlString;
}

CtiLMSavedControlString& CtiLMSavedControlString::setPAOId(LONG paoId)
{
    _paoId = paoId;
    return *this;
}
CtiLMSavedControlString& CtiLMSavedControlString::setControlString(const string& controlstr)
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

