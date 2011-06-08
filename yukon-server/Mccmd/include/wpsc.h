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

#include <rw/collstr.h>

#include "logger.h"
#include "guard.h"
#include "types.h"

/* custom cparm related, see mcu9000eoi command for purpose */
extern int gPagingConfigRouteID;
extern int gFMConfigRouteID;
extern int gFMConfigSerialLow[10];
extern int gFMConfigSerialHigh[10];

bool DecodeCFDATAFile(const std::string& file, std::vector<RWCollectableString*>* results);
bool DecodeEOIFile(const std::string& file, std::vector<RWCollectableString*>* results);
bool DecodeWepcoFile(const std::string& file, std::vector<RWCollectableString*>* results);

// These two functions are DecodeWepcoFile split into two
bool DecodeWepcoFileService(const std::string& file, std::vector<RWCollectableString*>* results);
bool DecodeWepcoFileConfig(const std::string& file, std::vector<RWCollectableString*>* results);

static bool DecodeCFDATALine( char* line, std::string& decoded );
static bool DecodeEOILine(char* line, std::vector<RWCollectableString*>* results );
static bool DecodeWepcoLine( char* line, std::vector<RWCollectableString*>* results );

static bool DecodeWepcoServiceLine( char* line, std::vector<RWCollectableString*>* results );
static bool DecodeWepcoConfigLine( char* line, std::vector<RWCollectableString*>* results );

static std::string GetSelectCustomRouteID(long serial_num);
#endif
