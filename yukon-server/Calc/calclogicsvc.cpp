#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

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
#include "message.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_dbchg.h"
#include "pointtypes.h"
#include "configparms.h"
#include "logger.h"

#include "calclogicsvc.h"

#define CALCVERSION         "1.40"
#define CHECK_RATE_SECONDS  60     // one minute check for db change

BOOL UserQuit = FALSE;

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
            return TRUE;
    }
}

IMPLEMENT_SERVICE(CtiCalcLogicService, CALCLOGIC)


CtiCalcLogicService::CtiCalcLogicService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType ) :
    CService( szName, szDisplay, dwType ), _ok(TRUE), _dbChange(FALSE)
{
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
    char temp[180];

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime( ) << " - CtiCalcLogicService::Init..." << endl;
    }


    HINSTANCE hLib = LoadLibrary( "cparms.dll" );

    if (hLib)
    {
        CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

        if( (*fpGetAsString)( "DISPATCH_MACHINE", temp, 180 ) )
        {
            _dispatchMachine = temp;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " DISPATCH_MACHINE not defined, defaulting to local 127.0.0.1 " << endl;
            }
            _dispatchMachine = RWCString ("127.0.0.1");
        }
        
        if( (*fpGetAsString)( "DISPATCH_PORT", temp, 180 ) )
        {
            _dispatchPort = atoi(temp);
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " DISPATCH_PORT not defined, defaulting to " << VANGOGHNEXUS << endl;
            }
            _dispatchPort = VANGOGHNEXUS;
        }
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
    CtiPointRegistrationMsg *msgPtReg;
    CtiMultiMsg *msgMulti;

    time_t   timeNow;
    time_t   nextCheckTime;


    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory.data());
    dout.setOutputFile("calc");

    if (_running_in_console)
    {
        dout.setToStdOut(true);
        dout.setWriteInterval(0);
    }
    else
    {
        dout.setToStdOut(false);
        dout.setWriteInterval(5000);
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if (_running_in_console)
            dout << RWTime( ) << " -  - Calc and Logic Version: " << CALCVERSION << " starting console mode ..." << endl;
        else
            dout << RWTime( ) << " -  - Calc and Logic Service Version: " << CALCVERSION << " starting ..." << endl;

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

        _conxion = new CtiConnection(_dispatchPort, _dispatchMachine);

        SetStatus(SERVICE_START_PENDING, 66, 5000 );

        //  write the registration message (this is only done once, because if the database changes,
        //    the program name and such doesn't change - only our requested points do.)

        // USE A SINGLE Simple Name - bdw
        RWCString regStr = "CalcLogic";
        _conxion->WriteConnQue( new CtiRegistrationMsg(regStr, rwThreadId( ), TRUE) );

        // set service as running
        SetStatus(SERVICE_RUNNING, 0, 0,
                  SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

        while( !UserQuit )
        {
            calcThread = new CtiCalculateThread;
            readCalcPoints( calcThread );

            //  validate the connection before we attempt to send anything across
            //    (note that the above database read delays this check long enough to allow the
            //     connection to complete.)
            //  FIX_ME:  This became broken when I ported this to be a a service.  It has something
            //             to do with threads.  It makes an ASSERTion fail in RW code.
            if( !_conxion->valid( ) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Calc could not establish a connection to Dispatch" << endl;
                }
                Sleep(30000);   // sleep for 30 seconds

                // try it again
                delete calcThread;

                continue;
            }

            //  iterate through the calc points' dependencies, adding them to the registration message
            //  XXX:  Possibly add the iterator and accessor functions to the calcThread class itself, rather than
            //          providing the iterator directly?
            RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> > *depIter;

            depIter = calcThread->getPointDependencyIterator( );
            msgPtReg = new CtiPointRegistrationMsg(0);
            for( ; (*depIter)( ); )
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " - Registered for point id: " << ((CtiPointStoreElement *)depIter->value( ))->getPointNum() << endl;
                }*/
                msgPtReg->insert( ((CtiPointStoreElement *)depIter->value( ))->getPointNum( ) );
            }
            delete depIter;

            //  now send off the point registration
            _conxion->WriteConnQue( msgPtReg );

            //  Up until this point, nothing has needed a mutex.  Now I'm spawning threads, and the
            //    commonly-accessed resources will be the calcThread's pointStore object and message
            //    queue (and during debug, the outstreams).  The calcThread object will be fed point
            //    data changes by the input thread.  The output thread will take the messages from the
            //    calcThread message queue and post them to Dispatch.
            RWThreadFunction calcThreadFunc = rwMakeThreadFunction( *calcThread, &CtiCalculateThread::calcLoop );

            calcThreadFunc.start( );

            CtiPointDataMsg     *msgPtData = NULL;

            RWThreadFunction inputFunc  = rwMakeThreadFunction( *this, &CtiCalcLogicService::_inputThread );
            RWThreadFunction outputFunc = rwMakeThreadFunction( *this, &CtiCalcLogicService::_outputThread );
            inputFunc.start( );
            outputFunc.start( );


            // get time now
            time(&nextCheckTime);

            //FIXFIXFIX - move CHECK_RATE_SECONDS to a CParm in the future
            //
            nextCheckTime += CHECK_RATE_SECONDS;    // added some time to it 
            
            _dbChange = FALSE;                      // make sure our flag is reset

            for( ; !UserQuit; )
            {
                rwSleep( 1000 );

                time (&timeNow);
                if( timeNow > nextCheckTime )
                {
                    
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Timer Checking for DB Change." << endl;
                    }
                    
                    
                    // check for DB Changes on a set interval
                    // this makes us a little kinder on re-registrations
                    if(_dbChange)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Calc has recieved DB Change- Reloading." << endl;

                        break; // exit the loop and reload
                    }

                    nextCheckTime = timeNow + CHECK_RATE_SECONDS; 
                }

            } // end for userquit


            //  interrupt the calculation and i/o threads, and tell it to come back home
            inputFunc.requestInterrupt( );
            outputFunc.requestInterrupt( );
            calcThreadFunc.requestInterrupt( );
            inputFunc.releaseInterrupt( );
            outputFunc.releaseInterrupt( );
            calcThreadFunc.releaseInterrupt( );
            inputFunc.join( );
            outputFunc.join( );
            calcThreadFunc.join( );

            //  from this point out, i'm one thread again
            delete calcThread;
        }

        SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

        //  tell Dispatch we're going away, then leave
        _conxion->WriteConnQue( new CtiCommandMsg( CtiCommandMsg::ClientAppShutdown, 15) );
        _conxion->ShutdownConnection();

        SetStatus(SERVICE_STOP_PENDING, 75, 5000 );

        delete _conxion;
    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception in Calc and Logic: ";
        dout << msg.why() << endl;
    }

   // stop dout thread
   dout.interrupt(CtiThread::SHUTDOWN);
   dout.join();

    SetStatus( SERVICE_STOPPED );
}


