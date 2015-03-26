#pragma once

#include <tcl/tcl.h>

#include <functional>
#include <iostream>
#include <set>
#include <queue>

#include <boost/assign/list_of.hpp>

#include "msg_pcrequest.h"
#include "msg_pcreturn.h"

#include "logger.h"
#include "ctdpcptrq.h"
#include "dllBase.h"
#include "tbl_meterreadlog.h"

#define DEFAULT_ONE_WAY_TIMEOUT 0
#define DEFAULT_TWO_WAY_TIMEOUT 900

/* CPARMS */
#define MCCMD_INIT_SCRIPT       "INIT_SCRIPT"
#define MCCMD_CTL_SCRIPTS_DIR   "CTL_SCRIPTS_DIR"
#define MCCMD_DEBUG_LEVEL       "MCCMD_DEBUGLEVEL"
#define MACS_DISABLE_CANCEL     "MACS_DISABLE_CANCEL"
#define MACS_IGNORE_QUEUES      "MACS_IGNORE_PORTER_QUEUES"
#define MACS_USE_OLD_MISSED_LIST "MACS_USE_OLD_MISSED_LIST"

/* Debug levels */
#define MCCMD_DEBUG_INIT       0x00000001
#define MCCMD_DEBUG_PILREQUEST 0x00000002

typedef int (*MacsCommandPtr)(ClientData, Tcl_Interp*, int, const char*[]);

typedef std::map< std::string, MacsCommandPtr > TclCommandMap;
typedef TclCommandMap::value_type TclCommandPair;

extern const TclCommandMap pilCommands;
extern const TclCommandMap caseSensitiveTclCommands;
extern const TclCommandMap tclCommands;

// Store for the data that we need out of the return message.
struct MACS_Return_Data
{
    CtiTime time;
    int     status;
    std::string  deviceName; // Stores the device names for requests whose status resulted in IDNF.
};

typedef std::map< long, MACS_Return_Data, std::less<long> > PILReturnMap;

/* Registers MACS commands with the interpreter
   and registers MCCmd as a package */
int Mccmd_Init(Tcl_Interp* interp);

//Below are registered with the tcl interpreter

/* Starts up the connection to the PIL */
static int Mccmd_Connect(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

/* Close the connection to the PIL */
static int Mccmd_Disconnect(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

/* Reset state info for the given interpreter */
static int Mccmd_Reset(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

static int Command(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int GetValue(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int PutValue(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int GetStatus(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int PutStatus(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int GetConfig(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int PutConfig(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int Loop(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int Control(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int Select(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int Scan(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int Pil(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

static int mcu8100(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int mcu9000eoi(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int mcu8100wepco(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int mcu8100service(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int mcu8100program(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

static int pmsi(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int importCommandFile(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static bool FileAppendAndDelete(const std::string &toFileName, const std::string &fromFileName);

/* Determines if the given time is a holiday */
/* takes a time parameters in seconds and an int which is the holiday schedule id */
static int isHoliday(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

/*  Command to send a signal message to van gogh (log an event) */
static int LogEvent(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int SendDBChange(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[] );

/*  Prints to stdout, used the global dout logger.
    Useful for debugging. */
static int Dout(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

/*  Sends a notification to a group */
static int SendNotification(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[] );

/* Use this instead of the 'after' Tcl command in order to respond to cancellation
   requests.  */
static int Wait(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

/* Handles exiting the interpreter properly */
static int Exit(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

/* Get a device name/id from the database */
static int getDeviceName(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int getDeviceID(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

/* Format an error (status) code from PIL */
static int formatError(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

/* Get the base directory yukon is installed in */
static int getYukonBaseDir(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

//  Workaround to add "clock" back to the interpreter without requiring TCL's init.tcl and the whole TCL library
static int yukonClock(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

static int CTICreateProcess(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

//MORE............

static int DoOneWayRequest(Tcl_Interp* interp, const std::string &cmd_line);
static int DoTwoWayRequest(Tcl_Interp* interp, const std::string &cmd_line);

static int DoRequest(Tcl_Interp* interp, const std::string &cmd_line, long timeout, bool two_way);

static void HandleReturnMessage(CtiReturnMsg* msg,
                PILReturnMap& good_map,
                PILReturnMap& bad_map,
                PILReturnMap& device_map,
                std::vector<std::string>& bad_names,
                std::deque<CtiTableMeterReadLog>& queueStatus);

static int WriteResultsToDatabase(std::deque<CtiTableMeterReadLog>& resultQueue, UINT requestLogId);

/* Retrieves the id of a notification group given its name */
static long GetNotificationGroupID( const std::string& name );

/* Retrive the name/id of a device */
static std::string GetDeviceName(long deviceID);
static long GetDeviceID(const std::string& name);

static std::vector<CtiRequestMsg *> BuildRequestSet(Tcl_Interp* interp, std::string cmd);
/* Nothing below here should be called from within this dll unless you have a good
   pretty good reason */

/* Generates a user message id
   Used to make each request unique */
unsigned int GenMsgID();

/* Strips the thread id out of the msg_id and returns it */
unsigned int GetThreadIDFromMsgID(unsigned int msg_id);

std::string BuildCommandString(const int argc, const char * const argv[]);
std::string DumpReturnMessage(const CtiReturnMsg& msg);
std::string DumpRequestMessage(const CtiRequestMsg& msg);


