#include <stdio.h>
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mccmd
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MCCMD/mccmd.cpp-arc  $
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2002/09/24 18:01:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <tcl.h>

#include "mccmd.h"

#include "dbaccess.h"
#include "connection.h"
#include "cparms.h"
#include "configparms.h"
#include "netports.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "msg_email.h"
#include "ctibase.h"
#include "collectable.h"
#include "pointtypes.h"
#include "numstr.h"
#include "mgr_holiday.h"

#include "wpsc.h"
#include "xcel.h"
#include "decodetextcmdfile.h"

#include <rw/collstr.h>
#include <rw/thr/thrutil.h>

#include <rw\re.h>
#include <rw/ctoken.h>

unsigned gMccmdDebugLevel = 0x00000000;

const RWCRExpr   re_num("[0-9]+");
const RWCRExpr   re_timeout("timeout[= ]+[0-9]+");
const RWCRExpr   re_select("select[ ]+[^ ]+[ ]+");
const RWCRExpr   re_select_list("select[ ]+list[ ]+");

char* SelectedVariable = "Selected";
char* GoodListVariable = "SuccessList";
char* BadListVariable  = "MissedList";
char* ScheduleIDVariable = "ScheduleID";
char* HolidayScheduleIDVariable = "HolidayScheduleID";
char* PILRequestPriorityVariable = "MessagePriority";

CtiConnection* PILConnection = 0;
CtiConnection* VanGoghConnection = 0;
RWThread MessageThr;

// Used to distinguish unique requests/responses to/from pil
unsigned char gUserMessageID = 0;

