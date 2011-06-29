#include "yukon.h"

#include <crtdbg.h>
#include <iostream>
#include <sstream>

#include <conio.h>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>

#include "id_calc.h"
#include "dbaccess.h"
#include "ctinexus.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_dbchg.h"
#include "numstr.h"
#include "pointtypes.h"
#include "configparms.h"
#include "logger.h"
#include "cparms.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"
#include "ThreadStatusKeeper.h"

#include "calclogicsvc.h"
#include "calcthread.h"

#define CHECK_RATE_SECONDS  30     // 30 second check for db change, on a change some re-loading is done, this slows the max rate down.

using Cti::ThreadStatusKeeper;

BOOL UserQuit = FALSE;
bool _shutdownOnThreadTimeout = false;
bool _runCalcHistorical = false;
bool _runCalcBaseline = false;
bool _ignoreTimeValidTag = false;

//Boolean if debug messages are printed
ULONG _CALC_DEBUG = CALC_DEBUG_THREAD_REPORTING;

BOOL MyCtrlHandler( DWORD fdwCtrlType )
{
    switch(fdwCtrlType)
    {
    case CTRL_C_EVENT:
    case CTRL_CLOSE_EVENT:
    case CTRL_BREAK_EVENT:
    case CTRL_LOGOFF_EVENT:
    case CTRL_SHUTDOWN_EVENT:
    default:
        UserQuit = TRUE;
        Sleep(5000);
        return TRUE;
    }
}

IMPLEMENT_SERVICE(CtiCalcLogicService, CALCLOGIC)


CtiCalcLogicService::CtiCalcLogicService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType ) :
CService( szName, szDisplay, dwType ), _ok(TRUE), _restart(false), _update(false), _dispatchPingedFailed(YUKONEOT),
_dispatchConnectionBad(false), _lastDispatchMessageTime(CtiTime::now())
{
    calcThread = 0;
    m_pThis = this;
}


void CtiCalcLogicService::RunInConsole( DWORD argc, LPTSTR *argv )
{
    CService::RunInConsole(argc, argv);

    //We need to catch ctrl-c so we can stop
    if( !SetConsoleCtrlHandler( (PHANDLER_ROUTINE)MyCtrlHandler,  TRUE ) )
    {
        cerr << "Could not install control handler" << Cti::endl;
        _ok = FALSE;
    }

    Init( );
    Run( );

    OnStop( );
}


void CtiCalcLogicService::DeInit( )
{
    CService::DeInit( );
}

