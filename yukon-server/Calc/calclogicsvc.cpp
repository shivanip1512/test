#include "yukon.h"

#include <crtdbg.h>
#include <windows.h>
#include <iostream>

#include <conio.h>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>
#include <rw/cstring.h>
#include <rw/db/reader.h>
#include <rw/db/connect.h>

#include "dbaccess.h"
#include "ctinexus.h"
#include "id_build.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_dbchg.h"
#include "numstr.h"
#include "pointtypes.h"
#include "configparms.h"
#include "logger.h"
#include "cparms.h"
#include "utility.h"

#include "calclogicsvc.h"

#define CHECK_RATE_SECONDS  60     // one minute check for db change

BOOL UserQuit = FALSE;

//Boolean if debug messages are printed
ULONG _CALC_DEBUG = CALC_DEBUG_THREAD_REPORTING;

#define CALCLOGICNAME "Calc & Logic"

static CTICOMPILEINFO CompileInfo = {
    CALCLOGICNAME,
    MAJORREVISION,
    MINORREVISION,
    BUILDNUMBER,
    __TIMESTAMP__
};

RWCString CALCVERSION = "1.50";

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
        Sleep(30000);
        return TRUE;
    }
}

IMPLEMENT_SERVICE(CtiCalcLogicService, CALCLOGIC)


CtiCalcLogicService::CtiCalcLogicService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType ) :
CService( szName, szDisplay, dwType ), _ok(TRUE), _restart(FALSE), _dispatchPingedFailed(YUKONEOT), _lastWasPingNoDataSince(false)
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
        cerr << "Could not install control handler" << endl;
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
        RWCString logFile = RWCString("calc");

        dout.setOutputFile(logFile.data());
        dout.setOutputPath(gLogDirectory.data());
        dout.setToStdOut(true);
        dout.setWriteInterval(1);
        dout.start();     // fire up the logger thread

        loadConfigParameters();

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCalcLogicService::ParseArgs( DWORD argc, LPTSTR *argv )
{
    //  dunno when this'll ever be needed, as the "console" parameter is parsed by now already
}


void CtiCalcLogicService::OnStop( )
{
    SetStatus(SERVICE_STOP_PENDING, 33, 5000 );
    UserQuit = true;
}


