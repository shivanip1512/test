

/*-----------------------------------------------------------------------------*
*
* File:   connection
*
* Date:   5/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/connection.cpp-arc  $
* REVISION     :  $Revision: 1.28 $
* DATE         :  $Date: 2004/10/08 20:39:25 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#include <windows.h>
#include <limits.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/toolpro/neterr.h>
#include <rw\thr\mutex.h>


#include "collectable.h"
#include "connection.h"
#include "message.h"
#include "numstr.h"
#include "dlldefs.h"
#include "yukon.h"

#include "logger.h"

CtiConnection::~CtiConnection()
{
    try
    {
        cleanConnection();
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error cleaning the outbound queue for connection " << who() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiConnection::cleanConnection()
{
    ShutdownConnection();

    if( _connectCalled && _localQueueAlloc && inQueue != NULL )
    {
        delete inQueue;
        inQueue = 0;
    }

    if(_regMsg != NULL)
    {
        delete _regMsg;
        _regMsg = 0;
    }

    if(_ptRegMsg != NULL)
    {
        delete _ptRegMsg;
        _ptRegMsg = 0;
    }

    try
    {
        outQueue.clearAndDestroy();
    }
    catch( RWxmsg &e )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error cleaning the outbound queue for connection " << who() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << e.why() << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error cleaning the outbound queue for connection " << who() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

int CtiConnection::WriteConnQue(CtiMessage *QEnt, unsigned timeout, bool cleaniftimedout)
{
    int status = NORMAL;

    int verify;

    if(outQueue.isFull())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << "OutThread  : " << who() << " queue is full.  Will BLOCK " << endl;
        }
    }

    if( (verify = verifyConnection()) != NORMAL )
    {
        status = verify;     // don't want to reset it unless there is a real problem.
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Connection error (" << status << "), message was NOT able to be queued to " << who() << "." << endl;
        }

        // autopsy( __FILE__, __LINE__ );

        delete QEnt;
    }
    else
    {
        if(timeout > 0)
        {
            if( !outQueue.putQueue(QEnt, timeout) )
            {
                // WAS NOT QUEUED!!!
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Message was NOT able to be queued to " << who() << " within " << timeout << " millis" << endl;
                }

                if(cleaniftimedout) delete QEnt;
                status = QUEUE_WRITE;
            }
        }
        else
        {
            outQueue.putQueue(QEnt);
        }
    }

    return status;
}

CtiMessage* CtiConnection::ReadConnQue(UINT Timeout)
{
    CtiMessage *Msg = 0;

    if(inQueue)
    {
        Msg = inQueue->getQueue(Timeout);
        if( Msg != NULL )
        {
            Msg->setConnectionHandle((void*)this);
        }
    }
    else if(Timeout)
    {
        Sleep(250);     // This prevents a crazy tight loop!
    }

    return Msg;
}

int CtiConnection::ThreadInitiate()
{
    try
    {
        outthread_  = rwMakeThreadFunction(*this, &CtiConnection::OutThread);
        outthread_.start();

        inthread_   = rwMakeThreadFunction(*this, &CtiConnection::InThread);
        inthread_.start();

        INT stat = 0;

        if( (stat = verifyConnection()) != NORMAL )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Connection has error status " << stat << endl;
            }
        }
    }
    catch(const RWxmsg& x)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
    }

    return 0;
}


/*----------------------------------------------------------------------------*
 * This thread function manages the inbound side of a RWPortal which has been
 * set up to accept objects. objects MUST be CtiMessages...
 *
 *----------------------------------------------------------------------------*/
