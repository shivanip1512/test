/*-----------------------------------------------------------------------------
    Filename:  decodetextcmdfile.h
    Programmer:  David Sutton

    Description:    Header file for Xcel Energy PMSI
                    file format decoders.

    Initial Date:  08 June 2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/

#ifndef DECODETEXTCMDFILE_H
#define DECODETEXTCMDFILE_H

#include <iostream>
#include <rw/collstr.h>
#include "logger.h"
#include "guard.h"
#include "types.h"
#include "rwutil.h"

#define TEXT_CMD_FILE_LOG_FAIL   1
#define TEXT_CMD_FILE_COMMAND_LIST_INVALID 2
#define TEXT_CMD_FILE_UNABLE_TO_OPEN_FILE 3
#define TEXT_CMD_FILE_ERROR 4
#define TEXT_CMD_FILE_UNABLE_TO_EDIT_ORIGINAL 5

#define TEXT_CMD_FILE_SPECIFY_VERSACOM      0
#define TEXT_CMD_FILE_SPECIFY_EXPRESSCOM    1
#define TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL   2



int decodeTextCommandFile(const std::string& fileName, int aCommandLimit, int aProtocolFlag, std::vector<RWCollectableString*>* commandList);
int decodeDSM2VconfigFile(const std::string& fileName, std::vector<RWCollectableString*>* commandList);
static bool getToken (char** InBuffer, std::string &OutBuffer);
static bool outputLogFile (std::vector<std::string> &aLog);
static bool outputCommandFile (const std::string &aFileName, int aLineCnt, std::vector<std::string> &aCmdVector);
static bool decodeDsm2Lines( std::string &function, std::string &route,std::string &serialNum,std::string &cmd,RWCollectableString* programming);
bool validateAndDecodeLine( std::string & line, int aProtocolFlag, RWCollectableString* programming, std::string aFileName);


#endif


