#include "precompiled.h"
#include <boost/regex.hpp>

#include <string>
#include <stdio.h>

#include <tcl.h>

#include "mccmd.h"

#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"
#include "database_transaction.h"
#include "connection.h"
#include "cparms.h"
#include "cmdparse.h"

#include "ctitokenizer.h"
#include "netports.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_requestcancel.h"
#include "msg_pdata.h"
#include "msg_queuedata.h"
#include "msg_signal.h"
#include "msg_dbchg.h"
#include "msg_notif_email.h"
#include "tbl_devicereadrequestlog.h"
#include "ctibase.h"
#include "collectable.h"
#include "pointtypes.h"
#include "numstr.h"
#include "ctistring.h"
#include "mgr_holiday.h"
#include "dsm2err.h"

#include "wpsc.h"
#include "xcel.h"
#include "decodetextcmdfile.h"
#include "utility.h"

#include <rw/collstr.h>
#include <rw/thr/thrutil.h>

using std::string;
using std::endl;
using std::vector;

using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

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

CtiConnection* PILConnection = 0;
CtiConnection* VanGoghConnection = 0;
CtiConnection* NotificationConnection = 0;

RWThread MessageThr;

// Seconds from 1970, updated every time we hear from porter
CtiTime gLastReturnMessageReceived(0UL);

// Used to distinguish unique requests/responses to/from pil
unsigned short gUserMessageID = 0;

static const TclCommandMap tclCamelCommandMap = boost::assign::map_list_of
    ("PILStartup", &Mccmd_Connect)
    ("PILShutdown", &Mccmd_Disconnect)
    ("MCCMDReset", &Mccmd_Reset)
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
    ("Pil", &Pil)
    ("Wait", &Wait)
    ("LogEvent", &LogEvent)
    ("SendDBChange", &SendDBChange)
    ("Dout", &Dout)
    ("SendNotification", &SendNotification)
    ("ImportCommandFile", &importCommandFile)
    ("createProcess", &CTICreateProcess);

static const TclCommandMap tclNonCamelCommandMap = boost::assign::map_list_of
    ("holiday", &isHoliday)
    ("exit", &Exit)
    ("mcu8100", &mcu8100)
    ("mcu9000eoi", &mcu9000eoi)
    ("mcu8100wepco", &mcu8100wepco)
    ("mcu8100service", &mcu8100service)
    ("mcu8100program", &mcu8100program)
    ("PMSI", &pmsi)
    ("pmsi", &pmsi)
    ("getDeviceID", &getDeviceID)
    ("getDeviceName", &getDeviceName)
    ("formatError", &formatError)
    ("getYukonBaseDir", &getYukonBaseDir);

void _MessageThrFunc()
{
    time_t last_cancellation_check = 0;

    try
    {
        while( 1 )
        {
            //Wake up every second to respect cancellation requests
            CtiMessage* in = PILConnection->ReadConnQue( 1000 );

            if( in != 0 )
            {
                boost::shared_ptr< CtiCountedPCPtrQueue<RWCollectable> > counted_ptr;

                unsigned int msgid = 0;

                if( in->isA() == MSG_PCRETURN )
                {
                    // update global message received timestamp, this is not used at the moment
                    gLastReturnMessageReceived = gLastReturnMessageReceived.now();
                    msgid =((CtiReturnMsg *)in)->UserMessageId();
                }
                else if( in->isA() == MSG_QUEUEDATA )
                {
                    msgid =((CtiQueueDataMsg *)in)->UserMessageId();
                }
                else if( in->isA() == MSG_REQUESTCANCEL )
                {
                    msgid =((CtiRequestCancelMsg *)in)->UserMessageId();
                }

                {
                    RWRecursiveLock<RWMutexLock>::LockGuard guard(_queue_mux);
                    if( InQueueStore.findValue( msgid, counted_ptr ) && (counted_ptr.use_count() > 0) )
                        counted_ptr->write(in);
                    else
                    {
                        {
                            CtiLockGuard< CtiLogger > logGuard(dout);
                            dout << CtiTime() << " [" << rwThreadId() <<
                            "] Received message for interpreter [" <<
                            GetThreadIDFromMsgID(msgid) << "]" << endl;
                            if( in->isA() == MSG_PCRETURN )
                            {
                                DumpReturnMessage(*(CtiReturnMsg*)in);
                            }
                            else if( in->isA() == MSG_REQUESTCANCEL )
                            {
                                in->dump();
                            }
                        }
                        delete in;
                    }
                }
            }

            //Clean out the VanGogh Connection
            RWCollectable* c;

            while( (c = VanGoghConnection->ReadConnQue( 0 )) != 0 )
            {
                // If it is a command message (are you there)
                // message then echo it right back
                if( c->isA() == MSG_COMMAND )
                {
                    VanGoghConnection->WriteConnQue( (CtiMessage*) c);
                }
                else
                {  // not interested, just delete it
                    delete c;
                }
            }

            if( ::time(0) != last_cancellation_check )
            {
                last_cancellation_check = ::time(0);

                rwRunnable().serviceCancellation();
            }
        }
    }
    catch( RWCancellation& )
    {
        //anything to do here?
        throw;
    }
    catch( ... )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << " **** EXCEPTION **** in porter in thread, this is bad."  << endl;
    }
}

void AppendToString(string& str, int argc, char* argv[])
{
    str += " ";

    for( int i = 0; i < argc; i++ )
    {
        str += argv[i];
        str += " ";
    }
}

void DumpReturnMessage(CtiReturnMsg& msg)
{
    string out;

    out += "deviceid: ";
    out += CtiNumStr(msg.DeviceId());
    out += " routeid: ";
    out += CtiNumStr(msg.RouteID());
    out += " status: ";
    out += CtiNumStr(msg.Status());
    out += " more: ";
    out += CtiNumStr(msg.ExpectMore());
    out += "\r\nresult: ";
    out += msg.ResultString();

    CtiMultiMsg_vec rw_set = msg.PointData();
    CtiMultiMsg_vec::iterator iter = rw_set.begin();
    CtiPointDataMsg* p_data;

    while( iter != rw_set.end() )
    {
        if((*iter)->isA() == MSG_POINTDATA)
        {
            p_data = (CtiPointDataMsg*)*iter;
            out += "\r\n  ";
            out += p_data->getString();
        }
        iter++;
    }

    WriteOutput((char*) out.c_str());
}

