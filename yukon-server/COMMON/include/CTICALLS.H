#pragma warning( disable : 4786)
#ifndef _cticalls_h
   #define _cticalls_h


   #include <stdlib.h>
   #include <stdio.h>
   #include <time.h>
   #include <sys\timeb.h>

   #ifdef _WIN32
      #include <windows.h>
      #include "os2_2w32.h"
   #endif

/*****************************************************************************
 * Accck! Pthht! Some Debug Tools
 ****************************************************************************/
   #define DEBUGLEVEL = 0x00010001

   #if 1
      #define  LINEDEBUG   {DebugLine(__FILE__, __FUNCTION__, __LINE__);}
   #else
      #define  LINEDEBUG
      #error "Ah pucky doodles"
   #endif

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

/* Define the various types that are used */
   #ifndef CHAR
      #define CHAR char
   #endif

   #ifndef UCHAR
      #define UCHAR unsigned char
   #endif

   #ifndef BYTE
      #define BYTE unsigned char
   #endif

   #ifndef SHORT
      #define SHORT short
   #endif

   #ifndef USHORT
      #define USHORT unsigned short
   #endif

   #ifndef UINT
      #define UINT unsigned int
   #endif

   #ifndef LONG
      #define LONG long
   #endif

   #ifndef ULONG
      #define ULONG unsigned long
   #endif

   #ifndef FLOAT
      #define FLOAT float
   #endif

   #ifndef VOID
      #define VOID void
   #endif


   #ifndef EXIT_THREAD
      #define EXIT_THREAD        0
   #endif

   #ifndef EXIT_PROCESS
      #define EXIT_PROCESS       1
   #endif


   #ifndef __OS2__
typedef struct _AVAILDATA              /* AVAILDATA */
{
   USHORT      cbpipe;            /* bytes left in the pipe */
   USHORT      cbmessage;         /* bytes left in the current message */
} AVAILDATA;
typedef AVAILDATA *PAVAILDATA;


typedef struct _FTIME
{
   UINT   twosecs : 5;
   UINT   minutes : 6;
   UINT   hours   : 5;
} FTIME;
typedef FTIME *PFTIME;

typedef struct _FDATE           /* fdate */
{
   UINT   day     : 5;
   UINT   month   : 4;
   UINT   year    : 7;
} FDATE;
typedef FDATE   *PFDATE;

/*** Time support */

typedef struct _DATETIME      /* date */
{
   UCHAR   hours;
   UCHAR   minutes;
   UCHAR   seconds;
   UCHAR   hundredths;
   UCHAR   day;
   UCHAR   month;
   USHORT  year;
   SHORT   timezone;
   UCHAR   weekday;
} DATETIME;
typedef DATETIME *PDATETIME;

typedef struct _FILESTATUS      /* fsts */
{
   FDATE  fdateCreation;
   FTIME  ftimeCreation;
   FDATE  fdateLastAccess;
   FTIME  ftimeLastAccess;
   FDATE  fdateLastWrite;
   FTIME  ftimeLastWrite;
   ULONG  cbFile;
   ULONG  cbFileAlloc;
   USHORT attrFile;
} FILESTATUS;
typedef FILESTATUS *PFILESTATUS;

typedef struct _FILESTATUS2     /* fsts2 */
{
   FDATE  fdateCreation;
   FTIME  ftimeCreation;
   FDATE  fdateLastAccess;
   FTIME  ftimeLastAccess;
   FDATE  fdateLastWrite;
   FTIME  ftimeLastWrite;
   ULONG  cbFile;
   ULONG  cbFileAlloc;
   USHORT attrFile;
   ULONG  cbList;
} FILESTATUS2;
typedef FILESTATUS2 *PFILESTATUS2;

typedef struct _FILESTATUS3     /* fsts3 */
{
   FDATE  fdateCreation;
   FTIME  ftimeCreation;
   FDATE  fdateLastAccess;
   FTIME  ftimeLastAccess;
   FDATE  fdateLastWrite;
   FTIME  ftimeLastWrite;
   ULONG  cbFile;
   ULONG  cbFileAlloc;
   ULONG  attrFile;
} FILESTATUS3;
typedef FILESTATUS3 *PFILESTATUS3;

typedef struct _FILESTATUS4      /* fsts4 */
{
   FDATE  fdateCreation;
   FTIME  ftimeCreation;
   FDATE  fdateLastAccess;
   FTIME  ftimeLastAccess;
   FDATE  fdateLastWrite;
   FTIME  ftimeLastWrite;
   ULONG  cbFile;
   ULONG  cbFileAlloc;
   ULONG  attrFile;
   ULONG  cbList;
} FILESTATUS4;
typedef FILESTATUS4  *PFILESTATUS4;


   #endif



