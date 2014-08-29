#include "precompiled.h"

#include <stdio.h>
#include <string.h>
#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"
#include "dllbase.h"
#include "dsm2.h"
#include "logger.h"

using namespace std;

/* CTI Sleep Function */
APIRET IM_EX_CTIBASE CTISleep (ULONG SleepTime)
{
   Sleep (SleepTime);      // Win32 does not return anything.
   return((APIRET)0);    // Return a OS/2 NO_ERROR!
}

APIRET IM_EX_CTIBASE CTIScanEnv ( PSZ Name, PSZ *Result )
{
   int status = -1;

   *Result = NULL;


   static char Env[512];    // This IS be non-threadsafe

   if( GetEnvironmentVariable(Name, Env, 500) )
   {
      *Result = Env;
      status = 0;
   }

   return (status);
}


void IM_EX_CTIBASE CTIExit (ULONG ExitType, ULONG ExitCode)
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " **** Checkpoint CTIExit (deprecated) **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   ExitThread(ExitCode);
}


APIRET IM_EX_CTIBASE CTICloseMutexSem(PHMTX phmtx)
{
   DWORD err;
   if(!(CloseHandle(*phmtx)))
   {
      if((err = GetLastError()) != ERROR_FILE_NOT_FOUND)
      {
         RWMutexLock::LockGuard guard(coutMux);
         printf("Error in CloseMutex = %05d\n",err);
      }
      return(APIRET)1;    // This is an error!!!!
   }
   else
   {
      return(APIRET)0;    // OS2 returns zero on success so do that.
   }
}


APIRET IM_EX_CTIBASE CTICreateMutexSem(PSZ pszName, PHMTX phmtx, ULONG Flags, BOOL bState)
{
   /**********************************************************************
      * CreateMutex pp. 1272 in the Windows NT Win32 SuperBible
      */
   if(!(*phmtx = CreateMutex( (LPSECURITY_ATTRIBUTES)NULL, FALSE, pszName))) // NULL = default security
   {
      {
         RWMutexLock::LockGuard guard(coutMux);
         printf("Error in CreateMutex = %u\n",GetLastError());
      }
      return(APIRET)1;    // This is an error!!!!
   }
   else
   {
      return(APIRET)0;    // OS2 returns zero on success so do that.
   }
}

APIRET IM_EX_CTIBASE CTIRequestMutexSem(HMTX hmtx, ULONG duration)
{
   DWORD rVal;

   rVal = WaitForSingleObject(hmtx, duration);

   switch(rVal)
   {
   case WAIT_OBJECT_0:
       {
           rVal = 0;
           break;
       }
   case WAIT_ABANDONED:
       {
           {
              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << CtiTime() << " **** CTIRequestMutexSem: Wait Abandoned **** " << __FILE__ << " (" << __LINE__ << "). Error = " << GetLastError() << endl;
           }

           rVal = 0;
           break;
       }
   case WAIT_TIMEOUT:
       {
           rVal = 1;
           break;
       }
   default:
       {
           {
              CtiLockGuard<CtiLogger> doubt_guard(dout);
              dout << CtiTime() << " **** CTIRequestMutexSem: WaitResult = " << rVal << " **** " << __FILE__ << " (" << __LINE__ << "). Error = " << GetLastError() << endl;
           }

           rVal = 1;
           break;
       }
   }

   return (APIRET)rVal;    // We have the mutex now!
}

APIRET IM_EX_CTIBASE CTIReleaseMutexSem(HMTX hmtx)
{
   return(!(ReleaseMutex(hmtx)));
}

APIRET IM_EX_CTIBASE CTIDelete(PSZ old)
{
   return(!(DeleteFile(old)));   // OS2 wants 0 ret if OK NT rets TRUE on OK
}

/* Function to return the current thread ID - 32 bit only */
DWORD      IM_EX_CTIBASE CurrentTID()
{
   return GetCurrentThreadId();
}

