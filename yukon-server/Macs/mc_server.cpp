
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mcserver
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_server.cpp-arc  $
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2004/03/25 17:43:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------
        Filename:  mc_server.cpp

        Programmer:  Aaron Lauinger

        Description:  Source file for CtiMCServer


        Initial Date: 1/9/01

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "mc_server.h"
#include "numstr.h"

#include <time.h>
#include <algorithm>
#include <utility.h>

/* The global debug level stored here, defined in mc.h */
unsigned gMacsDebugLevel = 0x00000000;


CtiMCServer::CtiMCServer()
: _debug(false),
  _client_listener(MC_PORT),
  _db_update_thread(_schedule_manager),
  _scheduler(_schedule_manager),
  _file_interface(_schedule_manager)
{
}

CtiMCServer::~CtiMCServer()
{
}

void CtiMCServer::run()
{
    CtiMessage* msg = NULL;
    unsigned long delay = 0; // how long to wait to wait up again max, in seconds
    
    try
    {
        if( init() )
        {

            /* Main Loop */
            while(true)
            {
	        // Workaround for bug in vc6 debug heap
	        ResetBreakAlloc();

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
                    RWTime nextTime = _scheduler.getNextEventTime();
                    if( nextTime.isValid() ) {
                        unsigned long next = secondsToTime(nextTime);
                        if( next < delay ) delay = next;
                    }
                   
                    continue;
                }

                // Release any outstanding interpreters we can
                releaseInterpreters();

                if( gMacsDebugLevel & MC_DEBUG_EVENTS )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << RWTime() << " Checking event queue" << endl;
                }

                if( gMacsDebugLevel & MC_DEBUG_EVENTS )
                    _scheduler.dumpEventQueue();

                // Check to see if the next event is ready to go.

                _scheduler.getEvents( RWTime::now(), work_around );
                   
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
            RWTime nextTime = _scheduler.getNextEventTime();
            if( nextTime.isValid() ) {
                long next = secondsToTime(nextTime);
                if( next < delay ) delay = next;
             }

            if( gMacsDebugLevel & MC_DEBUG_EVENTS )
            {
	        CtiLockGuard<CtiLogger> guard(dout);
                _scheduler.dumpEventQueue();
                dout << RWTime() << " Sleeping for " << delay << " millis" << endl;
            }
         }
        }
        else
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " An error occured during initialization" << endl;
        }
        /* End of Main Loop */
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " *PANIC* MC MAIN THREAD CAUGHT AN EXCEPTION OF UNKNOWN TYPE!" << endl;
        }
    }

    /* We're done lets close down the server */
    {
      CtiLockGuard<CtiLogger> guard(dout);
      dout << RWTime() << " Metering and Control shutting down" << endl;
    }

    deinit();

    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << RWTime() << " Metering and Control exiting" << endl;
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
        dout << RWTime() << cmd_string << endl;
    }

    // Acquire an interpreter and send out the command
    CtiInterpreter* interp = _interp_pool.acquireInterpreter();
    interp->evaluate( cmd_string, true ); //block
    _interp_pool.releaseInterpreter(interp);
}