void _MessageThrFunc()
{
    try
    {
        while( 1 )
        {
            //Wake up every second to respect cancellation requests
            CtiReturnMsg* in = (CtiReturnMsg*) PILConnection->ReadConnQue( 1000 );

            if( in != 0 )
            {
                RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > counted_ptr;

                unsigned int msgid = in->UserMessageId();

                {
                    RWRecursiveLock<RWMutexLock>::LockGuard guard(_queue_mux);
                    if( InQueueStore.findValue( in->UserMessageId(), counted_ptr ) && counted_ptr.isValid() )
                        counted_ptr->write(in);
                    else
                    {
                        {
                            CtiLockGuard< CtiLogger > guard(dout);
                            dout << RWTime() << " [" << rwThreadId() <<
                            "] Received message for interpreter [" <<
                            GetThreadIDFromMsgID(msgid) << "]" << endl;
                            DumpReturnMessage(*in);
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

            rwRunnable().serviceCancellation();
        }
    }
    catch( RWCancellation& )
    {
        //anything to do here?
        throw;
    }
}

void AppendToString(RWCString& str, int argc, char* argv[])
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
    RWCString out;

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

    RWOrdered rw_set = msg.PointData();
    RWOrderedIterator iter(rw_set);
    CtiPointDataMsg* p_data;

    while( (p_data = (CtiPointDataMsg*) iter()) != NULL )
    {
        out += "\r\n  ";
        out += p_data->getString();
    }

    WriteOutput((char*) out.data());
}

void DumpRequestMessage(CtiRequestMsg& msg)
{
    RWCString out;

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

    WriteOutput(out.data());
}

void WriteOutput(const char* output)
{
    int thrId = rwThreadId();

    //Write the output to stdout
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " [" << thrId << "] " << output << endl;
    }

    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> >ptr;

    OutQueueStore.findValue( thrId, ptr );

    if( ptr.isValid() )
    {
        RWCollectableString* msg = new RWCollectableString(output);
        ptr->write(msg);
    }
    return;
}

/* Connects to the PIL and VanGogh*/
int Mccmd_Connect()
{
    RWASSERT( PILConnection == 0 && VanGoghConnection == 0 );

    //Set up the defaults
    INT pil_port = PORTERINTERFACENEXUS;
    RWCString pil_host = "127.0.0.1";

    INT vangogh_port = VANGOGHNEXUS;
    RWCString vangogh_host = "127.0.0.1";

    HINSTANCE hLib = LoadLibrary("cparms.dll");

    if(hLib)
    {
        char temp[80];

        CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

        BOOL trouble = FALSE;

        //What are the keys?
        if( (*fpGetAsString)("PIL_MACHINE", temp, 64) )
        {
            dout << RWTime()  << " - Using " << temp << " as the pil host" << endl;
            pil_host = temp;
        }
        else
            trouble = TRUE;

        if( (*fpGetAsString)("PIL_PORT", temp, 64) )
        {
            dout << RWTime()  << " - Using " << temp << " as the pil port" << endl;
            pil_port = atoi(temp);
        }
        else
            trouble = TRUE;


        if( (*fpGetAsString)("DISPATCH_MACHINE", temp, 64) )
        {
            dout << RWTime()  << " - Using " << temp << " as the vangogh host" << endl;
            vangogh_host = temp;
        }
        else
            trouble = TRUE;

        if( (*fpGetAsString)("DISPATCH_PORT", temp, 64) )
        {
            dout << RWTime()  << " - Using " << temp << " as the vangogh port" << endl;
            vangogh_port = atoi(temp);
        }
        else
            trouble = TRUE;

        /* The next few are optional cparms for customs */
        if( (*fpGetAsString)("PAGING_CONFIG_ROUTE_ID", temp, 64) )
        {
            gPagingConfigRouteID = atoi(temp);
            dout << RWTime()  << " PAGING_CONFIG_ROUTE_ID=" << gPagingConfigRouteID << endl;
        }

        if( (*fpGetAsString)("FM_CONFIG_ROUTE_ID", temp, 64) )
        {
            gFMConfigRouteID = atoi(temp);
            dout << RWTime()  << " FM_CONFIG_ROUTE_ID=" << gFMConfigRouteID << endl;
        }

        // init these in case none are found
        gFMConfigSerialLow[0] = -1;
        gFMConfigSerialHigh[0] = -1;

        if( (*fpGetAsString)("FM_CONFIG_SERIAL_RANGE", temp, 64) )
        {
            RWCTokenizer nextRange(temp);
            RWCString range;

            int index = 0;
            while( !(range = nextRange(",\r\n")).isNull() )
            {
                RWCTokenizer nextSerial(range);
                RWCString low;
                RWCString high;

                low = nextSerial("-");
                high = nextSerial(" \r\n");
                high = high.strip(RWCString::leading, '-');

                if( !low.isNull() && !high.isNull() )
                {
                    int lowi = atoi(low.data());
                    int highi = atoi(high.data());

                    if( lowi != 0 && highi != 0 )
                    {
                        dout << RWTime() << " FM_CONFIG_SERIAL_RANGE " << lowi << "-" << highi << endl;
                        gFMConfigSerialLow[index] = lowi;
                        gFMConfigSerialHigh[index] = highi;
                        index++;
                    }
                }
            }

            gFMConfigSerialLow[index] = -1;
            gFMConfigSerialHigh[index] = -1;
        }

        if( trouble )
        {
            {
                CtiLockGuard< CtiLogger > guard(dout);
                dout << RWTime() << " - Unable to find one or more mccmd config values in the configuration file." << endl;
            }

        }

        FreeLibrary(hLib);
    }
    else
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "Unable to load cparms dll " << endl;
    }

    PILConnection = new CtiConnection( pil_port, pil_host );

    //Send a registration message
    CtiRegistrationMsg* reg = new CtiRegistrationMsg("MCCMD", 0, false );
    PILConnection->WriteConnQue( reg );

    VanGoghConnection = new CtiConnection( vangogh_port, vangogh_host );

    //Send a registration message
    CtiRegistrationMsg* reg2 = new CtiRegistrationMsg("MCCMD", 0, false );
    VanGoghConnection->WriteConnQue( reg2 );

    RWThreadFunction thr_func = rwMakeThreadFunction( _MessageThrFunc );
    thr_func.start();

    MessageThr = thr_func;

    return 0;
}

/* Disconnects from the PIL */
int Mccmd_Disconnect()
{
    if( MessageThr.isValid() )
        MessageThr.requestCancellation();

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " - " << "Shutting down connection to PIL" << endl;
    }

    PILConnection->ShutdownConnection();

    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " - " << "Shutting down connection to VanGogh" << endl;
    }

    VanGoghConnection->ShutdownConnection();

    delete PILConnection;
    PILConnection = 0;

    delete VanGoghConnection;
    VanGoghConnection = 0;

    return 0;

}

