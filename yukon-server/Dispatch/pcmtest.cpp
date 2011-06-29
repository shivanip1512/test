/*-----------------------------------------------------------------------------*
*
* File:   pcmtest
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/pcmtest.cpp-arc  $
* REVISION     :  $Revision: 1.5.34.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>
using namespace std;

#include "mgr_ptclients.h"


void main(int argc, char** argv)
{
   int                     Loops = 100;
   int                     Delay = 500;
   CtiPointClientManager   PointManager;

   cout << "Point Client Tester is starting up now" << Cti::endl;

   if(argc > 1)
   {
      Loops = atoi(argv[1]);
   }

   if(argc > 2)
   {
      Delay = atoi(argv[2]);
   }

   for(int i = 0; i < Loops; i++)
   {
      PointManager.refreshList();
      // PointManager.DumpList();

      cout << i << Cti::endl;
      Sleep(Delay);
   }

   PointManager.DeleteList();
   return;
}
