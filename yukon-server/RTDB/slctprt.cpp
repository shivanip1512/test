#pragma warning (disable : 4786)
/*-----------------------------------------------------------------------------*
*
* File:   slctprt
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/slctprt.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/09/19 15:57:58 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/db.h>

#include "port_dialout.h"
#include "port_direct.h"
#include "port_tcpip.h"

#include "devicetypes.h"
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
         pPort = (CtiPort*) new CtiPortDirect( new CtiPortDialout );
         break;
      }
   case PortTypeTServerDirect:
      {
         pPort = (CtiPort*) new CtiPortTCPIPDirect;
         break;
      }
   case PortTypeTServerDialup:
      {
         pPort = (CtiPort*) new CtiPortTCPIPDirect( new CtiPortDialout );
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