int Mccmd_Init(Tcl_Interp* interp)
{
    /* Register MACS commands with the interpreter */
    Tcl_CreateCommand( interp, "GetValue", GetValue, NULL, NULL );
    Tcl_CreateCommand( interp, "getvalue", GetValue, NULL, NULL );
    Tcl_CreateCommand( interp, "GETVALUE", GetValue, NULL, NULL );

    Tcl_CreateCommand( interp, "PutValue", PutValue, NULL, NULL );
    Tcl_CreateCommand( interp, "putvalue", PutValue, NULL, NULL );
    Tcl_CreateCommand( interp, "PUTVALUE", PutValue, NULL, NULL );

    Tcl_CreateCommand( interp, "GetStatus", GetStatus, NULL, NULL );
    Tcl_CreateCommand( interp, "getstatus", GetStatus, NULL, NULL );
    Tcl_CreateCommand( interp, "GETSTATUS", GetStatus, NULL, NULL );

    Tcl_CreateCommand( interp, "PutStatus", PutStatus, NULL, NULL );
    Tcl_CreateCommand( interp, "putstatus", PutStatus, NULL, NULL );
    Tcl_CreateCommand( interp, "PUTSTATUS", PutStatus, NULL, NULL );

    Tcl_CreateCommand( interp, "GetConfig", GetConfig, NULL, NULL );
    Tcl_CreateCommand( interp, "getconfig", GetConfig, NULL, NULL );
    Tcl_CreateCommand( interp, "GETCONFIG", GetConfig, NULL, NULL );

    Tcl_CreateCommand( interp, "PutConfig", PutConfig, NULL, NULL );
    Tcl_CreateCommand( interp, "putconfig", PutConfig, NULL, NULL );
    Tcl_CreateCommand( interp, "PUTCONFIG", PutConfig, NULL, NULL );

    Tcl_CreateCommand( interp, "Loop", Loop, NULL, NULL );
    Tcl_CreateCommand( interp, "loop", Loop, NULL, NULL );
    Tcl_CreateCommand( interp, "LOOP", Loop, NULL, NULL );

    Tcl_CreateCommand( interp, "Control", Control, NULL, NULL );
    Tcl_CreateCommand( interp, "control", Control, NULL, NULL );
    Tcl_CreateCommand( interp, "CONTROL", Control, NULL, NULL );

    Tcl_CreateCommand( interp, "Wait", Wait, NULL, NULL );
    Tcl_CreateCommand( interp, "wait", Wait, NULL, NULL );
    Tcl_CreateCommand( interp, "WAIT", Wait, NULL, NULL );

    Tcl_CreateCommand( interp, "LogEvent", LogEvent, NULL, NULL );
    Tcl_CreateCommand( interp, "logevent", LogEvent, NULL, NULL );
    Tcl_CreateCommand( interp, "LOGEVENT", LogEvent, NULL, NULL );

    Tcl_CreateCommand( interp, "Dout", Dout, NULL, NULL );
    Tcl_CreateCommand( interp, "dout", Dout, NULL, NULL );
    Tcl_CreateCommand( interp, "DOUT", Dout, NULL, NULL );

    Tcl_CreateCommand( interp, "SendNotification", SendNotification, NULL, NULL );
    Tcl_CreateCommand( interp, "sendnotification", SendNotification, NULL, NULL );
    Tcl_CreateCommand( interp, "SENDNOTIFICATION", SendNotification, NULL, NULL );

    Tcl_CreateCommand( interp, "holiday", isHoliday, NULL, NULL );

    Tcl_CreateCommand( interp, "exit", Exit, NULL, NULL );

    Tcl_CreateCommand( interp, "mcu8100", mcu8100, NULL, NULL );
    Tcl_CreateCommand( interp, "mcu9000eoi", mcu9000eoi, NULL, NULL );
    Tcl_CreateCommand( interp, "mcu8100wepco", mcu8100wepco, NULL, NULL );
    Tcl_CreateCommand( interp, "mcu8100service", mcu8100service, NULL, NULL );
    Tcl_CreateCommand( interp, "mcu8100program", mcu8100program, NULL, NULL );

    Tcl_CreateCommand( interp, "PMSI", pmsi, NULL, NULL );
    Tcl_CreateCommand( interp, "pmsi", pmsi, NULL, NULL );

    Tcl_CreateCommand( interp, "importcommandfile", importCommandFile, NULL, NULL );
    Tcl_CreateCommand( interp, "ImportCommandFile", importCommandFile, NULL, NULL );
    Tcl_CreateCommand( interp, "IMPORTCOMMANDFILE", importCommandFile, NULL, NULL );

    /* Load up the initialization script */
    RWCString init_script;
    HINSTANCE hLib = LoadLibrary("cparms.dll");

    if(hLib)
    {
        char temp[80];

        CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

        if( (*fpGetAsString)(MCCMD_CTL_SCRIPTS_DIR, temp, 64) )
            init_script = temp;
        else
            init_script = "c:/yukon/server/macsscripts";

        init_script += "/";

        if( (*fpGetAsString)(MCCMD_INIT_SCRIPT, temp, 64) )
            init_script += temp;
        else
            init_script += "init.tcl";

        if( (*fpGetAsString)(MCCMD_DEBUG_LEVEL, temp, 64) )
        {
            char *eptr;
            gMccmdDebugLevel = strtoul(temp, &eptr, 16);
        }
            //    gMccmdDebugLevel = atoi(temp);

        if( gMccmdDebugLevel > 0 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << MCCMD_DEBUG_LEVEL << ": 0x" << hex <<  gMccmdDebugLevel << dec << endl;
        }

        FreeLibrary(hLib);
    }
    else
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "Unable to load cparms dll " << endl;
    }


    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << "Using: " << init_script << endl;
    }


    Tcl_EvalFile(interp, (char*) init_script.data() );

    /* declare that we are implementing the MCCmd package so that scripts that have
    "package require McCmd" can load McCmd automatically. */
    Tcl_PkgProvide(interp, "mccmd", "1.0");

    if( PILConnection == 0 )
        Mccmd_Connect();

    return TCL_OK;
}