void CtiCalcLogicService::Init( )
{
    try
    {
        //defaults
        string logFile("calc");

        dout.setOwnerInfo(CompileInfo);
        dout.setOutputFile(logFile);
        dout.setOutputPath(gLogDirectory);
        dout.setToStdOut(true);
        dout.setWriteInterval(1);
        dout.start();     // fire up the logger thread

        loadConfigParameters();

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCalcLogicService::ParseArgs( DWORD argc, LPTSTR *argv )
{
    //  dunno when this'll ever be needed, as the "console" parameter is parsed by now already
}


void CtiCalcLogicService::OnStop( )
{
    SetStatus(SERVICE_STOP_PENDING, 33, 60000 );
    UserQuit = true;
}


void CtiCalcLogicService::Run( )
{
    CtiMultiMsg *msgMulti;
    time_t   timeNow;

    int conncnt= 0;
    long pointID;

    ThreadMonitor.start(); //ecs 1/4/2005
    CtiTime NextThreadMonitorReportTime;
    CtiThreadMonitor::State previous;

    pointID = ThreadMonitor.getPointIDFromOffset(CtiThreadMonitor::Calc);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if(_running_in_console)
            dout << CtiTime( ) << " - " << CompileInfo.project << " [Version " << CompileInfo.version << "] starting console mode ..." << endl;
        else
            dout << CtiTime( ) << " - " << CompileInfo.project << " [Version " << CompileInfo.version << "] starting as service..." << endl;

    }

    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    try
    {
        ThreadStatusKeeper threadStatus("CalcLogicSvc main");

        if( !_ok )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " CalcRun() NOT OK...  errors during initialization" << endl;
            }
            throw( RWxmsg( "NOT OK...  errors during initialization" ) );
        }

        SetStatus(SERVICE_START_PENDING, 66, 5000 );

        // set service as running
        SetStatus(SERVICE_RUNNING, 0, 0, SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

        _conxion = NULL;
        ULONG attempts = 0;

        CtiTime rwnow, pingTime;
        pingTime = nextScheduledTimeAlignedOnRate( CtiTime(), 3660);

        while( !UserQuit )
        {
            try
            {
                if( _conxion == NULL || (_conxion != NULL && _conxion->verifyConnection()) )
                {
                    _dispatchPingedFailed = CtiTime(YUKONEOT);
                    if(_conxion) dropDispatchConnection();

                    _restart = false;                      // make sure our flag is reset
                    _update = false;

                    loadConfigParameters();             // Reload the config.

                    _inputFunc  = rwMakeThreadFunction( *this, &CtiCalcLogicService::_inputThread );
                    _outputFunc = rwMakeThreadFunction( *this, &CtiCalcLogicService::_outputThread );
                    _inputFunc.start( );
                    _outputFunc.start( );

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Creating a new connection to dispatch." << endl;
                    }

                    _conxion = CTIDBG_new  CtiConnection(_dispatchPort, _dispatchMachine);

                    //  write the registration message (this is only done once, because if the database changes,
                    //    the program name and such doesn't change - only our requested points do.)

                    // USE A SINGLE Simple Name - bdw
                    _conxion->WriteConnQue( CTIDBG_new CtiRegistrationMsg("CalcLogic"+CtiNumStr(conncnt++), rwThreadId( ), FALSE) );
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Dispatch connection failed - " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                dropDispatchConnection();
                Sleep(1000);   // sleep for 1 second
                continue;
            }

            //  validate the connection before we attempt to send anything across
            //    (note that the above database read delays this check long enough to allow the
            //     connection to complete.)
            //  FIX_ME:  This became broken when I ported this to be a a service.  It has something
            //             to do with threads.  It makes an ASSERTion fail in RW code.

            try
            {
                if( !_conxion->valid( ) || _conxion->verifyConnection() )
                {
                    if( attempts++ % 60 == 0)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Connecting to Dispatch ...." << endl;
                    }
                    else if( attempts % 300 == 0 )
                    {//only say we can't get a Dispatch connection every 5 minutes
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Calc could not establish a connection to Dispatch" << endl;
                    }

                    // try it again
                    Sleep(1000);   // sleep for 1 second
                    continue;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Dispatch connection established.  Loading calc points." << endl;
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Dispatch connection failed - " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                dropDispatchConnection();
                Sleep(1000);   // sleep for 1 second
                continue;
            }

            RWThreadFunction calcThreadFunc;
            CtiCalculateThread *tempCalcThread;

            try
            {
                try
                {
                    if(calcThread)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Pausing the calcThreads for reload" << endl;
                        }
                        calcThread->interruptThreads(CtiCalculateThread::DBReload);       // Make certain these threads are paused if they can be.
                    }
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    calcThread = 0;
                }

                tempCalcThread = CTIDBG_new CtiCalculateThread;
                if( !readCalcPoints( tempCalcThread ) )
                {
                    try
                    {
                        if(calcThread) calcThread->resumeThreads();
                        {//only say we can't load any calc points every 5 minutes
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " No Calc Points Loaded.  Reusing old lists if possible." << endl;
                        }

                        // try it again
                        if(calcThread)
                        {
                            tempCalcThread->setPeriodicPointMap(calcThread->getPeriodicPointMap());
                            tempCalcThread->setOnUpdatePointMap(calcThread->getOnUpdatePointMap());
                            tempCalcThread->setConstantPointMap(calcThread->getConstantPointMap());
                            tempCalcThread->setHistoricalPointMap(calcThread->getHistoricalPointMap());

                            calcThread->clearPointMaps();
                        }
                        else
                        {
                            delete tempCalcThread;
                            tempCalcThread = 0;

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Unable to reuse the old point lists.  Will attempt a DB reload in 15 sec." << endl;
                            }
                            Sleep(15000);
                            continue;
                        }
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        delete tempCalcThread;
                        tempCalcThread = 0;
                        Sleep(1000);
                        continue;
                    }
                }

                try
                {
                    try
                    {
                        if(calcThread)
                        {
                            delete calcThread;
                            calcThread = 0;
                        }
                    }
                    catch(RWxmsg &msg)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << msg.why() << endl;
                        }
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    calcThread = tempCalcThread;

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << calcThread->numberOfLoadedCalcPoints() << " Calc Points Loaded" << endl;
                    }

                    try
                    {
                        _registerForPoints();
                        calcThread->sendConstants();
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }


                    //  Up until this point, nothing has needed a mutex.  Now I'm spawning threads, and the
                    //    commonly-accessed resources will be the calcThread's pointStore object and message
                    //    queue (and during debug, the outstreams).  The calcThread object will be fed point
                    //    data changes by the input thread.  The output thread will take the messages from the
                    //    calcThread message queue and post them to Dispatch.
                    calcThreadFunc = rwMakeThreadFunction( *calcThread, &CtiCalculateThread::calcThread );
                    calcThreadFunc.start( );
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                // delete tempCalcThread;
                // delete calcThread;
                tempCalcThread = 0;
                calcThread = 0;

                Sleep(5000);
                continue;
            }

            CtiPointDataMsg     *msgPtData = NULL;

            ::std::time(&_nextCheckTime);
            _nextCheckTime += CHECK_RATE_SECONDS;    // added some time to it
            _restart = false;                       // make sure our flag is reset
            _update = false;

            for( ; !UserQuit; )
            {
                try
                {
                    if(pointID!=0)
                    {
                        CtiThreadMonitor::State next;
                        if((next = ThreadMonitor.getState()) != previous || 
                           CtiTime::now() > NextThreadMonitorReportTime)
                        {
                            // Any time the state changes or every (StandardMonitorTime / 2) seconds, update the point
                            previous = next;
                            NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2 );
        
                            if(_conxion)
                            {
                                _conxion->WriteConnQue(CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString().c_str()));
                            }
                        }
                    }

                    // I think this is what we want instead... I believe it preserves existing functionality, but
                    // may require additional eyes to be sure.
                    if( threadStatus.monitorCheck( CtiThreadMonitor::ExtendedMonitorTime ) )
                    {    
                        if(_conxion)
                        {
                            if( _dispatchPingedFailed != CtiTime(YUKONEOT) )
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Error in the connection to dispatch.  Will attempt to restart it. " << endl;
                                }
                                _dispatchPingedFailed = CtiTime(YUKONEOT);
                                break;
                            }
                            else if(rwnow > pingTime)
                            {
                                pingTime = nextScheduledTimeAlignedOnRate( rwnow, 3660 );
                                if(_dispatchConnectionBad)
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** INFO **** No msg data has been recieved since last ping/verify.  Re-registering points." << endl;
                                    }
                                    _registerForPoints();                       // re-register if we haven't seen data since last ping.
                                }
                        
                                _dispatchPingedFailed = pingTime - 30;   // This is the future. Dispatch needs to answer us in this amount of time.
                        
                                CtiCommandMsg *pCmd = CTIDBG_new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15);
                                pCmd->setUser(CompileInfo.project);
                                if(_conxion) _conxion->WriteConnQue( pCmd );
                                else delete pCmd;
                            }
                        }
                    }

                    rwSleep( 1000 );
                    ::std::time(&timeNow);

                    if( timeNow > _nextCheckTime )
                    {

                        if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Timer Checking for DB Change." << endl;
                        }

                        // check for DB Changes on a set interval
                        // this makes us a little kinder on re-registrations
                        if( _restart )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " CalcLogicSvc main thread has recieved a reset command (DB Change)." << endl;

                            break; // exit the loop and reload
                        }

                        if( (_lastDispatchMessageTime.seconds() + 400) < CtiTime::now().seconds() )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " CalcLogic has not heard from dispatch for at least 7 minutes, resetting." << endl;
                            }
                            _lastDispatchMessageTime = CtiTime::now();
                            break; // exit the loop and reload
                        }

                        if( _update )
                        {
                            updateCalcData();
                        }

                        _nextCheckTime = timeNow + CHECK_RATE_SECONDS;
                    }

                    if( ((timeNow-18000) % 86400) <= 5 )        // CGP this is terrible code.  Fix it someday.
                    {//reset the max allocations once a day, midnight central standard time
                        LONG currentAllocations = ResetBreakAlloc();
                        if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Current Number of Historical Memory Allocations: " << currentAllocations << endl;
                        }
                    }
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    Sleep(5000);
                }
            } // end for userquit

            try
            {
                //  interrupt the calculation and i/o threads, and tell it to come back home
                _dispatchPingedFailed = CtiTime(YUKONEOT);

                if( !UserQuit )
                {
                    dropDispatchConnection();
                }
                
                threadStatus.monitorCheck();

                calcThreadFunc.requestInterrupt( );

                try
                {
                    calcThreadFunc.releaseInterrupt( );
                }
                catch(RWTHRIllegalUsage &msg)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                if(RW_THR_COMPLETED != calcThreadFunc.join( 30000 ))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " CalcLogicSvc main did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
                    }

                    calcThreadFunc.terminate();
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " CalcLogicSvc main shutdown correctly." << endl;
                    }
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                Sleep(5000);
            }
        }

        SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

        //  tell Dispatch we're going away, then leave
        if(_conxion) _conxion->WriteConnQue( CTIDBG_new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
        if(_conxion) _conxion->ShutdownConnection();

        SetStatus(SERVICE_STOP_PENDING, 75, 5000 );
        dropDispatchConnection();

        if( calcThread )
        {
            delete calcThread;
            calcThread = 0;
        }

        CtiPointStore::removeInstance();
    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception in Calc and Logic: ";
        dout << msg.why() << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    ThreadMonitor.interrupt(CtiThread::SHUTDOWN);
    ThreadMonitor.join();

    // stop dout thread
    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    SetStatus( SERVICE_STOPPED );
}


