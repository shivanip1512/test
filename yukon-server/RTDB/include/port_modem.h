
#pragma warning( disable : 4786)
#ifndef __PORT_MODEM_H__
#define __PORT_MODEM_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_modem
*
* Class:  CtiPortModem
* Date:   4/4/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/port_modem.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:56 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
using namespace std;

#include <rw\cstring.h>

#include "dev_base.h"
#include "dsm2.h"
#include "port_direct.h"
#include "portsup.h"

class IM_EX_PRTDB CtiPortModem : public CtiPortDirect
{
protected:

   /* Definitions for Dial Up */
   BOOL           _shouldDisconnect;

   LONG           _dialedUpDevice;
   RWCString      _dialedUpNumber;

   ULONG          _dialedUpCRC;



public:

   typedef CtiPortDirect  Inherited;


   CtiPortModem(LONG id = 0L);

   CtiPortModem(const CtiPortModem& aRef);

   virtual ~CtiPortModem();

   CtiPortModem& operator=(const CtiPortModem& aRef);

   BOOL&                getShouldDisconnect();
   BOOL                 shouldDisconnect() const;
   CtiPortModem&        setShouldDisconnect(BOOL b = TRUE);

   LONG                 getDialedUpDevice() const;
   LONG&                getDialedUpDevice();
   CtiPortModem&        setDialedUpDevice(const LONG &i);

   ULONG                getDialedUpDeviceCRC() const;
   ULONG&               getDialedUpDeviceCRC();
   CtiPortModem&        setDialedUpDeviceCRC(const ULONG &i);


   RWCString            getDialedUpNumber() const;
   RWCString&           getDialedUpNumber();
   CtiPortModem&        setDialedUpNumber(const RWCString &str);

   /*-----------------------------------------------------*
    * Used to establish a connection to the remote Device
    *-----------------------------------------------------*/

   virtual BOOL connectedTo(const LONG devID);
   virtual BOOL connectedTo(const ULONG crc);

   INT modemReset(USHORT Trace, BOOL dcdTest = TRUE);
   INT modemSetup(USHORT Trace, BOOL dcdTest = TRUE);
   INT modemHangup(USHORT Trace, BOOL dcdTest = TRUE);
   INT modemConnect(PCHAR Message, USHORT Trace, BOOL dcdTest = TRUE);

};


#endif // #ifndef __PORT_MODEM_H__