/*--- Below are the MACS Tcl extensions  ---*/

int Exit(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    Mccmd_Disconnect();

    //copied from default tcl exit handler
    int value;

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

int GetValue(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    RWCString cmd;
    AppendToString(cmd, argc, argv);

    return DoTwoWayRequest(interp, cmd);
}

int PutValue(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    RWCString cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int GetStatus(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    RWCString cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int PutStatus(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    RWCString cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int GetConfig(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    RWCString cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int PutConfig(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    RWCString cmd;
    AppendToString(cmd, argc, argv);

    return DoOneWayRequest(interp, cmd);
}

int Loop(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    RWCString cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int Control(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    RWCString cmd;
    AppendToString(cmd, argc, argv);
    return DoOneWayRequest(interp, cmd);
}

int mcu8100(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc != 2 )
    {
        WriteOutput("Usage: mcu8100 filename");
        return TCL_ERROR;
    }

    RWCString file = argv[1];
    RWOrdered results;

    if( DecodeCFDATAFile( file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    RWOrderedIterator iter(results);

    while( iter() )
    {
        RWCollectableString* str = (RWCollectableString*) iter.key();
        Tcl_Eval( interp, (char*) str->data());
    }

    results.clearAndDestroy();
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

    RWCString file = argv[1];
    RWOrdered results;

    if( DecodeEOIFile(file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    RWOrderedIterator iter(results);

    while( iter() )
    {
        RWCollectableString* str = (RWCollectableString*) iter.key();
        Tcl_Eval( interp, (char*) str->data());
    }

    results.clearAndDestroy();
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

    RWCString file = argv[1];
    RWOrdered results;

    if( DecodeWepcoFile( file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    RWOrderedIterator iter(results);

    while( iter() )
    {
        RWCollectableString* str = (RWCollectableString*) iter.key();
        Tcl_Eval( interp, (char*) str->data());
        Sleep(100); // CGP 051302  Buy some sanity.

        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            Tcl_SetResult( interp, "interrupted", NULL );
            tcl_ret = TCL_ERROR;
            break;
        }
    }

    results.clearAndDestroy();
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

    RWCString file = argv[1];
    RWOrdered results;

    if( DecodeWepcoFileService( file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    int num_sent = 0;
    RWOrderedIterator iter(results);

    while( iter() )
    {
        RWCollectableString* str = (RWCollectableString*) iter.key();
        Tcl_Eval( interp, (char*) str->data());
        num_sent++;
        Sleep(100); // CGP 051302  Buy some sanity.

        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            Tcl_SetResult( interp, "interrupted", NULL );
            tcl_ret = TCL_ERROR;
            break;
        }
    }

    results.clearAndDestroy();

    // set the number of pil requests sent to be the return val
    Tcl_SetResult( interp, CtiNumStr(num_sent), NULL);
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

    RWCString file = argv[1];
    RWOrdered results;

    if( DecodeWepcoFileConfig( file, &results) == false )
    {
        WriteOutput("Error decoding file");
        return TCL_ERROR;
    }

    int num_sent = 0;
    RWOrderedIterator iter(results);

    while( iter() )
    {
        RWCollectableString* str = (RWCollectableString*) iter.key();
        Tcl_Eval( interp, (char*) str->data());
        num_sent++;
        Sleep(100); // CGP 051302  Buy some sanity.

        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            Tcl_SetResult( interp, "interrupted", NULL );
            tcl_ret = TCL_ERROR;
            break;
        }
    }

    results.clearAndDestroy();

    // set the number of pil requests sent to be the return val
    Tcl_SetResult( interp, CtiNumStr(num_sent), NULL);
    return tcl_ret;
}

int pmsi(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc != 2 )
    {
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " - Usage:  pmsi filename" << endl;
        }

        return TCL_ERROR;
    }

    RWCString file = argv[1];
    RWOrdered results;

    if( DecodePMSIFile( file, &results) == false )
    {
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " - Error decoding file" << endl;
        }
        return TCL_ERROR;
    }

    RWOrderedIterator iter(results);

    while( iter() )
    {
        RWCollectableString* str = (RWCollectableString*) iter.key();
        Tcl_Eval( interp, (char*) str->data());
    }

    results.clearAndDestroy();
    return TCL_OK;
}

int importCommandFile (ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    bool rename=false;
    int commandLimit=0;
    int commandsPerTime=0;
    int interval=1,decodeResult=NORMAL;
    CHAR newFileName[50];
    int retVal = TCL_OK;
    bool dsm2ImportFlag = false;

    if( argc < 2 )
    {
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << " - Usage:  importcommandfile filename [/export][/cmdsperexecution:#]" << endl;
            dout << "           optional parameters: " << endl;
            dout << "                 /export - export commands sent to file export\\sent-mm-dd.txt" << endl;
            dout << "                 /cmdsperexecution:# - number of commands sent each execution (default to all)" << endl;
            dout << "                 /dsm2 - import dsm2 vconfig.dat type commands (not valid with other options)" << endl;
        }

        retVal = TCL_ERROR;
    }
    else
    {

        RWCString file = argv[1];
        RWOrdered results;

        if(argc > 2)
        {
            for(int i=2; i < argc; i++)
            {
                // check for rename
                if(!RWCString ("/export").compareTo (RWCString (argv[i]),RWCString::ignoreCase))
                {
                    {
                        sprintf (newFileName,"..\\export\\sent-%02d-%02d.txt",
                                 RWDate().month(),
                                 RWDate().dayOfMonth());
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << RWTime() << " - Will export commands from " << file << " to " << RWCString (newFileName) <<endl;;
                    }
                    rename = true;
                }
                // left in here to support Nevada Power
                if(RWCString(argv[i]).contains (RWCString ("/perinterval"),RWCString::ignoreCase))
                {
                    int colon = RWCString(argv[i]).first(':');

                    if( colon !=RW_NPOS )
                    {
                        commandLimit = atoi (argv[i]+colon+1);
                    }
                    {
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << RWTime() << " - Will export " << commandLimit << " commands from " << file << " per interval " <<endl;;
                    }
                }

                // left in here to support Nevada Power
                if(RWCString(argv[i]).contains (RWCString ("/interval"),RWCString::ignoreCase))
                {
                    int colon = RWCString(argv[i]).first(':');

                    if( colon !=RW_NPOS )
                    {
                        interval = atoi (argv[i]+colon+1);
                    }
                    {
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << RWTime() << " - Interval: " << interval << " minutes " <<endl;;
                    }
                }

                if(RWCString(argv[i]).contains (RWCString ("/cmdsperexecution"),RWCString::ignoreCase))
                {
                    int colon = RWCString(argv[i]).first(':');

                    if( colon !=RW_NPOS )
                    {
                        commandsPerTime = atoi (argv[i]+colon+1);
                    }
                    {
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << RWTime() << " - Will export " << commandsPerTime << " commands from " << file << " every execution " <<endl;;
                    }
                }

                if(RWCString(argv[i]).contains (RWCString ("/dsm2"),RWCString::ignoreCase))
                {
                    dsm2ImportFlag = true;
                    {
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << RWTime() << " - Will import file as DSM2 vconfig.dat format " <<endl;;
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
                    dout << RWTime() << " Error importing file " << file << endl;
                }
            }

        }
        else

        {

            decodeResult = decodeTextCommandFile( file, commandsPerTime, &results);

            if(decodeResult == TEXT_CMD_FILE_LOG_FAIL )
            {
                RWCString logMsg;
                RWCString infoMsg;
                logMsg = RWCString ("Logging commands from file ");
                logMsg += file;
                logMsg += RWCString (" failed ");
                infoMsg = RWCString ("Log file ");
                infoMsg += RWCString (newFileName);
                infoMsg += RWCString (" is locked by another process");

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
                RWCString logMsg;
                RWCString infoMsg;
                logMsg = RWCString ("Removing sent commands from file ");
                logMsg += file;
                logMsg += RWCString (" failed ");
                infoMsg = RWCString ("Original file ");
                infoMsg += file;
                infoMsg += RWCString (" is locked by another process");

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
                    dout << RWTime() << " Export file " << file << " does not exist or is locked " << endl;
                }
            }
        }
        // send what we do have
        RWOrderedIterator iter(results);

        while( iter() )
        {
            RWCollectableString* str = (RWCollectableString*) iter.key();
            Tcl_Eval( interp, (char*) str->data());
        }

        results.clearAndDestroy();
    }
    return retVal;
}

int isHoliday(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    time_t t = time(NULL);
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
    if( mgr.isHoliday(RWDate(localtime(&t)), id) )
    {
        Tcl_SetResult(interp, "true", NULL );
        return TCL_OK;
    }

    Tcl_SetResult(interp, "false", NULL );
    return TCL_OK;
}


int LogEvent(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[] )
{
    RWCString user = "";
    RWCString message = "";
    RWCString info = "";
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
        RWCString param(argv[index++]);
        param.toLower();
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

    RWCString name(argv[1]);

    long id = GetNotificationGroupID(name);

    if( id == -1 )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " Could not locate notification group named: " << name << endl;
        return TCL_ERROR;
    }

    CtiEmailMsg* msg = new CtiEmailMsg( id, CtiEmailMsg::NGroupIDEmailType );

    msg->setSubject( argv[2] );
    msg->setText( argv[3] );

    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << RWTime() << " Sending notify group to dispatch" << endl;
        dout << RWTime() << " sender: " << msg->getSender() << endl;
        dout << RWTime() << " subject: " << msg->getSubject() << endl;
        dout << RWTime() << " text: " << msg->getText() << endl;
    }

    VanGoghConnection->WriteConnQue(msg);

    return TCL_OK;
}


int Select(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc == 1 )
    {
        RWCString selection = Tcl_GetVar(interp, SelectedVariable, 0 );
        //GetSelected(interp,selection);

        RWCString out("current selection: ");
        out += selection;

        WriteOutput( (char*) selection.data() );
    }
    else
    {
        RWCString cmd;
        AppendToString(cmd, argc, argv);

        Tcl_SetVar(interp, SelectedVariable, (char*) cmd.data(), 0 );
        //SetSelected(interp,cmd);
    }
    return TCL_OK;
}

int Wait(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[])
{
    if( argc < 2 )
    {
        RWCString usage("Usage:  Wait <seconds>");
        WriteOutput((const char*) usage );
    }

    long delay = atol(argv[1]);
    time_t start = time(NULL);

    while( start + delay > time(NULL) )
    {
        //Check for cancellation
        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            WriteOutput("interrupted");
            Tcl_SetResult( interp, "interrupted", NULL );
            return TCL_ERROR;
        }

        rwSleep(1000);
    }

    return TCL_OK;
}

int DoOneWayRequest(Tcl_Interp* interp, RWCString& cmd_line)
{
    char* p;
    long timeout = DEFAULT_ONE_WAY_TIMEOUT;
    RWCString timeoutStr;

    if( !((timeoutStr = cmd_line.match(re_timeout)).isNull()) )
    {
        if( !((timeoutStr = timeoutStr.match(re_num)).isNull()) )
        {
            timeout = strtol(timeoutStr.data(),&p,10);
        }
    }
    return DoRequest(interp,cmd_line,timeout,false);
}

int DoTwoWayRequest(Tcl_Interp* interp, RWCString& cmd_line)
{
    char* p;
    long timeout = DEFAULT_TWO_WAY_TIMEOUT;
    RWCString timeoutStr;

    if( !((timeoutStr = cmd_line.match(re_timeout)).isNull()) )
    {
        if( !((timeoutStr = timeoutStr.match(re_num)).isNull()) )
        {
            timeout = strtol(timeoutStr.data(),&p,10);
        }
    }
    return DoRequest(interp,cmd_line,timeout,true);
}

static int DoRequest(Tcl_Interp* interp, RWCString& cmd_line, long timeout, bool two_way)
{
    bool interrupted = false;
    bool timed_out = false;

    RWSet req_set;

    //Create a CountedPCPtrQueue and place it into the InQueueStore
    //Be sure to remove it before exiting
    unsigned int msgid = GenMsgID();

    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue_ptr = new CtiCountedPCPtrQueue<RWCollectable>();

    if( timeout != 0 ) // don't bother if we don't want responses
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard(_queue_mux);
        InQueueStore.insertKeyAndValue(msgid, queue_ptr);
    }

    BuildRequestSet(interp, cmd_line,req_set);

    // Nothing to do get outta here
    if( req_set.entries() == 0 )
        return TCL_OK;
   
    //write out all of the requests
    RWSetIterator iter(req_set);
    for( ; iter(); )
    {
        CtiRequestMsg* req = (CtiRequestMsg*) iter.key();
        req->setUserMessageId(msgid);

        if( gMccmdDebugLevel & MCCMD_DEBUG_PILREQUEST )
            DumpRequestMessage(*req);
        else
            WriteOutput( (char*) req->CommandString().data() );

        PILConnection->WriteConnQue(req);
    }
    
    if( timeout == 0 ) // Not waiting for responses so we're done
        return TCL_OK;

    long start = time(NULL);

    set< long, less<long> > device_set;
    set< long, less<long> > good_set;
    set< long, less<long> > bad_set;

    RWCollectable* msg = NULL;
    RWWaitStatus status;

    do
    {
        status = queue_ptr->read(msg, 100);

        if( status != RW_THR_TIMEOUT && msg != NULL )
        {
            if( msg->isA() == MSG_PCRETURN )
            {
                // received a message, reset the timeout
                start = time(NULL);

                CtiReturnMsg* ret_msg = (CtiReturnMsg*) msg;
                DumpReturnMessage(*ret_msg);
                HandleReturnMessage(*ret_msg, good_set, bad_set, device_set);
                
                // have we received everything expected?
                if( device_set.size() == 0 )
                    break;                
            }
            else
            {
                RWCString err("Received unknown message __LINE__, __FILE__");
                WriteOutput(err.data());
            }

            delete msg;
            msg = NULL;
        }

        if( Tcl_DoOneEvent( TCL_ALL_EVENTS | TCL_DONT_WAIT) == 1 )
        {
            Tcl_SetResult( interp, "interrupted", NULL );
            interrupted = true;
            break;
        }

        if( time(NULL) > start + timeout )
        {
            RWCString info("timed out: ");
            info += cmd_line;
            WriteOutput(info);
            break;
        }

    } while(true);

    delete msg;

    // set up good and bad tcl lists
    Tcl_Obj* good_list = Tcl_NewListObj(0,NULL);
    Tcl_Obj* bad_list = Tcl_NewListObj(0,NULL);
    Tcl_SetVar2Ex(interp, GoodListVariable, NULL, good_list, 0 );
    Tcl_SetVar2Ex(interp, BadListVariable, NULL, bad_list, 0 );

    set< long, less<long> >::iterator s_iter;

    for( s_iter = good_set.begin();
       s_iter != good_set.end();
       s_iter++ )
    {
        RWCString dev_name;
        GetDeviceName(*s_iter,dev_name);
        Tcl_ListObjAppendElement(interp, good_list, Tcl_NewStringObj(dev_name, -1));
    }

    for( s_iter = bad_set.begin();
       s_iter != bad_set.end();
       s_iter++ )
    {
        RWCString dev_name;
        GetDeviceName(*s_iter,dev_name);
        Tcl_ListObjAppendElement(interp, bad_list, Tcl_NewStringObj(dev_name, -1));
    }

    // any device id's left in this set must have timed out
    if( device_set.size() > 0 )
    {

        for( s_iter = device_set.begin();
           s_iter != device_set.end();
           s_iter++ )
        {
            RWCString dev_name;
            GetDeviceName( *s_iter,dev_name);
            Tcl_ListObjAppendElement(interp, bad_list, Tcl_NewStringObj(dev_name, -1));
        }
    }

    //Remove the queue from the InQueueStore
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard(_queue_mux);
        InQueueStore.remove(msgid);
    }


    if( interrupted )
    {
        return TCL_ERROR;
    }
    else
    {
        return TCL_OK;
    }
}

