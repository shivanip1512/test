#include "precompiled.h"

#include "mc_server.h"
#include "numstr.h"
#include "thread_monitor.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_reg.h"
#include "connection.h"

#include <time.h>
#include <algorithm>
#include <utility.h>

#include "tbl_devicereadjoblog.h"
#include "boost/scoped_array.hpp"

using namespace std;

/* The global debug level stored here, defined in mc.h */
unsigned gMacsDebugLevel = 0x00000000;


CtiMCServer::CtiMCServer()
: _debug(false),
  _client_listener(MC_PORT),
  _db_update_thread(_schedule_manager),
  _scheduler(_schedule_manager),
  _file_interface(_schedule_manager),
  _interp_pool(createMacsCommandSet())
{
}

CtiMCServer::~CtiMCServer()
{
}

set<string> CtiMCServer::createMacsCommandSet() 
{
    std::set<string> macsCommands, upperCommands, lowerCommands;

    // Grab all of the command strings from the tcl maps in mccmd.
    std::transform(
        tclCamelCommandMap.begin(),
        tclCamelCommandMap.end(),
        inserter(macsCommands, macsCommands.begin()),
        bind(&TclCommandMap::value_type::first, _1));

    for each(const string &str in macsCommands)
    {   
        string upperStr(str); // initialize to get the correct size.
        std::transform(str.begin(), str.end(), upperStr.begin(), ::toupper);
        upperCommands.insert(upperStr);
    }

    for each(const string &str in macsCommands)
    {   
        string lowerStr(str); // initialize to get the correct size.
        std::transform(str.begin(), str.end(), lowerStr.begin(), ::tolower);
        lowerCommands.insert(lowerStr);
    }

    macsCommands.insert(upperCommands.begin(), upperCommands.end());

    macsCommands.insert(lowerCommands.begin(), lowerCommands.end());

    std::transform(
        tclNonCamelCommandMap.begin(),
        tclNonCamelCommandMap.end(),
        inserter(macsCommands, macsCommands.begin()),
        bind(&TclCommandMap::value_type::first, _1));

    return macsCommands;
}

void CtiMCServer::run()
{
    CtiMessage* msg = NULL;
    unsigned long delay = 0; // how long to wait to wait up again max, in seconds

    try
    {
        CtiConnection VanGoghConnection;
        string dispatch_host = gConfigParms.getValueAsString("DISPATCH_MACHINE", "127.0.0.1");
        int    dispatch_port = gConfigParms.getValueAsInt("DISPATCH_PORT", VANGOGHNEXUS);

        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << " Connecting to dispatch, host: " << dispatch_host << ", port: " << dispatch_port << endl;
        }

        VanGoghConnection.doConnect(dispatch_port, dispatch_host);
        VanGoghConnection.setName("MACServer to Dispatch");

        //Send a registration message
        CtiRegistrationMsg* regMsg = new CtiRegistrationMsg("MACServer", 0, false );
        VanGoghConnection.WriteConnQue( regMsg );

        if( init() )
        {
            const long threadMonitorPointId = ThreadMonitor.getPointIDFromOffset(CtiThreadMonitor::Macs);
            CtiTime LastThreadMonitorTime, NextThreadMonitorReportTime;
            CtiThreadMonitor::State previous = CtiThreadMonitor::Normal;

            /* Main Loop */
            while(true)
            {
                // Workaround for bug in vc6 debug heap
                ResetBreakAlloc();

                // Do thread monitor stuff
                if( threadMonitorPointId != 0 )
                {
                    if( (LastThreadMonitorTime.now().seconds() - LastThreadMonitorTime.seconds()) >= 2 )
                    {
                        CtiThreadMonitor::State next;
                        LastThreadMonitorTime = LastThreadMonitorTime.now();
                        if( (next = ThreadMonitor.getState()) != previous || LastThreadMonitorTime > NextThreadMonitorReportTime )
                        {
                            previous = next;
                            NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( LastThreadMonitorTime, CtiThreadMonitor::StandardMonitorTime / 2 );

                            VanGoghConnection.WriteConnQue(CTIDBG_new CtiPointDataMsg(threadMonitorPointId, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString().c_str()));
                        }
                    }
                }
                // end thread monitor stuff

                if( (msg =_main_queue.getQueue(delay*1000)) != NULL )
                {
                    if( isSet( CtiThread::SHUTDOWN) )
                    {
                        delete msg;
                        break;
                    }

                    processMessage(msg);

                    // go back to the top to empty out the queue
                    delete msg;

                    delay = secondsToNextMinute();
                    CtiTime nextTime = _scheduler.getNextEventTime();
                    if( nextTime.isValid() ) {
                        unsigned long next = secondsToTime(nextTime);
                        if( next < delay ) delay = next;
                    }

                    continue;
                }

                while( msg = VanGoghConnection.ReadConnQue(0) )
                {
                    if( msg->isA() == MSG_COMMAND && ((CtiCommandMsg*)msg)->getOperation() == CtiCommandMsg::AreYouThere )
                    {
                        VanGoghConnection.WriteConnQue(msg->replicateMessage());

                        if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - MCServer Replied to Are You There message." << endl;
                        }
                    }

                    delete msg;
                    msg = NULL;
                }

                // Release any outstanding interpreters we can
                releaseInterpreters();

                if( gMacsDebugLevel & MC_DEBUG_EVENTS )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << CtiTime() << " Checking event queue" << endl;
                }

                if( gMacsDebugLevel & MC_DEBUG_EVENTS )
                    _scheduler.dumpEventQueue();

                // Check to see if the next event is ready to go.

                _scheduler.getEvents( CtiTime::now(), work_around );

                for( work_around_iter = work_around.begin();
                     work_around_iter != work_around.end();
                     work_around_iter++ )
                {
                    processEvent(*work_around_iter);
                }

                work_around.clear();


            // purge any dead connections
            _client_listener.checkConnections();

            // force stop any schedules with completed scripts
            checkRunningScripts();

            delay = secondsToNextMinute();
            CtiTime nextTime = _scheduler.getNextEventTime();
            if( nextTime.isValid() ) {
                long next = secondsToTime(nextTime);
                if( next < delay ) delay = next;
             }

            if( gMacsDebugLevel & MC_DEBUG_EVENTS )
            {
            CtiLockGuard<CtiLogger> guard(dout);
                _scheduler.dumpEventQueue();
                dout << CtiTime() << " Sleeping for " << delay << " millis" << endl;
            }
         }
            /* End of Main Loop */
        }
        else
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " An error occured during initialization" << endl;
        }


        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " - " << "Shutting down MACServer connection to VanGogh" << endl;
        }

        VanGoghConnection.ShutdownConnection();
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " *PANIC* MC MAIN THREAD CAUGHT AN EXCEPTION OF UNKNOWN TYPE!" << endl;
        }
    }

    /* We're done lets close down the server */
    {
      CtiLockGuard<CtiLogger> guard(dout);
      dout << CtiTime() << " Metering and Control shutting down" << endl;
    }

    deinit();

    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << CtiTime() << " Metering and Control exiting" << endl;
    }
}


