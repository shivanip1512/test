/*-----------------------------------------------------------------------------
    Filename:  ccclientconn.cpp
                
    Programmer:  Josh Wolberg
    
    Description: Source file for CtiCCClientConnection
        
    Initial Date:  9/04/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "ccclientconn.h"
#include "ccmessage.h"
#include "ccexecutor.h"
#include "ccsubstationbusstore.h"
#include "ctibase.h"
#include "capcontroller.h"
#include "logger.h"

extern ULONG _CC_DEBUG;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientConnection::CtiCCClientConnection(RWPortal portal) : _valid(TRUE), _portal(new RWPortal(portal) )
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
BOOL CtiCCClientConnection::isValid() const
{
    return _valid;
}

/*---------------------------------------------------------------------------
    getConnectionName

    Closes the connection
---------------------------------------------------------------------------*/
RWSockAddr CtiCCClientConnection::getConnectionName()
{
    return ((RWSocketPortal*)_portal)->socket().getsockname();
}

/*---------------------------------------------------------------------------
    close
    
    Closes the connection
---------------------------------------------------------------------------*/
void CtiCCClientConnection::close()
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
void CtiCCClientConnection::write(RWCollectable* msg)
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
void CtiCCClientConnection::_sendthr()
{
    RWCollectable* out;

    try
    {   
        /*{
            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

            ULONG msgBitMask = CtiCCSubstationBusMsg::AllSubBusesSent;

            
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*store->getCCSubstationBuses(RWDBDateTime().seconds()),msgBitMask));
            executor->Execute();
            delete executor;
            executor = f.createExecutor(new CtiCCCapBankStatesMsg(*store->getCCCapBankStates(RWDBDateTime().seconds())));
            executor->Execute();
            delete executor;
            executor = f.createExecutor(new CtiCCGeoAreasMsg(*store->getCCGeoAreas(RWDBDateTime().seconds())) );
            executor->Execute();
            delete executor;
        }*/


        do
        {
            rwRunnable().serviceCancellation();

            out = _queue.read();

            try
            {
                if( out != NULL )
                {
                    try
                    {
                        if( out->isA()!=__RWCOLLECTABLE && oStream->good())
                        {
                            try
                            {    
                                *oStream << out;
                                oStream->vflush();
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }
                        }
                        delete out;
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
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

}


/*---------------------------------------------------------------------------
    _recvthr
    
    Receives RWCollectables which must also be cast to CtiCommandable and
    executes them.
---------------------------------------------------------------------------*/   
void CtiCCClientConnection::_recvthr()
{
    RWRunnable runnable;

   // CtiCCExecutorFactory f;

    try
    {
      //  rwRunnable().serviceCancellation();
        
        do
        {
            rwRunnable().serviceCancellation();
            //cout << RWTime()  << "waiting to receive - thr:  " << rwThreadId() << endl;
            RWCollectable* current = NULL;
             try
                {
                 *iStream >> current;
                 
             }
             catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            if ( current != NULL )
            {
               try
                {
                    if( CtiCapController::getInstance()->getInClientMsgQueueHandle().isOpen() )
                    {
                        CtiCapController::getInstance()->getInClientMsgQueueHandle().write(  current );
                    }   
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
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

}
