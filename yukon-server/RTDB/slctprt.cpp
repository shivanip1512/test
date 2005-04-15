/*-----------------------------------------------------------------------------*
*
* File:   slctprt
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/slctprt.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/04/15 19:04:10 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/db/db.h>

#include "port_dialout.h"
#include "port_dialin.h"
#include "port_direct.h"
#include "port_pool_out.h"
#include "port_tcpip.h"

#include "devicetypes.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "slctprt.h"

DLLEXPORT CtiPort* PortFactory(RWDBReader &rdr)
{
   RWCString rwsType;
   RWCString rwsPseudo;

   INT      Type;

   CtiPort *pPort = NULL;

   rdr["type"]  >> rwsType;

   if(getDebugLevel() & DEBUGLEVEL_FACTORY) cout << "Creating a Port of type " << rwsType << endl;

   Type = resolvePortType(rwsType);

   switch(Type)
   {
   case PortTypeLocalDirect:
      {
         pPort = (CtiPort*) CTIDBG_new CtiPortDirect;
         break;
      }
   case PortTypeLocalDialup:
      {
         pPort = (CtiPort*) CTIDBG_new CtiPortDirect( CTIDBG_new CtiPortDialout );
         break;
      }
   case PortTypeLocalDialBack:
      {
         pPort = (CtiPort*) CTIDBG_new CtiPortDirect( CTIDBG_new CtiPortDialin );
         break;
      }
   case PortTypeTServerDirect:
      {
         pPort = (CtiPort*) CTIDBG_new CtiPortTCPIPDirect;
         break;
      }
   case PortTypeTServerDialup:
      {
         pPort = (CtiPort*) CTIDBG_new CtiPortTCPIPDirect( CTIDBG_new CtiPortDialout );
         break;
      }
   case PortTypeTServerDialBack:
      {
         pPort = (CtiPort*) CTIDBG_new CtiPortTCPIPDirect( CTIDBG_new CtiPortDialin );
         break;
      }
   case PortTypePoolDialout:
      {
         pPort = (CtiPort*) CTIDBG_new CtiPortPoolDialout;
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