void CtiConnection::InThread()
{
    int           nRet = 0;

    CtiMessage    *MsgPtr;

    RWCollectable *c;
    RWTime        NowTime;

    if(getDebugLevel() & 0x00001000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if(_port >= 0)
        {
            dout << RWTime() << " InThread  : " << who() << " has begun operations " << endl;
        }
        else
        {
            dout << RWTime() << " InThread  : " << who() << " has begun operations " << " Server Connection" << endl;
        }
    }

    // SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    for(;!_bQuit;)
    {
        try
        {
            establishConnection( 15 );    // blocks until connected, marked for dontReconnect, or canceled.  Connect attempts made every 15 seconds

            if( _valid )
            {
                try
                {
                    Ex->In() >> c;                // NOTE: Memory is heaped by the RWCollectable class here
                }
                catch(RWSockErr& msg )
                {
                    ManageSocketError(msg);
                }
                catch( RWxmsg &e)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << e.why() << endl;
                    }
                    _bQuit = TRUE;
                    continue;
                }
                catch( ... )
                {
                    _bQuit = TRUE;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Terminating Connection with: " << who() << endl;
                    }
                    continue;
                }

                NowTime = NowTime.now();

                if( c == rwnil )                 // What happened here? No exception, but no message either....
                {
                    // Assume the connection has expired....
                    checkCancellation();

                    if(_serverConnection)
                    {
                        if(getDebugLevel() & 0x00001000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Received a null message from " << who() <<", shutting down the connection." << endl;
                        }
                        _bQuit = TRUE;
                    }
                    else
                    {
                        if(getDebugLevel() & 0x00001000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "**** Resetting the connection **** " << who() << endl;
                        }
                        cleanExchange();      // Make us prepare for reconnect or die trying...
                    }

                    continue;
                }

                checkCancellation();

                try
                {
                    if( c != NULL)
                    {
                        MsgPtr = (CtiMessage*)c;

                        MsgPtr->setConnectionHandle( (void*)this );   // Pee on this message to mark some teritory...

                        if(inQueue)
                        {
                            if(inQueue->isFull())
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << NowTime << " InThread  : " << who() << " queue is full.  Will BLOCK. It allows " << (INT)inQueue->size() << " entries" << endl;
                                }
                            }

                            _lastInQueueWrite = _lastInQueueWrite.now();    // Refresh the time...

                            inQueue->putQueue(MsgPtr);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else if( _dontReconnect || _serverConnection)        // This is really !_valid && _dontReconnect OK Joe...
            {
                _bQuit = TRUE;
                Sleep(500);       // No runnaway loops ok...
            }
        }
        catch(RWSockErr& msg )
        {
            ManageSocketError(msg);
        }
        catch(const RWxmsg& x)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
            }
            _bQuit = TRUE;
            break;
        }
        catch( ... )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << who() << endl;
            }
            _bQuit = TRUE;
            break;
        }
    }

    if((getDebugLevel() & 0x00001000))
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if(_port >= 0)
        {
            dout << NowTime << " InThread  : " << who() << " is terminating...." << endl;
        }
        else
        {
            dout << NowTime << " InThread  : " << who() << " is terminating...." << endl;
        }
    }

    forceTermination();
    return;
}

void CtiConnection::OutThread()
{
    int               verify;
    int               nRet        = NoError;
    CtiMessage        *MyMsg      = NULL;

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_HIGHEST);

    Sleep(1000);         // Let InThread start up the connection.

    if(getDebugLevel() & 0x00001000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if(_port >= 0)
        {
            dout << RWTime() << " OutThread : " << who() << " has begun operations " << endl;
        }
        else
        {
            dout << RWTime() << " OutThread : " << who() << " has begun operations " << " Server Connection" << endl;
        }
    }

    try
    {
        for(;!_bQuit;)
        {
            if( waitForConnect() ) continue;

            try
            {
                MyMsg = outQueue.getQueue(1000);
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << who() << endl;
                }
            }

            if(MyMsg == NULL)      // I need to look for a CANCELATION every second
            {
                checkCancellation();

                if( _dontReconnect )
                {
                    _bQuit = TRUE;
                }

                continue;   // No cancellation request here.
            }
            else     // MyMsg is a non-null pointer
            {
                try
                {
                    if(!_valid && _serverConnection)
                    {
                        _bQuit = TRUE;

                        Sleep(500);       // No runnaway loops ok...

                        if( MyMsg )
                        {
                            delete MyMsg;
                            MyMsg = NULL;
                        }

                        continue; // the for loop.
                    }

                    waitForConnect();

                    if( _valid )
                    {
                        Out() << *MyMsg;
                        Out().vflush();

                        messagePeek( MyMsg );

                        if( MyMsg ) // Clean up the memory, after all, it just went out the door...
                        {
                            delete MyMsg;
                            MyMsg = NULL;
                        }
                    }
                }
                catch( RWSockErr& msg )
                {
                    ManageSocketError( msg );

                    // Clean up the memory, after all, it just went out the door...
                    if( MyMsg != NULL )
                    {
                        delete MyMsg;
                        MyMsg = NULL;
                    }
                }
                catch( RWxmsg &e)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << e.why() << endl;
                    }
                    _bQuit = TRUE;
                    continue;
                }
                catch( ... )
                {
                    if(!_bQuit)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Attempting a connection reset with " << who() << endl;
                        }
                        cleanExchange();
                    }
                    else if(_bQuit)
                    {
                        size_t num = outQueue.entries();
                        if(num)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << " OutThread terminating with " << num << " entries" << endl;
                            }
                            outQueue.clearAndDestroy();      // Get rid of the evidence... I'm going away!
                        }
                    }

                    continue;
                }
            }
        }
    }
    catch(RWxmsg& msg )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        // Let the higher powers handle this crap!
        _valid = FALSE;
        throw;
    }

    forceTermination();

    if( MyMsg )
    {
        delete MyMsg;
        MyMsg = NULL;
    }

    if(getDebugLevel() & 0x00001000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if( _port >= 0 )
        {
            dout << RWTime() << " OutThread : " << who() << " is terminating...." << endl;
        }
        else
        {
            dout << RWTime() << " OutThread : " << who() << " Server Connection is terminating...." << endl;
        }
    }
}



