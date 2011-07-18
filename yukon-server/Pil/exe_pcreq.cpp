#include "precompiled.h"

#include <iostream>

// #include <rw\thr\threadid.h>
#include "message.h"
// #include "que_exec.h"
#include "pil_conmgr.h"
#include "pilserver.h"
#include "exe_pcreq.h"
#include "msg_pcrequest.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore


INT CtiRequestExecutor::ServerExecute(CtiServer *Svr)
{
   int nRet = NoError;

   CtiPILServer *Server = (CtiPILServer *)Svr;

   switch(getMessage()->isA())
   {
   case MSG_PCREQUEST:
      {
         nRet = Server->executeRequest((CtiRequestMsg*)getMessage());
         break;
      }
   case MSG_MULTI:
      {
         nRet = Server->executeMulti((CtiMultiMsg*)getMessage());
         break;
      }
   }

   return nRet;
}