void CtiMCServer::interrupt(int id)
{
    CtiThread::interrupt( id );
    _main_queue.putQueue( new CtiMessage(1) ); //wake up anyone blocking on a read
}

/*----------------------------------------------------------------------------
  logEvent

  Logs an event with dispatch.
  Uses a tcl interpreter to do the logging.

  user - the user who caused whatever that needs to be logged
  text - short description of what happened
----------------------------------------------------------------------------*/
void CtiMCServer::logEvent(const string& user, const string& text) const
{
    // build up the logevent tcl command
    // make sure to embed " in case there are spaces in
    // user or text
    string cmd_string("LogEvent \"");
    cmd_string.append(user);
    cmd_string.append("\" \"");
    cmd_string.append(text);
    cmd_string.append("\" \" \""); //last arg is blank space

    if( gMacsDebugLevel & MC_DEBUG_EVENTS )
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << cmd_string << endl;
    }

    // Acquire an interpreter and send out the command
    CtiInterpreter* interp = _interp_pool.acquireInterpreter();
    interp->evaluateRaw( cmd_string, true ); //block
    _interp_pool.releaseInterpreter(interp);
}


/*----------------------------------------------------------------------------
  executeCommand

  Executes the start command for the given schedule in a tcl interpreter
  synchronously
----------------------------------------------------------------------------*/
void CtiMCServer::executeCommand(const string& command, long target)
{
    const char* selectPrefix = "Select deviceid ";
    string to_send;

    if( command.length() == 0 )
        return;

    if( target > 0 )
    {
        to_send.append(selectPrefix)
               .append(CtiNumStr(target))
               .append("\r\n");
    }

    to_send.append(command);

    if( gMacsDebugLevel & MC_DEBUG_INTERP )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()
             << " Sending command to tcl for eval:  " << to_send << endl;
    }

    // Acquire an interpreter and send out the command
    CtiInterpreter* interp = _interp_pool.acquireInterpreter();

    // reset mccmd for this command
    interp->evaluate("MCCMDReset");

    interp->evaluate( to_send, false ); //no blocking

    _executing_commands.push_back(interp);
}

void CtiMCServer::setDebug(bool val)
{
    _debug = val;
}