INT CtiConnection::ManageSocketError( RWSockErr& msg )
{
    INT nRet = msg.errorNumber();

    switch( msg.errorNumber() )
    {
    case RWNETECONNREFUSED:
        {
            _valid = FALSE;
            break;
        }
    case RWNETECONNABORTED:
        {
            _valid = FALSE;
            break;
        }
    case RWNETECONNRESET:
        {
            _valid = FALSE;
            break;
        }
    case RWNETEINTR:
    case RWNETEBADF:
    case RWNETEACCES:
    case RWNETEFAULT:
    case RWNETEINVAL:
    case RWNETEMFILE:
    case RWNETEWOULDBLOCK:
    case RWNETEINPROGRESS:
    case RWNETEALREADY:
    case RWNETENOTSOCK:
    case RWNETEDESTADDRREQ:
    case RWNETEMSGSIZE:
    case RWNETEPROTOTYPE:
    case RWNETENOPROTOOPT:
    case RWNETEPROTONOSUPPORT:
    case RWNETESOCKTNOSUPPORT:
    case RWNETEOPNOTSUPP:
    case RWNETEPFNOSUPPORT:
    case RWNETEAFNOSUPPORT:
    case RWNETEADDRINUSE:
    case RWNETEADDRNOTAVAIL:
    case RWNETENETDOWN:
    case RWNETENETUNREACH:
    case RWNETENETRESET:
    case RWNETENOBUFS:
    case RWNETEISCONN:
    case RWNETENOTCONN:
    case RWNETESHUTDOWN:
    case RWNETETOOMANYREFS:
    case RWNETETIMEDOUT:
    case RWNETELOOP:
    case RWNETENAMETOOLONG:
    case RWNETEHOSTDOWN:
    case RWNETEHOSTUNREACH:
    case RWNETENOTEMPTY:
    case RWNETEPROCLIM:
    case RWNETEUSERS:
    case RWNETEDQUOT:
    case RWNETESTALE:
    case RWNETEREMOTE:
        {
#if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << msg.errorNumber() << " " << msg.why() << endl;
            }
#endif
            _valid = FALSE;
            break;
        }
    default:
        {
            break;
        }
    }

    return nRet;
}

