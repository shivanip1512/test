#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   msg_ptreg
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_ptreg.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __MSG_PTREG_H__
#define __MSG_PTREG_H__

#include <rw\rwtime.h>
#include <rw\thr\threadid.h>
#include <rw\tvslist.h>

#include "dlldefs.h"
#include "message.h"       // get the base class

#define REG_NOTHING           0x00000000     // This message registers for status points
#define REG_ALL_STATUS        0x00000001     // This message registers for status points
#define REG_ALL_ANALOG        0x00000002     // This message registers for analog points
#define REG_ALL_ACCUMULATOR   0x00000004     // This message registers for accum points
#define REG_ALL_CALCULATED    0x00000008     // This message registers for calc points
#define REG_EVENTS            0x00000010     // I wish to hear about events.
#define REG_ALARMS            0x00000020     // I wish to hear about alarms.
#define REG_LOAD_PROFILE      0x00000040     // This guy wants to know about LP data too.  NOTE: ONLY points in the PointList are reported.

#define REG_NO_UPLOAD         0x00010000     // Dispatch will not do a MOA if set.
#define REG_TAG_MARKMOA       0x00020000     // Dispatch will mark each pointin the MOA upload with TAG_POINT_MOA_REPORT

#define REG_ALL_PTS_MASK      0x0000000f     // This message registers for all points.
#define REG_ALL_SIGNALS_MASK  0x00000030

class IM_EX_MSG CtiPointRegistrationMsg : public CtiMessage
{
private:

   int                     RegFlags;
   RWTValSlist<LONG>       PointList;

public:
   RWDECLARE_COLLECTABLE( CtiPointRegistrationMsg );

   typedef CtiMessage Inherited;

   CtiPointRegistrationMsg(int Flag = (REG_NONE), int Pri = 14);
   virtual ~CtiPointRegistrationMsg();
   CtiPointRegistrationMsg(const CtiPointRegistrationMsg &aRef);

   CtiPointRegistrationMsg& operator=(const CtiPointRegistrationMsg& aRef);
   void saveGuts(RWvostream &aStream) const;
   void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;
   void What() const;

   // If list is empty, I assume you wanted them all!.
   int  getCount() const;

   LONG& operator[](size_t i);
   LONG operator[](size_t i) const;
   // Clear out the list.
   void clear();
   void insert(const LONG& a);
   int getFlags() const;

   virtual void dump() const;
};

#endif   // #ifndef __MSG_PTREG_H__

