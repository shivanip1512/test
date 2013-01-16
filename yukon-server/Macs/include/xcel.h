#pragma once

#include <iostream>

#include <rw/collstr.h>

#include "logger.h"
#include "guard.h"
#include "types.h"

bool DecodePMSIFile(const std::string& file, std::vector<RWCollectableString*>* results);
static bool isValidPMSILine( char* line, char &command, std::string &serialNum, std::string &programming);
static char * getEntry (char* InBuffer, std::string &OutBuffer);