void CtiCalcLogicService::Run( )
{
    CtiMultiMsg *msgMulti;

    time_t   timeNow;
    time_t   nextCheckTime;

    ThreadMonitor.start(); //ecs 1/4/2005

    CALCVERSION = identifyProjectVersion(CompileInfo);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if(_running_in_console)
            dout << RWTime( ) << " - Calc and Logic Version: " << CALCVERSION << " starting console mode ..." << endl;
        else
            dout << RWTime( ) << " - Calc and Logic Service Version: " << CALCVERSION << " starting ..." << endl;

    }


    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    try
    {
        if( !_ok )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " CalcRun() NOT OK...  errors during initialization" << endl;
            }
            throw( RWxmsg( "NOT OK...  errors during initialization" ) );
        }

        SetStatus(SERVICE_START_PENDING, 66, 5000 );

        // set service as running
        SetStatus(SERVICE_RUNNING, 0, 0, SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

        _conxion = NULL;
        ULONG attempts = 0;

        RWTime rwnow, announceTime;
        announceTime = nextScheduledTimeAlignedOnRate( RWTime(), 300);

        while( !UserQuit )
        {
            try
            {
                if( _conxion == NULL || (_conxion != NULL && _conxion->verifyConnection()) )
                {
                    if(_conxion) dropDispatchConnection();

                    _restart = false;                      // make sure our flag is reset

                    loadConfigParameters();             // Reload the config.

                    _inputFunc  = rwMakeThreadFunction( *this, &CtiCalcLogicService::_inputThread );
                    _outputFunc = rwMakeThreadFunction( *this, &CtiCalcLogicService::_outputThread );
                    _inputFunc.start( );
                    _outputFunc.start( );

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Creating a new connection to dispatch." << endl;
                    }

                    _conxion = new CtiConnection(_dispatchPort, _dispatchMachine);

                    //  write the registration message (this is only done once, because if the database changes,
                    //    the program name and such doesn't change - only our requested points do.)

                    // USE A SINGLE Simple Name - bdw
                    _conxion->WriteConnQue( new CtiRegistrationMsg("CalcLogic", rwThreadId( ), FALSE) );

                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Dispatch connection failed - " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        dout << RWTime() << " Connecting to Dispatch ...." << endl;
                    }
                    else if( attempts % 300 == 0 )
                    {//only say we can't get a Dispatch connection every 5 minutes
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Calc could not establish a connection to Dispatch" << endl;
                    }

                    // try it again
                    Sleep(1000);   // sleep for 1 second
                    continue;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Dispatch connection established.  Loading calc points." << endl;
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Dispatch connection failed - " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                dropDispatchConnection();
                Sleep(1000);   // sleep for 1 second
                continue;
            }

            CtiCalculateThread *tempCalcThread = new CtiCalculateThread;
            if( !readCalcPoints( tempCalcThread ) )
            {
                if( ++attempts % 120 == 0 )
                {//only say we can't load any calc points every 5 minutes
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " No Calc Points Loaded" << endl;
                }

                // try it again
                delete tempCalcThread;

                Sleep(15000);
                continue;
            }
            else
            {
                delete calcThread;
                calcThread = 0;
                calcThread = tempCalcThread;

                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << calcThread->numberOfLoadedCalcPoints() << " Calc Points Loaded" << endl;
            }

            _registerForPoints();

            //  Up until this point, nothing has needed a mutex.  Now I'm spawning threads, and the
            //    commonly-accessed resources will be the calcThread's pointStore object and message
            //    queue (and during debug, the outstreams).  The calcThread object will be fed point
            //    data changes by the input thread.  The output thread will take the messages from the
            //    calcThread message queue and post them to Dispatch.
            RWThreadFunction calcThreadFunc = rwMakeThreadFunction( *calcThread, &CtiCalculateThread::calcLoop );

            calcThreadFunc.start( );

            CtiPointDataMsg     *msgPtData = NULL;

            // get time now
//            time(&nextCheckTime);
            ::std::time(&nextCheckTime);  //ecs 1/4/2005

            //FIXFIXFIX - move CHECK_RATE_SECONDS to a CParm in the future
            //
            nextCheckTime += CHECK_RATE_SECONDS;    // added some time to it

            _restart = false;                      // make sure our flag is reset

            for( ; !UserQuit; )
            {
                try
                {
                    rwnow = rwnow.now();
                    if(rwnow > announceTime)
                    {
                        announceTime = nextScheduledTimeAlignedOnRate( rwnow, 300 );
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " CalcLogicSvc main thread is active" << endl;
                        }
                    }

                    //
                    // ecs 1/5/2005 added to register with our thread watcher
                    //
                    CtiThreadRegData *data = new CtiThreadRegData( rwThreadId(), "CalcLogicSvc main", CtiThreadRegData::Action1, 300, &CtiCalcLogicService::mainComplain, 0 , 0, 0 );
                    ThreadMonitor.tickle( data );
//                    ThreadMonitor.dump();

                    if(_conxion)
                    {
                        if( _dispatchPingedFailed != RWTime(YUKONEOT) )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Error in the connection to dispatch.  Will attempt to restart it. " << endl;
                            }

                            dropDispatchConnection();
                            break;
                        }
                        else
                            {
                                if(_lastWasPingNoDataSince)
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** INFO **** No data has been recieved since last ping/verify.  Re-registering points." << endl;
                                    }
                                    _registerForPoints();                       // re-register if we haven't seen data since last ping.
                                }

                                _dispatchPingedFailed = announceTime - 30;   // This is the future. Dispatch needs to answer us in this amount of time.

                                CtiCommandMsg *pCmd = new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15);
                                pCmd->setUser(CALCLOGICNAME);
                                if(_conxion) _conxion->WriteConnQue( pCmd );
                            }
                        }
                    }

                    rwSleep( 1000 );

                    //time (&timeNow);
                    ::std::time (&timeNow);   //ecs 1/4/2005


                    if( timeNow > nextCheckTime )
                    {

                        if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Timer Checking for DB Change." << endl;
                        }


                        // check for DB Changes on a set interval
                        // this makes us a little kinder on re-registrations
                        if(_restart)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Calc has recieved DB Change- Reloading." << endl;

                            break; // exit the loop and reload
                        }

                        nextCheckTime = timeNow + CHECK_RATE_SECONDS;

                    }

                    if( ((timeNow-18000) % 86400) == 0 )
                    {//reset the max allocations once a day, midnight central standard time
                        LONG currentAllocations = ResetBreakAlloc();
                        if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Current Number of Historical Memory Allocations: " << currentAllocations << endl;
                        }
                    }
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            } // end for userquit


            //  interrupt the calculation and i/o threads, and tell it to come back home
            dropDispatchConnection();
            calcThreadFunc.requestInterrupt( );
            calcThreadFunc.releaseInterrupt( );
            calcThreadFunc.join( );

            //  from this point out, i'm one thread again
            // CGP do not loose the list if it can be helped. // delete calcThread;
        }

        SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

        //  tell Dispatch we're going away, then leave
        if(_conxion) _conxion->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
        Sleep(2500);
        if(_conxion) _conxion->ShutdownConnection();

        // ecs 1/5/2005
        CtiThreadRegData *data = new CtiThreadRegData( rwThreadId(), "CalcLogicSvc main", CtiThreadRegData::LogOut );
        ThreadMonitor.tickle( data );

        SetStatus(SERVICE_STOP_PENDING, 75, 5000 );
        dropDispatchConnection();
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
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    // stop dout thread
    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    SetStatus( SERVICE_STOPPED );
}


