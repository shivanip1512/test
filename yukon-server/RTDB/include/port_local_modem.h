#pragma warning( disable : 4786)
#ifndef __PORT_LOCAL_MODEM_H__
#define __PORT_LOCAL_MODEM_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_local_modem
*
* Class:  CtiPortLocalModem
* Date:   3/31/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/port_local_modem.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "port_modem.h"
#include "tbl_port_dialup.h"

class IM_EX_PRTDB CtiPortLocalModem : public CtiPortModem
{
protected:

   CtiTablePortDialup    _localDialup;

private:

public:

   typedef CtiPortModem  Inherited;

   CtiPortLocalModem();

   CtiPortLocalModem(const CtiPortLocalModem& aRef);

   virtual ~CtiPortLocalModem();

   CtiPortLocalModem& operator=(const CtiPortLocalModem& aRef);

   CtiTablePortDialup         getLocalDialup() const;
   CtiTablePortDialup&        getLocalDialup();

   CtiPortLocalModem&        setLocalDialup(const CtiTablePortDialup& aRef);


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual INT init();

   virtual RWCString  getModemInit() const;
   virtual RWCString& getModemInit();

   virtual INT  connectToDevice(CtiDevice *Device, INT trace);
   virtual INT  disconnect(CtiDevice *Device, INT trace);
   virtual BOOL shouldDisconnect() const;
   virtual BOOL connected();
   virtual BOOL connectedTo(const LONG devID);
   virtual BOOL connectedTo(const ULONG crc);

   virtual INT reset(INT trace);
   virtual INT setup(INT trace);
   virtual INT close(INT trace);



};
#endif // #ifndef __PORT_LOCAL_MODEM_H__
