
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mcserver
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_server.h-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2007/12/10 23:02:57 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------
        Filename:  mc_server.h

        Programmer:  Aaron Lauinger

        Description:  Header file for CtiMCServer

                      CtiMCServer is where the MACS main loop lives.

        Initial Date: 1/9/01

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#ifndef _CTIMCSERVER_H
#define _CTIMCSERVER_H

#include <time.h>
#include <deque>
#include <set>
#include <map>

#include <rw/rwdate.h>

#include "mc.h"
#include "CParms.h"
#include "thread.h"
#include "guard.h"
#include "message.h"
#include "queue.h"
#include "interp.h"
#include "interp_pool.h"
#include "mgr_mcsched.h"
#include "mc_dbthr.h"
#include "rtdb.h"
#include "mccmd.h"
#include "clistener.h"
#include "mc_msg.h"
#include "mc_script.h"
#include "mc_scheduler.h"
#include "mc_fileint.h"

class CtiMCServer : public CtiThread
{
public:

    CtiMCServer();
    virtual ~CtiMCServer();

    // These funcs are overriden CtiThread funcs
    virtual void run();  // <-- MACS main loop
    virtual void interrupt(int id);

    // set this to true to dump the schedule list
    // and event queue at the top of each minute
    void setDebug(bool val);

    void dumpRunningScripts();
private:

    std::set < ScheduledEvent > work_around;
    std::set< ScheduledEvent >::iterator work_around_iter;

    // All of the client messages are put into here
    // and processed in the macs main thread.
    CtiQueue< CtiMessage, std::greater<CtiMessage> > _main_queue;

    // Keeps track of running script type schedules
    std::map< long, CtiInterpreter* > _running_scripts;

    // Keeps track of interpreters commands are executing on
    std::deque< CtiInterpreter* > _executing_commands;

    // Schedules the events
    CtiMCScheduler _scheduler;

    // handles schedule db work
    CtiMCScheduleManager _schedule_manager;

    // Handles client connections
    CtiMCClientListener _client_listener;

    // thread that periodically updates the database
    CtiMCDBThread _db_update_thread;

    CtiMCFileInterface _file_interface;

    // pool of interpreters to use
    mutable CtiInterpreterPool _interp_pool;

    bool _debug;

    bool init();
    bool deinit();

    bool processMessage(CtiMessage* msg);
    bool processEvent(const ScheduledEvent& event);

    bool loadDB();
    bool loadCParms();

    // log an event with dispatch
    void logEvent(const std::string& user, const std::string& text) const;

    // execute a command on a tcl interpreter
    void executeCommand(const std::string& command, long target = 0);

    void sendDBChange(const int& paoid, const std::string& user) const;

    // start and stops scripts
    void executeScript(const CtiMCSchedule& sched);
    void stopScript(long id);

    // The interpreter can call these.
    static void preScriptFunction(CtiInterpreter *interp);
    static void postScriptFunction(CtiInterpreter *interp);

    // Checks to see if all the schedules corresponding
    // scripts are actually running
    void checkRunningScripts();

    // Releases any interpreters executing simple commands that are finished
    void releaseInterpreters();

    // Returns now without seconds
    CtiTime stripSeconds(const CtiTime& now) const;
    bool isToday(const CtiTime& now) const;

    unsigned long secondsToNextMinute() const;
    unsigned long secondsToTime(const CtiTime& t) const;
};

#endif
