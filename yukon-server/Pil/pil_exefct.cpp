#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "executorfactory.h"
#include "pil_exefct.h"
#include "exe_pcreq.h"

CtiExecutor* CtiPILExecutorFactory::getExecutor(CtiMessage* msg)
{
   CtiExecutor *Ex = NULL;

   switch(msg->isA())
   {
   case MSG_PCREQUEST:
   case MSG_MULTI:
      {
         Ex = new CtiRequestExecutor(msg);
         break;
      }
   default:
      {
         Ex = Inherited::getExecutor(msg);

         if(!Ex)
            cout << "PIL Executor failed to manufacture an executor for " << msg->isA() << endl;

         break;
      }
   }

   return Ex;
}


