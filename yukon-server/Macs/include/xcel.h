#pragma once

#include <vector>
#include <string>

bool DecodePMSIFile(const std::string& file, std::vector<std::string*>* results);
static bool isValidPMSILine( char* line, char &command, std::string &serialNum, std::string &programming);
static char * getEntry (char* InBuffer, std::string &OutBuffer);
