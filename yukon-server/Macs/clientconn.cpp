/*-----------------------------------------------------------------------------*
*
* File:   clientconn
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/clientconn.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/05/26 20:57:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*-----------------------------------------------------------------------------
    Filename:  clientconn.cpp

    Programmer:  Aaron Lauinger

    Description: Source file for CtiMCConnection

    Initial Date:  5/12/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "clientconn.h"
#include "ctibase.h"

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiMCConnection::CtiMCConnection()
: _valid(true), _closed(false), _in(15), _out(100)
{
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiMCConnection::~CtiMCConnection()
{
    close();
}

void CtiMCConnection::initialize(RWPortal portal)
{
    try
    {
	_portal = new RWPortal(portal);
	
        sinbuf  = new RWPortalStreambuf(*_portal);
        soubuf  = new RWPortalStreambuf(*_portal);
        oStream = new RWpostream(soubuf);
        iStream = new RWpistream(sinbuf);

        RWThreadFunction send_thr = rwMakeThreadFunction(*this, &CtiMCConnection::_sendthr);
        RWThreadFunction recv_thr = rwMakeThreadFunction(*this, &CtiMCConnection::_recvthr);

        _sendrunnable = send_thr;
        _recvrunnable = recv_thr;

        send_thr.start();
        recv_thr.start();
    } catch (RWxmsg& msg)
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << "CtiMCConnection::CtiMCConnection - " << msg.why() << endl;
        }

        _valid = FALSE;
    }    
}

/*---------------------------------------------------------------------------
    isValid

    Returns TRUE is the connection is valid, FALSE otherwise
---------------------------------------------------------------------------*/
BOOL CtiMCConnection::isValid()
{
    return _valid;
}

/*---------------------------------------------------------------------------
    close

    Closes the connection
---------------------------------------------------------------------------*/
void CtiMCConnection::close()
{
    {
        CtiLockGuard< CtiMutex > guard(_mux );

        if( _closed )
            return;
    }
        _close();

        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime()  << " Connection closed" << endl;
        }
}

void CtiMCConnection::write(RWCollectable* msg)
{
    try
    {
        if( _out.isOpen() )
            _out.write(msg);
    }
    catch( RWTHRClosedException& msg )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << __FILE__ << " (" << __LINE__ << ") " << msg.why() << endl;
    }
}


RWCollectable* CtiMCConnection::read()
{
    try
    {
        return ( _in.isOpen() ? _in.read() : NULL );
    }
    catch( RWTHRClosedException& msg )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << __FILE__ << " (" << __LINE__ << ") " << msg.why() << endl;
    }

    return NULL;
}

RWCollectable* CtiMCConnection::read(unsigned long millis)
{
    RWCollectable* msg = NULL;

    try
    {
    //if( !_in.isOpen() )
    //    return NULL; // <--- catching the exceptoin should obviate theneed for this

        _in.read(msg, millis); //There is a wait status but we don't care
    }
    catch( RWTHRClosedException& msg )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << __FILE__ << " (" << __LINE__ << ") " << msg.why() << endl;
    }

    return msg;
}

/*---------------------------------------------------------------------------
    _sendthr

    Handles putting instances of RWCollectable found in the queue onto the
    output stream.
---------------------------------------------------------------------------*/
void CtiMCConnection::_sendthr()
{
    RWCollectable* out;

    try
    {
        do
        {
            rwRunnable().serviceCancellation();

            // grab the next collectable to send out
            out = _out.read();

            if( out != NULL )
            {
                *oStream << out;
                oStream->vflush();
                delete out;
            }
        }
        while ( isValid() && oStream->good() );
    } catch ( RWxmsg& msg )
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << "CtiMCConnection::_sendthr - " << msg.why() << endl;
        }
    }
    catch(...)
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << __FILE__ << " (" << __LINE__ << ")" << "An unknown exception was thrown. " << endl;
        }
    }


    try
    {
       _close();
    }
    catch(...)
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime()
                 <<  __FILE__ << " (" << __LINE__ << ")"
                 << " An unkown exception was thrown _sendthr(), was closing the connection"
                 << endl;
        }
    }

    if( gMacsDebugLevel & MC_DEBUG_CONN )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()  << " Exiting sendthr() " << this << endl;
    }
}


/*---------------------------------------------------------------------------
    _recvthr

    Receives RWCollectables which must also be cast to CtiCommandable and
    executes them.
---------------------------------------------------------------------------*/
void CtiMCConnection::_recvthr()
{
    RWCollectable* in = NULL;

    try
    {
        rwRunnable().serviceCancellation();

        do
        {
            *iStream >> in;

            if ( in != NULL )
            {
                _in.write(in);

                // message received, notify our observers
                setChanged();
                notifyObservers();
            }
        }
        while ( isValid()  && iStream->good() );
    }
    catch ( RWxmsg& msg )
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " An exception was thrown _recthr(): " << msg.why() << endl;
        }
    }
    catch(...)
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " An unkown exception was thrown _recthr()" << endl;
        }
    }

    try
    {
        _close();
    }
    catch(...)
    {
        if( gMacsDebugLevel & MC_DEBUG_CONN )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime()
                 << " An unkown exception was thrown _recvthr(), was closing the connection"
                 << endl;
        }
    }

    if( gMacsDebugLevel & MC_DEBUG_CONN )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime()  << " Exiting recvthr()" <<  this << endl;
    }
}

/*----------------------------------------------------------------------------
  _close

  Will set the connectoin to invalid, closed, and will unblock the in and
  out threads so they can realize this.

  The connection's mux must be acquired before calling _close so that it only
  executes once.

----------------------------------------------------------------------------*/

void CtiMCConnection::_close()
{
    {
       CtiLockGuard< CtiMutex > guard(_mux );

        if( _closed )
            return;

        _valid = false;
        _closed = true;
    }

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
    _out.write(unblocker);

    if( !_recvrunnable.isSelf() )
       _recvrunnable.requestCancellation();


    if( !_sendrunnable.isSelf() )
        _sendrunnable.requestCancellation();


    try
    {
        _in.close();

        RWCollectable* c;
        while ( _in.canRead() )
        {
            c = _in.read();
            delete c;
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " Exception occured closing connection in buffer" << endl;
    }

    try
    {
        _out.close();

        RWCollectable* c;
        while ( _out.canRead() )
        {
            c = _out.read();
            delete c;
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " Exception occured closing connection out buffer" << endl;
    }
}