INT CtiConnection::ConnectPortal()
{
    INT  nRet = NoError;
    INT  i;

    /*
     *  Don't do anything if the connection is already valid.
     */
    if(!_valid)
    {
        try
        {
            /*
             *  Servers never re-connect.
             *  _dontReconnect means someone is shutting us down, so this thread should go away.
             */
            if( !_serverConnection && !_dontReconnect )
            {
                RWSocketPortal psck(RWInetAddr(_port, _host));

                if(!psck.socket().valid())
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Socket Error " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = -1;
                }
                else
                {
                    cleanExchange();
                    Ex = CTIDBG_new CtiExchange(psck);

                    if(!Ex->In().bad() && !Ex->Out().bad())
                    {
                        _valid = TRUE;

                        if( _regMsg != NULL ) // I know who I am....
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " InThread  : " << who() << " re-registering connection " << endl;
                            }

                            WriteConnQue( _regMsg->replicateMessage() );

                            if( _ptRegMsg != NULL ) // I know who I am....
                            {
                                WriteConnQue( _ptRegMsg->replicateMessage() );
                            }
                        }
                    }
                }
            }
        }
        catch( RWSockErr& msg )
        {
            // dout << "ALERT ALERT ALERT " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = ManageSocketError( msg );
            cleanExchange();
        }
    }

    return nRet;
}

void CtiConnection::ShutdownConnection()
{
    LockGuard guard(monitor());
    try
    {
        if(_connectCalled)
        {
            // 1.  We want the outQueue/OutThread to flush itself.
            //     I allow _termTime seconds for this to occur

            int i = 0;

            while(outQueue.entries() > 0 && _valid)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Waiting for outbound Queue " << who() << " to flush " << outQueue.entries() << " entries" << endl;
                }

                Sleep(1000);

                if(i++ > _termTime)
                {
                    outQueue.clearAndDestroy();      // Get rid of the evidence...
                    break;
                }
            }

            // This flag tells InThread not to wait for a reconnection....
            _dontReconnect = TRUE;

            cleanExchange();

            // Should check the cancellation once per second.
            if( outthread_.join(100) == RW_THR_TIMEOUT )
            {
                if( outthread_.requestCancellation(2000)  == RW_THR_TIMEOUT )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "OutThread refuses to cancel after 2 seconds. " << who() << endl;
                }
                if(outthread_.join(2000) == RW_THR_TIMEOUT)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "OutThread refuses to join   after 2 seconds. " << who() << endl;
                    }
                    outthread_.terminate();
                }
            }

            if( inQueue && inQueue->entries() != 0 )
            {
                inQueue->clearAndDestroy();      // Get rid of the evidence...
            }

            if( inthread_.join(100) == RW_THR_TIMEOUT )
            {
                if( inthread_.requestCancellation(2000)  == RW_THR_TIMEOUT )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "InThread refuses to close after 2 seconds. " << who() << endl;
                }
                if(inthread_.join(2000) == RW_THR_TIMEOUT)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "InThread refuses to join  after 2 seconds. " << who() << endl;
                    }
                    inthread_.terminate();
                }
            }

            if(getDebugLevel() & 0x00001000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                if( _port >= 0 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " ShutdownConnection() " << who() << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " ShutdownConnection() " << who() << endl;
                }
            }
        }
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

}

void CtiConnection::ResetConnection()
{
    if(getDebugLevel() & 0x00001000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Resetting the connection **** " << who() << endl;
    }

    cleanExchange();
}

