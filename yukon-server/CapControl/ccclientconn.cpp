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
#include "utility.h"
#include "thread_monitor.h"

extern ULONG _CC_DEBUG;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiCCClientConnection::CtiCCClientConnection(RWPortal portal) : _valid(TRUE), _portal(new RWPortal(portal) )
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - New Client Connection, from " << ((RWSocketPortal*)_portal)->socket().getpeername() << endl;
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
        dout << CtiTime() << " - Client Connection closing." << endl;
    }
    try
    {
        close();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Client Connection closed." << endl;
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


RWSockAddr CtiCCClientConnection::getPeerName()
{
    return ((RWSocketPortal*)_portal)->socket().getpeername();
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

    oStream->vflush();

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
        _queue.write( msg );
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
    ThreadMonitor.start(); 
    try
    {   
        CtiTime rwnow;
        CtiTime announceTime((unsigned long) 0);
        CtiTime tickleTime((unsigned long) 0);


        {
            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

            //ULONG msgBitMask = CtiCCAreaMsg::AllAreasSent;
            ULONG msgBitMask = CtiCCSubstationBusMsg::AllSubBusesSent;

            
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::REQUEST_ALL_DATA));
            executor->Execute();
            delete executor;
        }


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
                            if( _CC_DEBUG & CC_DEBUG_RIDICULOUS && out->isA() == CTICCSUBSTATIONBUS_MSG_ID) 
                            {
                                long x = ((CtiCCSubstationBusMsg*) out)->getCCSubstationBuses()->size();
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Message begin writing to socket. Message contains: "<<x<<" sub entries." << endl;
                                }
                            }

                            try
                            {    
                                *oStream << out;
                                oStream->vflush();
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }

                            if( _CC_DEBUG & CC_DEBUG_RIDICULOUS && out->isA() == CTICCSUBSTATIONBUS_MSG_ID) 
                            {
                                long x = ((CtiCCSubstationBusMsg*) out)->getCCSubstationBuses()->size();
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " - Message finished writing to socket. Message contains: "<<x<<" sub entries." << endl;
                                }
                            }
                        }
                        delete out;
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            rwnow = rwnow.now();
            if(rwnow.seconds() > tickleTime.seconds())
            {
                tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                if( rwnow.seconds() > announceTime.seconds() )
                {
                    announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " CapControl _sendThr. TID: " << rwThreadId() << endl;
                }

               /* if(!_shutdownOnThreadTimeout)
                {*/
                    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl _sendThr", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::periodicComplain, 0) );
                /*}
                else
                {   
                    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl _sendThr", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::sendUserQuit, CTIDBG_new string("CapControl _sendThr")) );
                //}*/
            }  

        }
        while ( isValid() && oStream->good() );

        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl _sendThr", CtiThreadRegData::LogOut ) );
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
      // 
        CtiTime rwnow;
        CtiTime announceTime((unsigned long) 0);
        CtiTime tickleTime((unsigned long) 0);

        do
        {
            rwRunnable().serviceCancellation();
            //cout << CtiTime()  << "waiting to receive - thr:  " << rwThreadId() << endl;
            RWCollectable* current = NULL;
             try
             {
                 *iStream >> current;
                 
             }
             catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
            rwnow = rwnow.now();
            if(rwnow.seconds() > tickleTime.seconds())
            {
                tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                if( rwnow.seconds() > announceTime.seconds() )
                {
                    announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " CapControl _recvThr. TID: " << rwThreadId() << endl;
                }

               /* if(!_shutdownOnThreadTimeout)
                {*/
                    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl _recvThr", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::periodicComplain, 0) );
               /* }
                else
                {   
                    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl _recvThr", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::sendUserQuit, CTIDBG_new string("CapControl _recvThr")) );
                //}*/
            }  


        }
        while ( isValid()  && iStream->good() );

        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl _recvThr", CtiThreadRegData::LogOut ) );
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    _valid = FALSE;

}