void DumpRequestMessage(CtiRequestMsg& msg)
{
    string out;

    out += "deviceid: ";
    out += CtiNumStr(msg.DeviceId());
    out += " routeid: ";
    out += CtiNumStr(msg.RouteId());
    out += " msgid: ";
    out += CtiNumStr(msg.UserMessageId());
    out += " priority: ";
    out += CtiNumStr(msg.getMessagePriority());
    out += "\r\n";

    out += msg.CommandString();

    WriteOutput(out.c_str());
}

void WriteOutput(const char* output)
{
    int thrId = rwThreadId();

    //Write the output to stdout
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " [" << thrId << "] " << output << endl;
    }

    boost::shared_ptr< CtiCountedPCPtrQueue<RWCollectable> >ptr;//TS

    OutQueueStore.findValue( thrId, ptr );

    if( ptr.use_count() > 0 )
    {
        RWCollectableString* msg = new RWCollectableString(output);
        ptr->write(msg);
    }
    return;
}

/* Connects to the PIL and VanGogh*/
int Mccmd_Connect(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if(PILConnection != 0 && VanGoghConnection != 0 && NotificationConnection != 0)
    {
      return 0;
    }
    //RWASSERT( PILConnection == 0 && VanGoghConnection == 0 );

    //Set up the defaults
    int pil_port;
    string pil_host;

    int dispatch_port;
    string dispatch_host;
    string fm_config_range;

    string notification_host;
    int notification_port;

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " MCCMD loading cparms:" << endl;
    }

    pil_host = gConfigParms.getValueAsString("PIL_MACHINE", "127.0.0.1");
    pil_port = gConfigParms.getValueAsInt("PIL_PORT", PORTERINTERFACENEXUS);

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << " Connecting to porter, host: " << pil_host << ", port: " << pil_port << endl;
    }

    dispatch_host = gConfigParms.getValueAsString("DISPATCH_MACHINE", "127.0.0.1");
    dispatch_port = gConfigParms.getValueAsInt("DISPATCH_PORT", VANGOGHNEXUS);

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << " Connecting to dispatch, host: " << dispatch_host << ", port: " << dispatch_port << endl;
    }

    notification_host = gConfigParms.getValueAsString("NOTIFICATION_MACHINE", "127.0.0.1");
    notification_port = gConfigParms.getValueAsInt("NOTIFICATION_PORT", NOTIFICATIONNEXUS);

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << " Connecting to notification server, host: " << notification_host << ", port: " << notification_port << endl;
    }

    gPagingConfigRouteID = gConfigParms.getValueAsInt("PAGING_CONFIG_ROUTE_ID", -1);
    gFMConfigRouteID = gConfigParms.getValueAsInt("FM_CONFIG_ROUTE_ID", -1);
    fm_config_range = gConfigParms.getValueAsString("FM_CONFIG_SERIAL_RANGE", "");

    if(gPagingConfigRouteID != -1)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << " Using route id: " << gPagingConfigRouteID << " as the paging route" << endl;
    }

    if(gFMConfigRouteID != -1)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << " Using route id: " << gFMConfigRouteID << " as the FM config route" << endl;
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
                    CtiLockGuard< CtiLogger > guard(dout);
                    dout << " Using " << lowi << "-" << highi << " as a fm serial number range" << endl;
                    gFMConfigSerialLow[index] = lowi;
                    gFMConfigSerialHigh[index] = highi;
                    index++;
                }
            }
        }

        gFMConfigSerialLow[index] = -1;
        gFMConfigSerialHigh[index] = -1;
    }

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " MCCMD done loading cparms" << endl;
    }

    PILConnection = new CtiConnection( pil_port, pil_host );
    PILConnection->setName("MCCMD to Pil");

    //Send a registration message
    CtiRegistrationMsg* reg = new CtiRegistrationMsg("MCCMD", 0, false );
    PILConnection->WriteConnQue( reg );

    VanGoghConnection = new CtiConnection( dispatch_port, dispatch_host );
    VanGoghConnection->setName("MCCMD to Dispatch");

    //Send a registration message
    CtiRegistrationMsg* reg2 = new CtiRegistrationMsg("MCCMD", 0, false );
    VanGoghConnection->WriteConnQue( reg2 );

    NotificationConnection = new CtiConnection( notification_port, notification_host.c_str() );
    NotificationConnection->setName("MCCMD to Notification");

    RWThreadFunction thr_func = rwMakeThreadFunction( _MessageThrFunc );
    thr_func.start();

    MessageThr = thr_func;

    return 0;
}

/* Disconnects from the PIL */
int Mccmd_Disconnect(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( MessageThr.isValid() )
        MessageThr.requestCancellation();

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " - " << "Shutting down connection to PIL" << endl;
    }

    PILConnection->ShutdownConnection();

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " - " << "Shutting down connection to VanGogh" << endl;
    }

    VanGoghConnection->ShutdownConnection();

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " - " << "Shutting down connection to the Notification Server" << endl;
    }

    NotificationConnection->ShutdownConnection();

    delete PILConnection;
    PILConnection = 0;

    delete VanGoghConnection;
    VanGoghConnection = 0;

    delete NotificationConnection;
    NotificationConnection = 0;

    return 0;

}

int Mccmd_Reset(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    return Mccmd_Init(interp);
}