void CtiCalcLogicService::_outputThread( void )
{
    RWRunnableSelf _pSelf = rwRunnable( );
    RWTPtrDeque<CtiMultiMsg>::size_type entries = 0;
    BOOL interrupted = FALSE;
    CtiMultiMsg *toSend;

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
                    interrupted = TRUE;
                else if( !entries )
                    _pSelf.sleep( 200 );

                //ecs 1/5/2005
                CtiThreadRegData *data = new CtiThreadRegData( rwThreadId(), "CalcLogicSvc _outputThread", CtiThreadRegData::Action1, 210, &CtiCalcLogicService::outComplain, 0 , 0, 0 );
                ThreadMonitor.tickle( data );

            } while( !entries && !interrupted );

            if( !interrupted )
            {
                RWMutexLock::LockGuard outboxGuard(calcThread->outboxMux);

                //ecs 1/5/2005
                CtiThreadRegData *data = new CtiThreadRegData( rwThreadId(), "CalcLogicSvc _outputThread", CtiThreadRegData::Action1, 1000, &CtiCalcLogicService::outComplain, 0 , 0, 0 );
                ThreadMonitor.tickle( data );

                for( ; calcThread->outboxEntries( ); )
                {
                    toSend = calcThread->getOutboxEntry( );
                    if(_conxion)
                    {
                        if(toSend && toSend->getCount() > 0)
                        {
                            _conxion->WriteConnQue( toSend );
                        }
                        else
                        {
                            delete toSend;
                        }
                    }
                    else
                    {
                        delete toSend;
                        _pSelf.sleep( 1000 );
                        break;          // The for loop
                    }
                }
            }
        }
    }
    catch(...)
    {
        Sleep(1000);            // Keep the kamakazi loop from happening
        _restart = TRUE;       // Kick it so it restarts us in a failure mode

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

        while( !interrupted )
        {
            //  while i'm not getting anything
            while( !_conxion || (NULL == (incomingMsg = _conxion->ReadConnQue( 200 )) && !interrupted) )
            {
                //ecs 1/5/2005
                CtiThreadRegData *data = new CtiThreadRegData( rwThreadId(), "CalcLogicSvc _inputThread", CtiThreadRegData::Action1, 210, &CtiCalcLogicService::inComplain, 0 , 0, 0 );
                ThreadMonitor.tickle( data );

                if( _pSelf.serviceInterrupt( ) )
                {
                    interrupted = TRUE;
                }
                else
                {
                    _pSelf.sleep( 1000 );
                }
            }

            // Just in case we have lots of messages inbound.
            if( _pSelf.serviceInterrupt( ) )
                interrupted = TRUE;

            if(incomingMsg)
            {
                //  dump out if we're being called
                if( !interrupted )
                {
                    //ecs 1/5/2005
                    CtiThreadRegData *data = new CtiThreadRegData( rwThreadId(), "CalcLogicSvc _inputThread", CtiThreadRegData::Action1, 1000, &CtiCalcLogicService::inComplain, 0 , 0, 0 );
                    ThreadMonitor.tickle( data );

                    //  common variable, but this is the only place that writes to it, so i think it's okay.
                    parseMessage( incomingMsg, calcThread );
                }
                delete incomingMsg;   //  Make sure to delete this - its on the heap
                incomingMsg = 0;
            }
        }
    }
    catch(...)
    {
        Sleep(1000); // Keep the kamakazi loop from happening
        _restart = TRUE;       // Kick it so it restarts us in a failure mode

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

// return is not used at this time
BOOL CtiCalcLogicService::parseMessage( RWCollectable *message, CtiCalculateThread *calcThread )
{
    BOOL retval = TRUE;

    try
    {
        int op;
        CtiMultiMsg *msgMulti;
        CtiPointDataMsg *pData;
        int x;

        switch( message->isA( ) )
        {
        case MSG_DBCHANGE:
            // only reload on if a database change was made to a point
            if( ((CtiDBChangeMsg*)message)->getDatabase() == ChangePointDb)
            {
                if( ((CtiDBChangeMsg*)message)->getTypeOfChange() != ChangeTypeAdd)
                {

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime()  << " - Looking update DBChange PointID in Calc List..." << endl;
                    }

                    // Must have been a delete or update
                    if(calcThread->isACalcPointID(((CtiDBChangeMsg*)message)->getId()) == TRUE)
                    {
                        _restart = TRUE;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime()  << " - Database change on loaded calc.  Setting reload flag." << endl;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime()  << " - Database change does not affect Calculations.  Will not reload." << endl;
                        }
                    }
                }
                else
                {
                    // always load when a point is added
                    _restart = TRUE;

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime()  << " - Database change- Point Added.  Setting reload flag." << endl;
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
                    dout << RWTime() << " CalcLogic received a shutdown message from somewhere- Ignoring!!" << endl;
                }
                break;

            case (CtiCommandMsg::AreYouThere):
                {
                    // echo back the same message - we are here

                    CtiCommandMsg *pCmd = (CtiCommandMsg*)message;
                    if(pCmd->getUser() != CALCLOGICNAME)
                    {
                        if(_conxion) _conxion->WriteConnQue( pCmd->replicateMessage() );

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " CalcLogic has been pinged" << endl;
                        }
                    }
                    else
                    {
                        // This is a response to our own ping to dispatch.  Mark it out so the machinery does not try to reconnect
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Dispatch connection ping verified." << endl;
                        }
                        _dispatchPingedFailed = RWTime(YUKONEOT);
                        _lastWasPingNoDataSince = true;
                    }
                    break;
                }
            default:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " CalcLogic received a unknown/don't care Command message- " << op << endl;
                }
                break;

            }
            break;

        case MSG_POINTDATA:
            {
                pData = (CtiPointDataMsg *)message;
                calcThread->pointChange( pData->getId(), pData->getValue(), pData->getTime(), pData->getQuality(), pData->getTags() );
                _lastWasPingNoDataSince = false;
            }
            break;

        case MSG_MULTI:
            // pull all of the messages out
            msgMulti = (CtiMultiMsg *)message;

            if( _CALC_DEBUG & CALC_DEBUG_INBOUND_MSGS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime()  << "  Processing Multi Message with: " << msgMulti->getData( ).entries( ) << " messages -  " << endl;
            }

            for( x = 0; x < msgMulti->getData( ).entries( ); x++ )
            {
                // recursive call to parse this message
                parseMessage( msgMulti->getData( )[x], calcThread );
            }
            break;

        case MSG_SIGNAL:
            // not an error
            if( _CALC_DEBUG & CALC_DEBUG_INBOUND_MSGS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << "  Signal Message received for point id: " << ((CtiSignalMsg*)message)->getId() << endl;
            }
            break;

        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " - " << __FILE__ << " (" << __LINE__ << ") Calc_Logic does not know how to handle messages of type \"" << message->stringID( ) << "\";  skipping" << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return retval;
}


