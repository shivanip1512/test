#include <windows.h>
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   memtest
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/memtest.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:46 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <crtdbg.h>
#include <iostream>
using namespace std;

#include "dllbase.h"

void main(void)
{
   // Send all reports to STDOUT
   _CrtSetReportMode( _CRT_WARN, _CRTDBG_MODE_FILE );
   _CrtSetReportFile( _CRT_WARN, _CRTDBG_FILE_STDOUT );
   _CrtSetReportMode( _CRT_ERROR, _CRTDBG_MODE_FILE );
   _CrtSetReportFile( _CRT_ERROR, _CRTDBG_FILE_STDOUT );
   _CrtSetReportMode( _CRT_ASSERT, _CRTDBG_MODE_FILE );
   _CrtSetReportFile( _CRT_ASSERT, _CRTDBG_FILE_STDOUT );

   // Get current flag
   int tmpFlag = _CrtSetDbgFlag( _CRTDBG_REPORT_FLAG );

   // Turn on leak-checking bit, and always check on alloc/dealloc
   // tmpFlag |= _CRTDBG_LEAK_CHECK_DF | _CRTDBG_CHECK_ALWAYS_DF;
   tmpFlag |= _CRTDBG_LEAK_CHECK_DF;

   _CrtSetDbgFlag( tmpFlag );


   cout << "Hello World " << getDebugLevel() << endl;

}