int Mccmd_Init(Tcl_Interp* interp)
{
    /* Register MACS commands with the interpreter */

    for each(const TclCommandMap::value_type& itr in tclCamelCommandMap)
    {
        string commandName_lower(itr.first), commandName_upper(itr.first), commandName_camel(itr.first);

        CtiToLower(commandName_lower);
        CtiToUpper(commandName_upper);

        Tcl_CreateCommand( interp, const_cast<char*>(commandName_camel.c_str()), itr.second, NULL, NULL );
        Tcl_CreateCommand( interp, const_cast<char*>(commandName_lower.c_str()), itr.second, NULL, NULL );
        Tcl_CreateCommand( interp, const_cast<char*>(commandName_upper.c_str()), itr.second, NULL, NULL );
    }

    for each (const TclCommandMap::value_type& itr in tclNonCamelCommandMap)
    {
        Tcl_CreateCommand( interp, const_cast<char*>(itr.first.c_str()), itr.second, NULL, NULL );
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
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " " << MCCMD_DEBUG_LEVEL << ": 0x" << std::hex <<  gMccmdDebugLevel << std::dec << endl;
    }

    if( gMccmdDebugLevel & MCCMD_DEBUG_INIT )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "Using MCCMD init script: " << init_script << endl;
    }

    Tcl_EvalFile(interp, (char*) init_script.c_str() );

    /* declare that we are implementing the MCCmd package so that scripts that have
    "package require McCmd" can load McCmd automatically. */
    Tcl_PkgProvide(interp, "mccmd", "1.0");
    return TCL_OK;
}

/*--- Below are the MACS Tcl extensions  ---*/

int Exit(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
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

    // Shut down the global logger
    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    Tcl_Exit(value);
    /*NOTREACHED*/
    return TCL_OK;       /* Better not ever reach this! */

}

int Command(ClientData clientdata, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);

    return DoTwoWayRequest(interp, cmd);
}

int GetValue(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);

    return DoTwoWayRequest(interp, cmd);
}

int PutValue(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int GetStatus(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);
    return DoTwoWayRequest(interp, cmd);
}

int PutStatus(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int GetConfig(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);
    return DoTwoWayRequest(interp, cmd);
}

int PutConfig(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);

    return DoOneWayRequest(interp, cmd);
}

