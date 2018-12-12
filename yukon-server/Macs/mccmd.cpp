#include "precompiled.h"

#include "mccmd.h"

#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"
#include "database_transaction.h"
#include "connection_client.h"
#include "amq_constants.h"
#include "cparms.h"
#include "cmdparse.h"

#include "ctitokenizer.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_requestcancel.h"
#include "msg_pdata.h"
#include "msg_queuedata.h"
#include "msg_signal.h"
#include "msg_dbchg.h"
#include "msg_notif_email.h"
#include "msg_cmd.h"
#include "tbl_devicereadrequestlog.h"
#include "dllbase.h"
#include "collectable.h"
#include "pointtypes.h"
#include "numstr.h"
#include "mgr_holiday.h"
#include "error.h"

#include "wpsc.h"
#include "xcel.h"
#include "decodetextcmdfile.h"
#include "utility.h"
#include "smartmap.h"
#include "win_helper.h"

#include "string_util.h"

#include "MessageCounter.h"

#include "std_helper.h"

#include <boost/algorithm/string.hpp>
#include <boost/range.hpp>
#include <boost/regex.hpp>

#include <string>

using std::string;
using std::endl;
using std::vector;

using namespace std::string_literals;

using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

/* The global debug level stored here, defined in mc.h */
unsigned gMacsDebugLevel  = 0x00000000;
unsigned gMccmdDebugLevel = 0x00000000;

bool gDoNotSendCancel = false;
bool gIgnoreQueueCount = false;
bool gUseOldStyleMissed = false;

const boost::regex   re_num("[0-9]+");
const boost::regex   re_timeout("timeout[= ]+[0-9]+");
const boost::regex   re_select("select[ ]+[^ ]+[ ]+");
const boost::regex   re_select_list("select[ ]+list[ ]+");


char* SelectedVariable = "Selected";
char* GoodListVariable = "SuccessList";
char* BadListVariable  = "MissedList";
char* BadStatusVariable = "ErrorList";
char* ScheduleIDVariable = "ScheduleID";
char* HolidayScheduleIDVariable = "HolidayScheduleID";
char* PILRequestPriorityVariable = "MessagePriority";

std::unique_ptr<CtiClientConnection> PorterConnection;
std::unique_ptr<CtiClientConnection> VanGoghConnection;
std::unique_ptr<CtiClientConnection> NotificationConnection;

void _MessageThrFunc();

Cti::WorkerThread MessageThr(Cti::WorkerThread::Function(_MessageThrFunc).name("_MessageThrFunc"));

// Used to distinguish unique requests/responses to/from pil
unsigned short gUserMessageID = 0;

static const TclCommandMap pilCommands = boost::assign::map_list_of
    ("Command", &Command)
    ("GetValue", &GetValue)
    ("PutValue", &PutValue)
    ("GetStatus", &GetStatus)
    ("PutStatus", &PutStatus)
    ("GetConfig", &GetConfig)
    ("PutConfig", &PutConfig)
    ("Loop", &Loop)
    ("Ping", &Loop)
    ("Control", &Control)
    ("Pil", &Pil);

static const TclCommandMap caseSensitiveTclCommands = boost::assign::map_list_of
    ("PILStartup", &Mccmd_Connect)
    ("PILShutdown", &Mccmd_Disconnect)
    ("MCCMDReset", &Mccmd_Reset)
    ("Wait", &Wait)
    ("LogEvent", &LogEvent)
    ("SendDBChange", &SendDBChange)
    ("Dout", &Dout)
    ("SendNotification", &SendNotification)
    ("ImportCommandFile", &importCommandFile)
    ("createProcess", &CTICreateProcess);

static const TclCommandMap tclCommands {
    { "clock",          &yukonClock },  //  Workaround to add "clock" back to the interpreter
    { "holiday",        &isHoliday },
    { "exit",           &Exit },
    { "mcu8100",        &mcu8100 },
    { "mcu9000eoi",     &mcu9000eoi },
    { "mcu8100wepco",   &mcu8100wepco },
    { "mcu8100service", &mcu8100service },
    { "mcu8100program", &mcu8100program },
    { "PMSI",           &pmsi },
    { "pmsi",           &pmsi },
    { "getDeviceID",    &getDeviceID },
    { "getDeviceName",  &getDeviceName },
    { "formatError",    &formatError },
    { "getYukonBaseDir", &getYukonBaseDir }};

typedef CtiSmartMap<CtiFIFOQueue<CtiMessage>> RequestQueues;
RequestQueues inboundMessageQueues;

static CtiCriticalSection _queue_mux;

/* This function runs in it's own thread and simple watches the connection to the
   PIL for incoming messages and places them in the appropriate queue */
