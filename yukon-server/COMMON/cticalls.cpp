#include "yukon.h"

#include <windows.h>
#include <stdio.h>
#include <string.h>
#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"
#include "dllbase.h"
#include "dsm2.h"
#include "logger.h"

/* CTI Sleep Function */
APIRET IM_EX_CTIBASE CTISleep (ULONG SleepTime)
{
#if defined(__OS2__)
   return(DosSleep (SleepTime));
#elif defined(_WIN32)
   Sleep (SleepTime);      // Win32 does not return anything.
   return((APIRET)0);    // Return a OS/2 NO_ERROR!
#elif defined(__MOTIF__)
   return();
#endif
}

APIRET IM_EX_CTIBASE CTIScanEnv ( PSZ Name, PSZ *Result )
{
#if defined(__OS2__)

   return(DosScanEnv (Name, Result));

#elif defined(_WIN32)

#if 0
   int     status = 1;
   int     pos = 0;
   char    Key[512] = {'\0'};      // Anything larger is considered unruly by this programmer
   LPTSTR  lpValue;
   LPTSTR  lpNext;

   LPTSTR  lpStr = GetEnvironmentStrings();
   LPTSTR  lpTemp = lpStr;

   while(*lpTemp)
   {

      // printf("\nFull:  %s\n",lpTemp);
      lpNext = lpTemp+strlen(lpTemp)+1;

      lpValue = strstr(lpTemp+1, "=");         // This is the position of the = sign in the first key

      while(lpTemp != lpValue)
      {              // Make a copy of the Ksy Value
         Key[pos] = *lpTemp;
         pos++;
         lpTemp++;
      }
      Key[pos] = '\0';                         // Terminate the "key" string
      /****************************
       * lpTemp should point at the equal sign now just like lpValue
       ****************************/


      if(!strcmp(Name, Key))
      {
         *Result = ((lpTemp+1));
         status = 0;
         break;
      }
      else
      {
         pos = 0;
         lpTemp = lpNext;
      }
   }

   FreeEnvironmentStrings( lpStr );

   return(APIRET)status;   // Couldn't Find it.
#else

   int status = -1;

   *Result = NULL;


   static char Env[512];    // This IS be non-threadsafe

   if( GetEnvironmentVariable(Name, Env, 500) )
   {
      *Result = Env;
      status = 0;
   }

   return (status);

#endif



#elif defined(__MOTIF__)
   return();
#endif
}


VOID IM_EX_CTIBASE CTIExit (ULONG ExitType, ULONG ExitCode)
{
#if defined(__OS2__)

   DosExit (ExitType, ExitCode);

#elif defined(_WIN32)


   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " **** Checkpoint CTIExit (deprecated) **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   ExitThread(ExitCode);

#elif defined(__MOTIF__)
   return;
#endif

}


APIRET IM_EX_CTIBASE CTIOpenMutexSem(PSZ pszName, PHMTX phmtx, DWORD dwAccessFlags)
{
#if __OS2__
   char temp[1024] = {"\\SEM32\\"
      strcat(temp,pszName);

      return(DosOpenMutexSem(temp, phmtx));
#elif defined(_WIN32)
   DWORD err;
   /**********************************************************************
      * OpenMutex pp. 1328 in the Windows NT Win32 API SuperBible
      *
      * DWORD dwAccessFlags   may be MUTEX_ALL_ACCESS or SYNCHRONIZE
      * Hard coded FALSE is the Inheritance property.  This Mutex shall not be inherited
      *     by child processes.
      */
   if(!(*phmtx = OpenMutex(dwAccessFlags, FALSE, pszName)))
   {
      if((err = GetLastError()) != ERROR_FILE_NOT_FOUND)
      {
         RWMutexLock::LockGuard guard(coutMux);
         printf("Error in OpenMutex (%s) = %0d\n", pszName ,err);
      }
      return(APIRET)1;    // This is an error!!!!
   }
   else
   {
      return(APIRET)0;    // OS2 returns zero on success so do that.
   }
#endif
}

APIRET IM_EX_CTIBASE CTICloseMutexSem(PHMTX phmtx)
{
#if __OS2__
   return(DosCloseMutexSem(phmtx));
#elif defined(_WIN32)
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
#endif
}


APIRET IM_EX_CTIBASE CTICreateMutexSem(PSZ pszName, PHMTX phmtx, ULONG Flags, BOOL bState)
{
#if __OS2__
   char temp[1024] = {"\\SEM32\\"
      strcat(temp,pszName);

      return(DosCreateMutexSem(temp, phmtx, Flags, bState));
#elif defined(_WIN32)

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
#endif
}

APIRET IM_EX_CTIBASE CTIRequestMutexSem(HMTX hmtx, ULONG time)
{
#if __OS2__
   return(DosRequestMutexSem(hmtx, time));
#elif defined(_WIN32)
   DWORD rVal;

   rVal = WaitForSingleObject(hmtx, time);

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
              dout << RWTime() << " **** CTIRequestMutexSem: Wait Abandoned **** " << __FILE__ << " (" << __LINE__ << "). Error = " << GetLastError() << endl;
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
              dout << RWTime() << " **** CTIRequestMutexSem: WaitResult = " << rVal << " **** " << __FILE__ << " (" << __LINE__ << "). Error = " << GetLastError() << endl;
           }

           rVal = 1;
           break;
       }
   }

   return (APIRET)rVal;    // We have the mutex now!
