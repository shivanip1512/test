/*-----------------------------------------------------------------------------*
*
* File:   mc
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/05/03 18:20:01 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------
        Filename:  mc.h

        Programmer:  Aaron Lauinger

        Description:  Contains global declarations for the
                      Metering and Control Server

        Initial Date: 1/9/01

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#ifndef _MC_H_
#define _MC_H_

#include "logger.h"

#define MC_PORT 1900

#define MC_SERVICE_NAME "MACS"
#define MC_SERVICE_DISPLAY_NAME "Yukon MAC Scheduler Service"

#define MC_CONSOLE_DISPLAY_NAME "MAC Scheduler"

#define MC_FTP_INTERFACE_USER "MACS FTP Interface"

/* Various message classes */

#define MSG_MC_SCHEDULE             140
#define MSG_MC_ADD_SCHEDULE         141
#define MSG_MC_UPDATE_SCHEDULE      142
#define MSG_MC_RETRIEVE_SCHEDULE    143
#define MSG_MC_DELETE_SCHEDULE      144
#define MSG_MC_OVERRIDE_REQUEST     145
#define MSG_MC_INFO                 146
#define MSG_MC_RETRIEVE_SCRIPT      147
#define MSG_MC_SCRIPT               148
#define MSG_MC_VERIFY_SCRIPT        149

#define MC_DEBUG_LEVEL "MACS_DEBUGLEVEL"

/* Debug levels */
#define MC_DEBUG_MESSAGES 0x00000001
#define MC_DEBUG_EVENTS   0x00000002
#define MC_DEBUG_DB       0x00000004
#define MC_DEBUG_INTERP   0x00000008
#define MC_DEBUG_CONN     0x00000010
#define MC_DEBUG_FILEINT  0x00000020
#define MC_DEBUG_SHUTDOWN 0x00000040

#define FTP_INTERFACE_DIR "FTP_INTERFACE_DIR"
#define FTP_INTERFACE_EXT "FTP_INTERFACE_EXT"
#define FTP_DELETE_ON_START "FTP_DELETE_ON_START"

#define DEFAULT_MC_DEBUG_LEVEL 0x00000000
#define DEFAULT_MC_FTP_INTERFACE_DIR "c:\\yukon\\server\\macsftp"
#define DEFAULT_MC_FTP_INTERFACE_EXT ".ctl"

#define MC_EXCLUSION_EVENT "macs"
 
extern unsigned gMacsDebugLevel;

#endif
