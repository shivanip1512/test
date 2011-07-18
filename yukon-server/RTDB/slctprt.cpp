/*-----------------------------------------------------------------------------*
*
* File:   slctprt
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/slctprt.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/12/20 17:20:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"



#include "port_dialout.h"
#include "port_dialin.h"
#include "port_direct.h"
#include "port_pool_out.h"
#include "port_tcpipdirect.h"
#include "port_tcp.h"
#include "port_udp.h"

#include "devicetypes.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "slctprt.h"

using namespace Cti;
using namespace Ports;
using std::string;
using std::endl;

DLLEXPORT CtiPort* PortFactory(Cti::RowReader &rdr)
{
   string rwsType;
   string rwsPseudo;

   INT      Type;

   CtiPort *pPort = NULL;

   rdr["type"]  >> rwsType;

   if(getDebugLevel() & DEBUGLEVEL_FACTORY) { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " Creating a Port of type " << rwsType << endl; }

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
   case PortTypeTcp:
      {
         pPort = (CtiPort*) CTIDBG_new TcpPort;
         break;
      }
   case PortTypeUdp:
      {
         pPort = (CtiPort*) CTIDBG_new UdpPort;
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
         { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " Port Factory has failed to produce for type " << rwsType << "!" << endl; }
         break;
      }
   }

   return pPort;
}