void CtiCalcLogicService::_outputThread( void )
{
    RWRunnableSelf _pSelf = rwRunnable( );
    RWTPtrDeque<CtiMultiMsg>::size_type entries;
    BOOL interrupted = FALSE;
    CtiMultiMsg *toSend;

    while( !interrupted )
    {
        //  while there's nothing to send
        do
        {
            {
                RWMutexLock::LockGuard outboxGuard(calcThread->outboxMux);
                entries = calcThread->outboxEntries( );
            }
            if( _pSelf.serviceInterrupt( ) )
                interrupted = TRUE;
            else if( !entries )
                _pSelf.sleep( 200 );
        } while( !entries && !interrupted );

        if( !interrupted )
        {
            RWMutexLock::LockGuard outboxGuard(calcThread->outboxMux);
            for( ; calcThread->outboxEntries( ); )
            {
//                RWMutexLock::LockGuard coutGuard(coutMux);
//                cout << "(_outputThread( ) sending message) ";
                toSend = calcThread->getOutboxEntry( );
                _conxion->WriteConnQue( toSend );
            }
        }
    }

}

void CtiCalcLogicService::_inputThread( void )
{
    RWRunnableSelf  _pSelf = rwRunnable( );
    RWCollectable   *incomingMsg;
//    time_t          time_start, time_finish;
    BOOL            interrupted = FALSE;
    //int             numPDataVals;

    while( !interrupted )
    {
        //  while i'm not getting anything
        while( NULL == (incomingMsg = _conxion->ReadConnQue( 200 )) && !interrupted )
        {
            if( _pSelf.serviceInterrupt( ) )
                interrupted = TRUE;
            else
                _pSelf.sleep( 200 );
        }

        //  dump out if we're being called
        if( !interrupted )
        {
            /*{
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " - message received - " << endl;
            }*/

            //time_start = clock( );

            //  common variable, but this is the only place that writes to it, so i think it's okay.
            parseMessage( incomingMsg, calcThread );
                //_dbChange = TRUE;

            //time_finish = clock( );

            //{
            //    RWMutexLock::LockGuard coutGuard(coutMux);
            //    cout << endl;
            //    cout << "took " << (time_finish - time_start) << " ms to post " << numPDataVals << " messages" << endl;
            //}

            delete incomingMsg;   //  Make sure to delete this - its on the heap
        }
    }
}

