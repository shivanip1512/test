#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   xcel
*
* Date:   2/13/01
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MCCMD/INCLUDE/xcel.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:18:40 $
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
using std::iostream;

#include <rw/collstr.h>

#include <rw/ordcltn.h>

#include "logger.h"
#include "guard.h"
#include "types.h"

bool DecodePMSIFile(const string& file, RWOrdered* results);
static bool isValidPMSILine( char* line, char &command, string &serialNum, string &programming);
static char * getEntry (char* InBuffer, string &OutBuffer);


#endif