void CtiCalcLogicService::_outputThread( void )
{
    RWRunnableSelf _pSelf = rwRunnable( );
    int entries = 0;
    BOOL interrupted = FALSE;
    CtiMultiMsg *toSend;
    bool junkbool = false;

    ThreadStatusKeeper threadStatus("CalcLogicSvc _outputThread");

    try
    {
        while( !interrupted )
        {
            //  while there's nothing to send
            do
            {
                if(calcThread)
                {
                    RWMutexLock::LockGuard outboxGuard(calcThread->outboxMux);
                    entries = calcThread->outboxEntries( );
                }
                if( _pSelf.serviceInterrupt( ) )
                {
                    interrupted = TRUE;
                }
                else if( !entries || !_conxion )
                    _pSelf.sleep( 500 );

                if(!_shutdownOnThreadTimeout)
                {
                    threadStatus.monitorCheck();
                }
                else
                {
                    threadStatus.monitorCheck(&CtiCalcLogicService::sendUserQuit);
                }
            } while( !entries && !interrupted );

            if( !interrupted && _conxion)
            {
                RWMutexLock::LockGuard outboxGuard(calcThread->outboxMux);

                for( ; calcThread->outboxEntries( ); )
                {
                    toSend = calcThread->getOutboxEntry( );
                    if(_conxion)
                    {
                        if(toSend && toSend->getCount() > 0)
                        {
                            if(junkbool)
                                toSend->dump();
                            _conxion->WriteConnQue( toSend );
                        }
                        else
                        {
                            delete toSend;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Calculation result lost due to a connection problem." << endl;
                        }
                        delete toSend;
                        _pSelf.sleep(500);
                        break;          // The for loop
                    }
                }
            }
        }
    }
    catch(...)
    {
        Sleep(1000);            // Keep the kamakazi loop from happening
        _restart = true;       // Kick it so it restarts us in a failure mode
        ::std::time(&_nextCheckTime);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiCalcLogicService::_inputThread( void )
{
    try
    {
        RWRunnableSelf  _pSelf = rwRunnable( );
        RWCollectable   *incomingMsg;
        BOOL            interrupted = FALSE;

        ThreadStatusKeeper threadStatus("CalcLogicSvc _inputThread");

        while( !interrupted )
        {
            try
            {
                //  while i'm not getting anything
                while( !_conxion || (NULL == (incomingMsg = _conxion->ReadConnQue( 1000 )) && !interrupted) )
                {
                    if( _pSelf.serviceInterrupt( ) )
                    {
                        interrupted = ( _interruptReason == CtiCalculateThread::Pause ) ? false : true ;
                    }
                    else
                    {
                        if(!_shutdownOnThreadTimeout)
                        {
                            threadStatus.monitorCheck();
                        }
                        else
                        {
                            threadStatus.monitorCheck(&CtiCalcLogicService::sendUserQuit);
                        }
                    }
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            try
            {
                if(!_shutdownOnThreadTimeout)
                {
                    threadStatus.monitorCheck();
                }
                else
                {
                    threadStatus.monitorCheck(&CtiCalcLogicService::sendUserQuit);
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            // Just in case we have lots of messages inbound.
            if( _pSelf.serviceInterrupt( ) )
            {
                interrupted = ( _interruptReason == CtiCalculateThread::Pause ) ? false : true ;
            }

            try
            {
                if(incomingMsg)
                {
                    //  dump out if we're being called
                    if( !interrupted )
                    {
                        //  common variable, but this is the only place that writes to it, so i think it's okay.
                        parseMessage( incomingMsg, calcThread );
                    }
                    delete incomingMsg;   //  Make sure to delete this - its on the heap
                    incomingMsg = 0;
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    catch(...)
    {
        Sleep(1000); // Keep the kamakazi loop from happening
        _restart = true;       // Kick it so it restarts us in a failure mode
        ::std::time(&_nextCheckTime);

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

// return is not used at this time
BOOL CtiCalcLogicService::parseMessage( RWCollectable *message, CtiCalculateThread *thread )
{
    BOOL retval = TRUE;

    if(!thread)
    { // This prevents changes from messing us up while we are still loading
        retval = FALSE;
    }
    else
    {
        try
        {
            int op;
            CtiMultiMsg *msgMulti;
            CtiPointDataMsg *pData;
            CtiSignalMsg *pSignal;
            int x;

            switch( message->isA( ) )
            {
            case MSG_DBCHANGE:
                // only reload on if a database change was made to a point
                if( ((CtiDBChangeMsg*)message)->getDatabase() == ChangePointDb)
                {
                    if( ((CtiDBChangeMsg*)message)->getTypeOfChange() != ChangeTypeAdd)
                    {
                        // Must have been a delete or update
                        if(thread->isACalcPointID(((CtiDBChangeMsg*)message)->getId()) == TRUE)
                        {
                            _update = true;
                            _nextCheckTime = std::time(0) + CHECK_RATE_SECONDS;
                            _dbChangeMessages.push( *((CtiDBChangeMsg*)message) );

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime()  << " - Database change - Point change.  Setting reload flag. Reload at " << ctime(&_nextCheckTime);
                            }
                        }
                        else if( ((CtiDBChangeMsg*)message)->getTypeOfChange() == ChangeTypeUpdate && pointNeedsReload(((CtiDBChangeMsg*)message)->getId()) )
                        {
                            // Do something!!
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " DBChange recieved for point which has special values. Reloading single point: "<< ((CtiDBChangeMsg*)message)->getId() << endl;
                            }
                            reloadPointAttributes(((CtiDBChangeMsg*)message)->getId());
                        }
                        else
                        {
                            if( ((CtiDBChangeMsg*)message)->getId() == 0 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime()  << " - Database change does not affect Calculations.  Will not reload." << endl;
                                dout << CtiTime()  << " *** Checkpoint *** Change was a full reload, which is currently not acted upon." << endl;
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime()  << " - Database change does not affect Calculations.  Will not reload." << endl;
                            }
                        }
                    }
                    else
                    {
                        bool wasCalcPoint = true;

                        wasCalcPoint = isANewCalcPointID( ((CtiDBChangeMsg*)message)->getId() );
                        if( wasCalcPoint )
                        {
                            // always load when a point is added
                            _update = true;
                            _nextCheckTime = std::time(0) + CHECK_RATE_SECONDS;
                            _dbChangeMessages.push( *((CtiDBChangeMsg*)message) );

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime()  << " - Database change - PointDB.  Setting reload flag. Reload at " << ctime(&_nextCheckTime);
                            }
                        }
                    }
                }

                break;

            case MSG_COMMAND:
                // we will handle some messages
                op = ((CtiCommandMsg*)message)->getOperation();
                switch( op )
                {
                case (CtiCommandMsg::Shutdown):
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " CalcLogic received a shutdown message - Ignoring!!" << endl;
                    }
                    break;

                case (CtiCommandMsg::AreYouThere):
                    {
                        // echo back the same message - we are here

                        CtiCommandMsg *pCmd = (CtiCommandMsg*)message;
                        if(pCmd->getUser() != CompileInfo.project)
                        {
                            if(_conxion) _conxion->WriteConnQue( pCmd->replicateMessage() );

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " CalcLogic has been pinged" << endl;
                            }
                            _dispatchConnectionBad = false;
                        }
                        else
                        {
                            // This is a response to our own ping to dispatch.  Mark it out so the machinery does not try to reconnect
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Dispatch connection ping verified." << endl;
                            }
                            _dispatchPingedFailed = CtiTime(YUKONEOT);
                            _dispatchConnectionBad = true;
                        }
                        break;
                    }
                default:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " CalcLogic received a unknown/don't care Command message- " << op << endl;
                    }
                    break;

                }
                break;

            case MSG_POINTDATA:
                {
                    _lastDispatchMessageTime = CtiTime::now();

                    pData = (CtiPointDataMsg *)message;
                    thread->pointChange( pData->getId(), pData->getValue(), pData->getTime(), pData->getQuality(), pData->getTags() );
                    _dispatchConnectionBad = false;
                }
                break;

            case MSG_MULTI:
                // pull all of the messages out
                msgMulti = (CtiMultiMsg *)message;

                if( _CALC_DEBUG & CALC_DEBUG_INBOUND_MSGS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime()  << "  Processing Multi Message with: " << msgMulti->getData( ).size( ) << " messages -  " << endl;
                }
                for( x = 0; x < msgMulti->getData( ).size( ); x++ )
                {
                    // recursive call to parse this message
                    parseMessage( msgMulti->getData( )[x], thread );
                }
                break;

            case MSG_SIGNAL:
                {
                    _lastDispatchMessageTime = CtiTime::now();

                    pSignal = (CtiSignalMsg *)message;
                    if( pSignal->getId() )
                    {
                        thread->pointSignal( pSignal->getId(), pSignal->getTags() );
                    }
                    _dispatchConnectionBad = false;

                    if( _CALC_DEBUG & CALC_DEBUG_INBOUND_MSGS)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime( ) << "  Signal Message received for point id: " << ((CtiSignalMsg*)message)->getId() << endl;
                    }
                }
                break;

            default:
                if(isDebugLudicrous())
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime( ) << " - " << __FILE__ << " (" << __LINE__ << ") Calc_Logic does not know how to handle messages of type \"" << message->stringID( ) << "\";  skipping" << endl;
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    return retval;
}

/*-----------------------------------------------------------------------------*
* Function Name: isANewCalcPointID()
*
* Description: returns true if the PointID is a Calc Point ID
*              Different then isACalcPointID in it checks the database
*              not memory, so new points are checked also. Much less efficient.
*-----------------------------------------------------------------------------*
*/
BOOL CtiCalcLogicService::isANewCalcPointID(const long aPointID)
{
    int retVal = false;

    //  figure out what points are calc points
    static const string sqlCore = "SELECT CB.POINTID "
                                  "FROM CALCBASE CB "
                                  "WHERE CB.POINTID = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sqlCore);

    rdr << aPointID;

    rdr.execute();

    // If this exists, return true, else return false
    if( rdr() )
    {
        retVal = true;
    }

    return retVal;
}

bool CtiCalcLogicService::readCalcPoints( CtiCalculateThread *thread )
{
    bool returnBool = true;

    // should be collection but not now
    long CalcCount = 0;

    try
    {
        //  figure out what points are calc points
        static const string sqlBase =   "SELECT CB.POINTID, CB.UPDATETYPE, CB.PERIODICRATE, CB.QUALITYFLAG "
                                        "FROM CALCBASE CB";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection);

        rdr.setCommandText(sqlBase);

        rdr.execute();

        // Load all Base Calcs first
        while( rdr() )
        {
            int updateinterval;
            long pointid;
            string updatetype;
            string qualityflag;

            //  grab the point information from the database and stuff it into our class
            rdr["POINTID"] >> pointid;
            rdr["UPDATETYPE"] >> updatetype;
            rdr["PERIODICRATE"] >> updateinterval;
            rdr["QUALITYFLAG"] >> qualityflag;
            thread->appendCalcPoint( pointid );

            // put the collection in the correct collection based on type
            if( thread->appendPoint( pointid, updatetype, updateinterval, qualityflag ) )
            {
                ++CalcCount;

                if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Loaded Calc #" << CalcCount << " Id: " << pointid << " Type: " << updatetype << endl;
                }
            }
        }

        string componenttype, operationtype, functionname;
        long pointid, componentpointid;
        double constantvalue;

        static const string sqlCalc = "SELECT CC.POINTID, CC.COMPONENTTYPE, CC.COMPONENTPOINTID, CC.OPERATION, CC.CONSTANT, "
                                        "CC.FUNCTIONNAME "
                                      "FROM CALCCOMPONENT CC "
                                      "ORDER BY COMPONENTORDER ASC";

        Cti::Database::DatabaseReader componentRdr(connection);
        
        componentRdr.setCommandText(sqlCalc);

        componentRdr.execute();

        //  iterate through the components
        while( componentRdr() )
        {

            //  read 'em in, and append to the class
            componentRdr["POINTID"] >> pointid;
            componentRdr["COMPONENTTYPE"] >> componenttype;
            componentRdr["COMPONENTPOINTID"] >> componentpointid;
            componentRdr["OPERATION"] >> operationtype;
            componentRdr["CONSTANT"] >> constantvalue;
            componentRdr["FUNCTIONNAME"] >> functionname;


            //    order is defined externally - by the order that they're selected and appended
            thread->appendPointComponent( pointid, componenttype, componentpointid,
                                              operationtype, constantvalue, functionname );
            if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Component for calc Id " << pointid <<
                " CT " << componenttype <<
                ", CPID " << componentpointid <<
                ", OP " << operationtype <<
                ", CONST " << constantvalue <<
                ", FUNC " << functionname << endl;
            }
        }

        long uomid;
        //Read from PointUnit Table, insert into pointStore

        static const string sqlPoint =  "SELECT DISTINCT PU.POINTID, PU.UOMID "
                                        "FROM POINTUNIT PU, CALCBASE CB, CALCCOMPONENT CC "
                                        "WHERE PU.POINTID = CC.COMPONENTPOINTID OR PU.POINTID = CB.POINTID";

        Cti::Database::DatabaseReader unitRdr(connection);

        unitRdr.setCommandText(sqlPoint);

        unitRdr.execute();

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        //  iterate through the components
        while( unitRdr() )
        {

            //  read 'em in, and append to the class
            unitRdr["POINTID"] >> pointid;
            unitRdr["UOMID"] >> uomid;

            CtiHashKey pointHashKey(pointid);
            CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&pointHashKey));
            if( calcPointPtr != rwnil )
            {
                calcPointPtr->setUOMID(uomid);
            }
            if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "UOM for calc Id " << pointid <<
                " UOMID " << uomid << endl;
            }
        }

        static const string sqlLimit = "SELECT DISTINCT PL.pointid, PL.limitnumber, PL.highlimit, PL.lowlimit, "
                                          "PL.limitduration "
                                       "FROM POINTLIMITS PL, CALCCOMPONENT CC, CALCBASE CB "
                                       "WHERE PL.pointid = CC.COMPONENTPOINTID OR PL.pointid = CB.POINTID";

        Cti::Database::DatabaseReader limitReader(connection);

        limitReader.setCommandText(sqlLimit);

        limitReader.execute();

        while( limitReader() )
        {
            limitReader["pointid"] >> pointid;

            CtiHashKey pointHashKey(pointid);
            CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&pointHashKey));
            if( calcPointPtr != rwnil )
            {
                calcPointPtr->readLimits(limitReader);
            }

            if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
            {
                long limitNum, hlim, llim, limitdur;
                limitReader["limitnumber"] >> limitNum;
                limitReader["highlimit"] >> hlim;
                limitReader["lowlimit"] >> llim;
                limitReader["limitduration"] >> limitdur;

                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Limits for calc Id " << pointid <<
                " LNUM " << limitNum <<
                ", HLIM " << hlim <<
                ", LLIM " << llim <<
                ", LDUR " << limitdur << endl;
            }
        }

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception while reading calc points from database: " << msg.why( ) << endl;
        exit( -1 );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( thread->numberOfLoadedCalcPoints() <= 0 )
    {
        returnBool = false;
    }

    return returnBool;
}


