#define _WIN32_WINNT 0x403   // was 0x403! //NT4 sp3
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   precomp
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVICE/INCLUDE/precomp.h-arc  $
* REVISION     :  $Revision: 1.4.24.2 $
* DATE         :  $Date: 2008/11/13 17:23:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

//#define UNICODE
//#define _UNICODE


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <process.h>

#include <iostream>
using std::iostream;

#ifdef UNICODE
    #define _tcout wcout
    #define _tostream wostream
#else
    #define _tcout cout
    #define _tostream ostream
#endif

#include <TCHAR.h>
#include "Monitor.h"
