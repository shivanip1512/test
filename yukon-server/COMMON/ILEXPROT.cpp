#include "yukon.h"
#pragma title ( "ILEX RTU Protocol Support Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        ilexprot.c

    Purpose:
        Routines to build messages to ilex RTU's

    The following procedures are contained in this module:
        ILEXHeader

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-6-93  Converted to 32 bit                             WRO


   -------------------------------------------------------------------- */
#include <windows.h>       // These next few are required for Win32
#include "os2_2w32.h"
#include "cticalls.h"
// // #include <btrapi.h>

#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "ilex.h"


/* Routine to stuff the two byte header */
ILEXHeader (PBYTE Header,          /* Pointer to message */
            USHORT Remote,          /* RTU Remote */
            USHORT Function,        /* Function code */
            USHORT SubFunction1,    /* High order sub function code */
            USHORT SubFunction2)    /* Low order sub function code */

{
    Header[0] = (Function & 0x0007);
    Header[0] |= LOBYTE ((Remote << 5) & 0xe0);
    if (SubFunction1)
        Header[0] |= 0x10;
    if (SubFunction2)
        Header[0] |= 0x08;
    Header[1] = LOBYTE (Remote >> 3);
    return (NORMAL);

}




