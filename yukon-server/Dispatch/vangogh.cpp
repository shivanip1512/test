#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   vangogh
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/vangogh.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iomanip>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\cstring.h>
#include <rw\thr\thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw/toolpro/inetaddr.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>

#include "dllbase.h"
#include "dlldefs.h"
#include "dllvg.h"
#include "ctivangogh.h"
#include "stdexcepthdlr.h"
#include "logger.h"

DLLIMPORT extern BOOL  bGCtrlC;
DLLIMPORT extern CtiLogger dout;

static RWWinSockInfo  winsock;


int DispatchMainFunction(int argc, char **argv)
{
   try
   {
      CtiVanGogh VanGogh(1000);

      VanGogh.CmdLine(argc, argv);      // Record the Command line options for the world to see.

      int i = VanGogh.execute();

      VanGogh.join();

      return(i);
   }
   catch(const RWxmsg& x)
   {
      cout << "main() Exception: " << x.why() << endl;
   }

   return 0;
}


