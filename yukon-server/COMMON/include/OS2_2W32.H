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

/****************************************************************************
 * OS/2 - Win32 define translations.
 ***************************************************************************/

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

