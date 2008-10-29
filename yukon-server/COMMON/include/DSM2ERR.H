/* includes for emetcon error routines */

#ifndef DSM2ERR_H
#define DSM2ERR_H

#include <windows.h>

#include "dlldefs.h"
#include <string>

typedef struct _ERRSTRUCT {
    INT  Error;
    CHAR ErrorMessage[160];
    INT  Type;
    CHAR TypeDescription[16];
} ERRSTRUCT;

/* Prototypes from ERROR.C */
INT SetUnknown( ERRSTRUCT &ESt );

IM_EX_CTIBASE CHAR* GetError (INT err);
IM_EX_CTIBASE INT GetErrorType (INT err);
IM_EX_CTIBASE INT GetError (ERRSTRUCT &);
IM_EX_CTIBASE INT InitError (VOID);
IM_EX_CTIBASE INT PrintError (USHORT);
IM_EX_CTIBASE INT GetErrorString (USHORT Error, char *ErrStr);
IM_EX_CTIBASE std::string FormatError(USHORT Error);

#endif