void CtiCalcLogicService::dropDispatchConnection(  )
{
    _interruptReason = CtiCalculateThread::Shutdown;
    try
    {
        if( _conxion != NULL )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Dropping the connection to dispatch." << endl;
            }

            Sleep(2500);
            if(_conxion) _conxion->ShutdownConnection();
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        _conxion = 0;
    }


    try
    {
        _inputFunc.requestInterrupt( 5000 );
        _outputFunc.requestInterrupt( 5000 );


        try
        {
            _inputFunc.releaseInterrupt( );
            _outputFunc.releaseInterrupt( );
        }
        catch(RWTHRIllegalUsage &msg)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << msg.why() <<  ".  " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if(RW_THR_COMPLETED != _inputFunc.join( 10000 ))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " _inputThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
            }

            _inputFunc.terminate();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " _inputThread shutdown correctly." << endl;
            }
        }

        if(RW_THR_COMPLETED != _outputFunc.join( 10000 ))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " _outputThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
            }

            _outputFunc.terminate();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " _outputThread shutdown correctly." << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    try
    {
        if( _conxion != NULL )
        {
            delete _conxion;
        }
        _conxion = 0;
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ").  Error deleting connection to dispatch." << endl;
        }
        _conxion = 0;
    }


    return;
}