/*----------------------------------------------------------------------------
  executeCommand

  Executes the start command for the given schedule in a tcl interpreter
  synchronously
----------------------------------------------------------------------------*/
void CtiMCServer::executeCommand(const string& command, const string& target) 
{
    const char* selectPrefix = "Select name \"";
    string to_send;

    if( command.length() == 0 )
        return;

    if( target.length() > 0 )
    {
        to_send.append(selectPrefix)
               .append(target)
               .append("\"\r\n");
    }

    to_send.append(command);

    if( gMacsDebugLevel & MC_DEBUG_INTERP )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()
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
        dout << RWTime() << " Metering and Control starting up..." << endl;
    }

    // load up the database and start the db update thread
    // do not proceed until we have loaded the db successfully
    // but do respect interruptions

    while( !(status = loadDB()) )
    {
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " An error occured retrieving accessing the database, it may not be initialized.  Retry in 15 seconds." << endl;
        }

        if( sleep(15000) )
            break;
    }

    if( !loadCParms() )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " At least one cparm not found in master.cfg" << endl;
    }

    if( status )
    {
        CtiLockGuard< RWRecursiveLock<class RWMutexLock> > map_guard(_schedule_manager.getMux() );
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " Loaded " << _schedule_manager.getMap().entries() << " schedules from the database." << endl;
    }
    else
    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << RWTime() << " An error occured retrieving schedules from the database." << endl;
        status = false;
    }

    /* Set up our events */
    RWTime now = stripSeconds(RWTime::now());
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
    /* stop the file interface */
    _file_interface.stop();

    /* stop accepting connections */
    _client_listener.interrupt( CtiThread::SHUTDOWN );
    _client_listener.join();

    CtiInterpreter* interp = _interp_pool.acquireInterpreter();
    interp->evaluate("pilshutdown", true);
    _interp_pool.releaseInterpreter(interp);

    _interp_pool.stopAndDestroyAllInterpreters();

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

        dout << RWTime() << " Dumping interpreter pool before executing a new script" << endl;
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

        {        
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " [" << interp->getID() << "] " << script.getScriptName() << endl;
        }

        // start the evaluation, non-blocking
        interp->evaluate( script.getContents(), false );

        _running_scripts.insert(
            map< long, CtiInterpreter* >::value_type(sched.getScheduleID(), interp ) );
    }
    else
    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << RWTime() << " Failed to load script:  "
             << script.getScriptName() << endl;
    }

    if( gMacsDebugLevel & MC_DEBUG_INTERP )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dumpRunningScripts();

        dout << RWTime() << " Dumping interpreter pool after executing a new script" << endl;
        _interp_pool.dumpPool();
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

        dout << RWTime() << " Dumping interpreter before stopping script" << endl;
        _interp_pool.dumpPool();
    }

    // find the schedule and reset its manual start + stop times
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard( _schedule_manager.getMux() );
        CtiMCSchedule* sched = _schedule_manager.findSchedule( sched_id );
        sched->setManualStartTime( RWTime( (unsigned long) 0 ));
        sched->setManualStopTime( RWTime( (unsigned long) 0 ));
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

        dout << RWTime() << " Dumping interpreter after stopping script" << endl;
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
    RWTime now( stripSeconds(RWTime::now()) );
    
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
                RWRecursiveLock<RWMutexLock>::LockGuard guard( _schedule_manager.getMux() );
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
        dout << RWTime() << " Processing Message:  " <<  endl;
        msg->dump();
    }

   switch( msg->isA() )
    {
        case MSG_MC_ADD_SCHEDULE:
        {
            if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
            {
                CtiLockGuard<CtiLogger> guard(dout);
                dout << RWTime() << " Received AddSchedule message" << endl;
            }

            RWRecursiveLock<RWMutexLock>::LockGuard guard( _schedule_manager.getMux() );

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
                
                 _scheduler.initEvents( stripSeconds( RWTime::now()), *new_sched );
                
                 _client_listener.BroadcastMessage( new_sched->replicateMessage() );

                 string event_text("Created Schedule:  \\\"");
                 event_text += new_sched->getScheduleName();
                 event_text += "\\\"";

                 logEvent( string( add_msg->getUser().data() ), event_text );

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
                    dout << RWTime() << " Received UpdateSchedule message" << endl;
                }

                RWRecursiveLock<RWMutexLock>::LockGuard guard( _schedule_manager.getMux() );

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
                    _scheduler.initEvents( stripSeconds( RWTime::now()), *updated_sched );
                    
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
                dout << RWTime() << " Received Retrieve Schedule message" << endl;
            }
            id = ((CtiMCRetrieveSchedule*) msg)->getScheduleID();

            if( id == CtiMCRetrieveSchedule::AllSchedules )
            {
                //send all the schedules to the client that requested them
                RWRecursiveLock<RWMutexLock>::LockGuard guard( _schedule_manager.getMux() );

                CtiMCScheduleManager::CtiRTDBIterator itr(_schedule_manager.getMap());
                CtiMultiMsg* multi = new CtiMultiMsg();

                CtiMCSchedule* sched;
                for(;itr();)
                {
                    sched = itr.value();

                    if( sched != NULL )
                    {
                        multi->insert(sched->replicateMessage());
                    }
                }

                // This goes out to all clients
                _client_listener.BroadcastMessage(multi);
            }
            else
            {
                RWRecursiveLock<RWMutexLock>::LockGuard guard( _schedule_manager.getMux() );
                CtiMCSchedule* sched = _schedule_manager.findSchedule(id);

                if( sched != NULL )
                {
                    _client_listener.BroadcastMessage( sched->replicateMessage() );
                }
            }
            break;

            case MSG_MC_DELETE_SCHEDULE:
            {
                bool deleted = false;

                if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                {
                    CtiLockGuard<CtiLogger> guard(dout);
                    dout << RWTime() << " Received Delete Schedule message" << endl;
                }

                CtiMCDeleteSchedule* delete_msg = (CtiMCDeleteSchedule*) msg;
                id = delete_msg->getScheduleID();

                // Find out the schedule name before we delete it
                // so we can log it's name
                string sched_name;

                {                
                    RWRecursiveLock<RWMutexLock>::LockGuard guard( _schedule_manager.getMux() );
                    
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
                   dout << RWTime() << " Received Delete Schedule message" << endl;
               }

               CtiMCRetrieveScript* retrieve_msg = (CtiMCRetrieveScript*) msg;

               CtiMCScript script;
               script.setScriptName( retrieve_msg->getScriptName() );

               if( !script.readContents() )
               {
                   errorMsg = new CtiMCInfo();
                   errorMsg->setInfo("An error occured reading the script");
               }

               _client_listener.BroadcastMessage( script.replicateMessage() );
           }
            break;

            case MSG_MC_OVERRIDE_REQUEST:
            {
                if( gMacsDebugLevel & MC_DEBUG_MESSAGES )
                {
                    CtiLockGuard<CtiLogger> guard(dout);
                    dout << RWTime() << " Received Override Request message" << endl;
                }

                RWRecursiveLock<RWMutexLock>::LockGuard guard( _schedule_manager.getMux() );

                CtiMCOverrideRequest* request_msg = (CtiMCOverrideRequest*) msg;
                CtiMCSchedule* sched = _schedule_manager.findSchedule( request_msg->getID() );

                string event_text;
                RWTime real_time( (unsigned long) 0);

                if( sched != NULL )
                {
                    switch( request_msg->getAction() )
                    {
                        case CtiMCOverrideRequest::Start:

                        if( !request_msg->getStartTime().isValid() )
                        {
                            real_time = stripSeconds( RWTime::now() );
                            sched->setManualStartTime(real_time);
                        }
                        else
                        {
                            real_time = stripSeconds(request_msg->getStartTime());
                            sched->setManualStartTime(real_time);
                        }

                        _scheduler.scheduleManualStart( stripSeconds(RWTime::now()), *sched);

                        event_text = "Start Schedule: \\\"";
                        event_text += sched->getScheduleName();
                        event_text += "\\\" @ ";
                        event_text += real_time.asString().data();

                        break;

                        case CtiMCOverrideRequest::StartNow:

                        real_time = stripSeconds(RWTime::now());
                        sched->setManualStartTime(real_time);

                        _scheduler.scheduleManualStart( stripSeconds(RWTime::now()), *sched);

                        event_text = "Start Schedule: \\\"";
                        event_text += sched->getScheduleName();
                        event_text += "\\\" @ ";
                        event_text += real_time.asString().data();

                        break;


                        case CtiMCOverrideRequest::Stop:

                        if( !request_msg->getStopTime().isValid() )
                        {
                            real_time = stripSeconds( RWTime::now() );
                            sched->setManualStopTime(real_time);
                        }
                        else
                        {
                            real_time = request_msg->getStopTime();
                            sched->setManualStopTime(real_time);
                        }

                        _scheduler.scheduleManualStop( stripSeconds(RWTime::now()), *sched);

                        event_text = "Stop Schedule: \\\"";
                        event_text += sched->getScheduleName();
                        event_text += "\\\" @ ";
                        event_text += real_time.asString().data();

                        break;

                        case CtiMCOverrideRequest::StopNow:

                        real_time = stripSeconds(RWTime::now());
                        sched->setManualStopTime(real_time);

                        _scheduler.scheduleManualStop( stripSeconds(RWTime::now()), *sched);

                        event_text = "Stop Schedule: \\\"";
                        event_text += sched->getScheduleName();
                        event_text += "\\\" @ ";
                        event_text += real_time.asString().data();

                        break;

                        case CtiMCOverrideRequest::Enable:

                        // must be disabled for this to make sense
                        if( sched->getCurrentState() == CtiMCSchedule::Disabled )
                        {                            
                            sched->setCurrentState( CtiMCSchedule::Waiting );

                            //reschedule it
                            _scheduler.removeEvents(sched->getScheduleID());
                            _scheduler.initEvents( stripSeconds( RWTime::now()), *sched );

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
                            
                                real_time = stripSeconds(RWTime::now());
                                sched->setManualStopTime(real_time);
    
                                _scheduler.scheduleManualStop( stripSeconds(RWTime::now()), *sched);
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
                            dout << RWTime() << " Unknown action in a CtiMCOverrideRequest received" << endl;
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
       _client_listener.BroadcastMessage(errorMsg);
   }

   return ret_val;
}

bool CtiMCServer::processEvent(const ScheduledEvent& event)
{
    if( gMacsDebugLevel & MC_DEBUG_EVENTS )
    {
        CtiLockGuard<CtiLogger> g(dout);
        dout << RWTime() << " Processing Event: " << endl;
        dout << RWTime() <<" schedule id:  " << event.sched_id << endl;
        dout << RWTime() << " event type:   " << event.event_type << endl;
        dout << RWTime() << " timestamp:    " << event.timestamp << endl << endl;
    }

    CtiLockGuard< RWRecursiveLock<class RWMutexLock> > guard(_schedule_manager.getMux() );
    CtiMCSchedule* sched = _schedule_manager.findSchedule( event.sched_id );

    if( sched == NULL )
    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << RWTime() << " Attempting to process an event with schedule id:  " << event.sched_id << endl;
        dout << RWTime() << " No schedule was found with that id." << endl;
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
                executeCommand( sched->getStartCommand(), sched->getTargetSelect() );
            else
                executeScript(*sched); // start script

        break;
    case StopSchedule:

        event_text = "Schedule Stopped:  \\\"";
        event_text += sched->getScheduleName();
        event_text += "\\\"";

        if( sched->isSimpleSchedule() )
                executeCommand( sched->getStopCommand(), sched->getTargetSelect() );
            else
                stopScript(sched->getScheduleID()); // stop script

        break;
    case StartPending:
        break;
    default:
        {
            // Unknown event type.... did another one get added or?
            CtiLockGuard< CtiLogger > guard(dout);
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

   RWCString str;
   char var[128];

   strcpy(var, MC_DEBUG_LEVEL );
   if( !(str = gConfigParms.getValueAsString(var)).isNull() )
   {
      char *eptr;
      gMacsDebugLevel = strtoul(str.data(), &eptr, 16);

      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " " << MC_DEBUG_LEVEL << ": 0x" << hex <<  gMacsDebugLevel << dec << endl;
      }
   }
   else
   {
       gMacsDebugLevel = DEFAULT_MC_DEBUG_LEVEL;

       {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " " << MC_DEBUG_LEVEL << " not found in master.cfg" << endl;
       }

       result = false;
   }

   strcpy(var, FTP_INTERFACE_DIR );
   if( !(str = gConfigParms.getValueAsString(var)).isNull() )
   {
       RWCString consumed(str);
       consumed += "\\processed";

       _file_interface.setDirectory(str);
       _file_interface.setConsumedDirectory(consumed);
   }
   else
   {
       RWCString consumed(DEFAULT_MC_FTP_INTERFACE_DIR);
       consumed += "\\processed";

       _file_interface.setDirectory(DEFAULT_MC_FTP_INTERFACE_DIR);
       _file_interface.setConsumedDirectory(consumed);
       {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " " << FTP_INTERFACE_DIR << " not found in master.cfg" << endl;
       }
       result = false;
   }

   strcpy(var, FTP_INTERFACE_EXT );
   if( !(str = gConfigParms.getValueAsString(var)).isNull() )
   {
       _file_interface.setExtension(str);
   }
   else
   {
       _file_interface.setExtension(DEFAULT_MC_FTP_INTERFACE_EXT);

       {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " " << FTP_INTERFACE_EXT << " not found in master.cfg" << endl;
       }

       result = false;
   }

   strcpy(var, FTP_DELETE_ON_START );
   if( !(str = gConfigParms.getValueAsString(var)).isNull() )
   {
       str.toLower();
       str.strip();

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
            dout << RWTime() << " " << FTP_DELETE_ON_START << " not found in master.cfg" << endl;
       }

       result = false;
   }

   return result;
}

RWTime CtiMCServer::stripSeconds(const RWTime& now) const
{
    struct tm now_tm;
    now.extract(&now_tm);
    now_tm.tm_sec = 0;

    return RWTime(&now_tm);
}

bool CtiMCServer::isToday(const RWTime& t) const
{
    return ( RWDate(t) == RWDate::now() );
}

unsigned long CtiMCServer::secondsToNextMinute() const
{
    struct tm* b_time;

    time_t now = time(NULL);
    b_time = localtime(&now);
    return (60 - b_time->tm_sec);
}

unsigned long CtiMCServer::secondsToTime(const RWTime& t) const
{
    unsigned long t_secs = t.seconds();
    unsigned long now_secs = RWTime::now().seconds();

    return ( t_secs < now_secs ?
             0 :
             (t_secs - now_secs) );    
}

void CtiMCServer::dumpRunningScripts()
{
    CtiLockGuard< CtiLogger > guard(dout);

    dout << RWTime() << " Running scripts:" << endl;

    map< long, CtiInterpreter* >::iterator iter;
    for( iter = _running_scripts.begin();
         iter != _running_scripts.end();
         iter++ )
    {
        dout << RWTime() << " Schedule id: " << iter->first << " Interpreter: " << iter->second << endl;
    }
}