bool CtiMCServer::init()
{
    bool status = false;

    try
    {
        /* Start Initialization */
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " Metering and Control starting up..." << endl;
        }

        // load up the database and start the db update thread
        // do not proceed until we have loaded the db successfully
        // but do respect interruptions

        while( !(status = loadDB()) )
        {
            {
                CtiLockGuard<CtiLogger> guard(dout);
                dout << CtiTime() << " An error occured retrieving accessing the database, it may not be initialized.  Retry in 15 seconds." << endl;
            }

            if( sleep(15000) )
                break;
        }

        if( !loadCParms() )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " At least one cparm not found in master.cfg" << endl;
        }

        if( status )
        {
            CtiLockGuard<CtiMutex> map_guard(_schedule_manager.getMux() );
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Loaded " << _schedule_manager.getMap().size() << " schedules from the database." << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " An error occured retrieving schedules from the database." << endl;
            status = false;
        }

        /* Set up our events */
        CtiTime now = stripSeconds(CtiTime::now());
        _scheduler.initEvents(now);

        _db_update_thread.start();

        /* start up the client listener */
        _client_listener.setQueue(&_main_queue);
        _client_listener.start();

        /* start up the file interface */
        _file_interface.setQueue(&_main_queue);
        _file_interface.start();


        /* load commands into the interpreter &
            init connections */
        _interp_pool.evalOnInit("load mccmd");

        /* start up connections to other services */
        CtiInterpreter* interp = _interp_pool.acquireInterpreter();
        interp->evaluate("pilstartup", true );
        _interp_pool.releaseInterpreter(interp);
    }
    catch(...)
    {
        status = false;
    }

    return status;
}

bool CtiMCServer::deinit()
{
    if( gMacsDebugLevel & MC_DEBUG_SHUTDOWN )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " Stopping MACS file interface" << endl;
    }

    /* stop the file interface */
    _file_interface.stop();

    if( gMacsDebugLevel & MC_DEBUG_SHUTDOWN )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " Stopping MACS client listener" << endl;
    }

    /* stop accepting connections */
    _client_listener.interrupt( CtiThread::SHUTDOWN );
    _client_listener.join();

    if( gMacsDebugLevel & MC_DEBUG_SHUTDOWN )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " Stopping MACS tcl interpreter pool" << endl;
    }

    CtiInterpreter* interp = _interp_pool.acquireInterpreter();
    interp->evaluate("pilshutdown", true);
    _interp_pool.releaseInterpreter(interp);

    _interp_pool.stopAndDestroyAllInterpreters();

    if( gMacsDebugLevel & MC_DEBUG_SHUTDOWN )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " Stopping MACS database update thread" << endl;
    }

    _db_update_thread.interrupt( CtiThread::SHUTDOWN );
    _db_update_thread.join();

    return true;
}

 /*----------------------------------------------------------------------------
   executeScript

   Executes a script type schedules script.
   Inserts the schedule's id and a pointer to the interpreter
   into _running_scripts to keep track of it
 ----------------------------------------------------------------------------*/
void CtiMCServer::executeScript(const CtiMCSchedule& sched)
{
    if( gMacsDebugLevel & MC_DEBUG_INTERP )
    {
        CtiLockGuard< CtiLogger > guard(dout);

        dumpRunningScripts();

        dout << CtiTime() << " Dumping interpreter pool before executing a new script" << endl;
        _interp_pool.dumpPool();


    }

    CtiMCScript script;
    script.setScriptName( sched.getCommandFile() );

    if( script.readContents() )
    {
        //Acquire an interpreter to use
        CtiInterpreter* interp = _interp_pool.acquireInterpreter();

        // reset mccmd for this script
        interp->evaluate("MCCMDReset");

        // init the correct schedule id and holiday schedule id
        string init_id("set ScheduleID ");
        init_id += CtiNumStr( sched.getScheduleID() );

        interp->evaluate(init_id, true);

        init_id = "set HolidayScheduleID ";
        init_id += CtiNumStr( sched.getHolidayScheduleID() );

        interp->evaluate(init_id, true);

        // (re)set some variables
        interp->evaluate("set ScheduleName \"" + sched.getScheduleName() + "\"");
        interp->evaluate("set ScriptName \"" + script.getScriptName() + "\"");

        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " [" << interp->getID() << "] " << script.getScriptName() << endl;
        }

        interp->setScheduleId(sched.getScheduleID());

        // start the evaluation, non-blocking
        interp->evaluate( script.getContents(), false, CtiMCServer::preScriptFunction, CtiMCServer::postScriptFunction );

        _running_scripts.insert(
        map< long, CtiInterpreter* >::value_type(sched.getScheduleID(), interp ) );
    }
    else
    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << CtiTime() << " Failed to load script:  "
             << script.getScriptName() << endl;
    }

    if( gMacsDebugLevel & MC_DEBUG_INTERP )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dumpRunningScripts();

        dout << CtiTime() << " Dumping interpreter pool after executing a new script" << endl;
        _interp_pool.dumpPool();
    }
}

