#include "precompiled.h"
#include "logic.h"

#include "cparms.h"
#include "connection_client.h"
#include "amq_constants.h"
#include "msg_pdata.h"
#include "pointtypes.h"
#include "pointdefs.h"

using std::string;
using std::endl;

boost::scoped_ptr<CtiClientConnection> gDispatchConnection;

int Logic_Init(Tcl_Interp* interp) {
    Tcl_CreateCommand(interp, "dispatchstartup", Dispatch_Connect, NULL, NULL);
    Tcl_CreateCommand(interp, "SetPoint", SetPoint, NULL, NULL);
    return TCL_OK;
}

int Dispatch_Connect(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]) {
    if(gDispatchConnection != 0) {
    return TCL_OK;
    }

    gDispatchConnection.reset( new CtiClientConnection( Cti::Messaging::ActiveMQ::Queue::dispatch ));
    gDispatchConnection->start();

    //Send a registration message
    CtiRegistrationMsg* reg2 = new CtiRegistrationMsg("CTILOGIC", 0, false );
    gDispatchConnection->WriteConnQue(reg2, CALLSITE);

    return TCL_OK;
}

int SetPoint(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]) {
    if(argc != 3) {
    Tcl_SetErrorCode(interp, "SendPointData - wrong number of arguments");
    return TCL_ERROR;
    }

    int pointid = atoi(argv[1]);
    double value = atof(argv[2]);

    CtiPointDataMsg* msg = new CtiPointDataMsg(pointid, value, NormalQuality, InvalidPointType);
    gDispatchConnection->WriteConnQue(msg, CALLSITE);
    return TCL_OK;
}
