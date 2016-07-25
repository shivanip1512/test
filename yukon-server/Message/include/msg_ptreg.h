#pragma once

#include "dlldefs.h"
#include "message.h"       // get the base class

#define REG_NOTHING           0x00000000
#define REG_ALL_POINTS        0x00000001     // This message registers for all points
#define REG_EVENTS            0x00000010     // I wish to hear about events.
#define REG_ALARMS            0x00000020     // I wish to hear about alarms.

#define REG_ADD_POINTS        0x00000100     // This registration is intended to add points to an existing registration.
#define REG_REMOVE_POINTS     0x00000200     // This registration is intended to remove specified points from an existing registration.

#define REG_NO_UPLOAD         0x00010000     // Dispatch will not do a MOA if set.
#define REG_TAG_MARKMOA       0x00020000     // Dispatch will mark each pointin the MOA upload with TAG_POINT_MOA_REPORT


class IM_EX_MSG CtiPointRegistrationMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiPointRegistrationMsg )

private:

   int                     RegFlags;
   std::vector<LONG>         PointList;

public:

   typedef CtiMessage Inherited;

   CtiPointRegistrationMsg(int Flag = (REG_NOTHING), int Pri = 14);
   virtual ~CtiPointRegistrationMsg();
   CtiPointRegistrationMsg(const CtiPointRegistrationMsg &aRef);

   CtiPointRegistrationMsg& operator=(const CtiPointRegistrationMsg& aRef);

   virtual CtiMessage* replicateMessage() const;

   // If list is empty, I assume you wanted them all!.
   int  getCount() const;

   LONG& operator[](size_t i);
   LONG operator[](size_t i) const;
   // Clear out the list.
   void clear();
   void insert(const LONG& a);
   int getFlags() const;
   void setFlags(int flags);

   const std::vector<LONG>& getPointList() const;
   void setPointList( const std::vector<LONG>& points );

   virtual std::string toString() const override;
};
