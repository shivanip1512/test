#include "yukon.h"

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <memory.h>
#include <process.h>

#include "cticalls.h"
#include "ctinexus.h"

#include "proclog.h"
#include "errmsg.h"
#include "errclient.h"
#include "dllmain.h"


// Default

CErrClient::CErrClient()
{
   ClientNexus = NULL;
   ClientNexus = (CTINEXUS*)malloc(sizeof( CTINEXUS ));

   return;
}

CErrClient::CErrClient(const CErrClient& Cpy)
{
   if(this == &Cpy)
      return;

   ClientNexus = NULL;
   ClientNexus = (CTINEXUS*) malloc(sizeof( CTINEXUS ));

   memcpy(ClientNexus, &Cpy, sizeof(CTINEXUS));

}

INT CErrClient::Close()
{
   INT i = 0;

   if(ClientNexus->NexusState != CTINEXUS_STATE_NULL)
   {
      i = ClientNexus->CTINexusClose();
   }

   return i;
}


INT CErrClient::Connect(const char *Name)
{
   INT i = 0;
   INT j = 0;

   if(ClientNexus != NULL)
   {
      if(Name != NULL)
         strcpy(ClientNexus->Name, Name);
      else
         strcpy(ClientNexus->Name, "UnNamed ProcLog Client");

      while((i = ClientNexus->CTINexusOpen("", PROCLOGNEXUS, CTINEXUS_FLAG_READEXACTLY)) != 0)
      {

         if(j++ < 10)
         {
            CTISleep(1000L);
         }
         else
         {
            // cout << __FILE__ << " (" << __LINE__ << ")" << endl;
            break;
         }
      }
   }


   return i;

}

INT CErrClient::Log(CErrMsg &Msg)
{
   INT i;
   ULONG BytesWritten;

   if(!ProcLogServerInit())
   {
      /*
       *  Everything is up and running!
       */
      if(ClientNexus == NULL || ClientNexus->NexusState == CTINEXUS_STATE_NULL)
      {
         i = Connect("");      // Member function
      }

      if((i = ClientNexus->CTINexusWrite(Msg.getEMsgPtr(), sizeof (CTIERRMSG), &BytesWritten, 15L)) || BytesWritten != sizeof (CTIERRMSG))
      {
         if(ClientNexus->NexusState != CTINEXUS_STATE_NULL)
         {
            Close ();            // Member function
         }
      }
   }

   return(i);
}
