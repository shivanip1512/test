#include "yukon.h"

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <process.h>

#ifdef OLD_STUFF
extern "C" {
   #include "os2_2w32.h"
   #include "cticalls.h"
   #include "ctinexus.h"
   #include "dsm2.h"
}
#else
   #include "cticalls.h"
   #include "ctinexus.h"
#endif


#include "proclog.h"
#include "dllmain.h"
#include "errmsg.h"
#include "errclient.h"
#include "errserver.h"


INT ProcLogServerInit(VOID)
{
   DWORD    iRet = 0;
   STARTUPINFO         si;
   PROCESS_INFORMATION pi;

   if(!Initialized)
   {
      HANDLE hSingleton = NULL;

      if((hSingleton = CreateMutex(NULL, TRUE, "ProcLogMux")) != NULL)
      {
         if( WaitForSingleObject(hSingleton, 0) == WAIT_OBJECT_0 )
         {
            // Initialize structures.
            //.......................
            ZeroMemory( &si, sizeof(STARTUPINFO) );
            ZeroMemory( &pi, sizeof(PROCESS_INFORMATION) );

            si.cb          = sizeof( STARTUPINFO );
            si.dwFlags     = STARTF_USESHOWWINDOW;
            si.wShowWindow = SW_MINIMIZE; //  SW_SHOWNORMAL;

            iRet = !CreateProcess(  PROCLOGGER_APPNAME,
                                    NULL,
                                    NULL,
                                    NULL,
                                    FALSE,
                                    CREATE_NEW_CONSOLE,
                                    NULL,
                                    NULL,
                                    &si,
                                    &pi
                                  );
         }

         CloseHandle(hSingleton);
      }
   }

   return(iRet);
}

/*
 *  C interface functions for this DLL!
 */
extern "C" {

   DLLEXPORT INT ProcLogMsg(CHAR *Source, CHAR *File, CHAR *Message, INT Command, INT Flags, LONG t)
   {
      INT         j;
      CTIERRMSG   EMsg;
      ULONG       BytesWritten = 0;

      EMsg.Time         = t;             // Target sets time.
      EMsg.OpFlags      = Flags;
      strcpy (EMsg.Source , Source);
      strcpy (EMsg.Message, Message);
      strcpy (EMsg.File   , File);

      EMsg.Command = Command;

      j = ProcLogWrite(&EMsg);

      return j;

   }

   DLLEXPORT INT ProcLogWrite(CTIERRMSG *ErrMsg)
   {
      ULONG    BytesWritten = 0;
      CErrMsg  Msg(ErrMsg);                        // Use conversion constructor on this C structure.

      return (ErrClient->Log(Msg));
   }


   /*
    *  C interface for a client connection.  Uses Globally defined CErrClient (ErrClient) to
    *  connect to the proclogger server
    */
   DLLEXPORT INT ProcLogClientInit(CHAR *Name)
   {
      INT   iRet = 0;

      if(!(iRet = ProcLogServerInit()))
      {
         iRet = ErrClient->Connect(Name);
      }

      return iRet;
   }
}


