#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   xcel
*
* Date:   2/13/01
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MCCMD/INCLUDE/xcel.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:18 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------

    Filename:  xcel.h
    Programmer:  David Sutton

    Description:    Header file for Xcel Energy PMSI
                    file format decoders.

    Initial Date:  2/13/01

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#ifndef XCEL_H
#define XCEL_H

#include <iostream>
using namespace std;

#include <rw/cstring.h>
#include <rw/collstr.h>

#include <rw/ordcltn.h>

#include "logger.h"
#include "guard.h"
#include "types.h"

bool DecodePMSIFile(const RWCString& file, RWOrdered* results);
static bool isValidPMSILine( char* line, char &command, RWCString &serialNum, RWCString &programming);
static char * getEntry (char* InBuffer, RWCString &OutBuffer);


#endif