void CtiCalcLogicService::pauseInputThread()
{
    _interruptReason = CtiCalculateThread::Pause;
    try
    {
        _inputFunc.requestInterrupt( 5000 );
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    if( _CALC_DEBUG & CALC_DEBUG_RELOAD )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Input Thread Paused"<< endl;
    }
}

void CtiCalcLogicService::resumeInputThread()
{
    try
    {
        _inputFunc.releaseInterrupt( );
    }
    catch(RWTHRIllegalUsage &msg)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << msg.why() <<  ".  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiCalcLogicService::updateCalcData()
{
    CtiTime start, stop;

    try
    {
        start = start.now();

        CtiDBChangeMsg *dbChangeMsg;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " DB Update Started" << endl;
        }

        pauseInputThread();//Pause threads that would use our data
        calcThread->interruptThreads(CtiCalculateThread::Pause);
        _update = false;

        CtiPointStore* pointStore = CtiPointStore::getInstance();

        for( ; _dbChangeMessages.size() ; )
        {
            dbChangeMsg = &_dbChangeMessages.front();

            int pointID = dbChangeMsg->getId();

            if( dbChangeMsg->getTypeOfChange() != ChangeTypeAdd )
            {
                if( _CALC_DEBUG & CALC_DEBUG_RELOAD )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " DBUpdate changing point "<< pointID << endl;
                }
                calcThread->removePointStoreObject( pointID );

            }
    /*      else
            {
                //Do nothing on Add, the data was never there to begin with!
            }
    */
            _dbChangeMessages.pop();//Because of the way I made the queue, there is no need to delete
        }

        if( _CALC_DEBUG & CALC_DEBUG_RELOAD )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " DBUpdate done changing points"<< endl;
        }

        calcThread->clearAndDestroyPointMaps();
        readCalcPoints(calcThread);
        _registerForPoints();

        if( _CALC_DEBUG & CALC_DEBUG_RELOAD )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Points refreshed and registration message sent"<< endl;
        }

        //From here on Im not positive about this ordering, resume threads before or after we register?
        calcThread->sendConstants();
        calcThread->resumeThreads();
        resumeInputThread();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " DB Update Complete" << endl;
        }

        stop = stop.now();
        if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to reload calc tables" << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        calcThread->resumeThreads();
        calcThread->interruptThreads( CtiCalculateThread::Shutdown );
        calcThread->joinThreads();//lets try killing them completely, then reloading?
        resumeInputThread();
        _restart = true;
    }
}

