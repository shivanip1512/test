
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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2002/07/18 16:22:53 $
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

   RWCString      _dialedUpNumber;

public:

   typedef CtiPortDirect  Inherited;


   CtiPortModem(LONG id = 0L);

   CtiPortModem(const CtiPortModem& aRef);

   virtual ~CtiPortModem();

   CtiPortModem& operator=(const CtiPortModem& aRef);

   virtual BOOL                 shouldDisconnect() const;
   virtual CtiPort&             setShouldDisconnect(BOOL b = TRUE);

   RWCString            getDialedUpNumber() const;
   RWCString&           getDialedUpNumber();
   CtiPortModem&        setDialedUpNumber(const RWCString &str);

   /*-----------------------------------------------------*
    * Used to establish a connection to the remote Device
    *-----------------------------------------------------*/

   INT modemReset(USHORT Trace, BOOL dcdTest = TRUE);
   INT modemSetup(USHORT Trace, BOOL dcdTest = TRUE);
   INT modemHangup(USHORT Trace, BOOL dcdTest = TRUE);
   INT modemConnect(PCHAR Message, USHORT Trace, BOOL dcdTest = TRUE);
};


#endif // #ifndef __PORT_MODEM_H__
