#include "precompiled.h"

#include "message.h"
#include "pilserver.h"
#include "exe_pcreq.h"
#include "msg_pcrequest.h"

using namespace std;


YukonError_t CtiRequestExecutor::ServerExecute(CtiServer *Svr)
{
   Cti::Pil::PilServer *Server = static_cast<Cti::Pil::PilServer *>(Svr);

   switch( getMessage()->isA() )
   {
       case MSG_PCREQUEST: return Server->executeRequest((CtiRequestMsg*)getMessage());
       case MSG_MULTI:     return Server->executeMulti  ((CtiMultiMsg*)  getMessage());
       default:            return ClientErrors::None;
   }


}