#endif
}

APIRET IM_EX_CTIBASE CTIReleaseMutexSem(HMTX hmtx)
{
#if __OS2__
   return(DosReleaseMutexSem(hmtx));
#elif defined(_WIN32)
   return(!(ReleaseMutex(hmtx)));
#endif
}

APIRET IM_EX_CTIBASE CTIMove(PSZ old, PSZ nw)
{
#if __OS2__
   return(DosMove(old, nw));
#elif defined(_WIN32)
   return(!(MoveFile(old,nw))); // OS2 wants 0 ret if OK NT rets TRUE on OK
#endif
}

APIRET IM_EX_CTIBASE CTIDelete(PSZ old)
{
#if __OS2__

   return(DosDelete(old));
#elif defined(_WIN32)
   return(!(DeleteFile(old)));   // OS2 wants 0 ret if OK NT rets TRUE on OK
#endif
}

/* Function to return the current thread ID - 32 bit only */
DWORD      IM_EX_CTIBASE CurrentTID()
{
#if __OS2__

   PTIB ThreadInfoBlock;
   PPIB ProcessInfoBlock;

   /* Get the information Blocks */
   DosGetInfoBlocks (&ThreadInfoBlock,
                     &ProcessInfoBlock);

   /* Return the current thread ID */
   return(ThreadInfoBlock->tib_ptib2->tib2_ultid);

#elif defined(_WIN32)

   return GetCurrentThreadId();

#endif
}

/* Function to return the current Process ID - 32 bit only */
DWORD      IM_EX_CTIBASE CurrentPID()
{
#if __OS2__

   PTIB ThreadInfoBlock;
   PPIB ProcessInfoBlock;

   /* Get the information Blocks */
   DosGetInfoBlocks (&ThreadInfoBlock,
                     &ProcessInfoBlock);

   /* Return the current Process ID */
   return(ProcessInfoBlock->pib_ulpid);

#elif defined(_WIN32)

   return GetCurrentProcessId();

#endif
}


APIRET IM_EX_CTIBASE CTIClose(HANDLE &hFile)
{
#if __OS2__
   return(DosClose(hFile));
#elif defined(_WIN32)

   DWORD status = NORMAL;

   if( !CloseHandle(hFile) )
   {
      status = !NORMAL;
   }

   hFile = (HANDLE)NULL;

   return(status);        // Win32 returns positive if successfull.
#endif
}

APIRET IM_EX_CTIBASE CTIRead   (   HANDLE    &hFile,
                                   PVOID    pBuf,
                                   ULONG    BufLen,
                                   PULONG   pBytesRead
                               )
{
#if __OS2__
   return(DosRead(hFile, pBuf, BufLen, pBytesRead));
#elif defined(_WIN32)
   BOOL  bSuccess = FALSE;
//   fprintf(stderr,"*** DEBUG CTIRead File Handle %8d\n",hFile);
   bSuccess = ReadFile(hFile, pBuf, BufLen, pBytesRead, NULL);
//   fprintf(stderr,"*** DEBUG CTIRead File Handle %8d",hFile);
//   fprintf(stderr," * %d bytes DONE* \n",*pBytesRead);

   return(!bSuccess);        // Win32 returns positive if successfull.
#endif
}

