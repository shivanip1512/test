#include "precompiled.h"

#include "porter.h"
#include "logger.h"
#include "ctinexus.h"

extern CTINEXUS   PorterNexus;            // declared in dllmain.cpp

using namespace std;

/* Routine to initialize pipe to porter */
IM_EX_CTIBASE INT PortPipeInit (USHORT Wait)
{
   ULONG i = NOTNORMAL, j = 0;
   PCHAR ServerName = NULL;
   PSZ EnvServerName;
   CHAR Name[100];


   /* Check if we need to close the pipe */
   PorterNexus.CTINexusClose();     // Close it if it is open.

   /*
    *  OK, this is the client side of a Nexus to Port Control.
    */

   if(CTIScanEnv ("PORTSERVER", &EnvServerName))
   {
      ServerName = NULL;
   }
   else
   {
      ServerName = (PCHAR) EnvServerName;
   }

   if(ServerName == NULL)
   {
      strcpy (Name, "127.0.0.1");   // ME ME ME in gethostbyname call.
   }
   else
   {
      strcpy (Name, ServerName);
   }

   sprintf(PorterNexus.Name, "pexec nexus from client to %s port control", Name);

   while((i = PorterNexus.CTINexusOpen(Name, PORTCONTROLNEXUS, CTINEXUS_FLAG_READEXACTLY)) != NORMAL)
   {
      if(Wait != WAIT)
      {
         PorterNexus.CTINexusClose();
         i = PIPEOPEN;
         break;      // the while loop
      }
      else
      {
         if(!(++j % 60))
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could not connect to Port Control " << i << "   " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }

         CTISleep(1000L);
      }
   }

   return(i);
}

/* Routine that gets run when we go tits up */
IM_EX_CTIBASE void PortPipeCleanup (ULONG Reason)
{
   PorterNexus.CTINexusClose();     // Close it if it is open.
}

