#pragma once

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

#define REG_ADD_POINTS        0x00000100     // This registration is intended to add points to an existing registration.
#define REG_REMOVE_POINTS     0x00000200     // This registration is intended to remove specified points from an existing registration.

#define REG_NO_UPLOAD         0x00010000     // Dispatch will not do a MOA if set.
#define REG_TAG_MARKMOA       0x00020000     // Dispatch will mark each pointin the MOA upload with TAG_POINT_MOA_REPORT

#define REG_ALL_PTS_MASK      0x0000000f     // This message registers for all points.
#define REG_ALL_SIGNALS_MASK  0x00000030

class IM_EX_MSG CtiPointRegistrationMsg : public CtiMessage
{
private:

   int                     RegFlags;
   std::vector<LONG>         PointList;

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