void CtiCalcLogicService::_registerForPoints()
{
    try
    {
        //  iterate through the calc points' dependencies, adding them to the registration message
        //  XXX:  Possibly add the iterator and accessor functions to the calcThread class itself, rather than
        //          providing the iterator directly?
        RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> > *depIter;
        depIter = calcThread->getPointDependencyIterator( );
        CtiPointRegistrationMsg *msgPtReg = CTIDBG_new CtiPointRegistrationMsg(0);
        for( ; (*depIter)( ); )
        {
            if( _CALC_DEBUG & CALC_DEBUG_DISPATCH_INIT )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " - Registered for point id: " << ((CtiPointStoreElement *)depIter->value( ))->getPointNum() << endl;
            }
            msgPtReg->insert( ((CtiPointStoreElement *)depIter->value( ))->getPointNum( ) );
        }
        delete depIter;

        msgPtReg->insert( ThreadMonitor.getPointIDFromOffset(ThreadMonitor.Calc) );
        //  now send off the point registration
        if(_conxion)
        {
            if(msgPtReg)
            {
                _conxion->WriteConnQue( msgPtReg );
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            delete msgPtReg;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}


bool CtiCalcLogicService::pointNeedsReload(long pointID)
{
    bool retVal = false;
    CtiPointStore* pointStore = CtiPointStore::getInstance();
    CtiHashKey pointHashKey(pointID);
    CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&pointHashKey));

    if( calcPointPtr != rwnil )
    {
        if( calcPointPtr->getLimit(1) != NULL || calcPointPtr->getLimit(2) != NULL )
        {
            retVal = true;
        }
    }

    return retVal;
}