INT CtiConnection::verifyConnection()
{
    INT ok = NORMAL;
    INT status;

    try
    {
        if( (status = inthread_.getCompletionState())  != RW_THR_PENDING )
        {
            if(getDebugLevel() & 0x00001000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " InThread " << who() << " has exited with a completion state of " << status;
                if(!_serverConnection)
                {
                    dout << ". May restart";
                }

                dout << endl;
            }

            if(!_serverConnection)
            {
                if( inthread_.start() != RW_THR_PENDING )
                {
                    ok = InThreadTerminated; // the inthread has exited!
                }
            }
            else
            {
                ok = InThreadTerminated; // the inthread has exited!
            }
        }
        else if( (status = outthread_.getCompletionState()) != RW_THR_PENDING )
        {
            if(getDebugLevel() & 0x00001000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " OutThread " << who() << " has exited with a completion state of " << status;

                if(!_serverConnection)
                {
                    dout << ". May restart";
                }

                dout << endl;
            }

            if(!_serverConnection)
            {
                if( outthread_.start() != RW_THR_PENDING )
                {
                    ok = OutThreadTerminated; // the outthread has exited!
                }
            }
            else
            {
                ok = OutThreadTerminated; // the inthread has exited!
            }

            if(ok == OutThreadTerminated)
            {
                outQueue.clearAndDestroy();
            }
        }
        else
        {
            TryLockGuard guard(monitor());

            if(guard.isAcquired() && Ex != NULL)
            {
                if( Ex->In().bad() )
                {
                    ok = InboundSocketBad; // the stream indicates a bad condition.
                }
                else if( Ex->Out().bad() )
                {
                    ok = OutboundSocketBad; // the stream indicates a bad condition.
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return ok;
}

/*
 * This method blocks until
 *  1. connected to the remote (valid),
 *  2. or marked as a dontReconnect (=1),
 *  3. or until a cancellation request occurs
 *
 *  he tries to connect to the other side every freq seconds (def. arg. 15 sec)
 */
INT CtiConnection::establishConnection(INT freq)
{
    INT status = NORMAL;

    INT sleepCount = 0;
    INT SpinCount = 0;

    while( !_dontReconnect && !_valid )
    {
        /*
         *  OK, this guy lets us call ConnectPortal every % XX sexonds.
         *  Each loop iteration (and ServiceCancellation) should happen every second then
         */
        if( !(SpinCount++ % freq) )
        {
            /*************************
            * added because of turnaround time observed on Progress Energy's system
            **************************
            */
            Sleep(2000);

            if(!ConnectPortal())
            {
                break;         // the while.  because we connected correctly.
            }
        }

        if( !(++sleepCount % 60) && (getDebugLevel() & 0x00001000) )      // once per minute....
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " InThread  : " << who() << " connection is not valid. " << endl;
        }

        checkCancellation();

        Sleep(1000);         // Don't pound the system....
    }

    return status;
}

void CtiConnection::forceTermination()
{
    _bQuit         = TRUE;
    _dontReconnect = TRUE;
    _valid         = FALSE;
    _noLongerViable = TRUE;
    return;
}

INT CtiConnection::waitForConnect()
{
    INT status = NORMAL;
    int waitCount   = 0;

    while( !_valid )       /* We loop here until the connection goes valid... */
    {
        if((getDebugLevel() & 0x00001000) && !(++waitCount % 60))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " OutThread : " << who() << " connection is not valid. " << endl;
        }

        // Sleep for about 1 second while looking for a cancellation.
        status = checkCancellation(1000);

        /*
         *  We have just been placed in the shutdown condition.....
         */
        if( _dontReconnect )
        {
            forceTermination();
            status = NOTNORMAL;
            break;
        }
    }

    return status;
}

INT CtiConnection::checkCancellation(INT mssleep)
{
    INT status = NORMAL;

    try
    {
        if(mssleep > 0)
        {
            rwSleep(mssleep);
        }
        rwServiceCancellation( );
    }
    catch(const RWCancellation& cMsg)
    {
        if(getDebugLevel() & 0x00001000)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Connection Thread : " << who() << " canceled " << endl;
        }

        status = NOTNORMAL;
        forceTermination();
        // throw;  // 062800 CGP Unknown whether I care to re-throw these.
    }

    return status;
}

const RWTime& CtiConnection::getLastReceiptTime() const
{
    return _lastInQueueWrite;
}


void CtiConnection::recordRegistration( CtiMessage *msg )
{
    if(_regMsg != NULL)
    {
        delete _regMsg;
    }

    _regMsg = (CtiRegistrationMsg*)msg->replicateMessage();
}

void CtiConnection::recordPointRegistration( CtiMessage *msg )
{
    if(_ptRegMsg != NULL)
    {
        delete _ptRegMsg;
    }

    _ptRegMsg = (CtiPointRegistrationMsg*)msg->replicateMessage();
}

// This method examines the message and records anything we care about
void CtiConnection::messagePeek( CtiMessage *MyMsg )
{

    if(MyMsg->isA() == MSG_REGISTER)
    {
        recordRegistration( MyMsg );
    }
    else if(MyMsg->isA() == MSG_POINTREGISTRATION)
    {
        recordPointRegistration( MyMsg );
    }
    else if(MyMsg->isA() == MSG_MULTI)
    {
        int msgtype;
        CtiMultiMsg *pMulti = (CtiMultiMsg *)MyMsg;

        for(int i = 0; i < pMulti->getCount() && i < 3; i++)                // Only look at the first three entries
        {
            messagePeek(((CtiMessage*)(pMulti->getData()[i])));             // recurse.
        }
    }
    else if(MyMsg->isA() > 0x8000 || MyMsg->isA() < 1510)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "  ODD MESSAGE TYPE " << MyMsg->isA() << endl;
    }

    return;
}