bool CtiCalcLogicService::readCalcPoints( CtiCalculateThread *calcThread )
{
    bool returnBool = true;

    // should be collection but not now
    long pointIdList[2000];
    long CalcCount = 0;

    try
    {
        //  connect to the database
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection( );

        //  figure out what points are calc points
        //RWCString sql;

        RWDBDatabase db             = conn.database();
        RWDBTable    calcBaseTable  = db.table("CALCBASE");
        RWDBSelector selector       = db.selector();

        selector << calcBaseTable["POINTID"]
        << calcBaseTable["UPDATETYPE"]
        << calcBaseTable["PERIODICRATE"];

        selector.from( calcBaseTable );

        //{
        //    CtiLockGuard<CtiLogger> doubt_guard(dout);
        //    dout << selector.asString().data() << endl;
        //}

        RWDBReader  rdr = selector.reader( conn );

        // Load all Base Calcs first
        while( rdr() )
        {
            int updateinterval;
            long pointid;
            RWCString updatetype;

            //  grab the point information from the database and stuff it into our class
            rdr["POINTID"] >> pointid;
            rdr["UPDATETYPE"] >> updatetype;
            rdr["PERIODICRATE"] >> updateinterval;
            pointIdList[CalcCount] = pointid;
            ++CalcCount;

            if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Loaded Calc #" << CalcCount << " Id: " << pointid << " Type: " << updatetype << endl;
            }

            // put the collection in the correct collection based on type
            calcThread->appendPoint( pointid, updatetype, updateinterval );

            if(CalcCount > 2000)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                }
            }
        }



        for(int i=0; i < CalcCount; i++)
        {
            calcThread->appendCalcPoint( pointIdList[i] );

            long componentPointId;
            RWCString componenttype, operationtype, functionname;
            long componentpointid;
            double constantvalue;

            RWDBTable    componentTable     = db.table("CALCCOMPONENT");
            RWDBSelector componentselector  = db.selector();

            componentselector << componentTable["COMPONENTTYPE"]
            << componentTable["COMPONENTPOINTID"]
            << componentTable["OPERATION"]
            << componentTable["CONSTANT"]
            << componentTable["FUNCTIONNAME"];

            componentselector.from( componentTable );

            // use PointID for where
            componentselector.where(componentselector["POINTID"] == pointIdList[i]);

            // put in order
            componentselector.orderBy(componentselector["COMPONENTORDER"]);

            //cout << componentselector.asString().data() << endl;

            if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Loading Components for Calc Id: " << pointIdList[i] << endl;
            }

            RWDBReader  componentRdr = componentselector.reader( conn );

            //  iterate through the components
            while( componentRdr() )
            {

                //  read 'em in, and append to the class
                componentRdr["COMPONENTTYPE"] >> componenttype;
                componentRdr["COMPONENTPOINTID"] >> componentpointid;
                componentRdr["OPERATION"] >> operationtype;
                componentRdr["CONSTANT"] >> constantvalue;
                componentRdr["FUNCTIONNAME"] >> functionname;

                //  i'm not including COMPONENTORDER in the internal data structure because the
                //    order is defined externally - by the order that they're selected and appended
                calcThread->appendPointComponent( pointIdList[i], componenttype, componentpointid,
                                                  operationtype, constantvalue, functionname );
                if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Component for calc Id " << pointIdList[i] <<
                    " CT " << componenttype <<
                    ", CPID " << componentpointid <<
                    ", OP " << operationtype <<
                    ", CONST " << constantvalue <<
                    ", FUNC " << functionname << endl;
                }
            }

        } // end for loop

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
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( calcThread->numberOfLoadedCalcPoints() <= 0 )
    {
        returnBool = false;
    }

    return returnBool;
}


