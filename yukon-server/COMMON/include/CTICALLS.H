#pragma once

#include "yukon.h"  //  for YukonError_t

#include <windows.h>
#include "os2_2w32.h"

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


void         IM_EX_CTIBASE CTISleep              (ULONG SleepTime);
YukonError_t IM_EX_CTIBASE CTIScanEnv            (PSZ Name, PSZ *Result);
YukonError_t IM_EX_CTIBASE CTICloseMutexSem      (PHMTX phmtx);
YukonError_t IM_EX_CTIBASE CTICreateMutexSem     (PSZ pszName, PHMTX phmtx, ULONG Flags, BOOL bState);
YukonError_t IM_EX_CTIBASE CTIRequestMutexSem    (HMTX hmtx, ULONG time);
YukonError_t IM_EX_CTIBASE CTIReleaseMutexSem    (HMTX hmtx);
YukonError_t IM_EX_CTIBASE CTIDelete             (PSZ oldname);
YukonError_t IM_EX_CTIBASE CTIClose              (HANDLE &hFile);
YukonError_t IM_EX_CTIBASE CTIRead               (HANDLE &hFile, PVOID pBuf, ULONG BufLen, PULONG pBytesRead);
YukonError_t IM_EX_CTIBASE CTIWrite              (HANDLE &hFile, PVOID pBuf, ULONG BufLen, PULONG pBytesWritten);

DWORD        IM_EX_CTIBASE CurrentTID(void);

YukonError_t IM_EX_CTIBASE CTICreateEventSem     (PSZ pszName, PHEV phev, ULONG Flags, BOOL32 bState);
YukonError_t IM_EX_CTIBASE CTICloseEventSem      (PHEV phev);
YukonError_t IM_EX_CTIBASE CTIResetEventSem      (HEV hev, PULONG pulPostCt);
YukonError_t IM_EX_CTIBASE CTIPostEventSem       (HEV hev);

YukonError_t IM_EX_CTIBASE CTIOpen               (PSZ     pszFileName,
                                                  PHFILE  pHf,
                                                  PULONG  pAction,
                                                  ULONG   ulFSize,
                                                  ULONG   ulAttrib,
                                                  ULONG   Flags,
                                                  ULONG   Mode,
                                                  PEAOP2  peaop2);