APIRET   IM_EX_CTIBASE CTISleep              (ULONG SleepTime);
APIRET   IM_EX_CTIBASE CTIScanEnv            (PSZ Name, PSZ *Result);
VOID     IM_EX_CTIBASE CTIExit               (ULONG ExitType, ULONG ExitCode);
APIRET   IM_EX_CTIBASE CTIOpenMutexSem       (PSZ pszName, PHMTX phmtx, DWORD dwAccessFlags);
APIRET   IM_EX_CTIBASE CTICloseMutexSem      (PHMTX phmtx);
APIRET   IM_EX_CTIBASE CTICreateMutexSem     (PSZ pszName, PHMTX phmtx, ULONG Flags, BOOL bState);
APIRET   IM_EX_CTIBASE CTIRequestMutexSem    (HMTX hmtx, ULONG time);
APIRET   IM_EX_CTIBASE CTIReleaseMutexSem    (HMTX hmtx);
APIRET   IM_EX_CTIBASE CTIMove               (PSZ oldname, PSZ newname);
APIRET   IM_EX_CTIBASE CTIDelete             (PSZ oldname);
APIRET   IM_EX_CTIBASE CTIClose              (HANDLE &hFile);
APIRET   IM_EX_CTIBASE CTIRead               (HANDLE &hFile, PVOID pBuf, ULONG BufLen, PULONG pBytesRead);
APIRET   IM_EX_CTIBASE CTIWrite              (HANDLE &hFile, PVOID pBuf, ULONG BufLen, PULONG pBytesWritten);
APIRET   IM_EX_CTIBASE CTISetFilePtr         (HANDLE &hFile, LONG lDistance, ULONG ulMode, PULONG pNewPtr);

APIRET   IM_EX_CTIBASE CTIGetDateTime        (PDATETIME dt);
APIRET   IM_EX_CTIBASE CTISetDateTime        (PDATETIME dt);
APIRET   IM_EX_CTIBASE CTISetMaxFH           (ULONG ulFH);

APIRET   IM_EX_CTIBASE CTISetPriority        (ULONG ulPriClass, int tPri);

DWORD    IM_EX_CTIBASE CurrentTID(VOID);
DWORD    IM_EX_CTIBASE CurrentPID(VOID);

APIRET IM_EX_CTIBASE CTICreateEventSem      (PSZ pszName, PHEV phev, ULONG Flags, BOOL32 bState);
APIRET IM_EX_CTIBASE CTIOpenEventSem        (PSZ pszName, PHEV phev, DWORD dwAccessFlags);
APIRET IM_EX_CTIBASE CTICloseEventSem       (PHEV phev);
APIRET IM_EX_CTIBASE CTIRequestEventSem     (HEV hev, PULONG pulPostCt, ULONG time);
APIRET IM_EX_CTIBASE CTIResetEventSem       (HEV hev, PULONG pulPostCt);
APIRET IM_EX_CTIBASE CTIPostEventSem        (HEV hev);
APIRET IM_EX_CTIBASE CTIWaitEventSem        (HEV hev, ULONG time);
APIRET IM_EX_CTIBASE CTILoadModule          (PSZ        pszObjNameBuf,
                                             ULONG      ulObjNameBuf,
                                             PSZ        pszModName,
                                             PHMODULE   pModHandle
                                            );

APIRET IM_EX_CTIBASE CTIQueryProcAddr       (HMODULE    hmodModHandle,
                                             ULONG      ulOrdinal,
                                             PSZ        pszProcName,
                                             PFN        procAddr
                                            );

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


APIRET IM_EX_CTIBASE CTIAllocSharedMem      (
                                         PPVOID         ppBaseAddress,
                                         PSZ            pszName,
                                         ULONG          ulObjectSize,
                                         ULONG          ulFlags         // This is the last OS2 arg.
                                         );
APIRET IM_EX_CTIBASE CTIGetNamedSharedMem   (
                                         PPVOID         ppBaseAddress,
                                         PSZ            pszName,
                                         ULONG          ulFlags         // This is the last OS2 arg.
                                         );

APIRET IM_EX_CTIBASE CTICreateNPipe         (
                                         PSZ            pszName,
                                         PHPIPE         pPipeHandle,
                                         ULONG          ulOpenMode,
                                         ULONG          ulPipeMode,
                                         ULONG          ulOutSize,
                                         ULONG          ulInSize,
                                         ULONG          ulTimeOut
                                         );

APIRET IM_EX_CTIBASE CTICreatePipe          (
                                         PHPIPE         pReadHandle,
                                         PHPIPE         pWriteHandle,
                                         ULONG          ulSize
                                         );

APIRET IM_EX_CTIBASE CTIConnectNPipe        (  HPIPE         PipeHandle);
APIRET IM_EX_CTIBASE CTIDisConnectNPipe     (  HPIPE         PipeHandle);

APIRET IM_EX_CTIBASE CTIPeekNPipe        (
                                      HPIPE          PipeHandle,
                                      PVOID          pBuf,
                                      ULONG          ulBufLen,
                                      PULONG         pBytesRead,
                                      PAVAILDATA     pBytesAvail,
                                      PULONG         pPipeState
                                      );

APIRET IM_EX_CTIBASE CTIWaitNPipe        (
                                      PSZ            pszFileName,
                                      ULONG          ulTimeOut
                                      );

void  IM_EX_CTIBASE DisplayError(DWORD Error, DWORD Line, char* FileName, char *Func);
VOID  IM_EX_CTIBASE DebugLine(char *fName, char *funcName, int lineNum);

HANDLE IM_EX_CTIBASE InitializeShutdownEvent(LPCTSTR lName);


#endif   // #ifndef _cticalls_h