void CtiMCServer::preScriptFunction(CtiInterpreter *interp)
{
    Tcl_Interp *tclInterpreter = NULL;

    if( interp != NULL && (tclInterpreter = interp->getTclInterpreter()) != NULL )
    {
        ULONG logId = SynchronizedIdGen("DeviceReadJobLog", 1);

        if ( logId != 0 )
        {
            Tcl_SetVar(tclInterpreter, "DeviceReadLogId", (char *)CtiNumStr(logId).toString().c_str(), 0);

            CtiTblDeviceReadJobLog jobLogTable(logId, interp->getScheduleId() , CtiTime::now(), CtiTime::now());
            jobLogTable.Insert();
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }
    }
}

void CtiMCServer::postScriptFunction(CtiInterpreter *interp)
{
    UINT jobId = 0;
    Tcl_Interp *tclInterpreter = NULL;

    if( interp != NULL && (tclInterpreter = interp->getTclInterpreter()) != NULL )
    {
        char* jobIdStr = Tcl_GetVar(tclInterpreter, "DeviceReadLogId", 0 );
        if( jobIdStr != NULL )
        {
            jobId = atoi(jobIdStr);
            Tcl_UnsetVar(tclInterpreter, "DeviceReadLogId", 0 );
        }

        if( jobId != 0 )
        {
            CtiTblDeviceReadJobLog jobLogTable(jobId);
            jobLogTable.setStopTime(CtiTime::now());
            jobLogTable.UpdateStopTime();
        }
    }


}

 /*----------------------------------------------------------------------------
   stopScript

   Stops evaluation of a schedule's script.
   Removes the schedule, interpreter from _running_scripts
 ----------------------------------------------------------------------------*/
void CtiMCServer::stopScript(long sched_id)
{
    if( gMacsDebugLevel & MC_DEBUG_INTERP )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dumpRunningScripts();

        dout << CtiTime() << " Dumping interpreter before stopping script" << endl;
        _interp_pool.dumpPool();
    }

    // find the schedule and reset its manual start + stop times
    {
        CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );
        CtiMCSchedule* sched = _schedule_manager.findSchedule( sched_id );
        sched->setManualStartTime( CtiTime( (unsigned long) 0 ));
        sched->setManualStopTime( CtiTime( (unsigned long) 0 ));
        _schedule_manager.updateSchedule( *sched );
    }


    // find the entry for the schedule and the interpreter
    map< long, CtiInterpreter* >::iterator iter =
            _running_scripts.find( sched_id );

    if( iter != _running_scripts.end() )
    {
        CtiInterpreter* interp = iter->second;

        if( interp != NULL )
        {
            interp->stopEval();
            _interp_pool.releaseInterpreter(interp);
        }

        _running_scripts.erase(iter);
    }



    if( gMacsDebugLevel & MC_DEBUG_INTERP )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dumpRunningScripts();

        dout << CtiTime() << " Dumping interpreter after stopping script" << endl;
        _interp_pool.dumpPool();
    }

}

/*
    Releases any interpreters that were evaluating simple commands but have
    finished
*/
void CtiMCServer::releaseInterpreters()
{
    deque< CtiInterpreter* >::iterator iter = _executing_commands.begin();
    while( iter != _executing_commands.end() )
    {
        CtiInterpreter* interp = *iter;
        if( !interp->isEvaluating() )
        {
            interp->stopEval();
            _interp_pool.releaseInterpreter(interp);
            iter = _executing_commands.erase(iter);
        }
        else
        {
            iter++;
        }
    }
}

 /*----------------------------------------------------------------------------
   checkRunningScripts

   Goes through the running scripts list to see if they are actually
   executing.  If they aren't, the schedule will be forced stopped.
 ----------------------------------------------------------------------------*/
void CtiMCServer::checkRunningScripts()
{
    CtiTime now( stripSeconds(CtiTime::now()) );

    map< long, CtiInterpreter* >::iterator iter;
    for( iter = _running_scripts.begin();
         iter != _running_scripts.end();
         iter++ )
    {
        CtiInterpreter* interp = iter->second;

        if( interp != NULL && !interp->isEvaluating() )
        {
            // interpreter isn't evaluating, try to find the
            // schedule and force it to stop
            CtiMCSchedule* sched = NULL;

            {
                CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );
                sched = _schedule_manager.findSchedule( iter->first );
            }

            if( sched != NULL )
            {
                sched->setManualStopTime( now );
                _scheduler.scheduleManualStop( now, *sched);
            }
        }
    }
}