/***
    Handles the sorting of an incoming message form PIL
****/   
void HandleMessage(const RWCollectable& msg,
                   set<long, less<long> >& good_set,
                   set<long, less<long> >& bad_set,
                   set<long, less<long> >& device_set )
{
    if( msg.isA() == MSG_PCRETURN )
    {
        HandleReturnMessage( (CtiReturnMsg&) msg, good_set, bad_set, device_set);
    }
    if( msg.isA() == MSG_MULTI )
    {
        CtiMultiMsg& multi_msg = (CtiMultiMsg&) msg;
        
        for( unsigned i = 0; i < multi_msg.getData( ).entries( ); i++ )
        {        
            HandleMessage( *(multi_msg.getData()[i]), good_set, bad_set, device_set);
        }
    }
    else
    {
        RWCString warn("received an unkown message with class id: ");
        warn += CtiNumStr(msg.isA());
        WriteOutput(warn);
    }
}

void HandleReturnMessage(const CtiReturnMsg& msg, 
                         set<long, less<long> >& good_set,
                         set<long, less<long> >& bad_set,
                         set<long, less<long> >& device_set )                                                    
{
    long dev_id = msg.DeviceId();

    if( good_set.find(dev_id) != good_set.end() )
    {
        RWCString warn("received a message for a device already in the good list, id: ");
        warn += CtiNumStr(dev_id);
        WriteOutput(warn);
    }
    else
    {
        if( msg.ExpectMore() )
        {
            device_set.insert(dev_id);
        }
        else
        {
            if( msg.Status() == 0 )
            {
                if( bad_set.erase(dev_id) > 0 )
                {
                    RWCString warn("moved device from bad list to good list, id: ");
                    warn += CtiNumStr(dev_id);
                    WriteOutput(warn);
                }

                device_set.erase(dev_id);

                if( !good_set.insert(dev_id).second )
                {
                    RWCString warn("device already in good list, id: ");
                    warn += CtiNumStr(dev_id);
                    WriteOutput(warn);
                }
            }
            else
            {
                device_set.erase(dev_id);

                if( !bad_set.insert(dev_id).second)
                {
                    RWCString warn("device already in bad list, id: ");
                    warn += CtiNumStr(dev_id);
                    WriteOutput(warn);
                }
            }                       
        }
    }
}

