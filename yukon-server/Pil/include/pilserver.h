#ifndef __PILSERVER_H__
#define __PILSERVER_H__

#include <functional>
#include <iostream>
using namespace std;


#include <rw\cstring.h>
#include <rw\thr\thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>
#include <rw\tphasht.h>

#include "server_b.h"
#include "dlldefs.h"
#include "msg_pcrequest.h"
#include "mgr_device.h"
#include "mgr_route.h"
#include "ctibase.h"

class IM_EX_CTIPIL CtiPILServer : public CtiServer
{
private:

   BOOL                          bServerClosing;
   BOOL                          ListenerAvailable;

   CtiDeviceManager              *DeviceManager;
   CtiRouteManager               *RouteManager;

   RWThreadFunction              ResultThread_;      // Thread which translates INMESS to CtiReturnMsg
   RWThreadFunction              _vgConnThread;    // Thread which manages VanGogh requests!


public:

   typedef CtiServer Inherited;

   CtiPILServer(CtiDeviceManager *DM = NULL, CtiRouteManager *RM = NULL, int QueueSize = 1000) :
      DeviceManager(DM),
      RouteManager(RM),
      ListenerAvailable(0),
      bServerClosing(FALSE),
      CtiServer(QueueSize)
   {}

   virtual ~CtiPILServer()
   {
   }

   virtual void  clientShutdown(CtiConnectionManager *&CM);
   virtual void  shutdown();

   int   executeRequest(CtiRequestMsg*);
   int   executeMulti(CtiMultiMsg*);
   int   execute();
   void  mainThread();
   void  connectionThread();
   void  resultThread();
   void  vgConnThread();

   INT analyzeWhiteRabbits(CtiRequestMsg& pReq, RWTPtrSlist< CtiRequestMsg > & execList, RWTPtrSlist< CtiMessage > retList);

};

#endif // #ifndef __PILSERVER_H__


