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
* REVISION     :  $Revision: 1.16.14.1 $
* DATE         :  $Date: 2008/11/13 17:23:43 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <string.h>

#include "ctinexus.h"
#include "types.h"
#include "dsm2.h"
#include "port_shr.h"
#include "CTICALLS.H"
#include "cparms.h"
#include "numstr.h"

using std::string;
using std::endl;

CtiPortShare::CtiPortShare(CtiPortSPtr myPort, INT listenPort) :
   _sequenceFailReported(false),
   _port(myPort),
   _internalPort(listenPort)
{
   _returnNexus.NexusState = CTINEXUS_STATE_NULL;
   _returnNexus.sockt = INVALID_SOCKET;
   _requestCount = 0;
   
}


CtiPortShare::~CtiPortShare()
{
   interruptBlockingAPI();
}


CtiPortSPtr CtiPortShare::getPort()
{
   return _port;
}

string CtiPortShare::getIDString() const
{
    return string("Port Share \"" + _port->getName() + "\" (" + CtiNumStr(_port->getPortID()) + ")");
}


unsigned CtiPortShare::getDebugLevel( unsigned mask )
{
    return gConfigParms.getValueAsULong("DEBUG_PORT_SHARE", 0, 16) & mask;
}


UINT CtiPortShare::getRequestCount() const
{
   return _requestCount;
}


CtiPortShare& CtiPortShare::setRequestCount(UINT bc)
{
   _requestCount = bc;
   return *this;
}


CtiPortShare& CtiPortShare::incRequestCount()
{
   _requestCount++;
   return *this;
}

CtiPortShare& CtiPortShare::decRequestCount()
{
   if(_requestCount > 0) _requestCount--;
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

/*
 *  This method creates a server side nexus and waits for a client connection.  It then closes the listener
 *  and re-cycles the name as the port share's "server" side of the newly formed connection.
 *  The new connection is formed by a different thread calling connectNexus()
 *
 *  This nexus has NOTHING to do with the SCADA side of the system.  It is used to get results out from the internals of porter.
 *
 */
void CtiPortShare::createNexus(string nexusName)
{
   INT nRet;
   CTINEXUS ListenNexus;
   CTINEXUS newNexus;

   /*
    *  4/8/99 This is the server side of a new Nexus
    *  This thread listens only once and is shutdown as the listener.
    *
    *  The socket connection created here is then used to communicate to porter from port_shr.
    */

   strcpy(ListenNexus.Name, nexusName.data());

   if( isDebugLudicrous() )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      dout << "  Presenting port " << _internalPort << " for connection " << endl;
   }

   if(ListenNexus.CTINexusCreate(_internalPort))
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " Could not create a NEXUS for " << nexusName << endl;
      }
      return;
   }

   /*
    *  Blocking wait on the listening nexus.  Will return a new nexus for the connection
    */
   nRet = ListenNexus.CTINexusConnect(&newNexus);

   if(nRet)
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " Error establishing Nexus to InThread for " << nexusName << endl;
      }
      return;
   }

   /* Someone has connected to us.. */
   if( isDebugLudicrous() )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " closing listener nexus for in/outthread connection " << nexusName << endl;
   }

   ListenNexus.CTINexusClose();  // Don't need this anymore.

   if( isDebugLudicrous() )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " closed " << nexusName << endl;
   }

   if(_internalNexus.CTINexusValid())
   {
        _internalNexus.CTINexusClose();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Closing the internal port_shr nexus.  Will recreate." << endl;
        }
   }

   _internalNexus = newNexus;
   strcpy(_internalNexus.Name, nexusName.data());

   return;
}

void CtiPortShare::interruptBlockingAPI()
{
   // OK, need to sweep the nexus here.

   if(_internalNexus.NexusState != CTINEXUS_STATE_NULL)
   {
      _internalNexus.CTINexusClose(); // Kick it good.
   }

   if(_returnNexus.NexusState != CTINEXUS_STATE_NULL)
   {
      _returnNexus.CTINexusClose(); // Kick it good.
   }
}

/*
 *  This method creates a connection to the internal nexus via the local IP and _internalPort.  All responses from porter proper are
 *  sent back out via this nexus.  It has nothing to do with the SCADA side.
 */
void CtiPortShare::connectNexus()
{
   INT i, j = 0;

   if( isDebugLudicrous() )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      dout << "  Attempting connection on port " << _internalPort << endl;
   }

   while((i = _returnNexus.CTINexusOpen("127.0.0.1", _internalPort, CTINEXUS_FLAG_READANY)) != NORMAL)
   {
      if(!(++j % 15))
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " Port Sharing IP interface is having issues " << i << "   " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }

      CTISleep(1000L);
   }
}


void CtiPortShare::setSharedCCUError(unsigned char address, unsigned char error_byte)
{
    if( _ccuError.find(address) != _ccuError.end() )
    {
        _ccuError[address] |= error_byte;
    }
    else
    {
        _ccuError.insert(std::make_pair(address, error_byte));
    }
}


void CtiPortShare::clearSharedCCUError(unsigned char address)
{
    _ccuError.erase(address);
}


bool CtiPortShare::hasSharedCCUError(unsigned char address) const
{
    return _ccuError.find(address) != _ccuError.end();
}


unsigned char CtiPortShare::getSharedCCUError(unsigned char address) const
{
    unsigned char error_byte = 0;

    std::map<unsigned char, unsigned char>::const_iterator itr;

    itr = _ccuError.find(address);

    if( itr != _ccuError.end() )
    {
        error_byte = itr->second;
    }

    return error_byte;
}