int StoreQueue(void* queue, int threadid)
{
    /* queue had better be a CtiCountedPCPtrQueue<RWCollectable>* !!! */
    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > counted_ptr = (CtiCountedPCPtrQueue<RWCollectable>*) queue;
    OutQueueStore.insertKeyAndValue( threadid, counted_ptr );

    return 1;
}

int ReleaseQueue(int threadid)
{
    return OutQueueStore.remove( threadid );
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
long GetNotificationGroupID( const RWCString& name)
{
    try
    {
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWCString sql = "SELECT NotificationGroupID FROM NotificationGroup WHERE GroupName='" + name + "'";
            RWDBReader rdr = ExecuteQuery( conn, sql );

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
        dout << RWTime() << " Error retrieving notification group id." << err.why() << endl;
        return -1;
    }
}

static void GetDeviceName(long deviceID, RWCString& name)
{
    try
    {
        char devStr[12];
        sprintf(devStr, "%ld", deviceID);

        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWCString sql = "SELECT PAOName FROM YukonPAObject WHERE YukonPAObject.PAObjectID=";
            sql += devStr;
            RWDBReader rdr = ExecuteQuery( conn, sql );

            if( rdr() )
            {
                rdr >> name;
            }
        }
    }
    catch( RWExternalErr err )
    {
        dout << "Error retreive device name __LINE__ __FILE__" << endl;
    }
}