void CtiCalcLogicService::reloadPointAttributes(long pointID)
{
    try
    {
        long uomid, pointid;
        //Read from PointUnit Table, insert into pointStore

        static const string sqlCore =  "SELECT PTU.POINTID, PTU.UOMID "
                                       "FROM POINTUNIT PTU "
                                       "WHERE PTU.POINTID = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader unitRdr(connection, sqlCore);

        unitRdr << pointID;

        unitRdr.execute();

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        //  iterate through the components
        while( unitRdr() )
        {
            //  read 'em in, and append to the class
            unitRdr["POINTID"] >> pointid;
            unitRdr["UOMID"] >> uomid;

            CtiHashKey pointHashKey(pointid);
            CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&pointHashKey));
            if( calcPointPtr != rwnil )
            {
                if( _CALC_DEBUG & CALC_DEBUG_RELOAD )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Updating UOMID for point id " << pointid << endl;
                }
                calcPointPtr->setUOMID(uomid);
            }
        }

        const string limitSQL = "SELECT PTL.POINTID, PTL.limitnumber, PTL.highlimit, PTL.lowlimit, PTL.limitduration "
                                "FROM POINTLIMITS PTL "
                                "WHERE PTL.POINTID = ?";

        Cti::Database::DatabaseReader limitReader(connection, limitSQL);

        limitReader << pointID;

        limitReader.execute();

        while( limitReader() )
        {
            limitReader["POINTID"] >> pointid;

            CtiHashKey pointHashKey(pointid);
            CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&pointHashKey));
            if( calcPointPtr != rwnil )
            {
                if( _CALC_DEBUG & CALC_DEBUG_RELOAD )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Updating limits for point id " << pointid << endl;
                }
                calcPointPtr->readLimits(limitReader);
            }
        }

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception while reading calc points from database: " << msg.why( ) << endl;
        exit( -1 );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCalcLogicService::loadConfigParameters()
{

    string str;
    char var[256];

    //defaults
    string logFile = string("calc");
    _dispatchMachine = string("127.0.0.1");
    _dispatchPort = VANGOGHNEXUS;
    _CALC_DEBUG = CALC_DEBUG_THREAD_REPORTING;
    //defaults

    strcpy(var, "CALC_LOGIC_LOG_FILE");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        dout.setOutputFile(str);
        if( _CALC_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        dout.setOutputFile(logFile);
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    strcpy(var, "DISPATCH_MACHINE");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _dispatchMachine = str;
        if( _CALC_DEBUG )
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

    strcpy(var, "DISPATCH_PORT");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        _dispatchPort = atoi(str.c_str());
        if( _CALC_DEBUG )
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

    strcpy(var, "CALC_LOGIC_DEBUG");
    if(gConfigParms.isOpt(var,"true"))
    {
        _CALC_DEBUG = 0xFFFFFFFF;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << CtiNumStr(_CALC_DEBUG).xhex().zpad(8).toString() << endl;
        }
    }
    else if(gConfigParms.isOpt(var,"false"))
    {
        _CALC_DEBUG = 0x00000000;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << CtiNumStr(_CALC_DEBUG).xhex().zpad(8).toString() << endl;
        }
    }
    else if( 0 != (_CALC_DEBUG = gConfigParms.getValueAsULong(var,0,16)) )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << var << ":  " << CtiNumStr(_CALC_DEBUG).xhex().zpad(8).toString() << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    strcpy(var, "CALC_SHUTDOWN_ON_THREAD_TIMEOUT");
    if( ciStringEqual(gConfigParms.getValueAsString(var),"true") )
    {
        _shutdownOnThreadTimeout = true;
    }
    else
    {
        _shutdownOnThreadTimeout = false;
        if(DebugLevel & CALC_DEBUG_CALC_INIT) cout << "Configuration Parameter CALC_SHUTDOWN_ON_THREAD_TIMEOUT default : " << _shutdownOnThreadTimeout << Cti::endl;
    }

    strcpy(var, "CALC_LOGIC_RUN_HISTORICAL");
    if( ciStringEqual(gConfigParms.getValueAsString(var),"true") )
    {
        _runCalcHistorical = true;
        if(DebugLevel & CALC_DEBUG_CALC_INIT) cout << "Configuration Parameter CALC_LOGIC_RUN_HISTORICAL default : " << _shutdownOnThreadTimeout << Cti::endl;
    }
    else
    {
        _runCalcHistorical = false;
    }

    strcpy(var, "CALC_LOGIC_RUN_BASELINE");
    if( ciStringEqual(gConfigParms.getValueAsString(var),"true") )
    {
        _runCalcBaseline = true;
        if(DebugLevel & CALC_DEBUG_CALC_INIT) cout << "Configuration Parameter CALC_LOGIC_RUN_BASELINE default : " << _shutdownOnThreadTimeout << Cti::endl;
    }
    else
    {
        _runCalcBaseline = false;
    }

    strcpy(var, "CALC_IGNORE_TIME_VALID_TAG");
    if( ciStringEqual(gConfigParms.getValueAsString(var, "false"),"true") )
    {
        _ignoreTimeValidTag = true;
    }
    else
    {
        if(DebugLevel & CALC_DEBUG_CALC_INIT) cout << "Configuration Parameter CALC_IGNORE_TIME_VALID_TAG default : " << _ignoreTimeValidTag << Cti::endl;
        _ignoreTimeValidTag = false;
    }

    SET_CRT_OUTPUT_MODES;
    if(gConfigParms.isOpt("DEBUG_MEMORY") && gConfigParms.isTrue("DEBUG_MEMORY") )
        ENABLE_CRT_SHUTDOWN_CHECK;
}

void CtiCalcLogicService::sendUserQuit(void *who)
{
    string *strPtr = (string *) who;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << *strPtr << " has asked for shutdown."<< endl;
    }
    UserQuit = TRUE;
}
