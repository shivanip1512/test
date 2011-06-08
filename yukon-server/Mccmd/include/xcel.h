#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   xcel
*
* Date:   2/13/01
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MCCMD/INCLUDE/xcel.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2006/04/24 20:47:30 $
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

#include <rw/collstr.h>

#include "logger.h"
#include "guard.h"
#include "types.h"

bool DecodePMSIFile(const std::string& file, std::vector<RWCollectableString*>* results);
static bool isValidPMSILine( char* line, char &command, std::string &serialNum, std::string &programming);
static char * getEntry (char* InBuffer, std::string &OutBuffer);


#endif