void BuildRequestSet(Tcl_Interp* interp, RWCString& cmd_line, RWSet& req_set)
{
    if( !cmd_line.contains("select") )
    {
        // cmd_line doesn't specify a select string so lets append
        // this interpreters current select string
        RWCString select = Tcl_GetVar(interp, SelectedVariable, 0);
        cmd_line += " ";
        cmd_line += select;


    }

    size_t index;
    size_t end_index;

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

    if( cmd_line.index(".*select[ ]+list[ ]+", &end_index) != RW_NPOS )
    {
        int list_len;
        Tcl_Obj* sel_str = Tcl_NewStringObj( cmd_line.data() + end_index, -1 );
        Tcl_ListObjLength(interp, sel_str, &list_len );

        cmd_line.replace("\n"," ");
        cmd_line.replace("select.*","");

        if( list_len > 0 )
        {
            Tcl_Obj* sel_list;
            Tcl_ListObjIndex(interp, sel_str, 0, &sel_list);
            Tcl_ListObjLength(interp, sel_list, &list_len);

            for( int i = 0; i < list_len; i++ )
            {
                Tcl_Obj* elem;
                Tcl_ListObjIndex(interp, sel_list, i, &elem);

                RWCString cmd(cmd_line.data());
                cmd += "select name '";
                cmd += Tcl_GetString(elem);
                cmd += "'";

                CtiRequestMsg *msg = new CtiRequestMsg();
                msg->setDeviceId(0);
                msg->setCommandString(cmd);
                msg->setMessagePriority(priority);
                req_set.insert(msg);
            }
        }

        // clean up
        Tcl_DecrRefCount(sel_str);
    }
    else //dont add quotes if it is an id
        if( cmd_line.index(".*select[ ]+[^ ]+[ ]+id", &end_index) != RW_NPOS )
    {
        CtiRequestMsg *msg = new CtiRequestMsg();
        msg->setDeviceId(0);
        msg->setCommandString(cmd_line);
        msg->setMessagePriority(priority);
        req_set.insert(msg);
    }
    else
        if( cmd_line.index(".*select[ ]+[^ ]+[ ]+", &end_index) != RW_NPOS )
    {

        //PIL likes to see ' around any device, group, etc
        cmd_line.insert(end_index, "'");
        cmd_line = cmd_line.strip(RWCString::both, ' ');
        cmd_line.append("'");

        //strip out the braces
        while( (index = cmd_line.index("{")) != RW_NPOS )
        {
            cmd_line.remove(index,1);
        }

        while( (index = cmd_line.index('}')) != RW_NPOS )
        {
            cmd_line.remove(index,1);
        }

        CtiRequestMsg *msg = new CtiRequestMsg();
        msg->setDeviceId(0);
        msg->setCommandString(cmd_line);
        msg->setMessagePriority(priority);
        req_set.insert(msg);
    }
}
