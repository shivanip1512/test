#include "port_local_modem.h"
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   port_local_modem
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/port_local_modem.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/06/05 17:42:01 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"

RWCString  CtiPortLocalModem::getModemInit() const
{
    return _localDialup.getModemInitString();
}

RWCString& CtiPortLocalModem::getModemInit()
{
    return _localDialup.getModemInitString();
}


CtiTablePortDialup CtiPortLocalModem::getLocalDialup() const
{
    return _localDialup;
}

CtiTablePortDialup& CtiPortLocalModem::getLocalDialup()
{
    return _localDialup;
}

CtiPortLocalModem& CtiPortLocalModem::setLocalDialup(const CtiTablePortDialup& aRef)
{
    _localDialup = aRef;
    return *this;
}

INT CtiPortLocalModem::connectToDevice(CtiDevice *Device, INT trace)
{
    INT status     = NORMAL;
    ULONG DeviceCRC = Device->getUniqueIdentifier();

    /*
     *  This next block Makes sure we hang up if we are connected to a different device's PhoneCRC
     *
     *  If getCDWait is zero, we presume that the device does NOT provide DCD feedback.  In that case,
     *  we must assume that we still are connected and should send a hangup.
     */

    if(connected() && !connectedTo(DeviceCRC))      // This port connected to a device, and is not connected to this device.
    {
        // If we cannot use dcd to tell if we are online, we must do a hangup
        // dcdTest indicates that we ARE on/off line if !FALSE, we must hangup.
        if( _tblPortSettings.getCDWait() == 0  || dcdTest() )
        {
            disconnect(Device, trace);
        }
    }

    /*
     *  At this point we either connected to the device we need (Device), or are not connected to any device.
     */

    /*
     *  Again, the timing setting for DCD is relevant.
     *  If the port has a zero DCD time, we must assume that the only time we need to
     *  connect is if the "to-be-dialed" device represented by DeviceCRC, is not listed as the
     *  currently connected device.
     */

    if( !connectedTo(DeviceCRC) )
    {
        if( (status = checkCommStatus(trace)) == NORMAL)
        {
            RWCString number = getLocalDialup().getPrefixString() + Device->getPhoneNumber();
            /*  Now Dial */
            if(modemConnect((char*)number.data(), trace, _tblPortSettings.getCDWait() != 0))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Error Dialing Modem on Port " << getPortID() << ":  " << getName() << endl;
                }

                disconnect(Device, trace);    // Poke the handle down.

                status = DIALUPERROR;
            }
        }
    }

    if(status == NORMAL)
    {
        /* Make sure the remotes match */
        setConnectedDevice( Device->getID() );
        setConnectedDeviceUID( DeviceCRC );
        setDialedUpNumber( Device->getPhoneNumber() );
    }

    return status;
}

INT CtiPortLocalModem::disconnect(CtiDevice *Device, INT trace)
{
    INT status = NORMAL;

    Inherited::disconnect(Device, trace);

    status = modemHangup( trace );

    if(status)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << getName() << " modemHangup reported status " << status << endl;
    }
    status = Inherited::close( trace );

    return status;
}

BOOL CtiPortLocalModem::connected()
{
    BOOL status = FALSE;

    if(_tblPortSettings.getCDWait() != 0 && getConnectedDevice() > 0)
    {
        if(!dcdTest())    // No DCD and we think we are connected!  This is BAD.
        {
            disconnect(NULL, FALSE);
        }
    }

    return Inherited::connected();
}

BOOL CtiPortLocalModem::connectedTo(const LONG Id)
{
    return CtiPortModem::connectedTo( Id );
}

BOOL CtiPortLocalModem::connectedTo(const ULONG crc)
{
    return CtiPortModem::connectedTo( crc );
}

INT CtiPortLocalModem::reset(INT trace)
{
    setLastBaudRate(0);

    setConnectedDevice(0);
    setDialedUpNumber(RWCString());
    setConnectedDeviceUID(-1);

    return modemReset(getPortID(), trace);
}

INT CtiPortLocalModem::setup(INT trace)
{
    setConnectedDevice(0);
    setDialedUpNumber(RWCString());
    setConnectedDeviceUID(-1);

    return modemSetup(trace, (_tblPortSettings.getCDWait() != 0));
}

BOOL CtiPortLocalModem::shouldDisconnect() const
{
    return CtiPortModem::shouldDisconnect();
}

CtiPortLocalModem::CtiPortLocalModem() {}

CtiPortLocalModem::CtiPortLocalModem(const CtiPortLocalModem& aRef)
{
    *this = aRef;
}

CtiPortLocalModem::~CtiPortLocalModem() {}

CtiPortLocalModem& CtiPortLocalModem::operator=(const CtiPortLocalModem& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _localDialup = aRef.getLocalDialup();
    }
    return *this;
}

void CtiPortLocalModem::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTablePortDialup::getSQL(db, keyTable, selector);
}

void CtiPortLocalModem::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);
    _localDialup.DecodeDatabaseReader(rdr);       // get the base class handled
}


INT CtiPortLocalModem::init()
{
    INT status;

    if((status = Inherited::init()) != NORMAL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error initializing dial up modem on " << getName() << endl;
    }

    if((status = reset(true)) != NORMAL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error resetting dial up modem on " << getName() << endl;
    }

    /* set the modem parameters */
    if((status = setup(true)) != NORMAL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error setting up dial up modem on " << getName() << endl;
    }

    return status;
}


INT CtiPortLocalModem::close(INT trace)
{
    return disconnect(NULL, trace);
}
