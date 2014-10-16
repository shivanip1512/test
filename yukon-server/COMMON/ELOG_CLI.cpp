#include "precompiled.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "os2_2w32.h"
#include "cticalls.h"
#include "dsm2.h"
#include "elogger.h"
#include "logger.h"

using std::ostream;
using std::string;
using std::endl;

HPIPE       ElogPipeHandle = (HPIPE) NULL;
PUSHORT     DisableAudible = (PUSHORT) NULL;
PUSHORT     LCFunctionCode = (PUSHORT) NULL;


IM_EX_CTIBASE INT LogEvent(SYSTEMLOGMESS *LogMessage)
{
    CTILOG_INFO(dout, *LogMessage); // Stupid trick.

    return ClientErrors::None;
}
