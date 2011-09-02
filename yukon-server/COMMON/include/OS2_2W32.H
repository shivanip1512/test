#pragma once
/****************************************************************************
 *
 * Type conversions from OS/2 to Win32 types..
 *
 * In general, this file should follow the include of <windows.h> and all types
 * used in the code should be OS/2 types.
 *
 ***************************************************************************/

#include "dlldefs.h"
#include "types.h"

/****************************************************************************
 * OS2 - Helpful Macros
 ***************************************************************************/

/* Combine l & h to form a 32 bit quantity. */
// #define MAKEULONG(l, h)  ((ULONG)(((USHORT)(l)) | ((ULONG)((USHORT)(h))) << 16))
// #define MAKELONG(l, h)   ((LONG)MAKEULONG(l, h))

/* Combine l & h to form a 16 bit quantity. */
#define MAKEUSHORT(l, h)   (((USHORT)(l)) | ((USHORT)(h)) << 8)
#define MAKESHORT(l, h)    ((SHORT)MAKEUSHORT(l, h))
#define LOUSHORT(l)        ((USHORT)((ULONG)l))
#define HIUSHORT(l)        ((USHORT)(((ULONG)(l) >> 16) & 0xffff))

#define MAKEP(sel, off)    ((void*) off)
#define OFFSETOF(p)        (((PUSHORT)&(p))[0]);      // This may work??? CGP

/* Wait option values */

#define DCWW_WAIT          0
#define DCWW_NOWAIT        1

/*************************************************************************\
* CCHMAXPATH is the maximum fully qualified path name length including  *
* the drive letter, colon, backslashes and terminating NULL.            *
\*************************************************************************/
#define CCHMAXPATH         260


/****************************************************************************
 * OS/2 - Win32 define translations.
 ***************************************************************************/
#define  SEM_INDEFINITE_WAIT        INFINITE       // For CTIRequestMutex

/* Access protection                                                          */

#define PAG_READ          0x00000001U      /* read access                      */
#define PAG_WRITE         0x00000002U      /* write access                     */
#define PAG_EXECUTE       0x00000004U      /* execute access                   */
#define PAG_GUARD         0x00000008U      /* guard protection                 */
#define PAG_DEFAULT       0x00000400U      /* default (initial) access         */

/* Commit                                                                     */
#define PAG_COMMIT        0x00000010U      /* commit storage                   */
#define PAG_DECOMMIT      0x00000020U      /* decommit storage                 */

/* Allocation attributes                                                      */
#define OBJ_TILE          0x00000040U      /* tile object                      */
#define OBJ_PROTECTED     0x00000080U      /* protect object
                                              NOTE: This flag is NOT available at the api level */

#define OBJ_GETTABLE      0x00000100U      /* gettable by other processes      */
#define OBJ_GIVEABLE      0x00000200U      /* giveable to other processes      */

/* CTIOpen/CTIQFHandState/CTIQueryFileInfo et al file attributes; also */
/* known as CTI File Mode bits... */
#define FILE_NORMAL                    0x00000000
#define FILE_READONLY                  0x00000001
#define FILE_HIDDEN                    0x00000002
#define FILE_SYSTEM                    0x00000004
#define FILE_DIRECTORY                 0x00000010
#define FILE_ARCHIVED                  0x00000020

#define FILE_IGNORE                    0x00010000     /* ignore file attribute in */
/* CTIOpen() actions */
#define FILE_EXISTED                   0x00000001
#define FILE_CREATED                   0x00000002
#define FILE_TRUNCATED                 0x00000003

/* CTIOpen() open flags */
#define FILE_OPEN                      0x00000001
#define FILE_TRUNCATE                  0x00000002
#define FILE_CREATE                    0x00000010

/*     this nibble applies if file already exists                xxxx */
#define OPEN_ACTION_FAIL_IF_EXISTS     0x00000000  /* ---- ---- ---- 0000 */
#define OPEN_ACTION_OPEN_IF_EXISTS     0x00000001  /* ---- ---- ---- 0001 */
#define OPEN_ACTION_REPLACE_IF_EXISTS  0x00000002  /* ---- ---- ---- 0010 */