APIRET IM_EX_CTIBASE CTIClose(HANDLE &hFile)
{
   DWORD status = NORMAL;

   if( !CloseHandle(hFile) )
   {
      status = !NORMAL;
   }

   hFile = (HANDLE)NULL;

   return(status);        // Win32 returns positive if successfull.
}

YukonError_t CTIRead( HANDLE   &hFile,
                      PVOID    pBuf,
                      ULONG    BufLen,
                      PULONG   pBytesRead )
{
   BOOL  bSuccess = FALSE;

   bSuccess = ReadFile(hFile, pBuf, BufLen, pBytesRead, NULL);

   return bSuccess ? NoError : NOTNORMAL;  // Win32 returns positive if successfull.
}

APIRET IM_EX_CTIBASE CTIWrite         (   HANDLE    &hFile,
                                          PVOID    pBuf,
                                          ULONG    BufLen,
                                          PULONG   pBytesWritten
                                      )
{
   BOOL bSuccess = FALSE;

   DWORD status = NORMAL;

//   fprintf(stderr,"*** DEBUG CTIWrite File Handle %8d\n",hFile);
   bSuccess = WriteFile(hFile, pBuf, BufLen, pBytesWritten, NULL);

   if(!bSuccess)
   {
      LPVOID lpMsgBuf;

      status = GetLastError();

      if(DebugLevel & 0x00000002)
      {
         FormatMessage(
                      FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM,
                      NULL,
                      status,
                      MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
                      (LPTSTR) &lpMsgBuf,
                      0,
                      NULL
                      );

         {
            RWMutexLock::LockGuard guard(coutMux);
            printf("Error in CTIWrite (Win32 Write File): GetLastError() = %5d\n\t%s", status, lpMsgBuf);
         }
         // Free the buffer.
         LocalFree( lpMsgBuf );
      }
   }
   return(status);        // Win32 returns positive if successfull.
}




APIRET IM_EX_CTIBASE CTICreateEventSem(PSZ pszName, PHEV phev, ULONG Flags, BOOL32 bState)
{
   /**********************************************************************
      * CreateMutex pp. 1272 in the Windows NT Win32 SuperBible
      */
   if(!(*phev = CreateEvent(  (LPSECURITY_ATTRIBUTES)NULL,  // NULL = default security
                              TRUE,                         // Manual ReSet Event
                              bState,                       // TRUE == owned by this thread
                              pszName)))
   {
      {
         RWMutexLock::LockGuard guard(coutMux);
         printf("Error in CreateEvent = %u for %s\n",GetLastError(), pszName);
      }
      return(APIRET)1;    // This is an error!!!!
   }
   else
   {
      return(APIRET)0;    // OS2 returns zero on success so do that.
   }
}

APIRET IM_EX_CTIBASE CTICloseEventSem(PHEV phev)
{
   DWORD err;
   if(!(CloseHandle(*phev)))
   {
      if((err = GetLastError()) != ERROR_FILE_NOT_FOUND)
      {
         RWMutexLock::LockGuard guard(coutMux);
         printf("Error in CloseEvent = %05d\n",err);
      }
      return(APIRET)1;    // This is an error!!!!
   }
   else
   {
      return(APIRET)0;    // OS2 returns zero on success so do that.
   }
}


APIRET  CTIResetEventSem(HEV hev, PULONG pulPostCt)
{
   return(!(ResetEvent(hev)));
}

APIRET IM_EX_CTIBASE CTIPostEventSem(HEV hev)
{
   return(!(SetEvent(hev)));
}