APIRET IM_EX_CTIBASE CTIWrite         (   HANDLE    &hFile,
                                          PVOID    pBuf,
                                          ULONG    BufLen,
                                          PULONG   pBytesWritten
                                      )
{
#if __OS2__
   return(DosWrite(hFile, pBuf, BufLen, pBytesWritten));
#elif defined(_WIN32)
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
#endif
}




APIRET IM_EX_CTIBASE CTIGetDateTime      (PDATETIME dt)
{
#if __OS2__
   return(DosGetDateTime(dt));
#elif defined(_WIN32)

/* Win32
typedef struct _SYSTEMTIME {  // st
    WORD wYear;
    WORD wMonth;
    WORD wDayOfWeek;
    WORD wDay;
    WORD wHour;
    WORD wMinute;
    WORD wSecond;
    WORD wMilliseconds;
} SYSTEMTIME;
*/
   SYSTEMTIME  sysTime;

   GetSystemTime(&sysTime);

   dt->hours        = (UCHAR) sysTime.wHour;
   dt->minutes      = (UCHAR) sysTime.wMinute;
   dt->seconds      = (UCHAR) sysTime.wSecond;
   dt->hundredths   = (UCHAR) (sysTime.wMilliseconds/10);

   dt->day          = (UCHAR)  sysTime.wDay;
   dt->month        = (UCHAR)  sysTime.wMonth;
   dt->year         = (USHORT) sysTime.wYear;
   dt->weekday      = (UCHAR)  sysTime.wDayOfWeek;

   /* CGP - I'm not sure what to do with this yet... FIX?? */
   dt->timezone  = (SHORT)  -1;

   return((APIRET)0);        // Win32 returns positive if successfull.
#endif
}

APIRET IM_EX_CTIBASE CTISetDateTime      (PDATETIME dt)
{
#if __OS2__

   return(DosSetDateTime(dt));

#elif defined(_WIN32)

/* Win32
typedef struct _SYSTEMTIME {  // st
    WORD wYear;
    WORD wMonth;
    WORD wDayOfWeek;
    WORD wDay;
    WORD wHour;
    WORD wMinute;
    WORD wSecond;
    WORD wMilliseconds;
} SYSTEMTIME;
*/
   SYSTEMTIME  sysTime;

   sysTime.wHour          = (WORD) dt->hours;
   sysTime.wMinute     = (WORD) dt->minutes;
   sysTime.wSecond     = (WORD) dt->seconds;
   sysTime.wMilliseconds  = (WORD) (dt->hundredths * 10);

   sysTime.wDay        = (WORD) dt->day;
   sysTime.wMonth         = (WORD) dt->month;
   sysTime.wYear        = (WORD) dt->year;
   sysTime.wDayOfWeek     = (WORD) dt->weekday;

   return(!(SetSystemTime(&sysTime)));        // Win32 returns positive if successfull.
#endif
}

APIRET IM_EX_CTIBASE CTISetFilePtr        (HANDLE &hFile, LONG lDistance, ULONG ulMode, PULONG pNewPtr)
{
#if __OS2__

   return(DosSetFilePtr(hFile, lDistance, ulMode, pNewPtr));

#elif defined(_WIN32)
   *pNewPtr = SetFilePointer(
                            hFile,      // handle of file
                            lDistance,  // number of bytes to move file pointer
                            NULL,       // address of high-order word of distance to move
                            ulMode      // how to move
                            );

   if(0xFFFFFFFF == *pNewPtr)
   {
      return 1;
   }
   else
   {
      return 0;
   }
#endif

}