bool CtiMCServer::processMessage(CtiMessage* msg)
{
    CtiMCInfo* errorMsg = NULL;
    bool ret_val = false;
    long id;

    if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << CtiTime() << " Processing Message:  " <<  endl;
        msg->dump();
    }

   switch( msg->isA() )
    {
        case MSG_MC_ADD_SCHEDULE:
        {
            if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
            {
                CtiLockGuard<CtiLogger> guard(dout);
                dout << CtiTime() << " Received AddSchedule message" << endl;
            }

            CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );

            CtiMCAddSchedule* add_msg = (CtiMCAddSchedule*) msg;

            if( !add_msg->getSchedule().isSimpleSchedule() )
            {
                CtiMCScript script_to_write;
                script_to_write.setScriptName( add_msg->getSchedule().getCommandFile() );
                script_to_write.setContents( add_msg->getScript() );

                if( !script_to_write.writeContents() )
                {
                    errorMsg = new CtiMCInfo();
                    errorMsg->setInfo("An error occured writing a script file.");
                    break;
                }
            }

            CtiMCSchedule* new_sched = NULL;
            if( (new_sched = _schedule_manager.addSchedule( add_msg->getSchedule() )) != NULL )
            {
                 _db_update_thread.forceImmediateUpdate();
                 _scheduler.initEvents( stripSeconds( CtiTime::now()), *new_sched );

                 _client_listener.BroadcastMessage( new_sched->replicateMessage() );

                 string event_text("Created Schedule:  \\\"");
                 event_text += new_sched->getScheduleName();
                 event_text += "\\\"";

                 logEvent( add_msg->getUser(), event_text );
                 sleep(2500); // CGP Let the writer thread have a chance.  Yes this is bad, but we are lazy.
                 sendDBChange( new_sched->getScheduleID(), add_msg->getUser() );

                 ret_val = true;
            }
            else
            {
                errorMsg = new CtiMCInfo();
                errorMsg->setInfo("An error occured adding a new schedule");
            }
        }

        break;

        case MSG_MC_UPDATE_SCHEDULE:
            {
                if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                {
                    CtiLockGuard<CtiLogger> guard(dout);
                    dout << CtiTime() << " Received UpdateSchedule message" << endl;
                }

                CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );

                CtiMCUpdateSchedule* update_msg = (CtiMCUpdateSchedule*) msg;

                if( _schedule_manager.updateSchedule( update_msg->getSchedule() ) )
                {
                    CtiMCSchedule* updated_sched = _schedule_manager.findSchedule( update_msg->getSchedule().getScheduleID() );

                    if( updated_sched->getCurrentState() == CtiMCSchedule::Running )
                    {
                        errorMsg = new CtiMCInfo();
                        errorMsg->setInfo("Cannot update a running schedule, stop or disable it first");
                        break;
                    }

                     if( !update_msg->getSchedule().isSimpleSchedule() )
                     {
                         CtiMCScript script_to_write;
                         script_to_write.setScriptName( update_msg->getSchedule().getCommandFile() );
                         script_to_write.setContents( update_msg->getScript() );

                         if( !script_to_write.writeContents() )
                         {
                             errorMsg = new CtiMCInfo();
                             errorMsg->setInfo("An error occured writing a script file.");
                             break;
                         }
                     }

                    _scheduler.removeEvents(updated_sched->getScheduleID());
                    _scheduler.initEvents( stripSeconds( CtiTime::now()), *updated_sched );

                    _client_listener.BroadcastMessage( updated_sched->replicateMessage() );

                    string event_text("Updated Schedule: \\\"");
                    event_text += updated_sched->getScheduleName();
                    event_text += "\\\"";

                    logEvent( string(update_msg->getUser().data()), event_text );

                    ret_val = true;
                }
                else
                {
                    errorMsg = new CtiMCInfo();
                    errorMsg->setInfo("An error occured updating a schedule");
                }

            }

        break;

        case MSG_MC_RETRIEVE_SCHEDULE:

            if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
            {
                CtiLockGuard<CtiLogger> guard(dout);
                dout << CtiTime() << " Received Retrieve Schedule message" << endl;
            }
            id = ((CtiMCRetrieveSchedule*) msg)->getScheduleID();

            if( id == CtiMCRetrieveSchedule::AllSchedules )
            {
                //send all the schedules to the client that requested them
                CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );

                CtiMCScheduleManager::MapIterator itr = _schedule_manager.getMap().begin();
                CtiMultiMsg* multi = new CtiMultiMsg();

                CtiMCSchedule* sched;
                for(;itr != _schedule_manager.getMap().end();++itr)
                {
                    sched = (*itr).second;

                    if( sched != NULL )
                    {
                        multi->insert(sched->replicateMessage());
                    }
                }

                // This goes out to all clients
                _client_listener.BroadcastMessage(multi, msg->getConnectionHandle());
            }
            else
            {
                CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );
                CtiMCSchedule* sched = _schedule_manager.findSchedule(id);

                if( sched != NULL )
                {
                    _client_listener.BroadcastMessage( sched->replicateMessage(), msg->getConnectionHandle() );
                }
            }
            break;

            case MSG_MC_DELETE_SCHEDULE:
            {
                bool deleted = false;

                if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                {
                    CtiLockGuard<CtiLogger> guard(dout);
                    dout << CtiTime() << " Received Delete Schedule message" << endl;
                }

                CtiMCDeleteSchedule* delete_msg = (CtiMCDeleteSchedule*) msg;
                id = delete_msg->getScheduleID();

                // Find out the schedule name before we delete it
                // so we can log it's name
                string sched_name;

                {
                    CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );

                    CtiMCSchedule* sched = _schedule_manager.findSchedule(id);

                    if( sched != NULL )
                    {
                        sched_name = sched->getScheduleName();
                    }

                    if( _schedule_manager.deleteSchedule( id ) )
                    {
                        // there can be no more events for this schedule
                        _scheduler.removeEvents( id );
                        _client_listener.BroadcastMessage( delete_msg->replicateMessage() );

                        string event_text("Deleted Schedule:  \\\"");
                        event_text += sched_name;
                        event_text += "\\\"";

                        logEvent( string(delete_msg->getUser().data()), event_text );

                        ret_val = deleted = true;
                    }
                }

                if( !deleted)
                {
                    errorMsg = new CtiMCInfo();
                    errorMsg->setInfo("An error occured deleting schedule");
                }
            }
            break;
           case MSG_MC_RETRIEVE_SCRIPT:
           {
               if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
               {
                   CtiLockGuard<CtiLogger> guard(dout);
                   dout << CtiTime() << " Received Retrieve Script message" << endl;
               }

               CtiMCRetrieveScript* retrieve_msg = (CtiMCRetrieveScript*) msg;

               CtiMCScript script;
               script.setScriptName( retrieve_msg->getScriptName() );

               if( !script.readContents() )
               {
                   errorMsg = new CtiMCInfo();
                   errorMsg->setInfo("An error occured reading the script");
               }

               _client_listener.BroadcastMessage( script.replicateMessage(), msg->getConnectionHandle() );
           }
            break;

            case MSG_MC_OVERRIDE_REQUEST:
            {
                if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                {
                    CtiLockGuard<CtiLogger> guard(dout);
                    dout << CtiTime() << " Received Override Request message" << endl;
                }

                CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );

                CtiMCOverrideRequest* request_msg = (CtiMCOverrideRequest*) msg;
                CtiMCSchedule* sched = _schedule_manager.findSchedule( request_msg->getID() );

                string event_text;
                CtiTime real_time( (unsigned long) 0);

                if( sched != NULL )
                {
                    switch( request_msg->getAction() )
                    {
                        case CtiMCOverrideRequest::Start:

                        if( !request_msg->getStartTime().isValid() )
                        {
                            real_time = stripSeconds( CtiTime::now() );
                            sched->setManualStartTime(real_time);
                        }
                        else
                        {
                            real_time = stripSeconds(request_msg->getStartTime());
                            sched->setManualStartTime(real_time);
                        }

                        _scheduler.scheduleManualStart( stripSeconds(CtiTime::now()), *sched);

                        event_text = "Start Schedule: \\\"";
                        event_text += sched->getScheduleName();
                        event_text += "\\\" @ ";
                        event_text += real_time.asString().c_str();

                        break;

                        case CtiMCOverrideRequest::StartNow:

                        real_time = stripSeconds(CtiTime::now());
                        sched->setManualStartTime(real_time);

                        _scheduler.scheduleManualStart( stripSeconds(CtiTime::now()), *sched);

                        event_text = "Start Schedule: \\\"";
                        event_text += sched->getScheduleName();
                        event_text += "\\\" @ ";
                        event_text += real_time.asString().c_str();

                        break;


                        case CtiMCOverrideRequest::Stop:

                        if( !request_msg->getStopTime().isValid() )
                        {
                            real_time = stripSeconds( CtiTime::now() );
                            sched->setManualStopTime(real_time);
                        }
                        else
                        {
                            real_time = request_msg->getStopTime();
                            sched->setManualStopTime(real_time);
                        }

                        _scheduler.scheduleManualStop( stripSeconds(CtiTime::now()), *sched);

                        event_text = "Stop Schedule: \\\"";
                        event_text += sched->getScheduleName();
                        event_text += "\\\" @ ";
                        event_text += real_time.asString().c_str();

                        break;

                        case CtiMCOverrideRequest::StopNow:

                        real_time = stripSeconds(CtiTime::now());
                        sched->setManualStopTime(real_time);

                        _scheduler.scheduleManualStop( stripSeconds(CtiTime::now()), *sched);

                        event_text = "Stop Schedule: \\\"";
                        event_text += sched->getScheduleName();
                        event_text += "\\\" @ ";
                        event_text += real_time.asString().c_str();

                        break;

                        case CtiMCOverrideRequest::Enable:

                        // must be disabled for this to make sense
                        if( sched->getCurrentState() == CtiMCSchedule::Disabled )
                        {
                            sched->setCurrentState( CtiMCSchedule::Waiting );

                            //reschedule it
                            _scheduler.removeEvents(sched->getScheduleID());
                            _scheduler.initEvents( stripSeconds( CtiTime::now()), *sched );

                            event_text = "Enabled Schedule:  \\\"";
                            event_text += sched->getScheduleName();
                            event_text += "\\\"";

                            ret_val = true;
                        }
                        break;

                        case CtiMCOverrideRequest::Disable:
                        if( sched->getCurrentState() != CtiMCSchedule::Disabled )
                        {
                            _scheduler.removeEvents(sched->getScheduleID());

                            if( sched->getCurrentState() == CtiMCSchedule::Running )
                            {

                                real_time = stripSeconds(CtiTime::now());
                                sched->setManualStopTime(real_time);

                                _scheduler.scheduleManualStop( stripSeconds(CtiTime::now()), *sched);
                            }

                            sched->setCurrentState( CtiMCSchedule::Disabled );

                            event_text ="Disabled Schedule:  \\\"";
                            event_text += sched->getScheduleName();
                            event_text += "\\\"";
                            ret_val = true;
                        }
                        else
                        {
                            errorMsg = new CtiMCInfo();
                            errorMsg->setInfo("Schedule already disabled");
                        }
                        break;

                        default:
                        {
                            CtiLockGuard< CtiLogger > g(dout);
                            dout << CtiTime() << " Unknown action in a CtiMCOverrideRequest received" << endl;
                        }

                        sched = NULL;
                        break;
                    }


                    if( sched != NULL && _schedule_manager.updateSchedule( *sched ) )
                    {
                        _client_listener.BroadcastMessage( sched->replicateMessage() );
                        logEvent( string(msg->getUser().data()), event_text );
                        ret_val = true;
                    }
                }

            break;
            }

            case MSG_MC_VERIFY_SCRIPT:
            {


            }
            break;
   }

   if( errorMsg != NULL )
   {
       _client_listener.BroadcastMessage(errorMsg, msg->getConnectionHandle());
   }

   return ret_val;
}

