#pragma warning (disable : 4786)
/*-----------------------------------------------------------------------------*
*
* File:   slctprt
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/slctprt.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:21 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/db.h>

#include "port_direct.h"
#include "port_tcpip.h"
#include "port_local_modem.h"

#include "devicetypes.h"
// #include "rtdb.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "slctprt.h"
#include "yukon.h"

DLLEXPORT CtiPort* PortFactory(RWDBReader &rdr)
{
   RWCString rwsType;
   RWCString rwsPseudo;

   INT      Type;

   CtiPort *pPort = NULL;

   rdr["type"]  >> rwsType;

   if(getDebugLevel() & 0x00000400) cout << "Creating a Port of type " << rwsType << endl;

   Type = resolvePortType(rwsType);

   switch(Type)
   {
   case PortTypeLocalDirect:
      {
         pPort = (CtiPort*) new CtiPortDirect;
         break;
      }
   case PortTypeLocalDialup:
      {
         pPort = (CtiPort*) new CtiPortLocalModem;
         break;
      }
   case PortTypeTServerDirect:
      {
         pPort = (CtiPort*) new CtiPortTCPIPDirect;
         break;
      }
   default:
      {
         cout << "Port Factory has failed to produce for type " << rwsType << "!" << endl;
         break;
      }
   }

   return pPort;
}



