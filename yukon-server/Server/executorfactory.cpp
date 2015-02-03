#include "precompiled.h"

#include "executorfactory.h"
#include "message.h"
#include "exe_cmd.h"
#include "exe_reg.h"
#include "logger.h"

CtiExecutor* CtiExecutorFactory::getExecutor(CtiMessage* msg)
{
   switch(msg->isA())
   {
      case MSG_COMMAND:    return new CtiCommandExecutor     (msg);
      case MSG_REGISTER:   return new CtiRegistrationExecutor(msg);
   }

   CTILOG_WARN(dout,  "CtiExecutorFactory failed to manufacture an executor for " << msg->isA());

   return NULL;
}

