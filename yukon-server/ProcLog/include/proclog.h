#ifndef __PROCLOG_HPP__
#define __PROCLOG_HPP__

#include "os2_2w32.h"

#define PROCLOGGER_APPNAME "plog.exe"

typedef struct {

   INT               Command;
   INT               OpFlags;

   LONG              Time;

   CHAR              Source[64];    // Who sourced the message,
   CHAR              File[64];
   CHAR              Message[256];

} CTIERRMSG;

#define PLOG_OP_IMMEDIATE        0x00000001     // Writes out the current file immediately
// not implemented yet // #define PLOG_OP_WRITEALL         0x00000002     // Writes all files out immediately!
#define PLOG_OP_NOMASTER         0x00000004     // Do not write this message to the Main.log file
#define PLOG_OP_DEBUGONLY        0x00000004     // Do not write this message to the Main.log file

#define CMD_DEFAULT              0
#define CMD_CLOSEPROCLOG         10
#define CMD_CLOSEMYSOCKET        20
#define CMD_PURGEFILE            50
#define CMD_SETLIMITS            60


INT ProcLogServerInit(VOID);     // Done by any DLL functino which detects a non-initialized state!.

#ifdef __cplusplus
extern "C" {
#endif

   IM_EX_PROCLOG INT    ProcLogClientInit(CHAR *Name);      // Allows a "C" client to name itself.  Is not functionally needed
   IM_EX_PROCLOG INT    ProcLogMsg(CHAR *Source, CHAR *File, CHAR *Message, INT, INT , LONG);
   IM_EX_PROCLOG INT    ProcLogWrite(CTIERRMSG *ErrMsg);    // Allows logging from the "C" world.  Uses global ErrClient and accepts a CTIERRMSG structure to fill the class.

#ifdef __cplusplus
}
#endif


#endif // #ifndef __PROCLOG_HPP__


