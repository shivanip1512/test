#include "precompiled.h"

#include "porter.h"
#include "logger.h"
#include "streamSocketConnection.h"

extern Cti::StreamSocketConnection PorterNexus; // declared in dllmain.cpp

using namespace std;

/* Routine to initialize pipe to porter */
IM_EX_CTIBASE INT PortPipeInit (USHORT Wait)
{
    PSZ EnvServerName;

    /* Check if we need to close the pipe */
    PorterNexus.close();     // Close it if it is open.

    /*
     *  OK, this is the client side of a Nexus to Port Control.
     */

    const PCHAR ServerName = CTIScanEnv("PORTSERVER", &EnvServerName)
            ? NULL
            : (PCHAR)EnvServerName;

    const string Name = ServerName
            ? ServerName
            : "localhost"; // ME ME ME in gethostbyname call.

   PorterNexus.Name = "pexec nexus from client to " + Name + " port control";

   unsigned waitCount = 0;
   while( ! PorterNexus.open(Name, PORTCONTROLNEXUS, Cti::StreamSocketConnection::ReadExacly) )
   {
      if( Wait != WAIT )
      {
         return PIPEOPEN;
      }

      if( ! (waitCount++ % 60) )
      {
          CtiLockGuard<CtiLogger> doubt_guard(dout);
          dout << CtiTime() << " Could not connect to Port Control " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }

      Sleep(1000);
   }

   return NORMAL;
}

/* Routine that gets run when we go tits up */
IM_EX_CTIBASE void PortPipeCleanup (ULONG Reason)
{
    PorterNexus.close();     // Close it if it is open.
}

