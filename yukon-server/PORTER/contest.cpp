#include <windows.h>
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   contest
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/contest.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:36 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <malloc.h>

#include "queues.h"

#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "ilex.h"
#include "perform.h"

#include "portglob.h"
#include "color.h"


void main(int argc, char **argv)
{
   INT      i, nRet, LoopCount = 1;


   if(argc > 1)
   {
      LoopCount = atoi(argv[1]);
   }

   printf("Calling xIOInit()\n");
   xIOInit(argv[0]);

   for(i = 0; i < LoopCount; i++)
   {
      nRet = LoopBack(atoi(argv[2]), atoi(argv[3]));

      if(nRet)
      {
         printf("Loopback %d returned Error %d\n",i,nRet);

         Sleep(5000);
         xIOInit("nt_corey");
      }
      else
      {
         printf("Loopback %d SUCCESSFUL\n",i);
      }
   }
   Sleep(500);
}
