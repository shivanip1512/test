#include "precompiled.h"

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
#include "sepcyclegear.h"
#include "septempoffsetgear.h"
#include "ecobeeCycleGear.h"
#include "ecobeeSetpointGear.h"
#include "honeywellCycleGear.h"
#include "NestCriticalCycleGear.h"
#include "NestStandardCycleGear.h"
#include "ItronCycleGear.h"
#include "MeterDisconnectGear.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"
#include "database_util.h"
#include "dllbase.h"
#include "logger.h"
#include "msg_dbchg.h"
#include "loadmanager.h"
#include "lmfactory.h"
#include "utility.h"
#include "tbl_paoexclusion.h"
#include "tbl_lmprogramhistory.h"
#include "database_connection.h"
#include "database_writer.h"
#include "debug_timer.h"
#include "clistener.h"
#include "lmprogrambeatthepeakgear.h"
#include "std_helper.h"
#include "millisecond_timer.h"

using namespace std;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;
using Cti::Timing::MillisecondTimer;

extern ULONG _LM_DEBUG;
extern set<long> _CHANGED_GROUP_LIST;
extern set<long> _CHANGED_CONTROL_AREA_LIST;
extern set<long> _CHANGED_PROGRAM_LIST;

void lmprogram_delete(const LONG& program_id, CtiLMProgramBase*const& lm_program, void* d)
{
    delete lm_program;
}

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMControlAreaStore::CtiLMControlAreaStore() : _isvalid(false), _reregisterforpoints(true), _lastdbreloadtime(gInvalidCtiTime), _wascontrolareadeletedflag(false)
{
    _controlAreas = CTIDBG_new vector<CtiLMControlArea*>;
    //Start the reset thread
    _resetthr = boost::thread( &CtiLMControlAreaStore::doResetThr, this );
}

/*--------------------------------------------------------------------------
    Destrutor
-----------------------------------------------------------------------------*/
CtiLMControlAreaStore::~CtiLMControlAreaStore()
{
    _resetthr.interrupt();

    if (!_resetthr.timed_join(boost::posix_time::seconds(30))) 
    {
        CTILOG_WARN( dout, "Load Manager thread did not shutdown gracefully. "
                           "Attempting a forced shutdown" );

        TerminateThread( _resetthr.native_handle(), EXIT_SUCCESS );
    }

    shutdown();
}

