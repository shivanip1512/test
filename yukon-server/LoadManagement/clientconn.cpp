/*-----------------------------------------------------------------------------
    Filename:  clientconn.cpp
                
    Programmer:  Josh Wolberg
    
    Description: Source file for CtiLMConnection
        
    Initial Date:  2/7/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "clientconn.h"
#include "lmmessage.h"
#include "executor.h"
#include "lmcontrolareastore.h"
#include "loadmanager.h"
#include "ctibase.h"
#include "logger.h"

extern ULONG _LM_DEBUG;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiLMConnection::CtiLMConnection(RWPortal portal) : _valid(TRUE), _portal(new RWPortal(portal) )
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - New Client Connection, from " << ((RWSocketPortal*)_portal)->socket().getpeername() << endl;
    }

    try
    {
        sinbuf  = new RWPortalStreambuf(*_portal);
        soubuf  = new RWPortalStreambuf(*_portal);
        oStream = new RWpostream(soubuf);
        iStream = new RWpistream(sinbuf);

        RWThreadFunction send_thr = rwMakeThreadFunction(*this, &CtiLMConnection::_sendthr);
        RWThreadFunction recv_thr = rwMakeThreadFunction(*this, &CtiLMConnection::_recvthr);

        _sendrunnable = send_thr;
        _recvrunnable = recv_thr;

        send_thr.start();
        recv_thr.start();
    }
    catch(RWxmsg& msg)
    {
        _valid = FALSE;
    }
    catch(...)
    {
        _valid = FALSE;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMConnection::~CtiLMConnection()
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Client Connection closing." << endl;
    }

    try
    {
        close();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Client Connection closed." << endl;
    }
}

/*---------------------------------------------------------------------------
    isValid
    
    Returns TRUE is the connection is valid, FALSE otherwise
---------------------------------------------------------------------------*/
bool CtiLMConnection::isValid() const
{
    return _valid;
}

/*---------------------------------------------------------------------------
    close
    
    Closes the connection
---------------------------------------------------------------------------*/
void CtiLMConnection::close()
{
    //do not try to close again
    if( _portal == NULL )
        return;

    _valid = FALSE;
    delete sinbuf;
    delete soubuf;
    delete oStream;
    delete iStream;
    delete _portal;

    sinbuf = NULL;
    soubuf = NULL;
    oStream = NULL;
    iStream = NULL;
    _portal = NULL;

    //unblock the in and out thread
    RWCollectable* unblocker = new RWCollectable();
    _queue.write(unblocker);
    _sendrunnable.requestCancellation();
    _recvrunnable.requestCancellation();

    _recvrunnable.join();
    _sendrunnable.join();

    RWCollectable* c;
    _queue.close();
    while( _queue.canRead() )
    {
        c = _queue.read();
        delete c;
    }
}

/*---------------------------------------------------------------------------
    write

    Writes a message into the queue which will be sent to the client.
---------------------------------------------------------------------------*/
void CtiLMConnection::write(RWCollectable* msg)
{
    if( _queue.isOpen() )
    {
        _queue.write( (RWCollectable*) msg );
    }
}

/*---------------------------------------------------------------------------
    _sendthr
    
    Handles putting instances of RWCollectable found in the queue onto the
    output stream.
---------------------------------------------------------------------------*/    
void CtiLMConnection::_sendthr()
{
    RWCollectable* out;

    try
    {     
        do
        {
            rwRunnable().serviceCancellation();

            out = _queue.read();

            try
            {
                if( out != NULL )
                {
                    if( out->isA()!=__RWCOLLECTABLE )
                    {
                        *oStream << out;
                        oStream->vflush();
                    }
                    delete out;
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
        while ( isValid() && oStream->good() );
    }
    catch(RWCancellation& )
    {
        throw;
    }
    catch ( RWxmsg& msg )
    {
        /*{    
            RWMutexLock::LockGuard guard(coutMux);
            cout << "CtiLMConnection::_sendthr - " << msg.why() << endl;
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
void CtiLMConnection::_recvthr()
{
    RWRunnable runnable;

    CtiLMExecutorFactory f;

    try
    {
        rwRunnable().serviceCancellation();
        
        do
        {
            //cout << RWTime()  << "waiting to receive - thr:  " << rwThreadId() << endl;
            RWCollectable* current = NULL;
 
            *iStream >> current;

            if ( current != NULL )
            {
                CtiLoadManager::getInstance()->handleMessage((CtiMessage*)current);
            }
            else
            {
                /*CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Why did I get here? in: " << __FILE__ << " at:" << __LINE__ << endl;*/

                //_valid = FALSE;
            }

        }
        while ( isValid()  && iStream->good() );
    }
    catch(RWCancellation& )
    {
        throw;
    }
    catch ( RWxmsg& msg )
    {
        /*{    
            RWMutexLock::LockGuard guard(coutMux);
            cout << "CtiLMClientConnection::_recvthr - " << msg.why() << endl;
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