APIRET IM_EX_CTIBASE CTIOpen (
                             PSZ         pszFileName,      // OS2 & Win32
                             PHFILE      pHf,              // OS2 & Win32
                             PULONG      pAction,          // OS2
                             ULONG       ulFSize,          // OS2
                             ULONG       ulAttrib,         //
                             ULONG       Flags,
                             ULONG       Mode,
                             PEAOP2      peaop2
                             )
{
   DWORD    dwAccess = 0;      // GENERIC_READ/WRITE  - Attrib in OS2.
   DWORD    dwShare  = 0;      // share mode (Mode in OS2) FILE_SHARE_DELETE/READ/WRITE
   DWORD    dwCreate = 0;      // how to create (Flags for OS2)
   DWORD    dwAttFlags = 0;    // file attributes

   // Set the attributes
   if(ulAttrib & FILE_NORMAL)    dwAttFlags  |= FILE_ATTRIBUTE_NORMAL;
   if(ulAttrib & FILE_READONLY)  dwAttFlags  |= FILE_ATTRIBUTE_READONLY;
   if(ulAttrib & FILE_HIDDEN)    dwAttFlags  |= FILE_ATTRIBUTE_HIDDEN;
   if(ulAttrib & FILE_SYSTEM)    dwAttFlags  |= FILE_ATTRIBUTE_SYSTEM;
   if(ulAttrib & FILE_ARCHIVED)  dwAttFlags  |= FILE_ATTRIBUTE_ARCHIVE;

   dwCreate    |= OPEN_EXISTING;

   // Set the creation flags
   if(Flags & OPEN_ACTION_FAIL_IF_EXISTS) dwCreate    |= CREATE_NEW;
   else if(Flags & OPEN_ACTION_CREATE_IF_NEW) dwCreate    |= OPEN_ALWAYS;
   else if(Flags & OPEN_ACTION_REPLACE_IF_EXISTS) dwCreate    |= CREATE_ALWAYS;
   else if((Flags & OPEN_ACTION_OPEN_IF_EXISTS) || (Flags & OPEN_ACTION_FAIL_IF_NEW)) dwCreate    |= OPEN_EXISTING;

   // This is the tricky one.... First lets Get the sharing attributes
   if(Mode & OPEN_SHARE_DENYNONE)              dwShare = (FILE_SHARE_READ | FILE_SHARE_WRITE);
   if(Mode & OPEN_SHARE_DENYWRITE)             dwShare = (FILE_SHARE_READ);
   if(Mode & OPEN_SHARE_DENYREAD)              dwShare = (FILE_SHARE_WRITE);
   if(Mode & OPEN_SHARE_DENYREADWRITE)         dwShare = 0;

   // Now the access parameters are gleaned from the Mode
   if(Mode & OPEN_ACCESS_READONLY)             dwAccess |= GENERIC_READ;
   if(Mode & OPEN_ACCESS_WRITEONLY)            dwAccess |= GENERIC_WRITE;
   if(Mode & OPEN_ACCESS_READWRITE)            dwAccess |= GENERIC_READ | GENERIC_WRITE;

   // OS2 Mode also contains these elements... which are in Win32s AttrandFlags member

   if(Mode & OPEN_FLAGS_SEQUENTIAL)            dwAttFlags |= FILE_FLAG_SEQUENTIAL_SCAN;
   if(Mode & OPEN_FLAGS_RANDOM)                dwAttFlags |= FILE_FLAG_RANDOM_ACCESS;
   if(Mode & OPEN_FLAGS_NO_CACHE)              dwAttFlags |= FILE_FLAG_WRITE_THROUGH;
   if(Mode & OPEN_FLAGS_WRITE_THROUGH)         dwAttFlags |= FILE_FLAG_WRITE_THROUGH;

//   if (Mode & OPEN_FLAGS_NOINHERIT)             dwAttFlags |= FILE_FLAG_;
//   if (Mode & OPEN_FLAGS_NO_LOCALITY)           dwAttFlags |= FILE_FLAG_;
//   if (Mode & OPEN_FLAGS_RANDOMSEQUENTIAL)      dwAttFlags |= FILE_FLAG_;
//   if (Mode & OPEN_FLAGS_FAIL_ON_ERROR)         dwAttFlags |= FILE_FLAG_;
//   if (Mode & OPEN_FLAGS_DASD)                  dwAttFlags |= FILE_FLAG_;

   /*
    *  If this is done on a port > COM9, we need to add the device identifiaer \\.\COMXX to the name
    */

   char portname[80];

   if( !strnicmp(pszFileName, "COM", 3) )
   {
      strcpy(portname, "\\\\.\\");
      strncat(portname,pszFileName, 70);
   }
   else
   {
      strncpy(portname, pszFileName, 80);
   }

   *pHf = CreateFile( portname, dwAccess, dwShare, NULL, dwCreate, dwAttFlags, NULL );

   if(*pHf == INVALID_HANDLE_VALUE)
   {
      int err = GetLastError();

      if(err == ERROR_FILE_NOT_FOUND || err == ERROR_PATH_NOT_FOUND)
      {
         // printf("Error in CTIOpen (Win32 CreateFile):(%s) %5d\n", pszFileName,err);
      }
      else
      {
         LPVOID lpMsgBuf;

         if(DebugLevel & 0x00000002)
         {
            FormatMessage(
                         FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM,
                         NULL,
                         err,
                         MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
                         (LPTSTR) &lpMsgBuf,
                         0,
                         NULL
                         );

            // Display the string.
            DisplayError(err, __LINE__, __FILE__, "CTIOpen");
            // Free the buffer.
            LocalFree( lpMsgBuf );
         }

         *pHf = NULL; // Most of the code expects this!
      }
      return(APIRET)err;    // Error of some sort occured! timeout or otherwise
   }
   else
   {
      *pAction = 1;
      return(APIRET)0;    // We have the file handle now!
   }
}

