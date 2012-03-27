#include "precompiled.h"
#include "logic.h"

#include "cparms.h"
#include "connection.h"
#include "msg_pdata.h"
#include "pointtypes.h"
#include "pointdefs.h"
#include "ctinexus.h"

using std::string;
using std::endl;

CtiConnection* gDispatchConnection = 0;

int Logic_Init(Tcl_Interp* interp) {
    Tcl_CreateCommand(interp, "dispatchstartup", Dispatch_Connect, NULL, NULL);
    Tcl_CreateCommand(interp, "SetPoint", SetPoint, NULL, NULL);
    return TCL_OK;
}

int Dispatch_Connect(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[]) {
    if(gDispatchConnection != 0) {
    return TCL_OK;
    }

    int dispatch_port = VANGOGHNEXUS;
    string dispatch_host = "127.0.0.1";

    bool trouble = false;

    if( gConfigParms.isOpt("DISPATCH_MACHINE") ) {
        dispatch_host = gConfigParms.getValueAsString("DISPATCH_MACHINE");
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()  << " - Using " << dispatch_host << " as the dispatch host" << endl;
    }
    else {
        trouble = true;
    }

    if( gConfigParms.isOpt("DISPATCH_PORT") ) {
        dispatch_port = gConfigParms.getValueAsInt("DISPATCH_PORT");
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()  << " - Using " << dispatch_port << " as the dispatch port" << endl;
    }
    else {
        trouble = TRUE;
    }

    if( trouble ) {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " - Unable to find one or more mccmd config values in the configuration file." << endl;
    }

    gDispatchConnection = new CtiConnection(dispatch_port, dispatch_host.c_str());
    //Send a registration message
    CtiRegistrationMsg* reg2 = new CtiRegistrationMsg("CTILOGIC", 0, false );
    gDispatchConnection->WriteConnQue( reg2 );

    return TCL_OK;
}

int SetPoint(ClientData clientData, Tcl_Interp* interp, int argc, char* argv[]) {
    if(argc != 3) {
    Tcl_SetErrorCode(interp, "SendPointData - wrong number of arguments");
    return TCL_ERROR;
    }

    int pointid = atoi(argv[1]);
    double value = atof(argv[2]);

    CtiPointDataMsg* msg = new CtiPointDataMsg(pointid, value, NormalQuality, InvalidPointType);
    gDispatchConnection->WriteConnQue(msg);
    return TCL_OK;
}
