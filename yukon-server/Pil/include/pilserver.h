#pragma once

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

#include <functional>
#include <iostream>
#include <set>

class IM_EX_CTIPIL CtiPILServer : public CtiServer
{
   BOOL                 bServerClosing;
   BOOL                 ListenerAvailable;
   RWSocket             _listenerSocket;

   CtiDeviceManager    *DeviceManager;
   CtiPointManager     *PointManager;
   CtiRouteManager     *RouteManager;
   CtiConfigManager    *ConfigManager;

   RWThreadFunction     ResultThread_;     // Thread which translates INMESS to CtiReturnMsg
   RWThreadFunction     _vgConnThread;     // Thread which manages VanGogh requests!
   RWThreadFunction     _schedulerThread;
   RWThreadFunction     _nexusThread;
   RWThreadFunction     _nexusWriteThread;
   RWThreadFunction     _periodicActionThread;

   CtiCommandParser     _currentParse;
   long                 _currentUserMessageId;

   typedef std::multiset<CtiRequestMsg *, std::greater<CtiMessage *> > group_queue_t;

   group_queue_t _groupQueue;

   CtiQueue<CtiMessage, std::greater<CtiMessage> > _schedulerQueue;

   CtiQueue<INMESS, std::greater<INMESS> > _inQueue;  // Nexus dumps out into this list!

   CtiFIFOQueue< CtiOutMessage > _porterOMQueue;    // Queue for items to be sent to Porter!
   bool                          _broken;           // When the PILServer knows he's sick.

   int  getDeviceGroupMembers( std::string groupname, std::vector<long> &paoids );

   void copyReturnMessageToResponseMonitorQueue(const CtiReturnMsg &returnMsg, void *connectionHandle);

public:

   typedef CtiServer Inherited;

   CtiPILServer(CtiDeviceManager *DM = NULL, CtiPointManager *PM = NULL, CtiRouteManager *RM = NULL, CtiConfigManager *CM = NULL) :
      DeviceManager(DM),
      PointManager (PM),
      RouteManager (RM),
      ConfigManager(CM),
      ListenerAvailable(0),
      bServerClosing(FALSE),
      _currentParse(""),
      _currentUserMessageId(0)
   {}

   virtual ~CtiPILServer()
   {
      while( !_inQueue.isEmpty() )
      {
         delete _inQueue.getQueue();
      }
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
   void  schedulerThread();
   void  periodicActionThread();

   INT analyzeWhiteRabbits(CtiRequestMsg& pReq, CtiCommandParser &parse, std::list< CtiRequestMsg* > & execList, std::list< CtiMessage* > & retList);
   INT analyzeAutoRole(CtiRequestMsg& Req, CtiCommandParser &parse, std::list< CtiRequestMsg* > & execList, std::list< CtiMessage* > & retList);
   INT analyzePointGroup(CtiRequestMsg& Req, CtiCommandParser &parse, std::list< CtiRequestMsg* > & execList, std::list< CtiMessage* > & retList);

   void putQueue(CtiMessage *Msg);

   bool isBroken() const { return _broken; }
   void indicateControlOnSubGroups(CtiDeviceSPtr &Dev, CtiRequestMsg *&pReq, CtiCommandParser &parse, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList);

   int reportClientRequests(CtiDeviceSPtr &Dev, const CtiCommandParser &parse, const CtiRequestMsg * pReqOrig, const CtiRequestMsg *pExecReq, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList);

};

