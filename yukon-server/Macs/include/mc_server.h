#pragma once

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

#include <time.h>
#include <deque>
#include <set>
#include <map>

class CtiMCServer : public CtiThread
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiMCServer(const CtiMCServer&);
    CtiMCServer& operator=(const CtiMCServer&);

public:

    CtiMCServer();
    virtual ~CtiMCServer();

    // These funcs are overriden CtiThread funcs
    virtual void run();  // <-- MACS main loop
    virtual void interrupt(int id);

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
    CtiInterpreterPool _interp_pool;

    bool init();
    bool deinit();

    bool processMessage(CtiMessage* msg);
    bool processEvent(const ScheduledEvent& event);

    bool loadDB();
    bool loadCParms();

    std::set<std::string> createEscapeCommandSet();

    // log an event with dispatch
    void logEvent(const std::string& user, const std::string& text);

    // execute a command on a tcl interpreter
    void executeCommand(const std::string& command, long target = 0);

    void sendDBChange(const int& paoid, const std::string& user);

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

    static unsigned long secondsToNextMinute();
    static unsigned long secondsToTime(const CtiTime& t);
};
