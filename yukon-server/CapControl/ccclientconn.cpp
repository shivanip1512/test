/*-----------------------------------------------------------------------------
    Filename:  ccclientconn.cpp
                
    Programmer:  Josh Wolberg
    
    Description: Source file for CtiCCClientConnection
        
    Initial Date:  9/04/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "ccclientconn.h"
#include "ccmessage.h"
#include "ccexecutor.h"
#include "ccsubstationbusstore.h"
#include "ccserver.h"
#include "ctibase.h"
#include "logger.h"

extern BOOL _CC_DEBUG;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientConnection::CtiCCClientConnection(RWPortal portal) : _valid(TRUE), _portal(new RWPortal(portal) ), _queue( new CtiCountedPCPtrQueue<RWCollectable> )
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - New Client Connection." << endl;
    }

    try
    {
        sinbuf  = new RWPortalStreambuf(*_portal);
        soubuf  = new RWPortalStreambuf(*_portal);
        oStream = new RWpostream(soubuf);
        iStream = new RWpistream(sinbuf);

        RWThreadFunction send_thr = rwMakeThreadFunction(*this, &CtiCCClientConnection::_sendthr);
        RWThreadFunction recv_thr = rwMakeThreadFunction(*this, &CtiCCClientConnection::_recvthr);

        _sendrunnable = send_thr;
        _recvrunnable = recv_thr;

        send_thr.start();
        recv_thr.start();
    } catch (RWxmsg& msg)
    {
        _valid = FALSE;
    }
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCClientConnection::~CtiCCClientConnection()
{
    //if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Client Connection Closing." << endl;
    }
    close();
}

/*---------------------------------------------------------------------------
    isValid
    
    Returns TRUE is the connection is valid, FALSE otherwise
---------------------------------------------------------------------------*/
BOOL CtiCCClientConnection::isValid() const
{
    return _valid;
}

/*---------------------------------------------------------------------------
    close
    
    Closes the connection
---------------------------------------------------------------------------*/
void CtiCCClientConnection::close()
{
    //do not try to close again
    if( _valid == FALSE )
        return;

    _valid = FALSE;
                                                  
    delete sinbuf;
    delete soubuf;
    delete oStream;
    delete iStream;

    delete _portal;

    _recvrunnable.requestCancellation();
    _sendrunnable.requestCancellation();

    _recvrunnable.join();
    _sendrunnable.join();

    RWCollectable* c;

    while ( _queue->tryRead( c ) )
    {
        delete c;
    }

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Client Connection closed." << endl;
    }
}

/*---------------------------------------------------------------------------
    update
    
    Inherited from CtiObserver - called when an observable that self is 
    registered with is updated.
---------------------------------------------------------------------------*/
void CtiCCClientConnection::update(CtiObservable& observable)
{
    CtiMessage* ctiMessage = ((CtiCCServer&)observable).getBroadcastMessage();

    _queue->write( (RWCollectable*) ctiMessage->replicateMessage() );
}

/*---------------------------------------------------------------------------
    _sendthr
    
    Handles putting instances of RWCollectable found in the queue onto the
    output stream.
---------------------------------------------------------------------------*/    
void CtiCCClientConnection::_sendthr()
{
    RWCollectable* c;

    try
    {     
        do
        {
            rwRunnable().serviceCancellation();

            RWWaitStatus status = _queue->read(c, 50);

            if ( status == RW_THR_TIMEOUT  )
            {
                //_ostrm << beat;
                //_ostrm.flush();
            } else
            {
                *oStream << c;
                oStream->vflush();
                delete c;
            }
        }
        while ( isValid() && oStream->good() );
    }
    catch(RWCancellation& )
    {
        throw;
    }
    catch(RWxmsg& msg)
    {
        /*{    
            RWMutexLock::LockGuard guard(coutMux);
            cout << "CtiCCClientConnection::_sendthr - " << msg.why() << endl;
        }*/
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    _valid = FALSE;

    /*{    
        RWMutexLock::LockGuard guard(coutMux);
        cout << RWTime()  << "exiting _sendthr - conn:  " << this << endl;
    }*/
}


/*---------------------------------------------------------------------------
    _recvthr
    
    Receives RWCollectables which must also be cast to CtiCommandable and
    executes them.
---------------------------------------------------------------------------*/   
void CtiCCClientConnection::_recvthr()
{
    RWRunnable runnable;

    RWCollectable* current = NULL;
    CtiCCExecutor* saved = NULL;

    CtiCCExecutorFactory factory;

    try
    {
        rwRunnable().serviceCancellation();
        
        do
        {
            //cout << RWTime()  << "waiting to receive - thr:  " << rwThreadId() << endl;
            RWCollectable* c = NULL;

            *iStream >> current;

            if ( current != NULL )
            {

                try
                {
                    if ( saved != NULL )
                    {
                        runnable.requestCancellation();
                        delete saved;
                        saved = NULL;
                    }
                } catch ( RWxmsg& msg )
                {
                    /*{    
                        RWMutexLock::LockGuard guard(coutMux);
                        cout << "CtiCCClientConnection::_recvthr - " << msg.why() << endl;
                    }*/
                }

                CtiCCExecutor* executor = factory.createExecutor( (CtiMessage*) current );

                RWThreadFunction thr_func  = rwMakeThreadFunction( *executor, &CtiCCExecutor::Execute, _queue );

                runnable = thr_func;

                thr_func.start();

                saved = executor;
            } else
            {
                /*{    
                    RWMutexLock::LockGuard guard(coutMux);
                    cout << RWTime()  << "waiting for thread and then exiting in_thr" << endl;
                }*/

                if ( saved != NULL )
                {
                    if( runnable.isValid() )
                        runnable.requestCancellation();

                    delete saved;
                    saved = NULL;
                }

                _valid = FALSE;
            }

        }
        while ( isValid()  && iStream->good() );
    }
    catch(RWCancellation& )
    {
        throw;
    }
    catch(RWxmsg& msg)
    {
        /*{    
            RWMutexLock::LockGuard guard(coutMux);
            cout << "CtiCCClientConnection::_recvthr - " << msg.why() << endl;
        }*/
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    _valid = FALSE;

    /*{    
        RWMutexLock::LockGuard guard(coutMux);
        cout << RWTime()  << "exiting _recvthr - conn:  " <<  this << endl;
    }*/
}