void _MessageThrFunc()
{
    time_t last_cancellation_check = 0;
    Cti::MessageCounter mc("Porter->MACS");

    try
    {
        while( true )
        {
            //Wake up every second to respect cancellation requests
            if( CtiMessage *in_ptr = PorterConnection->ReadConnQue( 1000 ) )
            {
                mc.increment();

                std::unique_ptr<CtiMessage> inboundMessage(in_ptr);

                unsigned int msgid = 0;

                switch( inboundMessage->isA() )
                {
                    case MSG_PCRETURN:
                        msgid = static_cast<CtiReturnMsg &>(*inboundMessage).UserMessageId();
                        break;

                    case MSG_QUEUEDATA:
                        msgid = static_cast<CtiQueueDataMsg &>(*inboundMessage).UserMessageId();
                        break;

                    case MSG_REQUESTCANCEL:
                        msgid = static_cast<CtiRequestCancelMsg &>(*inboundMessage).UserMessageId();
                        break;
                }

                if( RequestQueues::ptr_type msgQueue = inboundMessageQueues.find(msgid) )
                {
                    msgQueue->putQueue(inboundMessage.release());
                }
                else
                {
                    Cti::StreamBuffer logmsg;
                    logmsg <<"Received message for interpreter ["<< GetThreadIDFromMsgID(msgid) <<"], type "<< inboundMessage->isA();

                    switch( inboundMessage->isA() )
                    {
                        case MSG_PCRETURN:
                            logmsg << DumpReturnMessage(static_cast<CtiReturnMsg &>(*inboundMessage));
                            break;

                        case MSG_REQUESTCANCEL:
                            logmsg << *inboundMessage;
                            break;
                    }

                    CTILOG_INFO(dout, logmsg);
                }
            }

            Cti::MessageCounter mc2("Dispatch->MACS");

            //Clean out the VanGogh Connection
            while( CtiMessage *vgMsg = VanGoghConnection->ReadConnQue( 0 ) )
            {
                mc2.increment();

                // If it is a command message (are you there)
                // message then echo it right back
                if( vgMsg->isA() == MSG_COMMAND && ((CtiCommandMsg*)vgMsg)->getOperation() == CtiCommandMsg::AreYouThere )
                {
                    VanGoghConnection->WriteConnQue(vgMsg, CALLSITE);
                }
                else
                {  // not interested, just delete it
                    delete vgMsg;
                }
            }

            if( ::time(0) != last_cancellation_check )
            {
                last_cancellation_check = ::time(0);

                Cti::WorkerThread::interruptionPoint();
            }
        }
    }
    catch( Cti::WorkerThread::Interrupted & )
    {
        CTILOG_INFO(dout, "Thread exiting");
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

string BuildCommandString(const int argc, const char * const argv[])
{
    std::vector<std::string> args(argv, argv + argc);

    return boost::algorithm::join(args, " ");
}

std::string DumpReturnMessage(const CtiReturnMsg& msg)
{
    Cti::StreamBuffer out;

    out << endl
            <<"deviceid: " << msg.DeviceId()
            <<" routeid: " << msg.RouteID()
            <<" status: "  << msg.Status()
            <<" more: "    << msg.ExpectMore();

    out << endl <<"result: "<< msg.ResultString();

    for each( const CtiMessage *multiMsg in msg.PointData() )
    {
        if( multiMsg->isA() == MSG_POINTDATA )
        {
            out << endl << static_cast<const CtiPointDataMsg *>(multiMsg)->getString();
        }
    }

    return out;
}

std::string DumpRequestMessage(const CtiRequestMsg& msg)
{
    Cti::StreamBuffer out;

    out << endl
            <<"deviceid: "  << msg.DeviceId()
            <<" routeid: "  << msg.RouteId()
            <<" msgid: "    << msg.UserMessageId()
            <<" priority: " << msg.getMessagePriority();

    out << endl << msg.CommandString();

    return out;
}

/* Connects to the PIL and VanGogh*/
int Mccmd_Connect(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if( PorterConnection && VanGoghConnection && NotificationConnection )
    {
      return 0;
    }

    //Set up the defaults
    string fm_config_range;

    CTILOG_INFO(dout, "MCCMD loading cparms:");

    gPagingConfigRouteID = gConfigParms.getValueAsInt("PAGING_CONFIG_ROUTE_ID", -1);
    gFMConfigRouteID = gConfigParms.getValueAsInt("FM_CONFIG_ROUTE_ID", -1);
    fm_config_range = gConfigParms.getValueAsString("FM_CONFIG_SERIAL_RANGE", "");

    if(gPagingConfigRouteID != -1)
    {
        CTILOG_INFO(dout, "Using route id: "<< gPagingConfigRouteID <<" as the paging route");
    }

    if(gFMConfigRouteID != -1)
    {
        CTILOG_INFO(dout, "Using route id: "<< gFMConfigRouteID <<" as the FM config route");
    }

    // init these in case none are found
    gFMConfigSerialLow[0] = -1;
    gFMConfigSerialHigh[0] = -1;

    if(fm_config_range != "")
    {
        boost::char_separator<char> sep(",\r\n");
        Boost_char_tokenizer nextRange(fm_config_range, sep);
        string range;

        int index = 0;
        for (Boost_char_tokenizer::iterator tok_iter = nextRange.begin(); tok_iter != nextRange.end(); ++tok_iter)
        {
            range = *tok_iter;
            boost::char_separator<char> low_sep("-");
            boost::char_separator<char> hig_sep(" \r\n");
            Boost_char_tokenizer nextLowSerial(range, low_sep);
            string low;
            string high;

            if( nextLowSerial.begin() != nextLowSerial.end() )
            {
                low = *nextLowSerial.begin();
            }
            range.erase(0,low.size());
            Boost_char_tokenizer nextHighSerial(range, hig_sep);
            if( nextHighSerial.begin() != nextHighSerial.end() )
            {
                high = *nextHighSerial.begin();
            }
            trim_left(high, "-");

            if( !low.empty() && !high.empty() )
            {
                int lowi = atoi(low.c_str());
                int highi = atoi(high.c_str());

                if( lowi != 0 && highi != 0 )
                {
                    CTILOG_INFO(dout, "Using "<< lowi <<"-"<< highi <<" as a fm serial number range");

                    gFMConfigSerialLow[index] = lowi;
                    gFMConfigSerialHigh[index] = highi;
                    index++;
                }
            }
        }

        gFMConfigSerialLow[index] = -1;
        gFMConfigSerialHigh[index] = -1;
    }

    CTILOG_INFO(dout, "MCCMD done loading cparms");

    PorterConnection = std::make_unique<CtiClientConnection>( Cti::Messaging::ActiveMQ::Queue::porter );
    PorterConnection->setName("MCCMD to Porter");
    PorterConnection->start();

    //Send a registration message
    auto reg = std::make_unique<CtiRegistrationMsg>("MCCMD", 0, false);
    PorterConnection->WriteConnQue(std::move(reg), CALLSITE);

    VanGoghConnection = std::make_unique<CtiClientConnection>( Cti::Messaging::ActiveMQ::Queue::dispatch );
    VanGoghConnection->setName("MCCMD to Dispatch");
    VanGoghConnection->start();

    //Send a registration message
    auto reg2 = std::make_unique<CtiRegistrationMsg>("MCCMD", 0, false);
    VanGoghConnection->WriteConnQue(std::move(reg2), CALLSITE);

    NotificationConnection = std::make_unique<CtiClientConnection>( Cti::Messaging::ActiveMQ::Queue::notification );
    NotificationConnection->setName("MCCMD to Notification");
    NotificationConnection->start();

    MessageThr.start();

    return 0;
}

/* Disconnects from the PIL */
int Mccmd_Disconnect(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if( MessageThr.isRunning() )
    {
        MessageThr.interrupt();
    }

    CTILOG_INFO(dout, "Shutting down connection to Porter");

    PorterConnection->close();

    CTILOG_INFO(dout, "Shutting down connection to VanGogh");

    VanGoghConnection->close();

    CTILOG_INFO(dout, "Shutting down connection to the Notification Server");

    NotificationConnection->close();

    PorterConnection       = nullptr;
    VanGoghConnection      = nullptr;
    NotificationConnection = nullptr;

    return 0;

}

int Mccmd_Reset(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    return Mccmd_Init(interp);
}

int Mccmd_Init(Tcl_Interp* interp)
{
    /* Register escape commands with the interpreter */

    for each(const TclCommandMap::value_type& itr in pilCommands)
    {
        string commandName_lower(itr.first), commandName_upper(itr.first), commandName_camel(itr.first);

        CtiToLower(commandName_lower);
        CtiToUpper(commandName_upper);

        std::vector<char> cmdCamel(commandName_camel.begin(), commandName_camel.end());
        cmdCamel.push_back('\0');

        std::vector<char> cmdUpper(commandName_upper.begin(), commandName_upper.end());
        cmdUpper.push_back('\0');

        std::vector<char> cmdLower(commandName_lower.begin(), commandName_lower.end());
        cmdLower.push_back('\0');

        Tcl_CreateCommand( interp, &cmdCamel[0], itr.second, NULL, NULL );
        Tcl_CreateCommand( interp, &cmdUpper[0], itr.second, NULL, NULL );
        Tcl_CreateCommand( interp, &cmdLower[0], itr.second, NULL, NULL );
    }

    for each(const TclCommandMap::value_type& itr in caseSensitiveTclCommands)
    {
        string commandName_lower(itr.first), commandName_upper(itr.first), commandName_camel(itr.first);

        CtiToLower(commandName_lower);
        CtiToUpper(commandName_upper);

        std::vector<char> cmdCamel(commandName_camel.begin(), commandName_camel.end());
        cmdCamel.push_back('\0');

        std::vector<char> cmdUpper(commandName_upper.begin(), commandName_upper.end());
        cmdUpper.push_back('\0');

        std::vector<char> cmdLower(commandName_lower.begin(), commandName_lower.end());
        cmdLower.push_back('\0');

        Tcl_CreateCommand( interp, &cmdCamel[0], itr.second, NULL, NULL );
        Tcl_CreateCommand( interp, &cmdUpper[0], itr.second, NULL, NULL );
        Tcl_CreateCommand( interp, &cmdLower[0], itr.second, NULL, NULL );
    }

    for each (const TclCommandMap::value_type& itr in tclCommands)
    {
        std::vector<char> cmd(itr.first.begin(), itr.first.end());
        cmd.push_back('\0');

        Tcl_CreateCommand( interp, &cmd[0], itr.second, NULL, NULL );
    }

    /* Load up the initialization script */
    string init_script;

    init_script = gConfigParms.getValueAsPath(MCCMD_CTL_SCRIPTS_DIR, "server\\macsscripts");

    //  convert the string to forward slashes
    int pos = 0;
    while( (pos = init_script.find_first_of('\\', pos)) != string::npos )
    {
        init_script[pos] = '/';
    }

    init_script += "/";
    init_script += gConfigParms.getValueAsString(MCCMD_INIT_SCRIPT, "init.tcl");

    gMccmdDebugLevel = gConfigParms.getValueAsULong(MCCMD_DEBUG_LEVEL, 0x00000000);
    gDoNotSendCancel = gConfigParms.isTrue(MACS_DISABLE_CANCEL);
    gIgnoreQueueCount = gConfigParms.isTrue(MACS_IGNORE_QUEUES, true);
    gUseOldStyleMissed = gConfigParms.isTrue(MACS_USE_OLD_MISSED_LIST);

    if( gMccmdDebugLevel > 0 )
    {
        CTILOG_DEBUG(dout, MCCMD_DEBUG_LEVEL <<": 0x"<< std::hex << gMccmdDebugLevel);
    }

    if( gMccmdDebugLevel & MCCMD_DEBUG_INIT )
    {
        CTILOG_DEBUG(dout, "Using MCCMD init script: "<< init_script);
    }

    Tcl_EvalFile(interp, const_cast<char *>(init_script.c_str()));

    return TCL_OK;
}

/*--- Below are the MACS Tcl extensions  ---*/

int Exit(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    //copied from default tcl exit handler
    int value;

    Tcl_Eval(interp, "pilshutdown");

    if( argc != 1 && argc != 2)
    {
        return TCL_ERROR;
    }

    if(argc == 1)
    {
        value = 0;
    }
    else
    {
        value = atoi(argv[1]);
    }

    Tcl_Exit(value);
    /*NOTREACHED*/
    return TCL_OK;       /* Better not ever reach this! */

}

int Command(ClientData clientdata, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoTwoWayRequest(interp, cmd);
}

int GetValue(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoTwoWayRequest(interp, cmd);
}

int PutValue(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoOneWayRequest(interp, cmd);
}

int GetStatus(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoTwoWayRequest(interp, cmd);
}

int PutStatus(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoOneWayRequest(interp, cmd);
}

int GetConfig(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoTwoWayRequest(interp, cmd);
}

int PutConfig(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoOneWayRequest(interp, cmd);
}

int Loop(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoOneWayRequest(interp, cmd);
}

int Control(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoOneWayRequest(interp, cmd);
}

int Scan(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    const string cmd = BuildCommandString(argc, argv);

    return DoOneWayRequest(interp, cmd);
}

int Pil(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if( argc <= 1 )
    {
        CTILOG_ERROR(dout, "Usage: pil command cmd_params");
        return TCL_ERROR;
    }

    //  trim off "pil" - this is the way to pass arbitrary commands to Porter that aren't explicitly handled by MACS
    std::vector<std::string> args(argv + 1, argv + argc);

    string cmd = boost::algorithm::join(args, " ");

    return DoOneWayRequest(interp, cmd);}

int mcu8100(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if( argc != 2 )
    {
        CTILOG_ERROR(dout, "Usage: mcu8100 filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<std::string*> results;

    if( DecodeCFDATAFile( file, &results) == false )
    {
        CTILOG_ERROR(dout, "Could not decode file");
        return TCL_ERROR;
    }

    for each( std::string *str in results )
    {
        Tcl_Eval(interp, const_cast<char *>(str->c_str()));
    }
    delete_container(results);
    results.clear();
    return TCL_OK;
}

/*
    $VSERV=<address>,CONTRACT=(IN|OUT),TEMP=(IN|OUT)

    address := <service area>[,VN<serial #>]
*/
int mcu9000eoi(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{

    if( argc != 2 )
    {
        CTILOG_ERROR(dout, "Usage: mcu9000eoi filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<std::string *> results;

    if( DecodeEOIFile(file, &results) == false )
    {
        CTILOG_ERROR(dout, "Could not decode file");
        return TCL_ERROR;
    }

    for each( std::string *str in results )
    {
        Tcl_Eval(interp, const_cast<char *>(str->c_str()));
    }
    delete_container(results);
    results.clear();
    return TCL_OK;
}

int mcu8100wepco(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    int tcl_ret = TCL_OK;

    if( argc != 2 )
    {
        CTILOG_ERROR(dout, "Usage: mcu8100wepco filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<std::string *> results;

    if( DecodeWepcoFile( file, &results) == false )
    {
        CTILOG_ERROR(dout, "Could not decode file");
        return TCL_ERROR;
    }

    for each( std::string *str in results )
    {
        Tcl_Eval( interp, const_cast<char *>(str->c_str()));
        Sleep(100); // CGP 051302  Buy some sanity.

        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            Tcl_SetResult( interp, "interrupted", TCL_VOLATILE );
            tcl_ret = TCL_ERROR;
            break;
        }
    }
    delete_container(results);
    results.clear();
    return tcl_ret;
}

int mcu8100service(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    int tcl_ret = TCL_OK;

    if( argc != 2 )
    {
        CTILOG_ERROR(dout, "Usage: mcu8100service filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<std::string *> results;

    if( DecodeWepcoFileService( file, &results) == false )
    {
        CTILOG_ERROR(dout, "Could not decode file");
        return TCL_ERROR;
    }

    int num_sent = 0;

    for each( std::string *str in results )
    {
        Tcl_Eval( interp, const_cast<char *>(str->c_str()));
        num_sent++;
        Sleep(100); // CGP 051302  Buy some sanity.

        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            Tcl_SetResult( interp, "interrupted", TCL_VOLATILE );
            tcl_ret = TCL_ERROR;
            break;
        }
    }
    delete_container(results);
    results.clear();

    // set the number of pil requests sent to be the return val
    Tcl_SetResult( interp, (char*)CtiNumStr(num_sent).toString().c_str(), TCL_VOLATILE);
    return tcl_ret;
}

int mcu8100program(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    int tcl_ret = TCL_OK;

    if( argc != 2 )
    {
        CTILOG_ERROR(dout, "Usage: mcu8100program filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<std::string *> results;

    if( DecodeWepcoFileConfig( file, &results) == false )
    {
        CTILOG_ERROR(dout, "Could not decode file");
        return TCL_ERROR;
    }

    int num_sent = 0;

    for each( std::string *str in results )
    {
        Tcl_Eval( interp, const_cast<char *>(str->c_str()));
        num_sent++;
        Sleep(100); // CGP 051302  Buy some sanity.

        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            Tcl_SetResult( interp, "interrupted", TCL_VOLATILE );
            tcl_ret = TCL_ERROR;
            break;
        }
    }
    delete_container(results);
    results.clear();

    // set the number of pil requests sent to be the return val
    Tcl_SetResult( interp, (char*)CtiNumStr(num_sent).toString().c_str(), TCL_VOLATILE);
    return tcl_ret;
}

int pmsi(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if( argc != 2 )
    {
        CTILOG_ERROR(dout, "Usage:  pmsi filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<std::string *> results;

    if( DecodePMSIFile( file, &results) == false )
    {
        CTILOG_ERROR(dout, "Could not decode file");
        return TCL_ERROR;
    }

    for each( std::string *str in results )
    {
        Tcl_Eval( interp, const_cast<char *>(str->c_str()));
    }
    delete_container(results);
    results.clear();
    return TCL_OK;
}

int importCommandFile (ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    int commandLimit=0;
    int commandsPerTime=0;
    int interval=1,decodeResult=ClientErrors::None;
    CHAR newFileName[100];
    int retVal = TCL_OK;
    bool dsm2ImportFlag = false;
    int protocol = TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL;

    if( argc < 2 )
    {
        CTILOG_INFO(dout,
                endl <<"Usage:  importcommandfile filename [/cmdsperexecution:#]"<<
                endl <<"        optional parameters: "<<
                endl <<"              /cmdsperexecution:# - number of commands sent each execution (default to all)"<<
                endl <<"              /protocol:x - specify the protocol all commands should use (default versacom)"<<
                endl <<"                   where x is versacom, expresscom, none (meaning don't specify)"<<
                endl <<"              /dsm2 - import dsm2 vconfig.dat type commands (not valid with other options)"<<
                endl <<"              /appendextension:zzz - files with this extension will be appended to file"<<
                endl <<"                   where zzz is an extension such as txt, dat, cfg, ect.."<<
                endl <<"                   NOTE: Commands are exported to file export\\sent-mm-dd-yyyy.txt");

        retVal = TCL_ERROR;
    }
    else
    {

        string file = argv[1];
        string temp;

        std::vector<std::string *> results;
        ::sprintf (newFileName,"..\\export\\sent-%02d-%02d-%04d.txt",
                 CtiDate().month(),
                 CtiDate().dayOfMonth(),
                 CtiDate().year());

        if( gMccmdDebugLevel > 0 )
        {
            CTILOG_DEBUG(dout, "Will export commands from "<< file <<" to "<< newFileName);
        }


        if(argc > 2)
        {
            for(int i=2; i < argc; i++)
            {
                string str = string(argv[i]);
                std::transform(str.begin(), str.end(), str.begin(), ::tolower);
                // left in here to support Nevada Power
                if(str.find(string ("/perinterval"))!=string::npos)
                {
                    int colon = str.find_first_of(':');

                    if( colon !=string::npos )
                    {
                        commandLimit = atoi (argv[i]+colon+1);
                    }

                    if( gMccmdDebugLevel > 0 )
                    {
                        CTILOG_DEBUG(dout, "Will export "<< commandLimit <<" commands from "<< file <<" per interval");
                    }

                }

                // left in here to support Nevada Power
                if(str.find(string ("/interval"))!=string::npos)
                {
                    int colon = str.find_first_of(':');

                    if( colon !=string::npos )
                    {
                        interval = atoi (argv[i]+colon+1);
                    }
                    if( gMccmdDebugLevel > 0 )
                    {
                        CTILOG_DEBUG(dout, "Interval: "<< interval <<" minutes");
                    }
                }

                if(str.find(string ("/cmdsperexecution"))!=string::npos)
                {
                    int colon = str.find_first_of(':');

                    if( colon !=string::npos )
                    {
                        commandsPerTime = atoi (argv[i]+colon+1);
                    }
                    if( gMccmdDebugLevel > 0 )
                    {
                        CTILOG_DEBUG(dout, "Will export "<< commandsPerTime <<" commands from "<< file <<" every execution");
                    }

                }

                if(str.find(string ("/appendextension"))!=string::npos)
                {
                    int colon = str.find_first_of(':');

                    HANDLE searchHandle;
                    string findstr = "*.";
                    findstr += (argv[i]+colon+1);
                    string path = file;
                    path.resize(path.find_last_of("\\/")+1);
                    findstr = path + findstr;

                    WIN32_FIND_DATA findData;

                    if( (searchHandle = FindFirstFile(findstr.c_str(), &findData)) != INVALID_HANDLE_VALUE )
                    {
                        do
                        {
                            FileAppendAndDelete(file, path + findData.cFileName);

                        } while( FindNextFile(searchHandle, &findData) );
                    }
                }

                if(str.find(string ("/dsm2"))!=string::npos)
                {
                    dsm2ImportFlag = true;
                    if( gMccmdDebugLevel > 0 )
                    {
                        CTILOG_DEBUG(dout, "Will import file as DSM2 vconfig.dat format");
                    }
                }
                if(str.find(string ("/protocol"))!=string::npos)
                {
                    if(str.find(string ("versacom"))!=string::npos)
                    {
                        protocol = TEXT_CMD_FILE_SPECIFY_VERSACOM;

                        if( gMccmdDebugLevel > 0 )
                        {
                            CTILOG_DEBUG(dout, "Will export commands using versacom only");
                        }
                    }
                    else if(str.find(string ("expresscom"))!=string::npos)
                    {
                        protocol = TEXT_CMD_FILE_SPECIFY_EXPRESSCOM;

                        if( gMccmdDebugLevel > 0 )
                        {
                            CTILOG_DEBUG(dout, "Will export commands using expresscom only");
                        }
                    }
                    else if(str.find(string ("none"))!=string::npos)
                    {
                        protocol = TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL;

                        if( gMccmdDebugLevel > 0 )
                        {
                            CTILOG_DEBUG(dout, "Will export commands without specifying protocol");
                        }
                    }
                    else
                    {
                        protocol = TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL;

                        if( gMccmdDebugLevel > 0 )
                        {
                            CTILOG_DEBUG(dout, "Will export commands without specifing protocol");
                        }
                    }
                }
            }
        }

        if(dsm2ImportFlag)
        {
            decodeResult = decodeDSM2VconfigFile (file, &results);
            if(decodeResult)
            {
                CTILOG_ERROR(dout, "Unable to import file "<< file);
            }

        }
        else
        {
            decodeResult = decodeTextCommandFile( file, commandsPerTime, protocol, &results);

            if(decodeResult == TEXT_CMD_FILE_LOG_FAIL )
            {
                string logMsg;
                string infoMsg;
                logMsg = string ("Logging commands from file ");
                logMsg += file;
                logMsg += string (" failed ");
                infoMsg = string ("Log file ");
                infoMsg += string (newFileName);
                infoMsg += string (" is locked by another process");

                if( VanGoghConnection )
                {
                    auto msg = std::make_unique<CtiSignalMsg>(
                        SYS_PID_MACS,
                        0,
                        logMsg,
                        infoMsg,
                        7,
                        SignalEvent,
                        "macs");

                    // we wait this to keep running even if it fails
                    VanGoghConnection->WriteConnQue(std::move(msg), CALLSITE);
                }
                else
                {
                    CTILOG_WARN(dout, "Could not send signal to Dispatch - VanGoghConnection is null\n" << logMsg << "- " << infoMsg);
                }

                decodeResult = ClientErrors::None;
            }
            else if(decodeResult == TEXT_CMD_FILE_UNABLE_TO_EDIT_ORIGINAL)
            {
                string logMsg;
                string infoMsg;
                logMsg = string ("Removing sent commands from file ");
                logMsg += file;
                logMsg += string (" failed ");
                infoMsg = string ("Original file ");
                infoMsg += file;
                infoMsg += string (" is locked by another process");

                if( VanGoghConnection )
                {
                    auto msg = std::make_unique<CtiSignalMsg>(
                        SYS_PID_MACS,
                        0,
                        logMsg,
                        infoMsg,
                        7,
                        SignalEvent,
                        "macs");

                    // we wait this to keep running even if it fails
                    VanGoghConnection->WriteConnQue(std::move(msg), CALLSITE);
                }
                else
                {
                    CTILOG_WARN(dout, "Could not send signal to Dispatch - VanGoghConnection is null\n" << logMsg << "- " << infoMsg);
                }
            }
            else if(decodeResult == TEXT_CMD_FILE_UNABLE_TO_OPEN_FILE)
            {
                CTILOG_ERROR(dout, "Export file "<< file <<" does not exist or is locked");
            }
        }
        // send what we do have

        for each( std::string *str in results )
        {
            Tcl_Eval( interp, const_cast<char *>(str->c_str()));
        }
        delete_container(results);
        results.clear();
    }
    return retVal;
}

//Remember this deletes the old file if the append is successful!
bool FileAppendAndDelete(const string &toFileName, const string &fromFileName)
{
    HANDLE toFileHandle;
    ULONG fileSize,bytesRead,bytesWritten;
    CHAR *workBuffer;
    CHAR newFileName[100];
    bool copyFailed = true;
    bool retVal = true;
    int cnt=0;

    if( toFileName != fromFileName )
    {

        // create or open file of the day
        toFileHandle = CreateFile (toFileName.c_str(),
                                     GENERIC_READ | GENERIC_WRITE,
                                     0,
                                     NULL,
                                     OPEN_ALWAYS,
                                     FILE_ATTRIBUTE_NORMAL,
                                     NULL);
        // loop around until we get exclusive access to this guy
        while (toFileHandle == INVALID_HANDLE_VALUE && cnt < 30)
        {
            if (GetLastError() == ERROR_SHARING_VIOLATION || GetLastError() == ERROR_LOCK_VIOLATION)
            {
                toFileHandle = CreateFile (toFileName.c_str(),
                                             GENERIC_READ | GENERIC_WRITE,
                                             0,
                                             NULL,
                                             OPEN_ALWAYS,
                                             FILE_ATTRIBUTE_NORMAL,
                                             NULL);

                CTILOG_ERROR(dout, "file "<< toFileName <<" is locked");

                cnt++;
                Sleep (1000);
            }
            else
                break;
        }

        // if we tried for 30 seconds, log that we failed to log the file
        if (cnt >= 30)
        {
            // since we couldn't create the tmp file, we won't delete the current one
            retVal = false;
        }
        else
        {
            FILE* fromfileptr;
            char workString[500];  // not real sure how long each line possibly is
            vector<string> aCmdVector;

            // open file
            if( (fromfileptr = fopen( fromFileName.c_str(), "r")) == NULL )
            {
                retVal = false;
            }
            else
            {
                // load list in the command vector
                while ( fgets( (char*) workString, 500, fromfileptr) != NULL )
                {
                    string entry (workString);
                    aCmdVector.push_back (entry);
                }
            }

            // loop the vector and append to the file
            int     totalLines = aCmdVector.size();
            int     lineCnt = 0;
            int     retCode = 0;

            while (lineCnt < totalLines)
            {
                // move to end of file and write
                retCode=SetFilePointer(toFileHandle,0,NULL,FILE_END);
                retCode=SetEndOfFile (toFileHandle);
                memset (workString, '\0',500);
                strcpy (workString,aCmdVector[lineCnt].c_str());

                if (workString[aCmdVector[lineCnt].length()-1] == '\n')
                {
                    workString[aCmdVector[lineCnt].length()-1] = '\r';
                    workString[aCmdVector[lineCnt].length()] = '\n';
                    retCode=WriteFile (toFileHandle,workString,aCmdVector[lineCnt].length()+1,&bytesWritten,NULL);
                }
                else
                {
                    workString[aCmdVector[lineCnt].length()] = '\r';
                    workString[aCmdVector[lineCnt].length()+1] = '\n';
                    retCode=WriteFile (toFileHandle,workString,aCmdVector[lineCnt].length()+2,&bytesWritten,NULL);
                }
                lineCnt++;
            }

            CloseHandle (toFileHandle);
            fclose(fromfileptr);
            DeleteFile (fromFileName.c_str());
        }
    }

    return retVal;
}


int isHoliday(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    time_t t = ::time(NULL);
    int id = 0;

    if( argc >= 2 )
    {
        t = (time_t) atoi(argv[1]);
    }

    if( argc >= 3 )
    {
        id = atoi(argv[2]);
    }
    // This will only get set if running inside MACS
    else if( const char* h_sched_id = Tcl_GetVar(interp, HolidayScheduleIDVariable, 0 ) )
    {
        id = atoi(h_sched_id);
    }

    CtiHolidayManager& mgr = CtiHolidayManager::getInstance();
    struct tm *temp;
    temp = CtiTime::localtime_r(&t);
    if( mgr.isHoliday(CtiDate(temp), id) )
    {
        Tcl_SetResult(interp, "true", TCL_VOLATILE );
        return TCL_OK;
    }

    Tcl_SetResult(interp, "false", TCL_VOLATILE );
    return TCL_OK;
}


int LogEvent(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[] )
{
    string user = "";
    string message = "";
    string info = "";
    int classification = 7;
    int index = 1;

    if( argc != 4 && argc != 6 )
    {
        CTILOG_ERROR(dout, "Usage:  LogEvent [ class # ] user message info - class # is greater than 1, user, message, info are strings");
        return TCL_OK;
    }

    if( argc == 6 )
    {
        string param(argv[index++]);
        std::transform(param.begin(), param.end(), param.begin(), ::tolower);
        if( param == "class" )
        {
            classification = atoi( argv[index++] );
        }
    }

    user = argv[index++];
    message = argv[index++];
    info = argv[index++];

    if( VanGoghConnection )
    {
        auto msg = std::make_unique<CtiSignalMsg>(
            SYS_PID_MACS,
            0,
            message,
            info,
            classification,
            SignalEvent,
            user);
        VanGoghConnection->WriteConnQue(std::move(msg), CALLSITE);
    }
    else
    {
        CTILOG_WARN(dout, "Could not log event - VanGoghConnection is null" << Cti::FormattedList::of(
            "Classification", classification,
            "User", user,
            "Message", message,
            "Info", info));
    }

    return TCL_OK;
}

int Dout(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if( argc != 2 )
        return TCL_ERROR;

    CTILOG_INFO(dout, argv[1]);

    return TCL_OK;
}

/*----------------------------------------------------------------------------
  SendNotification

  Sends a signal to dispatch that will tell it to notify a notification group.

  Takes 3 parameters in argv

  name of the notification group
  subject line of the email
  text or body of the email
----------------------------------------------------------------------------*/

int SendNotification(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if( argc != 4 )
    {
        CTILOG_ERROR(dout, "Usage:  SendNotification groupname subject text");
        return TCL_OK;
    }

    string name(argv[1]);

    long id = GetNotificationGroupID(name);

    if( id == -1 )
    {
        CTILOG_ERROR(dout, "Could not locate notification group named: "<< name);
        return TCL_ERROR;
    }

    auto msg = std::make_unique<CtiNotifEmailMsg>();
    msg->setNotifGroupId(id);
    msg->setSubject(argv[2]);
    msg->setBody(argv[3]);

    Cti::FormattedList logList;
    logList.add("notification group id") << msg->getNotifGroupId();
    logList.add("subject")               << msg->getSubject();
    logList.add("text")                  << msg->getBody();

    if( NotificationConnection )
    {
        CTILOG_INFO(dout, "Sending email notification to the notification server" 
            << logList);

        NotificationConnection->WriteConnQue(std::move(msg), CALLSITE);
    }
    else
    {
        CTILOG_WARN(dout, "Could not send email notification - NotificationConnection is null"
            << logList);
    }

    return TCL_OK;
}


int Select(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if( argc == 1 )
    {
        const string selection = Tcl_GetVar(interp, SelectedVariable, 0 );
        CTILOG_INFO(dout, "current selection: "<< selection);
    }
    else
    {
        //  create a "select deviceid 8"-style string to append to the MACS command
        string cmd = BuildCommandString(argc, argv);

        Tcl_SetVar(interp, SelectedVariable, (char*) cmd.c_str(), 0 );
    }
    return TCL_OK;
}

int Wait(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if( argc < 2 )
    {
        CTILOG_ERROR(dout, "Usage:  Wait <seconds>");
    }

    long delay = atol(argv[1]);
    time_t start = ::time(NULL);

    while( start + delay > ::time(NULL) )
    {
        //Check for cancellation
        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            CTILOG_WARN(dout, "interrupted");
            Tcl_SetResult( interp, "interrupted", TCL_VOLATILE );
            return TCL_ERROR;
        }

        Sleep(1000);
    }

    return TCL_OK;
}

int getDeviceName(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
  if(argc < 2)
    {
      CTILOG_ERROR(dout, "Usage: getDeviceName <deviceid>");
      Tcl_SetResult(interp, "0", TCL_VOLATILE);
      return TCL_OK;
    }

  const long id = atoi(argv[1]);
  const string name = GetDeviceName(id);
  Tcl_Obj* tcl_name = Tcl_NewStringObj((const char*)name.c_str(), -1);
  Tcl_SetObjResult(interp, tcl_name);
  return TCL_OK;
}

int getDeviceID(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
  if(argc < 2)
    {
      CTILOG_ERROR(dout, "Usage: getDeviceID <devicename>");
      return TCL_OK;
    }

  long id = GetDeviceID(string(argv[1]));
  Tcl_Obj* tcl_id = Tcl_NewStringObj(CtiNumStr(id).toString().c_str(),-1);
  Tcl_SetObjResult(interp, tcl_id);
  return TCL_OK;
}

int formatError(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
  if(argc < 2)
    {
      CTILOG_ERROR(dout, "Usage: formatError <errorcode>");
      return TCL_OK;
    }

  int id = atoi(argv[1]);
  string err_str = CtiError::GetErrorString(static_cast<YukonError_t>(id));
  Tcl_Obj* tcl_str = Tcl_NewStringObj(err_str.c_str(),-1);
  Tcl_SetObjResult(interp, tcl_str);
  return TCL_OK;
}

int getYukonBaseDir(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
  string base_dir = gConfigParms.getYukonBase();
  Tcl_Obj* tcl_str = Tcl_NewStringObj(base_dir.c_str(), -1);
  Tcl_SetObjResult(interp, tcl_str);
  return TCL_OK;
}

//  Workaround to add "clock" back to the interpreter without requiring TCL's init.tcl and the whole TCL library
int yukonClock(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[])
{
    if(argc >= 3 && argv[1] == "format"s)
    {
        struct TimeFormatter {
            const char* format_string;
            decltype(&localtime_s) time_method;
        };

        static const std::map<std::string, TimeFormatter> formats {
            { "mdy", { "%D %T", localtime_s } },
            { "ymd", { "%F %T", localtime_s } },
            { "utc", { "%F %T", gmtime_s } },
        };

        if( auto time_formatter = Cti::mapFind(formats, argv[2]) )
        {
            //  Get the time using the requested time method (localtime or gmtime)
            tm time_value;
            const auto current_time = std::time(nullptr);
            time_formatter->time_method(&time_value, &current_time);

            //  Format the string and assign it into the TCL string
            std::array<char, 25> result;
            const auto length = std::strftime(result.data(), result.size(), time_formatter->format_string, &time_value);
            Tcl_SetObjResult(interp, Tcl_NewStringObj(result.data(), length));
            return TCL_OK;
        }
    }
    else if(argc >= 2 && argv[1] == "seconds"s )
    {
        Tcl_Obj* tcl_seconds = Tcl_NewIntObj(time(nullptr));
        Tcl_SetObjResult(interp, tcl_seconds);
        return TCL_OK;
    }

  CTILOG_ERROR(dout, "Usage: clock (seconds|format (mdy|ymd|utc))");
  return TCL_ERROR;
}

int DoOneWayRequest(Tcl_Interp* interp, const string &cmd_line)
{
    char* p;
    long timeout = DEFAULT_ONE_WAY_TIMEOUT;

    string timeoutStr;
    boost::match_results<std::string::const_iterator> what;
    if(boost::regex_search(cmd_line, what, re_timeout, boost::match_default))
    {
        timeoutStr = static_cast<string>(what[0]);
        if(boost::regex_search(timeoutStr, what, re_num, boost::match_default))
        {
            timeout = strtol((static_cast<string>(what[0])).c_str(),&p,10);
        }
    }
    return DoRequest(interp,cmd_line,timeout,false);
}

int DoTwoWayRequest(Tcl_Interp* interp, const string &cmd_line)
{
    char* p;
    long timeout = DEFAULT_TWO_WAY_TIMEOUT;
    string timeoutStr;
    boost::match_results<std::string::const_iterator> what;
    if(boost::regex_search(cmd_line, what, re_timeout, boost::match_default))
    {
        timeoutStr = static_cast<string>(what[0]);
        if(boost::regex_search(timeoutStr, what, re_num, boost::match_default))
        {
            timeout = strtol((static_cast<string>(what[0])).c_str(),&p,10);
        }
    }
    return DoRequest(interp,cmd_line,timeout,true);
}

static bool isBreakStatus( int status )
{
    switch( status )
    {
        case ClientErrors::DeviceInhibited:
        case ClientErrors::NoMethod:
        case ClientErrors::InvalidSSPEC:
        case ClientErrors::VerifySSPEC:
        case ClientErrors::IdNotFound:
        case ClientErrors::CommandAlreadyInProgress:
            return false;

        default:
            return true;
    }
}

static int DoRequest(Tcl_Interp* interp, const string &cmd_line, long timeout, bool two_way)
{
    bool interrupted = false;
    bool timed_out = false;
    UINT jobId = 0;
    UINT const PORT_COUNT_REQUEST_SECONDS = 5*60;
    long requestLogId = 0;
    unsigned long porterCount = 0;
    CtiTime lastReturnMessageReceived;

    //Create a CountedPCPtrQueue and place it into the InQueueStore
    //Be sure to remove it before exiting
    unsigned int msgid = GenMsgID();

    if( const char* jobIdStr = Tcl_GetVar(interp, "DeviceReadLogId", 0 ) )
    {
        jobId = atoi(jobIdStr);
    }

    CtiTblDeviceReadRequestLog deviceReadLog(requestLogId, msgid, cmd_line, CtiTime::now(), CtiTime::now(), jobId);

    if( timeout != 0 ) // don't bother if we don't want responses
    {
        if( jobId > 0 )
        {
            requestLogId = SynchronizedIdGen("DeviceReadRequestLog", 1);

            if ( requestLogId != 0 )
            {
                deviceReadLog.setRequestLogId(requestLogId);
                deviceReadLog.Insert();
            }
            else
            {
                CTILOG_ERROR(dout, "Invalid Connection to Database.");
                return TCL_ERROR;
            }
        }
    }

    std::vector<CtiRequestMsg *> requests = BuildRequestSet(interp, cmd_line);

    // Nothing to do get outta here
    if( requests.empty() )
        return TCL_OK;

    //build up a multi and write out all of the requests
    CtiMultiMsg* multi_req = new CtiMultiMsg();

    for each( CtiRequestMsg *req in requests )
    {
        req->setUserMessageId(msgid);
        req->setGroupMessageId(msgid);

        if( gMccmdDebugLevel & MCCMD_DEBUG_PILREQUEST )
        {
            CTILOG_DEBUG(dout, "request: "<< DumpRequestMessage(*req));
        }
        else
        {
            CTILOG_INFO(dout, "request: "<< req->CommandString());
        }

        multi_req->getData().push_back(req);
    }

    RequestQueues::ptr_type requestQueue;

    if( timeout )
    {
        requestQueue.reset(new CtiFIFOQueue<CtiMessage>);

        inboundMessageQueues.insert(msgid, requestQueue);
    }

    PorterConnection->WriteConnQue(multi_req, CALLSITE);

    // We dont care about responses, dont set up the queue, send the message and exit.
    if ( ! timeout )
    {
        return TCL_OK;
    }

    const CtiTime start;
    CtiTime lastPorterCountTime;
    //We will poll this in 60 seconds
    lastPorterCountTime = lastPorterCountTime - PORT_COUNT_REQUEST_SECONDS + 60;

    // Some structures to sort the responses
    PILReturnMap device_map;
    PILReturnMap good_map;
    PILReturnMap bad_map;
    std::vector<string> bad_names;
    std::deque<CtiTableMeterReadLog> resultQueue;
    int queueDataZeroCount = 0;
    bool status;

    do
    {
        {
            std::unique_ptr<CtiMessage> msg(requestQueue->getQueue(100));

            if( msg.get() )
            {
                if( msg->isA() == MSG_PCRETURN )
                {
                    std::unique_ptr<CtiReturnMsg> ret_msg(static_cast<CtiReturnMsg*>(msg.release()));
                    CTILOG_INFO(dout, DumpReturnMessage(*ret_msg));
                    const bool allowExitOnError = isBreakStatus(ret_msg->Status());
                    HandleReturnMessage(ret_msg.release(), good_map, bad_map, device_map, bad_names, resultQueue);
                    lastReturnMessageReceived = lastReturnMessageReceived.now();

                    // have we received everything expected?
                    if( device_map.size() == 0 && allowExitOnError )
                        break; // from the loop
                }
                else if( msg->isA() == MSG_QUEUEDATA )
                {
                    const CtiQueueDataMsg& queueMessage = static_cast<const CtiQueueDataMsg&>(*msg);
                    if( queueMessage.getRequestId() == msgid )
                    {
                        porterCount = queueMessage.getRequestIdCount();

                        CTILOG_INFO(dout, "Queue Data Received, there are "<< porterCount <<" objects for this script in porter.");

                        if( porterCount == 0 )
                        {
                            CTILOG_WARN(dout, "Porter has reported a count of 0 messages for this script. "<<
                                    endl <<"MACS reports "<< device_map.size() <<" devices left to respond. If the script does not finish very soon this should be considered a problem.");

                            queueDataZeroCount++;

                            if( queueDataZeroCount > 1 )
                            {
                                // At some point we could break; here if we are confident about this.
                                CTILOG_INFO(dout, "Queue data reports a need to exit, current command is exiting!");
                                break; // from the loop
                            }
                        }
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "received unexpected message ("<< msg->isA() <<")");
                }
            }
        }

        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            Tcl_SetResult( interp, "interrupted", TCL_VOLATILE );
            interrupted = true;
            break;
        }

        // Time out if no messages have been received from porter for any request
        // in timeout seconds (not just the current one) - BUT make sure at least
        // one timeout has elapsed since we started.
        // The downside to this is if we are frequently scanning some device, macs
        // will potentially never time anything out - this should be considered a short
        // term fix.
        CtiTime now;
        if( (now > lastReturnMessageReceived + timeout) &&
            (now > start + timeout) && (gIgnoreQueueCount || porterCount <= 0) )
        {
            CTILOG_INFO(dout, "The following command timed out:"<<
                    endl << cmd_line <<
                    endl <<"command was originally submitted at: "<< start <<
                    endl <<"nothing was received from porter in the last "<< (now.seconds() - lastReturnMessageReceived.seconds()) <<" seconds");

            break;
        }

        if( now > lastPorterCountTime + PORT_COUNT_REQUEST_SECONDS )
        {
            lastPorterCountTime = now;
            PorterConnection->WriteConnQue( CTIDBG_new CtiRequestMsg( 0, "system message request count", msgid, msgid ), CALLSITE );
        }
    } while(true);

    if( interrupted )
    {
        CTILOG_INFO(dout, "Command exiting due to interrupt!");
    }

    // We now always send the cancel message.
    if( two_way && timeout > 0 && !gDoNotSendCancel)
    {
        if( PorterConnection )
        {
            PorterConnection->WriteConnQue(CTIDBG_new CtiRequestMsg(0, "system message request cancel", msgid, msgid), CALLSITE);
        }
        else
        {
            CTILOG_WARN(dout, "Porter connection has been reset - could not send cancel for request ID " << msgid);
        }
    }

    // set up good and bad tcl lists
    Tcl_Obj* good_list = Tcl_NewListObj(0,NULL);
    Tcl_Obj* bad_list = Tcl_NewListObj(0,NULL);
    Tcl_Obj* status_list = Tcl_NewListObj(0,NULL);

    Tcl_SetVar2Ex(interp, GoodListVariable, NULL, good_list, 0 );
    Tcl_SetVar2Ex(interp, BadListVariable, NULL, bad_list, 0 );
    Tcl_SetVar2Ex(interp, BadStatusVariable, NULL, status_list, 0);

    int count;

    count = 0;

    for each( PILReturnMap::value_type good_result in good_map )
    {
        if( !(++count % 10000) )
        {
            CTILOG_INFO(dout, "Writing good list, "<< count <<" / "<< good_map.size() <<" written");
        }

        std::string next_line = GetDeviceName(good_result.first);
        if( !gUseOldStyleMissed )
        {
            next_line += ", ";
            next_line += CtiNumStr(good_result.first);
        }
        Tcl_ListObjAppendElement(interp, good_list, Tcl_NewStringObj(next_line.c_str(), -1));
    }

    if( count % 10000 )
    {
        CTILOG_INFO(dout, "Writing good list, "<< count <<" / "<< good_map.size() <<" written");
    }

    count = 0;

    for each( PILReturnMap::value_type bad_result in bad_map )
    {
        if( !(++count % 10000) )
        {
            CTILOG_INFO(dout, "Writing bad list, "<< count <<" / "<< bad_map.size() <<" written");
        }

        std::string next_line = (bad_result.second.status == ClientErrors::IdNotFound)
                ? bad_result.second.deviceName
                : GetDeviceName(bad_result.first);

        if( !gUseOldStyleMissed )
        {
            next_line += ", ";
            next_line += CtiNumStr(bad_result.first);
        }

        Tcl_ListObjAppendElement(interp, bad_list, Tcl_NewStringObj(next_line.c_str(), -1));
        Tcl_ListObjAppendElement(interp, status_list,
        Tcl_NewIntObj(bad_result.second.status));
    }

    if( count % 10000 )
    {
        CTILOG_INFO(dout, "Writing bad list, "<< count <<" / "<< bad_map.size() <<" written");
    }

    for each( const string &str in bad_names )
    {
        Tcl_ListObjAppendElement(interp, bad_list, Tcl_NewStringObj(str.c_str(), -1));
    }

    // any device id's left in this set must have timed out
    count = 0;

    for each( PILReturnMap::value_type orphan_result in device_map )
    {
        if( !(++count % 10000) )
        {
            CTILOG_INFO(dout, "Writing orphans, "<< count <<" / "<< device_map.size() <<" written");
        }

        resultQueue.push_back(CtiTableMeterReadLog(0, orphan_result.first, 0, ClientErrors::MacsTimeout, orphan_result.second.time));

        std::string next_line = GetDeviceName(orphan_result.first);
        if( !gUseOldStyleMissed )
        {
            next_line += ", ";
            next_line += CtiNumStr(orphan_result.first);
        }

        Tcl_ListObjAppendElement(interp, bad_list, Tcl_NewStringObj(next_line.c_str(), -1));
        Tcl_ListObjAppendElement(interp, status_list,
        Tcl_NewIntObj(orphan_result.second.status));
    }

    if( count % 10000 )
    {
        CTILOG_INFO(dout, "Writing orphans, "<< count <<" / "<< device_map.size() <<" written");
    }

    //Remove the requestQueue from the inboundMessageQueues
    inboundMessageQueues.remove(msgid);

    //Lets write this to the screen before the database locks us up.
    while( CtiMessage* msg = requestQueue->getQueue(10) )
    {
        switch( msg->isA() )
        {
            case MSG_PCRETURN:
                CTILOG_INFO(dout, DumpReturnMessage(static_cast<CtiReturnMsg &>(*msg)));
                break;
            case MSG_QUEUEDATA:
            case MSG_REQUESTCANCEL:
                CTILOG_INFO(dout, *msg);
                break;
        }
        delete msg;
    }

    CTILOG_INFO(dout, "Database Write Starting");

    WriteResultsToDatabase(resultQueue, requestLogId);

    if( timeout != 0 && jobId > 0 ) // don't bother if we don't want responses
    {
        deviceReadLog.setStopTime(CtiTime::now());
        deviceReadLog.Update();
    }

    CTILOG_INFO(dout, "Database Write Complete");

    return (interrupted ?
                TCL_ERROR : TCL_OK);
}

void HandleReturnMessage(CtiReturnMsg* msg,
             PILReturnMap& good_map,
             PILReturnMap& bad_map,
             PILReturnMap& device_map,
             std::vector<string>& bad_names,
             std::deque<CtiTableMeterReadLog>& resultQueue )
{
    static const string UnknownName = "**Unknown Name**";

    long dev_id = msg->DeviceId();

    if( msg->Status() == ClientErrors::IdNotFound )
    {
        // Either the Device ID or the Device Name was wacky and we couldn't find it in the database.
        // Determine which and act accordingly...

        // The only way to know what happened here is to check the ID. If it's greater than zero, the
        // ID was not found in the database and we need to keep that ID. Otherwise we need to keep the
        // device name somehow...

        MACS_Return_Data data;
        data.time = msg->getMessageTime();
        data.status = msg->Status();

        if( dev_id == 0 ) // Our request has an invalid name.
        {
            CtiCommandParser parser(msg->CommandString());
            string dname = parser.getsValue("device");
            if(!dname.empty())
            {
                bad_names.push_back(dname);
            }
        }
        else // DeviceId > 0 (right?)
        {
            data.deviceName = UnknownName;
            bad_map.insert(PILReturnMap::value_type(dev_id, data));
        }
    }
    else if( good_map.find(dev_id) != good_map.end() )
    {
        CTILOG_WARN(dout, "Received a message for a device already in the good list, id: "<< dev_id);
    }
    else
    {
        MACS_Return_Data data;
        data.time = msg->getMessageTime();
        data.status = msg->Status();

        if( msg->Status() == ClientErrors::DeviceInhibited )
        {
            //Ignore it!
        }
        else if( msg->ExpectMore() )
        {
            device_map.insert(PILReturnMap::value_type(dev_id, data));

            if( msg->Status() )
            {
                CtiTableMeterReadLog result(0, msg->DeviceId(), 0, msg->Status(), msg->getMessageTime());
                resultQueue.push_back(result);
            }
        }
        else
        {
            CtiTableMeterReadLog result(0, msg->DeviceId(), msg->UserMessageId(), msg->Status(), msg->getMessageTime());
            resultQueue.push_back(result);
            PILReturnMap::iterator pos;
            if( msg->Status() == 0 )
            {
                pos = bad_map.find(dev_id);
                if(pos != bad_map.end())
                {
                    CTILOG_WARN(dout, "Moved device from bad list to good list, id: "<< dev_id);
                    bad_map.erase(pos);
                }

                pos = device_map.find(dev_id);
                if(pos != device_map.end())
                {
                    device_map.erase(pos);
                }

                if( !good_map.insert(PILReturnMap::value_type(dev_id,data)).second )
                {
                    CTILOG_WARN(dout, "Device already in good list, id: "<< dev_id)
                }
            }
            else
            {
                pos = device_map.find(dev_id);
                if(pos != device_map.end())
                {
                    device_map.erase(pos);
                }

                if( !bad_map.insert(PILReturnMap::value_type(dev_id,data)).second)
                {
                    CTILOG_WARN(dout, "Device already in bad list, id: "<< dev_id)
                }
            }
        }
    }

    if( msg != NULL )
    {
        delete msg;
        msg = NULL;
    }
}

/* put the thread id into the high order and the messageid into the low*/
unsigned int GenMsgID()
{
    CtiLockGuard<CtiCriticalSection> guard(_queue_mux);

    //  This can break - we need to be using a long long here instead
    return (GetCurrentThreadId() << 16) + gUserMessageID++;
}

unsigned int GetThreadIDFromMsgID(unsigned int msg_id)
{
    return(msg_id >> 16);
}

/*----------------------------------------------------------------------------
  GetNotificationGroupID

  Retrieves the notification group ID from the database given the groups name.
  Returns -1 if no id was found.
----------------------------------------------------------------------------*/
long GetNotificationGroupID( const string& name)
{
    const string sql = "SELECT NotificationGroupID FROM NotificationGroup WHERE GroupName='" + name + "'";

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
    rdr.execute();

    //Assume there is only one?
    if( ! rdr() )
    {
        CTILOG_ERROR(dout, "DB read failed to retrieve notification group id, SQL query: "<< sql);
        return -1;
    }

    long id;
    rdr >> id;

    return id;
}

string GetDeviceName(long deviceID)
{
    const string sql = "SELECT PAOName FROM YukonPAObject WHERE YukonPAObject.PAObjectID=" + CtiNumStr(deviceID);

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
    rdr.execute();

    if( ! rdr() )
    {
        CTILOG_ERROR(dout, "DB read failed to retrieve device name, SQL query: "<< sql);
        return "";
    }

    string name;
    rdr >> name;

    return name;
}

static long GetDeviceID(const string& name)
{
    const string sql = "SELECT PAOBJECTID FROM YukonPAObject WHERE YukonPAObject.PAOName='" + name + "'";

    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
    rdr.execute();

    if( ! rdr() )
    {
        CTILOG_ERROR(dout, "DB read failed to retrieve device id, SQL query: "<< sql);
        return 0;
    }

    long id;
    rdr >> id;

    return id;
}

std::vector<CtiRequestMsg *> BuildRequestSet(Tcl_Interp* interp, std::string cmd_line)
{
    std::vector<CtiRequestMsg *> req_set;

    unsigned int * ptr;
    if( cmd_line.find("select")==string::npos )
    {
        // cmd_line doesn't specify a select string so lets append
        // this interpreters current select string
        string selection = Tcl_GetVar(interp, SelectedVariable, 0);
        cmd_line += " ";
        cmd_line += selection;


    }

    size_t end_index;

    int priority = 7;
    if( const char* pStr = Tcl_GetVar(interp, PILRequestPriorityVariable, TCL_GLOBAL_ONLY) )
    {
        priority = atoi(pStr);
        if( priority < 1 || priority > 15 )
        {
            priority = 7;
            CTILOG_ERROR(dout, "MessagePriority is invalid, defaulting to "<< priority);
        }
    }

    boost::regex select_list_regex = boost::regex(".*select[ ]+list[ ]+");
    boost::regex select_id_regex = boost::regex(".*select[ ]+[^ ]+[ ]+id");
    boost::regex select_deviceid_regex = boost::regex(".*select deviceid");
    boost::regex select_regex = boost::regex(".*select[ ]+[^ ]+[ ]+");

    if( Cti::locateRegex(cmd_line, select_list_regex, &end_index, 0) != string::npos )
    {
        int list_len;
        Tcl_Obj* sel_str = Tcl_NewStringObj( cmd_line.c_str() + end_index, -1 );
        Tcl_ListObjLength(interp, sel_str, &list_len );

        boost::regex e1 = boost::regex("\n");
        boost::regex e2 = boost::regex("select.*");
        cmd_line = boost::regex_replace(cmd_line, e1, " ", boost::match_default | boost::format_all | boost::format_first_only);
        cmd_line = boost::regex_replace(cmd_line, e2, "", boost::match_default | boost::format_all | boost::format_first_only);


        if( list_len > 0 )
        {
            Tcl_Obj* sel_list;
            Tcl_ListObjIndex(interp, sel_str, 0, &sel_list);
            Tcl_ListObjLength(interp, sel_list, &list_len);

            for( int i = 0; i < list_len; i++ )
            {
                Tcl_Obj* elem;
                Tcl_ListObjIndex(interp, sel_list, i, &elem);

                string cmd(cmd_line.c_str());
                string tempString = Tcl_GetString(elem);
                CtiTokenizer token(tempString);

                string devName = token(",");
                string paoIdStr = token(",");
                int paoId = INT_MIN;
                if( !paoIdStr.empty() )
                {
                    paoId = atoi(paoIdStr.c_str()); // This can give INT_MIN or INT_MAX or 0 as errors.
                }

                CtiRequestMsg *msg = new CtiRequestMsg();

                // Note that yes, 0 could be valid, but we are excluding it as it can also be an error
                // and the select name will work properly.
                if( paoId == INT_MIN || paoId == INT_MAX || paoId == 0)
                {
                    cmd += "select name '";
                    cmd += devName;
                    cmd += "'";
                    msg->setDeviceId(0);
                }
                else
                {
                    msg->setDeviceId(paoId);
                }

                msg->setCommandString(cmd);
                msg->setMessagePriority(priority);
                req_set.push_back(msg);
            }
        }

        // clean up
        Tcl_DecrRefCount(sel_str);
    }
    else //dont add quotes if it is an id
    if( Cti::containsRegex(cmd_line, select_id_regex) ||
        Cti::containsRegex(cmd_line, select_deviceid_regex) )
    {
        CtiRequestMsg *msg = new CtiRequestMsg();
        msg->setDeviceId(0);
        msg->setCommandString(cmd_line);
        msg->setMessagePriority(priority);
        req_set.push_back(msg);
    }
    else
    if( Cti::locateRegex(cmd_line, select_regex, &end_index, 0) != string::npos )
    {

        //PIL likes to see ' around any device, group, etc
        cmd_line.insert(end_index, "'");
        trim(cmd_line);
        cmd_line.append("'");

        size_t index;

        //strip out the braces
        while( (index = cmd_line.find("{")) != string::npos )
        {
            cmd_line.erase(index,1);
        }

        while( (index = cmd_line.find('}')) != string::npos )
        {
            cmd_line.erase(index,1);
        }

        CtiRequestMsg *msg = new CtiRequestMsg();
        msg->setDeviceId(0);
        msg->setCommandString(cmd_line);
        msg->setMessagePriority(priority);
        req_set.push_back(msg);
    }

    return req_set;
}

/*
 *  This function sends a DBCHANGE to dispatch to help keep any clients in sync with the paobjectid.
 */
int SendDBChange(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[] )
{
    string app = "MACS Server";
    string user = "";
    string message = "";
    string info = "";
    int classification = 7;
    int index = 1;

    int paoid;
    string objtype;

    if( argc < 3 )
    {
        CTILOG_ERROR(dout, "Usage:  SendDBChange paoid user - paoid is the paobject id to notify about. user is a string.");

        return TCL_OK;
    }

    paoid = atoi( argv[index++] );
    user = argv[index++];

    if( VanGoghConnection )
    {
        auto dbChange = std::make_unique<CtiDBChangeMsg>(paoid, ChangePAODb, "Schedule", "Script", ChangeTypeAdd);
        dbChange->setUser(user);
        dbChange->setSource(app);

        VanGoghConnection->WriteConnQue(std::move(dbChange), CALLSITE);
    }
    else
    {
        CTILOG_WARN(dout, "Could not send DBChange for pao ID " << paoid << " - VanGoghConnection is null");
    }

    return TCL_OK;
}

int CTICreateProcess(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[] )
{
    string cmd;
    for(int i = 1; i < argc; i++)
    {
        cmd += argv[i];
        cmd += " ";
    }

    DWORD exitcode = -1;
    STARTUPINFO si;
    PROCESS_INFORMATION pi;
    ZeroMemory(&si, sizeof(si));
    ZeroMemory(&pi, sizeof(pi));
    DWORD dwFlags = 0;

    string cmdExe(getenv("SystemRoot"));
    cmdExe += "\\system32\\cmd.exe";
    char szCmd[MAX_PATH];
    std::strcpy(szCmd,"/c ");
    std::strcat(szCmd,cmd.c_str());

    if (!CreateProcess(cmdExe.c_str(),(char *)szCmd,
                       NULL, NULL, FALSE,
                       dwFlags,
                       NULL, NULL, &si, &pi))
    {
        const DWORD error = GetLastError();
        CTILOG_ERROR(dout, "Failed to create job object: "<< error <<" / "<< Cti::getSystemErrorMessage(error));

        return TCL_ERROR;
    }
    CloseHandle(pi.hThread);
    if (WAIT_OBJECT_0== WaitForSingleObject(pi.hProcess, INFINITE)) {
       GetExitCodeProcess(pi.hProcess, (DWORD *)&exitcode);
    } else {
       CTILOG_ERROR(dout, "Wait Failed");
    }
    CloseHandle(pi.hProcess);
    return TCL_OK;
}

//Warning, this function clears the Queue. This is done so the function can be called anytime.
//It is in no way thread safe.
int WriteResultsToDatabase(std::deque<CtiTableMeterReadLog>& resultQueue, UINT requestLogId)
{
    int endVal = 0;

    if( requestLogId > 0 )
    {
        endVal = SynchronizedIdGen("DeviceReadLog", resultQueue.size());
    }

    if( endVal > 0 && requestLogId > 0 )
    {
        Cti::Database::DatabaseConnection   conn;

        if ( ! conn.isValid() )
        {
            CTILOG_ERROR(dout, "Invalid Connection to Database.");

            return ClientErrors::Abnormal;
        }

        try
        {
            std::deque<CtiTableMeterReadLog>::iterator result_itr = resultQueue.begin(),
                                                       result_end = resultQueue.end();

            const int total = resultQueue.size();
            int i = endVal - total + 1, count = 0;

            for(; result_itr != result_end && i <= endVal; ++result_itr, ++i )
            {
                if( !(++count % 1000) )
                {
                    CTILOG_INFO(dout, "Writing results to DB, "<< count <<" / "<< total <<" written");
                }

                result_itr->setLogID(i);
                result_itr->setRequestLogID(requestLogId);
                result_itr->Insert(conn);
            }

            if( count % 1000 )
            {
                CTILOG_INFO(dout, "Writing results to DB, "<< count <<" / "<< total <<" written");
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }

    resultQueue.clear();

    return ClientErrors::None;
}
