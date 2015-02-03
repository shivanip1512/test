#pragma once

#include <string>

#include <tcl/tcl.h>

#ifdef __cplusplus
extern "C" {
#endif

int Logic_Init(Tcl_Interp* interp);

static int Dispatch_Connect(ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);
static int SetPoint        (ClientData clientData, Tcl_Interp* interp, int argc, const char* argv[]);

#ifdef __cplusplus
}
#endif