APIRET IM_EX_CTIBASE CTICreateEventSem(PSZ pszName, PHEV phev, ULONG Flags, BOOL32 bState)
{
#if __OS2__
   char temp[1024] = {"\\SEM32\\"
      strcat(temp,pszName);

      return(DosCreateEventSem(temp, phev, Flags, bState));
#elif defined(_WIN32)

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
#endif
}

APIRET IM_EX_CTIBASE CTIOpenEventSem(PSZ pszName, PHEV phev, DWORD dwAccessFlags)
{
#if __OS2__
   char temp[1024] = {"\\SEM32\\"
      strcat(temp,pszName);

      return(DosOpenEventSem(temp, phev));
#elif defined(_WIN32)
   DWORD err;
   /**********************************************************************
      * OpenMutex pp. 1328 in the Windows NT Win32 API SuperBible
      *
      * DWORD dwAccessFlags   may be EVENT_ALL_ACCESS or SYNCHRONIZE
      * Hard coded FALSE is the Inheritance property.  This Mutex shall not be inherited
      *     by child processes.
      */
   if(!(*phev = OpenEvent(dwAccessFlags, FALSE, pszName)))
   {
      if((err = GetLastError()) != ERROR_FILE_NOT_FOUND)
      {
         RWMutexLock::LockGuard guard(coutMux);
         printf("Error in OpenEvent(%s) = %05d\n",pszName,err);
         if(err == ERROR_BAD_PATHNAME)
         {
            printf("OpenEvent Bad Pathname.... OS2 - Win32 error... Check your naming\n");
         }
      }
      return(APIRET)1;    // This is an error!!!!
   }
   else
   {
      return(APIRET)0;    // OS2 returns zero on success so do that.
   }
#endif
}

APIRET IM_EX_CTIBASE CTICloseEventSem(PHEV phev)
{
#if __OS2__
   return(DosCloseEventSem(phev));
#elif defined(_WIN32)
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
#endif
}


APIRET IM_EX_CTIBASE CTIRequestEventSem(HEV hev, PULONG pulPostCt, ULONG time)
{
#if __OS2__
   return(DosRequestEventSem(hev, pulPostCt));
#elif defined(_WIN32)
   DWORD rVal;

   rVal = WaitForSingleObject(hev, time);

   if(
     (rVal == WAIT_OBJECT_0) ||
     (rVal == WAIT_ABANDONED)
     )
   {
      return(APIRET)0;    // We have the mutex now!
   }
   else
   {
      return(APIRET)1;    // Error of some sort occured! timeout or otherwise
   }
#endif
}

APIRET IM_EX_CTIBASE CTIWaitEventSem(HEV hev, ULONG time)
{
#if __OS2__
   return(DosWaitEventSem(hev, time));
#elif defined(_WIN32)
   DWORD rVal;

   rVal = WaitForSingleObject(hev, time);

   if(
     (rVal == WAIT_OBJECT_0) ||
     (rVal == WAIT_ABANDONED)
     )
   {
      return(APIRET)0;    // We have the object now!
   }
   else
   {
      return(APIRET)1;    // Error of some sort occured! timeout or otherwise
   }
#endif
}

APIRET IM_EX_CTIBASE CTIResetEventSem(HEV hev, PULONG pulPostCt)
{
#if __OS2__
   return(DosResetEventSem(hev, pulPostCnt));
#elif defined(_WIN32)
   return(!(ResetEvent(hev)));
#endif
}

APIRET IM_EX_CTIBASE CTIPostEventSem(HEV hev)
{
#if __OS2__
   return(DosPostEventSem(hev));
#elif defined(_WIN32)
   return(!(SetEvent(hev)));
#endif
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
#if __OS2__
   return(DosOpen( pszFileName,  pHf, pAction
                   cbFile,
                   ulAttrib,
                   Flags,
                   Mode,
                   peaop2
                 )
         );
#elif defined(_WIN32)

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
#endif
}

APIRET IM_EX_CTIBASE CTIAllocSharedMem      (
                                            PPVOID         ppBaseAddress,
                                            PSZ            pszName,
                                            ULONG          ulObjectSize,
                                            ULONG          ulFlags         // This is the last OS2 arg.

                                            )
{
#if __OS2__
   CHAR     shareName[sizeof(pszName) + sizeof("\\SHAREMEM\\") + 1] = {"\\SHAREMEM\\"};

   strcat(shareName,pszName);

   return(DosAllocSharedMem  (
                             ppBaseAddress,
                             shareName,
                             ulObjectSize,
                             ulFlags         // This is the last OS2 arg.
                             ));

#elif defined(_WIN32)


/* Win32 File MApping / Shared Memory Function

HANDLE CreateFileMapping(
                           HANDLE hFile,                          // handle to file to map
                           LPSECURITY_ATTRIBUTES lpFileMappingAttributes, // optional security attributes
                           DWORD flProtect,                       // protection for mapping object
                           DWORD dwMaximumSizeHigh,               // high-order 32 bits of object size
                           DWORD dwMaximumSizeLow,                // low-order 32 bits of object size
                           LPCTSTR lpName                         // name of file-mapping object
                        );
*/
   DWORD    ProtectFlags = 0;
   DWORD    PageAccess   = 0;
   HANDLE   hShmem;

//   if(ulFlags & PAG_READ && !(ulFlags & PAG_WRITE))     ProtectFlags |= PAGE_READONLY;
//   else

   {
      RWMutexLock::LockGuard  guard(coutMux);
      cout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   ProtectFlags |= PAGE_READWRITE;        // For now we create READ/WRITE and ask for the desired options
   hShmem = CreateFileMapping(   (HANDLE)0xFFFFFFFF,     // A kernel Object (shared memory)
                                 NULL,                   // Default Security
                                 ProtectFlags,           //
                                 0,                      // OS2 limits to 2^32 cti layer does too
                                 ulObjectSize,           // number of bytes.. rounded up to page boundary
                                 pszName                 // The known name to access this
                             );

   if(!hShmem)
   {
      DWORD err = GetLastError();
      {
         RWMutexLock::LockGuard guard(coutMux);
         fprintf(stderr,"Error creating Shared Memory: %05d (%u)\n",err,err);
      }
      if(err == ERROR_BAD_PATHNAME)
      {
         RWMutexLock::LockGuard guard(coutMux);
         fprintf(stderr,"Pathname = [%s]\n",pszName);
      }
      return((APIRET)1);
   }

   if(ulFlags & PAG_READ)       PageAccess |= FILE_MAP_READ;
   if(ulFlags & PAG_WRITE)      PageAccess |= FILE_MAP_WRITE;

   *ppBaseAddress = MapViewOfFile(
                                 hShmem,
                                 PageAccess,             // Access Mode to the SHmem
                                 0,                      // high 32-bit of offSet
                                 0,                      // low
                                 0                       // Map entire shmem region
                                 );
   if(!(*ppBaseAddress))
   {
      RWMutexLock::LockGuard guard(coutMux);
      fprintf(stderr,"Error in CTIAllocSharedMem mapping the memory\n");
      return((APIRET)1);
   }

   return((APIRET)0);

#endif


}


APIRET IM_EX_CTIBASE CTIGetNamedSharedMem      (
                                               PPVOID         ppBaseAddress,
                                               PSZ            pszName,
                                               ULONG          ulFlags         // This is the last OS2 arg.
                                               )
{
#if __OS2__
   CHAR     shareName[sizeof(pszName) + sizeof("\\SHAREMEM\\") + 1] = {"\\SHAREMEM\\"};
   strcat(shareName,pszName);

   return(DosGetNamedSharedMem  (
                                ppBaseAddress,
                                shareName,
                                ulFlags         // This is the last OS2 arg.
                                ));

#elif defined(_WIN32)

   {
      RWMutexLock::LockGuard  guard(coutMux);
      cout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }
   DWORD    PageAccess   = 0;

   HANDLE   hShmem = OpenFileMapping (
                                     FILE_MAP_ALL_ACCESS,       // Desired Access
                                     FALSE,                     // bInheritance - Not
                                     pszName                    // lpName of the File Object
                                     );

   if(!hShmem)
   {
      // fprintf(stderr,"Error opening Shared Memory: This is (possibly) expected\n");
      return((APIRET)1);
   }

   // Now we need to map this file handle into our memory space

   if(ulFlags & PAG_READ)       PageAccess |= FILE_MAP_READ;
   if(ulFlags & PAG_WRITE)      PageAccess |= FILE_MAP_WRITE;

   *ppBaseAddress = MapViewOfFile(
                                 hShmem,
                                 PageAccess,             // Access Mode to the SHmem
                                 0,                      // high 32-bit of offSet
                                 0,                      // low
                                 0                       // Map entire shmem region
                                 );
   if(!(*ppBaseAddress))
   {
      RWMutexLock::LockGuard guard(coutMux);
      fprintf(stderr,"Error in CTIGetNamedSharedMem mapping the shared memory\n");
      return((APIRET)1);
   }

   return((APIRET)0);

#endif

}

APIRET IM_EX_CTIBASE CTILoadModule          (PSZ        pszObjNameBuf,
                                             ULONG      ulObjNameBuf,
                                             PSZ        pszModName,
                                             PHMODULE   pModHandle
                                            )
{
#if __OS2__
   return(DosLoadModule       (pszObjNameBuf,
                               ulObjNameBuf,
                               pszModName,
                               pModHandle
                              ));
#elif defined(_WIN32)

   /**********************************************************************
      * CreateMutex pp. 1272 in the Windows NT Win32 SuperBible
      */
   if(!(*pModHandle = LoadLibrary(pszModName)))
   {
      RWMutexLock::LockGuard guard(coutMux);
      printf("Error in LoadLibrary = %u\n",GetLastError());
      return(APIRET)1;    // This is an error!!!!
   }
   else
   {
      return(APIRET)0;    // OS2 returns zero on success so do that.
   }
#endif
}


APIRET IM_EX_CTIBASE CTIQueryProcAddr       (HMODULE    hmodModHandle,
                                             ULONG      ulOrdinal,
                                             PSZ        pszProcName,
                                             PFN        procAddr
                                            )
{
#if __OS2__
   return(DosQueryProcAddr    (hmodModHandle,
                               ulOrdinal,
                               pszProcName,
                               procAddr
                              ));
#elif defined(_WIN32)

   /**********************************************************************
      * CreateMutex pp. 1272 in the Windows NT Win32 SuperBible
      */
   if(!(procAddr = GetProcAddress(hmodModHandle, pszProcName)))
   {
      RWMutexLock::LockGuard guard(coutMux);
      printf("Error in GetProcAddress = %u\n",GetLastError());
      return(APIRET)1;    // This is an error!!!!
   }
   else
   {
      return(APIRET)0;    // OS2 returns zero on success so do that.
   }
#endif
}

APIRET IM_EX_CTIBASE CTISetMaxFH          (ULONG ulFH)
{
#if __OS2__
   return(DosSetMaxFH    (ulFH));
#elif defined(_WIN32)
   return(0);
#endif
}

APIRET IM_EX_CTIBASE CTISetPriority  (ULONG   ulScope,
                                      ULONG   ulPriClass,
                                      LONG    lPriDelta,
                                      ULONG   ulID
                                     )
{
#if __OS2__
   return(DosSetPriority    (ulScope,
                             ulPriClass,
                             lPriDelta,
                             ulID));
#elif defined(_WIN32)

    // Make some priority decisions for the poor OS/2 programmers.
    int tPri;

    if(lPriDelta < -10)
    {
        tPri = THREAD_PRIORITY_LOWEST;
    }
    else if(lPriDelta < 0)
    {
        tPri = THREAD_PRIORITY_BELOW_NORMAL;
    }
    else if(lPriDelta == 0)
    {
        tPri = THREAD_PRIORITY_NORMAL;
    }
    else if(lPriDelta < 30)
    {
        tPri = THREAD_PRIORITY_ABOVE_NORMAL;
    }
    else if(lPriDelta < 32)
    {
        tPri = THREAD_PRIORITY_HIGHEST;
    }

    if(ulPriClass == 0) ulPriClass = GetPriorityClass(GetCurrentProcess());

    if(SetPriorityClass(GetCurrentProcess(), ulPriClass) )
    {
        return(!SetThreadPriority((HANDLE)GetCurrentThread(), tPri));
    }
   else
   {
        DWORD lerror = GetLastError();
        printf("Error in CTISetPriority %d\n", lerror);
      return(1);
   }

#endif
}

APIRET IM_EX_CTIBASE CTICreatePipe   (
                                     PHPIPE         pReadHandle,
                                     PHPIPE         pWriteHandle,
                                     ULONG          ulSize
                                     )
{
#if __OS2__
   return(DosCreatePipe    (pReadHandle, pWriteHandle, ulSize));

#elif defined(_WIN32)

   BOOL                          bSuccess;
   SECURITY_ATTRIBUTES           secAttr;

   secAttr.bInheritHandle        = TRUE;
   secAttr.lpSecurityDescriptor  = NULL;
   secAttr.nLength               = sizeof(SECURITY_ATTRIBUTES);


   bSuccess = CreatePipe(pReadHandle, pWriteHandle, &secAttr, ulSize);


   if(!bSuccess)
   {
      RWMutexLock::LockGuard guard(coutMux);
      printf("Error in CreatePipe = %u\n",GetLastError());
      return((APIRET) 1);
   }
   else
   {
      return((APIRET) 0);
   }


#endif

}

APIRET IM_EX_CTIBASE CTICreateNPipe         (
                                            PSZ            pszName,
                                            PHPIPE         pPipeHandle,
                                            ULONG          ulOpenMode,
                                            ULONG          ulPipeMode,
                                            ULONG          ulOutSize,
                                            ULONG          ulInSize,
                                            ULONG          ulTimeOut
                                            )
{
#if __OS2__
   return(DosCreateNPipe    (
                            pszName,
                            pPipeHandle,
                            ulOpenMode,
                            ulPipeMode,
                            ulOutSize,
                            ulInSize,
                            ulTimeOut
                            ));

#elif defined(_WIN32)

   char temp[1024] = {"\\\\."};

   DWORD OpenMode                = 0;
   DWORD PipeMode                = 0;
   SECURITY_ATTRIBUTES           secAttr;

   secAttr.bInheritHandle        = TRUE;
   secAttr.lpSecurityDescriptor  = NULL;
   secAttr.nLength               = sizeof(SECURITY_ATTRIBUTES);

   strcat(temp,pszName);

   if((ulOpenMode & NP_ACCESS_DUPLEX) == NP_ACCESS_DUPLEX)
   {
      OpenMode |= PIPE_ACCESS_DUPLEX;
   }
   if((ulOpenMode & NP_ACCESS_OUTBOUND) == NP_ACCESS_OUTBOUND)
   {
      OpenMode |= PIPE_ACCESS_OUTBOUND;
   }
   if((ulOpenMode & NP_ACCESS_INBOUND) == NP_ACCESS_INBOUND)
   {
      OpenMode |= PIPE_ACCESS_INBOUND;
   }

   if(ulOpenMode & NP_NOWRITEBEHIND)
   {
      OpenMode |= FILE_FLAG_WRITE_THROUGH;
   }

   OpenMode |= FILE_FLAG_WRITE_THROUGH;


   if(ulPipeMode & NP_NOWAIT)             PipeMode |= PIPE_NOWAIT;
   if(ulPipeMode & NP_TYPE_MESSAGE)       PipeMode |= PIPE_TYPE_MESSAGE;
   if(ulPipeMode & NP_READMODE_MESSAGE)   PipeMode |= PIPE_READMODE_MESSAGE;


   // fprintf(stderr,"--- DEBUG: CNP: %s OpenMode = 0x%08X PipeMode = 0x%08X\n",temp, OpenMode, PipeMode);

   *pPipeHandle = CreateNamedPipe(
                                 temp,
                                 OpenMode,
                                 PipeMode,
                                 PIPE_UNLIMITED_INSTANCES,    // As many as there is memory for.
                                 ulOutSize,
                                 ulInSize,
                                 ulTimeOut,
                                 &secAttr                         // Default Security
                                 );


   if(*pPipeHandle == INVALID_HANDLE_VALUE)
   {
      RWMutexLock::LockGuard guard(coutMux);
      printf("Error in CreateNamedPipe = %u\n",GetLastError());
      return((APIRET) 1);
   }
   else
   {
//      fprintf(stderr,"*** DEBUG: CTICreateNPipe  Handle %8d\n",*pPipeHandle);
      return((APIRET) 0);
   }


#endif
}


APIRET IM_EX_CTIBASE CTIConnectNPipe        (  HPIPE         PipeHandle)
{
#if __OS2__
   return(DosConnectNPipe    (PipeHandle));

#elif defined(_WIN32)

   if(!ConnectNamedPipe(PipeHandle, NULL))
   {
      RWMutexLock::LockGuard guard(coutMux);
      printf("Error in ConnectNamedPipe = %u\n",GetLastError());
      return((APIRET) 1);
   }
   else
   {
      return((APIRET) 0);
   }

#endif
}

APIRET IM_EX_CTIBASE CTIDisConnectNPipe        (  HPIPE         PipeHandle)
{
#if __OS2__
   return(DosDisConnectNPipe    (PipeHandle));

#elif defined(_WIN32)

   if(!DisconnectNamedPipe(PipeHandle))
   {
      printf("Error in ConnectNamedPipe = %u\n",GetLastError());
      return((APIRET) 1);
   }
   else
   {
      return((APIRET) 0);
   }

#endif
}

APIRET IM_EX_CTIBASE CTIPeekNPipe        (
                                         HPIPE          PipeHandle,
                                         PVOID          pBuf,
                                         ULONG          ulBufLen,
                                         PULONG         pBytesRead,
                                         PAVAILDATA     pBytesAvail,
                                         PULONG         pPipeState
                                         )
{
#if __OS2__
   return(DosPeekNPipe    (   PipeHandle,
                              pBuf,
                              ulBufLen,
                              pBytesRead,
                              pBytesAvail,
                              pPipeState
                          ));

#elif defined(_WIN32)

   DWORD cPipe=0;
   DWORD cMess=0;

   BOOL  result = PeekNamedPipe(
                               PipeHandle,
                               pBuf,
                               ulBufLen,
                               pBytesRead,
                               &cPipe,
                               &cMess
                               );

   pBytesAvail->cbpipe     = (USHORT)cPipe;     // CGP May lose data here ...
   pBytesAvail->cbmessage  = (USHORT)cMess;

   if(!result)
   {
      RWMutexLock::LockGuard guard(coutMux);
      printf("Error in PeekNamedPipe = %u\n",GetLastError());
      return((APIRET) 1);
   }
   else
   {
      return((APIRET) 0);
   }
#endif
}


APIRET IM_EX_CTIBASE CTIWaitNPipe(
                                 PSZ            pszFileName,
                                 ULONG          ulTimeOut
                                 )
{
#if __OS2__
   return(DosWaitNPipe    (
                          pszFileName,
                          ulTimeOut
                          ));

#elif defined(_WIN32)

   DWORD cPipe=0;
   DWORD cMess=0;

   BOOL  result = WaitNamedPipe(
                               pszFileName,
                               ulTimeOut
                               );

   if(!result)
   {
      RWMutexLock::LockGuard guard(coutMux);
      printf("Error in WaitNamedPipe = %u\n",GetLastError());
      return((APIRET) 1);
   }
   else
   {
      return((APIRET) 0);
   }
#endif
}


VOID  IM_EX_CTIBASE   DebugLine(char *fName, char *funcName, int lineNum)
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

      #if defined(_WIN32)
      FormatMessage(
                   FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM,
                   NULL,
                   Error,
                   MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
                   (LPTSTR) &lpMsgBuf,
                   0,
                   NULL
                   );
      #else

      #error "Fix this code"

      #endif

      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " ERROR " << Error << " executing in " << FileName << " (" << Line << ")." << endl;
         dout << "\tWin32 Error: " << lpMsgBuf << endl;
      }

      // Free the buffer.
      #if defined(_WIN32)
      LocalFree( lpMsgBuf );
      #endif
   }
   return;
}

void IM_EX_CTIBASE DumpOutMessage(void *Mess)
{
    OUTMESS  *Om = (OUTMESS*)(Mess);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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



/*
 *  May be called by any thread wanting to observe a shutdown operation
 */

HANDLE InitializeShutdownEvent(LPCTSTR lName)
{
   HANDLE evShutdown = NULL;

   if(NULL == (evShutdown = OpenEvent(  EVENT_ALL_ACCESS, FALSE, lName)))
   {
      if(NULL == (evShutdown = CreateEvent(NULL, TRUE, FALSE, lName)))
      {
         RWMutexLock::LockGuard guard(coutMux);
         printf("Unable to create shutdown event %s\n", lName);
      }
   }

   return evShutdown;
}