/*---------------------------------------------------------------------------
    getControlAreas

    Returns a Vector of CtiLMControlAreas
---------------------------------------------------------------------------*/
vector<CtiLMControlArea*>* CtiLMControlAreaStore::getControlAreas(CtiTime currentTime)
{
    if( !_isvalid && currentTime >= (_lastdbreloadtime + 90) )//is not valid and has been at 1 1/2 minutes from last db reload, so we don't do this a bunch of times in a row on multiple updates
    {
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
    vector<CtiLMControlArea*>* controlAreas = getControlAreas(CtiTime());
    for( LONG i=0; i < controlAreas->size(); i++ )
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*) (*controlAreas)[i];
        vector<CtiLMProgramBaseSPtr>& lmPrograms = currentControlArea->getLMPrograms();
        for( LONG j=0; j < lmPrograms.size(); j++ )
        {
            CtiLMProgramBaseSPtr currentLMProgram = (CtiLMProgramBaseSPtr) lmPrograms[j];
            if( programID == currentLMProgram->getPAOId() )
            {
                if( controlArea != NULL )
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
    return(iter == _point_group_map.end() ? CtiLMGroupPtr() : iter->second);
}

/*----------------------------------------------------------------------------
  Returns a vector with pointers to the control areas that reference the point
  id. This can be multiple control areas as trigger inputs can be shared.
----------------------------------------------------------------------------*/
vector<CtiLMControlArea*> CtiLMControlAreaStore::findControlAreasByPointID(long point_id)
{
    vector<CtiLMControlArea*> retVal;

    std::multimap< long, long >::_Pairii range = _point_control_area_map.equal_range(point_id);
    for( ; range.first != range.second; ++range.first )
    {
        CtiLMControlArea* area = getLMControlArea(range.first->second);
        retVal.push_back(area);
    }

    return retVal;
}

/*---------------------------------------------------------------------------
    dumpAllDynamicData

    Writes out the dynamic information for each of the control areas.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::dumpAllDynamicData()
{
    if( _controlAreas->size() > 0 )
    {
        Cti::Timing::DebugTimer debugTime("Dump All Dynamic Data", true, 3);
        CtiTime currentDateTime = CtiTime();

        Cti::Database::DatabaseConnection   conn;

        if ( ! conn.isValid() )
        {
            CTILOG_ERROR(dout, "Invalid Connection to Database.");

            return;
        }

        for( LONG i=0;i<_controlAreas->size();i++ )
        {
            CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];
            currentLMControlArea->dumpDynamicData(conn,currentDateTime);

            vector<CtiLMProgramBaseSPtr>& lmPrograms = currentLMControlArea->getLMPrograms();
            if( lmPrograms.size() > 0 )
            {
                for( LONG j=0;j<lmPrograms.size();j++ )
                {
                    CtiLMProgramBaseSPtr currentLMProgramBase = (CtiLMProgramBaseSPtr)lmPrograms[j];
                    currentLMProgramBase->dumpDynamicData(conn,currentDateTime);

                    if( currentLMProgramBase->getPAOType() ==  TYPE_LMPROGRAM_DIRECT )
                    {
                        CtiLMProgramDirectSPtr currentLMProgramDirect = boost::static_pointer_cast< CtiLMProgramDirect>(currentLMProgramBase);

                        CtiLMGroupVec groups  = currentLMProgramDirect->getLMProgramDirectGroups();
                        for( CtiLMGroupIter k = groups.begin(); k != groups.end(); k++ )
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

                            for( LONG k=0;k<lmCurtailCustomers.size();k++ )
                            {
                                CtiLMCurtailCustomer* currentLMCurtailCustomer = (CtiLMCurtailCustomer*)lmCurtailCustomers[k];
                                currentLMCurtailCustomer->dumpDynamicData(conn,currentDateTime);
                            }

                        }
                    }
                }
            }
        }
    }
    else
    {
        CTILOG_INFO(dout, "***No control areas to write to dynamic tables***");
    }
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
        std::multimap<long, long >     temp_point_control_area_map;
        std::map<long, CtiLMProgramBaseSPtr > temp_all_program_map;
        std::map<long, CtiLMControlArea* > temp_all_control_area_map;

        MillisecondTimer    overallTimer;
        CTILOG_INFO(dout, "Starting Database Reload...");

        {
            Cti::Database::DatabaseConnection connection;
            {
                if( _controlAreas->size() > 0 )   //Save off current data to database so that if can be loaded by new objects on reload
                {
                    dumpAllDynamicData();
                    saveAnyProjectionData();
                    saveAnyControlStringData();
                }
            }
            CtiTime currentDateTime;

            std::set<long>  controlPointHashMap;
            {//loading controllable statuses

                static const string sql =
                    "SELECT"
                        " PSC.PointId"
                    " FROM"
                        " PointStatusControl PSC"
                        " JOIN PointControl PC on PSC.PointId = PC.PointId"
                    " WHERE"
                        " PC.ControlOffset = 1";

                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                while( rdr() )
                {
                    LONG tempPointId = 0;
                    rdr["pointid"] >> tempPointId;

                    controlPointHashMap.insert( tempPointId );
                }
            }

            MillisecondTimer    componentLoadingTimer;

            /* First load all the groups, and put them into a map by group id */
            std::map< long, CtiLMGroupPtr > all_assigned_group_map; //remember which groups we have assigned
            std::map< long, vector<CtiLMGroupPtr> > all_program_group_map;

            {
                static const string sql =  "SELECT YP.paobjectid as groupid, YP.category, YP.paoclass, YP.paoname, "
                                             "YP.type, YP.description, YP.disableflag, DV.alarminhibit, "
                                             "DV.controlinhibit, LG.kwcapacity "
                                           "FROM yukonpaobject YP, device DV, lmgroup LG "
                                           "WHERE YP.paobjectid = LG.deviceid AND YP.paobjectid = DV.deviceid AND "
                                             "YP.paobjectid > 0";

                CtiLMGroupFactory lm_group_factory;
                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                while( rdr() )
                {
                    CtiLMGroupPtr lm_group = lm_group_factory.createLMGroup(rdr);
                    temp_all_group_map.insert(make_pair(lm_group->getPAOId(), lm_group));
                    attachControlStringData(lm_group);
                }

            } //end main group loading

            { /* Load up any group point specific information */
                static const string sql =  "SELECT LGP.deviceid as groupid, LGP.deviceidusage as deviceid, "
                                             "LGP.pointidusage as pointid, LGP.startcontrolrawstate "
                                           "FROM lmgrouppoint LGP";

                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                while( rdr() )
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
                    if( iter != temp_all_group_map.end() )
                    {
                        CtiLMGroupPoint* lm_group = (CtiLMGroupPoint*) iter->second.get();
                        lm_group->setDeviceIdUsage(device_id);
                        lm_group->setPointIdUsage(point_id);
                        lm_group->setStartControlRawState(start_control_raw_state);
                    }
                    else
                    {
                        CTILOG_WARN(dout, "Rows exist in the LMGroupPoint table exist but do not correspond with any lm groups already loaded.  Either groups didn't get loaded correctly or the LMGroupPoint table has missing constraints?");
                    }
                }
            }// end loading group point specific info

            { /* Start loading ripple group specific info */
                static const string sql = "SELECT LGR.deviceid as groupid, LGR.shedtime "
                                          "FROM lmgroupripple LGR";

                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                while( rdr() )
                {
                    int group_id;
                    int shed_time;

                    rdr >> group_id;
                    rdr >> shed_time;

                    std::map< long, CtiLMGroupPtr >::iterator iter = temp_all_group_map.find(group_id);
                    if( iter != temp_all_group_map.end() )
                    {
                        CtiLMGroupRipple* lm_group = (CtiLMGroupRipple*) iter->second.get();
                        lm_group->setShedTime(shed_time);
                    }
                    else
                    {
                        CTILOG_WARN(dout, "Rows exist in the LMGroupRipple table exist but do not correspond with any lm groups already loaded.  Either groups didn't get loaded correctly or the LMGroupRipple table has missing constraints?");
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

                static const string sql =  "SELECT PT.paobjectid, PT.pointid, PT.pointoffset, PT.pointtype "
                                           "FROM point PT, lmgroup LMG "
                                           "WHERE PT.paobjectid = LMG.deviceid";

                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

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

                    if( group_id <= 0 ) //we don't deal with groups with 0 or negative ids
                    {
                        continue;
                    }

                    std::map< long, CtiLMGroupPtr >::iterator iter = temp_all_group_map.find(group_id);
                    if( iter != temp_all_group_map.end() )
                    {
                        CtiLMGroupPtr lm_group = iter->second;

                        switch( resolvePointType(point_type.c_str()) )
                        {
                        case AnalogPointType:
                            switch( point_offset )
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
                                break;
                            }
                            break;
                        case StatusPointType:
                            if( point_offset == 0 )
                            {
                                if( controlPointHashMap.count(point_id) )
                                {
                                    lm_group->setControlStatusPointId(point_id);
                                    temp_point_group_map.insert(make_pair(point_id,lm_group));
                                }
                            }
                            break;
                        default:
                            CTILOG_WARN(dout, "Unknown point type:  " << resolvePointType(point_type.c_str()));
                        }
                    }
                }
            }

            /* Load dynamic group information */
            {
                static const string sql =  "SELECT DLG.deviceid as groupid, DLG.groupcontrolstate, DLG.currenthoursdaily, "
                                               "DLG.currenthoursmonthly, DLG.currenthoursseasonal, DLG.currenthoursannually, "
                                               "DLG.lastcontrolsent, DLG.timestamp, DLG.controlstarttime, "
                                               "DLG.controlcompletetime, DLG.nextcontroltime, DLG.internalstate, "
                                               "DLG.dailyops, DLG.laststoptimesent "
                                           "FROM dynamiclmgroup DLG";

                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                while( rdr() )
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
                    CtiTime lastStopTimeSent;
                    long internal_state;
                    long daily_ops;
                    bool constraint_override;

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
                    rdr["laststoptimesent"] >> lastStopTimeSent;

                    std::map< long, CtiLMGroupPtr >::iterator iter = temp_all_group_map.find(group_id);
                    if( iter != temp_all_group_map.end() )
                    {
                        CtiLMGroupPtr& lm_group = iter->second;

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
                        lm_group->setLastStopTimeSent(lastStopTimeSent);

                        lm_group->setDirty(false);
                        lm_group->_insertDynamicDataFlag = FALSE;
                    }
                }
            }
            /* Now lets load up info about macro groups */
            std::map< long, vector<long> > group_macro_map;  //ownerid, <childid>
            {
                static const string sql =  "SELECT GM.ownerid, GM.childid "
                                           "FROM genericmacro GM, lmgroup LMG "
                                           "WHERE GM.ownerid= LMG.deviceid "
                                           "ORDER BY GM.ownerid ASC, GM.childorder ASC";

                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                std::map<long, vector<long> >::iterator iter;
                while( rdr() )
                {
                    long owner_id;
                    long child_id;

                    rdr["ownerid"] >> owner_id;
                    rdr["childid"] >> child_id;

                    iter = group_macro_map.find(owner_id);
                    if( iter == group_macro_map.end() )
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
                static const string sql =  "SELECT PDG.deviceid as programid, PDG.lmgroupdeviceid as groupid "
                                           "FROM lmprogramdirectgroup PDG "
                                           "ORDER BY PDG.grouporder ASC";

                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                std::map<long, vector<CtiLMGroupPtr> >::iterator cur_iter;
                long cur_program = -1;
                while( rdr() )
                {
                    long program_id;
                    long group_id;
                    rdr["programid"] >> program_id;
                    rdr["groupid"] >> group_id;

                    cur_iter = all_program_group_map.find(program_id);
                    if( cur_iter == all_program_group_map.end() )
                    {
                        vector<CtiLMGroupPtr> group_vec;

                        std::map<long, CtiLMGroupPtr >::iterator temp_all_group_map_iter = temp_all_group_map.find(group_id);
                        if( temp_all_group_map_iter != temp_all_group_map.end() )
                        {
                            CtiLMGroupPtr lm_group = temp_all_group_map_iter->second;

                            std::map<long, vector<long> >::iterator macro_iter = group_macro_map.find(lm_group->getPAOId());
                            if( macro_iter != group_macro_map.end() ) //must be a macro group
                            {
                                vector<long> macro_vec = macro_iter->second;

                                for( vector<long>::iterator iter = macro_vec.begin();
                                   iter != macro_vec.end();
                                   iter++ ) //iterate over all the children in this macro group and insert them in place of the owner (macrogroup)
                                {
                                    std::map<long, CtiLMGroupPtr >::iterator all_group_map_iter = temp_all_group_map.find(*iter);
                                    if( all_group_map_iter != temp_all_group_map.end() )
                                    {
                                        CtiLMGroupPtr child_group = all_group_map_iter->second;

                                        group_vec.push_back(child_group);
                                        child_group->setGroupOrder(group_vec.size());
                                        all_assigned_group_map.insert(make_pair(child_group->getPAOId(), child_group));
                                    }
                                }
                            }
                            else //not a macro group, assign it to the program
                            {
                                group_vec.push_back(lm_group);
                                lm_group->setGroupOrder(group_vec.size());
                                all_assigned_group_map.insert(make_pair(lm_group->getPAOId(), lm_group));
                            }
                        }

                        all_program_group_map.insert(make_pair(program_id, group_vec));
                    }
                    else
                    {
                        std::map<long, CtiLMGroupPtr >::iterator temp_all_group_map_iter = temp_all_group_map.find(group_id);
                        if( temp_all_group_map_iter != temp_all_group_map.end() )
                        {
                            CtiLMGroupPtr& lm_group = temp_all_group_map_iter->second;

                            std::map<long, vector<long> >::iterator macro_iter = group_macro_map.find(lm_group->getPAOId());
                            if( macro_iter != group_macro_map.end() ) //must be a macro group
                            {
                                vector<long> macro_vec = macro_iter->second;

                                for( vector<long>::iterator iter = macro_vec.begin();
                                   iter != macro_vec.end();
                                   iter++ ) //iterate over all the children in this macro group and insert them in place of the owner (macrogroup)
                                {
                                    std::map<long, CtiLMGroupPtr >::iterator all_group_map_iter = temp_all_group_map.find(*iter);
                                    if( all_group_map_iter != temp_all_group_map.end() )
                                    {
                                        CtiLMGroupPtr& child_group = all_group_map_iter->second;

                                        cur_iter->second.push_back(child_group);
                                        child_group->setGroupOrder(cur_iter->second.size());
                                        all_assigned_group_map.insert(make_pair(child_group->getPAOId(), child_group));
                                    }
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
            }

            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CTILOG_DEBUG(dout, "DB Load Timer for All Groups is: " << componentLoadingTimer.elapsed() / 1000
                             << endl << "Loaded a total of " << temp_all_group_map.size() << " groups, " << group_macro_map.size() << " of them are in macro groups"
                             << endl << all_program_group_map.size() << "==" << all_assigned_group_map.size() << " groups are assigned to programs");
            }

            std::map<long, CtiLMProgramBaseSPtr>  directProgramHashMap;

            componentLoadingTimer.reset();
            {//loading direct programs start

                static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                               "YP.description, YP.disableflag, PV.controltype, PV.constraintid, "
                                               "PV.constraintname, PV.availableweekdays, PV.maxhoursdaily, "
                                               "PV.maxhoursmonthly, PV.maxhoursseasonal, PV.maxhoursannually, "
                                               "PV.minactivatetime, PV.minrestarttime, PV.maxdailyops, "
                                               "PV.maxactivatetime, PV.holidayscheduleid, PV.seasonscheduleid, "
                                               "PD.heading, PD.messageheader, PD.messagefooter, PD.notifyactiveoffset, "
                                               "PD.notifyinactiveoffset, PD.notifyadjust, PD.notifyschedule, PD.triggeroffset, "
                                               "PD.restoreoffset, DP.programstate, DP.reductiontotal, DP.startedcontrolling, "
                                               "DP.lastcontrolsent, DP.manualcontrolreceivedflag, DPD.currentgearnumber, "
                                               "DPD.lastgroupcontrolled, DPD.starttime, DPD.stoptime, DPD.timestamp, "
                                               "DPD.notifyactivetime, DPD.notifyinactivetime, DPD.startedrampingout, "
                                               "DPD.constraintoverride, DPD.additionalinfo, DPD.currentlogid, DPD.Origin, "
                                               "PT.pointid, PT.pointoffset, PT.pointtype "
                                           "FROM lmprogramdirect PD, yukonpaobject YP, "
                                               "lmprogram_view PV LEFT OUTER JOIN dynamiclmprogram DP ON "
                                               "PV.deviceid = DP.deviceid LEFT OUTER JOIN dynamiclmprogramdirect DPD ON "
                                               "PV.deviceid = DPD.deviceid LEFT OUTER JOIN point PT ON "
                                               "PV.deviceid = PT.paobjectid "
                                           "WHERE YP.paobjectid = PD.deviceid AND PD.deviceid = PV.deviceid "
                                           "ORDER BY YP.paobjectid ASC";

                CtiLMProgramDirectSPtr currentLMProgramDirect;
                DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

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
                        std::map< long, vector<CtiLMGroupPtr> >::iterator   group_vec_map_iter;
                        if( (group_vec_map_iter = all_program_group_map.find(tempProgramId)) != all_program_group_map.end() )
                        {
                            CtiLMGroupVec group_vec = group_vec_map_iter->second;

                            for( CtiLMGroupIter iter = group_vec.begin();
                               iter != group_vec.end();
                               iter++ )
                            {
                                directGroups.push_back(*iter);
                                total_groups_assigned++;
                            }
                        }

                        //Inserting this direct program into hash map
                        directProgramHashMap.insert(make_pair(currentLMProgramDirect->getPAOId(), currentLMProgramDirect));
                        temp_all_program_map.insert(make_pair(currentLMProgramDirect->getPAOId(), currentLMProgramDirect));
                    }

                    if( !rdr["pointid"].isNull() )
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
                CTILOG_DEBUG(dout, "DB Load Timer for Direct Programs is: " << componentLoadingTimer.elapsed() / 1000);
            }

            componentLoadingTimer.reset();
            { // loading master - subordinate direct program relationships
                static const string sql =  "SELECT PEX.paoid, PEX.excludedpaoid "
                                           "FROM paoexclusion PEX "
                                           "WHERE PEX.functionid = ?";

                Cti::Database::DatabaseReader rdr(connection, sql);

                rdr << static_cast<int>(CtiTablePaoExclusion::ExFunctionLMSubordination);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                while( rdr() )
                {
                    long master_program_id;
                    long subordinate_program_id;
                    rdr["paoid"] >> master_program_id;
                    rdr["excludedpaoid"] >> subordinate_program_id;

                    boost::optional<CtiLMProgramBaseSPtr>   master_program      = Cti::mapFind( directProgramHashMap, master_program_id ),
                                                            subordinate_program = Cti::mapFind( directProgramHashMap, subordinate_program_id );

                    if ( master_program && subordinate_program )
                    {
                        (boost::static_pointer_cast< CtiLMProgramDirect >(*master_program))->getSubordinatePrograms().insert(boost::static_pointer_cast< CtiLMProgramDirect >(*subordinate_program));
                        (boost::static_pointer_cast< CtiLMProgramDirect >(*subordinate_program))->getMasterPrograms().insert(boost::static_pointer_cast< CtiLMProgramDirect >(*master_program));
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "Unable to find either master program with id: " << master_program_id << " or subordinate program with id: " << subordinate_program_id <<
                                     " -- This suggests that database constraints on table PAOExclusion aren't correct or that we failed to load one or both of the programs.");
                    }

                }
            }

            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CTILOG_DEBUG(dout, "DB Load Timer for associating master/subordinate programs is: " << componentLoadingTimer.elapsed() / 1000);
            }

            componentLoadingTimer.reset();
            {//loading direct gears start

                static const string sql =  "SELECT PDG.deviceid, PDG.gearname, PDG.gearnumber, PDG.controlmethod, "
                                               "PDG.methodrate, PDG.methodperiod, PDG.methodratecount, "
                                               "PDG.cyclerefreshrate, PDG.methodstoptype, PDG.changecondition, "
                                               "PDG.changeduration, PDG.changepriority, PDG.changetriggernumber, "
                                               "PDG.changetriggeroffset, PDG.percentreduction, PDG.groupselectionmethod, "
                                               "PDG.methodoptiontype, PDG.methodoptionmax, PDG.gearid, PDG.rampininterval, "
                                               "PDG.rampinpercent, PDG.rampoutinterval, PDG.rampoutpercent, "
                                               "PDG.kwreduction, PDG.stopcommandrepeat, PDG.frontrampoption, PDG.frontramptime, "
                                               "PDG.backrampoption, PDG.backramptime, TSG.settings, TSG.minvalue, "
                                               "TSG.maxvalue, TSG.valueb, TSG.valued, TSG.valuef, TSG.random, TSG.valueta, "
                                               "TSG.valuetb, TSG.valuetc, TSG.valuetd, TSG.valuete, TSG.valuetf, "
                                               "TSG.ramprate, BTPG.AlertLevel, "
                                               "NLSG.PreparationOption, NLSG.PeakOption, NLSG.PostPeakOption, "
                                               "ICG.CycleOption "
                                           "FROM lmprogramdirectgear PDG "
                                           "LEFT OUTER JOIN lmthermostatgear TSG "
                                               "ON PDG.gearid = TSG.gearid "
                                           "LEFT OUTER JOIN lmbeatthepeakgear BTPG "
                                               "ON PDG.gearid = BTPG.gearid "
                                           "LEFT OUTER JOIN LMNestLoadShapingGear NLSG "
                                               "ON PDG.GearID = NLSG.GearId "
                                           "LEFT OUTER JOIN LMItronCycleGear ICG "
                                               "ON PDG.GearID = ICG.GearId "
                                           "ORDER BY PDG.deviceid ASC, PDG.gearnumber ASC";

                DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                while( rdr() )
                {
                    string controlmethod;
                    CtiLMProgramDirectGear* newDirectGear = NULL;

                    rdr["controlmethod"] >> controlmethod;

                    // NOTE, due to DBEditor problems, the thermostat table may exist even for non thermostat control methods.
                    // If it does exist and is not needed, CtiLMProgramThermoStatGear loads it but never uses it.
                    // SEPCycleGear never loads it. There are two independent loading concepts here, the SEP way (distinct classes
                    // per gear) and the original way (one class for many gears).
                    if( ciStringEqual(controlmethod,CtiLMProgramDirectGear::SEPCycleMethod) )
                    {
                        newDirectGear = CTIDBG_new SEPCycleGear(rdr);
                    }
                    else if( ciStringEqual(controlmethod,CtiLMProgramDirectGear::SEPTempOffsetMethod) )
                    {
                        if( !rdr["settings"].isNull() )
                        {
                            newDirectGear = CTIDBG_new SEPTemperatureOffsetGear(rdr);
                        }
                    }
                    else if( ciStringEqual(controlmethod,CtiLMProgramDirectGear::BeatThePeakMethod) )
                    {
                        if( !rdr["AlertLevel"].isNull() )
                        {
                            newDirectGear = CTIDBG_new CtiLMProgramBeatThePeakGear(rdr);
                        }
                    }
                    else if ( ciStringEqual(controlmethod, CtiLMProgramDirectGear::EcobeeCycleMethod) )
                    {
                        newDirectGear = CTIDBG_new EcobeeCycleGear(rdr);
                    }
                    else if ( ciStringEqual(controlmethod, CtiLMProgramDirectGear::EcobeeSetpointMethod) )
                    {
                        if ( ! rdr["settings"].isNull() )
                        {
                            newDirectGear = CTIDBG_new EcobeeSetpointGear(rdr);
                        }
                        else
                        {
                            CTILOG_ERROR( dout, "ecobee Setpoint Gear missing required temperature settings" );
                        }
                    }
                    else if ( ciStringEqual(controlmethod, CtiLMProgramDirectGear::HoneywellCycleMethod) )
                    {
                        newDirectGear = CTIDBG_new HoneywellCycleGear(rdr);
                    }
                    else if ( ciStringEqual(controlmethod, CtiLMProgramDirectGear::NestCriticalCycleMethod) )
                    {
                        newDirectGear = CTIDBG_new NestCriticalCycleGear(rdr);
                    }
                    else if ( ciStringEqual(controlmethod, CtiLMProgramDirectGear::NestStandardCycleMethod) )
                    {
                        if ( ! rdr[ "PreparationOption" ].isNull() )
                        {
                            newDirectGear = CTIDBG_new NestStandardCycleGear(rdr);
                        }
                    }
                    else if ( ciStringEqual(controlmethod, CtiLMProgramDirectGear::ItronCycleMethod) )
                    {
                        newDirectGear = CTIDBG_new Cti::LoadManagement::ItronCycleGear(rdr);
                    }
                    else if ( ciStringEqual(controlmethod, CtiLMProgramDirectGear::MeterDisconnectMethod) )
                    {
                        newDirectGear = CTIDBG_new Cti::LoadManagement::MeterDisconnectGear(rdr);
                    }
                    else if (rdr["settings"].isNull())
                    {
                        newDirectGear = CTIDBG_new CtiLMProgramDirectGear(rdr);
                    }
                    else
                    {
                        newDirectGear = CTIDBG_new CtiLMProgramThermostatGear(rdr);
                    }

                    if( newDirectGear != NULL )
                    {
                        if( boost::optional<CtiLMProgramBaseSPtr> programToPutGearIn = Cti::mapFind( directProgramHashMap, newDirectGear->getProgramPAOId() ) )
                        {
                            boost::static_pointer_cast<CtiLMProgramDirect>(*programToPutGearIn)->addGear(newDirectGear);
                        }
                    }
                    else // Currently this is only hit if the EcobeeThermostat gear, SEPTemperatureOffset gear or BeatThePeak gear is misconfigured.
                    {
                        string gearName;
                        int gearId;
                        rdr["gearname"] >> gearName;
                        rdr["gearid"] >> gearId;

                        CTILOG_ERROR(dout, "Gear setup is invalid for gear " << gearName << " " << gearId);
                    }
                }
            }//loading direct gears end
            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CTILOG_DEBUG(dout, "DB Load Timer for Direct Gears is: " << componentLoadingTimer.elapsed() / 1000);
            }

            componentLoadingTimer.reset();
            { //loading notification groups start

                static const string sql = "SELECT DNG.programid, DNG.notificationgrpid "
                                          "FROM lmdirectnotifgrplist DNG";

                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                while( rdr() )
                {
                    int program_id;
                    int notif_grp_id;
                    rdr["programid"] >> program_id;
                    rdr["notificationgrpid"] >> notif_grp_id;

                    if( boost::optional<CtiLMProgramBaseSPtr> program = Cti::mapFind( directProgramHashMap, program_id ) )
                    {
                        boost::static_pointer_cast< CtiLMProgramDirect >(*program)->getNotificationGroupIDs().push_back(notif_grp_id);
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "No program found with ID " << program_id);
                    }
                }
            } //loading notification groups end

            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CTILOG_DEBUG(dout, "DB Load Timer for Direct Program Notification Groups is: " << componentLoadingTimer.elapsed() / 1000);
            }

            std::map<long, CtiLMProgramBaseSPtr> curtailmentProgramHashMap;

            componentLoadingTimer.reset();
            {//loading curtailment programs start

                static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                               "YP.description, YP.disableflag, LPV.controltype, LPV.constraintid, "
                                               "LPV.constraintname, LPV.availableweekdays, LPV.maxhoursdaily, "
                                               "LPV.maxhoursmonthly, LPV.maxhoursseasonal, LPV.maxhoursannually, "
                                               "LPV.minactivatetime, LPV.minrestarttime, LPV.maxdailyops, "
                                               "LPV.maxactivatetime, LPV.holidayscheduleid, LPV.seasonscheduleid, "
                                               "LPC.minnotifytime, LPC.heading, LPC.messageheader, LPC.messagefooter, "
                                               "LPC.acktimelimit, LPC.canceledmsg, LPC.stoppedearlymsg, DLP.programstate, "
                                               "DLP.reductiontotal, DLP.startedcontrolling, DLP.lastcontrolsent, "
                                               "DLP.manualcontrolreceivedflag, DLP.timestamp, PT.pointid, PT.pointoffset, "
                                               "PT.pointtype "
                                           "FROM lmprogramcurtailment LPC, yukonpaobject YP, lmprogram_view LPV "
                                               "LEFT OUTER JOIN dynamiclmprogram DLP ON LPV.deviceid = DLP.deviceid "
                                               "LEFT OUTER JOIN point PT ON LPV.deviceid = PT.paobjectid "
                                           "WHERE YP.paobjectid = LPC.deviceid AND LPC.deviceid = LPV.deviceid "
                                           "ORDER BY YP.paobjectid ASC";


                CtiLMProgramCurtailmentSPtr currentLMProgramCurtailment;
                DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

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
                            currentLMProgramCurtailment->restoreDynamicData();
                        }
                        //Inserting this curtailment program into hash map
                        curtailmentProgramHashMap.insert(make_pair(currentLMProgramCurtailment->getPAOId(), currentLMProgramCurtailment));
                        temp_all_program_map.insert(make_pair(currentLMProgramCurtailment->getPAOId(), currentLMProgramCurtailment));
                    }

                    if( !rdr["pointid"].isNull() )
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

                for( std::map<long, CtiLMProgramBaseSPtr>::iterator itr = curtailmentProgramHashMap.begin();
                     itr != curtailmentProgramHashMap.end(); ++itr )
                {
                    CtiLMProgramCurtailmentSPtr currentLMProgramCurtailment = boost::static_pointer_cast< CtiLMProgramCurtailment >(itr->second);

                    static const string sql =  "SELECT CIC.customerid, CIC.companyname, CIC.customerdemandlevel, "
                                                   "CIC.curtailamount, CIC.curtailmentagreement, CUS.timezone, "
                                                   "CCL.customerorder, CCL.requireack "
                                               "FROM cicustomerbase CIC, customer CUS, lmprogramcurtailcustomerlist CCL "
                                               "WHERE CIC.customerid = CCL.customerid AND "
                                                 "CUS.customerid = CIC.customerid AND CCL.programid = ? "
                                               "ORDER BY CCL.customerorder ASC";

                    vector<CtiLMCurtailCustomer*>& lmProgramCurtailmentCustomers = currentLMProgramCurtailment->getLMProgramCurtailmentCustomers();
                    Cti::Database::DatabaseReader rdr(connection, sql);

                    rdr << currentLMProgramCurtailment->getPAOId();

                    rdr.execute();

                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                    {
                        CTILOG_DEBUG(dout, rdr.asString());
                    }

                    while( rdr() )
                    {
                        lmProgramCurtailmentCustomers.push_back(CTIDBG_new CtiLMCurtailCustomer(rdr));
                    }

                    if( currentLMProgramCurtailment->getManualControlReceivedFlag() && lmProgramCurtailmentCustomers.size() > 0 )
                    {
                        for( LONG temp=0;temp<lmProgramCurtailmentCustomers.size();temp++ )
                        {
                            ((CtiLMCurtailCustomer*)lmProgramCurtailmentCustomers[temp])->restoreDynamicData();
                        }
                    }
                }
            }//loading curtailment programs end
            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CTILOG_DEBUG(dout, "DB Load Timer for Curtailment Programs is: " << componentLoadingTimer.elapsed() / 1000);
            }


            std::map<long,CtiLMProgramBaseSPtr> energyExchangeProgramHashMap;

            componentLoadingTimer.reset();
            {//loading energy exchange programs start

                static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                               "YP.description, YP.disableflag, PV.controltype, PV.constraintid, "
                                               "PV.constraintname, PV.availableweekdays, PV.maxhoursdaily, "
                                               "PV.maxhoursmonthly, PV.maxhoursseasonal, PV.maxhoursannually, "
                                               "PV.minactivatetime, PV.minrestarttime, PV.maxdailyops, "
                                               "PV.maxactivatetime, PV.holidayscheduleid, PV.seasonscheduleid, "
                                               "PEX.minnotifytime, PEX.heading, PEX.messageheader, PEX.messagefooter, "
                                               "PEX.canceledmsg, PEX.stoppedearlymsg, DLP.programstate, "
                                               "DLP.reductiontotal, DLP.startedcontrolling, DLP.lastcontrolsent, "
                                               "DLP.manualcontrolreceivedflag, DLP.timestamp, PT.pointid, "
                                               "PT.pointoffset, PT.pointtype "
                                           "FROM lmprogramenergyexchange PEX, yukonpaobject YP, lmprogram_view PV "
                                               "LEFT OUTER JOIN dynamiclmprogram DLP ON PV.deviceid = DLP.deviceid "
                                               "LEFT OUTER JOIN point PT ON PV.deviceid = PT.paobjectid "
                                           "WHERE YP.paobjectid = PEX.deviceid AND PEX.deviceid = PV.deviceid "
                                           "ORDER BY YP.paobjectid ASC";

                CtiLMProgramBaseSPtr currentLMProgramEnergyExchange;
                DatabaseReader rdr(connection);

                rdr.setCommandText(sql);
                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                while( rdr() )
                {
                    LONG tempProgramId = 0;
                    rdr["paobjectid"] >> tempProgramId;

                    if( !currentLMProgramEnergyExchange ||
                        tempProgramId != currentLMProgramEnergyExchange->getPAOId() )
                    {
                        currentLMProgramEnergyExchange.reset(CTIDBG_new CtiLMProgramEnergyExchange(rdr));
                        //Inserting this curtailment program into hash map
                        energyExchangeProgramHashMap.insert(make_pair(currentLMProgramEnergyExchange->getPAOId(), currentLMProgramEnergyExchange));
                        temp_all_program_map.insert(make_pair(currentLMProgramEnergyExchange->getPAOId(), currentLMProgramEnergyExchange));
                    }

                    if( !rdr["pointid"].isNull() )
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

                for( std::map<long,CtiLMProgramBaseSPtr>::iterator itr = energyExchangeProgramHashMap.begin();
                     itr != energyExchangeProgramHashMap.end(); ++itr )
                {
                    CtiLMProgramEnergyExchangeSPtr currentLMProgramEnergyExchange = boost::static_pointer_cast< CtiLMProgramEnergyExchange >(itr->second);

                    if( currentLMProgramEnergyExchange->getManualControlReceivedFlag() )
                    {
                        CtiTime currentDateTime;
                        CtiDate currentDate(currentDateTime);
                        CtiTime compareDateTime = CtiTime(currentDate,0,0,0);

                        static const string sql =  "SELECT EPO.deviceid, EPO.offerid, EPO.runstatus, EPO.offerdate "
                                                   "FROM lmenergyexchangeprogramoffer EPO "
                                                   "WHERE EPO.deviceid = ? AND EPO.offerdate >= ? "
                                                   "ORDER BY EPO.offerdate ASC, EPO.offerid ASC";

                        Cti::Database::DatabaseReader rdr(connection, sql);

                        rdr << currentLMProgramEnergyExchange->getPAOId()
                            << compareDateTime;

                        rdr.execute();

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CTILOG_DEBUG(dout, rdr.asString());
                        }

                        std::vector<CtiLMEnergyExchangeOffer*>& offers = currentLMProgramEnergyExchange->getLMEnergyExchangeOffers();
                        while( rdr() )
                        {
                            offers.push_back(CTIDBG_new CtiLMEnergyExchangeOffer(rdr));
                        }

                        for( LONG r=0;r<offers.size();r++ )
                        {
                            CtiLMEnergyExchangeOffer* currentLMEnergyExchangeOffer = (CtiLMEnergyExchangeOffer*)offers[r];

                            static const string sql =  "SELECT EEO.offerid, EEO.revisionnumber, EEO.actiondatetime, "
                                                           "EEO.notificationdatetime, EEO.offerexpirationdatetime, "
                                                           "EEO.additionalinfo "
                                                       "FROM lmenergyexchangeofferrevision EEO "
                                                       "WHERE EEO.offerid = ? "
                                                       "ORDER BY EEO.revisionnumber ASC";

                            Cti::Database::DatabaseReader rdr(connection, sql);

                            rdr << currentLMEnergyExchangeOffer->getOfferId();

                            rdr.execute();

                            if( _LM_DEBUG & LM_DEBUG_DATABASE )
                            {
                                CTILOG_DEBUG(dout, rdr.asString());
                            }

                            vector<CtiLMEnergyExchangeOfferRevision*>& offerRevisions = currentLMEnergyExchangeOffer->getLMEnergyExchangeOfferRevisions();
                            while( rdr() )
                            {
                                offerRevisions.push_back(CTIDBG_new CtiLMEnergyExchangeOfferRevision(rdr));
                            }

                            for( LONG s=0;s<offerRevisions.size();s++ )
                            {
                                CtiLMEnergyExchangeOfferRevision* currentLMEnergyExchangeOfferRevision = (CtiLMEnergyExchangeOfferRevision*)offerRevisions[s];

                                static const string sql =  "SELECT XHH.offerid, XHH.revisionnumber, XHH.hour, "
                                                             "XHH.price, XHH.amountrequested "
                                                           "FROM lmenergyexchangehourlyoffer XHH "
                                                           "WHERE XHH.offerid = ? AND XHH.revisionnumber = ? "
                                                           "ORDER BY XHH.hour ASC";

                                Cti::Database::DatabaseReader rdr(connection, sql);

                                rdr << currentLMEnergyExchangeOfferRevision->getOfferId()
                                    << currentLMEnergyExchangeOfferRevision->getRevisionNumber();

                                rdr.execute();

                                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                {
                                    CTILOG_DEBUG(dout, rdr.asString());
                                }

                                vector<CtiLMEnergyExchangeHourlyOffer*>& hourlyOffers = currentLMEnergyExchangeOfferRevision->getLMEnergyExchangeHourlyOffers();
                                while( rdr() )
                                {
                                    hourlyOffers.push_back(CTIDBG_new CtiLMEnergyExchangeHourlyOffer(rdr));
                                }
                            }
                        }
                    }
                    {
                        static const string sql =  "SELECT CCB.customerid, CCB.companyname, CCB.customerdemandlevel, "
                                                       "CCB.curtailamount,CCB.curtailmentagreement, CUS.timezone, "
                                                       "XCL.customerorder "
                                                   "FROM cicustomerbase CCB, customer CUS, "
                                                       "lmenergyexchangecustomerlist XCL "
                                                   "WHERE CCB.customerid = XCL.customerid AND "
                                                       "CUS.customerid = CCB.customerid AND XCL.programid = ? "
                                                   "ORDER BY XCL.customerorder ASC";

                        Cti::Database::DatabaseReader rdr(connection, sql);

                        rdr << currentLMProgramEnergyExchange->getPAOId();

                        rdr.execute();

                        if( _LM_DEBUG & LM_DEBUG_DATABASE )
                        {
                            CTILOG_DEBUG(dout, rdr.asString());
                        }

                        std::vector<CtiLMEnergyExchangeCustomer*>& lmEnergyExchangeCustomers = currentLMProgramEnergyExchange->getLMEnergyExchangeCustomers();
                        while( rdr() )
                        {
                            lmEnergyExchangeCustomers.push_back(CTIDBG_new CtiLMEnergyExchangeCustomer(rdr));
                        }

                        if( currentLMProgramEnergyExchange->getManualControlReceivedFlag() && lmEnergyExchangeCustomers.size() > 0 )
                        {
                            for( LONG a=0;a<lmEnergyExchangeCustomers.size();a++ )
                            {
                                CtiLMEnergyExchangeCustomer* currentLMEnergyExchangeCustomer = (CtiLMEnergyExchangeCustomer*)lmEnergyExchangeCustomers[a];
                                CtiTime currentDateTime;
                                CtiDate currentDate(currentDateTime);
                                CtiTime compareDateTime = CtiTime(currentDate,0,0,0);

                                static const string sql =  "SELECT CCB.customerid, CCB.offerid, CCB.acceptstatus, "
                                                               "CCB.acceptdatetime, CCB.revisionnumber, "
                                                               "CCB.ipaddressofacceptuser, CCB.useridname, "
                                                               "CCB.nameofacceptperson, CCB.energyexchangenotes "
                                                           "FROM lmenergyexchangecustomerreply CCB, "
                                                               "lmenergyexchangeprogramoffer CUS "
                                                           "WHERE CCB.offerid = CUS.offerid AND CUS.offerdate >= ? "
                                                               "AND CCB.customerid = ? "
                                                           "ORDER BY CCB.offerid ASC, CCB.revisionnumber ASC";

                                Cti::Database::DatabaseReader rdr(connection, sql);

                                rdr << compareDateTime
                                    << currentLMEnergyExchangeCustomer->getCustomerId();

                                rdr.execute();

                                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                {
                                    CTILOG_DEBUG(dout, rdr.asString());
                                }

                                vector<CtiLMEnergyExchangeCustomerReply*>& lmEnergyExchangeCustomerReplies = currentLMEnergyExchangeCustomer->getLMEnergyExchangeCustomerReplies();
                                while( rdr() )
                                {
                                    lmEnergyExchangeCustomerReplies.push_back(CTIDBG_new CtiLMEnergyExchangeCustomerReply(rdr));
                                }

                                for( LONG b=0;b<lmEnergyExchangeCustomerReplies.size();b++ )
                                {
                                    CtiLMEnergyExchangeCustomerReply* currentCustomerReply = (CtiLMEnergyExchangeCustomerReply*)lmEnergyExchangeCustomerReplies[b];
                                    CtiTime currentDateTime;
                                    CtiDate currentDate(currentDateTime);
                                    CtiTime compareDateTime = CtiTime(currentDate,0,0,0);

                                    static const string sql =  "SELECT XHC.customerid, XHC.offerid, XHC.revisionnumber, "
                                                                 "XHC.hour, XHC.amountcommitted "
                                                               "FROM lmenergyexchangehourlycustomer XHC, "
                                                                 "lmenergyexchangeprogramoffer XPO "
                                                               "WHERE XHC.customerid = ? AND XHC.offerid = ? AND "
                                                                 "XHC.offerid = XPO.offerid AND XPO.offerdate >= ? "
                                                               "ORDER BY XHC.customerid ASC, XHC.offerid ASC, "
                                                                 "XHC.revisionnumber ASC, XHC.hour ASC";

                                    Cti::Database::DatabaseReader rdr(connection, sql);

                                    rdr << currentCustomerReply->getCustomerId()
                                        << currentCustomerReply->getOfferId()
                                        << compareDateTime;

                                    rdr.execute();

                                    if( _LM_DEBUG & LM_DEBUG_DATABASE )
                                    {
                                        CTILOG_DEBUG(dout, rdr.asString());
                                    }

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
                CTILOG_DEBUG(dout, "DB Load Timer for Energy Exchange Programs is: " << componentLoadingTimer.elapsed() / 1000);
            }


            componentLoadingTimer.reset();
            {//loading program control windows start

                static const string sql =  "SELECT PCW.deviceid, PCW.windownumber, PCW.availablestarttime, "
                                             "PCW.availablestoptime "
                                           "FROM lmprogramcontrolwindow PCW ORDER BY PCW.deviceid ASC, "
                                             "PCW.windownumber ASC";

                Cti::Database::DatabaseReader rdr(connection);

                rdr.setCommandText(sql);
                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                while( rdr() )
                {
                    CtiLMProgramControlWindow* newWindow = CTIDBG_new CtiLMProgramControlWindow(rdr);

                    CtiLMProgramBaseSPtr programToPutWindowIn;

                    if ( boost::optional<CtiLMProgramBaseSPtr> lookup = Cti::mapFind( directProgramHashMap, newWindow->getPAOId() ) )
                    {
                        programToPutWindowIn = *lookup;
                    }
                    else if ( boost::optional<CtiLMProgramBaseSPtr> lookup = Cti::mapFind( curtailmentProgramHashMap, newWindow->getPAOId() ) )
                    {
                        programToPutWindowIn = *lookup;
                    }
                    else if ( boost::optional<CtiLMProgramBaseSPtr> lookup = Cti::mapFind( energyExchangeProgramHashMap, newWindow->getPAOId() ) )
                    {
                        programToPutWindowIn = *lookup;
                    }

                    if( programToPutWindowIn )
                    {
                        std::vector<CtiLMProgramControlWindow*>& lmProgramControlWindowList = programToPutWindowIn->getLMProgramControlWindows();
                        lmProgramControlWindowList.push_back(newWindow);
                    }
                    else
                    {
                        CTILOG_WARN(dout, "Control Window with Id: " << newWindow->getPAOId() << " cannot find a program to associate with");

                        delete newWindow;
                    }
                }
            }//loading program control windows end
            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CTILOG_DEBUG(dout, "DB Load Timer for Program Control Windows is: " << componentLoadingTimer.elapsed() / 1000);
            }


            componentLoadingTimer.reset();
            {//loading control areas start

                static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                               "YP.description, YP.disableflag, LCA.defoperationalstate, "
                                               "LCA.controlinterval, LCA.minresponsetime, LCA.defdailystarttime, "
                                               "LCA.defdailystoptime, LCA.requirealltriggersactiveflag, "
                                               "CAP.lmprogramdeviceid, CAP.startpriority, CAP.stoppriority, "
                                               "DCA.nextchecktime, DCA.newpointdatareceivedflag, DCA.updatedflag, "
                                               "DCA.controlareastate, DCA.currentpriority, DCA.currentdailystarttime, "
                                               "DCA.currentdailystoptime, DCA.timestamp, PT.pointid, PT.pointoffset, "
                                               "PT.pointtype "
                                           "FROM yukonpaobject YP, lmcontrolarea LCA LEFT OUTER JOIN "
                                               "lmcontrolareaprogram CAP ON LCA.deviceid = CAP.deviceid LEFT OUTER JOIN "
                                               "dynamiclmcontrolarea DCA ON LCA.deviceid = DCA.deviceid LEFT OUTER JOIN "
                                               "point PT ON LCA.deviceid = PT.paobjectid "
                                           "WHERE YP.paobjectid = LCA.deviceid "
                                           "ORDER BY YP.paobjectid ASC, CAP.startpriority ASC";

                DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

                CtiLMControlArea* currentLMControlArea = NULL;
                CtiLMProgramBaseSPtr currentLMProgramBase;
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

                    if( !rdr["lmprogramdeviceid"].isNull() )
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

                            if( boost::optional<CtiLMProgramBaseSPtr> lookup = Cti::mapFind( directProgramHashMap,tempProgramId ) )
                            {
                                currentLMProgramBase = *lookup;

                                currentLMProgramBase->setStartPriority(start_priority);
                                currentLMProgramBase->setStopPriority(stop_priority);
                                currentLMProgramBase->setControlArea(currentLMControlArea);
                                lmControlAreaProgramList.push_back(currentLMProgramBase);

                                directProgramHashMap.erase(tempProgramId);
                            }
                            else if( boost::optional<CtiLMProgramBaseSPtr> lookup = Cti::mapFind( curtailmentProgramHashMap,tempProgramId ) )
                            {
                                currentLMProgramBase = *lookup;

                                currentLMProgramBase->setStartPriority(start_priority);
                                currentLMProgramBase->setStopPriority(stop_priority);
                                currentLMProgramBase->setControlArea(currentLMControlArea);
                                lmControlAreaProgramList.push_back(currentLMProgramBase);

                                curtailmentProgramHashMap.erase(tempProgramId);
                            }
                            else if( boost::optional<CtiLMProgramBaseSPtr> lookup = Cti::mapFind( energyExchangeProgramHashMap,tempProgramId ) )
                            {
                                currentLMProgramBase = *lookup;

                                currentLMProgramBase->setStartPriority(start_priority);
                                currentLMProgramBase->setStopPriority(stop_priority);
                                currentLMProgramBase->setControlArea(currentLMControlArea);
                                lmControlAreaProgramList.push_back(currentLMProgramBase);

                                energyExchangeProgramHashMap.erase(tempProgramId);
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Cannot find LM Program with Id: " << tempProgramId << " to insert into Control Area: " << currentLMControlArea->getPAOName() << ","
                                            << endl << "The LM Program may be in more than one Control Area, which is not allowed");
                            }
                        }
                    }

                    if( !rdr["pointid"].isNull() )
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
                CTILOG_DEBUG(dout, "DB Load Timer for Control Areas is: " << componentLoadingTimer.elapsed() / 1000
                             << endl << "directprghashmap size: " << directProgramHashMap.size());
            }


            componentLoadingTimer.reset();
            {//loading control area triggers start

                static const string sql =  "SELECT CAT.ThresholdPointId, CAT.triggerid, CAT.deviceid, CAT.triggernumber, CAT.triggertype, "
                                               "CAT.pointid,CAT.normalstate, CAT.threshold, CAT.projectiontype, "
                                               "CAT.projectionpoints, CAT.projectaheadduration, CAT.thresholdkickpercent, "
                                               "CAT.minrestoreoffset, CAT.peakpointid, DTR.pointvalue, "
                                               "DTR.lastpointvaluetimestamp, DTR.peakpointvalue, "
                                               "DTR.lastpeakpointvaluetimestamp "
                                           "FROM lmcontrolareatrigger CAT LEFT OUTER JOIN "
                                               "dynamiclmcontrolareatrigger DTR ON CAT.deviceid = DTR.deviceid AND "
                                               "CAT.triggernumber = DTR.triggernumber "
                                           "ORDER BY CAT.deviceid ASC, CAT.triggernumber ASC";


                DatabaseReader rdr(connection);

                rdr.setCommandText(sql);

                rdr.execute();

                if( _LM_DEBUG & LM_DEBUG_DATABASE )
                {
                    CTILOG_DEBUG(dout, rdr.asString());
                }

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
                    for( LONG i=0;i<temp_control_areas.size();i++ )
                    {
                        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(temp_control_areas[i]);
                        if( currentLMControlArea->getPAOId() == tempControlAreaId )
                        {
                            currentLMControlArea->getLMControlAreaTriggers().push_back(newTrigger);
                            break;
                        }

                    }

                    temp_point_control_area_map.insert(make_pair(newTrigger->getPointId(), tempControlAreaId));
                    if( newTrigger->getPeakPointId() != 0 )
                    {
                        temp_point_control_area_map.insert(make_pair(newTrigger->getPeakPointId(), tempControlAreaId));
                    }
                    if( newTrigger->getThresholdPointId() != 0 )
                    {
                        temp_point_control_area_map.insert(make_pair(newTrigger->getThresholdPointId(), tempControlAreaId));
                    }
                }
            }//loading control area triggers end
            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CTILOG_DEBUG(dout, "DB Load Timer for Triggers is: " << componentLoadingTimer.elapsed() / 1000);
            }

            //Make sure holidays and season schedules are refreshed
            CtiHolidayManager::getInstance().refresh();
            CtiSeasonManager::getInstance().refresh();
        }

        {
            // Clear out our old working objects
            _all_control_area_map.clear();
            delete_container(*_controlAreas);
            _controlAreas->clear(); //89
            _all_group_map.clear();
            _all_program_map.clear();
            _point_group_map.clear();
            _point_control_area_map.clear();

            // Lets start using the new objects we just loaded.
            // do a swap, make sure we have the mux
            *_controlAreas = temp_control_areas;
            _all_group_map = temp_all_group_map;
            _all_program_map = temp_all_program_map;
            _point_group_map = temp_point_group_map;
            _point_control_area_map = temp_point_control_area_map;
            _all_control_area_map = temp_all_control_area_map;

            _CHANGED_GROUP_LIST.clear();
            _CHANGED_PROGRAM_LIST.clear();
            _CHANGED_CONTROL_AREA_LIST.clear();

            _isvalid = TRUE;

            CTILOG_INFO(dout, "Control areas reset");

            if( _LM_DEBUG & LM_DEBUG_TIMING )
            {
                CTILOG_DEBUG(dout, "DB Load Timer for entire LM DB: " << overallTimer.elapsed() / 1000);
            }
        }
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

        // Make sure all the clients get the new control areas
        CtiLMClientListener::getInstance().BroadcastMessage(CTIDBG_new CtiLMControlAreaMsg(*_controlAreas,msgBitMask));
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*---------------------------------------------------------------------------
    shutdown

    Dumps the sub bus list.
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::shutdown()
{
    dumpAllDynamicData();

    delete_container(*_controlAreas);
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
                CTILOG_INFO(dout, var << " is ZERO!!!");
            }

            if( _LM_DEBUG & LM_DEBUG_STANDARD )
            {
                CTILOG_DEBUG(dout, var << ":  " << str);
            }
        }
        else
        {
            CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
        }

        CtiTime lastPeriodicDatabaseRefresh;
        CtiTime lastCheck;

        while( TRUE )
        {
            boost::this_thread::interruption_point();

            CtiTime now;

            // When we cross midnight these dates won't match and we can do our daily midnight maintenance
            if( now.day() != lastCheck.day() )//check to see if it is midnight
            {
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Resetting midnight defaults");
                }
                checkMidnightDefaultsForReset();
            }

            if( now.seconds() >= lastPeriodicDatabaseRefresh.seconds()+refreshrate )
            {
                if( _LM_DEBUG & LM_DEBUG_STANDARD )
                {
                    CTILOG_DEBUG(dout, "Periodic restore of control area list from the database");
                }

                setValid(false);
                lastPeriodicDatabaseRefresh = CtiTime();
            }
            else
            {
                lastCheck = now;

                boost::this_thread::sleep( boost::posix_time::seconds( 5 ) );
            }
        }
    }
    catch ( boost::thread_interrupted & )
    {
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Shutting down control area store...");
    }

    if( _instance != NULL )
    {
        delete _instance;
        _instance = NULL;
    }

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Control area store shutdown.");
    }
}

