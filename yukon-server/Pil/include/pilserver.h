#ifndef __PILSERVER_H__
#define __PILSERVER_H__

#include <functional>
#include <iostream>
using std::iostream;

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
#include "mgr_point.h"
#include "mgr_route.h"
#include "mgr_config.h"
#include "ctibase.h"

class IM_EX_CTIPIL CtiPILServer : public CtiServer
{
private:

   BOOL                 bServerClosing;
   BOOL                 ListenerAvailable;

   CtiDeviceManager    *DeviceManager;
   CtiPointManager     *PointManager;
   CtiRouteManager     *RouteManager;
   CtiConfigManager    *ConfigManager;

   RWThreadFunction     ResultThread_;     // Thread which translates INMESS to CtiReturnMsg
   RWThreadFunction     _vgConnThread;     // Thread which manages VanGogh requests!
   RWThreadFunction     _nexusThread;
   RWThreadFunction     _nexusWriteThread;

   CtiMutex             _inMux;            // Protects the _inList.
   list< INMESS* >      _inList;           // Nexus dumps out into this list!

   CtiFIFOQueue< CtiOutMessage > _porterOMQueue;    // Queue for items to be sent to Porter!
   bool                          _broken;           // When the PILServer knows he's sick.

   int  getDeviceGroupMembers( string groupname, vector<long> &paoids );
   void loadDevicePoints( const vector<long> &paoids );

public:

   typedef CtiServer Inherited;

   CtiPILServer(CtiDeviceManager *DM = NULL, CtiPointManager *PM = NULL, CtiRouteManager *RM = NULL, CtiConfigManager *CM = NULL) :
      DeviceManager(DM),
      PointManager (PM),
      RouteManager (RM),
      ConfigManager(CM),
      ListenerAvailable(0),
      bServerClosing(FALSE)
   {}

   virtual ~CtiPILServer()
   {
       delete_container(_inList);
       _inList.clear();
   }

   virtual void  clientShutdown(CtiServer::ptr_type CM);
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

   INT analyzeWhiteRabbits(CtiRequestMsg& pReq, CtiCommandParser &parse, list< CtiRequestMsg* > & execList, list< CtiMessage* > & retList);
   INT analyzeAutoRole(CtiRequestMsg& Req, CtiCommandParser &parse, list< CtiRequestMsg* > & execList, list< CtiMessage* > & retList);
   INT analyzePointGroup(CtiRequestMsg& Req, CtiCommandParser &parse, list< CtiRequestMsg* > & execList, list< CtiMessage* > & retList);

   void putQueue(CtiMessage *Msg);

   bool isBroken() const { return _broken; }
   void indicateControlOnSubGroups(CtiDeviceSPtr &Dev, CtiRequestMsg *&pReq, CtiCommandParser &parse, list< CtiMessage* > &vgList, list< CtiMessage* > &retList);

   int reportClientRequests(CtiDeviceSPtr &Dev, const CtiCommandParser &parse, const CtiRequestMsg *&pReqOrig, const CtiRequestMsg *&pExecReq, list< CtiMessage* > &vgList, list< CtiMessage* > &retList);

};

#endif // #ifndef __PILSERVER_H__


