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
using namespace std;

#include <rw/cstring.h>
#include <rw/collstr.h>

#include <rw/ordcltn.h>

#include "logger.h"
#include "guard.h"
#include "types.h"

#define TEXT_CMD_FILE_LOG_FAIL   1
#define TEXT_CMD_FILE_COMMAND_LIST_INVALID 2
#define TEXT_CMD_FILE_UNABLE_TO_OPEN_FILE 3
#define TEXT_CMD_FILE_ERROR 4
#define TEXT_CMD_FILE_UNABLE_TO_EDIT_ORIGINAL 5

#define TEXT_CMD_FILE_SPECIFY_VERSACOM      0
#define TEXT_CMD_FILE_SPECIFY_EXPRESSCOM    1
#define TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL   2



int decodeTextCommandFile(const RWCString& fileName, int aCommandLimit, int aProtocolFlag, RWOrdered* commandList);
int decodeDSM2VconfigFile(const RWCString& fileName,RWOrdered* commandList);
static bool validateAndDecodeLine( RWCString & line, int aProtocolFlag, RWCollectableString* programming);
static bool getToken (char** InBuffer, RWCString &OutBuffer);
static bool outputLogFile (vector<RWCString> &aLog);
static bool outputCommandFile (const RWCString &aFileName, int aLineCnt, vector<RWCString> &aCmdVector);
static bool decodeDsm2Lines( RWCString &function, RWCString &route,RWCString &serialNum,RWCString &cmd,RWCollectableString* programming);


#endif