/*---------------------------------------------------------------------------
    isValid

    Returns a TRUE if the strategystore was able to initialize properly
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::isValid()
{
    return _isvalid;
}

/*---------------------------------------------------------------------------
    setValid

    Sets the _isvalid flag
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::setValid(bool valid)
{
    _isvalid = valid;
}

/*---------------------------------------------------------------------------
    getReregisterForPoints

    Gets _reregisterforpoints
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::getReregisterForPoints()
{
    return _reregisterforpoints;
}

/*---------------------------------------------------------------------------
    setReregisterForPoints

    Sets _reregisterforpoints
---------------------------------------------------------------------------*/
void CtiLMControlAreaStore::setReregisterForPoints(bool reregister)
{
    _reregisterforpoints = reregister;
}

bool CtiLMControlAreaStore::getWasControlAreaDeletedFlag()
{
    return _wascontrolareadeletedflag;
}

void CtiLMControlAreaStore::setWasControlAreaDeletedFlag(bool wasDeleted)
{
    _wascontrolareadeletedflag = wasDeleted;
}

/*---------------------------------------------------------------------------
    UpdateControlAreaDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the control area.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateControlAreaDisableFlagInDB(CtiLMControlArea* controlArea)
{
    static const std::string sql = "update yukonpaobject"
                                   " set "
                                        "disableflag = ?"
                                   " where "
                                        "paobjectid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << ( controlArea->getDisableFlag() ? std::string("Y") : std::string("N") )
        << controlArea->getPAOId();

    if( ! Cti::Database::executeUpdater( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
    {
        return false;
    }

    CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(controlArea->getPAOId(), ChangePAODb,
                                                         controlArea->getPAOCategory(), controlArea->getPAOTypeString(),
                                                         ChangeTypeUpdate);
    dbChange->setSource(LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE);
    CtiLoadManager::getInstance()->sendMessageToDispatch(dbChange);

    return true; // No error occurred!
}

/*---------------------------------------------------------------------------
    UpdateProgramDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the program.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateProgramDisableFlagInDB(CtiLMProgramBaseSPtr program)
{
    static const std::string sql = "update yukonpaobject"
                                   " set "
                                        "disableflag = ?"
                                   " where "
                                        "paobjectid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << ( program->getDisableFlag() ? std::string("Y") : std::string("N") )
        << program->getPAOId();

    if( ! Cti::Database::executeUpdater( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
    {
        return false;
    }

    CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(program->getPAOId(), ChangePAODb,
                                                         program->getPAOCategory(), program->getPAOTypeString(),
                                                         ChangeTypeUpdate);
    dbChange->setSource(LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE);
    CtiLoadManager::getInstance()->sendMessageToDispatch(dbChange);

    return true; // No error occurred!
}

/*---------------------------------------------------------------------------
    UpdateGroupDisableFlagInDB

    Updates a disable flag in the yukonpaobject table in the database for
    the group.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateGroupDisableFlagInDB(CtiLMGroupPtr& group)
{
    static const std::string sql = "update yukonpaobject"
                                   " set "
                                        "disableflag = ?"
                                   " where "
                                        "paobjectid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << ( group->getDisableFlag() ? std::string("Y") : std::string("N") )
        << group->getPAOId();

    if( ! Cti::Database::executeUpdater( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
    {
        return false;
    }

    CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(group->getPAOId(), ChangePAODb,
                                                         group->getPAOCategory(), group->getPAOTypeString(),
                                                         ChangeTypeUpdate);
    dbChange->setSource(LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE);
    CtiLoadManager::getInstance()->sendMessageToDispatch(dbChange);

    return true;
}

/*---------------------------------------------------------------------------
    UpdateTriggerInDB

    Updates a trigger threshold, restore offset, etc. in the database either
    by TDC user or ATKU.
---------------------------------------------------------------------------*/
bool CtiLMControlAreaStore::UpdateTriggerInDB(CtiLMControlArea* controlArea, CtiLMControlAreaTrigger* trigger)
{
    static const std::string sql = "update lmcontrolareatrigger"
                                   " set "
                                        "threshold = ?, "
                                        "minrestoreoffset = ?"
                                   " where "
                                        "deviceid = ? and "
                                        "triggernumber = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << trigger->getThreshold()
        << trigger->getMinRestoreOffset()
        << trigger->getPAOId()
        << trigger->getTriggerNumber();

    if( ! Cti::Database::executeUpdater( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
    {
        return false;
    }

    CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(controlArea->getPAOId(), ChangePAODb,
                                                         controlArea->getPAOCategory(), controlArea->getPAOTypeString(),
                                                         ChangeTypeUpdate);
    dbChange->setSource(LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE);
    CtiLoadManager::getInstance()->sendMessageToDispatch(dbChange);

    return true;
}

/*---------------------------------------------------------------------------
    getLMGroup

    Returns the group based on the ID, returns NULL if the group is not
    in the list.
---------------------------------------------------------------------------*/
CtiLMGroupPtr CtiLMControlAreaStore::getLMGroup(long groupID)
{
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
    bool returnBool = false;
    vector<CtiLMControlArea*>& controlAreas = *getControlAreas(CtiTime());
    for( long i=0;i<controlAreas.size();i++ )
    {
        CtiLMControlArea* currentControlArea = (CtiLMControlArea*)controlAreas[i];

        if( !ciStringEqual(currentControlArea->getDefOperationalState(),CtiLMControlArea::DefOpStateNone ) )//check default operational state
        {
            if( ( ciStringEqual(currentControlArea->getDefOperationalState(),CtiLMControlArea::DefOpStateEnabled ) &&
                  currentControlArea->getDisableFlag() ) ||
                ( ciStringEqual(currentControlArea->getDefOperationalState(),CtiLMControlArea::DefOpStateDisabled ) &&
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
                    CTILOG_INFO(dout, text << ", " << additional);
                }
                currentControlArea->setDisableFlag(!currentControlArea->getDisableFlag());
                UpdateControlAreaDisableFlagInDB(currentControlArea);
                returnBool = true;
            }
        }
        if( currentControlArea->getCurrentDailyStartTime() != currentControlArea->getDefDailyStartTime() ||
            currentControlArea->getCurrentDailyStopTime() != currentControlArea->getDefDailyStopTime() )//check default daily start and stop times
        {
            if( currentControlArea->getCurrentDailyStartTime() != currentControlArea->getDefDailyStartTime() )
            {
                char tempchar[80] = "";
                string text = ("Automatic Daily Start Time Update");
                string additional = (" Current Daily Start Time not set to Default Start Time.  Control Area: ");
                additional += currentControlArea->getPAOName();
                additional += " Daily Start Time was automatically set back to default of ";
                LONG defStartTimeHours = currentControlArea->getDefDailyStartTime().hour();
                LONG defStartTimeMinutes = currentControlArea->getDefDailyStartTime().minute();
                _snprintf(tempchar,80,"%d",defStartTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",defStartTimeMinutes);
                additional += tempchar;
                additional += " from ";
                LONG currentStartTimeHours = currentControlArea->getCurrentDailyStartTime().hour();
                LONG currentStartTimeMinutes = currentControlArea->getCurrentDailyStartTime().minute();
                _snprintf(tempchar,80,"%d",currentStartTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",currentStartTimeMinutes);
                additional += tempchar;
                additional += ".";
                CtiLoadManager::getInstance()->sendMessageToDispatch(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                CTILOG_INFO(dout, text << ", " << additional);
            }
            if( currentControlArea->getCurrentDailyStopTime() != currentControlArea->getDefDailyStopTime() )
            {
                char tempchar[80] = "";
                string text = ("Automatic Daily Stop Time Update");
                string additional = (" Current Daily Stop Time not set to Default Stop Time.  Control Area: ");
                additional += currentControlArea->getPAOName();
                additional += " Daily Stop Time was automatically set back to default of ";
                LONG defStopTimeHours = currentControlArea->getDefDailyStopTime().hour();
                LONG defStopTimeMinutes = currentControlArea->getDefDailyStopTime().minute();
                _snprintf(tempchar,80,"%d",defStopTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",defStopTimeMinutes);
                additional += tempchar;
                additional += " from ";
                LONG currentStopTimeHours = currentControlArea->getCurrentDailyStopTime().hour();
                LONG currentStopTimeMinutes = currentControlArea->getCurrentDailyStopTime().minute();
                _snprintf(tempchar,80,"%d",currentStopTimeHours);
                additional += tempchar;
                additional += ":";
                _snprintf(tempchar,80,"%d",currentStopTimeMinutes);
                additional += tempchar;
                additional += ".";
                CtiLoadManager::getInstance()->sendMessageToDispatch(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,0,text,additional,GeneralLogType,SignalEvent));
                CTILOG_INFO(dout, text << ", " << additional);
            }
            currentControlArea->resetCurrentDailyStartTime();
            currentControlArea->resetCurrentDailyStopTime();
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

    for( LONG i=0;i<_controlAreas->size();i++ )
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];

        vector<CtiLMControlAreaTrigger*>& lmControlAreaTriggers = currentLMControlArea->getLMControlAreaTriggers();
        if( lmControlAreaTriggers.size() > 0 )
        {
            for( LONG j=0;j<lmControlAreaTriggers.size();j++ )
            {
                CtiLMControlAreaTrigger* currentLMControlAreaTrigger = (CtiLMControlAreaTrigger*)lmControlAreaTriggers.at(j);
                if( ciStringEqual(currentLMControlAreaTrigger->getProjectionType(),CtiLMControlAreaTrigger::LSFProjectionType ) &&
                     !ciStringEqual(currentLMControlAreaTrigger->getTriggerType(),CtiLMControlAreaTrigger::StatusTriggerType ) )   // LSF Projection not available on Status triggers
                {
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
    for( LONG i=0;i<_projectionQueues.size();i++ )
    {
        CtiLMSavedProjectionQueue currentSavedQueue =  _projectionQueues[i];
        if( trigger->getPointId() == currentSavedQueue.getPointId() )
        {
            std::vector<CtiLMProjectionPointEntry> queueToCopyFrom = currentSavedQueue.getProjectionEntryList();
            std::vector<CtiLMProjectionPointEntry>& queueToFillUp = trigger->getProjectionPointEntriesQueue();
            std::vector<CtiLMProjectionPointEntry>::iterator itr = queueToCopyFrom.begin();
            for( ; itr != queueToCopyFrom.end(); ++itr )
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

    for( LONG i=0;i<_controlAreas->size();i++ )
    {
        CtiLMControlArea* currentLMControlArea = (CtiLMControlArea*)(*_controlAreas)[i];

        vector<CtiLMProgramBaseSPtr>& lmPrograms = currentLMControlArea->getLMPrograms();
        if( lmPrograms.size() > 0 )
        {
            for( LONG j=0;j<lmPrograms.size();j++ )
            {
                CtiLMProgramBaseSPtr currentLMProgram = (CtiLMProgramBaseSPtr)lmPrograms[j];
                if( currentLMProgram->getPAOType() == TYPE_LMPROGRAM_DIRECT )
                {
                    CtiLMGroupVec groups  = boost::static_pointer_cast< CtiLMProgramDirect >(currentLMProgram)->getLMProgramDirectGroups();
                    for( CtiLMGroupIter k = groups.begin(); k != groups.end(); k++ )
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
    for( LONG i=0;i<_controlStrings.size();i++ )
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

