#include "logic.h"

#include "cparms.h"
#include "configparms.h"
#include "connection.h"
#include "msg_pdata.h"
#include "pointtypes.h"
#include "pointdefs.h"
#include "ctinexus.h"

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

    HINSTANCE hLib = LoadLibrary("cparms.dll");

    if(hLib) {
        char temp[80];

        CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

        bool trouble = false;
	if( (*fpGetAsString)("DISPATCH_MACHINE", temp, 64) ) {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime()  << " - Using " << temp << " as the dispatch host" << endl;
            dispatch_host = temp;
        }
        else {
            trouble = true;
	}

        if( (*fpGetAsString)("DISPATCH_PORT", temp, 64) ) {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime()  << " - Using " << temp << " as the dispatch port" << endl;
            dispatch_port = atoi(temp);
        }
        else {
            trouble = TRUE;
	}

	if( trouble ) {
	    CtiLockGuard< CtiLogger > guard(dout);
	    dout << RWTime() << " - Unable to find one or more mccmd config values in the configuration file." << endl;
	}

	FreeLibrary(hLib);
    }
    else
    {
	CtiLockGuard< CtiLogger > guard(dout);
	dout << "Unable to load cparms dll " << endl;
    }

    gDispatchConnection = new CtiConnection(dispatch_port, dispatch_host.data());
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