bool CtiMCServer::processEvent(const ScheduledEvent& event)
{
    if( gMacsDebugLevel & MC_DEBUG_EVENTS )
    {
        CtiLockGuard<CtiLogger> g(dout);
        dout << CtiTime() << " Processing Event: " << endl;
        dout << CtiTime() <<" schedule id:  " << event.sched_id << endl;
        dout << CtiTime() << " event type:   " << event.event_type << endl;
        dout << CtiTime() << " timestamp:    " << event.timestamp << endl << endl;
    }

    CtiLockGuard<CtiMutex> guard(_schedule_manager.getMux() );
    CtiMCSchedule* sched = _schedule_manager.findSchedule( event.sched_id );

    if( sched == NULL )
    {
        CtiLockGuard<CtiLogger> logGuard(dout);
        dout << CtiTime() << " Attempting to process an event with schedule id:  " << event.sched_id << endl;
        dout << CtiTime() << " No schedule was found with that id." << endl;
        return false;
    }

    string event_text;

    switch( event.event_type )
    {
    case StartSchedule:

        event_text = "Schedule Started:  \\\"";
        event_text += sched->getScheduleName();
        event_text += "\\\"";

    case RepeatInterval:

        if( sched->isSimpleSchedule() )
                executeCommand( sched->getStartCommand(), sched->getTargetPaoId() );
            else
                executeScript(*sched); // start script

        break;
    case StopSchedule:

        event_text = "Schedule Stopped:  \\\"";
        event_text += sched->getScheduleName();
        event_text += "\\\"";

        if( sched->isSimpleSchedule() )
                executeCommand( sched->getStopCommand(), sched->getTargetPaoId() );
            else
                stopScript(sched->getScheduleID()); // stop script

        {
            CtiLockGuard< CtiLogger > logGuard(dout);
            dout << CtiTime() << " Stopping Schedule: " << sched->getScheduleName() << endl;
        }

        break;
    case StartPending:
        break;
    default:
        {
            // Unknown event type.... did another one get added or?
            CtiLockGuard< CtiLogger > logGuard(dout);
            dout << "WARNING:  " << __FILE__ << " (" << __LINE__
                 << ") Unknown event type."
                 << endl;
        }
    }

    _client_listener.BroadcastMessage( sched->replicateMessage() );

    if( event_text.size() > 0 )
    {
        // no user on purpose
        logEvent(" ", event_text );
    }

    return true;
}

