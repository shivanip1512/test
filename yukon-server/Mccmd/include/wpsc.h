/*-----------------------------------------------------------------------------
    Filename:  cfdata.h
    
    Programmer:  Aaron Lauinger
    
    Description:    Header file for wisconsin public service company(corp)
                    file format decoders.
    
    Initial Date:  4/7/99
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#ifndef WPSC_H
#define WPSC_H

#include <iostream>
using namespace std;

#include <rw/cstring.h>
#include <rw/collstr.h>

#include <rw/ordcltn.h>
#include <rw\re.h>

#include "logger.h"
#include "guard.h"
#include "types.h"

/* custom cparm related, see mcu9000eoi command for purpose */
extern int gPagingConfigRouteID;
extern int gFMConfigRouteID;
extern int gFMConfigSerialLow[10];
extern int gFMConfigSerialHigh[10];

bool DecodeCFDATAFile(const RWCString& file, RWOrdered* results);
bool DecodeEOIFile(const RWCString& file, RWOrdered* results);
bool DecodeWepcoFile(const RWCString& file, RWOrdered* results);

static bool DecodeCFDATALine( char* line, RWCString* decoded );
static bool DecodeEOILine(char* line, RWOrdered* results );
static bool DecodeWepcoLine( char* line, RWOrdered* results );

static RWCString GetSelectCustomRouteID(long serial_num);
#endif