void CtiCalcLogicService::dropDispatchConnection(  )
{

    if( _conxion != NULL )
    {
        if(_conxion) _conxion->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
        Sleep(2500);
        if(_conxion) _conxion->ShutdownConnection();
    }

    //ecs 1/5/2005
    CtiThreadRegData *data = new CtiThreadRegData( rwThreadId(), "CalcLogicSvc _outputThread", CtiThreadRegData::LogOut );//, 210, &CtiCalcLogicService::outComplain, 0 , 0,  0 );
    ThreadMonitor.tickle( data );
    data = new CtiThreadRegData( rwThreadId(), "CalcLogicSvc _inputThread", CtiThreadRegData::LogOut );//, 210, &CtiCalcLogicService::inComplain, 0 , 0, 0 );
    ThreadMonitor.tickle( data );

    _inputFunc.requestInterrupt( 1000 );
    _outputFunc.requestInterrupt( 1000 );

    _inputFunc.join( 1000 );
    _outputFunc.join( 1000 );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dropping the connection to dispatch." << endl;
    }
    try
    {
        if( _conxion != NULL )
        {
            delete _conxion;
        }
        _conxion = NULL;
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ").  Error deleting connection to dispatch." << endl;
        }
    }


    return;
}

//ecs 1/5/2005
void CtiCalcLogicService::mainComplain( void *la )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " CalcLogicSvc MAIN thread is AWOL" << endl;
    }
}

