#pragma once

#include <rw\thr\thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>

#include "dsm2.h"
#include "server_b.h"
#include "dlldefs.h"
#include "msg_pcrequest.h"
#include "mgr_device.h"
#include "mgr_point.h"
#include "mgr_route.h"
#include "mgr_config.h"
#include "ctibase.h"

#include "connection_listener.h"
#include "amq_constants.h"

#include "mgr_rfn_request.h"

#include <boost/thread.hpp>
#include <boost/ptr_container/ptr_deque.hpp>

#include <functional>
#include <iostream>
#include <set>

namespace Cti {
namespace Pil {

class RfnDeviceResult;

class IM_EX_CTIPIL PilServer : public CtiServer
{
   BOOL                 bServerClosing;

   CtiListenerConnection _listenerConnection;

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

   RfnRequestManager    _rfnManager;
   unsigned long        _rfnRequestId;

   typedef std::multiset<CtiRequestMsg *, std::greater<CtiMessage *> > group_queue_t;

   group_queue_t _groupQueue;

   CtiQueue<CtiMessage, std::greater<CtiMessage> > _schedulerQueue;

   CtiQueue<INMESS, std::greater<INMESS> > _inQueue;  // Nexus dumps out into this list!

   CtiFIFOQueue< CtiOutMessage > _porterOMQueue;    // Queue for items to be sent to Porter!
   bool                          _broken;           // When the PILServer knows he's sick.

   static void copyReturnMessageToResponseMonitorQueue(const CtiReturnMsg &returnMsg, void *connectionHandle);

   void handleInMessageResult(const INMESS *InMessage);
   void handleRfnDeviceResult(const RfnDeviceResult &result);

   void sendResults(CtiDeviceBase::CtiMessageList &vgList, CtiDeviceBase::CtiMessageList &retList, const int priority, void *connectionHandle);

protected:

   virtual std::vector<long> getDeviceGroupMembers( std::string groupname ) const;

public:

   typedef CtiServer Inherited;

   PilServer(CtiDeviceManager *DM = NULL, CtiPointManager *PM = NULL, CtiRouteManager *RM = NULL, CtiConfigManager *CM = NULL) :
      DeviceManager(DM),
      PointManager (PM),
      RouteManager (RM),
      ConfigManager(CM),
      bServerClosing(FALSE),
      _currentParse(""),
      _currentUserMessageId(0),
      _listenerConnection( Cti::Messaging::ActiveMQ::Queue::pil ),
      _rfnRequestId(0)
   {}

   virtual ~PilServer()
   {
      while( !_inQueue.isEmpty() )
      {
         delete _inQueue.getQueue();
      }
   }

   virtual void  clientShutdown(CtiServer::ptr_type CM);
   virtual void  shutdown();

   int   executeRequest(const CtiRequestMsg*);
   int   executeMulti(const CtiMultiMsg*);
   int   execute();
   void  mainThread();
   void  connectionThread();
   void  resultThread();
   void  nexusThread();
   void  nexusWriteThread();
   void  vgConnThread();
   void  schedulerThread();
   void  periodicActionThread();

   void analyzeWhiteRabbits(const CtiRequestMsg& pReq, CtiCommandParser &parse, std::list< CtiRequestMsg* > & execList, boost::ptr_deque<CtiRequestMsg> &groupRequests, std::list< CtiMessage* > & retList);
   INT analyzeAutoRole(CtiRequestMsg& Req, CtiCommandParser &parse, std::list< CtiRequestMsg* > & execList, std::list< CtiMessage* > & retList);
   INT analyzePointGroup(CtiRequestMsg& Req, CtiCommandParser &parse, std::list< CtiRequestMsg* > & execList, std::list< CtiMessage* > & retList);

   void putQueue(CtiMessage *Msg);

   bool isBroken() const { return _broken; }
   void indicateControlOnSubGroups(CtiDeviceBase &Dev, CtiCommandParser &parse, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList);

   int reportClientRequests(const CtiDeviceBase &Dev, const CtiCommandParser &parse, const std::string &requestingUser, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList);

};

}
}

