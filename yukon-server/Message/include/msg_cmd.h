
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   msg_cmd
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_cmd.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/07/19 20:43:36 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __MSG_CMD_H__
#define __MSG_CMD_H__

#include <rw/rwint.h>
#include <rw/collect.h>
#include <rw/rwtime.h>
#include <rw/tvordvec.h>

#include "dlldefs.h"
#include "message.h"       // get the base class

// Defines OpArg[1] for the Operation "UpdateFailed"
#define OP_DEVICEID  0
#define OP_POINTID   1

class IM_EX_MSG CtiCommandMsg : public CtiMessage
{
private:
   int                              iOperation;
   RWCString                        iOpString;
   RWTValOrderedVector<RWInteger>   iOpArgList;

public:
   RWDECLARE_COLLECTABLE( CtiCommandMsg );

   typedef  CtiMessage                       Inherited;
   typedef  RWTValOrderedVector<RWInteger>   CtiOpArgList_t;

   CtiCommandMsg(int Op = NoOp, int Pri = 7);
   CtiCommandMsg(const CtiCommandMsg &aRef, int Pri = 7);
   virtual ~CtiCommandMsg();

   INT               getOperation() const;

   CtiCommandMsg&    operator=(const CtiCommandMsg& aRef);

   CtiOpArgList_t    getOpArgList() const;
   CtiOpArgList_t&   getOpArgList();
   INT               getOpArgument(INT i) const;
   RWCString         getOpString() const;
   RWCString&        getOpString();

   CtiCommandMsg&    setOpArgList(const CtiOpArgList_t &aRef);
   CtiCommandMsg&    setOpString(const RWCString &aRef);
   CtiCommandMsg&    setOperation(const INT &aInt);

   CtiOpArgList_t&   insert(INT i);
   INT               removeFirst();
   void              clear();

   void              saveGuts(RWvostream &aStream) const;
   void              restoreGuts(RWvistream& aStream);
   CtiMessage*       replicateMessage() const;

   void What() const;
   virtual void dump() const;

   // Command Operaitons I will support.
   // Real codes to be sent from client to server must be divisible by 10.
   enum {
      NoOp = 0,                        // Does a LoopClient command
      Shutdown = 10,                   // Bye bye to VanGogh
      ClientAppShutdown = 20,          // Client App has gone away
      NewClient = 30,                  // Client App has been connected by Connection Handler
      LoopClient = 40,                 // Sends message back to the client
      TraceRoute = 50,                 // Prints a blurb out in each place it is encountered.
      AreYouThere = 60,                // Used to find out if a client is still there. Receipt causes a response.
      AcknowledgeAlarm = 70,           //
      ClearAlarm = 80,                 //
      TokenGrant = 90,                 // Server is providing client with a security pass key.
      ReregistrationRequest = 100,     // Client must re-register with server application.
      UpdateFailed = 110,              // scan completed with an error, vector contains (token, idtype, dev/pointid, scantype, failcode) This causes Quality to go nonUpdated on all affected points!
      ControlRequest = 120,            // Vector contains token, deviceid (0 if not available), pointid/controloffset, raw state number, (optional) TRUE if cmd is to use control offset.
      Ablement = 130,                  // Vector contains token, idtype (0 = Device, 1 = Point), id, ablement bit (0 = dis, 1 = enable).
      CommStatus = 140,                // Vector contains token, deviceid, status (communication result in porter, 0 = NORMAL).
      AlternateScanRate = 150,         // Vector contains token, deviceid, seconds since midnight start (may be negative - use receipt time for start), duration in seconds (may be zero)
      ControlAblement = 160,           // Vector contains token, idtype (0 = Device, 1 = Point), id, ablement bit (0 = dis, 1 = enable).
      PointTagAdjust = 170,            // Vector contains token, pointid, tag(s) to set, tag(s) to reset.
      PorterConsoleInput = 180,        // Vector contains token, operation (same as keyboard character. see PorterConsoleInput:porter.cpp)
      ResetControlHours = 190,         // By default resets the Seasonal control history, could add to the op arg list to allow for resets of other control histories; Vector contains token

      LastCommand = 10000              //this is to be the last entry in the enum, so add new entries above here, otay
   };
};

#endif   // #ifndef __MSG_CMD_H__
