/*-----------------------------------------------------------------------------*
*
* File:   traceset
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/traceset.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:55 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "dlldefs.h"

extern IM_EX_PORTGLOB LONG          TracePort;
extern IM_EX_PORTGLOB LONG          TraceRemote;

extern INT PortDummy(void);

// Function Prototypes.

void     Usage(int, char**);

int main (int argc, char *argv[])
{
   INT i;

   if(argc == 1)
   {
      Usage(argc, argv);
      exit(-1);
   } else {
      fprintf(stderr,"Checking Arguments... \n");

      /* Check the arguments */
      for (i = 1; i < argc; i++) {
         /* Check for a count */
         if (!(strnicmp (argv[i], "/P:", 3)) || !(strnicmp (argv[i], "/p:", 3))) {
            TracePort = atoi (argv[i] + 3);
         } else if (!(strnicmp (argv[i], "/R:", 3)) || !(strnicmp (argv[i], "/r:", 3))) {
            TraceRemote = atof (argv[i] + 3);
         }
      }
   }

   return 0;
}

void Usage(int argc, char *argv[])
{
   fprintf(stderr,"Usage:\n\n%s /p:<port #> /r:<remote #>\n\n",argv[0]);
}

