/*-----------------------------------------------------------------------------
    Filename:  clientconn.cpp
                
    Programmer:  Josh Wolberg
    
    Description: Source file for CtiCCConnection
        
    Initial Date:  8/15/2000
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/

#include "clientconn.h"
#include "ccmessage.h"
#include "executor.h"
#include "strategystore.h"
#include "capcontrol.h"
#include "ctibase.h"
#include "logger.h"

extern BOOL _CAP_DEBUG;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCConnection::CtiCCConnection(RWPortal portal) : _valid(TRUE), _portal(new RWPortal(portal) ), _queue( new CtiCountedPCPtrQueue<RWCollectable> )
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "New Client Connection." << endl;
    }

    try
    {
        sinbuf  = new RWPortalStreambuf(*_portal);
        soubuf  = new RWPortalStreambuf(*_portal);
        oStream = new RWpostream(soubuf);
        iStream = new RWpistream(sinbuf);

        RWThreadFunction send_thr = rwMakeThreadFunction(*this, &CtiCCConnection::_sendthr);
        RWThreadFunction recv_thr = rwMakeThreadFunction(*this, &CtiCCConnection::_recvthr);

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
CtiCCConnection::~CtiCCConnection()
{
    if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Client Connection Closing." << endl;
    }
    close();
}

/*---------------------------------------------------------------------------
    isValid
    
    Returns TRUE is the connection is valid, FALSE otherwise
---------------------------------------------------------------------------*/
bool CtiCCConnection::isValid() const
{
    return _valid;
}

/*---------------------------------------------------------------------------
    close
    
    Closes the connection
---------------------------------------------------------------------------*/
void CtiCCConnection::close()
{
    //do not try to close again
    if( _valid == false )
        return;

    _valid = false;
                                                  
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
        dout << RWTime().asString() << " - " << "Client Connection closed." << endl;
    }
}

/*---------------------------------------------------------------------------
    update
    
    Inherited from CtiObserver - called when an observable that self is 
    registered with is updated.
---------------------------------------------------------------------------*/
void CtiCCConnection::update(CtiObservable& observable)
{
    CtiMessage* ctiMessage = ((CtiCCServer&)observable).BroadcastMessage();

    _queue->write( (RWCollectable*) ctiMessage->replicateMessage() );
}

/*---------------------------------------------------------------------------
    _sendthr
    
    Handles putting instances of RWCollectable found in the queue onto the
    output stream.
---------------------------------------------------------------------------*/    
void CtiCCConnection::_sendthr()
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
    } catch ( RWxmsg& msg )
    {
        /*{    
            RWMutexLock::LockGuard guard(coutMux);
            cout << "CtiCCConnection::_sendthr - " << msg.why() << endl;
        }*/
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
void CtiCCConnection::_recvthr()
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
                        cout << "CtiCCConnection::_recvthr - " << msg.why() << endl;
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
    } catch ( RWxmsg& msg )
    {
        /*{    
            RWMutexLock::LockGuard guard(coutMux);
            cout << "CtiCCClientConnection::_recvthr - " << msg.why() << endl;
        }*/
    }

    _valid = FALSE;

    /*{    
        RWMutexLock::LockGuard guard(coutMux);
        cout << RWTime()  << "exiting _recvthr - conn:  " <<  this << endl;
    }*/
}