/**
    loadDB

    Loads all the schedules into memory
**/
bool CtiMCServer::loadDB()
{
    return _schedule_manager.refreshAllSchedules();
}

bool CtiMCServer::loadCParms()
{
   bool result = true;

   string str;
   char var[128];

   strcpy(var, MC_DEBUG_LEVEL );
   if( !(str = gConfigParms.getValueAsString(var)).empty() )
   {
      char *eptr;
      gMacsDebugLevel = strtoul(str.c_str(), &eptr, 16);

      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " " << MC_DEBUG_LEVEL << ": 0x" << hex <<  gMacsDebugLevel << dec << endl;
      }
   }
   else
   {
       gMacsDebugLevel = DEFAULT_MC_DEBUG_LEVEL;

       {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " " << MC_DEBUG_LEVEL << " not found in master.cfg" << endl;
       }

       result = false;
   }

   str = gConfigParms.getValueAsPath(FTP_INTERFACE_DIR, DEFAULT_MC_FTP_INTERFACE_DIR);

   _file_interface.setDirectory(str);
   _file_interface.setConsumedDirectory(str + "\\processed");

   if( !gConfigParms.isOpt(FTP_INTERFACE_DIR) )
   {
       {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " " << FTP_INTERFACE_DIR << " not found in master.cfg" << endl;
       }
       result = false;
   }

   strcpy(var, FTP_INTERFACE_EXT );
   if( !(str = gConfigParms.getValueAsString(var)).empty() )
   {
       _file_interface.setExtension(str);
   }
   else
   {
       _file_interface.setExtension(DEFAULT_MC_FTP_INTERFACE_EXT);

       {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " " << FTP_INTERFACE_EXT << " not found in master.cfg" << endl;
       }

       result = false;
   }

   strcpy(var, FTP_DELETE_ON_START );
   if( !(str = gConfigParms.getValueAsString(var)).empty() )
   {
       std::transform(str.begin(), str.end(), str.begin(), ::tolower);
       str = trim(str);

       if( str == "true" )
       {
           _file_interface.setDeleteOnStart(true);
       }
       else
       {
           _file_interface.setDeleteOnStart(false);
       }
   }
   else
   {
       _file_interface.setDeleteOnStart(false);

       {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " " << FTP_DELETE_ON_START << " not found in master.cfg" << endl;
       }

       result = false;
   }

   return result;
}

