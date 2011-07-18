#pragma once

   #include <stdlib.h>
   #include <stdio.h>
   #include <time.h>
   #include <sys\timeb.h>

   #ifdef _WIN32

     #if !defined (NOMINMAX)
     #define NOMINMAX
     #endif

     #include <windows.h>
     #include "os2_2w32.h"
   #endif

/*****************************************************************************
 * Accck! Pthht! Some Debug Tools
 ****************************************************************************/
   #define DEBUGLEVEL = 0x00010001

/* Combine l & h to form a 32 bit quantity. */
   #ifndef MAKEULONG
      #define MAKEULONG(l, h)  ((ULONG)(((USHORT)(l)) | ((ULONG)((USHORT)(h))) << 16))
   #endif
   #ifndef MAKELONG
      #define MAKELONG(l, h)   ((LONG)MAKEULONG(l, h))
   #endif

/* Combine l & h to form a 16 bit quantity. */
   #define MAKEUSHORT(l, h) (((USHORT)(l)) | ((USHORT)(h)) << 8)
   #define MAKESHORT(l, h)  ((SHORT)MAKEUSHORT(l, h))


APIRET   IM_EX_CTIBASE CTISleep              (ULONG SleepTime);
APIRET   IM_EX_CTIBASE CTIScanEnv            (PSZ Name, PSZ *Result);
void     IM_EX_CTIBASE CTIExit               (ULONG ExitType, ULONG ExitCode);
APIRET   IM_EX_CTIBASE CTICloseMutexSem      (PHMTX phmtx);
APIRET   IM_EX_CTIBASE CTICreateMutexSem     (PSZ pszName, PHMTX phmtx, ULONG Flags, BOOL bState);
APIRET   IM_EX_CTIBASE CTIRequestMutexSem    (HMTX hmtx, ULONG time);
APIRET   IM_EX_CTIBASE CTIReleaseMutexSem    (HMTX hmtx);
APIRET   IM_EX_CTIBASE CTIDelete             (PSZ oldname);
APIRET   IM_EX_CTIBASE CTIClose              (HANDLE &hFile);
APIRET   IM_EX_CTIBASE CTIRead               (HANDLE &hFile, PVOID pBuf, ULONG BufLen, PULONG pBytesRead);
APIRET   IM_EX_CTIBASE CTIWrite              (HANDLE &hFile, PVOID pBuf, ULONG BufLen, PULONG pBytesWritten);

APIRET   IM_EX_CTIBASE CTISetPriority        (ULONG ulPriClass, int tPri);

DWORD    IM_EX_CTIBASE CurrentTID(void);

APIRET IM_EX_CTIBASE CTICreateEventSem      (PSZ pszName, PHEV phev, ULONG Flags, BOOL32 bState);
APIRET IM_EX_CTIBASE CTICloseEventSem       (PHEV phev);
APIRET IM_EX_CTIBASE CTIResetEventSem       (HEV hev, PULONG pulPostCt);
APIRET IM_EX_CTIBASE CTIPostEventSem        (HEV hev);

APIRET   IM_EX_CTIBASE CTIOpen             (
                                           PSZ         pszFileName,      // OS2 & Win32
                                           PHFILE      pHf,              // OS2 & Win32
                                           PULONG      pAction,          // OS2
                                           ULONG       ulFSize,          // OS2
                                           ULONG       ulAttrib,         //
                                           ULONG       Flags,
                                           ULONG       Mode,
                                           PEAOP2      peaop2
                                           );

void  IM_EX_CTIBASE DisplayError(DWORD Error, DWORD Line, char* FileName, char *Func);

