#define _WIN32_WINNT 0x403 //NT4 sp3
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   precomp
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVICE/INCLUDE/precomp.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:20:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

//#define UNICODE
//#define _UNICODE

#include <windows.h>
#include <process.h>

#include <iostream>
using namespace std;

#ifdef UNICODE
    #define _tcout wcout
    #define _tostream wostream
#else
    #define _tcout cout
    #define _tostream ostream
#endif

#include <TCHAR.h>
#include "Monitor.h"
