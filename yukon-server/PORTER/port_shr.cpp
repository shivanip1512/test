/*-----------------------------------------------------------------------------*
*
* File:   port_shr
*
* Date:   8/6/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/port_shr.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/02/10 23:23:54 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <string.h>

#include "ctinexus.h"
#include "types.h"
#include "dsm2.h"
#include "port_shr.h"
#include "tcpsup.h"
#include "CTICALLS.H"

CtiPortShare::CtiPortShare(CtiPortSPtr myPort, INT listenPort) :
   _port(myPort),
   _listenPort(listenPort)
{
   _returnNexus.NexusState = CTINEXUS_STATE_NULL;
}


CtiPortShare::~CtiPortShare()
{
   interruptBlockingAPI();
}


CtiPortSPtr CtiPortShare::getPort()
{
   return _port;
}



UINT CtiPortShare::getBusyCount() const
{
   return _busy;
}


CtiPortShare& CtiPortShare::setBusyCount(UINT bc)
{
   _busy = bc;
   return *this;
}


CtiPortShare& CtiPortShare::incBusyCount()
{
   _busy++;
   return *this;
}

CtiPortShare& CtiPortShare::decBusyCount()
{
   _busy--;
   return *this;
}


USHORT CtiPortShare::ProcessEventCode(USHORT EventCode)
{
   USHORT retValue;

   switch(EventCode)
   {
   case READTIMEOUT :
   case NODCD :
      retValue = CtiPortShare::PSHR_ERROR_NOREPLY;
      break;
   case BADSEQUENCE :
      retValue = CtiPortShare::PSHR_ERROR_SEQUENCE;
      break;
   case FRAMEERR :
      retValue = CtiPortShare::PSHR_ERROR_FRAME;
      break;
   case BADCRC:
      retValue = CtiPortShare::PSHR_ERROR_BADCRC;
      break;
   case BADLENGTH:
      retValue = CtiPortShare::PSHR_ERROR_BADLENGTH;
      break;
   case ERRUNKNOWN:
      retValue = CtiPortShare::PSHR_ERROR_UNKNOWN;
      break;
   case DBFERR:
      retValue = CtiPortShare::PSHR_ERROR_DATABASE;
      break;
   case REMOTEINHIBITED:
      retValue = CtiPortShare::PSHR_ERROR_RTU_DISABLED;
      break;
   case PORTINHIBITED:
      retValue = CtiPortShare::PSHR_ERROR_PORT_DISABLED;
      break;
   default:
      retValue = CtiPortShare::PSHR_ERROR_NOREPLY;
   }
   return retValue;
}


void CtiPortShare::createNexus(RWCString nexusName)
{
   INT nRet;
   CTINEXUS newNexus;

   /*
    *  4/8/99 This is the server side of a CTIDBG_new Nexus
    *  This thread listens only once and then may shutdown the listener?????
    *
    *  The socket connection created here is then used to communicate to porter
    */

   strcpy(_listenNexus.Name, nexusName.data());

   if( getDebugLevel() == DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      dout << "  Presenting port " << _listenPort << " for connection " << endl;
   }

   if(_listenNexus.CTINexusCreate(_listenPort))
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Could not create a NEXUS for " << nexusName << endl;
      }
      return;
   }

   /*
    *  Blocking wait on the listening nexus.  Will return a CTIDBG_new nexus for the connection
    */
   nRet = _listenNexus.CTINexusConnect(&newNexus);

   if(nRet)
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Error establishing Nexus to InThread for " << nexusName << endl;
      }
      return;
   }

   /* Someone has connected to us.. */
   if( getDebugLevel() == DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " closing listener nexus for in/outthread connection " << nexusName << endl;
   }

   _listenNexus.CTINexusClose();  // Don't need this anymore.

   if( getDebugLevel() == DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " closed " << nexusName << endl;
   }

   _listenNexus = newNexus;

   return;
}

void CtiPortShare::interruptBlockingAPI()
{
   // OK, need to sweep the nexus here.

   if(_listenNexus.NexusState != CTINEXUS_STATE_NULL)
   {
      _listenNexus.CTINexusClose(); // Kick it good.
   }

   if(_returnNexus.NexusState != CTINEXUS_STATE_NULL)
   {
      _returnNexus.CTINexusClose(); // Kick it good.
   }
}

void CtiPortShare::connectNexus()
{
   INT i, j = 0;

   if( getDebugLevel() == DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      dout << "  Attempting connection on port " << _listenPort << endl;
   }

//   while((i = _returnNexus.CTINexusOpen("127.0.0.1", _listenPort, CTINEXUS_FLAG_READEXACTLY)) != NORMAL)
   while((i = _returnNexus.CTINexusOpen("127.0.0.1", _listenPort, CTINEXUS_FLAG_READANY)) != NORMAL)
   {
      if(!(++j % 15))
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Port Sharing IP interface is having issues " << i << "   " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }

      CTISleep(1000L);
   }
}
