/*-----------------------------------------------------------------------------*
*
* File:   server_b
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/INCLUDE/server_b.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/10/19 20:23:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __SERVER_B_H__
#define __SERVER_B_H__

#include <windows.h>
#include <iostream>
#include <functional>
using namespace std;


#include <rw\thr\thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw\rwerr.h>
#include <rw\tphasht.h>

#include "con_mgr.h"
#include "cmdopts.h"
#include "mutex.h"
#include "queue.h"
#include "dlldefs.h"

class CtiCommandMsg;

IM_EX_CTISVR bool isQuestionable(const CtiConnectionManager *ptr, void *narg);

struct vg_hash
{
   unsigned long operator()(const CtiConnectionManager& x) const { return x.hash(x); }
};



class IM_EX_CTISVR CtiServer
{
public:
   typedef RWTPtrHashMultiSetIterator< CtiConnectionManager, vg_hash, equal_to<CtiConnectionManager> >      iterator;
   typedef RWTPtrHashMultiSet< CtiConnectionManager, vg_hash, equal_to<CtiConnectionManager> >::value_type  val_pair;
private:

protected:
   BOOL                       _listenerAvailable;

   CtiMutex                   server_mux;       // Mutual exclusion object.

   RWThreadFunction           MainThread_;      // Thread which does work on the MainQueue_
   RWThreadFunction           ConnThread_;      // Thread which accepts connections.

   CtiCmdLineOpts             Options_;         // Found in the CMDLINE.DLL currently!

   /*
    *  Data Stores.
    */
   RWTPtrHashMultiSet< CtiConnectionManager, vg_hash, equal_to<CtiConnectionManager> > mConnectionTable;

   CtiConnection::InQ_t          MainQueue_;    // Main queue (Message is the base class) Priority queue)

   RWInetPort                    NetPort;
   RWInetAddr                    NetAddr;    // This one for this server!
   RWSocketListener              *Listener;


public:

   CtiServer();
   virtual ~CtiServer();

   void CmdLine(int argc, char **argv);
   virtual void join();

   virtual RWWaitStatus join(unsigned long milliseconds);
   virtual void  clientConnect(CtiConnectionManager *CM);
   virtual void  clientShutdown(CtiConnectionManager *&CM);
   virtual int   clientRegistration(CtiConnectionManager *CM);
   virtual int   commandMsgHandler(CtiCommandMsg *Cmd);
   virtual int   clientArbitrationWinner(CtiConnectionManager *CM);
   virtual int   clientConfrontEveryone(PULONG pClientCount = NULL);
   virtual int   clientPurgeQuestionables(PULONG pDeadClients = NULL);

   virtual void  shutdown();

};


#endif      // #ifndef __SERVER_B_H__