RWCString CtiConnection::who()
{
    RWCString connectedto(_name);

    TryLockGuard guard(monitor());

    if(_port == -2 && guard.isAcquired() && Ex != NULL)
    {
        connectedto += (_name.isNull() ? "" : " / " ) + getPeer().id();
    }
    else
    {
        connectedto += (_name.isNull() ? "" : " / " ) + _host + " / " + CtiNumStr(_port);
    }

    return connectedto;
}

RWCString   CtiConnection::getName() const
{
    return _name;
}

CtiConnection& CtiConnection::setName(const RWCString &str)
{
    _name = str;

    outQueue.setName( str );
    if(inQueue != NULL) inQueue->setName( str );

    return *this;
}

int CtiConnection::outQueueCount() const
{
    return(int)outQueue.entries();
}

CtiConnection::CtiConnection( ) :
_regMsg(NULL),
_ptRegMsg(NULL),
_termTime(3),
Ex(NULL),
_flag(0),
inQueue(NULL),
_port(-1L),
_host("127.0.0.1")
{
}

CtiConnection::CtiConnection( const INT &Port, const RWCString &Host, InQ_t *inQ, INT tt) :
_regMsg(NULL),
_ptRegMsg(NULL),
_termTime(tt),
Ex(NULL),
inQueue(inQ),
_flag(0)
{
    doConnect(Port, Host, inQ);
}

CtiConnection::CtiConnection(CtiExchange *xchg, InQ_t *inQ, INT tt) :
_regMsg(NULL),
_ptRegMsg(NULL),
_termTime(tt),
Ex(xchg),
inQueue(inQ),
_flag(0),
_port(-2),
_host("127.0.0.1")
{
    // Connection exists from the server's listener!!!!
    _connectCalled    = TRUE;
    _valid            = TRUE;  // This should be the only Truth this guy gets!
    _serverConnection = TRUE;
}

void CtiConnection::doConnect( const INT &Port, const RWCString &Host, InQ_t *inQ)
{
    if(!_connectCalled)
    {
        if(inQ != NULL && inQueue != NULL)
        {
            delete inQueue;
        }
        inQueue = inQ;

        if(inQueue == NULL)
        {
            inQueue = CTIDBG_new InQ_t;
            _localQueueAlloc = TRUE;
        }

        _port = Port;
        _host = Host;

        ThreadInitiate();
        _connectCalled = TRUE;
    }

    return;
}

void CtiConnection::restartConnection( )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "  UNTESTED CODE HERE " << endl;
    }

    if(_connectCalled)
    {
        cleanConnection();

        // Reset the stuff..
        _flag = 0;

        doConnect(_port, _host);
    }

    return;
}

RWBoolean CtiConnection::operator==(const CtiConnection& aRef) const
{
    return(this == &aRef);
}

unsigned CtiConnection::hash(const CtiConnection& aRef)
{
    return(unsigned)&aRef;            // The address of the Object?
}

RWpistream& CtiConnection::In()                                { return Ex->In();}
RWpostream& CtiConnection::Out()                               { return Ex->Out();}
RWInetHost  CtiConnection::getPeer() const                     { return Ex->getPeer();}

void CtiConnection::ThreadTerminate()
{
    // Should check the cancellation once per second.
    if( outthread_.join(100) == RW_THR_TIMEOUT )
    {
        outthread_.requestCancellation();
        outthread_.join(5000);
    }
}

CtiQueue<CtiMessage, less<CtiMessage> > & CtiConnection::getOutQueueHandle()  { return outQueue;}
CtiQueue<CtiMessage, less<CtiMessage> > & CtiConnection::getInQueueHandle()   { return *inQueue;}

BOOL CtiConnection::isViable() const
{
    return
    !_noLongerViable;
}

UINT CtiConnection::valid() const
{
    return
    _valid;
}

void CtiConnection::cleanExchange()
{
    // LockGuard guard(monitor());

    if(Ex != NULL)
    {
        _valid = FALSE;
        delete Ex;
        Ex = NULL;
    }
}
