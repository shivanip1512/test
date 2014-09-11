#include "precompiled.h"

#include <string.h>

#include "types.h"
#include "dsm2.h"
#include "port_shr.h"
#include "CTICALLS.H"
#include "cparms.h"
#include "numstr.h"
#include "streamSocketListener.h"

using std::string;
using std::endl;
using Cti::Timing::Chrono;
using Cti::StreamSocketConnection;
using Cti::StreamSocketListener;

CtiPortShare::CtiPortShare(CtiPortSPtr myPort, INT listenPort) :
   _sequenceFailReported(false),
   _port(myPort),
   _internalPort(listenPort)
{
   _requestCount = 0;

   _shutdownEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
   if( _shutdownEvent == (HANDLE)NULL )
   {
       const char* message = "Couldn't create _shutdownEvent event!";

       {
           CtiLockGuard<CtiLogger> doubt_guard(dout);
           dout << CtiTime() << message << endl;
       }

       throw std::runtime_error(message);
   }
}


CtiPortShare::~CtiPortShare()
{
    CloseHandle(_shutdownEvent);
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
   case ClientErrors::ReadTimeout :
   case ClientErrors::NoDcd :
      retValue = CtiPortShare::PSHR_ERROR_NOREPLY;
      break;
   case ClientErrors::BadSequence :
      retValue = CtiPortShare::PSHR_ERROR_SEQUENCE;
      break;
   case ClientErrors::Framing :
      retValue = CtiPortShare::PSHR_ERROR_FRAME;
      break;
   case ClientErrors::BadCrc:
      retValue = CtiPortShare::PSHR_ERROR_BADCRC;
      break;
   case ClientErrors::BadLength:
      retValue = CtiPortShare::PSHR_ERROR_BADLENGTH;
      break;
   case ClientErrors::Unknown:
      retValue = CtiPortShare::PSHR_ERROR_UNKNOWN;
      break;
   case ClientErrors::RemoteInhibited:
      retValue = CtiPortShare::PSHR_ERROR_RTU_DISABLED;
      break;
   case ClientErrors::PortInhibited:
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
void CtiPortShare::createNexus(const string &nexusName)
{
    StreamSocketListener ListenNexus;

   /*
    *  4/8/99 This is the server side of a new Nexus
    *  This thread listens only once and is shutdown as the listener.
    *
    *  The socket connection created here is then used to communicate to porter from port_shr.
    */

   ListenNexus.Name = nexusName;

   if( isDebugLudicrous() )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
       dout << "  Presenting port " << _internalPort << " for connection " << endl;
   }

   if( ! ListenNexus.create(_internalPort) )
   {
       {
           CtiLockGuard<CtiLogger> doubt_guard(dout);
           dout << CtiTime() << " Could not create a NEXUS for " << nexusName << endl;
       }
       return;
   }

   // Blocking wait on the listening nexus.  Will return a new nexus for the connection
   std::auto_ptr<StreamSocketConnection> newNexus = ListenNexus.accept(StreamSocketConnection::ReadExactly, Chrono::infinite, &_shutdownEvent);

   if( ! newNexus.get() )
   {
       if( ! isSet(SHUTDOWN) )
       {
           CtiLockGuard<CtiLogger> doubt_guard(dout);
           dout << CtiTime() << " Error establishing Nexus to InThread for " << nexusName << endl;
       }
       return;
   }

   // Someone has connected to us.. or there was an error
   if( isDebugLudicrous() )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " closing listener nexus for in/outthread connection " << nexusName << endl;
   }

   ListenNexus.close();  // Don't need this anymore.

   // Someone has connected to us..
   if( _internalNexus.isValid() )
   {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Closing the internal port_shr nexus. Will recreate." << endl;
        }
        _internalNexus.close();
   }

   newNexus->Name = nexusName;
   _internalNexus.swap(*newNexus);
}

/*
 *  This method creates a connection to the internal nexus via the local IP and _internalPort.  All responses from porter proper are
 *  sent back out via this nexus.  It has nothing to do with the SCADA side.
 */
void CtiPortShare::connectNexus()
{
    unsigned attempt = 0;

    if( isDebugLudicrous() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "  Attempting connection on port " << _internalPort << endl;
    }

    while( ! isSet(CtiThread::SHUTDOWN) && ! _returnNexus.open("localhost", _internalPort, Cti::StreamSocketConnection::ReadAny) )
    {
        if( ! (attempt++ % 15) ) // print every 15 seconds
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Port Sharing IP interface is having issues " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

