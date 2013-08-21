#pragma once

#include "logger.h"
#include "guard.h"
#include "types.h"

#include <string>
#include <vector>

/* custom cparm related, see mcu9000eoi command for purpose */
extern int gPagingConfigRouteID;
extern int gFMConfigRouteID;
extern int gFMConfigSerialLow[10];
extern int gFMConfigSerialHigh[10];

bool DecodeCFDATAFile(const std::string& file, std::vector<std::string*>* results);
bool DecodeEOIFile   (const std::string& file, std::vector<std::string*>* results);
bool DecodeWepcoFile (const std::string& file, std::vector<std::string*>* results);

// These two functions are DecodeWepcoFile split into two
bool DecodeWepcoFileService(const std::string& file, std::vector<std::string*>* results);
bool DecodeWepcoFileConfig (const std::string& file, std::vector<std::string*>* results);

static bool DecodeCFDATALine(char* line, std::string* decoded );
static bool DecodeEOILine   (char* line, std::vector<std::string*>* results );
static bool DecodeWepcoLine (char* line, std::vector<std::string*>* results );

static bool DecodeWepcoServiceLine(char* line, std::vector<std::string*>* results );
static bool DecodeWepcoConfigLine (char* line, std::vector<std::string*>* results );

static std::string GetSelectCustomRouteID(long serial_num);
