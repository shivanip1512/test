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

#include "dsm2.h"
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

   RWThreadFunction              ResultThread_;     // Thread which translates INMESS to CtiReturnMsg
   RWThreadFunction              _vgConnThread;     // Thread which manages VanGogh requests!
   RWThreadFunction              _nexusThread;
   RWThreadFunction              _nexusWriteThread;

   CtiMutex                      _inMux;            // Protects the _inList.
   RWTPtrSlist< INMESS    >      _inList;           // Nexus dumps out into this list!

   CtiQueue< CtiOutMessage, less<CtiOutMessage> >     _porterOMQueue;    // Queue for items to be sent to Porter!
   bool                          _broken;           // When the PILServer knows he's sick.

public:

   typedef CtiServer Inherited;

   CtiPILServer(CtiDeviceManager *DM = NULL, CtiRouteManager *RM = NULL) :
      DeviceManager(DM),
      RouteManager(RM),
      ListenerAvailable(0),
      bServerClosing(FALSE)
   {}

   virtual ~CtiPILServer()
   {
       _inList.clearAndDestroy();
   }

   virtual void  clientShutdown(CtiConnectionManager *&CM);
   virtual void  shutdown();

   int   executeRequest(CtiRequestMsg*);
   int   executeMulti(CtiMultiMsg*);
   int   execute();
   void  mainThread();
   void  connectionThread();
   void  resultThread();
   void  nexusThread();
   void  nexusWriteThread();
   void  vgConnThread();

   INT analyzeWhiteRabbits(CtiRequestMsg& pReq, CtiCommandParser &parse, RWTPtrSlist< CtiRequestMsg > & execList, RWTPtrSlist< CtiMessage > & retList);
   INT analyzeAutoRole(CtiRequestMsg& Req, CtiCommandParser &parse, RWTPtrSlist< CtiRequestMsg > & execList, RWTPtrSlist< CtiMessage > & retList);

   void putQueue(CtiMessage *Msg);

   bool isBroken() const { return _broken; }
   void indicateControlOnSubGroups(CtiDeviceSPtr &Dev, CtiRequestMsg *&pReq, CtiCommandParser &parse, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList);

};

#endif // #ifndef __PILSERVER_H__