/*     this nibble applies if file does not exist           xxxx      */
#define OPEN_ACTION_FAIL_IF_NEW        0x00000000  /* ---- ---- 0000 ---- */
#define OPEN_ACTION_CREATE_IF_NEW      0x00000010  /* ---- ---- 0001 ---- */

/* CTIOpen/CTIsetFHandState flags */
#define OPEN_ACCESS_READONLY           0x00000000
#define OPEN_ACCESS_WRITEONLY          0x00000001
#define OPEN_ACCESS_READWRITE          0x00000002

#define OPEN_SHARE_DENYREADWRITE       0x00000010
#define OPEN_SHARE_DENYWRITE           0x00000020
#define OPEN_SHARE_DENYREAD            0x00000030
#define OPEN_SHARE_DENYNONE            0x00000040

#define OPEN_FLAGS_NOINHERIT           0x00000080  /* ---- ---- 1--- ---- */
#define OPEN_FLAGS_NO_LOCALITY         0x00000000  /* ---- -000 ---- ---- */
#define OPEN_FLAGS_SEQUENTIAL          0x00000100  /* ---- -001 ---- ---- */
#define OPEN_FLAGS_RANDOM              0x00000200  /* ---- -010 ---- ---- */
#define OPEN_FLAGS_RANDOMSEQUENTIAL    0x00000300  /* ---- -011 ---- ---- */
#define OPEN_FLAGS_NO_CACHE            0x00001000  /* ---1 ---- ---- ---- */
#define OPEN_FLAGS_FAIL_ON_ERROR       0x00002000  /* --1- ---- ---- ---- */
#define OPEN_FLAGS_WRITE_THROUGH       0x00004000  /* -1-- ---- ---- ---- */
#define OPEN_FLAGS_DASD                0x00008000  /* 1--- ---- ---- ---- */
#define OPEN_FLAGS_NONSPOOLED          0x00040000
#define OPEN_FLAGS_PROTECTED_HANDLE    0x40000000


#define PRTYS_PROCESS                  0
#define PRTYS_THREAD                   2

#define PRTYC_NOCHANGE                 0
#define PRTYC_IDLETIME                 IDLE_PRIORITY_CLASS
#define PRTYC_REGULAR                  NORMAL_PRIORITY_CLASS
#define PRTYC_TIMECRITICAL             HIGH_PRIORITY_CLASS
#define PRTYC_FOREGROUNDSERVER         REALTIME_PRIORITY_CLASS

/* CTIExitList functions */

#define EXLST_ADD          1
#define EXLST_REMOVE       2
#define EXLST_EXIT         3

#define NP_INDEFINITE_WAIT      -1
#define NP_DEFAULT_WAIT         0L

/* CTIPeekNmPipe() pipe states */

#define NP_STATE_DISCONNECTED   0x0001
#define NP_STATE_LISTENING      0x0002
#define NP_STATE_CONNECTED      0x0003
#define NP_STATE_CLOSING        0x0004

/* CTICreateNPipe open modes */

#define NP_ACCESS_INBOUND       0x0000
#define NP_ACCESS_OUTBOUND      0x0001
#define NP_ACCESS_DUPLEX        0x0002
#define NP_INHERIT              0x0000
#define NP_NOINHERIT            0x0080
#define NP_WRITEBEHIND          0x0000
#define NP_NOWRITEBEHIND        0x4000

/* CTICreateNPipe and CTIQueryNPHState state */

#define NP_READMODE_BYTE        0x0000
#define NP_READMODE_MESSAGE     0x0100
#define NP_TYPE_BYTE            0x0000
#define NP_TYPE_MESSAGE         0x0400
#define NP_END_CLIENT           0x0000
#define NP_END_SERVER           0x4000
#define NP_WAIT                 0x0000
#define NP_NOWAIT               0x8000
#define NP_UNLIMITED_INSTANCES  0x00FF

// #define ERROR_TIMEOUT         0x0000      // FIX this
#define ERROR_OCCURRED        0x0000      // FIX this