CtiTime CtiMCServer::stripSeconds(const CtiTime& now) const
{
    struct tm now_tm;
    now.extract(&now_tm);
    now_tm.tm_sec = 0;

    return CtiTime(&now_tm);
}

bool CtiMCServer::isToday(const CtiTime& t) const
{
    return ( CtiDate(t) == CtiDate::now() );
}

unsigned long CtiMCServer::secondsToNextMinute() const
{
    struct tm* b_time;

    time_t now = ::time(NULL);
    b_time = CtiTime::localtime_r(&now);
    return (60 - b_time->tm_sec);
}

unsigned long CtiMCServer::secondsToTime(const CtiTime& t) const
{
    unsigned long t_secs = t.seconds();
    unsigned long now_secs = CtiTime::now().seconds();

    return ( t_secs < now_secs ?
             0 :
             (t_secs - now_secs) );
}

void CtiMCServer::dumpRunningScripts()
{
    CtiLockGuard< CtiLogger > guard(dout);

    dout << CtiTime() << " Running scripts:" << endl;

    map< long, CtiInterpreter* >::iterator iter;
    for( iter = _running_scripts.begin();
         iter != _running_scripts.end();
         iter++ )
    {
        dout << CtiTime() << " Schedule id: " << iter->first << " Interpreter: " << iter->second << endl;
    }
}

/*----------------------------------------------------------------------------
  sendDBChange

  DBCHange Message to dispatch.
  Uses a tcl interpreter to do the logging.

  user - the user who caused whatever that needs to be logged
  text - short description of what happened
----------------------------------------------------------------------------*/
void CtiMCServer::sendDBChange(const int& paoid, const string& user) const
{
    // build up the SendDBChange tcl command
    // make sure to embed " in case there are spaces in
    // user
    string cmd_string("SendDBChange ");
    cmd_string.append(CtiNumStr(paoid));
    cmd_string.append(" \"");
    cmd_string.append(user);
    cmd_string.append("\" \" \""); //last arg is blank space

    if( gMacsDebugLevel & MC_DEBUG_EVENTS )
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << cmd_string << endl;
    }

    // Acquire an interpreter and send out the command
    CtiInterpreter* interp = _interp_pool.acquireInterpreter();
    interp->evaluate( cmd_string, true ); //block
    _interp_pool.releaseInterpreter(interp);
}