// return is not used at this time
BOOL CtiCalcLogicService::parseMessage( RWCollectable *message, CtiCalculateThread *calcThread )
{
    CtiMultiMsg *msgMulti;
    CtiPointDataMsg *pData;
    int x;
    
    BOOL retval = TRUE;

    switch( message->isA( ) )
    {
        case MSG_DBCHANGE:
            // only reload on if a database change was made to a point
            if ( ((CtiDBChangeMsg*)message)->getDatabase() == ChangePointDb)
            {
                //((CtiDBChangeMsg*)message)->getId()
                if ( ((CtiDBChangeMsg*)message)->getTypeOfChange() != ChangeTypeAdd)
                {

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime()  << " - Looking update DBChange PointID in Calc List..." << endl;
                    }

                    // Must have been a delete or update
                    if (calcThread->isACalcPointID(((CtiDBChangeMsg*)message)->getId()) == TRUE)
                    {
                        _dbChange = TRUE;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime()  << " - Database change on loaded calc.  Setting reload flag." << endl;
                        }

                    }
                }
                else
                {
                    // always load when a point is added
                    _dbChange = TRUE;

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime()  << " - Database change- Point Added.  Setting reload flag." << endl;
                    }
                }
            }

            break;

        case MSG_COMMAND:
            // we will handle some messages
            switch( ((CtiCommandMsg*)message)->getOperation())
            {
                case (CtiCommandMsg::Shutdown):
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " CalcLogic received a shutdown message from somewhere- Ignoring!!" << endl;
                    }
                    break;

                case (CtiCommandMsg::AreYouThere):
                    // echo back the same message - we are here
                    _conxion->WriteConnQue( new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15) );

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " CalcLogic has been pinged" << endl;
                    }
                    break;

                default:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " CalcLogic received a unknown/don't care Command message- " << endl;
                    }
                    break;

            }
            break;

        case MSG_POINTDATA:
            {
                pData = (CtiPointDataMsg *)message;
                calcThread->pointChange( pData->getId(), pData->getValue(), pData->getTime(), pData->getQuality(), pData->getTags() );
            }
            break;

        case MSG_MULTI:
            // pull all of the messages out
            msgMulti = (CtiMultiMsg *)message;

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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " - Signal Message received for point id: " << ((CtiSignalMsg*)message)->getId() << endl;
            }
            break;

        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " - " << __FILE__ << " (" << __LINE__ << ") Calc_Logic does not know how to handle messages of type \"" << message->stringID( ) << "\";  skipping" << endl;
            }
    }

    return retval;
}


void CtiCalcLogicService::readCalcPoints( CtiCalculateThread *calcThread )
{
    // should be collection but not now
    long pointIdList[1000];
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
        while ( rdr() )
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

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Loaded Calc #" << CalcCount << " Id: " << pointid << " Type: " << updatetype << endl;
            }

            // put the collection in the correct collection based on type
            calcThread->appendPoint( pointid, updatetype, updateinterval );
        }



        for (int i=0; i < CalcCount; i++)
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

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Loading Components for Calc Id: " << pointIdList[i] << endl;
            }

            RWDBReader  componentRdr = componentselector.reader( conn );

            //  iterate through the components
            while ( componentRdr() )
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
            }

        } // end for loop

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception while reading calc points from database: " << msg.why( ) << endl;
        exit( -1 );
    }
}