int Loop(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int Control(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int Scan(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int Pil(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    string cmd;
    string firsttok;
    AppendToString(cmd, argc, argv);

    Boost_char_tokenizer optoken(cmd);

    if( optoken.begin() != optoken.end() )
    {
        firsttok = *optoken.begin();
    }

    if(ciStringEqual(firsttok,"pil"))
    {   // Hack slash rip. CGP 11/07/2002  White rabbit entry point.
        cmd.replace((size_t)0, (size_t)4, "");    // erase the existence of "pil "
    }
    else
    {
        WriteOutput("Usage: pil command cmd_params");
        return TCL_ERROR;
    }

    return DoOneWayRequest(interp, cmd);
}

int mcu8100(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc != 2 )
    {
        WriteOutput("Usage: mcu8100 filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<RWCollectableString*> results;

    if( DecodeCFDATAFile( file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    std::vector<RWCollectableString*>::iterator iter = results.begin();

    for( ; iter != results.end() ; ++iter )
    {
        RWCollectableString* str = *iter;
        Tcl_Eval( interp, (char*) str->data());
    }
    delete_container(results);
    results.clear();
    return TCL_OK;
}

/*
    $VSERV=<address>,CONTRACT=(IN|OUT),TEMP=(IN|OUT)

    address := <service area>[,VN<serial #>]
*/
int mcu9000eoi(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{

    if( argc != 2 )
    {
        WriteOutput("Usage: mcu9000eoi filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<RWCollectableString*> results;

    if( DecodeEOIFile(file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    std::vector<RWCollectableString*>::iterator iter = results.begin();
    for( ; iter != results.end() ; ++iter )
    {
        RWCollectableString* str = *iter;
        Tcl_Eval( interp, (char*) str->data());
    }
    delete_container(results);
    results.clear();
    return TCL_OK;
}

int mcu8100wepco(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    int tcl_ret = TCL_OK;

    if( argc != 2 )
    {
        WriteOutput("Usage: mcu8100wepco filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<RWCollectableString*> results;

    if( DecodeWepcoFile( file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    std::vector<RWCollectableString*>::iterator iter = results.begin();
    for( ; iter != results.end() ; ++iter )
    {
        RWCollectableString* str = *iter;
        Tcl_Eval( interp, (char*) str->data());
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

int mcu8100service(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    int tcl_ret = TCL_OK;

    if( argc != 2 )
    {
        WriteOutput("Usage: mcu8100service filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<RWCollectableString*> results;

    if( DecodeWepcoFileService( file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    int num_sent = 0;

    std::vector<RWCollectableString*>::iterator iter = results.begin();
    for( ; iter != results.end() ; ++iter )
    {
        RWCollectableString* str = *iter;
        Tcl_Eval( interp, (char*) str->data());
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

int mcu8100program(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    int tcl_ret = TCL_OK;

    if( argc != 2 )
    {
        WriteOutput("Usage: mcu8100program filename");
        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<RWCollectableString*> results;

    if( DecodeWepcoFileConfig( file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    int num_sent = 0;
    std::vector<RWCollectableString*>::iterator iter = results.begin();
    for( ; iter != results.end() ; ++iter )
    {
        RWCollectableString* str = *iter;
        Tcl_Eval( interp, (char*) str->data());
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

int pmsi(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc != 2 )
    {
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " - Usage:  pmsi filename" << endl;
        }

        return TCL_ERROR;
    }

    string file = argv[1];
    std::vector<RWCollectableString*> results;

    if( DecodePMSIFile( file, &results) == false )
    {
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " - Error decoding file" << endl;
        }
        return TCL_ERROR;
    }

    std::vector<RWCollectableString*>::iterator iter = results.begin();

    for( ; iter != results.end() ; ++iter )
    {
        RWCollectableString* str = *iter;
        Tcl_Eval( interp, (char*) str->data());
    }
    delete_container(results);
    results.clear();
    return TCL_OK;
}

int importCommandFile (ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    int commandLimit=0;
    int commandsPerTime=0;
    int interval=1,decodeResult=NORMAL;
    CHAR newFileName[100];
    int retVal = TCL_OK;
    bool dsm2ImportFlag = false;
    int protocol = TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL;

    if( argc < 2 )
    {
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << " - Usage:  importcommandfile filename [/cmdsperexecution:#]" << endl;
            dout << "           optional parameters: " << endl;
            dout << "                 /cmdsperexecution:# - number of commands sent each execution (default to all)" << endl;
            dout << "                 /protocol:x - specify the protocol all commands should use (default versacom)" << endl;
            dout << "                      where x is versacom, expresscom, none (meaning don't specify)           " << endl;
            dout << "                 /dsm2 - import dsm2 vconfig.dat type commands (not valid with other options)" << endl;
            dout << "                 /appendextension:zzz - files with this extension will be appended to file" << endl;
            dout << "                      where zzz is an extension such as txt, dat, cfg, ect.." << endl;
            dout << "               NOTE: Commands are exported to file export\\sent-mm-dd-yyyy.txt" << endl;
        }

        retVal = TCL_ERROR;
    }
    else
    {

        string file = argv[1];
        string temp;

        std::vector<RWCollectableString*> results;
        ::sprintf (newFileName,"..\\export\\sent-%02d-%02d-%04d.txt",
                 CtiDate().month(),
                 CtiDate().dayOfMonth(),
                 CtiDate().year());

        if( gMccmdDebugLevel > 0 )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " - Will export commands from " << file << " to " << string (newFileName) <<endl;;
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
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << CtiTime() << " - Will export " << commandLimit << " commands from " << file << " per interval " <<endl;;
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
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << CtiTime() << " - Interval: " << interval << " minutes " <<endl;;
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
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << CtiTime() << " - Will export " << commandsPerTime << " commands from " << file << " every execution " <<endl;;
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
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << CtiTime() << " - Will import file as DSM2 vconfig.dat format " <<endl;;
                    }
                }
                if(str.find(string ("/protocol"))!=string::npos)
                {
                    if(str.find(string ("versacom"))!=string::npos)
                    {
                        protocol = TEXT_CMD_FILE_SPECIFY_VERSACOM;

                        if( gMccmdDebugLevel > 0 )
                        {
                            CtiLockGuard< CtiLogger > guard(dout);
                            dout << CtiTime() << " - Will export commands using versacom only " <<endl;
                        }
                    }
                    else if(str.find(string ("expresscom"))!=string::npos)
                    {
                        protocol = TEXT_CMD_FILE_SPECIFY_EXPRESSCOM;

                        if( gMccmdDebugLevel > 0 )
                        {
                            CtiLockGuard< CtiLogger > guard(dout);
                            dout << CtiTime() << " - Will export commands using expresscom only " <<endl;
                        }
                    }
                    else if(str.find(string ("none"))!=string::npos)
                    {
                        protocol = TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL;

                        if( gMccmdDebugLevel > 0 )
                        {
                            CtiLockGuard< CtiLogger > guard(dout);
                            dout << CtiTime() << " - Will export commands without specifying protocol " <<endl;
                        }
                    }
                    else
                    {
                        protocol = TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL;

                        if( gMccmdDebugLevel > 0 )
                        {
                            CtiLockGuard< CtiLogger > guard(dout);
                            dout << CtiTime() << " - Will export commands without specifing protocol " <<endl;
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
                {
                    CtiLockGuard< CtiLogger > guard(dout);
                    dout << CtiTime() << " Error importing file " << file << endl;
                }
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

                CtiSignalMsg* msg = new CtiSignalMsg( SYS_PID_MACS,
                                                      0,
                                                      logMsg,
                                                      infoMsg,
                                                      7,
                                                      SignalEvent,
                                                      "macs");

                // we wait this to keep running even if it fails
                VanGoghConnection->WriteConnQue(msg);
                decodeResult = NORMAL;
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

                CtiSignalMsg* msg = new CtiSignalMsg( SYS_PID_MACS,
                                                      0,
                                                      logMsg,
                                                      infoMsg,
                                                      7,
                                                      SignalEvent,
                                                      "macs");

                // we wait this to keep running even if it fails
                VanGoghConnection->WriteConnQue(msg);
            }
            else if(decodeResult == TEXT_CMD_FILE_UNABLE_TO_OPEN_FILE)
            {
                {
                    CtiLockGuard< CtiLogger > guard(dout);
                    dout << CtiTime() << " Export file " << file << " does not exist or is locked " << endl;
                }
            }
        }
        // send what we do have

        std::vector<RWCollectableString*>::iterator itr = results.begin();
        for( ; itr != results.end() ; ++itr )
        {
            RWCollectableString* str = *itr;
            Tcl_Eval( interp, (char*) str->data());
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
                {
                    CtiLockGuard< CtiLogger > guard(dout);
                    dout << CtiTime() << " - file " << string (toFileName) << " is locked "<< endl;
                }
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


int isHoliday(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
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
    else
    {
        // This will only get set if running inside MACS
        char* h_sched_id = Tcl_GetVar(interp, HolidayScheduleIDVariable, 0 );
        if( h_sched_id != NULL )
        {
            id = atoi(h_sched_id);
        }
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


int LogEvent(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[] )
{
    string user = "";
    string message = "";
    string info = "";
    int classification = 7;
    int index = 1;

    if( argc != 4 && argc != 6 )
    {
        WriteOutput( "Usage:  LogEvent [ class # ] user message info");
        WriteOutput( "class # is greater than 1");
        WriteOutput( "user, message, info are strings");
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


    CtiSignalMsg* msg = new CtiSignalMsg( SYS_PID_MACS,
                                          0,
                                          message,
                                          info,
                                          classification,
                                          SignalEvent,
                                          user);
    VanGoghConnection->WriteConnQue(msg);

    return TCL_OK;
}

int Dout(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc != 2 )
        return TCL_ERROR;

    WriteOutput( argv[1]);

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

int SendNotification(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc != 4 )
    {
        WriteOutput( "Usage:  SendNotification groupname subject text");
        return TCL_OK;
    }

    string name(argv[1]);

    long id = GetNotificationGroupID(name);

    if( id == -1 )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " Could not locate notification group named: " << name << endl;
        return TCL_ERROR;
    }

    CtiNotifEmailMsg* msg = new CtiNotifEmailMsg();
    msg->setNotifGroupId(id);
    msg->setSubject(argv[2]);
    msg->setBody(argv[3]);

    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << " Sending email notification to the notification server " << endl;
        dout << CtiTime() << " notification group id: " << msg->getNotifGroupId() << endl;
        dout << CtiTime() << " subject: " << msg->getSubject() << endl;
        dout << CtiTime() << " text: " << msg->getBody() << endl;
    }

    NotificationConnection->WriteConnQue(msg);

    return TCL_OK;
}


int Select(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc == 1 )
    {
        string selection = Tcl_GetVar(interp, SelectedVariable, 0 );
        //GetSelected(interp,selection);

        string out("current selection: ");
        out += selection;

        WriteOutput( (char*) selection.c_str() );
    }
    else
    {
        string cmd;
        AppendToString(cmd, argc, argv);

        Tcl_SetVar(interp, SelectedVariable, (char*) cmd.c_str(), 0 );
        //SetSelected(interp,cmd);
    }
    return TCL_OK;
}

int Wait(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc < 2 )
    {
        string usage("Usage:  Wait <seconds>");
        WriteOutput(usage.c_str());
    }

    long delay = atol(argv[1]);
    time_t start = ::time(NULL);

    while( start + delay > ::time(NULL) )
    {
        //Check for cancellation
        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            WriteOutput("interrupted");
            Tcl_SetResult( interp, "interrupted", TCL_VOLATILE );
            return TCL_ERROR;
        }

        rwSleep(1000);
    }

    return TCL_OK;
}

int getDeviceName(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
  if(argc < 2)
    {
      WriteOutput("Usage: getDeviceName <deviceid>");
      Tcl_SetResult(interp, "0", TCL_VOLATILE);
      return TCL_OK;
    }

  long id = atoi(argv[1]);
  string name;
  GetDeviceName(id,name);
  Tcl_Obj* tcl_name = Tcl_NewStringObj((const char*)name.c_str(), -1);
  Tcl_SetObjResult(interp, tcl_name);
  return TCL_OK;
}

int getDeviceID(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
  if(argc < 2)
    {
      WriteOutput("Usage: getDeviceID <devicename>");
      return TCL_OK;
    }

  long id = GetDeviceID(string(argv[1]));
  Tcl_Obj* tcl_id = Tcl_NewStringObj(CtiNumStr(id).toString().c_str(),-1);
  Tcl_SetObjResult(interp, tcl_id);
  return TCL_OK;
}

int formatError(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
  if(argc < 2)
    {
      WriteOutput("Usage: formatError <errorcode>");
      return TCL_OK;
    }

  int id = atoi(argv[1]);
  string err_str = FormatError(id);
  Tcl_Obj* tcl_str = Tcl_NewStringObj(err_str.c_str(),-1);
  Tcl_SetObjResult(interp, tcl_str);
  return TCL_OK;
}

int getYukonBaseDir(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
  string base_dir = gConfigParms.getYukonBase();
  Tcl_Obj* tcl_str = Tcl_NewStringObj(base_dir.c_str(), -1);
  Tcl_SetObjResult(interp, tcl_str);
  return TCL_OK;
}

int DoOneWayRequest(Tcl_Interp* interp, string& cmd_line)
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

int DoTwoWayRequest(Tcl_Interp* interp, string& cmd_line)
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

int WriteFailToFile(FILE* errFile, int status, string &dev_name)
{
    int retVal = 0;
    string tempStr = "  <ERROR>\n";

    if( errFile != NULL )
    {
        retVal = 1;
        fwrite(tempStr.c_str(), sizeof(char), tempStr.length(), errFile);

        tempStr = "    <STATUS>";
        tempStr.append(CtiNumStr(status));
        tempStr.append("</STATUS>\n");
        fwrite(tempStr.c_str(), sizeof(char), tempStr.length(), errFile);

        tempStr = "    <STATUSSTR>";
        tempStr.append(FormatError(status));
        tempStr.append("</STATUSSTR>\n");
        fwrite(tempStr.c_str(), sizeof(char), tempStr.length(), errFile);

        tempStr = "    <DEVNAME>";
        tempStr.append(dev_name);
        tempStr.append("</DEVNAME>\n");
        fwrite(tempStr.c_str(), sizeof(char), tempStr.length(), errFile);

        tempStr = "  </ERROR>\n";
        fwrite(tempStr.c_str(), sizeof(char), tempStr.length(), errFile);
    }
    return retVal;
}

static bool isBreakStatus( int status )
{
    switch( status )
    {
        case DEVICEINHIBITED:
        case NoMethod:
        case ErrorInvalidSSPEC:
        case ErrorVerifySSPEC:
        case IDNF: // IDNF means not in the database
        case ErrorCommandAlreadyInProgress:
            return false;

        default:
            return true;
    }
}

static int DoRequest(Tcl_Interp* interp, string& cmd_line, long timeout, bool two_way)
{
    bool interrupted = false;
    bool timed_out = false;
    UINT jobId = 0;
    UINT const PORT_COUNT_REQUEST_SECONDS = 5*60;
    long requestLogId = 0;
    unsigned long porterCount = 0;
    CtiTime lastReturnMessageReceived;

    RWSet req_set;

    //Create a CountedPCPtrQueue and place it into the InQueueStore
    //Be sure to remove it before exiting
    unsigned int msgid = GenMsgID();

    char* jobIdStr = Tcl_GetVar(interp, "DeviceReadLogId", 0 );
    if( jobIdStr != NULL )
    {
        jobId = atoi(jobIdStr);
    }

    boost::shared_ptr< CtiCountedPCPtrQueue<RWCollectable> > queue_ptr( new  CtiCountedPCPtrQueue<RWCollectable>() );
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
                std::string errorMsg( "**** ERROR **** Invalid Connection to Database.  __FILE__ (__LINE__)" );
                WriteOutput( errorMsg.c_str() );

                return TCL_ERROR;
            }
        }
    }

    BuildRequestSet(interp, cmd_line,req_set);

    // Nothing to do get outta here
    if( req_set.entries() == 0 )
        return TCL_OK;

    //build up a multi and write out all of the requests
    CtiMultiMsg* multi_req = new CtiMultiMsg();

    RWSetIterator iter(req_set);
    for( ; iter(); )
    {
        CtiRequestMsg* req = (CtiRequestMsg*) iter.key();
        req->setUserMessageId(msgid);
        req->setGroupMessageId(msgid);

        if( gMccmdDebugLevel & MCCMD_DEBUG_PILREQUEST )
            DumpRequestMessage(*req);
        else
            WriteOutput( (char*) req->CommandString().c_str() );

        multi_req->getData().push_back(req);
    }

    if( timeout == 0 ) // We dont care about responses, dont set up the queue, send the message and exit.
    {
        PILConnection->WriteConnQue(multi_req);
        return TCL_OK;
    }
    else // We do care about responses, set up the queue, send the message and continue.
    {
        {
            RWRecursiveLock<RWMutexLock>::LockGuard guard(_queue_mux);
            InQueueStore.insertKeyAndValue(msgid, queue_ptr);
            // Note from this point on, no early return is allowed as this queue must be un-initialized.
        }

        PILConnection->WriteConnQue(multi_req);
    }

    CtiTime start;
    CtiTime lastPorterCountTime;
    //We will poll this in 60 seconds
    lastPorterCountTime = lastPorterCountTime - PORT_COUNT_REQUEST_SECONDS + 60;

    // Some structures to sort the responses
    PILReturnMap device_map;
    PILReturnMap good_map;
    PILReturnMap bad_map;
    std::vector<string> bad_names;
    std::deque<CtiTableMeterReadLog> resultQueue;

    RWCollectable* msg = NULL;
    int queueDataZeroCount = 0;
    bool status;

    do
    {
        status = queue_ptr->read(msg,100);

       if( status != false && msg != NULL )
      {
            if( msg->isA() == MSG_PCRETURN )
            {
                CtiReturnMsg* ret_msg = (CtiReturnMsg*) msg;
                DumpReturnMessage(*ret_msg);
                bool allowExitOnError = isBreakStatus(ret_msg->Status());
                HandleReturnMessage(ret_msg, good_map, bad_map, device_map, bad_names, resultQueue);
                lastReturnMessageReceived = lastReturnMessageReceived.now();

                // have we received everything expected?
                if( device_map.size() == 0 && allowExitOnError )
                    break;
            }
            else if( msg->isA() == MSG_QUEUEDATA )
            {
                CtiQueueDataMsg *queueMessage = (CtiQueueDataMsg *)msg;
                if( queueMessage->getRequestId() == msgid )
                {
                    porterCount = queueMessage->getRequestIdCount();
                    string output("Queue Data Received, there are ");
                    output += CtiNumStr(porterCount);
                    output += " objects for this script in porter.";
                    WriteOutput(output.c_str());

                    if( porterCount == 0 )
                    {
                        output = "Porter has reported a count of 0 messages for this script. ";
                        output += "MACS reports " + CtiNumStr(device_map.size());
                        output += " devices left to respond. If the script does not finish very soon this";
                        output += " should be considered a problem.";
                        WriteOutput(output.c_str());
                        queueDataZeroCount++;

                        if( queueDataZeroCount > 1 )
                        {
                            // At some point we could break; here if we are confident about this.
                            output = "Queue data reports a need to exit, current command is exiting!";
                            WriteOutput(output.c_str());
                            break;
                        }
                    }
                }
            }
            else
            {
                delete msg;
                string err("Received unknown message __LINE__, __FILE__");
                WriteOutput(err.c_str());
            }

            msg = NULL;
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
            string info = "The following command timed out: \r\n";
            info += cmd_line;
            info += "\r\n command was originally submitted at: ";
            info += CtiTime(start).asString();
            info += "\r\n nothing was received from porter in the last ";
            info += CtiNumStr( (unsigned long) ( now.seconds() - lastReturnMessageReceived.seconds() ) );
            info += " seconds.";

            WriteOutput(info.c_str());

            break;
        }

        if( now > lastPorterCountTime + PORT_COUNT_REQUEST_SECONDS )
        {
            lastPorterCountTime = now;
            PILConnection->WriteConnQue(CTIDBG_new CtiRequestMsg(0, "system message request count", msgid, msgid));
        }
    } while(true);

    if( interrupted )
    {
        string info = "Command exiting due to interrupt!";

        WriteOutput(info.c_str());
    }

    //I left the next line because i'm not sure everything is getting cleanedup
    //uncommenting it will cause a bomb however since all the messages are now stored
    //and deleted below
    //delete msg;

    // We now always send the cancel message.
    if( two_way && timeout > 0 && !gDoNotSendCancel)
    {
        PILConnection->WriteConnQue(CTIDBG_new CtiRequestMsg(0, "system message request cancel", msgid, msgid));
    }

    // set up good and bad tcl lists
    Tcl_Obj* good_list = Tcl_NewListObj(0,NULL);
    Tcl_Obj* bad_list = Tcl_NewListObj(0,NULL);
    Tcl_Obj* status_list = Tcl_NewListObj(0,NULL);

    Tcl_SetVar2Ex(interp, GoodListVariable, NULL, good_list, 0 );
    Tcl_SetVar2Ex(interp, BadListVariable, NULL, bad_list, 0 );
    Tcl_SetVar2Ex(interp, BadStatusVariable, NULL, status_list, 0);

    PILReturnMap::iterator m_iter;
    string next_line;
    string dev_name;

    if( !good_map.empty() )
    {
        int count = 0;

        for( m_iter = good_map.begin();
             m_iter != good_map.end();
             m_iter++ )
        {
            if( !(++count % 10000) )
            {
                string current = "Writing good list, " + CtiNumStr(count) + " / " + CtiNumStr(good_map.size()) + " written";
                WriteOutput(current.c_str());
            }

            GetDeviceName(m_iter->first,dev_name);
            next_line = dev_name;
            if( !gUseOldStyleMissed )
            {
                next_line += ", " + CtiNumStr(m_iter->first);
            }
            Tcl_ListObjAppendElement(interp, good_list, Tcl_NewStringObj(next_line.c_str(), -1));
        }

        if( count % 10000 )
        {
            string current = "Writing good list, " + CtiNumStr(count) + " / " + CtiNumStr(good_map.size()) + " written";
            WriteOutput(current.c_str());
        }
    }

    if( !bad_map.empty() )
    {
        int count = 0;

        for( m_iter = bad_map.begin();
             m_iter != bad_map.end();
             m_iter++ )
        {
            if( !(++count % 10000) )
            {
                string current = "Writing bad list, " + CtiNumStr(count) + " / " + CtiNumStr(bad_map.size()) + " written";
                WriteOutput(current.c_str());
            }

            if( m_iter->second.status == IDNF )
            {
                dev_name = m_iter->second.deviceName;
            }
            else
            {
                GetDeviceName(m_iter->first,dev_name);
            }

            next_line = dev_name;
            if( !gUseOldStyleMissed )
            {
                next_line += ", " + CtiNumStr(m_iter->first);
            }

            Tcl_ListObjAppendElement(interp, bad_list, Tcl_NewStringObj(next_line.c_str(), -1));
            Tcl_ListObjAppendElement(interp, status_list,
            Tcl_NewIntObj(m_iter->second.status));
        }

        if( count % 10000 )
        {
            string current = "Writing bad list, " + CtiNumStr(count) + " / " + CtiNumStr(bad_map.size()) + " written";
            WriteOutput(current.c_str());
        }
    }

    for each( const string &str in bad_names )
    {
        Tcl_ListObjAppendElement(interp, bad_list, Tcl_NewStringObj(str.c_str(), -1));
    }

    // any device id's left in this set must have timed out
    if( device_map.size() > 0 )
    {
        int count = 0;

        for( m_iter = device_map.begin();
             m_iter != device_map.end();
             m_iter++ )
        {
            if( !(++count % 10000) )
            {
                string current = "Writing orphans, " + CtiNumStr(count) + " / " + CtiNumStr(device_map.size()) + " written";
                WriteOutput(current.c_str());
            }

            CtiTableMeterReadLog result(0, m_iter->first, 0, ErrorMACSTimeout, m_iter->second.time);
            resultQueue.push_back(result);
            GetDeviceName(m_iter->first,dev_name);
            next_line = dev_name;
            if( !gUseOldStyleMissed )
            {
                next_line += ", " + CtiNumStr(m_iter->first);
            }

            Tcl_ListObjAppendElement(interp, bad_list, Tcl_NewStringObj(next_line.c_str(), -1));
            Tcl_ListObjAppendElement(interp, status_list,
            Tcl_NewIntObj(m_iter->second.status));
        }

        if( count % 10000 )
        {
            string current = "Writing orphans, " + CtiNumStr(count) + " / " + CtiNumStr(device_map.size()) + " written";
            WriteOutput(current.c_str());
        }
    }

    //Remove the queue from the InQueueStore
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard(_queue_mux);
        InQueueStore.remove(msgid);
    }

    //Lets write this to the screen before the database locks us up.
    while( queue_ptr->read(msg,10) )
    {
        if( msg->isA() == MSG_PCRETURN )
        {
            CtiReturnMsg* ret_msg = (CtiReturnMsg*) msg;
            DumpReturnMessage(*ret_msg);
        }
        else if( msg->isA() == MSG_QUEUEDATA || msg->isA() == MSG_REQUESTCANCEL )
        {
            ((CtiMessage *)msg)->dump();
        }
        delete msg;
    }

    WriteOutput("Database Write Starting");

    WriteResultsToDatabase(resultQueue, requestLogId);

    if( timeout != 0 && jobId > 0 ) // don't bother if we don't want responses
    {
        deviceReadLog.setStopTime(CtiTime::now());
        deviceReadLog.Update();
    }

    WriteOutput("Database Write Complete");

    return (interrupted ?
                TCL_ERROR : TCL_OK);
}

/***
    Handles the sorting of an incoming message form PIL
    This appears to be Deprecated and unused.
****/
void HandleMessage(RWCollectable* msg,
           PILReturnMap& good_map,
           PILReturnMap& bad_map,
           PILReturnMap& device_map,
           std::vector<string>& bad_names,
           std::deque<CtiTableMeterReadLog>& resultQueue )
{
    if( msg->isA() == MSG_PCRETURN )
    {
        HandleReturnMessage( (CtiReturnMsg*) msg, good_map, bad_map, device_map, bad_names, resultQueue);
    }
    if( msg->isA() == MSG_MULTI )
    {
        CtiMultiMsg* multi_msg = (CtiMultiMsg*) msg;

        for( unsigned i = 0; i < multi_msg->getData( ).size( ); i++ )
        {
            HandleMessage( multi_msg->getData()[i], good_map, bad_map, device_map, bad_names, resultQueue);
        }
    }
    else
    {
        string warn("received an unkown message with class id: ");
        warn += CtiNumStr(msg->isA());
        WriteOutput(warn.c_str());
    }
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

    if( msg->Status() == IDNF )
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
        string warn("received a message for a device already in the good list, id: ");
        warn += CtiNumStr(dev_id);
        WriteOutput(warn.c_str());
    }
    else
    {
        MACS_Return_Data data;
        data.time = msg->getMessageTime();
        data.status = msg->Status();

        if( msg->Status() == DEVICEINHIBITED )
        {
            //Ignore it!
        }
        else if( msg->ExpectMore() )
        {
            device_map.insert(PILReturnMap::value_type(dev_id, data));

            if( msg->Status() != NORMAL )
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
                    string warn("moved device from bad list to good list, id: ");
                    warn += CtiNumStr(dev_id);
                    WriteOutput(warn.c_str());
                    bad_map.erase(pos);
                }

                pos = device_map.find(dev_id);
                if(pos != device_map.end())
                {
                    device_map.erase(pos);
                }

                if( !good_map.insert(PILReturnMap::value_type(dev_id,data)).second )
                {
                    string warn("device already in good list, id: ");
                    warn += CtiNumStr(dev_id);
                    WriteOutput(warn.c_str());
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
                    string warn("device already in bad list, id: ");
                    warn += CtiNumStr(dev_id);
                    WriteOutput(warn.c_str());
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
    RWRecursiveLock<RWMutexLock>::LockGuard guard(_queue_mux);
    return((unsigned int) rwThreadId() << 16) +
    (unsigned int) gUserMessageID++;
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
    try
    {
        {
            string sql = "SELECT NotificationGroupID FROM NotificationGroup WHERE GroupName='" + name + "'";
            DatabaseConnection conn;
            DatabaseReader rdr(conn, sql);
            rdr.execute();

            //Assume there is only one?
            if( rdr() )
            {
                long id;
                rdr >> id;
                return id;
            }
            else
                return -1;
        }

    }
    catch( RWExternalErr err )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " Error retrieving notification group id." << err.why() << endl;
        return -1;
    }
}

static void GetDeviceName(long deviceID, string& name)
{
    try
    {
        char devStr[12];
        ::sprintf(devStr, "%ld", deviceID);

        {
            string sql = "SELECT PAOName FROM YukonPAObject WHERE YukonPAObject.PAObjectID=";
            sql += devStr;
            DatabaseConnection conn;
            DatabaseReader rdr(conn, sql);
            rdr.execute();

            if( rdr() )
            {
                rdr >> name;
            }
            else
            {
                WriteOutput("Unable to retrive device name __LINE__ __FILE__");
            }
        }
    }
    catch( RWExternalErr err )
    {
        WriteOutput("Error retrieve device name __LINE__ __FILE__");
    }
}

static long GetDeviceID(const string& name)
{
  long id = 0;
  try
    {
      {
    string sql = "SELECT PAOBJECTID FROM YukonPAObject WHERE YukonPAObject.PAOName='";
    sql += name;
    sql += "'";
    DatabaseConnection conn;
    DatabaseReader rdr(conn, sql);
    rdr.execute();
    if(rdr())
      {
        rdr >> id;
      }
    else
      {
        WriteOutput("Unable to retrieve device id __LINE__ __FILE__");
      }
      }
    }
  catch(RWExternalErr err)
    {
      WriteOutput("Unsable to retrieve device id __LINE__ __FILE__");
    }
  return id;
}

void BuildRequestSet(Tcl_Interp* interp, string& cmd_line_b, RWSet& req_set)
{
    CtiString cmd_line(cmd_line_b);
    unsigned int * ptr;
    if( cmd_line.find("select")==string::npos )
    {
        // cmd_line doesn't specify a select string so lets append
        // this interpreters current select string
        string selection = Tcl_GetVar(interp, SelectedVariable, 0);
        cmd_line += " ";
        cmd_line += selection;


    }

    size_t index;
    size_t* end_index = new size_t(0);

    int priority = 7;
    char* pStr = Tcl_GetVar(interp, PILRequestPriorityVariable, TCL_GLOBAL_ONLY);
    if( pStr != NULL )
    {
        priority = atoi(pStr);
        if( priority < 1 || priority > 15 )
        {
            priority = 7;
            WriteOutput("MessagePriority is invalid, defaulting to 7");
        }
    }

    boost::regex select_list_regex = boost::regex(".*select[ ]+list[ ]+");
    boost::regex select_id_regex = boost::regex(".*select[ ]+[^ ]+[ ]+id");
    boost::regex select_deviceid_regex = boost::regex(".*select deviceid");
    boost::regex select_regex = boost::regex(".*select[ ]+[^ ]+[ ]+");

    if( cmd_line.index(select_list_regex, end_index) != string::npos )
    {
        int list_len;
        Tcl_Obj* sel_str = Tcl_NewStringObj( cmd_line.c_str() + *end_index, -1 );
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
                req_set.insert(msg);
            }
        }

        // clean up
        Tcl_DecrRefCount(sel_str);
    }
    else //dont add quotes if it is an id
    if( cmd_line.index(select_id_regex, end_index)       != string::npos ||
        cmd_line.index(select_deviceid_regex, end_index) != string::npos )
    {
        CtiRequestMsg *msg = new CtiRequestMsg();
        msg->setDeviceId(0);
        msg->setCommandString(cmd_line);
        msg->setMessagePriority(priority);
        req_set.insert(msg);
    }
    else
    if( cmd_line.index(select_regex, end_index) != string::npos )
    {

        //PIL likes to see ' around any device, group, etc
        cmd_line.insert(*end_index, "'");
        trim(cmd_line);
        cmd_line.append("'");

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
        req_set.insert(msg);
    }
    delete end_index;
}

/*
 *  This function sends a DBCHANGE to dispatch to help keep any clients in sync with the paobjectid.
 */
int SendDBChange(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[] )
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
        WriteOutput( "Usage:  SendDBChange paoid user");
        WriteOutput( "paoid is the paobject id to notify about.");
        WriteOutput( "user is a string");
        return TCL_OK;
    }

    paoid = atoi( argv[index++] );
    user = argv[index++];

    CtiDBChangeMsg *dbChange = new CtiDBChangeMsg(paoid, ChangePAODb, "Schedule", "Script", ChangeTypeAdd);
    dbChange->setUser(user);
    dbChange->setSource(app);

    VanGoghConnection->WriteConnQue(dbChange);

    return TCL_OK;
}

int CTICreateProcess(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[] )
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
        LPVOID lpMsgBuf;
        FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM |
                      FORMAT_MESSAGE_IGNORE_INSERTS,NULL,GetLastError(),MAKELANGID(LANG_NEUTRAL,
                                                                                   SUBLANG_DEFAULT),(LPTSTR)&lpMsgBuf,0,NULL);
        printf("Error creating job object: %s\n",(char*)lpMsgBuf);
        LocalFree(lpMsgBuf);
        return TCL_ERROR;
    }
    CloseHandle(pi.hThread);
    if (WAIT_OBJECT_0== WaitForSingleObject(pi.hProcess, INFINITE)) {
       GetExitCodeProcess(pi.hProcess, (DWORD *)&exitcode);
    } else {
       printf("error with wait\n");
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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return NOTNORMAL;
        }

        {
            Cti::Database::DatabaseTransaction trans(conn);

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
                        string current = "Writing results to DB, " + CtiNumStr(count) + " / " + CtiNumStr(total) + " written";
                        WriteOutput(current.c_str());
                    }

                    result_itr->setLogID(i);
                    result_itr->setRequestLogID(requestLogId);
                    result_itr->Insert(conn);
                }

                if( count % 1000 )
                {
                    string current = "Writing results to DB, " + CtiNumStr(count) + " / " + CtiNumStr(total) + " written";
                    WriteOutput(current.c_str());
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }

    resultQueue.clear();

    return NORMAL;
}