void  IM_EX_CTIBASE   DebugLine(char *fName, char *funcName, int lineNum)
{
   char  temp[1024];
   int   end = strlen(fName)-1;
   char  *baseStart;

   strcpy(temp,fName);
   baseStart = &temp[end];

   while(*baseStart != '\\' && (baseStart != temp))
   {
      baseStart--;
   }

   {
      RWMutexLock::LockGuard guard(coutMux);
      fprintf(stderr,"*** DEBUG: [TID: 0x%04X (%4d)] %.13s : %.20s() (Line: %4d)\n",CurrentTID(),CurrentTID(), ++baseStart,funcName, lineNum);
   }

}

void IM_EX_CTIBASE DisplayError(DWORD Error, DWORD Line, char* FileName, char *Func)
{
   void* lpMsgBuf;

   if(DebugLevel & 0x00000002)
   {
      FormatMessage(
                   FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM,
                   NULL,
                   Error,
                   MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
                   (LPTSTR) &lpMsgBuf,
                   0,
                   NULL
                   );

      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " ERROR " << Error << " executing in " << FileName << " (" << Line << ")." << endl;
         dout << "\tWin32 Error: " << lpMsgBuf << endl;
      }

      // Free the buffer.
      LocalFree( lpMsgBuf );
   }
}

void IM_EX_CTIBASE DumpOutMessage(void *Mess)
{
    OUTMESS  *Om = (OUTMESS*)(Mess);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "  Device ID:          " << Om->DeviceID << endl;
        dout << "  Target ID:          " << Om->TargetID << endl;
        dout << "  Port:               " << Om->Port << endl;
        dout << "  Remote:             " << Om->Remote << endl;
        dout << "  Sequence:           " << Om->Sequence << endl;
        dout << "  Priority:           " << Om->Priority << endl;
        dout << "  TimeOut:            " << Om->TimeOut << endl;
        dout << "  Retry:              " << Om->Retry << endl;
        dout << "  OutLength:          " << Om->OutLength << endl;
        dout << "  InLength:           " << Om->InLength << endl;
        dout << "  Source:             " << Om->Source << endl;
        dout << "  Destination:        " << Om->Destination << endl;
        dout << "  Command:            " << Om->Command << endl;
        dout << "  Function:           " << Om->Function << endl;
        dout << "  EventCode:          " << Om->EventCode << endl;
    }
}

