/*-----------------------------------------------------------------------------*
*
* File:   portgui
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/portgui.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:35:35 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


/*
 *  Porter GUI command types
 */
#define GUICMD_MODIIFYGLOBS      0x00000001     // Modify a porter global value
#define GUICMD_KILLPORTER        0x00000002     // Kill a vagrant porter


/*
 *  Porter GUI Global defines
 */

#define GLOB_PORT       0x00000001  // Trace port number
#define GLOB_REMOTE     0x00000002  // Trace Remote Number
#define GLOB_TRACE      0x00000003  // Trace ON/OFF
#define GLOB_TRACEERROR 0x00000004  // Trace Errors only
#define GLOB_DBGFLAGS   0x00000005  // Debugging Bit Flags
#define GLOB_RESET      0x00000006  // Reset all 711s

typedef struct
{
   INT            Command;
   INT            Global;

   // Data Value holders
   union {
      LONG           lValue;
      ULONG          ulValue;
      USHORT         usValue;
      SHORT          sValue;
      FLOAT          fValue;
      DOUBLE         dValue;
   } Data;

} CTIGUIMSG;

INT      ModifyPorterGlobs(CTIGUIMSG *Msg);
VOID     GUIConnectionThread (VOID *Arg);
VOID     PorterGUIConnectionThread (VOID *Arg);


