#pragma once

#include "dsm2.h"
#include "server_b.h"
#include "dlldefs.h"
#include "msg_pcrequest.h"
#include "mgr_device.h"
#include "mgr_point.h"
#include "mgr_route.h"
#include "mgr_config.h"
#include "dllbase.h"

#include "connection_listener.h"
#include "amq_constants.h"

#include "mgr_rfn_request.h"
#include "cmd_rfn_ConfigNotification.h"
#include "rf_data_streaming_processor.h"

#include <boost/thread.hpp>
#include <boost/ptr_container/ptr_deque.hpp>

#include <functional>
#include <iostream>
#include <set>

namespace Cti::Pil {

class IM_EX_CTIPIL PilServer : public CtiServer
{
   BOOL                 bServerClosing;
   HANDLE               serverClosingEvent;

   CtiListenerConnection _listenerConnection;

   CtiDeviceManager&    DeviceManager;
   CtiPointManager&     PointManager;
   CtiRouteManager&     RouteManager;

   WorkerThread         _resultThread;     // Thread which translates INMESS to CtiReturnMsg
   WorkerThread         _vgConnThread;     // Thread which manages VanGogh requests!
   WorkerThread         _schedulerThread;
   WorkerThread         _nexusThread;
   WorkerThread         _nexusWriteThread;
   WorkerThread         _periodicActionThread;

   CtiCommandParser     _currentParse;
   long                 _currentUserMessageId;

   RfnRequestManager    _rfnRequestManager;
   unsigned long        _rfnRequestId;

   RfDataStreamingProcessor _rfDataStreamingProcessor;

   typedef std::multiset<CtiRequestMsg *, std::greater<CtiMessage *> > group_queue_t;

   group_queue_t _groupQueue;

   CtiQueue<CtiMessage, std::greater<CtiMessage> > _schedulerQueue;

   CtiQueue<INMESS, std::greater<INMESS> > _inQueue;  // Nexus dumps out into this list!

   CtiFIFOQueue< CtiOutMessage > _porterOMQueue;    // Queue for items to be sent to Porter!
   bool                          _broken;           // When the PILServer knows he's sick.

   CtiCriticalSection _pilToPorterMux;

   static void copyReturnMessageToResponseMonitorQueue(const CtiReturnMsg &returnMsg, const ConnectionHandle connectionHandle);

   void validateConnections();

protected:

   using RequestQueue = std::deque<std::unique_ptr<CtiRequestMsg>>;

   virtual void sendResults(CtiDeviceBase::CtiMessageList &vgList, CtiDeviceBase::CtiMessageList &retList, const int priority, const ConnectionHandle connectionHandle);

   virtual std::vector<long> getDeviceGroupMembers( std::string groupname ) const;

   void handleInMessageResult(const INMESS &InMessage);
   void handleRfnDeviceResult(RfnDeviceResult result);
   void handleRfnUnsolicitedReport(RfnRequestManager::UnsolicitedReport report);

   void analyzeWhiteRabbits(const CtiRequestMsg& pReq, CtiCommandParser &parse, RequestQueue& execList, RequestQueue& groupRequests, std::list< CtiMessage* > & retList);
   int  analyzeAutoRole(CtiRequestMsg& Req, CtiCommandParser &parse, RequestQueue& execList, std::list< CtiMessage* > & retList);
   int  analyzePointGroup(CtiRequestMsg& Req, CtiCommandParser &parse, RequestQueue& execList, std::list< CtiMessage* > & retList);

public:

   typedef CtiServer Inherited;

   PilServer(CtiDeviceManager& DM, CtiPointManager& PM, CtiRouteManager& RM);
   virtual ~PilServer();

   void  clientShutdown(CtiServer::ptr_type &CM) override;
   void  shutdown() override;

   YukonError_t executeRequest(const CtiRequestMsg*);
   YukonError_t executeMulti(const CtiMultiMsg*);
   int   execute();
   void  mainThread();
   void  connectionThread();
   void  resultThread();
   void  nexusThread();
   void  nexusWriteThread();
   void  vgConnThread();
   void  schedulerThread();
   void  periodicActionThread();

   void putQueue(CtiMessage *Msg);

   bool isBroken() const { return _broken; }
   void indicateControlOnSubGroups(CtiDeviceBase &Dev, CtiCommandParser &parse, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList);

   int reportClientRequests(const CtiDeviceBase &Dev, const CtiCommandParser &parse, const std::string &requestingUser, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList);

};

}