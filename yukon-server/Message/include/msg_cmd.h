
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   msg_cmd
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_cmd.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/01/14 17:22:25 $
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

   // Command Operaitons I will support
   enum {
      NoOp = 0,                        // Does a LoopClient command
      Shutdown,                        // Bye bye to VanGogh
      ClientAppShutdown,               // Client App has gone away
      NewClient,                       // Client App has been connected by Connection Handler
      LoopClient,                      // Sends message back to the client
      TraceRoute,                      // Prints a blurb out in each place it is encountered.
      AreYouThere,                     // Used to find out if a client is still there. Receipt causes a response.
      AcknowledgeAlarm,                //
      ClearAlarm,                      //
      TokenGrant,                      // Server is providing client with a security pass key.
      ReregistrationRequest,           // Client must re-register with server application.
      UpdateFailed,                    // scan completed with an error, vector contains (token, idtype, dev/pointid, scantype, failcode) This causes Quality to go nonUpdated on all affected points!
      ControlRequest,                  // Vector contains token, deviceid (0 if not available), pointid/controloffset, raw state number, (optional) TRUE if cmd is to use control offset.
      Ablement,                        // Vector contains token, idtype (0 = Device, 1 = Point), id, ablement bit (0 = dis, 1 = enable).
      CommStatus,                      // Vector contains token, deviceid, status (communication result in porter, 0 = NORMAL).
      AlternateScanRate,               // Vector contains token, deviceid, seconds since midnight start (may be negative - use receipt time for start), duration in seconds (may be zero)
      ControlAblement,                 // Vector contains token, idtype (0 = Device, 1 = Point), id, ablement bit (0 = dis, 1 = enable).
      PointTagAdjust,                  // Vector contains token, pointid, tag(s) to set, tag(s) to reset.
      PorterConsoleInput,              // Vector contains token, operation (same as keyboard character. see PorterConsoleInput:porter.cpp)
      ResetControlHours,               // By default resets the Seasonal control history, could add to the op arg list to allow for resets of other control histories; Vector contains token
      LastCommand//this is to be the last entry in the enum, so add new entries above here, otay
   };
};

#endif   // #ifndef __MSG_CMD_H__