void CtiCalcLogicService::outComplain( void *la )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " CalcLogicSvc OUT thread is AWOL" << endl;
    }
}

void CtiCalcLogicService::inComplain( void *la )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " CalcLogicSvc IN thread is AWOL" << endl;
    }
}

void CtiCalcLogicService::_registerForPoints()
{

    //  iterate through the calc points' dependencies, adding them to the registration message
    //  XXX:  Possibly add the iterator and accessor functions to the calcThread class itself, rather than
    //          providing the iterator directly?
    RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> > *depIter;
    depIter = calcThread->getPointDependencyIterator( );
    CtiPointRegistrationMsg *msgPtReg = new CtiPointRegistrationMsg(0);
    for( ; (*depIter)( ); )
    {
        if( _CALC_DEBUG & CALC_DEBUG_DISPATCH_INIT )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - Registered for point id: " << ((CtiPointStoreElement *)depIter->value( ))->getPointNum() << endl;
        }
        msgPtReg->insert( ((CtiPointStoreElement *)depIter->value( ))->getPointNum( ) );
    }
    delete depIter;

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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        delete msgPtReg;
    }

    return;
}


void CtiCalcLogicService::loadConfigParameters()
{

    RWCString str;
    char var[256];

    //defaults
    RWCString logFile = RWCString("calc");
    _dispatchMachine = RWCString("127.0.0.1");
    _dispatchPort = VANGOGHNEXUS;
    _CALC_DEBUG = CALC_DEBUG_THREAD_REPORTING;
    //defaults

    strcpy(var, "CALC_LOGIC_LOG_FILE");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        dout.setOutputFile(str.data());
        if( _CALC_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        dout.setOutputFile(logFile.data());
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    strcpy(var, "DISPATCH_MACHINE");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        _dispatchMachine = str;
        if( _CALC_DEBUG )
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

    strcpy(var, "DISPATCH_PORT");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        _dispatchPort = atoi(str);
        if( _CALC_DEBUG )
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

    strcpy(var, "CALC_LOGIC_DEBUG");
    if(gConfigParms.isOpt(var,"true"))
    {
        _CALC_DEBUG = 0xFFFFFFFF;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << (char*)CtiNumStr(_CALC_DEBUG).xhex().zpad(8) << endl;
        }
    }
    else if(gConfigParms.isOpt(var,"false"))
    {
        _CALC_DEBUG = 0x00000000;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << (char*)CtiNumStr(_CALC_DEBUG).xhex().zpad(8) << endl;
        }
    }
    else if( 0 != (_CALC_DEBUG = gConfigParms.getValueAsULong(var,0,16)) )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << (char*)CtiNumStr(_CALC_DEBUG).xhex().zpad(8) << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }
}
